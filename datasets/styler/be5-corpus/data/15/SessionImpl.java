package com.developmentontheedge.be5.web.impl;

import com.developmentontheedge.be5.web.Session;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SessionImpl implements Session
{
    private HttpSession raw;

    @Inject
    public SessionImpl(HttpSession session)
    {
        this.raw = session;
    }

    @Override
    public String getSessionId()
    {
        return raw.getId();
    }

    @Override
    public Object get(String name)
    {
        return raw.getAttribute(name);
    }

    @Override
    public void set(String name, Object value)
    {
        raw.setAttribute(name, value);
    }

    @Override
    public void remove(String name)
    {
        raw.removeAttribute(name);
    }

    @Override
    public HttpSession getRawSession()
    {
        return raw;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getAttributeNames()
    {
        return Collections.list(raw.getAttributeNames());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAttributes()
    {
        Map<String, Object> map = new HashMap<>();

        Enumeration<String> enumeration = raw.getAttributeNames();
        while (enumeration.hasMoreElements())
        {
            String name = enumeration.nextElement();
            map.put(name, get(name));
        }

        return map;
    }

    @Override
    public void invalidate()
    {
        raw.invalidate();
    }
}
