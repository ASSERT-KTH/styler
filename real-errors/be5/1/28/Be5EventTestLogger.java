package com.developmentontheedge.be5.server.services.events;

import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.operation.model.Operation;

import javax.inject.Inject;
import java.util.Map;

import static org.mockito.Mockito.mock;

public class Be5EventTestLogger implements Be5EventLogger
{
    public static Be5EventLogger mock = mock(Be5EventLogger.class);

    @Inject
    public Be5EventTestLogger(EventManager eventManager)
    {
        eventManager.addListener(this);
    }

    public static void clearMock()
    {
        mock = mock(Be5EventLogger.class);
    }

    @Override
    public void operationCompleted(Operation operation, Map<String, Object> values, long startTime, long endTime)
    {
        mock.operationCompleted(operation, values, startTime, endTime);
    }

    @Override
    public void operationError(Operation operation, Map<String, Object> values, long startTime, long endTime, String exception)
    {
        mock.operationError(operation, values, startTime, endTime, exception);
    }

    @Override
    public void queryCompleted(Query query, Map<String, Object> parameters, long startTime, long endTime)
    {
        mock.queryCompleted(query, parameters, startTime, endTime);
    }

    @Override
    public void queryError(Query query, Map<String, Object> parameters, long startTime, long endTime, String exception)
    {
        mock.queryError(query, parameters, startTime, endTime, exception);
    }

    @Override
    public void servletCompleted(String servletName, String requestUri, Map<String, ?> params, long startTime, long endTime)
    {
        mock.servletCompleted(servletName, requestUri, params, startTime, endTime);
    }

    @Override
    public void servletError(String servletName, String requestUri, Map<String, ?> params, long startTime, long endTime, String exception)
    {
        mock.servletError(servletName, requestUri, params, startTime, endTime, exception);
    }
}
