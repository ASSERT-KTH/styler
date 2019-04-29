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

package org.eclipse.milo.opcua.sdk.client.api.model.nodes.objects;

import java.util.concurrent.CompletableFuture;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.model.nodes.variables.FiniteStateVariableNode;
import org.eclipse.milo.opcua.sdk.client.api.model.nodes.variables.FiniteTransitionVariableNode;
import org.eclipse.milo.opcua.sdk.client.api.model.types.objects.FiniteStateMachineType;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;


public class FiniteStateMachineNode extends StateMachineNode implements FiniteStateMachineType {

    public FiniteStateMachineNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }


    @Override
    public CompletableFuture<FiniteStateVariableNode> currentState() {
        return getVariableComponent(QualifiedName.parse("0:CurrentState"))
            .thenApply(FiniteStateVariableNode.class::cast);
    }

    public CompletableFuture<LocalizedText> getCurrentState() {
        return currentState()
            .thenCompose(UaVariableNode::getValue)
            .thenApply(o -> cast(o, LocalizedText.class));
    }

    @Override
    public CompletableFuture<StatusCode> setCurrentState(LocalizedText value) {
        return currentState()
            .thenCompose(node -> node.setValue(value));
    }

    @Override
    public CompletableFuture<FiniteTransitionVariableNode> lastTransition() {
        return getVariableComponent(QualifiedName.parse("0:LastTransition"))
            .thenApply(FiniteTransitionVariableNode.class::cast);
    }

    public CompletableFuture<LocalizedText> getLastTransition() {
        return lastTransition()
            .thenCompose(UaVariableNode::getValue)
            .thenApply(o -> cast(o, LocalizedText.class));
    }

    @Override
    public CompletableFuture<StatusCode> setLastTransition(LocalizedText value) {
        return lastTransition()
            .thenCompose(node -> node.setValue(value));
    }


}