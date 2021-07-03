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
import com.stratio.qa.models.cct.deployApi.DeployedApp;
import com.stratio.qa.models.cct.deployApi.DeployedTask;
import com.stratio.qa.models.cct.deployApi.SandboxItem;
import com.stratio.qa.models.cct.marathonServiceApi.*;
import com.stratio.qa.models.mesos.MesosTask;
import com.stratio.qa.utils.CCTUtils;
import com.stratio.qa.utils.ThreadProperty;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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

    private static final int MAX_TASKS = 10000;

    CCTUtils cctUtils;

    RestSpec restSpec;
    /**
     * Generic constructor.
     *
     * @param spec object
     */
    public CCTSpec(CommonG spec) {
        this.commonspec = spec;
        restSpec = new RestSpec(spec);
        cctUtils = new CCTUtils(spec);
    }

    @When("^I get taskId for task '(.+?)' in service with id '(.+?)' from CCT and save the value in environment variable '(.+?)'$")
    public void getTaskId(String taskName, String serviceId, String envVar) throws Exception {
        if (ThreadProperty.get("cct-marathon-services_id") == null) {
            DeployedTask task = getServiceTaskFromDeployApi(serviceId, taskName);
            ThreadProperty.set(envVar, task.getId());
        } else {
            DeployedServiceTask task = getServiceTaskFromCctMarathonService(serviceId, taskName);
            ThreadProperty.set(envVar, task.getId());
        }
    }

    @When("^I get (internal )?host ip for task '(.+?)'( in position '(\\d+)')? in service with id '(.+?)' from CCT and save the value in environment variable '(.+?)'$")
    public void getHostIp(String internalIP, String taskName, Integer position, String serviceId, String envVar) throws Exception {
        // Set REST connection
        commonspec.setCCTConnection(null, null);
        if (ThreadProperty.get("cct-marathon-services_id") == null) {
            DeployedTask task = position != null ? getServiceTaskFromDeployApi(serviceId, taskName, position) : getServiceTaskFromDeployApi(serviceId, taskName);
            Assert.assertNotNull(task, "Error obtaining IP");
            ThreadProperty.set(envVar, internalIP != null ? task.getCalicoIP() : task.getHost());
        } else {
            DeployedServiceTask task = position != null ? getServiceTaskFromCctMarathonService(serviceId, taskName, position) : getServiceTaskFromCctMarathonService(serviceId, taskName);
            Assert.assertNotNull(task, "Error obtaining IP");
            ThreadProperty.set(envVar, internalIP != null ? task.getSecuredHost() : task.getHost());
        }
    }

    @Deprecated
    public void getHostIp(String taskName, String serviceId, String envVar) throws Exception {
        getHostIp(null, taskName, null, serviceId, envVar);
    }

    @Deprecated
    public void getHostIp(String internalIP, String taskName, String serviceId, String envVar) throws Exception {
        getHostIp(internalIP, taskName, null, serviceId, envVar);
    }

    private DeployedTask getServiceTaskFromDeployApi(String serviceId, String taskName) throws Exception {
        DeployedApp app = this.commonspec.deployApiClient.getDeployedApp(serviceId);
        return app.getTasks().stream()
                .filter(task -> task.getState().equals(MesosTask.Status.TASK_RUNNING.toString()))
                .filter(task -> task.getName().matches(taskName))
                .findFirst().orElse(null);
    }

    private DeployedTask getServiceTaskFromDeployApi(String serviceId, String taskName, int position) throws Exception {
        DeployedApp app = this.commonspec.deployApiClient.getDeployedApp(serviceId);
        return app.getTasks().stream()
                .filter(task -> task.getState().equals(MesosTask.Status.TASK_RUNNING.toString()))
                .filter(task -> task.getName().matches(taskName))
                .skip(position)
                .findFirst().orElse(null);
    }

    private DeployedTask getServiceTaskHostFromDeployApi(String serviceId, String host) throws Exception {
        DeployedApp app = this.commonspec.deployApiClient.getDeployedApp(serviceId);
        return app.getTasks().stream()
                .filter(task -> task.getState().equals(MesosTask.Status.TASK_RUNNING.toString()))
                .filter(task -> task.getHost().matches(host))
                .findFirst().orElse(null);
    }

    private DeployedServiceTask getServiceTaskFromCctMarathonService(String serviceId, String taskName) throws Exception {
        DeployedService service = this.commonspec.cctMarathonServiceClient.getService(serviceId, 1, CCTSpec.MAX_TASKS);
        return service.getTasks().stream()
                .filter(task -> task.getStatus().equals(TaskStatus.RUNNING))
                .filter(task -> task.getName().matches(taskName))
                .findFirst().orElse(null);
    }

    private DeployedServiceTask getServiceTaskFromCctMarathonService(String serviceId, String taskName, int position) throws Exception {
        DeployedService service = this.commonspec.cctMarathonServiceClient.getService(serviceId, 1, CCTSpec.MAX_TASKS);
        return service.getTasks().stream()
                .filter(task -> task.getStatus().equals(TaskStatus.RUNNING))
                .filter(task -> task.getName().matches(taskName))
                .skip(position)
                .findFirst().orElse(null);
    }

    private DeployedServiceTask getServiceTaskHostFromCctMarathonService(String serviceId, String host) throws Exception {
        DeployedService service = this.commonspec.cctMarathonServiceClient.getService(serviceId, 1, CCTSpec.MAX_TASKS);
        return service.getTasks().stream()
                .filter(task -> task.getStatus().equals(TaskStatus.RUNNING))
                .filter(task -> task.getHost().matches(host))
                .findFirst().orElse(null);
    }

    @When("^I get container name for task '(.+?)' in service with id '(.+?)' and save the value in environment variable '(.+?)'$")
    public void getMesosTaskContainerName(String taskName, String serviceId, String envVar) throws Exception {
        String taskId = "";
        if (ThreadProperty.get("cct-marathon-services_id") == null) {
            DeployedTask task = getServiceTaskFromDeployApi(serviceId, taskName);
            // Deploy-api from 0.11
            taskId = task.getId();
        } else {
            DeployedServiceTask task = getServiceTaskFromCctMarathonService(serviceId, taskName);
            taskId = task.getId();
        }

        MesosTask mesosTask = this.commonspec.mesosApiClient.getMesosTask(taskId).getTasks().get(0);
        String containerId = this.commonspec.mesosUtils.getMesosTaskContainerId(mesosTask);
        assertThat(containerId).as("Error searching containerId for mesos task: " + taskId).isNotNull();

        String containerName = "mesos-".concat(containerId);
        ThreadProperty.set(envVar, containerName);
    }

    /**
     * Download last lines from logs of a service/framework
     *
     * @param logType      : type of log enum value)
     * @param service      : service to obtain log from
     * @param taskNameOrID : task from service
     * @throws Exception
     */
    @Given("^I want to download '(stdout|stderr)' last '(\\d+)' lines of service '(.+?)' with task (name|ID|host|securedHost) '(.+?)'( in position '(\\d+)')?( in any state)?")
    public void downLoadLogsFromService(String logType, Integer lastLinesToRead, String service, String taskAttrType, String taskNameOrID, Integer position, String taskState) throws Exception {
        // Set REST connection
        commonspec.setCCTConnection(null, null);
        position = (position == null) ? 0 : position;
        String fileOutputName = position == 0 ? taskNameOrID + "." + logType : taskNameOrID + "." + logType + "." + position;
        String logOfTask = getLog(logType, lastLinesToRead, service, taskNameOrID, position, taskState, taskAttrType);
        Assert.assertNotNull(logOfTask, "Error downloading log file");
        Files.write(Paths.get(System.getProperty("user.dir") + "/target/test-classes/" + fileOutputName), logOfTask.getBytes());
    }

    /**
     * Read last lines from logs of a service/framework
     *
     * @param logType         : type of log enum value)
     * @param service         : service to obtain log from
     * @param taskNameOrID    : task from service
     * @param logToCheck      : expression to look for
     * @param lastLinesToRead : number of lines to check from the end
     * @throws Exception
     */
    @Given("^The '(stdout|stderr)' of service '(.+?)' with task (name|ID|host|securedHost) '(.+?)' contains '(.+?)' in the last '(\\d+)' lines$")
    public void readLogsFromService(String logType, String service, String taskAttrType, String taskNameOrID, String logToCheck, Integer lastLinesToRead) throws Exception {
        // Set REST connection
        commonspec.setCCTConnection(null, null);
        String logOfTask = getLog(logType, lastLinesToRead, service, taskNameOrID, 0, null, taskAttrType);
        Assert.assertNotNull(logOfTask, "Error downloading log file");
        if (!logOfTask.contains(logToCheck)) {
            Files.write(Paths.get(System.getProperty("user.dir") + "/target/test-classes/log.txt"), logOfTask.getBytes());
            fail("The log '" + logToCheck + "' is not contained in the task logs. It is saved in target/test-classes/log.txt");
        }
    }

    /**
     * Read last lines from logs of a service/framework
     *
     * @param timeout      : maximun waiting time
     * @param wait         : check interval
     * @param logType      : type of log enum value)
     * @param service      : service to obtain log from
     * @param taskNameOrID : task from service
     * @param logToCheck   : expression to look for
     * @throws Exception
     */
    @Given("^in less than '(\\d+)' seconds, checking each '(\\d+)' seconds, the '(stdout|stderr)' of service '(.+?)' with task (name|ID|host|securedHost) '(.+?)' contains '(.+?)'( in the last '(\\d+)' lines)?$")
    public void readLogsInLessEachFromService(Integer timeout, Integer wait, String logType, String service, String taskAttrType, String taskNameOrID, String logToCheck, Integer lastLinesToRead) throws Exception {
        // Set REST connection
        commonspec.setCCTConnection(null, null);
        lastLinesToRead = lastLinesToRead == null ? -1 : lastLinesToRead;
        String logOfTask = null;
        for (int x = 0; x <= timeout; x += wait) {
            try {
                logOfTask = getLog(logType, lastLinesToRead, service, taskNameOrID, 0, null, taskAttrType);
                if (logOfTask != null && logOfTask.contains(logToCheck)) {
                    break;
                }
            } catch (Exception e) {

            }
            commonspec.getLogger().info(logToCheck + " not found after " + x + " seconds");
            if (x < timeout) {
                Thread.sleep(wait * 1000);
            }
        }
        Assert.assertNotNull(logOfTask, "Error downloading log file");
        if (!logOfTask.contains(logToCheck)) {
            Files.write(Paths.get(System.getProperty("user.dir") + "/target/test-classes/log.txt"), logOfTask.getBytes());
            fail("The log '" + logToCheck + "' is not contained in the task logs after " + timeout + " seconds. Last log downloaded is saved in target/test-classes/log.txt");
        }
    }

    /**
     * Read last lines from logs of a service/framework
     *
     * @param timeout      : maximun waiting time
     * @param wait         : check interval
     * @param logType      : type of log enum value)
     * @param service      : service to obtain log from
     * @param taskNameOrID : task from service
     * @param logToCheck   : expression to look for
     * @throws Exception
     */
    @Given("^in less than '(\\d+)' seconds, checking each '(\\d+)' seconds, last '(\\d+)' lines of '(stdout|stderr)' log of service '(.+?)' with task (name|ID|host|securedHost) '(.+?)', modifying it with command '(.+?)' contains '(.+?)'$")
    public void readLogsModifiedInLessEachFromService(Integer timeout, Integer wait, Integer lastLinesToRead, String logType, String service, String taskAttrType, String taskNameOrID, String modifyingCommand, String logToCheck) throws Exception {
        // Set REST connection
        commonspec.setCCTConnection(null, null);
        String logOfTask = null;
        for (int x = 0; x <= timeout; x += wait) {
            try {
                logOfTask = getLog(logType, lastLinesToRead, service, taskNameOrID, 0, null, taskAttrType);
                if (logOfTask != null) {
                    Files.write(Paths.get(System.getProperty("user.dir") + "/target/test-classes/log.txt"), logOfTask.getBytes());
                    commonspec.runLocalCommand("cat target/test-classes/log.txt | " + modifyingCommand);
                    commonspec.getLogger().debug("Log result modified =  " + commonspec.getCommandResult());
                    if (commonspec.getCommandResult().contains(logToCheck)) {
                        break;
                    }
                }
            } catch (Exception e) {

            }
            commonspec.getLogger().info(logToCheck + " not found after " + x + " seconds");
            if (x < timeout) {
                Thread.sleep(wait * 1000);
            }
        }
        Assert.assertNotNull(logOfTask, "Error downloading log file");
        if (!commonspec.getCommandResult().contains(logToCheck)) {
            Files.write(Paths.get(System.getProperty("user.dir") + "/target/test-classes/log.txt"), logOfTask.getBytes());
            commonspec.getLogger().error("Last log result modified =  " + commonspec.getCommandResult());
            fail("The log '" + logToCheck + "' is not contained in the task logs after " + timeout + " seconds. Last log downloaded is saved in target/test-classes/log.txt");
        }
    }

    /**
     * Obtain last lines of log
     *
     * @param logType         stdout / stderr
     * @param lastLinesToRead Last lines to read in log
     * @param service         Service ID
     * @param taskAttr        Task name
     * @return Last 'lastLinesToRead' or null
     * @throws Exception
     */
    private String getLog(String logType, Integer lastLinesToRead, String service, String taskAttr, Integer position, String taskState, String taskAttrType) throws Exception {
        String logPath;
        if (ThreadProperty.get("cct-marathon-services_id") == null) {
            // Deploy-api
            String expectedTaskStatus = taskState == null && !taskAttrType.equals("ID") ? "TASK_RUNNING" : null;
            logPath = getLogPathFromDeployApi(logType, service, taskAttr, expectedTaskStatus, position, taskAttrType);
        } else {
            // Marathon-services
            TaskStatus expectedTaskStatus = taskState == null && !taskAttrType.equals("ID") ? TaskStatus.RUNNING : null;
            logPath = getLogPathFromMarathonServices(logType, service, taskAttr, expectedTaskStatus, position, taskAttrType);
        }
        if (logPath == null) {
            if (taskAttrType.equals("ID")) {
                logPath = generateMesosLogPath(taskAttr, logType);
            } else {
                String id = ThreadProperty.get("cct-marathon-services_id") == null ? getServiceTaskHostFromDeployApi(service, taskAttr).getId() : getServiceTaskHostFromCctMarathonService(service, taskAttr).getId();
                if (id != null) {
                    logPath = generateMesosLogPath(id, logType);
                }
            }
        }
        if (logPath == null) {
            return null;
        }
        commonspec.getLogger().debug("Log path: " + logPath);
        return readLogsFromMesos(logPath, lastLinesToRead);
    }

    private String generateMesosLogPath(String taskId, String logType) {
        try {
            MesosTask mesosTask = this.commonspec.mesosApiClient.getMesosTasks().getTasks().stream()
                    .filter(task -> task.getTaskId().equals(taskId) || task.getFrameworkId().equals(taskId))
                    .findFirst().orElse(null);
            String containerId = ((LinkedHashMap<String, String>) mesosTask.getStatuses().get(0).getContainerStatus().get("container_id")).get("value");
            return "/agent/" + mesosTask.getSlaveId() + "/files/read?path=/var/lib/mesos/slave/slaves/" + mesosTask.getSlaveId() + "/frameworks/" + mesosTask.getFrameworkId() + "/executors/" + mesosTask.getTaskId() + "/runs/" + containerId + "/" + logType;
        } catch (Exception e) {
            commonspec.getLogger().warn("Error generating mesos log path: " + e.toString());
        }
        return null;
    }

    /**
     * Obtain log path through deploy-api service
     *
     * @param logType  stdout / stderr
     * @param service  Service ID
     * @param taskAttr Task name
     * @return Log path or null
     * @throws Exception
     */
    private String getLogPathFromDeployApi(String logType, String service, String taskAttr, String expectedTaskStatus, Integer position, String taskAttrType) throws Exception {
        DeployedTask deployedTask = this.commonspec.deployApiClient.getDeployedApp(service).getTasks().stream()
                .filter(expectedTaskStatus != null ? task -> task.getState().equals(expectedTaskStatus) : task -> true)
                .filter(task -> taskAttrType.equals("ID") ? task.getId().matches(taskAttr) :
                        taskAttrType.equals("name") ? task.getName().matches(taskAttr) :
                                taskAttrType.equals("host") ? task.getHost().matches(taskAttr) : task.getCalicoIP().matches(taskAttr))
                .sorted(Comparator.comparing(DeployedTask::getTimestamp).reversed())
                .skip(position)
                .findFirst().orElse(null);
        if (deployedTask != null) {
            SandboxItem sandboxItem = this.commonspec.deployApiClient.getLogPaths(deployedTask.getId()).getList().stream()
                    .filter(log -> log.getAction().equals("read"))
                    .findFirst().orElse(null);
            if (sandboxItem != null && sandboxItem.getPath() != null) {
                return sandboxItem.getPath() + logType;
            }
            commonspec.getLogger().warn("Log path not found for task with name " + taskAttr + " and service " + service);
        } else {
            commonspec.getLogger().warn("No task found with name " + taskAttr + " for service " + service);
        }
        return null;
    }

    /**
     * Obtain log path through marathon-services service
     *
     * @param logType  stdout / stderr
     * @param service  Service ID
     * @param taskAttr Task name
     * @return Log path or null
     * @throws Exception
     */
    private String getLogPathFromMarathonServices(String logType, String service, String taskAttr, TaskStatus expectedTaskStatus, Integer position, String taskAttrType) throws Exception {
        DeployedServiceTask deployedServiceTask = this.commonspec.cctMarathonServiceClient.getService(service, 1, 50).getTasks().stream()
                .filter(expectedTaskStatus != null ? task -> task.getStatus().equals(expectedTaskStatus) : task -> true)
                .filter(task -> taskAttrType.equals("ID") ? task.getId().matches(taskAttr) :
                        taskAttrType.equals("name") ? task.getName().matches(taskAttr) :
                                taskAttrType.equals("host") ? task.getHost().matches(taskAttr) : task.getSecuredHost().matches(taskAttr))
                .sorted(Comparator.comparing(DeployedServiceTask::getTimestamp).reversed())
                .skip(position)
                .findFirst().orElse(null);
        if (deployedServiceTask != null) {
            TaskLog taskLog = this.commonspec.cctMarathonServiceClient.getLogPaths(deployedServiceTask.getId()).getContent().stream()
                    .filter(log -> log.getAction() == LogAction.READ)
                    .filter(log -> log.getName().equals(logType))
                    .findFirst().orElse(null);
            if (taskLog != null && taskLog.getPath() != null) {
                return taskLog.getPath() + logType;
            }
            commonspec.getLogger().warn("Log path not found for task with name " + taskAttr + " and service " + service);
        } else {
            commonspec.getLogger().warn("No task found with name " + taskAttr + " for service " + service);
        }
        return null;
    }

    /**
     * Read log from mesos
     *
     * @param path      : path of service to obtain logs from
     * @param lastLines : number of lines to read from the end
     * @return lines read
     * @throws Exception
     */
    public String readLogsFromMesos(String path, Integer lastLines) throws Exception {
        // Set REST connection
        commonspec.setCCTConnection(null, null);

        //obtain last offset
        Future<Response> response = null;
        response = commonspec.generateRequest("GET", false, null, null, path, "", null);
        if (response.get().getStatusCode() != 200) {
            throw new Exception("Request failed to endpoint: " + path + " with status code: " + response.get().getStatusCode());
        }
        JSONObject offSetJson = new JSONObject(response.get().getResponseBody());
        int offSet = offSetJson.getInt("offset");
        StringBuilder logs = new StringBuilder();
        int bytes = 50000;
        if (lastLines >= 0) {
            //Read 50000 bytes
            int lineCount = 0;
            for (int i = offSet; (i >= 0) && (lineCount <= lastLines); i = i - bytes) {
                String endPoint = path + "&offset=" + (i - bytes) + "&length=" + i;
                if (i < bytes) {
                    endPoint = path + "&offset=0&length=" + i;
                }
                logs.insert(0, readLogsFromMesosEndpoint(path, endPoint));
                lineCount = logs.toString().split("\n").length;
            }
        } else {
            for (int i = offSet; i >= 0; i = i - bytes) {
                String endPoint = path + "&offset=" + (i - bytes) + "&length=" + i;
                if (i < bytes) {
                    endPoint = path + "&offset=0&length=" + i;
                }
                logs.insert(0, readLogsFromMesosEndpoint(path, endPoint));
            }
        }
        String[] logsArray = logs.toString().split("\n");
        if (lastLines < 0) {
            return String.join("\n", logsArray).replaceAll("BDTEOL", "\\\\n").replaceAll("BDTTAB", "\\\\t");
        }
        return String.join("\n", Arrays.copyOfRange(logsArray, Math.max(logsArray.length - lastLines, 0), logsArray.length)).replaceAll("BDTEOL", "\\\\n").replaceAll("BDTTAB", "\\\\t");
    }

    private String readLogsFromMesosEndpoint(String path, String endPoint) throws Exception {
        commonspec.getLogger().debug("Downloading log from endpoint: " + endPoint);
        Future<Response> response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null);
        if (response.get().getStatusCode() != 200) {
            throw new Exception("Request failed to endpoint: " + path + " with status code: " + commonspec.getResponse().getStatusCode());
        }
        commonspec.setResponse("GET", response.get());
        JSONObject cctJsonResponse = new JSONObject(commonspec.getResponse().getResponse());
        return cctJsonResponse.getString("data").replaceAll("\\\\n", "BDTEOL").replaceAll("\\\\t", "BDTTAB");
    }

    /**
     * Teardown a service with deploy-api
     *
     * @param service : service to teardown
     * @param tenant  : tenant where service lives
     * @throws Exception
     */
    @Given("^I teardown the service '(.+?)' of tenant '(.+?)'")
    public void tearDownService(String service, String tenant) throws Exception {
        // Set REST connection
        commonspec.setCCTConnection(null, null);
        Assert.assertNotNull(ThreadProperty.get("deploy_api_id"), "deploy_api_id variable is not set. Check deploy-api is installed and @dcos annotation is working properly.");
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
        String key;
        if (ThreadProperty.get("cct-marathon-services_id") == null) {
            endPointStatus = "/service/" + ThreadProperty.get("deploy_api_id") + "/deploy/status/all";
            key = "\"serviceName\"";
        } else {
            endPointStatus = "/service/" + ThreadProperty.get("cct-marathon-services_id") + "/v1/services?tenant=" + tenant;
            key = "\"key\"";
        }

        String serviceName = "/" + service;
        if (!"NONE".equals(tenant)) {
            serviceName = "/" + tenant + "/" + tenant + "-" + service;
        }
        restSpec.sendRequestTimeout(200, 20, "GET", endPointStatus, null, "does not", key + ":" + "\"" + serviceName + "\"");

        // Check all resources have been freed
        this.checkResources(serviceName);
    }

    /**
     * Scale service from deploy-api
     *
     * @param service   : service to be scaled
     * @param instances : number of instance to scale to
     * @throws Exception
     */
    @Given("^I scale service '(.+?)' to '(\\d+)' instances")
    public void scaleService(String service, Integer instances) throws Exception {
        // Set REST connection
        commonspec.setCCTConnection(null, null);

        Assert.assertNotNull(ThreadProperty.get("deploy_api_id"), "deploy_api_id variable is not set. Check deploy-api is installed and @dcos annotation is working properly.");
        String endPoint = "/service/" + ThreadProperty.get("deploy_api_id") + "/deploy/scale?instances=" + instances + "&serviceName=" + service;
        Future<Response> response;
        response = commonspec.generateRequest("PUT", false, null, null, endPoint, "", null, "");
        commonspec.setResponse("PUT", response.get());

        if (commonspec.getResponse().getStatusCode() != 200 && commonspec.getResponse().getStatusCode() != 201) {
            logger.error("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            throw new Exception("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
        }
    }


    /**
     * Checks service status in Command Center
     *
     * @param timeout        : maximun waiting time
     * @param wait           : check interval
     * @param service        : service to obtain status from
     * @param numTasks       : expected number of tasks to be found
     * @param taskType       : type of tasks
     * @param expectedStatus Expected status (healthy|unhealthy|running|stopped)
     * @throws Exception
     */
    @Given("^in less than '(\\d+)' seconds, checking each '(\\d+)' seconds, I check that the service '(.+?)' in CCT with '(\\d+)' tasks of type '(.+?)' is in '(healthy|unhealthy|running|stopped)' status")
    public void checkServiceStatus(Integer timeout, Integer wait, String service, Integer numTasks, String taskType, String expectedStatus) throws Exception {
        // Set REST connection
        commonspec.setCCTConnection(null, null);

        String endPoint = "/service/deploy-api/deployments/service?instanceName=" + service;
        if (ThreadProperty.get("cct-marathon-services_id") != null) {
            endPoint = "/service/cct-marathon-services/v1/services/" + service + "?tsize=100";
        }
        boolean statusService = false;
        for (int i = 0; (i <= timeout) && (!statusService); i += wait) {
            try {
                Future<Response> response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null);
                commonspec.setResponse(endPoint, response.get());
                statusService = checkServiceStatusInResponse(expectedStatus, commonspec.getResponse().getResponse(), numTasks, taskType);
            } catch (Exception e) {
                commonspec.getLogger().warn("Error in request " + endPoint + " - " + e.toString());
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
     *
     * @param expectedStatus : expected status to be found
     * @param response       : response obtained from request
     * @param tasks          : number of tasks
     * @param name           : tasks name
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
        HashMap<String, Long> taskTimestampMap = new HashMap<>();
        HashMap<String, String> taskStatusMap = new HashMap<>();
        for (int i = 0; i < arrayOfTasks.length(); i++) {
            JSONObject task = arrayOfTasks.getJSONObject(i);
            if (task.getString("name").matches(regex_name)) {
                if (taskTimestampMap.get(task.getString("name")) != null) {
                    if (taskTimestampMap.get(task.getString("name")) < task.getLong("timestamp")) {
                        taskTimestampMap.put(task.getString("name"), task.getLong("timestamp"));
                        taskStatusMap.put(task.getString("name"), task.getString(key));
                    }
                } else {
                    taskTimestampMap.put(task.getString("name"), task.getLong("timestamp"));
                    taskStatusMap.put(task.getString("name"), task.getString(key));
                }
            }
        }
        for (Map.Entry taskStatus : taskStatusMap.entrySet()) {
            if (!((String) taskStatus.getValue()).equalsIgnoreCase(expectedStatus)) {
                commonspec.getLogger().warn("The status of " + taskStatus.getKey() + " is " + taskStatus.getValue());
                commonspec.getLogger().warn(" Expected status of " + taskStatus.getKey() + " is " + expectedStatus);
                return false;
            }
        }
        if (taskStatusMap.size() == tasks) {
            return true;
        }
        commonspec.getLogger().error("The number of tasks deployed: " + task_counter + " are not the expected ones: " + tasks);
        return false;
    }

    /**
     * Checks in Command Center service status
     *
     * @param timeout        : maximun waiting time
     * @param wait           : check interval
     * @param service        : service to be checked
     * @param numTasks       : expected number fo tasks
     * @param expectedStatus : Expected status (healthy|unhealthy|running|stopped)
     * @throws Exception
     */
    @Given("^in less than '(\\d+)' seconds, checking each '(\\d+)' seconds, I check in CCT that the service '(.+?)'( with number of tasks '(\\d+)')? is in '(healthy|unhealthy|running|stopped)' status$")
    public void checkServiceStatus(Integer timeout, Integer wait, String service, Integer numTasks, String expectedStatus) throws Exception {
        // Set REST connection
        commonspec.setCCTConnection(null, null);

        String endPoint = "/service/deploy-api/deployments/service?instanceName=" + service;
        boolean useMarathonServices = false;
        if (ThreadProperty.get("cct-marathon-services_id") != null) {
            endPoint = "/service/cct-marathon-services/v1/services/" + service + "?tsize=100";
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
     * @param expectedStatus      : Expected status (healthy|unhealthy)
     * @param response            : Command center response
     * @param useMarathonServices : True if cct-marathon-services is used in request, False if deploy-api is used in request
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
                case "running":
                    return status.equalsIgnoreCase("RUNNING");
                case "stopped":
                    return status.equalsIgnoreCase("SUSPENDED");
                default:
            }
        } else {
            switch (expectedStatus) {
                case "healthy":
                    return response.contains("\"healthy\":1");
                case "unhealthy":
                    return response.contains("\"healthy\":2");
                case "running":
                    return response.contains("\"status\":2");
                case "stopped":
                    return response.contains("\"status\":1");
                default:
            }
        }
        return false;
    }


    /**
     * Checks in Command Center response if the service tasks are deployed successfully
     *
     * @param response            : Command center response
     * @param numTasks            : Command center response
     * @param useMarathonServices : True if cct-marathon-services is used in request, False if deploy-api is used in request
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
     * @param path     : path to obtain info from
     * @param envVar   : thread variable where to save info (OPTIONAL)
     * @param fileName : file name where to save info (OPTIONAL)
     * @throws Exception
     */
    @Given("^I get info from global config with path '(.*?)'( and save it in environment variable '(.*?)')?( and save it in file '(.*?)')?$")
    public void infoFromGlobalConfig(String path, String envVar, String fileName) throws Exception {
        // Set REST connection
        commonspec.setCCTConnection(null, null);

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
     * @param envVar   : thread variable where to save info (OPTIONAL)
     * @param fileName : file name where to save info (OPTIONAL)
     * @throws Exception
     */
    @Given("^I get global configuration( and save it in environment variable '(.*?)')?( and save it in file '(.*?)')?$")
    public void getGlobalConfig(String envVar, String fileName) throws Exception {
        // Set REST connection
        commonspec.setCCTConnection(null, null);

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
     * @param envVar   : thread variable where to save info (OPTIONAL)
     * @param fileName : file name where to save info (OPTIONAL)
     * @throws Exception
     */
    @Given("^I get schema from global configuration( and save it in environment variable '(.*?)')?( and save it in file '(.*?)')?$")
    public void getSchemaGlobalConfig(String envVar, String fileName) throws Exception {
        // Set REST connection
        commonspec.setCCTConnection(null, null);

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
     * @param networkId : network id to get info from
     * @param envVar    : thread variable where to save info (OPTIONAL)
     * @param fileName  : file name where to save info (OPTIONAL)
     * @throws Exception
     */
    @Given("^I get network '(.*?)'( and save it in environment variable '(.*?)')?( and save it in file '(.*?)')?$")
    public void getNetworkById(String networkId, String envVar, String fileName) throws Exception {
        // Set REST connection
        commonspec.setCCTConnection(null, null);

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
     * @param envVar   : thread variable where to save info (OPTIONAL)
     * @param fileName : file name where to save info (OPTIONAL)
     * @throws Exception
     */
    @Given("^I get all networks( and save it in environment variable '(.*?)')?( and save it in file '(.*?)')?$")
    public void getAllNetworks(String envVar, String fileName) throws Exception {
        // Set REST connection
        commonspec.setCCTConnection(null, null);

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
     * @param path     : path to obtain configuration from
     * @param envVar   : thread variable where to save info (OPTIONAL)
     * @param fileName : file name where to save info (OPTIONAL)
     * @throws Exception
     */
    @Given("^I get path '(.*?)' from Mesos configuration( and save it in environment variable '(.*?)')?( and save it in file '(.*?)')?$")
    public void getMesosConfiguration(String path, String envVar, String fileName) throws Exception {
        // Set REST connection
        commonspec.setCCTConnection(null, null);

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
     * @param timeout       : maximun waiting time
     * @param wait          : check interval
     * @param baseData      : path to file containing the schema to be used
     * @param type          : element to read from file (element should contain a json)
     * @param modifications : DataTable containing the modifications to be done to the
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
        // Set REST connection
        commonspec.setCCTConnection(null, null);

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
     * @param timeout   : maximun waiting time
     * @param wait      : check interval
     * @param networkId : id of the network to delete
     * @throws Exception
     */
    @Given("^(in less than '(\\d+)' seconds,)?( checking each '(\\d+)' seconds, )?I( force to)? delete calico network '(.+?)' so that the response( does not)? contains '(.+?)'$")
    public void deleteCalicoNetworkTimeout(Integer timeout, Integer wait, String force, String networkId, String contains, String responseVal) throws Exception {

        if (force == null && (networkId.equals("logs") || networkId.equals("stratio") || networkId.equals("metrics") || networkId.equals("stratio-shared"))) {
            throw new Exception("It is not possible deleting networks stratio, metrics, logs or stratio-shared");
        }

        // Set REST connection
        commonspec.setCCTConnection(null, null);

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
     * @param level    : schema level
     * @param service  : service name
     * @param model    : service model
     * @param version  : service version
     * @param envVar   : environment variable to save response in
     * @param fileName : file name where response is saved
     * @throws Exception
     */
    @Given("^I get schema( with level '(\\d+)')? from service '(.+?)' with model '(.+?)' and version '(.+?)'( and save it in environment variable '(.*?)')?( and save it in file '(.*?)')?$")
    public void getServiceSchema(Integer level, String service, String model, String version, String envVar, String fileName) throws Exception {
        if (ThreadProperty.get("isKeosEnv") != null && ThreadProperty.get("isKeosEnv").equals("true")) {
            getServiceSchemaKeos(service, model, version, envVar, fileName);
        } else {
            getServiceSchemaDcos(level, service, model, version, envVar, fileName);
        }
    }

    private void getServiceSchemaDcos(Integer level, String service, String model, String version, String envVar, String fileName) throws Exception {
        // Set REST connection
        commonspec.setCCTConnection(null, null);

        if (level == null) {
            level = 1;
        }
        String endPoint = "/service/" + ThreadProperty.get("deploy_api_id") + "/deploy/" + service + "/" + model + "/" + version + "/schema?enriched=true&level=" + level;
        Future<Response> response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null);
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

    private void getServiceSchemaKeos(String service, String model, String version, String envVar, String fileName) throws Exception {
        // Set REST connection
        commonspec.setCCTConnection(null, null);

        String endPoint = "/cct-universe-service/v1/descriptors/" + service + "/" + model + "/" + version;
        Future<Response> response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null);
        commonspec.setResponse("GET", response.get());

        if (commonspec.getResponse().getStatusCode() != 200) {
            logger.error("Request failed to endpoint: " + endPoint + " with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            throw new Exception("Request to endpoint: " + endPoint + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
        }

        String json = commonspec.getResponse().getResponse();

        if (envVar != null || fileName != null) {
            KeosSpec keosSpec = new KeosSpec(commonspec);
            JSONObject jsonSchema = new JSONObject();
            jsonSchema.put("service", service);
            jsonSchema.put("model", model);
            jsonSchema.put("version", version);
            keosSpec.convertDescriptorToK8sJsonSchema(json, jsonSchema.toString(), envVar, fileName);
        }
    }

    /**
     * Install service
     *
     * @param service  : service name
     * @param folder   : folder where service are going to be installed
     * @param model    : service model
     * @param version  : service version
     * @param name     : service instance name
     * @param tenant   : tenant where to install service in
     * @param jsonFile : marathon json to deploy
     * @throws Exception
     */
    @Given("^I install service '(.+?)'( in folder '(.+?)')?( with model '(.+?)')?( and version '(.+?)')?( and instance name '(.+?)')?( in tenant '(.+?)')?( in namespace '(.+?)')? using json '(.+?)'$")
    public void installServiceFromMarathonJson(String service, String folder, String model, String version, String name, String tenant, String namespace, String jsonFile) throws Exception {
        if (ThreadProperty.get("isKeosEnv") != null && ThreadProperty.get("isKeosEnv").equals("true")) {
            installServiceFromCCTKeos(jsonFile, tenant, namespace);
        } else {
            installServiceFromCCTDcos(service, folder, model, version, name, tenant, jsonFile);
        }
    }

    private void installServiceFromCCTDcos(String service, String folder, String model, String version, String name, String tenant, String jsonFile) throws Exception {
        // Set REST connection
        commonspec.setCCTConnection(null, null);
        if (model == null || version == null || name == null) {
            fail("Model, version and instance name are mandatory");
        }
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
            String[] installer_version = ThreadProperty.get("EOS_SCHEMA_VERSION").split("\\.");
            if (Integer.parseInt(installer_version[0]) < 1) {
                endPointStatus = "/service/" + ThreadProperty.get("cct-marathon-services_id") + "/v1/services";
            } else {
                endPointStatus = "/service/" + ThreadProperty.get("cct-marathon-services_id") + "/v1/services?tenant=" + tenant;
            }
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
                serviceName = "/" + tenant + "/" + folder + "/" + tenant + "-" + name;
            }
        }

        restSpec.sendRequestTimeout(200, 20, "GET", endPointStatus, null, null, serviceName);
    }

    private void installServiceFromCCTKeos(String jsonFile, String tenant, String namespace) throws Exception {
        // Set REST connection
        commonspec.setCCTConnection(null, null);

        String endPoint = "/cct-orchestrator-service/v1/install";
        if (tenant != null) {
            endPoint += "?tenant=" + tenant;
        }
        String data = this.commonspec.retrieveData(jsonFile, "json");
        if (namespace != null) {
            List<List<String>> rawData = Arrays.asList(
                    Arrays.asList("$.deployment.general.k8sNamespace", "ADD", namespace, "string")
            );
            DataTable modifications = DataTable.create(rawData);
            data = this.commonspec.modifyData(data, "json", modifications);
        }

        Future<Response> response = commonspec.generateRequest("POST", true, null, null, endPoint, data, "json");
        commonspec.setResponse("POST", response.get());

        if (commonspec.getResponse().getStatusCode() != 200) {
            logger.error("Request to endpoint: " + endPoint + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            throw new Exception("Request to endpoint: " + endPoint + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
        }

        //TODO Check application status in KEOS
    }

    /**
     * Uninstall service from tenant
     *
     * @param service : service name
     * @param tenant  : tenant where service is installed
     * @throws Exception
     */
    @Given("^I uninstall service '(.+?)'( in folder '(.+?)')? from tenant '(.+?)'$")
    public void uninstallService(String service, String folder, String tenant) throws Exception {
        if (folder != null && folder.startsWith("/")) {
            folder = folder.substring(1);
        }
        if (folder != null && folder.endsWith("/")) {
            folder = folder.substring(folder.length() - 1);
        }

        String serviceName = service;
        if (folder != null) {
            serviceName = folder + "/" + service;
        }
        if (!"NONE".equals(tenant)) {
            serviceName = tenant + "/" + tenant + "-" + service;
            if (folder != null) {
                serviceName = tenant + "/" + folder + "/" + tenant + "-" + service;
            }
        }

        // Set REST connection
        commonspec.setCCTConnection(null, null);

        String endPoint = "/service/" + ThreadProperty.get("deploy_api_id") + "/deploy/uninstall?app=" + serviceName;
        Future<Response> response = commonspec.generateRequest("DELETE", true, null, null, endPoint, "", "json");
        commonspec.setResponse("DELETE", response.get());

        if (commonspec.getResponse().getStatusCode() != 202 && commonspec.getResponse().getStatusCode() != 200) {
            logger.error("Request to endpoint: " + endPoint + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            throw new Exception("Request to endpoint: " + endPoint + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
        }

        // Check service has disappeared
        RestSpec restSpec = new RestSpec(commonspec);

        String endPointStatus;
        String key;
        if (ThreadProperty.get("cct-marathon-services_id") == null) {
            endPointStatus = "/service/" + ThreadProperty.get("deploy_api_id") + "/deploy/status/all";
            key = "\"serviceName\"";
        } else {
            endPointStatus = "/service/" + ThreadProperty.get("cct-marathon-services_id") + "/v1/services?tenant=" + tenant;
            key = "\"id\"";
        }

        restSpec.sendRequestTimeout(200, 20, "GET", endPointStatus, null, "does not", key + ":" + "\"" + serviceName + "\"");
        // Check all resources have been freed
        this.checkResources(serviceName);
    }

    /**
     * Uninstall service from tenant
     *
     * @param service : service name
     * @param tenant  : tenant where service is installed
     * @throws Exception
     */
    @Given("^I uninstall deployment '(.+?)'( from tenant '(.+?)')?( in namespace '(.+?)')? with schema located at file '(.+?)'$")
    public void uninstallServiceKeos(String service, String tenant, String namespace, String jsonFile) throws Exception {
        JSONObject schemaJson = new JSONObject(this.commonspec.retrieveData(jsonFile, "json"));
        schemaJson.put("applicationId", service + "." + tenant);
        if (namespace != null) {
            ((JSONObject) ((JSONObject) schemaJson.get("deployment")).get("general")).put("k8sNamespace", namespace);
        }
        // Set REST connection
        commonspec.setCCTConnection(null, null);

        String endPoint = "/cct-orchestrator-service/v1/uninstall";
        if (tenant != null) {
            endPoint += "?tenant=" + tenant;
        }
        Future<Response> response = commonspec.generateRequest("POST", true, null, null, endPoint, schemaJson.toString(), "json");
        commonspec.setResponse("POST", response.get());

        if (commonspec.getResponse().getStatusCode() != 202 && commonspec.getResponse().getStatusCode() != 200) {
            logger.error("Request to endpoint: " + endPoint + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            throw new Exception("Request to endpoint: " + endPoint + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
        }

        // TODO Check service is deleted successfully
    }

    /**
     * Upload rules
     *
     * @param rulesPath : path to rules zip file
     * @param priority  : priority to assign to the rules (OPTIONAL)
     * @param version   : version to use for rules (OPTIONAL)
     * @throws Exception
     */
    @Given("^I upload rules file '(.+?)'( with priority '(.+?)')?( overriding version to '(.+?)')?")
    public void uploadRules(String rulesPath, String priority, String version) throws Exception {
        // Check file exists
        File rules = new File(rulesPath);
        Assertions.assertThat(rules.exists()).as("File: " + rulesPath + " does not exist.").isTrue();

        // Set REST connection
        commonspec.setCCTConnection(null, null);

        // Obtain endpoint
        Assert.assertNotNull(ThreadProperty.get("deploy_api_id"), "deploy_api_id variable is not set. Check deploy-api is installed and @dcos annotation is working properly.");
        String endPointUpload = "/service/" + ThreadProperty.get("deploy_api_id") + "/knowledge/upload";

        // Obtain URL
        String restURL = "https://" + commonspec.getRestHost() + commonspec.getRestPort() + endPointUpload;

        // Form query parameters
        String headers = "-H \"accept: */*\" -H \"Content-Type: multipart/form-data\"";
        String forms = "-F \"file=@" + rulesPath + ";type=application/zip\"";

        if (priority == null) {
            priority = "0";
        }
        forms = forms + " -F \"priority=" + priority + "\"";

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

    /**
     * Upload descriptors
     *
     * @param descriptorsPath : path to descriptors zip file
     * @param version         : version to use for rules (OPTIONAL)
     * @throws Exception
     */
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

        // Set REST connection
        commonspec.setCCTConnection(null, null);

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

    /**
     * Update a deployed service
     *
     * @param serviceName   : name of the service to be updated
     * @param folder        : name of the folder where service is deployed (OPTIONAL)
     * @param tenant        : tenant where service is deployed
     * @param version       : version of the deployed service
     * @param modifications : modifications to perform in the deployed json
     * @throws Exception
     */
    @Given("^I update service '(.+?)'( in folder '(.+?)')?( in tenant '(.+?)')?( in namespace '(.+?)')?( based on version '(.+?)')?( based on json '(.+?)')? with:$")
    public void updateCCTService(String serviceName, String folder, String tenant, String namespace, String version, String jsonFile, DataTable modifications) throws Exception {
        if (ThreadProperty.get("isKeosEnv") != null && ThreadProperty.get("isKeosEnv").equals("true")) {
            updateCCTServiceKeos(jsonFile, tenant, namespace, modifications);
        } else {
            updateCCTServiceDcos(serviceName, folder, tenant, version, jsonFile, modifications);
        }
    }

    private void updateCCTServiceKeos(String jsonFile, String tenant, String namespace, DataTable modifications) throws Exception {
        // Set REST connection
        commonspec.setCCTConnection(null, null);

        String endPoint = "/cct-orchestrator-service/v1/update";
        if (tenant != null) {
            endPoint += "?tenant=" + tenant;
        }
        String data = this.commonspec.retrieveData(jsonFile, "json");
        if (namespace != null) {
            List<List<String>> rawData = Arrays.asList(
                    Arrays.asList("$.deployment.general.k8sNamespace", "ADD", namespace, "string")
            );
            DataTable modificationsAux = DataTable.create(rawData);
            data = this.commonspec.modifyData(data, "json", modificationsAux);
        }

        String modifiedData;
        if (modifications != null) {
            modifiedData = commonspec.modifyData(data, "json", modifications);
        } else {
            modifiedData = data;
        }

        Future<Response> response = commonspec.generateRequest("POST", true, null, null, endPoint, modifiedData, "json");
        commonspec.setResponse("POST", response.get());

        if (commonspec.getResponse().getStatusCode() != 200) {
            logger.error("Request to endpoint: " + endPoint + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            throw new Exception("Request to endpoint: " + endPoint + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
        }

        //TODO Check application status in KEOS
    }

    private void updateCCTServiceDcos(String serviceName, String folder, String tenant, String version, String jsonFile, DataTable modifications) throws Exception {
        Assert.assertNotNull(ThreadProperty.get("deploy_api_id"), "deploy_api_id variable is not set. Check deploy-api is installed and @dcos annotation is working properly.");

        // obtain service name
        if (folder != null && folder.startsWith("/")) {
            folder = folder.substring(1);
        }
        if (folder != null && folder.endsWith("/")) {
            folder = folder.substring(folder.length() - 1);
        }

        String service = serviceName;
        if (folder != null) {
            service = folder + "/" + serviceName;
        }
        if (!"NONE".equals(tenant)) {
            service = tenant + "/" + tenant + "-" + serviceName;
            if (folder != null) {
                service = tenant + "/" + folder + "/" + tenant + "-" + serviceName;
            }
        }

        // Check service is running
        try {
            checkServiceStatus(10, 1, service, null, "running");
        } catch (Exception e) {
            logger.error("Service: " + service + " is not deployed in cluster.");
            throw e;
        }

        String deployedJson = "";

        if (version == null && jsonFile == null || version != null && jsonFile != null) {
            throw new Exception("Version or json file must be defined (but NOT both)");
        }

        // Set REST connection
        commonspec.setCCTConnection(null, null);

        if (version != null) {
            // Obtain deployed service json
            String endpointJson = "/service/" + ThreadProperty.get("deploy_api_id") + "/update/" + service + "?version=" + version;
            Future<Response> responseJson = commonspec.generateRequest("GET", true, null, null, endpointJson, "", "json");
            commonspec.setResponse("GET", responseJson.get());

            if (commonspec.getResponse().getStatusCode() != 200) {
                logger.error("Request to endpoint: " + endpointJson + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
                throw new Exception("Request to endpoint: " + endpointJson + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            }

            // Modify json according to provided changes
            deployedJson = commonspec.getResponse().getResponse();

        }

        if (jsonFile != null) {
            deployedJson = this.commonspec.retrieveData(jsonFile, "json");
        }

        String modifiedData;
        if (modifications != null) {
            modifiedData = commonspec.modifyData(deployedJson, "json", modifications);
        } else {
            modifiedData = deployedJson;
        }

        // Deploy new json
        String endpointUpdate = "/service/" + ThreadProperty.get("deploy_api_id") + "/update/" + service;
        Future<Response> responseUpdate = commonspec.generateRequest("PUT", true, null, null, endpointUpdate, modifiedData, "json");
        commonspec.setResponse("PUT", responseUpdate.get());

        if (commonspec.getResponse().getStatusCode() != 202) {
            logger.error("Request to endpoint: " + endpointUpdate + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            throw new Exception("Request to endpoint: " + endpointUpdate + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
        }
    }

    /**
     * Update a deployed service
     *
     * @param serviceName : name of the service to be updated
     * @param folder      : name of the folder where service is deployed (OPTIONAL)
     * @param tenant      : tenant where service is deployed
     * @param version     : version of the deployed service
     * @throws Exception
     */
    @Given("^I update service '(.+?)'( in folder '(.+?)')?( in tenant '(.+?)')?( in namespace '(.+?)')?( based on version '(.+?)')?( based on json '(.+?)')?$")
    public void updateCCTService(String serviceName, String folder, String tenant, String namespace, String version, String jsonFile) throws Exception {
        updateCCTService(serviceName, folder, tenant, namespace, version, jsonFile, null);
    }

    @Given("^I upload descriptor for service '(.+?)', model '(.+?)' version '(.+?)' based on '(.+?)'$")
    public void uploadCCTDescriptor(String service, String model, String version, String originalJson) throws Exception {
        uploadCCTDescriptor(service, model, version, originalJson, null);
    }

    /**
     * Upload a descriptor
     *
     * @param service       : name of the descriptor to be updated
     * @param model         : model of the descriptor
     * @param version       : version of the descriptor
     * @param originalJson  : base descriptor json
     * @param modifications : modifications to perform in the descriptor json
     * @throws Exception
     */
    @Given("^I upload descriptor for service '(.+?)', model '(.+?)' version '(.+?)' based on '(.+?)' with:$")
    public void uploadCCTDescriptor(String service, String model, String version, String originalJson, DataTable modifications) throws Exception {
        String endpoint;
        String op;

        if (ThreadProperty.get("isKeosEnv") != null && ThreadProperty.get("isKeosEnv").equals("true")) {
            endpoint = "/cct-universe-service/v1/descriptors/" + service + "/" + model + "/" + version;
            op = "PUT";
        } else {
            // Obtain endpoint
            if (ThreadProperty.get("deploy_api_id") == null && ThreadProperty.get("cct-universe_id") == null) {
                fail("deploy_api_id variable and cct-universe_id are not set. Check deploy-api or cct-universe are installed and @dcos annotation is working properly.");
            }
            if (ThreadProperty.get("cct-universe_id") != null) {
                endpoint = "/service/" + ThreadProperty.get("cct-universe_id") + "/v1/descriptors/" + service + "/" + model + "/" + version;
                op = "PUT";
            } else {
                endpoint = "/service/" + ThreadProperty.get("deploy_api_id") + "/universe/" + service + "/" + model + "/" + version + "/descriptor";
                op = "POST";
            }
        }

        // Set REST connection
        commonspec.setCCTConnection(null, null);

        // Retrieve data
        String retrievedData = commonspec.retrieveData(originalJson, "json");
        // Modify json
        String modifiedData = modifications != null ? commonspec.modifyData(retrievedData, "json", modifications) : retrievedData;

        // Update version and model
        List<List<String>> rawData = Arrays.asList(
                Arrays.asList("$.data.model", "UPDATE", model, "string"),
                Arrays.asList("$.data.version", "UPDATE", version, "string")
        );
        DataTable modificationsAux = DataTable.create(rawData);
        modifiedData = this.commonspec.modifyData(modifiedData, "json", modificationsAux);

        // Upload new descriptor
        Future<Response> responseUpdate = commonspec.generateRequest(op, true, null, null, endpoint, modifiedData, "json");
        commonspec.setResponse(op, responseUpdate.get());

        if (commonspec.getResponse().getStatusCode() != 200 && commonspec.getResponse().getStatusCode() != 201) {
            logger.error("Upload descriptor: " + endpoint + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            throw new Exception("Upload descriptor: " + endpoint + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
        }
    }

    /**
     * Update a descriptor
     *
     * @param service       : name of the descriptor to be updated
     * @param model         : model of the descriptor
     * @param version       : version of the descriptor
     * @param originalJson  : base descriptor json
     * @param modifications : modifications to perform in the descriptor json
     * @throws Exception
     */
    @Given("^I update descriptor for service '(.+?)', model '(.+?)' version '(.+?)' based on '(.+?)' with:$")
    public void updateCCTDescriptor(String service, String model, String version, String originalJson, DataTable modifications) throws Exception {
        String endpoint;

        if (ThreadProperty.get("isKeosEnv") != null && ThreadProperty.get("isKeosEnv").equals("true")) {
            endpoint = "/cct-universe-service/v1/descriptors/" + service + "/" + model + "/" + version;
        } else {
            // Obtain endpoint
            if (ThreadProperty.get("deploy_api_id") == null && ThreadProperty.get("cct-universe_id") == null) {
                fail("deploy_api_id variable and cct-universe_id are not set. Check deploy-api or cct-universe are installed and @dcos annotation is working properly.");
            }
            if (ThreadProperty.get("cct-universe_id") != null) {
                endpoint = "/service/" + ThreadProperty.get("cct-universe_id") + "/v1/descriptors/" + service + "/" + model + "/" + version;
            } else {
                endpoint = "/service/" + ThreadProperty.get("deploy_api_id") + "/universe/" + service + "/" + model + "/" + version + "/descriptor";
            }
        }

        // Set REST connection
        commonspec.setCCTConnection(null, null);

        // Retrieve data
        String retrievedData = commonspec.retrieveData(originalJson, "json");
        // Modify json
        String modifiedData = modifications != null ? commonspec.modifyData(retrievedData, "json", modifications) : retrievedData;

        // Update descriptor
        Future<Response> responseUpdate = commonspec.generateRequest("PUT", true, null, null, endpoint, modifiedData, "json");
        commonspec.setResponse("PUT", responseUpdate.get());

        if (commonspec.getResponse().getStatusCode() != 200 && commonspec.getResponse().getStatusCode() != 201) {
            logger.error("Update descriptor: " + endpoint + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            throw new Exception("Update descriptor: " + endpoint + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
        }
    }

    /**
     * Delete a descriptor
     *
     * @param service : name of the descriptor to be updated
     * @param model   : model of the descriptor
     * @param version : version of the descriptor
     * @throws Exception
     */
    @Given("^I delete descriptor for service '(.+?)', model '(.+?)' version '(.+?)'$")
    public void deleteCCTDescriptor(String service, String model, String version) throws Exception {
        String endpoint;

        if (ThreadProperty.get("isKeosEnv") != null && ThreadProperty.get("isKeosEnv").equals("true")) {
            endpoint = "/cct-universe-service/v1/descriptors/" + service + "/" + model + "/" + version;
        } else {
            // Obtain endpoint
            if (ThreadProperty.get("deploy_api_id") == null && ThreadProperty.get("cct-universe_id") == null) {
                fail("deploy_api_id variable and cct-universe_id are not set. Check deploy-api or cct-universe are installed and @dcos annotation is working properly.");
            }
            if (ThreadProperty.get("cct-universe_id") != null) {
                endpoint = "/service/" + ThreadProperty.get("cct-universe_id") + "/v1/descriptors/" + service + "/" + model + "/" + version;
            } else {
                endpoint = "/service/" + ThreadProperty.get("deploy_api_id") + "/universe/" + service + "/" + model + "/" + version + "/descriptor";
            }
        }

        // Set REST connection
        commonspec.setCCTConnection(null, null);

        // Delete descriptor
        Future<Response> responseUpdate = commonspec.generateRequest("DELETE", true, null, null, endpoint, "", "json");
        commonspec.setResponse("DELETE", responseUpdate.get());

        if (commonspec.getResponse().getStatusCode() != 200 && commonspec.getResponse().getStatusCode() != 201 && commonspec.getResponse().getStatusCode() != 204) {
            logger.error("Delete descriptor: " + endpoint + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            throw new Exception("Delete descriptor: " + endpoint + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
        }
    }

    /**
     * Read value from centralized configuration path
     *
     * @param path   : path to read value from (separated with '/')
     * @param envVar : environment variable where to store the read value
     * @throws Exception
     */
    @When("^I read value in path '(.+?)' from central configuration and save it in environment variable '(.+?)'$")
    public void readValueCentralConfig(String path, String envVar) throws Exception {
        Assert.assertNotNull(ThreadProperty.get("configuration_api_id"), "configuration_api_id variable is not set. Check configuration-api is installed and @dcos annotation is working properly.");

        // Set REST connection
        commonspec.setCCTConnection(null, null);

        String fullPath = "/dcs/v1/fabric" + path;
        String endpoint = "/service/" + ThreadProperty.get("configuration_api_id") + "/etcd?path=" + fullPath;

        Future<Response> responseETCD = commonspec.generateRequest("GET", false, null, null, endpoint, "", null);
        commonspec.setResponse("GET", responseETCD.get());

        if (commonspec.getResponse().getStatusCode() != 200) {
            logger.error("Obtain info from ETCD: " + endpoint + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
            throw new Exception("Obtain info from ETCD: " + endpoint + " failed with status code: " + commonspec.getResponse().getStatusCode() + " and response: " + commonspec.getResponse().getResponse());
        }

        ThreadProperty.set(envVar, commonspec.getResponse().getResponse());
    }

    @Deprecated
    public void createSecret(String force, String secretType, String secret, String withOrWithout, String path, String cn, String name, String alt, String organizationName, String principal, String realm, String user, String password) throws Exception {
        createSecret(force, secretType, secret, withOrWithout, path, cn, name, alt, organizationName, principal, realm, user, password, null);
    }

    /**
     * Create secret
     *
     * @param force
     * @param secretType
     * @param secret
     * @param withOrWithout
     * @param path
     * @param cn
     * @param name
     * @param alt
     * @param organizationName
     * @param principal
     * @param realm
     * @param user
     * @param password
     * @throws Exception
     */
    @When("^I( force)? create '(certificate|keytab|password|password_nouser|password_custom)' '(.+?)' using deploy-api (with|without) parameters( path '(.+?)')?( cn '(.+?)')?( name '(.+?)')?( alt '(.+?)')?( organization '(.+?)')?( principal '(.+?)')?( realm '(.+?)')?( user '(.+?)')?( password '(.+?)')?( customPasswordContentFile '(.+?)')?$")
    public void createSecret(String force, String secretType, String secret, String withOrWithout, String path, String cn, String name, String alt, String organizationName, String principal, String realm, String user, String password, String customPasswordContentFile) throws Exception {
        if (ThreadProperty.get("isKeosEnv") != null && ThreadProperty.get("isKeosEnv").equals("true")) {
            createSecretKeos(force, secretType, secret, withOrWithout, path, cn, name, alt, organizationName, principal, realm, user, password, customPasswordContentFile);
        } else {
            String baseUrl = "/service/" + ThreadProperty.get("deploy_api_id") + "/secrets";
            String secretTypeAux;
            String urlParams;

            // Set REST connection
            commonspec.setCCTConnection(null, null);

            switch (secretType) {
                case "certificate":
                    urlParams = getCertificateUrlParams(secret, path, cn, name, alt, organizationName);
                    secretTypeAux = "certificates";
                    break;
                case "keytab":
                    urlParams = getKeytabUrlParams(secret, path, name, principal, realm);
                    secretTypeAux = "kerberos";
                    break;
                case "password":
                    urlParams = getPasswordUrlParams(secret, path, name, user, password);
                    secretTypeAux = "passwords";
                    break;
                default:
                    urlParams = "";
                    secretTypeAux = "default";
            }
            if (force != null) {
                String pathAux = path != null ? path.replaceAll("/", "%2F") + "%2F" + secret : "%2Fuserland%2F" + secretTypeAux + "%2F" + secret;
                restSpec.sendRequestNoDataTable("DELETE", baseUrl + "?path=" + pathAux, null, null, null);
            }
            if (!secretType.equals("password_nouser") && !secretType.equals("password_custom")) {
                restSpec.sendRequestNoDataTable("POST", baseUrl + "/" + secretType + urlParams, null, null, null);
            } else {
                String pathAux = (path != null ? path.replaceAll("/", "%2F") + "%2F" + secret : "%2Fuserland%2Fpasswords%2F" + secret) + "%2F" + (name != null ? name : secret);
                String filePath = createCustomSecretFile(password != null ? password : secret);
                if (secretType.equals("password_custom")) {
                    pathAux = path != null ? path.replaceAll("/", "%2F") + "%2F" + secret : "%2Fuserland%2Fpasswords%2F" + secret;
                    filePath = customPasswordContentFile;
                }
                restSpec.sendRequestNoDataTable("POST", baseUrl + "/custom?path=" + pathAux, null, filePath, "json");
            }
        }
    }

    @Deprecated
    public void createSecretKeos(String force, String secretType, String secret, String withOrWithout, String path, String cn, String name, String alt, String organizationName, String principal, String realm, String user, String password) throws Exception {
        createSecretKeos(force, secretType, secret, withOrWithout, path, cn, name, alt, organizationName, principal, realm, user, password, null);
    }

    /**
     * Create secret
     *
     * @param force
     * @param secretType
     * @param secret
     * @param withOrWithout
     * @param path
     * @param cn
     * @param name
     * @param alt
     * @param organizationName
     * @param principal
     * @param realm
     * @param user
     * @param password
     * @throws Exception
     */
    @When("^I( force)? create '(certificate|keytab|password|password_nouser)' '(.+?)' using CCT (with|without) parameters( path '(.+?)')?( cn '(.+?)')?( name '(.+?)')?( alt '(.+?)')?( organization '(.+?)')?( principal '(.+?)')?( realm '(.+?)')?( user '(.+?)')?( password '(.+?)')?( customPasswordContentFile '(.+?)')?$")
    public void createSecretKeos(String force, String secretType, String secret, String withOrWithout, String path, String cn, String name, String alt, String organizationName, String principal, String realm, String user, String password, String customPasswordContentFile) throws Exception {
        String baseUrl = "/cct-orchestrator-service/v1/secrets";
        String secretTypeAux;
        String secretTypeK8s;
        String urlParams;

        // Set REST connection
        commonspec.setCCTConnection(null, null);

        switch (secretType) {
            case "certificate":
                urlParams = getCertificateUrlParams(secret, path, cn, name, alt, organizationName);
                secretTypeAux = "certificates";
                secretTypeK8s = "certificates";
                break;
            case "keytab":
                urlParams = getKeytabUrlParams(secret, path, name, principal, realm);
                secretTypeAux = "kerberos";
                secretTypeK8s = "keytabs";
                break;
            case "password":
                urlParams = getPasswordUrlParams(secret, path, name, user, password);
                secretTypeAux = "passwords";
                secretTypeK8s = "passwords";
                break;
            case "password_nouser":
                urlParams = getPasswordNoUserUrlParams(secret, path, name, password);
                secretTypeAux = "passwords";
                secretTypeK8s = "passwords";
                break;
            default:
                urlParams = "";
                secretTypeK8s = "";
                secretTypeAux = "default";
        }
        if (force != null) {
            String pathAux = path != null ? path.replaceAll("/", "%2F") + "%2F" + secret : "%2Fuserland%2F" + secretTypeAux + "%2F" + secret;
            restSpec.sendRequestNoDataTable("DELETE", baseUrl + "?path=" + pathAux, null, null, null);
        }
        restSpec.sendRequestNoDataTable("POST", baseUrl + "/" + secretTypeK8s + urlParams, null, null, null);
    }

    /**
     * Create custom secet file
     *
     * @param password
     * @return
     * @throws IOException
     */
    private String createCustomSecretFile(String password) throws IOException {
        File tempDirectory = new File(System.getProperty("user.dir") + "/target/test-classes/");
        String fileName = System.currentTimeMillis() + ".json";
        String absolutePathFile = tempDirectory.getAbsolutePath() + "/" + fileName;
        commonspec.getLogger().debug("Creating file {} in 'target/test-classes'", absolutePathFile);
        // Note that this Writer will delete the file if it exists
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(absolutePathFile), StandardCharsets.UTF_8));
        try {
            out.write("{\"pass\": \"" + password + "\"}");
        } catch (Exception e) {
            commonspec.getLogger().error("Custom file {} hasn't been created:\n{}", absolutePathFile, e.toString());
        } finally {
            out.close();
        }
        return fileName;
    }

    /**
     * Delete resource in path
     *
     * @param secretType
     * @param secret
     * @param path
     * @throws Exception
     */
    @When("^I delete '(certificate|keytab|password)' '(.+?)'( located in path '(.+?)')?$")
    public void removeSecret(String secretType, String secret, String path) throws Exception {
        String baseUrl;
        if (ThreadProperty.get("isKeosEnv") != null && ThreadProperty.get("isKeosEnv").equals("true")) {
            baseUrl = "/cct-orchestrator-service/v1/secrets";
        } else {
            baseUrl = "/service/" + ThreadProperty.get("deploy_api_id") + "/secrets";
        }
        String secretTypeAux;

        // Set REST connection
        commonspec.setCCTConnection(null, null);

        switch (secretType) {
            case "certificate":
                secretTypeAux = "certificates";
                break;
            case "keytab":
                secretTypeAux = "kerberos";
                break;
            case "password":
                secretTypeAux = "passwords";
                break;
            default:
                secretTypeAux = "default";
        }
        String pathAux = path != null ? path.replaceAll("/", "%2F") + "%2F" + secret : "%2Fuserland%2F" + secretTypeAux + "%2F" + secret;
        restSpec.sendRequestNoDataTable("DELETE", baseUrl + "?path=" + pathAux, null, null, null);
        restSpec.sendRequestNoDataTable("GET", baseUrl + "?path=" + pathAux, null, null, null);
        restSpec.assertResponseStatusLength(404, null, null);
    }

    /**
     * @param secret
     * @param path
     * @param cn
     * @param name
     * @param alt
     * @param organizationName
     * @return
     */
    private String getCertificateUrlParams(String secret, String path, String cn, String name, String alt, String organizationName) {
        String pathAux = path != null ? path.replaceAll("/", "%2F") + secret : "%2Fuserland%2Fcertificates%2F" + secret;
        String cnAux = cn != null ? cn : secret;
        String nameAux = name != null ? name : secret;
        String urlParams = "?path=" + pathAux + "&cn=" + cnAux + "&name=" + nameAux;
        if (alt != null) {
            urlParams = urlParams + "&alt=" + alt;
        }
        if (organizationName != null) {
            urlParams = urlParams + "&organizationName=" + organizationName;
        }
        return urlParams;
    }

    /**
     * @param secret
     * @param path
     * @param name
     * @param principal
     * @param realm
     * @return
     * @throws Exception
     */
    private String getKeytabUrlParams(String secret, String path, String name, String principal, String realm) throws Exception {
        String pathAux = path != null ? path.replaceAll("/", "%2F") + secret : "%2Fuserland%2Fkerberos%2F" + secret;
        String principalAux = principal != null ? principal : secret;
        String nameAux = name != null ? name : secret;
        String realmAux = realm != null ? realm : ThreadProperty.get("isKeosEnv") != null && ThreadProperty.get("isKeosEnv").equals("true") ? ThreadProperty.get("KEOS_REALM") : ThreadProperty.get("EOS_REALM");
        if (realmAux == null) {
            throw new Exception("Realm is mandatory to generate keytab");
        }
        return "?path=" + pathAux + "&principal=" + principalAux + "&name=" + nameAux + "&realm=" + realmAux;
    }

    /**
     * @param secret
     * @param path
     * @param name
     * @param user
     * @param password
     * @return
     */
    private String getPasswordUrlParams(String secret, String path, String name, String user, String password) throws UnsupportedEncodingException {
        String pathAux = path != null ? path.replaceAll("/", "%2F") : "%2Fuserland%2Fpasswords%2F" + secret;
        String nameAux = name != null ? name : secret;
        String userAux = user != null ? user : secret;
        String passwordAux = password != null ? password : secret;
        return "?path=" + pathAux + "&name=" + nameAux + "&password=" + URLEncoder.encode(passwordAux, "UTF-8") + "&user=" + URLEncoder.encode(userAux, "UTF-8");
    }

    /**
     * @param secret
     * @param path
     * @param name
     * @param password
     * @return
     */
    private String getPasswordNoUserUrlParams(String secret, String path, String name, String password) throws UnsupportedEncodingException {
        String pathAux = path != null ? path.replaceAll("/", "%2F") : "%2Fuserland%2Fpasswords%2F" + secret;
        String nameAux = name != null ? name : secret;
        String passwordAux = password != null ? password : secret;
        return "?path=" + pathAux + "&name=" + nameAux + "&password=" + URLEncoder.encode(passwordAux, "UTF-8");
    }

    /**
     * @param endPoint : service endpoint to login to
     * @param baseData : data to base request on
     * @param type     : type of base data
     * @throws Exception
     */
    @When("^I login to '(.+?)' based on '([^:]+?)' as '(json|string)'$")
    public void loginUser(String endPoint, String baseData, String type) throws Exception {
        restSpec.sendRequestNoDataTable("POST", endPoint, null, baseData, type);
    }

    /**
     * @param endPoint      : service endpoint to login to
     * @param baseData      : data to base request on
     * @param type          : type of base data
     * @param modifications : modifications to perform over base data
     * @throws Exception
     */
    @When("^I login to '(.+?)' based on '([^:]+?)' as '(json|string)' with:$")
    public void loginUser(String endPoint, String baseData, String type, DataTable modifications) throws Exception {
        restSpec.sendRequest("POST", endPoint, null, baseData, type, modifications);
    }

    /**
     * @param endPoint : service endpoint to logout from
     * @throws Exception
     */
    @When("^I logout from '(.+?)'$")
    public void logoutUser(String endPoint) throws Exception {
        restSpec.sendRequestNoDataTable("GET", endPoint, null, "", "");
    }

    /**
     * Get internal or external ip for service Id and tasks
     *
     * @param type      type of ip, internal (calico) or external
     * @param serviceId service Id including '/'
     * @param taskName  name of task in the service
     * @param envVar    environment variable where to store the read value
     * @throws Exception
     */
    @Given("^I get the '(internal|external)' ip for service id '(.+?)' for task name '(.+?)'( and save it in environment variable '(.*?)')?")
    @Deprecated
    // TODO Refactor with "^I get host ip for task '(.+?)' in service with id '(.+?)' from CCT and save the value in environment variable '(.+?)'$"
    public void getMachineIp(String type, String serviceId, String taskName, String envVar) throws Exception {

        String ip = null;
        String selector = null;
        String status = null;
        String labelStatus = null;

        String endPointStatus;
        if (ThreadProperty.get("cct-marathon-services_id") == null) {
            endPointStatus = "/service/" + ThreadProperty.get("deploy_api_id") + "/deployments/service?instanceName=" + serviceId;
            if (type.equals("external")) {
                selector = "host";
            } else {
                selector = "calicoIP";
            }
            status = "TASK_RUNNING";
            labelStatus = "state";
        } else {
            endPointStatus = "/service/" + ThreadProperty.get("cct-marathon-services_id") + "/v1/services" + serviceId;
            if (type.equals("external")) {
                selector = "host";
            } else {
                selector = "securedHost";
            }
            status = "RUNNING";
            labelStatus = "status";
        }

        // Set REST connection
        commonspec.setCCTConnection(null, null);

        Future<Response> response = commonspec.generateRequest("GET", false, null, null, endPointStatus, "", null, "");
        commonspec.setResponse("GET", response.get());

        JSONObject deployment = new JSONObject(commonspec.getResponse().getResponse());
        JSONArray tasks = (JSONArray) deployment.get("tasks");

        for (int i = 0; i < tasks.length(); i++) {
            JSONObject item = tasks.getJSONObject(i);
            if (item.getString("name").equals(taskName) && item.getString(labelStatus).equals(status)) {
                ip = item.get(selector).toString();
                break;
            }
        }

        if (ip == null) {
            throw new Exception("Not found " + type + " IP for task " + taskName + " of service Id " + serviceId);
        } else if ((envVar != null) && (ip != null)) {
            ThreadProperty.set(envVar, ip);
        }
    }

    /**
     * Check service status has value specified
     *
     * @param service : name of the service to be checked
     * @param cluster : URI of the cluster
     * @param status  : status expected
     * @throws Exception exception     *
     */
    @Then("^service '(.+?)' status in cluster '(.+?)' is '(suspended|running|deploying)'( in less than '(\\d+)')?( seconds checking every '(\\d+)' seconds)?")
    public void serviceStatusCheck(String service, String cluster, String status, String sTotalWait, String sInterval) throws Exception {
        Integer totalWait = sTotalWait != null ? Integer.parseInt(sTotalWait) : null;
        Integer interval = sInterval != null ? Integer.parseInt(sInterval) : null;
        String response;
        Integer i = 0;
        boolean matched;

        response = commonspec.retrieveServiceStatus(service, cluster);

        if (totalWait != null && interval != null) {
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
     * @param service : name of the service to be checked
     * @param cluster : URI of the cluster
     * @param status  : health status expected
     * @throws Exception exception     *
     */
    @Then("^service '(.+?)' health status in cluster '(.+?)' is '(unhealthy|healthy|unknown)'( in less than '(\\d+)')?( seconds checking every '(\\d+)' seconds)?")
    public void serviceHealthStatusCheck(String service, String cluster, String status, String sTotalWait, String sInterval) throws Exception {
        Integer totalWait = sTotalWait != null ? Integer.parseInt(sTotalWait) : null;
        Integer interval = sInterval != null ? Integer.parseInt(sInterval) : null;
        String response;
        Integer i = 0;
        boolean matched;

        response = commonspec.retrieveHealthServiceStatus(service, cluster);

        if (totalWait != null && interval != null) {
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

    /**
     * Get service status
     *
     * @param service : name of the service to be checked
     * @param cluster : URI of the cluster
     * @param envVar  : environment variable where to store result
     * @throws Exception exception     *
     */
    @Given("^I get service '(.+?)' status in cluster '(.+?)' and save it in variable '(.+?)'")
    public void getServiceStatus(String service, String cluster, String envVar) throws Exception {
        String status = commonspec.retrieveServiceStatus(service, cluster);

        ThreadProperty.set(envVar, status);
    }

    /**
     * Get service health status
     *
     * @param service : name of the service to be checked
     * @param cluster : URI of the cluster
     * @param envVar  : environment variable where to store result
     * @throws Exception exception     *
     */
    @Given("^I get service '(.+?)' health status in cluster '(.+?)' and save it in variable '(.+?)'")
    public void getServiceHealthStatus(String service, String cluster, String envVar) throws Exception {
        String health = commonspec.retrieveHealthServiceStatus(service, cluster);

        ThreadProperty.set(envVar, health);
    }

    /**
     * Destroy specified service
     *
     * @param service : name of the service to be destroyed
     * @param cluster : URI of the cluster
     * @throws Exception exception     *
     */
    @Given("^I destroy service '(.+?)' in cluster '(.+?)'")
    public void destroyService(String service, String cluster) throws Exception {
        String endPoint = "/service/deploy-api/deploy/uninstall?app=" + service;
        Future response;

        this.commonspec.setRestProtocol("https://");
        this.commonspec.setRestHost(cluster);
        this.commonspec.setRestPort(":443");

        response = this.commonspec.generateRequest("DELETE", true, null, null, endPoint, null, "json");

        this.commonspec.setResponse("DELETE", (Response) response.get());
        assertThat(this.commonspec.getResponse().getStatusCode()).as("It hasn't been possible to destroy service: " + service).isIn(Arrays.asList(200, 202));
    }

    /**
     * Check if resources are released after uninstall and framework doesn't appear as inactive on mesos
     *
     * @param service : service to check
     * @throws Exception exception
     */
    @When("^All resources from service '(.+?)' have been freed$")
    public void checkResources(String service) throws Exception {
        Future<Response> response = commonspec.generateRequest("GET", true, null, null, "/mesos/state-summary", null, null);

        String json = "[" + response.get().getResponseBody() + "]";
        String parsedElement = "$..frameworks[?(@.active==false)].name";
        String value = commonspec.getJSONPathString(json, parsedElement, null);

        org.assertj.core.api.Assertions.assertThat(value).as("Inactive services").doesNotContain(service);
    }

    /**
     * Get both PEM and KEY from specified certificate. The ouput files are:
     * <p>
     * target/test-classes/<value>.pem
     * target/test-classes/<value>.key
     *
     * @param value    specific certificate's entry
     * @param path     certificate's path in Vault
     * @param inPeople [optional] look into /people in Vault (/userland by default)
     * @throws Exception
     */
    @When("^I get certificate '(.+?)' using deploy-api from path '(.+?)' in PEM/KEY format( in /people)?$")
    public void getCertificate(String value, String path, String inPeople) throws Exception {
        cctUtils.setSecretsBasePath(inPeople != null);
        cctUtils.getPEMKEYCertificate(path, value);

        File filePem = new File("target/test-classes/" + value + ".pem");
        Assertions.assertThat(filePem.length()).isGreaterThan(1);
        File fileKey = new File("target/test-classes/" + value + ".key");
        Assertions.assertThat(fileKey.length()).isGreaterThan(1);
    }

    /**
     * Get PEM from specified certificate. The ouput file is:
     * <p>
     * target/test-classes/<value>.pem
     *
     * @param value    specific certificate's entry
     * @param path     certificate's path in Vault
     * @param inPeople [optional] look into /people in Vault (/userland by default)
     * @throws Exception
     */
    @Given("^I get certificate '(.+?)' using deploy-api from path '(.+?)' in PEM format( in /people)?$")
    public void getPubCertificate(String value, String path, String inPeople) throws Exception {
        cctUtils.setSecretsBasePath(inPeople != null);
        cctUtils.getPEMCertificate(path, value);

        File filePem = new File("target/test-classes/" + value + ".pem");
        Assertions.assertThat(filePem.length()).isGreaterThan(1);
    }

    /**
     * Get KEY from specified certificate. The ouput file is:
     * <p>
     * target/test-classes/<value>.key
     *
     * @param value    specific certificate's entry
     * @param path     certificate's path in Vault
     * @param inPeople [optional] look into /people in Vault (/userland by default)
     * @throws Exception
     */
    @Given("^I get certificate '(.+?)' using deploy-api from path '(.+?)' in KEY format( in /people)?$")
    public void getKeyCertificate(String value, String path, String inPeople) throws Exception {
        cctUtils.setSecretsBasePath(inPeople != null);
        cctUtils.getKEYCertificate(path, value);

        File fileKey = new File("target/test-classes/" + value + ".key");
        Assertions.assertThat(fileKey.length()).isGreaterThan(1);
    }

    /**
     * Get CA Bundle from cluster. The ouput file is:
     * <p>
     * target/test-classes/ca.crt
     *
     * @throws Exception
     */
    @Given("^I get CA Bundle using deploy-api$")
    public void getCA() throws Exception {
        cctUtils.getCABundle(false);

        File fileCABundle = new File("target/test-classes/ca.crt");
        Assertions.assertThat(fileCABundle.length()).isGreaterThan(1);
        try (FileInputStream fileCABundleInputStream = new FileInputStream("target/test-classes/ca.crt")) {
            String fileCABundleContent = IOUtils.toString(fileCABundleInputStream);
            Assertions.assertThat(fileCABundleContent).isNotEqualToIgnoringCase("null\n");
        }
    }

    /**
     * Get P12 from specified certificate. The ouput file is:
     * <p>
     * target/test-classes/<value>.p12
     *
     * @param value    specific certificate's entry
     * @param path     certificate's path in Vault
     * @param envVar   environment variable to save the P12 password
     * @param inPeople [optional] look into /people in Vault (/userland by default)
     * @throws Exception
     */
    @Given("^I get certificate '(.+?)' using deploy-api from path '(.+?)' in P12 format and save the password in environment variable '(.+?)'( in /people)?$")
    public void getP12Certificate(String value, String path, String envVar, String inPeople) throws Exception {
        cctUtils.setSecretsBasePath(inPeople != null);
        cctUtils.getPKCS12Certificate(path, value, envVar);

        File fileP12 = new File("target/test-classes/" + value + ".p12");
        Assertions.assertThat(fileP12).exists();
    }

    /**
     * Get JKS from specified certificate. The ouput file is:
     * <p>
     * target/test-classes/<value>.jks
     *
     * @param value    specific certificate's entry
     * @param path     certificate's path in Vault
     * @param envVar   environment variable to save the P12 password
     * @param inPeople [optional] look into /people in Vault (/userland by default)
     * @throws Exception
     */
    @Given("^I get certificate '(.+?)' using deploy-api from path '(.+?)' in JKS and save the password in environment variable '(.+?)'( in /people)?$")
    public void getJKSCertificate(String value, String path, String envVar, String inPeople) throws Exception {
        cctUtils.setSecretsBasePath(inPeople != null);
        cctUtils.getKeystore(path, value, envVar);

        File fileJKS = new File("target/test-classes/" + value + ".jks");
        Assertions.assertThat(fileJKS).exists();
    }

    /**
     * Get PK8 from specified certificate. The ouput file is:
     * <p>
     * target/test-classes/<value>.pk8
     *
     * @param value    specific certificate's entry
     * @param path     certificate's path in Vault
     * @param inPeople [optional] look into /people in Vault (/userland by default)
     * @throws Exception
     */
    @Given("^I get certificate '(.+?)' using deploy-api from path '(.+?)' in PK8 format( in /people)?$")
    public void getPK8Certificate(String value, String path, String inPeople) throws Exception {
        cctUtils.setSecretsBasePath(inPeople != null);
        cctUtils.getPKCS8Certificate(path, value);

        File fileKey = new File("target/test-classes/" + value + ".pk8");
        Assertions.assertThat(fileKey.length()).isGreaterThan(0);
    }

    /**
     * Get Truststore with the cluster CA Bundle. The ouput file is:
     * <p>
     * target/test-classes/truststore.jks
     *
     * @throws Exception
     */
    @Given("^I get Truststore containing CA Bundle using deploy-api and save the password in environment variable '(.+?)'$")
    public void getTruststoreWithCABundle(String envVar) throws Exception {
        cctUtils.getTruststoreCABundle(envVar);

        File fileTruststore = new File("target/test-classes/truststore.jks");
        Assertions.assertThat(fileTruststore).exists();
    }

    /**
     * Get Keytab from specified certificate. The ouput file is:
     * <p>
     * target/test-classes/<value>.keytab
     *
     * @param value    specific keytab's entry
     * @param path     Keytab's path in Vault
     * @param inPeople [optional] look into /people in Vault (/userland by default)
     * @throws Exception
     */
    @Given("^I get keytab '(.+?)' using deploy-api from path '(.+?)'( in /people)?$")
    public void getKeytab(String value, String path, String inPeople) throws Exception {
        cctUtils.setSecretsBasePath(inPeople != null);
        cctUtils.getKeytabKrb(path, value);

        File fileKey = new File("target/test-classes/" + value + ".keytab");
        Assertions.assertThat(fileKey.length()).isGreaterThan(0);
    }

    /**
     * Get 'principal' from specified Keytab. The ouput is the 'principal' saved in environmental variable.
     *
     * @param value    specific principal's entry
     * @param path     Keytab's path in Vault
     * @param envVar   environment variable to save the principal
     * @param inPeople [optional] look into /people in Vault (/userland by default)
     * @throws Exception
     */
    @Given("^I get principal '(.+?)' using deploy-api from path '(.+?)' and save it in environment variable '(.+?)'( in /people)?$")
    public void getPrincipal(String value, String path, String envVar, String inPeople) throws Exception {
        cctUtils.setSecretsBasePath(inPeople != null);
        cctUtils.getPrincipalKrb(path, value, envVar);

        Assertions.assertThat(ThreadProperty.get(envVar)).isNotEmpty();
    }

    /**
     * Get 'pass' from specified password. The ouput is the 'pass' saved in environmental variable.
     *
     * @param path     Password's path in Vault
     * @param envVar   environment variable to save the password
     * @param inPeople [optional] look into /people in Vault (/userland by default)
     * @throws Exception
     */
    @Given("^I get password using deploy-api from path '(.+?)' and save it in environment variable '(.+?)'( in /people)?$")
    public void getPwd(String path, String envVar, String inPeople) throws Exception {
        cctUtils.setSecretsBasePath(inPeople != null);
        cctUtils.getPass(path, envVar);

        Assertions.assertThat(ThreadProperty.get(envVar)).isNotEmpty();
    }

    /**
     * Get 'user' from specified password. The ouput is the 'user' saved in environmental variable.
     *
     * @param path     Password's path in Vault
     * @param envVar   environment variable to save the user
     * @param inPeople [optional] look into /people in Vault (/userland by default)
     * @throws Exception
     */
    @Given("^I get user using deploy-api from path '(.+?)' and save it in environment variable '(.+?)'( in /people)?$")
    public void getUsr(String path, String envVar, String inPeople) throws Exception {
        cctUtils.setSecretsBasePath(inPeople != null);
        cctUtils.getUser(path, envVar);

        Assertions.assertThat(ThreadProperty.get(envVar)).isNotEmpty();
    }

}
