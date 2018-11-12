package se.kth.tcs.autocheckstyle.process.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;

public class CheckstyleError {

    private final String file;
    private final SeverityLevel severityLevel;
    private final int line;
    private final int column;
    private final String sourceName;

    public CheckstyleError(AuditEvent auditEvent){
        file = auditEvent.getFileName();
        severityLevel = auditEvent.getSeverityLevel();
        line = auditEvent.getLine();
        column = auditEvent.getColumn();
        sourceName = auditEvent.getSourceName();
    }

    public String getFile(){
        return file;
    }

    public SeverityLevel getSeverityLevel(){
        return severityLevel;
    }

    public int getLine(){
        return line;
    }

    public int getColumn(){
        return column;
    }

    public String getSourceName(){
        return sourceName;
    }

}
