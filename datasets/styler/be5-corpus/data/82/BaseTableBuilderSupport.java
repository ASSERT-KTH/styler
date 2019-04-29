package com.developmentontheedge.be5.query.support;

import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.query.TableBuilder;
import com.developmentontheedge.be5.query.model.CellModel;
import com.developmentontheedge.be5.query.model.ColumnModel;
import com.developmentontheedge.be5.query.model.RowModel;
import com.developmentontheedge.be5.query.model.TableModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class BaseTableBuilderSupport implements TableBuilder
{
    protected Query query;
    protected Map<String, Object> parameters;

    protected List<ColumnModel> columns = new ArrayList<>();
    protected List<RowModel> rows = new ArrayList<>();

    public TableBuilder initialize(Query query, Map<String, Object> parameters)
    {
        this.query = query;
        this.parameters = parameters;

        return this;
    }

    public void addColumns(String firstName, String... columnNames)
    {
        columns.add(new ColumnModel(firstName, firstName));
        if (columnNames != null)
        {
            for (String columnName : columnNames)
            {
                columns.add(new ColumnModel(columnName, columnName));
            }
        }
    }

    public List<CellModel> cells(Object cell, Object... cells)
    {
        List<Object> allCells = new ArrayList<>();
        allCells.add(cell);
        allCells.addAll(Arrays.asList(cells));

        List<CellModel> columns = new ArrayList<>();

        for (Object currentCell : allCells)
        {
            if (currentCell instanceof CellModel)
            {
                columns.add((CellModel) currentCell);
            }
            else
            {
                columns.add(new CellModel(currentCell, new HashMap<>()));
            }
        }

        return Collections.unmodifiableList(columns);
    }

    public List<CellModel> cells(CellModel firstCell, CellModel... cells)
    {
        List<CellModel> columns = new ArrayList<>();
        columns.add(firstCell);
        Collections.addAll(columns, cells);
        return Collections.unmodifiableList(columns);
    }

    public CellModel cell(Object content)
    {
        return new CellModel(content);
    }

    public void addRow(List<CellModel> cells)
    {
        rows.add(new RowModel("0", cells));
    }

    public void addRow(Integer id, List<CellModel> cells)
    {
        rows.add(new RowModel(id.toString(), cells));
    }

    public void addRow(String id, List<CellModel> cells)
    {
        rows.add(new RowModel(id, cells));
    }

    public TableModel table(List<ColumnModel> columns, List<RowModel> rows)
    {
        return getSimpleTable(columns, rows, false, (long) rows.size(), false);
    }

    public TableModel table(List<ColumnModel> columns, List<RowModel> rows, boolean selectable)
    {
        return getSimpleTable(columns, rows, selectable, (long) rows.size(), false);
    }

    public TableModel table(List<ColumnModel> columns, List<RowModel> rows,
                            boolean selectable, Long totalNumberOfRows, boolean hasAggregate)
    {
        return getSimpleTable(columns, rows, selectable, totalNumberOfRows, hasAggregate);
    }

    //todo support order, limit, offset
    private TableModel getSimpleTable(List<ColumnModel> columns, List<RowModel> rows,
                                      boolean selectable, Long totalNumberOfRows, boolean hasAggregate)
    {
        return new TableModel(columns, rows, selectable, totalNumberOfRows, hasAggregate,
                0, Integer.MAX_VALUE, -1, "asc");
    }

}
