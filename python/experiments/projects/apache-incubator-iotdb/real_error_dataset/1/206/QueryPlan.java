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

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.iotdb.db.exception.qp.QueryProcessorException;
import org.apache.iotdb.db.qp.executor.IQueryProcessExecutor;
import org.apache.iotdb.db.qp.logical.Operator;
import org.apache.iotdb.db.qp.physical.PhysicalPlan;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.read.common.Path;
import org.apache.iotdb.tsfile.read.expression.IExpression;

public class QueryPlan extends PhysicalPlan {

  private List<Path> paths = null;
  private List<TSDataType> dataTypes = null;
  private IExpression expression = null;

  private boolean isGroupByDevice = false; // for group by device sql
  private List<String> measurementColumnList; // for group by device sql
  private Map<String, Set<String>> measurementColumnsGroupByDevice; // for group by device sql
  private Map<String, TSDataType> dataTypeConsistencyChecker; // for group by device sql

  public QueryPlan() {
    super(true);
    setOperatorType(Operator.OperatorType.QUERY);
  }

  public QueryPlan(boolean isQuery, Operator.OperatorType operatorType) {
    super(isQuery, operatorType);
  }

  /**
   * Check if all paths exist.
   */
  public void checkPaths(IQueryProcessExecutor executor) throws QueryProcessorException {
    for (Path path : paths) {
      if (!executor.judgePathExists(path)) {
        throw new QueryProcessorException("Path doesn't exist: " + path);
      }
    }
  }

  public IExpression getExpression() {
    return expression;
  }

  public void setExpression(IExpression expression) {
    this.expression = expression;
  }

  @Override
  public List<Path> getPaths() {
    return paths;
  }

  public void setPaths(List<Path> paths) {
    this.paths = paths;
  }

  public List<TSDataType> getDataTypes() {
    return dataTypes;
  }

  public void setDataTypes(List<TSDataType> dataTypes) {
    this.dataTypes = dataTypes;
  }

  public boolean isGroupByDevice() {
    return isGroupByDevice;
  }

  public void setGroupByDevice(boolean groupByDevice) {
    isGroupByDevice = groupByDevice;
  }

  public void setMeasurementColumnList(List<String> measurementColumnList) {
    this.measurementColumnList = measurementColumnList;
  }

  public List<String> getMeasurementColumnList() {
    return measurementColumnList;
  }

  public void setMeasurementColumnsGroupByDevice(
      Map<String, Set<String>> measurementColumnsGroupByDevice) {
    this.measurementColumnsGroupByDevice = measurementColumnsGroupByDevice;
  }

  public Map<String, Set<String>> getMeasurementColumnsGroupByDevice() {
    return measurementColumnsGroupByDevice;
  }

  public void setDataTypeConsistencyChecker(
      Map<String, TSDataType> dataTypeConsistencyChecker) {
    this.dataTypeConsistencyChecker = dataTypeConsistencyChecker;
  }

  public Map<String, TSDataType> getDataTypeConsistencyChecker() {
    return dataTypeConsistencyChecker;
  }
}
