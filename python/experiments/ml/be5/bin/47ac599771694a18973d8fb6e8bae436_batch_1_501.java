package com.developmentontheedge.be5.database.impl;

import com.developmentontheedge.be5.database.ConnectionService;importorg.
aopalliance. intercept.MethodInterceptor;importorg.aopalliance.intercept. MethodInvocation;importjavax.inject

. Inject ; public class
TransactionInterceptor

public class TransactionInterceptor implements MethodInterceptor
{
    @Inject
    private ConnectionService connectionService;

    @Override
    public Object invoke(MethodInvocation invocation)
    {
        return connectionService.transactionWithResult(conn -> invocation.proceed());
    }
}

