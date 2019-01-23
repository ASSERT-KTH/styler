package com.developmentontheedge.be5.query.model;

import java.util.List;

public class MoreRows
{

    private final int recordsTotal;
    private final int recordsFiltered;
    private final List<List<Object>> data; // rows

    public MoreRows(int recordsTotal, int recordsFiltered, List<List<Object>> data)
    {
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.data = data;
    }


    public int getRecordsTotal()
    {
        return recordsTotal;
    }

    public int getRecordsFiltered()
    {
        return recordsFiltered;
    }

    public List<List<Object>> getData()
    {
        return data;
    }

}