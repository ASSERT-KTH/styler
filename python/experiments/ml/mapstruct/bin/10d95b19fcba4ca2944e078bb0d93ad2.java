/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.model;

import java.util.Collections;
import java.util.Set;

import org.mapstruct.ap.internal.model.common.ModelElement
; importorg.mapstruct.ap.internal.model.common.Type

;
/**
 * Represents the constructor of a decorator.
 *
 * @author Gunnar Morling
 */ public class DecoratorConstructor extends ModelElement implements Constructor

    { private final Stringname
    ; private final StringdelegateName
    ; private final booleaninvokeSuperConstructor

    ; publicDecoratorConstructor( Stringname , StringdelegateName , booleaninvokeSuperConstructor )
        {this. name =name
        ;this. delegateName =delegateName
        ;this. invokeSuperConstructor =invokeSuperConstructor
    ;

    }@
    Override publicSet<Type >getImportTypes( )
        { returnCollections.emptySet()
    ;

    }@
    Override public StringgetName( )
        { returnname
    ;

    } public StringgetDelegateName( )
        { returndelegateName
    ;

    } public booleanisInvokeSuperConstructor( )
        { returninvokeSuperConstructor
    ;
}
