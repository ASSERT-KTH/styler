package com.developmentontheedge.be5.metadata.sql;

import com.developmentontheedge.beans.BeanInfoEx;

import java.beans.PropertyDescriptor;

public class ConnectionUrlBeanInfo extends BeanInfoEx
{
    public ConnectionUrlBeanInfo()
    {
        super(ConnectionUrl.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        add("host");
        add("port");
        add("db");
        addHidden(new PropertyDescriptor("properties", beanClass, "getProperties", null));
    }
}
