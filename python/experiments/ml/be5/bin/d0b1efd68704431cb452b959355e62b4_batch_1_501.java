package com.developmentontheedge.be5.database.impl;

import com.developmentontheedge.be5.database.ConnectionService;importorg.
aopalliance. intercept.MethodInterceptor;importorg.aopalliance.intercept. MethodInvocation;importjavax.inject

. Inject ; public class
TransactionInterceptor
    implementsMethodInterceptor
    { @ Injectprivate

    ConnectionServiceconnectionService
    ; @ OverridepublicObject invoke(
    MethodInvocation
        invocation ){returnconnectionService. transactionWithResult (conn->invocation.proceed(
    )
)
