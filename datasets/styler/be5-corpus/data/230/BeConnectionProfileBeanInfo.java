package com.developmentontheedge.be5.metadata.model;


import com.developmentontheedge.beans.BeanInfoEx;

import java.beans.PropertyDescriptor;

public class BeConnectionProfileBeanInfo extends BeanInfoEx
{

    public BeConnectionProfileBeanInfo()
    {
        super(BeConnectionProfile.class);
    }

    public BeConnectionProfileBeanInfo(Class<?> beanClass)
    {
        super(beanClass);
    }

    public BeConnectionProfileBeanInfo(Class<?> beanClass, String resourceBundleName)
    {
        super(beanClass, resourceBundleName);
    }

    @Override
    protected void initProperties() throws Exception
    {
        super.initProperties();

        add("connectionUrl");
        add("username");
        add("password");
        add("protected");
        add("tomcatPath");
        add("tomcatAppName");
        add("tomcatManagerScriptUserName");
        add("tomcatManagerScriptPassword");
        add("tomcatManagerReloadUrlTemplate");
        addHidden("providerId");
        addHidden("driverDefinition");
        addHidden(new PropertyDescriptor("jdbcUrl", beanClass, "getJdbcUrl", null));
        addHidden(new PropertyDescriptor("properties", beanClass, "getProperties", null));
    }

}
