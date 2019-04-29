package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.DatabaseConstants;
import com.developmentontheedge.be5.metadata.QueryType;
import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.model.base.DataElementPath;
import com.developmentontheedge.be5.metadata.model.base.TemplateElement;
import com.developmentontheedge.be5.metadata.util.Strings2;
import com.developmentontheedge.beans.annot.PropertyDescription;
import com.developmentontheedge.beans.annot.PropertyName;
import com.developmentontheedge.beans.model.ComponentFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@PropertyName("Query")
public class Query extends EntityItem implements TemplateElement
{
    public static final String SPECIAL_LOST_RECORDS = "Lost records";
    public static final String SPECIAL_TABLE_DEFINITION = "Table definition";

    private static final Set<String> CUSTOMIZABLE_PROPERTIES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("menuName",
            "titleName", "type", "notSupported", "newDataCheckQuery", "invisible", "secure", "slow", "cacheable", "replicated", "defaultView",
            "contextID", "categoryID", "templateQueryName", "shortDescription", "messageWhenEmpty", "parametrizingOperationName",
            "wellKnownName", "operationNames", "query", "roles", "icon", "querySettings", "layout")));

    public static String[] getQueryTypes()
    {
        return Arrays.stream(QueryType.values()).map(QueryType::getName).toArray(String[]::new);
    }

    private String menuName = "";
    private String titleName;
    private QueryType type;
    private String query = " ";
    private String newDataCheckQuery;
    private boolean invisible = false;
    private boolean slow = false;
    private boolean cacheable = false;
    private boolean defaultView = false;
    private boolean replicated = false;
    private String templateQueryName = "";
    private String shortDescription;
    private String messageWhenEmpty;
    private OperationSet operations = new OperationSet(this);
    private Set<QuerySettings> querySettings = null;
    private String parametrizingOperationName = "";
    private String fileName = "";

    public Query(String name, Entity entity)
    {
        super(name, entity.getQueries());
    }

    @Override
    public void fireCodeChanged()
    {
        getProject().getAutomaticSerializationService().fireCodeChanged(this);
        fireChanged();
    }

    @Override
    protected void fireChanged()
    {
        final BeModelCollection<Query> queries = getEntity().getQueries();

        if (queries != null && queries.contains(getName()))
            super.fireChanged();
    }

    // ////////////////////////////////////////////////////////////////////////
    // Properties
    //

    @PropertyName("File name")
    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    @PropertyName("Menu name")
    public String getMenuName()
    {
        return getValue("menuName", menuName, "", () -> ((Query) prototype).getMenuName());
    }

    public void setMenuName(String menuName)
    {
        this.menuName = customizeProperty("menuName", this.menuName, Strings2.nullToEmpty(menuName));
        fireChanged();
    }

    @PropertyName("Title")
    public String getTitleName()
    {
        return getValue("titleName", titleName, () -> ((Query) prototype).getTitleName());
    }

    public void setTitleName(String titleName)
    {
        this.titleName = customizeProperty("titleName", this.titleName, titleName);
        fireChanged();
    }

    @PropertyName("Query type")
    @PropertyDescription("Query type")
    public QueryType getType()
    {
        return getValue("type", type, QueryType.D1, () -> ((Query) prototype).getType());
    }

    public String getTypeForBeCore()
    {
        if (QueryType.CONTAINER == type)
            return QueryType.D1_UNKNOWN.getName();

        return getType().getName();
    }

    public void setType(QueryType type)
    {
        this.type = customizeProperty("type", this.type, type);
        fireChanged();
    }

    public boolean isFileNameHidden()
    {
        return getType() != QueryType.JAVASCRIPT;
    }

    public boolean isQueryHidden()
    {
        return getType() != QueryType.STATIC;
    }

    public boolean isQueryClassHidden()
    {
        return getType() != QueryType.JAVA;
    }

    @PropertyName("Query code (freemarker template)")
    public String getQuery()
    {
        return getValue("query", query, " ", () -> ((Query) prototype).getQuery());
    }

    public void setQuery(String code)
    {
        this.query = customizeProperty("query", this.query, code == null || code.isEmpty() ? " " : code);
        updateLastModification();
        fireCodeChanged();
    }

    @PropertyName("Resulting query")
    public ParseResult getQueryCompiled()
    {
        return getProject().mergeTemplate(this);
    }

    @PropertyName("Query to check new data")
    public String getNewDataCheckQuery()
    {
        return getValue("newDataCheckQuery", newDataCheckQuery,
                () -> ((Query) prototype).getNewDataCheckQuery());
    }

    public void setNewDataCheckQuery(String newDataCheckQuery)
    {
        this.newDataCheckQuery = customizeProperty("newDataCheckQuery", this.newDataCheckQuery, newDataCheckQuery);
        fireChanged();
    }

    @PropertyName("Invisible view")
    public boolean isInvisible()
    {
        return getValue("invisible", invisible, DatabaseConstants.SELECTION_VIEW.equals(getName()),
                () -> ((Query) prototype).isInvisible());
    }

    public void setInvisible(boolean invisible)
    {
        this.invisible = customizeProperty("invisible", this.invisible, invisible);
        fireChanged();
    }

    @PropertyName("Slow view")
    public boolean isSlow()
    {
        return getValue("slow", slow, false, () -> ((Query) prototype).isSlow());
    }

    public void setSlow(boolean slow)
    {
        this.slow = customizeProperty("slow", this.slow, slow);
        fireChanged();
    }

    @PropertyName("Cacheable view")
    public boolean isCacheable()
    {
        return getValue("cacheable", cacheable, false, () -> ((Query) prototype).isCacheable());
    }

    public void setCacheable(boolean cacheable)
    {
        this.cacheable = customizeProperty("cacheable", this.cacheable, cacheable);
        fireChanged();
    }

    @PropertyName("Default view")
    public boolean isDefaultView()
    {
        return getValue("defaultView", defaultView, false, () -> ((Query) prototype).isDefaultView());
    }

    public void setDefaultView(boolean defaultView)
    {
        this.defaultView = customizeProperty("defaultView", this.defaultView, defaultView);
        fireChanged();
    }

    @PropertyName("Replicated view")
    public boolean isReplicated()
    {
        return getValue("replicated", replicated, false, () -> ((Query) prototype).isReplicated());
    }

    public void setReplicated(boolean replicated)
    {
        this.replicated = customizeProperty("replicated", this.replicated, replicated);
        fireChanged();
    }

    @PropertyName("Template query")
    public String getTemplateQueryName()
    {
        return getValue("templateQueryName", templateQueryName, "", () -> ((Query) prototype).getTemplateQueryName());
    }

    public void setTemplateQueryName(String templateQueryName)
    {
        this.templateQueryName = customizeProperty("templateQueryName", this.templateQueryName, Strings2.nullToEmpty(templateQueryName));
        fireChanged();
    }

    @PropertyName("Short description")
    public String getShortDescription()
    {
        return getValue("shortDescription", shortDescription,
                () -> ((Query) prototype).getShortDescription());
    }

    public void setShortDescription(String shortDescription)
    {
        this.shortDescription = customizeProperty("shortDescription", this.shortDescription, shortDescription);
        fireChanged();
    }

    @PropertyName("Message when empty")
    public String getMessageWhenEmpty()
    {
        return getValue("messageWhenEmpty", messageWhenEmpty,
                () -> ((Query) prototype).getMessageWhenEmpty());
    }

    public void setMessageWhenEmpty(String messageWhenEmpty)
    {
        this.messageWhenEmpty = customizeProperty("messageWhenEmpty", this.messageWhenEmpty, messageWhenEmpty);
        fireChanged();
    }

    public void addOperation(Operation op)
    {
        operations.add(op.getName());
    }

    @PropertyName("Operations for query")
    @PropertyDescription("Operations associated with query")
    public OperationSet getOperationNames()
    {
        if (prototype == null || (customizedProperties != null && customizedProperties.contains("operationNames")))
            return operations;
        operations.clear();
        operations.setPrototype(true, ((Query) prototype).getOperationNames());
        return operations;
    }

    /**
     * Stub method necessary for bean info
     *
     * @param names names to set (ignored)
     */
    public void setOperationNames(OperationSet names)
    {
        throw new UnsupportedOperationException();
    }

    public void removeOperation(String operationName)
    {
        this.operations.remove(operationName);
    }

    public QuickFilter[] getQuickFilters()
    {
        BeModelCollection<QuickFilter> filterCollection = getCollection(QuickFilter.QUICK_FILTERS_COLLECTION, QuickFilter.class);
        if (filterCollection == null)
        {
            return new QuickFilter[0];
        }
        return filterCollection.stream().toArray(QuickFilter[]::new);
    }

    @PropertyName("Query settings")
    public QuerySettings[] getQuerySettings()
    {
        return getValue("querySettings",
                querySettings == null ? new QuerySettings[0] : querySettings.toArray(new QuerySettings[querySettings.size()]),
                new QuerySettings[0],
                () -> ((Query) prototype).getQuerySettings());
    }

    protected Map<String, QuerySettings> getQuerySettingsPerRole()
    {
        Map<String, QuerySettings> result = new HashMap<>();
        for (QuerySettings settings : getQuerySettings())
        {
            for (String role : settings.getRoles().getFinalRoles())
            {
                QuerySettings newSettings = new QuerySettings(this, settings);
                newSettings.getRoles().clear();
                result.put(role, newSettings);
            }
        }
        return result;
    }

    public void setQuerySettings(QuerySettings[] settings)
    {
        internalCustomizeProperty("querySettings");
        if (settings == null || settings.length == 0)
        {
            querySettings = null;
            return;
        }
        querySettings = new LinkedHashSet<>();
        OUTER:
        for (QuerySettings setting : settings)
        {
            for (QuerySettings existingSetting : querySettings)
            {
                if (existingSetting.merge(setting))
                    continue OUTER;
            }
            querySettings.add(setting);
        }
        for (QuerySettings querySetting : querySettings)
        {
            querySetting.getRoles().foldSystemGroup();
        }
        ComponentFactory.recreateChildProperties(ComponentFactory.getModel(this, ComponentFactory.Policy.DEFAULT));
        fireChanged();
    }

    public Operation getParametrizingOperation()
    {
        String parametrizingOperation = getParametrizingOperationName();
        return parametrizingOperation.isEmpty() ? null : getEntity().getOperations().get(parametrizingOperation);
    }

    public void setParametrizingOperation(Operation parametrizingOperation)
    {
        setParametrizingOperationName(parametrizingOperation == null ? null : parametrizingOperation.getName());
    }

    @PropertyName("Parametrizing operation")
    public String getParametrizingOperationName()
    {
        return getValue("parametrizingOperationName", parametrizingOperationName, "",
                () -> ((Query) prototype).getParametrizingOperationName());
    }

    public void setParametrizingOperationName(String name)
    {
        this.parametrizingOperationName = customizeProperty("parametrizingOperationName", this.parametrizingOperationName, Strings2.nullToEmpty(name));
        fireChanged();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        Query other = (Query) obj;
        if (isCacheable() != other.isCacheable())
            return debugEquals("cacheable");
        if (isInvisible() != other.isInvisible())
            return debugEquals("invisible");
        if (!getMenuName().equals(other.getMenuName()))
            return debugEquals("menuName");
        if (!getTemplateQueryName().equals(other.getTemplateQueryName()))
            return debugEquals("templateQueryName");
        if (getMessageWhenEmpty() == null)
        {
            if (other.getMessageWhenEmpty() != null)
                return debugEquals("messageWhenEmpty");
        }
        else if (!getMessageWhenEmpty().equals(other.getMessageWhenEmpty()))
            return debugEquals("messageWhenEmpty");
        if (getNewDataCheckQuery() == null)
        {
            if (other.getNewDataCheckQuery() != null)
                return debugEquals("newDataCheckQuery");
        }
        else if (!getNewDataCheckQuery().equals(other.getNewDataCheckQuery()))
            return debugEquals("newDataCheckQuery");
        if (!getParametrizingOperationName().equals(other.getParametrizingOperationName()))
            return debugEquals("parametrizingOperation");
        if (isReplicated() != other.isReplicated())
            return debugEquals("isReplicated");
        if (isDefaultView() != other.isDefaultView())
            return debugEquals("isDefaultView");
        if (getShortDescription() == null)
        {
            if (other.getShortDescription() != null)
                return debugEquals("shortDescription");
        }
        else if (!getShortDescription().equals(other.getShortDescription()))
            return debugEquals("shortDescription");
        if (isSlow() != other.isSlow())
            return debugEquals("slow");
        if (getTitleName() == null)
        {
            if (other.getTitleName() != null)
                return debugEquals("titleName");
        }
        else if (!getTitleName().equals(other.getTitleName()))
            return debugEquals("titleName");
        if (getType() == null)
        {
            if (other.getType() != null)
                return debugEquals("type");
        }
        else if (!getType().equals(other.getType()))
            return debugEquals("type");
        if (!getOperationNames().equals(other.getOperationNames()))
            return debugEquals("operationNames");
        if (!DataElementUtils.equals(getCollection(QuickFilter.QUICK_FILTERS_COLLECTION, QuickFilter.class),
                other.getCollection(QuickFilter.QUICK_FILTERS_COLLECTION, QuickFilter.class)))
            return debugEquals("quickFilters");
        if (!getQuerySettingsPerRole().equals(other.getQuerySettingsPerRole()))
            return debugEquals("querySettings");
        if (!getQueryCompiled().equals(other.getQueryCompiled()))
            return debugEquals("queryCompiled");
        return true;
    }

    @Override
    public List<ProjectElementException> getErrors()
    {
        List<ProjectElementException> result = super.getErrors();
        try
        {
            ModelValidationUtils.checkValueInSet(this, "type", getType().getName(), getQueryTypes());
        }
        catch (ProjectElementException e)
        {
            result.add(e);
        }
        String templateQueryName = getTemplateQueryName();
        if (!templateQueryName.isEmpty())
        {
            int pos = templateQueryName.indexOf('.');
            if (pos < 0)
            {
                result.add(new ProjectElementException(getCompletePath(), "templateQueryName", new IllegalArgumentException("templateQueryName must have format <entity>:<query>")));
            }
        }
        Set<String> missingEntries = getRoles().getMissingEntries();
        if (!missingEntries.isEmpty())
        {
            result.add(new ProjectElementException(getCompletePath(), "roles", "Unknown role(s): " + missingEntries));
        }
        ProjectElementException error = getQueryCompiled().getError();
        if (error != null && !error.isNoError())
        {
            DataElementPath path = getCompletePath();
            if (error.getPath().equals(path.toString()))
                result.add(error);
            else
                result.add(new ProjectElementException(path, "query", error));
        }
        return result;
    }

    @Override
    public EntityItem clone(BeModelCollection<?> origin, String name, boolean inherit)
    {
        Query clone = (Query) super.clone(origin, name, inherit);
        if (clone.prototype == null)
        {
            clone.operations = new OperationSet(clone, operations);
        }
        else
        {
            clone.operations = new OperationSet(clone);
            clone.operations.setPrototype(true, ((Query) clone.prototype).operations);
        }
        if (querySettings != null)
        {
            clone.querySettings = new LinkedHashSet<>();
            for (QuerySettings setting : querySettings)
                clone.querySettings.add(setting.clone(clone, setting.getName()));
        }
        return clone;
    }

    @Override
    public String getEntityItemType()
    {
        return "query";
    }

    @Override
    public String getTemplateCode()
    {
        return getQuery();
    }

    @Override
    public Collection<String> getCustomizableProperties()
    {
        return CUSTOMIZABLE_PROPERTIES;
    }

    @Override
    public Collection<BeModelElement> getDependentElements()
    {
        return findReferencingQuickFilters();
    }

    private Collection<BeModelElement> findReferencingQuickFilters()
    {
        final BeModelCollection<Query> qs = getEntity().getQueries();
        final List<BeModelElement> quickFiltersWithLinks = new ArrayList<>();

        for (final Query q : qs)
        {
            final QuickFilter[] quickFilters = q.getQuickFilters();
            for (final QuickFilter quickFilter : quickFilters)
                if (quickFilter.getTargetQuery() == this)
                    quickFiltersWithLinks.add(quickFilter);
        }

        return quickFiltersWithLinks;
    }

    @Override
    public void merge(BeModelCollection<BeModelElement> other, boolean filterItems, boolean inherit)
    {
        super.merge(other, filterItems, inherit);
        if (inherit)
        {
            this.operations.setPrototype(false, ((Query) other).operations);
        }
        else
        {
            this.operations.addInclusionAll(((Query) other).operations.getAllIncludedValues());
            this.operations.addExclusionAll(((Query) other).operations.getAllExcludedValues());
        }
    }

    public boolean isSqlQuery()
    {
        QueryType type = getType();
        return type == QueryType.D1 || type == QueryType.D1_UNKNOWN || type == QueryType.D2;
    }

}
