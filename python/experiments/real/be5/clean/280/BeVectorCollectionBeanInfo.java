package com.developmentontheedge.be5.metadata.model.base;

import com.developmentontheedge.beans.BeanInfoEx;

import java.beans.PropertyDescriptor;

public class BeVectorCollectionBeanInfo extends BeanInfoEx
{
    public BeVectorCollectionBeanInfo()
    {
        super(BeVectorCollection.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        add(new PropertyDescriptor("name", beanClass, "getName", null));
        add(new PropertyDescriptor("size", beanClass, "getSize", null));
        add(new PropertyDescriptor("errors", beanClass, "hasErrors", null));
    }
}
