package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;

public class StaticPages extends BeVectorCollection<LanguageStaticPages>
{

    public StaticPages(final Module module)
    {
        super(Module.STATIC_PAGES, LanguageStaticPages.class, module);
    }

    @Override
    public void fireCodeChanged()
    {
        if (getModule().get(getName()) == this)
            getProject().getAutomaticSerializationService().fireCodeChanged(this);
    }

}
