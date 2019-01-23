package com.developmentontheedge.be5.database.impl;

import com.developmentontheedge.be5.database.DbService;
import com.developmentontheedge.sql.format.Ast;
import com.google.common.collect.ObjectArrays;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.developmentontheedge.sql.model.AstWhere.NOT_NULL;


public class SqlHelper
{
    private final DbService db;

    @Inject
    public SqlHelper(DbService db)
    {
        this.db = db;
    }

    public <T> T insert(String tableName, Map<String, ?> values)
    {
        return db.insert(generateInsertSql(tableName, values), values.values().toArray());
    }

    public int update(String tableName, Map<String, ?> conditions, Map<String, ?> values)
    {
        Map<String, String> conditionsPlaceholders = conditions.entrySet().stream()
                .collect(toLinkedMap(Map.Entry::getKey, e -> "?"));

        Map<String, String> valuePlaceholders = values.entrySet().stream()
                .collect(toLinkedMap(Map.Entry::getKey, e -> "?"));

        return db.update(generateUpdateSql(tableName, conditionsPlaceholders, valuePlaceholders),
                ObjectArrays.concat(values.values().toArray(), conditions.values().toArray(), Object.class));
    }

    public int updateIn(String tableName, String primaryKeyName, Object[] primaryKeyValue, Map<String, ?> values)
    {
        Map<String, String> valuePlaceholders = values.entrySet().stream()
                .collect(toLinkedMap(Map.Entry::getKey, e -> "?"));

        return db.update(generateUpdateInSql(tableName, primaryKeyName, primaryKeyValue.length, valuePlaceholders),
                ObjectArrays.concat(values.values().toArray(), primaryKeyValue, Object.class));
    }

    public int delete(String tableName, Map<String, ?> conditions)
    {
        return db.update(generateDeleteSql(tableName, conditions), getWithoutConstants(conditions));
    }

    public int deleteIn(String tableName, String columnName, Object[] values)
    {
        return db.update(generateDeleteInSql(tableName, columnName, values.length), values);
    }

    private String generateInsertSql(String tableName, Map<String, ?> values)
    {
        Object[] columns = values.keySet().toArray();

        Object[] valuePlaceholders = values.keySet().stream()
                .map(x -> "?")
                .toArray(Object[]::new);

        return Ast.insert(tableName).fields(columns).values(valuePlaceholders).format();
    }

    private String generateUpdateSql(String tableName, Map<String, String> conditionsPlaceholders, Map<String, String> valuePlaceholders)
    {
        return Ast.update(tableName).set(valuePlaceholders)
                .where(conditionsPlaceholders).format();
    }

    private String generateUpdateInSql(String tableName, String primaryKeyName, int count, Map<String, String> valuePlaceholders)
    {
        return Ast.update(tableName).set(valuePlaceholders)
                .whereInWithReplacementParameter(primaryKeyName, count).format();
    }

    private String generateDeleteSql(String tableName, Map<String, ?> conditions)
    {
        return Ast.delete(tableName).where(conditions).format();
    }

    private String generateDeleteInSql(String tableName, String columnName, int count)
    {
        return Ast.delete(tableName).whereInPredicate(columnName, count).format();
    }

    public Object[] getWithoutConstants(Map<String, ?> conditions)
    {
        List<?> list = conditions.values().stream()
                .filter(x -> x != null && !NOT_NULL.equals(x))
                .collect(Collectors.toList());
        return list.toArray();
    }

    private static <T, K, U> Collector<T, ?, Map<K, U>> toLinkedMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper)
    {
        return Collectors.toMap(keyMapper, valueMapper,
                (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                },
                LinkedHashMap::new);
    }
}
