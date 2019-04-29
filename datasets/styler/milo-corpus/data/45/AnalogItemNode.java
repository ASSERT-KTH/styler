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
import org.eclipse.milo.opcua.sdk.client.api.model.types.variables.AnalogItemType;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.structured.EUInformation;
import org.eclipse.milo.opcua.stack.core.types.structured.Range;


public class AnalogItemNode extends DataItemNode implements AnalogItemType {

    public AnalogItemNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }

    @Override
    public CompletableFuture<PropertyNode> instrumentRange() {
        return getPropertyNode(AnalogItemType.INSTRUMENT_RANGE.getBrowseName());
    }

    @Override
    public CompletableFuture<Range> getInstrumentRange() {
        return getProperty(AnalogItemType.INSTRUMENT_RANGE);
    }

    @Override
    public CompletableFuture<StatusCode> setInstrumentRange(Range value) {
        return setProperty(AnalogItemType.INSTRUMENT_RANGE, value);
    }

    @Override
    public CompletableFuture<PropertyNode> eURange() {
        return getPropertyNode(AnalogItemType.E_U_RANGE.getBrowseName());
    }

    @Override
    public CompletableFuture<Range> getEURange() {
        return getProperty(AnalogItemType.E_U_RANGE);
    }

    @Override
    public CompletableFuture<StatusCode> setEURange(Range value) {
        return setProperty(AnalogItemType.E_U_RANGE, value);
    }

    @Override
    public CompletableFuture<PropertyNode> engineeringUnits() {
        return getPropertyNode(AnalogItemType.ENGINEERING_UNITS.getBrowseName());
    }

    @Override
    public CompletableFuture<EUInformation> getEngineeringUnits() {
        return getProperty(AnalogItemType.ENGINEERING_UNITS);
    }

    @Override
    public CompletableFuture<StatusCode> setEngineeringUnits(EUInformation value) {
        return setProperty(AnalogItemType.ENGINEERING_UNITS, value);
    }


}