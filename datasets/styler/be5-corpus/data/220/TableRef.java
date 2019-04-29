package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.DatabaseConstants;
import com.developmentontheedge.be5.metadata.model.base.BeElementWithOriginModule;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElementSupport;
import com.developmentontheedge.be5.metadata.util.Strings2;
import com.developmentontheedge.beans.annot.PropertyName;

import java.util.Arrays;

@PropertyName("Table cross-reference")
public class TableRef extends BeModelElementSupport implements TableReference, BeElementWithOriginModule
{
    private final String columnsFrom;
    private String tableTo;
    private String columnsTo;
    private String viewName = DatabaseConstants.SELECTION_VIEW;
    // permitted tables are only useful when tableTo == null
    private String[] permittedTables;
    private String originModuleName;

    public TableRef(final String name, final String columnFrom, final BeModelCollection<? extends TableRef> parent)
    {
        super(name, parent);
        this.columnsFrom = columnFrom;
        this.originModuleName = getModule().getName();
    }

    @Override
    public boolean isCustomized()
    {
        return getProject().getProjectOrigin().equals(originModuleName) && !getModule().getName().equals(originModuleName);
    }

    @Override
    public Module getModule()
    {
        return getEntity().getModule();
    }

    public Entity getEntity()
    {
        return (Entity) getOrigin().getOrigin();
    }

    @PropertyName("Table from")
    @Override
    public String getTableFrom()
    {
        return getEntity().getName();
    }

    @PropertyName("Columns from")
    @Override
    public String getColumnsFrom()
    {
        return columnsFrom;
    }

    @PropertyName("Table to")
    @Override
    public String getTableTo()
    {
        return tableTo;
    }

    @Override
    public void setTableTo(String tableTo)
    {
        this.tableTo = tableTo;
        fireChanged();
    }

    @PropertyName("Column to")
    @Override
    public String getColumnsTo()
    {
        if (!Strings2.isNullOrEmpty(tableTo) && Strings2.isNullOrEmpty(columnsTo))
        {
            final Entity entity = getProject().getEntity(tableTo);
            if (entity != null)
            {
                final String primaryKey = entity.getPrimaryKey();
                return primaryKey;
            }
        }

        return columnsTo;
    }

    @Override
    public void setColumnsTo(String columnsTo)
    {
        this.columnsTo = columnsTo;
        fireChanged();
    }

    @PropertyName("Selection view name")
    @Override
    public String getViewName()
    {
        return viewName;
    }

    @Override
    public void setViewName(String viewName)
    {
        this.viewName = Strings2.emptyToNull(viewName);
        fireChanged();
    }

    @PropertyName("Permitted tables (for genericRef)")
    @Override
    public String[] getPermittedTables()
    {
        return permittedTables;
    }

    @Override
    public void setPermittedTables(String[] permittedTables)
    {
        this.permittedTables = permittedTables;
        if (permittedTables != null)
        {
            Arrays.sort(permittedTables);
        }
        fireChanged();
    }

    @Override
    public String getOriginModuleName()
    {
        return originModuleName;
    }

    @Override
    public void setOriginModuleName(String originModuleName)
    {
        this.originModuleName = originModuleName;
        fireChanged();
    }

    /**
     * @return tableTo or permittedTables
     */
    public String[] getTargetTables()
    {
        if (tableTo != null)
        {
            return new String[]{tableTo};
        }

        if (permittedTables == null)
        {
            return Strings2.EMPTY;
        }

        return permittedTables;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return debugEquals("null");
        if (!(obj instanceof TableReference))
            return debugEquals("class");
        return equalsReference((TableReference) obj);
    }

    public static String nameFor(final String columnFrom, final String tableTo)
    {
        return columnFrom + " -> " + (tableTo == null ? "(generic)" : tableTo);
    }

    @Override
    public boolean equalsReference(TableReference other)
    {
        if (columnsFrom == null)
        {
            if (other.getColumnsFrom() != null)
                return debugEquals("columnsFrom");
        }
        else if (!columnsFrom.equalsIgnoreCase(other.getColumnsFrom()))
            return debugEquals("columnsFrom");
        if (getColumnsTo() == null)
        {
            if (other.getColumnsTo() != null)
                return debugEquals("columnsTo");
        }
        else if (!getColumnsTo().equalsIgnoreCase(other.getColumnsTo()))
            return debugEquals("columnsTo");
        if (tableTo == null)
        {
            if (other.getTableTo() != null)
                return debugEquals("tableTo");
        }
        else if (!tableTo.equalsIgnoreCase(other.getTableTo()))
            return debugEquals("tableTo");
        if (tableTo != null)
        {
            if (viewName == null)
            {
                if (other.getViewName() != null)
                    return debugEquals("viewName");
            }
            else if (!viewName.equals(other.getViewName()))
                return debugEquals("viewName");
        }
        if (tableTo == null && !Arrays.equals(permittedTables, other.getPermittedTables()))
            return debugEquals("permittedTables");
        return true;
    }

    @Override
    protected void fireChanged()
    {
        if (getOrigin() != null && getOrigin().get(getName()) == this)
            getOrigin().fireCodeChanged();
    }

}
