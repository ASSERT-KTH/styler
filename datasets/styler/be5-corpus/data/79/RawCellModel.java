package com.developmentontheedge.be5.query.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Can be legacy descriptional cell.
 *
 * @author asko
 */
public class RawCellModel
{
    public final String name;
    public final Object content;
    public final Map<String, Map<String, String>> options;
    public final boolean hidden;

    public RawCellModel(String name, Object content, Map<String, Map<String, String>> options, boolean hidden)
    {
        this.name = name;
        this.content = content;
        this.options = options;
        this.hidden = hidden;
    }

    public RawCellModel(Object content)
    {
        this.name = "";
        this.content = content;
        this.options = new HashMap<>();
        this.hidden = false;
    }

}
