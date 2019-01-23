package com.developmentontheedge.be5.server.services.events;

import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge

 . be5.operation.model.

Operation ; import
java
    . util.Map ;public interfaceBe5EventLogger{void operationCompleted( Operationoperation
                            , Map< String ,Object>

    values ,longstartTime ,long endTime);void operationError( Operationoperation
                            , Map< String ,Object > values,long

    startTime ,longendTime ,String exception);void queryCompleted( Queryquery , Map< String ,Object>

    parameters ,longstartTime ,long endTime);void queryError( Queryquery , Map< String ,Object > parameters,long

startTime
,
long

    endTime
    ,
    String

    exception
)
