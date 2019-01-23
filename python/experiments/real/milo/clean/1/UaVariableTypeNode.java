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

package org.eclipse.milo.opcua.sdk.client.nodes;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.nodes.VariableTypeNode;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;

import static org.eclipse.milo.opcua.stack.core.types.builtin.DataValue.valueOnly;

public class UaVariableTypeNode extends UaNode implements VariableTypeNode {

    public UaVariableTypeNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }

    @Override
    public CompletableFuture<Object> getValue() {
        return readValue().thenApply(v -> v.getValue().getValue());
    }

    @Override
    public CompletableFuture<NodeId> getDataType() {
        return readDataType().thenApply(v -> (NodeId) v.getValue().getValue());
    }

    @Override
    public CompletableFuture<Integer> getValueRank() {
        return readValueRank().thenApply(v -> (Integer) v.getValue().getValue());
    }

    @Override
    public CompletableFuture<Optional<UInteger[]>> getArrayDimensions() {
        return readArrayDimensions().thenApply(v -> {
            StatusCode statusCode = v.getStatusCode();

            if (statusCode.getValue() == StatusCodes.Bad_AttributeIdInvalid) {
                return Optional.empty();
            } else {
                return Optional.ofNullable((UInteger[]) v.getValue().getValue());
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> getIsAbstract() {
        return readIsAbstract().thenApply(v -> (Boolean) v.getValue().getValue());
    }

    @Override
    public CompletableFuture<StatusCode> setValue(Object value) {
        return writeValue(valueOnly(new Variant(value)));
    }

    @Override
    public CompletableFuture<StatusCode> setDataType(NodeId dataType) {
        return writeDataType(valueOnly(new Variant(dataType)));
    }

    @Override
    public CompletableFuture<StatusCode> setValueRank(int valueRank) {
        return writeValueRank(valueOnly(new Variant(valueRank)));
    }

    @Override
    public CompletableFuture<StatusCode> setArrayDimensions(UInteger[] arrayDimensions) {
        return writeArrayDimensions(valueOnly(new Variant(arrayDimensions)));
    }

    @Override
    public CompletableFuture<StatusCode> setIsAbstract(boolean isAbstract) {
        return writeIsAbstract(valueOnly(new Variant(isAbstract)));
    }

    @Override
    public CompletableFuture<DataValue> readValue() {
        return readAttribute(AttributeId.Value);
    }

    @Override
    public CompletableFuture<DataValue> readDataType() {
        return readAttribute(AttributeId.DataType);
    }

    @Override
    public CompletableFuture<DataValue> readValueRank() {
        return readAttribute(AttributeId.ValueRank);
    }

    @Override
    public CompletableFuture<DataValue> readArrayDimensions() {
        return readAttribute(AttributeId.ArrayDimensions);
    }

    @Override
    public CompletableFuture<DataValue> readIsAbstract() {
        return readAttribute(AttributeId.IsAbstract);
    }

    @Override
    public CompletableFuture<StatusCode> writeValue(DataValue value) {
        return writeAttribute(AttributeId.Value, value);
    }

    @Override
    public CompletableFuture<StatusCode> writeDataType(DataValue value) {
        return writeAttribute(AttributeId.DataType, value);
    }

    @Override
    public CompletableFuture<StatusCode> writeValueRank(DataValue value) {
        return writeAttribute(AttributeId.ValueRank, value);
    }

    @Override
    public CompletableFuture<StatusCode> writeArrayDimensions(DataValue value) {
        return writeAttribute(AttributeId.ArrayDimensions, value);
    }

    @Override
    public CompletableFuture<StatusCode> writeIsAbstract(DataValue value) {
        return writeAttribute(AttributeId.IsAbstract, value);
    }

}
