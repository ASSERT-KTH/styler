package org.opentosca.toscana.model.relation;

import java.util.Objects;

import javax.validation.constraints.Size;

import org.opentosca.toscana.model.capability.StorageCapability;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.visitor.Visitor;

import lombok.Data;

/**
 Represents an attachment relationship between two nodes.
 <p>
 For example, an AttachesTo relationship would be used for attaching
 a Storage node ({@link StorageCapability}) to a {@link Compute} node.
 <p>
 (TOSCA Simple Profile in YAML Version 1.1, p. 161)
 */
@Data
public class AttachesTo extends RootRelationship {

    /**
     The relative location (e.g., path on the file system), which provides the root location to address an attached node.
     e.g., a mount point / path such as ‘/usr/data’
     <p>
     Note: The user must provide it and it cannot be “root”.
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p. 161)
     */
    @Size(min = 1)
    private final String mountPoint;

    /**
     The optional logical device name which for the attached device (which is represented by the target node in the model).
     <p>
     e.g., ‘/dev/hda1’
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p. 161)
     */
    private final String device;

    protected AttachesTo(String mountPoint,
                         String device,
                         String description) {
        super(description);
        this.mountPoint = Objects.requireNonNull(mountPoint);
        this.device = device;
    }

    /**
     @param mountPoint {@link #mountPoint}
     */
    public static AttachesToBuilder builder(String mountPoint) {
        return new AttachesToBuilder();
    }

    public static class AttachesToBuilder extends RootRelationshipBuilder {
    }


    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
