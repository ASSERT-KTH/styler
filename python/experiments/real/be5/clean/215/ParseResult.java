package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.beans.annot.PropertyName;

public class ParseResult
{
    private static final ProjectElementException NO_ERROR = new ProjectElementException();

    private final String result;
    private final ProjectElementException error;

    public ParseResult(String result)
    {
        this.result = result;
        this.error = NO_ERROR;
    }

    public ParseResult(ProjectElementException error)
    {
        this.error = error;
        this.result = null;
    }

    public String validate() throws ProjectElementException
    {
        if (error != null && error != NO_ERROR)
            throw error;
        return result;
    }

    @PropertyName("Result")
    public String getResult()
    {
        return result;
    }

    @PropertyName("Error")
    public ProjectElementException getError()
    {
        return error;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ParseResult other = (ParseResult) obj;
        if ((error != null && error != NO_ERROR) || (other.error != null && error != NO_ERROR))
            return false;
        if (result == null)
        {
            if (other.result != null)
                return false;
        }
        else if (!result.equals(other.result))
            return false;
        return true;
    }
}
