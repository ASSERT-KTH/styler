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

import com.stratio.qa.utils.ThreadProperty;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MandatoryAspectTest {

    @Test
    public void noMandatoryTag() {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        MandatoryAspect mandAspect = new MandatoryAspect();
        List<String> tagList = new ArrayList<>();

        assertThat(mandAspect.manageTags(tagList)).as("No mandatory tag should make feature run.").isEqualTo(true);
    }

    @Test
    public void noVarsTag() {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        MandatoryAspect mandAspect = new MandatoryAspect();
        List<String> tagList = new ArrayList<>();
        tagList.add(0, "@mandatory");

        assertThat(mandAspect.manageTags(tagList)).as("No vars tag should make feature run.").isEqualTo(true);
    }

    @Test
    public void onlyVarsTag() {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        MandatoryAspect mandAspect = new MandatoryAspect();
        List<String> tagList = new ArrayList<>();
        tagList.add(0, "@vars");

        assertThat(mandAspect.manageTags(tagList)).as("No mandatory tag with vars tag should make feature run.").isEqualTo(true);
    }

    @Test
    public void notDefinedVariableButVariablesDefined() {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        MandatoryAspect mandAspect = new MandatoryAspect();
        System.setProperty("SYSTEM", "system.domain");
        List<String> tagList = new ArrayList<>();
        tagList.add(0, "@mandatory");
        tagList.add(1, "@vars(SSH)");

        assertThat(mandAspect.manageTags(tagList)).as("Not defined variable should make feature not to run.").isEqualTo(false);
        System.clearProperty("SYSTEM");
    }

    @Test
    public void notDefinedVariable() {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        MandatoryAspect mandAspect = new MandatoryAspect();
        List<String> tagList = new ArrayList<>();
        tagList.add(0, "@mandatory");
        tagList.add(1, "@vars(SSH)");

        assertThat(mandAspect.manageTags(tagList)).as("Not defined variable should make feature not to run.").isEqualTo(false);
    }

    @Test
    public void definedVariable() {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        MandatoryAspect mandAspect = new MandatoryAspect();
        System.setProperty("SSH", "system.domain");
        List<String> tagList = new ArrayList<>();
        tagList.add(0, "@mandatory");
        tagList.add(1, "@vars(SSH)");

        assertThat(mandAspect.manageTags(tagList)).as("Defined variable should make feature to run.").isEqualTo(true);
        System.clearProperty("SSH");
    }
}
