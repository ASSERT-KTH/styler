package com.developmentontheedge.be5.base.util;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;


public class HashUrl
{
    private static final String[] EMPTY = new String[0];
    private final String[] components;
    private final String[] keyValues;

    private HashUrl(String[] components, String[] keyValues)
    {
        this.components = components;
        this.keyValues = keyValues;
    }

    public HashUrl(String action, String... params)
    {
        this.components = StreamEx.of(params).prepend(action).peek(Objects::requireNonNull).toArray(String[]::new);
        this.keyValues = EMPTY;
    }

    public HashUrl positional(String value)
    {
        Objects.requireNonNull(value, "Null positional parameter supplied");
        return new HashUrl(StreamEx.of(components).append(value).toArray(String[]::new), keyValues);
    }

    public HashUrl positional(String[] values)
    {
        return new HashUrl(StreamEx.of(components).append(values).peek(Objects::requireNonNull).toArray(String[]::new), keyValues);
    }

    public HashUrl named(String key, String value)
    {
        Objects.requireNonNull(key, "Null key supplied");
        Objects.requireNonNull(value, () -> "Null value supplied for key " + key);
        return new HashUrl(components, StreamEx.of(keyValues).append(key, value).toArray(String[]::new));
    }

    public HashUrl named(Map<String, ?> args)
    {
        if (args == null)
            return this;
        return new HashUrl(components, EntryStream.of(args).flatMap(entry -> Stream.of(entry.getKey(), entry.getValue())).
                peek(Objects::requireNonNull).prepend(keyValues).toArray(String[]::new));
    }

    public HashUrl optional(String key, String value)
    {
        if (key == null)
            return this;
        return named(key, value);
    }

    private void appendEncoded(StringBuilder out, String str)
    {
        for (char c : str.toCharArray())
        {
            switch (c)
            {
                case '#':
                    out.append("%23");
                    break;
                case '/':
                    out.append("%2F");
                    break;
                case '=':
                    out.append("%3D");
                    break;
                case '%':
                    out.append("%25");
                    break;
                default:
                    out.append(c);
            }
        }
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (String component : components)
        {
            if (sb.length() > 0)
                sb.append('/');
            appendEncoded(sb, component);
        }
        for (int i = 0; i < keyValues.length; i += 2)
        {
            sb.append('/');
            appendEncoded(sb, keyValues[i]);
            sb.append('=');
            appendEncoded(sb, keyValues[i + 1]);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HashUrl hashUrl = (HashUrl) o;

        if (!Arrays.equals(components, hashUrl.components)) return false;
        return Arrays.equals(keyValues, hashUrl.keyValues);
    }

    @Override
    public int hashCode()
    {
        int result = Arrays.hashCode(components);
        result = 31 * result + Arrays.hashCode(keyValues);
        return result;
    }
}
