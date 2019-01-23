package com.developmentontheedge.be5.metadata.util;

import com.developmentontheedge.be5.metadata.model.BeConnectionProfile;
import com.developmentontheedge.be5.metadata.model.ColumnDef;
import com.developmentontheedge.be5.metadata.model.DataElementUtils;
import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.be5.metadata.model.EntityType;
import com.developmentontheedge.be5.metadata.model.FreemarkerScript;
import com.developmentontheedge.be5.metadata.model.LanguageStaticPages;
import com.developmentontheedge.be5.metadata.model.Module;
import com.developmentontheedge.be5.metadata.model.Operation;
import com.developmentontheedge.be5.metadata.model.PageCustomization;
import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.metadata.model.StaticPage;
import com.developmentontheedge.be5.metadata.model.TableDef;
import com.developmentontheedge.be5.metadata.serialization.Serialization;
import com.developmentontheedge.be5.metadata.sql.Rdbms;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

public class ProjectTestUtils
{
    public static Project getProject(String name)
    {
        Project project = new Project(name);

        project.setRoles(Arrays.asList("Administrator", "Guest", "User", "Operator"));
        project.setDatabaseSystem(Rdbms.POSTGRESQL);
        return project;
    }

    public static TableDef createScheme(Entity entity)
    {
        TableDef scheme = new TableDef(entity);
        DataElementUtils.save(scheme);
        ColumnDef column = new ColumnDef("ID", scheme.getColumns());
        column.setTypeString("KEYTYPE");
        column.setAutoIncrement(true);
        column.setPrimaryKey(true);
        DataElementUtils.save(column);

        ColumnDef column2 = new ColumnDef("name", scheme.getColumns());
        column2.setTypeString("VARCHAR(20)");
        column2.setCanBeNull(true);
        column2.setTableTo(entity.getName());
        column2.setColumnsTo(column.getName());
        DataElementUtils.save(column2);

        return scheme;
    }

    public static Operation createOperation(Entity entity, String name)
    {
        Operation operation = Operation.createOperation(name, Operation.OPERATION_TYPE_JAVA, entity);
        DataElementUtils.save(operation);
        PageCustomization customization = new PageCustomization(PageCustomization.TYPE_CSS, PageCustomization.DOMAIN_OPERATION_FORM,
                operation.getOrCreateCollection(PageCustomization.CUSTOMIZATIONS_COLLECTION, PageCustomization.class));
        customization.setCode("form {color: #f1f1f1}");
        DataElementUtils.save(customization);
        return operation;
    }

    public static Query createQuery(Entity entity, String name, Collection<String> roles)
    {
        Query query = new Query(name, entity);
        query.getRoles().parseRoles(roles);
        query.setQuery("select * from entity");
        DataElementUtils.save(query);
        return query;
    }

    public static Entity createEntity(Project project, String entityName, String primaryKeyName)
    {
        Entity entity = new Entity(entityName, project.getApplication(), EntityType.TABLE);
        entity.setPrimaryKey(primaryKeyName);
        entity.setBesql(true);
        DataElementUtils.save(entity);
        return entity;
    }

    public static StaticPage createStaticPage(Project project, String lang, String name, String content)
    {
        LanguageStaticPages lsp = new LanguageStaticPages("en", project.getApplication().getStaticPageCollection());
        DataElementUtils.save(lsp);
        StaticPage staticPage = new StaticPage(name, lsp);
        DataElementUtils.save(staticPage);
        staticPage.setComment("Comment");
        staticPage.setContent(content);
        return staticPage;
    }

    public static void createScript(Project project, String name, String sql)
    {
        FreemarkerScript script = new FreemarkerScript(name,
                project.getApplication().getFreemarkerScripts());
        script.setSource(sql);
        DataElementUtils.save(script);
    }

    public static void createH2Profile(Project project, String name)
    {
        BeConnectionProfile profile = new BeConnectionProfile(name, project.getConnectionProfiles().getLocalProfiles());
        profile.setConnectionUrl("jdbc:h2:~/" + name);
        profile.setUsername("sa");
        profile.setPassword("");
        profile.setDriverDefinition(Rdbms.H2.getDriverDefinition());
        DataElementUtils.save(profile);
    }

    public static Project createModule(Project project, String moduleName, Path path) throws Exception
    {
        Project module = new Project(moduleName, true);
        Entity entity = ProjectTestUtils.createEntity(module, "moduleEntity", "ID");
        createScheme(entity);
        createScript(module, "Post-db", "INSERT INTO moduleEntity (name) VALUES ('foo')");
        Serialization.save(module, path);

        Module appModule = new Module(moduleName, project.getModules());
        project.setRoles(Arrays.asList("Administrator", "Guest", "User", "Operator"));
        DataElementUtils.save(appModule);

        return module;
    }

}
