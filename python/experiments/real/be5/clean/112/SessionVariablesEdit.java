package com.developmentontheedge.be5.modules.core.operations.system;

import com.developmentontheedge.be5.operation.model.OperationResult;
import com.developmentontheedge.be5.server.operations.support.GOperationSupport;
import com.developmentontheedge.beans.DynamicPropertyBuilder;

import java.util.Map;

import static com.developmentontheedge.beans.BeanInfoConstants.LABEL_FIELD;


public class SessionVariablesEdit extends GOperationSupport
{
    @Override
    public Object getParameters(Map<String, Object> presetValues) throws Exception
    {
        Object variable = session.get(context.getRecord());
        if (variable == null)
        {
            setResult(OperationResult.error("Session variable \'" + String.valueOf(getContext().getRecords()[0]) + "\' not found"));
            return null;
        }

        params.add(new DynamicPropertyBuilder("label", String.class)
                .attr(LABEL_FIELD, true)
                .value("Тип: " + variable.getClass().getName())
                .get());

        params.add(new DynamicPropertyBuilder("newValue", variable.getClass())
                .title("Новое значение:")
                .value(presetValues.getOrDefault("newValue", variable))
                .get());

        return params;
    }

    @Override
    public void invoke(Object parameters) throws Exception
    {
        session.putAt(context.getRecord(), params.getValue("newValue"));
    }
}
