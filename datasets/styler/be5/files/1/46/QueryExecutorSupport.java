package com.developmentontheedge.be5.server.queries.support;

import com.developmentontheedge.be5.base.model.UserInfo;
import com.developmentontheedge.be5.base.services.Meta;
import com.developmentontheedge.be5.base.services.UserAwareMeta;
import com.developmentontheedge.be5.database.DbService;
import com.developmentontheedge.be5.databasemodel.DatabaseModel;
import com.developmentontheedge.be5.operation.services.validation.Validator;
import com.developmentontheedge.be5.query.services.QueriesService;
import com.developmentontheedge.be5.query.support.BaseQueryExecutorSupport;
import com.developmentontheedge.be5.server.helpers.DpsHelper;
import com.developmentontheedge.be5.web.Request;
import com.developmentontheedge.be5.web.Session;

import javax.inject.Inject;


public abstract class QueryExecutorSupport extends BaseQueryExecutorSupport
{
    public DatabaseModel database;
    public DbService db;
    public DpsHelper dpsHelper;
    public Meta meta;
    public UserAwareMeta userAwareMeta;
    public QueriesService queries;
    public Validator validator;

    protected Request request;
    protected Session session;
    protected UserInfo userInfo;

    @Inject
    public void inject(Meta meta, UserAwareMeta userAwareMeta, DbService db, DatabaseModel database,
                       DpsHelper dpsHelper, Validator validator, QueriesService queries,
                       Session session, Request request, UserInfo userInfo) {
        this.meta = meta;
        this.userAwareMeta = userAwareMeta;
        this.db = db;
        this.database = database;
        this.dpsHelper = dpsHelper;
        this.validator = validator;
        this.queries = queries;
        this.session = session;
        this.request = request;
        this.userInfo = userInfo;
    }
}
