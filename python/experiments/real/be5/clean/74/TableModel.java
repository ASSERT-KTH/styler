package com.developmentontheedge.be5.query.model;

import java.util.Collections;
import java.util.List;


public class TableModel
{
    private final boolean selectable;
    private final List<ColumnModel> columns;
    private final List<RowModel> rows;
    private final Long totalNumberOfRows;
    private final boolean hasAggregate;

    public final int offset;
    public final int limit;
    public final int orderColumn;
    public final String orderDir;

    public TableModel(List<ColumnModel> columns, List<RowModel> rows, boolean selectable, Long totalNumberOfRows,
                      boolean hasAggregate, int offset, int limit, int orderColumn, String orderDir)
    {
        this.selectable = selectable;
        this.columns = Collections.unmodifiableList(columns);
        this.rows = Collections.unmodifiableList(rows);
        this.totalNumberOfRows = totalNumberOfRows;
        this.hasAggregate = hasAggregate;

        this.offset = offset;
        this.limit = limit;
        this.orderColumn = orderColumn;
        this.orderDir = orderDir;
    }

    public boolean isSelectable()
    {
        return selectable;
    }

    public List<ColumnModel> getColumns()
    {
        return columns;
    }

    /**
     * Returns prepared rows. Note that this table doesn't contain all the SQL table rows.
     *
     * @see TableModel#getTotalNumberOfRows()
     */
    public List<RowModel> getRows()
    {
        return rows;
    }

    /**
     * Counts all rows.
     */
    public Long getTotalNumberOfRows()
    {
        return totalNumberOfRows;
    }

    public boolean isHasAggregate()
    {
        return hasAggregate;
    }
}
