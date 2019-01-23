package com.developmentontheedge.be5.metadata.serialization;

import com.developmentontheedge.be5.metadata.exception.ProjectLoadException;
import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.model.DataElementUtils;
import com.developmentontheedge.be5.metadata.model.Module;
import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.model.ProjectFileStructure;
import com.developmentontheedge.be5.metadata.util.JULLogger;
import com.developmentontheedge.be5.metadata.util.ProcessController;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static java.util.stream.Collectors.joining;


public class ModuleLoader2
{
    private static final Logger log = Logger.getLogger(ModuleLoader2.class.getName());

    private static Map<String, Project> modulesMap;
    private static Map<String, Path> pathsToProjectsToHotReload = new HashMap<>();
    private static List<String> devRoles = new ArrayList<>();

    public static Project loadProjectWithModules(Path projectPath) throws ProjectLoadException, MalformedURLException
    {
        loadAllProjects(false, Collections.singletonList(projectPath.resolve("project.yaml").toUri().toURL()));

        return findProjectAndMergeModules();
    }

    public static Project findAndLoadProjectWithModules(boolean dirty) throws ProjectLoadException
    {
        loadAllProjects(dirty);

        return findProjectAndMergeModules();
    }

    private static Project findProjectAndMergeModules() throws ProjectLoadException
    {
        Project project = null;

        if (modulesMap.size() == 0)
        {
            throw new RuntimeException("modulesMap is empty");
        }

        for (Map.Entry<String, Project> module : modulesMap.entrySet())
        {
            if (module.getValue() != null && !module.getValue().isModuleProject())
            {
                if (project != null)
                {
                    throw new RuntimeException("Several projects were found: " + project + ", " + module);
                }
                else
                {
                    project = module.getValue();
                }
            }
        }

        if (project == null)
        {
            //todo create new not module project for tests?
            log.info("Project not found, try load main module.");
            project = new ProjectTopologicalSort(modulesMap.values()).getRoot();
        }

        ModuleLoader2.mergeModules(project, new JULLogger(log));

        return project;
    }

    public static Map<String, Project> getModulesMap()
    {
        return modulesMap;
    }

    public static void clear()
    {
        modulesMap = new HashMap<>();
        pathsToProjectsToHotReload = new HashMap<>();
        devRoles = new ArrayList<>();
    }

    private static synchronized void loadAllProjects(boolean dirty)
    {
        loadAllProjects(dirty, Collections.emptyList());
    }

    private static synchronized void loadAllProjects(boolean dirty, List<URL> additionalUrls)
    {
        if (modulesMap != null && !dirty)
            return;

        try
        {
            ArrayList<URL> urls = Collections.list(ModuleLoader2.class.getClassLoader().getResources(
                    ProjectFileStructure.PROJECT_FILE_NAME_WITHOUT_SUFFIX + ProjectFileStructure.FORMAT_SUFFIX));

            urls.addAll(additionalUrls);
            loadAllProjects(urls);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void loadAllProjects(List<URL> urls)
    {
        modulesMap = new HashMap<>();
        try
        {
            replaceAndAddURLtoSource(urls);

            for (URL url : urls)
            {
                LoadContext loadContext = new LoadContext();

                Project module;
                String ext = url.toExternalForm();
                if (ext.indexOf('!') < 0) // usual file in directory
                {
                    Path path = Paths.get(url.toURI()).getParent();
                    module = Serialization.load(path, loadContext);
                    log.fine("Load module from dir: " + path);
                }
                else // war or jar file
                {
                    String jar = ext.substring(0, ext.indexOf('!'));
                    FileSystem fs;

                    try
                    {
                        fs = FileSystems.newFileSystem(URI.create(jar), Collections.emptyMap());
                    }
                    catch (FileSystemAlreadyExistsException e)
                    {
                        fs = FileSystems.getFileSystem(URI.create(jar));
                        log.fine("Get exists FileSystem after exception");
                    }

                    Path path = fs.getPath("./");
                    module = Serialization.load(path, loadContext);

                    log.fine("Load module from " + url.toExternalForm() + ", path=" + path);
                }
                loadContext.check();
                modulesMap.put(module.getAppName(), module);
            }
        }
        catch (ProjectLoadException | IOException | URISyntaxException e)
        {
            e.printStackTrace();
        }
    }

    public static String parse(URL url) throws IOException
    {
        try (InputStream in = url.openStream();
             BufferedReader r = new BufferedReader(new InputStreamReader(in, "utf-8")))
        {
            String ln = r.readLine();
            return ln.substring(0, ln.indexOf(':')).trim();
        }
    }

    public static boolean containsModule(String name)
    {
        loadAllProjects(false);

        return modulesMap.containsKey(name);
    }

    public static Path getModulePath(String name)
    {
        loadAllProjects(false);

        return modulesMap.get(name).getLocation();
    }

    public static void addModuleScripts(Project project) throws ReadException
    {
        loadAllProjects(false);

        for (Module module : project.getModules())
        {
            Serialization.loadModuleMacros(module);
        }
    }

    public static List<Project> loadModules(Project application, ProcessController logger, LoadContext loadContext) throws ProjectLoadException
    {
        List<Project> result = new ArrayList<>();
        for (Module module : application.getModules())
        {
            if (containsModule(module.getName()))
            {
                Project moduleProject = modulesMap.get(module.getName());
                result.add(moduleProject);
            }
            else
            {
                throw new RuntimeException("Module project not found: '" + module.getName() + "'");
            }
        }
        //todo ????? topological sort?
        result.sort((o1, o2) -> {
            if (o1.getModules().contains(o2.getName()))
                return 1;
            if (o2.getModules().contains(o1.getName()))
                return -1;
            return 0;
        });
        return result;
    }

    public static void mergeModules(Project be5Project, ProcessController logger) throws ProjectLoadException
    {
        long startTime = System.nanoTime();
        LoadContext loadContext = new LoadContext();
        try
        {
            ModuleLoader2.mergeAllModules(be5Project, logger, loadContext);
        }
        catch (ProjectLoadException e)
        {
            throw new ProjectLoadException("Merge modules", e);
        }
        loadContext.check();
        log.info(ModuleLoader2.logLoadedProject(be5Project, startTime));
    }

    private static void mergeAllModules(
            final Project model,
            final ProcessController logger,
            final LoadContext context) throws ProjectLoadException
    {
        mergeAllModules(model, loadModules(model, logger, context), context);
    }

    public static void mergeAllModules(final Project model, List<Project> modules, final LoadContext context) throws ProjectLoadException
    {
        modules = new LinkedList<>(modules);

        for (Project module : modules)
        {
            module.mergeHostProject(model);
        }

        final Project compositeModule = foldModules(model, modules, context);
        if (compositeModule != null)
        {
            model.merge(compositeModule);
        }
    }

    private static Project foldModules(final Project model, final List<Project> modules, LoadContext context)
    {
        if (modules.isEmpty())
        {
            return null;
        }

        Project compositeModule = null;

        for (Project module : modules)
        {
            if (compositeModule != null)
            {
                module.getModules().merge(compositeModule.getModules(), true, true);
                module.getApplication().merge(compositeModule.getModule(module.getProjectOrigin()), true, true);
            }

            module.applyMassChanges(context);
            compositeModule = module;

            if (compositeModule.isModuleProject())
            {
                DataElementUtils.addQuiet(module.getModules(), module.getApplication());
                module.setApplication(new Module(model.getProjectOrigin(), model));
            }
        }

        return compositeModule;
    }

    /**
     * Returns BeanExplorerProjectFileSystem for given module if possible
     */
    public static ProjectFileSystem getFileSystem(Project app, String moduleName)
    {
        if (app.getProjectOrigin().equals(moduleName))
        {
            return new ProjectFileSystem(app);
        }
        Path modulePath = ModuleLoader2.getModulePath(moduleName);
        if (modulePath != null)
        {
            Project project = new Project(moduleName);
            project.setLocation(modulePath);
            project.setProjectFileStructure(new ProjectFileStructure(project));
            return new ProjectFileSystem(project);
        }

        return null;
    }

    private static String logLoadedProject(Project project, long startTime)
    {
        StringBuilder sb = new StringBuilder();
        if (project.isModuleProject())
        {
            sb.append("Module      : ");
        }
        else
        {
            sb.append("Project     : ");
        }

        sb.append(project.getName());

        if (project.getModules().getSize() > 0)
        {
            sb.append("\nModules     : ").append(project.getModules().getNameList().stream().collect(joining(", ")));
        }
        sb.append("\nLoading time: ")
                .append(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)).append(" ms");
        return JULLogger.infoBlock(sb.toString());
    }

    /**
     * For hot reload
     *
     * @param urls projects URL
     */
    private static void replaceAndAddURLtoSource(List<URL> urls)
    {
        try
        {
            readDevPathsToSourceProjects();
            if (pathsToProjectsToHotReload.isEmpty()) return;

            StringBuilder sb = new StringBuilder();
            sb.append(JULLogger.infoBlock("Replace project path for hot reload (dev.yaml):"));
            boolean started = false;

            for (Map.Entry<String, Path> moduleSource : pathsToProjectsToHotReload.entrySet())
            {
                boolean used = false;
                for (int i = 0; i < urls.size(); i++)
                {
                    String name = getProjectName(urls.get(i));
                    if (name.equals(moduleSource.getKey()))
                    {
                        used = true;
                        started = true;
                        urls.set(i, moduleSource.getValue().resolve("project.yaml").toUri().toURL());
                        sb.append("\n - ").append(String.format("%-20s", name)).append(urls.get(i)).append(" - replace");
                    }
                }
                if (!used)
                {
                    URL url = moduleSource.getValue().resolve("project.yaml").toUri().toURL();
                    urls.add(url);
                    sb.append("\n - ").append(moduleSource.getKey()).append(": ").append(url).append(" - add");
                }
            }
            sb.append("\n");
            if (started) log.info(sb.toString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static String getProjectName(URL url) throws IOException
    {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8")))
        {
            Map<String, Object> module = new Yaml().load(reader);
            return module.entrySet().iterator().next().getKey();
        }
    }

    private static void readDevPathsToSourceProjects() throws IOException
    {
        ArrayList<URL> urls = Collections.list(ModuleLoader2.class.getClassLoader().getResources("dev.yaml"));
        if (urls.size() > 1)
        {
            log.severe("dev.yaml should be only in the project.");
            throw new RuntimeException("dev.yaml should be only in the project.");
        }

        if (urls.size() == 1)
        {
            readDevPathsToSourceProjects(urls.get(0));
        }
    }

    /**
     * dev.yaml example:
     * paths:
     *   testBe5app: /home/uuinnk/workspace/github/testBe5app
     * roles:
     * - SystemDeveloper
     */
    @SuppressWarnings("unchecked")
    static void readDevPathsToSourceProjects(URL url) throws IOException
    {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8")))
        {
            Map<String, Object> content = new Yaml().load(reader);

            initPathsForDev(content);
            if (content.get("roles") != null)
            {
                devRoles = (List<String>) content.get("roles");
                log.info("Dev roles read - " + devRoles.toString());
            }
            else
            {
                devRoles = Collections.emptyList();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void initPathsForDev(Map<String, Object> content)
    {
        Map<String, String> paths = (Map<String, String>) content.get("paths");
        if (paths != null)
        {
            for (Map.Entry<String, String> entry : paths.entrySet())
            {
                if (Paths.get(entry.getValue()).resolve("project.yaml").toFile().exists())
                {
                    pathsToProjectsToHotReload.put(entry.getKey(), Paths.get(entry.getValue()));
                }
                else
                {
                    log.severe("Error path in dev.yaml for " + entry.getKey());
                }
            }
        }
    }

    public static Map<String, Path> getPathsToProjectsToHotReload()
    {
        return pathsToProjectsToHotReload;
    }

    public static List<String> getDevRoles()
    {
        return devRoles;
    }
}
