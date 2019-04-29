package com.developmentontheedge.be5.web;

import javax.servlet.http.HttpServletResponse;


public interface Response
{
    /**
     * @param value object for send as json
     */
    void sendAsJson(Object value);

    /**
     * @param value  object for send as json
     * @param status {@link javax.servlet.http.HttpServletResponse}
     */
    void sendErrorAsJson(Object value, int status);

    /**
     * @param status {@link javax.servlet.http.HttpServletResponse}
     */
    void setStatus(int status);

    void sendJson(String json);

    void sendHtml(String json);

    void sendXml(String xml);

    HttpServletResponse getRawResponse();

    void redirect(String location);
}
