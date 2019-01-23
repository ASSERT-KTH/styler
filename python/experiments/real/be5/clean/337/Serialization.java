package com.developmentontheedge.be5.metadata.serialization;

import com.developmentontheedge.be5.metadata.exception.ProjectLoadException;
import com.developmentontheedge.be5.metadata.exception.ProjectSaveException;
import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.exception.WriteException;
import com.developmentontheedge.be5.metadata.model.Module;
import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.serialization.yaml.YamlSerializer;
import com.developmentontheedge.be5.metadata.serialization.yaml.deserializers.YamlDeserializer;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * Facade.
 *
 * @author asko
 */
public class Serialization
{
    private static boolean automaticSerializationIsTurnedOn = true;

    public static boolean automaticSerializationIsTurnedOn()
    {
        return automaticSerializationIsTurnedOn;
    }

    private static void turnOffAutomaticSerialization()
    {
        automaticSerializationIsTurnedOn = false;
    }

    private static void turnOnAutomaticSerialization()
    {
        automaticSerializationIsTurnedOn = true;
    }

    public static void save(final Project project, final Path root) throws ProjectSaveException
    {
        Objects.requireNonNull(project);
        Objects.requireNonNull(root);

        try
        {
            new YamlSerializer().serializeProjectTo(project, root);
        }
        catch (final WriteException e)
        {
            throw new ProjectSaveException(root, e);
        }
    }
//
//    public static String save( final Entity entity ) throws WriteException
//    {
//        return new YamlSerializer().serialize( entity );
//    }
//
//    public static String toString( final Entity entity )
//    {
//        return new YamlSerializer().toString( entity );
//    }
//
//    public static void save( final LanguageLocalizations languageLocalizations ) throws WriteException
//    {
//        new YamlSerializer().serialize( languageLocalizations );
//    }
//
//    public static String toString( final LanguageLocalizations languageLocalizations )
//    {
//        return new YamlSerializer().toString( languageLocalizations );
//    }
//
//    public static void save( final MassChanges massChanges ) throws WriteException
//    {
//        new YamlSerializer().serialize( massChanges );
//    }
//
//    public static String toString( final MassChanges massChanges )
//    {
//        return new YamlSerializer().toString( massChanges );
//    }
//
//    public static void save( final SecurityCollection security ) throws WriteException
//    {
//        new YamlSerializer().serialize( security );
//    }
//
//    public static String toString( final SecurityCollection security )
//    {
//        return new YamlSerializer().toString( security );
//    }
//
//    public static void save( BeConnectionProfiles connectionProfiles ) throws WriteException
//    {
//        new YamlSerializer().serialize( connectionProfiles );
//    }
//
//    public static String toString( BeConnectionProfiles connectionProfiles )
//    {
//        return new YamlSerializer().toString( connectionProfiles );
//    }
//
//    public static void save( PageCustomizations customizations ) throws WriteException
//    {
//        new YamlSerializer().serialize( customizations );
//    }
//
//    public static String toString( PageCustomizations customizations, Module application )
//    {
//        return new YamlSerializer().toString( customizations, application );
//    }
//
//    public static void save( Daemons daemons ) throws WriteException
//    {
//        new YamlSerializer().serialize( daemons );
//    }
//
//    public static String toString( Daemons daemons )
//    {
//        return new YamlSerializer().toString( daemons );
//    }
//
//    public static void save( JavaScriptForms forms ) throws WriteException
//    {
//        new YamlSerializer().serialize( forms );
//    }
//
//    public static String toString( JavaScriptForms forms )
//    {
//        return new YamlSerializer().toString( forms );
//    }
//
//    public static void save( StaticPages pages ) throws WriteException
//    {
//        new YamlSerializer().serialize( pages );
//    }
//
//    public static String toString( StaticPages pages )
//    {
//        return new YamlSerializer().toString( pages );
//    }
//
//    public static void save( Project project ) throws WriteException
//    {
//        new YamlSerializer().serialize( project );
//    }
//
//    public static String toString( Project project )
//    {
//        return new YamlSerializer().toString( project );
//    }

    /**
     * Determines whether the given folder contain any correct BeanExplorer project file.
     */
    public static boolean canBeLoaded(final Path root)
    {
        return ProjectFileSystem.canBeLoaded(root);
    }
//
//    private static void checkProject( Project proj )
//    {
//        Objects.requireNonNull( proj, "Project is null" );
//        Objects.requireNonNull( proj.getLocation(), "Project location is null (project name is "+proj.getName()+")" );
//        if(!canBeLoaded( proj.getLocation() ))
//        {
//            throw new IllegalArgumentException( "Project "+proj.getName()+" cannot be loaded from "+proj.getLocation() );
//        }
//    }
//

    /**
     * Tries to determine the project format.
     *
     * @see Serialization#canBeLoaded(Path)
     */
    public static Project load(final Path root) throws ProjectLoadException
    {
        return load(root, null);
    }

    public static Project load(final Path root, final LoadContext loadContext) throws ProjectLoadException
    {
        return load(root, false, loadContext);
    }

    /**
     * @param fuseTemplates whether to fuse templates into entities (useful for modules loading)
     * @see Serialization#canBeLoaded(Path)
     */
    public static Project load(final Path root, final boolean fuseTemplates, final LoadContext loadContext) throws ProjectLoadException
    {
        Objects.requireNonNull(root);

        turnOffAutomaticSerialization();

        try
        {
            return new YamlDeserializer(loadContext == null ? new LoadContext() : loadContext, fuseTemplates).readProject(root);
        }
        catch (final ReadException e)
        {
            throw new ProjectLoadException(root, e);
        }
        finally
        {
            turnOnAutomaticSerialization();
        }
    }
//
//    public static Entity reloadEntity( final Entity oldEntity ) throws ReadException
//    {
//        checkProject(oldEntity.getProject());
//
//        turnOffAutomaticSerialization();
//
//        try
//        {
//            return new YamlDeserializer( new LoadContext() ).reloadEntity( oldEntity );
//        }
//        finally
//        {
//            turnOnAutomaticSerialization();
//        }
//    }
//
//    public static LanguageLocalizations reloadLocalization( final Path file, final Localizations localizations ) throws ReadException
//    {
//        checkProject( localizations.getProject() );
//
//        turnOffAutomaticSerialization();
//
//        try
//        {
//            return new YamlDeserializer( new LoadContext() ).reloadLocalization( file, localizations );
//        }
//        finally
//        {
//            turnOnAutomaticSerialization();
//        }
//    }
//
//    public static MassChanges reloadMassChanges( final Path file, final Module application ) throws ReadException
//    {
//        checkProject( application.getProject() );
//
//        turnOffAutomaticSerialization();
//
//        try
//        {
//            return new YamlDeserializer( new LoadContext() ).reloadMassChanges( file, application );
//        }
//        finally
//        {
//            turnOnAutomaticSerialization();
//        }
//    }
//
//    public static SecurityCollection reloadSecurity( final Path file, final Project project ) throws ReadException
//    {
//        checkProject( project );
//
//        turnOffAutomaticSerialization();
//
//        try
//        {
//            return new YamlDeserializer( new LoadContext() ).reloadSecurityCollection( file, project );
//        }
//        finally
//        {
//            turnOnAutomaticSerialization();
//        }
//    }
//
//    public static BeConnectionProfiles reloadConnectionProfiles( final Path file, final BeConnectionProfileType type, final BeConnectionProfilesRoot target ) throws ReadException
//    {
//        checkProject( target.getProject() );
//
//        turnOffAutomaticSerialization();
//
//        try
//        {
//            return new YamlDeserializer( new LoadContext() ).reloadConnectionProfiles( file, type, target );
//        }
//        finally
//        {
//            turnOnAutomaticSerialization();
//        }
//    }
//
//    public static PageCustomizations reloadCustomizations( final Path file, final Module target ) throws ReadException
//    {
//        checkProject( target.getProject() );
//
//        turnOffAutomaticSerialization();
//
//        try
//        {
//            return new YamlDeserializer( new LoadContext() ).reloadCustomizations( file, target );
//        }
//        finally
//        {
//            turnOnAutomaticSerialization();
//        }
//    }
//
//    public static Daemons reloadDaemons( final Path file, final Module target ) throws ReadException
//    {
//        checkProject( target.getProject() );
//
//        turnOffAutomaticSerialization();
//
//        try
//        {
//            return new YamlDeserializer( new LoadContext() ).reloadDaemons( file, target );
//        }
//        finally
//        {
//            turnOnAutomaticSerialization();
//        }
//    }
//
//    public static JavaScriptForms reloadForms( final Path file, final Module target ) throws ReadException
//    {
//        checkProject( target.getProject() );
//
//        turnOffAutomaticSerialization();
//
//        try
//        {
//            return new YamlDeserializer( new LoadContext() ).reloadForms( file, target );
//        }
//        finally
//        {
//            turnOnAutomaticSerialization();
//        }
//    }
//
//    public static StaticPages reloadPages( final Path file, final Module target ) throws ReadException
//    {
//        checkProject( target.getProject() );
//
//        turnOffAutomaticSerialization();
//
//        try
//        {
//            return new YamlDeserializer( new LoadContext() ).reloadPages( file, target );
//        }
//        finally
//        {
//            turnOnAutomaticSerialization();
//        }
//    }

    public static void loadModuleMacros(final Module module) throws ReadException
    {
        final Path root = ModuleLoader2.getModulePath(module.getName());

        if (root != null)
        {
            turnOffAutomaticSerialization();

            try
            {
                new YamlDeserializer(new LoadContext()).loadMacroFiles(module);
            }
            finally
            {
                turnOnAutomaticSerialization();
            }
        }
    }

    public static Object derepresent(Node node)
    {
        if (node instanceof MappingNode)
        {
            final List<NodeTuple> pairs = ((MappingNode) node).getValue();
            final LinkedHashMap<Object, Object> result = new LinkedHashMap<>();

            for (final NodeTuple pair : pairs)
                result.put(derepresent(pair.getKeyNode()), derepresent(pair.getValueNode()));

            return result;
        }
        else if (node instanceof SequenceNode)
        {
            final List<Node> value = ((SequenceNode) node).getValue();
            final ArrayList<Object> result = new ArrayList<>();

            for (final Node child : value)
                result.add(derepresent(child));

            return result;
        }
        else if (node instanceof ScalarNode)
        {
            return ((ScalarNode) node).getValue();
        }

        throw new AssertionError();
    }

}
