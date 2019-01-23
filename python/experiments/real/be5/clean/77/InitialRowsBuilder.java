package com.developmentontheedge.be5.query.model;

import java.util.List;

public class InitialRowsBuilder extends TableRowsBuilder<InitialRow, Object>
{
    private final TableModel tableModel;

    public InitialRowsBuilder(TableModel tableModel)
    {
        this.tableModel = tableModel;
    }

    @Override
    protected Object createCell(CellModel cellModel)
    {
        return cellModel;
    }

    @Override
    protected InitialRow createRow(RowModel rowModel, List<Object> cells)
    {
        return new InitialRow(tableModel.isSelectable() ? rowModel.getId() : null, cells);
    }

    @Override
    public TableModel getTableModel()
    {
        return tableModel;
    }
}
