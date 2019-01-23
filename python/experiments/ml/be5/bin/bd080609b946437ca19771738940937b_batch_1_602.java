package com.developmentontheedge.be5.database.impl;

import com.developmentontheedge.be5.database.ConnectionService;
import org.aopalliance.intercept.MethodInterceptor;importorg
.aopalliance .intercept.MethodInvocation;importjavax.inject.Inject ;
public
    classTransactionInterceptor
    implements MethodInterceptor {@

    Injectprivate
    ConnectionService connectionService ;@Override publicObject
    invoke
        ( MethodInvocationinvocation){return connectionService .transactionWithResult(conn->invocation.
    proceed
(
