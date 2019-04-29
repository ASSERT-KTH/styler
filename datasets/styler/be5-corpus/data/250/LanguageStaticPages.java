package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;

public class LanguageStaticPages extends BeVectorCollection<StaticPage>
{

    public LanguageStaticPages(String name, BeModelCollection<?> parent)
    {
        super(name, StaticPage.class, parent);
        propagateCodeChange();
    }

}
