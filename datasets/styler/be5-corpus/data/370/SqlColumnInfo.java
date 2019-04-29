package com.developmentontheedge.be5.metadata.sql.pojo;

import com.developmentontheedge.be5.metadata.util.ObjectCache;

import java.util.Arrays;

public class SqlColumnInfo
{
    private String name;
    private boolean canBeNull;
    private String defaultValue;
    private boolean autoIncrement;
    private String type;
    private String[] enumValues;
    private int size;
    private int precision;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isCanBeNull()
    {
        return canBeNull;
    }

    public void setCanBeNull(boolean canBeNull)
    {
        this.canBeNull = canBeNull;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public boolean isAutoIncrement()
    {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement)
    {
        this.autoIncrement = autoIncrement;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public int getPrecision()
    {
        return precision;
    }

    public void setPrecision(int precision)
    {
        this.precision = precision;
    }

    public String[] getEnumValues()
    {
        return enumValues;
    }

    public void setEnumValues(String[] enumValues)
    {
        this.enumValues = enumValues;
    }

    public void withCache(ObjectCache<String> strings)
    {
        if (name != null)
            name = strings.get(name);
        if (defaultValue != null)
            defaultValue = strings.get(defaultValue);
        if (type != null)
            type = strings.get(type);
        if (enumValues != null)
            Arrays.asList(enumValues).replaceAll(strings::get);
    }
}
