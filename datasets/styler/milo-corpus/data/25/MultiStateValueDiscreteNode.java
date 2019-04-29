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

package org.eclipse.milo.opcua.sdk.client.api.model.nodes.variables;

import java.util.concurrent.CompletableFuture;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.model.types.variables.MultiStateValueDiscreteType;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.structured.EnumValueType;


public class MultiStateValueDiscreteNode extends DiscreteItemNode implements MultiStateValueDiscreteType {

    public MultiStateValueDiscreteNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }

    @Override
    public CompletableFuture<PropertyNode> enumValues() {
        return getPropertyNode(MultiStateValueDiscreteType.ENUM_VALUES.getBrowseName());
    }

    @Override
    public CompletableFuture<EnumValueType[]> getEnumValues() {
        return getProperty(MultiStateValueDiscreteType.ENUM_VALUES);
    }

    @Override
    public CompletableFuture<StatusCode> setEnumValues(EnumValueType[] value) {
        return setProperty(MultiStateValueDiscreteType.ENUM_VALUES, value);
    }

    @Override
    public CompletableFuture<PropertyNode> valueAsText() {
        return getPropertyNode(MultiStateValueDiscreteType.VALUE_AS_TEXT.getBrowseName());
    }

    @Override
    public CompletableFuture<LocalizedText> getValueAsText() {
        return getProperty(MultiStateValueDiscreteType.VALUE_AS_TEXT);
    }

    @Override
    public CompletableFuture<StatusCode> setValueAsText(LocalizedText value) {
        return setProperty(MultiStateValueDiscreteType.VALUE_AS_TEXT, value);
    }


}