package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.editors.StringTagEditor;

public class SqlColumnTypeBeanInfo extends BeanInfoEx
{
    public SqlColumnTypeBeanInfo()
    {
        super(SqlColumnType.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        add("typeName", TypeSelector.class);
        add("size");
        add("precision");
        add("enumValues");
    }

    public static class TypeSelector extends StringTagEditor
    {
        @Override
        public String[] getTags()
        {
            return SqlColumnType.TYPES;
        }
    }
}
