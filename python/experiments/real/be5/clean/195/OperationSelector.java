package com.developmentontheedge.be5.metadata.model.editors;

import com.developmentontheedge.be5.metadata.model.EntityItem;
import com.developmentontheedge.beans.editors.StringTagEditor;

public class OperationSelector extends StringTagEditor
{
    @Override
    public String[] getTags()
    {
        try
        {
            return ((EntityItem) getBean()).getEntity().getOperations().names().prepend("").toArray(String[]::new);
        }
        catch (Exception e)
        {
            return new String[]{""};
        }
    }
}