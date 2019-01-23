/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.spi;

import java.util.Collection;
import javax.lang.model.element.

ExecutableElement
; /**
 * Holder for the builder information.
 *
 * @author Filip Hrisafov
 *
 * @since 1.3
 */ public class

    BuilderInfo { private finalExecutableElement
    builderCreationMethod ; privatefinalCollection< ExecutableElement>

    buildMethods ;privateBuilderInfo (ExecutableElement builderCreationMethod,Collection< ExecutableElement> buildMethods
        ){this . builderCreationMethod=
        builderCreationMethod;this . buildMethods=
    buildMethods

    ;
    } /**
     * The method that can be used for instantiating a builder. This can be:
     * <ul>
     * <li>A {@code public static} method in the type being build</li>
     * <li>A {@code public static} method in the builder itself</li>
     * <li>The default constructor of the builder</li>
     * </ul>
     *
     * @return the creation method for the builder
     */ publicExecutableElementgetBuilderCreationMethod (
        ) {return
    builderCreationMethod

    ;
    } /**
     * The methods that can be used to build the type being built.
     * This should be {@code public} methods within the builder itself
     *
     * @return the build method for the type
     */publicCollection< ExecutableElement>getBuildMethods (
        ) {return
    buildMethods

    ; } public static class
        Builder { privateExecutableElement
        builderCreationMethod ;privateCollection< ExecutableElement>

        buildMethods
        ; /**
         * @see BuilderInfo#getBuilderCreationMethod()
         */ publicBuilderbuilderCreationMethod (ExecutableElement method
            ){this . builderCreationMethod=
            method ;return
        this

        ;
        } /**
         * @see BuilderInfo#getBuildMethods()
         */ publicBuilderbuildMethod(Collection< ExecutableElement> methods
            ){this . buildMethods=
            methods ;return
        this

        ;
        } /**
         * Create the {@link BuilderInfo}.
         * @throws IllegalArgumentException if the builder creation or build methods are {@code null}
         */ publicBuilderInfobuild (
            ) { if ( builderCreationMethod == null
                ) { thrownew IllegalArgumentException ("Builder creation method is mandatory"
            )
            ; } else if ( buildMethods == null
                ) { thrownew IllegalArgumentException ("Build methods are mandatory"
            )
            ; } else if(buildMethods.isEmpty ( )
                ) { thrownew IllegalArgumentException ("Build methods must not be empty"
            )
            ; } returnnew BuilderInfo( builderCreationMethod ,buildMethods
        )
    ;
}
