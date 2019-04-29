package com.developmentontheedge.be5.operation.services.impl;

import com.developmentontheedge.be5.base.services.Meta;
import com.developmentontheedge.be5.operation.services.OperationBuilder;
import com.developmentontheedge.be5.operation.services.OperationExecutor;
import com.developmentontheedge.be5.operation.services.OperationsFactory;

import javax.inject.Inject;


public class OperationsFactoryImpl implements OperationsFactory
{
    private OperationExecutor operationExecutor;
    private Meta meta;

    @Inject
    public OperationsFactoryImpl(Meta meta, OperationExecutor operationExecutor)
    {
        this.meta = meta;
        this.operationExecutor = operationExecutor;
    }

    @Override
    public OperationBuilder get(String entityName, String operationName)
    {
        return new OperationBuilderImpl(meta, operationExecutor, entityName, operationName);
    }
}
