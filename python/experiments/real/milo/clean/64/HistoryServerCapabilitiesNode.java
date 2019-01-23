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

package org.eclipse.milo.opcua.sdk.client.api.model.nodes.objects;

import java.util.concurrent.CompletableFuture;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.model.nodes.variables.PropertyNode;
import org.eclipse.milo.opcua.sdk.client.api.model.types.objects.HistoryServerCapabilitiesType;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;


public class HistoryServerCapabilitiesNode extends BaseObjectNode implements HistoryServerCapabilitiesType {

    public HistoryServerCapabilitiesNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }

    @Override
    public CompletableFuture<PropertyNode> accessHistoryDataCapability() {
        return getPropertyNode(HistoryServerCapabilitiesType.ACCESS_HISTORY_DATA_CAPABILITY.getBrowseName());
    }

    @Override
    public CompletableFuture<Boolean> getAccessHistoryDataCapability() {
        return getProperty(HistoryServerCapabilitiesType.ACCESS_HISTORY_DATA_CAPABILITY);
    }

    @Override
    public CompletableFuture<StatusCode> setAccessHistoryDataCapability(Boolean value) {
        return setProperty(HistoryServerCapabilitiesType.ACCESS_HISTORY_DATA_CAPABILITY, value);
    }

    @Override
    public CompletableFuture<PropertyNode> accessHistoryEventsCapability() {
        return getPropertyNode(HistoryServerCapabilitiesType.ACCESS_HISTORY_EVENTS_CAPABILITY.getBrowseName());
    }

    @Override
    public CompletableFuture<Boolean> getAccessHistoryEventsCapability() {
        return getProperty(HistoryServerCapabilitiesType.ACCESS_HISTORY_EVENTS_CAPABILITY);
    }

    @Override
    public CompletableFuture<StatusCode> setAccessHistoryEventsCapability(Boolean value) {
        return setProperty(HistoryServerCapabilitiesType.ACCESS_HISTORY_EVENTS_CAPABILITY, value);
    }

    @Override
    public CompletableFuture<PropertyNode> maxReturnDataValues() {
        return getPropertyNode(HistoryServerCapabilitiesType.MAX_RETURN_DATA_VALUES.getBrowseName());
    }

    @Override
    public CompletableFuture<UInteger> getMaxReturnDataValues() {
        return getProperty(HistoryServerCapabilitiesType.MAX_RETURN_DATA_VALUES);
    }

    @Override
    public CompletableFuture<StatusCode> setMaxReturnDataValues(UInteger value) {
        return setProperty(HistoryServerCapabilitiesType.MAX_RETURN_DATA_VALUES, value);
    }

    @Override
    public CompletableFuture<PropertyNode> maxReturnEventValues() {
        return getPropertyNode(HistoryServerCapabilitiesType.MAX_RETURN_EVENT_VALUES.getBrowseName());
    }

    @Override
    public CompletableFuture<UInteger> getMaxReturnEventValues() {
        return getProperty(HistoryServerCapabilitiesType.MAX_RETURN_EVENT_VALUES);
    }

    @Override
    public CompletableFuture<StatusCode> setMaxReturnEventValues(UInteger value) {
        return setProperty(HistoryServerCapabilitiesType.MAX_RETURN_EVENT_VALUES, value);
    }

    @Override
    public CompletableFuture<PropertyNode> insertDataCapability() {
        return getPropertyNode(HistoryServerCapabilitiesType.INSERT_DATA_CAPABILITY.getBrowseName());
    }

    @Override
    public CompletableFuture<Boolean> getInsertDataCapability() {
        return getProperty(HistoryServerCapabilitiesType.INSERT_DATA_CAPABILITY);
    }

    @Override
    public CompletableFuture<StatusCode> setInsertDataCapability(Boolean value) {
        return setProperty(HistoryServerCapabilitiesType.INSERT_DATA_CAPABILITY, value);
    }

    @Override
    public CompletableFuture<PropertyNode> replaceDataCapability() {
        return getPropertyNode(HistoryServerCapabilitiesType.REPLACE_DATA_CAPABILITY.getBrowseName());
    }

    @Override
    public CompletableFuture<Boolean> getReplaceDataCapability() {
        return getProperty(HistoryServerCapabilitiesType.REPLACE_DATA_CAPABILITY);
    }

    @Override
    public CompletableFuture<StatusCode> setReplaceDataCapability(Boolean value) {
        return setProperty(HistoryServerCapabilitiesType.REPLACE_DATA_CAPABILITY, value);
    }

    @Override
    public CompletableFuture<PropertyNode> updateDataCapability() {
        return getPropertyNode(HistoryServerCapabilitiesType.UPDATE_DATA_CAPABILITY.getBrowseName());
    }

    @Override
    public CompletableFuture<Boolean> getUpdateDataCapability() {
        return getProperty(HistoryServerCapabilitiesType.UPDATE_DATA_CAPABILITY);
    }

    @Override
    public CompletableFuture<StatusCode> setUpdateDataCapability(Boolean value) {
        return setProperty(HistoryServerCapabilitiesType.UPDATE_DATA_CAPABILITY, value);
    }

    @Override
    public CompletableFuture<PropertyNode> deleteRawCapability() {
        return getPropertyNode(HistoryServerCapabilitiesType.DELETE_RAW_CAPABILITY.getBrowseName());
    }

    @Override
    public CompletableFuture<Boolean> getDeleteRawCapability() {
        return getProperty(HistoryServerCapabilitiesType.DELETE_RAW_CAPABILITY);
    }

    @Override
    public CompletableFuture<StatusCode> setDeleteRawCapability(Boolean value) {
        return setProperty(HistoryServerCapabilitiesType.DELETE_RAW_CAPABILITY, value);
    }

    @Override
    public CompletableFuture<PropertyNode> deleteAtTimeCapability() {
        return getPropertyNode(HistoryServerCapabilitiesType.DELETE_AT_TIME_CAPABILITY.getBrowseName());
    }

    @Override
    public CompletableFuture<Boolean> getDeleteAtTimeCapability() {
        return getProperty(HistoryServerCapabilitiesType.DELETE_AT_TIME_CAPABILITY);
    }

    @Override
    public CompletableFuture<StatusCode> setDeleteAtTimeCapability(Boolean value) {
        return setProperty(HistoryServerCapabilitiesType.DELETE_AT_TIME_CAPABILITY, value);
    }

    @Override
    public CompletableFuture<PropertyNode> insertEventCapability() {
        return getPropertyNode(HistoryServerCapabilitiesType.INSERT_EVENT_CAPABILITY.getBrowseName());
    }

    @Override
    public CompletableFuture<Boolean> getInsertEventCapability() {
        return getProperty(HistoryServerCapabilitiesType.INSERT_EVENT_CAPABILITY);
    }

    @Override
    public CompletableFuture<StatusCode> setInsertEventCapability(Boolean value) {
        return setProperty(HistoryServerCapabilitiesType.INSERT_EVENT_CAPABILITY, value);
    }

    @Override
    public CompletableFuture<PropertyNode> replaceEventCapability() {
        return getPropertyNode(HistoryServerCapabilitiesType.REPLACE_EVENT_CAPABILITY.getBrowseName());
    }

    @Override
    public CompletableFuture<Boolean> getReplaceEventCapability() {
        return getProperty(HistoryServerCapabilitiesType.REPLACE_EVENT_CAPABILITY);
    }

    @Override
    public CompletableFuture<StatusCode> setReplaceEventCapability(Boolean value) {
        return setProperty(HistoryServerCapabilitiesType.REPLACE_EVENT_CAPABILITY, value);
    }

    @Override
    public CompletableFuture<PropertyNode> updateEventCapability() {
        return getPropertyNode(HistoryServerCapabilitiesType.UPDATE_EVENT_CAPABILITY.getBrowseName());
    }

    @Override
    public CompletableFuture<Boolean> getUpdateEventCapability() {
        return getProperty(HistoryServerCapabilitiesType.UPDATE_EVENT_CAPABILITY);
    }

    @Override
    public CompletableFuture<StatusCode> setUpdateEventCapability(Boolean value) {
        return setProperty(HistoryServerCapabilitiesType.UPDATE_EVENT_CAPABILITY, value);
    }

    @Override
    public CompletableFuture<PropertyNode> deleteEventCapability() {
        return getPropertyNode(HistoryServerCapabilitiesType.DELETE_EVENT_CAPABILITY.getBrowseName());
    }

    @Override
    public CompletableFuture<Boolean> getDeleteEventCapability() {
        return getProperty(HistoryServerCapabilitiesType.DELETE_EVENT_CAPABILITY);
    }

    @Override
    public CompletableFuture<StatusCode> setDeleteEventCapability(Boolean value) {
        return setProperty(HistoryServerCapabilitiesType.DELETE_EVENT_CAPABILITY, value);
    }

    @Override
    public CompletableFuture<PropertyNode> insertAnnotationCapability() {
        return getPropertyNode(HistoryServerCapabilitiesType.INSERT_ANNOTATION_CAPABILITY.getBrowseName());
    }

    @Override
    public CompletableFuture<Boolean> getInsertAnnotationCapability() {
        return getProperty(HistoryServerCapabilitiesType.INSERT_ANNOTATION_CAPABILITY);
    }

    @Override
    public CompletableFuture<StatusCode> setInsertAnnotationCapability(Boolean value) {
        return setProperty(HistoryServerCapabilitiesType.INSERT_ANNOTATION_CAPABILITY, value);
    }


    @Override
    public CompletableFuture<FolderNode> aggregateFunctions() {
        return getObjectComponent(QualifiedName.parse("0:AggregateFunctions"))
            .thenApply(FolderNode.class::cast);
    }

}