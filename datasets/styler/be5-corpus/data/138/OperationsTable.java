package com.developmentontheedge.be5.modules.core.queries.system.meta;

import com.developmentontheedge.be5.metadata.model.Operation;
import com.developmentontheedge.be5.query.model.TableModel;
import com.developmentontheedge.be5.server.queries.support.TableBuilderSupport;

import java.util.List;


public class OperationsTable extends TableBuilderSupport
{
    @Override
    public TableModel getTableModel()
    {
        addColumns("EntityName", "Name", "Type", "Roles");

        String selectEntity = (String) parameters.get("entity");
        if (selectEntity != null)
        {
            addOperations(selectEntity);
        }
        else
        {
            meta.getOrderedEntities(userInfo.getLanguage()).forEach(
                    e -> addOperations(e.getName())
            );
        }
        return table(columns, rows);
    }

    public void addOperations(String entityName)
    {
        List<String> operationNames = meta.getOperationNames(meta.getEntity(entityName));
        for (String operationName : operationNames)
        {
            Operation operation = meta.getOperation(entityName, operationName);
            addRow(cells(
                    entityName,
                    operation.getName(),
                    operation.getType(),
                    operation.getRoles().getFinalRoles().toString()
            ));
        }
    }
}
