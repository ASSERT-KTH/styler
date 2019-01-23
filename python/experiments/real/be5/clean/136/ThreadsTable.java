package com.developmentontheedge.be5.modules.core.queries.system;

import com.developmentontheedge.be5.query.model.TableModel;
import com.developmentontheedge.be5.server.queries.support.TableBuilderSupport;


public class ThreadsTable extends TableBuilderSupport
{
    @Override
    public TableModel getTableModel()
    {
        addColumns("name", "groupName", "state", "alive", "priority", "threadGroup", "id");

        for (Thread thread : Thread.getAllStackTraces().keySet())
        {
            addRow(cells(
                    thread.getName(),
                    thread.getThreadGroup().getName(),
                    thread.getState().toString(),
                    thread.isAlive(),
                    thread.getPriority(),
                    thread.getThreadGroup().toString(),
                    thread.getId()
            ));
        }
        return table(columns, rows);
    }

}
