packagecom .developmentontheedge.be5.database.impl;importcom.developmentontheedge.be5.database.ConnectionService;

import org.aopalliance.intercept.MethodInterceptor;importorg
. aopalliance.intercept.MethodInvocation;importjavax
. inject.Inject;publicclassTransactionInterceptorimplements

MethodInterceptor {@InjectprivateConnectionServiceconnectionService

; @ Override public Object
invoke
   (MethodInvocation
   invocation ) {return

   connectionService.
   transactionWithResult ( conn->invocation .proceed
   (
       ) );}}