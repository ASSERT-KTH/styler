package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;

public class ModelValidationUtils
{
    public static void checkValueInSet(BeModelElement source, String property, Object value, Object[] set) throws ProjectElementException
    {
        for (Object element : set)
        {
            if (element.equals(value))
                return;
        }
        throw ProjectElementException.invalidValue(source, property, value);
    }
}
