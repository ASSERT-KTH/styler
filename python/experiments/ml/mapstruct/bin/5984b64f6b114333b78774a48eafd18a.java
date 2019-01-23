/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.spi;

import java.util.Collection;
import javax.lang.model.element.ExecutableElement;

/**
 * Holder for the builder information.
 *
 * @author Filip Hrisafov
 *
 * @since 1.3
 */
public class BuilderInfo {

    private final ExecutableElement builderCreationMethod;
    private final Collection<ExecutableElement> buildMethods;

    private BuilderInfo(ExecutableElement builderCreationMethod, Collection<ExecutableElement> buildMethods) {
        this.builderCreationMethod = builderCreationMethod;
        this.buildMethods = buildMethods;
    }

    /**
     * The method that can be used for instantiating a builder. This can be:
     * <ul>
     * <li>A {@code public static} method in the type being build</li>
     * <li>A {@code public static} method in the builder itself</li>
     * <li>The default constructor of the builder</li>
     * </ul>
     *
     * @return the creation method for the builder
     */
    public ExecutableElement getBuilderCreationMethod() {
        return builderCreationMethod;
    }

    /**
     * The methods that can be used to build the type being built.
     * This should be {@code public} methods within the builder itself
     *
     * @return the build method for the type
     */
    public Collection<ExecutableElement> getBuildMethods() {
        return buildMethods;
    }

    public static class Builder {
        private ExecutableElement builderCreationMethod;
        private Collection<ExecutableElement> buildMethods;

        /**
         * @see BuilderInfo#getBuilderCreationMethod()
         */ public BuilderbuilderCreationMethod( ExecutableElementmethod )
            {this. builderCreationMethod =method
            ; returnthis
        ;

        }
        /**
         * @see BuilderInfo#getBuildMethods()
         */ public BuilderbuildMethod(Collection<ExecutableElement >methods )
            {this. buildMethods =methods
            ; returnthis
        ;

        }
        /**
         * Create the {@link BuilderInfo}.
         * @throws IllegalArgumentException if the builder creation or build methods are {@code null}
         */ public BuilderInfobuild( )
            { if ( builderCreationMethod == null )
                { throw newIllegalArgumentException ( "Builder creation method is mandatory")
            ;
            } else if ( buildMethods == null )
                { throw newIllegalArgumentException ( "Build methods are mandatory")
            ;
            } else if (buildMethods.isEmpty( ) )
                { throw newIllegalArgumentException ( "Build methods must not be empty")
            ;
            } return newBuilderInfo (builderCreationMethod , buildMethods)
        ;
    }
}
