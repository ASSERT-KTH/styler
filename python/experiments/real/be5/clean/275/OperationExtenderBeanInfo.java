package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.editors.ClassSelector;
import com.developmentontheedge.be5.metadata.model.editors.ModuleSelector;
import com.developmentontheedge.beans.BeanInfoEx;

public class OperationExtenderBeanInfo extends BeanInfoEx
{
    public OperationExtenderBeanInfo()
    {
        super(OperationExtender.class);
    }

    public OperationExtenderBeanInfo(Class<? extends OperationExtender> clazz)
    {
        super(clazz);
    }

    @Override
    protected void initProperties() throws Exception
    {
        add("className", ClassSelector.class);
        add("invokeOrder");
        add("originModuleName", ModuleSelector.class);
    }
}
