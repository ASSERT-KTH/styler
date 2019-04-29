package com.developmentontheedge.sql.format;

import com.developmentontheedge.sql.model.AstDerivedColumn;
import com.developmentontheedge.sql.model.AstFieldReference;
import com.developmentontheedge.sql.model.AstIdentifierConstant;
import com.developmentontheedge.sql.model.AstQuery;
import com.developmentontheedge.sql.model.AstSelect;
import com.developmentontheedge.sql.model.AstStart;
import com.developmentontheedge.sql.model.AstTableRef;
import com.developmentontheedge.sql.model.SimpleNode;

public class ColumnRef
{
    private final String table;
    private final String name;

    ColumnRef(String table, String name)
    {
        this.table = table;
        this.name = name;
    }

    public SimpleNode asNode()
    {
        return table == null ? new AstIdentifierConstant(name) : new AstFieldReference(table, name);
    }

    public static ColumnRef resolve(AstStart ast, String value)
    {
        return resolve(ast.getQuery(), value);
    }

    public static ColumnRef resolve(AstQuery query, String value)
    {
        for (AstSelect select : query.children().select(AstSelect.class))
        {
            for (AstDerivedColumn derColumn : select.tree().select(AstDerivedColumn.class))
            {
                String columnName = derColumn.getAlias() != null ? derColumn.getAlias() : derColumn.getColumn();
                if (value.equals(columnName))
                    return new ColumnRef(null, value);
            }
            String[] parts = value.split("[.]");
            if (parts.length > 2)
                return null;
            for (AstTableRef table : select.tree().select(AstTableRef.class))
            {
                String tableName = table.getAlias() != null ? table.getAlias() : table.getTable();
                if (parts[0].equals(tableName) || parts[0].equals(table.getTable()))
                {
                    return new ColumnRef(tableName, parts[1]);
                }
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnRef columnRef = (ColumnRef) o;

        if (table != null ? !table.equals(columnRef.table) : columnRef.table != null) return false;
        return name != null ? name.equals(columnRef.name) : columnRef.name == null;
    }

    @Override
    public int hashCode()
    {
        int result = table != null ? table.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "ColumnRef{" +
                "table='" + table + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
