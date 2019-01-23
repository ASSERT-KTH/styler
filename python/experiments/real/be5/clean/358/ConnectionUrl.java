package com.developmentontheedge.be5.metadata.sql;

import com.developmentontheedge.be5.metadata.model.base.BeElementWithProperties;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.PropertiesDPS;
import com.developmentontheedge.beans.annot.PropertyName;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectionUrl implements BeElementWithProperties
{
    private static final Pattern DB_NAME_EXTRACTOR = Pattern.compile("/([a-zA-Z0-9_\\-]+)([\\;\\:\\?].*|)");
    private static final Pattern SQL_SERVER_PATTERN = Pattern.compile("jdbc:sqlserver:\\/\\/([^:;]+)(:(\\d+)|);databaseName=([a-zA-Z0-9_\\-]+)");
    private static final Pattern SQL_SERVER_JTDS_PATTERN = Pattern.compile("jdbc:jtds:sqlserver:\\/\\/([^:;]+)(:(\\d+)|)/([a-zA-Z0-9_\\-]+)(;.+|)");

    private final Rdbms rdbms;
    private String host;
    private int port;
    private String db;
    private final Properties properties = new Properties();

    public ConnectionUrl(String url)
    {
        rdbms = Rdbms.getRdbms(url);
        parse(url);
    }

    public ConnectionUrl(Rdbms rdbms)
    {
        this.rdbms = rdbms;
    }

    private void parse(String url)
    {
        if (!url.startsWith("jdbc:"))
            throw new IllegalArgumentException("Invalid JDBC URL (must start with 'jdbc:')");
        if (rdbms == null)
            throw new IllegalArgumentException("Unsupported DBMS (url = " + url + ")");
        Matcher jtdsMatcher = SQL_SERVER_JTDS_PATTERN.matcher(url);
        Matcher sqlServerMatcher = SQL_SERVER_PATTERN.matcher(url);
        if (jtdsMatcher.matches())
        {
            host = jtdsMatcher.group(1);
            String portNum = jtdsMatcher.group(3);
            if (portNum.isEmpty())
            {
                port = rdbms.getDefaultPort();
            }
            else
            {
                port = Integer.parseInt(portNum);
            }
            db = jtdsMatcher.group(4);
            properties.setProperty("driver", "jtds");
            setProperties(jtdsMatcher.group(5));
            return;
        }

        if (sqlServerMatcher.matches())
        {
            host = sqlServerMatcher.group(1);
            String portNum = sqlServerMatcher.group(3);
            if (portNum.isEmpty())
            {
                port = rdbms.getDefaultPort();
            }
            else
            {
                port = Integer.parseInt(portNum);
            }
            db = sqlServerMatcher.group(4);
            return;
        }


        URI uri = URI.create(url.substring("jdbc:".length()));
        if (uri.getPort() > 0)
        {
            port = uri.getPort();
        }
        else
        {
            port = rdbms.getDefaultPort();
        }

        if (uri.getHost() != null)
        {
            host = uri.getHost();
            String path = uri.getPath();
            Matcher matcher = DB_NAME_EXTRACTOR.matcher(path);
            if (!matcher.matches())
                throw new IllegalArgumentException("Unable to extract database name from URL");
            db = matcher.group(1);
            setProperties(matcher.group(2));
            if (uri.getQuery() != null)
                setProperties("?" + uri.getQuery());
        }
        else
        {
            // TODO: enhance algorithm to support port, service, etc.
            // see http://www.orafaq.com/wiki/JDBC#Thin_driver
            String specificPart = uri.getSchemeSpecificPart();
            int pos = specificPart.lastIndexOf(':');
            if (pos >= -1)
            {
                String sid = specificPart.substring(pos + 1);
                setProperty("SID", sid);
            }
            pos = specificPart.indexOf('@');
            if (pos >= -1)
            {
                int pos2 = specificPart.indexOf(':', pos);
                if (rdbms == Rdbms.H2 && pos2 == -1) pos2 = specificPart.length();
                if (pos2 >= -1)
                {
                    host = specificPart.substring(pos + 1, pos2);
                }
            }
        }
    }

    private void setProperties(String dbProperties)
    {
        if (dbProperties == null || dbProperties.isEmpty())
            return;
        for (String part : dbProperties.substring(1).split("[;&]"))
        {
            int equalPos = part.indexOf('=');
            if (equalPos > 0)
            {
                String key = part.substring(0, equalPos).trim();
                String value = part.substring(equalPos + 1).trim();
                setProperty(key, value);
            }
        }
    }

    public Rdbms getRdbms()
    {
        return rdbms;
    }

    @PropertyName("Database host name")
    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    @PropertyName("Database port")
    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    @PropertyName("Database name")
    public String getDb()
    {
        return db;
    }

    public void setDb(String db)
    {
        this.db = db;
    }

    @Override
    public void setProperty(String key, String value)
    {
        properties.setProperty(key, value);
    }

    @Override
    public String getProperty(String key)
    {
        return properties.getProperty(key);
    }

    @Override
    public Set<String> getPropertyNames()
    {
        return Collections.unmodifiableSet(properties.stringPropertyNames());
    }

    public DynamicPropertySet getProperties()
    {
        return new PropertiesDPS(properties);
    }

    public String createConnectionUrl(boolean forContext)
    {
        if (rdbms == null)
        {
            return "";
        }
        Map<String, String> props = new TreeMap<>();
        List<String> stringPropertyNames = new ArrayList<>(properties.stringPropertyNames());
        Collections.sort(stringPropertyNames);
        for (String name : stringPropertyNames)
        {
            props.put(name, properties.getProperty(name));
        }
        return rdbms.createConnectionUrl(forContext, host, port, db, props);
    }

    @Override
    public String toString()
    {
        return createConnectionUrl(true);
    }
}
