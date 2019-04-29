package com.developmentontheedge.be5.metadata.sql.schema;

import com.developmentontheedge.be5.metadata.util.ProcessController;
import com.developmentontheedge.dbms.DbmsConnector;
import com.developmentontheedge.dbms.ExtendedSqlException;
import com.developmentontheedge.dbms.SqlExecutor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class DefaultSchemaReader implements DbmsSchemaReader
{
    @Override
    public String getDefaultSchema(SqlExecutor sql) throws ExtendedSqlException
    {
        DbmsConnector connector = sql.getConnector();
        try
        {
            Connection connection = connector.getConnection();

            return connection.getMetaData().getUserName();
        }
        catch (SQLException ex)
        {
            throw new ExtendedSqlException(connector.getConnectString(), "getMetaData().getUserName()", ex);
        }
    }

    @Override
    public Map<String, String> readTableNames(SqlExecutor sql, String defSchema, ProcessController controller) throws SQLException
    {
        DbmsConnector connector = sql.getConnector();
        Connection connection = connector.getConnection();
        ResultSet rs = null;
        Map<String, String> result = new HashMap<>();
        try
        {
            rs = connection.getMetaData().getTables(null, defSchema, null, new String[]{"TABLE", "VIEW"});
            while (rs.next())
            {
                String name = rs.getString(3 /*"TABLE_NAME"*/).toLowerCase();
                String type = rs.getString(4 /*"TABLE_TYPE"*/);
                result.put(name, type);
            }
        }
        finally
        {
            connector.close(rs);
        }
        return result;
    }
}
