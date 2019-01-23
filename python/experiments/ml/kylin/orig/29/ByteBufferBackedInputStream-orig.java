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

package org.apache.kylin.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Utility to view content available in a {@link ByteBuffer} as a {@link InputStream}.
 *
 * <b>Not thread-safe</b>
 */
public class ByteBufferBackedInputStream extends InputStream {
    private final ByteBuffer buffer;

    public ByteBufferBackedInputStream(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int available() throws IOException {
        return buffer.remaining();
    }

    @Override
    public int read() throws IOException {
        return buffer.hasRemaining() ? (buffer.get() & 0xFF) : -1;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }

        if (!buffer.hasRemaining()) {
            return -1;
        }

        len = Math.min(buffer.remaining(), len);
        buffer.get(b, off, len);
        return len;
    }
}
