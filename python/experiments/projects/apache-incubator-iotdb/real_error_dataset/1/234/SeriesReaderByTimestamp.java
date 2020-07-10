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
package org.apache.iotdb.db.query.reader.seriesrelated;

import java.io.IOException;
import org.apache.iotdb.db.engine.querycontext.QueryDataSource;
import org.apache.iotdb.db.query.context.QueryContext;
import org.apache.iotdb.db.query.reader.IReaderByTimestamp;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.file.metadata.statistics.Statistics;
import org.apache.iotdb.tsfile.read.common.BatchData;
import org.apache.iotdb.tsfile.read.common.Path;
import org.apache.iotdb.tsfile.read.filter.TimeFilter;


public class SeriesReaderByTimestamp implements IReaderByTimestamp {

  private SeriesReader seriesReader;
  private BatchData batchData;

  public SeriesReaderByTimestamp(Path seriesPath, TSDataType dataType, QueryContext context,
      QueryDataSource dataSource) {
    seriesReader = new SeriesReader(seriesPath, dataType, context,
        dataSource, TimeFilter.gtEq(Long.MIN_VALUE), null);
  }

  @Override
  public Object getValueInTimestamp(long timestamp) throws IOException {
    seriesReader.setTimeFilter(timestamp);
    if ((batchData == null || batchData.getTimeByIndex(batchData.length() - 1) < timestamp)
        && !hasNext(timestamp)) {
      return null;
    }

    return batchData.getValueInTimestamp(timestamp);
  }

  private boolean hasNext(long timestamp) throws IOException {
    while (seriesReader.hasNextChunk()) {
      if (!satisfyTimeFilter(seriesReader.currentChunkStatistics())) {
        seriesReader.skipCurrentChunk();
        continue;
      }
      while (seriesReader.hasNextPage()) {
        if (!satisfyTimeFilter(seriesReader.currentPageStatistics())) {
          seriesReader.skipCurrentPage();
          continue;
        }
        if (!seriesReader.isPageOverlapped()) {
          batchData = seriesReader.nextPage();
        } else {
          batchData = seriesReader.nextOverlappedPage();
        }
        if (batchData.getTimeByIndex(batchData.length() - 1) >= timestamp) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean satisfyTimeFilter(Statistics statistics) {
    return seriesReader.getTimeFilter().satisfy(statistics);
  }

}
