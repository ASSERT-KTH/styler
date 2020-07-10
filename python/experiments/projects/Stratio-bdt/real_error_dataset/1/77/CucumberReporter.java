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

package com.stratio.qa.cucumber.testng;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.stratio.qa.specs.CommonG;
import com.stratio.qa.utils.ThreadProperty;
import cucumber.runtime.CucumberException;
import cucumber.runtime.Utils;
import cucumber.runtime.io.URLOutputStream;
import cucumber.runtime.io.UTF8OutputStreamWriter;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CucumberReporter implements Formatter, Reporter {

    public static final int DURATION_STRING = 1000000;

    public static final int DEFAULT_LENGTH = 11;

    public static final int DEFAULT_MAX_LENGTH = 140;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private static final String STATUS = "status";

    private final Document document;

    private final Document jUnitDocument;

    private final Element results;

    private final Element jUnitResults;

    private final Element suite;

    private final Element jUnitSuite;

    private final Element test;

    private String featureName;

    private Writer writer;

    private Writer  writerJunit;

    private Element clazz;

    private Element root;

    private Element jUnitRoot;

    private TestMethod testMethod;

    private Examples tmpExamples;

    private List<Result> tmpHooks = new ArrayList<Result>();

    private List<Step> tmpSteps = new ArrayList<Step>();

    private List<Step> tmpStepsBG = new ArrayList<Step>();

    private Integer iteration = 0;

    private Integer position = 0;

    private String callerClass;

    private Background background;

    private String url;

    private String cClass;

    private String additional;

    private final Logger logger = LoggerFactory.getLogger(this.getClass()
            .getCanonicalName());
    /**
     * Constructor of cucumberReporter.
     *
     * @param url url
     * @param cClass class
     * @throws IOException exception
     */
    public CucumberReporter(String url, String cClass, String additional) throws IOException {
        this.url = url;
        this.cClass = cClass;
        this.additional = additional;

        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            results = document.createElement("testng-results");
            suite = document.createElement("suite");
            test = document.createElement("test");
            callerClass = cClass;
            suite.appendChild(test);
            results.appendChild(suite);
            document.appendChild(results);
            jUnitDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            jUnitResults = jUnitDocument.createElement("testsuites");
            jUnitSuite = jUnitDocument.createElement("testsuite");
            jUnitDocument.appendChild(jUnitResults);
            jUnitResults.appendChild(jUnitSuite);
        } catch (ParserConfigurationException e) {
            throw new CucumberException("Error initializing DocumentBuilder.", e);
        }
    }

    @Override
    public void syntaxError(String state, String event, List<String> legalEvents, String uri, Integer line) {
    }

    @Override
    public void uri(String uri) {
    }

    @Override
    public void feature(Feature feature) {
        featureName = feature.getName();
        clazz = document.createElement("class");
        clazz.setAttribute("name", callerClass);
        test.appendChild(clazz);
    }

    @Override
    public void scenario(Scenario scenario) {
    }

    @Override
    public void scenarioOutline(ScenarioOutline scenarioOutline) {
        iteration = 1;
    }

    @Override
    public void examples(Examples examples) {
        tmpExamples = examples;
    }

    @Override
    public void startOfScenarioLifeCycle(Scenario scenario) {
        root = document.createElement("test-method");
        jUnitRoot = jUnitDocument.createElement("testcase");
        jUnitSuite.appendChild(jUnitRoot);
        clazz.appendChild(root);
        testMethod = new TestMethod(featureName, scenario);
        testMethod.hooks = tmpHooks;
        tmpStepsBG.clear();
        if (tmpExamples == null) {
            testMethod.steps = tmpSteps;
            testMethod.stepsbg = null;
        } else {
            testMethod.steps = new ArrayList<Step>();
            testMethod.stepsbg = tmpStepsBG;
        }
        testMethod.examplesData = tmpExamples;
        testMethod.start(root, iteration, jUnitRoot);
        iteration++;
    }

    @Override
    public void background(Background background) {
        this.background = background;
    }

    @Override
    public void step(Step step) {
        boolean bgstep = false;
        if (background != null && (background.getLineRange().getLast() <= step.getLine())
                && (step.getLine() >= background.getLineRange().getFirst())) {
            tmpStepsBG.add(step);
            bgstep = true;
        }

        if (step.getClass().toString().contains("ExampleStep") && !bgstep) {
            testMethod.steps.add(step);
        } else {
            tmpSteps.add(step);
        }
    }

    @Override
    public void endOfScenarioLifeCycle(Scenario scenario) {

        try {
            testMethod.finish(document, root, this.position, scenario.getTags(), jUnitDocument, jUnitRoot);
        } catch (ExecutionException  | InterruptedException  | IOException e) {
            e.printStackTrace();
        }

        this.position++;
        if ((tmpExamples != null) && (iteration >= tmpExamples.getRows().size())) {
            tmpExamples = null;
        }
        tmpHooks.clear();
        tmpSteps.clear();
        tmpStepsBG.clear();
        testMethod = null;
        jUnitRoot.setAttribute("classname", callerClass);
    }

    @Override
    public void eof() {
    }

    @Override
    public void done() {
        try {
            results.setAttribute("total", String.valueOf(getElementsCountByAttribute(suite, STATUS, ".*")));
            results.setAttribute("passed", String.valueOf(getElementsCountByAttribute(suite, STATUS, "PASS")));
            results.setAttribute("failed", String.valueOf(getElementsCountByAttribute(suite, STATUS, "FAIL")));
            results.setAttribute("skipped", String.valueOf(getElementsCountByAttribute(suite, STATUS, "SKIP")));
            suite.setAttribute("name", CucumberReporter.class.getName());
            suite.setAttribute("duration-ms",
                    String.valueOf(getTotalDuration(suite.getElementsByTagName("test-method"))));
            test.setAttribute("name", CucumberReporter.class.getName());
            test.setAttribute("duration-ms",
                    String.valueOf(getTotalDuration(suite.getElementsByTagName("test-method"))));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            URLOutputStream urlOS = null;
            try {
                urlOS = new URLOutputStream(Utils.toURL(url + cClass + additional + "TESTNG.xml"));
                this.writer = new UTF8OutputStreamWriter(urlOS);
            } catch (Exception e) {
                logger.error("error writing TESTNG.xml file", e);
            }

            StreamResult streamResult = new StreamResult(writer);
            DOMSource domSource = new DOMSource(document);
            transformer.transform(domSource, streamResult);
            jUnitSuite.setAttribute("name", callerClass + "." + featureName);
            jUnitSuite.setAttribute("tests", String.valueOf(getElementsCountByAttribute(suite, STATUS, ".*")));
            jUnitSuite.setAttribute("failures", String.valueOf(getElementsCountByAttribute(suite, STATUS, "FAIL")));
            jUnitSuite.setAttribute("errors", String.valueOf(getElementsCountByAttribute(suite, STATUS, "FAIL")));
            jUnitSuite.setAttribute("skipped", String.valueOf(getElementsCountByAttribute(suite, STATUS, "SKIP")));
            jUnitSuite.setAttribute("timestamp", new java.util.Date().toString());
            jUnitSuite.setAttribute("time",
                      String.valueOf(BigDecimal.valueOf(getTotalDurationMs(suite.getElementsByTagName("test-method"))).setScale(3, BigDecimal.ROUND_HALF_UP).floatValue()));

            Transformer transformerJunit = TransformerFactory.newInstance().newTransformer();
            transformerJunit.setOutputProperty(OutputKeys.INDENT, "yes");

            try {
                urlOS = new URLOutputStream(Utils.toURL(url + cClass + additional + "JUNIT.xml"));
                this.writerJunit = new UTF8OutputStreamWriter(urlOS);
            } catch (Exception e) {
                logger.error("error writing TESTNG.xml file", e);
            }

            StreamResult streamResultJunit = new StreamResult(writerJunit);
            DOMSource domSourceJunit = new DOMSource(jUnitDocument);
            transformerJunit.transform(domSourceJunit, streamResultJunit);

        } catch (TransformerException e) {
            throw new CucumberException("Error transforming report.", e);
        }
    }

    @Override
    public void close() {
    }

    // Reporter methods
    @Override
    public void before(Match match, Result result) {
        tmpHooks.add(result);
    }

    @Override
    public void match(Match match) {
    }

    @Override
    public void result(Result result) {
        testMethod.results.add(result);
    }

    @Override
    public void embedding(String mimeType, byte[] data) {
    }

    @Override
    public void write(String text) {
    }

    @Override
    public void after(Match match, Result result) {
        testMethod.hooks.add(result);
    }

    private int getElementsCountByAttribute(Node node, String attributeName, String attributeValue) {
        int count = 0;

        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            count += getElementsCountByAttribute(node.getChildNodes().item(i), attributeName, attributeValue);
        }

        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            Node namedItem = attributes.getNamedItem(attributeName);
            if (namedItem != null && namedItem.getNodeValue().matches(attributeValue)) {
                count++;
            }
        }
        return count;
    }

    private double getTotalDuration(NodeList testCaseNodes) {
        double totalDuration = 0;
        for (int i = 0; i < testCaseNodes.getLength(); i++) {
            try {
                String duration = "0";
                Node durationms = testCaseNodes.item(i).getAttributes().getNamedItem("duration-ms");
                if (durationms != null) {
                    duration = durationms.getNodeValue();
                }
                totalDuration += Double.parseDouble(duration);
            } catch (NumberFormatException e) {
                throw new CucumberException(e);
            }
        }
        return totalDuration;
    }

    private double getTotalDurationMs(NodeList testCaseNodes) {
        return getTotalDuration(testCaseNodes) / 1000;
    }

    public final class TestMethod {

        private boolean treatSkippedAsFailure = false;

        private final List<Result> results = new ArrayList<Result>();

        private Scenario scenario = null;

        private String featureName;

        private Examples examplesData;

        private List<Step> steps;

        private List<Step> stepsbg;

        private List<Result> hooks;

        private Integer iteration = 1;

        public TestMethod(String feature, Scenario scenario) {
            this.featureName = feature;
            this.scenario = scenario;
        }

        private void start(Element element, Integer iteration, Element JunitElement) {
            this.iteration = iteration;
            String testSuffix = System.getProperty("TESTSUFFIX");
            String name = scenario.getName();
            if (testSuffix != null) {
                name = name + " [" + testSuffix + "]";
            }
            if ((examplesData == null) || (this.iteration >= examplesData.getRows().size())) {
                element.setAttribute("name", name);
                JunitElement.setAttribute("name", name);
                ThreadProperty.set("dataSet", "");
            } else {
                String data = obtainOutlineScenariosExamples(examplesData.getRows().get(iteration).getCells().toString());
                element.setAttribute("name", name + " " + data);
                JunitElement.setAttribute("name", name + " " + data);
                ThreadProperty.set("dataSet", data);
            }
            element.setAttribute("started-at", DATE_FORMAT.format(new Date()));
        }

        public String obtainOutlineScenariosExamples(String examplesData) {
            String data = examplesData.replaceAll("\"", "¨");
            return data;
        }

        /**
         * Checks the passed by ticket parameter validity against a Attlasian Jira account
         *
         * @param ticket Jira ticket
         */
        private boolean isValidJiraTicket (String ticket) {
            String userJira = System.getProperty("usernamejira");
            String passJira = System.getProperty("passwordjira");
            Boolean validTicket = false;

            if ((userJira != null) || (passJira != null)  || "".equals(ticket)) {
                CommonG comm = new CommonG();
                AsyncHttpClient client = new AsyncHttpClient();
                Future<Response> response = null;
                Logger logger = LoggerFactory.getLogger(ThreadProperty.get("class"));

                comm.setRestProtocol("https://");
                comm.setRestHost("stratio.atlassian.net");
                comm.setRestPort("");
                comm.setClient(client);
                String endpoint = "/rest/api/2/issue/" + ticket;
                try {
                    response = comm.generateRequest("GET", true, userJira, passJira, endpoint, "", "json");
                    comm.setResponse(endpoint, response.get());
                } catch (Exception e) {
                    logger.error("Rest API Jira connection error " + String.valueOf(comm.getResponse().getStatusCode()));
                    return false;
                }

                String json = comm.getResponse().getResponse();
                String value = "";
                try {
                    value = JsonPath.read(json, "$.fields.status.name");
                    value = value.toLowerCase();
                } catch (PathNotFoundException pe) {
                    logger.error("Json Path $.fields.status.name not found\r");
                    logger.error(json);
                    return false;
                }

                if (!"done".equals(value) || !"finalizado".equals(value) || !"qa".equals(value)) {
                    validTicket = true;
                }
            }
            return validTicket;
        }

        /**
         * Builds a test result xml document, builds exception messages on non valid ignore causes such as
         * \@tillfixed without an in progress Stratio's Jira ticker
         *
         * @param doc report document
         * @param element scenario execution result
         * @param position position of element in document
         * @param tags tags that performs conditional inclusion of element
         * @param docJunit docJunit report document
         * @param Junit Junit scenario execution result
         * @throws ExecutionException exception
         * @throws InterruptedException exception
         * @throws IOException exception
         */
        public void finish(Document doc, Element element, Integer position, List<Tag> tags, Document docJunit,
                           Element Junit) throws ExecutionException, InterruptedException, IOException {

            Junit.setAttribute("time", String.valueOf(calculateTotalDurationString() / 1000));

            element.setAttribute("duration-ms", String.valueOf(calculateTotalDurationString()));
            element.setAttribute("finished-at", DATE_FORMAT.format(new Date()));

            StringBuilder stringBuilder = new StringBuilder();

            List<Step> mergedsteps = new ArrayList<Step>();
            if (stepsbg != null) {
                mergedsteps.addAll(stepsbg);
                mergedsteps.addAll(steps);
            } else {
                mergedsteps.addAll(steps);
            }
            addStepAndResultListing(stringBuilder, mergedsteps);
            Result skipped = null;
            Result failed = null;
            Boolean ignored = false;
            Boolean ignoreReason = false;
            String exceptionmsg = "Failed";
            String ticket = "";
            Boolean isJiraTicketDone = false;
            Boolean isWrongTicket = false;
            Boolean ignoreRun = false;

            List<String> tagList = new ArrayList<>();
            tagList = tags.stream().map(Tag::getName).collect(Collectors.toList());

            for (Result result : results) {
                if ("failed".equals(result.getStatus())) {
                    failed = result;
                } else if ("undefined".equals(result.getStatus()) || "pending".equals(result.getStatus())) {
                    skipped = result;
                }
            }
            for (Result result : hooks) {
                if (failed == null && "failed".equals(result.getStatus())) {
                    failed = result;
                }
            }

            if (tagList.contains("@ignore")) {
                ignored = true;
                for (String tag: tagList) {
                    Pattern pattern = Pattern.compile("@tillfixed\\((.*?)\\)");
                    Matcher matcher = pattern.matcher(tag);
                    if (matcher.find()) {
                        ticket = matcher.group(1);
//                        if (isValidJiraTicket(ticket)) {
                        if (true) {
                            exceptionmsg = "This scenario was skipped because of a pending Jira ticket: " + ticket;
                            ignoreReason = true;
                        }
                        break;
                    }
                }

                if (tagList.contains("@unimplemented")) {
                    exceptionmsg = "This scenario was skipped because of it is not yet implemented";
                    ignoreReason = true;
                }

                if (tagList.contains("@manual")) {
                    ignoreReason = true;
                    exceptionmsg = "This scenario was skipped because it is marked as manual.";
                }

                if (tagList.contains("@toocomplex")) {
                    exceptionmsg = "This scenario was skipped because of being too complex to test";
                    ignoreReason = true;
                }

                if (tagList.contains("@envCondition")) {
                    ignoreRun = true;
                }
            }

            String msg1 = "";
            String msg2 = "";

            if (ignoreRun) {
                docJunit.getDocumentElement().getLastChild().removeChild(docJunit.getDocumentElement().getLastChild().getLastChild());
                return;
            } else if (ignored && (!ignoreReason || (ignoreReason && isJiraTicketDone) || (ignoreReason && isWrongTicket))) {
                element.setAttribute(STATUS, "FAIL");
                if (isJiraTicketDone) {
                    msg1 = "The scenario was ignored due an already done (or in progress) ticket. " + "https://stratio.atlassian.net/browse/" + ticket;
                } else if (isWrongTicket) {
                    msg1 = "The scenario was ignored due to unexistant ticket " + ticket;
                } else {
                    msg1 = "The scenario has no valid reason for being ignored. \n Valid values: @tillfixed(ISSUE-007) @unimplemented @manual @toocomplex";
                }

                Element exception = createException(doc, msg1, msg1, msg2);
                element.appendChild(exception);
                Element systemOut = createExceptionJunit(docJunit, msg1, msg1, msg2);
                Junit.appendChild(systemOut);

            } else if (ignored && ignoreReason) {
                element.setAttribute(STATUS, "SKIP");
                Element exception = createException(doc, "skipped",
                        exceptionmsg, " ");
                element.appendChild(exception);
                Element skippedElementJunit = docJunit.createElement("skipped");
                Junit.appendChild(skippedElementJunit);
                Element systemOut = systemOutPrintJunit(docJunit, exceptionmsg);
                Junit.appendChild(systemOut);

            } else if ((stringBuilder.toString().contains("${") || stringBuilder.toString().contains("!{") || stringBuilder.toString().contains("@{")) && (failed != null || skipped != null)) {
                element.setAttribute(STATUS, "FAIL");
                Element exception = createException(doc, "The scenario has unreplaced variables.",
                        "The scenario has unreplaced variables.", " ");
                element.appendChild(exception);
                Element exceptionJunit = createExceptionJunit(docJunit, "The scenario has unreplaced variables.",
                        "The scenario has unreplaced variables.", " ");
                Junit.appendChild(exceptionJunit);
                Element systemOut = systemOutPrintJunit(docJunit, stringBuilder.toString());
                Junit.appendChild(systemOut);
            } else {
                if (failed != null) {
                    element.setAttribute(STATUS, "FAIL");
                    StringWriter stringWriter = new StringWriter();
                    if (failed.getErrorMessage().contains("An important scenario has failed!")) {
                        element.setAttribute(STATUS, "SKIP");
                        Element skippedElementJunit = docJunit.createElement("skipped");
                        Junit.appendChild(skippedElementJunit);
                        Element systemOut = systemOutPrintJunit(docJunit, stringBuilder.toString());
                        Junit.appendChild(systemOut);
                    } else {
                        failed.getError().printStackTrace(new PrintWriter(stringWriter));
                        Element exception = createException(doc, failed.getError().getClass().getName(),
                                stringBuilder.toString(), stringWriter.toString());
                        element.appendChild(exception);
                        Element exceptionJunit = createExceptionJunit(docJunit, failed.getError().getClass().getName(),
                                stringBuilder.toString(), stringWriter.toString());
                        Junit.appendChild(exceptionJunit);
                    }
                } else if (skipped != null) {
                    if (treatSkippedAsFailure) {
                        element.setAttribute(STATUS, "FAIL");
                        Element exception = createException(doc, "The scenario has pending or undefined step(s)",
                                stringBuilder.toString(), "The scenario has pending or undefined step(s)");
                        element.appendChild(exception);
                        Element exceptionJunit = createExceptionJunit(docJunit,
                                "The scenario has pending or undefined step(s)", stringBuilder.toString(),
                                "The scenario has pending or undefined step(s)");
                        Junit.appendChild(exceptionJunit);
                    } else {
                        element.setAttribute(STATUS, "SKIP");
                        Element skippedElementJunit = docJunit.createElement("skipped");
                        Junit.appendChild(skippedElementJunit);
                        Element systemOut = systemOutPrintJunit(docJunit, stringBuilder.toString());
                        Junit.appendChild(systemOut);
                    }

                } else {
                    element.setAttribute(STATUS, "PASS");
                    Element exception = createException(doc, "NonRealException", stringBuilder.toString(), " ");
                    element.appendChild(exception);
                    Element systemOut = systemOutPrintJunit(docJunit, stringBuilder.toString());
                    Junit.appendChild(systemOut);
                }
            }
        }


        private double calculateTotalDurationString() {
            double totalDurationNanos = 0;
            for (Result r : results) {
                totalDurationNanos += r.getDuration() == null ? 0 : r.getDuration();
            }
            for (Result r : hooks) {
                totalDurationNanos += r.getDuration() == null ? 0 : r.getDuration();
            }
            return totalDurationNanos / DURATION_STRING;
        }

        public void addStepAndResultListing(StringBuilder sb, List<Step> mergedsteps) {

            for (int i = 0; i < mergedsteps.size(); i++) {
                String resultStatus = "not executed";
                String resultStatusWarn = "*";
                if (i < results.size()) {
                    resultStatus = results.get(i).getStatus();
                    resultStatusWarn = ((results.get(i).getError() != null) && (results.get(i).getStatus()
                            .equals("passed"))) ? "(W)" : "";
                }
                sb.append(mergedsteps.get(i).getKeyword());
                sb.append(mergedsteps.get(i).getName());
                int len = 0;
                len = mergedsteps.get(i).getKeyword().length() + mergedsteps.get(i).getName().length();
                if (mergedsteps.get(i).getRows() != null) {
                    for (DataTableRow row : mergedsteps.get(i).getRows()) {
                        StringBuilder strrowBuilder = new StringBuilder();
                        strrowBuilder.append("| ");
                        for (String cell : row.getCells()) {
                            strrowBuilder.append(cell).append(" | ");
                        }
                        String strrow = strrowBuilder.toString();
                        len = strrow.length() + DEFAULT_LENGTH;
                        sb.append("\n           ");
                        sb.append(strrow);
                    }
                }
                for (int j = 0; j + len < DEFAULT_MAX_LENGTH; j++) {
                    sb.append(".");
                }

                sb.append(resultStatus);
                sb.append(resultStatusWarn);
                sb.append("\n");
            }
            String cap = "";
            if (!("".equals(cap = hasCapture(featureName, scenario.getName())))) {
                sb.append("evidence @ " + System.getProperty("BUILD_URL", "") + "/artifact/testsAT/" + cap.replaceAll("", ""));
            }
        }

        private String hasCapture(String feat, String scen) {
            String testSuffix = System.getProperty("TESTSUFFIX");
            File dir;
            if (testSuffix != null) {
                dir = new File("./target/executions/" + testSuffix + "/");
            } else {
                dir = new File("./target/executions/");
            }
            final String[] imgext = {"png"};
            Collection<File> files = FileUtils.listFiles(dir, imgext, true);

            for (File file : files) {
                if (file.getPath().contains(featureName.replaceAll(" ", "_") + "." + scenario.getName().replaceAll(" ", "_")) &&
                        file.getName().contains("assert")) {
                    return file.toString();
                }
            }
            return "";
        }

        private Element createException(Document doc, String clazz, String message, String stacktrace) {
            Element exceptionElement = doc.createElement("exception");
            exceptionElement.setAttribute("class", clazz);

            if (message != null) {
                Element messageElement = doc.createElement("message");
                messageElement.appendChild(doc.createCDATASection("\r\n<pre>\r\n" + message + "\r\n</pre>\r\n"));
                exceptionElement.appendChild(messageElement);
            }

            Element stacktraceElement = doc.createElement("full-stacktrace");
            stacktraceElement.appendChild(doc.createCDATASection(stacktrace));
            exceptionElement.appendChild(stacktraceElement);

            return exceptionElement;
        }

        private Element createExceptionJunit(Document doc, String clazz, String message, String stacktrace) {
            Element exceptionElement = doc.createElement("failure");
            if (message != null) {
                exceptionElement.setAttribute("message", "\r\n" + message + "\r\n");
            }
            exceptionElement.appendChild(doc.createCDATASection(stacktrace));
            return exceptionElement;
        }

        private Element systemOutPrintJunit(Document doc, String message) {
            Element systemOut = doc.createElement("system-out");
            systemOut.appendChild(doc.createCDATASection("\r\n" + message + "\r\n"));
            return systemOut;
        }
    }
}
