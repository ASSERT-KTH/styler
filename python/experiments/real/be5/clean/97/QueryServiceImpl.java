package com.developmentontheedge.be5.query.services.impl;

import com.developmentontheedge.be5.base.services.Meta;
import com.developmentontheedge.be5.base.services.UserInfoProvider;
import com.developmentontheedge.be5.database.DbService;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.query.QuerySession;
import com.developmentontheedge.be5.query.impl.Be5QueryExecutor;
import com.developmentontheedge.be5.query.services.QueryService;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QueryServiceImpl implements QueryService
{
    private final Meta meta;
    private final DbService db;
    private final Provider<QuerySession> querySession;
    private final UserInfoProvider userInfoProvider;

    @Inject
    public QueryServiceImpl(Meta meta, DbService db, Provider<QuerySession> querySession, UserInfoProvider userInfoProvider)
    {
        this.meta = meta;
        this.db = db;
        this.querySession = querySession;
        this.userInfoProvider = userInfoProvider;
    }

    @Override
    public Be5QueryExecutor build(Query query, Map<String, ?> parameters)
    {
        Map<String, List<String>> listParams = getMapOfList(parameters);

        return new Be5QueryExecutor(query, listParams, userInfoProvider.get(),
                querySession.get(), meta, db);
    }

    @Override
    public Be5QueryExecutor build(Query query)
    {
        return build(query, Collections.emptyMap());
    }

    private Map<String, List<String>> getMapOfList(Map<String, ?> parameters)
    {
        Map<String, List<String>> listParams = new HashMap<>();
        parameters.forEach((k, v) -> listParams.put(k, getParameterList(v)));

        return listParams;
    }

    @SuppressWarnings("unchecked")
    private List<String> getParameterList(Object parameter)
    {
        if (parameter == null) return null;

        if (parameter instanceof List)
        {
            return (List<String>) parameter;
        }
        else
        {
            return Collections.singletonList(parameter.toString());
        }
    }
}
