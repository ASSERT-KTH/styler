package com.developmentontheedge.be5.metadata.scripts.wizard;


import com.developmentontheedge.be5.metadata.model.DataElementUtils;
import com.developmentontheedge.be5.metadata.model.LanguageLocalizations;
import com.developmentontheedge.be5.metadata.model.Localizations;
import com.developmentontheedge.be5.metadata.model.Module;
import com.developmentontheedge.be5.metadata.model.Project;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * use Project instead. and to easy support read exists project too
 */
@Deprecated
public class ProjectGenerator
{
    public static class Parameters
    {
        private String projectName = "new-project";
        private String[] languages = new String[]{"ru"};
        //private String[] features = new String[] { "logging" };
        private String[] modules = new String[]{"core"};
        private String[] roles = new String[]{"Administrator", "SystemDeveloper", "Guest"};
//        private String testUserLogin;
//        private String testUserPassword;
//        private String testUserRole = "Administrator";
//        private String connectionUrl;
//        private boolean createLoginAndLogoutOperations = true;

        /**
         * This copy constructor uses getters to copy the original object.
         *
         * @param orig
         */
        public Parameters(final Parameters orig)
        {
            this.projectName = orig.getProjectName();
            this.languages = orig.getLanguages();
            //this.features = orig.getFeatures();
            this.modules = orig.getModules();
            this.roles = orig.getRoles();
//            this.testUserLogin = orig.getTestUserLogin();
//            this.testUserPassword = orig.getTestUserPassword();
//            this.testUserRole = orig.getTestUserRole();
//            this.connectionUrl = orig.getConnectionUrl();
//            this.createLoginAndLogoutOperations = orig.shouldCreateLoginAndLogoutOperations();
        }

        public Parameters()
        {
        }

        public String getProjectName()
        {
            return projectName;
        }

        public void setProjectName(String projectName)
        {
            this.projectName = projectName;
        }

        public String[] getLanguages()
        {
            return languages.clone();
        }

        public void setLanguages(String[] languages)
        {
            this.languages = languages;
        }

//        public String[] getFeatures()
//        {
//            return features.clone();
//        }
//
//        public void setFeatures( String[] features )
//        {
//            this.features = features;
//        }

        public String[] getModules()
        {
            return modules.clone();
        }

        public void setModules(String[] modules)
        {
            this.modules = modules;
        }

        public String[] getRoles()
        {
            return roles.clone();
        }

        public void setRoles(String[] roles)
        {
            this.roles = roles;
        }
//
//        public String getTestUserLogin()
//        {
//            return testUserLogin;
//        }
//
//        public void setTestUserLogin( String testUserLogin )
//        {
//            this.testUserLogin = testUserLogin;
//        }
//
//        public String getTestUserPassword()
//        {
//            return testUserPassword;
//        }
//
//        public void setTestUserPassword( String testUserPassword )
//        {
//            this.testUserPassword = testUserPassword;
//        }
//
//        public String getTestUserRole()
//        {
//            return testUserRole;
//        }
//
//        public void setTestUserRole( String testUserRole )
//        {
//            this.testUserRole = testUserRole;
//        }
//
//        public String getConnectionUrl()
//        {
//            return connectionUrl;
//        }
//
//        public void setConnectionUrl( String connectionUrl )
//        {
//            this.connectionUrl = connectionUrl;
//        }
//
//        public boolean shouldCreateLoginAndLogoutOperations()
//        {
//            return createLoginAndLogoutOperations;
//        }
//
//        public void setCreateLoginAndLogoutOperations( boolean createLoginAndLogoutOperations )
//        {
//            this.createLoginAndLogoutOperations = createLoginAndLogoutOperations;
//        }
    }

    //    public interface ISaveProject
//    {
//        void save(Project project) throws Exception;
//    }
//
    private final Parameters parameters;

    public ProjectGenerator(final Parameters parameters)
    {
        this.parameters = parameters;

        if (this.parameters.getProjectName() == null)
            throw new NullPointerException();
        if (this.parameters.getLanguages() == null)
            throw new NullPointerException();
//        if ( this.parameters.getFeatures() == null )
//            throw new NullPointerException();
        if (this.parameters.getModules() == null)
            throw new NullPointerException();
        if (this.parameters.getRoles() == null)
            throw new NullPointerException();
//        if ( this.parameters.getTestUserLogin() == null )
//            throw new NullPointerException();
//        if ( this.parameters.getTestUserPassword() == null )
//            throw new NullPointerException();
//        if ( this.parameters.getTestUserRole() == null )
//            throw new NullPointerException();
//        if ( this.parameters.getConnectionUrl() == null )
//            throw new NullPointerException();
//        if ( !Arrays.asList( this.parameters.getRoles() ).contains( this.parameters.getTestUserRole() ) )
//            throw new IllegalStateException();
    }

//    public static void generate( final Parameters parameters, final ISaveProject saveProject ) throws Exception
//    {
//        final ProjectGenerator generator = new ProjectGenerator( parameters );
//        generator.generate( saveProject );
//    }

    public Project generate()
    {
        final Project project = new Project(parameters.getProjectName());
        setRoles(project);
        setLanguages(project);
        addModules(project);
        //ModuleUtils.addModuleScripts( project );
        //addIncludes( project );
        //addFeatures( project );
        //addFtlScripts( project );

//        if ( parameters.shouldCreateLoginAndLogoutOperations() )
//            createLoginAndLogoutOperations( project );

//        saveProject.save( project );
//        copyTemplateFiles();
//        correctTemplateFiles();
        return project;
    }

    private void setRoles(final Project project)
    {
        final String[] roles = parameters.getRoles();

        for (final String role : roles)
            project.addRole(role);
    }

    private void setLanguages(final Project project)
    {
        for (final String language : parameters.getLanguages())
        {
            final Localizations localizations = project.getApplication().getLocalizations();
            final LanguageLocalizations languageLocalizations = new LanguageLocalizations(language, localizations);
            DataElementUtils.saveQuiet(languageLocalizations);
        }
    }
//
//    private void addFeatures( final Project project )
//    {
//        Set<String> features = new HashSet<>(Arrays.asList( parameters.getFeatures() ));
//        features.add( "logging" );
//        project.setFeatures( features );
//    }

    private void addModules(final Project project)
    {
        final String[] modules;

        {
            final List<String> ms = Lists.newArrayList(parameters.getModules());
            modules = Iterables.toArray(ms, String.class);
        }

        for (final String module : modules)
        {
            final Module newModule = new Module(module, project.getModules());
            DataElementUtils.saveQuiet(newModule);
        }
    }

//    public static void addIncludes( final Project project )
//    {
//        final StringBuilder sb = new StringBuilder();
//
//        for ( final Module module : project.getModules() )
//        {
//            final FreemarkerCatalog collection = module.getMacroCollection();
//
//            if ( collection == null )
//                continue;
//
//            final FreemarkerScript script = collection.optScript( FreemarkerCatalog.MAIN_MACRO_LIBRARY );
//
//            if ( script == null )
//                continue;
//
//            sb.append( "<#include \"../../Modules/" + module.getName() + "/Macros/common\"/>\n" );
//        }
//
//        final String includes = sb.toString();
//
//        if ( !includes.isEmpty() )
//        {
//            final FreemarkerScript script = new FreemarkerScript( FreemarkerCatalog.MAIN_MACRO_LIBRARY, project.getMacroCollection() );
//            script.setSource( includes );
//            DataElementUtils.saveQuiet( script );
//        }
//    }
//
//    private void addFtlScripts( final Project project )
//    {
//        final FreemarkerCatalog scripts = project.getApplication().getFreemarkerScripts();
//        final FreemarkerScript securityScript = new FreemarkerScript( "security", scripts );
//
//        securityScript.setSource( "INSERT INTO users (user_name, user_pass) VALUES( '$LOGIN', '$PASSWORD' );\nINSERT INTO user_roles VALUES( '$LOGIN', '$ROLE' );\n"
//            .replace( "$LOGIN", parameters.getTestUserLogin() )
//            .replace( "$PASSWORD", parameters.getTestUserPassword() )
//            .replace( "$ROLE", parameters.getTestUserRole() ) );
//        DataElementUtils.saveQuiet( securityScript );
//
//        final FreemarkerScript dictionariesScript = new FreemarkerScript( "dictionaries", scripts );
//        final FreemarkerScript attributesScript = new FreemarkerScript( "attributes", scripts );
//        dictionariesScript.setSource( "" );
//        attributesScript.setSource( "" );
//        DataElementUtils.saveQuiet( dictionariesScript );
//        DataElementUtils.saveQuiet( attributesScript );
//    }
//
//    private void createLoginAndLogoutOperations( final Project project )
//    {
//        final Entity logoutEntity = new Entity( "_logout_", project.getApplication(), EntityType.TABLE );
//        logoutEntity.setDisplayName( "Logout" );
//        logoutEntity.setOrder( "99" );
//        logoutEntity.setPrimaryKey( "dummy" );
//        logoutEntity.getIcon().setSource( Icon.SOURCE_BE );
//        logoutEntity.getIcon().setName( "logout.gif" );
//
//        final Query logout = new Query( "Logout", logoutEntity );
//        logout.setType( QueryType.STATIC );
//        logout.getRoles().add( '@'+SpecialRoleGroup.ALL_ROLES_EXCEPT_GUEST_GROUP );
//        logout.setQuery( "logout" );
//
//        DataElementUtils.saveQuiet( logout );
//        EntitiesFactory.addToModule( logoutEntity, project.getApplication() );
//
//        Module beModule = project.getModule( SYSTEM_MODULE );
//        Entity usersEntity = new Entity("users", beModule, EntityType.TABLE);
//        usersEntity.setOrder( "90" );
//        usersEntity.getIcon().setSource( "be:people.gif" );
//        DataElementUtils.saveQuiet( usersEntity );
//        Query loginQuery = new Query("Login", usersEntity);
//        loginQuery.setType( QueryType.STATIC );
//        loginQuery.setQuery( "login" );
//        loginQuery.getRoles().add( "Guest" );
//        DataElementUtils.saveQuiet( loginQuery );
////        Query forgotPassword = new Query("Forgot Password?", usersEntity);
////        forgotPassword.getRoles().add( "Guest" );
////        forgotPassword.setParametrizingOperationName( "Send password" );
////        DataElementUtils.saveQuiet( forgotPassword );
////        Operation sendPassword = Operation.createOperation( "Send password", Operation.OPERATION_TYPE_JAVA, usersEntity );
////        sendPassword.setCode( SendPassword.class.getName() );
////        DataElementUtils.saveQuiet( sendPassword );
//    }
//
//    private void copyTemplateFiles() throws IOException
//    {
////TODO        final Path basePath = ModuleUtils.getBasePath();
////        final Path sourceSrc = basePath.resolve( "template" );
////        final Path targetSrc = ModuleUtils.getBasePath().getParent().resolve( parameters.getProjectName() );
////
////        Files.createDirectories( targetSrc );
////        Files2.copyAll( sourceSrc, targetSrc );
//    }
//
//    private void correctTemplateFiles() throws IOException
//    {
////TODO        final Path target = ModuleUtils.getBasePath().getParent().resolve( parameters.getProjectName() );
////        final Path javaSrcFolder = target.resolve( "src/java" );
////        Files.createDirectories( javaSrcFolder );
////
////        {
////            // build.xml (@project@)
////            // .project
////            final Function<String, String> process = template -> template.replace( "@project@", parameters.getProjectName() );
////            processTemplate( target, "src/build.xml", process );
////            processTemplate( target, ".project", process );
////        }
////
////        {
////            // index.html/protected.html (@title@)
////            final Function<String, String> process = template -> template.replace( "@title@", parameters.getProjectName() );
////            processTemplate( target, "src/html/index.html", process );
////            processTemplate( target, "src/html/protected.html", process );
////        }
//    }
//
//    private void processTemplate( final Path targetSrc, final String fileName, final Function<String, String> process ) throws IOException
//    {
//        final Path file = targetSrc.resolve( fileName );
//        final String template = CharStreams.toString( new InputStreamReader( Files.newInputStream( file ), Charsets.UTF_8 ) );
//        final String content = process.apply( template );
//        Files.write( file, content.getBytes( Charsets.UTF_8 ) );
//    }

}
