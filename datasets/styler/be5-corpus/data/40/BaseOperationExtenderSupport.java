package com.developmentontheedge.be5.operation.support;

import com.developmentontheedge.be5.operation.model.Operation;
import com.developmentontheedge.be5.operation.model.OperationExtender;

import java.util.Map;


public abstract class BaseOperationExtenderSupport implements OperationExtender
{
    @Override
    public Object postGetParameters(Operation op, Object parameters, Map<String, Object> presetValues) throws Exception
    {
        return parameters;
    }

    @Override
    public boolean skipInvoke(Operation op, Object parameters)
    {
        return false;
    }

    @Override
    public void preInvoke(Operation op, Object parameters) throws Exception
    {

    }

    @Override
    public void postInvoke(Operation op, Object parameters) throws Exception
    {

    }
}
