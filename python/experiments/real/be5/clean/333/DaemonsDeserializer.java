package com.developmentontheedge.be5.metadata.serialization.yaml.deserializers;

import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.model.Daemon;
import com.developmentontheedge.be5.metadata.model.DataElementUtils;
import com.developmentontheedge.be5.metadata.model.ManagedFileType;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.serialization.Fields;
import com.developmentontheedge.be5.metadata.serialization.LoadContext;

import java.nio.file.Path;
import java.util.Map;

import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_COMMENT;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_DAEMONS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_EXTRAS;

class DaemonsDeserializer extends FileDeserializer
{

    private final BeModelCollection<Daemon> target;

    DaemonsDeserializer(LoadContext loadContext, final Path path, final BeModelCollection<Daemon> target) throws ReadException
    {
        super(loadContext, path, true);
        this.target = target;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doDeserialize(Object serializedRoot) throws ReadException
    {
        final Map<String, Object> daemonsByName = getRootMap(serializedRoot, TAG_DAEMONS);

        for (final String daemonName : daemonsByName.keySet())
        {
            final Object daemonContent = daemonsByName.get(daemonName);
            if (!(daemonContent instanceof Map))
                continue;
            final Daemon daemon = new Daemon(daemonName, target);
            final Map<String, Object> daemonContentMap = (Map<String, Object>) daemonContent;
            readFields(daemon, daemonContentMap, Fields.daemon());
            readUsedExtras(daemonContentMap, daemon);
            readDocumentation(daemonContentMap, daemon);
            checkChildren(daemon, daemonContentMap, Fields.daemon(), TAG_COMMENT, TAG_EXTRAS);
            DataElementUtils.saveQuiet(daemon);
        }

        target.getProject().getAutomaticDeserializationService().registerFile(path, ManagedFileType.DAEMONS);
    }

}
