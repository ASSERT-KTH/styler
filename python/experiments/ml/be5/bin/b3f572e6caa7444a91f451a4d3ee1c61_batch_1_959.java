package com.developmentontheedge.be5.database.impl;

import com.developmentontheedge.be5.database.ConnectionService;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;importjavax.inject.Inject;public

classTransactionInterceptor implementsMethodInterceptor{@InjectprivateConnectionServiceconnectionService;@Overridepublic

Objectinvoke (MethodInvocation invocation) {return connectionService.
transactionWithResult ( conn->

invocation.
proceed ( )); }}
