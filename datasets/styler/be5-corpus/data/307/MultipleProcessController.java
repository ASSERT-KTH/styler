package com.developmentontheedge.be5.metadata.util;

import com.developmentontheedge.be5.metadata.exception.ProcessInterruptedException;

public class MultipleProcessController implements ProcessController
{
    private final ProcessController[] controllers;

    public MultipleProcessController(ProcessController... controllers)
    {
        this.controllers = controllers.clone();
    }

    @Override
    public void setOperationName(String name)
    {
        for (ProcessController controller : controllers)
        {
            controller.setOperationName(name);
        }
    }

    @Override
    public void setProgress(double progress) throws ProcessInterruptedException
    {
        for (ProcessController controller : controllers)
        {
            controller.setProgress(progress);
        }
    }

    @Override
    public void info(String msg)
    {
        for (ProcessController controller : controllers)
        {
            controller.info(msg);
        }
    }

    @Override
    public void error(String msg)
    {
        for (ProcessController controller : controllers)
        {
            controller.error(msg);
        }
    }
}
