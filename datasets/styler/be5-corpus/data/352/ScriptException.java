package com.developmentontheedge.be5.metadata.scripts;


public class ScriptException extends RuntimeException
{
    public ScriptException(String msg)
    {
        super(msg);
    }

    public ScriptException(String msg, Throwable e)
    {
        super(msg, e);
    }
}
