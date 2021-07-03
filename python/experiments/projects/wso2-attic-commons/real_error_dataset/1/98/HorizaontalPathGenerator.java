package org.wso2.registry.secure;

import org.wso2.registry.Resource;
import org.wso2.registry.Registry;
import org.wso2.registry.ResourceImpl;
import org.wso2.registry.jdbc.EmbeddedRegistry;

import java.util.ArrayList;


public class HorizaontalPathGenerator implements PathGeneratorInterface {

    private String resourceName;
    private String root;
    private long level;
    private long chilperNode;
    private long numberofNodes;
    private int counter;
    ArrayList pathlist = new ArrayList();
    Object arrayObj[];


    public HorizaontalPathGenerator(long level, long chilperNode,  String root , long numberofNodes, String resourceName) {
        this.resourceName = resourceName;
        this.level = level;
        this.chilperNode = chilperNode;
        this.numberofNodes = numberofNodes;
        this.root = root;
        counter = 0;
        arrayObj = null;

    }

    public String getPath() throws Exception {

        String tempPath = null;
        //Object arrayObj[] = null;

        if (counter == 0){

            pathlist = generateTree(level, chilperNode, root, numberofNodes);
            arrayObj = pathlist.toArray();
        }

        if (counter < numberofNodes) {
            tempPath = arrayObj[counter].toString();
           // System.out.println(tempPath);
        }

        counter++;
        tempPath = tempPath + "/" + resourceName;
        //System.out.println(tempPath);
        return tempPath;

    }

    public ArrayList generateTree(long level, long chilperNode,  String root , long numberofNodes) throws Exception{
            ArrayList list = new ArrayList();
            int count =0;

            for (int i = 1; i <= level; i++) {
                for (int j = 1; j <= chilperNode; j++) {
                    Resource resource = new ResourceImpl();
                    resource.setContent("My Data".getBytes());
                    String collectionPath = root + root + i + root + j;
                    //System.out.println("the path is:"+ collectionPath);
                    list.add(collectionPath);
                    //registry.put(collectionPath,resource);
                    count ++;
                    //System.out.println (count);
                    if (count == numberofNodes) {
                        return list;
                    }
                }
            }
            return list;
        }

    //EmbeddedRegistry registry

        public String[] pathSplitter(String path) {

            String pathValue = null;
            String pathValue2 = null;
            String[] tokens = null;
            String splitPattern = "/";
            tokens = path.split(splitPattern);

            for (int i=1;i<tokens.length;i++)
            {
                pathValue = tokens[i];
                pathValue2 = "/"+pathValue;
                tokens[i-1] = pathValue2;
            }

            String yvalue = null;
            for (int j=0;j<tokens.length;j++)
            {
                if (j==0){
                    yvalue = tokens[j];
                }
                else{
                    yvalue = yvalue + tokens[j];
                }
            }
            return tokens;
        }

}

