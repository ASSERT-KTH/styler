package com.developmentontheedge.be5.metadata.serialization.yaml.deserializers;

import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.model.BeConnectionProfile;
import com.developmentontheedge.be5.metadata.model.BeConnectionProfileType;
import com.developmentontheedge.be5.metadata.model.BeConnectionProfiles;
import com.developmentontheedge.be5.metadata.model.BeConnectionProfilesRoot;
import com.developmentontheedge.be5.metadata.model.DataElementUtils;
import com.developmentontheedge.be5.metadata.model.ManagedFileType;
import com.developmentontheedge.be5.metadata.serialization.Fields;
import com.developmentontheedge.be5.metadata.serialization.LoadContext;
import com.developmentontheedge.be5.metadata.util.Strings2;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_CONNECTION_PROFILES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_CONNECTION_PROFILES_INNER;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_REQUESTED_PROPERTIES;


public class ConnectionProfilesDeserializer extends FileDeserializer
{
    private final BeConnectionProfiles target;

    /**
     * Creates a deserializer that is not bound with any file. Used to deserialize connection profiles from memory.
     */
    ConnectionProfilesDeserializer(LoadContext loadContext, final BeConnectionProfiles target)
    {
        super(loadContext);
        this.target = target;
    }

    /**
     * A normal way to create the deserializer.
     */
    ConnectionProfilesDeserializer(LoadContext loadContext, final Path path, final BeConnectionProfileType type, final BeConnectionProfilesRoot target) throws ReadException
    {
        super(loadContext, path);
        this.target = new BeConnectionProfiles(type, target);
    }

    @Override
    protected void doDeserialize(Object serializedRoot) throws ReadException
    {
        final Map<String, Object> serializedConnectionProfilesBody = asMap(asMap(serializedRoot).get(TAG_CONNECTION_PROFILES));
        final Map<String, Object> profilesMap = asMap(serializedConnectionProfilesBody.get(TAG_CONNECTION_PROFILES_INNER));

        for (Map.Entry<String, Object> serializedProfile : profilesMap.entrySet())
        {
            try
            {
                final String profileName = serializedProfile.getKey();
                final Map<String, Object> serializedProfileBody = asMap(serializedProfile.getValue());
                final BeConnectionProfile connectionProfile = deserializeConnectionProfile(profileName, serializedProfileBody);

                save(connectionProfile);
            }
            catch (ReadException e)
            {
                loadContext.addWarning(e.attachElement(target));
            }
        }

        // default connection profile can be deserialized after
        // deserialization of connection profiles
        Object defProfileObj = serializedConnectionProfilesBody.get("defaultProfileName");
        if (defProfileObj instanceof String)
        {
            target.getProject().setConnectionProfileName((String) defProfileObj);
        }
        DataElementUtils.saveQuiet(target);

        target.getProject().getAutomaticDeserializationService().registerFile(path, ManagedFileType.CONNECTION_PROFILES);
    }

    public BeConnectionProfile deserializeConnectionProfile(final String profileName, final Map<String, Object> serializedProfileBody) throws ReadException
    {
        final BeConnectionProfile connectionProfile = new BeConnectionProfile(profileName, target);
        readFields(connectionProfile, serializedProfileBody, Fields.connectionProfile());
        readFields(connectionProfile, serializedProfileBody, Fields.connectionProfileRead());
        readProperties(serializedProfileBody, connectionProfile);

        List<String> propertiesToRequest = readList(serializedProfileBody, TAG_REQUESTED_PROPERTIES);

        if (propertiesToRequest != null)
        {
            connectionProfile.setPropertiesToRequest(propertiesToRequest.toArray(new String[propertiesToRequest.size()]));
        }

        if (Strings2.isNullOrEmpty(connectionProfile.getProviderId()))
            connectionProfile.setProviderId(connectionProfile.getDefaultProviderId());

        if (Strings2.isNullOrEmpty(connectionProfile.getDriverDefinition()))
            connectionProfile.setDriverDefinition(connectionProfile.getDefaultDriverDefinition());

        return connectionProfile;
    }

    public BeConnectionProfiles getResult()
    {
        return target;
    }
}
