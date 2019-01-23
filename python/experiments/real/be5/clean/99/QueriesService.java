package com.developmentontheedge.be5.query.services;

import com.developmentontheedge.be5.base.services.Be5Caches;
import com.developmentontheedge.be5.base.services.Meta;
import com.developmentontheedge.be5.base.services.UserAwareMeta;
import com.developmentontheedge.be5.base.services.UserInfoProvider;
import com.developmentontheedge.be5.database.DbService;
import com.developmentontheedge.be5.metadata.DatabaseConstants;
import com.developmentontheedge.be5.metadata.QueryType;
import com.developmentontheedge.be5.metadata.model.ColumnDef;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.query.model.RowModel;
import com.developmentontheedge.be5.query.model.TableModel;
import com.developmentontheedge.be5.query.model.beans.QRec;
import com.developmentontheedge.be5.query.sql.QRecParser;
import com.developmentontheedge.beans.DynamicProperty;
import com.github.benmanes.caffeine.cache.Cache;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.developmentontheedge.be5.metadata.model.SqlBoolColumnType.NO;
import static com.developmentontheedge.be5.metadata.model.SqlBoolColumnType.YES;


public class QueriesService
{
    private final Cache<String, String[][]> tagsCache;

    private final DbService db;
    private final Meta meta;
    private final UserAwareMeta userAwareMeta;
    private final QueryService queryService;
    private final TableModelService tableModelService;
    private final UserInfoProvider userInfoProvider;

    @Inject
    public QueriesService(DbService db, Meta meta, UserAwareMeta userAwareMeta, Be5Caches be5Caches,
                          TableModelService tableModelService, QueryService queryService, UserInfoProvider userInfoProvider)
    {
        this.db = db;
        this.meta = meta;
        this.userAwareMeta = userAwareMeta;
        this.tableModelService = tableModelService;
        this.queryService = queryService;

        tagsCache = be5Caches.createCache("Tags");
        this.userInfoProvider = userInfoProvider;
    }

//    public HashUrl createQueryUrl(Request req)
//    {
//        return new HashUrl(FrontendConstants.TABLE_ACTION, req.get(RestApiConstants.ENTITY), req.get(RestApiConstants.QUERY));
//    }

    /**
     * Creates a list of options by a table name and columns that are used for optiion value and text respectively.
     */
    public String[][] getTags(String tableName, String valueColumnName, String textColumnName)
    {
        List<String[]> tags = db.list("SELECT " + valueColumnName + ", " + textColumnName + " FROM " + tableName,
                rs -> new String[]{rs.getString(valueColumnName), rs.getString(textColumnName)}
        );
        String[][] stockArr = new String[tags.size()][2];

        for (int i = 0; i < tags.size(); i++)
        {
            stockArr[i] = new String[]{tags.get(i)[0], userAwareMeta.getColumnTitle(tableName, tags.get(i)[1])};
        }

        return stockArr;
    }

//    public List<Option> formOptionsWithEmptyValue(String tableName, String valueColumnName, String textColumnName, String placeholder)
//    {
//        ImmutableList.Builder<Option> options = ImmutableList.builder();
//        options.add(new Option("", placeholder));
//        options.addAll(formOptions(tableName, valueColumnName, textColumnName));
//
//        return options.build();
//    }

    /**
     * Creates a list of options by a specified table name. An entity with a
     * table definition for this table must be defined. A "selection view" query
     * of the entity must be defined too. Roles and visibility of the query are
     * ignored.
     * </p>
     * A "selection view" query is a query with name "*** Selection view ***"
     * that selects rows with two fields: an identifier and a displayed text.
     * Names of columns are ignored, only the order matters.
     * </p>
     *
     * @throws IllegalArgumentException when an entity or a query is not defined
     * @throws Error                    if a found query cannot be compiled
     */
    public String[][] getTagsFromSelectionView(String tableName)
    {
        return getTagsFromCustomSelectionView(tableName, DatabaseConstants.SELECTION_VIEW, Collections.emptyMap());
    }

    public String[][] getTagsFromSelectionView(String tableName, Map<String, ?> parameters)
    {
        return getTagsFromCustomSelectionView(tableName, DatabaseConstants.SELECTION_VIEW, parameters);
    }

    public String[][] getTagsFromCustomSelectionView(String tableName, String queryName)
    {
        return getTagsFromCustomSelectionView(tableName, queryName, Collections.emptyMap());
    }

    public String[][] getTagsFromCustomSelectionView(String tableName, String queryName, Map<String, ?> parameters)
    {
        return getTagsFromCustomSelectionView(meta.getQuery(tableName, queryName), parameters);
    }

    public String[][] getTagsFromCustomSelectionView(Query query, Map<String, ?> parameters)
    {
        String entityName = query.getEntity().getName();
        if (query.isCacheable())
        {
            return tagsCache.get(entityName + "getTagsFromCustomSelectionView" + query.getEntity() +
                            parameters.toString() + userInfoProvider.get().getLanguage(),
                    k -> getTagsFromCustomSelectionViewExecute(query, parameters)
            );
        }
        return getTagsFromCustomSelectionViewExecute(query, parameters);
    }

    public String[][] getTagsFromCustomSelectionView(String sql, Map<String, ?> parameters)
    {
        return getTagsFromCustomSelectionView(meta.createQueryFromSql(sql), parameters);
    }

    public String[][] getTagsFromQuery(String sql, Object... params)
    {
        List<String[]> tags = db.list(sql,
                rs -> new String[]{rs.getString(1), rs.getString(2)}, params
        );
        String[][] stockArr = new String[tags.size()][2];
        return tags.toArray(stockArr);
    }

//
//    public Map<String, String> getTagsMapFromQuery( Map<String, Object> parameters, String query, Object... params )
//    {
//        //return getTagsListFromQuery( Collections.emptyMap(), query, params );
//        List<String[]> tags = db.selectList("SELECT " + valueColumnName + ", " + textColumnName + " FROM " + tableName,
//                rs -> new String[]{rs.getString(valueColumnName), rs.getString(textColumnName)}
//        );
//        String[][] stockArr = new String[tags.size()][2];
//        return tags.toArray(stockArr);
//    }

    private String[][] getTagsFromCustomSelectionViewExecute(Query query, Map<String, ?> parameters)
    {
        String entityName = query.getEntity().getName();

        TableModel tableModel;
        if (query.getType() == QueryType.GROOVY)
        {
            tableModel = tableModelService.getTableModel(query, parameters);
        }
        else
        {
            tableModel = tableModelService.builder(query, parameters)
                    .limit(Integer.MAX_VALUE)
                    .selectable(false)
                    .build();
        }

        String[][] stockArr = new String[tableModel.getRows().size()][2];

        int i = 0;
        for (RowModel row : tableModel.getRows())
        {
            String first = row.getCells().size() >= 1 ? row.getCells().get(0).content.toString() : "";
            String second = row.getCells().size() >= 2 && row.getCells().get(1).content != null ?
                    row.getCells().get(1).content.toString() : "";
            stockArr[i++] = new String[]{first, userAwareMeta.getColumnTitle(entityName, second)};
        }

        return stockArr;
    }

    public String[][] localizeTags(String tableName, List<List<String>> tags)
    {
        String[][] stockArr = new String[tags.size()][2];
        tags.stream().map(tag -> new String[]{tag.get(0), tag.get(1)}).collect(Collectors.toList()).toArray(stockArr);
        return localizeTags(tableName, stockArr);
    }

    public String[][] localizeTags(String tableName, Map<String, String> tags)
    {
        String[][] stockArr = new String[tags.size()][2];
        tags.entrySet().stream().map(tag -> new String[]{tag.getKey(), tag.getValue()}).collect(Collectors.toList()).toArray(stockArr);
        return localizeTags(tableName, stockArr);
    }

    public String[][] localizeTags(String tableName, String[][] tags)
    {
        for (String[] tag : tags)
        {
            tag[1] = userAwareMeta.getColumnTitle(tableName, tag[1]);
        }

        return tags;
    }

    public String[][] localizeTags(String tableName, String queryName, String[][] tags)
    {
        for (String[] tag : tags)
        {
            tag[1] = userAwareMeta.getColumnTitle(tableName, queryName, tag[1]);
        }

        return tags;
    }

    public String[][] getTagsFromEnum(String tableName, String name)
    {
        ColumnDef columnDef = meta.getColumn(tableName, name);
        if (columnDef == null) throw new IllegalArgumentException();
        return getTagsFromEnum(columnDef);
    }

    public String[][] getTagsFromEnum(ColumnDef columnDef)
    {
        String tableName = columnDef.getEntity().getName();
        return tagsCache.get(tableName + "getTagsFromEnum" + columnDef.getName() + userInfoProvider.get().getLanguage(), k ->
        {
            String[] enumValues = columnDef.getType().getEnumValues();

            String[][] stockArr = new String[enumValues.length][2];

            for (int i = 0; i < enumValues.length; i++)
            {
                stockArr[i] = new String[]{enumValues[i], userAwareMeta.getColumnTitle(tableName, enumValues[i])};
            }

            return stockArr;
        });
    }

    public String[][] getTagsYesNo()
    {
        return tagsCache.get("getTagsYesNo" + userInfoProvider.get().getLanguage(), k ->
        {
            String[][] arr = new String[2][2];
            arr[0] = new String[]{YES, userAwareMeta.getColumnTitle("query.jsp", "page", YES)};
            arr[1] = new String[]{NO, userAwareMeta.getColumnTitle("query.jsp", "page", NO)};
            return arr;
        });
    }

    public String[][] getTagsNoYes()
    {
        return tagsCache.get("getTagsNoYes" + userInfoProvider.get().getLanguage(), k ->
        {
            String[][] arr = new String[2][2];
            arr[0] = new String[]{NO, userAwareMeta.getColumnTitle("query.jsp", "page", NO)};
            arr[1] = new String[]{YES, userAwareMeta.getColumnTitle("query.jsp", "page", YES)};
            return arr;
        });
    }

    public String[][] addTags(Map<String, String> before, String[][] tags)
    {
        Map<String, String> newTags = new LinkedHashMap<>();

        newTags.putAll(before);
        Arrays.stream(tags).forEach(tag -> newTags.put(tag[0], tag[1]));

        return toTagsArray(newTags);
    }

    public String[][] addTags(Map<String, String> before, String[][] tags, Map<String, String> after)
    {
        Map<String, String> newTags = new LinkedHashMap<>();

        newTags.putAll(before);
        Arrays.stream(tags).forEach(tag -> newTags.put(tag[0], tag[1]));
        newTags.putAll(after);

        return toTagsArray(newTags);
    }

    public String[][] toTagsArray(Map<String, String> tags)
    {
        String[][] stockArr = new String[tags.size()][2];
        tags.entrySet().stream().map(tag -> new String[]{tag.getKey(), tag.getValue()}).collect(Collectors.toList())
                .toArray(stockArr);

        return stockArr;
    }

    public List<QRec> list(String sql, Object... params)
    {
        return db.list(sql, new QRecParser(), params);
    }

/* TODO add
    public <T> List<T> scalarList(String tableName, String queryName, Map<String, ?> parameters)
    {
        return list(meta.getQuery(tableName, queryName), new ScalarParser<T>(), parameters);
    }

    public List<Long> scalarLongList(String tableName, String queryName, Map<String, ?> parameters)
    {
        return list(meta.getQuery(tableName, queryName), new ScalarLongParser(), parameters);
    }

    public <T> List<T> list(Query query, ResultSetParser<T> parser, Map<String, ?> parameters)
    {
        return queryService.build(query, parameters).execute(parser);
    }
*/
    public List<List<Object>> listOfLists(String sql, Object... params)
    {
        List<List<Object>> vals = new ArrayList<>();
        List<QRec> list = list(sql, params);

        for (QRec aList : list)
        {
            List<Object> propertyList = new ArrayList<>();
            for (DynamicProperty property : aList)
            {
                propertyList.add(property.getValue());
            }
            vals.add(propertyList);
        }

        return vals;
    }

    public Map<String, String> map(String query, Object... params)
    {
        Map<String, String> values = new LinkedHashMap<>();
        db.query(query, rs -> {
            while (rs.next())
            {
                values.put(rs.getString(1), rs.getString(2));
            }
            return null;
        }, params);
        return values;
    }

    public QRec qRec(String sql, Object... params)
    {
        return db.select(sql, new QRecParser(), params);
    }

    //TODO rename records()
    public List<QRec> readAsRecordsFromQuery(String sql, Map<String, ?> parameters)
    {
        return readAsRecordsFromQuery(meta.createQueryFromSql(sql), parameters);
    }

    public List<QRec> readAsRecordsFromQuery(String tableName, String queryName, Map<String, ?> parameters)
    {
        return readAsRecordsFromQuery(meta.getQuery(tableName, queryName), parameters);
    }

    public List<QRec> readAsRecordsFromQuery(Query query, Map<String, ?> parameters)
    {
        return queryService.build(query, parameters).execute(new QRecParser());
    }

    //TODO rename one()
    public QRec readOneRecord(String sql, Map<String, ?> parameters)
    {
        return readOneRecord(meta.createQueryFromSql(sql), parameters);
    }

    public QRec readOneRecord(String tableName, String queryName, Map<String, ?> parameters)
    {
        return readOneRecord(meta.getQuery(tableName, queryName), parameters);
    }

    public QRec readOneRecord(Query query, Map<String, ?> parameters)
    {
        return queryService.build(query, parameters).getRow(new QRecParser());
    }

//    public QRec withCache( String sql, Object... params )
//    {
//        throw Be5Exception.internal("not implemented");
//        //return withCache( sql, null );
//    }

}
