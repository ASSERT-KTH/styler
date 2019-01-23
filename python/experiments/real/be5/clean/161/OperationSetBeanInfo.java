package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.editors.OperationMultiSelector;
import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

import java.beans.IntrospectionException;

public class OperationSetBeanInfo extends BeanInfoEx
{
    public OperationSetBeanInfo()
    {
        super(OperationSet.class);
        setSubstituteByChild(true);
    }

    @Override
    protected void initProperties() throws Exception
    {
        addArrayProperty("valuesArray");
        addArrayProperty("excludedValuesArray");
        addHidden("usePrototype", "isPrototypeHidden");
        addHidden(new PropertyDescriptorEx("prototypeValues", beanClass, "getPrototypeValues", null), "isPrototypeHidden");
        add(new PropertyDescriptorEx("finalValues", beanClass, "getFinalValuesString", null));
    }

    private void addArrayProperty(final String name) throws IntrospectionException
    {
        PropertyDescriptorEx pde = new PropertyDescriptorEx(name, beanClass);
        pde.setPropertyEditorClass(OperationMultiSelector.class);
        pde.setHideChildren(true);
        pde.setSimple(true);
        add(pde);
    }
}
