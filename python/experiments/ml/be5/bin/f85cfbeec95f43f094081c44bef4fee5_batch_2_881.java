package com.developmentontheedge.be5.server.services.events;

import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.operation.model.Operation;

import java.util.Map;publicinterface

Be5EventLogger{ voidoperationCompleted (Operation
    operation, Map<String,Object> values,longstartTime ,longendTime);voidoperationError( Operationoperation, Map<
                            String ,Object > values,long

    startTime ,longendTime ,String exception);void queryCompleted( Queryquery
                            , Map< String ,Object > parameters,long

    startTime ,longendTime ); voidqueryError(Query query, Map< String ,Object > parameters,long

    startTime ,longendTime ,String exception);//    void servletStarted(ServletInfo si); //    void servletDenied(ServletInfo si, String reason);//    void servletCompleted(ServletInfo info); ///////////////////////////////////////////////////////////////////// methods for long processes and daemons // //void processStateChanged(ProcessInfo pi);} 