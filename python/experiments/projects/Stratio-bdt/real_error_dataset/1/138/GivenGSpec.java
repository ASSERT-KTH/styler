/*
 * Copyright (C) 2014 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stratio.qa.specs;

import com.auth0.jwt.JWTSigner;
import com.ning.http.client.Response;
import com.ning.http.client.cookie.Cookie;
import com.stratio.qa.exceptions.DBException;
import com.stratio.qa.utils.GosecSSOUtils;
import com.stratio.qa.utils.RemoteSSHConnection;
import com.stratio.qa.utils.ThreadProperty;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import org.assertj.core.api.Assertions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.stratio.qa.assertions.Assertions.assertThat;

/**
 * Generic Given Specs.
 * @see <a href="GivenGSpec-annotations.html">Given Steps &amp; Matching Regex</a>
 */
public class GivenGSpec extends BaseGSpec {

    public static final Integer ES_DEFAULT_NATIVE_PORT = 9300;

    public static final String ES_DEFAULT_CLUSTER_NAME = "elasticsearch";

    /**
     * Generic constructor.
     *
     * @param spec object
     */
    public GivenGSpec(CommonG spec) {
        this.commonspec = spec;

    }

    /**
     * Create a basic Index.
     *
     * @param index_name index name
     * @param table      the table where index will be created.
     * @param column     the column where index will be saved
     * @param keyspace   keyspace used
     * @throws Exception exception
     */
    @Given("^I create a Cassandra index named '(.+?)' in table '(.+?)' using magic_column '(.+?)' using keyspace '(.+?)'$")
    public void createBasicMapping(String index_name, String table, String column, String keyspace) throws Exception {
        String query = "CREATE INDEX " + index_name + " ON " + table + " (" + column + ");";
        commonspec.getCassandraClient().executeQuery(query);
    }

    /**
     * Create a Cassandra Keyspace.
     *
     * @param keyspace cassandra keyspace
     */
    @Given("^I create a Cassandra keyspace named '(.+)'$")
    public void createCassandraKeyspace(String keyspace) {
        commonspec.getCassandraClient().createKeyspace(keyspace);
    }

    /**
     * Connect to cluster.
     *
     * @param clusterType DB type (Cassandra|Mongo|Elasticsearch)
     * @param url         url where is started Cassandra cluster
     */
    @Given("^I connect to '(Cassandra|Mongo|Elasticsearch)' cluster at '(.+)'$")
    public void connect(String clusterType, String url) throws DBException, UnknownHostException {
        switch (clusterType) {
            case "Cassandra":
                commonspec.getCassandraClient().setHost(url);
                commonspec.getCassandraClient().buildCluster();
                commonspec.getCassandraClient().connect();
                break;
            case "Mongo":
                commonspec.getMongoDBClient().connect();
                break;
            case "Elasticsearch":
                LinkedHashMap<String, Object> settings_map = new LinkedHashMap<String, Object>();
                settings_map.put("cluster.name", System.getProperty("ES_CLUSTER", ES_DEFAULT_CLUSTER_NAME));
                commonspec.getElasticSearchClient().setSettings(settings_map);
                commonspec.getElasticSearchClient().connect();
                break;
            default:
                throw new DBException("Unknown cluster type");
        }
    }

    /**
     * Connect to ElasticSearch using custom parameters
     *
     * @param host ES host
     * @param foo regex needed to match method
     * @param nativePort ES port
     * @param bar regex needed to match method
     * @param clusterName ES clustername
     * @throws DBException exception
     * @throws UnknownHostException exception
     * @throws NumberFormatException exception
     */
    @Given("^I connect to Elasticsearch cluster at host '(.+?)'( using native port '(.+?)')?( using cluster name '(.+?)')?$")
    public void connectToElasticSearch(String host, String foo, String nativePort, String bar, String clusterName) throws DBException, UnknownHostException, NumberFormatException {
        LinkedHashMap<String, Object> settings_map = new LinkedHashMap<String, Object>();
        if (clusterName != null) {
            settings_map.put("cluster.name", clusterName);
        } else {
            settings_map.put("cluster.name", ES_DEFAULT_CLUSTER_NAME);
        }
        commonspec.getElasticSearchClient().setSettings(settings_map);
        if (nativePort != null) {
            commonspec.getElasticSearchClient().setNativePort(Integer.valueOf(nativePort));
        } else {
            commonspec.getElasticSearchClient().setNativePort(ES_DEFAULT_NATIVE_PORT);
        }
        commonspec.getElasticSearchClient().setHost(host);
        commonspec.getElasticSearchClient().connect();
    }

    /**
     * Create table
     *
     * @param table Cassandra table
     * @param datatable datatable used for parsing elements
     * @param keyspace Cassandra keyspace
     */
    @Given("^I create a Cassandra table named '(.+?)' using keyspace '(.+?)' with:$")
    public void createTableWithData(String table, String keyspace, DataTable datatable) {
        try {
            commonspec.getCassandraClient().useKeyspace(keyspace);
            int attrLength = datatable.getGherkinRows().get(0).getCells().size();
            Map<String, String> columns = new HashMap<String, String>();
            ArrayList<String> pk = new ArrayList<String>();

            for (int i = 0; i < attrLength; i++) {
                columns.put(datatable.getGherkinRows().get(0).getCells().get(i),
                        datatable.getGherkinRows().get(1).getCells().get(i));
                if ((datatable.getGherkinRows().size() == 3) && datatable.getGherkinRows().get(2).getCells().get(i).equalsIgnoreCase("PK")) {
                    pk.add(datatable.getGherkinRows().get(0).getCells().get(i));
                }
            }
            if (pk.isEmpty()) {
                throw new Exception("A PK is needed");
            }
            commonspec.getCassandraClient().createTableWithData(table, columns, pk);
        } catch (Exception e) {
            commonspec.getLogger().debug("Exception captured");
            commonspec.getLogger().debug(e.toString());
            commonspec.getExceptions().add(e);
        }
    }

    /**
     * Insert Data
     *
     * @param table Cassandra table
     * @param datatable datatable used for parsing elements
     * @param keyspace Cassandra keyspace
     */
    @Given("^I insert in keyspace '(.+?)' and table '(.+?)' with:$")
    public void insertData(String keyspace, String table, DataTable datatable) {
        try {
            commonspec.getCassandraClient().useKeyspace(keyspace);
            int attrLength = datatable.getGherkinRows().get(0).getCells().size();
            Map<String, Object> fields = new HashMap<String, Object>();
            for (int e = 1; e < datatable.getGherkinRows().size(); e++) {
                for (int i = 0; i < attrLength; i++) {
                    fields.put(datatable.getGherkinRows().get(0).getCells().get(i), datatable.getGherkinRows().get(e).getCells().get(i));

                }
                commonspec.getCassandraClient().insertData(keyspace + "." + table, fields);

            }
        } catch (Exception e) {
            commonspec.getLogger().debug("Exception captured");
            commonspec.getLogger().debug(e.toString());
            commonspec.getExceptions().add(e);
        }
    }


    /**
     * Save value for future use.
     * <p>
     * If element is a jsonpath expression (i.e. $.fragments[0].id), it will be
     * applied over the last httpResponse.
     * <p>
     * If element is a jsonpath expression preceded by some other string
     * (i.e. ["a","b",,"c"].$.[0]), it will be applied over this string.
     * This will help to save the result of a jsonpath expression evaluated over
     * previous stored variable.
     *
     * @param position position from a search result
     * @param element  key in the json response to be saved
     * @param envVar   thread environment variable where to store the value
     * @throws IllegalAccessException exception
     * @throws IllegalArgumentException exception
     * @throws SecurityException exception
     * @throws NoSuchFieldException exception
     * @throws ClassNotFoundException exception
     * @throws InstantiationException exception
     * @throws InvocationTargetException exception
     * @throws NoSuchMethodException exception
     */
    @Given("^I save element (in position \'(.+?)\' in )?\'(.+?)\' in environment variable \'(.+?)\'$")
    public void saveElementEnvironment(String foo, String position, String element, String envVar) throws Exception {

        Pattern pattern = Pattern.compile("^((.*)(\\.)+)(\\$.*)$");
        Matcher matcher = pattern.matcher(element);
        String json;
        String parsedElement;

        if (matcher.find()) {
            json = matcher.group(2);
            parsedElement = matcher.group(4);
        } else {
            json = commonspec.getResponse().getResponse();
            parsedElement = element;
        }

        String value = commonspec.getJSONPathString(json, parsedElement, position);

        ThreadProperty.set(envVar, value.replaceAll("\n", ""));
    }


    /**
     * Save value for future use.
     *
     * @param value  value to be saved
     * @param envVar thread environment variable where to store the value
     */
    @Given("^I save \'(.+?)\' in variable \'(.+?)\'$")
    public void saveInEnvironment(String value, String envVar) {
        ThreadProperty.set(envVar, value);
    }


    /**
     * Save clustername of elasticsearch in an environment varible for future use.
     *
     * @param host   elasticsearch connection
     * @param port   elasticsearch port
     * @param envVar thread variable where to store the value
     * @throws IllegalAccessException exception
     * @throws IllegalArgumentException exception
     * @throws SecurityException exception
     * @throws NoSuchFieldException exception
     * @throws ClassNotFoundException exception
     * @throws InstantiationException exception
     * @throws InvocationTargetException exception
     * @throws NoSuchMethodException exception
     */
    @Given("^I obtain elasticsearch cluster name in '([^:]+?)(:.+?)?' and save it in variable '(.+?)'?$")
    public void saveElasticCluster(String host, String port, String envVar) throws Exception {

        setupRestClient(null, host, port);

        Future<Response> response;

        response = commonspec.generateRequest("GET", false, null, null, "/", "", "json", "");
        commonspec.setResponse("GET", response.get());

        String json;
        String parsedElement;
        json = commonspec.getResponse().getResponse();
        parsedElement = "$..cluster_name";

        String json2 = "[" + json + "]";
        String value = commonspec.getJSONPathString(json2, parsedElement, "0");

        if (value == null) {
            throw new Exception("No cluster name is found");
        } else {
            ThreadProperty.set(envVar, value);
        }
    }


    /**
     * Drop all the ElasticSearch indexes.
     */
    @Given("^I drop every existing elasticsearch index$")
    public void dropElasticsearchIndexes() {
        commonspec.getElasticSearchClient().dropAllIndexes();
    }

    /**
     * Drop an specific index of ElasticSearch.
     *
     * @param index ES index
     */
    @Given("^I drop an elasticsearch index named '(.+?)'$")
    public void dropElasticsearchIndex(String index) {
        commonspec.getElasticSearchClient().dropSingleIndex(index);
    }

    /**
     * Drop a Cassandra Keyspace.
     *
     * @param keyspace Cassandra keyspace
     */
    @Given("^I drop a Cassandra keyspace '(.+)'$")
    public void dropCassandraKeyspace(String keyspace) {
        commonspec.getCassandraClient().dropKeyspace(keyspace);
    }

    /**
     * Create a MongoDB dataBase.
     *
     * @param databaseName Mongo database
     */
    @Given("^I create a MongoDB dataBase '(.+?)'$")
    public void createMongoDBDataBase(String databaseName) {
        commonspec.getMongoDBClient().connectToMongoDBDataBase(databaseName);

    }

    /**
     * Drop MongoDB Database.
     *
     * @param databaseName mongo database
     */
    @Given("^I drop a MongoDB database '(.+?)'$")
    public void dropMongoDBDataBase(String databaseName) {
        commonspec.getMongoDBClient().dropMongoDBDataBase(databaseName);
    }

    /**
     * Insert data in a MongoDB table.
     *
     * @param dataBase Mongo database
     * @param tabName Mongo table
     * @param table Datatable used for insert elements
     */
    @Given("^I insert into a MongoDB database '(.+?)' and table '(.+?)' this values:$")
    public void insertOnMongoTable(String dataBase, String tabName, DataTable table) {
        commonspec.getMongoDBClient().connectToMongoDBDataBase(dataBase);
        commonspec.getMongoDBClient().insertIntoMongoDBCollection(tabName, table);
    }

    /**
     * Truncate table in MongoDB.
     *
     * @param database Mongo database
     * @param table Mongo table
     */
    @Given("^I drop every document at a MongoDB database '(.+?)' and table '(.+?)'")
    public void truncateTableInMongo(String database, String table) {
        commonspec.getMongoDBClient().connectToMongoDBDataBase(database);
        commonspec.getMongoDBClient().dropAllDataMongoDBCollection(table);
    }

    /**
     * Browse to {@code url} using the current browser.
     *
     * @param path path of running app
     * @throws Exception exception
     */
    @Given("^I( securely)? browse to '(.+?)'$")
    public void seleniumBrowse(String isSecured, String path) throws Exception {
        assertThat(path).isNotEmpty();

        if (commonspec.getWebHost() == null) {
            throw new Exception("Web host has not been set");
        }

        if (commonspec.getWebPort() == null) {
            throw new Exception("Web port has not been set");
        }
        String protocol = "http://";
        if (isSecured != null) {
            protocol = "https://";
        }

        String webURL = protocol + commonspec.getWebHost() + commonspec.getWebPort();

        commonspec.getDriver().get(webURL + path);
        commonspec.setParentWindow(commonspec.getDriver().getWindowHandle());
    }

    /**
     * Set app host and port {@code host, @code port}
     *
     * @param host host where app is running
     * @param port port where app is running
     */
    @Given("^My app is running in '([^:]+?)(:.+?)?'$")
    public void setupApp(String host, String port) {
        assertThat(host).isNotEmpty();

        if (port == null) {
            port = ":80";
        }

        commonspec.setWebHost(host);
        commonspec.setWebPort(port);
        commonspec.setRestHost(host);
        commonspec.setRestPort(port);
    }

    /**
     * Send requests to {@code restHost @code restPort}.
     *
     * @param restHost host where api is running
     * @param restPort port where api is running
     */
    @Given("^I( securely)? send requests to '([^:]+?)(:.+?)?'$")
    public void setupRestClient(String isSecured, String restHost, String restPort) {
        String restProtocol = "http://";

        if (isSecured != null) {
            restProtocol = "https://";
        }


        if (restHost == null) {
            restHost = "localhost";
        }

        if (restPort == null) {
            if (isSecured == null)  {
                restPort = ":80";
            } else {
                restPort = ":443";
            }
        }

        commonspec.setRestProtocol(restProtocol);
        commonspec.setRestHost(restHost);
        commonspec.setRestPort(restPort);
    }

    /**
     * Maximizes current browser window. Mind the current resolution could break a test.
     */
    @Given("^I maximize the browser$")
    public void seleniumMaximize() {
        commonspec.getDriver().manage().window().maximize();
    }

    /**
     * Switches to a frame/ iframe.
     */
    @Given("^I switch to the iframe on index '(\\d+?)'$")
    public void seleniumSwitchFrame(Integer index) {

        assertThat(commonspec.getPreviousWebElements()).as("There are less found elements than required")
                .hasAtLeast(index);

        WebElement elem = commonspec.getPreviousWebElements().getPreviousWebElements().get(index);
        commonspec.getDriver().switchTo().frame(elem);
    }

    /**
     * Swith to the iFrame where id matches idframe
     *
     * @param idframe iframe to swith to
     * @throws IllegalAccessException exception
     * @throws NoSuchFieldException exception
     * @throws ClassNotFoundException exception
     */
    @Given("^I switch to iframe with '([^:]*?):(.+?)'$")
    public void seleniumIdFrame(String method, String idframe) throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        assertThat(commonspec.locateElement(method, idframe, 1));

        if (method.equals("id") || method.equals("name")) {
            commonspec.getDriver().switchTo().frame(idframe);
        } else {
            throw new ClassNotFoundException("Can not use this method to switch iframe");
        }
    }

    /**
     * Switches to a parent frame/ iframe.
     */
    @Given("^I switch to a parent frame$")
    public void seleniumSwitchAParentFrame() {
        commonspec.getDriver().switchTo().parentFrame();
    }

    /**
     * Switches to the frames main container.
     */
    @Given("^I switch to the main frame container$")
    public void seleniumSwitchParentFrame() {
        commonspec.getDriver().switchTo().frame(commonspec.getParentWindow());
    }


    /**
     * Opens a ssh connection to remote host
     *
     * @param remoteHost remote host
     * @param user remote user
     * @param password (required if pemFile null)
     * @param pemFile (required if password null)
     * @throws Exception exception
     *
     */
    @Given("^I open a ssh connection to '(.+?)'( in port '(.+?)')? with user '(.+?)'( and password '(.+?)')?( using pem file '(.+?)')?$")
    public void openSSHConnection(String remoteHost, String tmp, String remotePort, String user, String foo, String password, String bar, String pemFile) throws Exception {
        if ((pemFile == null) || (pemFile.equals("none"))) {
            if (password == null) {
                throw new Exception("You have to provide a password or a pem file to be used for connection");
            }
            commonspec.setRemoteSSHConnection(new RemoteSSHConnection(user, password, remoteHost, remotePort, null));
            commonspec.getLogger().debug("Opening ssh connection with password: { " + password + "}", commonspec.getRemoteSSHConnection());
        } else {
            File pem = new File(pemFile);
            if (!pem.exists()) {
                throw new Exception("Pem file: " + pemFile + " does not exist");
            }
            commonspec.setRemoteSSHConnection(new RemoteSSHConnection(user, null, remoteHost, remotePort, pemFile));
            commonspec.getLogger().debug("Opening ssh connection with pemFile: {}", commonspec.getRemoteSSHConnection());
        }
    }


    /**
    * Authenticate in a DCOS cluster
    *
    * @param remoteHost remote host
    * @param email email for JWT singing
    * @param user remote user
    * @param password (required if pemFile null)
    * @param pemFile (required if password null)
    * @throws Exception exception
    *
    *
    */
    @Given("^I authenticate to DCOS cluster '(.+?)' using email '(.+?)'( with user '(.+?)'( and password '(.+?)'| and pem file '(.+?)'))?$")
    public void authenticateDCOSpem(String remoteHost, String email, String foo, String user, String bar, String password, String pemFile) throws Exception {
        String DCOSsecret;
        if (foo == null) {
            commonspec.setRemoteSSHConnection(new RemoteSSHConnection("root", "stratio", remoteHost, null));
        } else {
            commonspec.setRemoteSSHConnection(new RemoteSSHConnection(user, password, remoteHost, pemFile));
        }
        commonspec.getRemoteSSHConnection().runCommand("sudo cat /var/lib/dcos/dcos-oauth/auth-token-secret");
        DCOSsecret = commonspec.getRemoteSSHConnection().getResult().trim();
        setDCOSCookie(DCOSsecret, email);
    }

    public void setDCOSCookie(String DCOSsecret, String email) throws Exception {
        final JWTSigner signer = new JWTSigner(DCOSsecret);
        final HashMap<String, Object> claims = new HashMap();
        claims.put("uid", email);
        final String jwt = signer.sign(claims);
        Cookie cookie = new Cookie("dcos-acs-auth-cookie", jwt, false, "", "", 99999, false, false);
        List<Cookie> cookieList = new ArrayList<Cookie>();
        cookieList.add(cookie);
        commonspec.setCookies(cookieList);
    }

    /**
     * Generate token to authenticate in gosec SSO
     * @param ssoHost current sso host
     * @param userName username
     * @param passWord password
     * @throws Exception exception
     */
    @Given("^I( do not)? set sso token using host '(.+?)' with user '(.+?)' and password '(.+?)'$")
    public void setGoSecSSOCookie(String set, String ssoHost, String userName, String passWord) throws Exception {
        if (set == null) {
            HashMap<String, String> ssoCookies = new GosecSSOUtils(ssoHost, userName, passWord).ssoTokenGenerator();
            String[] tokenList = {"user", "dcos-acs-auth-cookie"};
            List<Cookie> cookiesAtributes = addSsoToken(ssoCookies, tokenList);

            commonspec.setCookies(cookiesAtributes);
        }
    }

    public List<Cookie> addSsoToken(HashMap<String, String> ssoCookies, String[] tokenList) {
        List<Cookie> cookiesAttributes = new ArrayList<>();

        for (String tokenKey : tokenList) {
            cookiesAttributes.add(new Cookie(tokenKey, ssoCookies.get(tokenKey),
                    false, null,
                    null, 999999, false, false));
        }
        return cookiesAttributes;
    }


    /*
     * Copies file/s from remote system into local system
     *
     * @param remotePath path where file is going to be copy
     * @param localPath path where file is located
     * @throws Exception exception
     *
     */
    @Given("^I inbound copy '(.+?)' through a ssh connection to '(.+?)'$")
    public void copyFromRemoteFile(String remotePath, String localPath) throws Exception {
        commonspec.getRemoteSSHConnection().copyFrom(remotePath, localPath);
    }


    /**
     * Copies file/s from local system to remote system
     *
     * @param remotePath path where file is going to be copy
     * @param localPath path where file is located
     * @throws Exception exception
     */
    @Given("^I outbound copy '(.+?)' through a ssh connection to '(.+?)'$")
    public void copyToRemoteFile(String localPath, String remotePath) throws Exception {
        commonspec.getRemoteSSHConnection().copyTo(localPath, remotePath);
    }


    /**
     * Executes the command specified in local system
     *
     * @param command command to be run locally
     * @param foo regex needed to match method
     * @param exitStatus command exit status
     * @param bar regex needed to match method
     * @param envVar environment variable name
     * @throws Exception exception
     **/
    @Given("^I run '(.+?)' locally( with exit status '(.+?)')?( and save the value in environment variable '(.+?)')?$")
    public void executeLocalCommand(String command, String foo, Integer exitStatus, String bar, String envVar) throws Exception {
        if (exitStatus == null) {
            exitStatus = 0;
        }

        commonspec.runLocalCommand(command);
        commonspec.runCommandLoggerAndEnvVar(exitStatus, envVar, Boolean.TRUE);

        Assertions.assertThat(commonspec.getCommandExitStatus()).isEqualTo(exitStatus);
    }

    /**
     * Executes the command specified in remote system
     *
     * @param command command to be run locally
     * @param foo regex needed to match method
     * @param exitStatus command exit status
     * @param bar regex needed to match method
     * @param envVar environment variable name
     * @throws Exception exception
     **/
    @Given("^I run '(.+?)' in the ssh connection( with exit status '(.+?)')?( and save the value in environment variable '(.+?)')?$")
    public void executeCommand(String command, String foo, Integer exitStatus, String bar, String envVar) throws Exception {
        if (exitStatus == null) {
            exitStatus = 0;
        }

        command = "set -o pipefail && alias grep='grep --color=never' && " + command;
        commonspec.getRemoteSSHConnection().runCommand(command);
        commonspec.setCommandResult(commonspec.getRemoteSSHConnection().getResult());
        commonspec.setCommandExitStatus(commonspec.getRemoteSSHConnection().getExitStatus());
        commonspec.runCommandLoggerAndEnvVar(exitStatus, envVar, Boolean.FALSE);

        Assertions.assertThat(commonspec.getRemoteSSHConnection().getExitStatus()).isEqualTo(exitStatus);
    }


    /**
     * Insert document in a MongoDB table.
     *
     * @param dataBase Mongo database
     * @param collection Mongo collection
     * @param document document used for schema
     */
    @Given("^I insert into MongoDB database '(.+?)' and collection '(.+?)' the document from schema '(.+?)'$")
    public void insertOnMongoTable(String dataBase, String collection, String document) throws Exception {
        String retrievedDoc = commonspec.retrieveData(document, "json");
        commonspec.getMongoDBClient().connectToMongoDBDataBase(dataBase);
        commonspec.getMongoDBClient().insertDocIntoMongoDBCollection(collection, retrievedDoc);
    }


    /**
     * Get all opened windows and store it.
     */
    @Given("^a new window is opened$")
    public void seleniumGetwindows() {
        Set<String> wel = commonspec.getDriver().getWindowHandles();

        Assertions.assertThat(wel).as("Element count doesnt match").hasSize(2);
    }


    /**
     * Connect to zookeeper.
     *
     * @param zookeeperHosts as host:port (comma separated)
     * @throws InterruptedException exception
     */
    @Given("^I connect to Zookeeper at '(.+)'$")
    public void connectToZk(String zookeeperHosts) throws InterruptedException {
        commonspec.getZookeeperSecClient().setZookeeperSecConnection(zookeeperHosts, 3000);
        commonspec.getZookeeperSecClient().connectZk();
    }


    /**
     * Disconnect from zookeeper.
     *
     */
    @Given("^I disconnect from Zookeeper$")
    public void disconnectFromZk() throws InterruptedException {
        commonspec.getZookeeperSecClient().disconnect();
    }


    /**
     * Connect to Kafka.
     *
     * @param zkHost ZK host
     * @param zkPath ZK port
     * @throws UnknownHostException exception
     */
    @Given("^I connect to kafka at '(.+)' using path '(.+)'$")
    public void connectKafka(String zkHost, String zkPath) throws UnknownHostException {
        String zkPort = zkHost.split(":")[1];
        zkHost = zkHost.split(":")[0];
        commonspec.getKafkaUtils().setZkHost(zkHost, zkPort, zkPath);
        commonspec.getKafkaUtils().connect();
    }

    /**
     * Connect to LDAP.
     *
     *
     */
    @Given("^I connect to LDAP$")
    public void connectLDAP() {
        commonspec.getLdapUtils().connect();
    }

    /**
     * Send a request of the type specified but in this case, the response is checked until it contains the expected value
     *
     * @param requestType   type of request to be sent. Possible values:
     *                      GET|DELETE|POST|PUT|CONNECT|PATCH|HEAD|OPTIONS|REQUEST|TRACE
     * @param timeout
     * @param wait
     * @param responseVal
     * @param endPoint      end point to be used
     * @param baseData      path to file containing the schema to be used
     * @param type          element to read from file (element should contain a json)
     * @param modifications DataTable containing the modifications to be done to the
     *                      base schema element. Syntax will be:
     *                      {@code
     *                      | <key path> | <type of modification> | <new value> |
     *                      }
     *                      where:
     *                      key path: path to the key to be modified
     *                      type of modification: DELETE|ADD|UPDATE
     *                      new value: in case of UPDATE or ADD, new value to be used
     *                      for example:
     *                      if the element read is {"key1": "value1", "key2": {"key3": "value3"}}
     *                      and we want to modify the value in "key3" with "new value3"
     *                      the modification will be:
     *                      | key2.key3 | UPDATE | "new value3" |
     *                      being the result of the modification: {"key1": "value1", "key2": {"key3": "new value3"}}
     * @throws Exception
     */
    @Given("^in less than '(\\d+?)' seconds, checking each '(\\d+?)' seconds, I send a '(.+?)' request to '(.+?)' so that the response( does not)? contains '(.+?)' based on '([^:]+?)'( as '(json|string)')? with:$")
    public void sendRequestDataTableTimeout(Integer timeout, Integer wait, String requestType, String endPoint, String contains, String responseVal, String baseData, String baz, String type, DataTable modifications) throws Exception {

        // Retrieve data
        String retrievedData = commonspec.retrieveData(baseData, type);

        // Modify data
        commonspec.getLogger().debug("Modifying data {} as {}", retrievedData, type);
        String modifiedData = commonspec.modifyData(retrievedData, type, modifications).toString();

        Boolean searchUntilContains;
        if (contains == null || contains.isEmpty()) {
            searchUntilContains = Boolean.TRUE;
        } else {
            searchUntilContains = Boolean.FALSE;
        }
        Boolean found = !searchUntilContains;
        AssertionError ex = null;

        Future<Response> response;

        Pattern pattern = CommonG.matchesOrContains(responseVal);

        for (int i = 0; (i <= timeout); i += wait) {
            if (found && searchUntilContains) {
                break;
            }
            commonspec.getLogger().debug("Generating request {} to {} with data {} as {}", requestType, endPoint, modifiedData, type);
            response = commonspec.generateRequest(requestType, false, null, null, endPoint, modifiedData, type);
            commonspec.getLogger().debug("Saving response");
            commonspec.setResponse(requestType, response.get());
            commonspec.getLogger().debug("Checking response value");
            try {
                if (searchUntilContains) {
                    assertThat(commonspec.getResponse().getResponse()).containsPattern(pattern);
                    found = true;
                    timeout = i;
                } else {
                    assertThat(commonspec.getResponse().getResponse()).doesNotContain(responseVal);
                    found = false;
                    timeout = i;
                }
            } catch (AssertionError e) {
                if (!found) {
                    commonspec.getLogger().info("Response value not found after " + i + " seconds");
                } else {
                    commonspec.getLogger().info("Response value found after " + i + " seconds");
                }
                Thread.sleep(wait * 1000);
                ex = e;
            }
            if (!found && !searchUntilContains) {
                break;
            }
        }
        if ((!found && searchUntilContains) || (found && !searchUntilContains)) {
            throw (ex);
        }
        if (searchUntilContains) {
            commonspec.getLogger().info("Success! Response value found after " + timeout + " seconds");
        } else {
            commonspec.getLogger().info("Success! Response value not found after " + timeout + " seconds");
        }
    }

    /**
     * Check if all task of a service are correctly distributed in all datacenters of the cluster
     *
     * @param serviceList all task deployed in the cluster separated by a semicolumn.
     * @throws Exception
     *
     */
    @Given("^services '(.*?)' are splitted correctly in datacenters$")
    public void checkServicesDistributionMultiDataCenter(String serviceList) throws Exception {
        executeCommand("dcos node --json >> aux.txt", "foo", 0, "bar", null);
        executeCommand("cat aux.txt", "foo", 0, "bar", null);
        checkDataCentersDistribution(serviceList.split(","), obtainsDataCenters(commonspec.getRemoteSSHConnection().getResult()).split(";"));
        executeCommand("rm -rf aux.txt", "foo", 0, "bar", null);

    }
    /**
     * Check if all task of a service are correctly distributed in all datacenters of the cluster
     *
     * @param serviceList all task deployed in the cluster separated by a semicolumn.
     * @param dataCentersIps all ips of the datacenters to be checked
     *                       Example: ip_1_dc1, ip_2_dc1;ip_3_dc2,ip_4_dc2
     * @throws Exception
     *
     */
    @Given("^services '(.+?)' are splitted correctly in datacenters '(.+?)'$")
    public void checkServicesDistributionMultiDataCenterPram(String serviceList, String dataCentersIps) throws Exception {
        checkDataCentersDistribution(serviceList.split(","), dataCentersIps.split(";"));
    }

    public void checkDataCentersDistribution(String[] serviceListArray, String[] dataCentersIpsArray) throws Exception {
        int[] expectedDistribution = new int[dataCentersIpsArray.length];
        int[] results = new int[dataCentersIpsArray.length];
        //Calculamos distribucion
        int div = serviceListArray.length / dataCentersIpsArray.length;
        int resto = serviceListArray.length % dataCentersIpsArray.length;
        for (int i = 0; i < expectedDistribution.length; i++) {
            expectedDistribution[i] = div;
        }
        for (int i = 0; i < resto; i++) {
            expectedDistribution[i] = expectedDistribution[i] + 1;
        }
        ///Fin calculo distribucion
        for (int i = 0; i < serviceListArray.length; i++) {
            executeCommand("dcos task | grep " + serviceListArray[i] + " | awk '{print $2}'", "foo", 0, "bar", null);
            String service_ip = commonspec.getRemoteSSHConnection().getResult();
            for (int x = 0; x < dataCentersIpsArray.length; x++) {
                if (dataCentersIpsArray[x].toLowerCase().contains(service_ip.toLowerCase())) {
                    results[x] = results[x] + 1;
                }
            }
        }
        Arrays.sort(expectedDistribution);
        Arrays.sort(results);
        assertThat(expectedDistribution.length).isEqualTo(results.length);
        for (int i = 0; i < results.length; i++) {
            assertThat(expectedDistribution[i]).isEqualTo(results[i]);
        }
    }

    public String obtainsDataCenters(String jsonString) {
        Map<String, String> datacentersDistribution = new HashMap<String, String>();
        JSONArray jsonArray = new JSONArray(jsonString);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            String ip = object.getString("hostname");
            String datacenter = ((JSONObject) object.get("attributes")).getString("dc");
            String existValue = datacentersDistribution.get(datacenter);
            if (existValue == null) {
                datacentersDistribution.put(datacenter, ip);
            } else {
                datacentersDistribution.put(datacenter, datacentersDistribution.get(datacenter) + "," + ip);
            }
        }
        String result = "";
        for (String ips : datacentersDistribution.keySet()) {
            String key = ips.toString();
            String value = datacentersDistribution.get(key).toString();
            result = result + ";" + value;
        }
        return result.substring(1, result.length());
    }

}
