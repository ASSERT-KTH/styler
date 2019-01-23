package com.developmentontheedge.be5.database.impl;

import com.developmentontheedge.be5.database.ConnectionService;
import org.aopalliance.intercept.MethodInterceptor;import
org. aopalliance.intercept.MethodInvocation;importjavax.inject.Inject;publicclassTransactionInterceptor

implementsMethodInterceptor {@InjectprivateConnectionServiceconnectionService;@OverridepublicObjectinvoke


(MethodInvocation invocation) {return connectionService. transactionWithResult
(
   conn->
   invocation . proceed(

   ))
   ; } }