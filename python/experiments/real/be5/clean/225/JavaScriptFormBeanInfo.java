package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.editors.ModuleSelector;
import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

public class JavaScriptFormBeanInfo extends BeanInfoEx
{
    public JavaScriptFormBeanInfo()
    {
        super(JavaScriptForm.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        add(new PropertyDescriptorEx("name", beanClass, "getName", null));
        add(new PropertyDescriptorEx("module", beanClass, "getModuleName", "setModule"), ModuleSelector.class);
        add("relativePath");
        add(new PropertyDescriptorEx("path", beanClass, "getFilePath", null));
    }
}
