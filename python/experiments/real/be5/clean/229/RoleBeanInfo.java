package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

import java.beans.PropertyDescriptor;

public class RoleBeanInfo extends BeanInfoEx
{
    public RoleBeanInfo()
    {
        super(Role.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        add(new PropertyDescriptor("name", beanClass, "getName", null));
        add("usedInExtras");
        add(new PropertyDescriptorEx("available", beanClass, "isAvailable", null));
    }
}
