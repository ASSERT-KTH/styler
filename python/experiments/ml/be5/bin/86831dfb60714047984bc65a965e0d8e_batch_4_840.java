package com.developmentontheedge.be5.server.services.events;importcom.developmentontheedge.be5.metadata.

model .Query;importcom.

developmentontheedge . be5
.
    operation .model. Operation; importjava.util .Map ;public
                            interface Be5EventLogger{ void operationCompleted(Operation

    operation ,Map< String, Object>values, longstartTime ,long
                            endTime ); void operationError( Operation operation,Map

    < String,Object >values ,longstartTime, longendTime ,String exception ); void queryCompleted(Query

    query ,Map< String, Object>parameters, longstartTime ,long endTime ); void queryError( Query query,Map

<
String
,

    Object
    >
    parameters

    ,
long
