package com.developmentontheedge.be5.metadata.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class StringUtils
{

    public static boolean isEmpty(String value)
    {
        return (value == null) || (value).trim().length() == 0;
    }

    public static String join(Object[] array, String delimiter)
    {
        return join(Arrays.asList(array), delimiter);
    }

    public static String join(Object[] array)
    {
        return join(Arrays.asList(array));
    }

    public static String join(Collection c)
    {
        return join(c, "");
    }

    public static String join(Collection c, String delimiter)
    {
        return join(c, delimiter, "");
    }

    public static String join(Collection c, String delimiter, String prefix)
    {
        if (c.isEmpty())
        {
            return "";
        }
        Iterator i = c.iterator();
        StringBuffer result = new StringBuffer().append(prefix).append(i.next());
        while (i.hasNext())
        {
            result.append(delimiter).append(prefix).append(i.next());
        }
        return result.toString();
    }
}
