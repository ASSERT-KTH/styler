package org.wso2.registry.secure;

import junit.framework.Test;
import junit.framework.TestSuite;
import com.clarkware.junitperf.LoadTest;
import org.wso2.registry.jdbc.utils.RegistryDataSource;
import org.wso2.registry.jdbc.realm.RegistryRealm;
import org.wso2.registry.jdbc.EmbeddedRegistry;
import org.wso2.registry.config.RegistryContext;
import org.wso2.registry.RegistryConstants;
import org.wso2.registry.Registry;
import org.wso2.registry.exceptions.RegistryException;
import org.wso2.registry.session.UserRegistry;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.FileInputStream;

public class ConcurrentPerformanceTestSuite extends TestSuite{

    static Registry adminRegistry = null;

    static UserRegistry userRegistry = null;
    static RegistryRealm realm = null;
    static DataSource dataSource = null;
    static RegistryRealm registryRealm = null;
    static EmbeddedRegistry registry = null;

    static int concurentUsers = 20;

    public static Test suite(){

        try{

            dataSource = new RegistryDataSource("jdbc:mysql://localhost:3306/regdb5",
                    "com.mysql.jdbc.Driver","regadmin","regadmin");

            registryRealm = new RegistryRealm(dataSource);
            InputStream in = new FileInputStream("registry.xml");
            RegistryContext registryContext = new RegistryContext(in);
            registry = new EmbeddedRegistry(registryContext);
            adminRegistry = registry.getUserRegistry(RegistryConstants.ADMIN_USER, RegistryConstants.ADMIN_PASSWORD);
            userRegistry = registry.getUserRegistry(RegistryConstants.ADMIN_USER, RegistryConstants.ADMIN_PASSWORD);

        }catch (RegistryException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Test testCaseGet = new ConcurrentUserTest("testGetConcurrentResources", userRegistry, concurentUsers);
        Test testCasePut = new ConcurrentUserTest("testPutConcurrentResource", userRegistry, concurentUsers);
        Test loadTestResourceGet = new LoadTest(testCaseGet, concurentUsers);
        Test loadTestResourcePut = new LoadTest(testCasePut, concurentUsers);
        TestSuite suite = new TestSuite();

        suite.addTest(new ConcurrentUserTest("testPopulateResources",userRegistry, concurentUsers));
        suite.addTest(loadTestResourceGet);
        suite.addTest(loadTestResourcePut);

//        try{
//            ConcurrentUserTest con = new ConcurrentUserTest();
//            System.out.println("Average Time take to read a resouce by" + concurentUsers + "concurrent users " + con.calculateGetTime());
//        }catch (Exception e) {
//            e.printStackTrace();
//        }

        return suite;
    }


}
