packagecom .developmentontheedge.be5.database.impl;importcom.developmentontheedge.be5.database.ConnectionService;

importorg .aopalliance.intercept.MethodInterceptor;importorg.aopalliance.intercept.MethodInvocation;importjavax.inject
. Inject;publicclassTransactionInterceptorimplementsMethodInterceptor{@Inject
private ConnectionServiceconnectionService;@OverridepublicObjectinvoke
( MethodInvocationinvocation){returnconnectionService.transactionWithResult

( conn->invocation.proceed(

) ) ; } }
