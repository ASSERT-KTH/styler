package com.developmentontheedge.dbms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MultiSqlParser
{
    private final Iterator<String> iterator;

    public MultiSqlParser(final DbmsType dbmsType, final String input)
    {
        final List<String> statements = new ArrayList<>();
        MultiSqlConsumer consumer = new MultiSqlConsumer(dbmsType, new SqlHandler()
        {
            @Override
            public void endStatement(String statement)
            {
                statements.add(statement);
            }

            @Override
            public void startStatement()
            {
            }
        });
        for (int i = 0; i < input.length(); i++)
        {
            consumer.symbol(input.charAt(i));
        }
        consumer.end();
        this.iterator = statements.iterator();
    }

    public String nextStatement()
    {
        return iterator.hasNext() ? iterator.next() : null;
    }

    /**
     * Normalizes multiple SQL statements.
     * After normalization the following changes will be applied:
     * - Each statement will start from the new line and end at the end of the line
     * - Empty statements removed
     * - Final semicolon added if necessary
     * - All types of comments are stripped (--, /*, //)
     * - Whitespaces are compactified to single whitespace
     * - Unnecessary linebreaks removed
     *
     * @param dbmsType a type of DBMS to determine its syntactic features
     * @param sql      SQL string to normalize
     * @return normalized string
     */
    public static String normalize(DbmsType dbmsType, String sql)
    {
        MultiSqlParser parser = new MultiSqlParser(dbmsType, sql);
        StringBuilder sb = new StringBuilder();
        while (true)
        {
            String line = parser.nextStatement();
            if (line == null)
                break;
            sb.append(line).append(";").append(System.lineSeparator());
        }
        return sb.toString();
    }
}
