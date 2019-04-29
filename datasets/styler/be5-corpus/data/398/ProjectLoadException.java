package com.developmentontheedge.be5.metadata.exception;

import java.nio.file.Path;

public class ProjectLoadException extends Exception
{
    private static final long serialVersionUID = 1L;

    public ProjectLoadException(Path path, Throwable cause)
    {
        super("Unable to load project from " + path, cause);
    }

    public ProjectLoadException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ProjectLoadException(String message)
    {
        super(message);
    }
}
