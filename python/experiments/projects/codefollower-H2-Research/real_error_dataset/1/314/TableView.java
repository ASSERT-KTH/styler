/*
 * Copyright 2004-2020 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.h2.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.h2.api.ErrorCode;
import org.h2.command.Prepared;
import org.h2.command.ddl.CreateTableData;
import org.h2.command.dml.AllColumnsForPlan;
import org.h2.command.dml.Query;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.Session;
import org.h2.engine.User;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Parameter;
import org.h2.index.Index;
import org.h2.index.IndexType;
import org.h2.index.ViewIndex;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.result.Row;
import org.h2.result.SortOrder;
import org.h2.schema.Schema;
import org.h2.util.ColumnNamer;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/**
 * A view is a virtual table that is defined by a query.
 * @author Thomas Mueller
 * @author Nicolas Fortin, Atelier SIG, IRSTV FR CNRS 24888
 */
/*
         有三种产生TableView的方式:
	1. CREATE VIEW语句

	如: CREATE OR REPLACE FORCE VIEW IF NOT EXISTS my_view COMMENT IS 'my view'(f1,f2) 
			AS SELECT id,name FROM CreateViewTest


	2. 嵌入在FROM中的临时视图

	如: select id,name from (select id,name from CreateViewTest)


	3. WITH RECURSIVE语句

	如: WITH RECURSIVE my_tmp_table(f1,f2) 
			AS (select id,name from CreateViewTest UNION ALL select 1, 2) 
				select f1, f2 from my_tmp_table
	RECURSIVE这个关键字是可选的


	只有2可以带Parameters，它是通过这个方法调用: org.h2.table.TableView.createTempView(Session, User, String, Query, Query)
	只有3的recursive是true 
 */
public class TableView extends Table {

    private static final long ROW_COUNT_APPROXIMATION = 100;

    private String querySQL;
    private ArrayList<Table> tables;
    private Column[] columnTemplates;
    private Query viewQuery;
    private ViewIndex index;
    private boolean allowRecursive;
    private DbException createException;
    private long lastModificationCheck;
    private long maxDataModificationId;
    private User owner;
    private Query topQuery;
    private ResultInterface recursiveResult;
    private boolean isRecursiveQueryDetected;
    private boolean isTableExpression;

    public TableView(Schema schema, int id, String name, String querySQL,
            ArrayList<Parameter> params, Column[] columnTemplates, Session session,
            boolean allowRecursive, boolean literalsChecked, boolean isTableExpression, boolean isTemporary) {
        super(schema, id, name, false, true);
        setTemporary(isTemporary);
        init(querySQL, params, columnTemplates, session, allowRecursive, literalsChecked, isTableExpression);
    }

    /**
     * Try to replace the SQL statement of the view and re-compile this and all
     * dependent views.
     *
     * @param querySQL the SQL statement
     * @param newColumnTemplates the columns
     * @param session the session
     * @param recursive whether this is a recursive view
     * @param force if errors should be ignored
     * @param literalsChecked if literals have been checked
     */
    public void replace(String querySQL,  Column[] newColumnTemplates, Session session,
            boolean recursive, boolean force, boolean literalsChecked) {
        String oldQuerySQL = this.querySQL;
        Column[] oldColumnTemplates = this.columnTemplates;
//<<<<<<< HEAD
//        boolean oldRecursive = this.recursive;
//        // init里执行了一次initColumnsAndTables，虽然执行了两次initColumnsAndTables，但是init中还建立了ViewIndex
//        // 所以这里是必须调用init的
//        init(querySQL, null, columnTemplates, session, recursive);
//        // recompile里又执行了一次initColumnsAndTables
//        DbException e = recompile(session, force, true);
//        if (e != null) {
//            // 如果失败了，按原来的重新来过
//            init(oldQuerySQL, null, oldColumnTemplates, session, oldRecursive);
//=======
        boolean oldRecursive = this.allowRecursive;
        init(querySQL, null, newColumnTemplates, session, recursive, literalsChecked, isTableExpression);
        DbException e = recompile(session, force, true);
        if (e != null) {
            init(oldQuerySQL, null, oldColumnTemplates, session, oldRecursive,
                    literalsChecked, isTableExpression);
            recompile(session, true, false);
            throw e;
        }
    }

    private synchronized void init(String querySQL, ArrayList<Parameter> params,
            Column[] columnTemplates, Session session, boolean allowRecursive, boolean literalsChecked,
            boolean isTableExpression) {
        this.querySQL = querySQL;
        this.columnTemplates = columnTemplates;
        this.allowRecursive = allowRecursive;
        this.isRecursiveQueryDetected = false;
        this.isTableExpression = isTableExpression;
        index = new ViewIndex(this, querySQL, params, allowRecursive);
        initColumnsAndTables(session, literalsChecked);
    }

    private Query compileViewQuery(Session session, String sql, boolean literalsChecked, String viewName) {
        Prepared p;
        session.setParsingCreateView(true, viewName);
        try {
            p = session.prepare(sql, false, literalsChecked);
        } finally {
            session.setParsingCreateView(false, viewName);
        }
        if (!(p instanceof Query)) {
            throw DbException.getSyntaxError(sql, 0);
        }
        Query q = (Query) p;
        // only potentially recursive cte queries need to be non-lazy
        if (isTableExpression && allowRecursive) {
            q.setNeverLazy(true);
        }
        return q;
    }

    /**
     * Re-compile the view query and all views that depend on this object.
     *
     * @param session the session
     * @param force if exceptions should be ignored
     * @param clearIndexCache if we need to clear view index cache
     * @return the exception if re-compiling this or any dependent view failed
     *         (only when force is disabled)
     */
    public synchronized DbException recompile(Session session, boolean force,
            boolean clearIndexCache) {
        try {
            compileViewQuery(session, querySQL, false, getName());
        } catch (DbException e) {
            if (!force) {
                return e;
            }
        }
//<<<<<<< HEAD
//        
//        //如下:
//        //CREATE OR REPLACE FORCE VIEW my_view(f1,f2) AS SELECT id,name FROM CreateViewTest
//		//CREATE OR REPLACE FORCE VIEW view1 AS SELECT f1 FROM my_view
//		//CREATE OR REPLACE FORCE VIEW view2 AS SELECT f2 FROM my_view
//        //view1和view2是建立在my_view这个视图之上，当要recompile my_view时，因为view1和view2依赖了my_view
//        //所以要对他们重新recompile，这里getViews()返回view1和view2
//        ArrayList<TableView> views = getViews();
//        if (views != null) {
//            views = New.arrayList(views);
//        }
//        initColumnsAndTables(session);
//        if (views != null) {
//            for (TableView v : views) {
//                DbException e = v.recompile(session, force, false);
//                if (e != null && !force) {
//                    return e;
//                }
//=======
        ArrayList<TableView> dependentViews = new ArrayList<>(getDependentViews());
        initColumnsAndTables(session, false);
        for (TableView v : dependentViews) {
            DbException e = v.recompile(session, force, false);
            if (e != null && !force) {
                return e;
            }
        }
        if (clearIndexCache) {
            clearIndexCaches(database);
        }
        return force ? null : createException;
    }

    private void initColumnsAndTables(Session session, boolean literalsChecked) {
        Column[] cols;
        removeCurrentViewFromOtherTables();
        setTableExpression(isTableExpression);
        try {
//<<<<<<< HEAD
//            Query query = compileViewQuery(session, querySQL); //重新对select语句进行解析和prepare
//            this.querySQL = query.getPlanSQL();
//            tables = New.arrayList(query.getTables());
//            ArrayList<Expression> expressions = query.getExpressions();
//            ArrayList<Column> list = New.arrayList();
//            
//    		//select字段个数比view字段多的情况，多出来的按select字段原来的算
//    		//这里实际是f1、name
//    		//sql = "CREATE OR REPLACE FORCE VIEW my_view COMMENT IS 'my view'(f1) " //
//    		//		+ "AS SELECT id,name FROM CreateViewTest";
//
//    		//select字段个数比view字段少的情况，view中少的字段被忽略
//    		//这里实际是f1，而f2被忽略了，也不提示错误
//    		//sql = "CREATE OR REPLACE FORCE VIEW my_view COMMENT IS 'my view'(f1, f2) " //
//    		//		+ "AS SELECT id FROM CreateViewTest";
//
//    		//不管加不加FORCE，跟上面也一样
//    		//sql = "CREATE OR REPLACE VIEW my_view COMMENT IS 'my view'(f1, f2) " //
//    		//		+ "AS SELECT id FROM CreateViewTest";
//            
//            //expressions.size有可能大于query.getColumnCount()，因为query.getColumnCount()不包含group by等额外加进来的表达式
//            for (int i = 0, count = query.getColumnCount(); i < count; i++) {
//=======
            Query compiledQuery = compileViewQuery(session, querySQL, literalsChecked, getName());
            this.querySQL = compiledQuery.getPlanSQL(true);
            tables = new ArrayList<>(compiledQuery.getTables());
            ArrayList<Expression> expressions = compiledQuery.getExpressions();
            ColumnNamer columnNamer = new ColumnNamer(session);
            final int count = compiledQuery.getColumnCount();
            ArrayList<Column> list = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                Expression expr = expressions.get(i);
                String name = null;
                //如CREATE OR REPLACE FORCE VIEW IF NOT EXISTS my_view AS SELECT id,name FROM CreateViewTest
                //没有为视图指定字段的时候，用select字段列表中的名字，此时columnTemplates==null
                TypeInfo type = TypeInfo.TYPE_UNKNOWN;
                if (columnTemplates != null && columnTemplates.length > i) {
                    name = columnTemplates[i].getName();
                    type = columnTemplates[i].getType();
                }
                if (name == null) {
                    name = expr.getAlias();
                }
                name = columnNamer.getColumnName(expr, i, name);
                if (type.getValueType() == Value.UNKNOWN) {
                    type = expr.getType();
                }
                Column col = new Column(name, type);
                col.setTable(this, i);
                list.add(col);
            }
            cols = list.toArray(new Column[0]);
            createException = null;
            viewQuery = compiledQuery;
        } catch (DbException e) {
            e.addSQL(getCreateSQL());
            createException = e;
            // If it can't be compiled, then it's a 'zero column table'
            // this avoids problems when creating the view when opening the
            // database.
            // If it can not be compiled - it could also be a recursive common
            // table expression query.
            if (isRecursiveQueryExceptionDetected(createException)) {
                this.isRecursiveQueryDetected = true;
            }
            tables = Utils.newSmallArrayList();
            cols = new Column[0];
            if (allowRecursive && columnTemplates != null) {
                cols = new Column[columnTemplates.length];
                for (int i = 0; i < columnTemplates.length; i++) {
                    cols[i] = columnTemplates[i].getClone();
                }
                index.setRecursive(true);
                createException = null;
            }
        }
        setColumns(cols);
        if (getId() != 0) {
            addDependentViewToTables();
        }
    }

    @Override
    public boolean isView() {
        return true;
    }

    /**
     * Check if this view is currently invalid.
     *
     * @return true if it is
     */
    public boolean isInvalid() {
        return createException != null;
    }

    @Override
    public PlanItem getBestPlanItem(Session session, int[] masks,
            TableFilter[] filters, int filter, SortOrder sortOrder,
            AllColumnsForPlan allColumnsSet) {
        final CacheKey cacheKey = new CacheKey(masks, this);
        Map<Object, ViewIndex> indexCache = session.getViewIndexCache(topQuery != null);
        ViewIndex i = indexCache.get(cacheKey);
        if (i == null || i.isExpired()) {
            i = new ViewIndex(this, index, session, masks, filters, filter, sortOrder);
            indexCache.put(cacheKey, i);
        }
        PlanItem item = new PlanItem();
        item.cost = i.getCost(session, masks, filters, filter, sortOrder, allColumnsSet);
        item.setIndex(i);
        return item;
    }

    @Override
    public boolean isQueryComparable() {
        if (!super.isQueryComparable()) {
            return false;
        }
        for (Table t : tables) {
            if (!t.isQueryComparable()) {
                return false;
            }
        }
        if (topQuery != null &&
                !topQuery.isEverything(ExpressionVisitor.QUERY_COMPARABLE_VISITOR)) {
            return false;
        }
        return true;
    }

    public Query getTopQuery() {
        return topQuery;
    }

    @Override
    public String getDropSQL() {
        return "DROP VIEW IF EXISTS " + getSQL(true) + " CASCADE";
    }

    @Override
    public String getCreateSQLForCopy(Table table, String quotedName) {
        return getCreateSQL(false, true, quotedName);
    }


    @Override
    public String getCreateSQL() {
        return getCreateSQL(false, true);
    }

    /**
     * Generate "CREATE" SQL statement for the view.
     *
     * @param orReplace if true, then include the OR REPLACE clause
     * @param force if true, then include the FORCE clause
     * @return the SQL statement
     */
    public String getCreateSQL(boolean orReplace, boolean force) {
        return getCreateSQL(orReplace, force, getSQL(true));
    }

    private String getCreateSQL(boolean orReplace, boolean force, String quotedName) {
        StringBuilder builder = new StringBuilder("CREATE ");
        if (orReplace) {
            builder.append("OR REPLACE ");
        }
        if (force) {
            builder.append("FORCE ");
        }
        builder.append("VIEW ");
        if (isTableExpression) {
            builder.append("TABLE_EXPRESSION ");
        }
        builder.append(quotedName);
        if (comment != null) {
            builder.append(" COMMENT ");
            StringUtils.quoteStringSQL(builder, comment);
        }
        if (columns != null && columns.length > 0) {
            builder.append('(');
            Column.writeColumns(builder, columns, true);
            builder.append(')');
        } else if (columnTemplates != null) {
            builder.append('(');
            Column.writeColumns(builder, columnTemplates, true);
            builder.append(')');
        }
        return builder.append(" AS\n").append(querySQL).toString();
    }

    @Override
    public boolean lock(Session session, boolean exclusive, boolean forceLockEvenInMvcc) {
        // exclusive lock means: the view will be dropped
        return false;
    }

    @Override
    public void close(Session session) {
        // nothing to do
    }

    @Override
    public void unlock(Session s) {
        // nothing to do
    }

    @Override
    public boolean isLockedExclusively() {
        return false;
    }

    @Override
    public Index addIndex(Session session, String indexName, int indexId,
            IndexColumn[] cols, IndexType indexType, boolean create,
            String indexComment) {
        throw DbException.getUnsupportedException("VIEW");
    }

    @Override
    public void removeRow(Session session, Row row) {
        throw DbException.getUnsupportedException("VIEW");
    }

    @Override
    public void addRow(Session session, Row row) {
        throw DbException.getUnsupportedException("VIEW");
    }

    @Override
    public void checkSupportAlter() {
        throw DbException.getUnsupportedException("VIEW");
    }

    @Override
    public void truncate(Session session) {
        throw DbException.getUnsupportedException("VIEW");
    }

    @Override
    public long getRowCount(Session session) {
        throw DbException.throwInternalError(toString());
    }

    @Override
    public boolean canGetRowCount() {
        // TODO view: could get the row count, but not that easy
        return false;
    }

    @Override
    public boolean canDrop() {
        return true;
    }

    @Override
    public TableType getTableType() {
        return TableType.VIEW;
    }

    @Override
    public void removeChildrenAndResources(Session session) {
        removeCurrentViewFromOtherTables();
        super.removeChildrenAndResources(session);
        database.removeMeta(session, getId());
        querySQL = null;
        index = null;
        clearIndexCaches(database);
        invalidate();
    }

    /**
     * Clear the cached indexes for all sessions.
     *
     * @param database the database
     */
    public static void clearIndexCaches(Database database) {
        for (Session s : database.getSessions(true)) {
            s.clearViewIndexCache();
        }
    }

    @Override
    public StringBuilder getSQL(StringBuilder builder, boolean alwaysQuote) {
        if (isTemporary() && querySQL != null) {
            builder.append("(\n");
            return StringUtils.indent(builder, querySQL, 4, true).append(')');
        }
        return super.getSQL(builder, alwaysQuote);
    }

    public String getQuery() {
        return querySQL;
    }

    @Override
    public Index getScanIndex(Session session) {
        return getBestPlanItem(session, null, null, -1, null, null).getIndex();
    }

    @Override
    public Index getScanIndex(Session session, int[] masks,
            TableFilter[] filters, int filter, SortOrder sortOrder,
            AllColumnsForPlan allColumnsSet) {
        if (createException != null) {
            String msg = createException.getMessage();
            throw DbException.get(ErrorCode.VIEW_IS_INVALID_2,
                    createException, getSQL(false), msg);
        }
        PlanItem item = getBestPlanItem(session, masks, filters, filter, sortOrder, allColumnsSet);
        return item.getIndex();
    }

    @Override
    public boolean canReference() {
        return false;
    }

    @Override
    public ArrayList<Index> getIndexes() {
        return null;
    }

    @Override
    public long getMaxDataModificationId() {
        if (createException != null) {
            return Long.MAX_VALUE;
        }
        if (viewQuery == null) {
            return Long.MAX_VALUE;
        }
        // if nothing was modified in the database since the last check, and the
        // last is known, then we don't need to check again
        // this speeds up nested views
        long dbMod = database.getModificationDataId();
        if (dbMod > lastModificationCheck && maxDataModificationId <= dbMod) {
            maxDataModificationId = viewQuery.getMaxDataModificationId();
            lastModificationCheck = dbMod;
        }
        return maxDataModificationId;
    }

    @Override
    public Index getUniqueIndex() {
        return null;
    }

    private void removeCurrentViewFromOtherTables() {
        if (tables != null) {
            for (Table t : tables) {
                t.removeDependentView(this);
            }
            tables.clear();
        }
    }

    private void addDependentViewToTables() {
        for (Table t : tables) {
            t.addDependentView(this);
        }
    }

    private void setOwner(User owner) {
        this.owner = owner;
    }

    public User getOwner() {
        return owner;
    }

    /**
     * Create a temporary view out of the given query.
     *
     * @param session the session
     * @param owner the owner of the query
     * @param name the view name
     * @param query the query
     * @param topQuery the top level query
     * @return the view table
     */
    public static TableView createTempView(Session session, User owner,
            String name, Query query, Query topQuery) {
        Schema mainSchema = session.getDatabase().getMainSchema();
        String querySQL = query.getPlanSQL(true);
        TableView v = new TableView(mainSchema, 0, name,
                querySQL, query.getParameters(), null /* column templates */, session,
                false/* allow recursive */, true /* literals have already been checked when parsing original query */,
                false /* is table expression */, true/*temporary*/);
        if (v.createException != null) {
            throw v.createException;
        }
        v.setTopQuery(topQuery);
        v.setOwner(owner);
        v.setTemporary(true);
        return v;
    }

    private void setTopQuery(Query topQuery) {
        this.topQuery = topQuery;
    }

    @Override
    public long getRowCountApproximation() {
        return ROW_COUNT_APPROXIMATION;
    }

    @Override
    public long getDiskSpaceUsed() {
        return 0;
    }

    /**
     * Get the index of the first parameter.
     *
     * @param additionalParameters additional parameters
     * @return the index of the first parameter
     */
    public int getParameterOffset(ArrayList<Parameter> additionalParameters) {
        int result = topQuery == null ? -1 : getMaxParameterIndex(topQuery.getParameters());
        if (additionalParameters != null) {
            result = Math.max(result, getMaxParameterIndex(additionalParameters));
        }
        return result + 1;
    }

    private static int getMaxParameterIndex(ArrayList<Parameter> parameters) {
        int result = -1;
        for (Parameter p : parameters) {
            result = Math.max(result, p.getIndex());
        }
        return result;
    }

    public boolean isRecursive() {
        return allowRecursive;
    }

    @Override
    public boolean isDeterministic() {
        if (allowRecursive || viewQuery == null) {
            return false;
        }
        return viewQuery.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR);
    }

    public void setRecursiveResult(ResultInterface value) {
        if (recursiveResult != null) {
            recursiveResult.close();
        }
        this.recursiveResult = value;
    }

    public ResultInterface getRecursiveResult() {
        return recursiveResult;
    }

    @Override
    public void addDependencies(HashSet<DbObject> dependencies) {
        super.addDependencies(dependencies);
        if (tables != null) {
            for (Table t : tables) {
                if (TableType.VIEW != t.getTableType()) {
                    t.addDependencies(dependencies);
                }
            }
        }
    }

    /**
     * The key of the index cache for views.
     */
    private static final class CacheKey {

        private final int[] masks;
        private final TableView view;

        CacheKey(int[] masks, TableView view) {
            this.masks = masks;
            this.view = view;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(masks);
            result = prime * result + view.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            CacheKey other = (CacheKey) obj;
            if (view != other.view) {
                return false;
            }
            return Arrays.equals(masks, other.masks);
        }
    }

    /**
     * Was query recursion detected during compiling.
     *
     * @return true if yes
     */
    public boolean isRecursiveQueryDetected() {
        return isRecursiveQueryDetected;
    }

    /**
     * Does exception indicate query recursion?
     */
    private boolean isRecursiveQueryExceptionDetected(DbException exception) {
        if (exception == null) {
            return false;
        }
        if (exception.getErrorCode() != ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1) {
            return false;
        }
        return exception.getMessage().contains("\"" + this.getName() + "\"");
    }

    public List<Table> getTables() {
        return tables;
    }

    /**
     * Create a view.
     *
     * @param schema the schema
     * @param id the view id
     * @param name the view name
     * @param querySQL the query
     * @param parameters the parameters
     * @param columnTemplates the columns
     * @param session the session
     * @param literalsChecked whether literals in the query are checked
     * @param isTableExpression if this is a table expression
     * @param isTemporary whether the view is persisted
     * @param db the database
     * @return the view
     */
    public static TableView createTableViewMaybeRecursive(Schema schema, int id, String name, String querySQL,
            ArrayList<Parameter> parameters, Column[] columnTemplates, Session session,
            boolean literalsChecked, boolean isTableExpression, boolean isTemporary, Database db) {


        Table recursiveTable = createShadowTableForRecursiveTableExpression(isTemporary, session, name,
                schema, Arrays.asList(columnTemplates), db);

        List<Column> columnTemplateList;
        String[] querySQLOutput = {null};
        ArrayList<String> columnNames = new ArrayList<>();
        for (Column columnTemplate: columnTemplates) {
            columnNames.add(columnTemplate.getName());
        }

        try {
            Prepared withQuery = session.prepare(querySQL, false, false);
            if (!isTemporary) {
                withQuery.setSession(session);
            }
            columnTemplateList = TableView.createQueryColumnTemplateList(columnNames.toArray(new String[1]),
                    (Query) withQuery, querySQLOutput);

        } finally {
            destroyShadowTableForRecursiveExpression(isTemporary, session, recursiveTable);
        }

        // build with recursion turned on
        TableView view = new TableView(schema, id, name, querySQL,
                parameters, columnTemplateList.toArray(columnTemplates), session,
                true/* try recursive */, literalsChecked, isTableExpression, isTemporary);

        // is recursion really detected ? if not - recreate it without recursion flag
        // and no recursive index
        if (!view.isRecursiveQueryDetected()) {
            if (!isTemporary) {
                db.addSchemaObject(session, view);
                view.lock(session, true, true);
                session.getDatabase().removeSchemaObject(session, view);

                // during database startup - this method does not normally get called - and it
                // needs to be to correctly un-register the table which the table expression
                // uses...
                view.removeChildrenAndResources(session);
            } else {
                session.removeLocalTempTable(view);
            }
            view = new TableView(schema, id, name, querySQL, parameters,
                    columnTemplates, session,
                    false/* detected not recursive */, literalsChecked, isTableExpression, isTemporary);
        }

        return view;
    }


    /**
     * Creates a list of column templates from a query (usually from WITH query,
     * but could be any query)
     *
     * @param cols - an optional list of column names (can be specified by WITH
     *            clause overriding usual select names)
     * @param theQuery - the query object we want the column list for
     * @param querySQLOutput - array of length 1 to receive extra 'output' field
     *            in addition to return value - containing the SQL query of the
     *            Query object
     * @return a list of column object returned by withQuery
     */
    public static List<Column> createQueryColumnTemplateList(String[] cols,
            Query theQuery, String[] querySQLOutput) {
        List<Column> columnTemplateList = new ArrayList<>();
        theQuery.prepare();
        // String array of length 1 is to receive extra 'output' field in addition to
        // return value
        querySQLOutput[0] = StringUtils.cache(theQuery.getPlanSQL(true));
        ColumnNamer columnNamer = new ColumnNamer(theQuery.getSession());
        ArrayList<Expression> withExpressions = theQuery.getExpressions();
        for (int i = 0; i < withExpressions.size(); ++i) {
            Expression columnExp = withExpressions.get(i);
            // use the passed in column name if supplied, otherwise use alias
            // (if found) otherwise use column name derived from column
            // expression
            String columnName = columnNamer.getColumnName(columnExp, i, cols);
            columnTemplateList.add(new Column(columnName, columnExp.getType()));

        }
        return columnTemplateList;
    }

    /**
     * Create a table for a recursive query.
     *
     * @param isTemporary whether the table is persisted
     * @param targetSession the session
     * @param cteViewName the name
     * @param schema the schema
     * @param columns the columns
     * @param db the database
     * @return the table
     */
    public static Table createShadowTableForRecursiveTableExpression(boolean isTemporary, Session targetSession,
            String cteViewName, Schema schema, List<Column> columns, Database db) {

        // create table data object
        CreateTableData recursiveTableData = new CreateTableData();
        recursiveTableData.id = db.allocateObjectId();
        recursiveTableData.columns = new ArrayList<>(columns);
        recursiveTableData.tableName = cteViewName;
        recursiveTableData.temporary = isTemporary;
        recursiveTableData.persistData = true;
        recursiveTableData.persistIndexes = !isTemporary;
        recursiveTableData.create = true;
        recursiveTableData.session = targetSession;

        // this gets a meta table lock that is not released
        Table recursiveTable = schema.createTable(recursiveTableData);

        if (!isTemporary) {
            // this unlock is to prevent lock leak from schema.createTable()
            db.unlockMeta(targetSession);
            synchronized (targetSession) {
                db.addSchemaObject(targetSession, recursiveTable);
            }
        } else {
            targetSession.addLocalTempTable(recursiveTable);
        }
        return recursiveTable;
    }

    /**
     * Remove a table for a recursive query.
     *
     * @param isTemporary whether the table is persisted
     * @param targetSession the session
     * @param recursiveTable the table
     */
    public static void destroyShadowTableForRecursiveExpression(boolean isTemporary, Session targetSession,
            Table recursiveTable) {
        if (recursiveTable != null) {
            if (!isTemporary) {
                recursiveTable.lock(targetSession, true, true);
                targetSession.getDatabase().removeSchemaObject(targetSession, recursiveTable);

            } else {
                targetSession.removeLocalTempTable(recursiveTable);
            }

            // both removeSchemaObject and removeLocalTempTable hold meta locks - release them here
            targetSession.getDatabase().unlockMeta(targetSession);
        }
    }
}
