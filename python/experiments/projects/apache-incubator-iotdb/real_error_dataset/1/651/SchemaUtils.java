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
package org.apache.iotdb.db.utils;

import static org.apache.iotdb.db.conf.IoTDBConstant.COLUMN_PRIVILEGE;
import static org.apache.iotdb.db.conf.IoTDBConstant.COLUMN_ROLE;
import static org.apache.iotdb.db.conf.IoTDBConstant.COLUMN_STORAGE_GROUP;
import static org.apache.iotdb.db.conf.IoTDBConstant.COLUMN_TTL;
import static org.apache.iotdb.db.conf.IoTDBConstant.COLUMN_USER;

import java.util.Collections;
import java.util.List;
import org.apache.iotdb.db.engine.StorageEngine;
import org.apache.iotdb.db.exception.StorageEngineException;
import org.apache.iotdb.db.exception.metadata.MetadataException;
import org.apache.iotdb.db.exception.metadata.PathAlreadyExistException;
import org.apache.iotdb.db.metadata.MManager;
import org.apache.iotdb.db.qp.constant.SQLConstant;
import org.apache.iotdb.tsfile.file.metadata.enums.CompressionType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding;
import org.apache.iotdb.tsfile.read.common.Path;
import org.apache.iotdb.tsfile.write.schema.MeasurementSchema;
import org.apache.iotdb.tsfile.write.schema.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SchemaUtils {

  private static final Logger logger = LoggerFactory.getLogger(SchemaUtils.class);

  private SchemaUtils() {
  }

  /**
   * Construct the Schema of the FileNode named processorName.
   *
   * @param processorName the name of a FileNode.
   * @return the schema of the FileNode named processorName.
   */
  public static Schema constructSchema(String processorName) throws MetadataException {
    List<MeasurementSchema> columnSchemaList;
    columnSchemaList = MManager.getInstance().getStorageGroupSchema(processorName);
    return getSchemaFromColumnSchema(columnSchemaList);
  }

  /**
   * getSchemaFromColumnSchema construct a Schema using the schema of the columns and device type.
   *
   * @param schemaList the schema of the columns in this file.
   * @return a Schema contains the provided schemas.
   */
  private static Schema getSchemaFromColumnSchema(List<MeasurementSchema> schemaList) {
    Schema schema = new Schema();
    for (MeasurementSchema measurementSchema : schemaList) {
      schema.registerMeasurement(measurementSchema);
    }
    return schema;
  }

  public static void registerTimeseries(MeasurementSchema schema) {
    try {
      logger.debug("Registering timeseries {}", schema);
      String path = schema.getMeasurementId();
      TSDataType dataType = schema.getType();
      TSEncoding encoding = schema.getEncodingType();
      CompressionType compressionType = schema.getCompressor();
      boolean result = MManager.getInstance().createTimeseries(path, dataType, encoding,
          compressionType, Collections.emptyMap());
      if (result) {
        StorageEngine.getInstance().addTimeSeries(new Path(path), dataType, encoding,
            compressionType, Collections.emptyMap());
      }
    } catch (PathAlreadyExistException ignored) {
      // ignore added timeseries
    } catch (MetadataException | StorageEngineException e) {
      logger.error("Cannot create timeseries {} in snapshot, ignored", schema.getMeasurementId(),
          e);
    }

  }

  public static TSDataType getSeriesType(String path)
      throws MetadataException {
    switch (path.toLowerCase()) {
      // authorization queries
      case COLUMN_ROLE:
      case COLUMN_USER:
      case COLUMN_PRIVILEGE:
      case COLUMN_STORAGE_GROUP:
        return TSDataType.TEXT;
      case SQLConstant.RESERVED_TIME:
      case COLUMN_TTL:
        return TSDataType.INT64;
      default:
        // do nothing
    }

    if (path.contains("(") && !path.startsWith("(") && path.endsWith(")")) {
      // aggregation
      int leftBracketIndex = path.indexOf('(');
      String aggrType = path.substring(0, leftBracketIndex);
      String innerPath = path.substring(leftBracketIndex + 1, path.length() - 1);
      switch (aggrType.toLowerCase()) {
        case SQLConstant.MIN_TIME:
        case SQLConstant.MAX_TIME:
        case SQLConstant.COUNT:
          return TSDataType.INT64;
        case SQLConstant.LAST_VALUE:
        case SQLConstant.FIRST_VALUE:
        case SQLConstant.MIN_VALUE:
        case SQLConstant.MAX_VALUE:
          return getSeriesType(innerPath);
        case SQLConstant.AVG:
        case SQLConstant.SUM:
          return TSDataType.DOUBLE;
        default:
          throw new MetadataException(
              "aggregate does not support " + aggrType + " function.");
      }
    }
    return MManager.getInstance().getSeriesType(path);
  }
}
