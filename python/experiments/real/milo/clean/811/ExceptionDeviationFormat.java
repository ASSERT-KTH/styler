/*
 * Copyright (c) 2016 Kevin Herron
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.html.
 */

package org.eclipse.milo.opcua.stack.core.types.enumerated;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import org.eclipse.milo.opcua.stack.core.serialization.DelegateRegistry;
import org.eclipse.milo.opcua.stack.core.serialization.UaDecoder;
import org.eclipse.milo.opcua.stack.core.serialization.UaEncoder;
import org.eclipse.milo.opcua.stack.core.serialization.UaEnumeration;

public enum ExceptionDeviationFormat implements UaEnumeration {

    AbsoluteValue(0),
    PercentOfValue(1),
    PercentOfRange(2),
    PercentOfEURange(3),
    Unknown(4);

    private final int value;

    ExceptionDeviationFormat(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    private static final ImmutableMap<Integer, ExceptionDeviationFormat> VALUES;

    static {
        Builder<Integer, ExceptionDeviationFormat> builder = ImmutableMap.builder();
        for (ExceptionDeviationFormat e : values()) {
            builder.put(e.getValue(), e);
        }
        VALUES = builder.build();
    }

    public static ExceptionDeviationFormat from(Integer value) {
        if (value == null) return null;
        return VALUES.getOrDefault(value, null);
    }

    public static void encode(ExceptionDeviationFormat exceptionDeviationFormat, UaEncoder encoder) {
        encoder.encodeInt32(null, exceptionDeviationFormat.getValue());
    }

    public static ExceptionDeviationFormat decode(UaDecoder decoder) {
        int value = decoder.decodeInt32(null);

        return VALUES.getOrDefault(value, null);
    }

    static {
        DelegateRegistry.registerEncoder(ExceptionDeviationFormat::encode, ExceptionDeviationFormat.class);
        DelegateRegistry.registerDecoder(ExceptionDeviationFormat::decode, ExceptionDeviationFormat.class);
    }

}
