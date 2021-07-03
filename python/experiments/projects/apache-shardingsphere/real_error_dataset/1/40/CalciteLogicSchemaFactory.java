/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.infra.optimize.schema;

import org.apache.calcite.schema.Schema;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.shardingsphere.infra.exception.ShardingSphereException;
import org.apache.shardingsphere.infra.metadata.ShardingSphereMetaData;

import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Calcite schema factory.
 *
 */
public final class CalciteLogicSchemaFactory {
    
    private final Map<String, Schema> schemas = new LinkedMap<>();
    
    public CalciteLogicSchemaFactory(final Map<String, ShardingSphereMetaData> metaDataMap) {
        for (Entry<String, ShardingSphereMetaData> each : metaDataMap.entrySet()) {
            try {
                schemas.put(each.getKey(), new CalciteLogicSchema(each.getValue()));
            } catch (final SQLException ex) {
                throw new ShardingSphereException(ex);
            }
        }
    }
    
    /**
     * Create schema.
     *
     * @param name name
     * @return schema
     */
    public Schema create(final String name) {
        if (!schemas.containsKey(name)) {
            throw new ShardingSphereException("No `%s` schema.", name);
        }
        return schemas.get(name);
    }
}
