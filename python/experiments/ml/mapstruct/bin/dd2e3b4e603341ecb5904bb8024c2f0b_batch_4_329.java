/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.model.assignment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mapstruct.ap.internal.model.common.Assignment;
import org.mapstruct.ap.internal.

model
. common . Type ; /**
 * Decorates an assignment as local variable.
 *
 * @author Sjaak Derksen
 */

    public class LocalVarWrapperextendsAssignmentWrapper{ privatefinal
    List < Type >thrownTypesToExclude

    ; privatefinalType targetType; publicLocalVarWrapper(Assignment decoratedAssignment, List <Type
                           > thrownTypesToExclude, Type
        targetType, booleanfieldAssignment ) {super
        (decoratedAssignment, fieldAssignment );
        this.thrownTypesToExclude = thrownTypesToExclude;
    this

    .targetType
    = targetType;}@ OverridepublicList <
        Type>getThrownTypes( ) { List<Type>parentThrownTypes=
        super.getThrownTypes( ) ; List <Type>result = newArrayList
        < > ( parentThrownTypes ) ; for (
            Type thrownTypeToExclude : thrownTypesToExclude ) { for (
                Type parentThrownType :parentThrownTypes){ if ( parentThrownType .
                    isAssignableTo(thrownTypeToExclude) ) {result
                .
            remove
        (
        parentThrownType );
    }

    }}
    return result;}@ OverridepublicSet <
        Type>getImportTypes( ) { Set <Type>imported =newHashSet<>(getAssignment ()
        .getImportTypes() ) ;imported
        .add(targetType );imported.addAll (targetType
        . getTypeParameters(
    )

)
