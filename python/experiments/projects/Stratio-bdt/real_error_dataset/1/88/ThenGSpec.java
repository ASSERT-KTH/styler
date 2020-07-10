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

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.Row;
import com.mongodb.DBObject;
import com.stratio.qa.assertions.DBObjectsAssert;
import com.stratio.qa.utils.PreviousWebElements;
import com.stratio.qa.utils.ThreadProperty;
import cucumber.api.DataTable;
import cucumber.api.java.en.Then;
import gherkin.formatter.model.DataTableRow;
import org.apache.zookeeper.KeeperException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Fail;
import org.assertj.core.api.WritableAssertionInfo;
import org.json.JSONArray;
import org.ldaptive.LdapAttribute;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Pattern;

import static com.stratio.qa.assertions.Assertions.assertThat;
import static org.testng.AssertJUnit.fail;

/**
 * Generic Then Specs.
 *
 * @see <a href="ThenGSpec-annotations.html">Then Steps &amp; Matching Regex</a>
 */
public class ThenGSpec extends BaseGSpec {

    public static final int VALUE_SUBSTRING = 3;

    /**
     * Class constructor.
     *
     * @param spec
     */
    public ThenGSpec(CommonG spec) {
        this.commonspec = spec;
    }

    /**
     * Checks if an exception has been thrown.
     *
     * @param exception    : "IS NOT" | "IS"
     * @param foo
     * @param clazz
     * @param bar
     * @param exceptionMsg
     */
    @Then("^an exception '(.+?)' thrown( with class '(.+?)'( and message like '(.+?)')?)?")
    public void assertExceptionNotThrown(String exception, String foo, String clazz, String bar, String exceptionMsg)
            throws ClassNotFoundException {
        List<Exception> exceptions = commonspec.getExceptions();
        if ("IS NOT".equals(exception)) {
            assertThat(exceptions).as("Captured exception list is not empty").isEmpty();
        } else {
            assertThat(exceptions).as("Captured exception list is empty").isNotEmpty();
            Exception ex = exceptions.get(exceptions.size() - 1);
            if ((clazz != null) && (exceptionMsg != null)) {
                assertThat(ex.toString()).as("Unexpected last exception class").contains(clazz);
                assertThat(ex.toString()).as("Unexpected last exception message").contains(exceptionMsg);

            } else if (clazz != null) {
                assertThat(exceptions.get(exceptions.size() - 1).getClass().getSimpleName()).as("Unexpected last exception class").isEqualTo(clazz);
            }

            commonspec.getExceptions().clear();
        }
    }

    /**
     * Checks if a keyspaces exists in Cassandra.
     *
     * @param keyspace
     */
    @Then("^a Cassandra keyspace '(.+?)' exists$")
    public void assertKeyspaceOnCassandraExists(String keyspace) {
        assertThat(commonspec.getCassandraClient().getKeyspaces()).as("The keyspace " + keyspace + " exists on cassandra").contains(keyspace);
    }

    /**
     * Checks a keyspace does not exist in Cassandra.
     *
     * @param keyspace
     */
    @Then("^a Cassandra keyspace '(.+?)' does not exist$")
    public void assertKeyspaceOnCassandraDoesNotExist(String keyspace) {
        assertThat(commonspec.getCassandraClient().getKeyspaces()).as("The keyspace " + keyspace + " does not exist on cassandra").doesNotContain(keyspace);
    }

    /**
     * Checks if a cassandra keyspace contains a table.
     *
     * @param keyspace
     * @param tableName
     */
    @Then("^a Cassandra keyspace '(.+?)' contains a table '(.+?)'$")
    public void assertTableExistsOnCassandraKeyspace(String keyspace, String tableName) {
        assertThat(commonspec.getCassandraClient().getTables(keyspace)).as("The table " + tableName + "exists on cassandra").contains(tableName);
    }

    /**
     * Checks a cassandra keyspace does not contain a table.
     *
     * @param keyspace
     * @param tableName
     */
    @Then("^a Cassandra keyspace '(.+?)' does not contain a table '(.+?)'$")
    public void assertTableDoesNotExistOnCassandraKeyspace(String keyspace, String tableName) {
        assertThat(commonspec.getCassandraClient().getTables(keyspace)).as("The table " + tableName + "exists on cassandra").doesNotContain(tableName);
    }

    /**
     * Checks the number of rows in a cassandra table.
     *
     * @param keyspace
     * @param tableName
     * @param numberRows
     */
    @Then("^a Cassandra keyspace '(.+?)' contains a table '(.+?)' with '(.+?)' rows$")
    public void assertRowNumberOfTableOnCassandraKeyspace(String keyspace, String tableName, String numberRows) {
        Long numberRowsLong = Long.parseLong(numberRows);
        commonspec.getCassandraClient().useKeyspace(keyspace);
        assertThat(commonspec.getCassandraClient().executeQuery("SELECT COUNT(*) FROM " + tableName + ";").all().get(0).getLong(0)).as("The table " + tableName + "exists on cassandra").
                isEqualTo(numberRowsLong);
    }

    /**
     * Checks if a cassandra table contains the values of a DataTable.
     *
     * @param keyspace
     * @param tableName
     * @param data
     * @throws InterruptedException
     */
    @Then("^a Cassandra keyspace '(.+?)' contains a table '(.+?)' with values:$")
    public void assertValuesOfTable(String keyspace, String tableName, DataTable data) throws InterruptedException {
        //  USE of Keyspace
        commonspec.getCassandraClient().useKeyspace(keyspace);
        // Obtain the types and column names of the datatable
        // to return in a hashmap,
        Map<String, String> dataTableColumns = extractColumnNamesAndTypes(data.raw().get(0));
        // check if the table has columns
        String query = "SELECT * FROM " + tableName + " LIMIT 1;";
        com.datastax.driver.core.ResultSet res = commonspec.getCassandraClient().executeQuery(query);
        equalsColumns(res.getColumnDefinitions(), dataTableColumns);
        //receiving the string from the select with the columns
        // that belong to the dataTable
        List<String> selectQueries = giveQueriesList(data, tableName, columnNames(data.raw().get(0)));
        //Check the data  of cassandra with different queries
        int index = 1;
        for (String execQuery : selectQueries) {
            res = commonspec.getCassandraClient().executeQuery(execQuery);
            List<Row> resAsList = res.all();
            assertThat(resAsList.size()).as("The query " + execQuery + " not return any result on Cassandra").isGreaterThan(0);
            assertThat(resAsList.get(0).toString()
                    .substring(VALUE_SUBSTRING)).as("The resultSet is not as expected").isEqualTo(data.raw().get(index).toString().replace("'", ""));
            index++;
        }
    }

    @SuppressWarnings("rawtypes")
    private void equalsColumns(ColumnDefinitions resCols, Map<String, String> dataTableColumns) {
        Iterator it = dataTableColumns.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            assertThat(resCols.toString()).as("The table not contains the column.").contains(e.getKey().toString());
            DataType type = resCols.getType(e.getKey().toString());
            assertThat(type.getName().toString()).as("The column type is not equals.").isEqualTo(e.getValue().toString());
        }
    }

    private List<String> giveQueriesList(DataTable data, String tableName, String colNames) {
        List<String> queryList = new ArrayList<String>();
        for (int i = 1; i < data.raw().size(); i++) {
            String query = "SELECT " + colNames + " FROM " + tableName;
            List<String> row = data.raw().get(i);
            query += conditionWhere(row, colNames.split(",")) + ";";
            queryList.add(query);
        }
        return queryList;
    }

    private String conditionWhere(List<String> values, String[] columnNames) {
        StringBuilder condition = new StringBuilder();
        condition.append(" WHERE ");
        Pattern numberPat = Pattern.compile("^\\d+(\\.*\\d*)?");
        Pattern booleanPat = Pattern.compile("true|false");
        for (int i = 0; i < values.size() - 1; i++) {
            condition.append(columnNames[i]).append(" =");
            condition.append(" ").append(values.get(i)).append(" AND ");
        }
        condition.append(columnNames[columnNames.length - 1]).append(" =");
        condition.append(" ").append(values.get(values.size() - 1));

        condition.append(" ALLOW FILTERING");
        return condition.toString();
    }

    private String columnNames(List<String> firstRow) {
        StringBuilder columnNamesForQuery = new StringBuilder();
        for (String s : firstRow) {
            String[] aux = s.split("-");
            columnNamesForQuery.append(aux[0]).append(",");
        }
        return columnNamesForQuery.toString().substring(0, columnNamesForQuery.length() - 1);
    }

    private Map<String, String> extractColumnNamesAndTypes(List<String> firstRow) {
        HashMap<String, String> columns = new HashMap<String, String>();
        for (String s : firstRow) {
            String[] aux = s.split("-");
            columns.put(aux[0], aux[1]);
        }
        return columns;
    }


    /**
     * Checks the values of a MongoDB table.
     *
     * @param dataBase
     * @param tableName
     * @param data
     */
    @Then("^a Mongo dataBase '(.+?)' contains a table '(.+?)' with values:")
    public void assertValuesOfTableMongo(String dataBase, String tableName, DataTable data) {
        commonspec.getMongoDBClient().connectToMongoDBDataBase(dataBase);
        ArrayList<DBObject> result = (ArrayList<DBObject>) commonspec.getMongoDBClient().readFromMongoDBCollection(
                tableName, data);
        DBObjectsAssert.assertThat(result).containedInMongoDBResult(data);

    }

    /**
     * Checks if a MongoDB database contains a table.
     *
     * @param database
     * @param tableName
     */
    @Then("^a Mongo dataBase '(.+?)' doesnt contains a table '(.+?)'$")
    public void aMongoDataBaseContainsaTable(String database, String tableName) {
        commonspec.getMongoDBClient().connectToMongoDBDataBase(database);
        Set<String> collectionsNames = commonspec.getMongoDBClient().getMongoDBCollections();
        assertThat(collectionsNames).as("The Mongo dataBase contains the table").doesNotContain(tableName);
    }

    /**
     * Verifies that a webelement previously found has {@code text} as text
     *
     * @param index
     * @param text
     */
    @Then("^the element on index '(\\d+?)' has '(.+?)' as text$")
    public void assertSeleniumTextOnElementPresent(Integer index, String text) {
        assertThat(commonspec.getPreviousWebElements()).as("There are less found elements than required")
                .hasAtLeast(index);
        String elementText = commonspec.getPreviousWebElements().getPreviousWebElements().get(index).getText().replace("\n", " ").replace("\r", " ");
        if (!elementText.startsWith("regex:")) {
            //We are verifying that a web element contains a string
            assertThat(elementText.matches("(.*)" + text + "(.*)")).isTrue();
        } else {
            //We are verifying that a web element contains a regex
            assertThat(elementText.matches(text.substring(text.indexOf("regex:") + 6, text.length()))).isTrue();
        }
    }

    /**
     * Checks if a text exists in the source of an already loaded URL.
     *
     * @param text
     */
    @Then("^this text exists '(.+?)'$")
    public void assertSeleniumTextInSource(String text) {
        assertThat(this.commonspec, commonspec.getDriver()).as("Expected text not found at page").contains(text);
    }

    /**
     * Checks if {@code expectedCount} webelements are found, with a location {@code method}.
     *
     * @param expectedCount
     * @param method
     * @param element
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws ClassNotFoundException
     */
    @Then("^'(\\d+?)' elements? exists? with '([^:]*?):(.+?)'$")
    public void assertSeleniumNElementExists(Integer expectedCount, String method, String element) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        List<WebElement> wel = commonspec.locateElement(method, element, expectedCount);
        PreviousWebElements pwel = new PreviousWebElements(wel);
        commonspec.setPreviousWebElements(pwel);
    }

    /**
     * Checks if {@code expectedCount} webelements are found, whithin a {@code timeout} and with a location
     * {@code method}. Each negative lookup is followed by a wait of {@code wait} seconds. Selenium times are not
     * accounted for the mentioned timeout.
     *
     * @param timeout
     * @param wait
     * @param expectedCount
     * @param method
     * @param element
     * @throws InterruptedException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws ClassNotFoundException
     */
    @Then("^in less than '(\\d+?)' seconds, checking each '(\\d+?)' seconds, '(\\d+?)' elements exists with '([^:]*?):(.+?)'$")
    public void assertSeleniumNElementExistsOnTimeOut(Integer timeout, Integer wait, Integer expectedCount,
                                                      String method, String element) throws InterruptedException, ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        List<WebElement> wel = null;
        for (int i = 0; i < timeout; i += wait) {
            wel = commonspec.locateElement(method, element, -1);
            if (wel.size() == expectedCount) {
                break;
            } else {
                Thread.sleep(wait * 1000);
            }
        }

        PreviousWebElements pwel = new PreviousWebElements(wel);
        assertThat(this.commonspec, pwel).as("Element count doesnt match").hasSize(expectedCount);
        commonspec.setPreviousWebElements(pwel);

    }

    /**
     * Checks if {@code expectedCount} element is found, whithin a {@code timeout} and with a location
     * {@code method}. Each negative lookup is followed by a wait of {@code wait} seconds. Selenium times are not
     * accounted for the mentioned timeout.
     *
     * @param timeout
     * @param wait
     * @param command
     * @param search
     * @throws InterruptedException
     */
    @Then("^in less than '(\\d+?)' seconds, checking each '(\\d+?)' seconds, the command output '(.+?)' contains '(.+?)'( with exit status '(.+?)')?$")
    public void assertCommandExistsOnTimeOut(Integer timeout, Integer wait, String command, String search, String foo, Integer exitStatus) throws Exception {
        Boolean found = false;
        AssertionError ex = null;
        command = "set -o pipefail && alias grep='grep --color=never' && " + command;
        for (int i = 0; (i <= timeout); i += wait) {
            if (found) {
                break;
            }
            commonspec.getLogger().debug("Checking output value");
            commonspec.getRemoteSSHConnection().runCommand(command);
            commonspec.setCommandResult(commonspec.getRemoteSSHConnection().getResult());
            try {
                if (exitStatus != null) {
                    assertThat(commonspec.getRemoteSSHConnection().getExitStatus()).isEqualTo(exitStatus);
                }
                assertThat(commonspec.getCommandResult()).as("Contains " + search + ".").contains(search);
                found = true;
                timeout = i;
            } catch (AssertionError e) {
                commonspec.getLogger().info("Command output don't found yet after " + i + " seconds");
                Thread.sleep(wait * 1000);
                ex = e;
            }
        }
        if (!found) {
            throw (ex);
        }
        commonspec.getLogger().info("Command output found after " + timeout + " seconds");
    }


    /**
     * Verifies that a webelement previously found {@code isDisplayed}
     *
     * @param index
     * @param isDisplayed
     */
    @Then("^the element on index '(\\d+?)' (IS|IS NOT) displayed$")
    public void assertSeleniumIsDisplayed(Integer index, Boolean isDisplayed) {
        assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                .hasAtLeast(index);
        assertThat(this.commonspec, commonspec.getPreviousWebElements().getPreviousWebElements().get(index).isDisplayed()).as(
                "Unexpected element display property").isEqualTo(isDisplayed);
    }

    /**
     * Verifies that a webelement previously found {@code isEnabled}
     *
     * @param index
     * @param isEnabled
     */
    @Then("^the element on index '(\\d+?)' (IS|IS NOT) enabled$")
    public void assertSeleniumIsEnabled(Integer index, Boolean isEnabled) {
        assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                .hasAtLeast(index);
        assertThat(this.commonspec, commonspec.getPreviousWebElements().getPreviousWebElements().get(index).isEnabled())
                .as("Unexpected element enabled property").isEqualTo(isEnabled);
    }

    /**
     * Verifies that a webelement previously found {@code isSelected}
     *
     * @param index
     * @param isSelected
     */
    @Then("^the element on index '(\\d+?)' (IS|IS NOT) selected$")
    public void assertSeleniumIsSelected(Integer index, Boolean isSelected) {
        assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                .hasAtLeast(index);
        assertThat(this.commonspec, commonspec.getPreviousWebElements().getPreviousWebElements().get(index).isSelected()).as(
                "Unexpected element selected property").isEqualTo(isSelected);
    }

    /**
     * Verifies that a webelement previously found has {@code attribute} with {@code value} (as a regexp)
     *
     * @param index
     * @param attribute
     * @param value
     */
    @Then("^the element on index '(\\d+?)' has '(.+?)' as '(.+?)'$")
    public void assertSeleniumHasAttributeValue(Integer index, String attribute, String value) {
        assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                .hasAtLeast(index);
        String val = commonspec.getPreviousWebElements().getPreviousWebElements().get(index).getAttribute(attribute);
        assertThat(this.commonspec, val).as("Attribute not found").isNotNull();
        assertThat(this.commonspec, val).as("Unexpected value for specified attribute").matches(value);
    }

    /**
     * Takes an snapshot of the current page
     *
     * @throws Exception
     */
    @Then("^I take a snapshot$")
    public void seleniumSnapshot() throws Exception {
        commonspec.captureEvidence(commonspec.getDriver(), "screenCapture");
    }

    /**
     * Checks that we are in the URL passed
     *
     * @param url
     * @throws Exception
     */
    @Then("^we are in page '(.+?)'$")
    public void checkURL(String url) throws Exception {

        if (commonspec.getWebHost() == null) {
            throw new Exception("Web host has not been set");
        }

        if (commonspec.getWebPort() == null) {
            throw new Exception("Web port has not been set");
        }

        String webURL = commonspec.getWebHost() + commonspec.getWebPort();

        assertThat(commonspec.getDriver().getCurrentUrl()).as("We are not in the expected url: " + webURL.toLowerCase() + url)
                .endsWith(webURL.toLowerCase() + url);
    }

    @Then("^the service response must contain the text '(.*?)'$")
    public void assertResponseMessage(String expectedText) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Pattern pattern = CommonG.matchesOrContains(expectedText);
        assertThat(commonspec.getResponse().getResponse()).containsPattern(pattern);
    }

    @Then("^the service response status must be '(.*?)'( and its response length must be '(.*?)' | and its response must contain the text '(.*?)')?$")
    public void assertResponseStatusLength(Integer expectedStatus, String foo, Integer expectedLength, String expectedText) {
        if (foo != null) {
            if (foo.contains("length")) {
                assertThat(Optional.of(commonspec.getResponse())).hasValueSatisfying(r -> {
                    assertThat(r.getStatusCode()).isEqualTo(expectedStatus);
                    assertThat((new JSONArray(r.getResponse())).length()).isEqualTo(expectedLength);
                });
            } else if (foo.contains("text")) {
                WritableAssertionInfo assertionInfo = new WritableAssertionInfo();
                Pattern pattern = CommonG.matchesOrContains(expectedText);
                assertThat(Optional.of(commonspec.getResponse())).hasValueSatisfying(r -> {
                    assertThat(r.getStatusCode()).isEqualTo(expectedStatus);
                    assertThat(r.getResponse()).containsPattern(pattern);
                });
            }
        } else {
            assertThat(commonspec.getResponse().getStatusCode()).isEqualTo(expectedStatus);
        }
    }

    /**
     * Checks the different results of a previous query
     *
     * @param expectedResults A DataTable Object with all data needed for check the results. The DataTable must contains at least 2 columns:
     *                        a) A field column from the result
     *                        b) Occurrences column (Integer type)
     *                        <p>
     *                        Example:
     *                        |latitude| longitude|place     |occurrences|
     *                        |12.5    |12.7      |Valencia  |1           |
     *                        |2.5     | 2.6      |Stratio   |0           |
     *                        |12.5    |13.7      |Sevilla   |1           |
     *                        IMPORTANT: There no should be no existing columns
     * @throws Exception
     */
    @Then("^There are results found with:$")
    public void resultsMustBe(DataTable expectedResults) throws Exception {

        String type = commonspec.getResultsType();
        assertThat(type).isNotEqualTo("").overridingErrorMessage("It's necessary to define the result type");
        switch (type) {
            case "cassandra":
                commonspec.resultsMustBeCassandra(expectedResults);
                break;
            case "mongo":
                commonspec.resultsMustBeMongo(expectedResults);
                break;
            case "elasticsearch":
                commonspec.resultsMustBeElasticsearch(expectedResults);
                break;
            case "csv":
                commonspec.resultsMustBeCSV(expectedResults);
                break;
            default:
                commonspec.getLogger().warn("default switch branch on results check");
        }
    }

    /**
     * Check the existence of a text at a command output
     *
     * @param search
     **/
    @Then("^the command output contains '(.+?)'$")
    public void findShellOutput(String search) throws Exception {
        assertThat(commonspec.getCommandResult()).as("Contains " + search + ".").contains(search);
    }

    /**
     * Check the non existence of a text at a command output
     *
     * @param search
     **/
    @Then("^the command output does not contain '(.+?)'$")
    public void notFindShellOutput(String search) throws Exception {
        assertThat(commonspec.getCommandResult()).as("NotContains " + search + ".").doesNotContain(search);
    }

    /**
     * Check the exitStatus of previous command execution matches the expected one
     *
     * @param expectedExitStatus
     * @deprecated Success exit status is directly checked in the "execute remote command" method, so this is not
     * needed anymore.
     **/
    @Deprecated
    @Then("^the command exit status is '(.+?)'$")
    public void checkShellExitStatus(int expectedExitStatus) throws Exception {
        assertThat(commonspec.getCommandExitStatus()).as("Is equal to " + expectedExitStatus + ".").isEqualTo(expectedExitStatus);
    }

    /**
     * Save cookie in context for future references
     **/
    @Then("^I save selenium cookies in context$")
    public void saveSeleniumCookies() throws Exception {
        commonspec.setSeleniumCookies(commonspec.getDriver().manage().getCookies());
    }


    /**
     * Get dcos-auth-cookie
     **/
    @Then("^I save selenium dcos acs auth cookie in variable '(.+?)'$")
    public void getDcosAcsAuthCookie(String envVar) throws Exception {
        if (commonspec.getSeleniumCookies() != null && commonspec.getSeleniumCookies().size() != 0) {
            for (Cookie cookie: commonspec.getSeleniumCookies()) {
                if (cookie.getName().contains("dcos-acs-auth-cookie")) {
                    //It's this cookie where we have to extract the value
                    ThreadProperty.set(envVar, cookie.getValue());
                    break;
                }
            }
        } else {
            ThreadProperty.set(envVar, null);
        }
    }

    /**
     * Get dcos-auth-cookie
     **/
    @Then("^I save selenium cookie '(.+?)' in variable '(.+?)'$")
    public void getDcosAcsAuthCookie(String cookieName, String envVar) throws Exception {
        if (commonspec.getSeleniumCookies() != null && commonspec.getSeleniumCookies().size() != 0) {
            for (Cookie cookie: commonspec.getSeleniumCookies()) {
                if (cookie.getName().contains(cookieName)) {
                    //It's this cookie where we have to extract the value
                    ThreadProperty.set(envVar, cookie.getValue());
                    break;
                }
            }
        } else {
            ThreadProperty.set(envVar, null);
        }
    }

    /**
     * Check if a cookie exists
     *
     * @param cookieName string with the name of the cookie
     */
    @Then("^The cookie '(.+?)' exists in the saved cookies$")
    public void checkIfCookieExists(String cookieName) {
        Assertions.assertThat(commonspec.cookieExists(cookieName)).isEqualTo(true);

    }
    /**
     * Check if the length of the cookie set match with the number of cookies thas must be saved
     *
     * @param numberOfCookies number of cookies that must be saved
     */
    @Then("^I have '(.+?)' selenium cookies saved$")
    public void getSeleniumCookiesSize(int numberOfCookies) throws Exception {
        Assertions.assertThat(commonspec.getSeleniumCookies().size()).isEqualTo(numberOfCookies);
    }

    /**
     * Check if expression defined by JSOPath (http://goessner.net/articles/JsonPath/index.html)
     * match in JSON stored in a environment variable.
     *
     * @param envVar environment variable where JSON is stored
     * @param table  data table in which each row stores one expression
     */
    @Then("^'(.+?)' matches the following cases:$")
    public void matchWithExpresion(String envVar, DataTable table) throws Exception {
        String jsonString = ThreadProperty.get(envVar);

        for (DataTableRow row : table.getGherkinRows()) {
            String expression = row.getCells().get(0);
            String condition = row.getCells().get(1);
            String result = row.getCells().get(2);

            String value = commonspec.getJSONPathString(jsonString, expression, null);
            commonspec.evaluateJSONElementOperation(value, condition, result);
        }
    }

    /**
     * A PUT request over the body value.
     *
     * @param key
     * @param value
     * @param service
     * @throws Exception
     */
    @Then("^I add a new DCOS label with key '(.+?)' and value '(.+?)' to the service '(.+?)'?$")
    public void sendAppendRequest(String key, String value, String service) throws Exception {
        commonspec.runCommandAndGetResult("touch " + service + ".json && dcos marathon app show " + service + " > /dcos/" + service + ".json");
        commonspec.runCommandAndGetResult("cat /dcos/" + service + ".json");

        String configFile = commonspec.getRemoteSSHConnection().getResult();
        String myValue = commonspec.getJSONPathString(configFile, ".labels", "0");
        String myJson = commonspec.updateMarathonJson(commonspec.removeJSONPathElement(configFile, "$.labels"));

        String newValue = myValue.replaceFirst("\\{", "{\"" + key + "\": \"" + value + "\", ");
        newValue = "\"labels\":" + newValue;
        String myFinalJson = myJson.replaceFirst("\\{", "{" + newValue.replace("\\n", "\\\\n") + ",");
        if (myFinalJson.contains("uris")) {
            String test = myFinalJson.replaceAll("\"uris\"", "\"none\"");
            commonspec.runCommandAndGetResult("echo '" + test + "' > /dcos/final" + service + ".json");
        } else {
            commonspec.runCommandAndGetResult("echo '" + myFinalJson + "' > /dcos/final" + service + ".json");
        }
        commonspec.runCommandAndGetResult("dcos marathon app update " + service + " < /dcos/final" + service + ".json");

        commonspec.setCommandExitStatus(commonspec.getRemoteSSHConnection().getExitStatus());
    }

    /**
     * Read zPath
     *
     * @param zNode    path at zookeeper
     * @param document expected content of znode
     */
    @Then("^the zNode '(.+?)' exists( and contains '(.+?)')?$")
    public void checkZnodeExists(String zNode, String foo, String document) throws Exception {
        if (document == null) {
            String breakpoint = commonspec.getZookeeperSecClient().zRead(zNode);
            assert breakpoint.equals("") : "The zNode does not exist";
        } else {
            assert commonspec.getZookeeperSecClient().zRead(zNode).contains(document) : "The zNode does not exist or the content does not match";
        }
    }

    @Then("^the zNode '(.+?)' does not exist")
    public void checkZnodeNotExist(String zNode) throws Exception {
        assert !commonspec.getZookeeperSecClient().exists(zNode) : "The zNode exists";
    }

    /**
     * Check that a kafka topic exist
     *
     * @param topic_name name of topic
     */
    @Then("^A kafka topic named '(.+?)' exists")
    public void kafkaTopicExist(String topic_name) throws KeeperException, InterruptedException {
        assert commonspec.getKafkaUtils().getZkUtils().pathExists("/" + topic_name) : "There is no topic with that name";
    }

    /**
     * Check that a kafka topic not exist
     *
     * @param topic_name name of topic
     */
    @Then("^A kafka topic named '(.+?)' does not exist")
    public void kafkaTopicNotExist(String topic_name) throws KeeperException, InterruptedException {
        assert !commonspec.getKafkaUtils().getZkUtils().pathExists("/" + topic_name) : "There is a topic with that name";
    }


    /**
     * Set a environment variable in marathon and deploy again.
     *
     * @param key
     * @param value
     * @param service
     * @throws Exception
     */
    @Then("^I modify marathon environment variable '(.+?)' with value '(.+?)' for service '(.+?)'?$")
    public void setMarathonProperty(String key, String value, String service) throws Exception {
        commonspec.runCommandAndGetResult("touch " + service + "-env.json && dcos marathon app show " + service + " > /dcos/" + service + "-env.json");
        commonspec.runCommandAndGetResult("cat /dcos/" + service + "-env.json");

        String configFile = commonspec.getRemoteSSHConnection().getResult();
        String myJson1 = commonspec.replaceJSONPathElement(configFile, key, value);
        String myJson4 = commonspec.updateMarathonJson(myJson1);
        String myJson = myJson4.replaceAll("\"uris\"", "\"none\"");

        commonspec.runCommandAndGetResult("echo '" + myJson + "' > /dcos/final" + service + "-env.json");
        commonspec.runCommandAndGetResult("dcos marathon app update " + service + " < /dcos/final" + service + "-env.json");
        commonspec.setCommandExitStatus(commonspec.getRemoteSSHConnection().getExitStatus());
    }

    /**
     * Check that the number of partitions is like expected.
     *
     * @param topic_name      Name of kafka topic
     * @param numOfPartitions Number of partitions
     * @throws Exception
     */
    @Then("^The number of partitions in topic '(.+?)' should be '(.+?)''?$")
    public void checkNumberOfPartitions(String topic_name, int numOfPartitions) throws Exception {
        Assertions.assertThat(commonspec.getKafkaUtils().getPartitions(topic_name)).isEqualTo(numOfPartitions);

    }

    /**
     * Check that the ElasticSearch index exists.
     *
     * @param indexName
     */
    @Then("^An elasticsearch index named '(.+?)' exists")
    public void elasticSearchIndexExist(String indexName) {
        assert (commonspec.getElasticSearchClient().indexExists(indexName)) : "There is no index with that name";
    }

    /**
     * Check that the ElasticSearch index does not exist.
     *
     * @param indexName
     */
    @Then("^An elasticsearch index named '(.+?)' does not exist")
    public void elasticSearchIndexDoesNotExist(String indexName) {
        assert !commonspec.getElasticSearchClient().indexExists(indexName) : "There is an index with that name";
    }

    /**
     * Check that an elasticsearch index contains a specific document
     *
     * @param indexName
     * @param columnName
     * @param columnValue
     */
    @Then("^The Elasticsearch index named '(.+?)' and mapping '(.+?)' contains a column named '(.+?)' with the value '(.+?)'$")
    public void elasticSearchIndexContainsDocument(String indexName, String mappingName, String columnName, String columnValue) throws Exception {
        Assertions.assertThat((commonspec.getElasticSearchClient().searchSimpleFilterElasticsearchQuery(
                indexName,
                mappingName,
                columnName,
                columnValue,
                "equals"
        ).size()) > 0).isTrue().withFailMessage("The index does not contain that document");
    }

    /*
     * Check value stored in environment variable "is|matches|is higher than|is lower than|contains||does not contain|is different from" to value provided
     *
     * @param envVar
     * @param value
     *
     */
    @Then("^'(?s)(.+?)' ((?!.*with).+?) '(.+?)'$")
    public void checkValue(String envVar, String operation, String value) throws Exception {
        switch (operation.toLowerCase()) {
            case "is":
                Assertions.assertThat(envVar).isEqualTo(value);
                break;
            case "matches":
                Assertions.assertThat(envVar).matches(value);
                break;
            case "is higher than":
                if (envVar.matches("^-?\\d+$") && value.matches("^-?\\d+$")) {
                    Assertions.assertThat(Integer.parseInt(envVar)).isGreaterThan(Integer.parseInt(value));
                } else {
                    Fail.fail("A number should be provided in order to perform a valid comparison.");
                }
                break;
            case "is lower than":
                if (envVar.matches("^-?\\d+$") && value.matches("^-?\\d+$")) {
                    Assertions.assertThat(Integer.parseInt(envVar)).isLessThan(Integer.parseInt(value));
                } else {
                    Fail.fail("A number should be provided in order to perform a valid comparison.");
                }
                break;
            case "contains":
                Assertions.assertThat(envVar).contains(value);
                break;
            case "does not contain":
                Assertions.assertThat(envVar).doesNotContain(value);
                break;
            case "is different from":
                Assertions.assertThat(envVar).isNotEqualTo(value);
                break;
            default:
                Fail.fail("Not a valid comparison. Valid ones are: is | matches | is higher than | is lower than | contains | does not contain | is different from");
        }
    }

    @Then("^The kafka topic '(.*?)' has a message containing '(.*?)'$")
    public void checkMessages(String topic, String content) {
        assert commonspec.getKafkaUtils().readTopicFromBeginning(topic).contains(content) : "Topic does not exist or the content does not match";
    }

    @Then("^The kafka topic '(.*?)' has a message that contains '(.*?)'$")
    public void checkMessagesContent(String topic, String content) {
        assert commonspec.getKafkaUtils().checkMessageContent(topic, content) : "Topic does not exist or the content does not match";
    }

    @Then("^The kafka topic '(.*?)' has '(.+?)' messages$")
    public void checkMessageOfTopicLentgh(String topic, int numberOfMessages) {
        Assertions.assertThat(commonspec.getKafkaUtils().checkTopicMessagesLenght(topic)).isEqualTo(numberOfMessages);
    }

    /**
     * Takes the content of a webElement and stores it in the thread environment variable passed as parameter
     *
     * @param index  position of the element in the array of webElements found
     * @param envVar name of the thread environment variable where to store the text
     */
    @Then("^I save content of element in index '(\\d+?)' in environment variable '(.+?)'$")
    public void saveContentWebElementInEnvVar(Integer index, String envVar) {
        assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                .hasAtLeast(index);
        String text = commonspec.getPreviousWebElements().getPreviousWebElements().get(index).getText();
        ThreadProperty.set(envVar, text);
    }

    /**
     * Checks if the previous LDAP search contained a single Entry with a specific attribute and an expected value
     *
     * @param attributeName The name of the attribute to look for in the LdapEntry
     * @param expectedValue The expected value of the attribute
     */
    @Then("^the LDAP entry contains the attribute '(.+?)' with the value '(.+?)'$")
    public void ldapEntryContains(String attributeName, String expectedValue) {
        if (this.commonspec.getPreviousLdapResults().isPresent()) {
            Assertions.assertThat(this.commonspec.getPreviousLdapResults().get().getEntry().getAttribute(attributeName).getStringValues()).contains(expectedValue);
        } else {
            fail("No previous LDAP results were stored in memory");
        }
    }

    /*
     * @param table
     * checks table existence
     *
     */
    @Then("^table '(.+?)' exists$")
    public void checkTable(String tableName) throws Exception {
        Statement myStatement = null;
        Connection myConnection = this.commonspec.getConnection();

        //query checks table existence, existence table name in system table  pg_tables
        String query = "SELECT * FROM pg_tables WHERE tablename = " + "\'" + tableName + "\'" + ";";
        try {
            myStatement = myConnection.createStatement();
            java.sql.ResultSet rs = myStatement.executeQuery(query);
            //if there are no data row
            if (rs.next() == false) {
                Assertions.assertThat(rs.next()).as("there are no table " + tableName).isTrue();
            } else {
                //data exist
                String resultTableName = rs.getString(2);
                assertThat(resultTableName).as("there are incorrect table name " + tableName).contains(tableName);
            }
            rs.close();
            myStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /*
     * @param table
     * checks table existence negative case
     *
     */
    @Then("^table '(.+?)' doesn't exists$")
    public void checkTableFalse(String tableName) throws Exception {
        Statement myStatement = null;
        Connection myConnection = this.commonspec.getConnection();

        String query = "SELECT * FROM pg_tables WHERE tablename = " + "\'" + tableName + "\'" + ";";
        try {
            myStatement = myConnection.createStatement();
            java.sql.ResultSet rs = myStatement.executeQuery(query);
            //if there are no data row, table doesn't exists
            if (rs.next() == false) {
                Assertions.assertThat(rs.next()).as("table exists " + tableName).isFalse();
            } else {
                String resultTableName = rs.getString(2);
                assertThat(resultTableName).as("table exists " + tableName).isEqualToIgnoringCase(tableName);
            }
            rs.close();
            myStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*
     * @param tableName
     * @param dataTable
     * compares two tables: pattern table and the result from remote database
     * by default: order by id
     *
     */
    @Then("^I check that table '(.+?)' is iqual to$")
    public void comparetable(String tableName, DataTable dataTable) throws Exception {
        Statement myStatement = null;
        java.sql.ResultSet rs = null;

        //from postgres table
        List<String> sqlTable = new ArrayList<String>();
        List<String> sqlTableAux = new ArrayList<String>();
        //from Cucumber Datatable
        List<String> tablePattern = new ArrayList<String>();
        //comparison is by lists of string
        tablePattern = dataTable.asList(String.class);


        Connection myConnection = this.commonspec.getConnection();
        String query = "SELECT * FROM " + tableName + " order by " + "id" + ";";
        try {
            myStatement = myConnection.createStatement();
            rs = myStatement.executeQuery(query);

            //takes column names and culumn count
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

            assertThat(sqlTable).as("Not equal elements!").isEqualTo(tablePattern);
            rs.close();
            myStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
            assertThat(rs).as("There are no table " + tableName).isNotNull();
        }
    }

    /*
     * closes opened database
     *
     */
    @Then("^I close database connection$")
    public void closeDatabase() throws Exception {
        this.commonspec.getConnection().close();
    }

    /*
     * @param tableName
     * checks the result from select
     *
     */
    @Then("^I check that result is:$")
    public void comparetable(DataTable dataTable) throws Exception {

        //from Cucumber Datatable, the pattern to verify
        List<String> tablePattern = new ArrayList<String>();
        tablePattern = dataTable.asList(String.class);
        //the result from select
        List<String> sqlTable = new ArrayList<String>();

        //the result is taken from previous step
        for (int i = 0; ThreadProperty.get("queryresponse" + i) != null; i++) {
            String ip_value = ThreadProperty.get("queryresponse" + i);
            sqlTable.add(i, ip_value);
        }

        for (int i = 0; ThreadProperty.get("queryresponse" + i) != null; i++) {
            ThreadProperty.remove("queryresponse" + i);
        }

        assertThat(tablePattern).as("response is not equal to the expected").isEqualTo(sqlTable);
    }

    /**
     * @param objetType
     * @param objectName
     * @throws Exception
     */
    @Then("^'(.+?)' '(.+?)' exists$")
    public void checkObjectExists(String objetType, String objectName) throws Exception {
        Statement myStatement = null;
        Connection myConnection = this.commonspec.getConnection();

        String query;

        switch (objetType) {
            case "Database":
                query = "SELECT datname FROM pg_database WHERE datname = " + "\'" + objectName + "\'" + ";";
                break;

            case "Table":
                query = "SELECT tablename FROM pg_tables WHERE tablename = " + "\'" + objectName + "\'" + ";";
                break;

            case "View":
                query = "SELECT viewname FROM pg_views WHERE viewname = " + "\'" + objectName + "\'" + ";";
                break;

            case "Sequence":
                query = "SELECT sequence_name FROM information_schema.sequences WHERE sequence_name = " + "\'" + objectName + "\'" + ";";
                break;

            case "Foreign Data Wrapper":
                query = "SELECT foreign_data_wrapper_name FROM information_schema.foreign_data_wrappers WHERE foreign_data_wrapper_name = " + "\'" + objectName + "\'" + ";";
                break;

            case "Foreign Server":
                query = "SELECT foreign_server_name FROM information_schema.foreign_servers WHERE foreign_server_name = " + "\'" + objectName + "\'" + ";";
                break;

            case "Function":
                query = "SELECT p.proname FROM pg_catalog.pg_proc p JOIN pg_catalog.pg_namespace n ON n.oid = p.pronamespace WHERE p.proname = " + "\'" + objectName + "\'" + ";";
                break;

            case "Schema":
                query = "SELECT schema_name from information_schema.schemata join pg_namespace on schema_name = nspname where schema_name = " + "\'" + objectName + "\'" + ";";
                break;

            case "Domain":
                query = "SELECT domain_name from information_schema.domains WHERE domain_name = " + "\'" + objectName + "\'" + ";";
                break;

            case "Type":
                query = "SELECT user_defined_type_name FROM information_schema.user_defined_types WHERE user_defined_type_name = " + "\'" + objectName + "\'" + ";";
                break;

            case "Column":
                query = "select column_name from information_schema.columns WHERE column_name = " + "\'" + objectName + "\'" + ";";
                break;

            default:
                query = "SELECT 1;";
                break;
        }

        try {
            myStatement = myConnection.createStatement();
            ResultSet rs = myStatement.executeQuery(query);
            //if there are no data row
            if (rs.next() == false) {
                assertThat(rs.next()).as("there are no " + objetType + ": " + objectName).isTrue();
            } else {
                //data exist
                String resultName = rs.getString(1);
                assertThat(resultName).as("there are incorrect " + objetType + " name: " + objectName).contains(objectName);
            }
            rs.close();
            myStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Then("^'(.+?)' '(.+?)' doesn't exists$")
    public void checkObjectNoExists(String objetType, String objectName) throws Exception {
        Statement myStatement = null;
        Connection myConnection = this.commonspec.getConnection();

        String query;

        switch (objetType) {
            case "Database":
                query = "SELECT datname FROM pg_database WHERE datname = " + "\'" + objectName + "\'" + ";";
                break;

            case "Table":
                query = "SELECT tablename FROM pg_tables WHERE tablename = " + "\'" + objectName + "\'" + ";";
                break;

            case "View":
                query = "SELECT viewname FROM pg_views WHERE viewname = " + "\'" + objectName + "\'" + ";";
                break;

            case "Sequence":
                query = "SELECT sequence_name FROM information_schema.sequences WHERE sequence_name = " + "\'" + objectName + "\'" + ";";
                break;

            case "Foreign Data Wrapper":
                query = "SELECT foreign_data_wrapper_name FROM information_schema.foreign_data_wrappers WHERE foreign_data_wrapper_name = " + "\'" + objectName + "\'" + ";";
                break;

            case "Foreign Server":
                query = "SELECT foreign_server_name FROM information_schema.foreign_servers WHERE foreign_server_name = " + "\'" + objectName + "\'" + ";";
                break;

            case "Function":
                query = "SELECT p.proname FROM pg_catalog.pg_proc p JOIN pg_catalog.pg_namespace n ON n.oid = p.pronamespace WHERE p.proname = " + "\'" + objectName + "\'" + ";";
                break;

            case "Schema":
                query = "SELECT schema_name from information_schema.schemata join pg_namespace on schema_name = nspname where schema_name = " + "\'" + objectName + "\'" + ";";
                break;

            case "Domain":
                query = "SELECT domain_name from information_schema.domains WHERE domain_name = " + "\'" + objectName + "\'" + ";";
                break;

            case "Type":
                query = "SELECT user_defined_type_name FROM information_schema.user_defined_types WHERE user_defined_type_name = " + "\'" + objectName + "\'" + ";";
                break;

            case "Column":
                query = "select column_name from information_schema.columns WHERE column_name = " + "\'" + objectName + "\'" + ";";
                break;

            default:
                query = "SELECT 1;";
                break;
        }

        try {
            myStatement = myConnection.createStatement();
            ResultSet rs = myStatement.executeQuery(query);
            //if there are no data row, table doesn't exists
            if (rs.next() == false) {
                assertThat(rs.next()).as(objectName + " exists " + objectName).isFalse();
            } else {
                String resultName = rs.getString(1);
                assertThat(resultName).as(objectName + " exists " + objectName).isEqualToIgnoringCase(objectName);
            }
            rs.close();
            myStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check service status has value specified
     *
     * @param service   name of the service to be checked
     * @param cluster   URI of the cluster
     * @param status    status expected
     * @throws Exception exception     *
     */
    @Then("^service '(.+?)' status in cluster '(.+?)' is '(suspended|running|deploying)'( in less than '(\\d+?)' seconds checking every '(\\d+?)' seconds)?")
    public void serviceStatusCheck(String service, String cluster, String status, String foo, Integer totalWait, Integer interval) throws Exception {
        String response;
        Integer i = 0;
        boolean matched;

        response = commonspec.retrieveServiceStatus(service, cluster);

        if (foo != null) {
            matched = status.matches(response);
            while (!matched && i < totalWait) {
                this.commonspec.getLogger().info("Service status not found yet after " + i + " seconds");
                i = i + interval;
                response = commonspec.retrieveServiceStatus(service, cluster);
                matched = status.matches(response);
            }
        }

        assertThat(status).as("Expected status: " + status + " doesn't match obtained one: " + response).matches(response);

    }

    /**
     * Check service health status has value specified
     *
     * @param service   name of the service to be checked
     * @param cluster   URI of the cluster
     * @param status    health status expected
     * @throws Exception exception     *
     */
    @Then("^service '(.+?)' health status in cluster '(.+?)' is '(unhealthy|healthy|unknown)'( in less than '(\\d+?)' seconds checking every '(\\d+?)' seconds)?")
    public void serviceHealthStatusCheck(String service, String cluster, String status, String foo, Integer totalWait, Integer interval) throws Exception {
        String response;
        Integer i = 0;
        boolean matched;

        response = commonspec.retrieveHealthServiceStatus(service, cluster);

        if (foo != null) {
            matched = status.matches(response);
            while (!matched && i < totalWait) {
                this.commonspec.getLogger().info("Service health status not found yet after " + i + " seconds");
                i = i + interval;
                response = commonspec.retrieveHealthServiceStatus(service, cluster);
                matched = status.matches(response);
            }
        }

        assertThat(status).as("Expected status: " + status + " doesn't match obtained one: " + response).matches(response);
    }

}

