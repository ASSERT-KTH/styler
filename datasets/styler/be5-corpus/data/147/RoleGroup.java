package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElementSupport;
import com.developmentontheedge.beans.annot.PropertyName;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RoleGroup extends BeModelElementSupport
{
    private final RoleSet roles = new RoleSet(this);

    public RoleGroup(String name, BeModelCollection<RoleGroup> origin)
    {
        super(name, origin);
    }

    private SecurityCollection getSecurityCollection()
    {
        return (SecurityCollection) getOrigin().getOrigin();
    }

    public boolean isPredefined()
    {
        return false;
    }

    @PropertyName("Roles in group")
    public RoleSet getRoleSet()
    {
        return roles;
    }

    /**
     * @return comma-separated list of final roles in current group
     * Can be used from query templates to use the group in &lt;roles&gt; clause
     */
    public String getList()
    {
        return String.join(",", getRoleSet().getFinalRoles());
    }

    /**
     * Stub method necessary for bean info
     *
     * @param roles roles to set (ignored)
     */
    public void setRoleSet(RoleSet roles)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ProjectElementException> getErrors()
    {
        Set<String> missingRoles = roles.getMissingEntries();
        if (!(missingRoles.isEmpty()))
        {
            return Collections
                    .singletonList(new ProjectElementException(this, "Group contains unknown roles/subgroups: " + missingRoles));
        }
        return Collections.emptyList();
    }

    @Override
    protected void fireChanged()
    {
        if (getOrigin().get(getName()) == this)
            getSecurityCollection().fireCodeChanged();
    }
}
