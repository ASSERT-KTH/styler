package com.developmentontheedge.be5.metadata.serialization.yaml.deserializers;

import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.model.ManagedFileType;
import com.developmentontheedge.be5.metadata.model.Module;
import com.developmentontheedge.be5.metadata.model.PageCustomizations;
import com.developmentontheedge.be5.metadata.serialization.LoadContext;

import java.nio.file.Path;

class CustomizationDeserializer extends FileDeserializer
{

    private YamlDeserializer yamlDeserializer;
    private final Module target;
    private boolean replace = false;

    CustomizationDeserializer(YamlDeserializer yamlDeserializer, LoadContext loadContext, final Path path, final Module target) throws ReadException
    {
        super(loadContext, path, true);
        this.yamlDeserializer = yamlDeserializer;
        this.target = target;
    }

    CustomizationDeserializer replace()
    {
        replace = true;
        return this;
    }

    @Override
    protected void doDeserialize(Object serializedRoot) throws ReadException
    {
        yamlDeserializer.readCustomizations(asMap(serializedRoot), target, replace);
        target.getProject().getAutomaticDeserializationService().registerFile(path, ManagedFileType.CUSTOMIZATION);
    }

    private PageCustomizations getResult()
    {
        return target.getPageCustomizations();
    }

}
