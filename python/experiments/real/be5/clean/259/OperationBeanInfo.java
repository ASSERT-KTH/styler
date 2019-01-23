package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.editors.ModuleSelector;
import com.developmentontheedge.be5.metadata.model.editors.OperationLoggingSelector;
import com.developmentontheedge.be5.metadata.model.editors.OperationVisibilityOptionsSelector;
import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

import java.beans.PropertyDescriptor;

public class OperationBeanInfo extends BeanInfoEx
{
    public OperationBeanInfo()
    {
        super(Operation.class);
    }

    public OperationBeanInfo(Class<? extends Operation> clazz)
    {
        super(clazz);
    }

    @Override
    public void initProperties() throws Exception
    {
        super.initProperties();
        add(new PropertyDescriptor("name", beanClass, "getName", null));
        add(new PropertyDescriptor("type", beanClass, "getType", null));
        add("notSupported");
        add("code");
        addHidden("records");
        add("visibleWhen", OperationVisibilityOptionsSelector.class);
        add("executionPriority");
        add("logging", OperationLoggingSelector.class);
        add("secure");
        add("confirm");
        add("wellKnownName");
        add("originModuleName", ModuleSelector.class);
        add("roles");
        add("usedInExtras");
        add("layout");
        add(new PropertyDescriptorEx("available", beanClass, "isAvailable", null));
        addHidden("contextID");
        addHidden("categoryID");
        addHidden(new PropertyDescriptor("icon", beanClass, "getIcon", null));
    }
}
