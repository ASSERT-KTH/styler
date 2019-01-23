package com.developmentontheedge.be5.query;

import com.developmentontheedge.be5.database.sql.ResultSetParser;
import com.developmentontheedge.beans.DynamicPropertySet;

import java.util.List;

/**
 * A way to run queries from the project model.
 * Note that a query executor has its state. Don't reuse the same executor for several requests.
 *
 * @author asko
 */
public interface QueryExecutor
{
    /**
     * Sets a limit (changes state). Returns the query executor itself.
     */
    QueryExecutor limit(int limit);

    /**
     * Sets an offset (changes state). Returns the query executor itself.
     */
    QueryExecutor offset(int offset);

    /**
     * Sets sort order (changes state). Returns the query executor itself.
     */
    QueryExecutor order(int orderColumn, String orderDir);

    QueryExecutor selectable(boolean selectable);

    /**
     * Executes the query.
     */
    List<DynamicPropertySet> execute();

    //List<DynamicPropertySet> execute(Object... params);

    /**
     * Executes the query for aggregate.
     */
    List<DynamicPropertySet> executeAggregate();

    /**
     * Executes the query.
     */
    <T> List<T> execute(ResultSetParser<T> parser);

    <T> T getRow(ResultSetParser<T> parser);

    /**
     * Counts the number of resulting rows.
     */
    long count();

    DynamicPropertySet getRow();

    /**
     * Returns a list of column names.
     */
    List<String> getColumnNames();

    List<DynamicPropertySet> executeSubQuery(String subqueryName, VarResolver varResolver);

    int getOrderColumn();

    String getOrderDir();

    int getOffset();

    int getLimit();

    Boolean getSelectable();


    //QueryExecutor setContextApplier(ContextApplier contextApplier);
}
