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

import com.stratio.qa.specs.BaseGSpec;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ZookeeperSecUtilsIT extends BaseGSpec {

    @Test
    public void zookeeperSecTest() throws Exception {
        ZookeeperSecUtils zkUtils = new ZookeeperSecUtils();

        // Connect
        zkUtils.connectZk();

        // createNonEphemeralZnode
        String znodePath = "/mypath";
        if (zkUtils.exists(znodePath)) {
            zkUtils.delete(znodePath);
        }
        zkUtils.zCreate(znodePath, false);
        assertThat(zkUtils.exists(znodePath)).isTrue();

        // createAnEphemeralZnode
        znodePath = "/mypath3";
        if (zkUtils.exists(znodePath)) {
            zkUtils.delete(znodePath);
        }
        zkUtils.zCreate(znodePath, true);
        assertThat(zkUtils.exists(znodePath)).isTrue();

        // deleteANonEphemeralZnode
        znodePath = "/mypath5";
        if (zkUtils.exists(znodePath)) {
            zkUtils.delete(znodePath);
        }
        zkUtils.zCreate(znodePath, false);
        zkUtils.delete(znodePath);
        assertThat(zkUtils.exists(znodePath)).isFalse();

        // deleteAnEphemeralZnode
        znodePath = "/mypath6";
        if (zkUtils.exists(znodePath)) {
            zkUtils.delete(znodePath);
        }
        zkUtils.zCreate(znodePath, true);
        zkUtils.delete(znodePath);
        assertThat(zkUtils.exists(znodePath)).isFalse();

        // verifyWriteAndReadDataToAnEphemeralZnode
        String znodeContent = "hello";
        znodePath = "/mypath7";
        if (zkUtils.exists(znodePath)) {
            zkUtils.delete(znodePath);
        }
        zkUtils.zCreate(znodePath, znodeContent, false);
        assertThat(zkUtils.zRead(znodePath)).isEqualToIgnoringCase(znodeContent);
        zkUtils.delete(znodePath);

        // Disconnect
        zkUtils.disconnect();
    }

    @Test
    public void zookeeperSecConnectionTest() throws Exception {
        System.setProperty("SECURIZED_ZOOKEEPER", "true");
        String zk_hosts = System.getProperty("ZOOKEEPER_HOSTS", "localhost");
        int timeout = Integer.parseInt(System.getProperty("ZOOKEEPER_SESSION_TIMEOUT", "1"));
        ZookeeperSecUtils zkUtils = new ZookeeperSecUtils(zk_hosts, timeout);

        // Connect
        zkUtils.connectZk();

        // createNonEphemeralZnode
        String znodePath = "/mypath";
        if (zkUtils.exists(znodePath)) {
            zkUtils.delete(znodePath);
        }
        zkUtils.zCreate(znodePath, false);
        assertThat(zkUtils.exists(znodePath)).isTrue();

        // createAnEphemeralZnode
        znodePath = "/mypath3";
        if (zkUtils.exists(znodePath)) {
            zkUtils.delete(znodePath);
        }
        zkUtils.zCreate(znodePath, true);
        assertThat(zkUtils.exists(znodePath)).isTrue();

        // deleteANonEphemeralZnode
        znodePath = "/mypath5";
        if (zkUtils.exists(znodePath)) {
            zkUtils.delete(znodePath);
        }
        zkUtils.zCreate(znodePath, false);
        zkUtils.delete(znodePath);
        assertThat(zkUtils.exists(znodePath)).isFalse();

        // deleteAnEphemeralZnode
        znodePath = "/mypath6";
        if (zkUtils.exists(znodePath)) {
            zkUtils.delete(znodePath);
        }
        zkUtils.zCreate(znodePath, true);
        zkUtils.delete(znodePath);
        assertThat(zkUtils.exists(znodePath)).isFalse();

        // verifyWriteAndReadDataToAnEphemeralZnode
        String znodeContent = "hello";
        znodePath = "/mypath7";
        if (zkUtils.exists(znodePath)) {
            zkUtils.delete(znodePath);
        }
        zkUtils.zCreate(znodePath, znodeContent, false);
        assertThat(zkUtils.zRead(znodePath)).isEqualToIgnoringCase(znodeContent);
        zkUtils.delete(znodePath);

        // Disconnect
        zkUtils.disconnect();
        System.setProperty("SECURIZED_ZOOKEEPER", "false");
    }
}
