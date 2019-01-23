package com.developmentontheedge.be5.query.impl.utils;

import com.developmentontheedge.be5.base.services.Meta;
import com.developmentontheedge.be5.metadata.DatabaseConstants;
import com.developmentontheedge.be5.metadata.QueryType;
import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.sql.format.Ast;
import com.developmentontheedge.sql.format.ColumnAdder;
import com.developmentontheedge.sql.format.ColumnRef;
import com.developmentontheedge.sql.format.FilterApplier;
import com.developmentontheedge.sql.model.AstBeParameterTag;
import com.developmentontheedge.sql.model.AstDerivedColumn;
import com.developmentontheedge.sql.model.AstIdentifierConstant;
import com.developmentontheedge.sql.model.AstLimit;
import com.developmentontheedge.sql.model.AstNumericConstant;
import com.developmentontheedge.sql.model.AstOrderBy;
import com.developmentontheedge.sql.model.AstOrderingElement;
import com.developmentontheedge.sql.model.AstParenthesis;
import com.developmentontheedge.sql.model.AstQuery;
import com.developmentontheedge.sql.model.AstSelect;
import com.developmentontheedge.sql.model.AstStart;
import com.developmentontheedge.sql.model.AstTableRef;
import com.developmentontheedge.sql.model.Token;
import one.util.streamex.EntryStream;
import one.util.streamex.MoreCollectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


public class QueryUtils
{
    public static void applyFilters(AstStart ast, String mainEntityName, Map<String, List<Object>> parameters, Meta meta)
    {
        Set<String> usedParams = ast.tree().select(AstBeParameterTag.class).map(AstBeParameterTag::getName).toSet();

        Map<String, String> aliasToTable = ast.tree()
                .select(AstTableRef.class)
                .filter(t -> t.getAlias() != null)
                .collect(Collectors.toMap(AstTableRef::getAlias, AstTableRef::getTable,
                        (address1, address2) -> address1));

        Map<ColumnRef, List<Object>> filters = EntryStream.of(parameters)
                .removeKeys(usedParams::contains)
                .removeKeys(QueryUtils::isNotQueryParameters)
                .removeKeys(k -> isNotContainsInQuery(mainEntityName, aliasToTable, meta, k))
                .mapKeys(k -> ColumnRef.resolve(ast, k.contains(".") ? k : mainEntityName + "." + k))
                .nonNullKeys().toMap();

        if (!filters.isEmpty())
        {
            new FilterApplier().addFilter(ast, filters);
        }
    }

    private static boolean isNotContainsInQuery(String mainEntityName, Map<String, String> aliasToTable, Meta meta, String key)
    {
        String[] split = key.split("\\.");
        if (split.length == 1)
        {
            return meta.getColumn(mainEntityName, split[0]) == null;
        }
        else
        {
            return meta.getColumn(aliasToTable.get(split[0]), split[1]) == null
                    && meta.getColumn(split[0], split[1]) == null;
        }
    }

    private static boolean isNotQueryParameters(String key)
    {
        return key.startsWith("_") && key.endsWith("_");
    }

    public static void countFromQuery(AstQuery query)
    {
        AstSelect select = Ast.selectCount().from(AstTableRef.as(
                new AstParenthesis(query.clone()),
                new AstIdentifierConstant("data", true)
        ));
        query.replaceWith(new AstQuery(select));
    }

    public static void resolveTypeOfRefColumn(AstStart ast, Meta meta)
    {
        ast.tree().select(AstBeParameterTag.class).forEach((AstBeParameterTag tag) -> {
            if (tag.getRefColumn() != null)
            {
                String[] split = tag.getRefColumn().split("\\.");
                String table, column;
                if (split.length == 2)
                {
                    table = split[0];
                    column = split[1];
                }
                else if (split.length == 3)
                {
                    table = split[0] + "." + split[1];
                    column = split[2];
                }
                else
                {
                    return;
                }
                Entity entity;

//                if(aliasToTable.get(table) != null)
//                    entity = meta.getEntity(aliasToTable.get(table));
//                else

                entity = meta.getEntity(table);

                if (entity != null)
                {
                    tag.setType(meta.getColumnType(entity, column).getName());
                }
            }
        });
    }

    public static Map<String, List<Object>> resolveTypes(Map<String, List<String>> parameters, Meta meta)
    {
        //todo resolveTypes
        Map<String, List<Object>> map = new HashMap<>();
        parameters.forEach((k, v) -> {
            if (v != null)
            {
                map.put(k, new ArrayList<>(v));
            }
        });
        return map;
    }

    public static int getQuerySortingColumn(DynamicProperty[] schema, int orderColumn)
    {
        int sortCol = -1;
        int restCols = orderColumn;
        for (int i = 0; i < schema.length; i++)
        {
            if (schema[i].isHidden()) continue;

            if (restCols-- == 0)
            {
                sortCol = i + 1;
                break;
            }
        }
        return sortCol;
    }

    public static void applySort(AstStart ast, DynamicProperty[] schema, DebugQueryLogger dql,
                                 int orderColumn, String orderDir)
    {
        if (orderColumn >= 0)
        {
            int sortCol = getQuerySortingColumn(schema, orderColumn);
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
            dql.log("With sort", ast);
        }
    }

    public static boolean hasColumnWithLabel(AstStart ast, String idColumnLabel)
    {
        AstQuery query = ast.getQuery();
        Optional<AstSelect> selectOpt = query.children().select(AstSelect.class).collect(MoreCollectors.onlyOne());
        if (!selectOpt.isPresent())
            return false;
        AstSelect select = selectOpt.get();
        return select.getSelectList().children()
                .select(AstDerivedColumn.class)
                .map(AstDerivedColumn::getAlias)
                .nonNull()
                .map(alias -> alias.replaceFirst("^\"(.+)\"$", "$1"))
                .map(String::toUpperCase)
                .has(idColumnLabel);
    }

    public static void addIDColumnIfNeeded(AstStart ast, Query query, DebugQueryLogger dql)
    {
        if (query.getType() == QueryType.D1 && query.getEntity().findTableDefinition() != null && !hasColumnWithLabel(ast, DatabaseConstants.ID_COLUMN_LABEL))
        {
            new ColumnAdder().addColumn(ast, query.getEntity().getName(), query.getEntity().getPrimaryKey(),
                    DatabaseConstants.ID_COLUMN_LABEL);
            dql.log("With ID column", ast);
        }
        else
        {
            dql.log("Without ID column", ast);
        }
    }
}
