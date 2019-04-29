package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.editors.SourceFileSelector;


public class GroovyOperationBeanInfo extends OperationBeanInfo
{
    public GroovyOperationBeanInfo()
    {
        super(GroovyOperation.class);
    }

    @Override
    public void initProperties() throws Exception
    {
        super.initProperties();
        findPropertyDescriptor("code").setHidden(true);
        add(SourceFileSelector.register("fileName", beanClass, SourceFileCollection.NAMESPACE_GROOVY_OPERATION));
    }
}
