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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class TestAnnotationSystem {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    public static class TestRecord implements Serializable {
        final String useragent;
        String deviceClass;
        String agentNameVersion;

        public TestRecord(String useragent) {
            this.useragent = useragent;
        }
    }

    public static class MyBaseMapper
        implements UserAgentAnnotationMapper<TestRecord>, Serializable {
        private final transient UserAgentAnnotationAnalyzer<TestRecord> userAgentAnalyzer;

        public MyBaseMapper() {
            userAgentAnalyzer = new UserAgentAnnotationAnalyzer<>();
            userAgentAnalyzer.initialize(this);
        }

        public TestRecord enrich(TestRecord record) {
            return userAgentAnalyzer.map(record);
        }

        @Override
        public String getUserAgentString(TestRecord record) {
            return record.useragent;
        }
    }

    // ----------------------------------------------------------------

    public static class MyMapper extends MyBaseMapper {
        @YauaaField("DeviceClass")
        public void setDeviceClass(TestRecord record, String value) {
            record.deviceClass = value;
        }

        @YauaaField("AgentNameVersion")
        public void setAgentNameVersion(TestRecord record, String value) {
            record.agentNameVersion = value;
        }
    }

    @Test
    public void testAnnotationBasedParser() {
        expectedEx = ExpectedException.none();

        MyMapper mapper = new MyMapper();

        TestRecord record = new TestRecord("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/48.0.2564.82 Safari/537.36");

        record = mapper.enrich(record);

        assertEquals("Desktop", record.deviceClass);
        assertEquals("Chrome 48.0.2564.82", record.agentNameVersion);
    }

    // ----------------------------------------------------------------

    public static class ImpossibleFieldMapper extends MyBaseMapper {
        @YauaaField("NielsBasjes")
        public void setImpossibleField(TestRecord record, String value) {
            record.agentNameVersion = value;
        }
    }

    @Test
    public void testImpossibleField() {
        expectedEx.expect(InvalidParserConfigurationException.class);
        expectedEx.expectMessage("We cannot provide these fields:[NielsBasjes]");
        new ImpossibleFieldMapper();
    }

    // ----------------------------------------------------------------

    public static class InaccessibleSetterMapper  extends MyBaseMapper {
        @YauaaField("DeviceClass")
        private void inaccessibleSetter(TestRecord record, String value) {
            fail("May NEVER call this method");
        }
    }

    @Test
    public void testInaccessibleSetter() {
        expectedEx.expect(InvalidParserConfigurationException.class);
        expectedEx.expectMessage("Method annotated with YauaaField is not public: inaccessibleSetter");
        new InaccessibleSetterMapper();
    }

    // ----------------------------------------------------------------

    public static class TooManyParameters extends MyBaseMapper {
        @YauaaField("DeviceClass")
        public void wrongSetter(TestRecord record, String value, String extra) {
            fail("May NEVER call this method");
        }
    }

    @Test
    public void testTooManyParameters() {
        expectedEx.expect(InvalidParserConfigurationException.class);
        expectedEx.expectMessage(containsString(
            "the method [wrongSetter] has been annotated with YauaaField but it has the wrong method signature. " +
            "It must look like [ public void wrongSetter(TestRecord record, String value) ]"));
        new TooManyParameters();
    }

    // ----------------------------------------------------------------

    public static class WrongTypeParameters1 extends MyBaseMapper {
        @YauaaField("DeviceClass")
        public void wrongSetter(String record, String value) {
            fail("May NEVER call this method");
        }
    }

    @Test
    public void testWrongTypeParameters1() {
        expectedEx.expect(InvalidParserConfigurationException.class);
        expectedEx.expectMessage(containsString(
            "the method [wrongSetter] has been annotated with YauaaField but it has the wrong method signature. " +
            "It must look like [ public void wrongSetter(TestRecord record, String value) ]"));
        new WrongTypeParameters1();
    }

    // ----------------------------------------------------------------

    public static class WrongTypeParameters2 extends MyBaseMapper {
        @YauaaField("DeviceClass")
        public void wrongSetter(TestRecord record, Double value) {
            fail("May NEVER call this method");
        }
    }

    @Test
    public void testWrongTypeParameters2() {
        expectedEx.expect(InvalidParserConfigurationException.class);
        expectedEx.expectMessage(containsString(
            "the method [wrongSetter] has been annotated with YauaaField but it has the wrong method signature. " +
            "It must look like [ public void wrongSetter(TestRecord record, String value) ]"));
        new WrongTypeParameters2();
    }

    // ----------------------------------------------------------------

    public static class MissingAnnotations extends MyBaseMapper {
        public void setWasNotAnnotated(TestRecord record, Double value) {
            fail("May NEVER call this method");
        }
    }

    @Test
    public void testMissingAnnotations() {
        expectedEx.expect(InvalidParserConfigurationException.class);
        expectedEx.expectMessage("You MUST specify at least 1 field to extract.");
        new MissingAnnotations();
    }

    // ----------------------------------------------------------------

    public static class WrongReturnType extends MyBaseMapper {
        @YauaaField("DeviceClass")
        public boolean nonVoidSetter(TestRecord record, String value) {
            fail("May NEVER call this method");
            return true;
        }
    }

    @Test
    public void testNonVoidSetter() {
        expectedEx.expect(InvalidParserConfigurationException.class);
        expectedEx.expectMessage(containsString(
            "the method [nonVoidSetter] has been annotated with YauaaField but it has the wrong method signature. " +
            "It must look like [ public void nonVoidSetter(TestRecord record, String value) ]"));
        new WrongReturnType();
    }

    // ----------------------------------------------------------------

    private static final class PrivateTestRecord implements Serializable {
        final String useragent;
        String deviceClass;
        String agentNameVersion;

        private PrivateTestRecord(String useragent) {
            this.useragent = useragent;
        }
    }

    public static class PrivateMyBaseMapper
        implements UserAgentAnnotationMapper<PrivateTestRecord>, Serializable {
        private final transient UserAgentAnnotationAnalyzer<PrivateTestRecord> userAgentAnalyzer;

        public PrivateMyBaseMapper() {
            userAgentAnalyzer = new UserAgentAnnotationAnalyzer<>();
            userAgentAnalyzer.initialize(this);
        }

        public PrivateTestRecord enrich(PrivateTestRecord record) {
            return userAgentAnalyzer.map(record);
        }

        @Override
        public String getUserAgentString(PrivateTestRecord record) {
            return record.useragent;
        }
    }

    private static class InaccessibleSetterMapperClass extends PrivateMyBaseMapper {
        @YauaaField("DeviceClass")
        public void correctSetter(PrivateTestRecord record, String value) {
            fail("May NEVER call this method");
        }
    }

    @Test
    public void testInaccessibleSetterClass() {
        expectedEx.expect(InvalidParserConfigurationException.class);
        expectedEx.expectMessage("The class nl.basjes.parse.useragent.annotate.TestAnnotationSystem.PrivateTestRecord is not public.");
        new InaccessibleSetterMapperClass();
    }

    // ----------------------------------------------------------------

    @SuppressWarnings("unchecked") // Here we deliberately created some bad code to check the behavior.
    @Test
    public void testBadGeneric(){
        expectedEx.expect(InvalidParserConfigurationException.class);
        expectedEx.expectMessage("[Map] The mapper instance is null.");
        UserAgentAnnotationAnalyzer userAgentAnalyzer = new UserAgentAnnotationAnalyzer();
        assertNull(userAgentAnalyzer.map("Foo"));
    }



}
