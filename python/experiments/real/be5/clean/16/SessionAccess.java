package com.developmentontheedge.be5.web;

/**
 * A high-level access to the session.
 *
 * @author lan
 */
public interface SessionAccess
{
    /**
     * Returns the current session identifier.
     */
    String getSessionId();

    /**
     * Retrieves named attribute from the session
     *
     * @param name an attribute name
     * @return an attribute value stored in the session
     */
    Object getAttribute(String name);

    /**
     * Stores named attribute into the session
     *
     * @param name  an attribute name
     * @param value an attribute value
     */
    void setAttribute(String name, Object value);
}
