package com.developmentontheedge.be5.database.impl;

import com.developmentontheedge.be5.database.ConnectionService;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.inject.Inject;publicclassTransactionInterceptorimplementsMethodInterceptor

{@ Injectprivate ConnectionServiceconnectionService ;@ Overridepublic
Objectinvoke
    (MethodInvocation invocation) {returnconnectionService.

    transactionWithResult(conn->
    invocation
    {
        return connectionService.transactionWithResult(conn -> invocation.proceed());
    }
	}

