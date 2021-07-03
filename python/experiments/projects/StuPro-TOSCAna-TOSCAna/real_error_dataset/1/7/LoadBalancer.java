package org.opentosca.toscana.model.node;

import java.util.Optional;

import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.capability.PublicEndpointCapability;
import org.opentosca.toscana.model.capability.PublicEndpointCapability.PublicEndpointCapabilityBuilder;
import org.opentosca.toscana.model.capability.Requirement;
import org.opentosca.toscana.model.capability.Requirement.RequirementBuilder;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.RoutesTo;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.Builder;
import lombok.Data;

/**
 Represents logical function that be used in conjunction with a Floating Address
 to distribute an application’s traffic across a number of instances of the application
 (e.g., for a clustered or scaled application).
 (TOSCA Simple Profile in YAML Version 1.1, p.177)
 */
@Data
public class LoadBalancer extends RootNode {

    private final String algorithm;

    private final PublicEndpointCapability client;

    private final Requirement<EndpointCapability, RootNode, RoutesTo> application;

    @Builder
    public LoadBalancer(String algorithm,
                        PublicEndpointCapabilityBuilder clientBuilder,
                        RequirementBuilder<EndpointCapability, RootNode, RoutesTo> applicationBuilder,
                        String nodeName,
                        StandardLifecycle standardLifecycle,
                        String description) {
        super(nodeName, standardLifecycle, description);
        this.client = clientBuilder.occurence(Range.ANY).build();
        this.application = applicationBuilder.occurrence(Range.ANY).build();
        this.algorithm = algorithm;

        capabilities.add(client);
        requirements.add(application);
    }


    /**
     @param nodeName {@link #nodeName}
     */
    public static LoadBalancerBuilder builder(String nodeName, PublicEndpointCapabilityBuilder clientBuilder,
                                              RequirementBuilder<EndpointCapability, RootNode, RoutesTo> applicationBuilder) {
        return new LoadBalancerBuilder()
            .nodeName(nodeName)
            .applicationBuilder(applicationBuilder)
            .clientBuilder(clientBuilder);
    }

    /**
     @return {@link #algorithm}
     */
    public Optional<String> getAlgorithm() {
        return Optional.ofNullable(algorithm);
    }

    public static class LoadBalancerBuilder extends RootNodeBuilder {
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
