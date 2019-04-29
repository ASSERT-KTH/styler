package com.developmentontheedge.be5.operation.support;

import com.developmentontheedge.be5.base.FrontendConstants;
import com.developmentontheedge.be5.base.util.HashUrl;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.operation.OperationConstants;
import com.developmentontheedge.be5.operation.model.Operation;
import com.developmentontheedge.be5.operation.model.OperationContext;
import com.developmentontheedge.be5.operation.model.OperationInfo;
import com.developmentontheedge.be5.operation.model.OperationResult;
import com.developmentontheedge.be5.operation.model.OperationStatus;
import com.developmentontheedge.be5.operation.util.OperationUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public abstract class BaseOperationSupport implements Operation
{
    protected OperationInfo info;
    protected OperationContext context;
    private OperationResult operationResult;

    private final Map<String, Object> redirectParams = new HashMap<>();

    @Override
    public void initialize(OperationInfo info, OperationContext context, OperationResult operationResult)
    {
        this.info = info;
        this.context = context;
        this.operationResult = operationResult;
    }

    @Override
    public final OperationInfo getInfo()
    {
        return info;
    }

    @Override
    public OperationContext getContext()
    {
        return context;
    }

    @Override
    public Object getParameters(Map<String, Object> presetValues) throws Exception
    {
        return null;
    }

    @Override
    public final OperationStatus getStatus()
    {
        return operationResult.getStatus();
    }

    @Override
    public final OperationResult getResult()
    {
        return operationResult;
    }

    @Override
    public void setResult(OperationResult operationResult)
    {
        this.operationResult = operationResult;
    }

    @Override
    public Map<String, Object> getRedirectParams()
    {
        Map<String, Object> map = new HashMap<>();

        for (Map.Entry<String, Object> entry : context.getOperationParams().entrySet())
        {
            if (!redirectParams.containsKey(entry.getKey()) && entry.getValue() != null)
            {
                map.put(entry.getKey(), entry.getValue().toString());
            }
        }

        for (Map.Entry<String, Object> entry : redirectParams.entrySet())
        {
            if (entry.getValue() != null && !entry.getValue().toString().isEmpty())
            {
                map.put(entry.getKey(), entry.getValue().toString());
            }
        }

        return map;
    }

    /**
     * Puts additional parameters for redirect OperationResult.
     *
     * @param extra parameters map
     */
    @Override
    public void addRedirectParams(Map<String, ?> extra)
    {
        redirectParams.putAll(extra);
    }

    /**
     * Puts additional parameter for redirect OperationResult.
     *
     * @param name  parameter name
     * @param value parameter value
     */
    @Override
    public void addRedirectParam(String name, Object value)
    {
        addRedirectParams(Collections.singletonMap(name, value));
    }

    @Override
    public void removeRedirectParam(String name)
    {
        redirectParams.put(name, "");
    }

//    todo addRedirectParam from DPS in invoke as be3
//    public void addNotNullRedirectParam( Map<String, Object> presetValues, String name )
//    {
//        Object value = presetValues.get(name);
//        if(value != null)
//        {
//            addRedirectParams(Collections.singletonMap(name, value.toString()));
//        }
//    }
//
//    public void addNotNullRedirectParam( Map<String, Object> presetValues )
//    {
//        presetValues.forEach((key, value) -> addNotNullRedirectParam(presetValues, key));
//    }

    public void redirectThisOperation()
    {
        String url = new HashUrl(FrontendConstants.FORM_ACTION,
                getInfo().getEntityName(), getContext().getQueryName(), getInfo().getName())
                .named(getRedirectParams()).toString();
        setResult(OperationResult.redirect(url));
    }

    public void redirectThisOperationNewId(Object newID)
    {
        setResult(OperationResult.redirect(getUrlForNewRecordId(newID).toString()));
    }

    public void redirectToTable(String entityName, String queryName, Map<String, Object> params)
    {
        setResult(OperationResult.redirect(new HashUrl(FrontendConstants.TABLE_ACTION, entityName, queryName)
                .named(OperationUtils.paramsWithoutSelectedRows(params)).toString()));
    }

    public void redirectToTable(String entityName, String queryName)
    {
        redirectToTable(entityName, queryName, getRedirectParams());
    }

    public void redirectToTable(Query query, Map<String, Object> params)
    {
        redirectToTable(query.getEntity().getName(), query.getName(), params);
    }

    public HashUrl getUrlForNewRecordId(Object newID)
    {
        Map<String, Object> params = getRedirectParams();
        params.put(OperationConstants.SELECTED_ROWS, newID.toString());
        return new HashUrl(FrontendConstants.FORM_ACTION, getInfo().getEntityName(), context.getQueryName(), getInfo().getName())
                .named(params);
    }

}
