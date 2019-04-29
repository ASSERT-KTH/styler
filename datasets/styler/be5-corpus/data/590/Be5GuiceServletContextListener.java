package com.developmentontheedge.be5.server.servlet;

import com.developmentontheedge.be5.metadata.serialization.ModuleLoader2;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceServletContextListener;

import java.util.logging.Logger;


public abstract class Be5GuiceServletContextListener extends GuiceServletContextListener
{
    private static final Logger log = Logger.getLogger(ModuleLoader2.class.getName());

    protected Stage getStage()
    {
        boolean mode = Be5GuiceServletContextListener.class.getClassLoader().getResource("dev.yaml") != null;

        Stage stage = mode ? Stage.DEVELOPMENT : Stage.PRODUCTION;

        log.info("Stage: " + stage);
        return stage;
    }
//
//    @Override
//    public void contextDestroyed(ServletContextEvent servletContextEvent)
//    {
//        getInjector().getInstance(DaemonStarterImpl.class).shutdown();
//
//        super.contextDestroyed(servletContextEvent);
//    }
}
