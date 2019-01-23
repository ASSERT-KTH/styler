package com.developmentontheedge.be5.metadata.scripts;

import com.developmentontheedge.be5.metadata.JulLogConfigurator;
import com.developmentontheedge.be5.metadata.model.DataElementUtils;
import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.be5.metadata.model.Module;
import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.metadata.model.SpecialRoleGroup;
import com.developmentontheedge.be5.metadata.serialization.LoadContext;
import com.developmentontheedge.be5.metadata.serialization.ModuleLoader2;
import com.developmentontheedge.be5.metadata.serialization.Serialization;
import com.developmentontheedge.be5.metadata.util.NullLogger;
import com.developmentontheedge.be5.metadata.util.ProjectTestUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public abstract class ScriptTestUtils
{
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    protected Path tpmProjectPath;
    protected Project project;

    public final String profileTestMavenPlugin = "profileTestMetadataScript";

    @BeforeClass
    public static void setUpClass()
    {
        JulLogConfigurator.config();
    }

    @Before
    public void setUp() throws Exception
    {
        tpmProjectPath = tmp.newFolder().toPath();
        project = ProjectTestUtils.getProject("test");
        Entity entity = ProjectTestUtils.createEntity(project, "entity", "ID");
        ProjectTestUtils.createScheme(entity);
        ProjectTestUtils.createScript(project, "Post-db", "INSERT INTO entity (name) VALUES ('foo')");
        ProjectTestUtils.createScript(project, "data", "DELETE FROM entity;\nINSERT INTO entity (name) VALUES ('foo')");
        ProjectTestUtils.createH2Profile(project, profileTestMavenPlugin);

        Query query = ProjectTestUtils.createQuery(entity, "All records", Arrays.asList('@' + SpecialRoleGroup.ALL_ROLES_EXCEPT_GUEST_GROUP, "-User"));
        query.getOperationNames().setValues(Collections.singleton("op"));

        ProjectTestUtils.createOperation(entity, "op");

        Path modulePath = tmp.newFolder().toPath();
        Project moduleProject = createModule(project, "testModule", modulePath);
        Serialization.save(project, tpmProjectPath);


        ArrayList<URL> urls = new ArrayList<>();
        urls.add(modulePath.resolve("project.yaml").toUri().toURL());
        urls.add(tpmProjectPath.resolve("project.yaml").toUri().toURL());
        ModuleLoader2.loadAllProjects(urls, new NullLogger());


        LoadContext ctx = new LoadContext();
        ModuleLoader2.mergeAllModules(project, Collections.singletonList(moduleProject), ctx);
    }

    private Project createModule(Project project, String moduleName, Path path) throws Exception
    {
        Project module = new Project(moduleName, true);
        Entity entity = ProjectTestUtils.createEntity(module, "moduleEntity", "ID");
        ProjectTestUtils.createScheme(entity);
        ProjectTestUtils.createScript(module, "Post-db", "INSERT INTO moduleEntity (name) VALUES ('foo')");
        Serialization.save(module, path);

        Module appModule = new Module(moduleName, project.getModules());
        project.setRoles(Arrays.asList("Administrator", "Guest", "User", "Operator"));
        DataElementUtils.save(appModule);

        return module;
    }

    protected void dropAndCreateTestDB() throws Exception
    {
        AppDropAllTables appDropAllTables = new AppDropAllTables();
        appDropAllTables.setBe5Project(project)
                .setProfileName(profileTestMavenPlugin)
                .execute();

        AppDb appDb = new AppDb();
        appDb.setBe5Project(project)
                .setProfileName(profileTestMavenPlugin)
                .execute();

        assertEquals(2, appDb.getCreatedTables());
        assertEquals(0, appDb.getCreatedViews());
    }

}
