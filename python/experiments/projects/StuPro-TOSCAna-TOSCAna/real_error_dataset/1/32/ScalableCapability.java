package org.opentosca.toscana.model.capability;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.Visitor;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import static java.lang.String.format;

/**
 The default TOSCA type that should be used to express a scalability capability of a node.
 (TOSCA Simple Profile in YAML Version 1.1, p. 158)
 */
@Data
public class ScalableCapability extends Capability {

    /**
     Indicates the minimum and maximum number of instances that should be created
     for the associated TOSCA Node Template by a TOSCA orchestrator.
     (TOSCA Simple Profile in YAML Version 1.1, p. 157)
     */
    private final Range scaleRange;

    /**
     The optional default number of instances that should be the starting number of instances
     a TOSCA orchestrator should attempt to allocate.
     (TOSCA Simple Profile in YAML Version 1.1, p. 157)
     */
    private final Integer defaultInstances;

    @Builder
    protected ScalableCapability(Range scaleRange,
                                 Integer defaultInstances,
                                 @Singular Set<Class<? extends RootNode>> validSourceTypes,
                                 Range occurence,
                                 String description) {
        super(validSourceTypes, occurence, description);
        if (defaultInstances != null && !scaleRange.inRange(defaultInstances)) {
            throw new IllegalArgumentException(format(
                "Constraint violation: range.min (%d) <= defaultInstances (%d) <= range.max (%d)",
                scaleRange.min, defaultInstances, scaleRange.max));
        }
        this.scaleRange = Objects.requireNonNull(scaleRange);
        this.defaultInstances = defaultInstances;
    }

    /**
     @param scaleRange {@link #scaleRange}
     */
    public static ScalableCapabilityBuilder builder(Range scaleRange) {
        return new ScalableCapabilityBuilder().scaleRange(scaleRange);
    }


    /**
     @return {@link #defaultInstances}
     */
    public Optional<Integer> getDefaultInstances() {
        return Optional.ofNullable(defaultInstances);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
