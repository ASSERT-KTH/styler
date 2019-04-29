package com.developmentontheedge.be5.metadata.exception;

import java.nio.file.Path;

public class ProjectSaveException extends Exception
{
    private static final long serialVersionUID = 1L;

    public ProjectSaveException(Path path, Throwable cause)
    {
        super("Unable to save project from " + path, cause);
    }
}
