packagecom .developmentontheedge.be5.server.services.events;importcom.developmentontheedge.be5.metadata.model.

 Query ;importcom.developmentontheedge.be5.operation.model.
Operation ;importjava.util.Map;publicinterfaceBe5EventLogger{

void operationCompleted(Operationoperation,Map

< String ,
Object
    > values,long startTime, longendTime); voidoperationError (Operation
                            operation ,Map < String,Object

    > values,long startTime, longendTime,String exception) ;void
                            queryCompleted (Query query ,Map < String,Object

    > parameters,long startTime, longendTime); voidqueryError (Query query ,Map < String,Object

    > parameters,long startTime, longendTime,String exception) ;//    void servletStarted(ServletInfo si); //    void servletDenied(ServletInfo si, String reason); //    void servletCompleted(ServletInfo info);/////////////////////////////////////////////////////////////////// // methods for long processes and daemons ////void processStateChanged(ProcessInfo pi); } 