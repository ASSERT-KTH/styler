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
import org.eclipse.milo.opcua.sdk.client.api.model.types.objects.AuditHistoryEventDeleteEventType;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.structured.HistoryEventFieldList;


public class AuditHistoryEventDeleteEventNode extends AuditHistoryDeleteEventNode implements AuditHistoryEventDeleteEventType {

    public AuditHistoryEventDeleteEventNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }

    @Override
    public CompletableFuture<PropertyNode> eventIds() {
        return getPropertyNode(AuditHistoryEventDeleteEventType.EVENT_IDS.getBrowseName());
    }

    @Override
    public CompletableFuture<ByteString[]> getEventIds() {
        return getProperty(AuditHistoryEventDeleteEventType.EVENT_IDS);
    }

    @Override
    public CompletableFuture<StatusCode> setEventIds(ByteString[] value) {
        return setProperty(AuditHistoryEventDeleteEventType.EVENT_IDS, value);
    }

    @Override
    public CompletableFuture<PropertyNode> oldValues() {
        return getPropertyNode(AuditHistoryEventDeleteEventType.OLD_VALUES.getBrowseName());
    }

    @Override
    public CompletableFuture<HistoryEventFieldList> getOldValues() {
        return getProperty(AuditHistoryEventDeleteEventType.OLD_VALUES);
    }

    @Override
    public CompletableFuture<StatusCode> setOldValues(HistoryEventFieldList value) {
        return setProperty(AuditHistoryEventDeleteEventType.OLD_VALUES, value);
    }


}