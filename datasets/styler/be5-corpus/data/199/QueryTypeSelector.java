package com.developmentontheedge.be5.metadata.model.editors;

import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.beans.editors.StringTagEditor;

public class QueryTypeSelector extends StringTagEditor
{
    @Override
    public String[] getTags()
    {
        return Query.getQueryTypes();
    }
}