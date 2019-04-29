package com.developmentontheedge.be5.metadata.sql.schema;

import com.developmentontheedge.be5.metadata.exception.ProcessInterruptedException;
import com.developmentontheedge.be5.metadata.sql.pojo.IndexInfo;
import com.developmentontheedge.be5.metadata.sql.pojo.SqlColumnInfo;
import com.developmentontheedge.be5.metadata.util.ProcessController;
import com.developmentontheedge.dbms.DbmsConnector;
import com.developmentontheedge.dbms.ExtendedSqlException;
import com.developmentontheedge.dbms.SqlExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class MySqlSchemaReader extends DefaultSchemaReader
{
    private static final Pattern UNNECESSARY_TYPE_LENGTH_PATTERN = Pattern.compile("^(\\w+)\\([\\d,]+\\)");

    @Override
    public Map<String, List<SqlColumnInfo>> readColumns(SqlExecutor sql, String defSchema, ProcessController controller) throws SQLException, ProcessInterruptedException
    {
        DbmsConnector connector = sql.getConnector();
        Map<String, List<SqlColumnInfo>> result = new HashMap<>();
        ResultSet rs = connector.executeQuery("SELECT table_name,column_name,column_type,column_default,is_nullable,"
                + "numeric_precision,numeric_scale,character_maximum_length,extra "
                + "FROM information_schema.columns "
                + "WHERE table_schema='" + defSchema + "' ORDER BY table_name, ordinal_position");
        try
        {
            while (rs.next())
            {
                String tableName = rs.getString(1 /*"table_name"*/).toLowerCase();
                List<SqlColumnInfo> list = result.get(tableName);
                if (list == null)
                {
                    list = new ArrayList<>();
                    result.put(tableName, list);
                }
                SqlColumnInfo info = new SqlColumnInfo();
                list.add(info);
                info.setName(rs.getString(2 /*"column_name"*/));
                String type = rs.getString(3 /*"column_type"*/);
                info.setCanBeNull("YES".equals(rs.getString(5 /* "is_nullable" */)));
                String defaultValue = rs.getString(4 /*"column_default"*/);
                if (defaultValue != null)
                {
                    if (type.startsWith("text") || type.startsWith("enum") || type.startsWith("varchar")
                            || type.startsWith("char"))
                    {
                        defaultValue = "'" + defaultValue + "'";
                    }
                    else if (type.startsWith("date") || type.startsWith("time"))
                    {
                        if (defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP"))
                        {
                            defaultValue = "NOW()";
                        }
                        else
                        {
                            defaultValue = "'" + defaultValue + "'";
                        }
                    }
                }
                info.setDefaultValue(defaultValue);
                info.setSize(rs.getInt(8 /*"character_maximum_length"*/));
                if (rs.wasNull())
                {
                    info.setSize(rs.getInt(6 /*"numeric_precision"*/));
                }
                info.setPrecision(rs.getInt(7 /* "numeric_scale" */));
                info.setAutoIncrement("auto_increment".equals(rs.getString(9 /* "extra" */)));

                if (type.startsWith("enum("))
                {
                    String[] enumValues = type.substring("enum(".length(), type.length() - 1).split(",", -1);
                    for (int i = 0; i < enumValues.length; i++)
                    {
                        enumValues[i] = enumValues[i].substring(1, enumValues[i].length() - 1);
                    }
                    type = "enum";
                    info.setEnumValues(enumValues);
                }
                type = UNNECESSARY_TYPE_LENGTH_PATTERN.matcher(type).replaceFirst("$1");
                info.setType(type.toUpperCase(Locale.ENGLISH));
                controller.setProgress(0); // Just to check for interrupts
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
        ResultSet rs = connector.executeQuery("SELECT table_name,index_name,column_name,non_unique FROM information_schema.statistics "
                + "WHERE table_schema='" + defSchema + "' ORDER BY table_name,index_name,seq_in_index");
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
                    int nonUnique = rs.getInt(4 /*"non_unique"*/);
                    curIndex.setUnique(nonUnique == 0);
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
    public String getDefaultSchema(SqlExecutor sql) throws ExtendedSqlException
    {
        return sql.readString("sql.selectSchema");
    }
}
