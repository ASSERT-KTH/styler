package org.opentosca.toscana.model.node;

import java.util.Objects;
import java.util.Optional;

import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.operation.StandardLifecycle;

import lombok.Builder;
import lombok.Data;

/**
 Represents storage that provides the ability to store data as objects (or BLOBs of data)
 without consideration for the underlying filesystem or devices.
 (TOSCA Simple Profile in YAML Version 1.1, p. 174)
 */
@Data
public class ObjectStorage extends RootNode {

    /**
     The logical name of the object store (or container).
     (TOSCA Simple Profile in YAML Version 1.1, p. 174)
     */
    private final String storageName;

    /**
     The optional requested initial storage size in GB.
     (TOSCA Simple Profile in YAML Version 1.1, p. 174)
     */
    private final Integer sizeInGB;

    /**
     The optional requested maximum storage size in GB.
     (TOSCA Simple Profile in YAML Version 1.1, p. 174)
     */
    private final Integer maxSizeInGB;

    private final EndpointCapability storageEndpoint;

    @Builder
    private ObjectStorage(String storageName,
                          Integer sizeInGB,
                          Integer maxSizeInGB,
                          EndpointCapability storageEndpoint,
                          String nodeName,
                          StandardLifecycle standardLifecycle,
                          String description) {
        super(nodeName, standardLifecycle, description);
        if ((sizeInGB != null && sizeInGB < 0) || (sizeInGB != null && maxSizeInGB < 0)) {
            throw new IllegalArgumentException("Size for ObjectStorage must not be < 0");
        }
        this.storageName = Objects.requireNonNull(storageName);
        this.sizeInGB = sizeInGB;
        this.maxSizeInGB = maxSizeInGB;
        this.storageEndpoint = Objects.requireNonNull(storageEndpoint);

        capabilities.add(storageEndpoint);
    }

    /**
     @param nodeName        {@link #nodeName}
     @param storageName     {@link #storageName}
     @param storageEndpoint {@link #storageEndpoint}
     */
    public static ObjectStorageBuilder builder(String nodeName,
                                               String storageName,
                                               EndpointCapability storageEndpoint) {
        return new ObjectStorageBuilder()
            .nodeName(nodeName)
            .storageName(storageName)
            .storageEndpoint(storageEndpoint);
    }


    /**
     @return {@link #sizeInGB}
     */
    public Optional<Integer> getSizeInGB() {
        return Optional.ofNullable(sizeInGB);
    }

    /**
     @return {@link #maxSizeInGB}
     */
    public Optional<Integer> getMaxSizeInGB() {
        return Optional.ofNullable(maxSizeInGB);
    }
}
