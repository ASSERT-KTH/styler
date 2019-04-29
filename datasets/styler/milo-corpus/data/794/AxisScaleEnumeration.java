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

public enum AxisScaleEnumeration implements UaEnumeration {

    Linear(0),
    Log(1),
    Ln(2);

    private final int value;

    AxisScaleEnumeration(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    private static final ImmutableMap<Integer, AxisScaleEnumeration> VALUES;

    static {
        Builder<Integer, AxisScaleEnumeration> builder = ImmutableMap.builder();
        for (AxisScaleEnumeration e : values()) {
            builder.put(e.getValue(), e);
        }
        VALUES = builder.build();
    }

    public static AxisScaleEnumeration from(Integer value) {
        if (value == null) return null;
        return VALUES.getOrDefault(value, null);
    }

    public static void encode(AxisScaleEnumeration axisScaleEnumeration, UaEncoder encoder) {
        encoder.encodeInt32(null, axisScaleEnumeration.getValue());
    }

    public static AxisScaleEnumeration decode(UaDecoder decoder) {
        int value = decoder.decodeInt32(null);

        return VALUES.getOrDefault(value, null);
    }

    static {
        DelegateRegistry.registerEncoder(AxisScaleEnumeration::encode, AxisScaleEnumeration.class);
        DelegateRegistry.registerDecoder(AxisScaleEnumeration::decode, AxisScaleEnumeration.class);
    }

}
