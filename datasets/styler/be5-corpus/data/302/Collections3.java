package com.developmentontheedge.be5.metadata.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class Collections3
{
    /**
     * @param iterable
     * @param map
     * @return
     * @throws IllegalArgumentException
     */
    public static <T, U> List<U> apply(final Iterable<T> iterable, final Map<T, U> map)
    {
        final List<U> us = new ArrayList<>();

        for (final T t : iterable)
        {
            if (!map.containsKey(t))
            {
                throw new IllegalArgumentException();
            }

            us.add(map.get(t));
        }

        return us;
    }

    public static boolean containsContainsIgnoreCase(final Iterable<String> iterable, final String s)
    {
        final Iterator<String> i = iterable.iterator();

        while (i.hasNext())
            if (Strings2.containsIgnoreCase(i.next(), s))
                return true;

        return false;
    }

    public static <T> boolean containsAny(final Iterable<T> iterable, final Collection<T> collection)
    {
        for (final T t : iterable)
            if (collection.contains(t))
                return true;

        return false;
    }

    public static <T extends Comparable<T>> List<T> makeUnique(final Collection<T> list)
    {
        final TreeSet<T> ts = new TreeSet<>(list);
        final List<T> result = new ArrayList<>(ts);
        return result;
    }

    public static <T extends Comparable<T>> void makeUniqueInPlace(final Collection<T> list)
    {
        final TreeSet<T> ts = new TreeSet<>(list);
        final List<T> result = new ArrayList<>(ts);

        list.clear();
        list.addAll(result);
    }

    public static String[] swap(final String[] array, final int index1, final int index2)
    {
        final String[] copy = array.clone();
        final String element = copy[index1];
        copy[index1] = copy[index2];
        copy[index2] = element;

        return copy;
    }

}
