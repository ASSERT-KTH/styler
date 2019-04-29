package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;

public class BeConnectionProfilesRoot extends BeVectorCollection<BeConnectionProfiles>
{

    public static final String NAME = "Connection profiles";

    public BeConnectionProfilesRoot(final BeModelCollection<?> parent)
    {
        super(NAME, BeConnectionProfiles.class, parent);
        DataElementUtils.saveQuiet(new BeConnectionProfiles(BeConnectionProfileType.LOCAL, this));
        DataElementUtils.saveQuiet(new BeConnectionProfiles(BeConnectionProfileType.REMOTE, this));
    }

    public void setProfiles(final BeConnectionProfiles profiles)
    {
        DataElementUtils.saveQuiet(profiles);
    }

    public BeConnectionProfiles getLocalProfiles()
    {
        return get(BeConnectionProfileType.LOCAL.getName());
    }

    public BeConnectionProfiles getRemoteProfiles()
    {
        return get(BeConnectionProfileType.REMOTE.getName());
    }

}
