package com.developmentontheedge.dbms;


public class ExtendedSqlException extends Exception
{
    private static final long serialVersionUID = 1L;
    private final String connectionString;
    private final String query;

    public ExtendedSqlException(String connectString, String query, Exception cause)
    {
        super(cause);
        this.connectionString = connectString;
        this.query = query;
    }

    public ExtendedSqlException(DbmsConnector connector, String query, Exception cause)
    {
        this(connector.getConnectString(), query, cause);
    }

    @Override
    public String getMessage()
    {
        return "SQL Error: " + getCause().getMessage() + "\nConnection: " + connectionString + "\nQuery: " + query;
    }
}
