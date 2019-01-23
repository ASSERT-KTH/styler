package com.developmentontheedge.be5.database.impl;

import com.developmentontheedge.be5.database.ConnectionService;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;importjavax.inject.

Inject; publicclassTransactionInterceptorimplementsMethodInterceptor{@InjectprivateConnectionServiceconnectionService;

@Override publicObject invoke( MethodInvocationinvocation ){
returnconnectionService
    .transactionWithResult(conn
    ->invocation

        . proceed(

        ))
        ; } }