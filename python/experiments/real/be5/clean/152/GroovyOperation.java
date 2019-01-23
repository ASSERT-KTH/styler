package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.beans.annot.PropertyName;

@PropertyName("Operation")
public class GroovyOperation extends SourceFileOperation
{
    protected GroovyOperation(String name, Entity entity)
    {
        super(name, OPERATION_TYPE_GROOVY, entity);
    }

    @Override
    public String getFileNameSpace()
    {
        return SourceFileCollection.NAMESPACE_GROOVY_OPERATION;
    }

    @Override
    public String getFileExtension()
    {
        return ".groovy";
    }
}
