package com.developmentontheedge.be5.metadata.model.editors;

import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.beans.editors.StringTagEditor;

public class LanguageSelector extends StringTagEditor
{
    @Override
    public String[] getTags()
    {
        return ((BeModelElement) getBean()).getProject().getApplication().getLocalizations().names().toArray(String[]::new);
    }
}
