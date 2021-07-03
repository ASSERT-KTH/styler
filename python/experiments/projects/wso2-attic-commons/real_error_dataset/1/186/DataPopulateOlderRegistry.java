package org.wso2.registry.secure;

import junit.framework.TestCase;
import org.wso2.registry.*;
import org.wso2.registry.jdbc.JDBCRegistry;
import org.wso2.registry.jdbc.realm.RegistryRealm;
import org.wso2.registry.jdbc.utils.RegistryDataSource;
import org.wso2.usermanager.Realm;
import org.wso2.usermanager.UserManagerException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;


public class DataPopulateOlderRegistry extends TestCase {

    private static Registry registry = null;
    private static RegistryRealm realm = null;
    int maxResourceCount = 2000;
    int maxUserCount = 100;
    int maxRoleCount = 10;
    int maxFileToputCollections = 100;
    int maxFileToputResource = 100;

    DataSource dataSource = null;

    public DataPopulateOlderRegistry(String text) {
      super(text);
    }


    public void setUp() {
        try {
            dataSource = new RegistryDataSource("jdbc:derby://localhost:1527/derbyDB",
                    "org.apache.derby.jdbc.ClientDriver","reg","reg");
            //dataSource = new RegistryDataSource("jdbc:hsqldb:mem:aname","org.hsqldb.jdbcDriver","","sa");
            realm = new RegistryRealm(dataSource);
            registry = new JDBCRegistry(dataSource, realm);

        }catch (Exception e) {
            fail("Failed to initialize the registry.");
            e.printStackTrace();
       }
    }


    public void testpopulateResources() throws RegistryException {

        SecureRegistry adminRegistry =
                new SecureRegistry(RegistryConstants.ADMIN_USER, "admin", registry, realm);

        for (int i=1;i<=maxResourceCount;i++){
            String path_1= "/c"+i;
            String path_2= path_1 + "/b" +i;
            String path= path_2+"/r1.txt";

            try {
                Resource r1 = new Resource();
                r1.setDescription("This is a file to be renamed");
                String fileContent = "This is file the content";

                for (int filec=1;filec<=5;filec++){
                    fileContent += fileContent;
                }

                byte[] r1content = (fileContent).getBytes();
                r1.setContent(r1content);
                r1.setMediaType("txt");

                Comment c1 = new Comment();
                c1.setResourcePath(path);
                c1.setText("This is a test comment1");

                Comment c2 = new Comment();
                c2.setResourcePath(path);
                c2.setText("This is a test comment2");

                r1.setProperty("key1", "value1");
                r1.setProperty("key2", "value2");
                r1.setProperty("key3", "value3");
                r1.setProperty("key4", "value4");

                adminRegistry.put(path,r1);
                adminRegistry.addComment(path,c1);
                adminRegistry.addComment(path,c2);
                adminRegistry.applyTag(path, "tag1");
                adminRegistry.applyTag(path, "tag2");
                adminRegistry.applyTag(path, "tag3");
                adminRegistry.rateResource(path, 4);

            }catch (RegistryException e) {
                e.printStackTrace();
            }
        }
        System.out.println("End of creating"+" "+maxResourceCount+" Resources");
    }
    /*
    public void testAddDependency() throws RegistryException {

        for (int i=1;i<=maxResourceCount;i++){
            String path_1= "/c"+i;
            String path_2= path_1 + "/b" +i;
            String path= path_2+"/r1.txt";

            if ((i+1)<maxResourceCount){
                        String path_dependent_1= "/c"+(i+1);
                        String path_dependent_2 = path_dependent_1 + "/b" +(i+1);
                        String path_dependent_3 = path_dependent_2 +"/r1.txt";
                        registry.addAssociation(Association.DEPENDENENT,path_dependent_3,path);

            }
        }
        System.out.println("End of Adding Dependencies to "+" "+maxResourceCount+ " Resources");
    }
    */
    public void testpopulateUserBasedAuthorization() throws RegistryException {

        SecureRegistry adminRegistry =
                new SecureRegistry(RegistryConstants.ADMIN_USER, "admin", registry, realm);
        Realm adminRealm = adminRegistry.getUserRealm();

        String user;
        String newRoleName = "registryTeam";

        /*add registry-team role*/
        try{
            adminRealm.getUserStoreAdmin().addRole(newRoleName);
        }catch (UserManagerException e) {
            e.printStackTrace();
        }

        /*create users and assing to a role*/
        for (int i=1; i<=maxUserCount;i++){
            user= "user" +i;

            try {
                adminRealm.getUserStoreAdmin().addUser(user, "psw");
                Map currentProperties = adminRealm.getUserStoreAdmin().getUserProperties(user);
                currentProperties.put("friendlyName", "friendlyName");
                adminRealm.getUserStoreAdmin().setUserProperties(user, currentProperties);
                adminRealm.getUserStoreAdmin().addUserToRole(user,newRoleName);

            }catch (UserManagerException e) {
               e.printStackTrace();
            }
        }

        System.out.println("End of Adding"+" "+maxUserCount+" users to the role"+" "+newRoleName);

        /*creating more roles*/
        for (int i=1; i<=maxRoleCount;i++){
            String role= "role" +i;
            try {
                adminRealm.getUserStoreAdmin().addRole(role);
            }catch (UserManagerException e) {
               e.printStackTrace();
            }
         }
        System.out.println("End of Adding"+" "+maxRoleCount+" "+"roles");

    }

    public void testAssignPermissionToResources() throws RegistryException{

        SecureRegistry adminRegistry =
                new SecureRegistry(RegistryConstants.ADMIN_USER, "admin", registry, realm);
        Realm adminRealm = adminRegistry.getUserRealm();
        String user;

        /*assing PUT and DELETE permission to resources for each user*/
        for (int i=1;i<=maxUserCount;i++){
            String resource_1= "/c"+i;
            String resource_2= resource_1 + "/b" +i;
            String resource_path= resource_2+"/r1.txt";
            user= "user" +i;

            try{
            adminRealm.getAccessControlAdmin()
                    .authorizeUser(user, resource_path, ActionConstants.PUT);
            adminRealm.getAccessControlAdmin()
                    .authorizeUser(user, resource_path, ActionConstants.DELETE);
            }catch (UserManagerException e) {
               e.printStackTrace();
            }
        }
        System.out.println("End of Assign User level write and delete permissoin to "+" "+maxUserCount+" "+"users");
        /*assing DELETE permission to resources for each role*/
        for (int i=1;i<=maxResourceCount;i++){
            for (int j=1;j<=maxRoleCount;j++){
                if ((i==j)||(i%maxRoleCount==j)||(i%maxRoleCount==0)){
                    String role = "role" +j;
                    String resource_1= "/c"+i;
                    String resource_2= resource_1 + "/b" +i;
                    String resource_path= resource_2+"/r1.txt";

                    try{
                    adminRealm.getAccessControlAdmin()
                            .authorizeRole(role,resource_path,ActionConstants.PUT);
                    adminRealm.getAccessControlAdmin()
                            .authorizeRole(role,resource_path,ActionConstants.DELETE);
                    }catch (UserManagerException e) {
                       e.printStackTrace();
                    }
                    if (j==10||(i%maxRoleCount==0)){
                        break;
                    }

                }
            }
        }
        System.out.println("End of Assign role level write and delete permissoin to "+" "+maxResourceCount+" "+"resources");
    }

    /* update existing resource to increase version numbers */
    public void testpopulateResourceVersioning() throws RegistryException{

        SecureRegistry adminRegistry =
                new SecureRegistry(RegistryConstants.ADMIN_USER, "admin", registry, realm);

        for (int i=1;i<=maxResourceCount;i++){
            String path_1= "/c"+i;
            String path_2= path_1 + "/b" +i;
            String path= path_2+"/r1.txt";
            try {
                Resource r1 = new Resource();
                r1.setDescription("This is a file to be renamed");
                String fileContent = "This is file the content updated";

                r1.setProperty("key1", "value1");
                r1.setProperty("key2", "value2");
                r1.setProperty("key3", "value3");
                r1.setProperty("key4", "value4");

                for (int filec=0;filec<=5;filec++){
                    fileContent += fileContent;
                }
                byte[] r1content = (fileContent).getBytes();
                r1.setContent(r1content);
                r1.setMediaType("txt");
                adminRegistry.put(path,r1);

            }catch (RegistryException e) {
                e.printStackTrace();
            }
        }
        System.out.println("End of creating 2nd version of "+" "+maxResourceCount+" "+"resources");
    }

    public void testgetresourcetime() throws RegistryException{

        SecureRegistry adminRegistry =
                new SecureRegistry("user50", "psw", registry, realm);

        String path = "/c50/b50/r1.txt";
        long totaltime = 0;
        long numberOfTimes = 10000;

        for (int i=1;i<=numberOfTimes;i++){
            try {
                  long startTime = System.currentTimeMillis();
                  adminRegistry.get(path);
                  long stopTime = System.currentTimeMillis();
                  long runTime = stopTime - startTime;
                  totaltime += runTime;
                  //System.out.println("Run time: " + runTime);

            } catch (RegistryException e) {
                fail("Couldn't get content from path" + path);
            }
        }

        System.out.println("Average Resource get time: " + totaltime/numberOfTimes+ " ms");
    }

    public void testgetCollectiontime() throws RegistryException{

        SecureRegistry adminRegistry =
                new SecureRegistry(RegistryConstants.ADMIN_USER, "admin", registry, realm);

        Resource r1_actual = new Resource();
        r1_actual.setDirectory(true);
        String path = "/c50/b50";
        long totaltime = 0;
        long numberOfTimes = 10000;

        for (int i=1;i<=numberOfTimes;i++){
            try {
                  long startTime = System.currentTimeMillis();
                  adminRegistry.get(path);
                  long stopTime = System.currentTimeMillis();
                  long runTime = stopTime - startTime;
                  totaltime += runTime;
                  //System.out.println("Run time: " + runTime);

            } catch (RegistryException e) {
                fail("Couldn't get content from path" + path);
            }
        }

        System.out.println("Average Collection get time: " + totaltime/numberOfTimes+ " ms");
    }

    public void testputresourcetime() throws RegistryException{

        SecureRegistry adminRegistry =
                new SecureRegistry(RegistryConstants.ADMIN_USER, "admin", registry, realm);

        long totaltime = 0;
        for (int i=1;i<=maxFileToputResource;i++){
            String path_1= "/d"+i;
            String path_2= path_1 + "/b" +i;
            String path= path_2+"/r1.txt";

            try {
                Resource r1 = new Resource();
                r1.setDescription("This is new file description");
                String fileContent = "This is file the content";

                for (int filec=0;filec<=10;filec++){
                    fileContent += fileContent;
                }

                byte[] r1content = (fileContent).getBytes();
                r1.setContent(r1content);
                r1.setMediaType("txt");

                Comment c1 = new Comment();
                c1.setResourcePath(path);
                c1.setText("This is a test comment1");

                Comment c2 = new Comment();
                c2.setResourcePath(path);
                c2.setText("This is a test comment2");

                r1.setProperty("key1", "value1");
                r1.setProperty("key2", "value2");
                r1.setProperty("key3", "value3");
                r1.setProperty("key4", "value4");

                long startTime = System.currentTimeMillis();
                adminRegistry.put(path,r1);
                long stopTime = System.currentTimeMillis();
                long runTime = stopTime - startTime;
                totaltime += runTime;

                adminRegistry.addComment(path,c1);
                adminRegistry.addComment(path,c2);
                adminRegistry.applyTag(path, "tag1");
                adminRegistry.applyTag(path, "tag2");
                adminRegistry.applyTag(path, "tag3");
                adminRegistry.rateResource(path, 4);

            }catch (RegistryException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Average Resource put time: " + totaltime/maxFileToputResource+ " ms");
    }

        public void testputcollectiontime() throws RegistryException{

        SecureRegistry adminRegistry =
                new SecureRegistry(RegistryConstants.ADMIN_USER, "admin", registry, realm);


        long totaltime = 0;
        for (int i=1;i<=maxFileToputCollections;i++){
            String path_1= "/dd"+i;
            String path_2= path_1 + "/bb" +i;


            try {
                Resource r1 = new Resource();
                r1.setDirectory(true);
                r1.setDescription("This is new collection description");

                Comment c1 = new Comment();
                c1.setResourcePath(path_2);
                c1.setText("This is a test comment1");

                Comment c2 = new Comment();
                c2.setResourcePath(path_2);
                c2.setText("This is a test comment2");

                r1.setProperty("key1", "value1");
                r1.setProperty("key2", "value2");
                r1.setProperty("key3", "value3");
                r1.setProperty("key4", "value4");

                long startTime = System.currentTimeMillis();
                adminRegistry.put(path_2,r1);
                long stopTime = System.currentTimeMillis();
                long runTime = stopTime - startTime;
                totaltime += runTime;

                adminRegistry.addComment(path_2,c1);
                adminRegistry.addComment(path_2,c2);
                adminRegistry.applyTag(path_2, "tag1");
                adminRegistry.applyTag(path_2, "tag2");
                adminRegistry.applyTag(path_2, "tag3");
                adminRegistry.rateResource(path_2, 4);

            }catch (RegistryException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Average Collection put time: " + totaltime/maxFileToputCollections+ " ms");
    }

    public void testrenameresourcetime() throws RegistryException{
        SecureRegistry adminRegistry =
                new SecureRegistry(RegistryConstants.ADMIN_USER, "admin", registry, realm);

        long totaltime = 0;
        for (int i=1;i<=maxFileToputResource;i++){
            String path_1= "/d"+i;
            String path_2= path_1 + "/b" +i;
            String path= path_2+"/r1.txt";
            String new_path_1= "/R"+i;
            String new_path_2= new_path_1 + "/A" +i;
            String new_path= new_path_2+"/r2.txt";

            try {
                long startTime = System.currentTimeMillis();
                adminRegistry.rename(path,new_path);
                long stopTime = System.currentTimeMillis();
                long runTime = stopTime - startTime;
                totaltime += runTime;
                //System.out.println(runTime);

            }catch (RegistryException e) {
                        e.printStackTrace();
            }
        }
        System.out.println("Average Time for resource rename: " + totaltime/maxFileToputResource+ " ms");
    }

    public void testrenamecollectiontime() throws RegistryException{
       SecureRegistry adminRegistry =
                new SecureRegistry(RegistryConstants.ADMIN_USER, "admin", registry, realm);

        long totaltime = 0;
        for (int i=1;i<=maxFileToputCollections;i++){
            System.out.println("This is a test");
            String path_1= "/dd"+i;
            String path_2= path_1 + "/bb" +i;
            String new_path_1= "/gg"+i;
            String new_path_2= new_path_1 + "/kk" +i;

            try {
                long startTime = System.currentTimeMillis();
                adminRegistry.rename(path_2,new_path_2);
                long stopTime = System.currentTimeMillis();
                long runTime = stopTime - startTime;
                totaltime += runTime;
                //System.out.println(runTime);
            }catch (RegistryException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Average time for collection rename: " + totaltime/maxFileToputCollections+ " ms");
    }

    public void testdeleteresourcetime() throws RegistryException{

        SecureRegistry adminRegistry =
                new SecureRegistry(RegistryConstants.ADMIN_USER, "admin", registry, realm);

        long totaltime = 0;
        for (int i=1;i<=maxFileToputResource;i++){
            String path_1= "/c"+i;
            String path_2= path_1 + "/b" +i;
            String path= path_2+"/r1.txt";

            try {
                long startTime = System.currentTimeMillis();
                adminRegistry.delete(path);
                long stopTime = System.currentTimeMillis();
                long runTime = stopTime - startTime;
                totaltime += runTime;
                //System.out.println("Run time: " + runTime);

            }catch (RegistryException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Average Resource delete time: " + totaltime/maxFileToputResource+ " ms");
    }

    public void testdeletecollectiontime() throws RegistryException{

        SecureRegistry adminRegistry =
                new SecureRegistry(RegistryConstants.ADMIN_USER, "admin", registry, realm);


        long totaltime = 0;
        for (int i=1;i<=maxFileToputCollections;i++){
            String path_1= "/dd"+i;
            String path_2= path_1 + "/bb" +i;


            try {
                Resource r1 = new Resource();
                r1.setDirectory(true);
                r1.setDescription("This is new collection description");

                long startTime = System.currentTimeMillis();
                adminRegistry.delete(path_2);
                long stopTime = System.currentTimeMillis();
                long runTime = stopTime - startTime;
                totaltime += runTime;
                //System.out.println("Run time: " + runTime);

            }catch (RegistryException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Average Collection delete time: " + totaltime/maxFileToputCollections+ " ms");
    }

    public void testdatabaseStatistics() throws RegistryException{

         try{
            Connection con = dataSource.getConnection();
            con.setAutoCommit(false);

            String[] tableArray= {"REG.VERSIONS","REG.ARTIFACTS","REG.COMMENTS",
                    "REG.DEPENDENCY","REG.LOGS","REG.PROPERTIES",
                    "REG.RATINGS","REG.TAGS","REG.UM_PERMISSIONS","REG.UM_ROLES",
                    "REG.UM_ROLE_ATTRIBUTES","REG.UM_ROLE_PERMISSIONS","REG.UM_USERS",
                    "REG.UM_USER_ATTRIBUTES","REG.UM_USER_PERMISSIONS","REG.UM_USER_ROLES"
                    ,"REG.CHILDREN"};

             System.out.println("Row Count of each Registry Table"+'\n');

             for (int i=0;i<tableArray.length;i++){
                 String query =  "SELECT COUNT(*) as RowCount FROM"+" "+tableArray[i];
                 PreparedStatement pstmt = con.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    int rowcount= rs.getInt("RowCount");
                    System.out.println("Row count"+" "+tableArray[i]+": "+rowcount);
                }

                 rs.close();
                 pstmt.close();

             }
             con.close();

             }catch (SQLException e){
                e.printStackTrace();
         }
    }
}

