package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.beans.annot.PropertyName;
import com.developmentontheedge.dbms.ExtendedSqlException;
import com.developmentontheedge.dbms.SqlExecutor;

import java.util.List;

public interface DdlElement extends BeModelElement
{
    public String getEntityName();

    @PropertyName("DDL")
    public String getDdl();

    public String getCreateDdl();

    public String getDropDdl();

    public String getDiffDdl(DdlElement other, SqlExecutor sql) throws ExtendedSqlException;

    public String getDangerousDiffStatements(DdlElement other, SqlExecutor sql) throws ExtendedSqlException;

    public List<ProjectElementException> getWarnings();

    public default boolean withoutDbScheme()
    {
        return !getEntityName().contains(".");
    }
}
