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
import org.eclipse.milo.opcua.sdk.client.api.model.types.objects.AuditConditionCommentEventType;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;


public class AuditConditionCommentEventNode extends AuditConditionEventNode implements AuditConditionCommentEventType {

    public AuditConditionCommentEventNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }

    @Override
    public CompletableFuture<PropertyNode> eventId() {
        return getPropertyNode(AuditConditionCommentEventType.EVENT_ID.getBrowseName());
    }

    @Override
    public CompletableFuture<ByteString> getEventId() {
        return getProperty(AuditConditionCommentEventType.EVENT_ID);
    }

    @Override
    public CompletableFuture<StatusCode> setEventId(ByteString value) {
        return setProperty(AuditConditionCommentEventType.EVENT_ID, value);
    }

    @Override
    public CompletableFuture<PropertyNode> comment() {
        return getPropertyNode(AuditConditionCommentEventType.COMMENT.getBrowseName());
    }

    @Override
    public CompletableFuture<LocalizedText> getComment() {
        return getProperty(AuditConditionCommentEventType.COMMENT);
    }

    @Override
    public CompletableFuture<StatusCode> setComment(LocalizedText value) {
        return setProperty(AuditConditionCommentEventType.COMMENT, value);
    }


}