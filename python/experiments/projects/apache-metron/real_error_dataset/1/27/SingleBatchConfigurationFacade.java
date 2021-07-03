/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.metron.common.configuration.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SingleBatchConfigurationFacade implements WriterConfiguration {
  private WriterConfiguration config;
  public SingleBatchConfigurationFacade(WriterConfiguration config) {
    this.config = config;
  }

  @Override
  public int getBatchSize(String sensorName) {
    return 1;
  }

  @Override
  public int getBatchTimeout(String sensorName) {
    return 0;
  }

  @Override
  public List<Integer> getAllConfiguredTimeouts() {
    // null implementation since batching is disabled
    return new ArrayList<Integer>();
  }

  @Override
  public String getIndex(String sensorName) {
    return config.getIndex(sensorName);
  }

  @Override
  public boolean isEnabled(String sensorName) {
    return true;
  }

  @Override
  public Map<String, Object> getSensorConfig(String sensorName) {
    return config.getSensorConfig(sensorName);
  }

  @Override
  public Map<String, Object> getGlobalConfig() {
    return config.getGlobalConfig();
  }

  @Override
  public boolean isDefault(String sensorName) {
    return false;
  }

  @Override
  public String getFieldNameConverter(String sensorName) {
    // not applicable
    return null;
  }
}
