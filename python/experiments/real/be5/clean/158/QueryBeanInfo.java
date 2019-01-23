package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.editors.ClassSelector;
import com.developmentontheedge.be5.metadata.model.editors.ModuleSelector;
import com.developmentontheedge.be5.metadata.model.editors.OperationSelector;
import com.developmentontheedge.be5.metadata.model.editors.QueryTypeSelector;
import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

import java.beans.PropertyDescriptor;

public class QueryBeanInfo extends BeanInfoEx
{
    public QueryBeanInfo()
    {
        super(Query.class);
    }

    @Override
    public void initProperties() throws Exception
    {
        add(new PropertyDescriptor("name", beanClass, "getName", null));
        add("type", QueryTypeSelector.class);
        addHidden("query", "isQueryHidden");
        addHidden(new PropertyDescriptorEx("queryClass", beanClass, "getQuery", "setQuery"), ClassSelector.class, "isQueryClassHidden");
        findPropertyDescriptor("queryClass").setDisplayName("Query class");
        findPropertyDescriptor("queryClass").setShortDescription("Must extend com.developmentontheedge.enterprise.query.QueryIterator");

        add(new PropertyDescriptorEx("queryCompiled", beanClass, "getQueryCompiled", null));
        findPropertyDescriptor("queryCompiled").setHidden(true);
        add("menuName");
        add("titleName");
        addHidden("fileName", "isFileNameHidden");
        add("parametrizingOperationName", OperationSelector.class);
        add("operationNames");
        add("templateQueryName"); // TODO: create editor for this field
        add("shortDescription");
        add("messageWhenEmpty");
        add("wellKnownName");
        add("invisible");
        add("secure");
        add("slow");
        add("cacheable");
        add("defaultView");
        add("replicated");
        add("notSupported");
        add("originModuleName", ModuleSelector.class);
        add("roles");
        add("querySettings");
        add("newDataCheckQuery");
        add("usedInExtras");
        add("layout");
        add(new PropertyDescriptorEx("available", beanClass, "isAvailable", null));
        addHidden("contextID");
        addHidden("categoryID");
        addHidden(new PropertyDescriptor("icon", beanClass, "getIcon", null));
    }
}
