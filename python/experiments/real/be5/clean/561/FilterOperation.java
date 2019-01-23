package com.developmentontheedge.be5.server.operations;

import com.developmentontheedge.be5.operation.model.OperationResult;
import com.developmentontheedge.be5.server.helpers.FilterHelper;
import com.developmentontheedge.be5.server.operations.support.OperationSupport;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.DynamicPropertySetSupport;

import javax.inject.Inject;
import java.util.Map;

import static com.developmentontheedge.be5.server.FrontendActions.closeMainModal;
import static com.developmentontheedge.be5.server.FrontendActions.updateParentDocument;


public class FilterOperation extends OperationSupport
{
    @Inject
    private FilterHelper filterHelper;

    @Override
    public Object getParameters(Map<String, Object> presetValues) throws Exception
    {
        DynamicPropertySet dps = new DynamicPropertySetSupport();
        dpsHelper.addDpExcludeAutoIncrement(dps, getInfo().getModel(), context.getOperationParams());

        return filterHelper.processFilterParams(dps, presetValues, context.getOperationParams());
    }

    @Override
    public void invoke(Object parameters) throws Exception
    {
        setResult(OperationResult.finished(null, new Object[]{
                updateParentDocument(filterHelper.filterDocument(getQuery(), parameters)),
                closeMainModal()
        }));
    }
}
