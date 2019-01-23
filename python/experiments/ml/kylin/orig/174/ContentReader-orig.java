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

package org.apache.kylin.common.persistence;

import java.io.DataInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class ContentReader<T extends RootPersistentEntity> {

    final private Serializer<T> serializer;

    public ContentReader(Serializer<T> serializer) {
        this.serializer = serializer;
    }

    public T readContent(RawResource res) throws IOException {
        if (res == null)
            return null;

        DataInputStream din = new DataInputStream(res.content());
        try {
            T r = serializer.deserialize(din);
            if (r != null) {
                r.setLastModified(res.lastModified());
            }
            return r;
        } finally {
            IOUtils.closeQuietly(din);
            IOUtils.closeQuietly(res.content());
        }
    }
}
