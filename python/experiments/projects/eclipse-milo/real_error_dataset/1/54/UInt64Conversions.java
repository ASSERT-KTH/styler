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

import org.eclipse.milo.opcua.stack.core.BuiltinDataType;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.ULong;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ubyte;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ushort;

final class UInt64Conversions {

    private UInt64Conversions() {}

    @NotNull
    static Boolean uInt64ToBoolean(@NotNull ULong ul) {
        return ul.intValue() != 0;
    }

    @Nullable
    static UByte uInt64ToByte(@NotNull ULong ul) {
        long l = ul.longValue();

        if (Long.compareUnsigned(l, UByte.MAX_VALUE) <= 0) {
            return ubyte(l);
        } else {
            return null;
        }
    }

    @Nullable
    static Double uInt64ToDouble(@NotNull ULong ul) {
        long l = ul.longValue();

        if (Long.compareUnsigned(l, (long) Double.MAX_VALUE) <= 0) {
            return ul.doubleValue();
        } else {
            return null;
        }
    }

    @Nullable
    static Float uInt64ToFloat(@NotNull ULong ul) {
        long l = ul.longValue();

        if (Long.compareUnsigned(l, (long) Float.MAX_VALUE) <= 0) {
            return ul.floatValue();
        } else {
            return null;
        }
    }

    @Nullable
    static Short uInt64ToInt16(@NotNull ULong ul) {
        long l = ul.longValue();

        if (Long.compareUnsigned(l, Short.MAX_VALUE) <= 0) {
            return (short) l;
        } else {
            return null;
        }
    }

    @Nullable
    static Integer uInt64ToInt32(@NotNull ULong ul) {
        long l = ul.longValue();

        if (Long.compareUnsigned(l, Integer.MAX_VALUE) <= 0) {
            return (int) l;
        } else {
            return null;
        }
    }

    @Nullable
    static Long uInt64ToInt64(@NotNull ULong ul) {
        long l = ul.longValue();

        if (Long.compareUnsigned(l, Long.MAX_VALUE) <= 0) {
            return l;
        } else {
            return null;
        }
    }

    @Nullable
    static Byte uInt64ToSByte(@NotNull ULong ul) {
        long l = ul.longValue();

        if (Long.compareUnsigned(l, Byte.MAX_VALUE) <= 0) {
            return (byte) l;
        } else {
            return null;
        }
    }

    @Nullable
    static StatusCode uInt64ToStatusCode(@NotNull ULong ul) {
        UInteger ui = uInt64ToUInt32(ul);

        return ui != null ? UInt32Conversions.uInt32ToStatusCode(ui) : null;
    }

    @NotNull
    static String uInt64ToString(ULong ul) {
        return ul.toString();
    }

    @Nullable
    static UShort uInt64ToUInt16(ULong ul) {
        long l = ul.longValue();

        if (Long.compareUnsigned(l, UShort.MAX_VALUE) <= 0) {
            return ushort((int) l);
        } else {
            return null;
        }
    }

    @Nullable
    static UInteger uInt64ToUInt32(ULong ul) {
        long l = ul.longValue();

        if (Long.compareUnsigned(l, UInteger.MAX_VALUE) <= 0) {
            return uint(l);
        } else {
            return null;
        }
    }

    @Nullable
    static Object convert(@Nullable Object o, BuiltinDataType targetType, boolean implicit) {
        if (o instanceof ULong) {
            ULong ul = (ULong) o;

            return implicit ?
                implicitConversion(ul, targetType) :
                explicitConversion(ul, targetType);
        } else {
            return null;
        }
    }

    @Nullable
    static Object explicitConversion(@NotNull ULong ul, BuiltinDataType targetType) {
        //@formatter:off
        switch (targetType) {
            case Boolean:       return uInt64ToBoolean(ul);
            case Byte:          return uInt64ToByte(ul);
            case Int16:         return uInt64ToInt16(ul);
            case Int32:         return uInt64ToInt32(ul);
            case SByte:         return uInt64ToSByte(ul);
            case StatusCode:    return uInt64ToStatusCode(ul);
            case String:        return uInt64ToString(ul);
            case UInt16:        return uInt64ToUInt16(ul);
            case UInt32:        return uInt64ToUInt32(ul);
            default:            return implicitConversion(ul, targetType);
        }
        //@formatter:on
    }

    @Nullable
    static Object implicitConversion(@NotNull ULong ul, BuiltinDataType targetType) {
        //@formatter:off
        switch (targetType) {
            case Double:        return uInt64ToDouble(ul);
            case Float:         return uInt64ToFloat(ul);
            case Int64:         return uInt64ToInt64(ul);
            default:            return null;
        }
        //@formatter:on
    }

}
