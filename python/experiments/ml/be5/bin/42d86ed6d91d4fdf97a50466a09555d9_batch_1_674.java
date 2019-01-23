package com.developmentontheedge.be5.database.impl;

import com.developmentontheedge.be5.database.ConnectionService;importorg.aopalliance.intercept.MethodInterceptor
;import org.aopalliance.intercept.MethodInvocation;importjavax.inject.Inject;public
classTransactionInterceptor implementsMethodInterceptor{@InjectprivateConnectionServiceconnectionService;@Override

public Objectinvoke(MethodInvocationinvocation)

{ return connectionService . transactionWithResult
(
    conn->
    invocation . proceed(

    ))
    ; } }