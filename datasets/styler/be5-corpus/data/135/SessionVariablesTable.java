package com.developmentontheedge.be5.modules.core.queries.system;

import com.developmentontheedge.be5.query.model.TableModel;
import com.developmentontheedge.be5.server.queries.support.TableBuilderSupport;


public class SessionVariablesTable extends TableBuilderSupport
{
    @Override
    public TableModel getTableModel()
    {
        addColumns("Name", "Value");

        for (String name : session.getAttributeNames())
        {
            addRow(name, cells(name, session.get(name).toString()));
        }
        return table(columns, rows, true);
    }
}
