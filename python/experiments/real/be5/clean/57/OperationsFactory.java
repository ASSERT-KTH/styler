package com.developmentontheedge.be5.operation.services;


public interface OperationsFactory
{
    OperationBuilder get(String entityName, String operationName);
}
