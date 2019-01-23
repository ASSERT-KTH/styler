package com.developmentontheedge.be5.operation.services.impl;

import com.developmentontheedge.be5.base.services.UserInfoProvider;
import com.developmentontheedge.be5.base.util.HashUrl;
import com.developmentontheedge.be5.operation.model.Operation;
import com.developmentontheedge.be5.operation.model.OperationContext;
import com.developmentontheedge.be5.operation.model.OperationResult;
import com.developmentontheedge.be5.operation.model.OperationStatus;
import com.developmentontheedge.be5.operation.services.OperationExecutor;
import com.developmentontheedge.be5.operation.services.OperationService;
import com.developmentontheedge.be5.operation.services.validation.Validator;
import com.developmentontheedge.be5.operation.util.Either;
import com.developmentontheedge.be5.operation.util.FilterUtil;
import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySet;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.developmentontheedge.be5.base.FrontendConstants.RELOAD_CONTROL_NAME;
import static com.developmentontheedge.be5.base.FrontendConstants.TABLE_ACTION;
import static com.developmentontheedge.be5.operation.util.OperationUtils.paramsWithoutSelectedRows;


public class OperationServiceImpl implements OperationService
{
    public static final Logger log = Logger.getLogger(OperationServiceImpl.class.getName());

    private final OperationExecutor operationExecutor;
    private final Validator validator;
    private final UserInfoProvider userInfoProvider;

    @Inject
    public OperationServiceImpl(OperationExecutor operationExecutor, Validator validator, UserInfoProvider userInfoProvider)
    {
        this.operationExecutor = operationExecutor;
        this.validator = validator;
        this.userInfoProvider = userInfoProvider;
    }

    @Override
    public Either<Object, OperationResult> generate(Operation operation, Map<String, Object> values)
    {
        Map<String, Object> presetValues = getPresetValues(operation.getContext(), values);

        operation.setResult(OperationResult.generate());

        Object parameters = operationExecutor.generate(operation, presetValues);

        if (OperationStatus.ERROR == operation.getStatus())
        {
            return Either.second(operation.getResult());
        }

        if (parameters == null)
        {
            return execute(operation, presetValues);
        }

        if (presetValues.containsKey(RELOAD_CONTROL_NAME))
        {
            validator.validate(parameters);
        }

        return replaceNullValueToEmptyStringAndReturn(parameters);
    }

    @Override
    public Either<Object, OperationResult> execute(Operation operation, Map<String, Object> values)
    {
        Map<String, Object> presetValues = getPresetValues(operation.getContext(), values);

        operation.setResult(OperationResult.execute());

        Object parameters = operationExecutor.execute(operation, presetValues);

        if (OperationStatus.EXECUTE == operation.getStatus())
        {
            operation.setResult(OperationResult.redirect(new HashUrl(TABLE_ACTION,
                    operation.getInfo().getEntityName(), operation.getContext().getQueryName())
                    .named(paramsWithoutSelectedRows(operation.getRedirectParams())).toString())
            );
        }

        if (OperationStatus.ERROR == operation.getStatus())
        {
            try
            {
                validator.throwExceptionIsError(parameters);
            }
            catch (RuntimeException e)
            {
                log.log(Level.INFO, "error on execute in parameters", e);
                //remove duplicate operation.setResult(OperationResult.error(e));
                return replaceNullValueToEmptyStringAndReturn(parameters);
            }

            Operation newOperation = operationExecutor.create(operation.getInfo(), operation.getContext());
            Object newParameters = operationExecutor.generate(newOperation, presetValues);

            if (newParameters != null && OperationStatus.ERROR != newOperation.getStatus())
            {
                return replaceNullValueToEmptyStringAndReturn(newParameters);
            }

            return Either.second(operation.getResult());
        }

        return Either.second(operation.getResult());
    }

    private static Map<String, Object> getPresetValues(OperationContext context, Map<String, Object> values)
    {
        Map<String, Object> presetValues =
                new HashMap<>(FilterUtil.getOperationParamsWithoutFilter(context.getOperationParams()));

        presetValues.putAll(values);
        return presetValues;
    }

    private static Either<Object, OperationResult> replaceNullValueToEmptyStringAndReturn(Object parameters)
    {
        replaceValuesToString(parameters);

        return Either.first(parameters);
    }

    static void replaceValuesToString(Object parameters)
    {
        if (parameters instanceof DynamicPropertySet)
        {
            for (DynamicProperty property : (DynamicPropertySet) parameters)
            {
                if (property.getValue() == null)
                {
                    property.setValue("");
                }
                else if (property.getValue().getClass() != String.class &&
                        property.getValue().getClass() != Boolean.class &&
                        !(property.getValue() instanceof Object[]))
                {
                    property.setValue(property.getValue().toString());
                }
            }
        }
    }


}
