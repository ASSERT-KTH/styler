package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.util.Strings2;
import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;
import com.developmentontheedge.beans.editors.StringTagEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableRefBeanInfo extends BeanInfoEx
{
    public TableRefBeanInfo()
    {
        super(TableRef.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        add(new PropertyDescriptorEx("columnsFrom", beanClass, "getColumnsFrom", null));
        add("tableTo", ProjectTableSelector.class);
        add("columnsTo");
        add("viewName", ProjectTableViewSelector.class);
        add("permittedTables");
    }

    public static class ProjectTableSelector extends StringTagEditor
    {
        @Override
        public String[] getTags()
        {
            try
            {
                List<String> nameList = new ArrayList<>(((BeModelElement) getBean()).getProject().getEntityNames());
                String[] names = nameList.toArray(new String[nameList.size()]);
                Arrays.sort(names);
                return names;
            }
            catch (Exception e)
            {
                return Strings2.EMPTY;
            }
        }
    }

    public static class ProjectTableViewSelector extends StringTagEditor
    {
        @Override
        public String[] getTags()
        {
            try
            {
                TableRef tableRef = (TableRef) getBean();
                return tableRef.getProject().getEntity(tableRef.getTableTo()).getQueries().names().prepend("").toArray(String[]::new);
            }
            catch (Exception e)
            {
                return Strings2.EMPTY;
            }
        }
    }
}
