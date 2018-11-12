package se.kth.tcs.autocheckstyle.process.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckstyleResults {
    HashMap<String, List<CheckstyleError>> filesErrors;

    public CheckstyleResults(){
        filesErrors = new HashMap<>();
    }

    public void addAuditEvent(AuditEvent auditEvent){
        final String file = auditEvent.getFileName();
        if ( !filesErrors.containsKey(file) ){
            addFile(file);
        }
        this.filesErrors.get(file).add(new CheckstyleError(auditEvent));
    }

    public void addFile(String file){
        this.filesErrors.put(file, new ArrayList<>());
    }
}
