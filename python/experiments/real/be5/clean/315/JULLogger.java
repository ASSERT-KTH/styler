package com.developmentontheedge.be5.metadata.util;

import java.util.logging.Logger;

public class JULLogger implements ProcessController
{
    protected final Logger log;

    public JULLogger(Logger log)
    {
        this.log = log;
    }

    @Override
    public void setOperationName(String name)
    {
        log.info("Operation started: " + name);
    }

    @Override
    public void setProgress(double progress)
    {
        log.info("  progress: " + progress * 100 + "%");
    }

    public static String infoBlock(String info)
    {
        return "\n------------------------------------------------------------------------" +
                "\n" + info +
                "\n------------------------------------------------------------------------";
    }

    @Override
    public void info(String msg)
    {
        log.info(msg);
    }

    @Override
    public void error(String msg)
    {
        log.severe(msg);
    }

//    public static String infoBlock(String level, String info)
//    {
//        return "[" + level + "] " + IntStream.range(0,72 - level.length() - 3).map(ch -> "-").collect(Collectors.joining()) +
//                "\n" + info +
//                "\n------------------------------------------------------------------------";
//    }
}
