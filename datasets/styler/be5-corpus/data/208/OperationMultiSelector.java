package com.developmentontheedge.be5.metadata.model.editors;

import com.developmentontheedge.be5.metadata.model.OperationSet;
import com.developmentontheedge.be5.metadata.util.Strings2;
import com.developmentontheedge.beans.editors.GenericMultiSelectEditor;

public class OperationMultiSelector extends GenericMultiSelectEditor
{
    @Override
    protected String[] getAvailableValues()
    {
        try
        {
            return ((OperationSet) getBean()).getOwner().getEntity().getOperations().names().toArray(String[]::new);
        }
        catch (Exception e)
        {
            return Strings2.EMPTY;
        }
    }
}
