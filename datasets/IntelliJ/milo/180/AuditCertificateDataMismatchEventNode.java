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
import org.eclipse.milo.opcua.sdk.client.api.model.types.objects.AuditCertificateDataMismatchEventType;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;


public class AuditCertificateDataMismatchEventNode extends AuditCertificateEventNode implements AuditCertificateDataMismatchEventType {

    public AuditCertificateDataMismatchEventNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }

    @Override
    public CompletableFuture<PropertyNode> invalidHostname() {
        return getPropertyNode(AuditCertificateDataMismatchEventType.INVALID_HOSTNAME.getBrowseName());
    }

    @Override
    public CompletableFuture<String> getInvalidHostname() {
        return getProperty(AuditCertificateDataMismatchEventType.INVALID_HOSTNAME);
    }

    @Override
    public CompletableFuture<StatusCode> setInvalidHostname(String value) {
        return setProperty(AuditCertificateDataMismatchEventType.INVALID_HOSTNAME, value);
    }

    @Override
    public CompletableFuture<PropertyNode> invalidUri() {
        return getPropertyNode(AuditCertificateDataMismatchEventType.INVALID_URI.getBrowseName());
    }

    @Override
    public CompletableFuture<String> getInvalidUri() {
        return getProperty(AuditCertificateDataMismatchEventType.INVALID_URI);
    }

    @Override
    public CompletableFuture<StatusCode> setInvalidUri(String value) {
        return setProperty(AuditCertificateDataMismatchEventType.INVALID_URI, value);
    }


}