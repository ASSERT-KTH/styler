packagecom .developmentontheedge.be5.server.services.events;importcom.developmentontheedge.be5.metadata.model.Query;

 import com.developmentontheedge.be5.operation.model.Operation;
import java.util.Map;publicinterfaceBe5EventLogger{voidoperationCompleted

( Operationoperation,Map<String

, Object >
values
    , longstartTime, longendTime );voidoperationError (Operation operation,
                            Map <String , Object>values

    , longstartTime, longendTime ,Stringexception) ;void queryCompleted(
                            Query query, Map <String , Object>parameters

    , longstartTime, longendTime );voidqueryError (Query query, Map <String , Object>parameters

    , longstartTime, longendTime ,Stringexception) ;//    void servletStarted(ServletInfo si); //    void servletDenied(ServletInfo si, String reason);//    void servletCompleted(ServletInfo info); /////////////////////////////////////////////////////////////////// // methods for long processes and daemons// //void processStateChanged(ProcessInfo pi); }