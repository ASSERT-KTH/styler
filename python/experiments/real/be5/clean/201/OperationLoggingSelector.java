package com.developmentontheedge.be5.metadata.model.editors;

import com.developmentontheedge.be5.metadata.model.Operation;
import com.developmentontheedge.beans.editors.StringTagEditor;

public class OperationLoggingSelector extends StringTagEditor
{
    @Override
    public String[] getTags()
    {
        return Operation.getOperationLoggingOptions();
    }
}