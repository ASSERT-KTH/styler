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
import org.eclipse.milo.opcua.sdk.client.api.model.types.objects.AuditCancelEventType;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;


public class AuditCancelEventNode extends AuditSessionEventNode implements AuditCancelEventType {

    public AuditCancelEventNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }

    @Override
    public CompletableFuture<PropertyNode> requestHandle() {
        return getPropertyNode(AuditCancelEventType.REQUEST_HANDLE.getBrowseName());
    }

    @Override
    public CompletableFuture<UInteger> getRequestHandle() {
        return getProperty(AuditCancelEventType.REQUEST_HANDLE);
    }

    @Override
    public CompletableFuture<StatusCode> setRequestHandle(UInteger value) {
        return setProperty(AuditCancelEventType.REQUEST_HANDLE, value);
    }


}