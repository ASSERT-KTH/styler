package com.developmentontheedge.be5.metadata.sql.type;

import com.developmentontheedge.be5.metadata.model.ColumnDef;
import com.developmentontheedge.be5.metadata.model.SqlColumnType;

public class MySqlTypeManager extends DefaultTypeManager
{
    @Override
    public String getRenameColumnStatements(ColumnDef column, String newName)
    {
        StringBuilder sb = new StringBuilder();
        ColumnDef newColumn = (ColumnDef) column.clone(column.getOrigin(), newName);
        sb.append("ALTER TABLE ").append(normalizeIdentifier(column.getTable().getEntityName())).append(" CHANGE COLUMN ")
                .append(normalizeIdentifier(column.getName())).append(' ').append(getColumnDefinitionClause(newColumn)).append(';');
        // TODO: check whether it's necessary to update existing null values
        return sb.toString();
    }

    @Override
    public String getAlterColumnStatements(ColumnDef newColumn, ColumnDef oldColumn)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append(normalizeIdentifier(newColumn.getTable().getEntityName())).append(" MODIFY COLUMN ").append(getColumnDefinitionClause(newColumn)).append(';');
        // TODO: check whether it's necessary to update existing null values
        return sb.toString();
    }

    @Override
    public boolean isFunctionalIndexSupported()
    {
        return false;
    }

    @Override
    public String getConstraintClause(ColumnDef column)
    {
        return "";
    }

    @Override
    public void correctType(SqlColumnType type)
    {
        switch (type.getTypeName())
        {
            case "DOUBLE":
                type.setTypeName(SqlColumnType.TYPE_DECIMAL);
                if (type.getSize() == 22 && type.getPrecision() == 0)
                {
                    type.setPrecision(10);
                }
                break;
            case "SET":
                type.setTypeName(SqlColumnType.TYPE_VARCHAR);
                break;
        }
        super.correctType(type);
    }

    @Override
    public String getTypeClause(SqlColumnType type)
    {
        switch (type.getTypeName())
        {
            case SqlColumnType.TYPE_BIGTEXT:
                return "TEXT";
            case SqlColumnType.TYPE_BLOB:
                return "MEDIUMBLOB";
            case SqlColumnType.TYPE_DECIMAL:
                if (type.getSize() == 22 && type.getPrecision() == 10)
                {
                    return "DOUBLE";
                }
                return super.getTypeClause(type);
            case SqlColumnType.TYPE_VARCHAR:
                if (type.getSize() > 255)
                {
                    return "TEXT";
                }
                return super.getTypeClause(type);
            case SqlColumnType.TYPE_BOOL:
                return "ENUM('no','yes')";
            default:
                return super.getTypeClause(type);
        }
    }

    @Override
    public String normalizeIdentifier(String identifier)
    {
        if (identifier == null)
            return null;
        return '`' + identifier + '`';
    }

    @Override
    public String getKeyType()
    {
        return SqlColumnType.TYPE_UBIGINT;
    }

    @Override
    public String getStartingIncrementDefinition(String table, String column, long startValue)
    {
        return "ALTER TABLE " + normalizeIdentifier(table) + " AUTO_INCREMENT=" + startValue + ";\n";
    }

    @Override
    public String getDropIndexClause(String index, String table)
    {
        return "DROP INDEX " + normalizeIdentifier(index) + " ON " + normalizeIdentifier(table);
    }

    @Override
    public String getDropTableStatements(String table)
    {
        return "DROP TABLE IF EXISTS " + normalizeIdentifier(table) + ";\n" +
                "DROP VIEW IF EXISTS " + normalizeIdentifier(table) + ";\n";
    }
}
