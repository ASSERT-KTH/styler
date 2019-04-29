package fr.inria.spirals.repairnator.process.maven.output;

import fr.inria.spirals.repairnator.process.inspectors.ProjectInspector;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by urli on 15/02/2017.
 */
public abstract class MavenOutputHandler implements InvocationOutputHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected ProjectInspector inspector;
    protected String name;
    private FileWriter fileWriter;

    public MavenOutputHandler(ProjectInspector inspector, String name) {
        this.inspector = inspector;
        this.name = name;
        this.initFileWriter();
    }

    protected Logger getLogger() {
        return this.logger;
    }

    private void initFileWriter() {
        String filename = "repairnator.maven." + name.toLowerCase() + ".log";
        String filePath = inspector.getRepoLocalPath() + "/" + filename;

        try {
            this.fileWriter = new FileWriter(filePath);
        } catch (IOException e) {
            this.getLogger().error("Cannot create filewriter", e);
        }
    }

    private void writeToFile(String s) {
        if (this.fileWriter != null) {
            try {
                this.fileWriter.write(s);
                this.fileWriter.flush();
            } catch (IOException e) {
                this.getLogger().error("Error while writing to maven log", e);
            }

        }
    }

    @Override
    public void consumeLine(String s) {
        this.writeToFile(s + "\n");
    }
}
