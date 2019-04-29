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

package org.eclipse.milo.opcua.sdk.server.api;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.ForwardingConcurrentMap;
import com.google.common.collect.MapMaker;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

public abstract class AbstractUaNodeManager extends ForwardingConcurrentMap<NodeId, UaNode> implements UaNodeManager {

    private final ConcurrentMap<NodeId, UaNode> nodeMap;

    public AbstractUaNodeManager() {
        MapMaker mapMaker = new MapMaker();

        nodeMap = makeNodeMap(mapMaker);
    }

    protected ConcurrentMap<NodeId, UaNode> makeNodeMap(MapMaker mapMaker) {
        return mapMaker.makeMap();
    }

    @Override
    protected final ConcurrentMap<NodeId, UaNode> delegate() {
        return nodeMap;
    }

}
