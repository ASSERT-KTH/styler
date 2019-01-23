package com.developmentontheedge.be5.server.services.events;import

com. developmentontheedge.be5.metadata.model.Query;importcom.developmentontheedge.be5.operation.model.Operation;import
java. util.Map;publicinterfaceBe5EventLogger{voidoperationCompleted(Operationoperation,Map<String,Object>

values ,longstartTime,longendTime

) ; void
operationError
    ( Operationoperation, Map< String,Object> values, longstartTime
                            , longendTime , Stringexception)

    ; voidqueryCompleted( Queryquery ,Map<String ,Object >parameters
                            , longstartTime , longendTime ) ;voidqueryError

    ( Queryquery, Map< String,Object> parameters, longstartTime , longendTime , Stringexception)

    ; //    void servletStarted(ServletInfo si);//    void servletDenied(ServletInfo si, String reason);//    void servletCompleted(ServletInfo info); ///////////////////////////////////////////////////////////////////// methods for long processes and daemons ////void processStateChanged(ProcessInfo pi);}