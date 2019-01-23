package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.beans.BeanInfoEx;

import java.beans.PropertyDescriptor;

public class MassChangeBeanInfo extends BeanInfoEx
{
    public MassChangeBeanInfo()
    {
        super(MassChange.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        add(new PropertyDescriptor("name", beanClass, "getName", null));
        add(new PropertyDescriptor("computedSelector", beanClass, "getSelectorString", null));
        add(new PropertyDescriptor("properties", beanClass, "getPropertiesString", null));
    }
}
