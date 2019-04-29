package com.developmentontheedge.dbms;


/**
 * Type of Dbms along with some metainformation
 */
public enum DbmsType
{
    DB2("db2", 50000),
    ORACLE("oracle", 1521),
    SQLSERVER("sqlserver", 1433),
    POSTGRESQL("postgres", 5432),
    BESQL("besql", 0),
    H2("h2", 0),
    MYSQL("mysql", 3306)
            {
                @Override
                public String quoteString(String input)
                {
                    return "\'" + input.replace("\\", "\\\\").replace("\'", "\'\'") + "\'";
                }
            };

    private DbmsType(String name, int port)
    {
        this.name = name;
        this.defaultPort = port;
    }

    private final String name;

    public String getName()
    {
        return name;
    }

    private final int defaultPort;

    public int getDefaultPort()
    {
        return defaultPort;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public String quoteString(String input)
    {
        return "\'" + input.replace("\'", "\'\'") + "\'";
    }
}
