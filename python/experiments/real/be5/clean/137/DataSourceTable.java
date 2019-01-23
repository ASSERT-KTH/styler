package com.developmentontheedge.be5.modules.core.queries.system;

import com.developmentontheedge.be5.database.DataSourceService;
import com.developmentontheedge.be5.query.model.TableModel;
import com.developmentontheedge.be5.server.queries.support.TableBuilderSupport;
import org.apache.commons.dbcp.BasicDataSource;

import javax.inject.Inject;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class DataSourceTable extends TableBuilderSupport
{
    @Inject private DataSourceService databaseService;

    @Override
    public TableModel getTableModel()
    {
        addColumns("name", "value");
        if (databaseService.getDataSource() instanceof BasicDataSource)
        {
            getBasicDataSourceInfo();
        }
        else
        {
            getMBeanInfo();
        }
        return table(columns, rows);
    }

    private void getMBeanInfo()
    {
        String connectUrl = databaseService.getConnectionUrl();
        try
        {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            Set<ObjectName> objectNames = server.queryNames(null, null);
            for (ObjectName name : objectNames)
            {
                MBeanInfo info = server.getMBeanInfo(name);
                if (info.getClassName().equals("org.apache.tomcat.jdbc.pool.jmx.ConnectionPool")
                        && connectUrl.equals(server.getAttribute(name, "Url")))
                {
                    for (MBeanAttributeInfo mf : info.getAttributes())
                    {
                        Object attributeValue = server.getAttribute(name, mf.getName());
                        if (attributeValue != null)
                        {
                            addRow(cells(mf.getName(), attributeValue.toString()));
                        }
                    }
                    break;
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private void getBasicDataSourceInfo()
    {
        Map<String, String> parameters = getParameters((BasicDataSource) databaseService.getDataSource());
        for (Map.Entry<String, String> entry : parameters.entrySet())
        {
            addRow(cells(entry.getKey(), entry.getValue() != null ? entry.getValue() : ""));
        }
    }

    private Map<String, String> getParameters(BasicDataSource dataSource)
    {
        return new TreeMap<String, String>() {{
            put("DataSource class", dataSource.getClass().getCanonicalName());
            put("Active/Idle", dataSource.getNumActive() + " / " + dataSource.getNumIdle());
            put("max Active/max Idle", dataSource.getMaxActive() + " / " + dataSource.getMaxIdle());
            put("max wait", dataSource.getMaxWait() + "");
            put("Username", dataSource.getUsername());
            put("DefaultCatalog", dataSource.getDefaultCatalog());
            put("DriverClassName", dataSource.getDriverClassName());
            put("Url", dataSource.getUrl());
            put("ValidationQuery", dataSource.getValidationQuery());
            put("ConnectionInitSqls", dataSource.getConnectionInitSqls().toString());
        }};
    }
}
