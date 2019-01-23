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

package org.apache.kylin.cube.gridtable;

import org.apache.kylin.common.util.ByteArray;
import org.apache.kylin.gridtable.GTRecord;

import java.util.Collection;

/**
 * asymmetric means compare(a,b) > 0 does not cause compare(b,a) < 0 
 * so min max functions will not be supported
 */
public class AsymmetricRecordComparator extends RecordComparator {

    AsymmetricRecordComparator(ComparatorEx<ByteArray> byteComparator) {
        super(byteComparator);
    }

    public GTRecord min(Collection<GTRecord> v) {
        throw new UnsupportedOperationException();
    }

    public GTRecord max(Collection<GTRecord> v) {
        throw new UnsupportedOperationException();
    }

    public GTRecord min(GTRecord a, GTRecord b) {
        throw new UnsupportedOperationException();
    }

    public GTRecord max(GTRecord a, GTRecord b) {
        throw new UnsupportedOperationException();
    }

    public boolean between(GTRecord v, GTRecord start, GTRecord end) {
        throw new UnsupportedOperationException();
    }
}
