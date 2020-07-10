package org.opentosca.toscana.core.transformation.logging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

/**
 The logs of a specific transformation.
 */
public class LogImpl implements Log {

    private final static org.slf4j.Logger exceptionHandlingLogger = LoggerFactory.getLogger(LogImpl.class);

    private final List<LogEntry> logEntries = Collections.synchronizedList(new LinkedList<>());
    private final AtomicLong index = new AtomicLong(0);

    private final MemoryAppender memoryAppender = new MemoryAppender(this);
    private final PersistentAppender persistentAppender;

    /**
     @param logFile the logFile to which the Logger will write to
     */
    public LogImpl(File logFile) {
        persistentAppender = new PersistentAppender(logFile);
        if (logFile.exists()){
            readLogFromFile(logFile);
        }
    }

    private void readLogFromFile(File file) {
        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(file)))) {
            LogEntry predecessor = null;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                try {
                    LogEntry entry = new LogEntry(line, predecessor);
                    addLogEntry(entry);
                    predecessor = entry;
                } catch (LogParserException e) {
                    exceptionHandlingLogger.error("Failed to parse log line from file '{}'", file, e);
                }
            }
        } catch (FileNotFoundException e) {
            // noop
        }
    }

    @Override
    public void addLogEntry(LogEntry e) {
        e.setIndex(index.getAndIncrement());
        logEntries.add(e);
    }

    @Override
    public List<LogEntry> getLogEntries(int first, int last) {
        return getLogEntries(first, last, true);
    }

    private List<LogEntry> getLogEntries(int first, int last, boolean checkUpperBound) {
        if (0 > first || (last < first && checkUpperBound)) {
            throw new IllegalArgumentException("Given indices are not within the bound 0 <= first <= last");
        } else if (first >= logEntries.size()) {
            return Collections.unmodifiableList(new ArrayList<>());
        }
        return Collections.unmodifiableList(logEntries.subList(first, last + 1));
    }

    @Override
    public List<LogEntry> getLogEntries(int firstIndex) {
        return getLogEntries(firstIndex, logEntries.size() - 1, false);
    }

    @Override
    public Logger getLogger(String context) {
        Logger logger = (Logger) LoggerFactory.getLogger(context);

        logger.addAppender(memoryAppender);
        logger.addAppender(persistentAppender);
        logger.setLevel(Level.DEBUG);
        logger.setAdditive(true); /* set to true if root should log too */
        return logger;
    }

    @Override
    public Logger getLogger(Class context) {
        return getLogger(context.getName());
    }

    @Override
    public void close() {
        getLogger("EOL").info("End of log");
        memoryAppender.stop();
        persistentAppender.stop();
    }
}
