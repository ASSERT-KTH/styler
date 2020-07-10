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
package org.apache.iotdb.db.query.externalsort.adapter;

import java.io.IOException;
import org.apache.iotdb.db.query.reader.IPointReader;
import org.apache.iotdb.db.query.reader.IReaderByTimestamp;
import org.apache.iotdb.db.utils.TimeValuePair;

/**
 * This class is an adapter which makes IPointReader implement IReaderByTimestamp interface.
 */
public class ByTimestampReaderAdapter implements IReaderByTimestamp {

  private IPointReader pointReader;
  private boolean hasCached;
  private TimeValuePair pair;

  public ByTimestampReaderAdapter(IPointReader pointReader) {
    this.pointReader = pointReader;
  }

  @Override
  public Object getValueInTimestamp(long timestamp) throws IOException {
    if (hasCached) {
      if (pair.getTimestamp() < timestamp) {
        hasCached = false;
      } else if (pair.getTimestamp() == timestamp) {
        hasCached = false;
        return pair.getValue().getValue();
      } else {
        return null;
      }
    }

    while (pointReader.hasNext()) {
      pair = pointReader.next();
      if (pair.getTimestamp() >= timestamp) {
        hasCached = true;
        break;
      }
    }

    if (!hasCached) {
      return null;
    }

    if (pair.getTimestamp() == timestamp) {
      hasCached = false;
      return pair.getValue().getValue();
    } else {
      return null;
    }

  }

  @Override
  public boolean hasNext() throws IOException {
    return pointReader.hasNext();
  }
}
