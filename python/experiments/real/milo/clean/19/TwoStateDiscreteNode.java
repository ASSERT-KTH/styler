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
import org.eclipse.milo.opcua.sdk.client.api.model.types.variables.TwoStateDiscreteType;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;


public class TwoStateDiscreteNode extends DiscreteItemNode implements TwoStateDiscreteType {

    public TwoStateDiscreteNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }

    @Override
    public CompletableFuture<PropertyNode> falseState() {
        return getPropertyNode(TwoStateDiscreteType.FALSE_STATE.getBrowseName());
    }

    @Override
    public CompletableFuture<LocalizedText> getFalseState() {
        return getProperty(TwoStateDiscreteType.FALSE_STATE);
    }

    @Override
    public CompletableFuture<StatusCode> setFalseState(LocalizedText value) {
        return setProperty(TwoStateDiscreteType.FALSE_STATE, value);
    }

    @Override
    public CompletableFuture<PropertyNode> trueState() {
        return getPropertyNode(TwoStateDiscreteType.TRUE_STATE.getBrowseName());
    }

    @Override
    public CompletableFuture<LocalizedText> getTrueState() {
        return getProperty(TwoStateDiscreteType.TRUE_STATE);
    }

    @Override
    public CompletableFuture<StatusCode> setTrueState(LocalizedText value) {
        return setProperty(TwoStateDiscreteType.TRUE_STATE, value);
    }


}