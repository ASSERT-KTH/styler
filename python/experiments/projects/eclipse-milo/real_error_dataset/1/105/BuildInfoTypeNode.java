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
import org.eclipse.milo.opcua.sdk.server.model.types.variables.BuildInfoType;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNodeContext;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;

public class BuildInfoTypeNode extends BaseDataVariableTypeNode implements BuildInfoType {
    public BuildInfoTypeNode(UaNodeContext context, NodeId nodeId, QualifiedName browseName,
                             LocalizedText displayName, LocalizedText description, UInteger writeMask,
                             UInteger userWriteMask) {
        super(context, nodeId, browseName, displayName, description, writeMask, userWriteMask);
    }

    public BuildInfoTypeNode(UaNodeContext context, NodeId nodeId, QualifiedName browseName,
                             LocalizedText displayName, LocalizedText description, UInteger writeMask,
                             UInteger userWriteMask, DataValue value, NodeId dataType, Integer valueRank,
                             UInteger[] arrayDimensions, UByte accessLevel, UByte userAccessLevel,
                             double minimumSamplingInterval, boolean historizing) {
        super(context, nodeId, browseName, displayName, description, writeMask, userWriteMask, value, dataType, valueRank, arrayDimensions, accessLevel, userAccessLevel, minimumSamplingInterval, historizing);
    }

    @Override
    public BaseDataVariableTypeNode getProductUriNode() {
        Optional<VariableNode> component = getVariableComponent("http://opcfoundation.org/UA/", "ProductUri");
        return (BaseDataVariableTypeNode) component.orElse(null);
    }

    @Override
    public String getProductUri() {
        Optional<VariableNode> component = getVariableComponent("ProductUri");
        return component.map(node -> (String) node.getValue().getValue().getValue()).orElse(null);
    }

    @Override
    public void setProductUri(String value) {
        getVariableComponent("ProductUri").ifPresent(n -> n.setValue(new DataValue(new Variant(value))));
    }

    @Override
    public BaseDataVariableTypeNode getManufacturerNameNode() {
        Optional<VariableNode> component = getVariableComponent("http://opcfoundation.org/UA/", "ManufacturerName");
        return (BaseDataVariableTypeNode) component.orElse(null);
    }

    @Override
    public String getManufacturerName() {
        Optional<VariableNode> component = getVariableComponent("ManufacturerName");
        return component.map(node -> (String) node.getValue().getValue().getValue()).orElse(null);
    }

    @Override
    public void setManufacturerName(String value) {
        getVariableComponent("ManufacturerName").ifPresent(n -> n.setValue(new DataValue(new Variant(value))));
    }

    @Override
    public BaseDataVariableTypeNode getProductNameNode() {
        Optional<VariableNode> component = getVariableComponent("http://opcfoundation.org/UA/", "ProductName");
        return (BaseDataVariableTypeNode) component.orElse(null);
    }

    @Override
    public String getProductName() {
        Optional<VariableNode> component = getVariableComponent("ProductName");
        return component.map(node -> (String) node.getValue().getValue().getValue()).orElse(null);
    }

    @Override
    public void setProductName(String value) {
        getVariableComponent("ProductName").ifPresent(n -> n.setValue(new DataValue(new Variant(value))));
    }

    @Override
    public BaseDataVariableTypeNode getSoftwareVersionNode() {
        Optional<VariableNode> component = getVariableComponent("http://opcfoundation.org/UA/", "SoftwareVersion");
        return (BaseDataVariableTypeNode) component.orElse(null);
    }

    @Override
    public String getSoftwareVersion() {
        Optional<VariableNode> component = getVariableComponent("SoftwareVersion");
        return component.map(node -> (String) node.getValue().getValue().getValue()).orElse(null);
    }

    @Override
    public void setSoftwareVersion(String value) {
        getVariableComponent("SoftwareVersion").ifPresent(n -> n.setValue(new DataValue(new Variant(value))));
    }

    @Override
    public BaseDataVariableTypeNode getBuildNumberNode() {
        Optional<VariableNode> component = getVariableComponent("http://opcfoundation.org/UA/", "BuildNumber");
        return (BaseDataVariableTypeNode) component.orElse(null);
    }

    @Override
    public String getBuildNumber() {
        Optional<VariableNode> component = getVariableComponent("BuildNumber");
        return component.map(node -> (String) node.getValue().getValue().getValue()).orElse(null);
    }

    @Override
    public void setBuildNumber(String value) {
        getVariableComponent("BuildNumber").ifPresent(n -> n.setValue(new DataValue(new Variant(value))));
    }

    @Override
    public BaseDataVariableTypeNode getBuildDateNode() {
        Optional<VariableNode> component = getVariableComponent("http://opcfoundation.org/UA/", "BuildDate");
        return (BaseDataVariableTypeNode) component.orElse(null);
    }

    @Override
    public DateTime getBuildDate() {
        Optional<VariableNode> component = getVariableComponent("BuildDate");
        return component.map(node -> (DateTime) node.getValue().getValue().getValue()).orElse(null);
    }

    @Override
    public void setBuildDate(DateTime value) {
        getVariableComponent("BuildDate").ifPresent(n -> n.setValue(new DataValue(new Variant(value))));
    }
}
