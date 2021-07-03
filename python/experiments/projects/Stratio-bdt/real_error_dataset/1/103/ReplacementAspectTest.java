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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import org.aspectj.lang.ProceedingJoinPoint;
import org.testng.annotations.Test;

import com.stratio.qa.exceptions.NonReplaceableException;
import com.stratio.qa.utils.ThreadProperty;

public class ReplacementAspectTest {

    @Test
    public void replaceEmptyPlaceholdersTest() throws NonReplaceableException {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        ReplacementAspect repAspect = new ReplacementAspect();
        ProceedingJoinPoint pjp = null;
        assertThat(repAspect.replaceEnvironmentPlaceholders("", pjp)).as("Replacing an empty placeholded string should not modify it").isEqualTo("");
    }

    @Test
    public void replaceSinglePlaceholdersTest() throws NonReplaceableException {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        ReplacementAspect repAspect = new ReplacementAspect();
        ProceedingJoinPoint pjp = null;
        System.setProperty("STRATIOBDD_ENV1", "33");
        System.setProperty("STRATIOBDD_ENV2", "aa");

        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1}", pjp))
                .as("Unexpected replacement").isEqualTo("33");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1}${STRATIOBDD_ENV2}", pjp))
                .as("Unexpected replacement").isEqualTo("33aa");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1}:${STRATIOBDD_ENV2}", pjp))
                .as("Unexpected replacement").isEqualTo("33:aa");
        assertThat(repAspect.replaceEnvironmentPlaceholders("|${STRATIOBDD_ENV1}|:|${STRATIOBDD_ENV2}|", pjp))
                .as("Unexpected replacement").isEqualTo("|33|:|aa|");
    }

    @Test
    public void replaceSinglePlaceholderCaseTest() throws NonReplaceableException {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        ReplacementAspect repAspect = new ReplacementAspect();
        ProceedingJoinPoint pjp = null;
        System.setProperty("STRATIOBDD_ENV1", "33");
        System.setProperty("STRATIOBDD_ENV2", "aA");

        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1.toUpper}", pjp)).as("Unexpected replacement").isEqualTo("33");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1.toLower}", pjp)).as("Unexpected replacement").isEqualTo("33");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV2.toUpper}", pjp)).as("Unexpected replacement").isEqualTo("AA");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV2.toLower}", pjp)).as("Unexpected replacement").isEqualTo("aa");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1}${STRATIOBDD_ENV2.toLower}", pjp)).as("Unexpected replacement").isEqualTo("33aa");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1}:${STRATIOBDD_ENV2.toUpper}", pjp)).as("Unexpected replacement").isEqualTo("33:AA");
        assertThat(repAspect.replaceEnvironmentPlaceholders("|${STRATIOBDD_ENV2}.toUpper", pjp)).as("Unexpected replacement").isEqualTo("|aA.toUpper");
    }

    @Test
    public void replaceElementPlaceholderCaseTest() throws NonReplaceableException {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        ReplacementAspect repAspect = new ReplacementAspect();
        ProceedingJoinPoint pjp = null;
        System.setProperty("STRATIOBDD_ENV4", "33");
        System.setProperty("STRATIOBDD_ENV5", "aA");
        ThreadProperty.set("STRATIOBDD_ENV6", "44");
        ThreadProperty.set("STRATIOBDD_ENV7", "cC");

        assertThat(repAspect.replacedElement("${STRATIOBDD_ENV4}", pjp)).isEqualTo("33");
        assertThat(repAspect.replacedElement("${STRATIOBDD_ENV5.toLower}", pjp)).isEqualTo("aa");
        assertThat(repAspect.replacedElement("${STRATIOBDD_ENV5.toUpper}", pjp)).isEqualTo("AA");
        assertThat(repAspect.replacedElement("${STRATIOBDD_ENV5}", pjp)).isEqualTo("aA");
        assertThat(repAspect.replacedElement("${STRATIOBDD_ENV4}${STRATIOBDD_ENV5}", pjp)).isEqualTo("33aA");
        assertThat(repAspect.replacedElement("${STRATIOBDD_ENV4}:${STRATIOBDD_ENV5}", pjp)).isEqualTo("33:aA");

        assertThat(repAspect.replacedElement("!{STRATIOBDD_ENV6}", pjp)).isEqualTo("44");
        assertThat(repAspect.replacedElement("!{STRATIOBDD_ENV7}", pjp)).isEqualTo("cC");
        assertThat(repAspect.replacedElement("!{STRATIOBDD_ENV6}!{STRATIOBDD_ENV7}", pjp)).isEqualTo("44cC");
        assertThat(repAspect.replacedElement("!{STRATIOBDD_ENV6}:!{STRATIOBDD_ENV7}", pjp)).isEqualTo("44:cC");

        assertThat(repAspect.replacedElement("@{JSON.schemas/simple1.json}", pjp)).isEqualTo("{\"a\":true}");
    }

    @Test
    public void replaceReflectionPlaceholderCaseTest() throws NonReplaceableException {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        ReplacementAspect repAspect = new ReplacementAspect();
        ProceedingJoinPoint pjp = null;

        assertThatExceptionOfType(Exception.class).isThrownBy(() -> repAspect.replaceReflectionPlaceholders("!{NO_VAL}", pjp));
    }

    @Test
    public void replaceCodePlaceholderCaseTest() throws NonReplaceableException {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        ReplacementAspect repAspect = new ReplacementAspect();
        ProceedingJoinPoint pjp = null;

        assertThat(repAspect.replaceCodePlaceholders("@{schemas/simple1.json}", pjp)).isEqualTo("");
        assertThat(repAspect.replaceCodePlaceholders("@{JSON.schemas/simple1.json}", pjp)).isEqualTo("{\"a\":true}");
        assertThatExceptionOfType(Exception.class).isThrownBy(() -> repAspect.replaceCodePlaceholders("@{IP.10.10.10.10}", pjp));
    }

    @Test
    public void replaceMixedPlaceholdersTest() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        ThreadProperty.set("STRATIOBDD_LOCAL1", "LOCAL");
        ProceedingJoinPoint pjp = null;
        ReplacementAspect repAspect = new ReplacementAspect();
        System.setProperty("STRATIOBDD_ENV2", "aa");

        assertThat(repAspect.replaceReflectionPlaceholders(repAspect.replaceEnvironmentPlaceholders("!{STRATIOBDD_LOCAL1}:${STRATIOBDD_ENV2}", pjp), pjp))
                .as("Unexpected replacement").isEqualTo("LOCAL:aa");
        assertThat(repAspect.replaceReflectionPlaceholders(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV2}:!{STRATIOBDD_LOCAL1}", pjp), pjp))
                .as("Unexpected replacement").isEqualTo("aa:LOCAL");
    }

    @Test
    public void replaceDefaultValue() throws Exception {
        ThreadProperty.set("class", this.getClass().getCanonicalName());
        ProceedingJoinPoint pjp = null;
        ReplacementAspect repAspect = new ReplacementAspect();
        System.setProperty("STRATIOBDD_ENV1", "aa");
        System.setProperty("STRATIOBDD_ENV3", "cc");

        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1:-bb}", pjp)).as("Unexpected replacement").isEqualTo("aa");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV2:-bb}", pjp)).as("Unexpected replacement").isEqualTo("bb");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV2:-bb}${STRATIOBDD_ENV4:-dd}", pjp)).as("Unexpected replacement").isEqualTo("bbdd");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV2:-bb}${STRATIOBDD_ENV1}", pjp)).as("Unexpected replacement").isEqualTo("bbaa");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1}${STRATIOBDD_ENV2:-bb}", pjp)).as("Unexpected replacement").isEqualTo("aabb");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1}${STRATIOBDD_ENV2:-bb}${STRATIOBDD_ENV3}", pjp)).as("Unexpected replacement").isEqualTo("aabbcc");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1.toUpper}${STRATIOBDD_ENV2:-bb}${STRATIOBDD_ENV3}", pjp)).as("Unexpected replacement").isEqualTo("AAbbcc");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1}${STRATIOBDD_ENV2.toUpper:-bb}${STRATIOBDD_ENV3}", pjp)).as("Unexpected replacement").isEqualTo("aaBBcc");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1}${STRATIOBDD_ENV2:-bb}${STRATIOBDD_ENV3:-aa}", pjp)).as("Unexpected replacement").isEqualTo("aabbcc");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1}${STRATIOBDD_ENV2:-bb}${STRATIOBDD_ENV3.toUpper:-aa}", pjp)).as("Unexpected replacement").isEqualTo("aabbCC");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1}${STRATIOBDD_ENV2:-bb.bb}${STRATIOBDD_ENV3:-aa}", pjp)).as("Unexpected replacement").isEqualTo("aabb.bbcc");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV1}${STRATIOBDD_ENV2:-bb}${STRATIOBDD_ENV3:-aa.aa}", pjp)).as("Unexpected replacement").isEqualTo("aabbcc");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV2:-\"\"}", pjp)).as("Unexpected replacement").isEqualTo("");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV2:-{}}", pjp)).as("Unexpected replacement").isEqualTo("{}");
        assertThat(repAspect.replaceEnvironmentPlaceholders("${STRATIOBDD_ENV2:-[]}", pjp)).as("Unexpected replacement").isEqualTo("[]");
    }
}
