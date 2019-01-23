package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElementSupport;
import com.developmentontheedge.beans.annot.PropertyName;

public class QuickFilter extends BeModelElementSupport
{
    public static final String QUICK_FILTERS_COLLECTION = "Quick filters";
    private String queryParam;
    private String targetQueryName;
    private String filteringClass;
    private String originModule;

    public QuickFilter(String name, Query query)
    {
        super(name, query.getOrCreateCollection(QUICK_FILTERS_COLLECTION, QuickFilter.class));
        setOriginModuleName(getQuery().getOriginModuleName());
    }

    /**
     * Copy constructor
     *
     * @param name
     * @param query
     * @param orig
     */
    public QuickFilter(String name, Query query, QuickFilter orig)
    {
        this(name, query);
        setQueryParam(orig.getQueryParam());
        setTargetQueryName(orig.getTargetQueryName());
        setFilteringClass(orig.getFilteringClass());
    }

    @PropertyName("Filter parameter")
    public String getQueryParam()
    {
        return queryParam;
    }

    public void setQueryParam(String queryParam)
    {
        this.queryParam = queryParam;
        fireChanged();
    }

    @PropertyName("Filtering query")
    public String getTargetQueryName()
    {
        return targetQueryName;
    }

    public void setTargetQueryName(String targetQueryName)
    {
        this.targetQueryName = targetQueryName;
        fireChanged();
    }

    @PropertyName("Filtering class")
    public String getFilteringClass()
    {
        return filteringClass;
    }

    public void setFilteringClass(String filteringClass)
    {
        this.filteringClass = filteringClass;
        fireChanged();
    }

    public Query getQuery()
    {
        return (Query) getOrigin().getOrigin();
    }

    public Query getTargetQuery()
    {
        return getQuery().getEntity().getQueries().get(targetQueryName);
    }

    @PropertyName("Module")
    public String getOriginModuleName()
    {
        return originModule;
    }

    public void setOriginModuleName(String name)
    {
        this.originModule = name;
        fireChanged();
    }

    @Override
    public boolean isCustomized()
    {
        return !getOriginModuleName().equals(getModule().getName()) && getOriginModuleName().equals(getProject().getProjectOrigin());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return debugEquals("null");
        if (getClass() != obj.getClass())
            return debugEquals("class");
        QuickFilter other = (QuickFilter) obj;
        if (!getName().equals(other.getName()))
            return debugEquals("name");
        if (filteringClass == null)
        {
            if (other.filteringClass != null)
                return debugEquals("filteringClass");
        }
        else if (!filteringClass.equals(other.filteringClass))
            return debugEquals("filteringClass");
        if (queryParam == null)
        {
            if (other.queryParam != null)
                return debugEquals("queryParam");
        }
        else if (!queryParam.equals(other.queryParam))
            return debugEquals("queryParam");
        if (targetQueryName == null)
        {
            if (other.targetQueryName != null)
                return debugEquals("targetQueryName");
        }
        else if (!targetQueryName.equals(other.targetQueryName))
            return debugEquals("targetQueryName");
        return true;
    }

    protected void fireChanged()
    {
        final BeModelCollection<QuickFilter> quickFilters = getQuery().getCollection(QUICK_FILTERS_COLLECTION, QuickFilter.class);
        if (quickFilters != null && quickFilters.contains(getName()))
            getQuery().fireCodeChanged();
    }

    public boolean quickFiltersWithTheSameTargetExist()
    {
        if (getTargetQuery() == null)
            return false;

        final Entity entity = getQuery().getEntity();
        final BeModelCollection<Query> queries = entity.getQueries();

        for (final Query query : queries)
            for (final QuickFilter qf : query.getQuickFilters())
                if (qf.getTargetQuery() == getTargetQuery())
                    return true;

        return false;
    }
}
