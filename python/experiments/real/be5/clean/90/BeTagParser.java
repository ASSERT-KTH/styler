package com.developmentontheedge.be5.query.impl;

import one.util.streamex.EntryStream;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Parser to parse BE tags like <blahbla/>;<foo bar="baz" a="b" c="d"/>;<oneMoreTag/>
public class BeTagParser
{
    private enum TokenType
    {
        NONE, WHITESPACE, WORD, EQUALS, STRING, LEFT_TAG, RIGHT_TAG, SEMICOLON
    }

    private static class Tokenizer
    {
        private static final Pattern TOKENS = Pattern.compile("(\\s+)|(\\w+)|(=)|[\"\']([^\"\']+)[\"\']|(<)|(/>)|(;)");
        private final String input;
        private int pos;
        private final Matcher matcher;

        class ParseException extends Exception
        {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            public ParseException(String message)
            {
                super(String.format(Locale.ENGLISH, "%s%n%s%n%" + (pos + 1) + "s", message, input, "^"));
            }
        }

        public Tokenizer(String input)
        {
            this.input = input;
            this.pos = 0;
            this.matcher = TOKENS.matcher(input);
        }

        public String consume(TokenType token, boolean optional) throws ParseException
        {
            while (true)
            {
                if (!matcher.find(pos) || matcher.start() != pos)
                    throw new ParseException("Invalid symbol");
                pos = matcher.end();
                if (matcher.group(TokenType.WHITESPACE.ordinal()) == null)
                    break;
            }
            String tokenStr = matcher.group(token.ordinal());
            if (tokenStr == null)
            {
                if (!optional)
                    throw new ParseException("Unexpected token " + matcher.group() + " (wanted: " + token + ")");
                pos = matcher.start();
            }
            return tokenStr;
        }

        public boolean finished()
        {
            return pos == input.length();
        }
    }

    public static void parseTags(Map<String, Map<String, String>> map, String tagString)
    {
        try
        {
            String input = tagString.trim();
            if (input.length() == 0) return;
            Tokenizer tokenizer = new Tokenizer(input);
            if (!input.startsWith("<"))
            {
                // legacy-style link attribute:
                String[] cols = tagString.split(";");
                if (cols.length != 3)
                {
                    throw tokenizer.new ParseException(
                            "Must either start with '<' or contain three semicolon-separated fields like <using>;<table>;<columns>");
                }
                Map<String, String> attrs = EntryStream.of("using", cols[0].trim(), "table", cols[1].trim(), "columns", cols[2].trim()).toMap();
                map.put("link", attrs);
                return;
            }
            while (!tokenizer.finished())
            {
                tokenizer.consume(TokenType.LEFT_TAG, false);
                String name = tokenizer.consume(TokenType.WORD, false);
                if (map.containsKey(name))
                {
                    throw tokenizer.new ParseException("Duplicate tag: " + name);
                }
                Map<String, String> subMap = null;
                while (tokenizer.consume(TokenType.RIGHT_TAG, true) == null)
                {
                    String key = tokenizer.consume(TokenType.WORD, false);
                    tokenizer.consume(TokenType.EQUALS, false);
                    String value = tokenizer.consume(TokenType.STRING, false);
                    if (subMap == null)
                        subMap = new TreeMap<>();
                    subMap.put(key, value);
                }
                map.put(name, subMap == null ? Collections.emptyMap() : subMap);
                if (!tokenizer.finished())
                {
                    tokenizer.consume(TokenType.SEMICOLON, false);
                }
            }

        }
        catch (Tokenizer.ParseException e)
        {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
