package com.developmentontheedge.be5.operation;

import com.developmentontheedge.be5.base.BaseModule;
import com.developmentontheedge.be5.base.services.UserInfoProvider;
import com.developmentontheedge.be5.metadata.RoleType;
import com.developmentontheedge.be5.operation.services.OperationsFactory;
import com.developmentontheedge.be5.testbase.StaticUserInfoProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;


public abstract class OperationsSqlMockProjectTest extends OperationTestUtils
{
    private static Injector injector;
    protected static OperationsFactory operations;

    @ClassRule
    public static TemporaryFolder tmp = new TemporaryFolder();

    @BeforeClass
    public static void operationsSqlMockProjectTestSetUp() throws Exception
    {
        //initProjectWithOperation();

        injector = initInjector(
                Modules.override(new BaseModule()).with(new OperationsTestModule()),
                new OperationModule()
        );

        operations = injector.getInstance(OperationsFactory.class);
    }

    @Before
    public void baseTestUtilsSetUp() throws Exception
    {
        setStaticUserInfo(RoleType.ROLE_GUEST);
    }

    @Override
    public Injector getInjector()
    {
        return injector;
    }

    public static class OperationsTestModule extends AbstractModule
    {
        @Override
        protected void configure()
        {
            install(new BaseDbMockTestModule());
            bind(UserInfoProvider.class).to(StaticUserInfoProvider.class).in(Scopes.SINGLETON);
        }
    }
//
//    private static void initProjectWithOperation() throws IOException
//    {
//        Path path = tmp.newFolder().toPath();
//        Project prj = new Project("test");
//        Entity entity = new Entity( "testEntity", prj.getApplication(), EntityType.TABLE );
//        DataElementUtils.save( entity );
//        com.developmentontheedge.be5.metadata.model.Operation op = com.developmentontheedge.be5.metadata.model.Operation.createOperation( "ErrorProcessing",
//                com.developmentontheedge.be5.metadata.model.Operation.OPERATION_TYPE_JAVA, entity );
//
//        op.setCode(ErrorProcessing.class.getCanonicalName());
//        DataElementUtils.save( op );
//
//        try
//        {
//            Serialization.save( prj, path );
//            ModuleLoader2.loadAllProjects(Collections.singletonList(path.resolve("project.yaml").toUri().toURL()));
//        }
//        catch (IOException | ProjectSaveException e1)
//        {
//            e1.printStackTrace();
//        }
//    }

}
