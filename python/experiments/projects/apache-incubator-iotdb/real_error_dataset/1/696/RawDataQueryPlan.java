/*
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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.iotdb.db.qp.physical.crud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.iotdb.db.qp.logical.Operator;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.read.common.Path;
import org.apache.iotdb.tsfile.read.expression.IExpression;

public class RawDataQueryPlan extends QueryPlan {

  private List<Path> deduplicatedPaths = new ArrayList<>();
  private List<TSDataType> deduplicatedDataTypes = new ArrayList<>();
  private IExpression expression = null;
  private Map<String, Set<String>> deviceToMeasurements = new HashMap<>();

  public RawDataQueryPlan() {
    super();
  }

  public RawDataQueryPlan(boolean isQuery, Operator.OperatorType operatorType) {
    super(isQuery, operatorType);
  }

  public IExpression getExpression() {
    return expression;
  }

  public void setExpression(IExpression expression) {
    this.expression = expression;
  }

  public List<Path> getDeduplicatedPaths() {
    return deduplicatedPaths;
  }

  public void addDeduplicatedPaths(Path path) {
    deviceToMeasurements.computeIfAbsent(path.getDevice(), key -> new HashSet<>())
        .add(path.getMeasurement());
    this.deduplicatedPaths.add(path);
  }

  /**
   * used for AlignByDevice Query, the query is executed by each device, So we only maintain
   * measurements of current device.
   */
  public void setDeduplicatedPaths(List<Path> deduplicatedPaths) {
    deviceToMeasurements.clear();
    deduplicatedPaths.forEach(
        path -> deviceToMeasurements.computeIfAbsent(path.getDevice(), key -> new HashSet<>())
            .add(path.getMeasurement()));
    this.deduplicatedPaths = deduplicatedPaths;
  }

  public List<TSDataType> getDeduplicatedDataTypes() {
    return deduplicatedDataTypes;
  }

  public void addDeduplicatedDataTypes(TSDataType dataType) {
    this.deduplicatedDataTypes.add(dataType);
  }

  public void setDeduplicatedDataTypes(
      List<TSDataType> deduplicatedDataTypes) {
    this.deduplicatedDataTypes = deduplicatedDataTypes;
  }

  public Set<String> getAllMeasurementsInDevice(String device) {
    return deviceToMeasurements.getOrDefault(device, Collections.emptySet());
  }

  public void addFilterPathInDeviceToMeasurements(Path path) {
    deviceToMeasurements.computeIfAbsent(path.getDevice(), key -> new HashSet<>())
        .add(path.getMeasurement());
  }

}
