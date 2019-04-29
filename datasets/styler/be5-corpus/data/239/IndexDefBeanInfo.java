package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

public class IndexDefBeanInfo extends BeanInfoEx
{
    public IndexDefBeanInfo()
    {
        super(IndexDef.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        add(new PropertyDescriptorEx("name", beanClass, "getName", null));
        add("unique");
        add(new PropertyDescriptorEx("definition", beanClass, "getCreateDdl", null));
        add("usedInExtras");
        add(new PropertyDescriptorEx("available", beanClass, "isAvailable", null));
    }
}
