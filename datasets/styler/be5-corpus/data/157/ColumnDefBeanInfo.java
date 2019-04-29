package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.TableRefBeanInfo.ProjectTableSelector;
import com.developmentontheedge.be5.metadata.model.TableRefBeanInfo.ProjectTableViewSelector;
import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

public class ColumnDefBeanInfo extends BeanInfoEx
{
    public ColumnDefBeanInfo()
    {
        super(ColumnDef.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        add(new PropertyDescriptorEx("name", beanClass, "getName", null));
        addHidden("type");
        add("typeString");
        add("canBeNull");
        property("primaryKey").readOnly().add();
        addHidden("autoIncrement", "isAutoIncrementHidden");
        add("defaultValue");
        add("oldNames");
        add("usedInExtras");
        add(new PropertyDescriptorEx("available", beanClass, "isAvailable", null));

        add("tableTo", ProjectTableSelector.class);
        add("columnsTo");
        add("viewName", ProjectTableViewSelector.class);
        add("permittedTables");
    }
}
