package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.editors.SourceFileSelector;


public class JavaScriptOperationBeanInfo extends OperationBeanInfo
{
    public JavaScriptOperationBeanInfo()
    {
        super(JavaScriptOperation.class);
    }

    @Override
    public void initProperties() throws Exception
    {
        super.initProperties();
        findPropertyDescriptor("code").setHidden(true);
        add(SourceFileSelector.register("fileName", beanClass, SourceFileCollection.NAMESPACE_JAVASCRIPT_OPERATION));
    }
}
