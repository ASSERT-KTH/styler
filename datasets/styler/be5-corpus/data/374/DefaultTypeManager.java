package com.developmentontheedge.be5.metadata.sql.type;

import com.developmentontheedge.be5.metadata.model.ColumnDef;
import com.developmentontheedge.be5.metadata.model.ColumnFunction;
import com.developmentontheedge.be5.metadata.model.IndexColumnDef;
import com.developmentontheedge.be5.metadata.model.IndexDef;
import com.developmentontheedge.be5.metadata.model.SqlColumnType;
import com.developmentontheedge.be5.metadata.util.Strings2;

import java.util.Arrays;

/**
 * Type manager for unknown Dbms
 *
 * @author lan
 */
public class DefaultTypeManager implements DbmsTypeManager
{
    @Override
    public void correctType(SqlColumnType type)
    {
        if (SqlColumnType.TYPE_DECIMAL.equals(type.getTypeName())
                && type.getSize() == 18 && type.getPrecision() == 2)
        {
            type.setTypeName(SqlColumnType.TYPE_CURRENCY);
        }
    }

    @Override
    public String getKeyType()
    {
        return SqlColumnType.TYPE_BIGINT;
    }

    @Override
    public String getTypeClause(SqlColumnType type)
    {
        if (type.getTypeName().equals(SqlColumnType.TYPE_CURRENCY))
            return "DECIMAL(18,2)";
        if (type.getTypeName().equals(SqlColumnType.TYPE_KEY))
            return getKeyType();
        if (type.getTypeName().equals(SqlColumnType.TYPE_JSONB))
            return SqlColumnType.TYPE_TEXT;
        return type.toString();
    }

    public String getTypeClause(ColumnDef col)
    {
        return getTypeClause(col.getType());
    }

    /**
     * @param column - column to get the clause to (may be used in subclasses)
     */
    public String getAutoIncrementClause(ColumnDef column)
    {
        return "AUTO_INCREMENT";
    }

    @Override
    public String normalizeIdentifierCase(String identifier)
    {
        return identifier;
    }

    @Override
    public String normalizeIdentifier(String identifier)
    {
        if (identifier == null)
            return null;
        if (identifier.contains("_"))
            return '"' + identifier + '"';
        return identifier;
    }

    @Override
    public String getConstraintClause(ColumnDef column)
    {
        if (column.getType().getTypeName().equals(SqlColumnType.TYPE_ENUM) || column.getType().getTypeName().equals(SqlColumnType.TYPE_BOOL))
        {
            StringBuilder sb = new StringBuilder();
            sb.append("CHECK(").append(normalizeIdentifier(column.getName())).append(" IN (");
            String[] enumValues = column.getType().getEnumValues().clone();
            Arrays.sort(enumValues);
            for (int i = 0; i < enumValues.length; i++)
            {
                if (i > 0)
                    sb.append(", ");
                String value = enumValues[i];
                sb.append("'").append(value.replace("'", "''")).append("'");
            }
            sb.append(") )");
            return sb.toString();
        }
        return "";
    }

    @Override
    public String getColumnDefinitionClause(
            ColumnDef column)
    {
        StringBuilder sb = new StringBuilder(normalizeIdentifier(column.getName()));
        sb.append(' ').append(getTypeClause(column));
        if (column.isAutoIncrement())
        {
            sb.append(' ').append(getAutoIncrementClause(column));
        }
        if (column.getDefaultValue() != null)
        {
            String defaultValue = getDefaultValue(column);
            ColumnFunction function = new ColumnFunction(defaultValue);
            if (function.isTransformed())
            {
                if (getGeneratedPrefix() != null)
                {
                    sb.append(' ').append(getGeneratedPrefix()).append(' ')
                            .append(function.getDefinition(column.getProject().getDatabaseSystem(), column.getEntity().getName()));
                }
            }
            else
            {
                sb.append(' ').append("DEFAULT ").append(defaultValue);
            }
        }

        addCanBeNullAndAndConstraintClause(column, sb);

        if (column.isPrimaryKey())
        {
            sb.append(' ').append("PRIMARY KEY");
        }
        return sb.toString();
    }

    @Override
    public void addCanBeNullAndAndConstraintClause(ColumnDef column, StringBuilder sb)
    {
        String constraint = getConstraintClause(column);
        if (!constraint.isEmpty())
        {
            sb.append(' ').append(constraint);
        }
        if (!column.isCanBeNull())
        {
            sb.append(' ').append("NOT NULL");
        }
    }

    public String getGeneratedPrefix()
    {
        return null;
    }

    public String getDefaultValue(ColumnDef column)
    {
        return column.getDefaultValue();
    }

    @Override
    public String getDropTableStatements(String table)
    {
        return "DROP TABLE IF EXISTS " + normalizeIdentifier(table) + ";\n";
    }

    @Override
    public String getDropIndexClause(String index, String table)
    {
        return "DROP INDEX IF EXISTS " + normalizeIdentifier(index);
    }

    @Override
    public boolean isFunctionalIndexSupported()
    {
        return true;
    }

    @Override
    public boolean isCustomAutoincrementSupported()
    {
        return true;
    }

    @Override
    public String getStartingIncrementDefinition(String table, String column, long startValue)
    {
        return "";
    }

    @Override
    public String getAlterColumnStatements(ColumnDef column, ColumnDef oldColumn)
    {
        // Works for Postgres and Db2
        String prefix = "ALTER TABLE " + normalizeIdentifier(column.getTable().getEntityName()) + " ALTER COLUMN " + normalizeIdentifier(column.getName()) + " ";
        StringBuilder sb = new StringBuilder();
        if (!getTypeClause(column.getType()).equals(getTypeClause(oldColumn.getType())))
        {
            sb.append(prefix).append("SET DATA TYPE " + getTypeClause(column.getType())).append(";\n");
        }
        String constraintClause = getConstraintClause(column);
        String oldConstraintClause = getConstraintClause(oldColumn);
        if (!constraintClause.equals(oldConstraintClause))
        {
            String constraintName = normalizeIdentifier(column.getTableFrom() + "_" + column.getName() + "_check");
            if (!oldConstraintClause.isEmpty())
            {
                sb.append("ALTER TABLE ").append(normalizeIdentifier(column.getTable().getEntityName())).append(" DROP CONSTRAINT ")
                        .append(constraintName).append(";\n");
            }
            if (!constraintClause.isEmpty())
            {
                sb.append("ALTER TABLE ").append(normalizeIdentifier(column.getTable().getEntityName())).append(" ADD CONSTRAINT ")
                        .append(constraintName).append(' ').append(constraintClause).append(";\n");
            }
        }
        if (column.isCanBeNull() && !oldColumn.isCanBeNull())
        {
            sb.append(prefix).append("DROP NOT NULL;");
        }
        if (!column.isCanBeNull() && oldColumn.isCanBeNull())
        {
            if (!Strings2.isNullOrEmpty(column.getDefaultValue()))
            {
                sb.append("UPDATE ").append(normalizeIdentifier(column.getTable().getEntityName())).append(" SET ")
                        .append(normalizeIdentifier(column.getName())).append('=').append(column.getDefaultValue()).append(" WHERE ")
                        .append(normalizeIdentifier(column.getName())).append(" IS NULL;");
            }
            sb.append(prefix).append("SET NOT NULL;");
        }
        if (!Strings2.nullToEmpty(column.getDefaultValue()).equals(Strings2.nullToEmpty(oldColumn.getDefaultValue())))
        {
            if (Strings2.isNullOrEmpty(column.getDefaultValue()))
            {
                sb.append(prefix).append("DROP DEFAULT;");
            }
            else
            {
                sb.append(prefix).append("SET DEFAULT ").append(column.getDefaultValue()).append(";");
            }
        }
        return sb.toString();
    }

    @Override
    public String getDropColumnStatements(ColumnDef column)
    {
        return "ALTER TABLE " + normalizeIdentifier(column.getTable().getEntityName()) + " DROP COLUMN " + normalizeIdentifier(column.getName()) + ";\n";
    }

    @Override
    public String getAddColumnStatements(ColumnDef column)
    {
        return "ALTER TABLE " + normalizeIdentifier(column.getTable().getEntityName()) + " ADD COLUMN " + getColumnDefinitionClause(column) + ";\n";
    }

    @Override
    public String getRenameColumnStatements(ColumnDef column, String newName)
    {
        return "ALTER TABLE " + normalizeIdentifier(column.getTable().getEntityName()) + " RENAME COLUMN "
                + normalizeIdentifier(column.getName()) + " TO " + normalizeIdentifier(newName) + ";\n";
    }

    @Override
    public String getCreateTableClause(String name)
    {
        return "CREATE TABLE " + normalizeIdentifier(name);
    }

    @Override
    public boolean isGeneratedColumnSupported()
    {
        return false;
    }

    @Override
    public String getColumnTriggerDefinition(ColumnDef column)
    {
        return "";
    }

    @Override
    public String getDropTriggerDefinition(ColumnDef column)
    {
        return "";
    }

    @Override
    public String getCreateIndexClause(IndexDef indexDef)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE ");
        if (indexDef.isUnique())
            sb.append("UNIQUE ");
        sb.append("INDEX ");
        sb.append(normalizeIdentifier(indexDef.getName()));
        sb.append(" ON ");
        sb.append(normalizeIdentifier(indexDef.getTable().getEntityName()));
        sb.append("(");
        sb.append(indexDef.stream().map(IndexColumnDef::getDefinition).joining(", "));
        sb.append(")");
        return sb + ";";
    }
}
