package com.developmentontheedge.be5.web.impl;

import com.developmentontheedge.be5.web.Response;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.servlet.http.HttpServletResponse;


public class ResponseImpl implements Response
{
    private static final Jsonb jsonb = JsonbBuilder.create();

    /**
     * Guarantees correct state of the response.
     */
    private final RawResponseWrapper rawWrapper;

    @Inject
    public ResponseImpl(HttpServletResponse rawResponse)
    {
        this.rawWrapper = new RawResponseWrapper(rawResponse);
    }

    @Override
    public void sendAsJson(Object value)
    {
        sendJson(jsonb.toJson(value));
    }

    @Override
    public void sendErrorAsJson(Object value, int status)
    {
        setStatus(status);
        sendJson(jsonb.toJson(value));
    }

    @Override
    public void setStatus(int status)
    {
        rawWrapper.setStatus(status);
    }

    @Override
    public void sendJson(String json)
    {
        // The MIME media type for JSON text is 'application/json'.
        // The default encoding is UTF-8. Source: RFC 4627, http://www.ietf.org/rfc/rfc4627.txt.
        sendText("application/json;charset=UTF-8", json);
    }

    @Override
    public void sendHtml(String html)
    {
        sendText("text/html;charset=UTF-8", html);
    }

    @Override
    public void sendXml(String xml)
    {
        // text/xml or application/xml
        // RFC 2376, http://www.ietf.org/rfc/rfc2376.txt
        sendText("application/xml;charset=UTF-8", xml);
    }

    private void sendText(String contentType, String text)
    {
        // The MIME media type for JSON text is 'application/json'.
        // The default encoding is UTF-8. Source: RFC 4627, http://www.ietf.org/rfc/rfc4627.txt.
        rawWrapper.setContentType(contentType);
        //response.setCharacterEncoding(StandardCharsets.UTF_8);
        rawWrapper.append(text);
        rawWrapper.flush();
    }

    @Override
    public HttpServletResponse getRawResponse()
    {
        return rawWrapper.getRawResponse();
    }

    @Override
    public void redirect(String location)
    {
        try
        {
            rawWrapper.getRawResponse().sendRedirect(location);
        }
        catch (Throwable e)
        {
            throw new RuntimeException("Response error", e);
        }
    }
}
