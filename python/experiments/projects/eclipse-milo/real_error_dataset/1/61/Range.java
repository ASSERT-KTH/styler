/*
 * Copyright (c) 2019 the Eclipse Milo Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.milo.opcua.stack.core.types.structured;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.eclipse.milo.opcua.stack.core.serialization.SerializationContext;
import org.eclipse.milo.opcua.stack.core.serialization.UaDecoder;
import org.eclipse.milo.opcua.stack.core.serialization.UaEncoder;
import org.eclipse.milo.opcua.stack.core.serialization.UaStructure;
import org.eclipse.milo.opcua.stack.core.serialization.codecs.GenericDataTypeCodec;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;

@EqualsAndHashCode(
    callSuper = false
)
@SuperBuilder(
    toBuilder = true
)
@ToString
public class Range extends Structure implements UaStructure {
    public static final ExpandedNodeId TYPE_ID = ExpandedNodeId.parse("nsu=http://opcfoundation.org/UA/;i=884");

    public static final ExpandedNodeId XML_ENCODING_ID = ExpandedNodeId.parse("nsu=http://opcfoundation.org/UA/;i=885");

    public static final ExpandedNodeId BINARY_ENCODING_ID = ExpandedNodeId.parse("nsu=http://opcfoundation.org/UA/;i=886");

    private final Double low;

    private final Double high;

    public Range(Double low, Double high) {
        this.low = low;
        this.high = high;
    }

    @Override
    public ExpandedNodeId getTypeId() {
        return TYPE_ID;
    }

    @Override
    public ExpandedNodeId getXmlEncodingId() {
        return XML_ENCODING_ID;
    }

    @Override
    public ExpandedNodeId getBinaryEncodingId() {
        return BINARY_ENCODING_ID;
    }

    public Double getLow() {
        return low;
    }

    public Double getHigh() {
        return high;
    }

    public static final class Codec extends GenericDataTypeCodec<Range> {
        @Override
        public Class<Range> getType() {
            return Range.class;
        }

        @Override
        public Range decode(SerializationContext context, UaDecoder decoder) {
            Double low = decoder.readDouble("Low");
            Double high = decoder.readDouble("High");
            return new Range(low, high);
        }

        @Override
        public void encode(SerializationContext context, UaEncoder encoder, Range value) {
            encoder.writeDouble("Low", value.getLow());
            encoder.writeDouble("High", value.getHigh());
        }
    }
}
