package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

public class ViewDefBeanInfo extends BeanInfoEx
{
    public ViewDefBeanInfo()
    {
        super(ViewDef.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        add(new PropertyDescriptorEx("name", beanClass, "getName", null));
        add("definition");
        add(new PropertyDescriptorEx("ddl", beanClass, "getDdl", null));
    }
}
