package com.developmentontheedge.be5.query.model;

import java.util.List;
import java.util.Objects;

public class RowModel
{
    private final List<CellModel> cells;
    private final String id;

    /**
     * @param id can be null
     */
    public RowModel(String id, List<CellModel> cells)
    {
        Objects.requireNonNull(cells);
        this.id = id;
        this.cells = cells;
    }

    public List<CellModel> getCells()
    {
        return cells;
    }

    /**
     * Returns an identifier. Never returns null.
     *
     * @throws NullPointerException if null
     */
    public String getId()
    {
        return Objects.requireNonNull(id);
    }

    @Override
    public String toString()
    {
        return "RowModel{" +
                "cells=" + cells +
                '}';
    }
}
