package com.developmentontheedge.be5.server.operations.support;

import com.developmentontheedge.be5.base.model.UserInfo;
import com.developmentontheedge.be5.base.services.Meta;
import com.developmentontheedge.be5.base.services.UserAwareMeta;
import com.developmentontheedge.be5.database.DbService;
import com.developmentontheedge.be5.databasemodel.DatabaseModel;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.operation.model.Operation;
import com.developmentontheedge.be5.operation.model.OperationResult;
import com.developmentontheedge.be5.operation.services.OperationBuilder;
import com.developmentontheedge.be5.operation.services.validation.Validator;
import com.developmentontheedge.be5.operation.support.BaseOperationSupport;
import com.developmentontheedge.be5.query.services.QueriesService;
import com.developmentontheedge.be5.server.helpers.DpsHelper;
import com.developmentontheedge.be5.server.model.FrontendAction;
import com.developmentontheedge.be5.web.Request;
import com.developmentontheedge.be5.web.Session;

import javax.inject.Inject;


public abstract class OperationSupport extends BaseOperationSupport implements Operation
{
    protected Meta meta;
    protected UserAwareMeta userAwareMeta;
    protected DbService db;
    protected DatabaseModel database;
    protected DpsHelper dpsHelper;
    protected Validator validator;
    protected OperationBuilder.OperationsFactory operations;
    protected QueriesService queries;

    protected Session session;
    protected Request request;
    protected UserInfo userInfo;

    @Inject
    protected void inject(Meta meta, UserAwareMeta userAwareMeta, DbService db, DatabaseModel database,
                       DpsHelper dpsHelper, Validator validator, OperationBuilder.OperationsFactory operations,
                       QueriesService queries, Session session, Request request, UserInfo userInfo) {
        this.meta = meta;
        this.userAwareMeta = userAwareMeta;
        this.db = db;
        this.database = database;
        this.dpsHelper = dpsHelper;
        this.validator = validator;
        this.operations = operations;
        this.queries = queries;
        this.session = session;
        this.request = request;
        this.userInfo = userInfo;
    }

    protected Query getQuery()
    {
        return meta.getQuery(getInfo().getEntityName(), context.getQueryName());
    }

    protected void setResultFinished()
    {
        setResult(OperationResult.finished());
    }

    protected void setResultFinished(String message)
    {
        setResult(OperationResult.finished(message));
    }

    protected void setResultFinished(String message, FrontendAction... frontendActions)
    {
        setResult(OperationResult.finished(message, frontendActions));
    }

    protected void setResultFinished(FrontendAction... frontendActions)
    {
        setResult(OperationResult.finished(null, frontendActions));
    }
}
