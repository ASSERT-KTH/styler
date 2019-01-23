package com.developmentontheedge.be5.web.impl;

import com.developmentontheedge.be5.web.Request;
import com.developmentontheedge.be5.web.Session;

import javax.inject.Inject;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class RequestImpl implements Request
{
    private final HttpServletRequest raw;
    private final String remoteAddr;

    @Inject
    public RequestImpl(HttpServletRequest raw)
    {
        this.raw = raw;
        this.remoteAddr = getClientIpAddr(raw);
    }

    @Override
    public Object getAttribute(String name)
    {
        return raw.getAttribute(name);
    }

    @Override
    public void setAttribute(String name, Object value)
    {
        raw.setAttribute(name, value);
    }

    @Override
    public Session getSession()
    {
        return new SessionImpl(raw.getSession());
    }

    @Override
    public Session getSession(boolean create)
    {
        HttpSession rawSession = raw.getSession(create);
        if (rawSession == null) return null;
        return new SessionImpl(rawSession);
    }

    @Override
    public String getSessionId()
    {
        return getSession().getSessionId();
    }

    @Override
    public String get(String name)
    {
        return raw.getParameter(name);
    }

    @Override
    public List<String> getList(String name)
    {
        return Arrays.asList(getParameterValues(name));
    }

    @Override
    public String[] getParameterValues(String name)
    {
        String[] values = raw.getParameterValues(name + "[]");
        if (values == null)
        {
            String value = raw.getParameter(name);
            if (value != null)
            {
                return new String[]{value};
            }
            else
            {
                return new String[]{};
            }
        }
        return values;
    }

    @Override
    public Map<String, String[]> getParameters()
    {
        return Collections.unmodifiableMap(raw.getParameterMap());
    }

    @Override
    public String getRequestUri()
    {
        return raw.getRequestURI();
    }

    @Override
    public String getRemoteAddr()
    {
        return remoteAddr;
    }

    @Override
    public Locale getLocale()
    {
        return raw.getLocale();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException
    {
        return raw.getInputStream();
    }

    @Override
    public HttpServletRequest getRawRequest()
    {
        return raw;
    }

    @Override
    public HttpSession getRawSession()
    {
        return getRawRequest().getSession();
    }

    @Override
    public String getServerUrl()
    {
        String scheme = raw.getScheme() + "://";
        String serverName = raw.getServerName();
        String serverPort = (raw.getServerPort() == 80) ? "" : ":" + raw.getServerPort();
        return scheme + serverName + serverPort;
    }

    @Override
    public String getServerUrlWithContext()
    {
        String contextPath = raw.getContextPath();
        return getServerUrl() + contextPath;
    }

    @Override
    public String getContextPath()
    {
        return raw.getContextPath();
    }

    @Override
    public String getBody()
    {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = raw.getReader())
        {
            String str;

            if ((str = br.readLine()) != null)
            {
                sb.append(str);
            }

            while ((str = br.readLine()) != null)
            {
                sb.append("\n");
                sb.append(str);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return sb.toString();
    }

    /**
     * https://stackoverflow.com/a/15323776
     *
     * @return remote address of a client
     */
    private String getClientIpAddr(HttpServletRequest request)
    {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
