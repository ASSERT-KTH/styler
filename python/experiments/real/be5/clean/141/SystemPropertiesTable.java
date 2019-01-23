package com.developmentontheedge.be5.modules.core.queries.system;

import com.developmentontheedge.be5.query.model.TableModel;
import com.developmentontheedge.be5.server.queries.support.TableBuilderSupport;

import java.util.Enumeration;
import java.util.Properties;


public class SystemPropertiesTable extends TableBuilderSupport
{
    @Override
    public TableModel getTableModel()
    {
        addColumns("name", "value");

        Properties p = System.getProperties();
        Enumeration keys = p.keys();
        while (keys.hasMoreElements())
        {
            String key = (String) keys.nextElement();

            addRow(cells(key, p.get(key).toString()));
        }
        return table(columns, rows);
    }

}
