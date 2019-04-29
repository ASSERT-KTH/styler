package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;

public class PageCustomizations extends BeVectorCollection<PageCustomization>
{

    public PageCustomizations(BeModelCollection<?> parent)
    {
        super(PageCustomization.CUSTOMIZATIONS_COLLECTION, PageCustomization.class, parent);
    }

    @Override
    public void fireCodeChanged()
    {
        if (getOrigin() instanceof Module && getModule().get(getName()) == this)
            getProject().getAutomaticSerializationService().fireCodeChanged(this);
    }

}
