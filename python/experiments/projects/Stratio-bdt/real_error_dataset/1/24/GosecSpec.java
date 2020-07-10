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
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;
import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class GosecSpec extends BaseGSpec {

    private final Logger logger = LoggerFactory.getLogger(GosecSpec.class);

    RestSpec restSpec;
    /**
     * Generic constructor.
     *
     * @param spec object
     */
    public GosecSpec(CommonG spec) {
        this.commonspec = spec;
        restSpec = new RestSpec(spec);
    }

    @When("^I create '(policy|user|group)' '(.+?)'( using API service path '(.+?)')?( with user and password '(.+:.+?)')? based on '([^:]+?)'( as '(json|string|gov)')? with:$")
    public void createResource(String resource, String resourceId, String endPoint, String loginInfo, String baseData, String type, DataTable modifications) throws Exception {
        createResourceIfNotExist(resource, resourceId, endPoint, loginInfo, false, baseData, type, modifications);

    }

    @When("^I create '(policy|user|group)' '(.+?)'( using API service path '(.+?)')?( with user and password '(.+:.+?)')? if it does not exist based on '([^:]+?)'( as '(json|string|gov)')? with:$")
    public void createResourceIfNotExist(String resource, String resourceId, String endPoint, String loginInfo, String baseData, String type, DataTable modifications) throws Exception {
        createResourceIfNotExist(resource, resourceId, endPoint, loginInfo, true, baseData, type, modifications);
    }

    /**
     * Creates a custom resource in gosec management if the resource doesn't exist
     *
     * @param resource
     * @param resourceId    (userId, groupId or policyId)
     * @param endPoint
     * @param loginInfo
     * @param doesNotExist  (if 'empty', creation is forced deleting the previous policy if exists)
     * @param baseData
     * @param type
     * @param modifications
     * @throws Exception
     */
    private void createResourceIfNotExist(String resource, String resourceId, String endPoint, String loginInfo, boolean doesNotExist, String baseData, String type, DataTable modifications) throws Exception {
        Integer expectedStatusCreate = 201;
        Integer expectedStatusDelete = 200;
        String endPointResource = "";
        String endPointPolicies = "/service/gosecmanagement" + ThreadProperty.get("API_POLICIES");
        String endPointPolicy = "/service/gosecmanagement" + ThreadProperty.get("API_POLICY");
        String newEndPoint = "";
        String gosecVersion = ThreadProperty.get("gosec-management_version");
        List<List<String>> newModifications;
        newModifications = convertDataTableToModifiableList(modifications);
        Boolean addSourceType = false;

        if (endPoint != null) {
            endPointResource = endPoint + resourceId;

            if (endPoint.contains("id")) {
                newEndPoint = endPoint.replace("?id=", "");
            } else {
                newEndPoint = endPoint.substring(0, endPoint.length() - 1);
            }
        } else {
            if (resource.equals("policy")) {
                endPoint = "/service/gosecmanagement" + ThreadProperty.get("API_POLICY");
            } else {
                if (resource.equals("user")) {
                    endPoint = "/service/gosecmanagement" + ThreadProperty.get("API_USER");
                } else {
                    endPoint = "/service/gosecmanagement" + ThreadProperty.get("API_GROUP");
                }
            }
            if (endPoint.contains("id")) {
                newEndPoint = endPoint.replace("?id=", "");
            } else {
                newEndPoint = endPoint.substring(0, endPoint.length() - 1);
            }
            endPointResource = endPoint + resourceId;
        }

        if (gosecVersion != null) {
            String[] gosecVersionArray = gosecVersion.split("\\.");
            // Add inputSourceType if Gosec >= 1.4.x
            if (Integer.parseInt(gosecVersionArray[0]) >= 1 && Integer.parseInt(gosecVersionArray[1]) >= 4) {
                addSourceType = true;
            }
        }

        try {
            assertThat(commonspec.getRestHost().isEmpty() || commonspec.getRestPort().isEmpty());

            if (resource.equals("policy")) {
                restSpec.sendRequestNoDataTable("GET", endPointPolicies, loginInfo, null, null);
                if (commonspec.getResponse().getStatusCode() == 200) {
                    commonspec.runLocalCommand("echo '" + commonspec.getResponse().getResponse() + "' | jq '.list[] | select (.name == \"" + resourceId + "\").id' | sed s/\\\"//g");
                    String policyId = commonspec.getCommandResult().trim();
                    if (!policyId.equals("")) {
                        commonspec.getLogger().debug("PolicyId obtained: {}", policyId);
                        endPointResource = endPointPolicy + policyId;
                    } else {
                        commonspec.runLocalCommand("echo '" + commonspec.getResponse().getResponse() + "' | jq '.[] | select (.name == \"" + resourceId + "\").id' | sed s/\\\"//g");
                        policyId = commonspec.getCommandResult().trim();
                        if (!policyId.equals("")) {
                            commonspec.getLogger().debug("PolicyId obtained: {}", policyId);
                            endPointResource = endPointPolicy + policyId;
                        } else {
                            endPointResource = endPointPolicy + "thisIsANewPolicyId";
                        }
                    }
                }
            }

            restSpec.sendRequestNoDataTable("GET", endPointResource, loginInfo, null, null);

            if (commonspec.getResponse().getStatusCode() != 200) {
                if (resource.equals("user") && (addSourceType)) {
                    commonspec.getLogger().warn("Gosec Version:{} -> Adding inputsourceType = CUSTOM", gosecVersion);
                    List<String> newField = Arrays.asList("$.inputSourceType", "ADD", "CUSTOM", "string");
                    newModifications.add(newField);
                }
                // Create datatable with modified data
                DataTable gosecModifications = DataTable.create(newModifications);
                // Send POST request
                restSpec.sendRequest("POST", newEndPoint, loginInfo, baseData, type, gosecModifications);
                try {
                    if (commonspec.getResponse().getStatusCode() == 409) {
                        commonspec.getLogger().warn("The resource {} already exists", resourceId);
                    } else {
                        try {
                            assertThat(commonspec.getResponse().getStatusCode()).isEqualTo(expectedStatusCreate);
                        } catch (AssertionError e) {
                            commonspec.getLogger().warn("Error creating Resource {}: {}", resourceId, commonspec.getResponse().getResponse());
                            throw e;
                        }
                        commonspec.getLogger().warn("Resource {} created", resourceId);
                    }
                } catch (Exception e) {
                    commonspec.getLogger().warn("Error creating user {}: {}", resourceId, commonspec.getResponse().getResponse());
                    throw e;
                }
            } else {
                commonspec.getLogger().warn("{}:{} already exist", resource, resourceId);
                if (resource.equals("policy") && commonspec.getResponse().getStatusCode() == 200) {
                    if (doesNotExist) {
                        // Policy already exists
                        commonspec.getLogger().warn("Policy {} already exist - not created", resourceId);

                    } else {
                        // Delete policy if exists
                        restSpec.sendRequest("DELETE", endPointResource, loginInfo, baseData, type, modifications);
                        commonspec.getLogger().warn("Policy {} deleted", resourceId);

                        try {
                            assertThat(commonspec.getResponse().getStatusCode()).isEqualTo(expectedStatusDelete);
                        } catch (AssertionError e) {
                            commonspec.getLogger().warn("Error deleting Policy {}: {}", resourceId, commonspec.getResponse().getResponse());
                            throw e;
                        }
                        createResourceIfNotExist(resource, resourceId, endPoint, loginInfo, doesNotExist, baseData, type, modifications);
                    }
                }
            }
        } catch (Exception e) {
            commonspec.getLogger().error("Rest Host or Rest Port are not initialized {}{}", commonspec.getRestHost(), commonspec.getRestPort());
            throw e;
        }
    }

    /**
     * Deletes a resource in gosec management if the resourceId exists previously.
     *
     * @param resource
     * @param resourceId
     * @param endPoint
     * @param loginInfo
     * @throws Exception
     */
    @When("^I delete '(policy|user|group)' '(.+?)'( using API service path '(.+?)')?( with user and password '(.+:.+?)')? if it exists$")
    public void deleteUserIfExists(String resource, String resourceId, String endPoint, String loginInfo) throws Exception {
        Integer[] expectedStatusDelete = {200, 204};
        String endPointResource = "";
        String endPointPolicy = "/service/gosecmanagement" + ThreadProperty.get("API_POLICY");
        String endPointPolicies = "/service/gosecmanagement" + ThreadProperty.get("API_POLICIES");

        if (endPoint != null) {
            endPointResource = endPoint + resourceId;
        } else {
            if (resource.equals("policy")) {
                endPoint = "/service/gosecmanagement" + ThreadProperty.get("API_POLICY");
            } else {
                if (resource.equals("user")) {
                    endPoint = "/service/gosecmanagement" + ThreadProperty.get("API_USER");
                } else {
                    endPoint = "/service/gosecmanagement" + ThreadProperty.get("API_GROUP");
                }
            }
        }

        try {
            assertThat(commonspec.getRestHost().isEmpty() || commonspec.getRestPort().isEmpty());

            if (resource.equals("policy")) {
                restSpec.sendRequestNoDataTable("GET", endPointPolicies, loginInfo, null, null);
                if (commonspec.getResponse().getStatusCode() == 200) {
                    commonspec.runLocalCommand("echo '" + commonspec.getResponse().getResponse() + "' | jq '.list[] | select (.name == \"" + resourceId + "\").id' | sed s/\\\"//g");
                    String policyId = commonspec.getCommandResult().trim();
                    if (!policyId.equals("")) {
                        commonspec.getLogger().debug("PolicyId obtained: {}", policyId);
                        endPointResource = endPointPolicy + policyId;
                    } else {
                        commonspec.runLocalCommand("echo '" + commonspec.getResponse().getResponse() + "' | jq '.[] | select (.name == \"" + resourceId + "\").id' | sed s/\\\"//g");
                        policyId = commonspec.getCommandResult().trim();
                        if (!policyId.equals("")) {
                            commonspec.getLogger().debug("PolicyId obtained: {}", policyId);
                            endPointResource = endPointPolicy + policyId;
                        } else {
                            endPointResource = endPointPolicy + "thisPolicyDoesNotExistId";
                        }
                    }
                }
            } else {
                endPointResource = endPoint + resourceId;
            }

            restSpec.sendRequestNoDataTable("GET", endPointResource, loginInfo, null, null);

            if (commonspec.getResponse().getStatusCode() == 200) {
                // Delete resource if exists
                restSpec.sendRequestNoDataTable("DELETE", endPointResource, loginInfo, null, null);
                commonspec.getLogger().warn("Resource {} deleted", resourceId);

                try {
                    assertThat(commonspec.getResponse().getStatusCode()).isIn(expectedStatusDelete);
                } catch (AssertionError e) {
                    commonspec.getLogger().warn("Error deleting Resource {}: {}", resourceId, commonspec.getResponse().getResponse());
                    throw e;
                }
            } else {
                commonspec.getLogger().warn("Resource {} with id {} not found so it's not deleted", resource, resourceId);
            }
        } catch (Exception e) {
            commonspec.getLogger().error("Rest Host or Rest Port are not initialized {}: {}", commonspec.getRestHost(), commonspec.getRestPort());
            throw e;
        }
    }

    @When("^I get id from( tag)? policy with name '(.+?)' and save it in environment variable '(.+?)'$")
    public void getPolicyId(String tag, String policyName, String envVar) throws Exception {
        String endPoint = "/service/gosecmanagement" + ThreadProperty.get("API_POLICIES");

        if (tag != null) {
            endPoint = "/service/gosecmanagement" + ThreadProperty.get("API_TAGS");
        }

        assertThat(commonspec.getRestHost().isEmpty() || commonspec.getRestPort().isEmpty());
        restSpec.sendRequestNoDataTable("GET", endPoint, null, null, null);
        if (commonspec.getResponse().getStatusCode() == 200) {
            commonspec.runLocalCommand("echo '" + commonspec.getResponse().getResponse() + "' | jq '.list[] | select (.name == \"" + policyName + "\").id' | sed s/\\\"//g");
            commonspec.runCommandLoggerAndEnvVar(0, envVar, Boolean.TRUE);
            if (ThreadProperty.get(envVar) == null || ThreadProperty.get(envVar).trim().equals("")) {
                commonspec.runLocalCommand("echo '" + commonspec.getResponse().getResponse() + "' | jq '.[] | select (.name == \"" + policyName + "\").id' | sed s/\\\"//g");
                commonspec.runCommandLoggerAndEnvVar(0, envVar, Boolean.TRUE);
                if (ThreadProperty.get(envVar) == null || ThreadProperty.get(envVar).trim().equals("")) {
                    fail("Error obtaining ID from policy " + policyName);
                }
            }
        } else {
            if (commonspec.getResponse().getStatusCode() == 404) {
                fail("Error obtaining policies from gosecmanagement {} (Response code = " + commonspec.getResponse().getStatusCode() + ")", endPoint);
            }
        }
    }

    @When("^I create tenant '(.+?)' if it does not exist based on '([^:]+?)'( as '(json|string|gov)')? with:$")
    public void createTenant(String tenantId, String baseData, String type, DataTable modifications) throws Exception {
        String endPoint = "/service/gosec-identities-daas/identities/tenants";
        String endPointResource = endPoint + "/" + tenantId;
        Integer expectedStatus = 201;
        assertThat(commonspec.getRestHost().isEmpty() || commonspec.getRestPort().isEmpty());
        restSpec.sendRequestNoDataTable("GET", endPointResource, null, null, null);
        if (commonspec.getResponse().getStatusCode() == 200) {
            commonspec.getLogger().warn("Tenant {} already exist - not created", tenantId);
        } else {
            restSpec.sendRequest("POST", endPoint, null, baseData, type, modifications);
            try {
                assertThat(commonspec.getResponse().getStatusCode()).isEqualTo(expectedStatus);
            } catch (AssertionError e) {
                commonspec.getLogger().warn("Error creating Tenant {}: {}", tenantId, commonspec.getResponse().getResponse());
                throw e;
            }
        }
    }

    @When("^I delete tenant '(.+?)' if it exists$")
    public void deleteTenant(String tenantId) throws Exception {
        String endPoint = "/service/gosec-identities-daas/identities/tenants";
        String endPointResource = endPoint + "/" + tenantId;
        Integer expectedStatus = 204;
        assertThat(commonspec.getRestHost().isEmpty() || commonspec.getRestPort().isEmpty());
        restSpec.sendRequestNoDataTable("GET", endPointResource, null, null, null);
        if (commonspec.getResponse().getStatusCode() == 200) {
            restSpec.sendRequestNoDataTable("DELETE", endPointResource, null, null, null);
            commonspec.getLogger().warn("Tenant {} deleted", tenantId);
            try {
                assertThat(commonspec.getResponse().getStatusCode()).isEqualTo(expectedStatus);
            } catch (AssertionError e) {
                commonspec.getLogger().warn("Error deleting Tenant {}: {}", tenantId, commonspec.getResponse().getResponse());
                throw e;
            }
        } else {
            commonspec.getLogger().warn("Tenant {} does not exist - not deleted", tenantId);
        }
    }

    @When("^I include '(user|group)' '(.+?)' in tenant '(.+?)'$")
    public void includeResourceInTenant(String resource, String resourceId, String tenantId) throws Exception {
        String endPointGetAllUsers = "/service/gosec-identities-daas/identities/users";
        String endPointGetAllGroups = "/service/gosec-identities-daas/identities/groups";
        String endPointTenant = "/service/gosec-identities-daas/identities/tenants/" + tenantId;
        assertThat(commonspec.getRestHost().isEmpty() || commonspec.getRestPort().isEmpty());
        String uidOrGid = "uid";
        String uidOrGidTenant = "uids";
        String endPointGosec = endPointGetAllUsers;
        if (resource.equals("group")) {
            uidOrGid = "gid";
            uidOrGidTenant = "gids";
            endPointGosec = endPointGetAllGroups;
        }
        restSpec.sendRequestNoDataTable("GET", endPointGosec, null, null, null);
        if (commonspec.getResponse().getStatusCode() == 200) {
            if (commonspec.getResponse().getResponse().contains("\"" + uidOrGid + "\":\"" + resourceId + "\"")) {
                restSpec.sendRequestNoDataTable("GET", endPointTenant, null, null, null);
                if (commonspec.getResponse().getStatusCode() == 200) {
                    JsonObject jsonTenantInfo = new JsonObject(JsonValue.readHjson(commonspec.getResponse().getResponse()).asObject());
                    if (((JsonArray) jsonTenantInfo.get(uidOrGidTenant)).values().contains(JsonValue.valueOf(resourceId))) {
                        commonspec.getLogger().debug("{} is already included in tenant", resourceId);
                    } else {
                        ((JsonArray) jsonTenantInfo.get(uidOrGidTenant)).add(resourceId);
                        Future<Response> response = commonspec.generateRequest("PATCH", false, null, null, endPointTenant, JsonValue.readHjson(jsonTenantInfo.toString()).toString(), "json", "");
                        commonspec.setResponse("PATCH", response.get());
                        if (commonspec.getResponse().getStatusCode() != 204) {
                            throw new Exception("Error adding " + resource + " " + resourceId + " in tenant " + tenantId + " - Status code: " + commonspec.getResponse().getStatusCode());
                        }
                    }
                } else {
                    throw new Exception("Error obtaining info from tenant " + tenantId + " - Status code: " + commonspec.getResponse().getStatusCode());
                }
            } else {
                throw new Exception(resource + " " + resourceId + " doesn't exist in Gosec");
            }
        } else {
            throw new Exception("Error obtaining " + resource + "s - Status code: " + commonspec.getResponse().getStatusCode());
        }
    }

    @When("^I get id from profile with name '(.+?)' and save it in environment variable '(.+?)'$")
    public void getProfiled(String profileName, String envVar) throws Exception {
        String endPoint = "/service/gosec-identities-daas/identities/profiles";

        assertThat(commonspec.getRestHost().isEmpty() || commonspec.getRestPort().isEmpty());
        restSpec.sendRequestNoDataTable("GET", endPoint, null, null, null);
        if (commonspec.getResponse().getStatusCode() == 200) {
            commonspec.runLocalCommand("echo '" + commonspec.getResponse().getResponse() + "' | jq '.list[] | select (.name == \"" + profileName + "\").pid' | sed s/\\\"//g");
            commonspec.runCommandLoggerAndEnvVar(0, envVar, Boolean.TRUE);
            if (ThreadProperty.get(envVar) == null || ThreadProperty.get(envVar).trim().equals("")) {
                fail("Error obtaining ID from profile " + profileName);
            }
        } else {
            commonspec.getLogger().warn("Profile with id: {} does not exist", profileName);
        }
    }

    @When("^I get json from( tag)? policy with name '(.+?)' and save it( in environment variable '(.*?)')?( in file '(.*?)')?$")
    public void getPolicyJson(String tag, String policyName, String envVar, String fileName) throws Exception {
        String endPoint = "/service/gosecmanagement/api/policy";
        String newEndPoint = "/service/gosecmanagement/api/policies";
        String errorMessage = "api/policies";
        String errorMessage2 = "api/policy";

        if (tag != null) {
            endPoint = "/service/gosecmanagement/api/policy/tag";
            newEndPoint = "/service/gosecmanagement/api/policies/tags";
            errorMessage = "api/policies/tags";
            errorMessage2 = "api/policy/tag";
        }
        assertThat(commonspec.getRestHost().isEmpty() || commonspec.getRestPort().isEmpty());
        restSpec.sendRequestNoDataTable("GET", endPoint, null, null, null);
        if (commonspec.getResponse().getStatusCode() == 200) {
            commonspec.runLocalCommand("echo '" + commonspec.getResponse().getResponse() + "' | jq '.list[] | select (.name == \"" + policyName + "\").id' | sed s/\\\"//g");
            if (commonspec.getCommandResult().trim().equals("")) {
                commonspec.runLocalCommand("echo '" + commonspec.getResponse().getResponse() + "' | jq '.[] | select (.name == \"" + policyName + "\").id' | sed s/\\\"//g");
            }
            restSpec.sendRequestNoDataTable("GET", "/service/gosecmanagement/api/policy/" + commonspec.getCommandResult(), null, null, null);

            if (envVar != null) {
                ThreadProperty.set(envVar, commonspec.getResponse().getResponse());

                if (ThreadProperty.get(envVar) == null || ThreadProperty.get(envVar).trim().equals("")) {
                    fail("Error obtaining JSON from policy " + policyName);
                }
            }

            if (fileName != null) {
                // Create file (temporary) and set path to be accessible within test
                File tempDirectory = new File(System.getProperty("user.dir") + "/target/test-classes/");
                String absolutePathFile = tempDirectory.getAbsolutePath() + "/" + fileName;
                commonspec.getLogger().debug("Creating file {} in 'target/test-classes'", absolutePathFile);
                // Note that this Writer will delete the file if it exists
                Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(absolutePathFile), StandardCharsets.UTF_8));
                try {
                    out.write(commonspec.getResponse().getResponse());
                } catch (Exception e) {
                    commonspec.getLogger().error("Custom file {} hasn't been created:\n{}", absolutePathFile, e.toString());
                } finally {
                    out.close();
                }

                Assertions.assertThat(new File(absolutePathFile).isFile());
            }

        } else {
            if (commonspec.getResponse().getStatusCode() == 404) {
                commonspec.getLogger().warn("Error 404 accessing endpoint {}: checking the new endpoint for Gosec 1.1.1", endPoint);
                restSpec.sendRequestNoDataTable("GET", newEndPoint, null, null, null);
                if (commonspec.getResponse().getStatusCode() == 200) {
                    commonspec.runLocalCommand("echo '" + commonspec.getResponse().getResponse() + "' | jq '.list[] | select (.name == \"" + policyName + "\").id' | sed s/\\\"//g");
                    if (commonspec.getCommandResult().trim().equals("")) {
                        commonspec.runLocalCommand("echo '" + commonspec.getResponse().getResponse() + "' | jq '.[] | select (.name == \"" + policyName + "\").id' | sed s/\\\"//g");
                    }
                    restSpec.sendRequestNoDataTable("GET", "/service/gosecmanagement/api/policy?id=" + commonspec.getCommandResult(), null, null, null);

                    if (envVar != null) {
                        ThreadProperty.set(envVar, commonspec.getResponse().getResponse());
                        if (ThreadProperty.get(envVar) == null || ThreadProperty.get(envVar).trim().equals("")) {
                            fail("Error obtaining JSON from policy " + policyName);
                        }
                    }

                    if (fileName != null) {
                        // Create file (temporary) and set path to be accessible within test
                        File tempDirectory = new File(System.getProperty("user.dir") + "/target/test-classes/");
                        String absolutePathFile = tempDirectory.getAbsolutePath() + "/" + fileName;
                        commonspec.getLogger().debug("Creating file {} in 'target/test-classes'", absolutePathFile);
                        // Note that this Writer will delete the file if it exists
                        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(absolutePathFile), StandardCharsets.UTF_8));
                        try {
                            out.write(commonspec.getResponse().getResponse());
                        } catch (Exception e) {
                            commonspec.getLogger().error("Custom file {} hasn't been created:\n{}", absolutePathFile, e.toString());
                        } finally {
                            out.close();
                        }

                        Assertions.assertThat(new File(absolutePathFile).isFile());
                    }

                } else {
                    fail("Error obtaining policies from gosecmanagement {} (Response code = " + commonspec.getResponse().getStatusCode() + ")", errorMessage);
                }
            } else {
                fail("Error obtaining policies from gosecmanagement {} (Response code = " + commonspec.getResponse().getStatusCode() + ")", errorMessage2);
            }
        }
    }

    @When("^I include group '(.+?)' in profile '(.+?)'$")
    public void includeGroupInProfile(String groupId, String profileId) throws Exception {
        String endPointGetGroup = "/service/gosecmanagement/api/group?id=" + groupId;
        String endPointGetProfile = "/service/gosecmanagement/api/profile?id=" + profileId;
        String groups = "groups";
        String pid = "pid";
        String id = "id";
        String roles = "roles";
        Boolean content = false;

        assertThat(commonspec.getRestHost().isEmpty() || commonspec.getRestPort().isEmpty());

        restSpec.sendRequestNoDataTable("GET", endPointGetGroup, null, null, null);
        if (commonspec.getResponse().getStatusCode() == 200) {
            JsonObject jsonGroupInfo = new JsonObject(JsonValue.readHjson(commonspec.getResponse().getResponse()).asObject());
            restSpec.sendRequestNoDataTable("GET", endPointGetProfile, null, null, null);
            if (commonspec.getResponse().getStatusCode() == 200) {
                JsonObject jsonProfileInfo = new JsonObject(JsonValue.readHjson(commonspec.getResponse().getResponse()).asObject());
                // Get groups from profile
                JsonArray jsonGroups = (JsonArray) jsonProfileInfo.get(groups);
                // Get size of groups
                String[] stringGroups = new String[jsonGroups.size() + 1];
                // Create json for put
                JSONObject putObject = new JSONObject(commonspec.getResponse().getResponse());
                // Remove groups and roles in json
                putObject.remove(groups);
                putObject.remove(roles);

                for (int i = 0; i < jsonGroups.size(); i++) {
                    String jsonIds = ((JsonObject) jsonGroups.get(i)).getString("id", "");

                    if (jsonIds.equals(groupId)) {
                        commonspec.getLogger().warn("{} is already included in the profile {}", groupId, profileId);
                        content = true;
                        break;
                    } else {
                        stringGroups[i] = jsonIds;
                    }
                }

                if (!content) {
                    // Add new group in array of gids
                    stringGroups[jsonGroups.size()] = groupId;
                    // Add gids array to new json for PUT request
                    putObject.put("gids", stringGroups);

                    commonspec.getLogger().warn("Json for PUT request---> {}", putObject.toString());
                    Future<Response> response = commonspec.generateRequest("PUT", false, null, null, endPointGetProfile, JsonValue.readHjson(putObject.toString()).toString(), "json", "");
                    commonspec.setResponse("PUT", response.get());
                    if (commonspec.getResponse().getStatusCode() != 204) {
                        throw new Exception("Error adding Group: " + groupId + " in Profile " + profileId + " - Status code: " + commonspec.getResponse().getStatusCode());
                    }
                }

            } else {
                throw new Exception("Error obtaining Profile: " + profileId + "- Status code: " + commonspec.getResponse().getStatusCode());
            }

        } else {
            throw new Exception("Error obtaining Group: " + groupId + "- Status code: " + commonspec.getResponse().getStatusCode());
        }
    }

    private List<List<String>> convertDataTableToModifiableList(DataTable dataTable) {
        List<List<String>> lists = dataTable.asLists(String.class);
        List<List<String>> updateableLists = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            List<String> list = lists.get(i);
            List<String> updateableList = new ArrayList<>();
            for (int j = 0; j < list.size(); j++) {
                updateableList.add(j, list.get(j));
            }
            updateableLists.add(i, updateableList);
        }
        return updateableLists;
    }

    /**
     * Updates a resource in gosec management if the resourceId exists previously.
     *
     * @param resource      : type of resource (policy, user, group or tenant)
     * @param resourceId    : policy name, userId, groupId or tenantId
     * @param loginInfo     (optional)
     * @param type          : type of data (json,string,gov)
     * @param modifications : data to modify the resource
     * @throws Exception if the resource does not exists or the request fails
     */
    @When("^I update '(policy|user|group|tenant)' '(.+?)'( with user and password '(.+:.+?)')? based on '([^:]+?)'( as '(json|string|gov)')? with:$")
    public void updateResource(String resource, String resourceId, String loginInfo, String baseData, String type, DataTable modifications) throws Exception {
        Integer[] expectedStatusUpdate = {200, 201, 204};
        String endPointPolicy = "/service/gosecmanagement" + ThreadProperty.get("API_POLICY");
        String endPointPolicies = "/service/gosecmanagement" + ThreadProperty.get("API_POLICIES");
        String endPoint = "";
        String endPointResource = "";

        if (resource.equals("policy")) {
            endPoint = "/service/gosecmanagement" + ThreadProperty.get("API_POLICY");
        } else {
            if (resource.equals("user")) {
                endPoint = "/service/gosecmanagement" + ThreadProperty.get("API_USER");
            } else {
                endPoint = "/service/gosecmanagement" + ThreadProperty.get("API_GROUP");
            }
            if (resource.equals("tenant")) {
                endPoint = "/service/gosec-identities-daas/identities/tenants/";
            }
        }

        try {
            assertThat(commonspec.getRestHost().isEmpty() || commonspec.getRestPort().isEmpty());

            if (resource.equals("policy")) {
                restSpec.sendRequestNoDataTable("GET", endPointPolicies, loginInfo, null, null);
                if (commonspec.getResponse().getStatusCode() == 200) {
                    commonspec.runLocalCommand("echo '" + commonspec.getResponse().getResponse() + "' | jq '.list[] | select (.name == \"" + resourceId + "\").id' | sed s/\\\"//g");
                    String policyId = commonspec.getCommandResult().trim();
                    if (!policyId.equals("")) {
                        commonspec.getLogger().debug("PolicyId obtained: {}", policyId);
                        endPointResource = endPointPolicy + policyId;
                    } else {
                        commonspec.runLocalCommand("echo '" + commonspec.getResponse().getResponse() + "' | jq '.[] | select (.name == \"" + resourceId + "\").id' | sed s/\\\"//g");
                        policyId = commonspec.getCommandResult().trim();
                        if (!policyId.equals("")) {
                            commonspec.getLogger().debug("PolicyId obtained: {}", policyId);
                            endPointResource = endPointPolicy + policyId;
                        } else {
                            endPointResource = endPointPolicy + "thisPolicyDoesNotExistId";
                        }
                    }
                }
            } else {
                endPointResource = endPoint + resourceId;
            }

            restSpec.sendRequestNoDataTable("GET", endPointResource, loginInfo, null, null);

            if (commonspec.getResponse().getStatusCode() == 200) {
                if (resource.equals("tenant")) {
                    restSpec.sendRequest("PATCH", endPointResource, loginInfo, baseData, type, modifications);
                } else {
                    restSpec.sendRequest("PUT", endPointResource, loginInfo, baseData, type, modifications);
                }
                commonspec.getLogger().warn("Resource {}:{} updated", resource, resourceId);

                try {
                    assertThat(commonspec.getResponse().getStatusCode()).isIn(expectedStatusUpdate);
                } catch (AssertionError e) {
                    commonspec.getLogger().error("Error updating Resource {} {}: {}", resource, resourceId, commonspec.getResponse().getResponse());
                    throw e;
                }
            } else {
                commonspec.getLogger().error("Resource {}:{} not found so it's not updated", resource, resourceId);
            }
        } catch (Exception e) {
            commonspec.getLogger().error("Rest Host or Rest Port are not initialized {}: {}", commonspec.getRestHost(), commonspec.getRestPort());
            throw e;
        }
    }

    /**
     * Removes user or group from tenant if the resource exists and has been assigned previously
     * @param resource      : type of resource (user or group)
     * @param resourceId    : userId or groupId
     * @throws Exception if the resource does not exists or the request fails
     */
    @When("^I remove '(user|group)' '(.+?)' from tenant '(.+?)'$")
    public void removeResourceInTenant(String resource, String resourceId, String tenantId) throws Exception {
        String endPointGetAllUsers = "/service/gosec-identities-daas/identities/users";
        String endPointGetAllGroups = "/service/gosec-identities-daas/identities/groups";
        String endPointTenant = "/service/gosec-identities-daas/identities/tenants/" + tenantId;
        assertThat(commonspec.getRestHost().isEmpty() || commonspec.getRestPort().isEmpty());
        String uidOrGid = "uid";
        String uidOrGidTenant = "uids";
        String endPointGosec = endPointGetAllUsers;
        if (resource.equals("group")) {
            uidOrGid = "gid";
            uidOrGidTenant = "gids";
            endPointGosec = endPointGetAllGroups;
        }
        restSpec.sendRequestNoDataTable("GET", endPointGosec, null, null, null);
        if (commonspec.getResponse().getStatusCode() == 200) {
            if (commonspec.getResponse().getResponse().contains("\"" + uidOrGid + "\":\"" + resourceId + "\"")) {
                restSpec.sendRequestNoDataTable("GET", endPointTenant, null, null, null);
                if (commonspec.getResponse().getStatusCode() == 200) {
                    JsonObject jsonTenantInfo = new JsonObject(JsonValue.readHjson(commonspec.getResponse().getResponse()).asObject());
                    if (((JsonArray) jsonTenantInfo.get(uidOrGidTenant)).values().contains(JsonValue.valueOf(resourceId))) {
                        // remove resource from tenant
                        // Get groups/users from tenant
                        JsonArray jsonGroups = (JsonArray) jsonTenantInfo.get(uidOrGidTenant);
                        // Create new string for new data without resource
                        String[] stringGroups = new String[jsonGroups.size() - 1];
                        // Create json for put
                        JSONObject putObject = new JSONObject(commonspec.getResponse().getResponse());
                        // Remove ids in json
                        putObject.remove(uidOrGidTenant);
                        // create new array with values without resourceId
                        for (int i = 0; i < jsonGroups.size(); i++) {
                            int j = 0;
                            String jsonIds = jsonGroups.get(i).toString().substring(1, jsonGroups.get(i).toString().length() - 1);
                            if (jsonIds.equals(resourceId)) {
                                commonspec.getLogger().warn("{} {} removed from tenant {}", resource, resourceId, tenantId);
                            } else {
                                stringGroups[j] = jsonIds;
                                j = j + 1;
                            }
                        }
                        putObject.put(uidOrGidTenant, stringGroups);
                        commonspec.getLogger().debug("Json for PATCH request---> {}", putObject.toString());
                        Future<Response> response = commonspec.generateRequest("PATCH", false, null, null, endPointTenant, JsonValue.readHjson(putObject.toString()).toString(), "json", "");
                        commonspec.setResponse("PATCH", response.get());
                        if (commonspec.getResponse().getStatusCode() != 204) {
                            throw new Exception("Error removing " + resource + " " + resourceId + " in tenant " + tenantId + " - Status code: " + commonspec.getResponse().getStatusCode());
                        }

                    } else {
                        commonspec.getLogger().error("{} is not included in tenant -> not removed", resourceId);
                    }
                } else {
                    throw new Exception("Error obtaining info from tenant " + tenantId + " - Status code: " + commonspec.getResponse().getStatusCode());
                }
            } else {
                throw new Exception(resource + " " + resourceId + " doesn't exist in Gosec");
            }
        } else {
            throw new Exception("Error obtaining " + resource + "s - Status code: " + commonspec.getResponse().getStatusCode());
        }
    }

}
