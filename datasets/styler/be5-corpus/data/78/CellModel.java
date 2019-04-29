package com.developmentontheedge.be5.query.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Result rendered cell.
 *
 * @author asko
 */
public class CellModel
{
    /**
     * A string or a list of strings.
     */
    public final Object content;
    public final Map<String, Map<String, String>> options;

    public CellModel(Object content)
    {
        this.content = content;
        this.options = new HashMap<>();
    }

    public CellModel(Object content, Map<String, Map<String, String>> options)
    {
        this.content = content;
        this.options = options;
    }

    public Object getContent()
    {
        return content;
    }

    public Map<String, Map<String, String>> getOptions()
    {
        return options;
    }

    public CellModel option(String type, String attribute, String value)
    {
        options.computeIfAbsent(type, k -> new HashMap<>());
        options.get(type).put(attribute, value);
        return this;
    }

    @Override
    public String toString()
    {
        return "CellModel{" +
                "content=" + content +
                ", options=" + options +
                '}';
    }
}
