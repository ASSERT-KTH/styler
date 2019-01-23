package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.beans.annot.PropertyName;

@PropertyName("Operation")
public class JavaOperation extends Operation
{
    protected JavaOperation(String name, Entity entity)
    {
        super(name, OPERATION_TYPE_JAVA, entity);
    }
}
