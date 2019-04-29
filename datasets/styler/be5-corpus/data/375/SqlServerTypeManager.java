package com.developmentontheedge.be5.metadata.sql.type;

import com.developmentontheedge.be5.metadata.model.ColumnDef;
import com.developmentontheedge.be5.metadata.model.ColumnFunction;
import com.developmentontheedge.be5.metadata.model.IndexColumnDef;
import com.developmentontheedge.be5.metadata.model.IndexDef;
import com.developmentontheedge.be5.metadata.model.SqlColumnType;
import com.developmentontheedge.be5.metadata.util.Strings2;

public class SqlServerTypeManager extends DefaultTypeManager
{

    @Override
    public String getRenameColumnStatements(ColumnDef column, String newName)
    {
        return "EXEC sp_RENAME '[" + column.getTable().getEntityName() + "].[" + column.getName() + "]', '" + newName + "', 'COLUMN';\n";
    }

    @Override
    public String getAlterColumnStatements(ColumnDef column, ColumnDef oldColumn)
    {
        String tableName = column.getTable().getEntityName();
        String columnName = column.getName();
        String prefix = "ALTER TABLE " + normalizeIdentifier(tableName) + " ALTER COLUMN " + normalizeIdentifier(columnName) + " ";
        StringBuilder sb = new StringBuilder();
        StringBuilder endSb = new StringBuilder();
        for (IndexDef index : oldColumn.getTable().getIndices().getAvailableElements())
        {
            for (IndexColumnDef indexColumn : index.getAvailableElements())
            {
                if (indexColumn.getName().equals(oldColumn.getName()))
                {
                    sb.append(index.getDropDdl());
                    endSb.append(index.getCreateDdl());
                    break;
                }
            }
        }
        if (column.isCanBeNull() && (!oldColumn.isCanBeNull()
                || (!column.getType().toString().equals(oldColumn.getType().toString()))))
        {
            sb.append(prefix).append(getTypeClause(column.getType())).append(" NULL;");
        }
        if (!column.isCanBeNull() && (oldColumn.isCanBeNull()
                || (!column.getType().toString().equals(oldColumn.getType().toString()))))
        {
            if (oldColumn.isCanBeNull() && !Strings2.isNullOrEmpty(column.getDefaultValue()))
            {
                sb.append("UPDATE ").append(normalizeIdentifier(tableName)).append(" SET ")
                        .append(normalizeIdentifier(columnName)).append('=').append(column.getDefaultValue()).append(" WHERE ")
                        .append(normalizeIdentifier(columnName)).append(" IS NULL;");
            }
            sb.append(prefix).append(getTypeClause(column.getType())).append(" NOT NULL;");
        }
        if (!Strings2.nullToEmpty(column.getDefaultValue()).equals(Strings2.nullToEmpty(oldColumn.getDefaultValue())))
        {
            if (!Strings2.isNullOrEmpty(oldColumn.getDefaultValue()))
            {
                sb.append(getDropDefaultStatements(tableName, columnName));
            }
            if (!Strings2.isNullOrEmpty(column.getDefaultValue()))
            {
                sb.append("ALTER TABLE ").append(normalizeIdentifier(tableName)).append(" ADD CONSTRAINT df_")
                        .append(tableName).append('_').append(columnName).append(" DEFAULT ")
                        .append(column.getDefaultValue()).append(" FOR ").append(normalizeIdentifier(columnName)).append(';');
            }
        }
        sb.append(endSb);
        return sb.toString();
    }

    private String getDropDefaultStatements(String tableName, String columnName)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("BEGIN DECLARE @Command nvarchar(max), @ConstaintName nvarchar(max)\n");
        sb.append("SELECT @ConstaintName = name FROM sys.default_constraints WHERE parent_object_id = object_id('"
                + tableName + "') AND parent_column_id = columnproperty(object_id('" + tableName
                + "'), '" + columnName + "', 'ColumnId')\n");
        sb.append("SELECT @Command = 'ALTER TABLE " + normalizeIdentifier(tableName)
                + " DROP CONSTRAINT '+ @ConstaintName\n");
        sb.append("EXECUTE sp_executeSQL @Command\nEND;\n");
        return sb.toString();
    }

    @Override
    public String getGeneratedPrefix()
    {
        return "AS";
    }

    @Override
    public String getTypeClause(ColumnDef column)
    {
        if (column.getDefaultValue() != null)
        {
            String defaultValue = getDefaultValue(column);
            ColumnFunction function = new ColumnFunction(defaultValue);
            if (function.isTransformed())
            {
                return "";
            }
        }
        return super.getTypeClause(column);
    }

    @Override
    public void correctType(SqlColumnType type)
    {
        switch (type.getTypeName())
        {
            case "image":
                type.setTypeName(SqlColumnType.TYPE_BLOB);
                break;
            case "varchar":
                if (type.getSize() == -1)
                    type.setTypeName(SqlColumnType.TYPE_TEXT);
                else
                    type.setTypeName(SqlColumnType.TYPE_VARCHAR);
                break;
            default:
                type.setTypeName(type.getTypeName().toUpperCase());
        }
        super.correctType(type);
    }

    @Override
    public String getDropTableStatements(String table)
    {
        return "IF EXISTS (SELECT ID FROM sysobjects WHERE id = OBJECT_ID(N'" + table + "') AND OBJECTPROPERTY(id, N'IsUserTable') = 1 )\n"
                + "DROP TABLE " + table + ";\n";
    }

    @Override
    public String getDropColumnStatements(ColumnDef column)
    {
        String result;
        if (!Strings2.isNullOrEmpty(column.getDefaultValue()))
            result = getDropDefaultStatements(column.getTable().getEntityName(), column.getName());
        else
            result = "";
        return result + super.getDropColumnStatements(column);
    }

    @Override
    public String getDropIndexClause(String index, String table)
    {
        return "DROP INDEX " + normalizeIdentifier(index) + " ON " + normalizeIdentifier(table);
    }

    @Override
    public String getAddColumnStatements(ColumnDef column)
    {
        return "ALTER TABLE " + normalizeIdentifier(column.getTable().getEntityName()) + " ADD " + getColumnDefinitionClause(column) + ";";
    }

    @Override
    public String normalizeIdentifier(String identifier)
    {
        if (identifier.startsWith("\""))
            return identifier;
        return "\"" + identifier + "\"";
    }

    @Override
    public String getTypeClause(SqlColumnType type)
    {
        switch (type.getTypeName())
        {
            case SqlColumnType.TYPE_TIMESTAMP:
                return "DATETIME";
            case SqlColumnType.TYPE_UBIGINT:
                return "BIGINT";
            case SqlColumnType.TYPE_UINT:
                return "INT";
            case SqlColumnType.TYPE_TEXT:
            case SqlColumnType.TYPE_BIGTEXT:
                return "VARCHAR(MAX)";
            case SqlColumnType.TYPE_MEDIUMBLOB:
            case SqlColumnType.TYPE_BLOB:
                return "IMAGE";
            case SqlColumnType.TYPE_BOOL:
            case SqlColumnType.TYPE_ENUM:
                int maxLen = 0;
                for (String enumValue : type.getEnumValues())
                {
                    maxLen = Math.max(maxLen, enumValue.length());
                }
                return "VARCHAR(" + (maxLen) + ")";
        }
        return super.getTypeClause(type);
    }

    @Override
    public String getAutoIncrementClause(ColumnDef column)
    {
        return "IDENTITY";
    }

    @Override
    public String getStartingIncrementDefinition(String table, String column, long startValue)
    {
        return "ALTER TABLE " + normalizeIdentifier(table) + " ALTER COLUMN " + normalizeIdentifier(column) + " IDENTITY(" + startValue + ", 1);\n";
    }

    @Override
    public boolean isFunctionalIndexSupported()
    {
        return false;
    }

    @Override
    public boolean isGeneratedColumnSupported()
    {
        return true;
    }
}
