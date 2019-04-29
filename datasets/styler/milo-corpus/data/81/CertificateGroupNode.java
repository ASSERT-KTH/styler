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
import org.eclipse.milo.opcua.sdk.client.api.model.types.objects.CertificateGroupType;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;


public class CertificateGroupNode extends BaseObjectNode implements CertificateGroupType {

    public CertificateGroupNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }

    @Override
    public CompletableFuture<PropertyNode> certificateTypes() {
        return getPropertyNode(CertificateGroupType.CERTIFICATE_TYPES.getBrowseName());
    }

    @Override
    public CompletableFuture<NodeId[]> getCertificateTypes() {
        return getProperty(CertificateGroupType.CERTIFICATE_TYPES);
    }

    @Override
    public CompletableFuture<StatusCode> setCertificateTypes(NodeId[] value) {
        return setProperty(CertificateGroupType.CERTIFICATE_TYPES, value);
    }


    @Override
    public CompletableFuture<TrustListNode> trustList() {
        return getObjectComponent(QualifiedName.parse("0:TrustList"))
            .thenApply(TrustListNode.class::cast);
    }

}