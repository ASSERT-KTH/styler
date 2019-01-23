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

package org.eclipse.milo.opcua.sdk.server.model.nodes.objects;

import java.util.Optional;

import org.eclipse.milo.opcua.sdk.core.annotations.UaObjectNode;
import org.eclipse.milo.opcua.sdk.server.api.UaNodeManager;
import org.eclipse.milo.opcua.sdk.server.api.nodes.VariableNode;
import org.eclipse.milo.opcua.sdk.server.model.nodes.variables.TwoStateVariableNode;
import org.eclipse.milo.opcua.sdk.server.model.types.objects.NonExclusiveLimitAlarmType;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;

@UaObjectNode(typeName = "0:NonExclusiveLimitAlarmType")
public class NonExclusiveLimitAlarmNode extends LimitAlarmNode implements NonExclusiveLimitAlarmType {

    public NonExclusiveLimitAlarmNode(
        UaNodeManager nodeManager,
        NodeId nodeId,
        QualifiedName browseName,
        LocalizedText displayName,
        Optional<LocalizedText> description,
        Optional<UInteger> writeMask,
        Optional<UInteger> userWriteMask,
        UByte eventNotifier) {

        super(nodeManager, nodeId, browseName, displayName, description, writeMask, userWriteMask, eventNotifier);
    }

    @Override
    public LocalizedText getActiveState() {
        Optional<VariableNode> component = getVariableComponent("ActiveState");

        return component.map(node -> (LocalizedText) node.getValue().getValue().getValue()).orElse(null);
    }

    @Override
    public TwoStateVariableNode getActiveStateNode() {
        Optional<VariableNode> component = getVariableComponent("ActiveState");

        return component.map(node -> (TwoStateVariableNode) node).orElse(null);
    }

    @Override
    public void setActiveState(LocalizedText value) {
        getVariableComponent("ActiveState")
            .ifPresent(n -> n.setValue(new DataValue(new Variant(value))));
    }

    @Override
    public LocalizedText getHighHighState() {
        Optional<VariableNode> component = getVariableComponent("HighHighState");

        return component.map(node -> (LocalizedText) node.getValue().getValue().getValue()).orElse(null);
    }

    @Override
    public TwoStateVariableNode getHighHighStateNode() {
        Optional<VariableNode> component = getVariableComponent("HighHighState");

        return component.map(node -> (TwoStateVariableNode) node).orElse(null);
    }

    @Override
    public void setHighHighState(LocalizedText value) {
        getVariableComponent("HighHighState")
            .ifPresent(n -> n.setValue(new DataValue(new Variant(value))));
    }

    @Override
    public LocalizedText getHighState() {
        Optional<VariableNode> component = getVariableComponent("HighState");

        return component.map(node -> (LocalizedText) node.getValue().getValue().getValue()).orElse(null);
    }

    @Override
    public TwoStateVariableNode getHighStateNode() {
        Optional<VariableNode> component = getVariableComponent("HighState");

        return component.map(node -> (TwoStateVariableNode) node).orElse(null);
    }

    @Override
    public void setHighState(LocalizedText value) {
        getVariableComponent("HighState")
            .ifPresent(n -> n.setValue(new DataValue(new Variant(value))));
    }

    @Override
    public LocalizedText getLowState() {
        Optional<VariableNode> component = getVariableComponent("LowState");

        return component.map(node -> (LocalizedText) node.getValue().getValue().getValue()).orElse(null);
    }

    @Override
    public TwoStateVariableNode getLowStateNode() {
        Optional<VariableNode> component = getVariableComponent("LowState");

        return component.map(node -> (TwoStateVariableNode) node).orElse(null);
    }

    @Override
    public void setLowState(LocalizedText value) {
        getVariableComponent("LowState")
            .ifPresent(n -> n.setValue(new DataValue(new Variant(value))));
    }

    @Override
    public LocalizedText getLowLowState() {
        Optional<VariableNode> component = getVariableComponent("LowLowState");

        return component.map(node -> (LocalizedText) node.getValue().getValue().getValue()).orElse(null);
    }

    @Override
    public TwoStateVariableNode getLowLowStateNode() {
        Optional<VariableNode> component = getVariableComponent("LowLowState");

        return component.map(node -> (TwoStateVariableNode) node).orElse(null);
    }

    @Override
    public void setLowLowState(LocalizedText value) {
        getVariableComponent("LowLowState")
            .ifPresent(n -> n.setValue(new DataValue(new Variant(value))));
    }

}
