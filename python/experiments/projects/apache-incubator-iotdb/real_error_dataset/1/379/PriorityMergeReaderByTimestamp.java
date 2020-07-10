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
package org.apache.iotdb.db.query.reader.universal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.iotdb.db.query.reader.series.IReaderByTimestamp;

/**
 * This class implements {@link IReaderByTimestamp} for data sources with different priorities.
 */
public class PriorityMergeReaderByTimestamp implements IReaderByTimestamp {

  private List<IReaderByTimestamp> readerList = new ArrayList<>();
  private List<Integer> priorityList = new ArrayList<>();

  public void addReaderWithPriority(IReaderByTimestamp reader, int priority) {
    readerList.add(reader);
    priorityList.add(priority);
  }

  @Override
  public Object getValueInTimestamp(long timestamp) throws IOException {
    Object value = null;
    for (int i = readerList.size() - 1; i >= 0; i--) {
      value = readerList.get(i).getValueInTimestamp(timestamp);
      if (value != null) {
        // Note that the remaining readers do not perform getValueInTimestamp. As a result,
        // the traditional implementation of hasNext will lead to unregulated results.
        return value;
      }
    }
    return value;
  }
}
