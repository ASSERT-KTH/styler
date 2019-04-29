package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.beans.annot.PropertyName;

@PropertyName("Operation")
public class JavaScriptOperation extends SourceFileOperation
{
    protected JavaScriptOperation(String name, Entity entity)
    {
        super(name, OPERATION_TYPE_JSSERVER, entity);
    }

    @Override
    public String getFileNameSpace()
    {
        return SourceFileCollection.NAMESPACE_JAVASCRIPT_OPERATION;
    }

    @Override
    public String getFileExtension()
    {
        return ".js";
    }
}
