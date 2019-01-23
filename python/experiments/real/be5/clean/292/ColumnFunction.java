package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.sql.Rdbms;

public class ColumnFunction
{
    public static final String TRANSFORM_NONE = "none";
    public static final String TRANSFORM_UPPER = "upper";
    public static final String TRANSFORM_LOWER = "lower";
    public static final String TRANSFORM_GENERIC = "generic";
    public static final String[] TRANSFORMS = new String[]{TRANSFORM_NONE, TRANSFORM_UPPER, TRANSFORM_LOWER, TRANSFORM_GENERIC};

    private final String columnName;
    private final String transform;

    public ColumnFunction(String definition)
    {
        String transform = TRANSFORM_NONE;
        String columnName = definition == null ? "" : definition;

        if (columnName.startsWith("upper(") && columnName.endsWith(")"))
        {
            transform = TRANSFORM_UPPER;
            columnName = columnName.substring("upper(".length(), columnName.length() - ")".length());
        }
        else if (columnName.startsWith("lower(") && columnName.endsWith(")"))
        {
            transform = TRANSFORM_LOWER;
            columnName = columnName.substring("lower(".length(), columnName.length() - ")".length());
        }
        else if (columnName.startsWith("generic(") && columnName.endsWith(")"))
        {
            transform = TRANSFORM_GENERIC;
            columnName = columnName.substring("generic(".length(), columnName.length() - ")".length());
        }
        this.columnName = columnName;
        this.transform = transform;
    }

    public ColumnFunction(String columnName, String transform)
    {
        this.columnName = columnName;
        this.transform = transform;
    }

    public String getColumnName()
    {
        return columnName;
    }

    public String getTransform()
    {
        return transform;
    }

    public boolean isTransformed()
    {
        return !getTransform().equals(TRANSFORM_NONE);
    }

    public String getDefinition(Rdbms databaseSystem, String entity)
    {
        String definition = databaseSystem.getTypeManager().normalizeIdentifier(getColumnName());
        if (ColumnFunction.TRANSFORM_GENERIC.equals(getTransform()))
        {
            definition = databaseSystem.getMacroProcessorStrategy().genericRefLowLevel(entity, definition);
        }
        else if (ColumnFunction.TRANSFORM_UPPER.equals(getTransform()))
        {
            definition = databaseSystem.getMacroProcessorStrategy().upper(definition);
        }
        else if (ColumnFunction.TRANSFORM_LOWER.equals(getTransform()))
        {
            definition = databaseSystem.getMacroProcessorStrategy().lower(definition);
        }
        return definition;
    }

    @Override
    public String toString()
    {
        String definition = getColumnName();
        if (isTransformed())
        {
            definition = getTransform() + "(" + definition + ")";
        }
        return definition;
    }
}
