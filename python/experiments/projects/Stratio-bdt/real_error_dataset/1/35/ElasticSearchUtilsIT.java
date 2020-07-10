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

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class ElasticSearchUtilsIT {
    private final Logger logger = LoggerFactory
            .getLogger(ElasticSearchUtilsIT.class);

    private ElasticSearchUtils es_utils;

    @BeforeMethod
    public void setSettingsTest() {
        es_utils = new ElasticSearchUtils();
        LinkedHashMap<String, Object> settings_map = new LinkedHashMap<String, Object>();
        settings_map.put("cluster.name", System.getProperty("ES_CLUSTER", "elasticsearch"));
        es_utils.setSettings(settings_map);
        assertThat(es_utils.getSettings().get("cluster.name")).as("Non empty Exception list on boot").isEqualTo(System
                .getProperty("ES_CLUSTER", "elasticsearch"));
    }

    @Test
    public void connectTest() throws IOException {
        es_utils.connect();
        assertThat(es_utils.getClient().info(RequestOptions.DEFAULT).getClusterName()).isNotNull();
        es_utils.getClient().close();
    }

    @Test
    public void createIndexTest() throws UnknownHostException {
        es_utils.connect();
        if (es_utils.indexExists("testindex")) {
            es_utils.dropSingleIndex("testindex");
        }
        es_utils.createSingleIndex("testindex");
        assertThat(es_utils.indexExists("testindex")).isTrue();
    }

    @Test
    public void dropIndexTest() throws IOException {
        es_utils.connect();
        if (!es_utils.indexExists("testindex")) {
            es_utils.createSingleIndex("testindex");
        }
        es_utils.dropSingleIndex("testindex");
        assertThat(es_utils.indexExists("testindex")).isFalse();
        es_utils.getClient().close();
    }

    @Test
    public void dropAllIndexTest() throws IOException {
        es_utils.connect();
        if (!es_utils.indexExists("testindex")) {
            es_utils.createSingleIndex("testindex");
        }
        es_utils.dropAllIndexes();
        assertThat(es_utils.indexExists("testindex")).isFalse();
        es_utils.getClient().close();
    }

    @Test
    public void indexDocument() throws UnknownHostException, IOException {
        es_utils.connect();
        if (es_utils.indexExists("testindex")) {
            es_utils.dropSingleIndex("testindex");
        }
        es_utils.createSingleIndex("testindex");
        XContentBuilder document = jsonBuilder()
                .startObject()
                .field("ident", 1)
                .field("name", "test")
                .field("money", 10.2)
                .field("new", false).endObject();
        try {
            es_utils.indexDocument("testindex",  "1", document);
            Thread.sleep(2000);
            List<JSONObject> results = es_utils.searchSimpleFilterElasticsearchQuery("testindex",
                    "ident", "1",
                    "equals");
            assertThat(results.size()).isEqualTo(1);
            JSONObject result = results.get(0);
            assertThat(result.getInt("ident")).isEqualTo(1);
            assertThat(result.getString("name")).isEqualTo("test");
            assertThat(result.getDouble("money")).isEqualTo(10.2);
            assertThat(result.getBoolean("new")).isEqualTo(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void deleteDocument() throws UnknownHostException, IOException {
        es_utils.connect();
        if (es_utils.indexExists("testindex")) {
            es_utils.dropSingleIndex("testindex");
        }
        es_utils.createSingleIndex("testindex");
        XContentBuilder document = jsonBuilder()
                .startObject()
                .field("ident", 1)
                .field("name", "test")
                .field("money", 10.2)
                .field("new", false).endObject();
        try {
            es_utils.indexDocument("testindex",  "1", document);
            Thread.sleep(2000);
            List<JSONObject> results = es_utils.searchSimpleFilterElasticsearchQuery("testindex",
                    "ident", "1",
                    "equals");
            assertThat(results.size()).isEqualTo(1);
            es_utils.deleteDocument("testindex",  "1");
            Thread.sleep(2000);
            List<JSONObject> results2 = es_utils.searchSimpleFilterElasticsearchQuery("testindex",
                    "ident", "1",
                    "equals");
            assertThat(results2.size()).isEqualTo(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
