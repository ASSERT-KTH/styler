package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

public class SourceFileBeanInfo extends BeanInfoEx
{
    public SourceFileBeanInfo()
    {
        super(SourceFile.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        add(new PropertyDescriptorEx("name", beanClass, "getName", null));
        add(new PropertyDescriptorEx("path", beanClass, "getFilePath", null));
    }
}
