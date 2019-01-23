package com.developmentontheedge.be5.database.impl;

import com.developmentontheedge.be5.database.ConnectionService;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax
.
    inject.
    Inject ; publicclass

    TransactionInterceptorimplements
    MethodInterceptor { @Injectprivate ConnectionServiceconnectionService
    ;
        @ OverridepublicObjectinvoke( MethodInvocation invocation){returnconnectionService.transactionWithResult
    (
conn
