package com.developmentontheedge.be5.metadata.serialization.yaml;

import com.developmentontheedge.be5.metadata.DatabaseConstants;
import com.developmentontheedge.be5.metadata.QueryType;
import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.exception.WriteException;
import com.developmentontheedge.be5.metadata.model.BeConnectionProfile;
import com.developmentontheedge.be5.metadata.model.BeConnectionProfileType;
import com.developmentontheedge.be5.metadata.model.BeConnectionProfiles;
import com.developmentontheedge.be5.metadata.model.BeConnectionProfilesRoot;
import com.developmentontheedge.be5.metadata.model.ColumnDef;
import com.developmentontheedge.be5.metadata.model.Daemon;
import com.developmentontheedge.be5.metadata.model.DdlElement;
import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.be5.metadata.model.EntityItem;
import com.developmentontheedge.be5.metadata.model.EntityLocalizations;
import com.developmentontheedge.be5.metadata.model.FreemarkerCatalog;
import com.developmentontheedge.be5.metadata.model.FreemarkerScript;
import com.developmentontheedge.be5.metadata.model.Icon;
import com.developmentontheedge.be5.metadata.model.IndexColumnDef;
import com.developmentontheedge.be5.metadata.model.IndexDef;
import com.developmentontheedge.be5.metadata.model.JavaScriptForm;
import com.developmentontheedge.be5.metadata.model.JavaScriptForms;
import com.developmentontheedge.be5.metadata.model.LanguageLocalizations;
import com.developmentontheedge.be5.metadata.model.LanguageStaticPages;
import com.developmentontheedge.be5.metadata.model.LocalizationElement;
import com.developmentontheedge.be5.metadata.model.Localizations;
import com.developmentontheedge.be5.metadata.model.MassChange;
import com.developmentontheedge.be5.metadata.model.Module;
import com.developmentontheedge.be5.metadata.model.Operation;
import com.developmentontheedge.be5.metadata.model.OperationExtender;
import com.developmentontheedge.be5.metadata.model.OperationSet;
import com.developmentontheedge.be5.metadata.model.PageCustomization;
import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.model.ProjectFileStructure;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.metadata.model.QuerySettings;
import com.developmentontheedge.be5.metadata.model.QuickFilter;
import com.developmentontheedge.be5.metadata.model.Role;
import com.developmentontheedge.be5.metadata.model.RoleGroup;
import com.developmentontheedge.be5.metadata.model.RoleSet;
import com.developmentontheedge.be5.metadata.model.SecurityCollection;
import com.developmentontheedge.be5.metadata.model.SourceFile;
import com.developmentontheedge.be5.metadata.model.SourceFileCollection;
import com.developmentontheedge.be5.metadata.model.SourceFileOperation;
import com.developmentontheedge.be5.metadata.model.SourceFileOperationExtender;
import com.developmentontheedge.be5.metadata.model.StaticPage;
import com.developmentontheedge.be5.metadata.model.StaticPages;
import com.developmentontheedge.be5.metadata.model.TableDef;
import com.developmentontheedge.be5.metadata.model.TableRef;
import com.developmentontheedge.be5.metadata.model.TableReference;
import com.developmentontheedge.be5.metadata.model.Templates;
import com.developmentontheedge.be5.metadata.model.ViewDef;
import com.developmentontheedge.be5.metadata.model.base.BeElementWithProperties;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;
import com.developmentontheedge.be5.metadata.serialization.Field;
import com.developmentontheedge.be5.metadata.serialization.Fields;
import com.developmentontheedge.be5.metadata.serialization.ProjectFileSystem;
import com.developmentontheedge.be5.metadata.util.Strings2;
import com.developmentontheedge.beans.util.Beans;
import one.util.streamex.StreamEx;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_CLASS_NAME;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_ENTITY_TEMPLATE;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_ENTITY_TYPE;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_FEATURES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_FILEPATH;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_ICON;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_LOCALIZATIONS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_LOCALIZATION_TOPICS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_MODULE_PROJECT;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_NAME;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_OPERATION_TYPE;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_QUERY_CODE;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_QUERY_OPERATIONS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_ROLES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_APPLICATION;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_BUGTRACKERS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_CODE;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_COLUMNS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_COMMENT;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_CONNECTION_PROFILES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_CONNECTION_PROFILES_INNER;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_CUSTOMIZATIONS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_DAEMONS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_ENTITIES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_EXTRAS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_INDICES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_JS_FORMS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_LOCALIZATION_ENTRIES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_MACRO_FILES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_MASS_CHANGES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_MODULES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_OLD_NAMES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_PROJECT_FILE_STRUCTURE;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_PROPERTIES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_REFERENCE;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_REFERENCES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_REQUESTED_PROPERTIES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_ROLES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_ROLE_GROUPS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_SCHEME;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_SCRIPTS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_SECURITY;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_SETTINGS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_STATIC_PAGES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_VIEW_DEFINITION;

/**
 * The new serializer.
 *
 * @author asko
 */
public class YamlSerializer
{

    private static class TreeTransformer
    {
        private static final List<String> booleans = Arrays.asList(Boolean.toString(true), Boolean.toString(false));

        private static boolean isBool(final String s)
        {
            return booleans.contains(s);
        }

        Object transform(Object node)
        {
            if (node instanceof String)
            {
                final String s = (String) node;
                if (isBool(s))
                    return Boolean.valueOf(s);

                return formatText(s);
            }

            if (node instanceof Map)
            {
                @SuppressWarnings("unchecked") final Map<String, Object> map = (Map<String, Object>) node;
                final Map<String, Object> result = map();
                for (final Entry<String, Object> entry : map.entrySet())
                    result.put(entry.getKey(), transform(entry.getValue()));

                return result;
            }

            if (node instanceof List)
            {
                @SuppressWarnings("unchecked") final List<Object> list = (List<Object>) node;
                if (list.size() == 1 && list.get(0) instanceof String)
                    return transform(list.get(0));

                final List<Object> result = list();
                for (final Object object : list)
                    result.add(transform(object));

                return result;
            }

            return node;
        }

        private static String formatText(final String text)
        {
            if (" ".equals(text))
            {
                return text;
            }

            final List<String> lines = StreamEx.split(text, "\n").toList();

            while (!lines.isEmpty() && lines.get(0).trim().isEmpty())
            {
                lines.remove(0);
            }

            for (int i = 0; i < lines.size(); i++)
            {
                String line = lines.get(i);
                line = line.replace("\t", "  ");
                lines.set(i, trimTrailingSpaces(line));
            }

            return String.join("\n", lines);
        }

        private static String trimTrailingSpaces(final String s)
        {
            final StringBuilder sb = new StringBuilder(s);
            int index = s.length() - 1;

            while (index >= 0 && Character.isWhitespace(sb.charAt(index)))
            {
                sb.deleteCharAt(index);
                index--;
            }

            return sb.toString();
        }

    }

    private class DaemonsSerializer
    {

        void serialize(final BeModelCollection<Daemon> daemons) throws WriteException
        {
            final String string = toString(daemons);

            try
            {
                fileSystem.writeDaemonsFile(string);
            }
            catch (final IOException e)
            {
                throw new WriteException(daemons, e);
            }
        }

        String toString(final BeModelCollection<Daemon> daemons)
        {
            final Map<String, Object> root = map();
            final Map<String, Object> content = map();

            for (final Daemon daemon : daemons)
            {
                final Map<String, Object> serializedDaemon = map();
                serializeDocumentation(daemon, serializedDaemon);
                serializeFields(daemon, Fields.daemon(), serializedDaemon);
                serializeUsedExtras(daemon, serializedDaemon);
                content.put(daemon.getName(), serializedDaemon);
            }

            root.put(TAG_DAEMONS, content);
            final String string = print(root);
            return string;
        }

    }

    private class MassChangesSerializer
    {
        void serialize(final BeModelCollection<MassChange> massChanges) throws WriteException
        {
            final String string = toString(massChanges);

            try
            {
                fileSystem.writeMassChangesFile(string);
            }
            catch (final IOException e)
            {
                throw new WriteException(massChanges, e);
            }
        }

        String toString(final BeModelCollection<MassChange> massChanges)
        {
            final Map<String, Object> root = map();
            List<Object> content = list();
            root.put(TAG_MASS_CHANGES, content);

            for (final MassChange massChange : massChanges)
            {
                final Map<String, Object> serializedMassChange = map();
                serializedMassChange.put("select", massChange.getName());
                serializeDocumentation(massChange, serializedMassChange);
                serializedMassChange.putAll(massChange.getData());
                content.add(serializedMassChange);
            }

            final String string = print(root);

            return string;
        }
    }

    private class StaticPagesSerializer
    {

        void serialize(final BeVectorCollection<LanguageStaticPages> pages) throws WriteException
        {
            final String string = toString(pages, true);

            try
            {
                fileSystem.writeStaticPagesFile(string);
            }
            catch (final IOException e)
            {
                throw new WriteException(pages, e);
            }
        }

        String toString(StaticPages pages)
        {
            try
            {
                return toString(pages, false);
            }
            catch (WriteException e)
            {
                throw new AssertionError();
            }
        }

        private String toString(final BeVectorCollection<LanguageStaticPages> pages, final boolean saveReferencedFiles) throws WriteException
        {
            final Map<String, Object> root = map();
            final Map<String, Object> content = map();

            for (final LanguageStaticPages languagePages : pages)
            {
                final Map<String, Object> serializedPages = map();

                for (final StaticPage page : languagePages)
                {
                    final BeModelCollection<PageCustomization> customizations =
                            page.getCollection(
                                    PageCustomization.CUSTOMIZATIONS_COLLECTION,
                                    PageCustomization.class);

                    if (customizations == null || customizations.getNameList().isEmpty())
                        serializedPages.put(page.getName(), getContentOrFileReference(page, saveReferencedFiles));
                    else
                    {
                        final Map<String, Object> serializedPage = map();
                        serializedPage.put(TAG_CODE, getContentOrFileReference(page, saveReferencedFiles));
                        serializeCustomizationsStatement(page, serializedPage);
                        serializedPages.put(page.getName(), serializedPage);
                    }
                }

                content.put(languagePages.getName(), serializedPages);
            }

            root.put(TAG_STATIC_PAGES, content);
            final String string = print(root);

            return string;
        }

        private Object getContentOrFileReference(final StaticPage page, final boolean saveReferencedFiles) throws WriteException
        {
            if (!Strings2.isNullOrEmpty(page.getFileName()))
            {
                try
                {
                    if (saveReferencedFiles)
                        fileSystem.writeStaticPageFile(page.getFileName(), page.getContent());
                }
                catch (final IOException e)
                {
                    throw new WriteException(page, e);
                }

                return map("file", page.getFileName());
            }

            return page.getContent();
        }

    }

    private class CustomizationSerializer
    {

        void serialize(final Module application) throws WriteException
        {
            final String string = toString(application);

            try
            {
                fileSystem.writeCustomizationFile(string);
            }
            catch (final IOException e)
            {
                throw new WriteException(application, e);
            }
        }

        String toString(final Module application)
        {
            final Map<String, Object> root = map();
            final Map<String, Object> serializedCustomizations = serializeCustomizations(application);
            root.put(TAG_CUSTOMIZATIONS, serializedCustomizations);
            final String string = print(root);

            return string;
        }

    }

    private class SchemeSerializer
    {
        private String projectOrigin;

        void serialize(final Entity entity, final Map<String, Object> entityContent)
        {
            this.projectOrigin = entity.getProject().getProjectOrigin();
            serializeScheme(entity, entityContent);
            serializeReferences(entity, entityContent);
        }

        private void serializeScheme(final Entity entity, final Map<String, Object> entityContent)
        {
            final DdlElement scheme = entity.getScheme();
            final Map<String, Object> schemeContent = map();

            if (scheme instanceof TableDef)
                serializeTableDefinition((TableDef) scheme, schemeContent);
            else if (scheme instanceof ViewDef && entity.getModule().getName().equals(projectOrigin))
                // Currently views from modules cannot be customized
                serializeViewDefinition((ViewDef) scheme, schemeContent);
            else
                return;

            if (!schemeContent.isEmpty())
            {
                entityContent.put(TAG_SCHEME, schemeContent);
            }
        }

        private void serializeReferences(Entity entity, Map<String, Object> entityContent)
        {
            BeModelCollection<TableRef> tableReferences = entity.getTableReferences();
            if (tableReferences == null)
                return;
            final Map<String, Object> serializedTableReferences = serializeTableReferences(tableReferences);

            if (!serializedTableReferences.isEmpty())
                entityContent.put(TAG_REFERENCES, serializedTableReferences);
        }

        private void serializeTableDefinition(final TableDef tableDef, final Map<String, Object> schemeContent)
        {
            final Map<String, Object> serializedTableDefinitionBody = schemeContent;
            if (tableDef.getModule().getName().equals(projectOrigin))
            {
                serializeDocumentation(tableDef, serializedTableDefinitionBody);
                serializeFields(tableDef, Fields.tableDef(), serializedTableDefinitionBody);
                serializeUsedExtras(tableDef, serializedTableDefinitionBody);
            }

            final List<Object> serializedColumnDefinitionList = list();
            for (final ColumnDef column : tableDef.getColumns())
            {
                if (column.getOriginModuleName().equals(projectOrigin))
                {
                    serializedColumnDefinitionList.add(serializeColumnDefinition(column));
                }
            }
            if (!serializedColumnDefinitionList.isEmpty())
                serializedTableDefinitionBody.put(TAG_COLUMNS, serializedColumnDefinitionList);

            final List<Object> serializedIndexDefinitionList = list();
            for (final IndexDef index : tableDef.getIndices())
            {
                if (index.getOriginModuleName().equals(projectOrigin))
                {
                    serializedIndexDefinitionList.add(serializeIndexDefinition(index));
                }
            }
            if (!serializedIndexDefinitionList.isEmpty())
                serializedTableDefinitionBody.put(TAG_INDICES, serializedIndexDefinitionList);
        }

        private Map<String, Object> serializeColumnDefinition(final ColumnDef column)
        {
            final Map<String, Object> serializedColumnDefinition = map();
            final Map<String, Object> serializedColumnDefinitionBody = map();
            serializeDocumentation(column, serializedColumnDefinitionBody);

            if (column.getRawType() != null && column.getCustomizedProperties().contains("type"))
            {
                serializedColumnDefinitionBody.put("type", column.getRawType().toString());
            }
            String[] oldNames = column.getOldNames();
            if (oldNames.length > 0)
            {
                List<Object> oldNamesList = list();
                oldNamesList.addAll(Arrays.asList(oldNames));
                serializedColumnDefinitionBody.put(TAG_OLD_NAMES, oldNamesList);
            }

            final List<Field> columnFields = new ArrayList<>(Fields.columnDef());
            columnFields.removeIf(field -> field.name.equals("type"));

            serializeFields(column, columnFields, serializedColumnDefinitionBody);
            serializeUsedExtras(column, serializedColumnDefinitionBody);
            serializedColumnDefinition.put(column.getName(), serializedColumnDefinitionBody);

            if (column.hasReference())
            {
                serializedColumnDefinitionBody.put(TAG_REFERENCE, serializeTableReference(column));
            }

            return serializedColumnDefinition;
        }

        private Map<String, Object> serializeIndexDefinition(final IndexDef index)
        {
            Map<String, Object> serializedIndex = map();
            final Map<String, Object> serializedIndexDefinitionBody = map();
            serializeDocumentation(index, serializedIndexDefinitionBody);
            serializeFields(index, Fields.indexDef(), serializedIndexDefinitionBody);
            List<Object> serializedColumns = list();
            for (IndexColumnDef col : index)
            {
                serializedColumns.add(col.getAsString());
            }
            serializedIndexDefinitionBody.put(TAG_COLUMNS, serializedColumns);
            serializeUsedExtras(index, serializedIndexDefinitionBody);
            serializedIndex.put(index.getName(), serializedIndexDefinitionBody);
            return serializedIndex;
        }

        private void serializeViewDefinition(final ViewDef viewDef, final Map<String, Object> schemeContent)
        {
            final Map<String, Object> serializedViewDefinitionBody = schemeContent;
            serializeDocumentation(viewDef, serializedViewDefinitionBody);
            serializeFields(viewDef, Fields.viewDef(), serializedViewDefinitionBody);
            serializedViewDefinitionBody.put(TAG_VIEW_DEFINITION, viewDef.getDefinition());
        }

        private Map<String, Object> serializeTableReferences(final BeModelCollection<TableRef> tableReferences)
        {
            final Map<String, Object> serializedReferences = map();

            for (final TableRef tableRef : tableReferences)
            {
                if (tableRef.getOriginModuleName().equals(projectOrigin))
                    serializedReferences.put(tableRef.getColumnsFrom(), serializeTableReference(tableRef));
            }

            return serializedReferences;
        }

        private Object serializeTableReference(final TableReference tableRef)
        {
            if (tableRef.getViewName() != null && tableRef.getViewName().equals(DatabaseConstants.SELECTION_VIEW)
                    && (tableRef.getUsedInExtras() == null || tableRef.getUsedInExtras().length == 0))
            {
                return serializeTargetTableAndColumn(tableRef);
            }
            Map<String, Object> serializedTableReferenceBody = map();
            serializedTableReferenceBody.put("to", serializeTargetTableAndColumn(tableRef));
            serializedTableReferenceBody.put("view", Strings2.nullToEmpty(tableRef.getViewName()));
            serializeUsedExtras(tableRef, serializedTableReferenceBody);
            return serializedTableReferenceBody;
        }

        private Object serializeTargetTableAndColumn(final TableReference tableRef)
        {
            if (!Strings2.isNullOrEmpty(tableRef.getTableTo()))
            {
                // empty columnTo means a primary key
                if (Strings2.isNullOrEmpty(tableRef.getColumnsTo()))
                    return tableRef.getTableTo();

                final Project project = tableRef.getProject();
                final Entity entity = project.getEntity(tableRef.getTableTo());

                // don't save the columnTo if this column is primary key
                if (entity != null && !Strings2.isNullOrEmpty(entity.getPrimaryKey()) && entity.getPrimaryKey().toLowerCase().equals(tableRef.getColumnsTo().toLowerCase()))
                    return tableRef.getTableTo();

                return tableRef.getTableTo() + "." + tableRef.getColumnsTo();
            }
            else
            {
                final String[] permittedTables = tableRef.getPermittedTables() == null ? Strings2.EMPTY : tableRef.getPermittedTables();
                return list(permittedTables);
            }
        }

    }

    private class FormsSerializer
    {

        void serialize(final JavaScriptForms forms) throws WriteException
        {
            final String string = toString(forms, true);

            try
            {
                fileSystem.writeJavaScriptFormsFile(string);
            }
            catch (final IOException e)
            {
                throw new WriteException(forms, e);
            }
        }

        String toString(final JavaScriptForms forms)
        {
            try
            {
                return toString(forms, false);
            }
            catch (WriteException e)
            {
                throw new AssertionError();
            }
        }

        private String toString(final JavaScriptForms forms, final boolean saveReferencedFiles) throws WriteException
        {
            final Map<String, Object> root = map();
            final Map<String, Object> serializedForms = map();
            final String projectOrigin = forms.getProject().getProjectOrigin();

            for (final JavaScriptForm form : forms)
            {
                final Map<String, Object> serializedForm = map();
                serializeFields(form, Fields.jsForms(), serializedForm);
                serializedForms.put(form.getName(), serializedForm);

                if (form.getModuleName().equals(projectOrigin))
                {
                    try
                    {
                        if (saveReferencedFiles)
                            form.save();
                    }
                    catch (IOException e)
                    {
                        throw new WriteException(form, e);
                    }
                }
            }

            root.put(TAG_JS_FORMS, serializedForms);
            final String string = print(root);

            return string;
        }

    }

    private class LocalizationSerializer
    {

        void serialize(final LanguageLocalizations localizations) throws WriteException
        {
            final String language = localizations.getName();
            final String string = toString(localizations);

            try
            {
                fileSystem.writeLocalizationFile(language, string);
            }
            catch (final IOException e)
            {
                throw new WriteException(localizations, e);
            }
        }

        String toString(final LanguageLocalizations localizations)
        {
            final String language = localizations.getName();
            final Map<String, Object> root = map();
            final Map<String, Object> content = map();
            final List<Object> serializedEntitiesLocalizations = list();

            for (final EntityLocalizations entityLocalizations : localizations)
                serializedEntitiesLocalizations.add(serializeEntityLocalizations(entityLocalizations));

            serializeDocumentation(localizations, content);
            content.put(TAG_ENTITIES, serializedEntitiesLocalizations);
            root.put(language, content);
            final String string = print(root);

            return string;
        }

        private Object serializeEntityLocalizations(final EntityLocalizations entityLocalizations)
        {
            final Map<String, Object> serializedEntityLocalizations = map();
            final List<Object> serializedBlocks = list();
            final Map<String, List<LocalizationElement>> blocks = new TreeMap<>();

            for (final LocalizationElement entry : entityLocalizations.elements())
            {
                final String blockName = String.join(";", entry.getTopics());
                List<LocalizationElement> block = blocks.get(blockName);

                if (block == null)
                {
                    block = new ArrayList<>();
                    blocks.put(blockName, block);
                }

                block.add(entry);
            }

            for (final List<LocalizationElement> block : blocks.values())
            {
                serializedBlocks.add(serializeBlock(block));
            }

            serializedEntityLocalizations.put(entityLocalizations.getName(), serializedBlocks);

            return serializedEntityLocalizations;
        }

        private Object serializeBlock(final List<LocalizationElement> block)
        {
            final Map<String, Object> serializedBlock = map();
            final List<String> serializedTopics = list(block.get(0).getTopics());
            final List<Object> serializedEntries = list();

            for (final LocalizationElement entry : block)
                serializedEntries.add(map(entry.getKey(), entry.getValue()));

            serializedBlock.put(ATTR_LOCALIZATION_TOPICS, serializedTopics);
            serializedBlock.put(TAG_LOCALIZATION_ENTRIES, serializedEntries);

            return serializedBlock;
        }

    }

    private class SecuritySerializer
    {

        void serialize(final SecurityCollection securityCollection) throws WriteException
        {
            final String string = toString(securityCollection);

            try
            {
                fileSystem.writeSecurityFile(string);
            }
            catch (final IOException e)
            {
                throw new WriteException(securityCollection, e);
            }
        }

        String toString(final SecurityCollection securityCollection)
        {
            final Map<String, Object> root = map();
            final Map<String, Object> content = map();

            final Map<String, Object> roles = map();
            for (final Role role : securityCollection.getRoleCollection())
            {
                final Map<String, Object> serializedRole = map();
                serializeDocumentation(role, serializedRole);
                serializeUsedExtras(role, serializedRole);
                roles.put(role.getName(), serializedRole);
            }
            content.put(TAG_ROLES, roles);

            final Map<String, Object> roleGroups = map();
            for (final RoleGroup roleGroup : securityCollection.getRoleGroupCollection())
            {
                if (!roleGroup.isPredefined())
                {
                    RoleSet r = roleGroup.getRoleSet();
                    roleGroups.put(roleGroup.getName(), list(r.printValues()));
                }
            }
            content.put(TAG_ROLE_GROUPS, roleGroups);

            root.put(TAG_SECURITY, content);
            final String string = print(root);

            return string;
        }

    }

    public class ConnectionProfilesSerializer
    {

        void serialize(final BeConnectionProfileType type, final BeConnectionProfiles profiles) throws WriteException
        {
            final String string = toString(profiles);

            try
            {
                fileSystem.writeConnectionProfilesFile(type, string);
            }
            catch (final IOException e)
            {
                throw new WriteException(profiles, e);
            }
        }

        String toString(final BeConnectionProfiles profiles)
        {
            final Map<String, Object> root = map();
            final Map<String, Object> content = map();
            final Map<String, Object> serializedProfiles = map();

            for (final BeConnectionProfile profile : profiles)
            {
                serializedProfiles.put(profile.getName(), serializeProfile(profile));
            }

            content.put(TAG_CONNECTION_PROFILES_INNER, serializedProfiles);
            root.put(TAG_CONNECTION_PROFILES, content);
            final String string = print(root);

            return string;
        }

    }

    private class EntitySerializer
    {

        private Module module;
        private String projectOrigin;

        String serialize(final Entity entity) throws WriteException
        {
            this.module = entity.getModule();
            final String oldContent = fileSystem.readEntityFile(module.getName(), entity.getName());
            final String newContent = serializeToString(entity, true);

            if (Objects.equals(newContent, oldContent))
            {
                entity.setLinkedFile(fileSystem.getEntityFile(module.getName(), entity.getName()));
                return oldContent;
            }

            try
            {
                fileSystem.writeEntityFile(module.getName(), entity.getName(), newContent);
            }
            catch (final IOException e)
            {
                throw new WriteException(entity, e);
            }

            entity.setLinkedFile(fileSystem.getEntityFile(module.getName(), entity.getName()));

            return newContent;
        }

        String toString(final Entity entity)
        {
            module = entity.getModule();

            try
            {
                return serializeToString(entity, false);
            }
            catch (WriteException e)
            {
                throw new AssertionError(); // no write operations should be called
            }
        }

        private String serializeToString(final Entity entity, final boolean serializeReferencedFiles) throws WriteException
        {
            this.projectOrigin = module.getProject().getProjectOrigin();

            final String type = entity.getType().getSqlName();
            final Map<String, Object> root = map();
            final Map<String, Object> content = map();

            BeModelElement prototype = entity.getPrototype();
            if (prototype != null && prototype.getProject().getName().equals(Templates.TEMPLATES_PROJECT_NAME))
            {
                content.put(ATTR_ENTITY_TEMPLATE, prototype.getName());
            }

            serializeDocumentation(entity, content);

            final Collection<String> customizedProperties = entity.getCustomizedProperties();

            if (customizedProperties.contains("type"))
            {
                content.put(ATTR_ENTITY_TYPE, type);
            }

            serializeUsedExtras(entity, content);
            serializeFields(entity, Fields.entity(), content);

            root.put(entity.getName(), content);

            if (customizedProperties.contains("icon"))
            {
                writeIcon(content, entity.getIcon());
            }

            serializeScheme(entity, content);

            final BeModelCollection<Query> queries = entity.getQueries();
            final List<Object> serializedQueries = list();

            for (final Query query : queries)
            {
                if (projectOrigin.equals(query.getOriginModuleName()) || query.isCustomized())
                {
                    serializedQueries.add(serializeQuery(query, serializeReferencedFiles));
                }
            }

            final BeModelCollection<Operation> operations = entity.getOperations();
            final List<Object> serializedOperations = list();

            for (final Operation operation : operations)
            {
                if (projectOrigin.equals(operation.getOriginModuleName()) || operation.isCustomized())
                {
                    serializedOperations.add(serializeOperation(operation));
                }
            }

            if (!serializedQueries.isEmpty())
                content.put("queries", serializedQueries);

            if (!serializedOperations.isEmpty())
                content.put("operations", serializedOperations);

            serializeCustomizationsStatement(entity, content);
            final String stringContent = print(root);

            return stringContent;
        }

        private Object serializeQuery(final Query query, final boolean serializeReferencedFiles) throws WriteException
        {
            final Map<String, Object> root = map();
            final Map<String, Object> content = map();
            serializeDocumentation(query, content);
            serializeFields(query, Fields.query(), content);
            serializeUsedExtras(query, content);

            final Collection<String> customizedProperties =
                    projectOrigin.equals(query.getOriginModuleName())
                            ? query.getCustomizedProperties()
                            : Collections.<String>emptySet();

            writeRoles(customizedProperties, query, content);

            if (customizedProperties.contains("operationNames"))
            {
                final boolean writeEmpty = query.isCustomized();
                if (writeEmpty || !query.getOperationNames().isEmpty())
                {
                    OperationSet s = query.getOperationNames();
                    content.put(ATTR_QUERY_OPERATIONS, list(s.printValues()));
                }
            }

            final Map<String, Object> serializedFilters = serializeQuickFilters(query.getQuickFilters());

            if (!serializedFilters.isEmpty())
                content.put("quickFilters", serializedFilters);

            if (customizedProperties.contains("querySettings"))
            {
                content.put("settings", serializeSettings(query.getQuerySettings(), new HashSet<>(query.getProject().getRoles())));
            }

            serializeCustomizationsStatement(query, content);

            if (customizedProperties.contains("icon"))
            {
                writeIcon(content, query.getIcon());
            }

            if (customizedProperties.contains("query"))
            {
                if (query.getType() == QueryType.STATIC)
                {
                    content.put(ATTR_QUERY_CODE, query.getQuery());
                }
                else if (query.getType() == QueryType.JAVASCRIPT)
                {
                    jsQuery(query, serializeReferencedFiles, content);
                }
                else if (query.getType() == QueryType.GROOVY)
                {
                    groovyQuery(query, serializeReferencedFiles, content);
                }
                else
                {
                    content.put(TAG_CODE, query.getQuery());
                }
            }

            root.put(query.getName(), content);

            return root;
        }

        private void jsQuery(Query query, boolean serializeReferencedFiles, Map<String, Object> content) throws WriteException
        {
            if (query.getFileName().isEmpty())
                query.setFileName(query.getName() + ".js");
            else if (!query.getFileName().endsWith(".js"))
                query.setFileName(query.getFileName() + ".js");

            query.setFileName(query.getFileName().replace(':', '_'));

            content.put("file", query.getFileName());

            write:
            if (serializeReferencedFiles)
            {
                try
                {
                    final String newContent = query.getQuery();

                    if (Files.isRegularFile(fileSystem.getJavaScriptQueryFile(query.getFileName())))
                    {
                        try
                        {
                            final String oldContent = fileSystem.readJavaScriptQuery(query.getFileName());
                            if (oldContent.equals(newContent))
                                break write;
                        }
                        catch (ReadException e)
                        {
                            // ignore
                        }
                    }

                    fileSystem.writeJavaScriptQuery(query.getFileName(), query.getQuery());
                }
                catch (IOException e)
                {
                    throw new WriteException(query, e);
                }
            }
        }

        private void groovyQuery(Query query, boolean serializeReferencedFiles, Map<String, Object> content) throws WriteException
        {
            if (query.getFileName().isEmpty())
                query.setFileName(query.getName() + "groovy");
            else if (!query.getFileName().endsWith("groovy"))
                query.setFileName(query.getFileName() + ".groovy");

            query.setFileName(query.getFileName().replace(':', '_'));

            content.put("file", query.getFileName());

            write:
            if (serializeReferencedFiles)
            {
                try
                {
                    final String newContent = query.getQuery();

                    if (Files.isRegularFile(fileSystem.getGroovyQueryFile(query.getFileName())))
                    {
                        try
                        {
                            final String oldContent = fileSystem.readGroovyQuery(query.getFileName());
                            if (oldContent.equals(newContent))
                                break write;
                        }
                        catch (ReadException e)
                        {
                            // ignore
                        }
                    }

                    fileSystem.writeGroovyQuery(query.getFileName(), query.getQuery());
                }
                catch (IOException e)
                {
                    throw new WriteException(query, e);
                }
            }
        }

        private Map<String, Object> serializeQuickFilters(final QuickFilter[] quickFilters)
        {
            final Map<String, Object> serializedFilters = map();

            for (final QuickFilter quickFilter : quickFilters)
            {
                if (!projectOrigin.equals(quickFilter.getOriginModuleName()))
                    continue;

                final Map<String, Object> serializedFilterBody = map();
                serializeFields(quickFilter, Fields.quickFilter(), serializedFilterBody);
                serializedFilters.put(quickFilter.getName(), serializedFilterBody);
            }

            return serializedFilters;
        }

        private List<Object> serializeSettings(final QuerySettings[] querySettings, final Set<String> roles)
        {
            final List<Object> serializedSettings = list();

            for (final QuerySettings setting : querySettings)
            {
                if (setting.getRoles().getAllIncludedValues().isEmpty())
                    continue;

                final Map<String, Object> settingsBlock = map();
                final Map<String, Object> settingsBlockBody = map();
                serializeFields(setting, Fields.querySettings(), settingsBlockBody);

                if (!setting.getRoles().equals(roles))
                    settingsBlockBody.put(ATTR_ROLES, list(setting.getRoles().getAllIncludedValues()));

                settingsBlock.put(TAG_SETTINGS, settingsBlockBody);
                serializedSettings.add(settingsBlock);
            }

            return serializedSettings;
        }

        private Object serializeOperation(final Operation operation)
        {
            final Map<String, Object> root = map();
            final Map<String, Object> content = map();

            serializeDocumentation(operation, content);
            serializeFields(operation, Fields.operation(), content);
            serializeUsedExtras(operation, content);

            final Collection<String> customizedProperties =
                    projectOrigin.equals(operation.getOriginModuleName())
                            ? operation.getCustomizedProperties()
                            : Collections.<String>emptySet();

            root.put(operation.getName(), content);

            if (!operation.getType().equals(Operation.OPERATION_TYPE_JAVA))
                content.put(ATTR_OPERATION_TYPE, operation.getType());

            writeRoles(customizedProperties, operation, content);

            if (customizedProperties.contains("icon"))
                writeIcon(content, operation.getIcon());

            final BeModelCollection<OperationExtender> extenders = operation.getExtenders();
            final List<Object> sExtenders = list();

            if (extenders != null)
            {
                for (final OperationExtender extender : extenders)
                    if (extender.getOriginModuleName().equals(projectOrigin))
                        sExtenders.add(serializeExtender(extender));
            }

            if (!sExtenders.isEmpty())
                content.put("extenders", sExtenders);

            serializeCustomizationsStatement(operation, content);

            if (customizedProperties.contains("code"))
            {
                if (operation instanceof SourceFileOperation)
                    content.put(ATTR_FILEPATH, ((SourceFileOperation) operation).getFileName());
                else
                    content.put(TAG_CODE, operation.getCode());
            }

            return root;
        }

        private Object serializeExtender(final OperationExtender extender)
        {
            final Map<String, Object> serializedExtender = map();
            serializeFields(extender, Fields.extender(), serializedExtender);

            if (extender instanceof SourceFileOperationExtender)
                serializedExtender.put(ATTR_FILEPATH, ((SourceFileOperationExtender) extender).getFileName());
            else
                serializedExtender.put(ATTR_CLASS_NAME, extender.getClassName());

            return serializedExtender;
        }

        private void writeRoles(final Collection<String> customizedProperties, final EntityItem item, final Map<String, Object> target)
        {
            if (customizedProperties.contains("roles"))
            {
                final boolean writeEmpty = item.isCustomized();
                if (!item.getRoles().isEmpty() || writeEmpty)
                {
                    RoleSet r = item.getRoles();
                    target.put(ATTR_ROLES, list(r.printValues()));
                }
            }
        }

        private void writeIcon(final Map<String, Object> target, final Icon icon)
        {
            if (!icon.getOriginModuleName().equals(projectOrigin))
            {
                return;
            }

            final String path = icon.getMetaPath();

            if (path != null)
            {
                target.put(ATTR_ICON, path);
            }

            try
            {
                icon.save();
            }
            catch (final IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    private ProjectFileSystem fileSystem;

    public YamlSerializer()
    {
    }

    private static void serializeUsedExtras(BeModelElement element, Map<String, Object> content)
    {
        String[] usedInExtras = element.getUsedInExtras();
        if (usedInExtras == null || usedInExtras.length == 0)
            return;
        List<Object> extrasList = list();
        extrasList.addAll(Arrays.asList(usedInExtras));
        content.put(TAG_EXTRAS, extrasList);
    }

    public void serializeProjectTo(final Project project, final Path projectRoot) throws WriteException
    {
        project.setLocation(projectRoot);
        serialize(project, true);
    }

    //
//    public String serialize( final Entity entity ) throws WriteException
//    {
//        this.fileSystem = new ProjectFileSystem( entity.getProject() );
//        return new EntitySerializer().serialize( entity );
//    }
//
//    public String toString( final Entity entity )
//    {
//        this.fileSystem = new ProjectFileSystem( entity.getProject() );
//        return new EntitySerializer().toString( entity );
//    }
//
//    public void serialize( final LanguageLocalizations languageLocalizations ) throws WriteException
//    {
//        this.fileSystem = new ProjectFileSystem( languageLocalizations.getProject() );
//        new LocalizationSerializer().serialize( languageLocalizations );
//    }
//
//    public String toString( final LanguageLocalizations languageLocalizations )
//    {
//        this.fileSystem = new ProjectFileSystem( languageLocalizations.getProject() );
//        return new LocalizationSerializer().toString( languageLocalizations );
//    }
//
//    public void serialize( final MassChanges massChanges ) throws WriteException
//    {
//        this.fileSystem = new ProjectFileSystem( massChanges.getProject() );
//        new MassChangesSerializer().serialize( massChanges );
//    }
//
//    public String toString( final MassChanges massChanges )
//    {
//        this.fileSystem = new ProjectFileSystem( massChanges.getProject() );
//        return new MassChangesSerializer().toString( massChanges );
//    }
//
//    public void serialize( final SecurityCollection security ) throws WriteException
//    {
//        this.fileSystem = new ProjectFileSystem( security.getProject() );
//        new SecuritySerializer().serialize( security );
//    }
//
//    public String toString( final SecurityCollection security )
//    {
//        this.fileSystem = new ProjectFileSystem( security.getProject() );
//        return new SecuritySerializer().toString( security );
//    }
//
//    public void serialize( final BeConnectionProfiles connectionProfiles ) throws WriteException
//    {
//        this.fileSystem = new ProjectFileSystem( connectionProfiles.getProject() );
//        new ConnectionProfilesSerializer().serialize( connectionProfiles.getType(), connectionProfiles );
//    }
//
//    public String toString( final BeConnectionProfiles connectionProfiles )
//    {
//        this.fileSystem = new ProjectFileSystem( connectionProfiles.getProject() );
//        return new ConnectionProfilesSerializer().toString( connectionProfiles );
//    }
//
//    public void serialize( final PageCustomizations customizations ) throws WriteException
//    {
//        this.fileSystem = new ProjectFileSystem( customizations.getProject() );
//        new CustomizationSerializer().serialize( customizations.getModule() );
//    }
//
//    public String toString( @SuppressWarnings( "unused" ) final PageCustomizations customizations, final Module application )
//    {
//        this.fileSystem = new ProjectFileSystem( application.getProject() );
//        return new CustomizationSerializer().toString( application );
//    }
//
//    public void serialize( final Daemons daemons ) throws WriteException
//    {
//        this.fileSystem = new ProjectFileSystem( daemons.getProject() );
//        new DaemonsSerializer().serialize( daemons );
//    }
//
//    public String toString( final Daemons daemons )
//    {
//        this.fileSystem = new ProjectFileSystem( daemons.getProject() );
//        return new DaemonsSerializer().toString( daemons );
//    }
//
//    public void serialize( final JavaScriptForms forms ) throws WriteException
//    {
//        this.fileSystem = new ProjectFileSystem( forms.getProject() );
//        new FormsSerializer().serialize( forms );
//    }
//
//    public String toString( final JavaScriptForms forms )
//    {
//        this.fileSystem = new ProjectFileSystem( forms.getProject() );
//        return new FormsSerializer().toString( forms );
//    }
//
//    public void serialize( static final Pages pages ) throws WriteException
//    {
//        this.fileSystem = new ProjectFileSystem( pages.getProject() );
//        new StaticPagesSerializer().serialize( pages );
//    }
//
//    public String toString( static final Pages pages )
//    {
//        this.fileSystem = new ProjectFileSystem( pages.getProject() );
//        return new StaticPagesSerializer().toString( pages );
//    }
//
//    public void serialize( final Project project ) throws WriteException
//    {
//        Objects.requireNonNull( project.getLocation(), project.getName() );
//        serialize( project, false );
//    }
//
//    public String toString( final Project project )
//    {
//        Objects.requireNonNull( project.getLocation(), project.getName() );
//
//        try
//        {
//            return serializeToString( project, false );
//        }
//        catch ( WriteException e )
//        {
//            throw new AssertionError();
//        }
//    }
//
    private void serialize(final Project project, final boolean serializeReferencedFiles) throws WriteException, AssertionError
    {
        final String string = serializeToString(project, serializeReferencedFiles);

        try
        {
            new ProjectFileSystem(project).writeProject(string);
        }
        catch (final IOException e)
        {
            throw new WriteException(project, e);
        }
    }

    private String serializeToString(final Project project, final boolean serializeReferencedFiles) throws WriteException, AssertionError
    {
        final Object serialized = serializeToObject(project, serializeReferencedFiles);

        if (serialized == null)
            throw new AssertionError();

        final String string = print(serialized);

        return string;
    }

    private Object serializeToObject(final Project project, final boolean serializeReferencedFiles) throws WriteException
    {
        final Map<String, Object> root = map();
        final Map<String, Object> content = map();

        this.fileSystem = new ProjectFileSystem(project);

        if (project.isModuleProject())
            content.put(ATTR_MODULE_PROJECT, Boolean.valueOf(true));

        serializeDocumentation(project, content);
        content.put(ATTR_FEATURES, list(project.getFeatures()));
        content.put(ATTR_LOCALIZATIONS, list(project.getApplication().getLocalizations().getNameList()));

        serializeBugtrackers(project, content);

        content.put(TAG_PROJECT_FILE_STRUCTURE, serialize(project.getProjectFileStructure()));
        content.put(TAG_MODULES, serializeModules(project.getModules(), serializeReferencedFiles));
        content.put(TAG_APPLICATION, serializeApplication(project.getApplication(), serializeReferencedFiles));

        final List<Object> scriptNames = serializeScripts(project.getApplication().getFreemarkerScripts(), serializeReferencedFiles);
        if (!scriptNames.isEmpty())
            content.put(TAG_SCRIPTS, scriptNames);

        final List<Object> macroFilesNames = serializeMacroFiles(project.getApplication().getMacroCollection(), serializeReferencedFiles);
        if (!macroFilesNames.isEmpty())
            content.put(TAG_MACRO_FILES, macroFilesNames);

        serializeProperties(project, content);

        if (serializeReferencedFiles)
        {
            serializeDaemons(project.getApplication().getDaemonCollection());
            serializeStaticPages(project.getApplication().getStaticPageCollection());
            serializeSources(project);
            serializeForms(project.getApplication());
            serializeLocalizations(project);
            serializeSecurity(project.getSecurityCollection());
            serializeCustomization(project.getApplication());
            serializeMassChanges(project.getApplication().getMassChangeCollection());

            if (!project.isModuleProject())
            {
                serializeConnectionProfiles(project.getConnectionProfiles());
                if (project.getConnectionProfileName() != null)
                {
                    try
                    {
                        fileSystem.writeSelectedProfileFile(project.getConnectionProfileName());
                    }
                    catch (IOException e)
                    {
                        throw new WriteException(project, e);
                    }
                }
            }
        }

        root.put(project.getName(), content);

        return root;
    }

    private void serializeBugtrackers(final Project project, final Map<String, Object> content)
    {
        final Collection<String> connectedBugtrackers = project.getConnectedBugtrackers();

        if (!connectedBugtrackers.isEmpty())
        {
            final Map<String, String> serializedConnectedBugtrackers = new LinkedHashMap<>();

            for (final String bugtracker : connectedBugtrackers)
                serializedConnectedBugtrackers.put(bugtracker, project.getRemoteProjectId(bugtracker));

            content.put(TAG_BUGTRACKERS, serializedConnectedBugtrackers);
        }
    }

    private Object serialize(final ProjectFileStructure projectFileStructure)
    {
        final Map<String, Object> pfs = map();
        serializeFields(projectFileStructure, Fields.projectFileStructure(), pfs);

        return pfs;
    }

    private Object serializeModules(final BeModelCollection<Module> modules, boolean serializeReferencedFiles) throws WriteException
    {
        final List<Object> serializedModules = list();

        for (final Module module : modules)
        {
            final Map<String, Object> serializedModule = map();
            final Map<String, Object> serializedModuleContent = map();
            final List<Object> serializedEntities = list();
            final Iterable<Entity> entities = module.getEntities();
            for (final Entity entity : entities)
                if (entity.isCustomized())
                {
                    if (serializeReferencedFiles)
                        new EntitySerializer().serialize(entity);
                    serializedEntities.add(entity.getName());
                }
            serializedModuleContent.put(TAG_ENTITIES, serializedEntities);
            final List<Object> serializedExtras = list();
            for (String extra : module.getExtras())
            {
                serializedExtras.add(extra);
            }
            if (!serializedExtras.isEmpty())
            {
                serializedModuleContent.put(TAG_EXTRAS, serializedExtras);
            }
            serializedModule.put(module.getName(), serializedModuleContent);
            serializedModules.add(serializedModule);
        }

        return serializedModules;
    }

    private Object serializeApplication(final Module application, boolean serializeReferencedFiles) throws WriteException
    {
        final List<Object> serializedEntities = list();
        final Iterable<Entity> entities = application.getEntities();
        for (final Entity entity : entities)
        {
            if (serializeReferencedFiles)
                new EntitySerializer().serialize(entity);
            serializedEntities.add(entity.getName());
        }

        return serializedEntities;
    }

    private List<Object> serializeScripts(final FreemarkerCatalog scripts, boolean serializeReferencedFiles) throws WriteException
    {
        return serialize(scripts, fileSystem::getScriptFile, serializeReferencedFiles);
    }

    private List<Object> serializeMacroFiles(final FreemarkerCatalog scripts, boolean serializeReferencedFiles) throws WriteException
    {
        return serialize(scripts, fileSystem::getMacroFile, serializeReferencedFiles);
    }

    private List<Object> serialize(final FreemarkerCatalog scripts, Function<String, Path> getPath, final boolean serializeReferencedFiles) throws WriteException
    {
        List<Object> paths = list();
        for (final FreemarkerScript script : scripts.getScripts())
        {
            try
            {
                String relativePath = script.getRelativePath(scripts);
                if (serializeReferencedFiles)
                {
                    final Path scriptFile = getPath.apply(relativePath);

                    if (!Files.exists(scriptFile.getParent()))
                        Files.createDirectories(scriptFile.getParent());

                    if (!script.isLoaded())
                        continue;

                    write(scriptFile, script);
                    script.setLinkedFile(scriptFile);
                }
                paths.add(relativePath);
            }
            catch (final Exception e)
            {
                throw new WriteException(script, e);
            }
        }

        return paths;
    }

    private void write(final Path scriptFile, final FreemarkerScript script) throws IOException, UnsupportedEncodingException
    {
        try
        {
            // skip writing of the same content as this action
            // changes editors state
            if (Files.isRegularFile(scriptFile))
            {
                final String oldSource = new String(Files.readAllBytes(scriptFile), StandardCharsets.UTF_8);
                final String newSource = script.getSource();
                if (newSource.equals(oldSource))
                    return;
            }
        }
        catch (Throwable e)
        { /* skip this action to fallback to the default behavior */ }

        Files.write(scriptFile, script.getSource().getBytes(StandardCharsets.UTF_8));
    }

    private void serializeMassChanges(final BeModelCollection<MassChange> massChanges) throws WriteException
    {
        new MassChangesSerializer().serialize(massChanges);
    }

    private void serializeDaemons(final BeModelCollection<Daemon> daemons) throws WriteException
    {
        new DaemonsSerializer().serialize(daemons);
    }

    private void serializeStaticPages(final BeVectorCollection<LanguageStaticPages> staticPages) throws WriteException
    {
        new StaticPagesSerializer().serialize(staticPages);
    }

    private void serializeScheme(final Entity entity, Map<String, Object> content)
    {
        new SchemeSerializer().serialize(entity, content);
    }

    private void serializeSources(final Project project) throws WriteException
    {
        for (SourceFileCollection collection : project.getApplication().getSourceFiles())
        {
            for (final SourceFile file : collection)
            {
                if (!file.isLoaded())
                    continue;

                try
                {
                    fileSystem.writeSourceFile(collection.getName(), file.getName(), file.getSource());
                    file.setLinkedFile(fileSystem.getNameSpaceFile(collection.getName(), file.getName()));
                }
                catch (final IOException e)
                {
                    throw new WriteException(file, e);
                }
            }
        }
    }

    private void serializeForms(final Module application) throws WriteException
    {
        new FormsSerializer().serialize(application.getJavaScriptFormsCollection());
    }

    private void serializeLocalizations(final Project project) throws WriteException
    {
        final Localizations localizations = project.getApplication().getLocalizations();

        for (final LanguageLocalizations languageLocalizations : localizations)
        {
            new LocalizationSerializer().serialize(languageLocalizations);
        }
    }

    private void serializeSecurity(final SecurityCollection securityCollection) throws WriteException
    {
        new SecuritySerializer().serialize(securityCollection);
    }

    private void serializeCustomization(final Module application) throws WriteException
    {
        new CustomizationSerializer().serialize(application);
    }

    private void serializeConnectionProfiles(final BeConnectionProfilesRoot connectionProfiles) throws WriteException
    {
        final ConnectionProfilesSerializer serializer = new ConnectionProfilesSerializer();
        serializer.serialize(BeConnectionProfileType.LOCAL, connectionProfiles.getLocalProfiles());
        serializer.serialize(BeConnectionProfileType.REMOTE, connectionProfiles.getRemoteProfiles());
    }

    private void serializeCustomizationsStatement(final BeVectorCollection<?> model, final Map<String, Object> target)
    {
        final Map<String, Object> serializedCustomizations = serializeCustomizations(model);

        if (!serializedCustomizations.isEmpty())
            target.put("customizations", serializedCustomizations);
    }

    private Map<String, Object> serializeCustomizations(final BeVectorCollection<?> parent)
    {
        final Map<String, Object> serializedCustomizations = map();
        final BeModelCollection<PageCustomization> customiazations =
                parent.getCollection(
                        PageCustomization.CUSTOMIZATIONS_COLLECTION,
                        PageCustomization.class);

        if (customiazations != null)
        {
            final String projectOrigin = parent.getProject().getProjectOrigin();

            for (final PageCustomization customization : customiazations)
            {
                if (!projectOrigin.equals(customization.getOriginModuleName()))
                    continue;

                final String name = customization.getDomain() + "." + customization.getType();
                final Map<String, Object> serializedCustomization = map();
                final List<String> serializedRoles = list(customization.getRoles());
                if (!serializedRoles.isEmpty())
                    serializedCustomization.put(ATTR_ROLES, serializedRoles);
                serializedCustomization.put(TAG_CODE, customization.getCode());
                serializedCustomizations.put(name, serializedCustomization);
            }
        }

        return serializedCustomizations;
    }

    private void serializeDocumentation(final BeModelElement model, final Map<String, Object> target)
    {
        if (model.getComment() != null && !model.getComment().trim().isEmpty())
            target.put(TAG_COMMENT, model.getComment());
    }

    public static Map<String, Object> serializeProfile(final BeConnectionProfile profile)
    {
        final Map<String, Object> serializedProfile = map();
        serializeFields(profile, Fields.connectionProfile(), serializedProfile);
        serializeProperties(profile, serializedProfile);
        String[] propertiesToRequest = profile.getPropertiesToRequest();
        if (propertiesToRequest != null && propertiesToRequest.length > 0)
            serializedProfile.put(TAG_REQUESTED_PROPERTIES, list(propertiesToRequest));

        if (!profile.hasDefaultProviderId())
            serializedProfile.put(Fields.connectionProfileProviderId(), profile.getProviderId());

        if (!profile.hasDefaultDriverDefinition())
            serializedProfile.put(Fields.connectionProfileDriverDefinition(), profile.getDriverDefinition());

        return serializedProfile;
    }

    static void serializeProperties(final BeElementWithProperties element, final Map<String, Object> serializedElement)
    {
        final List<Object> serializedProperties = list();
        for (final String property : element.getPropertyNames())
            serializedProperties.add(map(property, element.getProperty(property)));

        if (!serializedProperties.isEmpty())
            serializedElement.put(TAG_PROPERTIES, serializedProperties);
    }

    static void serializeFields(final BeModelElement model, final List<Field> fields, final Map<String, Object> target)
    {
        final Collection<String> inheritedProperties;
        final Collection<String> customizedProperties;

        inheritedProperties = new HashSet<>(model.getCustomizableProperties());
        customizedProperties = model.getCustomizedProperties();
        inheritedProperties.removeAll(customizedProperties);

        for (final Field field : fields)
        {
            // TODO
            // Remove 'name' field.
            if (inheritedProperties.contains(field.name) || field.name.equals(ATTR_NAME))
                continue;

            try
            {
                final Object fieldValue = Beans.getBeanPropertyValue(model, field.name);

                if (fieldValue != null && (!fieldValue.equals(field.defaultValue) || customizedProperties.contains(field.name)))
                {
                    if (fieldValue instanceof Integer || fieldValue instanceof Boolean || fieldValue instanceof String)
                        target.put(field.name, fieldValue);
                    else
                        target.put(field.name, String.valueOf(fieldValue));
                }
            }
            catch (final Exception e)
            {
                throw new RuntimeException("Unexpected error when serializing '" + field.name + "' of " + model.getCompletePath(), e);
            }
        }
    }

    /**
     * This method not just prints the tree. It transforms the tree using following rules:<br>
     * - any list of strings with one element is transformed to string;<br>
     * - 'true' and 'false' string values is transformed to boolean values.<br>
     *
     * @param root
     * @return
     */
    static String print(Object root)
    {
        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(FlowStyle.BLOCK);
        options.setDefaultScalarStyle(ScalarStyle.PLAIN);
        options.setPrettyFlow(false);

        final Object transformed = new TreeTransformer().transform(root);

        return new Yaml(new Constructor(), new YamlRepresenter(transformed), options).dump(transformed);
    }

    static Map<String, Object> map()
    {
        return new LinkedHashMap<>();
    }

    static Map<String, Object> map(final String key, final Object value)
    {
        final LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put(key, value);

        return map;
    }

    static List<Object> list()
    {
        return new ArrayList<>();
    }

    static List<String> list(final Collection<String> elements)
    {
        return new ArrayList<>(elements);
    }

    static List<String> list(final String[] elements)
    {
        return new ArrayList<>(Arrays.asList(elements));
    }

}
