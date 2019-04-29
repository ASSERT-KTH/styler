package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.editors.RoleMultiSelector;
import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

import java.beans.IntrospectionException;

public class RoleSetBeanInfo extends BeanInfoEx
{
    public RoleSetBeanInfo()
    {
        super(RoleSet.class);
        setSubstituteByChild(true);
    }

    @Override
    protected void initProperties() throws Exception
    {
        addArrayProperty("rolesArray");
        addArrayProperty("excludedRolesArray");
        addHidden("usePrototype", "isPrototypeHidden");
        addHidden(new PropertyDescriptorEx("prototypeRoles", beanClass, "getPrototypeRoles", null), "isPrototypeHidden");
        add(new PropertyDescriptorEx("finalRoles", beanClass, "getFinalRolesString", null));
    }

    private void addArrayProperty(final String name) throws IntrospectionException
    {
        PropertyDescriptorEx pde = new PropertyDescriptorEx(name, beanClass);
        pde.setPropertyEditorClass(RoleMultiSelector.class);
        pde.setHideChildren(true);
        pde.setSimple(true);
        add(pde);
    }
}
