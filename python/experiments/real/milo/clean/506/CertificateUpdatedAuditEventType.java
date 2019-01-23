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

package org.eclipse.milo.opcua.sdk.server.model.types.objects;

import org.eclipse.milo.opcua.sdk.core.model.BasicProperty;
import org.eclipse.milo.opcua.sdk.core.model.Property;
import org.eclipse.milo.opcua.sdk.server.model.types.variables.PropertyType;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;

public interface CertificateUpdatedAuditEventType extends AuditUpdateMethodEventType {

    Property<NodeId> CERTIFICATE_GROUP = new BasicProperty<>(
        QualifiedName.parse("0:CertificateGroup"),
        NodeId.parse("ns=0;i=17"),
        -1,
        NodeId.class
    );

    Property<NodeId> CERTIFICATE_TYPE = new BasicProperty<>(
        QualifiedName.parse("0:CertificateType"),
        NodeId.parse("ns=0;i=17"),
        -1,
        NodeId.class
    );

    NodeId getCertificateGroup();

    PropertyType getCertificateGroupNode();

    void setCertificateGroup(NodeId value);

    NodeId getCertificateType();

    PropertyType getCertificateTypeNode();

    void setCertificateType(NodeId value);
}
