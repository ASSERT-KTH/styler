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

import com.ning.http.client.AsyncHttpClient;
import com.stratio.qa.assertions.Assertions;
import com.stratio.qa.utils.ThreadProperty;
import io.cucumber.datatable.DataTable;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.ning.http.client.Response;

public class RestTest {

    @Test
    public void testsendRequestDataTableTimeout() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        commong.setRestHost("jenkins.stratio.com");
        commong.setRestPort(":80");
        String endPoint = "endpoint";
        String expectedMsg = "regex:tag";
        String requestType = "POST";
        String baseData = "retrieveDataStringTest.conf";
        String type = "string";
        List<List<String>> rawData = Arrays.asList(Arrays.asList("key1", "DELETE", "N/A"));
        DataTable modifications = DataTable.create(rawData);

        RestSpec rest = new RestSpec(commong);

        try {
            rest.sendRequestDataTableTimeout(10, 1, requestType, endPoint, null, expectedMsg, baseData, type, modifications);
            fail("Expected Exception");
        } catch (NullPointerException e) {
            assertThat(e.getClass().toString()).as("Unexpected exception").isEqualTo(NullPointerException.class.toString());
            assertThat(e.getMessage()).as("Unexpected exception message").isEqualTo(null);
        }

    }

    @Test
    public void testsaveResponseInEnvironmentVariableFile() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        commong.setClient(new AsyncHttpClient());
        commong.setRestHost("jenkins.stratio.com");
        commong.setRestPort(":80");

        Future<Response> response = commong.generateRequest("GET", false, null, null, "/job/AI/job/Nightly/", null, "string");
        commong.setResponse("GET", (Response) response.get());

        RestSpec rest = new RestSpec(commong);
        rest.saveResponseInEnvironmentVariableFile("envVar", "file.txt");

        assertThat(ThreadProperty.get("envVar")).as("Unexpected content in thread variable").contains("Responsibles: QA, Pedro Bedia");

        File tempDirectory = new File(String.valueOf(System.getProperty("user.dir") + "/target/test-classes/"));
        String absolutePathFile = tempDirectory.getAbsolutePath() + "/file.txt";

        Assertions.assertThat(new File(absolutePathFile).exists());
        new File(absolutePathFile).delete();
    }

    @Test
    public void testsaveResponseInEnvironmentVariableFileNoFile() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        CommonG commong = new CommonG();
        commong.setClient(new AsyncHttpClient());
        commong.setRestHost("jenkins.stratio.com");
        commong.setRestPort(":80");

        Future<Response> response = commong.generateRequest("GET", false, null, null, "/job/AI/job/Nightly/", null, "string");
        commong.setResponse("GET", (Response) response.get());

        RestSpec rest = new RestSpec(commong);
        rest.saveResponseInEnvironmentVariableFile("envVar", null);

        assertThat(ThreadProperty.get("envVar")).as("Unexpected content in thread variable").contains("Responsibles: QA, Pedro Bedia");

        File tempDirectory = new File(String.valueOf(System.getProperty("user.dir") + "/target/test-classes/"));
        String absolutePathFile = tempDirectory.getAbsolutePath() + "/file.txt";

        Assertions.assertThat(new File(absolutePathFile).exists()).isFalse();
    }

}
