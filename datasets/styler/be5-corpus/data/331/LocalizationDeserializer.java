package com.developmentontheedge.be5.metadata.serialization.yaml.deserializers;

import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.model.LanguageLocalizations;
import com.developmentontheedge.be5.metadata.model.Localizations;
import com.developmentontheedge.be5.metadata.model.ManagedFileType;
import com.developmentontheedge.be5.metadata.serialization.LoadContext;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_LOCALIZATION_TOPICS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_ENTITIES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_LOCALIZATION_ENTRIES;

class LocalizationDeserializer extends FileDeserializer
{
    private final String lang;
    private final LanguageLocalizations target;

    LocalizationDeserializer(LoadContext loadContext, final String lang, final Path path, final Localizations target) throws ReadException
    {
        super(loadContext, path);
        this.lang = lang;
        this.target = new LanguageLocalizations(lang, target);
    }

    @Override
    protected void doDeserialize(final Object serializedRoot) throws ReadException
    {
        final Map<String, Object> localizationContent = asMap(asMap(serializedRoot).get(lang));
        final List<Object> serializedEntitiesLocalization = asList(localizationContent.get(TAG_ENTITIES));

        for (Object serializedEntityLocalization : serializedEntitiesLocalization)
        {
            readEntityLocalization(asMap(serializedEntityLocalization));
        }

        save(target);
        readDocumentation(localizationContent, target);
        target.getProject().getAutomaticDeserializationService().registerFile(path, ManagedFileType.LOCALIZATION);
    }

    private LanguageLocalizations getResult()
    {
        return target;
    }

    private void readEntityLocalization(Map<String, Object> serializedEntityLocalization) throws ReadException
    {
        if (serializedEntityLocalization.size() != 1)
        {
            loadContext.addWarning(new ReadException(path, "Each entity localization should have only one key that reperesents an entity name"));
            return;
        }

        final String entityName = serializedEntityLocalization.keySet().iterator().next();
        final List<Object> serializedBlocks = asList(serializedEntityLocalization.get(entityName));

        readBlocks(entityName, serializedBlocks);
    }

    private void readBlocks(final String entityName, final List<Object> serializedBlocks)
    {
        for (final Object serializedBlock : serializedBlocks)
        {
            try
            {
                readBlock(entityName, asMap(serializedBlock));
            }
            catch (ReadException e)
            {
                loadContext.addWarning(e.attachElement(target));
            }
        }
    }

    private void readBlock(final String entityName, final Map<String, Object> serializedBlock) throws ReadException
    {
        final List<String> topics = asStrList(serializedBlock.get(ATTR_LOCALIZATION_TOPICS));
        final List<Object> serializedEntries = asList(serializedBlock.get(TAG_LOCALIZATION_ENTRIES));

        for (Object serializedEntry : serializedEntries)
        {
            readEntry(entityName, topics, asMap(serializedEntry));
        }
    }

    private void readEntry(String entityName, List<String> topics, Map<String, Object> serializedEntry) throws ReadException
    {
        if (serializedEntry.size() != 1)
        {
            loadContext.addWarning(new ReadException(path, "Each localization entry should have only one key"));
            return;
        }

        final String name = serializedEntry.keySet().iterator().next();
        final String value = asStr(serializedEntry.get(name));

        if (!target.addLocalization(entityName, topics, name, value))
        {
            loadContext.addWarning(new ReadException(target.get(entityName), path, "Duplicate localization: topics = " + topics + "; name = " + name));
        }
    }

}
