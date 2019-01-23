package com.developmentontheedge.be5.metadata.util;

import com.developmentontheedge.be5.metadata.exception.ProcessInterruptedException;

public class DerivedController implements ProcessController
{
    private final ProcessController primary;
    private final String operation;
    private final double from;
    private final double to;

    public DerivedController(ProcessController primary, double from, double to, String operation)
    {
        this.primary = primary;
        this.from = from;
        this.to = to;
        this.operation = operation;
    }

    public DerivedController(ProcessController primary, double from, double to)
    {
        this(primary, from, to, null);
    }

    @Override
    public void setOperationName(String name)
    {
        if (operation != null)
        {
            primary.setOperationName(operation + ": " + name);
        }
        else
        {
            primary.setOperationName(name);
        }
    }

    @Override
    public void setProgress(double progress) throws ProcessInterruptedException
    {
        primary.setProgress(progress * (to - from) + from);
    }

    @Override
    public void info(String msg)
    {
        primary.info(msg);
    }

    @Override
    public void error(String msg)
    {
        primary.error(msg);
    }
}
