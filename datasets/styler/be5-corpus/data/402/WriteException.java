package com.developmentontheedge.be5.metadata.exception;

import com.developmentontheedge.be5.metadata.model.base.BeModelElement;

public class WriteException extends Exception
{
    private static final long serialVersionUID = 1L;

    public WriteException(BeModelElement de, Throwable t)
    {
        super("Unable to write " + de.getCompletePath() + ": " + t.getMessage(), t);
    }
}
