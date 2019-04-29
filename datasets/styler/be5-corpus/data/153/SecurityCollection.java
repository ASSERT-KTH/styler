package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SecurityCollection extends BeVectorCollection<BeModelElement>
{
    public static final String ROLE_GROUPS = "Role groups";
    public static final String ROLES = "Roles";

    public SecurityCollection(String name, BeModelCollection<?> parent)
    {
        super(name, BeModelElement.class, parent, true);
        DataElementUtils.saveQuiet(new BeVectorCollection<>(ROLES, Role.class, this).propagateCodeChange());
        BeVectorCollection<RoleGroup> roleGroups = new BeVectorCollection<>(ROLE_GROUPS, RoleGroup.class, this).propagateCodeChange();
        DataElementUtils.saveQuiet(roleGroups);
        DataElementUtils.saveQuiet(new SpecialRoleGroup(SpecialRoleGroup.ALL_ROLES_GROUP, roleGroups));
        DataElementUtils.saveQuiet(new SpecialRoleGroup(SpecialRoleGroup.ALL_ROLES_EXCEPT_GUEST_GROUP, roleGroups));
    }

    public BeModelCollection<RoleGroup> getRoleGroupCollection()
    {
        return getCollection(ROLE_GROUPS, RoleGroup.class);
    }

    public BeModelCollection<Role> getRoleCollection()
    {
        return getCollection(ROLES, Role.class);
    }

    public Set<String> getRoles()
    {
        return getRoleCollection().names().toCollection(TreeSet::new);
    }

    public List<String> getRolesWithGroups()
    {
        return getRoleCollection().names().append(getRoleGroupCollection().names().map(group -> '@' + group)).toList();
    }

    @Override
    public void fireCodeChanged()
    {
        getProject().getAutomaticSerializationService().fireCodeChanged(this);
    }

}
