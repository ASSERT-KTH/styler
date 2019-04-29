package com.developmentontheedge.be5.query.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import java.util.List;

public class MoreRowsBuilder extends TableRowsBuilder<List<Object>, Object>
{

    private final TableModel tableModel;

    public MoreRowsBuilder(TableModel tableModel)
    {
        this.tableModel = tableModel;
    }

    @Override
    protected Object createCell(CellModel cellModel)
    {
        return cellModel;
    }

    @Override
    protected List<Object> createRow(RowModel rowModel, List<Object> cells)
    {
        Builder<Object> builder = ImmutableList.builder();

        if (tableModel.isSelectable())
        {
            builder = builder.add(rowModel.getId());
        }

        return builder.addAll(cells).build();
    }

    @Override
    public TableModel getTableModel()
    {
        return tableModel;
    }
}
