package com.developmentontheedge.be5.operation.model;

import java.util.Map;

public interface OperationExtender
{
    //boolean preGetParameters(Operation op, Map<String, Object> presetValues) throws Exception
    Object postGetParameters(Operation op, Object parameters, Map<String, Object> presetValues) throws Exception;

    boolean skipInvoke(Operation op, Object parameters);

    void preInvoke(Operation op, Object parameters) throws Exception;

    void postInvoke(Operation op, Object parameters) throws Exception;
}
