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
package org.apache.iotdb.db.query.dataset.groupby;

import org.apache.iotdb.db.qp.physical.crud.GroupByPlan;
import org.apache.iotdb.db.query.context.QueryContext;
import org.apache.iotdb.db.utils.TestOnly;
import org.apache.iotdb.tsfile.read.query.dataset.QueryDataSet;
import org.apache.iotdb.tsfile.utils.Pair;

public abstract class GroupByEngineDataSet extends QueryDataSet {

  protected long queryId;
  private long interval;
  private long slidingStep;
  // total query [startTime, endTime)
  private long startTime;
  private long endTime;

  // current interval [curStartTime, curEndTime)
  protected long curStartTime;
  protected long curEndTime;
  private int usedIndex;
  protected boolean hasCachedTimeInterval;

  /**
   * groupBy query.
   */
  public GroupByEngineDataSet(QueryContext context, GroupByPlan groupByPlan) {
    super(groupByPlan.getDeduplicatedPaths(), groupByPlan.getDeduplicatedDataTypes());
    this.queryId = context.getQueryId();
    this.interval = groupByPlan.getInterval();
    this.slidingStep = groupByPlan.getSlidingStep();
    this.startTime = groupByPlan.getStartTime();
    this.endTime = groupByPlan.getEndTime();

    // init group by time partition
    this.usedIndex = 0;
    this.hasCachedTimeInterval = false;
    this.curEndTime = -1;
  }

  @Override
  protected boolean hasNextWithoutConstraint() {
    // has cached
    if (hasCachedTimeInterval) {
      return true;
    }

    curStartTime = usedIndex * slidingStep + startTime;
    usedIndex++;
    //This is an open interval , [0-100)
    if (curStartTime < endTime) {
      hasCachedTimeInterval = true;
      curEndTime = Math.min(curStartTime + interval, endTime);
      return true;
    } else {
      return false;
    }
  }

  @TestOnly
  public Pair<Long, Long> nextTimePartition() {
    hasCachedTimeInterval = false;
    return new Pair<>(curStartTime, curEndTime);
  }
}
