package com.developmentontheedge.be5.server.services.events;

import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.operation.
model.Operation;importjava

. util.Map;publicinterface

Be5EventLogger { void
operationCompleted
    ( Operationoperation, Map< String,Object> values, longstartTime
                            , longendTime ) ;voidoperationError

    ( Operationoperation, Map< String,Object> values, longstartTime
                            , longendTime , Stringexception ) ;voidqueryCompleted

    ( Queryquery, Map< String,Object> parameters, longstartTime , longendTime ) ;voidqueryError

    ( Queryquery, Map< String,Object> parameters, longstartTime , longendTime , Stringexception ) ;//    void servletStarted(ServletInfo si);//    void servletDenied(ServletInfo si, String reason);

//    void servletCompleted(ServletInfo info);
///////////////////////////////////////////////////////////////////
// methods for long processes and daemons

    //
    //void processStateChanged(ProcessInfo pi);
    }

    