package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;

public class Entities extends BeVectorCollection<Entity>
{

    /**
     * Not intended to be used to create any entity collection.
     */
    static final String NAME = "Entities";

    public Entities(Module module)
    {
        super(Entities.NAME, Entity.class, module);
    }

    @Override
    protected void fireElementAdded(Object source, String dataElementName)
    {
        super.fireElementAdded(source, dataElementName);
        getProject().getAutomaticSerializationService().fireCodeAdded(get(dataElementName));
    }

    @Override
    protected void fireElementRemoved(Object source, String dataElementName, BeModelElement oldElement)
    {
        super.fireElementRemoved(source, dataElementName, oldElement);
        getProject().getAutomaticSerializationService().fireCodeRemoved((Entity) oldElement);
    }

}
