package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.beans.annot.PropertyName;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class RoleSet extends InheritableStringSet
{
    private final BeModelElement owner;

    public RoleSet(BeModelElement owner)
    {
        super(owner);
        this.owner = owner;
    }

    public RoleSet(BeModelElement owner, RoleSet source)
    {
        super(owner, source);
        this.owner = owner;
    }

    /**
     * Splits a collection of role and role groups to
     * included and excluded parts to change the state
     * of the set.
     *
     * @param roles
     */
    public void parseRoles(Collection<String> roles)
    {
        super.parseValues(roles);
    }

    /**
     * @return roles and role groups
     * @see RoleSet#setRoles(Collection)
     * @see RoleSetBeanInfo
     */
    @PropertyName("Roles")
    public String[] getRolesArray()
    {
        return getValuesArray();
    }

    /**
     * @see RoleSetBeanInfo
     */
    public void setRolesArray(String[] roles)
    {
        setValuesArray(roles);
    }

    @PropertyName("Excluded roles")
    public String[] getExcludedRolesArray()
    {
        return getExcludedValuesArray();
    }

    public void setExcludedRolesArray(String[] excludedRoles)
    {
        setExcludedValuesArray(excludedRoles);
    }

    @PropertyName("List of prototype roles")
    public String getPrototypeRoles()
    {
        return getPrototypeValues();
    }

    @PropertyName("List of computed roles")
    public String getFinalRolesString()
    {
        return getFinalValuesString();
    }

    public Set<String> getFinalRoles()
    {
        return getFinalValues();
    }

    /**
     * Substitutes all role groups as sets of groups and combines all these roles together.
     *
     * @return set of all roles, that was explicitly or implicitly (using groups) selected
     */
    @Override
    public Set<String> getFinalIncludedValues()
    {
        return getFinalRoles(Collections.<String>emptySet());
    }

    @Override
    public Set<String> getFinalExcludedValues()
    {
        return getFinalExcludedRoles(Collections.<String>emptySet());
    }

    /**
     * @param roleOrRoleGroup role or role group (starts with '@')
     * @see {@link RoleGroup#ALL_ROLES_GROUP}
     * @see {@link RoleGroup#ALL_ROLES_EXCEPT_GUEST_GROUP}
     */
    @Override
    public boolean add(String roleOrRoleGroup)
    {
        return super.add(roleOrRoleGroup);
    }

    public void foldSystemGroup()
    {
        Set<String> projectRoles = getProject().getRoles();
        if (projectRoles.size() == includedValues.size() || projectRoles.size() == includedValues.size() + 1)
        {
            boolean allRoles = true;
            boolean allRolesExceptGuest = true;
            for (String projectRole : projectRoles)
            {
                if (!this.includedValues.contains(projectRole))
                {
                    allRoles = false;
                    if (!projectRole.equals("Guest"))
                    {
                        allRolesExceptGuest = false;
                        break;
                    }
                }
            }
            if (allRoles)
            {
                this.includedValues = Collections.singleton('@' + SpecialRoleGroup.ALL_ROLES_GROUP);
            }
            else if (allRolesExceptGuest)
            {
                this.includedValues = Collections.singleton('@' + SpecialRoleGroup.ALL_ROLES_EXCEPT_GUEST_GROUP);
            }
        }
    }

    public Set<String> getMissingEntries()
    {
        Set<String> result = new HashSet<>();
        Set<String> projectRoles = project.getRoles();
        for (String role : includedValues)
        {
            if (role.startsWith("@") || projectRoles.contains(role))
                continue;
            result.add(role);
        }
        for (String role : excludedValues)
        {
            if (role.startsWith("@") || projectRoles.contains(role))
                continue;
            result.add(role);
        }
        return result;
    }

    private Set<String> getFinalRoles(Set<String> dependentGroups)
    {
        return collectRoles(includedValues, dependentGroups);
    }

    private Set<String> getFinalExcludedRoles(Set<String> dependentGroups)
    {
        return collectRoles(excludedValues, dependentGroups);
    }

    private Set<String> collectRoles(final Set<String> initialRoleSet, Set<String> dependentGroups)
    {
        Set<String> projectRoles = project.getAvailableRoles();
        Set<String> result = new TreeSet<>();
        for (String role : initialRoleSet)
        {
            if (role.startsWith("@"))
            {
                String groupName = role.substring(1);
                if (dependentGroups.contains(groupName))
                {
                    continue;
                }
                RoleGroup group = project.getRoleGroups().get(groupName);
                if (group != null)
                {
                    Set<String> newDependentGroups;
                    if (dependentGroups.isEmpty())
                    {
                        newDependentGroups = Collections.singleton(groupName);
                    }
                    else
                    {
                        newDependentGroups = new HashSet<>(dependentGroups);
                        newDependentGroups.add(groupName);
                    }
                    result.addAll(group.getRoleSet().getFinalRoles(newDependentGroups));
                }
            }
            else
            {
                if (projectRoles.contains(role))
                {
                    result.add(role);
                }
            }
        }
        return result;
    }

    @Override
    protected void customizeAndFireChanged()
    {
        if (this.owner instanceof BeModelCollection)
            ((BeModelCollection<?>) this.owner).customizeProperty("roles");

        if (this.owner instanceof EntityItem)
            ((EntityItem) this.owner).fireChanged();
        else if (this.owner instanceof QuerySettings)
            ((QuerySettings) this.owner).fireChanged();
        else if (this.owner instanceof RoleGroup && !(this.owner instanceof SpecialRoleGroup))
        {
            final RoleGroup roleGroup = (RoleGroup) this.owner;
            if (roleGroup.getRoleSet() == this)
                roleGroup.fireChanged();
        }
    }
}
