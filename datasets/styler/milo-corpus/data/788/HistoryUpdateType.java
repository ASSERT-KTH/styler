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

public enum HistoryUpdateType implements UaEnumeration {

    Insert(1),
    Replace(2),
    Update(3),
    Delete(4);

    private final int value;

    HistoryUpdateType(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    private static final ImmutableMap<Integer, HistoryUpdateType> VALUES;

    static {
        Builder<Integer, HistoryUpdateType> builder = ImmutableMap.builder();
        for (HistoryUpdateType e : values()) {
            builder.put(e.getValue(), e);
        }
        VALUES = builder.build();
    }

    public static HistoryUpdateType from(Integer value) {
        if (value == null) return null;
        return VALUES.getOrDefault(value, null);
    }

    public static void encode(HistoryUpdateType historyUpdateType, UaEncoder encoder) {
        encoder.encodeInt32(null, historyUpdateType.getValue());
    }

    public static HistoryUpdateType decode(UaDecoder decoder) {
        int value = decoder.decodeInt32(null);

        return VALUES.getOrDefault(value, null);
    }

    static {
        DelegateRegistry.registerEncoder(HistoryUpdateType::encode, HistoryUpdateType.class);
        DelegateRegistry.registerDecoder(HistoryUpdateType::decode, HistoryUpdateType.class);
    }

}
