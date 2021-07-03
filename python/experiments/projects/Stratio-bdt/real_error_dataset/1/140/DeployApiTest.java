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

package com.stratio.qa.clients.cct;

import com.stratio.qa.clients.BaseClientTest;
import com.stratio.qa.models.BaseResponseList;
import com.stratio.qa.models.cct.deployApi.DeployedApp;
import com.stratio.qa.utils.ThreadProperty;
import org.mockserver.client.MockServerClient;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;


public class DeployApiTest extends BaseClientTest {

    private DeployApiClient deployApiClient;

    private String baseResponsePath = "responses/cct/deployApi/";

    protected DeployApiClient getClient() {
        setHTTPClient();
        return DeployApiClient.getInstance(commong);
    }

    @BeforeTest
    public void start() throws Exception {
        startMockServer();
        deployApiClient = getClient();
        deployApiClient.setPort(Integer.toString(port));
    }

    private void setEnvs(){
        ThreadProperty.set("EOS_ACCESS_POINT", "localhost");
        ThreadProperty.set("deploy_api_id", "deploy-api");
    }

    @Test
    public void example() throws Exception {
        setEnvs();
        String endpoint = "/service/";
        endpoint = endpoint.concat(ThreadProperty.get("deploy_api_id")).concat("/deployments");

        String responsePath = baseResponsePath.concat("getAppsResponseOK.json");
        String response = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(responsePath).getFile())));

        new MockServerClient("localhost", port)
            .when(
                request()
                    .withMethod("GET")
                    .withPath(endpoint)
            )
            .respond(
                response()
                    .withStatusCode(200)
                    .withBody(response)
            );

        BaseResponseList<DeployedApp> responseList = deployApiClient.getDeployedApps();
        assertThat(responseList.getList()).as("List should not be empty").isNotEmpty();
        assertThat(responseList.getList().size()).as("Response elements do not match").isEqualTo(16);
    }

    @AfterTest
    public void stop() {
        stopMockServer();
    }

}
