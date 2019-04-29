package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;

public class BeConnectionProfiles extends BeVectorCollection<BeConnectionProfile>
{
    private final BeConnectionProfileType type;

    public BeConnectionProfiles(BeConnectionProfileType type, BeModelCollection<?> parent)
    {
        super(type.getName(), BeConnectionProfile.class, parent);
        this.type = type;
    }

    public BeConnectionProfileType getType()
    {
        return type;
    }
}
