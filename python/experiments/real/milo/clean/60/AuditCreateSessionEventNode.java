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
import org.eclipse.milo.opcua.sdk.client.api.model.types.objects.AuditCreateSessionEventType;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;


public class AuditCreateSessionEventNode extends AuditSessionEventNode implements AuditCreateSessionEventType {

    public AuditCreateSessionEventNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }

    @Override
    public CompletableFuture<PropertyNode> secureChannelId() {
        return getPropertyNode(AuditCreateSessionEventType.SECURE_CHANNEL_ID.getBrowseName());
    }

    @Override
    public CompletableFuture<String> getSecureChannelId() {
        return getProperty(AuditCreateSessionEventType.SECURE_CHANNEL_ID);
    }

    @Override
    public CompletableFuture<StatusCode> setSecureChannelId(String value) {
        return setProperty(AuditCreateSessionEventType.SECURE_CHANNEL_ID, value);
    }

    @Override
    public CompletableFuture<PropertyNode> clientCertificate() {
        return getPropertyNode(AuditCreateSessionEventType.CLIENT_CERTIFICATE.getBrowseName());
    }

    @Override
    public CompletableFuture<ByteString> getClientCertificate() {
        return getProperty(AuditCreateSessionEventType.CLIENT_CERTIFICATE);
    }

    @Override
    public CompletableFuture<StatusCode> setClientCertificate(ByteString value) {
        return setProperty(AuditCreateSessionEventType.CLIENT_CERTIFICATE, value);
    }

    @Override
    public CompletableFuture<PropertyNode> clientCertificateThumbprint() {
        return getPropertyNode(AuditCreateSessionEventType.CLIENT_CERTIFICATE_THUMBPRINT.getBrowseName());
    }

    @Override
    public CompletableFuture<String> getClientCertificateThumbprint() {
        return getProperty(AuditCreateSessionEventType.CLIENT_CERTIFICATE_THUMBPRINT);
    }

    @Override
    public CompletableFuture<StatusCode> setClientCertificateThumbprint(String value) {
        return setProperty(AuditCreateSessionEventType.CLIENT_CERTIFICATE_THUMBPRINT, value);
    }

    @Override
    public CompletableFuture<PropertyNode> revisedSessionTimeout() {
        return getPropertyNode(AuditCreateSessionEventType.REVISED_SESSION_TIMEOUT.getBrowseName());
    }

    @Override
    public CompletableFuture<Double> getRevisedSessionTimeout() {
        return getProperty(AuditCreateSessionEventType.REVISED_SESSION_TIMEOUT);
    }

    @Override
    public CompletableFuture<StatusCode> setRevisedSessionTimeout(Double value) {
        return setProperty(AuditCreateSessionEventType.REVISED_SESSION_TIMEOUT, value);
    }


}