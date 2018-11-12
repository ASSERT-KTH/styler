package se.kth.tcs.autocheckstyle;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.tcs.autocheckstyle.process.checkstyle.CheckstyleHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);


    public static void main(String... args) {
        BasicConfigurator.configure();

        ArrayList<File> files = new ArrayList<File>();

        files.add(new File("/home/benjaminl/Documents/checkstyle-repair/hook/src/main/java/se/kth/tcs/autocheckstyle/Main.java"));
        files.add(new File("/home/benjaminl/Documents/checkstyle-repair/hook/./src/main/java/se/kth/tcs/autocheckstyle/process/checkstyle/CheckstyleHelper.java"));

        try {
            CheckstyleHelper.runCheckstyle(files);
        } catch (Exception exeption) {
            LOGGER.error(exeption.toString());
        }
    }
}
