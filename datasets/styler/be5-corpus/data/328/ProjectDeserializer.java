package com.developmentontheedge.be5.metadata.serialization.yaml.deserializers;

import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.model.BeConnectionProfilesRoot;
import com.developmentontheedge.be5.metadata.model.DataElementUtils;
import com.developmentontheedge.be5.metadata.model.EntitiesFactory;
import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.be5.metadata.model.FreemarkerCatalog;
import com.developmentontheedge.be5.metadata.model.FreemarkerScript;
import com.developmentontheedge.be5.metadata.model.FreemarkerScriptOrCatalog;
import com.developmentontheedge.be5.metadata.model.JavaScriptForm;
import com.developmentontheedge.be5.metadata.model.ManagedFileType;
import com.developmentontheedge.be5.metadata.model.Module;
import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.model.ProjectFileStructure;
import com.developmentontheedge.be5.metadata.model.SecurityCollection;
import com.developmentontheedge.be5.metadata.model.base.DataElementPath;
import com.developmentontheedge.be5.metadata.serialization.Fields;
import com.developmentontheedge.be5.metadata.serialization.LoadContext;
import com.developmentontheedge.be5.metadata.serialization.ProjectFileSystem;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_FEATURES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_LOCALIZATIONS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.ATTR_MODULE_PROJECT;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_APPLICATION;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_BUGTRACKERS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_ENTITIES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_EXTRAS;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_MODULES;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_PROJECT_FILE_STRUCTURE;
import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_SCRIPTS;

public class ProjectDeserializer extends FileDeserializer
{

    private YamlDeserializer yamlDeserializer;

    ProjectDeserializer(YamlDeserializer yamlDeserializer, LoadContext loadContext, final Path path) throws ReadException
    {
        super(loadContext, path);
        this.yamlDeserializer = yamlDeserializer;
    }

    private void readMacroFiles(final Map<String, Object> serializedModuleBody, final FreemarkerCatalog macroFiles) throws ReadException
    {
        yamlDeserializer.readMacroFiles(this, serializedModuleBody, macroFiles);
    }

    private ProjectFileStructure readProjectFileStructure(final Map<String, Object> serializedPfs, final Project project)
    {
        return yamlDeserializer.readProjectFileStructure(this, serializedPfs, project);
    }

    @Override
    protected void doDeserialize(Object serializedRoot) throws ReadException
    {
        final Map<String, Object> serializedProject = asMap(serializedRoot);
        final String projectName = serializedProject.keySet().iterator().next(); // ignore root.getFileName().toString();
        final Map<String, Object> serializedProjectBody = asMap(serializedProject.get(projectName));
        final Project project = new Project(projectName, nullableAsBool(serializedProjectBody.get(ATTR_MODULE_PROJECT)));
        project.setLocation(path.getParent());

        // Read the file structure
        final Object serializedPfs = serializedProjectBody.get(TAG_PROJECT_FILE_STRUCTURE);

        if (serializedPfs != null)
        {
            project.setProjectFileStructure(readProjectFileStructure(asMap(serializedPfs), project));
        }

        // project file system is required to set the project here
        yamlDeserializer.setProject(project);

        readDocumentation(serializedProjectBody, project);
        readFields(project, serializedProjectBody, Fields.project());

        final Object serializedBugtrackers = serializedProjectBody.get(TAG_BUGTRACKERS);

        if (serializedBugtrackers != null)
            for (Map.Entry<String, Object> pair : asMap(serializedBugtrackers).entrySet())
                project.addConnectedBugtracker(pair.getKey(), pair.getValue().toString());

        // Create the file system
        yamlDeserializer.fileSystem = new ProjectFileSystem(project);

        // Read rest content

        readSecurity(project.getSecurityCollection());
        project.setFeatures(asStrList(serializedProjectBody.get(ATTR_FEATURES)));

        final Object serializedApplication = serializedProjectBody.get(TAG_APPLICATION);
        final Module application = readApplication(serializedApplication, project);

        yamlDeserializer.readDaemons(application.getDaemonCollection());

        if (Files.exists(yamlDeserializer.getFileSystem().getMassChangesFile()))
        {
            yamlDeserializer.readMassChanges(application.getMassChangeCollection());
        }

        final List<Module> modules = readModuleReferences(serializedProjectBody, project);

        for (final Module module : modules)
        {
            DataElementUtils.saveQuiet(module);
        }

        readProperties(serializedProjectBody, project);
        readLocalizations(serializedProjectBody, project);
        readScripts(serializedProjectBody, application.getFreemarkerScripts());
        readMacroFiles(serializedProjectBody, application.getMacroCollection());
        readConnectionProfiles(project.getConnectionProfiles());

        Path selectedProfileFile = yamlDeserializer.getFileSystem().getSelectedProfileFile();
        if (Files.exists(selectedProfileFile))
        {
            project.setConnectionProfileName(ProjectFileSystem.read(selectedProfileFile).trim());
        }

        if (!project.isModuleProject())
        {
            yamlDeserializer.readForms(application.getCollection(Module.JS_FORMS, JavaScriptForm.class));
        }

        yamlDeserializer.readStaticPages(project.getApplication().getStaticPageCollection());

        yamlDeserializer.readCustomization(project.getApplication());
        project.getAutomaticDeserializationService().registerFile(path, ManagedFileType.PROJECT);
    }

    private void readLocalizations(final Map<String, Object> serializedProjectBody, final Project project) throws ReadException
    {
        final Object serializedL10ns = serializedProjectBody.get(ATTR_LOCALIZATIONS);

        if (serializedL10ns != null)
        {
            yamlDeserializer.readLocalizations(asStrList(serializedL10ns), project.getApplication().getLocalizations());
        }
    }

    private void readConnectionProfiles(final BeConnectionProfilesRoot target)
    {
        yamlDeserializer.readConnectionProfiles(target);
    }

    private void readSecurity(final SecurityCollection securityCollection)
    {
        yamlDeserializer.readSecurity(securityCollection);
    }

    private List<Module> readModuleReferences(final Map<String, Object> projectElement, final Project project) throws ReadException
    {
        final List<Module> modules = new ArrayList<>();
        final Object serializedModules = projectElement.get(TAG_MODULES);

        if (serializedModules == null)
            return modules;

        final DataElementPath modulesPath = project.getModules().getCompletePath();

        for (final Map.Entry<String, Object> serializedModule : asPairs(serializedModules))
        {
            final String name = serializedModule.getKey();

            try
            {
                modules.add(readModuleReference(name, asMap(serializedModule.getValue()), project));
            }
            catch (Exception e)
            {
                loadContext.addWarning(new ReadException(e, name == null ? modulesPath : modulesPath.getChildPath(name), path));
            }
        }

        return modules;
    }

    private Module readModuleReference(final String moduleName, final Map<String, Object> serializedModuleBody, final Project project) throws ReadException
    {
        final Module module = new Module(moduleName, project.getModules());
        final Object entities = serializedModuleBody.get(TAG_ENTITIES);

        if (entities != null)
        {
            for (final String entityName : asStrList(entities))
            {
                readEntity(module, entityName);
            }
        }

        final Object serializedExtras = serializedModuleBody.get(TAG_EXTRAS);

        if (serializedExtras != null)
        {
            final List<String> extras = asStrList(serializedExtras);
            module.setExtras(extras.toArray(new String[extras.size()]));
        }

        return module;
    }

    private Module readApplication(final Object entities, final Project project) throws ReadException
    {
        final Module application = new Module(project.getProjectOrigin(), project);
        project.setApplication(application);

        if (entities != null)
        {
            for (final String entityName : asStrList(entities))
            {
                readEntity(application, entityName);
            }
        }

        return application;
    }

    private void readEntity(final Module module, final String entityName)
    {
        try
        {
            final Path file = yamlDeserializer.getFileSystem().getEntityFile(module.getName(), entityName);
            final Entity entity = yamlDeserializer.readEntity(module, entityName);

            EntitiesFactory.addToModule(entity, module);
            entity.getProject().getAutomaticDeserializationService().registerFile(file, ManagedFileType.ENTITY);
        }
        catch (ReadException e)
        {
            loadContext.addWarning(e.attachElement(module));
        }
    }

    private void readScripts(final Map<String, Object> serializedProjectBody, final FreemarkerCatalog scripts) throws ReadException
    {
        final Object serializedScripts = serializedProjectBody.get(TAG_SCRIPTS);

        if (serializedScripts == null)
            return;

        for (final String scriptName : asStrList(serializedScripts))
        {
            final Path scriptsFile = yamlDeserializer.getFileSystem().getScriptFile(scriptName);
            try
            {
                FreemarkerCatalog parent = scripts;
                String[] pathComponents = DataElementPath.create(scriptName).getPathComponents();
                for (int i = 0; i < pathComponents.length - 1; i++)
                {
                    FreemarkerScriptOrCatalog newParent = parent.get(pathComponents[i]);
                    if (newParent instanceof FreemarkerScript)
                    {
                        loadContext.addWarning(new ReadException(new Exception("Cannot create catalog for script " + scriptName + ": script with the same name exists"), newParent.getCompletePath(), path));
                        continue;
                    }
                    if (newParent == null)
                    {
                        newParent = new FreemarkerCatalog(pathComponents[i], parent);
                        DataElementUtils.saveQuiet(newParent);
                    }
                    parent = (FreemarkerCatalog) newParent;
                }
                FreemarkerScriptOrCatalog scriptOrCatalog = parent.get(pathComponents[pathComponents.length - 1]);
                if (scriptOrCatalog instanceof FreemarkerCatalog)
                {
                    loadContext.addWarning(new ReadException(new Exception("Cannot create script " + scriptName + ": catalog with the same name exists"), scriptOrCatalog.getCompletePath(), path));
                    continue;
                }
                final FreemarkerScript script = new FreemarkerScript(pathComponents[pathComponents.length - 1], parent);
                if (Files.exists(scriptsFile))
                {
                    script.setLinkedFile(scriptsFile);
                }
                DataElementUtils.saveQuiet(script);
            }
            catch (final Exception e)
            {
                loadContext.addWarning(new ReadException(e, scripts.getCompletePath().getChildPath(scriptName), scriptsFile));
            }
        }
    }
}
