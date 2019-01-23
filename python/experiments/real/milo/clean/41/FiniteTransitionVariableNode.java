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
import org.eclipse.milo.opcua.sdk.client.api.model.types.variables.FiniteTransitionVariableType;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;


public class FiniteTransitionVariableNode extends TransitionVariableNode implements FiniteTransitionVariableType {

    public FiniteTransitionVariableNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }

    @Override
    public CompletableFuture<PropertyNode> id() {
        return getPropertyNode(FiniteTransitionVariableType.ID.getBrowseName());
    }

    @Override
    public CompletableFuture<NodeId> getId() {
        return getProperty(FiniteTransitionVariableType.ID);
    }

    @Override
    public CompletableFuture<StatusCode> setId(NodeId value) {
        return setProperty(FiniteTransitionVariableType.ID, value);
    }


}