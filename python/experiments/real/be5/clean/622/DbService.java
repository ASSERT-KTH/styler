package com.developmentontheedge.be5.database;

import com.developmentontheedge.be5.database.sql.ResultSetParser;
import com.developmentontheedge.be5.database.sql.SqlExecutor;
import com.developmentontheedge.be5.database.sql.SqlExecutorVoid;
import com.developmentontheedge.be5.database.sql.parsers.ScalarLongParser;
import com.developmentontheedge.be5.database.sql.parsers.ScalarParser;
import com.developmentontheedge.be5.database.util.SqlUtils;
import org.apache.commons.dbutils.ResultSetHandler;

import java.util.List;


public interface DbService
{
    <T> T query(String sql, ResultSetHandler<T> rsh, Object... params);

    <T> T select(String sql, ResultSetParser<T> parser, Object... params);

    <T> List<T> list(String sql, ResultSetParser<T> parser, Object... params);

    <T> T one(String sql, Object... params);

    int update(String sql, Object... params);

    int updateUnsafe(String sql, Object... params);

    <T> T insert(String sql, Object... params);

    <T> T execute(SqlExecutor<T> executor);

    <T> T transactionWithResult(SqlExecutor<T> executor);

    void transaction(SqlExecutorVoid executor);

    default Long oneLong(String sql, Object... params)
    {
        return SqlUtils.longFromDbObject(one(sql, params));
    }

    default String oneString(String sql, Object... params)
    {
        return SqlUtils.stringFromDbObject(one(sql, params));
    }

    default Integer oneInteger(String sql, Object... params)
    {
        return one(sql, params);
    }

    default <T> List<T> scalarList(String sql, Object... params)
    {
        return list(sql, new ScalarParser<T>(), params);
    }

    default List<Long> scalarLongList(String sql, Object... params)
    {
        return list(sql, new ScalarLongParser(), params);
    }

    default Long[] longArray(String sql, Object... params)
    {
        return list(sql, new ScalarLongParser(), params).toArray(new Long[0]);
    }

    default String[] stringArray(String sql, Object... params)
    {
        return list(sql, new ScalarParser<String>(), params).toArray(new String[0]);
    }

}
