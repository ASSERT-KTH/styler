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
import org.eclipse.milo.opcua.sdk.client.api.model.types.variables.DataTypeDictionaryType;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;


public class DataTypeDictionaryNode extends BaseDataVariableNode implements DataTypeDictionaryType {

    public DataTypeDictionaryNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }

    @Override
    public CompletableFuture<PropertyNode> dataTypeVersion() {
        return getPropertyNode(DataTypeDictionaryType.DATA_TYPE_VERSION.getBrowseName());
    }

    @Override
    public CompletableFuture<String> getDataTypeVersion() {
        return getProperty(DataTypeDictionaryType.DATA_TYPE_VERSION);
    }

    @Override
    public CompletableFuture<StatusCode> setDataTypeVersion(String value) {
        return setProperty(DataTypeDictionaryType.DATA_TYPE_VERSION, value);
    }

    @Override
    public CompletableFuture<PropertyNode> namespaceUri() {
        return getPropertyNode(DataTypeDictionaryType.NAMESPACE_URI.getBrowseName());
    }

    @Override
    public CompletableFuture<String> getNamespaceUri() {
        return getProperty(DataTypeDictionaryType.NAMESPACE_URI);
    }

    @Override
    public CompletableFuture<StatusCode> setNamespaceUri(String value) {
        return setProperty(DataTypeDictionaryType.NAMESPACE_URI, value);
    }


}