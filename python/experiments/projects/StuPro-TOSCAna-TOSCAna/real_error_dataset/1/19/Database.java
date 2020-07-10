package org.opentosca.toscana.model.node;

import java.util.Objects;
import java.util.Optional;

import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.DatabaseEndpointCapability;
import org.opentosca.toscana.model.capability.Requirement;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.Builder;
import lombok.Data;

/**
 Represents a logical database that can be managed and hosted by a {@link Dbms} node.
 (TOSCA Simple Profile in YAML Version 1.1, p. 173)
 */
@Data
public class Database extends RootNode {

    public final Requirement<ContainerCapability, Dbms, HostedOn> host;

    /**
     The logical database databaseName.
     (TOSCA Simple Profile in YAML Version 1.1, p. 173)
     */
    private final String databaseName;

    /**
     The optional port the database service will use for incoming data and request.
     (TOSCA Simple Profile in YAML Version 1.1, p. 173)
     */
    private final Integer port;

    /**
     The optional special user account used for database administration.
     (TOSCA Simple Profile in YAML Version 1.1, p. 173)
     */
    private final String user;

    /**
     The optional password associated with the user account provided in the {@link #user} field.
     (TOSCA Simple Profile in YAML Version 1.1, p. 173)
     */
    private final String password;


    private final DatabaseEndpointCapability databaseEndpoint;

    @Builder
    private Database(String databaseName,
                     Integer port,
                     String user,
                     String password,
                     Requirement<ContainerCapability, Dbms, HostedOn> host,
                     DatabaseEndpointCapability databaseEndpoint,
                     String nodeName,
                     StandardLifecycle standardLifecycle,
                     String description) {
        super(nodeName, standardLifecycle, description);
        this.databaseName = Objects.requireNonNull(databaseName);
        this.port = port;
        this.user = user;
        this.password = password;
        this.host = Objects.requireNonNull(host);
        this.databaseEndpoint = Objects.requireNonNull(databaseEndpoint);

        capabilities.add(databaseEndpoint);
        requirements.add(host);
    }

    // only use when subclassing this and hiding host field
    protected Database(String databaseName,
                       Integer port,
                       String user,
                       String password,
                       DatabaseEndpointCapability databaseEndpoint,
                       String nodeName,
                       StandardLifecycle standardLifecycle,
                       String description) {
        super(nodeName, standardLifecycle, description);
        this.databaseName = Objects.requireNonNull(databaseName);
        this.port = port;
        this.user = user;
        this.password = password;
        this.host = null;
        this.databaseEndpoint = Objects.requireNonNull(databaseEndpoint);

        capabilities.add(databaseEndpoint);
    }

    /**
     @param nodeName         {@link #nodeName}
     @param databaseName     {@link #databaseName}
     @param host             {@link #host}
     @param databaseEndpoint {@link #databaseEndpoint}
     */
    public static DatabaseBuilder builder(String nodeName,
                                          String databaseName,
                                          Requirement<ContainerCapability, Dbms, HostedOn> host,
                                          DatabaseEndpointCapability databaseEndpoint) {
        return new DatabaseBuilder()
            .nodeName(nodeName)
            .databaseName(databaseName)
            .host(host)
            .databaseEndpoint(databaseEndpoint);
    }


    /**
     @return {@link #port}
     */
    public Optional<Integer> getPort() {
        return Optional.ofNullable(port);
    }

    /**
     @return {@link #user}
     */
    public Optional<String> getUser() {
        return Optional.ofNullable(user);
    }

    /**
     @return {@link #password}
     */
    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
