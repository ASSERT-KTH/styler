package com.developmentontheedge.be5.metadata.sql.schema;

import com.developmentontheedge.be5.metadata.exception.ProcessInterruptedException;
import com.developmentontheedge.be5.metadata.model.ColumnFunction;
import com.developmentontheedge.be5.metadata.sql.pojo.IndexInfo;
import com.developmentontheedge.be5.metadata.sql.pojo.SqlColumnInfo;
import com.developmentontheedge.be5.metadata.util.ProcessController;
import com.developmentontheedge.dbms.DbmsConnector;
import com.developmentontheedge.dbms.SqlExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OracleSchemaReader extends DefaultSchemaReader
{
    private static final Pattern GENERIC_REF_INDEX_PATTERN = Pattern.compile("^\'(\\w+)\\.\'\\|\\|\"(\\w+)\"$");
    private static final Pattern LOWER_INDEX_PATTERN = Pattern.compile("^LOWER\\(\"(\\w+)\"\\)$");
    private static final Pattern UPPER_INDEX_PATTERN = Pattern.compile("^UPPER\\(\"(\\w+)\"\\)$");

    private static final Pattern DEFAULT_DATE_PATTERN = Pattern.compile("^TO_DATE\\('(\\d+\\-\\d+\\-\\d+)','YYYY-MM-DD'\\)$");

    private static final Pattern GENERATED_TRIGGER_PATTERN = Pattern.compile("^BEGIN :new\\.\"?(\\w+)\"? := '(\\w+)\\.' \\|\\| :new\\.(\\w+); END;");

    @Override
    public Map<String, List<SqlColumnInfo>> readColumns(SqlExecutor sql, String defSchema, ProcessController controller) throws SQLException
    {
        DbmsConnector connector = sql.getConnector();
        Map<String, List<SqlColumnInfo>> columns = new HashMap<>();
        ResultSet rs = connector.executeQuery("SELECT "
                + "c.table_name,"
                + "c.column_name,"
                + "c.data_type,"
                + "c.char_length,"
                + "c.data_precision,"
                + "c.data_scale,"
                + "c.nullable "
                + "from user_tab_cols c" +
                " JOIN entities e ON (UPPER(e.name)=c.table_name)" +
                " WHERE NOT(c.column_id IS NULL) ORDER BY c.table_name,c.column_id");
        try
        {
            while (rs.next())
            {
                String tableName = rs.getString(1 /*"table_name"*/).toLowerCase();
                List<SqlColumnInfo> list = columns.get(tableName);
                if (list == null)
                {
                    list = new ArrayList<>();
                    columns.put(tableName, list);
                }
                SqlColumnInfo info = new SqlColumnInfo();
                list.add(info);
                info.setName(rs.getString(2 /*"column_name"*/));
                info.setType(rs.getString(3 /*"data_type"*/));
                info.setCanBeNull("Y".equals(rs.getString(7 /*"nullable"*/)));
                info.setSize(rs.getInt(5 /*"data_precision"*/));
                if (rs.wasNull())
                {
                    info.setSize(rs.getInt(4 /*"char_length"*/));
                }
                info.setPrecision(rs.getInt(6 /* "data_scale" */));
            }
        }
        finally
        {
            connector.close(rs);
        }

        readDefaultValues(sql, columns);
        readEnumValues(sql, columns);
        readTrigger(sql, columns);

        return columns;
    }

    /**
     * Read default values as separate query, because it's LONG column which is streaming and
     * transmitted slowly
     */
    private void readDefaultValues(SqlExecutor sql, Map<String, List<SqlColumnInfo>> columns) throws SQLException
    {
        DbmsConnector connector = sql.getConnector();
        ResultSet rs = connector.executeQuery("SELECT "
                + "c.data_default,"
                + "c.table_name,"
                + "c.column_name "
                + "from user_tab_cols c" +
                " JOIN entities e ON (UPPER(e.name)=c.table_name)" +
                " WHERE NOT(c.column_id IS NULL) AND NOT (data_default IS NULL) ORDER BY c.table_name");
        try
        {
            while (rs.next())
            {
                // Read streaming column at first
                String defaultValue = rs.getString(1 /* data_default */);
                String tableName = rs.getString(2 /*"table_name"*/);
                String columnName = rs.getString(3 /*"column_name"*/);
                SqlColumnInfo column = findColumn(columns, tableName, columnName);
                if (column == null)
                    continue;
                defaultValue = defaultValue.trim();
                defaultValue = DEFAULT_DATE_PATTERN.matcher(defaultValue).replaceFirst("'$1'");
                if ("'auto-identity'".equals(defaultValue))
                {
                    column.setAutoIncrement(true);
                }
                else
                {
                    column.setDefaultValue(defaultValue);
                }
            }
        }
        finally
        {
            connector.close(rs);
        }
    }

    /**
     *
     */
    private void readEnumValues(SqlExecutor sql, Map<String, List<SqlColumnInfo>> columns) throws SQLException
    {
        DbmsConnector connector = sql.getConnector();
        /*rs = connector.executeQuery( "SELECT uc.SEARCH_CONDITION,uc.TABLE_NAME FROM user_constraints uc "
            + " JOIN entities e ON (UPPER(e.name)=uc.table_name)"
            + " WHERE uc.CONSTRAINT_TYPE = 'C'" );*/
        // The following query works faster (much faster!) as it doesn't return "NOT NULL" constraints
        // though it's probably Oracle version specific (at least undocumented)
        // tested on Oracle 11r2
        ResultSet rs = connector.executeQuery("SELECT c.condition,o.name "
                + "FROM sys.cdef$ c, sys.\"_CURRENT_EDITION_OBJ\" o,entities e "
                + "WHERE c.type#=1 AND c.obj# = o.obj# "
                + "AND o.owner# = userenv('SCHEMAID') "
                + "AND UPPER(e.name)=o.name");
        try
        {
            while (rs.next())
            {
                String constr = rs.getString(1);
                String table = rs.getString(2);
                // ENUM VALUES
                // Copied from OperationSupport.loadEntityEnums
                StringTokenizer st = new StringTokenizer(constr.trim());
                int nTok = st.countTokens();
                if (nTok < 3)
                {
                    continue;
                }
                String colName = st.nextToken().toUpperCase();
                String in = st.nextToken();
                if (!"IN".equalsIgnoreCase(in))
                {
                    continue;
                }
                SqlColumnInfo column = findColumn(columns, table, colName);
                if (column == null)
                {
                    continue;
                }
                List<String> values = new ArrayList<>();
                try
                {
                    do
                    {
                        String val = st.nextToken("(,')");
                        if (!val.trim().isEmpty())
                        {
                            values.add(val);
                        }
                    }
                    while (st.hasMoreTokens());
                }
                catch (NoSuchElementException ignore)
                {
                }
                if (values.size() > 0)
                {
                    column.setEnumValues(values.toArray(new String[values.size()]));
                }
            }
        }
        finally
        {
            connector.close(rs);
        }
    }

    private void readTrigger(SqlExecutor sql, Map<String, List<SqlColumnInfo>> columns) throws SQLException
    {
        DbmsConnector connector = sql.getConnector();
        ResultSet rs = connector.executeQuery("SELECT trigger_name,table_name,trigger_body FROM user_triggers "
                + "WHERE triggering_event='INSERT OR UPDATE' "
                + "AND TRIGGER_TYPE='BEFORE EACH ROW'");
        try
        {
            while (rs.next())
            {
                // Read streaming column as first
                String triggerBody = rs.getString(3);
                String tableName = rs.getString(2);

                Matcher matcher = GENERATED_TRIGGER_PATTERN.matcher(triggerBody);
                if (matcher.find())
                {
                    String columnName = matcher.group(1);
                    String targetName = matcher.group(3);
                    SqlColumnInfo column = findColumn(columns, tableName, columnName);
                    if (column == null)
                        continue;
                    column.setDefaultValue(new ColumnFunction(targetName, ColumnFunction.TRANSFORM_GENERIC).toString());
                }
            }
        }
        finally
        {
            connector.close(rs);
        }
    }

    private SqlColumnInfo findColumn(Map<String, List<SqlColumnInfo>> result, String table, String colName)
    {
        List<SqlColumnInfo> list = result.get(table.toLowerCase());
        if (list == null)
            return null;
        for (SqlColumnInfo column : list)
        {
            if (colName.equals(column.getName()))
                return column;
        }
        return null;
    }

    @Override
    public Map<String, String> readTableNames(SqlExecutor sql, String defSchema, ProcessController controller) throws SQLException
    {
        DbmsConnector connector = sql.getConnector();
        Map<String, String> result = new HashMap<>();
        ResultSet rs = connector.executeQuery("SELECT "
                + "t.table_name FROM user_tables t JOIN entities e ON (UPPER(e.name)=t.table_name)");
        try
        {
            while (rs.next())
            {
                String tableName = rs.getString(1 /*"table_name"*/);
                result.put(tableName.toLowerCase(), "TABLE");
            }
        }
        finally
        {
            connector.close(rs);
        }
        rs = connector.executeQuery("SELECT "
                + "view_name FROM user_views v JOIN entities e ON (UPPER(e.name)=v.view_name)");
        try
        {
            while (rs.next())
            {
                String viewName = rs.getString(1 /*"view_name"*/);
                result.put(viewName.toLowerCase(), "VIEW");
            }
        }
        finally
        {
            connector.close(rs);
        }
        return result;
    }

    @Override
    public Map<String, List<IndexInfo>> readIndices(SqlExecutor sql, String defSchema, ProcessController controller) throws SQLException, ProcessInterruptedException
    {
        DbmsConnector connector = sql.getConnector();
        Map<String, List<IndexInfo>> result = new HashMap<>();
        ResultSet rs = connector.executeQuery("SELECT "
                + "i.table_name,i.index_name,ic.column_name,i.uniqueness "
                + "FROM user_indexes i "
                + "JOIN user_ind_columns ic ON i.index_name=ic.index_name "
                + "JOIN entities e ON (UPPER(e.name)=i.table_name) "
                + "ORDER BY i.table_name,i.index_name,ic.column_position");
        try
        {
            IndexInfo curIndex = null;
            String lastTable = null;
            while (rs.next())
            {
                String tableName = rs.getString(1 /*"table_name"*/).toLowerCase();
                String indexName = rs.getString(2 /*"index_name"*/);
                if (!tableName.equals(lastTable) || curIndex == null || !curIndex.getName().equals(indexName))
                {
                    List<IndexInfo> list = result.get(tableName);
                    if (list == null)
                    {
                        list = new ArrayList<>();
                        result.put(tableName, list);
                    }
                    curIndex = new IndexInfo();
                    lastTable = tableName;
                    list.add(curIndex);
                    curIndex.setName(indexName);
                    curIndex.setUnique("UNIQUE".equals(rs.getString(4 /* "uniqueness" */)));
                }
                String column = rs.getString(3 /*"column_name"*/);
                curIndex.addColumn(column);
            }
        }
        finally
        {
            connector.close(rs);
        }
        // Read functional indices separately
        // as this query contains streaming column which is read slowly
        rs = connector.executeQuery("SELECT "
                + "i.table_name,i.index_name,ic.column_position,c.data_default "
                + "FROM user_indexes i "
                + "JOIN user_ind_columns ic ON i.index_name=ic.index_name "
                + "JOIN entities e ON (UPPER(e.name)=i.table_name) "
                + "JOIN user_tab_cols c ON (c.column_name=ic.column_name AND c.table_name=ic.table_name) "
                + "WHERE c.virtual_column='YES' "
                + "ORDER BY i.table_name,i.index_name,ic.column_position");
        try
        {
            while (rs.next())
            {
                // Read streaming column at first
                String defaultValue = rs.getString(4 /* data_default */);
                String tableName = rs.getString(1 /* "table_name" */).toLowerCase();
                String indexName = rs.getString(2 /* "index_name" */);
                int pos = rs.getInt(3 /* "column_position" */) - 1;
                List<IndexInfo> list = result.get(tableName);
                if (list == null)
                    continue;
                for (IndexInfo indexInfo : list)
                {
                    if (indexInfo.getName().equals(indexName))
                    {
                        defaultValue = GENERIC_REF_INDEX_PATTERN.matcher(defaultValue).replaceFirst("generic($2)");
                        defaultValue = UPPER_INDEX_PATTERN.matcher(defaultValue).replaceFirst("upper($1)");
                        defaultValue = LOWER_INDEX_PATTERN.matcher(defaultValue).replaceFirst("lower($1)");
                        indexInfo.getColumns().set(pos, defaultValue);
                    }
                }
            }
        }
        finally
        {
            connector.close(rs);
        }
        return result;
    }

}
