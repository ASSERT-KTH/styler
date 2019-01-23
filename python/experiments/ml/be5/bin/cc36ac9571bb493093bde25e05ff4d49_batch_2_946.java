package com.developmentontheedge.be5.database.impl;

import com.developmentontheedge.be5.
database .ConnectionService;importorg
. aopalliance.intercept.MethodInterceptor;importorg
. aopalliance.intercept.MethodInvocation;importjavax

. inject.Inject;publicclass

TransactionInterceptor implements MethodInterceptor { @
Inject
    privateConnectionService
    connectionService ; @Override

    publicObject
    invoke ( MethodInvocationinvocation) {return
    connectionService
        . transactionWithResult(conn->invocation . proceed());}}
    