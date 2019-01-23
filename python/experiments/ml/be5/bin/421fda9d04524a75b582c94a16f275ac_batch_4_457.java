package com.developmentontheedge.be5.database.impl;importcom. developmentontheedge.be5.database.ConnectionService;importorg.aopalliance.intercept.MethodInterceptor;importorg.
aopalliance. intercept.MethodInvocation;importjavax.inject.Inject;publicclassTransactionInterceptorimplementsMethodInterceptor
{ @InjectprivateConnectionServiceconnectionService;@Override

public Objectinvoke(MethodInvocationinvocation)

{ return connectionService . transactionWithResult
(
    conn->
    invocation . proceed(

    ))
    ; } }