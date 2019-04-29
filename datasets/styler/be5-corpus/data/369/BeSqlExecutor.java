package com.developmentontheedge.be5.metadata.sql;

import com.developmentontheedge.be5.metadata.exception.FreemarkerSqlException;
import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.freemarker.FreemarkerSqlHandler;
import com.developmentontheedge.be5.metadata.model.FreemarkerScript;
import com.developmentontheedge.be5.metadata.util.ProcessController;
import com.developmentontheedge.dbms.DbmsConnector;
import com.developmentontheedge.dbms.SqlExecutor;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Class helps to execute SQL queries using only query name and list of arguments.
 * All queries are stored in special properties file: <code>sql.properties</code>.
 */
public class BeSqlExecutor extends SqlExecutor
{

    public BeSqlExecutor(DbmsConnector connector) throws IOException
    {
        this(connector, null);
    }

    public BeSqlExecutor(DbmsConnector connector, PrintStream log) throws IOException
    {
        super(connector, log, BeSqlExecutor.class.getResource("sql.properties"));
    }

    public void executeScript(FreemarkerScript script, ProcessController log) throws ProjectElementException, FreemarkerSqlException
    {
        if (script == null || script.getSource().trim().isEmpty())
            return;
        try
        {
            new FreemarkerSqlHandler(this, false, log).execute(script);
        }
        catch (ProjectElementException | FreemarkerSqlException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ProjectElementException(script, e);
        }
    }

}
