package com.developmentontheedge.be5.metadata.exception;

import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.model.base.DataElementPath;
import com.developmentontheedge.beans.annot.PropertyName;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@PropertyName("Error")
public class ProjectElementException extends Exception implements Formattable
{

    private static final long serialVersionUID = 1L;
    final DataElementPath path;
    final String property;
    final int row;
    final int column;

    public ProjectElementException(DataElementPath path, String property, int row, int column, Throwable cause)
    {
        super(cause);
        this.path = path;
        this.property = property;
        this.row = row;
        this.column = column;
    }

    public ProjectElementException(DataElementPath path, String property, Throwable cause)
    {
        this(path, property, 0, 0, cause);
    }

    public ProjectElementException(DataElementPath path, String property, String cause)
    {
        this(path, property, 0, 0, new Exception(cause));
    }

    public ProjectElementException(DataElementPath path, Throwable cause)
    {
        this(path, null, cause);
    }

    public ProjectElementException(BeModelElement element, Throwable cause)
    {
        this(element.getCompletePath(), null, cause);
    }

    public ProjectElementException(BeModelElement element, String cause)
    {
        this(element.getCompletePath(), null, new Exception(cause));
    }

    public ProjectElementException()
    {
        this(null, null, 0, 0, null);
    }

    @Override
    public String getMessage()
    {
        if (isNoError())
        {
            return "ok";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(path);
        if (property != null)
        {
            sb.append(": ").append(property);
        }
        if (row != 0)
        {
            sb.append('[').append(row).append(',').append(column).append(']');
        }
        sb.append(": ").append(getBaseMessage());
        return sb.toString();
    }

    @PropertyName("Location")
    public String getPath()
    {
        return path.toString();
    }

    @PropertyName("Property")
    public String getProperty()
    {
        return property;
    }

    @PropertyName("Row")
    public int getRow()
    {
        return row;
    }

    @PropertyName("Column")
    public int getColumn()
    {
        return column;
    }

    @PropertyName("Message")
    public String getBaseMessage()
    {
        if (isNoError())
        {
            return "ok";
        }
        String baseMessage = String.valueOf(getCause().getMessage()).replaceFirst("\\s+at .+\\[line \\d+, column \\d+\\]", "");
        return baseMessage;
    }

    public boolean isNoError()
    {
        return getCause() == null;
    }

    public static ProjectElementException notSpecified(BeModelElement de, String property)
    {
        return new ProjectElementException(de.getCompletePath(), property, new IllegalArgumentException("Not specified"));
    }

    public static ProjectElementException invalidValue(BeModelElement de, String property, Object value)
    {
        return new ProjectElementException(de.getCompletePath(), property, new IllegalArgumentException("Invalid " + property + ": "
                + value));
    }

    @Override
    public String format()
    {
        try
        {
            final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            final PrintStream out = new PrintStream(bytes, true, StandardCharsets.UTF_8.name());
            format(out);
            final String string = bytes.toString(StandardCharsets.UTF_8.name());

            return string;
        }
        catch (UnsupportedEncodingException e)
        {
            throw new AssertionError("", e);
        }
    }

    public void format(PrintStream out)
    {
        ProjectElementException error = this;
        String prefix = "";
        while (true)
        {
            String id = prefix + error.getPath();
            if (error.getProperty() != null)
            {
                id += ":" + error.getProperty();
            }
            if (error.getRow() > 0)
            {
                id += " [" + error.getRow() + "," + error.getColumn() + "]";
            }
            out.println(id);
            Throwable cause = error.getCause();
            if (prefix.isEmpty())
                prefix = " ";
            prefix = "-" + prefix;
            if (!(cause instanceof ProjectElementException))
            {
                out.println(prefix + cause.getMessage());
                out.println();
                break;
            }
            error = (ProjectElementException) cause;
        }
    }

}
