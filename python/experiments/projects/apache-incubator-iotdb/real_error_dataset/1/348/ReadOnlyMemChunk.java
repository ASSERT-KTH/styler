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
package org.apache.iotdb.db.engine.querycontext;

import java.io.IOException;
import java.util.Map;
import org.apache.iotdb.db.query.reader.MemChunkLoader;
import org.apache.iotdb.db.utils.datastructure.TVList;
import org.apache.iotdb.tsfile.common.conf.TSFileDescriptor;
import org.apache.iotdb.tsfile.encoding.encoder.Encoder;
import org.apache.iotdb.tsfile.file.metadata.ChunkMetaData;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding;
import org.apache.iotdb.tsfile.file.metadata.statistics.Statistics;
import org.apache.iotdb.tsfile.read.IPointReader;
import org.apache.iotdb.tsfile.read.TimeValuePair;

//TODO: merge ReadOnlyMemChunk and WritableMemChunk and IWritableMemChunk
public class ReadOnlyMemChunk {

  private String measurementUid;
  private TSDataType dataType;
  private TSEncoding encoding;

  private long version;
  Map<String, String> props;

  private int floatPrecision = TSFileDescriptor.getInstance().getConfig().getFloatPrecision();

  private ChunkMetaData cachedMetaData;

  private TVList chunkData;

  private IPointReader chunkPointReader;

  public ReadOnlyMemChunk(String measurementUid, TSDataType dataType, TSEncoding encoding,
      TVList tvList, Map<String, String> props, long version) throws IOException {
    this.measurementUid = measurementUid;
    this.dataType = dataType;
    this.encoding = encoding;
    this.version = version;
    this.props = props;
    if (props.containsKey(Encoder.MAX_POINT_NUMBER)) {
      this.floatPrecision = Integer.parseInt(props.get(Encoder.MAX_POINT_NUMBER));
    }
    tvList.sort();
    this.chunkData = tvList;
    this.chunkPointReader = tvList.getIterator(floatPrecision, encoding);
    initChunkMeta();
  }

  private void initChunkMeta() throws IOException {
    Statistics statsByType = Statistics.getStatsByType(dataType);
    ChunkMetaData metaData = new ChunkMetaData(measurementUid, dataType, 0, statsByType);
    if (!isEmpty()) {
      IPointReader iterator = chunkData.getIterator(floatPrecision, encoding);
      while (iterator.hasNextTimeValuePair()) {
        TimeValuePair timeValuePair = iterator.nextTimeValuePair();
        switch (dataType) {
          case BOOLEAN:
            statsByType.update(timeValuePair.getTimestamp(), timeValuePair.getValue().getBoolean());
            break;
          case TEXT:
            statsByType.update(timeValuePair.getTimestamp(), timeValuePair.getValue().getBinary());
            break;
          case FLOAT:
            statsByType.update(timeValuePair.getTimestamp(), timeValuePair.getValue().getFloat());
            break;
          case INT32:
            statsByType.update(timeValuePair.getTimestamp(), timeValuePair.getValue().getInt());
            break;
          case INT64:
            statsByType.update(timeValuePair.getTimestamp(), timeValuePair.getValue().getLong());
            break;
          case DOUBLE:
            statsByType.update(timeValuePair.getTimestamp(), timeValuePair.getValue().getDouble());
            break;
          default:
            throw new RuntimeException("Unsupported data types");
        }
      }
    }
    statsByType.setEmpty(isEmpty());
    metaData.setChunkLoader(new MemChunkLoader(this));
    metaData.setVersion(Long.MAX_VALUE);
    cachedMetaData = metaData;
  }

  public TSDataType getDataType() {
    return dataType;
  }

  public boolean isEmpty() throws IOException {
    return !chunkPointReader.hasNextTimeValuePair();
  }

  public ChunkMetaData getChunkMetaData() {
    return cachedMetaData;
  }

  public IPointReader getPointReader() {
    return chunkPointReader;
  }

  public long getVersion() {
    return version;
  }
}
