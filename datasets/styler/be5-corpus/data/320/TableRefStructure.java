package com.developmentontheedge.be5.metadata.serialization.yaml.deserializers;

import com.developmentontheedge.be5.metadata.DatabaseConstants;
import com.developmentontheedge.be5.metadata.model.TableReference;

class TableRefStructure
{
    String tableTo;
    String columnTo;
    String view = DatabaseConstants.SELECTION_VIEW;
    String[] permittedTables;

    void applyTo(TableReference tableRef)
    {
        tableRef.setTableTo(tableTo);

        if (tableTo != null && columnTo.isEmpty()) // primary key
        {
            // no warnings, but the field will be empty
            // and it will be interpreted as a primary key
            tableRef.setColumnsTo("");
        }
        else
        {
            tableRef.setColumnsTo(columnTo);
        }

        tableRef.setPermittedTables(permittedTables);
        tableRef.setViewName(view);
    }
}
