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
package org.apache.iotdb.tsfile.file.metadata.statistics;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.apache.iotdb.tsfile.exception.filter.StatisticsClassException;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.utils.BytesUtils;
import org.apache.iotdb.tsfile.utils.ReadWriteIOUtils;

public class BooleanStatistics extends Statistics<Boolean> {

  private boolean firstValue;
  private boolean lastValue;

  @Override
  public TSDataType getType() {
    return TSDataType.BOOLEAN;
  }

  @Override
  public int getStatsSize() {
    return 2;
  }

  /**
   * initialize boolean Statistics.
   *
   * @param firstValue first boolean value
   * @param lastValue last boolean value
   */
  private void initializeStats(boolean firstValue, boolean lastValue) {
    this.firstValue = firstValue;
    this.lastValue = lastValue;
  }

  private void updateStats(boolean firstValue, boolean lastValue) {
    this.lastValue = lastValue;
  }

  @Override
  void updateStats(boolean value) {
    if (isEmpty) {
      initializeStats(value, value);
      isEmpty = false;
    } else {
      updateStats(value, value);
    }
  }

  @Override
  void updateStats(boolean[] values, int batchSize) {
    for (int i = 0; i < batchSize; i++) {
      updateStats(values[i]);
    }
  }

  @Override
  public void setMinMaxFromBytes(byte[] minBytes, byte[] maxBytes) {
  }

  @Override
  public Boolean getMinValue() {
    throw new StatisticsClassException("Boolean statistics does not support: min");
  }

  @Override
  public Boolean getMaxValue() {
    throw new StatisticsClassException("Boolean statistics does not support: max");
  }

  @Override
  public Boolean getFirstValue() {
    return firstValue;
  }

  @Override
  public Boolean getLastValue() {
    return lastValue;
  }

  @Override
  public double getSumValue() {
    throw new StatisticsClassException("Boolean statistics does not support: sum");
  }

  @Override
  public ByteBuffer getMinValueBuffer() {
    throw new StatisticsClassException("Boolean statistics do not support: min");
  }

  @Override
  public ByteBuffer getMaxValueBuffer() {
    throw new StatisticsClassException("Boolean statistics do not support: max");
  }

  @Override
  public ByteBuffer getFirstValueBuffer() {
    return ReadWriteIOUtils.getByteBuffer(firstValue);
  }

  @Override
  public ByteBuffer getLastValueBuffer() {
    return ReadWriteIOUtils.getByteBuffer(lastValue);
  }

  @Override
  public ByteBuffer getSumValueBuffer() {
    throw new StatisticsClassException("Boolean statistics do not support: sum");
  }

  @Override
  protected void mergeStatisticsValue(Statistics stats) {
    BooleanStatistics boolStats = (BooleanStatistics) stats;
    if (isEmpty) {
      initializeStats(boolStats.getFirstValue(), boolStats.getLastValue());
      isEmpty = false;
    } else {
      updateStats(boolStats.getFirstValue(), boolStats.getLastValue());
    }
  }

  @Override
  public byte[] getMinValueBytes() {
    throw new StatisticsClassException("Boolean statistics does not support: min");
  }

  @Override
  public byte[] getMaxValueBytes() {
    throw new StatisticsClassException("Boolean statistics does not support: max");
  }

  @Override
  public byte[] getFirstValueBytes() {
    return BytesUtils.boolToBytes(firstValue);
  }

  @Override
  public byte[] getLastValueBytes() {
    return BytesUtils.boolToBytes(lastValue);
  }

  @Override
  public byte[] getSumValueBytes() {
    throw new StatisticsClassException("Boolean statistics does not support: sum");
  }

  @Override
  public int serializeStats(OutputStream outputStream) throws IOException {
    int byteLen = 0;
    byteLen += ReadWriteIOUtils.write(firstValue, outputStream);
    byteLen += ReadWriteIOUtils.write(lastValue, outputStream);
    return byteLen;
  }

  @Override
  void deserialize(InputStream inputStream) throws IOException {
    this.firstValue = ReadWriteIOUtils.readBool(inputStream);
    this.lastValue = ReadWriteIOUtils.readBool(inputStream);
  }

  @Override
  void deserialize(ByteBuffer byteBuffer) {
    this.firstValue = ReadWriteIOUtils.readBool(byteBuffer);
    this.lastValue = ReadWriteIOUtils.readBool(byteBuffer);
  }

  @Override
  public String toString() {
    return "[firstValue:" + firstValue + ",lastValue:" + lastValue + "]";
  }
}
