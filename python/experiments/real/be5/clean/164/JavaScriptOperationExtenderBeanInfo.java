package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.editors.ModuleSelector;
import com.developmentontheedge.be5.metadata.model.editors.SourceFileSelector;
import com.developmentontheedge.beans.BeanInfoEx;

public class JavaScriptOperationExtenderBeanInfo extends BeanInfoEx
{
    public JavaScriptOperationExtenderBeanInfo()
    {
        super(JavaScriptOperationExtender.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        add("invokeOrder");
        add(SourceFileSelector.register("fileName", beanClass, SourceFileCollection.NAMESPACE_JAVASCRIPT_EXTENDER));
        add("originModuleName", ModuleSelector.class);
    }
}
