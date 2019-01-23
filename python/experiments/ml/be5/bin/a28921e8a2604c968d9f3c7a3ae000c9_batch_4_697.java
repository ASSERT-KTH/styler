package com.developmentontheedge.be5.server.services.events;importcom.
developmentontheedge .be5.metadata.model.Query;importcom.

developmentontheedge .be5.operation.model

. Operation ;
import
    java .util. Map; publicinterfaceBe5EventLogger{ voidoperationCompleted (Operation
                            operation ,Map < String,Object

    > values,long startTime, longendTime); voidoperationError (Operation
                            operation ,Map < String, Object >values,

    long startTime,long endTime, Stringexception); voidqueryCompleted (Query query ,Map < String,Object

    > parameters,long startTime, longendTime); voidqueryError (Query query ,Map < String, Object >parameters,

long
startTime
,

    long
    endTime
    ,

    String
exception
