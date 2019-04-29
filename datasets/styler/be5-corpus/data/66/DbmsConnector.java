package com.developmentontheedge.dbms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DbmsConnector
{
    DbmsType getType();

    String getConnectString();

    int executeUpdate(String query) throws SQLException;

    ResultSet executeQuery(String sql) throws SQLException;

    String executeInsert(String sql) throws SQLException;

    void close(ResultSet rs);

    Connection getConnection() throws SQLException;

    //void releaseConnection( Connection conn );

}
