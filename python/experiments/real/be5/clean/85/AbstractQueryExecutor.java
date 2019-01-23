package com.developmentontheedge.be5.query.impl;

import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.query.QueryExecutor;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;


public abstract class AbstractQueryExecutor implements QueryExecutor
{
    protected final Query query;

    protected int offset = 0;
    protected int limit = Integer.MAX_VALUE;

    protected int orderColumn = -1;
    protected String orderDir = "asc";

    protected Boolean selectable = false;

    public AbstractQueryExecutor(Query query)
    {
        this.query = Objects.requireNonNull(query);
    }

    @Override
    public final QueryExecutor offset(int offset)
    {
        checkArgument(offset >= 0);
        this.offset = offset;
        return this;
    }

    @Override
    public final QueryExecutor limit(int limit)
    {
        checkArgument(limit >= 0);
        this.limit = limit;
        return this;
    }

    @Override
    public final QueryExecutor order(int orderColumn, String orderDir)
    {
        checkArgument(orderColumn >= -2);
        this.orderColumn = orderColumn;
        this.orderDir = orderDir;
        return this;
    }

    @Override
    public final QueryExecutor selectable(boolean selectable)
    {
        this.selectable = selectable;
        return this;
    }

    @Override
    public int getOrderColumn()
    {
        return orderColumn + (selectable ? -1 : 0);
    }

    @Override
    public String getOrderDir()
    {
        return orderDir;
    }

    @Override
    public int getOffset()
    {
        return offset;
    }

    @Override
    public int getLimit()
    {
        return limit;
    }

    @Override
    public Boolean getSelectable()
    {
        return selectable;
    }

    public abstract String getFinalSql();
}
