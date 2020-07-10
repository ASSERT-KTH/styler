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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.milo.opcua.stack.core.BuiltinDataType;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.ULong;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ubyte;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ulong;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ushort;

final class Int64Conversions {

    private Int64Conversions() {}

    @Nonnull
    static Boolean int64ToBoolean(@Nonnull Long l) {
        return l != 0;
    }

    @Nullable
    static UByte int64ToByte(@Nonnull Long l) {
        if (l >= 0 && l <= UByte.MAX_VALUE) {
            return ubyte(l);
        } else {
            return null;
        }
    }

    @Nonnull
    static Double int64ToDouble(@Nonnull Long l) {
        return l.doubleValue();
    }

    @Nonnull
    static Float int64ToFloat(@Nonnull Long l) {
        return l.floatValue();
    }

    @Nullable
    static Short int64ToInt16(@Nonnull Long l) {
        if (l >= Short.MIN_VALUE && l <= Short.MAX_VALUE) {
            return l.shortValue();
        } else {
            return null;
        }
    }

    @Nullable
    static Integer int64ToInt32(@Nonnull Long l) {
        if (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) {
            return l.intValue();
        } else {
            return null;
        }
    }

    @Nullable
    static Byte int64ToSByte(@Nonnull Long l) {
        if (l >= Byte.MIN_VALUE && l <= Byte.MAX_VALUE) {
            return l.byteValue();
        } else {
            return null;
        }
    }

    @Nonnull
    static StatusCode int64ToStatusCode(@Nonnull Long l) {
        return new StatusCode(l);
    }

    @Nonnull
    static String int64ToString(@Nonnull Long l) {
        return l.toString();
    }

    @Nullable
    static UShort int64ToUInt16(@Nonnull Long l) {
        if (l >= 0 && l <= UShort.MAX_VALUE) {
            return ushort(l.intValue());
        } else {
            return null;
        }
    }

    @Nullable
    static UInteger int64ToUInt32(@Nonnull Long l) {
        if (l >= 0 && l <= UInteger.MAX_VALUE) {
            return uint(l);
        } else {
            return null;
        }
    }

    @Nullable
    static ULong int64ToUInt64(@Nonnull Long l) {
        if (l >= 0) {
            return ulong(l);
        } else {
            return null;
        }
    }

    @Nullable
    static Object convert(@Nullable Object o, BuiltinDataType targetType, boolean implicit) {
        if (o instanceof Long) {
            Long l = (Long) o;

            return implicit ?
                implicitConversion(l, targetType) :
                explicitConversion(l, targetType);
        } else {
            return null;
        }
    }

    @Nullable
    static Object explicitConversion(@Nonnull Long l, BuiltinDataType targetType) {
        //@formatter:off
        switch (targetType) {
            case Boolean:       return int64ToBoolean(l);
            case Byte:          return int64ToByte(l);
            case Int16:         return int64ToInt16(l);
            case Int32:         return int64ToInt32(l);
            case SByte:         return int64ToSByte(l);
            case StatusCode:    return int64ToStatusCode(l);
            case String:        return int64ToString(l);
            case UInt16:        return int64ToUInt16(l);
            case UInt32:        return int64ToUInt32(l);
            case UInt64:        return int64ToUInt64(l);
            default:            return implicitConversion(l, targetType);
        }
        //@formatter:on
    }

    @Nullable
    static Object implicitConversion(@Nonnull Long l, BuiltinDataType targetType) {
        //@formatter:off
        switch (targetType) {
            case Double:    return int64ToDouble(l);
            case Float:     return int64ToFloat(l);
            default:        return null;
        }
        //@formatter:on
    }

}
