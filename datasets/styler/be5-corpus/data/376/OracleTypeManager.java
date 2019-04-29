package com.developmentontheedge.be5.metadata.sql.type;

import com.developmentontheedge.be5.metadata.model.ColumnDef;
import com.developmentontheedge.be5.metadata.model.ColumnFunction;
import com.developmentontheedge.be5.metadata.model.SqlColumnType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class OracleTypeManager extends DefaultTypeManager
{
    @Override
    public String getRenameColumnStatements(ColumnDef column, String newName)
    {
        return "RENAME COLUMN " + normalizeIdentifier(column.getTable().getEntityName()) + "." + normalizeIdentifier(column.getName())
                + " TO " + normalizeIdentifier(newName) + ";\n";
    }

    @Override
    public String getAutoIncrementClause(ColumnDef column)
    {
        return "DEFAULT 'auto-identity'";
    }

    @Override
    public String getAlterColumnStatements(ColumnDef newColumn, ColumnDef oldColumn)
    {
        StringBuilder sb = new StringBuilder();
        String addend = "";
        if (!oldColumn.isCanBeNull())
        {
            if (newColumn.isCanBeNull())
                addend = " NULL";
            newColumn = (ColumnDef) newColumn.clone(newColumn.getOrigin(), newColumn.getName());
            newColumn.setCanBeNull(true); // Omit can be null declaration if it should not be changed
        }
        sb.append("ALTER TABLE ").append(normalizeIdentifier(newColumn.getTable().getEntityName())).append(" MODIFY (")
                .append(getColumnDefinitionClause(newColumn)).append(addend).append(");");
        // TODO: check whether it's necessary to update existing null values
        return sb.toString();
    }

    @Override
    public String getAddColumnStatements(ColumnDef column)
    {
        return "ALTER TABLE " + normalizeIdentifier(column.getTable().getEntityName()) + " ADD " + getColumnDefinitionClause(column) + ";\n";
    }

    private static Set<String> KEYWORDS = new HashSet<>(Arrays.asList("SELECT", "KEY", "ORDER", "TABLE", "WHERE", "GROUP", "FROM", "TO",
            "BY", "JOIN", "LEFT", "INNER", "OUTER", "NUMBER", "DISTINCT", "COMMENT", "START", "END", "INDEX", "DATE", "LEVEL"));

    @Override
    public void correctType(SqlColumnType type)
    {
        switch (type.getTypeName())
        {
            case "TIMESTAMP(6)":
                type.setTypeName(SqlColumnType.TYPE_TIMESTAMP);
                break;
            case "VARCHAR2":
                type.setTypeName(SqlColumnType.TYPE_VARCHAR);
                break;
            case "CLOB":
                type.setTypeName(SqlColumnType.TYPE_BIGTEXT);
                break;
            case "NUMBER":
                if (type.getPrecision() > 0)
                {
                    type.setTypeName(SqlColumnType.TYPE_DECIMAL);
                }
                else if (type.getSize() > 10)
                {
                    type.setTypeName(SqlColumnType.TYPE_BIGINT);
                }
                else if (type.getSize() > 5)
                {
                    type.setTypeName(SqlColumnType.TYPE_INT);
                }
                else
                {
                    type.setTypeName(SqlColumnType.TYPE_SMALLINT);
                }
                break;
        }
        super.correctType(type);
    }

    @Override
    public String getTypeClause(SqlColumnType type)
    {
        switch (type.getTypeName())
        {
            case SqlColumnType.TYPE_SMALLINT:
                return "NUMBER(5)";
            case SqlColumnType.TYPE_INT:
            case SqlColumnType.TYPE_UINT:
                return "NUMBER(10)";
            case SqlColumnType.TYPE_BIGINT:
            case SqlColumnType.TYPE_UBIGINT:
                return "NUMBER(20)";
            case SqlColumnType.TYPE_DATETIME:
                return "TIMESTAMP";
            case SqlColumnType.TYPE_VARCHAR:
                return "VARCHAR2(" + type.getSize() + " CHAR)";
            case SqlColumnType.TYPE_CURRENCY:
            case SqlColumnType.TYPE_DECIMAL:
                return "NUMBER(" + type.getSize() + "," + type.getPrecision() + ")";
            case SqlColumnType.TYPE_TEXT:
                return "VARCHAR2(4000 CHAR)";
            case SqlColumnType.TYPE_BIGTEXT:
                return "CLOB";
            case SqlColumnType.TYPE_BOOL:
            case SqlColumnType.TYPE_ENUM:
                int maxLen = 0;
                for (String enumValue : type.getEnumValues())
                {
                    maxLen = Math.max(maxLen, enumValue.length());
                }
                return "VARCHAR2(" + (maxLen + 1) + " CHAR)";
            default:
                return super.getTypeClause(type);
        }
    }

    @Override
    public String getKeyType()
    {
        return "VARCHAR2(15 CHAR)";
    }

    @Override
    public String normalizeIdentifier(String identifier)
    {
        if (identifier == null)
            return null;
        if (KEYWORDS.contains(identifier.toUpperCase()) || identifier.startsWith("___"))
            return '"' + identifier + '"';
        return normalizeIdentifierCase(identifier);
    }

    @Override
    public String normalizeIdentifierCase(String identifier)
    {
        return identifier == null ? null : identifier.toUpperCase();
    }

    @Override
    public String getDefaultValue(ColumnDef column)
    {
        if (column.getType().getTypeName().equals(SqlColumnType.TYPE_DATE))
            return "TO_DATE(" + column.getDefaultValue() + ",'YYYY-MM-DD')";
        return super.getDefaultValue(column);
    }

    @Override
    public String getDropTableStatements(String table)
    {
        // We use our 'drop_if_exists' procedure
        return "call drop_if_exists( '" + table + "' );\n";
    }

    @Override
    public String getDropIndexClause(String index, String table)
    {
        return "DROP INDEX " + normalizeIdentifier(index);
    }

    @Override
    public String getColumnTriggerDefinition(ColumnDef column)
    {
        String defaultValue = column.getDefaultValue();
        if (defaultValue == null)
            return "";
        ColumnFunction function = new ColumnFunction(defaultValue);
        if (ColumnFunction.TRANSFORM_GENERIC.equals(function.getTransform()))
        {
            return "CREATE OR REPLACE TRIGGER " + getTriggerName(column) +
                    "\nBEFORE INSERT OR UPDATE OF " + normalizeIdentifier(function.getColumnName()) + " ON " + normalizeIdentifier(column.getEntity().getName()) +
                    "\nFOR EACH ROW" +
                    "\nBEGIN" +
                    "\n   :new." + normalizeIdentifier(column.getName()) + " := '" + column.getEntity().getName() +
                    ".' || :new." + normalizeIdentifier(function.getColumnName()) + ";" +
                    "\nEND;\n";
        }
        return "";
    }

    private String getTriggerName(ColumnDef column)
    {
        int maxTriggerNameLength = 30;
        String postfix = "_trigger";
        String name = column.getEntity().getName() + "_" + column.getName();
        if (maxTriggerNameLength < (name.length() + postfix.length()))
        {
            name = column.getEntity().getName().substring(0, 1) + Integer.toHexString(column.getEntity().getName().hashCode()) + "_"
                    + column.getName().substring(0, 1) + Integer.toHexString(column.getName().hashCode());
        }
        return normalizeIdentifier(name + postfix);
    }

    @Override
    public String getDropTriggerDefinition(ColumnDef column)
    {
        String defaultValue = column.getDefaultValue();
        if (defaultValue == null)
            return "";
        ColumnFunction function = new ColumnFunction(defaultValue);
        if (ColumnFunction.TRANSFORM_GENERIC.equals(function.getTransform()))
        {
            return "DROP TRIGGER " + getTriggerName(column) + ";\n";
        }
        return "";
    }

    @Override
    public boolean isCustomAutoincrementSupported()
    {
        return false;
    }

}
