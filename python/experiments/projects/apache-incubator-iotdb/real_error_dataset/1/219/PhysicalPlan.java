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
package org.apache.iotdb.db.qp.physical;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import org.apache.iotdb.db.qp.logical.Operator;
import org.apache.iotdb.db.qp.physical.crud.BatchInsertPlan;
import org.apache.iotdb.db.qp.physical.crud.DeletePlan;
import org.apache.iotdb.db.qp.physical.crud.InsertPlan;
import org.apache.iotdb.tsfile.read.common.Path;
import org.apache.iotdb.tsfile.utils.ReadWriteIOUtils;

/**
 * This class is a abstract class for all type of PhysicalPlan.
 */
public abstract class PhysicalPlan {

  private boolean isQuery;
  private Operator.OperatorType operatorType;
  private static final int NULL_VALUE_LEN = -1;

  protected PhysicalPlan(boolean isQuery) {
    this.isQuery = isQuery;
  }

  protected PhysicalPlan(boolean isQuery, Operator.OperatorType operatorType) {
    this.isQuery = isQuery;
    this.operatorType = operatorType;
  }

  public String printQueryPlan() {
    return "abstract plan";
  }

  public abstract List<Path> getPaths();

  public boolean isQuery() {
    return isQuery;
  }

  public Operator.OperatorType getOperatorType() {
    return operatorType;
  }

  public void setOperatorType(Operator.OperatorType operatorType) {
    this.operatorType = operatorType;
  }

  public List<String> getAggregations() {
    return Collections.emptyList();
  }

  public void setQuery(boolean query) {
    isQuery = query;
  }

  public void serializeTo(ByteBuffer buffer) {
    throw new UnsupportedOperationException("serialize of unimplemented");
  }

  public void deserializeFrom(ByteBuffer buffer) {
    throw new UnsupportedOperationException("serialize of unimplemented");
  }

  protected void putString(ByteBuffer buffer, String value) {
    if (value == null) {
      buffer.putInt(NULL_VALUE_LEN);
    } else {
      ReadWriteIOUtils.write(value, buffer);
    }
  }

  protected String readString(ByteBuffer buffer) {
    int valueLen = buffer.getInt();
    if (valueLen == NULL_VALUE_LEN) {
      return null;
    }
    return ReadWriteIOUtils.readStringWithLength(buffer, valueLen);
  }

  public static class Factory {

    private Factory() {
      // hidden initializer
    }

    public static PhysicalPlan create(ByteBuffer buffer) throws IOException {
      int typeNum = buffer.get();
      if (typeNum >= PhysicalPlanType.values().length) {
        throw new IOException("unrecognized log type " + typeNum);
      }
      PhysicalPlanType type = PhysicalPlanType.values()[typeNum];
      PhysicalPlan plan;
      switch (type) {
        case INSERT:
          plan = new InsertPlan();
          plan.deserializeFrom(buffer);
          break;
        case DELETE:
          plan = new DeletePlan();
          plan.deserializeFrom(buffer);
          break;
        case BATCHINSERT:
          plan = new BatchInsertPlan();
          plan.deserializeFrom(buffer);
          break;
        default:
          throw new IOException("unrecognized log type " + type);
      }
      return plan;
    }
  }

  public enum PhysicalPlanType {
    INSERT, DELETE, BATCHINSERT
  }


}
