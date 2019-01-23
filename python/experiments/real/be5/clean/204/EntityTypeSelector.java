package com.developmentontheedge.be5.metadata.model.editors;

import com.developmentontheedge.be5.metadata.model.EntityType;
import com.developmentontheedge.be5.metadata.util.EnumsWithHumanReadableName;
import com.developmentontheedge.beans.editors.StringTagEditor;

public class EntityTypeSelector extends StringTagEditor
{

    @Override
    public String[] getTags()
    {
        return EnumsWithHumanReadableName.namesArray(EntityType.values());
    }

}
