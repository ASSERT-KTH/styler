/*
 * Copyright (c) 2019 the Eclipse Milo Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.milo.opcua.sdk.server.events.conversions;

import java.nio.ByteBuffer;
import java.util.UUID;

import io.netty.buffer.ByteBufUtil;
import org.eclipse.milo.opcua.stack.core.BuiltinDataType;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ByteStringConversions {

    private ByteStringConversions() {}

    @Nullable
    static UUID byteStringToGuid(@NotNull ByteString bs) {
        if (bs.length() != 16) {
            return null;
        } else {
            ByteBuffer byteBuffer = ByteBuffer.wrap(bs.bytesOrEmpty());
            long high = byteBuffer.getLong();
            long low = byteBuffer.getLong();
            return new UUID(high, low);
        }
    }

    @NotNull
    static String byteStringToString(@NotNull ByteString bs) {
        return ByteBufUtil.hexDump(bs.bytesOrEmpty());
    }

    @Nullable
    static Object convert(@NotNull Object o, BuiltinDataType targetType, boolean implicit) {
        if (o instanceof ByteString) {
            ByteString bs = (ByteString) o;

            return implicit ?
                implicitConversion(bs, targetType) :
                explicitConversion(bs, targetType);
        } else {
            return null;
        }
    }

    @Nullable
    static Object explicitConversion(@NotNull ByteString bs, BuiltinDataType targetType) {
        //@formatter:off
        switch (targetType) {
            case Guid:      return byteStringToGuid(bs);
            case String:    return byteStringToString(bs);
            default:        return implicitConversion(bs, targetType);
        }
        //@formatter:on
    }

    @Nullable
    static Object implicitConversion(@NotNull ByteString bs, BuiltinDataType targetType) {
        // no implicit conversions exist
        return null;
    }

}
