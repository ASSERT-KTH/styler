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

package org.eclipse.milo.opcua.sdk.server.model.nodes.variables;

import java.util.Optional;

import org.eclipse.milo.opcua.sdk.server.api.UaNodeManager;
import org.eclipse.milo.opcua.sdk.server.api.nodes.VariableNode;
import org.eclipse.milo.opcua.sdk.server.api.nodes.VariableTypeNode;
import org.eclipse.milo.opcua.sdk.server.model.types.variables.TwoStateDiscreteType;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;

@org.eclipse.milo.opcua.sdk.core.annotations.UaVariableNode(typeName = "0:TwoStateDiscreteType")
public class TwoStateDiscreteNode extends DiscreteItemNode implements TwoStateDiscreteType {

    public TwoStateDiscreteNode(
        UaNodeManager nodeManager,
        NodeId nodeId,
        VariableTypeNode variableTypeNode) {

        super(nodeManager, nodeId, variableTypeNode);
    }

    public TwoStateDiscreteNode(
        UaNodeManager nodeManager,
        NodeId nodeId,
        QualifiedName browseName,
        LocalizedText displayName,
        Optional<LocalizedText> description,
        Optional<UInteger> writeMask,
        Optional<UInteger> userWriteMask,
        DataValue value,
        NodeId dataType,
        Integer valueRank,
        Optional<UInteger[]> arrayDimensions,
        UByte accessLevel,
        UByte userAccessLevel,
        Optional<Double> minimumSamplingInterval,
        boolean historizing) {

        super(nodeManager, nodeId, browseName, displayName, description, writeMask, userWriteMask,
            value, dataType, valueRank, arrayDimensions, accessLevel, userAccessLevel, minimumSamplingInterval, historizing);
    }


    @Override
    public LocalizedText getFalseState() {
        Optional<LocalizedText> property = getProperty(TwoStateDiscreteType.FALSE_STATE);

        return property.orElse(null);
    }

    @Override
    public PropertyNode getFalseStateNode() {
        Optional<VariableNode> propertyNode = getPropertyNode(TwoStateDiscreteType.FALSE_STATE.getBrowseName());

        return propertyNode.map(n -> (PropertyNode) n).orElse(null);
    }

    @Override
    public void setFalseState(LocalizedText value) {
        setProperty(TwoStateDiscreteType.FALSE_STATE, value);
    }

    @Override
    public LocalizedText getTrueState() {
        Optional<LocalizedText> property = getProperty(TwoStateDiscreteType.TRUE_STATE);

        return property.orElse(null);
    }

    @Override
    public PropertyNode getTrueStateNode() {
        Optional<VariableNode> propertyNode = getPropertyNode(TwoStateDiscreteType.TRUE_STATE.getBrowseName());

        return propertyNode.map(n -> (PropertyNode) n).orElse(null);
    }

    @Override
    public void setTrueState(LocalizedText value) {
        setProperty(TwoStateDiscreteType.TRUE_STATE, value);
    }

}
