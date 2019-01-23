/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.
internal .model.assignment;importjava.

util .HashSet;importjava.
util .Set;importorg.

mapstruct .ap.internal.model.common.Assignment;importorg.
mapstruct .ap.internal.model.common.Type;/**
 * Decorates the assignment as a Map or Collection constructor
 *
 * @author Sjaak Derksen
 */publicclass

ArrayCopyWrapper
extends AssignmentWrapper { private final Type

    arraysType ; private finalType
    targetType ; public ArrayCopyWrapper(

    Assignment rhs,String targetPropertyName,
                            Type arraysType,
                            Type targetType,
                            boolean fieldAssignment)
                            { super( rhs
        ,fieldAssignment ); this .arraysType
        =arraysType; this .targetType
        =targetType; rhs .setSourceLocalVarName
        (rhs.createLocalVarName (targetPropertyName)) ; } @Override
    public

    Set<
    Type >getImportTypes() {Set< Type
        >imported=new HashSet < > ();imported.addAll
        (getAssignment() .getImportTypes());imported .add
        (arraysType); imported .add
        (targetType); return imported;
        } publicboolean
    isIncludeSourceNullCheck

    ( ) {returntrue ;
        } }