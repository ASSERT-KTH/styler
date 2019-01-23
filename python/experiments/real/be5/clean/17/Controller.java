package com.developmentontheedge.be5.web;

/**
 * <p>This is the general interface for controllers.</p>
 * <p>The identifier of the component is used to route requests to the component, e.g. <code>GET /api/socialLogin/fasebookAppId</code>
 * will use the <code>socialLogin</code> component and result of calling the {@link Request#getRequestUri()} will be <code>facebookAppId</code>.</p>
 *
 * @see Request
 * @see Response
 */
public interface Controller
{
    /**
     * <p>Generates some content of any content type.</p>
     * <p>Use the service provider to delegate the action to the model.
     * The business-logic must be implemented in the model, not in controllers (components, initializers).</p>
     *
     * @param req a request
     * @param res a response
     * @see Request
     * @see Response
     */
    void generate(Request req, Response res);
}
