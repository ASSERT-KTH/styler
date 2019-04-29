package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.util.Strings2;
import com.developmentontheedge.beans.annot.PropertyDescription;
import com.developmentontheedge.beans.annot.PropertyName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public class ColumnDef extends TableRef
{
    private SqlColumnType type;
    private boolean canBeNull;
    private boolean autoIncrement;
    private String defaultValue;
    private String[] oldNames = new String[0];

    private static final Set<String> CUSTOMIZABLE_PROPERTIES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("type", "canBeNull", "defaultValue")));

    public ColumnDef(String name, BeModelCollection<? extends ColumnDef> origin)
    {
        super(name, name, origin);
    }

    @Override
    public Entity getEntity()
    {
        return getTable().getEntity();
    }

    public TableDef getTable()
    {
        return (TableDef) getOrigin().getOrigin();
    }

    public SqlColumnType getRawType()
    {
        return getValue("type", type, null, () -> ((ColumnDef) prototype).getRawType());
    }

    @PropertyName("Type")
    public SqlColumnType getType()
    {
        SqlColumnType result = getRawType();
        if (result != null)
            return result;
        return getType(Collections.newSetFromMap(new IdentityHashMap<>()));
    }

    @PropertyName("Type")
    public String getTypeString()
    {
        return getType().toString();
    }

    private static SqlColumnType typeFromString(final String typeString)
    {
        if (Strings2.isNullOrEmpty(typeString) || typeString.equalsIgnoreCase(SqlColumnType.TYPE_UNKNOWN))
            return null;
        final SqlColumnType newType = new SqlColumnType(typeString);
        if (newType.isValid())
            return newType;
        return null;
    }

    public void setTypeString(final String typeString)
    {
        setType(typeFromString(typeString));
    }

    /**
     * The stack is used to avoid cyclic recursion.
     */
    private SqlColumnType getType(Collection<ColumnDef> stack)
    {
        // avoiding cyclic recursion
        if (stack.contains(this))
            return SqlColumnType.unknown();

        if (!Strings2.isNullOrEmpty(getTableTo()))
        {
            final Entity entity = getProject().getEntity(getTableTo());
            if (entity != null && entity.findTableDefinition() != null)
            {
                final TableDef targetTableDef = entity.findTableDefinition();
                final String columnTo = getColumnsTo();
                if (!Strings2.isNullOrEmpty(columnTo))
                {
                    final ColumnDef targetColumn = targetTableDef.findColumn(columnTo);
                    if (targetColumn != null)
                    {
                        stack.add(this);
                        return targetColumn.getType(stack);
                    }
                }
            }
        }

        if (getPermittedTables() != null && getPermittedTables().length != 0)
            return new SqlColumnType("VARCHAR(50)");

        // failed to determine a column type
        if (!Strings2.isNullOrEmpty(getTableTo()))
            return SqlColumnType.unknown();

        if (type == null)
            return SqlColumnType.unknown();

        return type;
    }

    public void setType(SqlColumnType type)
    {
        this.type = customizeProperty("type", this.type, type);
        fireChanged();
    }

    @PropertyName("Can be null")
    public boolean isCanBeNull()
    {
        return getValue("canBeNull", canBeNull, false, () -> ((ColumnDef) prototype).isCanBeNull());
    }

    public void setCanBeNull(boolean canBeNull)
    {
        this.canBeNull = customizeProperty("canBeNull", this.canBeNull, canBeNull);
        fireChanged();
    }

    @PropertyName("Primary Key")
    public boolean isPrimaryKey()
    {
        return getName().equals(getTable().getEntity().getPrimaryKey());
    }

    public void setPrimaryKey(boolean primaryKey)
    {
        if (primaryKey)
            getTable().getEntity().setPrimaryKey(getName());
        else if (getName().equals(getTable().getEntity().getPrimaryKey()))
            getTable().getEntity().setPrimaryKey("");
    }

    @PropertyName("Default value")
    public String getDefaultValue()
    {
        return getValue("defaultValue", defaultValue, () -> ((ColumnDef) prototype).getDefaultValue());
    }

    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = customizeProperty("defaultValue", this.defaultValue, Strings2.emptyToNull(defaultValue));
        fireChanged();
    }

    @PropertyName("Old names of the column")
    @PropertyDescription("When column with old name is detected in the database, then rename will be attempted")
    public String[] getOldNames()
    {
        return oldNames;
    }

    public void setOldNames(String[] oldNames)
    {
        this.oldNames = oldNames == null ? new String[0] : oldNames;
    }

    @PropertyName("Auto-increment")
    public boolean isAutoIncrement()
    {
        return isPrimaryKey() && autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement)
    {
        this.autoIncrement = autoIncrement;
        fireChanged();
    }

    public boolean isAutoIncrementHidden()
    {
        return !isPrimaryKey();
    }

    @Override
    public List<ProjectElementException> getErrors()
    {
        List<ProjectElementException> errors = new ArrayList<>();

        if (getName().length() > Constants.MAX_ID_LENGTH)
        {
            errors.add(new ProjectElementException(getCompletePath(), "name", "Column name is too long: " + getName().length() + " characters (" + Constants.MAX_ID_LENGTH + " allowed)"));
        }

        if (!hasReference() && getRawType() == null)
        {
            errors.add(new ProjectElementException(getCompletePath(), "type", "Column type should be specified."));
        }

        if (!getType().isValid())
        {
            errors.add(new ProjectElementException(getCompletePath(), "type", "Type is invalid: " + getType()));
        }
        if (isAutoIncrement() && !getType().doesSupportGeneratedKey())
        {
            errors.add(new ProjectElementException(getCompletePath(), "autoIncrement",
                    "Autoincrement set for non-integral type " + getType()));
        }
        for (String oldName : getOldNames())
        {
            if (getTable().getColumns().getCaseInsensitive(oldName) != null)
            {
                errors.add(new ProjectElementException(getCompletePath(), "oldNames", "List of old names contains existing column name '"
                        + oldName + "'."));
            }
        }
        return errors;
    }

    @Override
    public Collection<BeModelElement> getDependentElements()
    {
        final List<BeModelElement> dependentElements = new ArrayList<>();
        dependentElements.addAll(findReferencingIndices());

        // TODO: fix for new structure
        dependentElements.addAll(findReferences());

        return dependentElements;
    }

    private List<IndexDef> findReferencingIndices()
    {
        final List<IndexDef> indices = new ArrayList<>();

        for (IndexDef indexDef : getTable().getIndices())
            for (IndexColumnDef indexColumnDef : indexDef)
                if (indexColumnDef.getName().equals(getName()))
                    indices.add(indexDef);

        return indices;
    }

    /**
     * Finds both referenced and references table references.
     */
    private List<TableRef> findReferences()
    {
        final Module module = getTable().getModule();
        final List<TableRef> tableRefs = new ArrayList<>();

        for (final TableRef tableRef : module.getProject().findTableReferences())
            if (references(tableRef))
                tableRefs.add(tableRef);

        return tableRefs;
    }

    private boolean references(final TableRef tableRef)
    {
        return getTable().getEntityName().equals(tableRef.getTableFrom()) && getName().equals(tableRef.getColumnsFrom())
                || getTable().getEntityName().equals(tableRef.getTableTo()) && getName().equals(tableRef.getColumnsTo());
    }

    public boolean hasReference()
    {
        return !Strings2.isNullOrEmpty(getTableTo()) || getPermittedTables() != null;
    }

    @Override
    public String getTableFrom()
    {
        return getTable().getEntityName();
    }

    @Override
    public Collection<String> getCustomizableProperties()
    {
        return CUSTOMIZABLE_PROPERTIES;
    }

    @Override
    protected void internalCustomizeProperty(String propertyName)
    {
        super.internalCustomizeProperty(propertyName);
        moveToApplication();
    }

    public void moveToApplication()
    {
        setOriginModuleName(getProject().getProjectOrigin());
        fireChanged();
    }
}
