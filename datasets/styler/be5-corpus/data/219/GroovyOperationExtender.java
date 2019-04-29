package com.developmentontheedge.be5.metadata.model;


public class GroovyOperationExtender extends SourceFileOperationExtender
{
    public GroovyOperationExtender(Operation owner, String module)
    {
        super(owner, module);
        namespace = SourceFileCollection.NAMESPACE_GROOVY_EXTENDER;
    }

    @Override
    public String getFileExtension()
    {
        return ".groovy";
    }
}
