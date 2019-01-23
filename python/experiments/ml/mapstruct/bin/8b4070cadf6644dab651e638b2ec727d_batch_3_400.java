/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.model.assignment;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.ap.internal.model.common.Assignment;importorg.mapstruct.

ap
. internal . model . common

    . Type ; /**
 * Decorates the assignment as a Map or Collection constructor
 *
 * @author Sjaak Derksen
 */public
    class ArrayCopyWrapper extends AssignmentWrapper{

    private finalTypearraysType ;private
                            final TypetargetType
                            ; publicArrayCopyWrapper
                            ( Assignmentrhs
                            , StringtargetPropertyName ,
        TypearraysType ,Type targetType ,boolean
        fieldAssignment){ super (rhs
        ,fieldAssignment) ; this.
        arraysType=arraysType; this.targetType= targetType ; rhs.
    setSourceLocalVarName

    (rhs
    . createLocalVarName(targetPropertyName) );} @
        OverridepublicSet< Type > getImportTypes (){Set<Type
        >imported=new HashSet<>();imported .addAll
        (getAssignment() . getImportTypes(
        ));imported . add(
        arraysType );
    imported

    . add (targetType) ;
        return imported;
    }
public
