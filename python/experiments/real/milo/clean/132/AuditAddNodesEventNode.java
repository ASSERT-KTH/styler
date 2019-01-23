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
import org.eclipse.milo.opcua.sdk.client.api.model.types.objects.AuditAddNodesEventType;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.structured.AddNodesItem;


public class AuditAddNodesEventNode extends AuditNodeManagementEventNode implements AuditAddNodesEventType {

    public AuditAddNodesEventNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }

    @Override
    public CompletableFuture<PropertyNode> nodesToAdd() {
        return getPropertyNode(AuditAddNodesEventType.NODES_TO_ADD.getBrowseName());
    }

    @Override
    public CompletableFuture<AddNodesItem[]> getNodesToAdd() {
        return getProperty(AuditAddNodesEventType.NODES_TO_ADD);
    }

    @Override
    public CompletableFuture<StatusCode> setNodesToAdd(AddNodesItem[] value) {
        return setProperty(AuditAddNodesEventType.NODES_TO_ADD, value);
    }


}