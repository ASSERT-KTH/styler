package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.editors.ClassSelector;

public class JavaOperationBeanInfo extends OperationBeanInfo
{
    public JavaOperationBeanInfo()
    {
        super(JavaOperation.class);
    }

    @Override
    public void initProperties() throws Exception
    {
        super.initProperties();
        findPropertyDescriptor("code").setPropertyEditorClass(ClassSelector.class);
    }
}
