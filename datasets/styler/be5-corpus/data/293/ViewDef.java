package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.model.base.BeModelElementSupport;
import com.developmentontheedge.beans.annot.PropertyName;
import com.developmentontheedge.dbms.SqlExecutor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * SQL view definition
 *
 * @author lan
 */
@PropertyName("SQL view")
public class ViewDef extends BeModelElementSupport implements DdlElement
{
    private String definition;

    public ViewDef(Entity entity)
    {
        super(Entity.SCHEME, entity);
    }

    private Entity getEntity()
    {
        return (Entity) getOrigin();
    }

    @PropertyName("Definition")
    public String getDefinition()
    {
        return definition;
    }

    public void setDefinition(String definition)
    {
        this.definition = definition;
        fireChanged();
    }

    @Override
    public String getDdl()
    {
        return getDropDdl() + getCreateDdl();
    }

    @Override
    public String getCreateDdl()
    {
        return "CREATE VIEW " + getEntityName() + " AS \n" + getDefinition() + ";\n";
    }

    @Override
    public String getDropDdl()
    {
        return "DROP VIEW IF EXISTS " + getEntityName() + ";\n";
    }

    @Override
    public String getDiffDdl(DdlElement other, SqlExecutor sql)
    {
        if (other == null)
            return getCreateDdl();
        return other.getDropDdl() + getCreateDdl();
    }

    @Override
    public String getEntityName()
    {
        return getOrigin().getName();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ViewDef other = (ViewDef) obj;
        return Objects.equals(definition, other.definition);
    }

    @Override
    public String getDangerousDiffStatements(DdlElement other, SqlExecutor sql)
    {
        return "";
    }

    @Override
    public List<ProjectElementException> getWarnings()
    {
        return Collections.emptyList();
    }

    @Override
    protected void fireChanged()
    {
        if (getEntity().getScheme() == this)
            getEntity().fireCodeChanged();
    }

}
