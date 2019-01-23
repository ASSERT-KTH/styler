package com.developmentontheedge.be5.server.services.events;

import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.operation.model.Operation;

import java.util.Map;publicinterface

Be5EventLogger{ voidoperationCompleted (Operation
operation,
    Map< String,Object>values, longstartTime,long endTime);voidoperationError(Operationoperation ,Map< String,
                            Object >values , longstartTime,

    long endTime,String exception) ;voidqueryCompleted( Queryquery ,Map
                            < String, Object >parameters , longstartTime,

    long endTime); voidqueryError (Queryquery, Map< String, Object >parameters , longstartTime,

    long endTime,String exception) ;//    void servletStarted(ServletInfo si);//    void servletDenied(ServletInfo si, String reason);//    void servletCompleted(ServletInfo info); ///////////////////////////////////////////////////////////////////// methods for long processes and daemons ////void processStateChanged(ProcessInfo pi); } 