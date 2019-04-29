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

package org.eclipse.milo.opcua.stack.core.serialization.binary;

import java.nio.ByteOrder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.testng.annotations.BeforeMethod;

public abstract class BinarySerializationFixture {

    ByteBuf buffer;
    BinaryEncoder encoder;
    BinaryDecoder decoder;

    @BeforeMethod
    public void setUp() {
        buffer = Unpooled.buffer().order(ByteOrder.LITTLE_ENDIAN);

        encoder = new BinaryEncoder().setBuffer(buffer);
        decoder = new BinaryDecoder().setBuffer(buffer);
    }

}
