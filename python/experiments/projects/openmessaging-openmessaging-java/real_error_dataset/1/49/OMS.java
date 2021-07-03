/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.openmessaging;

import io.openmessaging.internal.DefaultKeyValue;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The OMS class provides some useful util methods.
 *
 * @author yukon@apache.org
 * @version OMS 1.0
 * @since OMS 1.0
 */
public class OMS {
    /**
     * Returns a default and internal {@code KeyValue} implementation instance.
     *
     * @return a {@code KeyValue} instance
     */
    public static KeyValue newKeyValue() {
        return new DefaultKeyValue();
    }

    /**
     * The version format is X.Y.Z (Major.Minor.Patch), a pre-release version may be denoted by appending a hyphen and a
     * series of dot-separated identifiers immediately following the patch version, like X.Y.Z-alpha.
     *
     * <p>
     * OMS version follows semver scheme partially.
     *
     * @see <a href="http://semver.org">http://semver.org</a>
     */
    public static String specVersion = "UnKnown";

    static {
        InputStream stream = OMS.class.getClassLoader().getResourceAsStream("oms.spec.properties");
        try {
            if (stream != null) {
                Properties properties = new Properties();
                properties.load(stream);
                specVersion = String.valueOf(properties.get("version"));
            }
        } catch (IOException ignore) {
        }
    }
}
