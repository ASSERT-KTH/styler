package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.model.base.BeCaseInsensitiveCollection;
import com.developmentontheedge.be5.metadata.model.base.BeElementWithOriginModule;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.sql.Rdbms;
import com.developmentontheedge.beans.annot.PropertyName;
import com.developmentontheedge.dbms.SqlExecutor;

import java.util.Collections;
import java.util.List;

public class IndexDef extends BeCaseInsensitiveCollection<IndexColumnDef> implements DdlElement, BeElementWithOriginModule
{
    private boolean unique;
    private String originModuleName;

    public IndexDef(String name, BeModelCollection<IndexDef> origin)
    {
        super(name, IndexColumnDef.class, origin, true);
        this.originModuleName = getModule().getName();
        propagateCodeChange();
    }

    public TableDef getTable()
    {
        return (TableDef) getOrigin().getOrigin();
    }

    @PropertyName("Unique")
    public boolean isUnique()
    {
        return unique;
    }

    public void setUnique(boolean unique)
    {
        this.unique = unique;
        fireCodeChanged();
    }

    public boolean isFunctional()
    {
        for (IndexColumnDef col : this)
        {
            if (col.isFunctional())
                return true;
        }
        return false;
    }

    @Override
    public String getDdl()
    {
        return getDropDdl() + getCreateDdl();
    }

    @Override
    @PropertyName("Definition")
    public String getCreateDdl()
    {
        Rdbms rdbms = getProject().getDatabaseSystem();
        if (rdbms == null)
            return "";
        return rdbms.getTypeManager().getCreateIndexClause(this);
    }

    @Override
    public String getDropDdl()
    {
        Rdbms rdbms = getProject().getDatabaseSystem();
        if (rdbms == null)
            return "";
        return rdbms.getTypeManager().getDropIndexClause(getName(), getTable().getEntityName()) + ";\n";
    }

    @Override
    public String getDiffDdl(DdlElement other, SqlExecutor sql)
    {
        if (!(other instanceof IndexDef))
            return getCreateDdl();
        if (((IndexDef) other).getCreateDdl().equals(getCreateDdl()))
            return "";
        return getDdl();
    }

    @Override
    public List<ProjectElementException> getErrors()
    {
        List<ProjectElementException> errors = super.getErrors();
        if (getName().length() > Constants.MAX_ID_LENGTH)
        {
            errors.add(new ProjectElementException(getCompletePath(), "name", "Index name is too long: " + getName().length() + " characters (" + Constants.MAX_ID_LENGTH + " allowed)"));
        }
        if (getSize() == 0)
        {
            errors.add(new ProjectElementException(this, "Index must have at least one column"));
        }
        return errors;
    }

    @Override
    public boolean hasErrors()
    {
        if (getSize() == 0)
            return true;
        return super.hasErrors();
    }

    @Override
    public String getEntityName()
    {
        return getTable().getEntityName();
    }

    @Override
    public String getDangerousDiffStatements(DdlElement other, SqlExecutor sql)
    {
        return "";
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
    }

    @Override
    public List<ProjectElementException> getWarnings()
    {
        return Collections.emptyList();
    }

    @Override
    public boolean isCustomized()
    {
        return getProject().getProjectOrigin().equals(originModuleName) && !getModule().getName().equals(originModuleName);
    }

    @Override
    public void merge(BeModelCollection<IndexColumnDef> other, boolean ignoreMyItems, boolean inherit)
    {
        // Do not merge indices at all
        // Currently new index with the same name totally rewrites parent index object
    }

    public boolean isValidIndex()
    {
        for (IndexColumnDef col : this)
        {
            ColumnDef column = getTable().getColumns().getCaseInsensitive(col.getName());
            if (column == null || !column.isAvailable())
                return false;
        }
        return true;
    }
}
