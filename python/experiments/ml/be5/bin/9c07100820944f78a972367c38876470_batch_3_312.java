package com.developmentontheedge.be5.database.impl;

import com.developmentontheedge.be5.database.ConnectionService;
import org.aopalliance.intercept.MethodInterceptor;import
org. aopalliance.intercept.MethodInvocation;importjavax.inject.
Inject
   ;public
   class TransactionInterceptor implementsMethodInterceptor

   {@
   Inject private ConnectionServiceconnectionService; @Override
   public
       Object invoke(MethodInvocationinvocation) { returnconnectionService.transactionWithResult(conn->
   invocation
.
