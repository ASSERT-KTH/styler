package com.developmentontheedge.be5.maven;

import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.metadata.model.SpecialRoleGroup;
import com.developmentontheedge.be5.metadata.scripts.AppDb;
import com.developmentontheedge.be5.metadata.scripts.AppDropAllTables;
import com.developmentontheedge.be5.metadata.serialization.LoadContext;
import com.developmentontheedge.be5.metadata.serialization.ModuleLoader2;
import com.developmentontheedge.be5.metadata.serialization.Serialization;
import com.developmentontheedge.be5.metadata.util.NullLogger;
import com.developmentontheedge.be5.metadata.util.ProjectTestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;


public abstract class TestMavenUtils
{
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    protected Path tpmProjectPath;
    Project project;

    public final String profileTestMavenPlugin = "profileTestMavenPlugin2";

    @Before
    public void setUp() throws Exception
    {
        tpmProjectPath = tmp.newFolder().toPath();
        project = ProjectTestUtils.getProject("mavenTestProject");
        Entity entity = ProjectTestUtils.createEntity(project, "entity", "ID");
        ProjectTestUtils.createScheme(entity);
        ProjectTestUtils.createScript(project, "Post-db", "INSERT INTO entity (name) VALUES ('foo')");
        ProjectTestUtils.createScript(project, "data", "DELETE FROM entity;\nINSERT INTO entity (name) VALUES ('foo')");
        ProjectTestUtils.createH2Profile(project, profileTestMavenPlugin);

        Query query = ProjectTestUtils.createQuery(entity, "All records", Arrays.asList('@' + SpecialRoleGroup.ALL_ROLES_EXCEPT_GUEST_GROUP, "-User"));
        query.getOperationNames().setValues(Collections.singleton("op"));

        ProjectTestUtils.createOperation(entity, "op");

        Serialization.save(project, tpmProjectPath);

        ArrayList<URL> urls = new ArrayList<>();
        urls.add(tpmProjectPath.resolve("project.yaml").toUri().toURL());
        ModuleLoader2.loadAllProjects(urls, new NullLogger());

        LoadContext ctx = new LoadContext();
        ModuleLoader2.mergeAllModules(project, Collections.emptyList(), ctx);

        new AppDropAllTables()
                .setBe5ProjectPath(tpmProjectPath.toFile().toPath())
                .setProfileName(profileTestMavenPlugin)
                .setLogger(new NullLogger())
                .execute();
    }

    void createTestDB()
    {
        AppDb appDb = new AppDb();
        appDb.setBe5Project(project)
                .setProfileName(profileTestMavenPlugin)
                .setLogger(new NullLogger())
                .execute();

        assertEquals(1, appDb.getCreatedTables());
        assertEquals(0, appDb.getCreatedViews());
    }

    protected InputStream inputStream(String str)
    {
        try
        {
            return new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8.name()));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }
}
