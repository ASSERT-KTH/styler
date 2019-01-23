package com.developmentontheedge.be5.operation.services.impl;

import com.developmentontheedge.be5.base.services.Meta;
import com.developmentontheedge.be5.operation.model.Operation;
import com.developmentontheedge.be5.operation.model.OperationContext;
import com.developmentontheedge.be5.operation.model.OperationInfo;
import com.developmentontheedge.be5.operation.model.OperationResult;
import com.developmentontheedge.be5.operation.model.OperationStatus;
import com.developmentontheedge.be5.operation.services.OperationBuilder;
import com.developmentontheedge.be5.operation.services.OperationExecutor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.developmentontheedge.be5.operation.OperationConstants.SELECTED_ROWS;


public class OperationBuilderImpl implements OperationBuilder
{
    private OperationExecutor operationExecutor;
    private Meta meta;

    private Object[] records = new Object[]{};

    private String entityName;
    private String queryName;
    private String operationName;

    private Map<String, ?> presetValues = Collections.emptyMap();
    private Map<String, Object> operationParams = Collections.emptyMap();

    OperationBuilderImpl(Meta meta, OperationExecutor operationExecutor, String entityName, String operationName)
    {
        this.meta = meta;
        this.operationExecutor = operationExecutor;

        this.entityName = entityName;
        this.operationName = operationName;
    }

    @Override
    public OperationBuilder setQueryName(String queryName)
    {
        this.queryName = queryName;
        return this;
    }

    @Override
    public OperationBuilder setRecords(Object[] records)
    {
        this.records = records;
        return this;
    }

    @Override
    public OperationBuilder setPresetValues(Map<String, ?> presetValues)
    {
        this.presetValues = presetValues;
        return this;
    }

    @Override
    public OperationBuilder setOperationParams(Map<String, Object> operationParams)
    {
        this.operationParams = operationParams;
        return this;
    }

    @Override
    public Object generate()
    {
        Operation operation = operationExecutor.create(getOperationInfo(), getOperationContext());
        operation.setResult(OperationResult.generate());

        return operationExecutor.generate(operation, (Map<String, Object>) presetValues);
    }

    @Override
    public Operation execute()
    {
        Operation operation = operationExecutor.create(getOperationInfo(), getOperationContext());
        operation.setResult(OperationResult.execute());

        operationExecutor.execute(operation, (Map<String, Object>) presetValues);
        if (operation.getStatus() == OperationStatus.ERROR)
        {
            throw (RuntimeException) operation.getResult().getDetails();
        }

        return operation;
    }
//
//    @Override
//    public Object generate(@DelegatesTo(GOperationModelBaseBuilder.class) final Closure closure)
//    {
//        closure.setResolveStrategy( Closure.DELEGATE_FIRST );
//        closure.setDelegate( this );
//        closure.call();
//
//        return generate();
//    }
//
//    @Override
//    public Operation execute(@DelegatesTo(GOperationModelBaseBuilder.class) final Closure closure)
//    {
//        closure.setResolveStrategy( Closure.DELEGATE_FIRST );
//        closure.setDelegate( this );
//        closure.call();
//
//        return execute();
//    }

    private OperationInfo getOperationInfo()
    {
        com.developmentontheedge.be5.metadata.model.Operation operationModel =
                meta.getOperation(entityName, operationName);

        Objects.requireNonNull(operationModel, "Operation '" + entityName + "." + operationName + "' not found.");

        return new OperationInfo(operationModel);
    }

    private OperationContext getOperationContext()
    {
        operationParams = new HashMap<>(operationParams);
        if (records.length > 0)
        {
            operationParams.put(SELECTED_ROWS, Arrays.stream(records)
                    .map(Object::toString)
                    .collect(Collectors.joining(",")));
        }

        return operationExecutor.getOperationContext(getOperationInfo(), queryName, operationParams);
    }

}
