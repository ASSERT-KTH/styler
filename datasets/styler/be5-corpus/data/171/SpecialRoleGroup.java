package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;

import java.util.Collections;
import java.util.List;

public class SpecialRoleGroup extends RoleGroup
{
    public static final String ALL_ROLES_GROUP = "AllRoles";
    public static final String ALL_ROLES_EXCEPT_GUEST_GROUP = "AllRolesExceptGuest";

    public SpecialRoleGroup(String name, BeModelCollection<RoleGroup> origin)
    {
        super(name, origin);
    }

    @Override
    public boolean isPredefined()
    {
        return true;
    }

    @Override
    public RoleSet getRoleSet()
    {
        RoleSet roleSet = new RoleSet(this);
        roleSet.addInclusionAll(getProject().getSecurityCollection().getRoleCollection().getNameList());
        if (getName().equals(ALL_ROLES_EXCEPT_GUEST_GROUP))
            roleSet.remove("Guest");
        return roleSet;
    }

    @Override
    public List<ProjectElementException> getErrors()
    {
        return Collections.emptyList();
    }
}
