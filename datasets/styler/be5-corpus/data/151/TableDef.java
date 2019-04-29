package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.MetadataUtils;
import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.model.base.BeCaseInsensitiveCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;
import com.developmentontheedge.be5.metadata.sql.Rdbms;
import com.developmentontheedge.be5.metadata.sql.type.DbmsTypeManager;
import com.developmentontheedge.be5.metadata.util.Strings2;
import com.developmentontheedge.beans.annot.PropertyName;
import com.developmentontheedge.dbms.ExtendedSqlException;
import com.developmentontheedge.dbms.SqlExecutor;
import one.util.streamex.MoreCollectors;
import one.util.streamex.StreamEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.developmentontheedge.be5.metadata.model.SqlColumnType.TYPE_BOOL;
import static com.developmentontheedge.be5.metadata.model.SqlColumnType.TYPE_CHAR;
import static com.developmentontheedge.be5.metadata.model.SqlColumnType.TYPE_DECIMAL;
import static com.developmentontheedge.be5.metadata.model.SqlColumnType.TYPE_ENUM;
import static com.developmentontheedge.be5.metadata.model.SqlColumnType.TYPE_UNKNOWN;
import static com.developmentontheedge.be5.metadata.model.SqlColumnType.TYPE_VARCHAR;

public class TableDef extends BeVectorCollection<BeModelElement> implements DdlElement
{
    public static final String COLUMN_COLLECTION = "Columns";
    public static final String INDEX_COLLECTION = "Indexes";

    private String startIdVariable = "";

    public TableDef(Entity entity)
    {
        super(Entity.SCHEME, BeModelElement.class, entity);
        DataElementUtils.saveQuiet(new BeCaseInsensitiveCollection<>(COLUMN_COLLECTION, ColumnDef.class, this, true).propagateCodeChange());
        DataElementUtils.saveQuiet(new BeCaseInsensitiveCollection<>(INDEX_COLLECTION, IndexDef.class, this, true).propagateCodeChange());
        propagateCodeChange();
    }

    public Entity getEntity()
    {
        return (Entity) getOrigin();
    }

    @Override
    public Module getModule()
    {
        return (Module) getOrigin().getOrigin().getOrigin();
    }

    public BeCaseInsensitiveCollection<ColumnDef> getColumns()
    {
        return (BeCaseInsensitiveCollection<ColumnDef>) getCollection(COLUMN_COLLECTION, ColumnDef.class);
    }

    public BeCaseInsensitiveCollection<IndexDef> getIndices()
    {
        return (BeCaseInsensitiveCollection<IndexDef>) getCollection(INDEX_COLLECTION, IndexDef.class);
    }

    public List<IndexDef> getIndicesUsingColumn(String columnName)
    {
        List<IndexDef> result = new ArrayList<>();
        for (IndexDef index : getIndices().getAvailableElements())
        {
            if (index.getCaseInsensitive(columnName) != null)
            {
                result.add(index);
            }
        }
        return result;
    }

    @PropertyName("Variable with starting autoincrement ID")
    public String getStartIdVariable()
    {
        return startIdVariable;
    }

    public void setStartIdVariable(String startIdVariable)
    {
        this.startIdVariable = Strings2.nullToEmpty(startIdVariable);
        fireCodeChanged();
    }

    public String getStartId()
    {
        return getProject().getVariableValue(getStartIdVariable());
    }

    public int getColumnsCount()
    {
        return getColumns().getSize();
    }

    public ColumnDef getAutoincrementColumn()
    {
        for (ColumnDef column : getColumns().getAvailableElements())
        {
            if (column.isAutoIncrement())
                return column;
        }
        return null;
    }

    @Override
    public String getDropDdl()
    {
        return getProject().getDatabaseSystem().getTypeManager().getDropTableStatements(getEntityName());
    }

    @Override
    public String getDdl()
    {
        return getDropDdl() + getCreateDdl();
    }

    public IndexDef renameIndex(String oldName, String newName)
    {
        IndexDef oldIndex = getIndices().getCaseInsensitive(oldName);
        IndexDef newIndex = (IndexDef) oldIndex.clone(oldIndex.getOrigin(), newName);
        getIndices().replace(oldIndex, newIndex);
        return newIndex;
    }

    public ColumnDef renameColumn(String oldName, String newName)
    {
        ColumnDef oldColumn = getColumns().getCaseInsensitive(oldName);
        if (oldColumn == null)
            throw new IllegalArgumentException("Column with name " + oldName + " not found");
        if (newName.equals(oldColumn.getName()))
            return oldColumn;
        ColumnDef collision = getColumns().getCaseInsensitive(newName);
        if (collision != null && collision != oldColumn)
            throw new IllegalArgumentException("Column with name " + newName + " already exists");
        ColumnDef newColumn = (ColumnDef) oldColumn.clone(oldColumn.getOrigin(), newName);
        // Update oldNames
        Set<String> oldNames = new LinkedHashSet<>(Arrays.asList(newColumn.getOldNames()));
        oldNames.removeIf(previousOldName -> previousOldName.equalsIgnoreCase(newName));
        oldNames.add(oldName);
        newColumn.setOldNames(oldNames.toArray(new String[0]));
        // Store
        getColumns().replace(oldColumn, newColumn);
        replaceColumnInIndices(oldColumn, newColumn);
        Project project = getProject();
        String currentEntity = getEntityName();
        for (String entityName : project.getEntityNames())
        {
            for (TableReference reference : project.getEntity(entityName).getAllReferences())
            {
                if (currentEntity.equalsIgnoreCase(reference.getTableTo())
                        && oldColumn.getName().equalsIgnoreCase(reference.getColumnsTo()))
                {
                    reference.setColumnsTo(newColumn.getName());
                }
            }
        }
        return newColumn;
    }

    @Override
    public String getCreateDdl()
    {
        StringBuilder sb = new StringBuilder();
        Project project = getProject();
        Rdbms dbms = project.getDatabaseSystem();
        DbmsTypeManager typeManager = dbms.getTypeManager();
        sb.append(typeManager.getCreateTableClause(getEntityName()) + " (\n");
        StringBuilder triggers = new StringBuilder();
        for (ColumnDef column : getColumns().getAvailableElements())
        {
            sb.append(typeManager.getColumnDefinitionClause(column));
            sb.append(",\n");
            triggers.append(typeManager.getColumnTriggerDefinition(column));
        }
        if (getColumnsCount() > 0)
        {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append(");\n");
        ColumnDef autoincrementColumn = getAutoincrementColumn();
        if (autoincrementColumn != null)
        {
            String value = getStartId();
            if (value != null)
                sb.append(typeManager.getStartingIncrementDefinition(getEntityName(), autoincrementColumn.getName(), Long.parseLong(value)));
        }
        sb.append(triggers);
        List<String> indexDdls = new ArrayList<>();
        for (IndexDef index : getIndices().getAvailableElements())
        {
            if (!typeManager.isFunctionalIndexSupported() && index.isFunctional())
                continue;
            if (dbms == Rdbms.DB2 && index.getName().length() > Rdbms.DB2_INDEX_LENGTH)
                continue;
            indexDdls.add(index.getCreateDdl() + "\n");
        }
        Collections.sort(indexDdls);
        sb.append(String.join("", indexDdls));
        return sb.toString();
    }

    @Override
    public String getDiffDdl(DdlElement other, SqlExecutor sql) throws ExtendedSqlException
    {
        if (other == null)
            return getCreateDdl();
        if (!(other instanceof TableDef))
            return other.getDropDdl() + getCreateDdl();
        TableDef def = (TableDef) ((TableDef) other).clone(other.getOrigin(), other.getName());
        Rdbms dbms = getProject().getDatabaseSystem();
        DbmsTypeManager typeManager = dbms.getTypeManager();
        String columnsDiff = getColumnsDiff(def, typeManager, false, sql);
        if (columnsDiff == null) // no columns match
            return getDdl();
        String indicesDiff = getIndicesDiff(def, dbms, typeManager);
        if (columnsDiff.isEmpty())
        {
            return indicesDiff;
        }
        if (getModule().getName().equals(getProject().getProjectOrigin()) && sql != null && sql.isEmpty(getEntityName()))
        {
            return def.getDropDdl() + getCreateDdl();
        }
        return columnsDiff + indicesDiff;
    }

    @Override
    public String getDangerousDiffStatements(DdlElement other, SqlExecutor sql) throws ExtendedSqlException
    {
        if (other == null || (sql != null && sql.isEmpty(getEntityName())))
        {
            return "";
        }
        if (!(other instanceof TableDef))
            return other.getDropDdl();
        TableDef def = (TableDef) ((TableDef) other).clone(other.getOrigin(), other.getName());
        DbmsTypeManager typeManager = getProject().getDatabaseSystem().getTypeManager();
        String diff = getColumnsDiff(def, typeManager, true, sql);
        if (diff == null)
            return getDropDdl();
        return diff;
    }

    private String getIndicesDiff(TableDef def, Rdbms dbms, DbmsTypeManager typeManager)
    {
        StringBuilder sb = new StringBuilder();
        Map<String, IndexDef> oldIndexNames = new HashMap<>();
        Map<String, IndexDef> preservedIndices = new HashMap<>();
        for (IndexDef oldIndex : def.getIndices().getAvailableElements())
        {
            if (!typeManager.isFunctionalIndexSupported() && oldIndex.isFunctional())
                continue;
            if (dbms == Rdbms.DB2 && oldIndex.getName().length() > Rdbms.DB2_INDEX_LENGTH)
                continue;
            oldIndexNames.put(typeManager.normalizeIdentifier(oldIndex.getName()), oldIndex);
        }
        for (IndexDef index : getIndices().getAvailableElements())
        {
            if (!typeManager.isFunctionalIndexSupported() && index.isFunctional())
                continue;
            if (!index.isValidIndex())
                continue;
            if (dbms == Rdbms.DB2 && index.getName().length() > Rdbms.DB2_INDEX_LENGTH)
                continue;
            String normalizedName = typeManager.normalizeIdentifier(index.getName());
            preservedIndices.put(normalizedName, oldIndexNames.remove(normalizedName));
        }
        for (IndexDef index : oldIndexNames.values())
        {
            sb.append(index.getDropDdl());
        }
        for (IndexDef index : getIndices().getAvailableElements())
        {
            String indexName = typeManager.normalizeIdentifier(index.getName());
            if (!preservedIndices.containsKey(indexName))
                continue;
            IndexDef oldIndex = preservedIndices.get(indexName);
            sb.append(index.getDiffDdl(oldIndex, null));
        }
        return sb.toString();
    }

    private String getColumnsDiff(TableDef def, DbmsTypeManager typeManager, boolean dangerousOnly, SqlExecutor sql) throws ExtendedSqlException
    {
        StringBuilder sb = new StringBuilder();
        Map<String, ColumnDef> oldColumnNames = new HashMap<>();
        Map<String, ColumnDef> preservedColumns = new HashMap<>();
        Map<String, ColumnDef> knownOldNames = new HashMap<>();
        boolean hasOldColumn = false;
        for (ColumnDef oldColumn : def.getColumns().getAvailableElements())
        {
            oldColumnNames.put(typeManager.normalizeIdentifier(oldColumn.getName()), oldColumn);
        }
        for (ColumnDef newColumn : getColumns().getAvailableElements())
        {
            String newName = newColumn.getName();
            String normalizedName = typeManager.normalizeIdentifier(newName);
            ColumnDef oldColumn = oldColumnNames.remove(normalizedName);
            if (oldColumn != null)
                hasOldColumn = true;
            preservedColumns.put(normalizedName, oldColumn);
            for (String oldName : newColumn.getOldNames())
            {
                knownOldNames.put(typeManager.normalizeIdentifier(oldName), newColumn);
            }
        }
        if (!hasOldColumn && !oldColumnNames.isEmpty())
            return null;

        getColumnsDiffFromOldColumns(def, typeManager, dangerousOnly, sb, oldColumnNames, preservedColumns, knownOldNames);

        getColumnsDiffFromCurrentColumns(typeManager, dangerousOnly, sql, sb, preservedColumns);
        // TODO: preserve column order
        // TODO: support alter column for types
        return sb.toString();
    }

    private void getColumnsDiffFromOldColumns(TableDef def, DbmsTypeManager typeManager, boolean dangerousOnly, StringBuilder sb, Map<String, ColumnDef> oldColumnNames, Map<String, ColumnDef> preservedColumns, Map<String, ColumnDef> knownOldNames)
    {
        for (ColumnDef oldColumn : oldColumnNames.values())
        {
            String normalizedName = typeManager.normalizeIdentifier(oldColumn.getName());
            ColumnDef renamedColumn = knownOldNames.get(normalizedName);
            if (renamedColumn != null)
            {
                String normalizedNewName = typeManager.normalizeIdentifier(renamedColumn.getName());
                ColumnDef currentlyMappedColumn = preservedColumns.get(normalizedNewName);
                if (currentlyMappedColumn == null)
                {
                    if (!dangerousOnly)
                    {
                        sb.append(typeManager.getDropTriggerDefinition(oldColumn));
                        sb.append(typeManager.getRenameColumnStatements(oldColumn, renamedColumn.getName()));
                        sb.append(typeManager.getColumnTriggerDefinition(renamedColumn));
                    }
                    preservedColumns.put(normalizedNewName, oldColumn);
                    // Indices will be changed automatically by DB, so we must synchronize old schema with these changes
                    def.replaceColumnInIndices(oldColumn, renamedColumn);
                    continue;
                }
            }
            addDropColumnStatements(typeManager, oldColumn, sb, dangerousOnly);
        }
    }

    private void getColumnsDiffFromCurrentColumns(DbmsTypeManager typeManager, boolean dangerousOnly, SqlExecutor sql, StringBuilder sb, Map<String, ColumnDef> preservedColumns) throws ExtendedSqlException
    {
        for (ColumnDef column : getColumns().getAvailableElements())
        {
            String columnName = typeManager.normalizeIdentifier(column.getName());
            String columnDef = typeManager.getColumnDefinitionClause(column);
            ColumnDef oldColumn = preservedColumns.get(columnName);
            if (oldColumn != null)
            {
                ColumnDef oldColumnNewName = column.getName().equals(oldColumn.getName()) ? oldColumn : (ColumnDef) oldColumn.clone(
                        oldColumn.getOrigin(), column.getName());
                String oldColumnDef = typeManager.getColumnDefinitionClause(oldColumnNewName);
                String oldTriggers = typeManager.getColumnTriggerDefinition(oldColumnNewName);
                String newTriggers = typeManager.getColumnTriggerDefinition(column);
                if (!oldTriggers.equals(newTriggers))
                {
                    sb.append(typeManager.getDropTriggerDefinition(oldColumnNewName));
                    sb.append(typeManager.getColumnTriggerDefinition(column));
                }
                if (oldColumnDef.equals(columnDef))
                    continue;
                if (getProject().getDebugStream() != null)
                {
                    getProject().getDebugStream().println("Table " + getEntityName() + ": column " + column.getName() + " differs in DDL");
                    getProject().getDebugStream().println("- old: " + oldColumnDef);
                    getProject().getDebugStream().println("- new: " + columnDef);
                }
                if (isSafeTypeUpdate(oldColumn, column, typeManager, sql) && oldColumn.isPrimaryKey() == column.isPrimaryKey() && oldColumn.isAutoIncrement() == column.isAutoIncrement())
                {
                    if (!dangerousOnly)
                        sb.append(typeManager.getAlterColumnStatements(column, oldColumnNewName));
                    continue;
                }
                addDropColumnStatements(typeManager, oldColumnNewName, sb, dangerousOnly);
            }
            if (column.getDefaultValue() == null && !column.isCanBeNull() && !column.isAutoIncrement())
            {
                ColumnDef columnWithDefault = (ColumnDef) column.clone(column.getOrigin(), column.getName());
                if (column.getType().doesSupportGeneratedKey())
                    columnWithDefault.setDefaultValue("0");
                else if (column.getType().isDateTime())
                    columnWithDefault.setDefaultValue("'1970/01/01 00:00:00'");
                else
                    columnWithDefault.setDefaultValue("''");
                sb.append(typeManager.getAddColumnStatements(columnWithDefault));
                if (!dangerousOnly)
                {
                    sb.append(typeManager.getAlterColumnStatements(column, columnWithDefault));
                }
            }
            else
            {
                if (!dangerousOnly)
                {
                    sb.append(typeManager.getAddColumnStatements(column));
                    sb.append(typeManager.getColumnTriggerDefinition(column));
                }
            }
        }
    }

    private void replaceColumnInIndices(ColumnDef oldColumn, ColumnDef newColumn)
    {
        for (IndexDef oldIndex : getIndices())
        {
            for (IndexColumnDef col : oldIndex)
            {
                if (col.getName().equalsIgnoreCase(oldColumn.getName()))
                {
                    oldIndex.replace(col, col.clone(oldIndex, newColumn.getName()));
                }
            }
        }
    }

    private void addDropColumnStatements(DbmsTypeManager typeManager, ColumnDef oldColumn, StringBuilder sb, boolean dangerousOnly)
    {
        if (!dangerousOnly)
        {
            sb.append(typeManager.getDropTriggerDefinition(oldColumn));
        }
        for (IndexDef index : oldColumn.getTable().getIndicesUsingColumn(oldColumn.getName()))
        {
            if (!dangerousOnly)
            {
                sb.append(typeManager.getDropIndexClause(index.getName(), oldColumn.getEntity().getName())).append(";\n");
            }
            DataElementUtils.remove(index);
        }
        sb.append(typeManager.getDropColumnStatements(oldColumn));
    }

    private boolean isSafeTypeUpdate(ColumnDef oldColumn, ColumnDef column, DbmsTypeManager typeManager, SqlExecutor sql) throws ExtendedSqlException
    {
        SqlColumnType oldType = oldColumn.getType();
        SqlColumnType type = column.getType();
        if (typeManager.getTypeClause(oldType).equals(typeManager.getTypeClause(type)))
            return true;

        // Enlarging VARCHAR column
        if (oldType.getTypeName().equals(TYPE_VARCHAR) && type.getTypeName().equals(TYPE_VARCHAR))
        {
            if (type.getSize() >= oldType.getSize())
                return true;
            return sql != null && !sql.hasResult("sql.select.longer", typeManager.normalizeIdentifier(getEntityName()),
                    typeManager.normalizeIdentifier(oldColumn.getName()), type.getSize());
        }

        // Enlarging DECIMAL column
        if (oldType.getTypeName().equals(TYPE_DECIMAL) && type.getTypeName().equals(TYPE_DECIMAL))
        {
            if (type.getSize() >= oldType.getSize() && type.getPrecision() >= oldType.getPrecision())
                return true;

        }
        // Adding new variants for ENUM column
        if ((oldType.getTypeName().equals(TYPE_ENUM) || oldType.getTypeName().equals(TYPE_BOOL) || oldType.getTypeName().equals(TYPE_VARCHAR)) &&
                (type.getTypeName().equals(TYPE_ENUM) || type.getTypeName().equals(TYPE_BOOL)))
        {
            List<String> newValues = Arrays.asList(type.getEnumValues());
            if (!oldType.getTypeName().equals(TYPE_VARCHAR) && newValues.containsAll(Arrays.asList(oldType.getEnumValues())))
                return true;
            return sql != null && !sql.hasResult("sql.select.not.in.range", typeManager.normalizeIdentifier(getEntityName()),
                    typeManager.normalizeIdentifier(oldColumn.getName()), MetadataUtils.toInClause(newValues));
        }

        // Changing ENUM to varchar
        if ((oldType.getTypeName().equals(TYPE_ENUM) || oldType.getTypeName().equals(TYPE_BOOL))
                && type.getTypeName().equals(TYPE_VARCHAR))
        {
            int len = 0;
            for (String value : oldType.getEnumValues())
            {
                len = Math.max(len, value.length());
            }
            if (type.getSize() >= len)
                return true;
            return sql != null && !sql.hasResult("sql.select.longer", typeManager.normalizeIdentifier(getEntityName()),
                    typeManager.normalizeIdentifier(oldColumn.getName()), type.getSize());
        }

        // Changing ENUM to char
        if (oldType.getTypeName().equals(TYPE_ENUM) && type.getTypeName().equals(TYPE_CHAR))
        {
            return StreamEx.of(oldType.getEnumValues()).map(String::length).distinct().collect(MoreCollectors.onlyOne())
                    .filter(len -> type.getSize() == len).isPresent();
        }
        return false;
    }

    @Override
    public List<ProjectElementException> getErrors()
    {
        List<ProjectElementException> errors = super.getErrors();
        if (isFromApplication() && getColumnsCount() == 0)
        {
            errors.add(new ProjectElementException(this, "Table must have at least one column"));
        }
        String startValue = null;
        try
        {
            startValue = getStartId();
        }
        catch (Exception e1)
        {
            errors.add(new ProjectElementException(getCompletePath(), "startIdVariable", "Unable to calculate start value"));
        }
        if (startValue != null)
        {
            try
            {
                Long.parseLong(startValue);
            }
            catch (NumberFormatException e)
            {
                errors.add(new ProjectElementException(getCompletePath(), "startIdVariable", "Invalid start id value (must be number): "
                        + startValue));
            }
        }
        return errors;
    }

    @Override
    public boolean hasErrors()
    {
        return !getErrors().isEmpty();
    }

    @Override
    public String getEntityName()
    {
        return getEntity().getName();
    }

    /**
     * Returns a sorted set of names.
     */
    public static Set<String> getEntityNames(Iterable<TableDef> tableDefinitions)
    {
        final Set<String> names = new TreeSet<>();

        for (TableDef tableDef : tableDefinitions)
            names.add(tableDef.getEntityName());

        return names;
    }

    public static Map<String, TableDef> toMap(Iterable<TableDef> tableDefinitions)
    {
        final Map<String, TableDef> map = new HashMap<>();

        for (TableDef tableDefinition : tableDefinitions)
            map.put(tableDefinition.getEntityName(), tableDefinition);

        return map;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return debugEquals("null");
        if (getClass() != obj.getClass())
            return debugEquals("class");
        TableDef other = (TableDef) obj;
        // TODO: make this without modification of the objects
        String var = getStartIdVariable();
        setStartIdVariable(null);
        String ddl = getDdl();
        setStartIdVariable(var);
        String otherVar = other.getStartIdVariable();
        other.setStartIdVariable(null);
        String otherDdl = other.getDdl();
        other.setStartIdVariable(otherVar);
        try
        {
            if (!ddl.equals(otherDdl) && !getDiffDdl(other, null).isEmpty())
                return debugEquals("ddl: old = " + otherDdl.replaceAll("\n", System.lineSeparator()));
        }
        catch (ExtendedSqlException e)
        {
            throw new InternalError("Unexpected " + e);
        }
        return true;
    }

    /**
     * Searches a column ignoring case.
     */
    public ColumnDef findColumn(String name)
    {
        return getColumns().stream().findFirst(column -> column.getName().equalsIgnoreCase(name)).orElse(null);
    }

    @Override
    public List<ProjectElementException> getWarnings()
    {
        ArrayList<ProjectElementException> warnings = new ArrayList<>();
        Rdbms rdbms = getProject().getDatabaseSystem();
        DbmsTypeManager typeManager = rdbms.getTypeManager();
        if (!typeManager.isFunctionalIndexSupported())
        {
            for (IndexDef index : getIndices().getAvailableElements())
            {
                if (index.isFunctional())
                {
                    warnings.add(new ProjectElementException(index, "Functional indices are not supported by " + rdbms));
                }
            }
        }
        if (rdbms == Rdbms.DB2)
        {
            for (IndexDef index : getIndices().getAvailableElements())
            {
                if (index.getName().length() > Rdbms.DB2_INDEX_LENGTH)
                {
                    warnings.add(new ProjectElementException(index, "Index name too long for DB2 (" + index.getName().length() + ">"
                            + Rdbms.DB2_INDEX_LENGTH + "); will be skipped"));
                }
            }
        }
        if (!typeManager.isCustomAutoincrementSupported())
        {
            ColumnDef autoincrementColumn = getAutoincrementColumn();
            if (autoincrementColumn != null && getStartId() != null)
            {
                warnings.add(new ProjectElementException(autoincrementColumn, "Custom starting autoincrement value is not supported by "
                        + rdbms));
            }
        }
        for (ColumnDef def : getColumns().getAvailableElements())
        {
            if (def.getType().getTypeName().equals(TYPE_UNKNOWN))
            {
                warnings.add(new ProjectElementException(def, "Cannot determine column type: probably it references to invalid table or column"));
            }
        }
        for (IndexDef def : getIndices().getAvailableElements())
        {
            for (IndexColumnDef col : def)
            {
                ColumnDef column = getColumns().getCaseInsensitive(col.getName());
                if (column == null)
                    warnings.add(new ProjectElementException(col, "Index refers to unknown column"));
                else if (!column.isAvailable())
                    warnings.add(new ProjectElementException(col, "Index refers to non-available column"));
            }
        }
        return warnings;
    }
}
