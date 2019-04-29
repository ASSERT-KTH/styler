package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.model.base.BeCaseInsensitiveCollection;
import com.developmentontheedge.be5.metadata.model.base.BeFileBasedElement;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;
import com.developmentontheedge.be5.metadata.util.EnumsWithHumanReadableName;
import com.developmentontheedge.be5.metadata.util.Strings2;
import com.developmentontheedge.beans.annot.PropertyDescription;
import com.developmentontheedge.beans.annot.PropertyName;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@PropertyName("Entity")
public class Entity extends BeVectorCollection<BeModelElement> implements BeFileBasedElement
{

    public static final String OPERATIONS = "Operations";
    public static final String QUERIES = "Queries";
    private static final String REFERENCES = "References";
    static final String SCHEME = "Scheme";
    private static final Set<String> CUSTOMIZABLE_PROPERTIES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("type", "displayName",
            "primaryKey", "order", "icon")));

    private EntityType type = null;
    private String displayName = "";
    private String primaryKey = "";
    private Icon icon = new Icon(this);
    private String order = "";
    private Path linkedFile;
    private boolean besql = false;

    public Entity(final String name, final Module module, final EntityType type)
    {
        super(name, BeVectorCollection.class, module.getOrCreateEntityCollection());
        if (type != null)
            setType(type);
        init();
    }

    private void init()
    {
        icon.setOriginModuleName(getModule().getName());
        put(new BeVectorCollection<>(OPERATIONS, Operation.class, this).propagateCodeChange());
        put(new BeVectorCollection<>(QUERIES, Query.class, this).propagateCodeChange());
    }

    public DdlElement getScheme()
    {
        return (DdlElement) this.get(SCHEME);
    }

    public TableDef findTableDefinition()
    {
        DdlElement scheme = getScheme();
        if (scheme instanceof TableDef)
            return (TableDef) scheme;
        return null;
    }

    public List<TableReference> getAllReferences()
    {
        List<TableReference> result = new ArrayList<>();
        Set<String> names = new HashSet<>();
        TableDef tableDef = findTableDefinition();
        if (tableDef != null)
        {
            for (ColumnDef column : tableDef.getColumns().getAvailableElements())
            {
                if (column.hasReference())
                {
                    result.add(column);
                    names.add(column.getName().toLowerCase());
                }
            }
        }
        BeModelCollection<TableRef> tableRefs = getTableReferences();
        if (tableRefs != null)
        {
            for (TableRef ref : tableRefs.getAvailableElements())
            {
                String name = ref.getColumnsFrom().toLowerCase();
                if (!names.contains(name))
                {
                    result.add(ref);
                    names.add(name);
                }
            }
        }
        return result;
    }

    public BeModelCollection<TableRef> getTableReferences()
    {
        return getCollection(REFERENCES, TableRef.class);
    }

    public BeModelCollection<TableRef> getOrCreateTableReferences()
    {
        @SuppressWarnings("unchecked")
        BeVectorCollection<TableRef> element = (BeVectorCollection<TableRef>) get(REFERENCES);
        if (element == null)
        {
            element = new BeCaseInsensitiveCollection<>(REFERENCES, TableRef.class, this).propagateCodeChange();
            DataElementUtils.saveQuiet(element);
        }
        return element;
    }

    @Override
    protected void fireChanged()
    {
        fireCodeChanged();
    }

    @Override
    public void fireCodeChanged()
    {
        if (getModule().getEntity(getName()) == this)
            getProject().getAutomaticSerializationService().fireCodeChanged(this);
        // don't propagate
    }

    @PropertyName("Type")
    public EntityType getType()
    {
        return getValue("type", type, EntityType.TABLE, () -> ((Entity) prototype).getType());
    }

    public void setType(EntityType type)
    {
        this.type = customizeProperty("type", this.type, type);
        fireCodeChanged();
    }

    @PropertyName("Type")
    public String getTypeString()
    {
        try
        {
            final EntityType typeValue = getType();

            if (typeValue == null)
                return "";

            return typeValue.getHumanReadableName();
        }
        catch (Exception e)
        {
            return "";
        }
    }

    public void setTypeString(String typeString)
    {
        setType(EnumsWithHumanReadableName.byName(EntityType.class, typeString));
    }

    @SuppressWarnings("unchecked")
    public BeModelCollection<Operation> getOperations()
    {
        return (BeModelCollection<Operation>) this.get(OPERATIONS);
    }

    @SuppressWarnings("unchecked")
    public BeModelCollection<Query> getQueries()
    {
        return (BeModelCollection<Query>) this.get(QUERIES);
    }

    private boolean equalTableReferences(List<TableReference> c1, List<TableReference> c2)
    {
        if (c1 == null && c2 == null)
            return true;
        if (c1 == null)
            return c2.size() == 0;
        if (c2 == null)
            return c1.size() == 0;
        if (c1.size() != c2.size())
            return false;
        Map<String, TableReference> oldNames = new HashMap<>();
        for (TableReference oldTableRef : c2)
        {
            oldNames.put(oldTableRef.getColumnsFrom().toLowerCase(), oldTableRef);
        }
        for (TableReference newTableRef : c1)
        {
            TableReference ref = oldNames.remove(newTableRef.getColumnsFrom().toLowerCase());
            if (ref == null || !newTableRef.equalsReference(ref))
                return false;
        }
        return true;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return debugEquals("null");
        if (getClass() != obj.getClass())
            return debugEquals("class");
        final Entity other = (Entity) obj;
        return equals(other, true);
    }

    public boolean equals(final Entity other, boolean compareScheme)
    {
        if (other == null)
            return debugEquals("null");
        if (getType() != other.getType())
            return debugEquals("type");
        if (!getDisplayName().equals(other.getDisplayName()))
            return debugEquals("displayName");
        if (!getOrder().equals(other.getOrder()))
            return debugEquals("order");
        if (!getPrimaryKey().equals(other.getPrimaryKey()))
            return debugEquals("primaryKey");
        if (!getIcon().equals(other.getIcon()))
            return debugEquals("icon");
        if (compareScheme)
        {
            DdlElement scheme = getScheme();
            DdlElement otherScheme = other.getScheme();
            if (scheme == null)
            {
                if (otherScheme != null)
                    return debugEquals("scheme");
            }
            else if (!scheme.equals(otherScheme))
                return debugEquals("scheme");
        }
        if (!equalTableReferences(getAllReferences(), other.getAllReferences()))
            return debugEquals("tableReferences");
        final Map<String, Operation> ops = new HashMap<>();
        final Map<String, Operation> otherOps = new HashMap<>();
        for (final Operation op : getOperations().getAvailableElements())
        {
            ops.put(op.getName(), op);
        }
        for (final Operation op : other.getOperations().getAvailableElements())
        {
            otherOps.put(op.getName(), op);
        }
        if (!ops.equals(otherOps))
        {
            return debugEquals("operations");
        }
        final Map<String, Query> queries = new HashMap<>();
        final Map<String, Query> otherQueries = new HashMap<>();
        for (final Query query : getQueries().getAvailableElements())
        {
            queries.put(query.getName(), query);
        }
        for (final Query query : other.getQueries().getAvailableElements())
        {
            otherQueries.put(query.getName(), query);
        }
        if (!queries.equals(otherQueries))
        {
            return debugEquals("queries");
        }
        if (!DataElementUtils.equals(getCollection(PageCustomization.CUSTOMIZATIONS_COLLECTION, PageCustomization.class),
                other.getCollection(PageCustomization.CUSTOMIZATIONS_COLLECTION, PageCustomization.class)))
            return debugEquals("customizations");
        return true;
    }

    @Override
    public Module getModule()
    {
        return (Module) (getOrigin().getOrigin());
    }

    // ////////////////////////////////////////////////////////////////////////
    // Properties
    //

    @PropertyName("Display name")
    public String getDisplayName()
    {
        return getValue("displayName", displayName, "", () -> ((Entity) prototype).getDisplayName());
    }

    public void setDisplayName(final String displayName)
    {
        this.displayName = customizeProperty("displayName", this.displayName, Strings2.nullToEmpty(displayName));
        fireCodeChanged();
    }

    @PropertyName("Primary key")
    public String getPrimaryKey()
    {
        return getValue("primaryKey", primaryKey, "", () -> ((Entity) prototype).getPrimaryKey());
    }

    public void setPrimaryKey(final String primaryKey)
    {
        this.primaryKey = customizeProperty("primaryKey", this.primaryKey, Strings2.nullToEmpty(primaryKey));
        fireCodeChanged();
    }

    @PropertyName("Icon")
    public Icon getIcon()
    {
        return icon;
    }

    @PropertyName("Order")
    public String getOrder()
    {
        return getValue("order", order, "", () -> ((Entity) prototype).getOrder());
    }

    public void setOrder(final String order)
    {
        this.order = customizeProperty("order", this.order, order);
        fireCodeChanged();
    }

    public String getSqlDisplayName()
    {
        String order = getOrder();
        return (Strings2.isNullOrEmpty(order) ? "" : "<!--" + order + "-->") + Strings2.nullToEmpty(getDisplayName());
    }

    @Override
    public List<ProjectElementException> getErrors()
    {
        final List<ProjectElementException> errors = new ArrayList<>();

        if (getName().length() > Constants.MAX_ID_LENGTH)
        {
            errors.add(new ProjectElementException(getCompletePath(), "name", "Entity name is too long."));
        }

        for (final Module module : getProject().getModulesAndApplication())
        {
            final Module thisModule = getModule();
            if (module == thisModule)
                break;
            final Entity duplicate = module.getEntity(getName());
            if (duplicate != null)
            {
                errors.add(new ProjectElementException(getCompletePath(), "name", "Entity with name '" + getName() + "' already exists."));
                break;
            }
        }

        final TableDef tableDef = findTableDefinition();

        if (tableDef != null)
        {
            errors.addAll(tableDef.getErrors());
        }
        for (final Query query : getQueries())
        {
            errors.addAll(query.getErrors());
        }
        for (final Operation operation : getOperations())
        {
            errors.addAll(operation.getErrors());
        }
        return errors;
    }

    @Override
    public Collection<String> getCustomizableProperties()
    {
        return CUSTOMIZABLE_PROPERTIES;
    }

    @Override
    public Entity clone(BeModelCollection<?> origin, String name, boolean inherit)
    {
        Entity clone = (Entity) super.clone(origin, name, inherit);
        clone.icon = new Icon(clone);
        clone.icon.copyFrom(icon);
        return clone;
    }

    @Override
    public Path getLinkedFile()
    {
        return linkedFile;
    }

    @Override
    public void setLinkedFile(Path path)
    {
        this.linkedFile = path;
    }

    @Override
    protected void mergeThis(BeModelElement other, boolean inherit)
    {
        super.mergeThis(other, inherit);
        if (customizedProperties == null || !customizedProperties.contains("icon"))
            icon.copyFrom(((Entity) other).icon);
    }

    @PropertyName("Use BE-SQL")
    @PropertyDescription("All queries in this entity use BE-SQL syntax")
    public boolean isBesql()
    {
        return besql;
    }

    public void setBesql(boolean besql)
    {
        this.besql = besql;
        fireCodeChanged();
    }
}
