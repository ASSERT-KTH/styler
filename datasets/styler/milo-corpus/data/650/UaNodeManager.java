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

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

public interface UaNodeManager extends ConcurrentMap<NodeId, UaNode> {

    /**
     * Add a {@link UaNode} to this {@link UaNodeManager}.
     * <p>
     * This method is shorthand for:
     * <pre>
     *     {@code nodeManager.put(node.getNodeId(), node);}
     * </pre>
     *
     * @param node the {@link UaNode} to add.
     */
    default void addNode(UaNode node) {
        put(node.getNodeId(), node);
    }

    /**
     * Add a {@link Reference} to the {@link UaNode} indicated by {@link Reference#getSourceNodeId()}.
     *
     * @param reference the {@link Reference} to add.
     * @return {@code true} if the {@link UaNode} exists and the reference was added.
     */
    default boolean addReference(Reference reference) {
        return getNode(reference.getSourceNodeId()).map(node -> {
            node.addReference(reference);

            return true;
        }).orElse(false);
    }

    /**
     * Check if a {@link UaNode} exists in this {@link UaNodeManager}.
     *
     * @param node the {@link UaNode} in question.
     * @return {@code true} if this {@link UaNodeManager} contains the {@link UaNode}.
     */
    default boolean containsNode(UaNode node) {
        return containsNodeId(node.getNodeId());
    }

    /**
     * Check if a {@link UaNode} identified by {@link NodeId} exists in this {@link UaNodeManager}.
     *
     * @param nodeId the {@link NodeId} of the {@link UaNode} in question.
     * @return {@code true} if this {@link UaNodeManager} contains the {@link UaNode} identified by {@code nodeId}.
     */
    default boolean containsNodeId(NodeId nodeId) {
        return containsKey(nodeId);
    }

    /**
     * Get the {@link UaNode} identified by the provided {@link NodeId}, if it exists.
     *
     * @param nodeId the {@link NodeId} of the {@link UaNode}.
     * @return an {@link Optional} containing the {@link UaNode}, if present.
     */
    default Optional<UaNode> getNode(NodeId nodeId) {
        return Optional.ofNullable(get(nodeId));
    }

    /**
     * Get the {@link UaNode} identified by the provided {@link ExpandedNodeId}, if it exists.
     *
     * @param nodeId the {@link ExpandedNodeId} of the {@link UaNode}.
     * @return an {@link Optional} containing the {@link UaNode}, if present.
     */
    default Optional<UaNode> getNode(ExpandedNodeId nodeId) {
        return nodeId.local().flatMap(this::getNode);
    }

    /**
     * Remove the {@link UaNode} identified by the provided {@link NodeId}, if it exists.
     *
     * @param nodeId the {@link NodeId} of the {@link UaNode}.
     * @return an {@link Optional} containing the {@link UaNode}, if removed.
     */
    default Optional<UaNode> removeNode(NodeId nodeId) {
        return Optional.ofNullable(remove(nodeId));
    }

}
