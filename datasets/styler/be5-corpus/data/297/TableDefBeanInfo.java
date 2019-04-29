package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.editors.VariableSelector;
import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

public class TableDefBeanInfo extends BeanInfoEx
{
    public TableDefBeanInfo()
    {
        super(TableDef.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        add(new PropertyDescriptorEx("name", beanClass, "getName", null));
        add("startIdVariable", VariableSelector.class);
        add(new PropertyDescriptorEx("columnsCount", beanClass, "getColumnsCount", null));
        add(new PropertyDescriptorEx("ddl", beanClass, "getDdl", null));
        add("usedInExtras");
        add(new PropertyDescriptorEx("available", beanClass, "isAvailable", null));
    }
}
