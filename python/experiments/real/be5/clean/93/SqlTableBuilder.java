package com.developmentontheedge.be5.query.impl;

import com.developmentontheedge.be5.base.exceptions.Be5Exception;
import com.developmentontheedge.be5.base.model.UserInfo;
import com.developmentontheedge.be5.base.services.UserAwareMeta;
import com.developmentontheedge.be5.metadata.DatabaseConstants;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.query.QueryExecutor;
import com.developmentontheedge.be5.query.model.CellModel;
import com.developmentontheedge.be5.query.model.ColumnModel;
import com.developmentontheedge.be5.query.model.RawCellModel;
import com.developmentontheedge.be5.query.model.RowModel;
import com.developmentontheedge.be5.query.model.TableModel;
import com.developmentontheedge.be5.query.services.QueryService;
import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.DynamicPropertySetAsMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SqlTableBuilder
{
    private final Query query;
    private final UserInfo userInfo;
    private final Map<String, Object> parameters;
    private final QueryService queryService;
    private final QueryExecutor queryExecutor;
    private final UserAwareMeta userAwareMeta;
    private final CellFormatter cellFormatter;

    public SqlTableBuilder(Query query, Map<String, Object> parameters, UserInfo userInfo, QueryService queryService,
                           UserAwareMeta userAwareMeta)
    {
        this.query = query;
        this.parameters = parameters;
        this.userInfo = userInfo;

        this.queryService = queryService;
        this.queryExecutor = queryService.build(query, parameters);
        this.userAwareMeta = userAwareMeta;
        this.cellFormatter = new CellFormatter(query, queryExecutor, userAwareMeta);
    }

    public SqlTableBuilder offset(int offset)
    {
        this.queryExecutor.offset(offset);
        return this;
    }

    public SqlTableBuilder limit(int limit)
    {
        this.queryExecutor.limit(limit);
        return this;
    }

    public SqlTableBuilder sortOrder(int orderColumn, String orderDir)
    {
        queryExecutor.order(orderColumn, orderDir);
        return this;
    }

    public SqlTableBuilder selectable(boolean selectable)
    {
        queryExecutor.selectable(selectable);
        return this;
    }
//
//        public Builder setContextApplier(ContextApplier contextApplier)
//        {
//            queryExecutor.setContextApplier(contextApplier);
//            return this;
//        }

    public long count()
    {
        return queryExecutor.count();
    }

    public TableModel build()
    {
        List<ColumnModel> columns = new ArrayList<>();
        List<RowModel> rows = new ArrayList<>();

        collectColumnsAndRows(query.getEntity().getName(), query.getName(), queryExecutor.execute(), columns, rows);

        boolean hasAggregate = addAggregateRowIfNeeded(rows);

        filterWithRoles(columns, rows);

        Long totalNumberOfRows;
        if (queryExecutor.getOffset() + rows.size() < queryExecutor.getLimit())
        {
            totalNumberOfRows = (long) rows.size();
        }
        else
        {
            totalNumberOfRows = queryService.build(query, parameters).count();
        }

        return new TableModel(
                columns,
                rows,
                queryExecutor.getSelectable(),
                totalNumberOfRows,
                hasAggregate,
                queryExecutor.getOffset(),
                queryExecutor.getLimit(),
                queryExecutor.getOrderColumn(),
                queryExecutor.getOrderDir());
    }

    /*
     * com.developmentontheedge.enterprise.query.TabularFragmentBuilder.filterBeanWithRoles()
     * */
    void filterWithRoles(List<ColumnModel> columns, List<RowModel> rows)
    {
        if (rows.size() == 0) return;
        List<String> currRoles = userInfo.getCurrentRoles();

        List<CellModel> firstLine = rows.get(0).getCells();
        for (int i = firstLine.size() - 1; i >= 0; i--)
        {
            Map<String, String> columnRoles = firstLine.get(i).options.get(DatabaseConstants.COL_ATTR_ROLES);

            if (columnRoles == null)
            {
                continue;
            }

            String roles = columnRoles.get("name");
            List<String> roleList = Arrays.asList(roles.split(","));
            List<String> forbiddenRoles = roleList.stream().filter(x -> x.startsWith("!")).collect(Collectors.toList());

            roleList.removeAll(forbiddenRoles);

            boolean hasAccess = false;

            if (roleList.stream().anyMatch(currRoles::contains))
            {
                hasAccess = true;
            }

            if (!hasAccess && !forbiddenRoles.isEmpty() && currRoles.stream().anyMatch(x -> !forbiddenRoles.contains(x)))
            {
                hasAccess = true;
            }

            if (!hasAccess)
            {
                for (RowModel rowModel : rows)
                {
                    rowModel.getCells().remove(i);
                }
                columns.remove(i);
            }
        }
    }

    private boolean addAggregateRowIfNeeded(List<RowModel> rows)
    {
        if (rows.size() == 0 || rows.get(0).getCells().stream()
                .noneMatch(x -> x.options.containsKey(DatabaseConstants.COL_ATTR_AGGREGATE))) return false;

        List<RowModel> aggregateRow = new ArrayList<>();

        collectColumnsAndRows(query.getEntity().getName(), query.getName(), queryExecutor.executeAggregate(), new ArrayList<>(), aggregateRow);

        List<CellModel> firstLine = aggregateRow.get(0).getCells();
        double[] resD = new double[firstLine.size()];

        for (RowModel row : aggregateRow)
        {
            for (int i = 0; i < firstLine.size(); i++)
            {
                Map<String, String> aggregate = firstLine.get(i).options.get(DatabaseConstants.COL_ATTR_AGGREGATE);
                if (aggregate != null)
                {
                    Double add;
                    if (row.getCells().get(i).content instanceof List)
                    {
                        add = Double.parseDouble((String) ((List) ((List) row.getCells().get(i).content).get(0)).get(0));
                    }
                    else
                    {
                        add = (double) row.getCells().get(i).content; //todo test aggregate
                    }
                    if ("Number".equals(aggregate.get("type")))
                    {
                        switch (aggregate.get("function"))
                        {
                            case "COUNT":
                                resD[i]++;
                                break;
                            case "SUM":
                            case "AVG":
                                resD[i] += add;
                                break;
                            default:
                                throw Be5Exception.internal("aggregate not support function: " + aggregate.get("function"));
                        }
                    }
                    else
                    {
                        throw Be5Exception.internal("aggregate not support function: " + aggregate.get("function"));
                    }
                }
            }
        }
        for (int i = 0; i < firstLine.size(); i++)
        {
            Map<String, String> aggregate = firstLine.get(i).options.get(DatabaseConstants.COL_ATTR_AGGREGATE);
            if (aggregate != null)
            {
                if ("Number".equals(aggregate.get("type")))
                {
                    switch (aggregate.get("function"))
                    {
                        case "SUM":
                        case "COUNT":
                            break;
                        case "AVG":
                            resD[i] /= aggregateRow.size();
                            break;
                        default:
                            throw Be5Exception.internal("aggregate not support function: " + aggregate.get("function"));
                    }
                }
                else
                {
                    throw Be5Exception.internal("aggregate not support function: " + aggregate.get("function"));
                }
            }
        }
        List<CellModel> res = new ArrayList<>();
        for (int i = 0; i < firstLine.size(); i++)
        {
            Map<String, String> aggregate = firstLine.get(i).options.get(DatabaseConstants.COL_ATTR_AGGREGATE);
            Map<String, Map<String, String>> options = new HashMap<>();
            Object content = "";
            if (aggregate != null)
            {
                content = resD[i];
                options.put("css", Collections.singletonMap("class", aggregate.getOrDefault("cssClass", "")));
                options.put("format", Collections.singletonMap("mask", aggregate.getOrDefault("format", "")));
            }
            else
            {
                if (i == 0)
                {
                    content = userAwareMeta.getColumnTitle(query.getEntity().getName(), query.getName(),
                            "total");
                }
            }
            res.add(new CellModel(content, options));
        }

        rows.add(new RowModel("aggregate", res));
        return true;
    }

    private void collectColumnsAndRows(String entityName, String queryName, List<DynamicPropertySet> list, List<ColumnModel> columns,
                                       List<RowModel> rows)
    {
        for (DynamicPropertySet properties : list)
        {
            if (columns.isEmpty())
            {
                columns.addAll(new PropertiesToRowTransformer(entityName, queryName, properties, userAwareMeta).collectColumns());
            }
            rows.add(generateRow(entityName, queryName, properties));
        }
    }

    private RowModel generateRow(String entityName, String queryName, DynamicPropertySet properties) throws AssertionError
    {
        PropertiesToRowTransformer transformer = new PropertiesToRowTransformer(entityName, queryName, properties, userAwareMeta);
        List<RawCellModel> cells = transformer.collectCells(); // can contain hidden cells
        addRowClass(cells);
        List<CellModel> processedCells = processCells(cells); // only visible cells
        String id = queryExecutor.getSelectable() ? transformer.getRowId() : null;

        return new RowModel(id, processedCells);
    }

    private void addRowClass(List<RawCellModel> cells)
    {
        Optional<Object> addClassName = cells.stream()
                .filter(x -> x.name.equals(DatabaseConstants.CSS_ROW_CLASS) && x.content != null)
                .map(x -> x.content).findFirst();

        if (addClassName.isPresent())
        {
            for (RawCellModel cell : cells)
            {
                if (cell.options.get("grouping") != null) continue;
                Map<String, String> css = cell.options.putIfAbsent("css", new HashMap<>());
                if (css == null) css = cell.options.get("css");

                String className = css.getOrDefault("class", "");
                css.put("class", className + " " + addClassName.get());
            }
        }
    }

    /**
     * Processes each cell's content and selects only visible cells.
     *
     * @param cells raw cells
     *              columns.size() == cells.size()
     */
    private List<CellModel> processCells(List<RawCellModel> cells)
    {
        List<CellModel> processedCells = new ArrayList<>();
        DynamicPropertySet previousCells = new DynamicPropertySetAsMap();

        for (RawCellModel cell : cells)
        {
            Object processedContent = cellFormatter.formatCell(cell, previousCells);
            previousCells.add(new DynamicProperty(cell.name, processedContent == null ? String.class
                    : processedContent.getClass(), processedContent));
            if (!cell.hidden)
            {
                processedCells.add(new CellModel(processedContent, cell.options));
            }
        }

        return processedCells;
    }

}
