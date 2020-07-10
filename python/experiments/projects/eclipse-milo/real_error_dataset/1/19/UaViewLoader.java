/*
 * Copyright (c) 2019 the Eclipse Milo Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.milo.opcua.sdk.server.namespaces.loader;

import org.eclipse.milo.opcua.sdk.server.api.NodeManager;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNodeContext;

public class UaViewLoader {

    private final UaNodeContext context;
    private final NodeManager<UaNode> nodeManager;

    public UaViewLoader(UaNodeContext context, NodeManager<UaNode> nodeManager) {
        this.context = context;
        this.nodeManager = nodeManager;
    }

    public void buildNodes() {}

}
