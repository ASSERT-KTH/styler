package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

public class FreemarkerScriptBeanInfo extends BeanInfoEx
{
    public FreemarkerScriptBeanInfo()
    {
        super(FreemarkerScript.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        add(new PropertyDescriptorEx("name", beanClass, "getName", null));
        add(new PropertyDescriptorEx("path", beanClass, "getFilePath", null));
        add(new PropertyDescriptorEx("errors", beanClass, "hasErrors", null));
    }
}
