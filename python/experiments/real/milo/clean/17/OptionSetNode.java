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
import org.eclipse.milo.opcua.sdk.client.api.model.types.variables.OptionSetType;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;


public class OptionSetNode extends BaseDataVariableNode implements OptionSetType {

    public OptionSetNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }

    @Override
    public CompletableFuture<PropertyNode> optionSetValues() {
        return getPropertyNode(OptionSetType.OPTION_SET_VALUES.getBrowseName());
    }

    @Override
    public CompletableFuture<LocalizedText[]> getOptionSetValues() {
        return getProperty(OptionSetType.OPTION_SET_VALUES);
    }

    @Override
    public CompletableFuture<StatusCode> setOptionSetValues(LocalizedText[] value) {
        return setProperty(OptionSetType.OPTION_SET_VALUES, value);
    }

    @Override
    public CompletableFuture<PropertyNode> bitMask() {
        return getPropertyNode(OptionSetType.BIT_MASK.getBrowseName());
    }

    @Override
    public CompletableFuture<Boolean[]> getBitMask() {
        return getProperty(OptionSetType.BIT_MASK);
    }

    @Override
    public CompletableFuture<StatusCode> setBitMask(Boolean[] value) {
        return setProperty(OptionSetType.BIT_MASK, value);
    }


}