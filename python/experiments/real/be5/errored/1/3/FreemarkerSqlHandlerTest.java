package com.developmentontheedge.be5.metadata.freemarker;

import com.developmentontheedge.be5.metadata.exception.FreemarkerSqlException;
import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.model.DataElementUtils;
import com.developmentontheedge.be5.metadata.model.FreemarkerScript;
import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.sql.Rdbms;
import com.developmentontheedge.be5.metadata.util.WriterLogger;
import com.developmentontheedge.dbms.DbmsConnector;
import com.developmentontheedge.dbms.DbmsType;
import com.developmentontheedge.dbms.SqlExecutor;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class FreemarkerSqlHandlerTest
{
    private static class NullConnector implements DbmsConnector
    {
        @Override
        public DbmsType getType()
        {
            return DbmsType.POSTGRESQL;
        }

        @Override
        public String getConnectString()
        {
            return "jdbc:null";
        }

        @Override
        public int executeUpdate(String query) throws SQLException
        {
            return 0;
        }

        @Override
        public ResultSet executeQuery(String sql) throws SQLException
        {
            return null;
        }

        @Override
        public String executeInsert(String sql) throws SQLException
        {
            return "1";
        }

        @Override
        public void close(ResultSet rs)
        {
        }

        @Override
        public Connection getConnection() throws SQLException
        {
            throw new UnsupportedOperationException();
        }
//
//        @Override
//        public void releaseConnection( Connection conn )
//        {
//        }
    }

    @Test
    public void testHandler() throws UnsupportedEncodingException, IOException, FreemarkerSqlException, ProjectElementException
    {
        ByteArrayOutputStream log = new ByteArrayOutputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(log, true, "UTF-8");
        PrintStream psOut = new PrintStream(out, true, "UTF-8");
        SqlExecutor executor = new SqlExecutor(new NullConnector(), ps,
                SqlExecutor.class.getResource("basesql.properties"));
        FreemarkerSqlHandler handler = new FreemarkerSqlHandler(executor, true, new WriterLogger(psOut));
        Project proj = new Project("test");
        proj.setDatabaseSystem(Rdbms.POSTGRESQL);
        FreemarkerScript sql = new FreemarkerScript("sql", proj.getApplication().getFreemarkerScripts());
        DataElementUtils.save(sql);
        FreemarkerScript sql2 = new FreemarkerScript("sql2", proj.getApplication().getFreemarkerScripts());
        DataElementUtils.save(sql2);
        sql2.setSource("UPDATE test SET b = 'c';");
        sql.setSource("delete from test;-- hehehe\nINSERT INTO \"test\" VALUES('a','b','c');\nBEGIN update test SET a='a''b END;';END;\nDELETE FROM test;<#include 'sql2'/>");
        handler.execute(sql);
        String result = new String(log.toByteArray(), StandardCharsets.UTF_8);
        String expected = "\n" +
                "-- Execute test/application/Scripts/sql\n" +
                "-- At test/application/Scripts/sql[1,1]-[1,17]\n" +
                "delete from test;\n" +
                "-- At test/application/Scripts/sql[2,1]-[2,39]\n" +
                "INSERT INTO \"test\" VALUES('a','b','c');\n" +
                "-- At test/application/Scripts/sql[3,1]-[3,40]\n" +
                "BEGIN update test SET a='a''b END;';END;\n" +
                "-- At test/application/Scripts/sql[4,1]-[4,17]\n" +
                "DELETE FROM test;\n" +
                "-- At test/application/Scripts/sql[4,18]-[4,35]\n" +
                "-- At test/application/Scripts/sql2[1,1]-[1,24]\n" +
                "\n" +
                "-- Start of included test/application/Scripts/sql2\n" +
                "UPDATE test SET b = 'c';\n" +
                "-- End of included test/application/Scripts/sql2\n";
        assertEquals(expected, result.replace("\r", ""));
        String outResult = new String(out.toByteArray(), StandardCharsets.UTF_8);
        String outExpected = "xx:xx:xx: [>] test/application/Scripts/sql\n" +
                "xx:xx:xx: [>]   test/application/Scripts/sql2\n";
        assertEquals(outExpected, outResult.replace("\r", "").replaceAll("\\d\\d", "xx"));
    }
}
