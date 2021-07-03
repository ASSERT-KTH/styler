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

import com.csvreader.CsvReader;
import com.datastax.driver.core.ResultSet;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.ning.http.client.Response;
import com.stratio.qa.cucumber.converter.ArrayListConverter;
import com.stratio.qa.cucumber.converter.NullableStringConverter;
import com.stratio.qa.utils.ThreadProperty;
import cucumber.api.DataTable;
import cucumber.api.Transform;
import cucumber.api.java.en.When;
import org.assertj.core.api.Assertions;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.hjson.JsonArray;
import org.hjson.JsonValue;
import org.ldaptive.SearchRequest;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import static com.stratio.qa.assertions.Assertions.assertThat;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


/**
 * Generic When Specs.
 *
 * @see <a href="WhenGSpec-annotations.html">When Steps &amp; Matching Regex</a>
 */
public class WhenGSpec extends BaseGSpec {

    public static final int DEFAULT_TIMEOUT = 1000;

    /**
     * Default constructor.
     *
     * @param spec
     */
    public WhenGSpec(CommonG spec) {
        this.commonspec = spec;
    }

    /**
     * Wait seconds.
     *
     * @param seconds
     * @throws InterruptedException
     */
    @When("^I wait '(\\d+?)' seconds?$")
    public void idleWait(Integer seconds) throws InterruptedException {
        Thread.sleep(seconds * DEFAULT_TIMEOUT);
    }

    /**
     * Searchs for two webelements dragging the first one to the second
     *
     * @param source
     * @param destination
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws ClassNotFoundException
     */
    @When("^I drag '([^:]*?):(.+?)' and drop it to '([^:]*?):(.+?)'$")
    public void seleniumDrag(String smethod, String source, String dmethod, String destination) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Actions builder = new Actions(commonspec.getDriver());

        List<WebElement> sourceElement = commonspec.locateElement(smethod, source, 1);
        List<WebElement> destinationElement = commonspec.locateElement(dmethod, destination, 1);

        builder.dragAndDrop(sourceElement.get(0), destinationElement.get(0)).perform();
    }

    /**
     * Click on an numbered {@code url} previously found element.
     *
     * @param index
     * @throws InterruptedException
     */
    @When("^I click on the element on index '(\\d+?)'$")
    public void seleniumClick(Integer index) throws InterruptedException {

        try {
            assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                    .hasAtLeast(index);
            commonspec.getPreviousWebElements().getPreviousWebElements().get(index).click();
        } catch (AssertionError e) {
            Thread.sleep(1000);
            assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                    .hasAtLeast(index);
            commonspec.getPreviousWebElements().getPreviousWebElements().get(index).click();
        }
    }

    /**
     * Double Click on an numbered {@code url} previously found element.
     *
     * @param index
     * @throws InterruptedException
     */
    @When("^I double click on the element on index '(\\d+?)'$")
    public void seleniumDoubleClick(Integer index) throws InterruptedException {
        Actions action = new Actions(commonspec.getDriver());
        try {
            assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                    .hasAtLeast(index);
            action.doubleClick(commonspec.getPreviousWebElements().getPreviousWebElements().get(index)).perform();

        } catch (AssertionError e) {
            Thread.sleep(1000);
            assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                    .hasAtLeast(index);
            action.doubleClick(commonspec.getPreviousWebElements().getPreviousWebElements().get(index)).perform();
        }
    }

    /**
     * Clear the text on a numbered {@code index} previously found element.
     *
     * @param index
     */
    @When("^I clear the content on text input at index '(\\d+?)'$")
    public void seleniumClear(Integer index) {
        assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                .hasAtLeast(index);

        assertThat(this.commonspec, commonspec.getPreviousWebElements().getPreviousWebElements().get(index)).isTextField(commonspec.getTextFieldCondition());

        commonspec.getPreviousWebElements().getPreviousWebElements().get(index).clear();
    }


    /**
     * Delete or replace the text on a numbered {@code index} previously found element.
     *
     * @param index
     */
    @When("^I delete the text '(.+?)' on the element on index '(\\d+?)'( and replace it for '(.+?)')?$")
    public void seleniumDelete(String text, Integer index, String foo, String replacement) {
        assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                .hasAtLeast(index);

        Actions actions = new Actions(commonspec.getDriver());
        actions.moveToElement(commonspec.getPreviousWebElements().getPreviousWebElements().get(index), (text.length() / 2), 0);
        for (int i = 0; i < (text.length() / 2); i++) {
            actions.sendKeys(Keys.ARROW_LEFT);
            actions.build().perform();
        }
        for (int i = 0; i < text.length(); i++) {
            actions.sendKeys(Keys.DELETE);
            actions.build().perform();
        }
        if (replacement != null && replacement.length() != 0) {
            actions.sendKeys(replacement);
            actions.build().perform();
        }
    }


    /**
     * Type a {@code text} on an numbered {@code index} previously found element.
     *
     * @param text
     * @param index
     */
    @When("^I type '(.+?)' on the element on index '(\\d+?)'$")
    public void seleniumType(@Transform(NullableStringConverter.class) String text, Integer index) {
        assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                .hasAtLeast(index);
        while (text.length() > 0) {
            Actions actions = new Actions(commonspec.getDriver());
            if (-1 == text.indexOf("\\n")) {
                actions.moveToElement(commonspec.getPreviousWebElements().getPreviousWebElements().get(index));
                actions.click();
                actions.sendKeys(text);
                actions.build().perform();
                text = "";
            } else {
                actions.moveToElement(commonspec.getPreviousWebElements().getPreviousWebElements().get(index));
                actions.click();
                actions.sendKeys(text.substring(0, text.indexOf("\\n")));
                actions.build().perform();
                text = text.substring(text.indexOf("\\n") + 2);
            }
        }
    }

    /**
     * Send a {@code strokes} list on an numbered {@code url} previously found element or to the driver. strokes examples are "HOME, END"
     * or "END, SHIFT + HOME, DELETE". Each element in the stroke list has to be an element from
     * {@link org.openqa.selenium.Keys} (NULL, CANCEL, HELP, BACK_SPACE, TAB, CLEAR, RETURN, ENTER, SHIFT, LEFT_SHIFT,
     * CONTROL, LEFT_CONTROL, ALT, LEFT_ALT, PAUSE, ESCAPE, SPACE, PAGE_UP, PAGE_DOWN, END, HOME, LEFT, ARROW_LEFT, UP,
     * ARROW_UP, RIGHT, ARROW_RIGHT, DOWN, ARROW_DOWN, INSERT, DELETE, SEMICOLON, EQUALS, NUMPAD0, NUMPAD1, NUMPAD2,
     * NUMPAD3, NUMPAD4, NUMPAD5, NUMPAD6, NUMPAD7, NUMPAD8, NUMPAD9, MULTIPLY, ADD, SEPARATOR, SUBTRACT, DECIMAL,
     * DIVIDE, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, META, COMMAND, ZENKAKU_HANKAKU) , a plus sign (+), a
     * comma (,) or spaces ( )
     *
     * @param strokes
     * @param foo
     * @param index
     */
    @When("^I send '(.+?)'( on the element on index '(\\d+?)')?$")
    public void seleniumKeys(@Transform(ArrayListConverter.class) List<String> strokes, String foo, Integer index) {
        if (index != null) {
            assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                    .hasAtLeast(index);
        }
        assertThat(strokes).isNotEmpty();

        for (String stroke : strokes) {
            if (stroke.contains("+")) {
                List<Keys> csl = new ArrayList<Keys>();
                for (String strokeInChord : stroke.split("\\+")) {
                    csl.add(Keys.valueOf(strokeInChord.trim()));
                }
                Keys[] csa = csl.toArray(new Keys[csl.size()]);
                if (index == null) {
                    new Actions(commonspec.getDriver()).sendKeys(commonspec.getDriver().findElement(By.tagName("body")), csa).perform();
                } else {
                    commonspec.getPreviousWebElements().getPreviousWebElements().get(index).sendKeys(csa);
                }
            } else {
                if (index == null) {
                    new Actions(commonspec.getDriver()).sendKeys(commonspec.getDriver().findElement(By.tagName("body")), Keys.valueOf(stroke)).perform();
                } else {
                    commonspec.getPreviousWebElements().getPreviousWebElements().get(index).sendKeys(Keys.valueOf(stroke));
                }
            }
        }
    }

    /**
     * Choose an @{code option} from a select webelement found previously
     *
     * @param option
     * @param index
     */
    @When("^I select '(.+?)' on the element on index '(\\d+?)'$")
    public void elementSelect(String option, Integer index) {
        Select sel = null;
        sel = new Select(commonspec.getPreviousWebElements().getPreviousWebElements().get(index));

        sel.selectByVisibleText(option);
    }

    /**
     * Choose no option from a select webelement found previously
     *
     * @param index
     */
    @When("^I de-select every item on the element on index '(\\d+?)'$")
    public void elementDeSelect(Integer index) {
        Select sel = null;
        sel = new Select(commonspec.getPreviousWebElements().getPreviousWebElements().get(index));

        if (sel.isMultiple()) {
            sel.deselectAll();
        }
    }

    /**
     * Send a request of the type specified
     *
     * @param requestType   type of request to be sent. Possible values:
     *                      GET|DELETE|POST|PUT|CONNECT|PATCH|HEAD|OPTIONS|REQUEST|TRACE
     * @param endPoint      end point to be used
     * @param foo           parameter generated by cucumber because of the optional expression
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
    @When("^I send a '(.+?)' request to '(.+?)'( with user and password '(.+:.+?)')? based on '([^:]+?)'( as '(json|string)')? with:$")
    public void sendRequest(String requestType, String endPoint, String foo, String loginInfo, String baseData, String baz, String type, DataTable modifications) throws Exception {
        // Retrieve data
        String retrievedData = commonspec.retrieveData(baseData, type);

        // Modify data
        commonspec.getLogger().debug("Modifying data {} as {}", retrievedData, type);
        String modifiedData = commonspec.modifyData(retrievedData, type, modifications).toString();

        String user = null;
        String password = null;
        if (loginInfo != null) {
            user = loginInfo.substring(0, loginInfo.indexOf(':'));
            password = loginInfo.substring(loginInfo.indexOf(':') + 1, loginInfo.length());
        }


        commonspec.getLogger().debug("Generating request {} to {} with data {} as {}", requestType, endPoint, modifiedData, type);
        Future<Response> response = commonspec.generateRequest(requestType, false, user, password, endPoint, modifiedData, type, "");

        // Save response
        commonspec.getLogger().debug("Saving response");
        commonspec.setResponse(requestType, response.get());
    }

    /**
     * Same sendRequest, but in this case, we do not receive a data table with modifications.
     * Besides, the data and request header are optional as well.
     * In case we want to simulate sending a json request with empty data, we just to avoid baseData
     *
     * @param requestType
     * @param endPoint
     * @param foo
     * @param baseData
     * @param bar
     * @param type
     * @throws Exception
     */
    @When("^I send a '(.+?)' request to '(.+?)'( with user and password '(.+:.+?)')?( based on '([^:]+?)')?( as '(json|string)')?$")
    public void sendRequestNoDataTable(String requestType, String endPoint, String foo, String loginInfo, String bar, String baseData, String baz, String type) throws Exception {
        Future<Response> response;
        String user = null;
        String password = null;

        if (loginInfo != null) {
            user = loginInfo.substring(0, loginInfo.indexOf(':'));
            password = loginInfo.substring(loginInfo.indexOf(':') + 1, loginInfo.length());
        }

        if (baseData != null) {
            // Retrieve data
            String retrievedData = commonspec.retrieveData(baseData, type);
            // Generate request
            response = commonspec.generateRequest(requestType, false, user, password, endPoint, retrievedData, type, "");
        } else {
            // Generate request
            response = commonspec.generateRequest(requestType, false, user, password, endPoint, "", type, "");
        }

        // Save response
        commonspec.setResponse(requestType, response.get());
    }


    /**
     * Same sendRequest, but in this case, the rersponse is checked until it contains the expected value
     *
     * @param timeout
     * @param wait
     * @param requestType
     * @param endPoint
     * @param responseVal
     * @throws Exception
     */
    @When("^in less than '(\\d+?)' seconds, checking each '(\\d+?)' seconds, I send a '(.+?)' request to '(.+?)'( so that the response( does not)? contains '(.+?)')?$")
    public void sendRequestTimeout(Integer timeout, Integer wait, String requestType, String endPoint, String foo, String contains, String responseVal) throws Exception {

        AssertionError ex = null;
        String type = "";
        Future<Response> response;

        if (foo != null) {
            Boolean searchUntilContains;
            if (contains == null || contains.isEmpty()) {
                searchUntilContains = Boolean.TRUE;
            } else {
                searchUntilContains = Boolean.FALSE;
            }
            Boolean found = !searchUntilContains;

            Pattern pattern = CommonG.matchesOrContains(responseVal);
            for (int i = 0; (i <= timeout); i += wait) {
                if (found && searchUntilContains) {
                    break;
                }
                response = commonspec.generateRequest(requestType, false, null, null, endPoint, "", type, "");
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
        } else {

            for (int i = 0; (i <= timeout); i += wait) {
                response = commonspec.generateRequest(requestType, false, null, null, endPoint, "", type, "");
                commonspec.setResponse(requestType, response.get());
                commonspec.getLogger().debug("Checking response value");
                try {
                    assertThat(commonspec.getResponse().getResponse());
                    timeout = i;
                } catch (AssertionError e) {
                    Thread.sleep(wait * 1000);
                    ex = e;
                }
            }
        }
    }

    @When("^I login to '(.+?)' based on '([^:]+?)' as '(json|string)'$")
    public void loginUser(String endPoint, String baseData, String type) throws Exception {
        sendRequestNoDataTable("POST", endPoint, null, null, null, baseData, null, type);
    }

    @When("^I login to '(.+?)' based on '([^:]+?)' as '(json|string)' with:$")
    public void loginUser(String endPoint, String baseData, String type, DataTable modifications) throws Exception {
        sendRequest("POST", endPoint, null, null, baseData, "", type, modifications);
    }

    @When("^I logout from '(.+?)'$")
    public void logoutUser(String endPoint) throws Exception {
        sendRequestNoDataTable("GET", endPoint, null, null, null, "", null, "");
    }

    /**
     * Execute a query with schema over a cluster
     *
     * @param fields        columns on which the query is executed. Example: "latitude,longitude" or "*" or "count(*)"
     * @param schema        the file of configuration (.conf) with the options of mappin. If schema is the word "empty", method will not add a where clause.
     * @param type          type of the changes in schema (string or json)
     * @param table         table for create the index
     * @param magic_column  magic column where index will be saved. If you don't need index, you can add the word "empty"
     * @param keyspace      keyspace used
     * @param modifications all data in "where" clause. Where schema is "empty", query has not a where clause. So it is necessary to provide an empty table. Example:  ||.
     */
    @When("^I execute a query over fields '(.+?)' with schema '(.+?)' of type '(json|string)' with magic_column '(.+?)' from table: '(.+?)' using keyspace: '(.+?)' with:$")
    public void sendQueryOfType(String fields, String schema, String type, String magic_column, String table, String keyspace, DataTable modifications) {
        try {
            commonspec.setResultsType("cassandra");
            commonspec.getCassandraClient().useKeyspace(keyspace);
            commonspec.getLogger().debug("Starting a query of type " + commonspec.getResultsType());

            String query = "";

            if (schema.equals("empty") && magic_column.equals("empty")) {

                query = "SELECT " + fields + " FROM " + table + ";";

            } else if (!schema.equals("empty") && magic_column.equals("empty")) {
                String retrievedData = commonspec.retrieveData(schema, type);
                String modifiedData = commonspec.modifyData(retrievedData, type, modifications).toString();
                query = "SELECT " + fields + " FROM " + table + " WHERE " + modifiedData + ";";


            } else {
                String retrievedData = commonspec.retrieveData(schema, type);
                String modifiedData = commonspec.modifyData(retrievedData, type, modifications).toString();
                query = "SELECT " + fields + " FROM " + table + " WHERE " + magic_column + " = '" + modifiedData + "';";

            }
            commonspec.getLogger().debug("query: {}", query);
            ResultSet results = commonspec.getCassandraClient().executeQuery(query);
            commonspec.setCassandraResults(results);
        } catch (Exception e) {
            commonspec.getLogger().debug("Exception captured");
            commonspec.getLogger().debug(e.toString());
            commonspec.getExceptions().add(e);
        }


    }

    /**
     * Execute a query on (mongo) database
     *
     * @param query         path to query
     * @param type          type of data in query (string or json)
     * @param collection    collection in database
     * @param modifications modifications to perform in query
     */
    @When("^I execute a query '(.+?)' of type '(json|string)' in mongo '(.+?)' database using collection '(.+?)' with:$")
    public void sendQueryOfType(String query, String type, String database, String collection, DataTable modifications) throws Exception {
        try {
            commonspec.setResultsType("mongo");
            String retrievedData = commonspec.retrieveData(query, type);
            String modifiedData = commonspec.modifyData(retrievedData, type, modifications);
            commonspec.getMongoDBClient().connectToMongoDBDataBase(database);
            DBCollection dbCollection = commonspec.getMongoDBClient().getMongoDBCollection(collection);
            DBObject dbObject = (DBObject) JSON.parse(modifiedData);
            DBCursor cursor = dbCollection.find(dbObject);
            commonspec.setMongoResults(cursor);
        } catch (Exception e) {
            commonspec.getExceptions().add(e);
        }
    }

    /**
     * Execute query with filter over elasticsearch
     *
     * @param indexName
     * @param mappingName
     * @param columnName
     * @param filterType  it could be equals, gt, gte, lt and lte.
     * @param value       value of the column to be filtered.
     */
    @When("^I execute an elasticsearch query over index '(.*?)' and mapping '(.*?)' and column '(.*?)' with value '(.*?)' to '(.*?)'$")
    public void elasticSearchQueryWithFilter(String indexName, String mappingName, String
            columnName, String filterType, String value) {
        try {
            commonspec.setResultsType("elasticsearch");
            commonspec.setElasticsearchResults(
                    commonspec.getElasticSearchClient()
                            .searchSimpleFilterElasticsearchQuery(indexName, mappingName, columnName,
                                    value, filterType)
            );
        } catch (Exception e) {
            commonspec.getLogger().debug("Exception captured");
            commonspec.getLogger().debug(e.toString());
            commonspec.getExceptions().add(e);
        }
    }


    /**
     * Create a Cassandra index.
     *
     * @param index_name    index name
     * @param schema        the file of configuration (.conf) with the options of mappin
     * @param type          type of the changes in schema (string or json)
     * @param table         table for create the index
     * @param magic_column  magic column where index will be saved
     * @param keyspace      keyspace used
     * @param modifications data introduced for query fields defined on schema
     */
    @When("^I create a Cassandra index named '(.+?)' with schema '(.+?)' of type '(json|string)' in table '(.+?)' using magic_column '(.+?)' using keyspace '(.+?)' with:$")
    public void createCustomMapping(String index_name, String schema, String type, String table, String magic_column, String keyspace, DataTable modifications) throws Exception {
        String retrievedData = commonspec.retrieveData(schema, type);
        String modifiedData = commonspec.modifyData(retrievedData, type, modifications).toString();
        String query = "CREATE CUSTOM INDEX " + index_name + " ON " + keyspace + "." + table + "(" + magic_column + ") "
                + "USING 'com.stratio.cassandra.lucene.Index' WITH OPTIONS = " + modifiedData;
        commonspec.getLogger().debug("Will execute a cassandra query: {}", query);
        commonspec.getCassandraClient().executeQuery(query);
    }

    /**
     * Drop table
     *
     * @param table
     * @param keyspace
     */
    @When("^I drop a Cassandra table named '(.+?)' using keyspace '(.+?)'$")
    public void dropTableWithData(String table, String keyspace) {
        try {
            commonspec.getCassandraClient().useKeyspace(keyspace);
            commonspec.getCassandraClient().dropTable(table);
        } catch (Exception e) {
            commonspec.getLogger().debug("Exception captured");
            commonspec.getLogger().debug(e.toString());
            commonspec.getExceptions().add(e);
        }
    }

    /**
     * Truncate table
     *
     * @param table
     * @param keyspace
     */
    @When("^I truncate a Cassandra table named '(.+?)' using keyspace '(.+?)'$")
    public void truncateTable(String table, String keyspace) {
        try {
            commonspec.getCassandraClient().useKeyspace(keyspace);
            commonspec.getCassandraClient().truncateTable(table);
        } catch (Exception e) {
            commonspec.getLogger().debug("Exception captured");
            commonspec.getLogger().debug(e.toString());
            commonspec.getExceptions().add(e);
        }
    }

    /**
     * Read csv file and store result in list of maps
     *
     * @param csvFile
     */
    @When("^I read info from csv file '(.+?)'$")
    public void readFromCSV(String csvFile) throws Exception {
        CsvReader rows = new CsvReader(csvFile);

        String[] columns = null;
        if (rows.readRecord()) {
            columns = rows.getValues();
            rows.setHeaders(columns);
        }

        List<Map<String, String>> results = new ArrayList<Map<String, String>>();
        while (rows.readRecord()) {
            Map<String, String> row = new HashMap<String, String>();
            for (String column : columns) {
                row.put(column, rows.get(rows.getIndex(column)));
            }
            results.add(row);
        }

        rows.close();

        commonspec.setResultsType("csv");
        commonspec.setCSVResults(results);
    }


    /**
     * Change current window to another opened window.
     */
    @When("^I change active window$")
    public void seleniumChangeWindow() {
        String originalWindowHandle = commonspec.getDriver().getWindowHandle();
        Set<String> windowHandles = commonspec.getDriver().getWindowHandles();

        for (String window : windowHandles) {
            if (!window.equals(originalWindowHandle)) {
                commonspec.getDriver().switchTo().window(window);
            }
        }

    }

    /**
     * Sort elements in envVar by a criteria and order.
     *
     * @param envVar   Environment variable to be sorted
     * @param criteria alphabetical,...
     * @param order    ascending or descending
     */
    @When("^I sort elements in '(.+?)' by '(.+?)' criteria in '(.+?)' order$")
    public void sortElements(String envVar, String criteria, String order) {

        String value = ThreadProperty.get(envVar);
        JsonArray jsonArr = JsonValue.readHjson(value).asArray();

        List<JsonValue> jsonValues = new ArrayList<JsonValue>();
        for (int i = 0; i < jsonArr.size(); i++) {
            jsonValues.add(jsonArr.get(i));
        }

        Comparator<JsonValue> comparator;
        switch (criteria) {
            case "alphabetical":
                commonspec.getLogger().debug("Alphabetical criteria selected.");
                comparator = new Comparator<JsonValue>() {
                    public int compare(JsonValue json1, JsonValue json2) {
                        int res = String.CASE_INSENSITIVE_ORDER.compare(json1.toString(), json2.toString());
                        if (res == 0) {
                            res = json1.toString().compareTo(json2.toString());
                        }
                        return res;
                    }
                };
                break;
            default:
                commonspec.getLogger().debug("No criteria selected.");
                comparator = null;
        }

        if ("ascending".equals(order)) {
            Collections.sort(jsonValues, comparator);
        } else {
            Collections.sort(jsonValues, comparator.reversed());
        }

        ThreadProperty.set(envVar, jsonValues.toString());
    }

    /**
     * Create a Kafka topic.
     *
     * @param topic_name topic name
     */
    @When("^I create a Kafka topic named '(.+?)'")
    public void createKafkaTopic(String topic_name) throws Exception {
        commonspec.getKafkaUtils().createTopic(topic_name);
    }

    /**
     * Delete a Kafka topic.
     *
     * @param topic_name topic name
     */
    @When("^I delete a Kafka topic named '(.+?)'")
    public void deleteKafkaTopic(String topic_name) throws Exception {
        commonspec.getKafkaUtils().deleteTopic(topic_name);
    }

    /**
     * Copy Kafka Topic content to file
     *
     * @param topic_name
     * @param filename
     * @param header
     * @throws Exception
     */
    @When("^I copy the kafka topic '(.*?)' to file '(.*?)' with headers '(.*?)'$")
    public void topicToFile(String topic_name, String filename, String header) throws Exception {
        commonspec.getKafkaUtils().resultsToFile(topic_name, filename, header);
    }
    /**
     * Delete zPath, it should be empty
     *
     * @param zNode path at zookeeper
     */
    @When("^I remove the zNode '(.+?)'$")
    public void removeZNode(String zNode) throws Exception {
        commonspec.getZookeeperSecClient().delete(zNode);
    }


    /**
     * Create zPath and domcument
     *
     * @param path      path at zookeeper
     * @param foo       a dummy match group
     * @param content   if it has content it should be defined
     * @param ephemeral if it's created as ephemeral or not
     */
    @When("^I create the zNode '(.+?)'( with content '(.+?)')? which (IS|IS NOT) ephemeral$")
    public void createZNode(String path, String foo, String content, boolean ephemeral) throws Exception {
        if (content != null) {
            commonspec.getZookeeperSecClient().zCreate(path, content, ephemeral);
        } else {
            commonspec.getZookeeperSecClient().zCreate(path, ephemeral);
        }
    }

    /**
     * Modify partitions in a Kafka topic.
     *
     * @param topic_name    topic name
     * @param numPartitions number of partitions
     */
    @When("^I increase '(.+?)' partitions in a Kafka topic named '(.+?)'")
    public void modifyPartitions(int numPartitions, String topic_name) throws Exception {
        commonspec.getKafkaUtils().modifyTopicPartitioning(topic_name, numPartitions);
    }


    /**
     * Sending a message in a Kafka topic.
     *
     * @param topic_name topic name
     * @param message    string that you send to topic
     */
    @When("^I send a message '(.+?)' to the kafka topic named '(.+?)'")
    public void sendAMessage(String message, String topic_name) throws Exception {
        commonspec.getKafkaUtils().sendMessage(message, topic_name);
    }

    /**
     * Create an elasticsearch index.
     *
     * @param index
     */
    @When("^I create an elasticsearch index named '(.+?)'( removing existing index if exist)?$")
    public void createElasticsearchIndex(String index, String removeIndex) {
        if (removeIndex != null && commonspec.getElasticSearchClient().indexExists(index)) {
            commonspec.getElasticSearchClient().dropSingleIndex(index);
        }
        commonspec.getElasticSearchClient().createSingleIndex(index);
    }

    /**
     * Index a document within a mapping type.
     *
     * @param indexName
     * @param mappingName
     * @param key
     * @param value
     * @throws Exception
     */
    @When("^I index a document in the index named '(.+?)' using the mapping named '(.+?)' with key '(.+?)' and value '(.+?)'$")
    public void indexElasticsearchDocument(String indexName, String mappingName, String key, String value) throws Exception {
        ArrayList<XContentBuilder> mappingsource = new ArrayList<XContentBuilder>();
        XContentBuilder builder = jsonBuilder().startObject().field(key, value).endObject();
        mappingsource.add(builder);
        commonspec.getElasticSearchClient().createMapping(indexName, mappingName, mappingsource);
    }

    /**
     * Create a JSON in resources directory with given name, so for using it you've to reference it as:
     * $(pwd)/target/test-classes/fileName
     *
     * @param fileName      name of the JSON file to be created
     * @param baseData      path to file containing the schema to be used
     * @param type          element to read from file (element should contain a json)
     * @param modifications DataTable containing the modifications to be done to the base schema element
     *                      <p>
     *                      - Syntax will be:
     *                      {@code
     *                      | <key path> | <type of modification> | <new value> |
     *                      }
     *                      for DELETE/ADD/UPDATE/APPEND/PREPEND
     *                      where:
     *                      key path: path to the key to be modified
     *                      type of modification: DELETE/ADD/UPDATE/APPEND/PREPEND
     *                      new value: new value to be used
     *                      <p>
     *                      - Or:
     *                      {@code
     *                      | <key path> | <type of modification> | <new value> | <new value type> |
     *                      }
     *                      for REPLACE
     *                      where:
     *                      key path: path to the key to be modified
     *                      type of modification: REPLACE
     *                      new value: new value to be used
     *                      json value type: type of the json property (array|object|number|boolean|null|n/a (for string))
     *                      <p>
     *                      <p>
     *                      For example:
     *                      <p>
     *                      (1)
     *                      If the element read is {"key1": "value1", "key2": {"key3": "value3"}}
     *                      and we want to modify the value in "key3" with "new value3"
     *                      the modification will be:
     *                      | key2.key3 | UPDATE | "new value3" |
     *                      being the result of the modification: {"key1": "value1", "key2": {"key3": "new value3"}}
     *                      <p>
     *                      (2)
     *                      If the element read is {"key1": "value1", "key2": {"key3": "value3"}}
     *                      and we want to replace the value in "key2" with {"key4": "value4"}
     *                      the modification will be:
     *                      | key2 | REPLACE | {"key4": "value4"} | object |
     *                      being the result of the modification: {"key1": "value1", "key2": {"key4": "value4"}}
     * @throws Exception
     */
    @When("^I create file '(.+?)' based on '(.+?)' as '(.+?)' with:$")
    public void createFile(String fileName, String baseData, String type, DataTable modifications) throws Exception {
        // Retrieve data
        String retrievedData = commonspec.retrieveData(baseData, type);

        // Modify data
        commonspec.getLogger().debug("Modifying data {} as {}", retrievedData, type);
        String modifiedData = commonspec.modifyData(retrievedData, type, modifications).toString();

        // Create file (temporary) and set path to be accessible within test
        File tempDirectory = new File(String.valueOf(System.getProperty("user.dir") + "/target/test-classes/"));
        String absolutePathFile = tempDirectory.getAbsolutePath() + "/" + fileName;
        commonspec.getLogger().debug("Creating file {} in 'target/test-classes'", absolutePathFile);
        // Note that this Writer will delete the file if it exists
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(absolutePathFile), "UTF-8"));
        try {
            out.write(modifiedData);
        } catch (Exception e) {
            commonspec.getLogger().error("Custom file {} hasn't been created:\n{}", absolutePathFile, e.toString());
        } finally {
            out.close();
        }

        Assertions.assertThat(new File(absolutePathFile).isFile());
    }

    /**
     * Read the file passed as parameter, perform the modifications specified and save the result in the environment
     * variable passed as parameter.
     *
     * @param baseData      file to read
     * @param type          whether the info in the file is a 'json' or a simple 'string'
     * @param envVar        name of the variable where to store the result
     * @param modifications modifications to perform in the content of the file
     */
    @When("^I read file '(.+?)' as '(.+?)' and save it in environment variable '(.+?)' with:$")
    public void readFileToVariable(String baseData, String type, String envVar, DataTable modifications) throws Exception {
        // Retrieve data
        String retrievedData = commonspec.retrieveData(baseData, type);

        // Modify data
        commonspec.getLogger().debug("Modifying data {} as {}", retrievedData, type);
        String modifiedData = commonspec.modifyData(retrievedData, type, modifications).toString();

        // Save in environment variable
        ThreadProperty.set(envVar, modifiedData);
    }

    /**
     * Read the file passed as parameter and save the result in the environment
     * variable passed as parameter.
     *
     * @param baseData file to read
     * @param type     whether the info in the file is a 'json' or a simple 'string'
     * @param envVar   name of the variable where to store the result
     */
    @When("^I read file '(.+?)' as '(.+?)' and save it in environment variable '(.+?)'$")
    public void readFileToVariableNoDataTable(String baseData, String type, String envVar) throws Exception {
        // Retrieve data
        String retrievedData = commonspec.retrieveData(baseData, type);

        // Save in environment variable
        ThreadProperty.set(envVar, retrievedData);
    }

    /**
     * Search for a LDAP object
     */
    @When("^I search in LDAP using the filter '(.+?)' and the baseDn '(.+?)'$")
    public void searchLDAP(String filter, String baseDn) throws Exception {
        this.commonspec.setPreviousLdapResults(commonspec.getLdapUtils().search(new SearchRequest(baseDn, filter)));
    }

    /**
     * Method to convert one json to yaml file - backup&restore functionality
     * <p>
     * File will be placed on path /target/test-classes
     */
    @When("^I convert the json file '(.+?)' to yaml file '(.+?)'$")
    public void convertJsonToYaml(String fileToConvert, String fileName) throws Exception {

        // Retrieve data
        String retrievedData = commonspec.asYaml(fileToConvert);

        // Create file (temporary) and set path to be accessible within test
        File tempDirectory = new File(String.valueOf(System.getProperty("user.dir") + "/target/test-classes/"));
        String absolutePathFile = tempDirectory.getAbsolutePath() + "/" + fileName;
        commonspec.getLogger().debug("Creating file {} in 'target/test-classes'", absolutePathFile);
        // Note that this Writer will delete the file if it exists
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(absolutePathFile), "UTF-8"));
        try {
            out.write(retrievedData);
        } catch (Exception e) {
            commonspec.getLogger().error("Custom file {} hasn't been created:\n{}", absolutePathFile, e.toString());
            throw new RuntimeException("Custom file {} hasn't been created");
        } finally {
            out.close();
        }

        Assertions.assertThat(new File(absolutePathFile).isFile());
    }

    /*
     * @param query
     * executes query in database
     *
     *
     */
    @When("^I execute query '(.+?)'$")
    public void executeQuery(String query) throws Exception {
        Statement myStatement = null;
        int result = 0;
        Connection myConnection = this.commonspec.getConnection();

        try {
            myStatement = myConnection.createStatement();
            result = myStatement.executeUpdate(query);
            myStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
            assertThat(result).as(e.getClass().getName() + ": " + e.getMessage()).isNotEqualTo(0);
        }
    }

    /*
     * @param query
     * selects data from database
     * and sends it to environment variable
     *
     */
    @When("^I query the database with '(.+?)'$")
    public void selectData(String query) throws Exception {
        Statement myStatement = null;
        //postgres table
        List<String> sqlTable = new ArrayList<String>();
        List<String> sqlTableAux = new ArrayList<String>();
        Connection myConnection = this.commonspec.getConnection();
        java.sql.ResultSet rs = null;
        try {
            myStatement = myConnection.createStatement();
            rs = myStatement.executeQuery(query);
            //column names
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int count = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= count; i++) {
                sqlTable.add(resultSetMetaData.getColumnName(i).toString());
            }
            //takes column names and culumn count
            while (rs.next()) {
                for (int i = 1; i <= count; i++) {
                    //aux list without column names
                    sqlTableAux.add(rs.getObject(i).toString());
                }
            }
            sqlTable.addAll(sqlTableAux);

            //sends raws to environment variable
            for (int i = 0; i < sqlTable.size(); i++) {
                ThreadProperty.set("queryresponse" + i, sqlTable.get(i));
            }
            rs.close();
            myStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.assertThat(rs).as("There are no response from SELECT").isNotNull();
        }
    }

}
