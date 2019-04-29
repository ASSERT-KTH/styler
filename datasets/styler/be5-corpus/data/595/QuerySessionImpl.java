package com.developmentontheedge.be5.server.services.impl;

import com.developmentontheedge.be5.query.QuerySession;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;


public class QuerySessionImpl implements QuerySession
{
    private final HttpSession session;

    @Inject
    public QuerySessionImpl(HttpSession session)
    {
        this.session = session;
    }

    @Override
    public Object get(String name)
    {
        return session.getAttribute(name);
    }
}