package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.editors.ModuleSelector;
import com.developmentontheedge.be5.metadata.model.editors.QuerySelector;
import com.developmentontheedge.beans.BeanInfoEx;

import java.beans.PropertyDescriptor;

public class QuickFilterBeanInfo extends BeanInfoEx
{
    public QuickFilterBeanInfo()
    {
        super(QuickFilter.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        add(new PropertyDescriptor("name", beanClass, "getName", null));
        add("queryParam");
        add("targetQueryName", QuerySelector.class);
        add("filteringClass");
        add("originModuleName", ModuleSelector.class);
    }
}
