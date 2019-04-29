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

package org.eclipse.milo.opcua.sdk.server.api.nodes;

import java.util.Optional;

import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;

public interface Node {

    /**
     * See OPC-UA Part 3, section 5.2.2.
     *
     * @return the NodeId ({@link NodeId}) of this node.
     */
    NodeId getNodeId();

    /**
     * See OPC-UA Part 3, section 5.2.3.
     *
     * @return the NodeClass ({@link NodeClass}) of this node.
     */
    NodeClass getNodeClass();

    /**
     * See OPC-UA Part 3, section 5.2.4.
     *
     * @return the BrowseName ({@link QualifiedName}) of this node.
     */
    QualifiedName getBrowseName();

    /**
     * See OPC-UA Part 3, section 5.2.5.
     *
     * @return the DisplayName ({@link QualifiedName}) of this node.
     */
    LocalizedText getDisplayName();

    /**
     * See OPC-UA Part 3, section 5.2.6.
     *
     * @return if this attribute is present, the Description ({@link LocalizedText}).
     */
    Optional<LocalizedText> getDescription();

    /**
     * See OPC-UA Part 3, section 5.2.7.
     *
     * @return if this attribute is present, the WriteMask ({@link UInteger}).
     */
    Optional<UInteger> getWriteMask();

    /**
     * See OPC-UA Part 3, section 5.2.8.
     *
     * @return if this attribute is present, the UserWriteMask ({@link UInteger}).
     */
    Optional<UInteger> getUserWriteMask();

    /**
     * Set the NodeId attribute of this Node.
     *
     * @param nodeId the NodeId to set.
     */
    void setNodeId(NodeId nodeId);

    /**
     * Set the NodeClass attribute of this Node.
     *
     * @param nodeClass the NodeClass to set.
     */
    void setNodeClass(NodeClass nodeClass);

    /**
     * Set the BrowseName attribute of this Node.
     *
     * @param browseName the BrowseName to set.
     */
    void setBrowseName(QualifiedName browseName);

    /**
     * Set the DisplayName attribute of this Node.
     *
     * @param displayName the DisplayName to set.
     */
    void setDisplayName(LocalizedText displayName);

    /**
     * Set the Description attribute of this Node.
     *
     * @param description the Description to set.
     */
    void setDescription(Optional<LocalizedText> description);

    /**
     * Set the WriteMask attribute of this Node.
     *
     * @param writeMask the WriteMask to set.
     */
    void setWriteMask(Optional<UInteger> writeMask);

    /**
     * Set the UserWriteMask attribute of this Node.
     *
     * @param userWriteMask the UserWriteMask to set.
     */
    void setUserWriteMask(Optional<UInteger> userWriteMask);

}
