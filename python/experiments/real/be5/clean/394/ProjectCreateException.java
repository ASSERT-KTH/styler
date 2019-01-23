package com.developmentontheedge.be5.metadata.exception;

public class ProjectCreateException extends Exception
{
    private static final long serialVersionUID = 1L;

    public ProjectCreateException(String projectName, Throwable cause)
    {
        super("Unable to create project " + projectName, cause);
    }
}
