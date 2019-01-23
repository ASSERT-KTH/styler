package com.developmentontheedge.be5.metadata.serialization;

import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.model.BeConnectionProfileType;
import com.developmentontheedge.be5.metadata.model.BeConnectionProfiles;
import com.developmentontheedge.be5.metadata.model.Daemons;
import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.be5.metadata.model.FreemarkerCatalog;
import com.developmentontheedge.be5.metadata.model.FreemarkerScript;
import com.developmentontheedge.be5.metadata.model.JavaScriptForms;
import com.developmentontheedge.be5.metadata.model.LanguageLocalizations;
import com.developmentontheedge.be5.metadata.model.LanguageStaticPages;
import com.developmentontheedge.be5.metadata.model.MassChanges;
import com.developmentontheedge.be5.metadata.model.Module;
import com.developmentontheedge.be5.metadata.model.PageCustomizations;
import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.model.ProjectFileStructure;
import com.developmentontheedge.be5.metadata.model.SecurityCollection;
import com.developmentontheedge.be5.metadata.model.SourceFileCollection;
import com.developmentontheedge.be5.metadata.model.StaticPages;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnmappableCharacterException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProjectFileSystem
{
    private final Path root;
    private final ProjectFileStructure structure;

    /**
     * Function references and closures. Remove this class if you use Java 8.
     */
    public static class Fn
    {

        public static Function<ProjectFileSystem, Path> languageLocalizationsPath(final LanguageLocalizations languageLocalizations)
        {
            return fs -> fs.getLocalizationFile(languageLocalizations.getName());
        }

        public static Function<ProjectFileSystem, Path> entityPath(final Entity entity)
        {
            return fs -> fs.getEntityFile(entity.getModule().getName(), entity.getName());
        }

        public static Function<ProjectFileSystem, Path> connectionProfilesPath(final BeConnectionProfiles connectionProfiles)
        {
            return fs -> fs.getConnectionProfilesFile(connectionProfiles.getType());
        }

        public static Function<ProjectFileSystem, Path> customizationsPath()
        {
            return fs -> fs.getCustomizationFile();
        }

        public static Function<ProjectFileSystem, Path> daemonsPath()
        {
            return fs -> fs.getDaemonsFile();
        }

        public static Function<ProjectFileSystem, Path> formsPath()
        {
            return fs -> fs.getJavaScriptFormsFile();
        }

        public static Function<ProjectFileSystem, Path> massChangesPath()
        {
            return fs -> fs.getMassChangesFile();
        }

        public static Function<ProjectFileSystem, Path> staticPagesPath()
        {
            return fs -> fs.getStaticPagesFile();
        }

        public static Function<ProjectFileSystem, Path> securityPath()
        {
            return fs -> fs.getSecurityFile();
        }

        public static Function<ProjectFileSystem, Path> projectPath()
        {
            return fs -> fs.getProjectFile();
        }

    } // Fn

    public ProjectFileSystem(final Project project)
    {
        if (project == null)
        {
            throw new IllegalArgumentException();
        }
        Path root = project.getLocation();
        if (root == null)
        {
            throw new IllegalArgumentException("Project " + project.getName() + " is not bound to file system");
        }
        ProjectFileStructure structure = project.getProjectFileStructure();
        if (structure == null)
        {
            throw new IllegalArgumentException("Project " + project.getName() + " has no structure defined");
        }
        this.root = root;
        this.structure = structure;
    }

    public static boolean canBeLoaded(final Path projectRoot)
    {
        final String projectFileName = ProjectFileStructure.PROJECT_FILE_NAME_WITHOUT_SUFFIX + ProjectFileStructure.FORMAT_SUFFIX;
        final Path projectFile = projectRoot.resolve(projectFileName);

        return Files.isRegularFile(projectFile);
    }

    public Path getRoot()
    {
        return root;
    }

    /**
     * @return map of paths which contain project model files (excluding
     * non-model files like html, java, etc.). If map value is true,
     * then subpaths also may contain project files.
     */
    public NavigableMap<Path, Boolean> getPaths()
    {
        NavigableMap<Path, Boolean> result = new TreeMap<>();

        List<String> files = Arrays.asList(structure.getSecurityFile(), structure.getDaemonsFile(), structure.getPagesFile(),
                structure.getLocalConnectionProfilesFile(), structure.getRemoteConnectionProfilesFile(), structure.getMassChangesFile(),
                structure.getCustomizationFile(), structure.getSelectedProfileFile(), structure.getDevFile());
        StreamEx.of(files).map(path -> resolve(root, path)).map(Path::getParent).forEach(p -> result.put(p, false));

        List<String> dirs = Arrays.asList(
                structure.getDataDir(), structure.getEntitiesDir(),
                structure.getJsExtendersDir(), structure.getJsFormsDir(), structure.getJsOperationsDir(), structure.getJsQueriesDir(),
                structure.getL10nDir());
        StreamEx.of(dirs).map(path -> resolve(root, path)).append(root).forEach(p -> result.put(p, false));

        List<String> recursiveDirs = Arrays.asList(structure.getScriptsDir(), structure.getModulesDir(), structure.getMacroDir(),
                structure.getGroovyOperationsDir(), structure.getGroovyExtendersDir(), structure.getGroovyQueriesDir(),
                structure.getPagesDir());
        StreamEx.of(recursiveDirs).map(path -> resolve(root, path)).forEach(p -> {
            result.keySet().removeIf(pp -> pp.startsWith(p));
            if (!EntryStream.of(result).anyMatch(entry -> entry.getValue() && p.startsWith(entry.getKey())))
                result.put(p, true);
        });
        return result;
    }

    public Path getProjectFile()
    {
        return root.resolve(ProjectFileStructure.PROJECT_FILE_NAME_WITHOUT_SUFFIX + ProjectFileStructure.FORMAT_SUFFIX);
    }

    public Path getSecurityFile()
    {
        return resolve(root, structure.getSecurityFile());
    }

    public Path getDaemonsFile()
    {
        return resolve(root, structure.getDaemonsFile());
    }

    public Path getJavaScriptFormsFile()
    {
        return resolve(root, structure.getJsFormsFile());
    }

    public Path getCustomizationFile()
    {
        return resolve(root, structure.getCustomizationFile());
    }

    public Path getMassChangesFile()
    {
        return resolve(root, structure.getMassChangesFile());
    }

    public Path getSelectedProfileFile()
    {
        return resolve(root, structure.getSelectedProfileFile());
    }

    public Path getStaticPagesFile()
    {
        return resolve(root, structure.getPagesFile());
    }

    public Path getStaticPageFile(String fileName)
    {
        return resolve(root, structure.getPagesDir() + "/" + fileName);
    }

    public Path getConnectionProfilesFile(final BeConnectionProfileType type)
    {
        final String fileName;

        switch (type)
        {
            case LOCAL:
                fileName = structure.getLocalConnectionProfilesFile();
                break;
            case REMOTE:
                fileName = structure.getRemoteConnectionProfilesFile();
                break;
            default:
                throw new AssertionError();
        }

        return resolve(root, fileName);
    }

    public static String readProjectFile(final Path projectRoot) throws ReadException
    {
        return read(getProjectFile(projectRoot));
    }

    public static Path getProjectFile(final Path projectRoot)
    {
        return projectRoot.resolve(ProjectFileStructure.PROJECT_FILE_NAME_WITHOUT_SUFFIX + ProjectFileStructure.FORMAT_SUFFIX);
    }

    public void writeProject(final String content) throws IOException
    {
        write(getProjectFile(), content);
    }

    public Path getEntitiesFolder()
    {
        return resolve(root, structure.getEntitiesDir());
    }

    public Path getLocalizationsFolder()
    {
        return resolve(root, structure.getL10nDir());
    }

    public Path getFile(final FreemarkerScript freemarkerScript)
    {
        if (isFromFreemarkerScripts(freemarkerScript))
            return getScriptFile(freemarkerScript.getRelativePath(freemarkerScript.getModule().getFreemarkerScripts()));

        if (isFromMacroCollection(freemarkerScript))
            return getMacroFile(freemarkerScript.getRelativePath(freemarkerScript.getModule().getMacroCollection()));

        throw new IllegalStateException();
    }

    private boolean isFromMacroCollection(final FreemarkerScript freemarkerScript)
    {
        final FreemarkerCatalog macros = freemarkerScript.getModule().getMacroCollection();
        final boolean isFromMacroCollection = macros != null && macros.getCompletePath().isAncestorOf(freemarkerScript.getCompletePath());

        return isFromMacroCollection;
    }

    private boolean isFromFreemarkerScripts(final FreemarkerScript freemarkerScript)
    {
        final FreemarkerCatalog scripts = freemarkerScript.getModule().getFreemarkerScripts();
        final boolean isFromFreemarkerScripts = scripts != null && scripts.getCompletePath().isAncestorOf(freemarkerScript.getCompletePath());

        return isFromFreemarkerScripts;
    }

    public Path getScriptFile(final String name)
    {
        return resolve(root, structure.getScriptsDir() + "/" + name + ".ftl");
    }

    public Path getMacroFile(final String name)
    {
        return resolve(root, structure.getMacroDir() + "/" + name + ".ftl");
    }

    public Path getIconsFolder()
    {
        return resolve(root, structure.getIconsDir());
    }

    public List<String> getIcons()
    {
        final Path iconsFolder = getIconsFolder();

        if (!Files.isDirectory(iconsFolder))
            return Collections.emptyList();

        try (Stream<Path> list = Files.list(iconsFolder))
        {
            return list
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }
        catch (IOException | UncheckedIOException e)
        {
            return Collections.emptyList();
        }
    }

    public Path getIconsFile(final String name)
    {
        return resolve(root, structure.getIconsDir() + "/" + name);
    }

    public Path getModulesFolder()
    {
        return resolve(root, structure.getModulesDir());
    }

    public Path getJavaSourcesFolder()
    {
        return resolve(root, structure.getJavaSourcesDir());
    }

    public Path getJavaFileByClassName(final String name)
    {
        return resolve(getJavaSourcesFolder(), name.replace('.', '/').concat(".java"));
    }

    public Path getNameSpaceFile(String nameSpace, String fileName)
    {
        return getNameSpaceFolder(nameSpace).resolve(fileName);
    }

    public Path getNameSpaceFolder(String nameSpace)
    {
        switch (nameSpace)
        {
            case SourceFileCollection.NAMESPACE_JAVASCRIPT_OPERATION:
                return getJavaScriptOperationsFolder();
            case SourceFileCollection.NAMESPACE_GROOVY_OPERATION:
                return getGroovyOperationsFolder();
            case SourceFileCollection.NAMESPACE_JAVASCRIPT_EXTENDER:
                return getJavaScriptExtendersFolder();
            case SourceFileCollection.NAMESPACE_GROOVY_EXTENDER:
                return getGroovyExtendersFolder();
            default:
                throw new IllegalArgumentException(nameSpace);
        }
    }

    public Path getJavaScriptOperationsFolder()
    {
        return resolve(root, structure.getJsOperationsDir());
    }

    public Path getGroovyOperationsFolder()
    {
        return resolve(root, structure.getGroovyOperationsDir());
    }

    public Path getJavaScriptQueriesFolder()
    {
        return resolve(root, structure.getJsQueriesDir());
    }

    public Path getGroovyQueriesFolder()
    {
        return resolve(root, structure.getGroovyQueriesDir());
    }

    public Path getJavaScriptExtendersFolder()
    {
        return resolve(root, structure.getJsExtendersDir());
    }

    public Path getGroovyExtendersFolder()
    {
        return resolve(root, structure.getGroovyExtendersDir());
    }

    public Path getJavaScriptFormsFolder()
    {
        return resolve(root, structure.getJsFormsDir());
    }

    public Path getJavaScriptOperationFile(final String name)
    {
        return getNameSpaceFile(SourceFileCollection.NAMESPACE_JAVASCRIPT_OPERATION, name);
    }

    public Path getJavaScriptQueryFile(final String name)
    {
        return getJavaScriptQueriesFolder().resolve(name);
    }

    public Path getGroovyQueryFile(final String name)
    {
        return getGroovyQueriesFolder().resolve(name);
    }

    public Path getJavaScriptExtenderFile(final String name)
    {
        return getJavaScriptExtendersFolder().resolve(name);
    }

    public Path getGroovyExtenderFile(final String name)
    {
        return getGroovyExtendersFolder().resolve(name);
    }

    public Path getEntityFile(final Entity entity)
    {
        return getEntityFile(entity.getModule().getName(), entity.getName());
    }

    public Path getEntityFile(final String moduleName, final String name)
    {
        if (structure.getProject().getProjectOrigin().equals(moduleName))
        {
            return getEntitiesFolder().resolve(name + ProjectFileStructure.FORMAT_SUFFIX);
        }

        return resolve(getModulesFolder(), moduleName + "/" + name + ProjectFileStructure.FORMAT_SUFFIX);
    }

    public void writeEntityFile(final String moduleName, final String name, final String content) throws IOException
    {
        write(getEntityFile(moduleName, name), content);
    }

    /**
     * Never throws an exception. Returns null if there is no file or if an error occurred;
     *
     * @param moduleName
     * @param name
     * @return
     */
    public String readEntityFile(final String moduleName, final String name)
    {
        try
        {
            final Path file = getEntityFile(moduleName, name);

            if (!Files.isRegularFile(file))
                return null;

            return read(file);
        }
        catch (ReadException e)
        {
            return null;
        }
    }

    public Path getLocalizationFile(final String langCode)
    {
        return getLocalizationsFolder().resolve(langCode + ProjectFileStructure.FORMAT_SUFFIX);
    }

    public void writeLocalizationFile(final String langCode, final String content) throws IOException
    {
        write(getLocalizationFile(langCode), content);
    }

    public void writeSecurityFile(final String content) throws IOException
    {
        write(getSecurityFile(), content);
    }

    public void writeDaemonsFile(final String content) throws IOException
    {
        write(getDaemonsFile(), content);
    }

    public void writeMassChangesFile(String content) throws IOException
    {
        write(getMassChangesFile(), content);
    }

    public void writeJavaScriptFormsFile(String content) throws IOException
    {
        write(getJavaScriptFormsFile(), content);
    }

    public void writeCustomizationFile(final String content) throws IOException
    {
        write(getCustomizationFile(), content);
    }

    public void writeStaticPagesFile(final String content) throws IOException
    {
        write(getStaticPagesFile(), content);
    }

    public void writeSelectedProfileFile(final String connectionProfileName) throws IOException
    {
        write(getSelectedProfileFile(), connectionProfileName);
    }

    public void writeStaticPageFile(final String filename, final String content) throws IOException
    {
        write(getStaticPageFile(filename), content);
    }

    public void writeConnectionProfilesFile(final BeConnectionProfileType type, final String content) throws IOException
    {
        write(getConnectionProfilesFile(type), content);
    }

    public String readStaticPagesFileContent() throws ReadException
    {
        return read(getStaticPagesFile());
    }

    public String readStaticPageFileContent(final String filename) throws ReadException
    {
        return read(getStaticPageFile(filename));
    }

    public String readCustomizationFileContent() throws ReadException
    {
        return read(getCustomizationFile());
    }

    public String getJavaScriptExtenderCodeByRelativePath(final String relativePath) throws ReadException
    {
        assert relativePath != null;
        return read(resolve(getJavaScriptExtendersFolder(), relativePath));
    }

    public void writeSourceFile(final String nameSpace, final String name, final String content) throws IOException
    {
        write(getNameSpaceFile(nameSpace, name), content);
    }

    public String readJavaScriptQuery(final String name) throws ReadException
    {
        return read(getJavaScriptQueryFile(name));
    }

    public String readGroovyQuery(final String name) throws ReadException
    {
        return read(getGroovyQueryFile(name));
    }

    public void writeJavaScriptQuery(final String name, final String content) throws IOException
    {
        write(getJavaScriptQueryFile(name), content);
    }

    public void writeGroovyQuery(final String name, final String content) throws IOException
    {
        write(getGroovyQueryFile(name), content);
    }

    public static Function<ProjectFileSystem, Path> getSerializedFilePathEvaluator(final BeModelElement element)
    {
        if (element instanceof LanguageLocalizations)
        {
            return Fn.languageLocalizationsPath((LanguageLocalizations) element);
        }
        else if (element instanceof Entity)
        {
            // TODO remove some old code and use this case
            return null;
        }
        else if (element instanceof BeConnectionProfiles)
        {
            return Fn.connectionProfilesPath((BeConnectionProfiles) element);
        }
        else if (element instanceof SecurityCollection)
        {
            return Fn.securityPath();
        }
        else if (element instanceof MassChanges)
        {
            return Fn.massChangesPath();
        }
        else if (element instanceof PageCustomizations && element.getOrigin() instanceof Module)
        {
            return Fn.customizationsPath();
        }
        else if (element instanceof Daemons)
        {
            return Fn.daemonsPath();
        }
        else if (element instanceof JavaScriptForms)
        {
            return Fn.formsPath();
        }
        else if (element instanceof StaticPages)
        {
            return Fn.staticPagesPath();
        }
        else if (element instanceof LanguageStaticPages)
        {
            return Fn.staticPagesPath();
        }

        return null;
    }

    private static Path resolve(final Path parent, final String path)
    {
        assert parent != null && path != null;

        return StreamEx.split(path, "/").foldLeft(parent, Path::resolve);
    }

    private static void write(final Path file, final String content) throws IOException
    {
        if (!Files.exists(file.getParent()))
        {
            Files.createDirectories(file.getParent());
        }

        Files.write(file, content.getBytes(StandardCharsets.UTF_8));
    }

    public static String read(final Path file) throws ReadException
    {
        return read(file, false);
    }

    public static String read(final Path file, boolean nullAble) throws ReadException
    {
        if (!Files.exists(file))
        {
            if (nullAble)
            {
                return null;
            }
            else
            {
                throw new ReadException(file, ReadException.LEE_NOT_FOUND);
            }
        }
        if (!Files.isRegularFile(file))
        {
            throw new ReadException(file, ReadException.LEE_NOT_A_FILE);
        }
        final byte[] bytes;
        try
        {
            bytes = readBytes(file);
        }
        catch (Exception e)
        {
            throw new ReadException(e, file, ReadException.LEE_UNREADABLE);
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
        decoder.onMalformedInput(CodingErrorAction.REPORT);
        decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        char[] resultArray = new char[(int) (bytes.length * decoder.maxCharsPerByte() + 1)];
        CharBuffer decoded = CharBuffer.wrap(resultArray);
        CoderResult result = decoder.decode(buffer, decoded, true);
        try
        {
            if (!result.isUnderflow())
                result.throwException();
            result = decoder.flush(decoded);
            if (!result.isUnderflow())
                result.throwException();
        }
        catch (UnmappableCharacterException e)
        {
            throw new ReadException(new Exception("Unmappable character at " + calcPosition(decoded)), file, ReadException.LEE_ENCODING_ERROR);
        }
        catch (CharacterCodingException e)
        {
            throw new ReadException(new Exception("Malformed character at " + calcPosition(decoded)), file, ReadException.LEE_ENCODING_ERROR);
        }
        int start = 0;
        if (resultArray.length > 0 && resultArray[0] == '\uFEFF') // Ignore BOM
            start++;
        return new String(resultArray, start, decoded.position() - start);
    }

    /**
     * Here was a bug during Git pulling. Changed files couldn't be read due to ClosedByInterruptExceptions.
     * This method replaces the default {@link Files#readAllBytes(Path)} to avoid this issue.<br />
     * <br />
     * Note that {@link Thread#sleep(long)} is required as repeating of reading without sleeping would cause
     * repeated exceptions.
     */
    private static byte[] readBytes(final Path file) throws IOException
    {
        int nAttempts = 10;

        for (int iAttempt = 0; iAttempt < nAttempts; iAttempt++)
        {
            boolean lastAttempt = (iAttempt == nAttempts - 1);

            try
            {
                return Files.readAllBytes(file);
            }
            catch (ClosedByInterruptException e)
            {
                if (lastAttempt)
                    throw e;
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e1)
                {
                    continue;
                }
            }
        }

        throw new AssertionError(); // shouldn't be reached
    }

    private static String calcPosition(CharBuffer decoded)
    {
        char[] data = decoded.array();
        int position = decoded.position();
        int row = 1, col = 1;
        for (int i = 0; i < position; i++)
        {
            if (data[i] == '\r')
                continue;
            if (data[i] == '\n')
            {
                row++;
                col = 1;
            }
            else
                col++;
        }
        return "row " + row + ", column " + col;
    }

}
