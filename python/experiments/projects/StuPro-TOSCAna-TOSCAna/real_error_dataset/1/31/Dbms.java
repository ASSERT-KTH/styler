package org.opentosca.toscana.model.node;

import java.util.Objects;
import java.util.Optional;

import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.visitor.Visitor;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

/**
 Represents a typical relational, SQL Database Management System software component or service.
 (TOSCA Simple Profile in YAML Version 1.1, p. 172)
 */
@Data
public class Dbms extends SoftwareComponent {

    @Getter(AccessLevel.NONE)
    public final ContainerCapability host;

    /**
     The optional root password for the Dbms server.
     (TOSCA Simple Profile in YAML Version 1.1, p. 172)
     */
    private final String rootPassword;

    /**
     The Dbms server’s port.
     (TOSCA Simple Profile in YAML Version 1.1, p. 172)
     */
    private final Integer port;

    @Builder
    protected Dbms(ContainerCapability host,
                   String rootPassword,
                   Integer port,
                   String componentVersion,
                   Credential adminCredential,
                   String nodeName,
                   StandardLifecycle standardLifecycle,
                   String description) {
        super(componentVersion, adminCredential, nodeName, standardLifecycle, description);
        this.host = Objects.requireNonNull(host);
        this.port = port;
        this.rootPassword = rootPassword;

        capabilities.add(host);
    }


    /**
     @param nodeName {@link #nodeName}
     @param host     {@link #host}
     */
    public static DbmsBuilder builder(String nodeName,
                                      ContainerCapability host) {
        return new DbmsBuilder()
            .nodeName(nodeName)
            .host(host);
    }

    /**
     @return {@link #rootPassword}
     */
    public Optional<String> getRootPassword() {
        return Optional.ofNullable(rootPassword);
    }

    /**
     @return {@link #port}
     */
    public Optional<Integer> getPort() {
        return Optional.ofNullable(port);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
