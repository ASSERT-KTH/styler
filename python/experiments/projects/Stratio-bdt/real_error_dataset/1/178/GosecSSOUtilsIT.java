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

package com.stratio.qa.utils;

import org.testng.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;


public class GosecSSOUtilsIT {
    private final Logger logger = LoggerFactory.getLogger(GosecSSOUtilsIT.class);
    private static GosecSSOUtils gosecSsoUtils = new GosecSSOUtils("www.google.com",
            "anyUser", "anyPassWord");

    @Test
    public void gosecUtilsConstructorTest() throws Exception {
        assertThat(gosecSsoUtils.ssoHost).isEqualTo("www.google.com");
        assertThat(gosecSsoUtils.userName).isEqualTo("anyUser");
        assertThat(gosecSsoUtils.passWord).isEqualTo("anyPassWord");
    }

    @Test
    public void gosecUtilsFakeTokenGeneratorTest() throws Exception {
        assertThat(gosecSsoUtils.ssoTokenGenerator().size()).isEqualTo(0);
    }

}
