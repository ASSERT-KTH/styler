package com.developmentontheedge.be5.metadata.sql.pojo;

import java.util.ArrayList;
import java.util.List;

public class IndexInfo
{
    private String name;
    private boolean unique;
    private final List<String> columns = new ArrayList<>();

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isUnique()
    {
        return unique;
    }

    public void setUnique(boolean unique)
    {
        this.unique = unique;
    }

    public void addColumn(String col)
    {
        columns.add(col);
    }

    public List<String> getColumns()
    {
        return columns;
    }
}
