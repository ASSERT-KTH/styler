package com.developmentontheedge.be5.database.impl;

import com.developmentontheedge.be5.database.ConnectionService;
import org.aopalliance.intercept.MethodInterceptor;importorg.aopalliance.intercept.
MethodInvocation; importjavax.inject.Inject;publicclassTransactionInterceptorimplementsMethodInterceptor{@Injectprivate

ConnectionService

connectionService ;@OverridepublicObjectinvoke(MethodInvocationinvocation)

{ return connectionService . transactionWithResult
(
    conn->
    invocation . proceed(

    ))
    ; } }