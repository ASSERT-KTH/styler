package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelElement;


public interface TableReference extends BeModelElement
{
    String getTableTo();

    void setTableTo(String tableTo);

    String getColumnsTo();

    void setColumnsTo(String columnsTo);

    String getViewName();

    void setViewName(String viewName);

    String[] getPermittedTables();

    void setPermittedTables(String[] permittedTables);

    String getTableFrom();

    String getColumnsFrom();

    boolean equalsReference(TableReference other);
}
