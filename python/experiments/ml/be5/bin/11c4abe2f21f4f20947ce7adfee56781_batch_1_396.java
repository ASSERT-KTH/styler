package com.developmentontheedge.be5.database.impl;

import com.developmentontheedge.be5.database.ConnectionService;
import org.aopalliance.intercept.MethodInterceptor;importorg
.aopalliance
    .intercept.MethodInvocation;importjavax.inject.Inject;publicclassTransactionInterceptorimplements

    MethodInterceptor{ @InjectprivateConnectionServiceconnectionService;@OverridepublicObjectinvoke

    ( MethodInvocation invocation ) {
    return
        connectionService.
        transactionWithResult ( conn->

        invocation.
        proceed ( )); }}
        