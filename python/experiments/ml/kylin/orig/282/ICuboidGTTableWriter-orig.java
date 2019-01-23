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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/


package org.apache.kylin.cube.inmemcubing;

import java.io.IOException;

import org.apache.kylin.gridtable.GTRecord;
import org.apache.kylin.gridtable.GTScanRequest;
import org.apache.kylin.gridtable.GTScanRequestBuilder;
import org.apache.kylin.gridtable.GridTable;
import org.apache.kylin.gridtable.IGTScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ICuboidGTTableWriter implements ICuboidWriter{
    
    private static Logger logger = LoggerFactory.getLogger(ICuboidGTTableWriter.class);
    
    @Override
    public void write(long cuboidId, GridTable gridTable) throws IOException {
        long startTime = System.currentTimeMillis();
        GTScanRequest req = new GTScanRequestBuilder().setInfo(gridTable.getInfo()).setRanges(null).setDimensions(null).setFilterPushDown(null).createGTScanRequest();
        IGTScanner scanner = gridTable.scan(req);
        for (GTRecord record : scanner) {
            write(cuboidId, record);
        }
        scanner.close();
        logger.info("Cuboid " + cuboidId + " output takes " + (System.currentTimeMillis() - startTime) + "ms");
    }
}
