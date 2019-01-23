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
import org.eclipse.milo.opcua.sdk.client.api.model.types.objects.AuditUpdateStateEventType;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;


public class AuditUpdateStateEventNode extends AuditUpdateMethodEventNode implements AuditUpdateStateEventType {

    public AuditUpdateStateEventNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }

    @Override
    public CompletableFuture<PropertyNode> oldStateId() {
        return getPropertyNode(AuditUpdateStateEventType.OLD_STATE_ID.getBrowseName());
    }

    @Override
    public CompletableFuture<? extends Object> getOldStateId() {
        return getProperty(AuditUpdateStateEventType.OLD_STATE_ID);
    }

    @Override
    public CompletableFuture<StatusCode> setOldStateId(Object value) {
        return setProperty(AuditUpdateStateEventType.OLD_STATE_ID, value);
    }

    @Override
    public CompletableFuture<PropertyNode> newStateId() {
        return getPropertyNode(AuditUpdateStateEventType.NEW_STATE_ID.getBrowseName());
    }

    @Override
    public CompletableFuture<? extends Object> getNewStateId() {
        return getProperty(AuditUpdateStateEventType.NEW_STATE_ID);
    }

    @Override
    public CompletableFuture<StatusCode> setNewStateId(Object value) {
        return setProperty(AuditUpdateStateEventType.NEW_STATE_ID, value);
    }


}