package com.developmentontheedge.be5.metadata.model.editors;

import com.developmentontheedge.be5.metadata.model.Operation;
import com.developmentontheedge.beans.editors.StringTagEditor;

public class OperationTypeSelector extends StringTagEditor
{
    @Override
    public String[] getTags()
    {
        return Operation.getOperationTypes();
    }
}