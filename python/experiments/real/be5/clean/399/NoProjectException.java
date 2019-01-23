package com.developmentontheedge.be5.metadata.exception;

public class NoProjectException extends Exception
{
    private static final long serialVersionUID = 1L;

    public NoProjectException(String projectName)
    {
        super("Project '" + projectName + "' not found");
    }
}
