package com.developmentontheedge.be5.query.util;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Unzipper
{

    public static Unzipper on(Pattern pattern)
    {
        return new Unzipper(pattern, false);
    }

    private final Pattern pattern;
    private final boolean trim;

    public Unzipper(Pattern pattern, boolean trim)
    {
        this.pattern = pattern;
        this.trim = trim;
    }

    public Unzipper trim()
    {
        return new Unzipper(pattern, true);
    }

    public void unzip(String string, Consumer<String> onUnmatched, Consumer<String> onMatched)
    {
        if (string == null) return;

        Matcher matcher = pattern.matcher(string);
        int previousMatchEnd = 0;

        while (matcher.find())
        {
            if (!(trim && matcher.start() == 0))
            {
                onUnmatched.accept(string.substring(previousMatchEnd, matcher.start()));
            }
            onMatched.accept(string.substring(matcher.start(), matcher.end()));
            previousMatchEnd = matcher.end();
        }

        if (!(trim && previousMatchEnd == string.length()))
        {
            onUnmatched.accept(string.substring(previousMatchEnd));
        }
    }

}