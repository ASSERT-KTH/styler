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
import org.eclipse.milo.opcua.sdk.client.api.model.types.objects.ModellingRuleType;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NamingRuleType;


public class ModellingRuleNode extends BaseObjectNode implements ModellingRuleType {

    public ModellingRuleNode(OpcUaClient client, NodeId nodeId) {
        super(client, nodeId);
    }

    @Override
    public CompletableFuture<PropertyNode> namingRule() {
        return getPropertyNode(ModellingRuleType.NAMING_RULE.getBrowseName());
    }

    @Override
    public CompletableFuture<NamingRuleType> getNamingRule() {
        return getProperty(ModellingRuleType.NAMING_RULE);
    }

    @Override
    public CompletableFuture<StatusCode> setNamingRule(NamingRuleType value) {
        return setProperty(ModellingRuleType.NAMING_RULE, value);
    }


}