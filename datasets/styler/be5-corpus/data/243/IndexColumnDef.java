package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElementSupport;
import com.developmentontheedge.be5.metadata.sql.Rdbms;
import com.developmentontheedge.beans.annot.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class IndexColumnDef extends BeModelElementSupport
{
    private ColumnFunction function;

    public IndexColumnDef(String name, IndexDef origin)
    {
        super(name, origin);
        this.function = new ColumnFunction(name, ColumnFunction.TRANSFORM_NONE);
    }

    public IndexColumnDef(ColumnFunction f, IndexDef origin)
    {
        super(f.getColumnName(), origin);
        this.function = f;
    }

    private IndexDef getIndexDef()
    {
        return (IndexDef) getOrigin();
    }

    public String getAsString()
    {
        return function.toString();
    }

    public static IndexColumnDef createFromString(String definition, IndexDef parent)
    {
        ColumnFunction f = new ColumnFunction(definition);
        IndexColumnDef indexColumnDef = new IndexColumnDef(f, parent);
        indexColumnDef.setTransform(f.getTransform());
        return indexColumnDef;
    }

    @Override
    @PropertyName("Column name")
    public String getName()
    {
        return super.getName();
    }

    @PropertyName("Transform")
    public String getTransform()
    {
        return function.getTransform();
    }

    public void setTransform(String transform)
    {
        function = new ColumnFunction(getName(), transform);
        getIndexDef().fireCodeChanged();
    }

    public boolean isFunctional()
    {
        return function.isTransformed();
    }

    public String getDefinition()
    {
        Rdbms rdbms = getProject().getDatabaseSystem();
        if (rdbms == Rdbms.MYSQL && !function.isTransformed())
        {
            ColumnDef column = getIndexDef().getTable().getColumns().get(getName());
            if (column != null && (column.getType().getTypeName().equals(SqlColumnType.TYPE_TEXT)
                    || column.getType().getTypeName().equals(SqlColumnType.TYPE_BIGTEXT)
                    || column.getType().getTypeName().equals(SqlColumnType.TYPE_BLOB)))
            {
                return rdbms.getTypeManager().normalizeIdentifier(getName()) + "(" + 100 + ")";
            }
        }
        return function.getDefinition(rdbms, getIndexDef().getEntityName());
    }

    @Override
    public List<ProjectElementException> getErrors()
    {
        List<ProjectElementException> result = new ArrayList<>();
        try
        {
            ModelValidationUtils.checkValueInSet(this, "transform", getTransform(), ColumnFunction.TRANSFORMS);
        }
        catch (ProjectElementException e)
        {
            result.add(e);
        }
        if (isFromApplication() && ((IndexDef) getOrigin()).getTable().getColumns().getCaseInsensitive(getName()) == null)
        {
            result.add(new ProjectElementException(this, "Column not found"));
        }
        return result;
    }

    @Override
    public IndexColumnDef clone(BeModelCollection<?> origin, String name)
    {
        IndexColumnDef clone = (IndexColumnDef) super.clone(origin, name);
        clone.function = new ColumnFunction(name, function.getTransform());
        return clone;
    }
}