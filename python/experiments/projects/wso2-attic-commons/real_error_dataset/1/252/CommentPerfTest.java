package org.wso2.registry.secure;

import org.wso2.registry.jdbc.utils.RegistryDataSource;
import org.wso2.registry.jdbc.realm.RegistryRealm;
import org.wso2.registry.jdbc.EmbeddedRegistry;
import org.wso2.registry.config.RegistryContext;
import org.wso2.registry.session.UserRegistry;
import org.wso2.registry.RegistryConstants;
import org.wso2.registry.Resource;
import org.wso2.registry.Comment;
import org.wso2.registry.Collection;
import org.wso2.registry.exceptions.RegistryException;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Map;
import java.util.HashMap;

import junit.framework.TestCase;


public class CommentPerfTest extends PerfTestingSetup {

    public CommentPerfTest (String text) {
      super(text);
    }

    public void testAddCommentTime() throws RegistryException {
         long totalTime = 0;
         for (int i=1;i<=maxFileToputResource;i++){
                     String path_1= "/c"+i;
                     String path_2= path_1 + "/b" +i;
                     String path= path_2+"/r1.txt";

                     try {
                         Comment c1 = new Comment();
                         c1.setResourcePath(path);
                         c1.setText("This is a test comment1");

                         long startTime = System.nanoTime();
                         adminRegistry.addComment(path,c1);
                         long runTime = System.nanoTime() - startTime;
                         totalTime += runTime;

                     }catch (Exception e) {
                        fail("Failed to add comments.");
                        e.printStackTrace();
                     }
         }
         System.out.println("Average time to add resource comment: "+ ((totalTime/1000000)/maxFileToputResource)+" ms");
     }

    public void testAccessComment() throws RegistryException {

         long totalTime = 0;

         for (int i=1;i<=maxFileToputResource;i++){
                     String path_1= "/c"+i;
                     String path_2= path_1 + "/b" +i;
                     String path= path_2+"/r1.txt";

                     try {
                         long startTime = System.nanoTime();
                         Comment[] comments1 = adminRegistry.getComments(path);
                         long runTime = System.nanoTime() - startTime;
                         totalTime += runTime;

                     }catch (Exception e) {
                        fail("Failed to read comments.");
                        e.printStackTrace();
                     }
         }
         System.out.println("Average time to access resource comment: "+ ((totalTime/1000000)/maxFileToputResource)+" ms");
     }

    public void testAddProperties() throws RegistryException {

        long totalTime = 0;
        try{
            for (int i=1;i<maxFileToputResource;i++){

                String path_1= "/c"+i;
                String path_2= path_1 + "/b" +i;
                String path= path_2+"/r1.txt";
                String key = "Registykey"+i;
                Resource r1 = adminRegistry.get(path);

                long startTime = System.nanoTime();
                r1.setProperty(key,"keyvalue2");
                adminRegistry.put(path,r1);
                long runTime = System.nanoTime() - startTime;
                totalTime += runTime;
            }
        }catch (Exception e) {
            fail("Failed to initialize the registry.");
            e.printStackTrace();
        }
        System.out.println("Average time to add property: "+ ((totalTime/1000000)/maxFileToputResource)+" ms");
    }

    public void testReadProperties() throws RegistryException {
        long totalTime = 0;
        try{
            for (int i=1;i<maxFileToputResource;i++){
                Resource r1 = adminRegistry.newResource();
                String path_1= "/c"+i;
                String path_2= path_1 + "/b" +i;
                String path= path_2+"/r1.txt";

                long startTime = System.nanoTime();
                r1 = adminRegistry.get(path);
                r1.getProperties();
                long runTime = System.nanoTime() - startTime;
                totalTime += runTime;
            }

        }catch (Exception e) {
            fail("Failed to initialize the registry.");
            e.printStackTrace();
        }
        System.out.println("Average time to access Properties: "+((totalTime/1000000)/maxFileToputResource)+" ms");
    }

    public void testPropertySearch() throws RegistryException {

        long totalTime = 0;
        try{
            for (int i=1;i<maxFileToputResource;i++){
                String path_1= "/c"+i;
                String path_2= path_1 + "/b" +i;
                String path= path_2+"/r1.txt";

                String sql1 = "SELECT R.PATH FROM RESOURCE R, PROPERTY P WHERE P.NAME LIKE ? AND R.RID=P.RID";

                Resource q1 = adminRegistry.newResource();
                q1.setContent(sql1);
                q1.setMediaType(RegistryConstants.SQL_QUERY_MEDIA_TYPE);
                adminRegistry.put(path, q1);

                Map<String, String> params = new HashMap <String, String> ();
                params.put("1", "%Registykey%");

                long startTime = System.nanoTime();
                Collection result = adminRegistry.executeQuery(path, params);
                String[] qPaths = (String[]) result.getContent();
                long runTime = System.nanoTime() - startTime;
                totalTime += runTime;
            }
        }catch (Exception e) {
            fail("Failed to execute query for resource name.");
            e.printStackTrace();
        }
        System.out.println("Average time to search Property Names: "+((totalTime/1000000)/maxFileToputResource)+" ms");
    }
}
