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

import com.ning.http.client.Response;
import com.stratio.qa.assertions.Assertions;
import com.stratio.qa.utils.ThreadProperty;
import cucumber.api.java.en.Given;
import io.cucumber.datatable.DataTable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.fail;

/**
 * Generic Command Center Specs.
 *
 * @see <a href="CCTSpec-annotations.html">Command Center Steps</a>
 */
public class CCTSpec extends BaseGSpec {

    private final Logger logger = LoggerFactory.getLogger(CCTSpec.class);
    /**
     * Generic constructor.
     *
     * @param spec object
     */
    public CCTSpec(CommonG spec) {
        this.commonspec = spec;
    }

    /**
     * Download last lines from logs of a service/framework
     * @param logType
     * @param service
     * @param taskType
     * @throws Exception
     */
    @Given("^I want to download '(stdout|stderr)' last '(\\d+)' lines of service '(.+?)'( with task type '(.+?)')?")
    public void downLoadLogsFromService(String logType, Integer lastLinesToRead, String service, String taskType) throws Exception {
        String fileOutputName = service.replace('/', '_') + taskType + logType;
        String endPoint;
        if (ThreadProperty.get("cct-marathon-services_id") == null) {
            endPoint = "/service/" + ThreadProperty.get("deploy_api_id") + " /deployments/service?instanceName=" + service;
        } else {
            endPoint = "/service/cct-marathon-services/v1/services/" + service;
        }
        Future<Response> response = null;
        commonspec.getLogger().debug("Trying to send http request to: " + endPoint);
        response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null);
        if (response.get().getStatusCode() != 200) {
            throw new Exception("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode());
        }
        commonspec.setResponse(endPoint, response.get());
        ArrayList<String> mesosTaskId = obtainMesosTaskInfo(commonspec.getResponse().getResponse(), null, "id");
        ArrayList<String> mesosTaskName = obtainMesosTaskInfo(commonspec.getResponse().getResponse(), null, "name");
        boolean contained = false;
        if (mesosTaskId.size() > 1) {
            for (int i = 0; i < mesosTaskName.size() && !contained; i++) {
                if (mesosTaskName.get(i).contains(taskType)) {
                    contained = true;
                    taskType = mesosTaskId.get(i);
                }
            }
        } else {
            contained = true;
            taskType = mesosTaskId.get(0);
        }
        if (!contained) {
            fail("The mesos task type does not exists");
        }

        String endpointTask;
        if (ThreadProperty.get("cct-marathon-services_id") == null) {
            endpointTask = "/service/" + ThreadProperty.get("deploy_api_id") + "/deployments/logs/" + taskType;
        } else {
            endpointTask = "/service/cct-marathon-services/v1/services/tasks/" + taskType + "/logs";
        }
        commonspec.getLogger().debug("Trying to send http request to: " + endpointTask);
        response = commonspec.generateRequest("GET", false, null, null, endpointTask, "", null);
        if (response.get().getStatusCode() != 200) {
            throw new Exception("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode());
        }
        commonspec.setResponse("GET", response.get());
        commonspec.getLogger().debug("Trying to obtain mesos logs path");
        String path = obtainLogsPath(commonspec.getResponse().getResponse(), logType, "READ") + "/" +  logType;
        String logOfTask = readLogsFromMesos(path, lastLinesToRead);
        Files.write(Paths.get(System.getProperty("user.dir") + "/target/test-classes/" + fileOutputName), logOfTask.getBytes());
    }

     /**
     * Read last lines from logs of a service/framework
     * @param logType
     * @param service
     * @param taskType
     * @param logToCheck
     * @param lastLinesToRead
     * @throws Exception
     */
    @Given("^The '(stdout|stderr)' of service '(.+?)'( with task type '(.+?)')? contains '(.+?)' in the last '(\\d+)' lines$")
    public void readLogsFromService(String logType, String service, String taskType, String logToCheck, Integer lastLinesToRead) throws Exception {
        commonspec.getLogger().debug("Start process of read " + lastLinesToRead + " from the mesos log");
        String endPoint;
        if (ThreadProperty.get("cct-marathon-services_id") == null) {
            endPoint = "/service/" + ThreadProperty.get("deploy_api_id") + " /deployments/service?instanceName=" + service;
        } else {
            endPoint = "/service/cct-marathon-services/v1/services/" + service;
        }
        Future<Response> response = null;
        commonspec.getLogger().debug("Trying to send http request to: " + endPoint);
        response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null);
        if (response.get().getStatusCode() != 200) {
            throw new Exception("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode());
        }
        commonspec.setResponse(endPoint, response.get());
        ArrayList<String> mesosTaskId = obtainMesosTaskInfo(commonspec.getResponse().getResponse(), taskType, "id");
        commonspec.getLogger().info("Mesos Task Ids obtained successfully");
        commonspec.getLogger().debug("Mesos task ids: "  + Arrays.toString(mesosTaskId.toArray()));
        boolean contained = false;
        for (int i = 0; i < mesosTaskId.size() && !contained; i++) {
            String endpointTask;
            if (ThreadProperty.get("cct-marathon-services_id") == null) {
                endpointTask = "/service/" + ThreadProperty.get("deploy_api_id") + "/deployments/logs/" + taskType;
            } else {
                endpointTask = "/service/cct-marathon-services/v1/services/tasks/" + taskType + "/logs";
            }
            commonspec.getLogger().debug("Trying to send http request to: " + endpointTask);
            response = commonspec.generateRequest("GET", false, null, null, endpointTask, "", null);
            if (response.get().getStatusCode() != 200) {
                throw new Exception("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode());
            }
            commonspec.setResponse("GET", response.get());
            commonspec.getLogger().debug("Trying to obtain mesos logs path");
            String path = obtainLogsPath(commonspec.getResponse().getResponse(), logType, "READ") + "/" +  logType;
            commonspec.getLogger().debug("Trying to read mesos logs");
            String logOfTask = readLogsFromMesos(path, lastLinesToRead);
            if (logOfTask.contains(logToCheck)) {
                contained = true;
            }
        }
        if (!contained) {
            fail("The log " + logToCheck + " is not contaided in the task logs");
        }
    }

    /**
     * Read log from mesos
     * @param path
     * @param lastLines
     * @return
     * @throws Exception
     */
    public String readLogsFromMesos(String path, Integer lastLines) throws Exception {
        //obtain last offset
        Future<Response> response = null;
        response = commonspec.generateRequest("GET", false, null, null, path, "", null);
        if (response.get().getStatusCode() != 200) {
            throw new Exception("Request failed to endpoint: " + path + " with status code: " + commonspec.getResponse().getStatusCode());
        }
        JSONObject offSetJson = new JSONObject(response.get().getResponseBody());

        Integer offSet = offSetJson.getInt("offset");
        //Read 1000 bytes
        String logs = "";
        Integer lineCount = 0;
        for (int i = offSet; (i >= 0) && (lineCount <= lastLines); i = i - 1000) {
            String endPoint = path + "&offset=" + (i - 1000) + "&length=" + i;
            if (i < 1000) {
                endPoint = path + "&offset=0&length=" + i;

            }
            response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null);
            if (response.get().getStatusCode() != 200) {
                throw new Exception("Request failed to endpoint: " + path + " with status code: " + commonspec.getResponse().getStatusCode());
            }
            commonspec.setResponse("GET", response.get());
            JSONObject cctJsonResponse = new JSONObject(commonspec.getResponse().getResponse());
            logs = cctJsonResponse.getString("data") + logs;
            lineCount = logs.split("\n").length + lineCount;
        }
        return logs;
    }

    /**
     * Obtain logs path from JSON
     * @param response
     * @param logType
     * @param action
     * @return path
     */
    public String obtainLogsPath(String response, String logType, String action) {
        String path = null;
        JSONObject cctJsonResponse = new JSONObject(response);
        JSONArray arrayOfPaths = (JSONArray) cctJsonResponse.get("content");
        for (int i = 0; i < arrayOfPaths.length(); i++) {
            if (arrayOfPaths.getJSONObject(i).getString("name").equalsIgnoreCase(logType) && arrayOfPaths.getJSONObject(i).getString("action").equalsIgnoreCase(action)) {
                path = arrayOfPaths.getJSONObject(i).getString("path");
            }
        }
        return path;
    }

    /**
     * Obtain info about task type from json
     * @param response
     * @param taskType
     * @param info
     * @return
     */
    public ArrayList<String> obtainMesosTaskInfo (String response, String taskType, String info) {
        ArrayList<String> result = new ArrayList<String>();
        JSONObject cctJsonResponse = new JSONObject(response);
        JSONArray arrayOfTasks = (JSONArray) cctJsonResponse.get("tasks");
        if (arrayOfTasks.length() == 1 || taskType == null) {
            result.add((arrayOfTasks.getJSONObject(0).getString(info)));
        }
        String regex_name = ".*";
        if (taskType != null) {
            regex_name = ".[" + taskType + "]*";
        }
        for (int i = 0; i < arrayOfTasks.length(); i++) {
            JSONObject task = arrayOfTasks.getJSONObject(i);
            if (task.getString("name").matches(regex_name)) {
                result.add((task.getString(info)));
            }
        }
        return result;
    }


    /**
     * TearDown a service with deploy-api
     * @param service
     * @throws Exception
     */
    @Given("^I teardown the service '(.+?)' of tenant '(.+?)'")
    public void tearDownService(String service, String tenant) throws Exception {
        if (ThreadProperty.get("deploy_api_id") == null) {
            fail("deploy_api_id variable is not set. Check deploy-api is installed and @dcos annotation is working properly.");
        }
        String endPoint = "/service/" + ThreadProperty.get("deploy_api_id") + "/deploy/teardown?frameworkName=" + service;
        Future<Response> response;
        response = commonspec.generateRequest("DELETE", false, null, null, endPoint, "", null, "");
        commonspec.setResponse("DELETE", response.get());
        if (commonspec.getResponse().getStatusCode() != 200 || commonspec.getResponse().getStatusCode() != 201) {
            logger.error("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            throw new Exception("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
        }
        // Check service has disappeared
        RestSpec restSpec = new RestSpec(commonspec);

        String endPointStatus;
        if (ThreadProperty.get("cct-marathon-services_id") == null) {
            endPointStatus = "/service/" + ThreadProperty.get("deploy_api_id") + "/deploy/status/all";
        } else {
            endPointStatus = "/service/" + ThreadProperty.get("cct-marathon-services_id") + "/v1/services?tenant=" + tenant;
        }

        String serviceName = "/" + service;
        if (!"NONE".equals(tenant)) {
            serviceName = "/" + tenant + "/" + tenant + "-" + service;
        }
        restSpec.sendRequestTimeout(200, 20, "GET", endPointStatus, "does not", serviceName);

        // Check all resources have been freed
        DcosSpec dcosSpec = new DcosSpec(commonspec);
        dcosSpec.checkResources(serviceName);
    }

    /**
     * Scale service from deploy-api
     * @param service
     * @param instances
     * @throws Exception
     */
    @Given("^I scale service '(.+?)' to '(\\d+)' instances")
    public void scaleService(String service, Integer instances) throws Exception {
        if (ThreadProperty.get("deploy_api_id") == null) {
            fail("deploy_api_id variable is not set. Check deploy-api is installed and @dcos annotation is working properly.");
        }
        String endPoint = "/service/" + ThreadProperty.get("deploy_api_id") + "/deploy/scale?instances=" + instances + "&serviceName=" + service;
        Future<Response> response;
        response = commonspec.generateRequest("PUT", false, null, null, endPoint, "", null, "");
        commonspec.setResponse("PUT", response.get());

        if (commonspec.getResponse().getStatusCode() != 200 || commonspec.getResponse().getStatusCode() != 201) {
            logger.error("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            throw new Exception("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
        }
    }


    /**
     * Checks in Command Center service status
     * @param timeout
     * @param wait
     * @param service
     * @param numTasks
     * @param taskType
     * @param expectedStatus Expected status (healthy|unhealthy|running|stopped)
     * @throws Exception
     */
    @Given("^in less than '(\\d+)' seconds, checking each '(\\d+)' seconds, I check that the service '(.+?)' in CCT with '(\\d+)' tasks of type '(.+?)' is in '(healthy|unhealthy|running|stopped)' status")
    public void checkServiceStatus(Integer timeout, Integer wait, String service, Integer numTasks, String taskType, String expectedStatus) throws Exception {
        String endPoint = "/service/deploy-api/deployments/service?instanceName=" + service;
        if (ThreadProperty.get("cct-marathon-services_id") != null) {
            endPoint = "/service/cct-marathon-services/v1/services/" + service;
        }
        boolean  statusService = false;
        for (int i = 0; (i <= timeout) && (!statusService); i += wait) {
            try {
                Future<Response> response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null);
                commonspec.setResponse(endPoint, response.get());
                statusService = checkServiceStatusInResponse(expectedStatus, commonspec.getResponse().getResponse(), numTasks, taskType);
            } catch (Exception e) {
                commonspec.getLogger().debug("Error in request " + endPoint + " - " + e.toString());
            }
            if (i < timeout) {
                Thread.sleep(wait * 1000);
            }
        }
        if (!statusService) {
            fail(expectedStatus + " status not found after " + timeout + " seconds for service " + service);
        }
    }

    /**
     * Check status of a task in response of the CCT
     * @param expectedStatus
     * @param response
     * @param tasks
     * @param name
     * @return
     */
    public boolean checkServiceStatusInResponse(String expectedStatus, String response, Integer tasks, String name) {
        JSONObject cctJsonResponse = new JSONObject(response);
        JSONArray arrayOfTasks = (JSONArray) cctJsonResponse.get("tasks");
        int task_counter = 0;
        String key = "status";
        if (arrayOfTasks.getJSONObject(0).toString().contains("state")) {
            key = "state";
            switch (expectedStatus.toLowerCase()) {
                case "running":
                case "healthy":
                    expectedStatus = "TASK_RUNNING";
                    break;
                case "stopped":
                case "unhealthy":
                    expectedStatus = "TASK_KILLED";
                    break;
                default:
                    return false;
            }
        }
        if (arrayOfTasks.length() == 1 || tasks == null) {
            boolean res = (arrayOfTasks.getJSONObject(0).getString(key).equalsIgnoreCase(expectedStatus));
            if (!res) {
                commonspec.getLogger().warn("The status of " + arrayOfTasks.getJSONObject(0).getString("name") + " is " + arrayOfTasks.getJSONObject(0).getString(key));
                commonspec.getLogger().warn("Expected status of " + arrayOfTasks.getJSONObject(0).getString("name") + " is " + expectedStatus);
            }
            return res;
        }
        String regex_name = ".[" + name + "]*";
        for (int i = 0; i < arrayOfTasks.length(); i++) {
            JSONObject task = arrayOfTasks.getJSONObject(i);
            if (task.getString("name").matches(regex_name)) {
                task_counter++;
                if (!task.getString(key).equalsIgnoreCase(expectedStatus)) {
                    commonspec.getLogger().warn("The status of " + task.getString("name") + " is " + task.getString(key));
                    commonspec.getLogger().warn(" Expected status of " + task.getString("name") + " is " + expectedStatus);
                    return false;
                }
            }
        }
        if (task_counter == tasks) {
            return true;
        }
        commonspec.getLogger().error("The number of tasks deployed: " + task_counter + " are not the expected ones: " + tasks);
        return false;
    }

    /**
     * Checks in Command Center service status
     *
     * @param timeout
     * @param wait
     * @param service
     * @param numTasks
     * @param expectedStatus Expected status (healthy|unhealthy|running|stopped)
     * @throws Exception
     */
    @Given("^in less than '(\\d+)' seconds, checking each '(\\d+)' seconds, I check in CCT that the service '(.+?)'( with number of tasks '(\\d+)')? is in '(healthy|unhealthy|running|stopped)' status$")
    public void checkServiceStatus(Integer timeout, Integer wait, String service, Integer numTasks, String expectedStatus) throws Exception {
        String endPoint = "/service/deploy-api/deployments/service?instanceName=" + service;
        boolean useMarathonServices = false;
        if (ThreadProperty.get("cct-marathon-services_id") != null) {
            endPoint = "/service/cct-marathon-services/v1/services/" + service;
            useMarathonServices = true;
        }
        boolean found = false;
        boolean isDeployed = false;

        for (int i = 0; (i <= timeout); i += wait) {
            try {
                Future<Response> response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null);
                commonspec.setResponse(endPoint, response.get());
                found = checkServiceStatusInResponse(expectedStatus, commonspec.getResponse().getResponse(), useMarathonServices);
                if (numTasks != null) {
                    isDeployed = checkServiceDeployed(commonspec.getResponse().getResponse(), numTasks, useMarathonServices);
                }
            } catch (Exception e) {
                commonspec.getLogger().debug("Error in request " + endPoint + " - " + e.toString());
            }
            if ((found && (numTasks == null)) || (found && (numTasks != null) && isDeployed)) {
                break;
            } else {
                if (!found) {
                    commonspec.getLogger().info(expectedStatus + " status or tasks not found after " + i + " seconds for service " + service);
                } else if (numTasks != null && !isDeployed) {
                    commonspec.getLogger().info("Tasks have not been deployed successfully after " + i + " seconds for service " + service);
                }
                if (i < timeout) {
                    Thread.sleep(wait * 1000);
                }
            }
        }
        if (!found) {
            fail(expectedStatus + " status not found after " + timeout + " seconds for service " + service);
        }
        if ((numTasks != null) && !isDeployed) {
            fail("Tasks have not been deployed successfully after " + timeout + " seconds for service " + service);
        }
    }

    /**
     * Checks in Command Center response if the service has the expected status
     *
     * @param expectedStatus Expected status (healthy|unhealthy)
     * @param response Command center response
     * @param useMarathonServices True if cct-marathon-services is used in request, False if deploy-api is used in request
     * @return If service status has the expected status
     */
    private boolean checkServiceStatusInResponse(String expectedStatus, String response, boolean useMarathonServices) {
        if (useMarathonServices) {
            JSONObject cctJsonResponse = new JSONObject(response);
            String status = cctJsonResponse.getString("status");
            String healthiness = cctJsonResponse.getString("healthiness");
            switch (expectedStatus) {
                case "healthy":
                case "unhealthy":
                    return healthiness.equalsIgnoreCase(expectedStatus);
                case "running":     return status.equalsIgnoreCase("RUNNING");
                case "stopped":     return status.equalsIgnoreCase("SUSPENDED");
                default:
            }
        } else {
            switch (expectedStatus) {
                case "healthy":     return response.contains("\"healthy\":1");
                case "unhealthy":   return response.contains("\"healthy\":2");
                case "running":     return response.contains("\"status\":2");
                case "stopped":     return response.contains("\"status\":1");
                default:
            }
        }
        return false;
    }


    /**
     * Checks in Command Center response if the service tasks are deployed successfully
     *
     * @param response Command center response
     * @param numTasks Command center response
     * @param useMarathonServices True if cct-marathon-services is used in request, False if deploy-api is used in request
     * @return If service status has the expected status
     */
    private boolean checkServiceDeployed(String response, int numTasks, boolean useMarathonServices) {

        JSONObject deployment = new JSONObject(response);
        JSONArray tasks = (JSONArray) deployment.get("tasks");
        int numTasksRunning = 0;

        for (int i = 0; i < tasks.length(); i++) {
            if (useMarathonServices) {
                numTasksRunning = tasks.getJSONObject(i).get("status").equals("RUNNING") ? (numTasksRunning + 1) : numTasksRunning;
            } else {
                numTasksRunning = tasks.getJSONObject(i).get("state").equals("TASK_RUNNING") ? (numTasksRunning + 1) : numTasksRunning;
            }
        }
        return numTasksRunning == numTasks;
    }

    /**
     * Get info from centralized configuration
     *
     * @param path
     * @param envVar
     * @param fileName
     * @throws Exception
     */
    @Given("^I get info from global config with path '(.*?)'( and save it in environment variable '(.*?)')?( and save it in file '(.*?)')?$")
    public void infoFromGlobalConfig(String path, String envVar, String fileName) throws Exception {

        String endPoint = "/service/" + ThreadProperty.get("configuration_api_id") + "/central";
        Future<Response> response;

        String pathEndpoint = "?path=" + path.replaceAll("/", "%2F");
        endPoint = endPoint.concat(pathEndpoint);

        response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null, "");
        commonspec.setResponse("GET", response.get());

        if (commonspec.getResponse().getStatusCode() != 200) {
            logger.error("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            throw new Exception("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
        }

        String json = commonspec.getResponse().getResponse();

        if (envVar != null) {
            ThreadProperty.set(envVar, json);
        }

        if (fileName != null) {
            writeInFile(json, fileName);
        }
    }

    /**
     * Get global configuration from centralized configuration
     *
     * @param envVar
     * @param fileName
     * @throws Exception
     */
    @Given("^I get global configuration( and save it in environment variable '(.*?)')?( and save it in file '(.*?)')?$")
    public void getGlobalConfig(String envVar, String fileName) throws Exception {

        String endPoint = "/service/" + ThreadProperty.get("configuration_api_id") + "/central/config";
        Future<Response> response;

        response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null, "");
        commonspec.setResponse("GET", response.get());

        if (commonspec.getResponse().getStatusCode() != 200) {
            logger.error("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            throw new Exception("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
        }

        String json = commonspec.getResponse().getResponse();

        if (envVar != null) {
            ThreadProperty.set(envVar, json);
        }

        if (fileName != null) {
            writeInFile(json, fileName);
        }
    }

    /**
     * Get schema from global configuration
     *
     * @param envVar
     * @param fileName
     * @throws Exception
     */
    @Given("^I get schema from global configuration( and save it in environment variable '(.*?)')?( and save it in file '(.*?)')?$")
    public void getSchemaGlobalConfig(String envVar, String fileName) throws Exception {

        String endPoint = "/service/" + ThreadProperty.get("configuration_api_id") + "/central/schema";
        Future<Response> response;

        response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null, "");
        commonspec.setResponse("GET", response.get());

        if (commonspec.getResponse().getStatusCode() != 200) {
            logger.error("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            throw new Exception("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
        }

        String json = commonspec.getResponse().getResponse();

        if (envVar != null) {
            ThreadProperty.set(envVar, json);
        }

        if (fileName != null) {
            writeInFile(json, fileName);
        }
    }

    /**
     * Get info for network Id
     *
     * @param networkId
     * @param envVar
     * @param fileName
     * @throws Exception
     */
    @Given("^I get network '(.*?)'( and save it in environment variable '(.*?)')?( and save it in file '(.*?)')?$")
    public void getNetworkById(String networkId, String envVar, String fileName) throws Exception {

        String endPoint = "/service/" + ThreadProperty.get("configuration_api_id") + "/network/" + networkId;
        Future<Response> response;

        response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null, "");
        commonspec.setResponse("GET", response.get());

        if (commonspec.getResponse().getStatusCode() != 200) {
            logger.error("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            throw new Exception("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
        }

        String json = commonspec.getResponse().getResponse();

        if (envVar != null) {
            ThreadProperty.set(envVar, json);
        }

        if (fileName != null) {
            writeInFile(json, fileName);
        }
    }

    /**
     * Get info for all networks
     *
     * @param envVar
     * @param fileName
     * @throws Exception
     */
    @Given("^I get all networks( and save it in environment variable '(.*?)')?( and save it in file '(.*?)')?$")
    public void getAllNetworks(String envVar, String fileName) throws Exception {

        String endPoint = "/service/" + ThreadProperty.get("configuration_api_id") + "/network/all";
        Future<Response> response;

        response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null, "");
        commonspec.setResponse("GET", response.get());

        if (commonspec.getResponse().getStatusCode() != 200) {
            logger.error("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            throw new Exception("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
        }

        String json = commonspec.getResponse().getResponse();

        if (envVar != null) {
            ThreadProperty.set(envVar, json);
        }

        if (fileName != null) {
            writeInFile(json, fileName);
        }
    }

    /**
     * Get Mesos configuration
     *
     * @param path
     * @param envVar
     * @param fileName
     * @throws Exception
     */
    @Given("^I get path '(.*?)' from Mesos configuration( and save it in environment variable '(.*?)')?( and save it in file '(.*?)')?$")
    public void getMesosConfiguration(String path, String envVar, String fileName) throws Exception {

        Future<Response> response;

        String endPoint = "/service/" + ThreadProperty.get("configuration_api_id") + "/mesos";
        String pathEndpoint = "?path=" + path.replaceAll("/", "%2F");
        endPoint = endPoint.concat(pathEndpoint);

        response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null, "");
        commonspec.setResponse("GET", response.get());

        if (commonspec.getResponse().getStatusCode() != 200) {
            logger.error("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            throw new Exception("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
        }

        String json = commonspec.getResponse().getResponse();

        if (envVar != null) {
            ThreadProperty.set(envVar, json);
        }

        if (fileName != null) {
            writeInFile(json, fileName);
        }
    }

    /**
     * Create/Update calico network
     *
     * @param timeout
     * @param wait
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
    @Given("^(in less than '(\\d+)' seconds,)?( checking each '(\\d+)' seconds, )?I (create|update) calico network '(.+?)' so that the response( does not)? contains '(.+?)' based on '([^:]+?)'( as '(json|string|gov)')? with:$")
    public void calicoNetworkTimeout(Integer timeout, Integer wait, String operation, String networkId, String contains, String responseVal, String baseData, String type, DataTable modifications) throws Exception {

        // Retrieve data
        String retrievedData = commonspec.retrieveData(baseData, type);

        // Modify data
        commonspec.getLogger().debug("Modifying data {} as {}", retrievedData, type);
        String modifiedData = commonspec.modifyData(retrievedData, type, modifications);

        String endPoint = "/service/" + ThreadProperty.get("configuration_api_id") + "/network";
        String requestType = operation.equals("create") ? "PUT" : "POST";

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

        if (wait == null || timeout == null) {
            timeout = 0;
            wait = 0;
        }

        for (int i = 0; (i <= timeout); i += wait) {
            if (found && searchUntilContains) {
                break;
            }
            try {
                commonspec.getLogger().debug("Generating request {} to {} with data {} as {}", requestType, endPoint, modifiedData, type);
                response = commonspec.generateRequest(requestType, false, null, null, endPoint, modifiedData, type);
                commonspec.getLogger().debug("Saving response");
                commonspec.setResponse(requestType, response.get());
                commonspec.getLogger().debug("Checking response value");

                if (searchUntilContains) {
                    assertThat(commonspec.getResponse().getResponse()).containsPattern(pattern);
                    found = true;
                    timeout = i;
                } else {
                    assertThat(commonspec.getResponse().getResponse()).doesNotContain(responseVal);
                    found = false;
                    timeout = i;
                }
            } catch (AssertionError | Exception e) {
                if (!found) {
                    commonspec.getLogger().info("Response value not found after " + i + " seconds");
                } else {
                    commonspec.getLogger().info("Response value found after " + i + " seconds");
                }
                Thread.sleep(wait * 1000);
                if (e instanceof AssertionError) {
                    ex = (AssertionError) e;
                }
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
     * Delete calico network
     *
     * @param timeout
     * @param wait
     * @param networkId
     * @throws Exception
     */
    @Given("^(in less than '(\\d+)' seconds,)?( checking each '(\\d+)' seconds, )?I( force to)? delete calico network '(.+?)' so that the response( does not)? contains '(.+?)'$")
    public void deleteCalicoNetworkTimeout(Integer timeout, Integer wait, String force, String networkId, String contains, String responseVal) throws Exception {

        if (force == null && (networkId.equals("logs") || networkId.equals("stratio") || networkId.equals("metrics") || networkId.equals("stratio-shared"))) {
            throw new Exception("It is not possible deleting networks stratio, metrics, logs or stratio-shared");
        }
        String endPoint = "/service/" + ThreadProperty.get("configuration_api_id") + "/network/" + networkId;
        String requestType = "DELETE";

        if (wait == null || timeout == null) {
            timeout = 0;
            wait = 0;
        }

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
            try {
                commonspec.getLogger().debug("Generating request {} to {} with data {} as {}", requestType, endPoint, null, null);
                response = commonspec.generateRequest(requestType, false, null, null, endPoint, null, null);
                commonspec.getLogger().debug("Saving response");
                commonspec.setResponse(requestType, response.get());
                commonspec.getLogger().debug("Checking response value");

                if (searchUntilContains) {
                    assertThat(commonspec.getResponse().getResponse()).containsPattern(pattern);
                    found = true;
                    timeout = i;
                } else {
                    assertThat(commonspec.getResponse().getResponse()).doesNotContain(responseVal);
                    found = false;
                    timeout = i;
                }
            } catch (AssertionError | Exception e) {
                if (!found) {
                    commonspec.getLogger().info("Response value not found after " + i + " seconds");
                } else {
                    commonspec.getLogger().info("Response value found after " + i + " seconds");
                }
                Thread.sleep(wait * 1000);
                if (e instanceof AssertionError) {
                    ex = (AssertionError) e;
                }
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
     * Get service schema
     *
     * @param level     schema level
     * @param service   service name
     * @param model     service model
     * @param version   service version
     * @param envVar    environment variable to save response in
     * @param fileName  file name where response is saved
     * @throws Exception
     */
    @Given("^I get schema( with level '(\\d+)')? from service '(.+?)' with model '(.+?)' and version '(.+?)'( and save it in environment variable '(.*?)')?( and save it in file '(.*?)')?$")
    public void getServiceSchema(Integer level, String service, String model, String version, String envVar, String fileName) throws Exception {

        if (level == null) {
            level = 1;
        }

        String endPoint = "/service/" + ThreadProperty.get("deploy_api_id") + "/deploy/" + service + "/" + model + "/" + version + "/schema?enriched=true&level=" + level;
        Future<Response> response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null, "");
        commonspec.setResponse("GET", response.get());

        if (commonspec.getResponse().getStatusCode() != 200) {
            logger.error("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            throw new Exception("Request to endpoint: " + endPoint + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
        }

        String json = commonspec.getResponse().getResponse();

        if (envVar != null || fileName != null) {
            DcosSpec dcosSpec = new DcosSpec(commonspec);
            dcosSpec.convertJSONSchemaToJSON(json, envVar, fileName);
        }

    }

    private void writeInFile(String json, String fileName) throws Exception {

        // Create file (temporary) and set path to be accessible within test
        File tempDirectory = new File(System.getProperty("user.dir") + "/target/test-classes/");
        String absolutePathFile = tempDirectory.getAbsolutePath() + "/" + fileName;
        commonspec.getLogger().debug("Creating file {} in 'target/test-classes'", absolutePathFile);
        // Note that this Writer will delete the file if it exists
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(absolutePathFile), StandardCharsets.UTF_8));
        try {
            out.write(json);
        } catch (Exception e) {
            commonspec.getLogger().error("Custom file {} hasn't been created:\n{}", absolutePathFile, e.toString());
        } finally {
            out.close();
        }

        Assertions.assertThat(new File(absolutePathFile).isFile());
    }

    /**
     * Install service
     * @param service   service name
     * @param folder    folder where service are going to be installed
     * @param model     service model
     * @param version   service version
     * @param name      service instance name
     * @param tenant    tenant where to install service in
     * @param jsonFile  marathon json to deploy
     * @throws Exception
     */
    @Given("^I install service '(.+?)'( in folder '(.+?)')? with model '(.+?)' and version '(.+?)' and instance name '(.+?)' in tenant '(.+?)' using json '(.+?)'$")
    public void installServiceFromMarathonJson(String service, String folder, String model, String version, String name, String tenant, String jsonFile) throws Exception {
        String endPoint = "/service/" + ThreadProperty.get("deploy_api_id") + "/deploy/" + service + "/" + model + "/" + version + "/schema?tenantId=" + tenant;
        String data = this.commonspec.retrieveData(jsonFile, "json");

        Future<Response> response = commonspec.generateRequest("POST", true, null, null, endPoint, data, "json");
        commonspec.setResponse("POST", response.get());

        if (commonspec.getResponse().getStatusCode() != 202) {
            logger.error("Request to endpoint: " + endPoint + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            throw new Exception("Request to endpoint: " + endPoint + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
        }

        // Check Application in API
        RestSpec restSpec = new RestSpec(commonspec);

        String endPointStatus;
        if (ThreadProperty.get("cct-marathon-services_id") == null) {
            endPointStatus = "/service/" + ThreadProperty.get("deploy_api_id") + "/deploy/status/all";
        } else {
            endPointStatus = "/service/" + ThreadProperty.get("cct-marathon-services_id") + "/v1/services?tenant=" + tenant;
        }

        if (folder != null && folder.startsWith("/")) {
            folder = folder.substring(1);
        }
        if (folder != null && folder.endsWith("/")) {
            folder = folder.substring(folder.length() - 1);
        }

        String serviceName = "/" + name;
        if (folder != null) {
            serviceName = "/" + folder + "/" + name;
        }
        if (!"NONE".equals(tenant)) {
            serviceName = "/" + tenant + "/" + tenant + "-" + name;
            if (folder != null) {
                serviceName =  "/" + tenant + "/" + folder + "/" + tenant + "-" + name;
            }
        }

        restSpec.sendRequestTimeout(200, 20, "GET", endPointStatus, null, serviceName);
    }

    /**
     * Uninstall service from tenant
     *
     * @param service   service name
     * @param tenant    tenant where service is installed
     * @throws Exception
     */
    @Given("^I uninstall service '(.+?)' from tenant '(.+?)'$")
    public void uninstallService(String service, String tenant) throws Exception {
        String tenant_prefix = "";

        if (!"NONE".equals(tenant)) {
            tenant_prefix = tenant + "/" + tenant + "-";
        }

        String endPoint = "/service/" + ThreadProperty.get("deploy_api_id") + "/deploy/uninstall?app=" + tenant_prefix + service;

        Future<Response> response = commonspec.generateRequest("DELETE", true, null, null, endPoint, "", "json");
        commonspec.setResponse("DELETE", response.get());

        if (commonspec.getResponse().getStatusCode() != 202 && commonspec.getResponse().getStatusCode() != 200) {
            logger.error("Request to endpoint: " + endPoint + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            throw new Exception("Request to endpoint: " + endPoint + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
        }

        // Check service has disappeared
        RestSpec restSpec = new RestSpec(commonspec);

        String endPointStatus;
        if (ThreadProperty.get("cct-marathon-services_id") == null) {
            endPointStatus = "/service/" + ThreadProperty.get("deploy_api_id") + "/deploy/status/all";
        } else {
            endPointStatus = "/service/" + ThreadProperty.get("cct-marathon-services_id") + "/v1/services?tenant=" + tenant;
        }

        String serviceName = "/" + service;
        if (!"NONE".equals(tenant)) {
            serviceName = "/" + tenant + "/" + tenant + "-" + service;
        }
        restSpec.sendRequestTimeout(200, 20, "GET", endPointStatus, "does not", serviceName);

        // Check all resources have been freed
        DcosSpec dcosSpec = new DcosSpec(commonspec);
        dcosSpec.checkResources(serviceName);
    }

    @Given("^I upload rules file '(.+?)'( with priority '(.+?)')?( overriding version to '(.+?)')?")
    public void uploadRules(String rulesPath, String priority, String version) throws Exception {
        // Check file exists
        File rules = new File(rulesPath);
        Assertions.assertThat(rules.exists()).as("File: " + rulesPath + " does not exist.").isTrue();

        // Obtain endpoint
        if (ThreadProperty.get("deploy_api_id") == null) {
            fail("deploy_api_id variable is not set. Check deploy-api is installed and @dcos annotation is working properly.");
        }
        String endPointUpload = "/service/" + ThreadProperty.get("deploy_api_id") + "/knowledge/upload";

        // Obtain URL
        String restURL = "https://" + commonspec.getRestHost() + commonspec.getRestPort() + endPointUpload;

        // Form query parameters
        String headers = "-H \"accept: */*\" -H \"Content-Type: multipart/form-data\"";
        String forms = "-F \"file=@" + rulesPath + ";type=application/zip\"";

        if (priority == null) {
            priority = "0";
        }
        forms = forms + " -F \"priority=" + priority  + "\"";

        if (version != null) {
            forms = forms + " -F \"version=" + version + "\"";
        }

        String cookie = "-H \"Cookie:dcos-acs-auth-cookie=" + ThreadProperty.get("dcosAuthCookie") + "\"";
        String command = "curl -X POST -k " + cookie + " \"" + restURL + "\" " + headers + " " + forms;

        // Execute command
        commonspec.runLocalCommand(command);

        Assertions.assertThat(commonspec.getCommandExitStatus()).isEqualTo(0);
        Assertions.assertThat(commonspec.getCommandResult()).as("Not possible to upload rules: " + commonspec.getCommandResult()).doesNotContain("Error");
    }

    @Given("^I upload descriptors file '(.+?)'( overriding version to '(.+?)')?")
    public void uploadDescriptors(String descriptorsPath, String version) throws Exception {
        String headers = "";
        String forms = "";
        String op = "";

        // Check file exists
        File descriptors = new File(descriptorsPath);
        Assertions.assertThat(descriptors.exists()).as("File: " + descriptorsPath + " does not exist.").isTrue();

        // Obtain endpoint
        if (ThreadProperty.get("deploy_api_id") == null && ThreadProperty.get("cct-universe_id") == null) {
            fail("deploy_api_id variable and cct-universe_id are not set. Check deploy-api or cct-universe are installed and @dcos annotation is working properly.");
        }

        // Obtain cookie
        String cookie = "-H \"Cookie:dcos-acs-auth-cookie=" + ThreadProperty.get("dcosAuthCookie") + "\"";

        String endPointUpload = "";
        if (ThreadProperty.get("cct-universe_id") != null) {
            endPointUpload = "/service/" + ThreadProperty.get("cct-universe_id") + "/v1/descriptors";
            headers = "-H \"accept: application/json\" -H \"Content-Type: multipart/form-data\"";
            forms = "-F \"file=@" + descriptorsPath + ";type=application/zip\"";
            op = "PUT";
        } else {
            endPointUpload = "/service/" + ThreadProperty.get("deploy_api_id") + "/universe/upload";
            headers = "-H \"Content-Type: multipart/form-data\"";
            forms = "-F \"file=@" + descriptorsPath + "\"";

            if (version != null) {
                forms = forms + " -F \"version=" + version + "\"";
            }
            op = "POST";
        }

        // Obtain URL
        String restURL = "https://" + commonspec.getRestHost() + commonspec.getRestPort() + endPointUpload;

        // Form query
        String command = "curl -X " + op + " -k " + cookie + " \"" + restURL + "\" " + headers + " " + forms;

        // Execute command
        commonspec.runLocalCommand(command);

        String result = commonspec.getCommandResult();

        Assertions.assertThat(commonspec.getCommandExitStatus()).isEqualTo(0);

        if (ThreadProperty.get("cct-universe_id") != null) {
            String accepted = commonspec.getJSONPathString(result, "$.accepted", null);
            String rejected = commonspec.getJSONPathString(result, "$.rejected", null);

            if ("[]".equals(accepted)) {
                fail("No descriptors have been uploaded correctly: " + result);
            }

            if (!"[]".equals(rejected)) {
                fail("There are descriptors that have been rejected: " + rejected);
            }
        } else {
            String added = commonspec.getJSONPathString(result, "$.added", null);
            String error = commonspec.getJSONPathString(result, "$.error", null);
            String existing = commonspec.getJSONPathString(result, "$.existing", null);

            if ("[]".equals(added)) {
                fail("No descriptors have been uploaded correctly: " + result);
            }

            if (!"[]".equals(error)) {
                fail("There are descriptors that have been rejected: " + result);
            }

            if (!"[]".equals(existing)) {
                fail("There are descriptors that already exist: " + result);
            }
        }
    }
}