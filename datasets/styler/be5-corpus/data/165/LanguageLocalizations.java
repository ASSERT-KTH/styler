package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;

import java.util.Collection;

public class LanguageLocalizations extends BeVectorCollection<EntityLocalizations>
{
    public LanguageLocalizations(String name, BeModelCollection<?> parent)
    {
        super(name, EntityLocalizations.class, parent);
    }

    /**
     * @param entity
     * @param topics
     * @param key
     * @param value
     * @return true if there was no such localization before
     */
    public boolean addLocalization(String entity, Collection<String> topics, String key, String value)
    {
        EntityLocalizations localizations = this.get(entity);
        if (localizations == null)
        {
            localizations = new EntityLocalizations(entity, this);
            DataElementUtils.saveQuiet(localizations);
        }
        return localizations.add(topics, key, value);
    }

    @Override
    public void fireCodeChanged()
    {
        getProject().getAutomaticSerializationService().fireCodeChanged(this);
    }

}
