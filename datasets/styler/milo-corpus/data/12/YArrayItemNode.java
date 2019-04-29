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

package org.eclipse.milo.opcua.sdk.client.api.model.nodes.variables;

import java.util.concurrent.CompletableFuture;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.model.types.variables.YArrayItemType;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.structured.AxisInformation;


public class YArrayItemNode extends ArrayItemNode implements YArrayItemType {

    public YArrayItemNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }

    @Override
    public CompletableFuture<PropertyNode> xAxisDefinition() {
        return getPropertyNode(YArrayItemType.X_AXIS_DEFINITION.getBrowseName());
    }

    @Override
    public CompletableFuture<AxisInformation> getXAxisDefinition() {
        return getProperty(YArrayItemType.X_AXIS_DEFINITION);
    }

    @Override
    public CompletableFuture<StatusCode> setXAxisDefinition(AxisInformation value) {
        return setProperty(YArrayItemType.X_AXIS_DEFINITION, value);
    }


}