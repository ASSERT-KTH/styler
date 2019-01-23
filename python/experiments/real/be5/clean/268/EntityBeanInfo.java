package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.editors.ColumnSelector;
import com.developmentontheedge.be5.metadata.model.editors.EntityTypeSelector;
import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

import java.beans.PropertyDescriptor;

public class EntityBeanInfo extends BeanInfoEx
{
    public EntityBeanInfo()
    {
        super(Entity.class);
        try
        {
            setDisplayNameMethod(Entity.class.getMethod("getType"));
        }
        catch (Exception e)
        {
            throw new InternalError("Unexpected error while registering EntityBeanInfo: " + e);
        }
    }

    @Override
    public void initProperties() throws Exception
    {
        add(new PropertyDescriptorEx("name", beanClass, "getName", null));
        addHidden(new PropertyDescriptor("type", beanClass, "getType", "setType"));
        add("typeString", EntityTypeSelector.class);
        add("displayName");
        add("order");
        add("primaryKey", ColumnSelector.class);
        addHidden(new PropertyDescriptor("icon", beanClass, "getIcon", null));
        add("besql");
        add("usedInExtras");
        add(new PropertyDescriptorEx("available", beanClass, "isAvailable", null));
    }
}
