package com.developmentontheedge.be5.metadata.model.editors;

import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.model.RoleSet;
import com.developmentontheedge.be5.metadata.util.Strings2;
import com.developmentontheedge.beans.editors.GenericMultiSelectEditor;

import java.util.List;

public class RoleMultiSelector extends GenericMultiSelectEditor
{
    @Override
    protected String[] getAvailableValues()
    {
        try
        {
            Object bean = getBean();
            Project project = ((RoleSet) bean).getProject();
            List<String> roleList = project.getRolesWithGroups();
            return roleList.toArray(new String[roleList.size()]);
        }
        catch (Exception e)
        {
            return Strings2.EMPTY;
        }
    }
}
