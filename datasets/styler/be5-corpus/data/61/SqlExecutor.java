package com.developmentontheedge.dbms;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Locale;
import java.util.Properties;

public class SqlExecutor
{
    public static final int MAX_ORACLE_STRING_LENGTH = 4000;
    private static final String YES = "yes";
    private static final String NO = "no";

    protected final DbmsType platform;
    protected final PrintStream log;
    protected final DbmsConnector connector;
    protected final Properties properties;
    protected String sectionName;
    //private final Map<String, BulkInserter> inserters = new HashMap<>();

    static URL getDefaultPropertiesFile()
    {
        return SqlExecutor.class.getResource("basesql.properties");
    }

    public SqlExecutor(DbmsConnector connector, PrintStream log, URL propertiesURL) throws IOException
    {
        this.connector = connector;
        this.platform = connector.getType();
        this.log = log;
        this.properties = loadSQL(propertiesURL, loadSQL(getDefaultPropertiesFile(), null));
    }

    public SqlExecutor(DbmsConnector connector, URL propertiesURL) throws IOException
    {
        this(connector, null, propertiesURL);
    }

    public DbmsType getType()
    {
        return platform;
    }

    public DbmsConnector getConnector()
    {
        return connector;
    }

    public void close(ResultSet rs)
    {
        if (rs != null)
            connector.close(rs);
    }

    /**
     * Executes the query with the given name.
     *
     * @param queryName The name of the query to be executed.
     * @param args      Query arguments.
     * @throws ExtendedSqlException SQL execution error
     */
    public void exec(String queryName, Object... args) throws ExtendedSqlException
    {
        if (mustUsePreparedStatement(args))
        {
            execPrepared(queryName, args, false);
            return;
        }
        String sql = sql(queryName, args);
        if (sql.isEmpty())
            return;
        try
        {
            log(sql);
            connector.executeUpdate(sql);
        }
        catch (Exception e)
        {
            comment("Warning: SQL error " + e.getMessage() + " trying again...", false);
            try
            {
                connector.executeUpdate(sql);
            }
            catch (SQLException e1)
            {
                throw handleError(sql, e);
            }
        }
    }

    public void comment(String comment)
    {
        comment(comment, true);
    }

    public void comment(String comment, boolean margin)
    {
        if (log != null)
        {
            if (margin)
            {
                log.println();
            }
            for (String line : comment.split("\n", -1))
            {
                log.print("-- ");
                log.println(line.replace("\r", ""));
            }
        }
    }

    public void startSection(String name)
    {
        this.sectionName = name;
    }

    public void executeSingle(String statement) throws ExtendedSqlException
    {
        try
        {
            log(statement);
            if (platform == DbmsType.ORACLE)
            {
                String upperStatement = statement.toUpperCase(Locale.ENGLISH);
                if (upperStatement.endsWith("END"))
                {
                    // Oracle CREATE TRIGGER statements must have a trailing semicolon
                    statement += ";";
                    comment("(trailing semicolon included)", false);
                }
            }
            connector.executeUpdate(statement);
        }
        catch (Exception e1)
        {
            if ((statement.startsWith("DROP INDEX ")) && platform == DbmsType.DB2)
            {
                comment("Exception ignored: " + e1.getMessage(), false);
                return;
            }
            comment("Failure: " + e1.getMessage());
            throw new ExtendedSqlException(connector, statement, e1);
        }
    }

    public void executeMultiple(String sql) throws ExtendedSqlException
    {
        MultiSqlParser multiSqlParser = new MultiSqlParser(platform, sql);
        String statement;
        while ((statement = multiSqlParser.nextStatement()) != null)
        {
            executeSingle(statement);
        }
    }

    public ResultSet executeNamedQuery(final String queryName, Object... args) throws ExtendedSqlException
    {
        String sql = sql(queryName, args);
        try
        {
            log(sql);
            return connector.executeQuery(sql);
        }
        catch (Exception e)
        {
            throw new ExtendedSqlException(connector, sql, e);
        }
    }
//
//    /**
//     * Executes the query with the given name and returns its insert ID.
//     *
//     * @param queryName The name of the query to be executed.
//     * @param args Query arguments.
//     * @return id of inserted record (db-specific)
//     * @throws ExtendedSqlException SQL execution error
//     */
//    public String execInsert(String queryName, Object ... args) throws ExtendedSqlException
//    {
//        if( mustUsePreparedStatement( args ) )
//        {
//            return execPrepared( queryName, args, true );
//        }
//        String sql = sql( queryName, args );
//        try
//        {
//            log( sql );
//            return connector.executeInsert( sql );
//        }
//        catch( SQLException e )
//        {
//            comment( "Warning: SQL error " + e.getMessage() + " trying again...", false );
//            try
//            {
//                return connector.executeInsert( sql );
//            }
//            catch( SQLException e1 )
//            {
//                throw handleError( sql, e );
//            }
//        }
//    }
//
//    public void execDelayedInsert(String queryName, Object ... args) throws ExtendedSqlException
//    {
//        if( sectionName != null )
//        {
//            comment( sectionName );
//            sectionName = null;
//        }
//        if( mustUsePreparedStatement( args ) )
//        {
//            execPrepared( queryName, args, false );
//            return;
//        }
//        BulkInserter inserter = inserters.get( queryName );
//        if( inserter == null )
//        {
//            String query = getQuery( queryName );
//            int pos = query.indexOf( "VALUES(" );
//            int lastPos = query.lastIndexOf( ')' );
//            if( pos <= 0 || lastPos <= 0 )
//            {
//                throw new IllegalArgumentException( "Template " + queryName + " is not proper insert template" );
//            }
//            String initString = query.substring( 0, pos );
//            String valuesTemplate = query.substring( pos + "VALUES(".length(), lastPos );
//            inserter = new BulkInserter( connector, initString, valuesTemplate, log );
//            inserters.put( queryName, inserter );
//        }
//        comment( "(delayed) " + sql( queryName, args ) + ";", false );
//        try
//        {
//            inserter.add( args );
//        }
//        catch( Exception e )
//        {
//            throw handleError( "", e );
//        }
//    }
//
//    public void flushDelayedInserts() throws ExtendedSqlException
//    {
//        for( BulkInserter inserter : inserters.values() )
//        {
//            try
//            {
//                inserter.flush();
//            }
//            catch( Exception e )
//            {
//                throw handleError( "", e );
//            }
//        }
//    }

    /**
     * Executes query with the given name and returns
     * first value of the first column in result set as <code>String</code>.
     * If result set is empty, then returns <code>null</code>.
     *
     * @param queryName The name of the query to execute.
     * @param args      Query arguments.
     * @return Value of first column in result se or null, if result set is empty.
     * @throws ExtendedSqlException SQL execution error
     */
    public String readString(String queryName, Object... args) throws ExtendedSqlException
    {
        String sql = sql(queryName, args);
        try
        {
            log(sql);
            ResultSet rs = connector.executeQuery(sql);
            try
            {
                if (rs.next())
                    return rs.getString(1);
                return null;
            }
            finally
            {
                connector.close(rs);
            }
        }
        catch (Exception e)
        {
            throw new ExtendedSqlException(connector, sql, e);
        }
    }
//
//    /**
//     * Executes query with the specified name and returns result
//     * as a list containing values of the first column in record set.
//     *
//     * @param queryName Query name to execute.
//     * @param args Query arguments.
//     * @return List containing values of the first column in record set.
//     * @throws ExtendedSqlException SQL execution error
//     */
//    public List<String> readStringList(String queryName, Object ... args) throws ExtendedSqlException
//    {
//        String sql = sql( queryName, args );
//        try
//        {
//            log( sql );
//            ResultSet rs = connector.executeQuery( sql );
//            try
//            {
//                List<String> result = new ArrayList<>();
//                while( rs.next() )
//                    result.add( rs.getString( 1 ) );
//                return result;
//            }
//            finally
//            {
//                connector.close( rs );
//            }
//        }
//        catch( Exception e )
//        {
//            throw new ExtendedSqlException( connector, sql, e );
//        }
//    }
//
//    /**
//     * Executes query with the specified name and returns result
//     * as a map with keys from first column of result set and values from the second column
//     *
//     * @param queryName Query name to execute.
//     * @param args Query arguments.
//     * @return Map
//     * @throws ExtendedSqlException SQL execution error
//     */
//    public Map<String, String> readMap(String queryName, Object ... args) throws ExtendedSqlException
//    {
//        String sql = sql( queryName, args );
//        try
//        {
//            log( sql );
//            ResultSet rs = connector.executeQuery( sql );
//            try
//            {
//                Map<String, String> result = new HashMap<>();
//                while( rs.next() )
//                    result.put( rs.getString( 1 ), rs.getString( 2 ) );
//                return result;
//            }
//            finally
//            {
//                connector.close( rs );
//            }
//        }
//        catch( Exception e )
//        {
//            throw new ExtendedSqlException( connector, sql, e );
//        }
//    }
//
//    public boolean hasTable(String tableName)
//    {
//        try
//        {
//            readString( "sql.test.table", tableName );
//            return true;
//        }
//        catch( ExtendedSqlException e )
//        {
//            return false;
//        }
//    }

    public boolean hasResult(String queryName, Object... args) throws ExtendedSqlException
    {
        return readString(queryName, args) != null;
    }

    public boolean isEmpty(String tableName) throws ExtendedSqlException
    {
        return !hasResult("sql.test.table", tableName);
    }

    public void testConnection() throws ExtendedSqlException
    {
        readString("sql.test.connection");
    }

    public int count(String table) throws ExtendedSqlException
    {
        String str = readString("sql.countRows", table);
        return str == null ? 0 : Integer.parseInt(str);
    }

    /**
     * Replaces all occurrences of ? in statement with the given arguments.
     * Note that if ? is NOT preceded by &, then this method will escape
     * all special symbols in arg using Utils.safestr() to avoid SQL injection.
     *
     * @param connector Database connector
     * @param statement SQL statement
     * @param args      Actual values
     * @return Prepared statement
     */
    static String prepare(DbmsType platform, String statement, Object... args)
    {
        String[] tokens = statement.split("\\?", -1);
        if (tokens.length == 1)
            return statement;
        StringBuilder prepared = new StringBuilder();
        for (int i = 0; i < tokens.length - 1; i++)
        {
            String token = tokens[i];
            if (token.startsWith("'") && token.length() > 1)
            {
                token = token.substring(1);
            }
            boolean escape = !token.endsWith("&");
            boolean quote = token.endsWith("'");
            prepared.append(escape && !quote ? token : token.substring(0, token.length() - 1));
            String nextValue = stringValue(i < args.length ? args[i] : null);
            prepared.append((escape && quote && nextValue != null) ? platform.quoteString(nextValue) : nextValue);
        }
        String lastToken = tokens[tokens.length - 1];
        if (lastToken.startsWith("'"))
            lastToken = lastToken.substring(1);
        prepared.append(lastToken);
        return prepared.toString();
    }

    private static String stringValue(Object value)
    {
        if (null == value)
        {
            return null;
        }
        if (value instanceof Boolean)
        {
            return ((Boolean) value) ? YES : NO;
        }
        if (value instanceof byte[])
        {
            return "<binary_data>";
        }
        return value.toString();
    }

    /**
     * Loads properties from file named <code>sql.properties</code>.
     *
     * @throws IOException Problem with file loading.
     */
    private static Properties loadSQL(URL url, Properties defaults) throws IOException
    {
        try (InputStream s = url.openStream())
        {
            Properties properties = new Properties(defaults);
            properties.load(s);
            return properties;
        }
    }

    protected void log(String sql)
    {
        if (log != null)
        {
            if (sectionName != null)
            {
                comment(sectionName);
                sectionName = null;
            }
            log.print(sql);
            log.println(';');
        }
    }

    protected String execPrepared(String queryName, Object[] args, boolean needId) throws ExtendedSqlException
    {
        String sql = sql(queryName, args);
        try
        {
            log(sql + "; -- via PreparedStatement");
            String query = getQuery(queryName).replace("'?'", "?");
            int params = 0;
            int lastParamPos = 0;
            while ((lastParamPos = query.indexOf('?', lastParamPos) + 1) > 0)
                params++;
            Connection conn = connector.getConnection();
            try (PreparedStatement st = conn.prepareStatement(query))
            {
                for (int i = 0; i < params; i++)
                {
                    Object arg = args[i];
                    if (arg == null)
                    {
                        st.setNull(i + 1, Types.VARCHAR);
                    }
                    else if (arg instanceof Boolean)
                    {
                        st.setString(i + 1, ((Boolean) arg) ? YES : NO);
                    }
                    else if (arg instanceof byte[])
                    {
                        st.setBytes(i + 1, (byte[]) arg);
                    }
                    else
                    {
                        st.setString(i + 1, arg.toString());
                    }
                }
                st.execute();
                if (needId)
                {
                    if (platform == DbmsType.ORACLE)
                    {
                        return readString("sql.lastInsertId", "beIDGenerator");
                    }
                    // TODO: support other DBMS if necessary
                }
                return null;
            }
        }
        catch (Exception e)
        {
            throw handleError(sql, e);
        }
    }

    /**
     * @param sql query which was executing when error occurred
     * @param e   an Exception which should be wrapped
     * @return ExtendedSqlException
     */
    protected ExtendedSqlException handleError(String sql, Exception e)
    {
        comment("Failure: " + e);
        if (e instanceof ExtendedSqlException)
            return (ExtendedSqlException) e;
        return new ExtendedSqlException(connector, sql, e);
    }

    protected boolean mustUsePreparedStatement(Object[] args)
    {
        boolean oracle = platform == DbmsType.ORACLE;
        for (Object arg : args)
        {
            if (arg instanceof byte[])
                return true;
            if (oracle && arg != null && arg.toString().getBytes(StandardCharsets.UTF_8).length > MAX_ORACLE_STRING_LENGTH)
                return true;
        }
        return false;
    }

    /**
     * Returns prepared SQL query with the specified name.
     *
     * @param queryName Query name.
     * @param args      Query arguments (replacements for ?)
     * @return Prepared SQL.
     * @throws IllegalArgumentException When query with the specified name not found.
     */
    protected String sql(String queryName, Object... args)
    {
        String query = getQuery(queryName);
        return prepare(platform, query, args);
    }

    protected String getQuery(String queryName)
    {
        // try to load db specific query
        String query = properties.getProperty(queryName + '.' + platform.getName());

        // try to load common query
        if (query == null)
        {
            query = properties.getProperty(queryName);
        }

        if (null == query)
        {
            throw new IllegalArgumentException("Cannot find query with name \"" + queryName + "\" for " + platform + " platform."
                    + " Please add it in sql.properties.");
        }
        return query;
    }
}
