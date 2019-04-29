package com.developmentontheedge.be5.metadata.model;


public class JavaScriptOperationExtender extends SourceFileOperationExtender
{
    public static final String JAVASCRIPT_EXTENDER_CLASS_NAME = "com.developmentontheedge.enterprise.operations.JavaScriptOperationExtenderSupport";

    public JavaScriptOperationExtender(Operation owner, String module)
    {
        super(owner, module);
        namespace = SourceFileCollection.NAMESPACE_JAVASCRIPT_EXTENDER;
    }

    @Override
    public void setClassName(String className)
    {
    }

    @Override
    public String getClassName()
    {
        return JAVASCRIPT_EXTENDER_CLASS_NAME;
    }

    @Override
    public String getFileExtension()
    {
        return ".js";
    }
}
