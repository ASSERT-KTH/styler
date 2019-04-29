package com.developmentontheedge.be5.metadata.serialization.yaml.deserializers;

import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.model.ColumnDef;
import com.developmentontheedge.be5.metadata.model.DataElementUtils;
import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.be5.metadata.model.IndexColumnDef;
import com.developmentontheedge.be5.metadata.model.IndexDef;
import com.developmentontheedge.be5.metadata.model.TableDef;
import com.developmentontheedge.be5.metadata.model.TableRef;
import com.developmentontheedge.be5.metadata.model.ViewDef;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;
import com.developmentontheedge.be5.metadata.serialization.Fields;
import com.developmentontheedge.be5.metadata.serialization.LoadContext;
import com.developmentontheedge.be5.metadata.serialization.SerializationConstants;
import com.developmentontheedge.be5.metadata.util.Strings2;
import one.util.streamex.StreamEx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_COLUMNS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_COMMENT;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_EXTRAS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_INDICES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_OLD_NAMES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_REFERENCE;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_REFERENCES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_SCHEME;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_VIEW_DEFINITION;

class SchemeDeserializer extends BaseDeserializer
{

    private YamlDeserializer yamlDeserializer;

    /**
     * Creates a scheme deserializer with empty path.
     */
    SchemeDeserializer(YamlDeserializer yamlDeserializer, LoadContext loadContext)
    {
        super(loadContext);
        this.yamlDeserializer = yamlDeserializer;
    }

    void deserialize(Map<String, Object> serializedEntityBody, Entity entity) throws ReadException
    {
        readScheme(serializedEntityBody, entity);
        readReferences(serializedEntityBody, entity);
    }

    private void readReferences(final Map<String, Object> serializedEntityBody, final Entity entity) throws ReadException
    {
        final Map<String, Object> serializedReferences;

        try
        {
            serializedReferences = asMapOrEmpty(serializedEntityBody.get(TAG_REFERENCES));
        }
        catch (ReadException e)
        {
            loadContext.addWarning(e.attachElement(entity));
            return;
        }

        for (final String name : serializedReferences.keySet())
        {
            final String columnFrom = name;
            final Object content = serializedReferences.get(name);

            final TableRefStructure tableRefStructure = toTableReference(content);

            if (tableRefStructure != null)
            {
                final BeModelCollection<TableRef> references = entity.getOrCreateTableReferences();
                final String tableRefName = TableRef.nameFor(columnFrom, tableRefStructure.tableTo);
                final TableRef tableRef = new TableRef(tableRefName, columnFrom, references);
                tableRef.setOriginModuleName(yamlDeserializer.project.getProjectOrigin());
                if (content instanceof Map)
                    readUsedExtras(asMap(content), tableRef);
                tableRefStructure.applyTo(tableRef);
                DataElementUtils.saveQuiet(tableRef);
            }
        }
    }

    private void readScheme(Map<String, Object> serializedEntityBody, Entity entity) throws ReadException
    {
        final Object serializedScheme = serializedEntityBody.get(TAG_SCHEME);

        if (!(serializedScheme instanceof Map))
            return;

        @SuppressWarnings("unchecked") final Map<String, Object> schemeContent = (Map<String, Object>) serializedScheme;

        readSchemeContent(entity, schemeContent);
    }

    private void readSchemeContent(Entity entity, final Map<String, Object> schemeContent) throws ReadException
    {
        if (schemeContent.get(TAG_VIEW_DEFINITION) instanceof String)
        {
            save(readViewDef(schemeContent, entity));
        }
        else
        {
            save(readTableDef(schemeContent, entity));
        }
    }

    TableDef readTableDef(Map<String, Object> tableContent, Entity entity) throws ReadException
    {
        final TableDef tableDef = new TableDef(entity);

        readDocumentation(tableContent, tableDef);
        readTableColumns(tableContent, tableDef);
        readTableIndices(tableContent, tableDef);
        readFields(tableDef, tableContent, Fields.tableDef());
        readUsedExtras(tableContent, tableDef);
        checkChildren(tableDef, tableContent, Fields.tableDef(), TAG_EXTRAS, TAG_COMMENT, TAG_INDICES, TAG_COLUMNS);

        return tableDef;
    }

    private void readTableIndices(Map<String, Object> tableContent, final TableDef tableDef)
    {
        final BeVectorCollection<IndexDef> indices = tableDef.getIndices();
        final List<Map<String, Object>> indicesList = asMaps(tableContent.get(TAG_INDICES));

        try
        {
            for (Map<String, Object> indexElement : indicesList)
            {
                for (Map.Entry<String, Object> indexPair : indexElement.entrySet()) // should be only one pair
                {
                    IndexDef index = readIndexDef(indexPair.getKey(), asMap(indexPair.getValue()), indices);
                    save(index);
                }
            }
        }
        catch (ReadException e)
        {
            loadContext.addWarning(e.attachElement(indices));
        }
    }

    private void readTableColumns(Map<String, Object> tableContent, final TableDef tableDef)
    {
        final BeVectorCollection<ColumnDef> columns = tableDef.getColumns();
        final List<Map<String, Object>> columnsList = asMaps(tableContent.get(TAG_COLUMNS));

        try
        {
            for (Map<String, Object> columnElement : columnsList)
            {
                for (Map.Entry<String, Object> columnPair : columnElement.entrySet()) // should be only one pair
                {
                    ColumnDef column = readColumnDef(columnPair.getKey(), asMap(columnPair.getValue()), columns);
                    save(column);
                }
            }
        }
        catch (ReadException e)
        {
            loadContext.addWarning(e.attachElement(columns));
        }
    }

    public ColumnDef readColumnDef(String columnName, Map<String, Object> columnElement, BeVectorCollection<ColumnDef> parent) throws ReadException
    {
        ColumnDef column = new ColumnDef(columnName, parent);
        readDocumentation(columnElement, column);
        readFields(column, columnElement, Fields.columnDef());
        readUsedExtras(columnElement, column);

        final Object serializedOldNames = columnElement.get(SerializationConstants.TAG_OLD_NAMES);

        if (serializedOldNames != null)
        {
            final List<String> oldNames = asStrList(serializedOldNames);
            column.setOldNames(oldNames.toArray(new String[oldNames.size()]));
        }

        column.setOriginModuleName(column.getProject().getProjectOrigin());

        final TableRefStructure tableRefStructure = toTableReference(columnElement.get(TAG_REFERENCE));

        if (tableRefStructure != null)
        {
            tableRefStructure.applyTo(column);
        }

        checkChildren(column, columnElement, Fields.columnDef(), TAG_COMMENT, TAG_EXTRAS, TAG_REFERENCE, TAG_OLD_NAMES);

        return column;
    }

    public IndexDef readIndexDef(String indexName, Map<String, Object> indexElement, BeVectorCollection<IndexDef> parent) throws ReadException
    {
        IndexDef index = new IndexDef(indexName, parent);
        index.setOriginModuleName(index.getProject().getProjectOrigin());
        readDocumentation(indexElement, index);
        readFields(index, indexElement, Fields.indexDef());
        readUsedExtras(indexElement, index);
        List<String> cols = asStrList(indexElement.get(TAG_COLUMNS));

        for (String indexColumnStr : cols)
        {
            DataElementUtils.saveQuiet(IndexColumnDef.createFromString(indexColumnStr, index));
        }

        checkChildren(index, indexElement, Fields.indexDef(), TAG_COMMENT, TAG_EXTRAS, TAG_COLUMNS);

        return index;
    }

    private ViewDef readViewDef(final Map<String, Object> schemeContent, final Entity entity)
    {
        final ViewDef viewDef = new ViewDef(entity);
        readDocumentation(schemeContent, viewDef);

        final String viewDefinition = (String) schemeContent.get(TAG_VIEW_DEFINITION);
        assert viewDefinition != null;

        viewDef.setDefinition(viewDefinition);
        readFields(viewDef, schemeContent, Fields.viewDef());
        checkChildren(viewDef, schemeContent, Fields.viewDef(), TAG_VIEW_DEFINITION, TAG_COMMENT, TAG_EXTRAS);

        return viewDef;
    }

    private TableRefStructure toTableReference(Object content)
    {
        TableRefStructure tableRef = new TableRefStructure();

        if (content instanceof String)
        {
            final String joined = (String) content;
            final List<String> splittedTo = StreamEx.split(joined, "\\.").toList();

            if (splittedTo.size() == 1)
            {
                final String tableTo = splittedTo.get(0);
                final String columnTo = "";
                tableRef.tableTo = tableTo;
                tableRef.columnTo = columnTo;

                return tableRef;
            }

            if (splittedTo.size() >= 2)
            {
                final String tableTo = splittedTo.get(0);
                final String columnTo = Strings2.joinTail(".", splittedTo);
                tableRef.tableTo = tableTo;
                tableRef.columnTo = columnTo;

                return tableRef;
            }
        }
        else if (content instanceof List)
        {
            final List<?> tablesRaw = (List<?>) content;
            final List<String> tables2 = new ArrayList<>();

            for (Object tableRaw : tablesRaw)
            {
                tables2.add(String.valueOf(tableRaw));
            }

            tableRef.permittedTables = tables2.toArray(new String[tables2.size()]);

            return tableRef;
        }
        else if (content instanceof Map)
        {
            final Map<?, ?> map = (Map<?, ?>) content;
            final Object view = map.get("view");
            final Object to = map.get("to");

            if (view != null)
            {
                tableRef.view = String.valueOf(view);
            }

            if (to instanceof String)
            {
                final String joined = (String) to;
                final List<String> splittedTo = StreamEx.split(joined, "\\.").toList();

                if (splittedTo.size() == 1)
                {
                    final String tableTo = splittedTo.get(0);
                    final String columnTo = "";
                    tableRef.tableTo = tableTo;
                    tableRef.columnTo = columnTo;

                    return tableRef;
                }

                if (splittedTo.size() >= 2)
                {
                    final String tableTo = splittedTo.get(0);
                    final String columnTo = Strings2.joinTail(".", splittedTo);
                    tableRef.tableTo = tableTo;
                    tableRef.columnTo = columnTo;

                    return tableRef;
                }
            }
            else
            {
                final List<?> tablesRaw = (to instanceof List) ? (List<?>) to : new ArrayList<>();
                final List<String> tables2 = new ArrayList<>();

                for (Object tableRaw : tablesRaw)
                {
                    tables2.add(String.valueOf(tableRaw));
                }

                tableRef.permittedTables = tables2.toArray(new String[tables2.size()]);

                return tableRef;
            }
        }

        return null;
    }

}
