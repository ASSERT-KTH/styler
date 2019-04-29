package com.developmentontheedge.be5.metadata.exception;

import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.model.base.DataElementPath;

import java.nio.file.Path;

public class ReadException extends Exception implements Formattable
{
    private static final long serialVersionUID = 1L;

    public static final String LEE_NOT_FOUND = "File not found";
    public static final String LEE_NOT_A_FILE = "Not a regular file";
    public static final String LEE_UNREADABLE = "Unable to read file";
    public static final String LEE_ENCODING_ERROR = "Error decoding file content. Make sure the file was saved in UTF-8";
    public static final String LEE_INVALID_STRUCTURE = "Invalid file structure";
    public static final String LEE_INTERNAL_ERROR = "Internal error";

    private final Path path;
    private final DataElementPath element;
    private final String message;

    public ReadException(Path path, String message)
    {
        this(null, (DataElementPath) null, path, message);
    }

    public ReadException(Throwable t, Path path, String message)
    {
        this(t, (DataElementPath) null, path, message);
    }

    public ReadException(DataElementPath element, Path path, String message)
    {
        this(null, element, path, message);
    }

    public ReadException(BeModelElement element, Path path, String message)
    {
        this(null, element.getCompletePath(), path, message);
    }

    public ReadException(Throwable t, BeModelElement element, Path path)
    {
        this(t, element.getCompletePath(), path, null);
    }

    public ReadException(Throwable t, DataElementPath element, Path path)
    {
        this(t, element, path, null);
    }

    public ReadException(Throwable t, BeModelElement element, Path path, String message)
    {
        this(t, element.getCompletePath(), path, message);
    }

    public ReadException(Throwable t, DataElementPath element, Path path, String message)
    {
        super(t);
        this.path = path;
        this.element = element;
        this.message = message;
    }

    public ReadException attachElement(BeModelElement element)
    {
        return new ReadException(getCause(), element.getCompletePath(), path, message);
    }

    @Override
    public String getMessage()
    {
        String msg = message;
        if (getCause() != null)
        {
            if (msg == null)
            {
                msg = getCause().getMessage();
            }
            else
            {
                msg += ": " + getCause().getMessage();
            }
        }
        if (this.path != null)
        {
            msg += System.lineSeparator() + "\tFile: " + this.path;
        }
        if (this.element != null)
        {
            msg += System.lineSeparator() + "\tElement: " + this.element;
        }
        return msg;
    }

    public String getBaseMessage()
    {
        return message;
    }

    public Path getPath()
    {
        return path;
    }

    public DataElementPath getElement()
    {
        return element;
    }

    @Override
    public String format()
    {
        return getMessage();
    }

}
