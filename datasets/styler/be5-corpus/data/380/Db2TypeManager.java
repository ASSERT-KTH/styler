package com.developmentontheedge.be5.metadata.sql.type;

import com.developmentontheedge.be5.metadata.model.ColumnDef;
import com.developmentontheedge.be5.metadata.model.SqlColumnType;
import com.developmentontheedge.be5.metadata.model.TableDef;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Db2TypeManager extends DefaultTypeManager
{
    private static Set<String> KEYWORDS = new HashSet<>(Arrays.asList("SELECT", "KEY", "ORDER", "TABLE", "WHERE", "GROUP", "FROM", "TO",
            "BY", "JOIN", "LEFT", "INNER", "OUTER", "NUMBER", "DISTINCT", "COMMENT", "START", "END", "INDEX", "DATE", "LEVEL"));

    @Override
    public void correctType(SqlColumnType type)
    {
        switch (type.getTypeName())
        {
            case "INTEGER":
                if (type.getSize() == 4)
                {
                    type.setTypeName(SqlColumnType.TYPE_INT);
                }
                break;
            case "CHARACTER":
                type.setTypeName(SqlColumnType.TYPE_CHAR);
                break;
            case "DOUBLE":
                type.setTypeName(SqlColumnType.TYPE_DECIMAL);
                type.setPrecision(10);
                type.setSize(22);
                break;
            case "BLOB":
                if (type.getSize() < 4_000_000)
                {
                    type.setTypeName(SqlColumnType.TYPE_MEDIUMBLOB);
                }
                break;
            case "VARCHAR () FOR BIT DATA":
                type.setTypeName(SqlColumnType.TYPE_MEDIUMBLOB);
                break;
            case "LONG VARCHAR":
                type.setTypeName(SqlColumnType.TYPE_TEXT);
                break;
            case "CLOB":
                type.setTypeName(type.getSize() > 65536 ? SqlColumnType.TYPE_BIGTEXT : SqlColumnType.TYPE_TEXT);
                break;
            case "TIME":
                type.setTypeName(SqlColumnType.TYPE_TIMESTAMP);
                break;
        }
        super.correctType(type);
    }

    @Override
    public String getTypeClause(SqlColumnType type)
    {
        switch (type.getTypeName())
        {
            case SqlColumnType.TYPE_DATETIME:
                return "TIMESTAMP";
            case SqlColumnType.TYPE_UINT:
                return "INT";
            case SqlColumnType.TYPE_UBIGINT:
                return "BIGINT";
            case SqlColumnType.TYPE_TEXT:
                return "CLOB(64K)";
            case SqlColumnType.TYPE_BIGTEXT:
                return "CLOB(128K)";
            case SqlColumnType.TYPE_MEDIUMBLOB:
                return "BLOB(4M)";
            case SqlColumnType.TYPE_BLOB:
                return "BLOB(16M)";
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
    public String normalizeIdentifier(String identifier)
    {
        if (identifier == null)
            return null;
        if (KEYWORDS.contains(identifier.toUpperCase()) || identifier.startsWith("___"))
            return '"' + identifier + '"';
        return normalizeIdentifierCase(identifier);
    }

    @Override
    public String getGeneratedPrefix()
    {
        return "GENERATED ALWAYS AS";
    }

    @Override
    public String getAlterColumnStatements(ColumnDef column, ColumnDef oldColumn)
    {
        return super.getAlterColumnStatements(column, oldColumn) + getReorgTableStatements(column.getTable());
    }

    @Override
    public String getDropColumnStatements(ColumnDef column)
    {
        return super.getDropColumnStatements(column) + getReorgTableStatements(column.getTable());
    }

    private String getReorgTableStatements(TableDef table)
    {
        return "CALL admin_cmd('REORG TABLE " + normalizeIdentifier(table.getEntityName()) + "');";
    }

    @Override
    public String getAddColumnStatements(ColumnDef column)
    {
        return super.getAddColumnStatements(column) + getReorgTableStatements(column.getTable());
    }

    @Override
    public String normalizeIdentifierCase(String identifier)
    {
        return identifier == null ? null : identifier.toUpperCase();
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

    @Override
    public String getDropTableStatements(String table)
    {
        return "BEGIN DECLARE CONTINUE HANDLER FOR SQLSTATE '42704' BEGIN END; EXECUTE IMMEDIATE 'DROP TABLE " + normalizeIdentifier(table) + "'; END;\n";
    }

    @Override
    public String getAutoIncrementClause(ColumnDef column)
    {
        Long startId = null;
        try
        {
            startId = Long.parseLong(column.getTable().getStartId());
        }
        catch (Exception e)
        {
            // Ignore
        }
        if (startId != null)
            return "GENERATED BY DEFAULT AS IDENTITY (NO CACHE, START WITH " + startId + ")";
        return "GENERATED BY DEFAULT AS IDENTITY (NO CACHE)";
    }
}
