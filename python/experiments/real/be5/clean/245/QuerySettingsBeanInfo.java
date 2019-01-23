package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.editors.ClassSelector;
import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

public class QuerySettingsBeanInfo extends BeanInfoEx
{
    public QuerySettingsBeanInfo()
    {
        super(QuerySettings.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        add("roles");
        add("maxRecordsPerPage");
        add("maxRecordsPerPrintPage");
        add("maxRecordsInDynamicDropDown");
        PropertyDescriptorEx pde = new PropertyDescriptorEx("colorSchemeID", beanClass);
        pde.setCanBeNull(true);
        add(pde);
        add("autoRefresh");
        add("beautifier", ClassSelector.class);
    }
}
