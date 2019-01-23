package com.developmentontheedge.be5.web;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

/**
 * Request injected to components.
 *
 * @author asko
 * @see Controller
 */
public interface Request extends SessionAccess, ParametersAccess
{
    Session getSession();

    Session getSession(boolean create);

    /**
     * Returns a remaining part of the request URI after the component ID.
     */
    String getRequestUri();

    /**
     * Returns the IP address of the client or last proxy that sent the request.
     */
    String getRemoteAddr();

    /**
     * Low-level request.
     */
    HttpServletRequest getRawRequest();

    /**
     * Low-level session.
     */
    HttpSession getRawSession();

    String getServerUrl();

    String getServerUrlWithContext();

    String getContextPath();

    String getBody();

    Locale getLocale();

    ServletInputStream getInputStream() throws IOException;
}
