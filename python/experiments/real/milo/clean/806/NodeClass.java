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

public enum NodeClass implements UaEnumeration {

    Unspecified(0),
    Object(1),
    Variable(2),
    Method(4),
    ObjectType(8),
    VariableType(16),
    ReferenceType(32),
    DataType(64),
    View(128);

    private final int value;

    NodeClass(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    private static final ImmutableMap<Integer, NodeClass> VALUES;

    static {
        Builder<Integer, NodeClass> builder = ImmutableMap.builder();
        for (NodeClass e : values()) {
            builder.put(e.getValue(), e);
        }
        VALUES = builder.build();
    }

    public static NodeClass from(Integer value) {
        if (value == null) return null;
        return VALUES.getOrDefault(value, null);
    }

    public static void encode(NodeClass nodeClass, UaEncoder encoder) {
        encoder.encodeInt32(null, nodeClass.getValue());
    }

    public static NodeClass decode(UaDecoder decoder) {
        int value = decoder.decodeInt32(null);

        return VALUES.getOrDefault(value, null);
    }

    static {
        DelegateRegistry.registerEncoder(NodeClass::encode, NodeClass.class);
        DelegateRegistry.registerDecoder(NodeClass::decode, NodeClass.class);
    }

}
