package com.developmentontheedge.be5.server.servlet.support;

import com.developmentontheedge.be5.web.Response;
import com.developmentontheedge.be5.web.impl.ResponseImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ServletUtils
{
    static Response getResponse(HttpServletRequest request, HttpServletResponse response)
    {
        String origin = request.getHeader("Origin"); // TODO test origin

        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Origin", origin);
        response.addHeader("Access-Control-Allow-Methods", "POST, GET");
        response.addHeader("Access-Control-Max-Age", "1728000");

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        return new ResponseImpl(response);
    }

}
