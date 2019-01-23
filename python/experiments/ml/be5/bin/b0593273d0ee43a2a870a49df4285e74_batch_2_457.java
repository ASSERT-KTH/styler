package com.developmentontheedge.be5.database.impl;

import com.developmentontheedge.be5.database.ConnectionService;
import org.aopalliance.intercept.MethodInterceptor;
importorg .aopalliance.intercept.MethodInvocation;import

javax .inject.Inject;public

class TransactionInterceptor implements MethodInterceptor {
@
    Injectprivate
    ConnectionService connectionService ;@

    Overridepublic
    Object invoke (MethodInvocationinvocation ){
    return
        connectionService .transactionWithResult(conn-> invocation .proceed());}
    }
