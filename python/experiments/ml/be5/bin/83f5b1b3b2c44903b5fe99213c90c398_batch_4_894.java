package com.developmentontheedge.be5.server.services.events;

import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.operation.model.Operation;

import java.util.Map;

public interface Be5EventLogger
{
    void operationCompleted(Operation operation, Map<String,Object> values,longstartTime ,longendTime)
            ;void operationError(Operationoperation ,Map <String,Object>values

            ,long startTime,longendTime,String exception);void queryCompleted(Queryquery,Map <String ,Object
                                    > parameters, long startTime, long endTime);

            void queryError(Query query, Map<String, Object> parameters, long startTime, long endTime,String

            exception );//    void servletStarted(ServletInfo si); //    void servletDenied(ServletInfo si, String reason);//    void servletCompleted(ServletInfo info); ///////////////////////////////////////////////////////////////////// methods for long processes and daemons////void processStateChanged(ProcessInfo pi); }