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

package org.apache.kylin.dict.lookup;

import org.apache.kylin.metadata.model.TableDesc;

public interface IExtLookupProvider {
    ILookupTable getLookupTable(TableDesc tableDesc, ExtTableSnapshotInfo extTableSnapshot);

    /**
     * @return the local cache if the provider has, return null if no local cache exist
     */
    IExtLookupTableCache getLocalCache();

    /**
     * Return an adaptor that implements specified interface as requested by the build engine.
     * The ILookupMaterializer in particular, is required by the MR build engine.
     */
    <I> I adaptToBuildEngine(Class<I> engineInterface);
}
