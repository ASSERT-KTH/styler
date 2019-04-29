package com.developmentontheedge.be5.metadata.model.editors;

import com.developmentontheedge.be5.metadata.model.Daemon;
import com.developmentontheedge.beans.editors.StringTagEditor;

public class DaemonTypeSelector extends StringTagEditor
{

    public DaemonTypeSelector()
    {
    }

    @Override
    public String[] getTags()
    {
        return Daemon.getTypes();
    }

}
