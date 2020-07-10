package org.opentosca.toscana.model.capability;

import java.net.URL;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.datatype.NetworkProtocol;
import org.opentosca.toscana.model.datatype.Port;
import org.opentosca.toscana.model.datatype.PortSpec;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.Visitor;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 Represents a public endpoint which is accessible to the general internet (and its public IP
 address ranges).
 <p>
 This public endpoint capability also can be used to create a floating (IP) address that the underlying
 network assigns from a pool allocated from the application’s underlying public network. This floating
 address is managed by the underlying network such that can be routed an application’s private address
 and remains reliable to internet clients.
 (TOSCA Simple Profile in YAML Version 1.1, p. 154)
 <p>
 Note: If the networkName set to the name of a network (or sub-network) that is not public
 (i.e., has non-public IP address ranges assigned to it), then TOSCA orchestrators SHALL treat this as an error.
 (TOSCA Simple Profile in YAML Version 1.1, p. 155)
 */
@Data
public class PublicEndpointCapability extends EndpointCapability {

    /**
     indicates that the public address should be allocated from a pool of floating IPs
     that are associated with the network
     (TOSCA Simple Profile in YAML Version 1.1, p. 155)
     */
    private final boolean floating;

    /**
     The optional name to register with DNS (experimental)
     <p>
     Note: If a dns_name is set, TOSCA orchestrators SHALL attempt to register the name
     in the (local) DNS registry for the Cloud Provider.
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p. 155)
     */
    private final String dnsName;

    @Builder
    protected PublicEndpointCapability(boolean floating,
                                       String dnsName,
                                       NetworkProtocol protocol,
                                       Port port,
                                       boolean secure,
                                       URL urlPath,
                                       String portName,
                                       String networkName,
                                       Initiator initiator,
                                       @Singular Set<PortSpec> ports,
                                       String ipAddress,
                                       @Singular Set<Class<? extends RootNode>> validSourceTypes,
                                       Range occurence,
                                       String description) {
        super(protocol, port, secure, urlPath, portName, (networkName != null ? networkName : "PUBLIC"),
            initiator, ports, ipAddress, validSourceTypes, occurence, description);
        if (networkName.equalsIgnoreCase("private")) {
            throw new IllegalArgumentException("Constraint violation: network name must not equal 'private'");
        }
        this.floating = floating;
        this.dnsName = dnsName;
    }

    /**
     @return {@link #dnsName}
     */
    public Optional<String> getDnsName() {
        return Optional.ofNullable(dnsName);
    }


    /**
     @param ipAddress {@link #ipAddress}
     */
    public static PublicEndpointCapabilityBuilder builder(String ipAddress) {
        return new PublicEndpointCapabilityBuilder().ipAddress(ipAddress);
    }

    public static class PublicEndpointCapabilityBuilder extends EndpointCapabilityBuilder {
    }
    
    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
