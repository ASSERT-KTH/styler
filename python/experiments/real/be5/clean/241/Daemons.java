package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;

public class Daemons extends BeVectorCollection<Daemon>
{

    public Daemons(final Module module)
    {
        super(Module.DAEMONS, Daemon.class, module);
    }

    @Override
    public void fireCodeChanged()
    {
        if (getModule().get(getName()) == this)
            getProject().getAutomaticSerializationService().fireCodeChanged(this);
    }

}
