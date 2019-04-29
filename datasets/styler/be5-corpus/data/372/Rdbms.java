package com.developmentontheedge.be5.metadata.sql;

import com.developmentontheedge.be5.metadata.sql.macro.BeSQLMacroProcessorStrategy;
import com.developmentontheedge.be5.metadata.sql.macro.Db2MacroProcessorStrategy;
import com.developmentontheedge.be5.metadata.sql.macro.IMacroProcessorStrategy;
import com.developmentontheedge.be5.metadata.sql.macro.MySqlMacroProcessorStrategy;
import com.developmentontheedge.be5.metadata.sql.macro.OracleMacroProcessorStrategy;
import com.developmentontheedge.be5.metadata.sql.macro.PostgresMacroProcessorStrategy;
import com.developmentontheedge.be5.metadata.sql.macro.SqlServerMacroProcessorStrategy;
import com.developmentontheedge.be5.metadata.sql.schema.Db2SchemaReader;
import com.developmentontheedge.be5.metadata.sql.schema.DbmsSchemaReader;
import com.developmentontheedge.be5.metadata.sql.schema.H2SchemaReader;
import com.developmentontheedge.be5.metadata.sql.schema.MySqlSchemaReader;
import com.developmentontheedge.be5.metadata.sql.schema.OracleSchemaReader;
import com.developmentontheedge.be5.metadata.sql.schema.PostgresSchemaReader;
import com.developmentontheedge.be5.metadata.sql.schema.SqlServerSchemaReader;
import com.developmentontheedge.be5.metadata.sql.type.Db2TypeManager;
import com.developmentontheedge.be5.metadata.sql.type.DbmsTypeManager;
import com.developmentontheedge.be5.metadata.sql.type.H2TypeManager;
import com.developmentontheedge.be5.metadata.sql.type.MySqlTypeManager;
import com.developmentontheedge.be5.metadata.sql.type.OracleTypeManager;
import com.developmentontheedge.be5.metadata.sql.type.PostgresTypeManager;
import com.developmentontheedge.be5.metadata.sql.type.SqlServerTypeManager;
import com.developmentontheedge.dbms.DbmsConnector;
import com.developmentontheedge.dbms.DbmsType;
import com.developmentontheedge.sql.format.dbms.Dbms;

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum Rdbms
{
    DB2(DbmsType.DB2,
            new Db2MacroProcessorStrategy(),
            new Db2TypeManager(),
            new Db2SchemaReader(),
            "org.eclipse.datatools.enablement.ibm.db2.luw.connectionProfile",
            "DriverDefn.org.eclipse.datatools.enablement.ibm.db2.luw.jdbc4.driverTemplate.IBM Data Server Driver for JDBC and SQLJ (JDBC 4.0) Default", "v97"),
    MYSQL(DbmsType.MYSQL,
            new MySqlMacroProcessorStrategy(),
            new MySqlTypeManager(),
            new MySqlSchemaReader(),
            "", "com.mysql.jdbc.Driver", "6"),
    ORACLE(DbmsType.ORACLE,
            new OracleMacroProcessorStrategy(),
            new OracleTypeManager(),
            new OracleSchemaReader(),
            "", "oracle.jdbc.driver.OracleDriver", "11"),
    SQLSERVER(DbmsType.SQLSERVER,
            new SqlServerMacroProcessorStrategy(),
            new SqlServerTypeManager(),
            new SqlServerSchemaReader(),
            "org.eclipse.datatools.enablement.msft.sqlserver.connectionProfile",
            "DriverDefn.org.eclipse.datatools.enablement.msft.sqlserver.2008.driverTemplate.Microsoft SQL Server 2008 JDBC Driver", "2008"),
    POSTGRESQL(DbmsType.POSTGRESQL,
            new PostgresMacroProcessorStrategy(),
            new PostgresTypeManager(),
            new PostgresSchemaReader(),
            "", "org.postgresql.Driver", "91"),
    BESQL(DbmsType.BESQL,
            new BeSQLMacroProcessorStrategy(),
            new PostgresTypeManager(),
            null, "", "", ""),
    H2(DbmsType.H2,
            new PostgresMacroProcessorStrategy(),
            new H2TypeManager(),
            new H2SchemaReader(),
            "", "org.h2.Driver", "");

    private static final Logger log = Logger.getLogger(Rdbms.class.getName());

    public static Rdbms getRdbms(final String url)
    {
        String realUrl = url.startsWith("jdbc:") ? url.substring("jdbc:".length()) : url;
        if (realUrl.startsWith("mysql:"))
        {
            return Rdbms.MYSQL;
        }
        if (realUrl.startsWith("db2:"))
        {
            return Rdbms.DB2;
        }
        if (realUrl.startsWith("oracle:"))
        {
            return Rdbms.ORACLE;
        }
        if (realUrl.startsWith("postgresql:"))
        {
            return Rdbms.POSTGRESQL;
        }
        if (realUrl.startsWith("h2:"))
        {
            return Rdbms.H2;
        }
        if (realUrl.startsWith("sqlserver:") || realUrl.startsWith("microsoft:sqlserver:") || realUrl.startsWith("jtds:sqlserver:"))
        {
            return Rdbms.SQLSERVER;
        }

        log.log(Level.SEVERE, "Database type not supported or not determined: " + realUrl);
        throw new RuntimeException("Database type not supported or not determined: " + realUrl);
    }

    public static Rdbms getRdbms(DbmsConnector connector)
    {
        return getRdbms(connector.getType());
    }

    public static Rdbms getRdbms(DbmsType dbmsType)
    {
        Rdbms[] values = Rdbms.values();
        for (int i = 0; i < values.length; i++)
        {
            if (values[i].getType() == dbmsType) return values[i];
        }

        throw new IllegalStateException("Unsupported connector: " + dbmsType);
    }

    public Dbms getDbms()
    {
        return Dbms.valueOf(this.name().toUpperCase());
    }

    ///////////////////////////////////////////////////////////////////
    // RDBMS implementation
    //

    private final DbmsType type;
    private final IMacroProcessorStrategy macroProcessor;
    private final DbmsTypeManager typeManager;
    private final DbmsSchemaReader schemaReader;
    private final String providerId;
    private final String driverDefinition;
    private final String version;

    private Rdbms(DbmsType type, IMacroProcessorStrategy macroProcessor, DbmsTypeManager typeManager, DbmsSchemaReader schemaReader, String providerId, String driverDefinition, String version)
    {
        this.type = type;
        this.macroProcessor = macroProcessor;
        this.typeManager = typeManager;
        this.schemaReader = schemaReader;
        this.providerId = providerId;
        this.driverDefinition = driverDefinition;
        this.version = version;
    }

    public String getName()
    {
        return type.getName();
    }

    public String getAntName()
    {
        return type.getName().equals("postgres") ? "postgresql" : type.getName();
    }

    public IMacroProcessorStrategy getMacroProcessorStrategy()
    {
        return macroProcessor;
    }

    public DbmsTypeManager getTypeManager()
    {
        return typeManager;
    }

    public DbmsSchemaReader getSchemaReader()
    {
        return schemaReader;
    }

    public String getProviderId()
    {
        return providerId;
    }

    public String getDriverDefinition()
    {
        return driverDefinition;
    }

    public DbmsType getType()
    {
        return type;
    }

    public int getDefaultPort()
    {
        return type.getDefaultPort();
    }

    public String createConnectionUrl(boolean forContext, String host, int port, String database, Map<String, String> properties)
    {
        switch (this)
        {
            case ORACLE:
                return "jdbc:oracle:thin:@" + host + ":" + port + ":" + (database == null ? properties.get("SID") : database);
            case H2:
                return "jdbc:h2:" + host;
            case SQLSERVER:
                if ("jtds".equals(properties.get("driver")))
                {
                    StringBuilder url = new StringBuilder("jdbc:jtds:sqlserver://").append(host).append(':').append(port).append('/')
                            .append(database);
                    for (Entry<String, String> entry : properties.entrySet())
                    {
                        if (!entry.getKey().equals("driver"))
                            url.append(';').append(entry.getKey()).append('=').append(entry.getValue());
                    }
                    return url.toString();
                }
                return "jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + database;
            default:
                StringBuilder url = new StringBuilder("jdbc:").append(toString().toLowerCase()).append("://").append(host).append(':')
                        .append(port).append('/').append(database);
                if (!properties.isEmpty())
                {
                    if (this == DB2)
                        url.append(':');
                    else
                        url.append('?');

                    boolean first = true;
                    for (Entry<String, String> entry : properties.entrySet())
                    {
                        if (!first)
                        {
                            if (forContext)
                                url.append(';');
                            else
                                url.append('&');
                        }
                        first = false;
                        url.append(entry.getKey()).append('=').append(entry.getValue());
                    }
                }
                return url.toString();
        }
    }

    public static final int DB2_INDEX_LENGTH = 18;

    public String getDefaultVersion()
    {
        return version;
    }
}
