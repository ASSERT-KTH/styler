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

import com.stratio.qa.utils.ThreadProperty;
import io.cucumber.datatable.DataTable;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import org.junit.ComparisonFailure;

public class MiscTest {

    @Test
    public void testSaveElementFromVariable() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());

        String baseData = "indicesJSON.conf";
        String envVar = "envVar";

        String jsonString = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(baseData).getFile())));

        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        try {
            misc.saveElementEnvironment(null, jsonString.concat(".$.[0]"), envVar);
        } catch (Exception e) {
            fail("Error parsing JSON String");
        }

        assertThat(ThreadProperty.get(envVar)).as("Not correctly ordered").isEqualTo("stratiopaaslogs-2016-07-26");
    }

    @Test
    public void testSortJSONElementsAscending() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());

        String baseData = "indicesJSON.conf";
        String ascendingFile = "indicesJSONAscending.conf";
        String envVar = "envVar";

        String jsonString = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(baseData).getFile())));
        String jsonStringAscending = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(ascendingFile).getFile())));

        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        ThreadProperty.set(envVar, jsonString);

        try {
            misc.sortElements(envVar, "alphabetical", "ascending");
        } catch (Exception e) {
            fail("Error parsing JSON String");
        }

        String value = ThreadProperty.get(envVar);

        assertThat(value).as("Not correctly ordered").isEqualTo(jsonStringAscending);
    }

    @Test
    public void testSortJSONElementsDescending() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());

        String baseData = "indicesJSON.conf";
        String descendingFile = "indicesJSONDescending.conf";
        String envVar = "envVar";

        String jsonString = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(baseData).getFile())));
        String jsonStringDescending = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(descendingFile).getFile())));

        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        ThreadProperty.set(envVar, jsonString);

        try {
            misc.sortElements(envVar, "alphabetical", "descending");
        } catch (Exception e) {
            fail("Error parsing JSON String");
        }

        String value = ThreadProperty.get(envVar);

        assertThat(value).as("Not correctly ordered").isEqualTo(jsonStringDescending);
    }

    @Test
    public void testSortJSONElementsOrderedByDefault() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());

        String ascendingFile = "indicesJSONAscending.conf";
        String envVar = "envVar";

        String jsonStringAscending = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(ascendingFile).getFile())));

        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        ThreadProperty.set(envVar, jsonStringAscending);

        try {
            misc.sortElements(envVar, "alphabetical", "ascending");
        } catch (Exception e) {
            fail("Error parsing JSON String");
        }

        String value = ThreadProperty.get(envVar);

        assertThat(value).as("Not correctly ordered").isEqualTo(jsonStringAscending);
    }

    @Test
    public void testSortJSONElementsNoCriteria() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());

        String baseData = "indicesJSON.conf";
        String envVar = "envVar";

        String jsonString = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(baseData).getFile())));

        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        ThreadProperty.set(envVar, jsonString);

        try {
            misc.sortElements(envVar, "nocriteria", "ascending");
            fail("No exception returned ordering without criteria");
        } catch (Exception e) {

        }
    }

    @Test
    public void testValueEqualInJSON() throws Exception {
        String baseData = "consulMesosJSON.conf";
        String envVar = "consulMesos";
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        String result = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(baseData).getFile())));

        ThreadProperty.set(envVar, result);

        List<String> row1 = Arrays.asList("$.[0].Node", "equal", "paaslab31.stratio.com");
        List<String> row2 = Arrays.asList("[0].Node", "equal", "paaslab31.stratio.com");

        List<List<String>> rawData = Arrays.asList(row1, row2);

        DataTable table = DataTable.create(rawData);

        misc.matchWithExpresion(envVar, table);

    }

    @Test
    public void testValueNotEqualInJSON() throws Exception {
        String baseData = "consulMesosJSON.conf";
        String envVar = "consulMesos";
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        String result = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(baseData).getFile())));

        ThreadProperty.set(envVar, result);

        List<String> row1 = Arrays.asList("$.[1].Node", "not equal", "paaslab31.stratio.com");
        List<String> row2 = Arrays.asList("[2].Node", "not equal", "paaslab32.stratio.com");

        List<List<String>> rawData = Arrays.asList(row1, row2);

        DataTable table = DataTable.create(rawData);

        misc.matchWithExpresion(envVar, table);

    }

    @Test
    public void testValueContainsInJSON() throws Exception {
        String baseData = "consulMesosJSON.conf";
        String envVar = "consulMesos";
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        String result = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(baseData).getFile())));

        ThreadProperty.set(envVar, result);

        List<String> row1 = Arrays.asList("$.[0].ServiceTags", "contains", "leader");
        List<String> row2 = Arrays.asList("[1].ServiceTags", "contains", "master");

        List<List<String>> rawData = Arrays.asList(row1, row2);

        DataTable table = DataTable.create(rawData);

        misc.matchWithExpresion(envVar, table);

    }

    @Test
    public void testValueDoesNotContainInJSON() throws Exception {
        String baseData = "consulMesosJSON.conf";
        String envVar = "consulMesos";
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        String result = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(baseData).getFile())));

        ThreadProperty.set(envVar, result);

        List<String> row1 = Arrays.asList("$.[0].ServiceTags", "does not contain", "adsads");
        List<String> row2 = Arrays.asList("[1].Node", "does not contain", "rgrerg");

        List<List<String>> rawData = Arrays.asList(row1, row2);

        DataTable table = DataTable.create(rawData);

        misc.matchWithExpresion(envVar, table);

    }

    @Test(expectedExceptions = AssertionError.class)
    public void testWrongOperatorInJSON() throws Exception {
        String baseData = "consulMesosJSON.conf";
        String envVar = "consulMesos";
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        String result = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(baseData).getFile())));

        ThreadProperty.set(envVar, result);

        List<String> row1 = Arrays.asList("$.[0].ServiceTags", "&&", "leader");
        List<String> row2 = Arrays.asList("[1].Node", "||", "paaslab32.stratio.com");

        List<List<String>> rawData = Arrays.asList(row1, row2);

        DataTable table = DataTable.create(rawData);

        misc.matchWithExpresion(envVar, table);

    }

    @Test
    public void testKeysContainsInJSON() throws Exception {
        String baseData = "exampleJSON.conf";
        String envVar = "exampleEnvVar";
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        String result = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(baseData).getFile())));

        ThreadProperty.set(envVar, result);

        List<String> row1 = Arrays.asList("$.glossary.~[0]", "contains", "title");
        List<String> row2 = Arrays.asList("$.glossary.GlossDiv.~", "contains", "GlossList");

        List<List<String>> rawData = Arrays.asList(row1, row2);

        DataTable table = DataTable.create(rawData);

        misc.matchWithExpresion(envVar, table);

    }

    @Test
    public void testSizeInJSON() throws Exception {
        String baseData = "consulMesosJSON.conf";
        String envVar = "exampleEnvVar";
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        String result = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(baseData).getFile())));

        ThreadProperty.set(envVar, result);

        List<String> row1 = Arrays.asList("$", "size", "4");
        List<String> row2 = Arrays.asList("$.[0].ServiceTags", "size", "2");

        List<List<String>> rawData = Arrays.asList(row1, row2);

        DataTable table = DataTable.create(rawData);

        misc.matchWithExpresion(envVar, table);

    }

    @Test(expectedExceptions = AssertionError.class)
    public void testNotParsedArraySizeInJSON() throws Exception {
        String baseData = "consulMesosJSON.conf";
        String envVar = "exampleEnvVar";
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        String result = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(baseData).getFile())));

        ThreadProperty.set(envVar, result);

        List<String> row1 = Arrays.asList("$.[0]", "size", "4");
        List<List<String>> rawData = Arrays.asList(row1);

        DataTable table = DataTable.create(rawData);
        misc.matchWithExpresion(envVar, table);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = ".*?Expected array for size operation.*?")
    public void testNotArraySizeInJSON() throws Exception {
        String baseData = "consulMesosJSON.conf";
        String envVar = "exampleEnvVar";
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        String result = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(baseData).getFile())));

        ThreadProperty.set(envVar, result);

        List<String> row1 = Arrays.asList("$.[0].Node", "size", "4");
        List<List<String>> rawData = Arrays.asList(row1);

        DataTable table = DataTable.create(rawData);

        misc.matchWithExpresion(envVar, table);
    }

    @Test
    public void testCheckValueInvalidComparison() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        assertThatExceptionOfType(Exception.class).isThrownBy(() -> misc.checkValue("BlaBlaBla", "not valid comparison", "BleBleBle")).withMessageContaining("Not a valid comparison. Valid ones are: is | matches | is higher than | is higher than or equal to | is lower than | is lower than or equal to | contains | does not contain | is different from");
    }

    @Test
    public void testCheckValueIsFail() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        assertThatExceptionOfType(ComparisonFailure.class).isThrownBy(() -> misc.checkValue("10", "is", "5")).withMessageContaining("Values are not equal.");
    }

    @Test()
    public void testCheckValueIsSuccess() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        misc.checkValue("10", "is", "10");
    }

    @Test
    public void testCheckValueMatchesFail() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> misc.checkValue("prueba", "matches", "test")).withMessageContaining("Values are different.");
    }

    @Test
    public void testCheckValueMatchesSuccess() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        misc.checkValue("prueba", "is", "prueba");
    }

    @Test
    public void testCheckValueIsHigherThanException() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        assertThatExceptionOfType(Exception.class).isThrownBy(() -> misc.checkValue("prueba", "is higher than", "10")).withMessageContaining("A number should be provided in order to perform a valid comparison.");
    }

    @Test
    public void testCheckValueIsHigherThanFail() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> misc.checkValue("5", "is higher than", "10")).withMessageContaining("First value is not higher than second one.");
    }

    @Test
    public void testCheckValueIsHigherThanSuccess() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        misc.checkValue("10", "is higher than", "5");
    }

    @Test
    public void testCheckValueIsHigherThanOrEqualToException() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        assertThatExceptionOfType(Exception.class).isThrownBy(() -> misc.checkValue("prueba", "is higher than or equal to", "10")).withMessageContaining("A number should be provided in order to perform a valid comparison.");
    }

    @Test
    public void testCheckValueIsHigherThanOrEqualToFail() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> misc.checkValue("5", "is higher than or equal to", "10")).withMessageContaining("First value is not higher than or equal to second one.");
    }

    @Test
    public void testCheckValueIsHigherThanOrEqualToSuccess() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        misc.checkValue("10", "is higher than or equal to", "5");
    }

    @Test
    public void testCheckValueIsHigherThanOrEqualToSuccess2() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        misc.checkValue("5", "is higher than or equal to", "5");
    }

    @Test
    public void testCheckValueIsLowerThanException() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        assertThatExceptionOfType(Exception.class).isThrownBy(() -> misc.checkValue("prueba", "is lower than", "10")).withMessageContaining("A number should be provided in order to perform a valid comparison.");
    }

    @Test
    public void testCheckValueIsLowerThanFail() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> misc.checkValue("10", "is lower than", "5")).withMessageContaining("First value is not lower than second one.");
    }

    @Test
    public void testCheckValueIsLowerThanSuccess() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        misc.checkValue("5", "is lower than", "10");
    }

    @Test
    public void testCheckValueIsLowerThanOrEqualToException() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        assertThatExceptionOfType(Exception.class).isThrownBy(() -> misc.checkValue("prueba", "is lower than or equal to", "10")).withMessageContaining("A number should be provided in order to perform a valid comparison.");
    }

    @Test
    public void testCheckValueIsLowerThanOrEqualToFail() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> misc.checkValue("10", "is lower than or equal to", "5")).withMessageContaining("First value is not lower than or equal to second one.");
    }

    @Test
    public void testCheckValueIsLowerThanOrEqualToSuccess() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        misc.checkValue("5", "is lower than or equal to", "10");
    }

    @Test
    public void testCheckValueIsLowerThanOrEqualToSuccess2() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        misc.checkValue("5", "is lower than or equal to", "5");
    }

    @Test
    public void testCheckValueContainsFail() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> misc.checkValue("Prueba", "contains", "test")).withMessageContaining("Second value is not contained in first one.");
    }

    @Test
    public void testCheckValueContainsSuccess() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        misc.checkValue("Prueba", "contains", "rueb");
    }

    @Test
    public void testCheckValueDoesNotContainFail() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> misc.checkValue("Prueba", "does not contain", "rueb")).withMessageContaining("Second value is contained in first one.");
    }

    @Test
    public void testCheckValueDoesNotContainSuccess() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        misc.checkValue("Prueba", "does not contain", "test");
    }

    @Test
    public void testCheckValueIsDifferentFromFail() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> misc.checkValue("Prueba", "is different from", "Prueba")).withMessageContaining("Both values are equal.");
    }

    @Test
    public void testCheckValueIsDifferentFromSuccess() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);

        misc.checkValue("Prueba", "is different from", "test");
    }

    @Test
    public void testTenantVariablesException() {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        System.clearProperty("DCOS_TENANT");
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);
        assertThatExceptionOfType(Exception.class).isThrownBy(misc::setTenantVariables)
                .withMessage("DCOS_TENANT is null");
    }

    @Test
    public void testTenantVariablesDefaultBehaviour() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        System.setProperty("DCOS_TENANT", "test");
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);
        misc.setTenantVariables();
        assertThat("NONE").as("Check CC_TENANT").isEqualTo(ThreadProperty.get("CC_TENANT"));
        assertThat("test").as("Check XD_TENANT").isEqualTo(ThreadProperty.get("XD_TENANT"));
        assertThat("test").as("Check ZK_TENANT").isEqualTo(ThreadProperty.get("ZK_TENANT"));
        assertThat("test").as("Check PG_TENANT").isEqualTo(ThreadProperty.get("PG_TENANT"));
        assertThat("test").as("Check ELASTIC_TENANT").isEqualTo(ThreadProperty.get("ELASTIC_TENANT"));
        assertThat("test").as("Check KAFKA_TENANT").isEqualTo(ThreadProperty.get("KAFKA_TENANT"));
        assertThat("test").as("Check SPARK_TENANT").isEqualTo(ThreadProperty.get("SPARK_TENANT"));
        assertThat("test").as("Check PGD_TENANT").isEqualTo(ThreadProperty.get("PGD_TENANT"));
        assertThat("test").as("Check SCHEMA_REGISTRY_TENANT").isEqualTo(ThreadProperty.get("SCHEMA_REGISTRY_TENANT"));
        assertThat("test").as("Check REST_PROXY_TENANT").isEqualTo(ThreadProperty.get("REST_PROXY_TENANT"));
        assertThat("test").as("Check GOV_TENANT").isEqualTo(ThreadProperty.get("GOV_TENANT"));
        assertThat("test").as("Check CASSANDRA_TENANT").isEqualTo(ThreadProperty.get("CASSANDRA_TENANT"));
        assertThat("test").as("Check IGNITE_TENANT").isEqualTo(ThreadProperty.get("IGNITE_TENANT"));
        assertThat("test").as("Check ETCD_TENANT").isEqualTo(ThreadProperty.get("ETCD_TENANT"));
        assertThat("test").as("Check K8S_TENANT").isEqualTo(ThreadProperty.get("K8S_TENANT"));
        assertThat("test").as("Check ARANGO_TENANT").isEqualTo(ThreadProperty.get("ARANGO_TENANT"));
        assertThat("test").as("Check KIBANA_TENANT").isEqualTo(ThreadProperty.get("KIBANA_TENANT"));
        assertThat("test").as("Check HDFS_TENANT").isEqualTo(ThreadProperty.get("HDFS_TENANT"));
        assertThat("test").as("Check SPARTA_TENANT").isEqualTo(ThreadProperty.get("SPARTA_TENANT"));
        System.clearProperty("DCOS_TENANT");
    }

    @Test
    public void testTenantVariablesAll() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        System.setProperty("DCOS_TENANT", "test");
        System.setProperty("CC_TENANT", "cc");
        System.setProperty("XD_TENANT", "xd");
        System.setProperty("ZK_TENANT", "zk");
        System.setProperty("PG_TENANT", "pg");
        System.setProperty("ELASTIC_TENANT", "elastic");
        System.setProperty("KAFKA_TENANT", "kafka");
        System.setProperty("SPARK_TENANT", "spark");
        System.setProperty("PGD_TENANT", "pgd");
        System.setProperty("SCHEMA_REGISTRY_TENANT", "sr");
        System.setProperty("REST_PROXY_TENANT", "rp");
        System.setProperty("GOV_TENANT", "gov");
        System.setProperty("CASSANDRA_TENANT", "cas");
        System.setProperty("IGNITE_TENANT", "ign");
        System.setProperty("ETCD_TENANT", "etcd");
        System.setProperty("K8S_TENANT", "k8s");
        System.setProperty("ARANGO_TENANT", "arango");
        System.setProperty("KIBANA_TENANT", "kib");
        System.setProperty("HDFS_TENANT", "hdfs");
        System.setProperty("SPARTA_TENANT", "sparta");
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);
        misc.setTenantVariables();
        assertThat("cc").as("Check CC_TENANT").isEqualTo(ThreadProperty.get("CC_TENANT"));
        assertThat("xd").as("Check XD_TENANT").isEqualTo(ThreadProperty.get("XD_TENANT"));
        assertThat("zk").as("Check ZK_TENANT").isEqualTo(ThreadProperty.get("ZK_TENANT"));
        assertThat("pg").as("Check PG_TENANT").isEqualTo(ThreadProperty.get("PG_TENANT"));
        assertThat("elastic").as("Check ELASTIC_TENANT").isEqualTo(ThreadProperty.get("ELASTIC_TENANT"));
        assertThat("kafka").as("Check KAFKA_TENANT").isEqualTo(ThreadProperty.get("KAFKA_TENANT"));
        assertThat("spark").as("Check SPARK_TENANT").isEqualTo(ThreadProperty.get("SPARK_TENANT"));
        assertThat("pgd").as("Check PGD_TENANT").isEqualTo(ThreadProperty.get("PGD_TENANT"));
        assertThat("sr").as("Check SCHEMA_REGISTRY_TENANT").isEqualTo(ThreadProperty.get("SCHEMA_REGISTRY_TENANT"));
        assertThat("rp").as("Check REST_PROXY_TENANT").isEqualTo(ThreadProperty.get("REST_PROXY_TENANT"));
        assertThat("gov").as("Check GOV_TENANT").isEqualTo(ThreadProperty.get("GOV_TENANT"));
        assertThat("cas").as("Check CASSANDRA_TENANT").isEqualTo(ThreadProperty.get("CASSANDRA_TENANT"));
        assertThat("ign").as("Check IGNITE_TENANT").isEqualTo(ThreadProperty.get("IGNITE_TENANT"));
        assertThat("etcd").as("Check ETCD_TENANT").isEqualTo(ThreadProperty.get("ETCD_TENANT"));
        assertThat("k8s").as("Check K8S_TENANT").isEqualTo(ThreadProperty.get("K8S_TENANT"));
        assertThat("arango").as("Check ARANGO_TENANT").isEqualTo(ThreadProperty.get("ARANGO_TENANT"));
        assertThat("kib").as("Check KIBANA_TENANT").isEqualTo(ThreadProperty.get("KIBANA_TENANT"));
        assertThat("hdfs").as("Check HDFS_TENANT").isEqualTo(ThreadProperty.get("HDFS_TENANT"));
        assertThat("sparta").as("Check SPARTA_TENANT").isEqualTo(ThreadProperty.get("SPARTA_TENANT"));
        System.clearProperty("DCOS_TENANT");
        System.clearProperty("CC_TENANT");
        System.clearProperty("XD_TENANT");
        System.clearProperty("ZK_TENANT");
        System.clearProperty("PG_TENANT");
        System.clearProperty("ELASTIC_TENANT");
        System.clearProperty("KAFKA_TENANT");
        System.clearProperty("SPARK_TENANT");
        System.clearProperty("PGD_TENANT");
        System.clearProperty("SCHEMA_REGISTRY_TENANT");
        System.clearProperty("REST_PROXY_TENANT");
        System.clearProperty("GOV_TENANT");
        System.clearProperty("CASSANDRA_TENANT");
        System.clearProperty("IGNITE_TENANT");
        System.clearProperty("ETCD_TENANT");
        System.clearProperty("K8S_TENANT");
        System.clearProperty("ARANGO_TENANT");
        System.clearProperty("KIBANA_TENANT");
        System.clearProperty("HDFS_TENANT");
        System.clearProperty("SPARTA_TENANT");
    }

    @Test
    public void testGosecVariablesNoGosecVersion() {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);
        assertThatExceptionOfType(Exception.class).isThrownBy(misc::setGosecVariables)
                .withMessage("gosec-management_version has not been defined");
    }

    @Test
    public void testGosecVariablesInvalidGosecVersion() {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        ThreadProperty.set("gosec-management_version", "1.0");

        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);
        assertThatExceptionOfType(Exception.class).isThrownBy(misc::setGosecVariables)
                .withMessage("gosec-management_version must have X.X.X format");
        ThreadProperty.remove("gosec-management_version");
    }

    @Test
    public void testGosecVariablesGosecVersionWithWrongCharacters() {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        ThreadProperty.set("gosec-management_version", "1.x.1");
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);
        assertThatExceptionOfType(Exception.class).isThrownBy(misc::setGosecVariables);
        ThreadProperty.remove("gosec-management_version");
    }

    @Test
    public void testGosecVariables() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        ThreadProperty.set("gosec-management_version", "1.1.0");
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);
        misc.setGosecVariables();
        assertThat("/api/user/").as("Check API_USER").isEqualTo(ThreadProperty.get("API_USER"));
        assertThat("/api/group/").as("Check API_GROUP").isEqualTo(ThreadProperty.get("API_GROUP"));
        assertThat("/api/policy/").as("Check API_POLICY").isEqualTo(ThreadProperty.get("API_POLICY"));
        assertThat("/api/policy/tag/").as("Check API_TAG").isEqualTo(ThreadProperty.get("API_TAG"));
        assertThat("/api/user").as("Check API_USERS").isEqualTo(ThreadProperty.get("API_USERS"));
        assertThat("/api/group").as("Check API_GROUPS").isEqualTo(ThreadProperty.get("API_GROUPS"));
        assertThat("/api/policy").as("Check API_POLICIES").isEqualTo(ThreadProperty.get("API_POLICIES"));
        assertThat("/api/policy/tag").as("Check API_TAGS").isEqualTo(ThreadProperty.get("API_TAGS"));
        ThreadProperty.remove("gosec-management_version");
    }

    @Test
    public void testGosecVariables2() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        ThreadProperty.set("gosec-management_version", "0.17.4");
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);
        misc.setGosecVariables();
        assertThat("/api/user/").as("Check API_USER").isEqualTo(ThreadProperty.get("API_USER"));
        assertThat("/api/group/").as("Check API_GROUP").isEqualTo(ThreadProperty.get("API_GROUP"));
        assertThat("/api/policy/").as("Check API_POLICY").isEqualTo(ThreadProperty.get("API_POLICY"));
        assertThat("/api/policy/tag/").as("Check API_TAG").isEqualTo(ThreadProperty.get("API_TAG"));
        assertThat("/api/user").as("Check API_USERS").isEqualTo(ThreadProperty.get("API_USERS"));
        assertThat("/api/group").as("Check API_GROUPS").isEqualTo(ThreadProperty.get("API_GROUPS"));
        assertThat("/api/policy").as("Check API_POLICIES").isEqualTo(ThreadProperty.get("API_POLICIES"));
        assertThat("/api/policy/tag").as("Check API_TAGS").isEqualTo(ThreadProperty.get("API_TAGS"));
        ThreadProperty.remove("gosec-management_version");
    }

    @Test
    public void testGosecVariables3() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        ThreadProperty.set("gosec-management_version", "1.1.1");
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);
        misc.setGosecVariables();
        assertThat("/api/user?id=").as("Check API_USER").isEqualTo(ThreadProperty.get("API_USER"));
        assertThat("/api/group?id=").as("Check API_GROUP").isEqualTo(ThreadProperty.get("API_GROUP"));
        assertThat("/api/policy?id=").as("Check API_POLICY").isEqualTo(ThreadProperty.get("API_POLICY"));
        assertThat("/api/policy/tag?id=").as("Check API_TAG").isEqualTo(ThreadProperty.get("API_TAG"));
        assertThat("/api/users").as("Check API_USERS").isEqualTo(ThreadProperty.get("API_USERS"));
        assertThat("/api/groups").as("Check API_GROUPS").isEqualTo(ThreadProperty.get("API_GROUPS"));
        assertThat("/api/policies").as("Check API_POLICIES").isEqualTo(ThreadProperty.get("API_POLICIES"));
        assertThat("/api/policies/tags").as("Check API_TAGS").isEqualTo(ThreadProperty.get("API_TAGS"));
        ThreadProperty.remove("gosec-management_version");
    }

    @Test
    public void testGosecVariables4() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        ThreadProperty.set("gosec-management_version", "1.2.0");
        CommonG commong = new CommonG();
        MiscSpec misc = new MiscSpec(commong);
        misc.setGosecVariables();
        assertThat("/api/user?id=").as("Check API_USER").isEqualTo(ThreadProperty.get("API_USER"));
        assertThat("/api/group?id=").as("Check API_GROUP").isEqualTo(ThreadProperty.get("API_GROUP"));
        assertThat("/api/policy?id=").as("Check API_POLICY").isEqualTo(ThreadProperty.get("API_POLICY"));
        assertThat("/api/policy/tag?id=").as("Check API_TAG").isEqualTo(ThreadProperty.get("API_TAG"));
        assertThat("/api/users").as("Check API_USERS").isEqualTo(ThreadProperty.get("API_USERS"));
        assertThat("/api/groups").as("Check API_GROUPS").isEqualTo(ThreadProperty.get("API_GROUPS"));
        assertThat("/api/policies").as("Check API_POLICIES").isEqualTo(ThreadProperty.get("API_POLICIES"));
        assertThat("/api/policies/tags").as("Check API_TAGS").isEqualTo(ThreadProperty.get("API_TAGS"));
        ThreadProperty.remove("gosec-management_version");
    }

}
