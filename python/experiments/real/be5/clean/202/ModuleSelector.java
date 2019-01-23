package com.developmentontheedge.be5.metadata.model.editors;

import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.beans.editors.StringTagEditor;

public class ModuleSelector extends StringTagEditor
{
    @Override
    public String[] getTags()
    {
        try
        {
            Project project = ((BeModelElement) getBean()).getProject();
            return project.allModules().toArray(String[]::new);
        }
        catch (Exception e)
        {
            return new String[]{Project.APPLICATION};
        }
    }
}
