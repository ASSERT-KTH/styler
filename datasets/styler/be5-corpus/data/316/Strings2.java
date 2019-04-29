package com.developmentontheedge.be5.metadata.util;

import one.util.streamex.StreamEx;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Strings2
{
    public static final String[] EMPTY = new String[0];

    public static boolean containsAnyIgnoreCase(final String s, final Collection<String> substrings)
    {
        return StreamEx.of(substrings).anyMatch(substring -> containsIgnoreCase(s, substring));
    }

    public static boolean containsIgnoreCase(final String s, final String sub)
    {
        return s.toLowerCase().contains(sub.toLowerCase());
    }

    public static boolean startsWithIgnoreCase(final String s, final String sub)
    {
        return s.toLowerCase().startsWith(sub.toLowerCase());
    }

    public static void sortIgnoreCase(final List<String> strings)
    {
        Comparator<String> c = Comparator.comparing(String::toLowerCase);
        Collections.sort(strings, c);
    }

    public static String nullOrEmptyDefault(final String string, final String defaultValue)
    {
        return isNullOrEmpty(string) ? defaultValue : string;
    }

    public static String nullToEmpty(String string)
    {
        return (string == null) ? "" : string;
    }

    public static boolean isNullOrEmpty(String string)
    {
        return string == null || string.isEmpty();
    }

    public static String emptyToNull(String string)
    {
        return isNullOrEmpty(string) ? null : string;
    }

    public static String joinTail(final String separator, final List<String> splitted)
    {
        return StreamEx.of(splitted).skip(1).joining(separator);
    }

}
