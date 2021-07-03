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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class FileTest {

    @Test
    public void testReadFileToVariableJSON() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());

        String baseData = "schemas/testCreateFile.json";
        String type = "json";
        String envVar = "myjson";
        List<List<String>> rawData = Arrays.asList(Arrays.asList("key1", "UPDATE", "new_value", "n/a"), Arrays.asList("key2", "ADDTO", "[\"new_value\"]", "array"));
        DataTable modifications = DataTable.create(rawData);

        CommonG commong = new CommonG();
        FileSpec file = new FileSpec(commong);

        file.readFileToVariable(baseData, type, envVar, modifications);

        String envVarResult = ThreadProperty.get(envVar);
        String expectedResult = "{\"key1\":\"new_value\",\"key2\":[[\"new_value\"]],\"key3\":{\"key3_2\":\"value3_2\",\"key3_1\":\"value3_1\"}}";

        assertThat(envVarResult).as("Not as expected").isEqualTo(expectedResult);
    }

    @Test
    public void testReadFileToVariableString() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());

        String baseData = "schemas/krb5.conf";
        String type = "string";
        String envVar = "mystring";
        List<List<String>> rawData = Arrays.asList(Arrays.asList("foo", "REPLACE", "bar", "n/a"));
        DataTable modifications = DataTable.create(rawData);

        CommonG commong = new CommonG();
        FileSpec file = new FileSpec(commong);

        file.readFileToVariable(baseData, type, envVar, modifications);

        String envVarResult = ThreadProperty.get(envVar);
        String expectedResult = "bar = bar";

        assertThat(envVarResult).as("Not as expected").isEqualTo(expectedResult);
    }

    @Test
    public void testReadFileToVariableNoDataTableString() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());

        String baseData = "schemas/krb5.conf";
        String type = "string";
        String envVar = "mystring";

        CommonG commong = new CommonG();
        FileSpec file = new FileSpec(commong);

        file.readFileToVariableNoDataTable(baseData, type, envVar);

        String envVarResult = ThreadProperty.get(envVar);
        String expectedResult = "foo = bar";

        assertThat(envVarResult).as("Not as expected").isEqualTo(expectedResult);
    }

    @Test
    public void testReadCSVFile() throws Exception {
        List<Map<String, String>> expectedCSVResults = new ArrayList<>();
        Map<String, String> mapAux = new HashMap<>();
        mapAux.put("qa", "bdt");
        mapAux.put("test1", "test2");
        expectedCSVResults.add(mapAux);

        ThreadProperty.set("class", this.getClass().getCanonicalName());

        CommonG commong = new CommonG();
        FileSpec file = new FileSpec(commong);

        file.readFromCSV(getClass().getClassLoader().getResource("exampleCSV.csv").getPath(), ",");

        List<Map<String, String>> results = commong.getCSVResults();

        assertThat(commong.getResultsType()).isEqualTo("csv");
        assertThat(commong.getCSVResults()).isEqualTo(expectedCSVResults);
    }

}
