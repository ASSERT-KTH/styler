package com.developmentontheedge.be5.query.impl;

import com.developmentontheedge.be5.base.services.Meta;
import com.developmentontheedge.be5.base.services.UserAwareMeta;
import com.developmentontheedge.be5.base.util.Utils;
import com.developmentontheedge.be5.metadata.DatabaseConstants;
import com.developmentontheedge.be5.metadata.model.ColumnDef;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.metadata.util.Strings2;
import com.developmentontheedge.be5.query.util.CategoryFilter;
import com.developmentontheedge.sql.format.Ast;
import com.developmentontheedge.sql.format.ColumnAdder;
import com.developmentontheedge.sql.format.ColumnRef;
import com.developmentontheedge.sql.model.AstBeParameterTag;
import com.developmentontheedge.sql.model.AstDerivedColumn;
import com.developmentontheedge.sql.model.AstFieldReference;
import com.developmentontheedge.sql.model.AstIdentifierConstant;
import com.developmentontheedge.sql.model.AstLimit;
import com.developmentontheedge.sql.model.AstNumericConstant;
import com.developmentontheedge.sql.model.AstOrderBy;
import com.developmentontheedge.sql.model.AstOrderingElement;
import com.developmentontheedge.sql.model.AstParenthesis;
import com.developmentontheedge.sql.model.AstQuery;
import com.developmentontheedge.sql.model.AstSelect;
import com.developmentontheedge.sql.model.AstSelectList;
import com.developmentontheedge.sql.model.AstStart;
import com.developmentontheedge.sql.model.AstStringConstant;
import com.developmentontheedge.sql.model.AstTableRef;
import com.developmentontheedge.sql.model.SimpleNode;
import com.developmentontheedge.sql.model.Token;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class QueryMetaHelper
{
    private final Meta meta;
    private final UserAwareMeta userAwareMeta;

    @Inject
    public QueryMetaHelper(Meta meta, UserAwareMeta userAwareMeta)
    {
        this.meta = meta;
        this.userAwareMeta = userAwareMeta;
    }

    void applyFilters(AstStart ast, Map<String, List<Object>> parameters)
    {
        String fromTableDef = getFromTableDefName(ast);
        Set<String> usedParams = ast.tree().select(AstBeParameterTag.class).map(AstBeParameterTag::getName).toSet();

        Map<String, String> aliasToTable = getAliasToTable(ast);

        Map<ColumnRef, List<Object>> rawFilters = EntryStream.of(parameters)
                .removeKeys(usedParams::contains)
                .removeKeys(QueryMetaHelper::isNotQueryParameters)
                .removeKeys(k -> isNotContainsInQuery(fromTableDef, aliasToTable, k))
                .mapKeys(k -> ColumnRef.resolve(ast, k.contains(".") ? k : fromTableDef + "." + k))
                .nonNullKeys()
                .toMap();

        Map<ColumnRef, List<Object>> filters = resolveTypes(rawFilters, aliasToTable);

        if (!filters.isEmpty())
        {
            new Be5FilterApplier().addFilter(ast, filters);
        }
    }

    static void applyCategory(Query query, AstStart ast, String categoryString)
    {
        if (categoryString != null)
        {
            new CategoryFilter(query.getEntity().getName(), query.getEntity().getPrimaryKey(),
                    Long.parseLong(categoryString)).apply(ast);
        }
    }

    private static Map<String, String> getAliasToTable(AstStart ast)
    {
        return ast.tree()
                    .select(AstTableRef.class)
                    .filter(t -> t.getTable() != null && t.getAlias() != null)
                    .collect(Collectors.toMap(AstTableRef::getAlias, AstTableRef::getTable,
                            (address1, address2) -> address1));
    }

    private boolean isNotContainsInQuery(String mainEntityName, Map<String, String> aliasToTable, String key)
    {
        String[] split = key.split("\\.");
        if (split.length == 1)
        {
            return meta.getColumn(mainEntityName, split[0]) == null;
        }
        else
        {
            String entityName;
            if (aliasToTable.get(split[0]) != null)
            {
                entityName = aliasToTable.get(split[0]);
            }
            else
            {
                entityName = split[0];
            }
            return meta.getColumn(entityName, split[1]) == null;
        }
    }

    private static boolean isNotQueryParameters(String key)
    {
        return key.startsWith("_") && key.endsWith("_");
    }

    static void countFromQuery(AstQuery query)
    {
        AstSelect select = Ast.selectCount().from(AstTableRef.as(
                new AstParenthesis(query.clone()),
                new AstIdentifierConstant("data", true)
        ));
        query.replaceWith(new AstQuery(select));
    }

    void resolveTypeOfRefColumn(AstStart ast)
    {
        String fromTableDef = getFromTableDefName(ast);
        ast.tree().select(AstBeParameterTag.class).forEach((AstBeParameterTag tag) -> {
            ColumnDef columnDef = getColumnDef(ast, tag, fromTableDef);
            if (columnDef != null)
            {
                tag.setType(meta.getColumnType(columnDef).getName());
            }
        });
    }

    private static String getFromTableDefName(AstStart ast)
    {
        return ast.getQuery().tree()
                .select(AstTableRef.class)
                .findFirst().get().getTable();
    }

    public ColumnDef getColumnDef(AstStart ast, AstBeParameterTag beParameterTag)
    {
        return getColumnDef(ast, beParameterTag, getFromTableDefName(ast));
    }

    private ColumnDef getColumnDef(AstStart ast, AstBeParameterTag beParameterTag, String mainEntityName)
    {
        if (beParameterTag.getRefColumn() != null)
        {
            return getColumnDef(ast, beParameterTag.getRefColumn(), mainEntityName);
        }
        else
        {
            SimpleNode node = beParameterTag.jjtGetParent();
            if (node.getClass() == AstStringConstant.class)
            {
                node = node.jjtGetParent();
            }
            Optional<AstFieldReference> first = node.children().select(AstFieldReference.class).findFirst();
            if (first.isPresent())
            {
                return getColumnDef(ast, first.get().getValue(), mainEntityName);
            }
            else
            {
                return null;
            }
        }
    }

    private ColumnDef getColumnDef(AstStart ast, String rawColumnDef, String mainEntityName)
    {
        final List<String> splittedTo = StreamEx.split(rawColumnDef, "\\.").toList();
        if (splittedTo.size() == 1)
        {
            return meta.getColumn(mainEntityName, splittedTo.get(0));
        }
        else
        {
            String entityName = Strings2.joinWithoutTail(".", splittedTo);
            final String column = splittedTo.get(splittedTo.size() -1);
            Set<String> entityNames = meta.getProject().getEntityNames();
            if (!entityNames.contains(entityName))
            {
                if (getAliasToTable(ast).get(entityName) == null)
                {
                    throw new RuntimeException("Entity with alias '" + entityName + "' not found.");
                }
                entityName = getAliasToTable(ast).get(entityName);
            }
            return meta.getColumn(entityName, column);
        }
    }

    private Map<ColumnRef, List<Object>> resolveTypes(Map<ColumnRef, List<Object>> parameters,
                                                             Map<String, String> aliasToTable)
    {
        Map<ColumnRef, List<Object>> map = new HashMap<>();
        parameters.forEach((k, v) -> {
            if (v != null)
            {
                List<Object> list = new ArrayList<>();
                ColumnDef columnDef = meta.getColumn(aliasToTable.getOrDefault(k.getTable(), k.getTable()), k.getName());
                Class<?> columnType = meta.getColumnType(columnDef);
                if (columnType == String.class)
                {
                    if (v.size() == 1 && columnDef.getType().getEnumValues().length == 0 && !columnDef.hasReference())
                    {
                        String value = (String) v.get(0);
                        if (!value.startsWith("%") && !value.endsWith("%"))
                        {
                            value = "%" + value + "%";
                        }
                        list.add(value);
                    }
                    else
                    {
                        list.addAll(v);
                    }
                }
                else
                {
                    v.forEach(a -> list.add(Utils.changeType(a, columnType)));
                }
                map.put(k, list);
            }
        });
        return map;
    }

    private static int getQuerySortingColumn(AstStart ast, int orderColumn)
    {
        AstSelectList selectList = ast.getQuery().getSelect().getSelectList();
        if (selectList.isAllColumns()) return orderColumn;

        int sortCol = -1;
        int restCols = orderColumn;
        for (int i = 0; i < selectList.jjtGetNumChildren(); i++)
        {
            if (((AstDerivedColumn) selectList.child(i)).getAlias().startsWith(";")) continue;

            if (restCols-- == 0)
            {
                sortCol = i + 1;
                break;
            }
        }
        return sortCol;
    }

    static void applySort(AstStart ast, int orderColumn, String orderDir)
    {
        if (orderColumn >= 0)
        {
            int sortCol = getQuerySortingColumn(ast, orderColumn);
            if (sortCol > 0)
            {
                AstSelect sel = (AstSelect) ast.getQuery().jjtGetChild(
                        ast.getQuery().jjtGetNumChildren() - 1);

                AstOrderBy orderBy = sel.getOrderBy();
                if (orderBy == null)
                {
                    orderBy = new AstOrderBy();
                    sel.addChild(orderBy);
                    AstLimit astLimit = sel.children().select(AstLimit.class).findFirst().orElse(null);
                    if (astLimit != null)
                    {
                        sel.removeChild(astLimit);
                        sel.addChild(astLimit);
                    }
                }
                AstOrderingElement oe = new AstOrderingElement(AstNumericConstant.of(sortCol));
                if ("desc".equals(orderDir))
                {
                    oe.setDirectionToken(new Token(0, "DESC"));
                }
                orderBy.addChild(oe);
                orderBy.moveToFront(oe);
            }
        }
    }

    static void addIDColumnLabel(AstStart ast, Query query)
    {
        new ColumnAdder().addColumn(ast, query.getEntity().getName(), query.getEntity().getPrimaryKey(),
                DatabaseConstants.ID_COLUMN_LABEL);
    }

    String getTotalTitle(Query query)
    {
        return userAwareMeta.getColumnTitle(query.getEntity().getName(), query.getName(), "total");
    }
}
