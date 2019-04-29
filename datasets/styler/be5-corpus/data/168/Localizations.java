package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;

import java.util.Collection;

public class Localizations extends BeVectorCollection<LanguageLocalizations>
{
    public Localizations(String name, BeModelCollection<?> parent)
    {
        super(name, LanguageLocalizations.class, parent);
        propagateCodeChange();
    }

    public void addLocalization(String langCode, String entity, Collection<String> topics, String key, String value)
    {
        LanguageLocalizations localizations = this.get(langCode);
        if (localizations == null)
        {
            localizations = new LanguageLocalizations(langCode, this);
            DataElementUtils.saveQuiet(localizations);
        }
        localizations.addLocalization(entity, topics, key, value);
    }
}
