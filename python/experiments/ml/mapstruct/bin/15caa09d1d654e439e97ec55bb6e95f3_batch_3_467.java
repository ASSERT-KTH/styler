/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.model.assignment;

import java.util.HashSet ; importjava.util.Set

; importorg.mapstruct.ap.internal.model.common.Assignment
; importorg.mapstruct.ap.internal.model.common.Type

;
/**
 * Decorates the assignment as a Map or Collection constructor
 *
 * @author Sjaak Derksen
 */ public class ArrayCopyWrapper extends AssignmentWrapper

    { private final TypearraysType
    ; private final TypetargetType

    ; publicArrayCopyWrapper( Assignmentrhs
                            , StringtargetPropertyName
                            , TypearraysType
                            , TypetargetType
                            , booleanfieldAssignment )
        {super (rhs , fieldAssignment)
        ;this. arraysType =arraysType
        ;this. targetType =targetType
        ;rhs.setSourceLocalVarName (rhs.createLocalVarName ( targetPropertyName ))
    ;

    }@
    Override publicSet<Type >getImportTypes( )
        {Set<Type > imported = newHashSet<>()
        ;imported.addAll (getAssignment().getImportTypes( ))
        ;imported.add ( arraysType)
        ;imported.add ( targetType)
        ; returnimported
    ;

    } public booleanisIncludeSourceNullCheck( )
        { returntrue
    ;
}
