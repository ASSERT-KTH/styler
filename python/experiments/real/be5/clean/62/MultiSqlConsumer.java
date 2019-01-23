package com.developmentontheedge.dbms;

/**
 * A consumer which produces separate SQL statements from the source
 * TODO support MySQL-specific escaping (like "\'")
 */
public class MultiSqlConsumer implements CharConsumer
{
    private final StringBuilder statement = new StringBuilder();
    private final StringBuilder token = new StringBuilder();
    private int nesting = 0;
    private TokenType type = TokenType.NO_TOKEN;
    private char prevChar;
    private final SqlHandler handler;
    private final DbmsType dbmsType;

    private enum TokenType
    {
        NO_TOKEN, WHITESPACE, WORD, PUNCTUATION, SLASH_SLASH_COMMENT, SLASH_ASTERISK_COMMENT,
        DASH_DASH_COMMENT, STRING, SEPARATOR, IDENTIFIER, MYSQL_IDENTIFIER, UNSURE
    }

    protected void finishToken()
    {
        token(type, token.toString());
        token.setLength(0);
        type = TokenType.NO_TOKEN;
        prevChar = 0;
    }

    @Override
    public void symbol(char c)
    {
        switch (type)
        {
            case NO_TOKEN:
                noToken(c);
                break;
            case DASH_DASH_COMMENT:
            case SLASH_SLASH_COMMENT:
                token.append(c);
                if (c == '\n')
                    finishToken();
                break;
            case IDENTIFIER:
                token.append(c);
                if (c == '"')
                {
                    finishToken();
                }
                break;
            case MYSQL_IDENTIFIER:
                token.append(c);
                if (c == '`')
                {
                    finishToken();
                }
                break;
            case PUNCTUATION:
                punctuation(c);
                break;
            case SEPARATOR:
                // Impossible
                break;
            case SLASH_ASTERISK_COMMENT:
                token.append(c);
                if (c == '/' && prevChar == '*')
                    finishToken();
                if (c == '*')
                    prevChar = c;
                else
                    prevChar = 0;
                break;
            case STRING:
                if (prevChar == '\'')
                {
                    if (c == '\'')
                    {
                        token.append(c);
                        prevChar = 0;
                    }
                    else
                    {
                        finishToken();
                        symbol(c);
                    }
                }
                else
                {
                    if (c == '\'')
                        prevChar = c;
                    else
                        prevChar = 0;
                    token.append(c);
                }
                break;
            case WHITESPACE:
                if (Character.isWhitespace(c))
                    token.append(c);
                else
                {
                    finishToken();
                    symbol(c);
                }
                break;
            case WORD:
                if ((c >= 'A' && c <= 'Z')
                        || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_')
                    token.append(c);
                else
                {
                    finishToken();
                    symbol(c);
                }
                break;
            case UNSURE:
                unsure(c);
                break;
            default:
                break;
        }
    }

    private void punctuation(char c)
    {
        if (token.length() > 0)
        {
            char pc = token.charAt(token.length() - 1);
            if ((pc == '-' && c == '-') || (pc == '/' && (c == '/' || c == '*')))
            {
                token.setLength(token.length() - 1);
                finishToken();
                symbol(pc);
                symbol(c);
                return;
            }
        }
        if (Character.isWhitespace(c) || c == '\'' || c == '\"' || c == '`' || c == ';' || (c >= 'A' && c <= 'Z')
                || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_')
        {
            finishToken();
            symbol(c);
            return;
        }
        token.append(c);
    }

    private void noToken(char c)
    {
        token.append(c);
        prevChar = 0;
        if (Character.isWhitespace(c))
        {
            type = TokenType.WHITESPACE;
            return;
        }
        if (c == ';')
        {
            type = TokenType.SEPARATOR;
            finishToken();
            return;
        }
        if (c == '\'')
        {
            type = TokenType.STRING;
            checkStart();
            return;
        }
        if (c == '\"')
        {
            type = TokenType.IDENTIFIER;
            checkStart();
            return;
        }
        if (c == '`')
        {
            type = TokenType.MYSQL_IDENTIFIER;
            checkStart();
            return;
        }
        if ((c >= 'A' && c <= 'Z')
                || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_')
        {
            type = TokenType.WORD;
            checkStart();
            return;
        }
        if (c == '-' || c == '/')
        {
            type = TokenType.UNSURE;
            prevChar = c;
            return;
        }
        type = TokenType.PUNCTUATION;
        checkStart();
    }

    private void unsure(char c)
    {
        if (prevChar == '-' && c == '-')
        {
            token.append(c);
            type = TokenType.DASH_DASH_COMMENT;
            prevChar = 0;
            return;
        }
        if (prevChar == '/' && c == '/')
        {
            token.append(c);
            type = TokenType.SLASH_SLASH_COMMENT;
            prevChar = 0;
            return;
        }
        if (prevChar == '/' && c == '*')
        {
            token.append(c);
            type = TokenType.SLASH_ASTERISK_COMMENT;
            prevChar = 0;
            return;
        }
        prevChar = 0;
        type = TokenType.PUNCTUATION;
        checkStart();
        symbol(c);
    }

    private void checkStart()
    {
        for (int i = statement.length() - 1; i >= 0; i--)
        {
            if (statement.charAt(i) != ' ')
                return;
        }
        handler.startStatement();
    }

    @Override
    public void end()
    {
        finishToken();
        String result = statement.toString().trim();
        if (!result.isEmpty())
            statement(result);
    }

    private void statement(String statement)
    {
        handler.endStatement(statement);
    }

    protected void token(TokenType type, String token)
    {
        switch (type)
        {
            case WORD:
                if (token.equalsIgnoreCase("BEGIN"))
                    nesting++;
                else if (token.equalsIgnoreCase("END"))
                    nesting--;
                statement.append(token);
                break;
            case SLASH_SLASH_COMMENT:
            case SLASH_ASTERISK_COMMENT:
            case DASH_DASH_COMMENT:
                if (statement.length() > 0 && statement.charAt(statement.length() - 1) != ' ')
                    statement.append(' ');
                break;
            case WHITESPACE:
                statement.append(' ');
                break;
            case SEPARATOR:
                if (nesting == 0)
                {
                    String result = statement.toString().trim();
                    statement.setLength(0);
                    if (!result.isEmpty())
                    {
                        statement(result);
                    }
                    break;
                }
                // passthru
            default:
                statement.append(token);
                break;
        }
    }

    public MultiSqlConsumer(DbmsType dbmsType, SqlHandler handler)
    {
        this.dbmsType = dbmsType;
        this.handler = handler;
    }
}
