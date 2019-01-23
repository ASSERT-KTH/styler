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

package org.eclipse.milo.opcua.sdk.server.nodes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.core.ValueRanks;
import org.eclipse.milo.opcua.sdk.core.model.BasicProperty;
import org.eclipse.milo.opcua.sdk.core.model.Property;
import org.eclipse.milo.opcua.sdk.core.model.UaOptional;
import org.eclipse.milo.opcua.sdk.server.api.UaNodeManager;
import org.eclipse.milo.opcua.sdk.server.api.nodes.Node;
import org.eclipse.milo.opcua.sdk.server.api.nodes.ObjectNode;
import org.eclipse.milo.opcua.sdk.server.api.nodes.ObjectTypeNode;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NamingRuleType;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;

import static org.eclipse.milo.opcua.sdk.core.Reference.HAS_COMPONENT_PREDICATE;
import static org.eclipse.milo.opcua.sdk.core.Reference.HAS_DESCRIPTION_PREDICATE;
import static org.eclipse.milo.opcua.sdk.core.Reference.HAS_EVENT_SOURCE_PREDICATE;
import static org.eclipse.milo.opcua.sdk.core.Reference.HAS_NOTIFIER_PREDICATE;
import static org.eclipse.milo.opcua.sdk.core.Reference.HAS_PROPERTY_PREDICATE;
import static org.eclipse.milo.opcua.sdk.core.Reference.HAS_TYPE_DEFINITION_PREDICATE;
import static org.eclipse.milo.opcua.sdk.core.Reference.ORGANIZES_PREDICATE;
import static org.eclipse.milo.opcua.sdk.core.util.StreamUtil.opt2stream;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ubyte;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

public class UaObjectNode extends UaNode implements ObjectNode {

    private volatile UByte eventNotifier = ubyte(0);

    public UaObjectNode(
        UaNodeManager nodeManager,
        NodeId nodeId,
        QualifiedName browseName,
        LocalizedText displayName) {

        super(nodeManager, nodeId, NodeClass.Object, browseName, displayName);
    }

    public UaObjectNode(
        UaNodeManager nodeManager,
        NodeId nodeId,
        QualifiedName browseName,
        LocalizedText displayName,
        Optional<LocalizedText> description,
        Optional<UInteger> writeMask,
        Optional<UInteger> userWriteMask,
        UByte eventNotifier) {

        super(nodeManager, nodeId, NodeClass.Object,
            browseName, displayName, description, writeMask, userWriteMask);

        this.eventNotifier = eventNotifier;
    }

    @Override
    public UByte getEventNotifier() {
        return eventNotifier;
    }

    @Override
    public synchronized void setEventNotifier(UByte eventNotifier) {
        this.eventNotifier = eventNotifier;

        fireAttributeChanged(AttributeId.EventNotifier, eventNotifier);
    }

    public List<Node> getComponentNodes() {
        return getReferences().stream()
            .filter(HAS_COMPONENT_PREDICATE)
            .flatMap(r -> opt2stream(getNode(r.getTargetNodeId())))
            .collect(Collectors.toList());
    }

    public List<Node> getPropertyNodes() {
        return getReferences().stream()
            .filter(HAS_PROPERTY_PREDICATE)
            .flatMap(r -> opt2stream(getNode(r.getTargetNodeId())))
            .collect(Collectors.toList());
    }

    public ObjectTypeNode getTypeDefinitionNode() {
        Node node = getReferences().stream()
            .filter(HAS_TYPE_DEFINITION_PREDICATE)
            .findFirst()
            .flatMap(r -> getNode(r.getTargetNodeId()))
            .orElse(null);

        return (node instanceof ObjectTypeNode) ? (ObjectTypeNode) node : null;
    }

    public List<Node> getEventSourceNodes() {
        return getReferences().stream()
            .filter(HAS_EVENT_SOURCE_PREDICATE)
            .flatMap(r -> opt2stream(getNode(r.getTargetNodeId())))
            .collect(Collectors.toList());
    }

    public List<Node> getNotifierNodes() {
        return getReferences().stream()
            .filter(HAS_NOTIFIER_PREDICATE)
            .flatMap(r -> opt2stream(getNode(r.getTargetNodeId())))
            .collect(Collectors.toList());
    }

    public List<Node> getOrganizesNodes() {
        return getReferences().stream()
            .filter(ORGANIZES_PREDICATE)
            .flatMap(r -> opt2stream(getNode(r.getTargetNodeId())))
            .collect(Collectors.toList());
    }

    public Optional<Node> getDescriptionNode() {
        Optional<UaNode> node = getReferences().stream()
            .filter(HAS_DESCRIPTION_PREDICATE)
            .findFirst()
            .flatMap(r -> getNode(r.getTargetNodeId()));

        return node.map(n -> n);
    }

    /**
     * Add a 'HasComponent' reference from this Object to {@code node} and an inverse 'ComponentOf' reference from
     * {@code node} back to this Object.
     *
     * @param node the node to add as a component of this Object.
     */
    public void addComponent(UaNode node) {
        addReference(new Reference(
            getNodeId(),
            Identifiers.HasComponent,
            node.getNodeId().expanded(),
            node.getNodeClass(),
            true
        ));

        node.addReference(new Reference(
            node.getNodeId(),
            Identifiers.HasComponent,
            getNodeId().expanded(),
            getNodeClass(),
            false
        ));
    }

    /**
     * Remove the 'HasComponent' reference from this Object to {@code node} and the inverse 'ComponentOf' reference
     * from {@code node} back to this Object.
     *
     * @param node the node to remove as a component of this Object.
     */
    public void removeComponent(UaNode node) {
        removeReference(new Reference(
            getNodeId(),
            Identifiers.HasComponent,
            node.getNodeId().expanded(),
            node.getNodeClass(),
            true
        ));

        node.removeReference(new Reference(
            node.getNodeId(),
            Identifiers.HasComponent,
            getNodeId().expanded(),
            getNodeClass(),
            false
        ));
    }

    @UaOptional("NodeVersion")
    public String getNodeVersion() {
        return getProperty(NodeVersion).orElse(null);
    }

    @UaOptional("Icon")
    public ByteString getIcon() {
        return getProperty(Icon).orElse(null);
    }

    @UaOptional("NamingRule")
    public NamingRuleType getNamingRule() {
        return getProperty(NamingRule).orElse(null);
    }

    public void setNodeVersion(String nodeVersion) {
        setProperty(NodeVersion, nodeVersion);
    }

    public void setIcon(ByteString icon) {
        setProperty(Icon, icon);
    }

    public void setNamingRule(NamingRuleType namingRule) {
        setProperty(NamingRule, namingRule);
    }

    public static final Property<String> NodeVersion = new BasicProperty<>(
        new QualifiedName(0, "NodeVersion"),
        Identifiers.String,
        ValueRanks.Scalar,
        String.class
    );

    public static final Property<ByteString> Icon = new BasicProperty<>(
        new QualifiedName(0, "Icon"),
        Identifiers.Image,
        ValueRanks.Scalar,
        ByteString.class
    );

    public static final Property<NamingRuleType> NamingRule = new BasicProperty<>(
        new QualifiedName(0, "NamingRule"),
        Identifiers.NamingRuleType,
        ValueRanks.Scalar,
        NamingRuleType.class
    );

    public static UaObjectNodeBuilder builder(UaNodeManager nodeManager) {
        return new UaObjectNodeBuilder(nodeManager);
    }

    public static class UaObjectNodeBuilder implements Supplier<UaObjectNode> {

        private final List<Reference> references = Lists.newArrayList();

        private NodeId nodeId;
        private QualifiedName browseName;
        private LocalizedText displayName;
        private Optional<LocalizedText> description = Optional.empty();
        private Optional<UInteger> writeMask = Optional.of(uint(0));
        private Optional<UInteger> userWriteMask = Optional.of(uint(0));
        private UByte eventNotifier = ubyte(0);

        private final UaNodeManager nodeManager;

        public UaObjectNodeBuilder(UaNodeManager nodeManager) {
            this.nodeManager = nodeManager;
        }

        @Override
        public UaObjectNode get() {
            return build();
        }

        /**
         * Builds the configured {@link UaObjectNode}.
         * <p>
         * The following fields are required: NodeId, NodeClass, BrowseName, DisplayName.
         * <p>
         * Exactly one HasTypeDefinition reference must be present.
         *
         * @return a {@link UaObjectNode}.
         * @throws NullPointerException  if any of the required fields are null.
         * @throws IllegalStateException if exactly one HasTypeDefinition reference is not present.
         */
        public UaObjectNode build() {
            Preconditions.checkNotNull(nodeId, "NodeId cannot be null");
            Preconditions.checkNotNull(browseName, "BrowseName cannot be null");
            Preconditions.checkNotNull(displayName, "DisplayName cannot be null");

            long hasTypeDefinitionCount = references.stream()
                .filter(r -> Identifiers.HasTypeDefinition.equals(r.getReferenceTypeId())).count();

            Preconditions.checkState(
                hasTypeDefinitionCount == 1,
                "Object Node must have exactly one HasTypeDefinition reference.");

            // TODO More validation on references.

            UaObjectNode node = new UaObjectNode(
                nodeManager,
                nodeId,
                browseName,
                displayName,
                description,
                writeMask,
                userWriteMask,
                eventNotifier
            );

            node.addReferences(references);

            return node;
        }

        public UaObjectNodeBuilder setNodeId(NodeId nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public UaObjectNodeBuilder setBrowseName(QualifiedName browseName) {
            this.browseName = browseName;
            return this;
        }

        public UaObjectNodeBuilder setDisplayName(LocalizedText displayName) {
            this.displayName = displayName;
            return this;
        }

        public UaObjectNodeBuilder setDescription(LocalizedText description) {
            this.description = Optional.of(description);
            return this;
        }

        public UaObjectNodeBuilder setWriteMask(UInteger writeMask) {
            this.writeMask = Optional.of(writeMask);
            return this;
        }

        public UaObjectNodeBuilder setUserWriteMask(UInteger userWriteMask) {
            this.userWriteMask = Optional.of(userWriteMask);
            return this;
        }

        public UaObjectNodeBuilder setEventNotifier(UByte eventNotifier) {
            this.eventNotifier = eventNotifier;
            return this;
        }

        public UaObjectNodeBuilder addReference(Reference reference) {
            references.add(reference);
            return this;
        }

        /**
         * Convenience method for adding the required HasTypeDefinition reference.
         * <p>
         * {@link #setNodeId(NodeId)} must have already been called before invoking this method.
         *
         * @param typeDefinition The {@link NodeId} of the TypeDefinition.
         * @return this {@link UaObjectNodeBuilder}.
         */
        public UaObjectNodeBuilder setTypeDefinition(NodeId typeDefinition) {
            Objects.requireNonNull(nodeId, "NodeId cannot be null");

            references.add(new Reference(
                nodeId,
                Identifiers.HasTypeDefinition,
                new ExpandedNodeId(typeDefinition),
                NodeClass.ObjectType,
                true
            ));

            return this;
        }

    }

}
