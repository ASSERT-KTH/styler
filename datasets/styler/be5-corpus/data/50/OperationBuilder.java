package com.developmentontheedge.be5.operation.services;

import com.developmentontheedge.be5.operation.model.Operation;

import java.util.Map;


public interface OperationBuilder
{
    OperationBuilder setQueryName(String queryName);

    OperationBuilder setRecords(Object[] records);

    OperationBuilder setPresetValues(Map<String, ?> presetValues);

    OperationBuilder setOperationParams(Map<String, Object> operationParams);

    Object generate();

    Operation execute();

//    Object generate(@DelegatesTo(GOperationModelBaseBuilder.class) Closure closure);
//
//    Operation execute(@DelegatesTo(GOperationModelBaseBuilder.class) Closure closure);

    default Operation executeIfNotEmptyRecords(Object[] records)
    {
        if (records.length > 0)
        {
            setRecords(records);
            return execute();
        }
        else
        {
            return null;
        }
    }
}
