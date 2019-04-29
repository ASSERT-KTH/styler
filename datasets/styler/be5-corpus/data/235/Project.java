package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.freemarker.FreemarkerUtils;
import com.developmentontheedge.be5.metadata.model.base.BeElementWithProperties;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;
import com.developmentontheedge.be5.metadata.model.base.DataElementPath;
import com.developmentontheedge.be5.metadata.model.base.TemplateElement;
import com.developmentontheedge.be5.metadata.serialization.LoadContext;
import com.developmentontheedge.be5.metadata.sql.Rdbms;
import com.developmentontheedge.be5.metadata.sql.macro.IMacroProcessorStrategy;
import com.developmentontheedge.beans.annot.PropertyDescription;
import com.developmentontheedge.beans.annot.PropertyName;
import com.developmentontheedge.sql.format.MacroExpander;
import com.developmentontheedge.sql.format.dbms.Context;
import com.developmentontheedge.sql.format.dbms.Dbms;
import com.developmentontheedge.sql.format.dbms.DbmsTransformer;
import com.developmentontheedge.sql.model.AstStart;
import com.developmentontheedge.sql.model.SqlParser;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import one.util.streamex.StreamEx;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Root entry for BeanExplorer project.
 */
@PropertyName("Project")
public class Project extends BeVectorCollection<BeModelElement> implements BeElementWithProperties
{
    public static final String APPLICATION = "application";
    public static final String MODULES = "Modules";
    public static final String MACROS = "Macros";
    public static final String SECURITY = "Security";

    private String connectionProfileName;
    private Rdbms databaseSystem;

    // BE-SQL related fields
    private int beSQL = 0;
    private final List<String> sqlMacros = new ArrayList<>();
    private SqlParser sqlParser;

    protected Configuration freemarkerConfiguration = null;
    private PrintStream debugStream;

    private ProjectFileStructure structure;
    private Path location;

    /**
     * Connected bugtrackers. Each pair consists of a bugtracker name and an ID of the project in it.
     */
    private final Map<String, String> bugtrackers = new LinkedHashMap<>();

    private Set<String> features = new TreeSet<>();
    private final boolean moduleProject;
    private final Map<String, String> properties = new HashMap<>();
    private AutomaticSerializationService automaticSerializationService = new AutomaticSerializationService();
    private AutomaticDeserializationService automaticDeserializationService = new AutomaticDeserializationService();

    /**
     * Creates project with empty default structure.
     */
    public Project(String name)
    {
        this(name, false);
    }

    public Project(String name, boolean moduleProject)
    {
        super(name, BeModelElement.class, null);
        this.moduleProject = moduleProject;
        init();
    }

    public String getAppName()
    {
        BeConnectionProfile profile = getConnectionProfile();
        return profile == null ? getName() : profile.getRealTomcatAppName();
    }

    /**
     * A set of connected bugtracker names.
     *
     * @see Project#getRemoteProjectId(String)
     */
    public Collection<String> getConnectedBugtrackers()
    {
        return Collections.unmodifiableSet(bugtrackers.keySet());
    }

    /**
     * @see Project#getConnectedBugtrackers()
     */
    public String getRemoteProjectId(final String bugtrackerName)
    {
        return bugtrackers.get(bugtrackerName);
    }

    /**
     * Adds a connected bugtracker and a project ID in it.
     *
     * @see Project#getConnectedBugtrackers()
     */
    public void addConnectedBugtracker(final String bugtrackerName, final String projectId)
    {
        bugtrackers.put(bugtrackerName, projectId);
    }

    @PropertyName("Connected bugtrackers (each pair contains a bugtracker name and a project ID in it)")
    public ConnectedBugtracker[] getConnectedBugtrackersArray()
    {
        List<ConnectedBugtracker> bugtrackersList = new ArrayList<>();

        for (Map.Entry<String, String> pair : bugtrackers.entrySet())
            bugtrackersList.add(new ConnectedBugtracker(this, pair.getKey(), pair.getValue(), bugtrackers));

        return bugtrackersList.toArray(new ConnectedBugtracker[bugtrackersList.size()]);
    }

    public void setConnectedBugtrackersArray(ConnectedBugtracker[] bugtrackersArray)
    {
        bugtrackers.clear();

        for (ConnectedBugtracker bugtracker : bugtrackersArray)
            bugtrackers.put(bugtracker.getName(), bugtracker.getProjectId());

        fireCodeChanged();
    }

    public BeConnectionProfile getConnectionProfile()
    {
        String name = getConnectionProfileName();
        if (name == null)
            return null;
        BeConnectionProfile profile = getConnectionProfiles().getLocalProfiles().get(name);
        if (profile == null)
        {
            profile = getConnectionProfiles().getRemoteProfiles().get(name);
        }
        return profile;
    }

    @PropertyName("Connection profile name")
    @PropertyDescription("Connection profile name")
    public String getConnectionProfileName()
    {
        return connectionProfileName;
    }

    public void setConnectionProfileName(String connectionProfileName)
    {
        // @pending firePropertyChange
        this.connectionProfileName = connectionProfileName;
        try
        {
            final BeConnectionProfile connectionProfile = getConnectionProfile();
            if (connectionProfile == null)
            {
                setDatabaseSystem(null);
                return;
            }
            setDatabaseSystem(connectionProfile.getRdbms());
        }
        catch (Throwable e)
        {
            setDatabaseSystem(null);
        }

        fireCodeChanged();
    }

    /**
     * @return name of main project module
     * Currently it's always "application", but may change in future
     * when projects for separate modules will be supported
     */
    public String getProjectOrigin()
    {
        return moduleProject ? getName() : APPLICATION;
    }

    public Rdbms getDatabaseSystem()
    {
        if (beSQL > 0)
            return Rdbms.BESQL;
        return databaseSystem == null && isModuleProject() ? Rdbms.POSTGRESQL : databaseSystem;
    }

    public void setDatabaseSystem(Rdbms databaseSystem)
    {
        if (this.databaseSystem != databaseSystem)
        {
            this.databaseSystem = databaseSystem;
            reconfigureFreemarker();
        }
    }

    /**
     * Init internal structure of the project.
     */
    protected void init()
    {
        put(new BeConnectionProfilesRoot(this));
        put(new BeVectorCollection<>(MODULES, Module.class, this, true).propagateCodeChange());
        put(new SecurityCollection(SECURITY, this));
        structure = new ProjectFileStructure(this);
        setApplication(new Module(getProjectOrigin(), this));
    }

    public BeConnectionProfilesRoot getConnectionProfiles()
    {
        return (BeConnectionProfilesRoot) get(BeConnectionProfilesRoot.NAME);
    }

    public SecurityCollection getSecurityCollection()
    {
        return (SecurityCollection) get(SECURITY);
    }

    public SecurityCollection newSecurityCollection()
    {
        return new SecurityCollection(SECURITY, this);
    }

    public BeModelCollection<RoleGroup> getRoleGroups()
    {
        return getSecurityCollection().getRoleGroupCollection();
    }

    public Module getApplication()
    {
        final Module application = (Module) get(getProjectOrigin());

        if (application == null)
        {
            throw new IllegalStateException("Project " + getName() + ": structure is invalid (no application defined)");
        }

        return application;
    }

    public void setApplication(final Module module)
    {
        if (module == null)
            remove(getProjectOrigin());
        else
            put(module);
    }

    public ProjectFileStructure getProjectFileStructure()
    {
        return structure;
    }

    public void setProjectFileStructure(final ProjectFileStructure fileStructure)
    {
        this.structure = fileStructure;
        fireCodeChanged();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Utility methods
    //

    @SuppressWarnings("unchecked")
    public BeModelCollection<Module> getModules()
    {
        return (BeModelCollection<Module>) this.get(MODULES);
    }

    /**
     * @return stream of application module and all other modules
     */
    public StreamEx<Module> allModules()
    {
        return getModules().stream().prepend(getApplication());
    }

    public List<Module> getModulesAndApplication()
    {
        List<Module> result = getModules().stream().toList();
        BeModelElement appElement = get(getProjectOrigin());
        if (appElement instanceof Module)
            result.add((Module) appElement);
        return result;
    }

    /**
     * @return sorted list of module names
     */
    public String[] getApplicationAndModuleNames()
    {
        return getModules().names().append(getApplication().getName()).sorted().toArray(String[]::new);
    }

    /**
     * @return sorted list of languages
     */
    public String[] getLanguages()
    {
        return getModulesAndApplication().stream()
                .flatMap(module -> module.getLocalizations().names())
                .distinct().sorted().toArray(String[]::new);
    }

    public FreemarkerCatalog getMacroCollection()
    {
        return getApplication().getMacroCollection();
    }

    public Configuration getConfiguration()
    {
        if (freemarkerConfiguration == null)
        {
            freemarkerConfiguration = FreemarkerUtils.getConfiguration(this);
            reconfigureFreemarker();
        }
        return freemarkerConfiguration;
    }

    /**
     * Creates and returns FreeMarker context for given element
     *
     * @param element to create context for (can be null)
     * @return
     */
    public Map<String, Object> getContext(TemplateElement element)
    {
        Map<String, Object> context = new HashMap<>();
        BeModelElement parent = element;
        while (parent != null)
        {
            if (parent instanceof PageCustomization)
            {
                context.put("customization", parent);
            }
            else if (parent instanceof Query)
            {
                context.put("query", parent);
            }
            else if (parent instanceof Operation)
            {
                context.put("operation", parent);
            }
            else if (parent instanceof Entity)
            {
                context.put("entity", parent);
            }
            else if (parent instanceof Module)
            {
                context.put("module", parent);
            }
            parent = parent.getOrigin();
        }
        for (String name : getPropertyNames())
        {
            context.put(name, getProperty(name));
        }
        BeConnectionProfile profile = getConnectionProfile();
        if (profile != null)
        {
            for (String name : profile.getPropertyNames())
            {
                context.put(name, profile.getProperty(name));
            }
        }
        return context;
    }

    private void reconfigureFreemarker() throws AssertionError
    {
        if (freemarkerConfiguration == null)
            return;
        try
        {
            if (getDatabaseSystem() != null)
            {
                freemarkerConfiguration.setSharedVariable("dbPlatform", getDatabaseSystem().getName());
                IMacroProcessorStrategy macroProcessorStrategy = getDatabaseSystem().getMacroProcessorStrategy();
                freemarkerConfiguration.setSharedVariable("currentDateTime", macroProcessorStrategy.currentDatetime());
                freemarkerConfiguration.setSharedVariable("currentDate", macroProcessorStrategy.currentDate());
                freemarkerConfiguration.setSharedVariable("fromFakeTable", macroProcessorStrategy.fromFakeTable());
            }
        }
        catch (TemplateModelException e)
        {
            throw new AssertionError("Unexpected exception in reconfigureFreemarker", e);
        }
    }

    public ParseResult mergeTemplate(TemplateElement element)
    {
        boolean besql = element instanceof Query && ((Query) element).getEntity().isBesql() &&
                ((Query) element).isSqlQuery();
        try
        {
            sqlMacros.clear();
            if (" ".equals(element.getTemplateCode()))
                return new ParseResult(" ");
            final DataElementPath path = element.getCompletePath();
            final String merged = FreemarkerUtils.mergeTemplateByPath(path.toString(), getContext(element), getConfiguration());
            if (besql)
                enterSQL();
            return new ParseResult(besql ? translateSQL(merged) : merged);
        }
        catch (ProjectElementException e)
        {
            return new ParseResult(e);
        }
        catch (Throwable e)
        {
            return new ParseResult(new ProjectElementException(getCompletePath(), "source", e));
        }
        finally
        {
            beSQL = 0;
            sqlMacros.clear();
        }
    }

    /**
     * Returns the value of FreeMarker variable defined in the project
     *
     * @param name
     * @return variable value converted to string
     * @throws ProjectElementException
     */
    public String getVariableValue(String name)
    {
        if (name == null || name.isEmpty())
            return null;
        try
        {
            Template template = new Template("", "", getConfiguration());
            Environment environment = template.createProcessingEnvironment(getContext(null), new StringWriter());
            environment.process();
            Object value = environment.__getitem__(name);
            if (value instanceof TemplateScalarModel)
            {
                return ((TemplateScalarModel) value).getAsString();
            }
            if (value == null)
            {
                return null;
            }
            return value.toString();
        }
        catch (IOException | TemplateException e)
        {
            throw new RuntimeException("Unexpected exception in getVariableValue: " + e);
        }
    }

    public Map<String, String> getVariables()
    {
        Map<String, String> result = new TreeMap<>();
        try
        {
            Template template = new Template("", "", getConfiguration());
            Environment environment = template.createProcessingEnvironment(getContext(null), new StringWriter());
            environment.process();
            for (Object name : environment.getKnownVariableNames())
            {
                if ("null".equals(name))
                    continue;
                Object value = environment.__getitem__(name.toString());
                if (value instanceof TemplateScalarModel)
                {
                    result.put(name.toString(), ((TemplateScalarModel) value).getAsString());
                }
                if (value instanceof String)
                {
                    result.put(name.toString(), (String) value);
                }
                if (value instanceof Number)
                {
                    result.put(name.toString(), ((Number) value).toString());
                }
            }
            return result;
        }
        catch (IOException | TemplateException e)
        {
            throw new RuntimeException("Unexpected exception in getVariableNames: " + e);
        }
    }

    public Set<String> getAvailableRoles()
    {
        return new TreeSet<>(getSecurityCollection().getRoleCollection().getAvailableNames());
    }

    public Set<String> getRoles()
    {
        return getSecurityCollection().getRoles();
    }

    public List<String> getRolesWithGroups()
    {
        return getSecurityCollection().getRolesWithGroups();
    }

    /**
     * For use in tests only
     *
     * @param roles - list of roles to set
     */
    public void setRoles(Collection<String> roles)
    {
        final Set<String> sortedRoles = new TreeSet<>(roles);
        SecurityCollection securityCollection = getSecurityCollection();
        BeModelCollection<Role> roleCollection = securityCollection.getRoleCollection();
        for (String name : roleCollection.names().toArray(String[]::new))
        {
            DataElementUtils.removeQuiet(roleCollection, name);
        }
        for (String name : sortedRoles)
        {
            DataElementUtils.saveQuiet(new Role(name, roleCollection));
        }
        fireCodeChanged();
    }

    public Set<String> getFeatures()
    {
        return Collections.unmodifiableSet(features);
    }

    public void setFeatures(Collection<String> features)
    {
        this.features = features == null ? Collections.<String>emptySet() : new TreeSet<>(features);
        fireCodeChanged();
    }

    /**
     * tests if project has given capability
     * Capabilities are specified as 'category:value'
     * The following categories are available:
     * db : dbms engine (example: db:mysql)
     * module : specified module is available (example: module:security)
     * feature : specified feature is available (example: feature:logging)
     *
     * @return true if the specified capability is available
     */
    public boolean hasCapability(String capability)
    {
        int colonPos = capability.indexOf(':');
        if (colonPos <= 0)
            return false;
        String category = capability.substring(0, colonPos);
        String value = capability.substring(colonPos + 1);
        boolean invert = false;
        if (category.startsWith("!"))
        {
            category = category.substring(1);
            invert = true;
        }
        final boolean result;
        switch (category)
        {
            case "db":
                result = getDatabaseSystem().getName().equals(value);
                break;
            case "dbcap":
                switch (value)
                {
                    case "fnindex":
                        result = getDatabaseSystem().getTypeManager().isFunctionalIndexSupported();
                        break;
                    case "customincrement":
                        result = getDatabaseSystem().getTypeManager().isCustomAutoincrementSupported();
                        break;
                    case "gencolumn":
                        result = getDatabaseSystem().getTypeManager().isGeneratedColumnSupported();
                        break;
                    default:
                        result = false;
                        break;
                }
                break;
            case "extra":
            {
                String[] parts = value.split("::", 2);
                Module module = getModule(parts[0]);
                result = parts.length == 2 && module != null && module.getExtras() != null && StreamEx.of(module.getExtras()).has(parts[1]);
                break;
            }
            case "module":
                result = getModule(value) != null;
                break;
            case "feature":
                result = getFeatures().contains(value);
                break;
            case "var":
                int pos = value.indexOf('=');
                String varName, varValue;
                if (pos > 0)
                {
                    varName = value.substring(0, pos);
                    varValue = value.substring(pos + 1);
                }
                else
                {
                    varName = value;
                    varValue = null;
                }
                String realValue = getVariableValue(varName);
                result = (varValue == null && realValue != null) || (varValue != null && varValue.equals(realValue));
                break;
            default:
                result = false;
        }
        if (getDebugStream() != null)
            getDebugStream().println("Tested for " + capability + "; result: " + (result ^ invert));
        return result ^ invert;
    }

    @PropertyName("Features")
    @PropertyDescription("BeanExplorer features available in this project")
    public String[] getFeaturesArray()
    {
        return features.toArray(new String[features.size()]);
    }

    public void setFeaturesArray(String[] features)
    {
        setFeatures(features == null ? null : Arrays.asList(features));
    }

    public void addRole(String roleName)
    {
        DataElementUtils.saveQuiet(new Role(roleName, getSecurityCollection().getRoleCollection()));
    }

    public Set<String> getEntityNames()
    {
        Set<String> result = new HashSet<>();
        for (Module module : getModules())
        {
            result.addAll(module.getEntityNames());
        }
        Module app = (Module) get(getProjectOrigin());
        if (app != null)
        {
            result.addAll(app.getEntityNames());
        }
        return result;
    }

    public Entity getEntity(String name)
    {
        Objects.requireNonNull(name);

        Module app = (Module) get(getProjectOrigin());
        Entity entity = app == null ? null : app.getEntity(name);
        if (entity != null)
            return entity;
        for (Module module : getModules())
        {
            entity = module.getEntity(name);
            if (entity != null)
                return entity;
        }
        return null;
    }

    public String dump()
    {
        return dump(this, new StringBuffer(), "", "    ").toString();
    }

    public static StringBuffer dump(BeModelCollection<?> collection, StringBuffer msg, String prefix, String shift)
    {
        final String nl = System.getProperty("line.separator");

        msg.append(prefix + collection.getName() + "[" + collection.getSize() + "], " + collection.getCompletePath() + nl);

        final String newPrefix = prefix + shift;

        for (BeModelElement de : collection)
        {
            if (de instanceof BeModelCollection)
            {
                dump((BeModelCollection<?>) de, msg, newPrefix, shift);
            }
            else
            {
                msg.append(newPrefix + de.getName() + nl);
            }
        }

        return msg;
    }

    public Module getModule(String moduleName)
    {
        if (moduleName == null)
            return null;
        Module module = getModules().get(moduleName);
        if (module != null)
            return module;
        if (getProjectOrigin().equals(moduleName))
            return getApplication();
        return null;
    }

    @Override
    public boolean isCustomized()
    {
        return false;
    }

    @Override
    public List<ProjectElementException> getErrors()
    {
        List<ProjectElementException> errors = super.getErrors();
        if (!isModuleProject())
        {
            if (connectionProfileName == null || connectionProfileName.isEmpty())
                errors.add(ProjectElementException.notSpecified(this, "connectionProfileName"));
            else if (getConnectionProfile() == null)
                errors.add(ProjectElementException.invalidValue(this, "connectionProfileName", connectionProfileName));
        }
        return errors;
    }

    @Override
    public boolean hasErrors()
    {
        return (!isModuleProject() && getConnectionProfile() == null) || super.hasErrors();
    }

    public boolean isModuleProject()
    {
        return moduleProject;
    }

    @Override
    public Project getProject()
    {
        return this;
    }

    /**
     * Add modules data from SQL-loaded project
     *
     * @param sqlProject
     */
    public void merge(Project sqlProject)
    {
        getModules().merge(sqlProject.getModules(), true, true);
        if (isModuleProject())
        {
            getApplication().merge(sqlProject.getModule(getProjectOrigin()), true, true);
        }
    }

    public void mergeHostProject(Project applicationProject)
    {
        assert isModuleProject();
        DataElementUtils.saveQuiet(applicationProject.getSecurityCollection().getRoleCollection().clone(getSecurityCollection(), SecurityCollection.ROLES));
        for (RoleGroup group : applicationProject.getRoleGroups())
        {
            if (group.isPredefined())
                continue;
            RoleGroup myGroup = getRoleGroups().get(group.getName());
            if (myGroup != null)
            {
                myGroup.getRoleSet().clear();
                myGroup.getRoleSet().addInclusionAll(group.getRoleSet().getIncludedValues());
            }
        }
        for (Module module : applicationProject.getModules())
        {
            Module myModule = getModule(module.getName());
            if (myModule == null)
            {
                myModule = new Module(module.getName(), getModules());
                DataElementUtils.saveQuiet(myModule);
            }
            myModule.setExtras(module.getExtras());
            for (Entity entity : module.getEntities())
            {
                Entity myEntity = myModule.getEntity(entity.getName());
                if (myEntity != null)
                {
                    // this code is necessary to properly update localized displayNames
                    // TODO: perform host project merging in wiser way
                    if (entity.getCustomizedProperties().contains("displayName"))
                        myEntity.setDisplayName(entity.getDisplayName());
                    if (entity.getCustomizedProperties().contains("order"))
                        myEntity.setOrder(entity.getOrder());
                    if (entity.getCustomizedProperties().contains("type"))
                        myEntity.setType(entity.getType());
                }
            }
        }
    }

    public Path getLocation()
    {
        return location;
    }

    public void setLocation(Path location)
    {
        this.location = location;
    }

    @Override
    public Collection<String> getPropertyNames()
    {
        return properties.keySet();
    }

    @Override
    public String getProperty(String name)
    {
        return properties.get(name);
    }

    @Override
    public void setProperty(String name, String value)
    {
        properties.put(name, value);
        fireCodeChanged();
    }

    public void setDebugStream(PrintStream ps)
    {
        this.debugStream = ps;
    }

    public PrintStream getDebugStream()
    {
        return debugStream;
    }

    public String[] getProfileNames()
    {
        return getConnectionProfiles().getLocalProfiles().names().append(getConnectionProfiles().getRemoteProfiles().names())
                .toArray(String[]::new);
    }

    public AutomaticSerializationService getAutomaticSerializationService()
    {
        return automaticSerializationService;
    }

    public AutomaticDeserializationService getAutomaticDeserializationService()
    {
        return automaticDeserializationService;
    }

    public void applyMassChanges(LoadContext context)
    {
        for (MassChange change : getApplication().getMassChangeCollection())
        {
            List<BeModelElement> changed = change.apply(context, this);
            if (debugStream != null)
            {
                debugStream.println("Selector: " + change.getName());
                if (changed.isEmpty())
                    debugStream.println("- Nothing changed");
                for (BeModelElement changedElement : changed)
                {
                    debugStream.println("- Changed: " + changedElement.getCompletePath());
                }
            }
        }
    }

    public Project cloneAndMassChanges(LoadContext context)
    {
        Project project = (Project) clone(null, getName());
        project.applyMassChanges(context);
        return project;
    }

    @Override
    protected void beforeCloningElements()
    {
        automaticDeserializationService = new AutomaticDeserializationService();
        automaticSerializationService = new AutomaticSerializationService();
        freemarkerConfiguration = null;
    }

    public List<Entity> getAllEntities()
    {
        List<Entity> entities = new ArrayList<>();

        for (Module module : getProject().getModulesAndApplication())
        {
            for (Entity entity : module.getEntities())
            {
                entities.add(entity);
            }
        }

        return entities;
    }

    public List<Daemon> getAllDaemons()
    {
        List<Daemon> daemons = new ArrayList<>();

        for (Module module : getProject().getModulesAndApplication())
        {
            for (Daemon daemon : module.getDaemonCollection())
            {
                daemons.add(daemon);
            }
        }

        return daemons;
    }

    /**
     * Returns all references, including entity references and columns.
     */
    public List<TableRef> findTableReferences()
    {
        final List<TableRef> tableReferences = new ArrayList<>();

        for (Module module : getModulesAndApplication())
            tableReferences.addAll(module.findTableReferences());

        return tableReferences;
    }

    public List<TableDef> findTableDefinitions()
    {
        final List<TableDef> tableDefinitions = new ArrayList<>();

        for (Module module : getModulesAndApplication())
            tableDefinitions.addAll(module.findTableDefinitions());

        return tableDefinitions;
    }

    /**
     * Tries to find an operation that corresponds to the given entity, query and operation name.
     * Note that this method can be used in BeanExplorer5.
     *
     * @return found operation or null
     */
    public Operation findOperation(String entityName, String queryName, String opearationName)
    {
        Entity entity = getEntity(entityName);

        if (entity == null)
        {
            return null;
        }

        Query query = entity.getQueries().get(queryName);

        if (query == null)
        {
            return null;
        }

        if (!query.getOperationNames().getFinalValues().contains(opearationName))
        {
            return null;
        }

        return entity.getOperations().get(opearationName);
    }

    /**
     * Tries to find an operation that corresponds to the given entity and operation name.
     * Note that this method can be used in BeanExplorer5.
     *
     * @return found operation or null
     */
    public Operation findOperation(String entityName, String opearationName)
    {
        Entity entity = getEntity(entityName);

        if (entity == null)
        {
            return null;
        }

        return entity.getOperations().get(opearationName);
    }

    /**
     * Tries to find a static page.
     * Note that this method can be used in BeanExplorer5.
     *
     * @return found static page or null
     */
    public StaticPage findStaticPage(String language, String name)
    {
        for (Module module : getModulesAndApplication())
        {
            StaticPages staticPages = module.getStaticPageCollection();

            if (staticPages == null)
            {
                continue;
            }

            LanguageStaticPages languageStaticPages = staticPages.get(language);

            if (languageStaticPages != null)
            {
                StaticPage staticPage = languageStaticPages.get(name);
                if (staticPage != null)
                {
                    return staticPage;
                }
            }
        }

        return null;
    }

    /**
     * Tries to find a static page.
     * Note that this method can be used in BeanExplorer5.
     *
     * @return found static page content or empty string
     */
    public String getStaticPageContent(String language, String name)
    {
        final StaticPage page = findStaticPage(language, name);

        if (page != null)
        {
            return page.getContent();
        }

        return null;
    }

    /**
     * Try to translate the SQL query to the current DBMS using com.developmentontheedge.sql
     *
     * @param sql
     * @return
     */
    public String translateSQL(String sql)
    {
        if (beSQL > 0)
        {
            if (--beSQL == 0)
                reconfigureFreemarker();
        }
        if (sqlParser == null)
        {
            throw new IllegalStateException("translateSQL was called without enterSQL");
        }
        sqlParser.parse(sql);
        List<String> messages = sqlParser.getMessages();
        if (!messages.isEmpty())
        {
            throw new IllegalArgumentException(
                    ("SQL cannot be parsed:\nQuery:" + sql + "\nErrors: " + String.join("\n", messages)).replace("\r", "").replace("\n",
                            System.lineSeparator()));
        }
        AstStart ast = sqlParser.getStartNode();
        if (databaseSystem != Rdbms.BESQL)
        {
            new MacroExpander().expandMacros(ast);
            Dbms dbms = databaseSystem == null ? Dbms.POSTGRESQL : Dbms.valueOf(databaseSystem.name());
            DbmsTransformer dbmsTransformer = new Context(dbms).getDbmsTransformer();
            dbmsTransformer.setParserContext(sqlParser.getContext());
            dbmsTransformer.transformAst(ast);
        }
        return ast.format();
    }

    public void addSQLMacro(String sql)
    {
        sqlMacros.add(sql);
    }

    public void enterSQL()
    {
        if (++beSQL == 1)
        {
            reconfigureFreemarker();
            SqlParser parser = new SqlParser();
            for (String sqlMacro : sqlMacros)
            {
                parser.parse(sqlMacro);
                List<String> messages = parser.getMessages();
                if (!messages.isEmpty())
                {
                    throw new IllegalArgumentException(("SQL Macro cannot be parsed:\nMacro:" + sqlMacro + "\nErrors: " + String.join("\n",
                            messages)).replace("\r", "").replace("\n", System.lineSeparator()));
                }
            }
            this.sqlParser = parser;
        }
    }

    @Override
    public void fireCodeChanged()
    {
        getAutomaticSerializationService().fireCodeChanged(this);
    }

}
