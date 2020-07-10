package org.opentosca.toscana.model.node;

import java.util.Objects;

import org.opentosca.toscana.model.capability.AdminEndpointCapability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.operation.StandardLifecycle;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

/**
 Represents an abstract software component or service that is capable of hosting and providing management operations
 for one or more {@link WebApplication} nodes.
 <p>
 This node SHALL export both a secure endpoint capability ({@link #adminEndpoint}), typically for
 administration, as well as a regular endpoint ({@link #dataEndpoint}) for serving data.
 (TOSCA Simple Profile in YAML Version 1.1, p.171)
 */
@Data
public class WebServer extends SoftwareComponent {

    // public by design (is hiding parent field of different type -> getter conflict)
    @Getter(AccessLevel.NONE)
    public final ContainerCapability host;

    private final EndpointCapability dataEndpoint;

    private final AdminEndpointCapability adminEndpoint;

    @Builder
    protected WebServer(String componentVersion,
                        Credential adminCredential,
                        ContainerCapability host,
                        EndpointCapability dataEndpoint,
                        AdminEndpointCapability adminEndpoint,
                        String nodeName,
                        StandardLifecycle standardLifecycle,
                        String description) {
        super(componentVersion, adminCredential, nodeName, standardLifecycle, description);
        this.host = Objects.requireNonNull(host);
        this.dataEndpoint = Objects.requireNonNull(dataEndpoint);
        this.adminEndpoint = Objects.requireNonNull(adminEndpoint);

        capabilities.add(host);
        capabilities.add(dataEndpoint);
        capabilities.add(adminEndpoint);
    }


    /**
     @param nodeName      {@link #nodeName}
     @param host          {@link #host}
     @param dataEndpoint  {@link #dataEndpoint}
     @param adminEndpoint {@link #adminEndpoint}
     */
    public static WebServerBuilder builder(String nodeName,
                                           ContainerCapability host,
                                           EndpointCapability dataEndpoint,
                                           AdminEndpointCapability adminEndpoint) {
        return new WebServerBuilder()
            .nodeName(nodeName)
            .host(host)
            .dataEndpoint(dataEndpoint)
            .adminEndpoint(adminEndpoint);
    }

}
