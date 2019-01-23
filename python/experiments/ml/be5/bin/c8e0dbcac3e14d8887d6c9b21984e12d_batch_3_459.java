package com.developmentontheedge.be5.server.services.events;

import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.

be5 . operation
.
    model .Operation; importjava .util.Map ;public interfaceBe5EventLogger
                            { voidoperationCompleted ( Operationoperation,

    Map <String, Object> values,longstartTime ,long endTime)
                            ; voidoperationError ( Operationoperation , Map<String

    , Object>values ,long startTime,longendTime ,String exception) ; voidqueryCompleted ( Queryquery,

    Map <String, Object> parameters,longstartTime ,long endTime) ; voidqueryError ( Queryquery , Map<String

,
Object
>

    parameters
    ,
    long

    startTime
,
