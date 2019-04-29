package com.developmentontheedge.be5.web;

import java.util.List;
import java.util.Map;


/**
 * <p>An interface providing access to key-value style parameters (e.g. from HTTP request or WebSocket request).
 * <code>Controller</code>'s {@link Request} implement this interface.</p>
 * <p>Parameters of HTTP requests are get parameters or fields of the <code>x-www-form-urlencoded</code> content.</p>
 *
 * @author lan
 * @see Request
 */
public interface ParametersAccess
{
    /**
     * Returns an unchangeable map of request parameters.
     */
    Map<String, String[]> getParameters();

    String get(String parameter);

    List<String> getList(String parameter);

    String[] getParameterValues(String name);

    default Integer getInteger(String parameter)
    {
        return getInteger(parameter, null);
    }

    default Integer getInteger(String parameter, Integer defaultValue)
    {
        String s = get(parameter);

        return s != null ? (Integer) Integer.parseInt(s) : defaultValue;
    }

    default Long getLong(String parameter)
    {
        return getLong(parameter, null);
    }

    default Long getLong(String parameter, Long defaultValue)
    {
        String s = get(parameter);

        return s != null ? Long.parseLong(s) : defaultValue;
    }

    /**
     * Returns a request parameter or empty string if there's no such parameter.
     *
     * @see ParametersAccess#get(String)
     */
    default String getOrEmpty(String parameter)
    {
        String value = get(parameter);
        return value == null ? "" : value;
    }

    default String getOrDefault(String parameter, String defaultValue)
    {
        String value = get(parameter);
        return value == null ? defaultValue : value;
    }

    /**
     * Returns a boolean request parameter or the given default value if there's no such parameter.
     *
     * @see ParametersAccess#get(String)
     */
    default boolean getBoolean(String parameter, boolean defaultValue)
    {
        String value = get(parameter);

        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    /**
     * Returns a request parameter.
     *
     * @see ParametersAccess#get(String)
     */
    default String getNonEmpty(String parameter)
    {
        String value = get(parameter);
        if (value == null)
            throw new IllegalArgumentException("Invalid request: parameter " + parameter + " is missing.");
        value = value.trim();
        if (value.isEmpty())
            throw new IllegalArgumentException("Invalid request: parameter " + parameter + " is empty.");

        return value;
    }
}
