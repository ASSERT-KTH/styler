package com.developmentontheedge.be5.metadata.serialization.yaml.deserializers;

import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.model.ManagedFileType;
import com.developmentontheedge.be5.metadata.model.Role;
import com.developmentontheedge.be5.metadata.model.RoleGroup;
import com.developmentontheedge.be5.metadata.model.SecurityCollection;
import com.developmentontheedge.be5.metadata.serialization.LoadContext;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_COMMENT;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_EXTRAS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_ROLES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_ROLE_GROUPS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_SECURITY;

class SecurityDeserializer extends FileDeserializer
{

    private final SecurityCollection target;

    SecurityDeserializer(LoadContext loadContext, final Path path, final SecurityCollection target) throws ReadException
    {
        super(loadContext, path);
        this.target = target;
    }

    @Override
    protected void doDeserialize(Object serializedRoot) throws ReadException
    {
        final Map<String, Object> serializedContent = asMap(asMap(serializedRoot).get(TAG_SECURITY));
        final Map<String, Object> serializedRoles = asMap(serializedContent.get(TAG_ROLES));
        final Map<String, Object> serializedGroups = asMap(serializedContent.get(TAG_ROLE_GROUPS));

        readRoles(serializedRoles);
        readGroups(serializedGroups);
        checkChildren(target, serializedContent, TAG_ROLES, TAG_ROLE_GROUPS);
        target.getProject().getAutomaticDeserializationService().registerFile(path, ManagedFileType.SECURITY);
    }

    private void readRoles(final Map<String, Object> serializedRoles)
    {
        for (final Map.Entry<String, Object> serializedRole : serializedRoles.entrySet())
        {
            try
            {
                readRole(serializedRole);
            }
            catch (ReadException e)
            {
                loadContext.addWarning(e.attachElement(target.getRoleCollection()));
            }
        }
    }

    private void readRole(final Map.Entry<String, Object> serializedRole) throws ReadException
    {
        final Map<String, Object> serializedRoleContent = asMap(serializedRole.getValue());
        final String roleName = serializedRole.getKey();
        final Role role = new Role(roleName, target.getRoleCollection());
        readDocumentation(serializedRoleContent, role);
        readUsedExtras(serializedRoleContent, role);
        save(role);
        checkChildren(role, serializedRoleContent, TAG_COMMENT, TAG_EXTRAS);
    }

    private void readGroups(Map<String, Object> serializedGroups)
    {
        for (final Map.Entry<String, Object> serializedGroup : serializedGroups.entrySet())
        {
            try
            {
                readGroup(serializedGroup);
            }
            catch (ReadException e)
            {
                loadContext.addWarning(e.attachElement(target.getRoleGroupCollection()));
            }
        }
    }

    private void readGroup(Map.Entry<String, Object> serializedGroup) throws ReadException
    {
        final String roleGroupName = serializedGroup.getKey();
        final List<String> roles = asStrList(serializedGroup.getValue());
        final RoleGroup roleGroup = new RoleGroup(roleGroupName, target.getRoleGroupCollection());

        roleGroup.getRoleSet().parseRoles(roles);
        save(roleGroup);
    }

    public SecurityCollection getResult()
    {
        return target;
    }

}
