package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElementSupport;
import com.developmentontheedge.beans.annot.PropertyName;

@PropertyName("Query settings")
public class QuerySettings extends BeModelElementSupport
{
    private RoleSet roles = new RoleSet(this);
    private int maxRecordsPerPage = Integer.MAX_VALUE;
    private int maxRecordsPerPrintPage = Integer.MAX_VALUE;
    private int maxRecordsInDynamicDropDown = 20;
    private Long colorSchemeID = null;
    private int autoRefresh = 0;
    private String beautifier = "com.developmentontheedge.web.html.HtmlTableBeautifier";

    public QuerySettings(Query query)
    {
        super("", query);
    }

    /**
     * copy constructor
     *
     * @param query
     */
    public QuerySettings(Query query, QuerySettings orig)
    {
        super("", query);
        copyFrom(orig);
    }

    private void copyFrom(QuerySettings orig)
    {
        RoleSet r = orig.getRoles();
        getRoles().setValues(r.getIncludedValues());
        RoleSet r1 = orig.getRoles();
        getRoles().setExcludedValues(r1.getExcludedValues());
        setMaxRecordsPerPage(orig.getMaxRecordsPerPage());
        setMaxRecordsPerPrintPage(orig.getMaxRecordsPerPrintPage());
        setMaxRecordsInDynamicDropDown(orig.getMaxRecordsInDynamicDropDown());
        setColorSchemeID(orig.getColorSchemeID());
        setAutoRefresh(orig.getAutoRefresh());
        setBeautifier(orig.getBeautifier());
    }

    public Query getQuery()
    {
        return (Query) getOrigin();
    }

    @PropertyName("Roles")
    public RoleSet getRoles()
    {
        return roles;
    }

    /**
     * Stub method necessary for bean info
     *
     * @param roles roles to set (ignored)
     */
    public void setRoles(RoleSet roles)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * If possible merge two QuerySettings objects into one (updating roles list)
     *
     * @param settings to add to current object
     * @return true if merge was successful
     */
    public boolean merge(QuerySettings other)
    {
        if (!equalsExceptRoles(other))
            return false;
        this.roles.addInclusionAll(other.roles.getAllIncludedValues());
        return true;
    }

    @PropertyName("Max records per page")
    public int getMaxRecordsPerPage()
    {
        return maxRecordsPerPage;
    }

    public void setMaxRecordsPerPage(int maxRecordsPerPage)
    {
        this.maxRecordsPerPage = maxRecordsPerPage;
        fireChanged();
    }

    @PropertyName("Max records per print page")
    public int getMaxRecordsPerPrintPage()
    {
        return maxRecordsPerPrintPage;
    }

    public void setMaxRecordsPerPrintPage(int maxRecordsPerPrintPage)
    {
        this.maxRecordsPerPrintPage = maxRecordsPerPrintPage;
        fireChanged();
    }

    @PropertyName("Max records in dynamic drop-down")
    public int getMaxRecordsInDynamicDropDown()
    {
        return maxRecordsInDynamicDropDown;
    }

    public void setMaxRecordsInDynamicDropDown(int maxRecordsInDynamicDropDown)
    {
        this.maxRecordsInDynamicDropDown = maxRecordsInDynamicDropDown;
        fireChanged();
    }

    @PropertyName("Color scheme ID")
    public Long getColorSchemeID()
    {
        return colorSchemeID;
    }

    public void setColorSchemeID(Long colorSchemeID)
    {
        this.colorSchemeID = colorSchemeID;
        fireChanged();
    }

    @PropertyName("Auto-refresh (in seconds)")
    public int getAutoRefresh()
    {
        return autoRefresh;
    }

    public void setAutoRefresh(int autoRefresh)
    {
        this.autoRefresh = autoRefresh;
        fireChanged();
    }

    @PropertyName("Beautifier class")
    public String getBeautifier()
    {
        return beautifier;
    }

    public void setBeautifier(String beautifier)
    {
        this.beautifier = beautifier;
        fireChanged();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + autoRefresh;
        result = prime * result + ((beautifier == null) ? 0 : beautifier.hashCode());
        result = prime * result + ((colorSchemeID == null) ? 0 : colorSchemeID.hashCode());
        result = prime * result + maxRecordsInDynamicDropDown;
        result = prime * result + maxRecordsPerPage;
        result = prime * result + maxRecordsPerPrintPage;
        result = prime * result + ((roles == null) ? 0 : roles.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        QuerySettings other = (QuerySettings) obj;
        if (!equalsExceptRoles(other))
            return false;
        if (!roles.equals(other.roles))
            return false;
        return true;
    }

    private boolean equalsExceptRoles(QuerySettings other)
    {
        if (autoRefresh != other.autoRefresh)
            return false;
        if (beautifier == null)
        {
            if (other.beautifier != null)
                return false;
        }
        else if (!beautifier.equals(other.beautifier))
            return false;
        if (colorSchemeID == null)
        {
            if (other.colorSchemeID != null)
                return false;
        }
        else if (!colorSchemeID.equals(other.colorSchemeID))
            return false;
        if (maxRecordsInDynamicDropDown != other.maxRecordsInDynamicDropDown)
            return false;
        if (maxRecordsPerPage != other.maxRecordsPerPage)
            return false;
        if (maxRecordsPerPrintPage != other.maxRecordsPerPrintPage)
            return false;
        return true;
    }

    @Override
    public QuerySettings clone(BeModelCollection<?> origin, String name)
    {
        QuerySettings clone = (QuerySettings) super.clone(origin, name);
        clone.roles = new RoleSet(clone, roles);
        return clone;
    }

    @Override
    protected void fireChanged()
    {
        for (QuerySettings querySettings : getQuery().getQuerySettings())
            if (querySettings == this) // identity!
            {
                getQuery().fireCodeChanged();
                break;
            }
    }
}
