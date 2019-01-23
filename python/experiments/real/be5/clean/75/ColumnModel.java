package com.developmentontheedge.be5.query.model;

import java.util.Objects;

public class ColumnModel
{
    private final String title;
    private final String name;

    public ColumnModel(String name, String title)
    {
        Objects.requireNonNull(title);
        this.title = title;
        this.name = name;
    }


    public String getName()
    {
        return name;
    }

    public String getTitle()
    {
        return title;
    }
}
