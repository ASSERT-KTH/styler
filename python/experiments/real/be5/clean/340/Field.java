package com.developmentontheedge.be5.metadata.serialization;

import java.util.Objects;

public class Field
{
    public final String name;
    public final Object defaultValue;

    public Field(final String name, final Object defaultValue)
    {
        this.name = Objects.requireNonNull(name);
        this.defaultValue = defaultValue;
    }

    public Field(final String name)
    {
        this(name, null);
    }

    @Override
    public String toString()
    {
        return "Field[" + name + "=" + defaultValue + "]";
    }

}
