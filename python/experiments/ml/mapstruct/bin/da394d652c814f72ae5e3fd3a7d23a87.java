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
import org.mapstruct.ap.internal.model.common.Type;/**
 * Decorates an assignment as local variable.
 *
 * @author Sjaak Derksen
 */public
class
LocalVarWrapper extends AssignmentWrapper { private final

    List < Type>thrownTypesToExclude; privatefinal
    Type targetType ; publicLocalVarWrapper

    ( AssignmentdecoratedAssignment, List< Type>thrownTypesToExclude, TypetargetType , booleanfieldAssignment
                           ) {super (
        decoratedAssignment, fieldAssignment) ; this.
        thrownTypesToExclude=thrownTypesToExclude ; this.
        targetType=targetType ; }@
    Override

    publicList
    < Type>getThrownTypes( ){List <
        Type>parentThrownTypes= super . getThrownTypes();List<
        Type>result= new ArrayList < >(parentThrownTypes) ; for(
        Type thrownTypeToExclude : thrownTypesToExclude ) { for (
            Type parentThrownType : parentThrownTypes ) { if (
                parentThrownType . isAssignableTo(thrownTypeToExclude) ) { result .
                    remove(parentThrownType) ; }}
                }
            return
        result
        ; }@
    Override

    publicSet
    < Type>getImportTypes( ){Set <
        Type>imported= new HashSet < >(getAssignment( ).getImportTypes()); imported.
        add(targetType) ; imported.
        addAll(targetType. getTypeParameters()); returnimported
        ; }}
    