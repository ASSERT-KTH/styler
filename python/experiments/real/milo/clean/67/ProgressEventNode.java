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
import org.eclipse.milo.opcua.sdk.client.api.model.types.objects.ProgressEventType;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;


public class ProgressEventNode extends BaseEventNode implements ProgressEventType {

    public ProgressEventNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }

    @Override
    public CompletableFuture<PropertyNode> context() {
        return getPropertyNode(ProgressEventType.CONTEXT.getBrowseName());
    }

    @Override
    public CompletableFuture<? extends Object> getContext() {
        return getProperty(ProgressEventType.CONTEXT);
    }

    @Override
    public CompletableFuture<StatusCode> setContext(Object value) {
        return setProperty(ProgressEventType.CONTEXT, value);
    }

    @Override
    public CompletableFuture<PropertyNode> progress() {
        return getPropertyNode(ProgressEventType.PROGRESS.getBrowseName());
    }

    @Override
    public CompletableFuture<UShort> getProgress() {
        return getProperty(ProgressEventType.PROGRESS);
    }

    @Override
    public CompletableFuture<StatusCode> setProgress(UShort value) {
        return setProperty(ProgressEventType.PROGRESS, value);
    }


}