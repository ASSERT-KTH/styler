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
package com.stratio.qa.aspects;


import com.stratio.qa.exceptions.IncludeException;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;


public class IncludeTagAspectTest {
    public LoopIncludeTagAspect inctag = new LoopIncludeTagAspect();

    @Test
    public void testGetFeature() {
        assertThat("test.feature").as("Test feature name is extracted correctly").isEqualTo(inctag.getFeatureName("@include(feature: test.feature,scenario: To copy)"));
    }

    @Test
    public void testGetScenario() {
        assertThat("To copy").as("Test scenario name is extracted correctly").isEqualTo(inctag.getScenName("@include(feature: test.feature,scenario: To copy)"));
    }

    @Test
    public void testGetParams() {
        assertThat(4).as("Test that the number of keys and values are correctly calculated for params").isEqualTo(inctag.getParams("@include(feature: test.feature,scenario: To copy,params: [time1:9, time2:9])").length);
    }

    @Test
    public void testDoReplaceKeys() throws IncludeException {
        String keysNotReplaced = "Given that <time1> is not equal to <time2> into a step";
        String[] keys = {"<time1>", "9", "<time2>", "8"};
        assertThat("Given that 9 is not equal to 8 into a step").as("Test that keys are correctly replaced at scenario outlines").isEqualTo(inctag.doReplaceKeys(keysNotReplaced, keys));
    }

    @Test
    public void testCheckParams() throws IncludeException {
        String lineOfParams = "| hey | ho |";
        String[] keys = {"<time1>", "9", "<time2>", "8"};
        String[] tonsOfKeys = {"<time1>", "9", "<time2>", "23", "33", "32", "10"};
        assertThat(inctag.checkParams(lineOfParams, keys)).as("Test that include parameters match the number of them at the scenario outline included").isTrue();
        assertThat(inctag.checkParams(lineOfParams, tonsOfKeys)).as("Test that include parameters match the number of them at the scenario outline included").isFalse();
    }

    @Test
    public void testTagIterationSkip() throws Exception {
        String path = "";
        List<String> lines = new ArrayList<>();
        lines.add("@include(testCheckParams)");

        assertThatExceptionOfType(Exception.class).isThrownBy(() -> inctag.parseLines(lines, path));
    }
}