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
import org.eclipse.milo.opcua.sdk.client.api.model.types.objects.TrustListType;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;


public class TrustListNode extends FileNode implements TrustListType {

    public TrustListNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }

    @Override
    public CompletableFuture<PropertyNode> lastUpdateTime() {
        return getPropertyNode(TrustListType.LAST_UPDATE_TIME.getBrowseName());
    }

    @Override
    public CompletableFuture<DateTime> getLastUpdateTime() {
        return getProperty(TrustListType.LAST_UPDATE_TIME);
    }

    @Override
    public CompletableFuture<StatusCode> setLastUpdateTime(DateTime value) {
        return setProperty(TrustListType.LAST_UPDATE_TIME, value);
    }


}