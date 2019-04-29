package com.developmentontheedge.be5.metadata.serialization.yaml.deserializers;

import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.model.BeConnectionProfile;
import com.developmentontheedge.be5.metadata.model.BeConnectionProfileType;
import com.developmentontheedge.be5.metadata.model.BeConnectionProfiles;
import com.developmentontheedge.be5.metadata.model.BeConnectionProfilesRoot;
import com.developmentontheedge.be5.metadata.model.ColumnDef;
import com.developmentontheedge.be5.metadata.model.Daemon;
import com.developmentontheedge.be5.metadata.model.DataElementUtils;
import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.be5.metadata.model.EntityType;
import com.developmentontheedge.be5.metadata.model.FreemarkerCatalog;
import com.developmentontheedge.be5.metadata.model.FreemarkerScript;
import com.developmentontheedge.be5.metadata.model.JavaScriptForm;
import com.developmentontheedge.be5.metadata.model.LanguageStaticPages;
import com.developmentontheedge.be5.metadata.model.Localizations;
import com.developmentontheedge.be5.metadata.model.MassChanges;
import com.developmentontheedge.be5.metadata.model.Module;
import com.developmentontheedge.be5.metadata.model.Operation;
import com.developmentontheedge.be5.metadata.model.PageCustomization;
import com.developmentontheedge.be5.metadata.model.PageCustomizations;
import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.model.ProjectFileStructure;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.metadata.model.SecurityCollection;
import com.developmentontheedge.be5.metadata.model.TableDef;
import com.developmentontheedge.be5.metadata.model.Templates;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;
import com.developmentontheedge.be5.metadata.serialization.Fields;
import com.developmentontheedge.be5.metadata.serialization.LoadContext;
import com.developmentontheedge.be5.metadata.serialization.ProjectFileSystem;
import com.developmentontheedge.be5.metadata.util.ObjectCache;
import one.util.streamex.StreamEx;
import org.yaml.snakeyaml.Yaml;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_CODE;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_MACRO_FILES;


public class YamlDeserializer
{
    final LoadContext loadContext;
    Project project;
    ProjectFileSystem fileSystem;
    private final ObjectCache<String> strings = new ObjectCache<>();
    private Project templates;
    // Whether the entity template should be fused with entity (false to inherit)
    final boolean fuseTemplate;

    List<String> stringCache(Collection<String> collection)
    {
        if (collection == null)
            return null;
        List<String> result = new ArrayList<>();
        for (String str : collection)
        {
            result.add(strings.get(str));
        }
        return result;
    }

    public static TableDef readTableDef(final LoadContext loadContext, Project project, String tableName, Map<String, Object> tableDefHash) throws ReadException
    {
        /*
         * Information about primary key is contained in an entity, so any entity is required to deserialize some set of columns.
         * See ColumnDef#isPrimaryKey().
         */
        Entity entity = new Entity(tableName, project.getApplication(), EntityType.TABLE);
        return schemeDeserializer(loadContext).readTableDef(tableDefHash, entity);
    }

    public static ColumnDef readColumnDef(final LoadContext loadContext, Project project, final String columnName, final Map<String, Object> columnContent) throws ReadException
    {
        /*
         * Information about primary key is contained in an entity, so any entity is required to deserialize some set of columns.
         * See ColumnDef#isPrimaryKey().
         */
        Entity entity = new Entity("table", project.getApplication(), EntityType.TABLE);
        return schemeDeserializer(loadContext).readColumnDef(columnName, columnContent, new TableDef(entity).getColumns());
    }

    private static SchemeDeserializer schemeDeserializer(final LoadContext loadContext)
    {
        return new SchemeDeserializer(new YamlDeserializer(loadContext), loadContext);
    }

    public static Entity readEntity(final LoadContext loadContext, final String entityName, final Map<String, Object> content, final Module module)
    {
        YamlDeserializer yamlDeserializer = new YamlDeserializer(loadContext);
        yamlDeserializer.setProject(module.getProject());
        EntityDeserializer entityDeserializer = new EntityDeserializer(yamlDeserializer, loadContext);

        try
        {
            return entityDeserializer.readEntity(entityName, content, module);
        }
        catch (ReadException e)
        {
            Entity entity = new Entity(entityName, module, EntityType.TABLE);
            loadContext.addWarning(e.attachElement(entity));
            return entity;
        }
    }

    public static Query readQuery(final LoadContext loadContext, final String queryName, final Map<String, Object> content, final Entity entity)
    {
        YamlDeserializer yamlDeserializer = new YamlDeserializer(loadContext);
        yamlDeserializer.setProject(entity.getProject());
        EntityDeserializer entityDeserializer = new EntityDeserializer(yamlDeserializer, loadContext);
        try
        {
            return entityDeserializer.readQuery(queryName, content, entity);
        }
        catch (ReadException e)
        {
            Query query = new Query(queryName, entity);
            loadContext.addWarning(e.attachElement(query));
            return query;
        }
    }

    public static Operation readOperation(final LoadContext loadContext, final String operationName, final Map<String, Object> content, final Entity entity)
    {
        YamlDeserializer yamlDeserializer = new YamlDeserializer(loadContext);
        yamlDeserializer.setProject(entity.getProject());
        EntityDeserializer entityDeserializer = new EntityDeserializer(yamlDeserializer, loadContext);

        try
        {
            return entityDeserializer.readOperation(operationName, content, entity);
        }
        catch (ReadException e)
        {
            Operation operation = Operation.createOperation(operationName, Operation.OPERATION_TYPE_JAVA, entity);
            loadContext.addWarning(e.attachElement(operation));
            return operation;
        }
    }

    /**
     * Parses a connection profile. Doesn't save it to connection profiles
     * collection.
     *
     * @param serialized Must contain a serialized JSON with a map as a root element.
     *                   This map should have only one key, that represents a
     *                   connection profile name. The value should contain pairs of connection profile fields.
     * @see Fields#connectionProfile()
     * @see Fields#connectionProfileRead()
     */
    public static BeConnectionProfile deserializeConnectionProfile(final LoadContext loadContext, final String serialized, final Project project) throws ReadException
    {
        final LinkedHashMap<String, Object> namedProfile = new Yaml().load(serialized);
        final String profileName = namedProfile.keySet().iterator().next();
        @SuppressWarnings("unchecked") // unsafe
        final Map<String, Object> serializedProfileBody = (Map<String, Object>) namedProfile.get(profileName);
        final BeConnectionProfile profile = readConnectionProfile(loadContext, profileName, serializedProfileBody, project);
        return profile;
    }

    /**
     * Parses a connection profile. Doesn't save it to connection profiles collection.
     *
     * @param serializedProfileBody just a map of properties
     */
    private static BeConnectionProfile readConnectionProfile(final LoadContext loadContext, final String profileName, final Map<String, Object> serializedProfileBody, final Project project) throws ReadException
    {
        final YamlDeserializer yamlDeserializer = new YamlDeserializer(loadContext);
        final ConnectionProfilesDeserializer connectionProfilesDeserializer = new ConnectionProfilesDeserializer(loadContext, project
                .getConnectionProfiles().getLocalProfiles());
        final BeConnectionProfile connectionProfile = connectionProfilesDeserializer.deserializeConnectionProfile(profileName,
                serializedProfileBody);

        return connectionProfile;
    }

    public Project getTemplates() throws ReadException
    {
        if (templates == null)
        {
            templates = Templates.getTemplatesProject();
            templates.mergeHostProject(project);
        }
        return templates;
    }

    public YamlDeserializer(final LoadContext loadContext)
    {
        this(loadContext, false);
    }

    public YamlDeserializer(final LoadContext loadContext, boolean fuseTemplate)
    {
        this.loadContext = loadContext;
        this.fuseTemplate = fuseTemplate;
    }

    public Project readProject(final Path root) throws ReadException
    {
        final ProjectDeserializer projectDeserializer = new ProjectDeserializer(this, loadContext, ProjectFileSystem.getProjectFile(root));
        projectDeserializer.deserialize();

        return this.project;
    }

    //
//    public Entity reloadEntity( final Entity oldEntity ) throws ReadException
//    {
//        this.fileSystem = new ProjectFileSystem( oldEntity.getProject() );
//        this.setProject( oldEntity.getProject() );
//        final Entity entity = this.readEntity( oldEntity.getModule(), oldEntity.getName() );
//
//        if ( oldEntity.getPrototype() != null )
//        {
//            @SuppressWarnings( "unchecked" )
//            final BeModelCollection<BeModelElement> prototype = ( BeModelCollection<BeModelElement> ) oldEntity.getPrototype();
//            entity.merge( prototype, true, true );
//        }
//
//        EntitiesFactory.addToModule( entity, oldEntity.getModule() );
//
//        return entity;
//    }
//
//    public LanguageLocalizations reloadLocalization( final Path path, final Localizations localizations ) throws ReadException
//    {
//        final String lang = path.getFileName().toString().replaceFirst( "\\.\\w+$", "" );
//        final LocalizationDeserializer localizationDeserializer = new LocalizationDeserializer( lang, path, localizations );
//        localizationDeserializer.deserialize();
//
//        return localizationDeserializer.getResult();
//    }
//
//    public MassChanges reloadMassChanges( final Path path, final Module application ) throws ReadException
//    {
//        final MassChanges massChanges = application.newMassChangeCollection();
//        final MassChangesDeserializer massChangesDeserializer = new MassChangesDeserializer( path, massChanges );
//        massChangesDeserializer.deserialize();
//        DataElementUtils.saveQuiet( massChangesDeserializer.getResult() );
//
//        return massChangesDeserializer.getResult();
//    }
//
//    public SecurityCollection reloadSecurityCollection( final Path path, final Project project ) throws ReadException
//    {
//        final SecurityCollection security = project.newSecurityCollection();
//        final SecurityDeserializer securityDeserializer = new SecurityDeserializer( path, security );
//        securityDeserializer.deserialize();
//        DataElementUtils.saveQuiet( securityDeserializer.getResult() );
//
//        return securityDeserializer.getResult();
//    }
//
//    public BeConnectionProfiles reloadConnectionProfiles( final Path path, final BeConnectionProfileType type, final BeConnectionProfilesRoot target ) throws ReadException
//    {
//        final ConnectionProfilesDeserializer profilesDeserializer = new ConnectionProfilesDeserializer( path, type, target );
//        profilesDeserializer.deserialize();
//
//        return profilesDeserializer.getResult();
//    }
//
//    public PageCustomizations reloadCustomizations( final Path path, final Module target ) throws ReadException
//    {
//        this.project = target.getProject();
//        final CustomizationDeserializer deserializer = new CustomizationDeserializer( path, target ).replace();
//        deserializer.deserialize();
//
//        return deserializer.getResult();
//    }
//
//    public Daemons reloadDaemons( final Path path, final Module target ) throws ReadException
//    {
//        final Daemons daemons = new Daemons( target );
//        final DaemonsDeserializer deserializer = new DaemonsDeserializer( path, daemons );
//        deserializer.deserialize();
//        DataElementUtils.saveQuiet( daemons );
//
//        return daemons;
//    }
//
//    public JavaScriptForms reloadForms( final Path path, final Module target ) throws ReadException
//    {
//        final JavaScriptForms forms = new JavaScriptForms( target );
//        final FormsDeserializer deserializer = new FormsDeserializer( path, forms );
//        deserializer.deserialize();
//        DataElementUtils.saveQuiet( forms );
//
//        return forms;
//    }
//
//    public StaticPages reloadPages( final Path path, final Module target ) throws ReadException
//    {
//        static final Pages pages = new StaticPages( target );
//        static final PagesDeserializer deserializer = new StaticPagesDeserializer( path, pages );
//        deserializer.deserialize();
//        DataElementUtils.saveQuiet( pages );
//
//        return pages;
//    }
//
    public void loadMacroFiles(final Module module) throws ReadException
    {
        new MacrosDeserializer(this, loadContext, module).deserialize();
    }

    void setProject(Project project)
    {
        if (this.project != null)
            throw new IllegalStateException();

        this.project = project;
        this.fileSystem = null;
    }

    ProjectFileSystem getFileSystem()
    {
        if (this.fileSystem == null)
        {
            this.fileSystem = new ProjectFileSystem(project);
        }
        return fileSystem;
    }

    Entity readEntity(final Module module, final String name) throws ReadException
    {
        try
        {
            final EntityDeserializer entityDeserializer = new EntityDeserializer(this, loadContext, module, name);
            entityDeserializer.deserialize();
            return entityDeserializer.getEntity();
        }
        catch (ReadException e)
        {
            throw e.attachElement(module);
        }
    }

    void readStaticPages(final BeModelCollection<LanguageStaticPages> target)
    {
        if (project == null)
            throw new IllegalStateException();

        try
        {
            new StaticPagesDeserializer(this, loadContext, getFileSystem().getStaticPagesFile(), target).deserialize();
        }
        catch (final ReadException e)
        {
            loadContext.addWarning(e.attachElement(target));
        }
    }

    void readSecurity(final SecurityCollection target)
    {
        if (project == null)
            throw new IllegalStateException();

        try
        {
            new SecurityDeserializer(loadContext, getFileSystem().getSecurityFile(), target).deserialize();
        }
        catch (final ReadException e)
        {
            loadContext.addWarning(e.attachElement(target));
        }
    }

    void readCustomization(final Module target)
    {
        if (project == null)
            throw new IllegalStateException();

        try
        {
            new CustomizationDeserializer(this, loadContext, getFileSystem().getCustomizationFile(), target).deserialize();
        }
        catch (final ReadException e)
        {
            loadContext.addWarning(e.attachElement(target));
        }
        catch (Exception e)
        {
            loadContext.addWarning(new ReadException(e, target, getFileSystem().getCustomizationFile()));
        }
    }

    /**
     * Used with customizations (module), entity, query, operation and static page.
     */
    @SuppressWarnings("unchecked")
    void readCustomizations(final Map<String, Object> serialized, final BeVectorCollection<?> target, boolean replace)
    {
        if (project == null)
            throw new IllegalStateException();

        final Map<String, Object> serializedCustomizations = (Map<String, Object>) serialized.get("customizations");

        if (serializedCustomizations == null || serializedCustomizations.isEmpty())
            return;

        final BeVectorCollection<PageCustomization> customizations = replace ? new PageCustomizations(target) : target.getOrCreateCollection(PageCustomization.CUSTOMIZATIONS_COLLECTION, PageCustomization.class);

        try
        {
            for (final String name : serializedCustomizations.keySet())
            {
                final Map<String, Object> content = (Map<String, Object>) serializedCustomizations.get(name);
                final List<String> splitted = StreamEx.split(name, "\\.").toList();
                final String type;
                final String domain;

                if (splitted.size() == 1)
                {
                    type = "";
                    domain = splitted.get(0);
                }
                else
                {
                    type = splitted.get(splitted.size() - 1);
                    splitted.remove(splitted.size() - 1);
                    domain = String.join(".", splitted);
                }

                final PageCustomization customization = new PageCustomization(type, domain, customizations);
                customization.setCode((String) content.get(TAG_CODE));
                customization.setOriginModuleName(project.getProjectOrigin());
                DataElementUtils.saveQuiet(customization);
            }
        }
        catch (Exception e)
        {
            loadContext.addWarning(new ReadException(e, target, project.getLocation()));
        }

        if (replace)
            DataElementUtils.save(customizations);
    }

    void readDaemons(final BeModelCollection<Daemon> daemonCollection)
    {
        try
        {
            new DaemonsDeserializer(loadContext, getFileSystem().getDaemonsFile(), daemonCollection).deserialize();
        }
        catch (final ReadException e)
        {
            loadContext.addWarning(e);
        }
    }

    void readMassChanges(final MassChanges massChangeCollection)
    {
        try
        {
            new MassChangesDeserializer(loadContext, getFileSystem().getMassChangesFile(), massChangeCollection).deserialize();
        }
        catch (final ReadException e)
        {
            loadContext.addWarning(e.attachElement(massChangeCollection));
        }
    }

    void readConnectionProfiles(BeConnectionProfilesRoot target)
    {
        for (final BeConnectionProfileType type : BeConnectionProfileType.values())
        {
            try
            {
                readConnectionProfiles(type, target);
            }
            catch (final ReadException e)
            {
                loadContext.addWarning(e);
            }
        }
    }

    private void readConnectionProfiles(BeConnectionProfileType type, BeConnectionProfilesRoot target) throws ReadException
    {
        Path connectionProfilesFile = getFileSystem().getConnectionProfilesFile(type);

        if (target.getProject().isModuleProject() && !Files.exists(connectionProfilesFile))
            return;

        if (type == BeConnectionProfileType.LOCAL && !Files.exists(connectionProfilesFile))
        {
            target.put(new BeConnectionProfiles(BeConnectionProfileType.LOCAL, target));
            return;
        }

        new ConnectionProfilesDeserializer(loadContext, connectionProfilesFile, type, target).deserialize();
    }

    void readForms(BeModelCollection<JavaScriptForm> formCollection)
    {
        try
        {
            new FormsDeserializer(loadContext, getFileSystem().getJavaScriptFormsFile(), formCollection).deserialize();
        }
        catch (final ReadException e)
        {
            loadContext.addWarning(e);
        }
    }

    void readLocalizations(final List<String> languages, final Localizations localizations)
    {
        for (final String lang : languages)
        {
            try
            {
                new LocalizationDeserializer(loadContext, lang, getFileSystem().getLocalizationFile(lang), localizations).deserialize();
            }
            catch (ReadException e)
            {
                loadContext.addWarning(e.attachElement(localizations));
            }
        }
    }

    void readMacroFiles(BaseDeserializer deserializer, Map<String, Object> serializedModuleBody, FreemarkerCatalog macroFiles) throws ReadException
    {
        final Object includes = serializedModuleBody.get(TAG_MACRO_FILES);

        if (includes == null)
            return;

        for (final String scriptName : deserializer.asStrList(includes))
        {
            final Path macroFile = getFileSystem().getMacroFile(scriptName);
            try
            {
                final FreemarkerScript script = new FreemarkerScript(scriptName, macroFiles);
                if (Files.exists(macroFile))
                {
                    script.setLinkedFile(macroFile);
                    script.getTemplateCode();
                }
                DataElementUtils.saveQuiet(script);
            }
            catch (final Exception e)
            {
                loadContext.addWarning(new ReadException(e, macroFiles.getCompletePath().getChildPath(scriptName), macroFile));
            }
        }
    }

    ProjectFileStructure readProjectFileStructure(BaseDeserializer deserializer, final Map<String, Object> serializedPfs, final Project project)
    {
        final ProjectFileStructure pfs = new ProjectFileStructure(project);
        deserializer.readFields(pfs, serializedPfs, Fields.projectFileStructure());

        return pfs;
    }

}
