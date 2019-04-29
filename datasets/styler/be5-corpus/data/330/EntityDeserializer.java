package com.developmentontheedge.be5.metadata.serialization.yaml.deserializers;

import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.model.DataElementUtils;
import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.be5.metadata.model.EntityItem;
import com.developmentontheedge.be5.metadata.model.EntityType;
import com.developmentontheedge.be5.metadata.model.GroovyOperationExtender;
import com.developmentontheedge.be5.metadata.model.JavaScriptOperationExtender;
import com.developmentontheedge.be5.metadata.model.Module;
import com.developmentontheedge.be5.metadata.model.Operation;
import com.developmentontheedge.be5.metadata.model.OperationExtender;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.metadata.model.QuerySettings;
import com.developmentontheedge.be5.metadata.model.QuickFilter;
import com.developmentontheedge.be5.metadata.model.SourceFile;
import com.developmentontheedge.be5.metadata.model.SourceFileOperation;
import com.developmentontheedge.be5.metadata.model.SourceFileOperationExtender;
import com.developmentontheedge.be5.metadata.model.SpecialRoleGroup;
import com.developmentontheedge.be5.metadata.serialization.Fields;
import com.developmentontheedge.be5.metadata.serialization.LoadContext;
import com.developmentontheedge.be5.metadata.util.Strings2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.developmentontheedge.be5.metadata.MetadataUtils.classPathToFileName;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_CLASS_NAME;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_ENTITY_TEMPLATE;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_ENTITY_TYPE;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_FILEPATH;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_ICON;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_OPERATION_TYPE;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_QUERY_CODE;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_QUERY_OPERATIONS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_ROLES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_CODE;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_COMMENT;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_CUSTOMIZATIONS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_EXTRAS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_REFERENCES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_SETTINGS;

class EntityDeserializer extends FileDeserializer
{

    private YamlDeserializer yamlDeserializer;
    private final String name;
    private final Module module;
    private Entity result;

    EntityDeserializer(YamlDeserializer yamlDeserializer, LoadContext loadContext)
    {
        super(loadContext);
        this.yamlDeserializer = yamlDeserializer;
        name = null;
        module = null;
    }

    EntityDeserializer(YamlDeserializer yamlDeserializer, LoadContext loadContext, Module module, String name) throws ReadException
    {
        super(loadContext, yamlDeserializer.getFileSystem().getEntityFile(module.getName(), name));
        this.yamlDeserializer = yamlDeserializer;
        this.name = name;
        this.module = module;
    }

    /**
     * Can throw a {@link RuntimeException} with {@link ReadException} as its cause.
     *
     * @throws ReadException
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void doDeserialize(Object serializedRoot) throws ReadException
    {
        if (!(serializedRoot instanceof Map))
        {
            throw new ReadException(path, "Expected YAML map on the top level");
        }

        final Map<String, Object> serialized = (Map<String, Object>) serializedRoot;

        if (!(serialized.containsKey(name)))
        {
            throw new ReadException(path, "YAML map should start with entity name '" + name + "', found instead: " + serialized.keySet());
        }

        final Map<String, Object> entityContent = (Map<String, Object>) serialized.get(name);
        this.result = readEntity(name, entityContent, module);
    }

    Entity readEntity(final String name, final Map<String, Object> entityContent, final Module module) throws ReadException
    {
        Entity entity = new Entity(name, module, null);
        final boolean isFromApp = module == module.getProject().getApplication();

        String template = (String) entityContent.get(ATTR_ENTITY_TEMPLATE);
        Entity templateEntity = null;
        if (template != null)
        {
            templateEntity = yamlDeserializer.getTemplates().getEntity(template);
            if (templateEntity == null)
            {
                loadContext.addWarning(new ReadException(entity, path, "Unknown template name specified: " + template));
            }
            else if (!isFromApp)
            {
                loadContext.addWarning(new ReadException(entity, path, "Cannot use template with non-application entity"));
                templateEntity = null;
            }
        }

        final String type = (String) entityContent.get(ATTR_ENTITY_TYPE);
        if (type == null)
        {
            if (isFromApp && templateEntity == null)
            {
                loadContext.addWarning(new ReadException(entity, path, "Entity has no type"));
            }
        }
        else
        {
            EntityType entityType = EntityType.forSqlName(type);
            if (entityType == null)
            {
                loadContext.addWarning(new ReadException(entity, path, "Entity type is invalid: " + type));
            }
            else
            {
                entity.setType(entityType);
            }
        }

        if (yamlDeserializer.fileSystem != null)
        {
            entity.setLinkedFile(yamlDeserializer.getFileSystem().getEntityFile(module.getName(), entity.getName()));
        }

        readDocumentation(entityContent, entity);
        readUsedExtras(entityContent, entity);

        readFields(entity, entityContent, Fields.entity());
        yamlDeserializer.readCustomizations(entityContent, entity, false);
        readIcon(entityContent, entity.getIcon());

        new SchemeDeserializer(yamlDeserializer, loadContext).deserialize(entityContent, entity);

        final List<Map<String, Object>> operationsList = asMaps(entityContent.get("operations"));

        for (Map<String, Object> operationElement : operationsList)
        {
            for (Map.Entry<String, Object> operationPair : operationElement.entrySet()) // should have only one element
            {
                try
                {
                    save(readOperation(operationPair.getKey(), asMap(operationPair.getValue()), entity));
                }
                catch (ReadException e)
                {
                    Operation operation = Operation.createOperation(operationPair.getKey(), Operation.OPERATION_TYPE_JAVA, entity);
                    save(operation);
                    loadContext.addWarning(e.attachElement(operation));
                }
            }
        }

        readQueries(entity, asMaps(entityContent.get("queries")));

        checkChildren(entity, entityContent, Fields.entity(), "type", TAG_COMMENT, TAG_EXTRAS, TAG_CUSTOMIZATIONS, ATTR_ICON,
                "operations", "queries", "scheme", TAG_REFERENCES, ATTR_ENTITY_TEMPLATE);

        if (templateEntity != null)
        {
            entity.merge(templateEntity, false, !yamlDeserializer.fuseTemplate);
        }

        return entity;
    }

    private void readQueries(Entity entity, final List<Map<String, Object>> queriesList)
    {
        for (Map<String, Object> queryElement : queriesList)
        {
            for (Map.Entry<String, Object> queryPair : queryElement.entrySet()) // should have only one element
            {
                try
                {
                    if (queryPair.getKey().equals(Query.SPECIAL_TABLE_DEFINITION)
                            || queryPair.getKey().equals(Query.SPECIAL_LOST_RECORDS))
                    {
                        loadContext.addWarning(new ReadException(
                                entity.getQueries().getCompletePath().getChildPath(queryPair.getKey()), path, "Illegal query name: '"
                                + queryPair.getKey()
                                + "'. Such query is managed by BE automatically and should not appear in metadata."));
                        continue;
                    }
                    save(readQuery(queryPair.getKey(), asMap(queryPair.getValue()), entity));
                }
                catch (ReadException e)
                {
                    Query query = new Query(queryPair.getKey(), entity);
                    save(query);
                    loadContext.addWarning(e.attachElement(query));
                }
            }
        }
    }

    public Entity getEntity()
    {
        return result;
    }

    public Operation readOperation(final String name, final Map<String, Object> operationElement, final Entity entity) throws ReadException
    {
        final Operation operation = Operation.createOperation(name, (String) operationElement.get(ATTR_OPERATION_TYPE), entity);
        readDocumentation(operationElement, operation);

        readFields(operation, operationElement, Fields.operation());
        readUsedExtras(operationElement, operation);
        yamlDeserializer.readCustomizations(operationElement, operation, false);
        readIcon(operationElement, operation.getIcon());

        operation.setOriginModuleName(getProjectOrigin());

        if (operation instanceof SourceFileOperation)
        {
            if (operationElement.containsKey(ATTR_FILEPATH))
            {
                final SourceFileOperation fileOperation = (SourceFileOperation) operation;
                final String filepath = classPathToFileName((String) operationElement.get(ATTR_FILEPATH), fileOperation.getFileExtension());
                final String nameSpace = fileOperation.getFileNameSpace();
                SourceFile sourceFile = yamlDeserializer.project.getApplication().getSourceFile(nameSpace, filepath);
                if (sourceFile == null)
                {
                    sourceFile = yamlDeserializer.project.getApplication().addSourceFile(nameSpace, filepath);
                    sourceFile.setLinkedFile(yamlDeserializer.getFileSystem().getNameSpaceFile(nameSpace, filepath));
                }
                fileOperation.setFileName(sourceFile.getName());
                fileOperation.customizeProperty("code");
            }
            else
            {
                if (operation.getType().equals(Operation.OPERATION_TYPE_GROOVY))
                {
                    throw new ReadException(path, "Groovy operation required 'file' attribute.");
                }
            }
        }
        else
        {
            String text = (String) operationElement.get(TAG_CODE);
            if (!Strings2.isNullOrEmpty(text))
            {
                operation.setCode(text);
                operation.customizeProperty("code");
            }
            else
            {
                if (operation.getType().equals(Operation.OPERATION_TYPE_JAVA) &&
                        operationElement.containsKey(ATTR_FILEPATH))
                {
                    throw new ReadException(path, "Java operation required 'code' instead 'file' attribute.");
                }
            }
        }

        readRoles(operationElement, operation);
        readExtenders(operationElement, operation);
        checkChildren(operation, operationElement, Fields.operation(), TAG_CODE, ATTR_ICON, TAG_EXTRAS, TAG_CUSTOMIZATIONS, TAG_COMMENT, ATTR_FILEPATH, ATTR_ROLES, "extenders", "type");
        return operation;
    }

    private void readExtenders(final Map<String, Object> operationElement, final Operation operation)
    {
        final List<Map<String, Object>> extendersElement = asMaps(operationElement.get("extenders"));

        for (Map<String, Object> extenderElement : extendersElement)
        {
            final OperationExtender extender;

            if (extenderElement.containsKey(ATTR_CLASS_NAME))
            {
                extender = new OperationExtender(operation, getProjectOrigin());
                extender.setClassName((String) extenderElement.get(ATTR_CLASS_NAME));
            }
            else
            {
                final String filepath = (String) extenderElement.get(ATTR_FILEPATH);

                if (filepath == null)
                {
                    loadContext.addWarning(new ReadException(path, "Extender: no " + ATTR_FILEPATH + " attribute found").attachElement(operation));
                    continue;
                }

                if (filepath.endsWith(".js") || filepath.endsWith(".groovy"))
                {
                    SourceFileOperationExtender fileExtender;
                    if (filepath.endsWith(".js"))
                    {
                        fileExtender = new JavaScriptOperationExtender(operation, getProjectOrigin());
                    }
                    else
                    {
                        fileExtender = new GroovyOperationExtender(operation, getProjectOrigin());
                    }

                    String realFilePath = classPathToFileName(filepath, fileExtender.getFileExtension());
                    SourceFile sourceFile = yamlDeserializer.project.getApplication().getSourceFile(fileExtender.getNamespace(), realFilePath);
                    if (sourceFile == null)
                    {
                        sourceFile = yamlDeserializer.project.getApplication().addSourceFile(fileExtender.getNamespace(), realFilePath);
                        sourceFile.setLinkedFile(yamlDeserializer.getFileSystem().getNameSpaceFile(fileExtender.getNamespace(), realFilePath));
                    }

                    fileExtender.setFileName(sourceFile.getName());
                    extender = fileExtender;
                }
                else
                {
                    loadContext.addWarning(new ReadException(path, "Not supported file extention.").attachElement(operation));
                    continue;
                }
            }
            readFields(extender, extenderElement, Fields.extender());
            DataElementUtils.saveQuiet(extender);
            checkChildren(extender, extenderElement, Fields.extender(), ATTR_FILEPATH, ATTR_CLASS_NAME);
        }
    }

    Query readQuery(final String name, final Map<String, Object> queryElement, final Entity entity) throws ReadException
    {
        final Query query = new Query(name, entity);
        readDocumentation(queryElement, query);

        readFields(query, queryElement, Fields.query());
        readUsedExtras(queryElement, query);
        yamlDeserializer.readCustomizations(queryElement, query, false);
        readIcon(queryElement, query.getIcon());

        query.setOriginModuleName(getProjectOrigin());

        String text;

        switch (query.getType())
        {
            case STATIC:
                text = (String) queryElement.get(ATTR_QUERY_CODE);
                break;

            case GROOVY:
                final String groovyFileName = (String) queryElement.get("file");
                // try to read 'code' if there's no 'file'
                if (groovyFileName == null)
                {
                    text = (String) queryElement.get(TAG_CODE);
                    query.setFileName(classPathToFileName(query.getName().replace(':', '_') + ".groovy", ".groovy"));
                }
                else
                {
                    text = yamlDeserializer.getFileSystem().readGroovyQuery(classPathToFileName(groovyFileName.replace(':', '_'), ".groovy"));
                    query.setFileName(groovyFileName);
                }
                break;

            case JAVASCRIPT:
                final String jsFileName = (String) queryElement.get("file");
                // try to read 'code' if there's no 'file'
                if (jsFileName == null)
                {
                    text = (String) queryElement.get(TAG_CODE);
                    query.setFileName(query.getName().replace(':', '_') + ".js");
                }
                else
                {
                    text = yamlDeserializer.getFileSystem().readJavaScriptQuery(jsFileName.replace(':', '_'));
                    query.setFileName(jsFileName);
                }
                break;

            default:
                text = (String) queryElement.get(TAG_CODE);
                break;
        }

        // setQuerySettings must be called before setQuery
        // as setQuerySettings causes Freemarker initialization if query is not empty
        // and Freemarker may initialize incorrectly when project is not completely loaded
        if (queryElement.containsKey("settings"))
        {
            query.setQuerySettings(readQuerySettings(queryElement, query));
        }

        if (text != null)
        {
            query.setQuery(text);
            query.customizeProperty("query");
        }

        readQuickFilters(queryElement, query);
        readRoles(queryElement, query);
        if (queryElement.containsKey(ATTR_QUERY_OPERATIONS))
        {
            query.getOperationNames().parseValues(yamlDeserializer.stringCache(readList(queryElement, ATTR_QUERY_OPERATIONS)));
            query.customizeProperty("operationNames");
        }
        checkChildren(query, queryElement, Fields.query(), TAG_EXTRAS, TAG_COMMENT, TAG_CUSTOMIZATIONS, ATTR_ICON, ATTR_QUERY_CODE, TAG_CODE,
                ATTR_QUERY_OPERATIONS, "quickFilters", ATTR_ROLES, TAG_SETTINGS, "file");
        return query;
    }

    private void readRoles(final Map<String, Object> element, final EntityItem item)
    {
        if (element.containsKey(ATTR_ROLES))
        {
            item.getRoles().parseRoles(yamlDeserializer.stringCache(readList(element, ATTR_ROLES)));
            item.customizeProperty("roles");
        }
    }

    private QuerySettings[] readQuerySettings(final Map<String, Object> queryElement, final Query query)
    {
        final Set<String> allRoles = Collections.singleton('@' + SpecialRoleGroup.ALL_ROLES_GROUP);
        final List<QuerySettings> result = new ArrayList<>();
        final List<Map<String, Object>> settingsList = asMaps(queryElement.get("settings"));

        try
        {
            for (Map<String, Object> settingsElement : settingsList)
            {
                for (Map.Entry<String, Object> settingsPair : settingsElement.entrySet()) // should be only one pair
                {
                    if (!(settingsPair.getKey().equals("settings"))) // incorrect
                        continue;

                    final Map<String, Object> settingsContent = asMap(settingsPair.getValue());
                    final QuerySettings settings = new QuerySettings(query);
                    readFields(settings, settingsContent, Fields.querySettings());
                    final List<String> roles = yamlDeserializer.stringCache(readList(settingsContent, ATTR_ROLES));

                    if (roles.isEmpty())
                    {
                        settings.getRoles().parseRoles(allRoles);
                    }
                    else
                    {
                        settings.getRoles().parseRoles(roles);
                    }

                    result.add(settings);
                }
            }
        }
        catch (ReadException e)
        {
            loadContext.addWarning(e.attachElement(query));
        }

        return result.toArray(new QuerySettings[result.size()]);
    }

    private void readQuickFilters(final Map<String, Object> queryElement, final Query query)
    {
        Map<String, Object> filterElements;
        try
        {
            filterElements = asMapOrEmpty(queryElement.get("quickFilters"));
        }
        catch (ReadException e)
        {
            loadContext.addWarning(e.attachElement(query));
            return;
        }

        for (final Map.Entry<String, Object> filterElement : filterElements.entrySet())
        {
            final QuickFilter filter = new QuickFilter(filterElement.getKey(), query);
            try
            {
                readFields(filter, asMap(filterElement.getValue()), Fields.quickFilter());
            }
            catch (ReadException e)
            {
                loadContext.addWarning(e.attachElement(filter));
            }
            filter.setOriginModuleName(getProjectOrigin());
            DataElementUtils.saveQuiet(filter);
        }
    }

    private String getProjectOrigin()
    {
        return (yamlDeserializer.project == null ? module.getProject() : yamlDeserializer.project).getProjectOrigin();
    }

}
