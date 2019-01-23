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
import org.eclipse.milo.opcua.sdk.client.api.model.nodes.variables.PropertyNode;
import org.eclipse.milo.opcua.sdk.client.api.model.types.objects.AuditUpdateMethodEventType;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;


public class AuditUpdateMethodEventNode extends AuditEventNode implements AuditUpdateMethodEventType {

    public AuditUpdateMethodEventNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }

    @Override
    public CompletableFuture<PropertyNode> methodId() {
        return getPropertyNode(AuditUpdateMethodEventType.METHOD_ID.getBrowseName());
    }

    @Override
    public CompletableFuture<NodeId> getMethodId() {
        return getProperty(AuditUpdateMethodEventType.METHOD_ID);
    }

    @Override
    public CompletableFuture<StatusCode> setMethodId(NodeId value) {
        return setProperty(AuditUpdateMethodEventType.METHOD_ID, value);
    }

    @Override
    public CompletableFuture<PropertyNode> inputArguments() {
        return getPropertyNode(AuditUpdateMethodEventType.INPUT_ARGUMENTS.getBrowseName());
    }

    @Override
    public CompletableFuture<Object[]> getInputArguments() {
        return getProperty(AuditUpdateMethodEventType.INPUT_ARGUMENTS);
    }

    @Override
    public CompletableFuture<StatusCode> setInputArguments(Object[] value) {
        return setProperty(AuditUpdateMethodEventType.INPUT_ARGUMENTS, value);
    }


}