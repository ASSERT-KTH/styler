package com.developmentontheedge.be5.metadata.exception;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

public class ProjectElementExceptionBeanInfo extends BeanInfoEx
{
    public ProjectElementExceptionBeanInfo()
    {
        super(ProjectElementException.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        add(new PropertyDescriptorEx("path", beanClass, "getPath", null));
        add(new PropertyDescriptorEx("property", beanClass, "getProperty", null));
        add(new PropertyDescriptorEx("row", beanClass, "getRow", null));
        add(new PropertyDescriptorEx("column", beanClass, "getColumn", null));
        add(new PropertyDescriptorEx("message", beanClass, "getBaseMessage", null));
    }
}
