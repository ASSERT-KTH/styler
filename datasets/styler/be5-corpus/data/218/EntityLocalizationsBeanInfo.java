package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.beans.BeanInfoEx;

import java.beans.PropertyDescriptor;

public class EntityLocalizationsBeanInfo extends BeanInfoEx
{
    public EntityLocalizationsBeanInfo()
    {
        super(EntityLocalizations.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        add(new PropertyDescriptor("pairs", beanClass, "getPairs", null));
    }
}
