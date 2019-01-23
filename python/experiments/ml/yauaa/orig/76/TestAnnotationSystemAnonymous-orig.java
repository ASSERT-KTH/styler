/*
 * Yet Another UserAgent Analyzer
 * Copyright (C) 2013-2018 Niels Basjes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.basjes.parse.useragent.annotate;

import nl.basjes.parse.useragent.analyze.InvalidParserConfigurationException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.Serializable;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestAnnotationSystemAnonymous {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    TestRecord record = new TestRecord();

    public static class TestRecord implements Serializable {
        final String useragent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.82 Safari/537.36";
        String deviceClass;
        String agentNameVersion;
    }

    @SuppressWarnings("unused")
    public abstract static class MyMapper<T>
        implements UserAgentAnnotationMapper<T>, Serializable {
        private final transient UserAgentAnnotationAnalyzer<T> userAgentAnalyzer;

        public MyMapper() {
            userAgentAnalyzer = new UserAgentAnnotationAnalyzer<>();
            userAgentAnalyzer.initialize(this);
        }

        public T enrich(T record) {
            return userAgentAnalyzer.map(record);
        }
    }

    @Test
    public void testAnnotationBasedParser(){
        expectedEx = ExpectedException.none();

        record =
            new MyMapper<TestRecord>() {
                @Override
                public String getUserAgentString(TestRecord testRecord) {
                    return testRecord.useragent;
                }

                @YauaaField("DeviceClass")
                public void setDeviceClass(TestRecord testRecord, String value) {
                    testRecord.deviceClass = value;
                }

                @YauaaField("AgentNameVersion")
                public void setAgentNameVersion(TestRecord testRecord, String value) {
                    testRecord.agentNameVersion = value;
                }
            } .enrich(record);

        assertEquals("Desktop", record.deviceClass);
        assertEquals("Chrome 48.0.2564.82", record.agentNameVersion);
    }

    // ----------------------------------------------------------------

    public abstract static class MyErrorMapper extends MyMapper<TestRecord> {
        @Override
        public String getUserAgentString(TestRecord record) {
            return record.useragent;
        }
    }

    @Test
    public void testImpossibleField() {
        expectedEx.expect(InvalidParserConfigurationException.class);
        expectedEx.expectMessage("We cannot provide these fields:[NielsBasjes]");
        record =
            new MyErrorMapper() {
                @YauaaField("NielsBasjes")
                public void setImpossibleField(TestRecord testRecord, String value) {
                    fail("May NEVER call this method");
                }
            } .enrich(record);
    }

    // ----------------------------------------------------------------

    @Test
    public void testWrongReturnType() {
        expectedEx.expectMessage(containsString("the method [wrongSetter] " +
            "has been annotated with YauaaField but it has the wrong method signature. It must look like " +
            "[ public void wrongSetter(TestRecord record, String value) ]"));
        record =
            new MyErrorMapper() {
                @YauaaField("DeviceClass")
                public boolean wrongSetter(TestRecord testRecord, Double value) {
                    fail("May NEVER call this method");
                    return false;
                }
            } .enrich(record);
    }

    // ----------------------------------------------------------------

    @Test
    public void testInaccessibleSetter() {
        expectedEx.expect(InvalidParserConfigurationException.class);
        expectedEx.expectMessage("Method annotated with YauaaField is not public: inaccessibleSetter");
        record =
            new MyErrorMapper() {
                @YauaaField("DeviceClass")
                private void inaccessibleSetter(TestRecord testRecord, String value) {
                    fail("May NEVER call this method");
                }
            } .enrich(record);
    }

    // ----------------------------------------------------------------

    @Test
    public void testTooManyParameters() {
        expectedEx.expect(InvalidParserConfigurationException.class);
        expectedEx.expectMessage(containsString("the method [wrongSetter] " +
            "has been annotated with YauaaField but it has the wrong method signature. It must look like " +
            "[ public void wrongSetter(TestRecord record, String value) ]"));
        record =
            new MyErrorMapper() {
                @YauaaField("DeviceClass")
                public void wrongSetter(TestRecord testRecord, String value, String extra) {
                    fail("May NEVER call this method");
                }
            } .enrich(record);
    }

    // ----------------------------------------------------------------

    @Test
    public void testWrongTypeParameters1() {
        expectedEx.expectMessage(containsString("the method [wrongSetter] " +
            "has been annotated with YauaaField but it has the wrong method signature. It must look like " +
            "[ public void wrongSetter(TestRecord record, String value) ]"));
        record =
            new MyErrorMapper() {
                @YauaaField("DeviceClass")
                public void wrongSetter(String string, String value) {
                    fail("May NEVER call this method");
                }
            } .enrich(record);
    }

    // ----------------------------------------------------------------

    @Test
    public void testWrongTypeParameters2() {
        expectedEx.expectMessage(containsString("the method [wrongSetter] " +
            "has been annotated with YauaaField but it has the wrong method signature. It must look like " +
            "[ public void wrongSetter(TestRecord record, String value) ]"));
        record =
            new MyErrorMapper() {
                @YauaaField("DeviceClass")
                public void wrongSetter(TestRecord testRecord, Double value) {
                    fail("May NEVER call this method");
                }
            } .enrich(record);
    }

    // ----------------------------------------------------------------

    @Test
    public void testMissingAnnotations() {
        expectedEx.expect(InvalidParserConfigurationException.class);
        expectedEx.expectMessage("You MUST specify at least 1 field to extract.");
        record =
            new MyErrorMapper() {
                public void setWasNotAnnotated(TestRecord testRecord, String value) {
                    fail("May NEVER call this method");
                }
            } .enrich(record);
    }

    // ----------------------------------------------------------------

    @Test
    public void testSetterFailure() {
        expectedEx.expectMessage("A problem occurred while calling the requested setter");
        record =
            new MyErrorMapper() {
                @YauaaField("DeviceClass")
                public void failingSetter(TestRecord testRecord, String value) {
                    throw new IllegalStateException("Just testing the error handling");
                }
            } .enrich(record);
    }

    // ----------------------------------------------------------------

}
