package se.kth.tcs.autocheckstyle.process.checkstyle;


import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.AutomaticBean;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CheckstyleCheckerListener extends AutomaticBean implements AuditListener {

    private final Logger LOGGER;

    private List<AuditEvent> events;
    private CheckstyleResults results;

    public CheckstyleCheckerListener(Logger logger) {
        this.LOGGER = logger;
        results = new CheckstyleResults();
    }

    @Override
    public void auditStarted(AuditEvent event) {
        LOGGER.info("Audit started.");
    }

    @Override
    public void auditFinished(AuditEvent event) {
        LOGGER.info("Audit finished.");
    }

    @Override
    public void fileStarted(AuditEvent event) {
    }

    @Override
    public void fileFinished(AuditEvent event) {
    }

    @Override
    public void addError(AuditEvent event) {
        LOGGER.info("Error : " + event.getFileName());
        this.results.addAuditEvent(event);
    }

    @Override
    public void addException(AuditEvent event, Throwable throwable) {

    }

    @Override
    protected void finishLocalSetup() throws CheckstyleException {

    }

    public CheckstyleResults getResults()
    {
        return results;
    }
}
