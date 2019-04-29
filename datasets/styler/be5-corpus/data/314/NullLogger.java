package com.developmentontheedge.be5.metadata.util;


public class NullLogger implements ProcessController
{
    public NullLogger()
    {
    }

    @Override
    public void setOperationName(String name)
    {
    }

    @Override
    public void setProgress(double progress)
    {
    }

    @Override
    public void info(String msg)
    {
    }

    @Override
    public void error(String msg)
    {
    }
}
