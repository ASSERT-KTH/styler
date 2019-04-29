package com.developmentontheedge.be5.metadata.sql.schema;

import com.developmentontheedge.be5.metadata.exception.ProcessInterruptedException;
import com.developmentontheedge.be5.metadata.model.ColumnFunction;
import com.developmentontheedge.be5.metadata.sql.pojo.IndexInfo;
import com.developmentontheedge.be5.metadata.sql.pojo.SqlColumnInfo;
import com.developmentontheedge.be5.metadata.util.ProcessController;
import com.developmentontheedge.dbms.DbmsConnector;
import com.developmentontheedge.dbms.ExtendedSqlException;
import com.developmentontheedge.dbms.SqlExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlServerSchemaReader extends DefaultSchemaReader
{
    private static final Pattern ENUM_VALUES_PATTERN = Pattern.compile("=\'(.+?)\'");

    private static final Pattern DATE_DEFVALUE_PATTERN = Pattern.compile("^CONVERT\\(\\[date\\],'(\\d+\\-\\d+\\-\\d+)',\\(120\\)\\)$");

    private static final Pattern GENERIC_REF_COLUMN_PATTERN = Pattern.compile("\\('(\\w+)\\.'\\+CONVERT\\([^,]+,\\[(\\w+)\\]\\)\\)");

    @Override
    public String getDefaultSchema(SqlExecutor sql) throws ExtendedSqlException
    {
        return sql.readString("sql.selectSchema");
    }

    @Override
    public Map<String, List<IndexInfo>> readIndices(SqlExecutor sql, String defSchema, ProcessController controller) throws SQLException, ProcessInterruptedException
    {
        DbmsConnector connector = sql.getConnector();
        Map<String, List<IndexInfo>> result = new HashMap<>();
        ResultSet rs = connector.executeQuery("SELECT st.name AS \"table_name\","
                + "si.name AS \"index_name\","
                + "sc.name AS \"column_name\","
                + "si.is_unique "
                + "FROM sys.indexes si "
                + "JOIN sys.tables st ON (si.object_ID=st.object_ID) "
                + "JOIN sys.index_columns sic ON (sic.object_id=si.object_id AND sic.index_id = si.index_id) "
                + "JOIN sys.columns sc ON (sc.object_id=sic.object_id AND sc.column_id=sic.column_id) "
                + "JOIN sys.schemas ss ON (st.schema_id=ss.schema_id) " + (defSchema == null ? "" : "WHERE ss.name='" + defSchema + "' ")
                + "ORDER by st.object_id,si.index_id,sic.key_ordinal ");
        try
        {
            IndexInfo curIndex = null;
            String lastTable = null;
            while (rs.next())
            {
                String tableName = rs.getString(1 /*"table_name"*/).toLowerCase(Locale.ENGLISH);
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
                    curIndex.setUnique(rs.getBoolean(4 /*"is_unique"*/));
                }
                String column = rs.getString(3 /*"column_name"*/);
                curIndex.addColumn(column);
            }
        }
        finally
        {
            connector.close(rs);
        }
        return result;
    }

    @Override
    public Map<String, List<SqlColumnInfo>> readColumns(SqlExecutor sql, String defSchema, ProcessController controller) throws SQLException, ProcessInterruptedException
    {
        DbmsConnector connector = sql.getConnector();
        Map<String, List<SqlColumnInfo>> result = new HashMap<>();
        ResultSet rs = connector.executeQuery("SELECT " +
                "c.table_name, " +
                "c.column_name, " +
                "c.column_default, " +
                "c.data_type, " +
                "c.character_maximum_length, " +
                "c.numeric_precision, " +
                "c.numeric_scale, " +
                "c.is_nullable, " +
                "sc.is_identity, " +
                "ch.check_clause, " +
                "scc.definition " +
                "FROM information_schema.columns c " +
                "JOIN sys.columns sc ON (sc.object_id=object_id(c.table_name) AND sc.name = c.column_name) " +
                "LEFT JOIN sys.computed_columns scc ON (scc.object_id=object_id(c.table_name) AND scc.name = c.column_name) " +
                "LEFT JOIN information_schema.constraint_column_usage cc ON (cc.table_name=c.table_name AND cc.table_schema=c.table_schema AND cc.column_name=c.column_name) " +
                "LEFT JOIN information_schema.table_constraints tc ON (cc.constraint_name = tc.constraint_name) " +
                "LEFT JOIN information_schema.check_constraints ch ON (cc.constraint_name = ch.constraint_name) " +
                "WHERE (ch.check_clause IS NULL OR tc.constraint_type = 'CHECK') " + (defSchema == null ? "" : "AND c.table_schema='" + defSchema + "' ") +
                "ORDER BY c.table_name,c.ordinal_position");
        try
        {
            while (rs.next())
            {
                controller.setProgress(0); // Just to check for interrupts
                String tableName = rs.getString(1 /*"table_name"*/).toLowerCase(Locale.ENGLISH);
                List<SqlColumnInfo> list = result.get(tableName);
                if (list == null)
                {
                    list = new ArrayList<>();
                    result.put(tableName, list);
                }
                SqlColumnInfo info = new SqlColumnInfo();
                list.add(info);
                info.setName(rs.getString(2 /*"column_name"*/));
                info.setType(rs.getString(4 /*"data_type"*/));
                info.setCanBeNull("YES".equals(rs.getString(8 /*"is_nullable"*/)));
                String defaultValue = rs.getString(3 /*"column_default"*/);
                while (defaultValue != null && defaultValue.startsWith("(") && defaultValue.endsWith(")"))
                {
                    defaultValue = defaultValue.substring(1, defaultValue.length() - 1);
                }
                if (defaultValue != null)
                {
                    defaultValue = DATE_DEFVALUE_PATTERN.matcher(defaultValue).replaceFirst("'$1'");
                }
                info.setDefaultValue(defaultValue);
                info.setSize(rs.getInt(5 /*"character_maximum_length"*/));
                if (rs.wasNull())
                {
                    info.setSize(rs.getInt(6 /*"numeric_precision"*/));
                }
                info.setPrecision(rs.getInt(7 /* "numeric_precision" */));
                info.setAutoIncrement(rs.getBoolean(9 /* "is_identity" */));

                // ENUM VALUES
                String check = rs.getString(10 /* "check_clause" */);
                if (check != null)
                {
                    Matcher matcher = ENUM_VALUES_PATTERN.matcher(check);
                    List<String> vals = new ArrayList<>();
                    while (matcher.find())
                    {
                        vals.add(matcher.group(1));
                    }
                    Collections.sort(vals);
                    info.setEnumValues(vals.toArray(new String[vals.size()]));
                }
                // GENERATED column
                String definition = rs.getString(11 /* "definition" */);
                if (definition != null)
                {
                    Matcher matcher = GENERIC_REF_COLUMN_PATTERN.matcher(definition);
                    if (matcher.matches())
                    {
                        String colName = matcher.group(2);
                        info.setDefaultValue(new ColumnFunction(colName, ColumnFunction.TRANSFORM_GENERIC).toString());
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

    @Override
    public Map<String, String> readTableNames(SqlExecutor sql, String defSchema, ProcessController controller) throws SQLException
    {
        DbmsConnector connector = sql.getConnector();
        Map<String, String> result = new HashMap<>();
        ResultSet rs = connector.executeQuery("SELECT table_name,table_type FROM information_schema.tables t WHERE table_schema='" + defSchema + "' AND table_type IN ('BASE TABLE','VIEW')");
        try
        {
            while (rs.next())
            {
                String tableName = rs.getString(1 /*"table_name"*/).toLowerCase();
                String type = rs.getString(2 /*"table_type"*/);
                if ("BASE TABLE".equals(type))
                {
                    type = "TABLE";
                }
                result.put(tableName, type);
            }
        }
        finally
        {
            connector.close(rs);
        }
        return result;
    }


}
