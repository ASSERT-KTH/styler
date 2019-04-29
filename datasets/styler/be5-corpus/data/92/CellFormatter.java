package com.developmentontheedge.be5.query.impl;

import com.developmentontheedge.be5.base.exceptions.Be5Exception;
import com.developmentontheedge.be5.base.services.UserAwareMeta;
import com.developmentontheedge.be5.base.util.HashUrl;
import com.developmentontheedge.be5.query.util.Unzipper;
import com.developmentontheedge.be5.metadata.DatabaseConstants;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.query.QueryExecutor;
import com.developmentontheedge.be5.query.VarResolver;
import com.developmentontheedge.be5.query.model.RawCellModel;
import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.DynamicPropertySetAsMap;
import com.google.common.collect.ImmutableList;
import one.util.streamex.StreamEx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class CellFormatter
{
    private static final Unzipper unzipper = Unzipper.on(Pattern.compile("<sql> SubQuery# [0-9]+</sql>")).trim();
    private final Query query;
    private final UserAwareMeta userAwareMeta;
    private final QueryExecutor queryExecutor;

    CellFormatter(Query query, QueryExecutor queryExecutor, UserAwareMeta userAwareMeta)
    {
        this.query = query;
        this.userAwareMeta = userAwareMeta;
        this.queryExecutor = queryExecutor;
    }

    /**
     * Executes subqueries of the cell or returns the cell content itself.
     */
    Object formatCell(RawCellModel cell, DynamicPropertySet previousCells)
    {
        return format(cell, new RootVarResolver(previousCells));
    }

    private Object format(RawCellModel cell, VarResolver varResolver)
    {
        //ImmutableList<Object> formattedParts = getFormattedPartsWithoutLink(cell, varResolver);

        Object formattedContent = getFormattedPartsWithoutLink(cell, varResolver);
//        if(formattedContent == null) {
//            return null;
//        }
        //formattedContent = StreamEx.of(formattedParts).map(this::print).joining();
        if (formattedContent instanceof String)
        {
            formattedContent = userAwareMeta.getLocalizedCell((String) formattedContent, query.getEntity().getName(), query.getName());
        }
        //TODO && extraQuery == Be5QueryExecutor.ExtraQuery.DEFAULT

        Map<String, String> blankNullsProperties = cell.options.get(DatabaseConstants.COL_ATTR_BLANKNULLS);
        if (blankNullsProperties != null)
        {
            if (formattedContent == null || formattedContent.equals("null"))
            {
                formattedContent = blankNullsProperties.getOrDefault("value", "");
            }
        }


        Map<String, String> nullIfProperties = cell.options.get(DatabaseConstants.COL_ATTR_NULLIF);
        if (nullIfProperties != null)
        {
            if (formattedContent == null || formattedContent.equals(nullIfProperties.get("value")))
            {
                formattedContent = nullIfProperties.getOrDefault("result", "");
            }
        }

        Map<String, String> linkProperties = cell.options.get(DatabaseConstants.COL_ATTR_LINK);
        if (linkProperties != null)
        {
            try
            {
                HashUrl url = new HashUrl("table").positional(linkProperties.get("table"))
                        .positional(linkProperties.getOrDefault("queryName", DatabaseConstants.ALL_RECORDS_VIEW));
                String cols = linkProperties.get("columns");
                String vals = linkProperties.get("using");
                if (cols != null && vals != null)
                {
                    String[] colsArr = cols.split(",");
                    String[] valuesArr = vals.split(",");
                    for (int i = 0; i < colsArr.length; i++)
                    {
                        String resolveValue = varResolver.resolve(valuesArr[i]);
                        if (resolveValue != null) url = url.named(colsArr[i], resolveValue);
                    }
                }
                cell.options.put(DatabaseConstants.COL_ATTR_LINK, Collections.singletonMap("url", url.toString()));
            }
            catch (Throwable e)
            {
                throw Be5Exception.internalInQuery(query,
                        new RuntimeException("Error in process COL_ATTR_LINK: " + cell.name, e));
            }
        }

        return formattedContent;
    }

    private Object getFormattedPartsWithoutLink(RawCellModel cell, VarResolver varResolver)
    {
        Objects.requireNonNull(cell);

        boolean hasLink = cell.options.containsKey("link");
        Map<String, String> link = null;
        if (hasLink)
        {
            link = cell.options.get("link");
            cell.options.remove("link");
        }

        ImmutableList.Builder<Object> builder = ImmutableList.builder();

        if (cell.content == null)
        {
            return null;
        }

        if (cell.content instanceof String)
        {
            unzipper.unzip((String) cell.content, builder::add, subquery ->
                    builder.add(toTable(subquery, varResolver))
            );
            ImmutableList<Object> formattedParts = builder.build();

            if (hasLink)
            {
                cell.options.put("link", link);
            }
            return StreamEx.of(formattedParts).map(this::print).joining();
        }
        else
        {
            return cell.content;
        }
    }

    /**
     * Dynamically casts tables to string using default formatting;
     */
    private String print(Object formattedPart)
    {
        if (formattedPart instanceof String)
        {
            return (String) formattedPart;
        }
        else if (formattedPart instanceof List)
        {
            @SuppressWarnings("unchecked")
            List<List<Object>> table = (List<List<Object>>) formattedPart;
            //todo support beautifiers - <br/> or ; or ...
            return StreamEx.of(table).map(list -> StreamEx.of(list).map(this::print).joining(" ")).joining("<br/> ");
//            return StreamEx.of(table).map(list -> StreamEx.of(list).map(this::print).joining(" "))
//                    .map(x -> "<div class=\"inner-sql-row\">" + x + "</div>").joining("");
        }
        else
        {
            throw new AssertionError(formattedPart.getClass().getName());
        }
    }

    /**
     * Returns a two-dimensional listDps of processed content. Each element is either a string or a table.
     */
    private List<List<Object>> toTable(String subquery, VarResolver varResolver)
    {
        List<DynamicPropertySet> list = queryExecutor.executeSubQuery(subquery, varResolver);

        List<List<Object>> lists = new ArrayList<>();

        for (DynamicPropertySet dps : list)
        {
            List<Object> objects = toRow(dps, varResolver);
            lists.add(objects);
        }

        return lists;
    }

    /**
     * Transforms a set of properties to a listDps. Each element of the listDps is a string or a table.
     */
    private List<Object> toRow(DynamicPropertySet dps, VarResolver varResolver)
    {
        DynamicPropertySet previousCells = new DynamicPropertySetAsMap();

        return StreamEx.of(dps.spliterator()).map(property -> {
            String name = property.getName();
            Object value = property.getValue();
            Object processedCell = format(new RawCellModel(value != null ? value.toString() : ""), new CompositeVarResolver(new RootVarResolver(previousCells), varResolver));
            previousCells.add(new DynamicProperty(name, String.class, processedCell));
            return !name.startsWith("___") ? processedCell : "";
        }).toList();
    }


    private static class RootVarResolver implements VarResolver
    {
        private final DynamicPropertySet dps;

        RootVarResolver(DynamicPropertySet dps)
        {
            this.dps = dps;
        }

        @Override
        public String resolve(String varName)
        {
            String value = dps.getValueAsString(varName);
            return value; // != null ? value : varName;
        }

    }

    private static class CompositeVarResolver implements VarResolver
    {
        private final VarResolver local;
        private final VarResolver parent;

        CompositeVarResolver(VarResolver local, VarResolver parent)
        {
            this.local = local;
            this.parent = parent;
        }

        @Override
        public String resolve(String varName)
        {
            String value = local.resolve(varName);

            if (value != null)
                return value;

            return parent.resolve(varName);
        }

    }

}
