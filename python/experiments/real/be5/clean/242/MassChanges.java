package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;

public class MassChanges extends BeVectorCollection<MassChange>
{

    public MassChanges(final Module module)
    {
        super(Module.MASS_CHANGES, MassChange.class, module, true);
    }

    @Override
    protected void fireElementRemoved(Object source, String dataElementName, BeModelElement oldElement)
    {
        fireCodeChanged();
    }

    @Override
    public void fireCodeChanged()
    {
        getProject().getAutomaticSerializationService().fireCodeChanged(this);
    }

}
