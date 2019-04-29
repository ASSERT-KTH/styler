package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;
import com.developmentontheedge.beans.editors.StringTagEditor;

public class IndexColumnDefBeanInfo extends BeanInfoEx
{
    public IndexColumnDefBeanInfo()
    {
        super(IndexColumnDef.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        add(new PropertyDescriptorEx("name", beanClass, "getName", null));
        add("transform", IndexTransformSelector.class);
    }

    public static class IndexTransformSelector extends StringTagEditor
    {
        @Override
        public String[] getTags()
        {
            return ColumnFunction.TRANSFORMS;
        }
    }
}