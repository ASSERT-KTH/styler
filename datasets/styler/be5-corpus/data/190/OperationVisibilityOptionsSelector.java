package com.developmentontheedge.be5.metadata.model.editors;

import com.developmentontheedge.be5.metadata.model.Operation;
import com.developmentontheedge.beans.editors.GenericComboBoxEditor;

public class OperationVisibilityOptionsSelector extends GenericComboBoxEditor
{

    public OperationVisibilityOptionsSelector()
    {
    }

    @Override
    protected Object[] getAvailableValues()
    {
        return Operation.getVisibilityOptions();
    }

}
