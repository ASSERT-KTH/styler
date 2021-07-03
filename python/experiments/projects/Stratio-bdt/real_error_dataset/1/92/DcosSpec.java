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
import com.stratio.qa.utils.GosecSSOUtils;
import com.stratio.qa.utils.RemoteSSHConnection;
import com.stratio.qa.utils.ThreadProperty;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.assertj.core.api.Assertions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.Future;

import com.jayway.jsonpath.JsonPath;

import static com.stratio.qa.assertions.Assertions.assertThat;

/**
 * Generic DC/OS Specs.
 */
public class DcosSpec extends BaseGSpec {

    RestSpec restSpec;
    /**
     * Generic constructor.
     *
     * @param spec object
     */
    public DcosSpec(CommonG spec) {
        this.commonspec = spec;
        this.restSpec = new RestSpec(spec);
    }

    /**
     * Authenticate in a DCOS cluster
     *
     * @param remoteHost remote host
     * @param email      email for JWT singing
     * @param user       remote user
     * @param password   (required if pemFile null)
     * @param pemFile    (required if password null)
     * @throws Exception exception
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
        com.ning.http.client.cookie.Cookie cookie = new com.ning.http.client.cookie.Cookie("dcos-acs-auth-cookie", jwt, false, "", "", 99999, false, false);
        List<com.ning.http.client.cookie.Cookie> cookieList = new ArrayList<com.ning.http.client.cookie.Cookie>();
        cookieList.add(cookie);
        commonspec.setCookies(cookieList);
        ThreadProperty.set("dcosAuthCookie", jwt);
    }

    /**
     * Generate token to authenticate in gosec SSO
     *
     * @param ssoHost  current sso host
     * @param userName username
     * @param passWord password
     * @throws Exception exception
     */
    @Given("^I( do not)? set sso token using host '(.+?)' with user '(.+?)' and password '(.+?)'( and tenant '(.+?)')?$")
    public void setGoSecSSOCookie(String set, String ssoHost, String userName, String passWord, String foo, String tenant) throws Exception {
        if (set == null) {
            HashMap<String, String> ssoCookies = new GosecSSOUtils(ssoHost, userName, passWord, tenant).ssoTokenGenerator();
            String[] tokenList = {"user", "dcos-acs-auth-cookie"};
            List<com.ning.http.client.cookie.Cookie> cookiesAtributes = addSsoToken(ssoCookies, tokenList);

            commonspec.setCookies(cookiesAtributes);
        }
    }

    public List<com.ning.http.client.cookie.Cookie> addSsoToken(HashMap<String, String> ssoCookies, String[] tokenList) {
        List<com.ning.http.client.cookie.Cookie> cookiesAttributes = new ArrayList<>();

        for (String tokenKey : tokenList) {
            cookiesAttributes.add(new com.ning.http.client.cookie.Cookie(tokenKey, ssoCookies.get(tokenKey),
                    false, null,
                    null, 999999, false, false));
        }
        return cookiesAttributes;
    }

    /**
     * Checks if there are any unused nodes in the cluster and returns the IP of one of them.
     * REQUIRES A PREVIOUSLY-ESTABLISHED SSH CONNECTION TO DCOS-CLI TO WORK
     *
     * @param hosts:  list of IPs that will be investigated
     * @param envVar: environment variable name
     * @throws Exception
     */
    @Given("^I save the IP of an unused node in hosts '(.+?)' in the in environment variable '(.+?)'?$")
    public void getUnusedNode(String hosts, String envVar) throws Exception {
        Set<String> hostList = new HashSet(Arrays.asList(hosts.split(",")));

        //Get the list of currently used hosts
        commonspec.executeCommand("dcos task | awk '{print $2}'", 0, null);
        String results = commonspec.getRemoteSSHConnection().getResult();
        Set<String> usedHosts = new HashSet(Arrays.asList(results.replaceAll("\r", "").split("\n")));

        //We get the nodes not being used
        hostList.removeAll(usedHosts);

        if (hostList.size() == 0) {
            throw new IllegalStateException("No unused nodes in the cluster.");
        } else {
            //Pick the first available node
            ThreadProperty.set(envVar, hostList.iterator().next());
        }
    }


    /**
     * Check if all task of a service are correctly distributed in all datacenters of the cluster
     *
     * @param serviceList all task deployed in the cluster separated by a semicolumn.
     * @throws Exception
     */
    @Given("^services '(.*?)' are splitted correctly in datacenters$")
    public void checkServicesDistributionMultiDataCenter(String serviceList) throws Exception {
        commonspec.executeCommand("dcos node --json >> aux.txt", 0, null);
        commonspec.executeCommand("cat aux.txt", 0, null);
        checkDataCentersDistribution(serviceList.split(","), obtainsDataCenters(commonspec.getRemoteSSHConnection().getResult()).split(";"));
        commonspec.executeCommand("rm -rf aux.txt", 0, null);

    }

    /**
     * Check if all task of a service are correctly distributed in all datacenters of the cluster
     *
     * @param serviceList    all task deployed in the cluster separated by a semicolumn.
     * @param dataCentersIps all ips of the datacenters to be checked
     *                       Example: ip_1_dc1, ip_2_dc1;ip_3_dc2,ip_4_dc2
     * @throws Exception
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
            commonspec.executeCommand("dcos task | grep " + serviceListArray[i] + " | awk '{print $2}'", 0, null);
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

    /**
     * Get info about secrets according input parameter
     *
     * @param type       what type of info (cert, key, ca, principal or keytab)
     * @param path       path where get info
     * @param value      value inside path
     * @param token      vault value
     * @param isUnsecure vault by http instead of https
     * @param host       gosec machine IP
     * @param contains   regex needed to match method
     * @param exitStatus command exit status
     * @param envVar:    environment variable name
     * @throws Exception exception     *
     */
    @Given("^I get '(.+?)' from path '(.+?)' for value '(.+?)' with token '(.+?)',( unsecure)? vault host '(.+?)'( with exit status '(.+?)')? and save the value in environment variable '(.+?)'$")
    public void getSecretInfo(String type, String path, String value, String token, String isUnsecure, String host, String contains, Integer exitStatus, String envVar) throws Exception {

        if (exitStatus == null) {
            exitStatus = 0;
        }

        String httpProtocol;
        if (isUnsecure != null) {
            httpProtocol = "http://";
        } else {
            httpProtocol = "https://";
        }

        String command;
        switch (type) {
            case "crt":
                command = "curl -X GET -fskL --tlsv1.2 -H \"X-Vault-Token:" + token + "\" \"" + httpProtocol + host + ":8200/v1" + path + "\" | jq -r '.data.\"" + value + "_" + type + "\"' | sed 's/-----BEGIN CERTIFICATE-----/-----BEGIN CERTIFICATE-----#####/g' | sed 's/-----END CERTIFICATE-----/#####-----END CERTIFICATE-----/g' | sed 's/-----END CERTIFICATE----------BEGIN CERTIFICATE-----/-----END CERTIFICATE-----#####-----BEGIN CERTIFICATE-----/g' > " + value + ".pem";
                commonspec.runLocalCommand(command);
                commonspec.setCommandResult(commonspec.getCommandResult().replace("#####", "\n"));
                command = "ls $PWD/" + value + ".pem";
                commonspec.runLocalCommand(command);
                commonspec.runCommandLoggerAndEnvVar(exitStatus, envVar, Boolean.TRUE);
                break;
            case "key":
                command = "curl -X GET -fskL --tlsv1.2 -H \"X-Vault-Token:" + token + "\" \"" + httpProtocol + host + ":8200/v1" + path + "\" | jq -r '.data.\"" + value + "_" + type + "\"' | sed 's/-----BEGIN RSA PRIVATE KEY-----/-----BEGIN RSA PRIVATE KEY-----#####/g' | sed 's/-----END RSA PRIVATE KEY-----/#####-----END RSA PRIVATE KEY-----/g' > " + value + ".key";
                commonspec.runLocalCommand(command);
                commonspec.setCommandResult(commonspec.getCommandResult().replace("#####", "\n"));
                command = "ls $PWD/" + value + ".key";
                commonspec.runLocalCommand(command);
                commonspec.runCommandLoggerAndEnvVar(exitStatus, envVar, Boolean.TRUE);
                break;
            case "ca":
                command = "curl -X GET -fskL --tlsv1.2 -H \"X-Vault-Token:" + token + "\" \"" + httpProtocol + host + ":8200/v1" + path + "\" | jq -r '.data.\"" + value + "_crt\"' | sed 's/-----BEGIN CERTIFICATE-----/-----BEGIN CERTIFICATE-----#####/g' | sed 's/-----END CERTIFICATE-----/#####-----END CERTIFICATE-----/g' > " + value + ".crt";
                commonspec.runLocalCommand(command);
                commonspec.setCommandResult(commonspec.getCommandResult().replace("#####", "\n"));
                command = "ls $PWD/" + value + ".crt";
                commonspec.runLocalCommand(command);
                commonspec.runCommandLoggerAndEnvVar(exitStatus, envVar, Boolean.TRUE);
                break;
            case "keytab":
                command = "curl -X GET -fskL --tlsv1.2 -H \"X-Vault-Token:" + token + "\" \"" + httpProtocol + host + ":8200/v1" + path + "\" | jq -r '.data.\"" + value + "_" + type + "\"' | base64 -d > " + value + ".keytab";
                commonspec.runLocalCommand(command);
                command = "ls $PWD/" + value + ".keytab";
                commonspec.runLocalCommand(command);
                commonspec.runCommandLoggerAndEnvVar(exitStatus, envVar, Boolean.TRUE);
                break;
            case "principal":
                command = "curl -X GET -fskL --tlsv1.2 -H \"X-Vault-Token:" + token + "\" \"" + httpProtocol + host + ":8200/v1" + path + "\" | jq -r '.data.\"" + value + "_" + type + "\"'";
                commonspec.runLocalCommand(command);
                commonspec.runCommandLoggerAndEnvVar(exitStatus, envVar, Boolean.TRUE);
                break;
            default:
                break;
        }

    }

    /**
     * Convert jsonSchema to json
     *
     * @param jsonSchema jsonSchema to be converted to json
     * @param envVar     environment variable where to store json
     * @throws Exception exception     *
     */
    @Given("^I convert jsonSchema '(.+?)' to json and save it in variable '(.+?)'")
    public void convertJSONSchemaToJSON(String jsonSchema, String envVar) throws Exception {
        String json = commonspec.parseJSONSchema(new JSONObject(jsonSchema)).toString();
        ThreadProperty.set(envVar, json);
    }

    /**
     * Check if json is validated against a schema
     *
     * @param json    json to be validated against schema
     * @param schema  schema to be validated against
     * @throws Exception exception     *
     */
    @Given("^json (.+?) matches schema (.+?)$")
    public void jsonMatchesSchema(String json, String schema) throws Exception {
        JSONObject jsonschema = new JSONObject(schema);
        JSONObject jsondeploy = new JSONObject(json);

        commonspec.matchJsonToSchema(jsonschema, jsondeploy);
    }

    /**
     * Get service status
     *
     * @param service   name of the service to be checked
     * @param cluster   URI of the cluster
     * @param envVar    environment variable where to store result
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
     * @param service   name of the service to be checked
     * @param cluster   URI of the cluster
     * @param envVar    environment variable where to store result
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
     * @param service   name of the service to be destroyed
     * @param cluster   URI of the cluster
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
     * @param service service
     * @throws Exception exception
     */
    @When("^All resources from service '(.+?)' have been freed$")
    public void checkResources(String service) throws Exception {
        restSpec.sendRequestNoDataTable("GET", "/mesos/state-summary", null, null, null, null, null, "json");

        String json = "[" + commonspec.getResponse().getResponse() + "]";
        String parsedElement = "$..frameworks[?(@.active==false)].name";
        String value = commonspec.getJSONPathString(json, parsedElement, null);

        Assertions.assertThat(value).as("Inactive services").doesNotContain(service);
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

    @Then("^I obtain metabase id for user '(.+?)' and password '(.+?)' in endpoint '(.+?)' and save in context cookies$")
    public void saveMetabaseCookie(String user, String password, String url) throws Exception {
        String command = "curl -X POST -k -H \"Content-Type: application/json\" -d '{\"username\": \"" + user + "\", \"password\": \"" + password + "\"}' " + url;
        commonspec.runLocalCommand(command);
        commonspec.runCommandLoggerAndEnvVar(0, null, Boolean.TRUE);

        Assertions.assertThat(commonspec.getCommandExitStatus()).isEqualTo(0);
        String result = JsonPath.parse(commonspec.getCommandResult().trim()).read("$.id");

        com.ning.http.client.cookie.Cookie cookie = new com.ning.http.client.cookie.Cookie("metabase.SESSION_ID", result, false, "", "", 99999L, false, false);
        ArrayList cookieList = new ArrayList();
        cookieList.add(cookie);
        this.commonspec.setCookies(cookieList);
    }
}
