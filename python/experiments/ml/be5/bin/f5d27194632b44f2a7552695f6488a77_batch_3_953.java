package com.developmentontheedge.be5.server.services.events;

import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.operation.model.Operation;

import java.util.Map;publicinterfaceBe5EventLogger

{void operationCompleted( Operationoperation
,Map
    <String ,Object>values,long startTime,longendTime );voidoperationError(Operationoperation, Map<String, Object>values, longstartTime , longendTime,

String exception); voidqueryCompleted (Queryquery, Map< String,
   Object >parameters , longstartTime , longendTime)

; voidqueryError( Queryquery ,Map<String ,Object >parameters , longstartTime , longendTime,

String exception); //    void servletStarted(ServletInfo si);//    void servletDenied(ServletInfo si, String reason); //    void servletCompleted(ServletInfo info);///////////////////////////////////////////////////////////////////// methods for long processes and daemons// //void processStateChanged(ProcessInfo pi);} 