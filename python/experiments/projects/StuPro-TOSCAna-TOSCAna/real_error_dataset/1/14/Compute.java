package org.opentosca.toscana.model.node;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.capability.AdminEndpointCapability;
import org.opentosca.toscana.model.capability.AttachmentCapability;
import org.opentosca.toscana.model.capability.BindableCapability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.Requirement;
import org.opentosca.toscana.model.capability.ScalableCapability;
import org.opentosca.toscana.model.datatype.NetworkInfo;
import org.opentosca.toscana.model.datatype.PortInfo;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.AttachesTo;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.Builder;
import lombok.Data;

/**
 Represents one or more real or virtual processors of software applications or services along with other essential local resources.
 Collectively, the resources the compute node represents can logically be viewed as a (real or virtual) “server”.
 (TOSCA Simple Profile in YAML Version 1.1, p. 169)
 */
@Data
public class Compute extends RootNode {
    /**
     The optional primary private IP address assigned by the cloud provider that applications may use to access the Compute node.
     (TOSCA Simple Profile in YAML Version 1.1, p. 169)
     */
    private final String privateAddress;

    /**
     The optional primary public IP address assigned by the cloud provider that applications may use to access the Compute node.
     (TOSCA Simple Profile in YAML Version 1.1, p. 169)
     */
    private final String publicAddress;

    /**
     The collection of logical networks assigned to the compute host instance and information about them.
     (TOSCA Simple Profile in YAML Version 1.1, p. 169)
     */
    private final Set<NetworkInfo> networks = new HashSet<>();

    /**
     The set of logical ports assigned to this compute host instance and information about them.
     (TOSCA Simple Profile in YAML Version 1.1, p. 169)
     */
    private final Set<PortInfo> ports = new HashSet<>();

    private final ContainerCapability host;

    private final AdminEndpointCapability adminEndpoint;

    private final ScalableCapability scalable;

    private final BindableCapability binding;

    private final Requirement<AttachmentCapability, BlockStorage, AttachesTo> localStorage;

    @Builder
    protected Compute(String privateAddress,
                      String publicAddress,
                      ContainerCapability host,
                      AdminEndpointCapability adminEndpoint,
                      ScalableCapability scalable,
                      BindableCapability binding,
                      Requirement<AttachmentCapability, BlockStorage, AttachesTo> localStorage,
                      String nodeName,
                      StandardLifecycle standardLifecycle,
                      String description) {
        super(nodeName, standardLifecycle, description);
        this.privateAddress = privateAddress;
        this.publicAddress = publicAddress;
        this.host = Objects.requireNonNull(host);
        this.adminEndpoint = Objects.requireNonNull(adminEndpoint);
        this.scalable = Objects.requireNonNull(scalable);
        this.binding = Objects.requireNonNull(binding);
        this.localStorage = Objects.requireNonNull(localStorage);

        capabilities.add(host);
        capabilities.add(adminEndpoint);
        capabilities.add(scalable);
        capabilities.add(binding);
        requirements.add(localStorage);
    }

    /**
     @param nodeName      {@link #nodeName}
     @param adminEndpoint {@link #adminEndpoint}
     @param scalable      {@link #scalable}
     @param binding       {@link #binding}
     @param localStorage  {@link #localStorage}
     */
    public ComputeBuilder builder(String nodeName,
                                  AdminEndpointCapability adminEndpoint,
                                  ScalableCapability scalable,
                                  BindableCapability binding,
                                  Requirement<AttachmentCapability, BlockStorage, AttachesTo> localStorage) {
        return new ComputeBuilder()
            .nodeName(nodeName)
            .adminEndpoint(adminEndpoint)
            .scalable(scalable)
            .binding(binding)
            .localStorage(localStorage);
    }


    /**
     @return {@link #privateAddress}
     */
    public Optional<String> getPrivateAddress() {
        return Optional.ofNullable(privateAddress);
    }

    /**
     @return {@link #publicAddress}
     */
    public Optional<String> getPublicAddress() {
        return Optional.ofNullable(publicAddress);
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}

