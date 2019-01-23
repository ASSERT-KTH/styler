package com.developmentontheedge.be5.database.impl;
import com.developmentontheedge.be5.database.
ConnectionService ;importorg.aopalliance.intercept.

MethodInterceptor ;importorg.aopalliance.

intercept . MethodInvocation ; import
javax
    .inject
    . Inject ;public

    classTransactionInterceptor
    implements MethodInterceptor {@Inject privateConnectionService
    connectionService
        ; @OverridepublicObjectinvoke ( MethodInvocationinvocation){returnconnectionService.
    transactionWithResult
(
