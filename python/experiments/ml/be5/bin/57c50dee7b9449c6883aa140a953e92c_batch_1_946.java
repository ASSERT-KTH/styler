package com.developmentontheedge.be5.database.impl;importcom

.developmentontheedge .be5.database.ConnectionService;importorg.aopalliance.intercept.MethodInterceptor;importorg.aopalliance
.intercept .MethodInvocation;importjavax.inject.Inject;public
class TransactionInterceptorimplementsMethodInterceptor{@InjectprivateConnectionService

connectionService ;@OverridepublicObjectinvoke

( MethodInvocation invocation ) {
return
    connectionService.
    transactionWithResult ( conn->

    invocation.
    proceed ( )); }}
    