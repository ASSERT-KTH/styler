/*
 * Copyright (c) 2019 the Eclipse Milo Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.milo.opcua.sdk.server.model.nodes.variables;

import java.util.Optional;

import org.eclipse.milo.opcua.sdk.server.api.nodes.VariableNode;
import org.eclipse.milo.opcua.sdk.server.model.types.variables.TwoStateDiscreteType;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNodeContext;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;

public class TwoStateDiscreteTypeNode extends DiscreteItemTypeNode implements TwoStateDiscreteType {
    public TwoStateDiscreteTypeNode(UaNodeContext context, NodeId nodeId, QualifiedName browseName,
                                    LocalizedText displayName, LocalizedText description, UInteger writeMask,
                                    UInteger userWriteMask) {
        super(context, nodeId, browseName, displayName, description, writeMask, userWriteMask);
    }

    public TwoStateDiscreteTypeNode(UaNodeContext context, NodeId nodeId, QualifiedName browseName,
                                    LocalizedText displayName, LocalizedText description, UInteger writeMask,
                                    UInteger userWriteMask, DataValue value, NodeId dataType, Integer valueRank,
                                    UInteger[] arrayDimensions, UByte accessLevel, UByte userAccessLevel,
                                    double minimumSamplingInterval, boolean historizing) {
        super(context, nodeId, browseName, displayName, description, writeMask, userWriteMask, value, dataType, valueRank, arrayDimensions, accessLevel, userAccessLevel, minimumSamplingInterval, historizing);
    }

    @Override
    public PropertyTypeNode getFalseStateNode() {
        Optional<VariableNode> propertyNode = getPropertyNode(TwoStateDiscreteType.FALSE_STATE);
        return (PropertyTypeNode) propertyNode.orElse(null);
    }

    @Override
    public LocalizedText getFalseState() {
        Optional<LocalizedText> propertyValue = getProperty(TwoStateDiscreteType.FALSE_STATE);
        return propertyValue.orElse(null);
    }

    @Override
    public void setFalseState(LocalizedText value) {
        setProperty(TwoStateDiscreteType.FALSE_STATE, value);
    }

    @Override
    public PropertyTypeNode getTrueStateNode() {
        Optional<VariableNode> propertyNode = getPropertyNode(TwoStateDiscreteType.TRUE_STATE);
        return (PropertyTypeNode) propertyNode.orElse(null);
    }

    @Override
    public LocalizedText getTrueState() {
        Optional<LocalizedText> propertyValue = getProperty(TwoStateDiscreteType.TRUE_STATE);
        return propertyValue.orElse(null);
    }

    @Override
    public void setTrueState(LocalizedText value) {
        setProperty(TwoStateDiscreteType.TRUE_STATE, value);
    }
}
