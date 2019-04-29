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
import org.eclipse.milo.opcua.sdk.client.api.model.types.variables.MultiStateDiscreteType;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;


public class MultiStateDiscreteNode extends DiscreteItemNode implements MultiStateDiscreteType {

    public MultiStateDiscreteNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }

    @Override
    public CompletableFuture<PropertyNode> enumStrings() {
        return getPropertyNode(MultiStateDiscreteType.ENUM_STRINGS.getBrowseName());
    }

    @Override
    public CompletableFuture<LocalizedText[]> getEnumStrings() {
        return getProperty(MultiStateDiscreteType.ENUM_STRINGS);
    }

    @Override
    public CompletableFuture<StatusCode> setEnumStrings(LocalizedText[] value) {
        return setProperty(MultiStateDiscreteType.ENUM_STRINGS, value);
    }


}