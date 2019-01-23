package com.developmentontheedge.be5.metadata.model.editors;

import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.beans.editors.StringTagEditor;

public class ConnectionProfileNameSelector extends StringTagEditor
{
    /**
     * @return local and remote profiles, sorted by name
     */
    @Override
    public String[] getTags()
    {
        final Project project = (Project) getBean();
        final String[] profileNames = project.getProfileNames();

        return profileNames;
    }

}
