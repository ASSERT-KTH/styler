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
import org.eclipse.milo.opcua.stack.core.types.structured.AddNodesItem;

public interface AuditAddNodesEventType extends AuditNodeManagementEventType {

    Property<AddNodesItem[]> NODES_TO_ADD = new BasicProperty<>(
        QualifiedName.parse("0:NodesToAdd"),
        NodeId.parse("ns=0;i=376"),
        1,
        AddNodesItem[].class
    );

    AddNodesItem[] getNodesToAdd();

    PropertyType getNodesToAddNode();

    void setNodesToAdd(AddNodesItem[] value);
}
