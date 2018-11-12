package se.kth.tcs.autocheckstyle.process.checkstyle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import com.puppycrawl.tools.checkstyle.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.RootModule;


public class CheckstyleHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckstyleHelper.class);

    private static RootModule getRootModule(String name, ClassLoader moduleClassLoader)
        throws CheckstyleException {
        final ModuleFactory factory = new PackageObjectFactory(
            Checker.class.getPackage().getName(), moduleClassLoader);

        return (RootModule) factory.createModule(name);
    }

    public static CheckstyleResults runCheckstyle(List<File> filesToProcess)
        throws CheckstyleException, IOException {
        Options options = new Options("./out.xml");

        // setup the properties
        final Properties props = System.getProperties();

        // create a configuration
        final ThreadModeSettings multiThreadModeSettings =
            new ThreadModeSettings(options.checkerThreadsNumber,
                options.treeWalkerThreadsNumber);

        final ConfigurationLoader.IgnoredModulesOptions ignoredModulesOptions;

        ignoredModulesOptions = ConfigurationLoader.IgnoredModulesOptions.OMIT;


        final Configuration config = ConfigurationLoader.loadConfiguration(
            options.configurationFile, new PropertiesExpander(props),
            ignoredModulesOptions, multiThreadModeSettings);

        // create RootModule object and run it
        final int errorCounter;
        final ClassLoader moduleClassLoader = Checker.class.getClassLoader();
        final RootModule rootModule = getRootModule(config.getName(), moduleClassLoader);

        final CheckstyleResults results;

        try {
            final CheckstyleCheckerListener listener;

            listener = new CheckstyleCheckerListener(LOGGER);


            rootModule.setModuleClassLoader(moduleClassLoader);
            rootModule.configure(config);
            rootModule.addListener(listener);

            // run RootModule
            errorCounter = rootModule.process(filesToProcess);

            results = listener.getResults();
        } finally {
            rootModule.destroy();
        }

        return results;
    }

    private static class Options {
        private static final int DEFAULT_THREAD_COUNT = 1;

        private int checkerThreadsNumber = DEFAULT_THREAD_COUNT;

        private int treeWalkerThreadsNumber = DEFAULT_THREAD_COUNT;

        private String configurationFile = "./checkstyle.xml";

        private Path outputPath;

        Options(String output){
            outputPath = Paths.get(output);
        }
    }
}
