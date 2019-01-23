package com.developmentontheedge.be5.metadata.serialization.yaml.deserializers;

import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.model.ManagedFileType;
import com.developmentontheedge.be5.metadata.model.MassChange;
import com.developmentontheedge.be5.metadata.model.MassChanges;
import com.developmentontheedge.be5.metadata.serialization.LoadContext;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_MASS_CHANGES;

class MassChangesDeserializer extends FileDeserializer
{
    private final MassChanges target;

    MassChangesDeserializer(LoadContext loadContext, final Path path, final MassChanges target) throws ReadException
    {
        super(loadContext, path, true);
        this.target = target;
    }

    @Override
    protected void doDeserialize(Object serializedRoot) throws ReadException
    {
        Object changes = asMap(serializedRoot).get(TAG_MASS_CHANGES);
        if (!(changes instanceof List))
            throw new ReadException(path, "Top-element must be a list");
        @SuppressWarnings("unchecked")
        List<Object> changesList = (List<Object>) changes;
        for (Object massChangeObject : changesList)
        {
            Map<String, Object> massChangeElement = asMap(massChangeObject);
            Object selectObject = massChangeElement.get("select");
            if (selectObject == null)
            {
                throw new ReadException(path, "'select' string must be present in each massChange");
            }
            if (!(selectObject instanceof String))
            {
                throw new ReadException(path, "'select' value must be a string");
            }
            String selectString = (String) selectObject;
            MassChange massChange = new MassChange(selectString, target, massChangeElement);
            readDocumentation(massChangeElement, massChange);
            save(massChange);
        }

        target.getProject().getAutomaticDeserializationService().registerFile(path, ManagedFileType.MASS_CHANGES);
    }

    private MassChanges getResult()
    {
        return target;
    }
}
