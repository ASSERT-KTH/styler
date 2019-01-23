package com.developmentontheedge.be5.database.impl;

import com.developmentontheedge.be5.database.ConnectionService;
import com.developmentontheedge.be5.database.DataSourceService;
import com.developmentontheedge.be5.database.DbService;
import com.developmentontheedge.be5.database.sql.ResultSetParser;
import com.developmentontheedge.be5.database.sql.SqlExecutor;
import com.developmentontheedge.be5.database.sql.SqlExecutorVoid;
import com.developmentontheedge.sql.format.dbms.Context;
import com.developmentontheedge.sql.format.dbms.Formatter;
import com.developmentontheedge.sql.model.DefaultParserContext;
import com.developmentontheedge.sql.model.SqlQuery;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DbServiceImpl implements DbService
{
    private static final Logger log = Logger.getLogger(DbServiceImpl.class.getName());

    private QueryRunner queryRunner;
    private DataSourceService databaseService;
    private ConnectionService connectionService;

    @Inject
    public DbServiceImpl(ConnectionService connectionService, DataSourceService databaseService)
    {
        this.databaseService = databaseService;
        this.connectionService = connectionService;
        queryRunner = new QueryRunner();
    }

    @Override
    public <T> T query(String sql, ResultSetHandler<T> rsh, Object... params)
    {
        return execute(true, conn -> query(conn, sql, rsh, params));
    }

    @Override
    public <T> T select(String sql, ResultSetParser<T> parser, Object... params)
    {
        return execute(true, conn -> query(conn, sql, rs -> rs.next() ? parser.parse(rs) : null, params));
    }

    @Override
    public <T> List<T> list(String sql, ResultSetParser<T> parser, Object... params)
    {
        return execute(true, conn -> query(conn, sql, rs -> {
            List<T> rows = new ArrayList<>();
            while (rs.next())
            {
                rows.add(parser.parse(rs));
            }
            return rows;
        }, params));
    }

    @Override
    public <T> T one(String sql, Object... params)
    {
        return execute(true, conn -> query(conn, sql, new ScalarHandler<T>(), params));
    }

    @Override
    public int update(String sql, Object... params)
    {
        return execute(false, conn -> update(conn, sql, params));
    }

    @Override
    public int updateUnsafe(String sql, Object... params)
    {
        return execute(false, conn -> updateUnsafe(conn, sql, params));
    }

    @Override
    public <T> T insert(String sql, Object... params)
    {
        return execute(false, conn -> insert(conn, sql, params));
    }

    private String format(String sql)
    {
        return new Formatter().format(SqlQuery.parse(sql),
                new Context(databaseService.getDbms()), new DefaultParserContext());
    }

    private <T> T query(Connection conn, String sql, ResultSetHandler<T> rsh, Object... params) throws SQLException
    {
        sql = format(sql);
        log.fine(sql + Arrays.toString(params));
        return queryRunner.query(conn, sql, rsh, params);
    }

    private int update(Connection conn, String sql, Object... params) throws SQLException
    {
        sql = format(sql);
        log.fine(sql + Arrays.toString(params));
        return queryRunner.update(conn, sql, params);
    }

    private int updateUnsafe(Connection conn, String sql, Object... params) throws SQLException
    {
        log.warning("Unsafe update (not be-sql parsed and formatted): " + sql + Arrays.toString(params));
        return queryRunner.update(conn, sql, params);
    }

    private <T> T insert(Connection conn, String sql, Object... params) throws SQLException
    {
        sql = format(sql);
        log.fine(sql + Arrays.toString(params));
        return queryRunner.insert(conn, sql, new ScalarHandler<T>(), params);
    }

    private <T> T execute(boolean isReadOnly, SqlExecutor<T> executor)
    {
        Connection conn = null;
        Connection txConn = connectionService.getCurrentTxConn();

        try
        {
            conn = (txConn != null) ? txConn : connectionService.getConnection(isReadOnly);
            return executor.run(conn);
        }
        catch (Throwable e)
        {
            log.log(Level.SEVERE, "", e);
            throw new RuntimeException(e);
        }
        finally
        {
            if (txConn == null)
            {
                connectionService.releaseConnection(conn);
            }
        }
    }

    @Override
    public <T> T execute(SqlExecutor<T> executor)
    {
        return execute(false, executor);
    }

    @Override
    public <T> T transactionWithResult(SqlExecutor<T> executor)
    {
        return connectionService.transactionWithResult(executor);
    }

    @Override
    public void transaction(SqlExecutorVoid executor)
    {
        connectionService.transaction(executor);
    }

}
