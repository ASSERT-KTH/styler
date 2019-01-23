package com.developmentontheedge.be5.database.impl;importcom.developmentontheedge.be5.database

.ConnectionService ;importorg.aopalliance.intercept.MethodInterceptor;importorg.aopalliance.intercept.MethodInvocation;import
javax. inject.Inject;publicclassTransactionInterceptorimplementsMethodInterceptor{@
Inject privateConnectionServiceconnectionService;@OverridepublicObject

invoke (MethodInvocationinvocation){return

connectionService . transactionWithResult ( conn
->
    invocation.
    proceed ( ))

    ;}
    } 