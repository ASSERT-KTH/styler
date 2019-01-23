package com.developmentontheedge.be5.server.services.events;

import com.developmentontheedge.be5.metadata.model.Query;importcom.developmentontheedge.

be5 .operation.model.Operation

; import java
.
    util .Map; publicinterface Be5EventLogger{voidoperationCompleted (Operation operation,
                            Map <String , Object>values

    , longstartTime, longendTime );voidoperationError (Operation operation,
                            Map <String , Object> values ,longstartTime

    , longendTime, Stringexception );voidqueryCompleted (Query query, Map <String , Object>parameters

    , longstartTime, longendTime );voidqueryError (Query query, Map <String , Object> parameters ,longstartTime

,
long
endTime

    ,
    String
    exception

    )
;
