package com.developmentontheedge.dbms;

import org.junit.BeforeClass;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public abstract class BaseDbmsTests
{
    protected static SqlExecutor sqlExecutor;
    protected static DbmsConnector connector;
    protected static PrintStream psOut;

    @BeforeClass
    public static void setUpClass() throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        psOut = new PrintStream(out, true, "UTF-8");

        connector = new SimpleConnector(DbmsType.H2, "jdbc:h2:mem:SimpleConnectorTest;DB_CLOSE_DELAY=-1;USER=sa;PASSWORD=sa",
                "sa", "sa");
        connector.executeUpdate("DROP TABLE IF EXISTS persons;CREATE TABLE persons ( id BIGSERIAL PRIMARY KEY, name VARCHAR(255) NOT NULL, " +
                "password VARCHAR(255) NOT NULL, email VARCHAR(255), age INT);");
        sqlExecutor = new SqlExecutor(connector, psOut, SqlExecutor.getDefaultPropertiesFile());
        sqlExecutor.testConnection();
    }
}
