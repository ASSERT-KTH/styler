/*
 * Copyright (c) 2019 the Eclipse Milo Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.milo.opcua.sdk.server.model.nodes.objects;

import java.util.Optional;

import org.eclipse.milo.opcua.sdk.server.api.nodes.ObjectNode;
import org.eclipse.milo.opcua.sdk.server.api.nodes.VariableNode;
import org.eclipse.milo.opcua.sdk.server.model.nodes.variables.TwoStateVariableTypeNode;
import org.eclipse.milo.opcua.sdk.server.model.types.objects.ExclusiveLimitAlarmType;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNodeContext;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;

public class ExclusiveLimitAlarmTypeNode extends LimitAlarmTypeNode implements ExclusiveLimitAlarmType {
    public ExclusiveLimitAlarmTypeNode(UaNodeContext context, NodeId nodeId, QualifiedName browseName,
                                       LocalizedText displayName, LocalizedText description, UInteger writeMask,
                                       UInteger userWriteMask) {
        super(context, nodeId, browseName, displayName, description, writeMask, userWriteMask);
    }

    public ExclusiveLimitAlarmTypeNode(UaNodeContext context, NodeId nodeId, QualifiedName browseName,
                                       LocalizedText displayName, LocalizedText description, UInteger writeMask,
                                       UInteger userWriteMask, UByte eventNotifier) {
        super(context, nodeId, browseName, displayName, description, writeMask, userWriteMask, eventNotifier);
    }

    @Override
    public TwoStateVariableTypeNode getActiveStateNode() {
        Optional<VariableNode> component = getVariableComponent("http://opcfoundation.org/UA/", "ActiveState");
        return (TwoStateVariableTypeNode) component.orElse(null);
    }

    @Override
    public LocalizedText getActiveState() {
        Optional<VariableNode> component = getVariableComponent("http://opcfoundation.org/UA/", "ActiveState");
        return component.map(node -> (LocalizedText) node.getValue().getValue().getValue()).orElse(null);
    }

    @Override
    public void setActiveState(LocalizedText value) {
        getVariableComponent("http://opcfoundation.org/UA/", "ActiveState").ifPresent(n -> n.setValue(new DataValue(new Variant(value))));
    }

    @Override
    public ExclusiveLimitStateMachineTypeNode getLimitStateNode() {
        Optional<ObjectNode> component = getObjectComponent("http://opcfoundation.org/UA/", "LimitState");
        return (ExclusiveLimitStateMachineTypeNode) component.orElse(null);
    }
}
