/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.model.source.builtin;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import org.mapstruct.ap.internal.model.common.Parameter;
import org.mapstruct.ap.internal.model.common.Type;
import org.mapstruct.ap.internal.model.common.TypeFactory;

import
static org . mapstruct . ap

    . internal . util.
    Collections . asSet;/**
 * @author Sjaak Derksen
 */public classDateToXmlGregorianCalendar

    extends AbstractToXmlGregorianCalendar{private finalParameter parameter
        ;private final Set<
        Type>importTypes ; public DateToXmlGregorianCalendar( TypeFactorytypeFactory ){super( typeFactory); this .parameter
        =newParameter ( "date",
            typeFactory.getType(Date.
            class)); this.importTypes =
        asSet(
    parameter

    .getType
    ( ),typeFactory. getType(GregorianCalendar .
        class)); } @ OverridepublicSet<Type>
        getImportTypes(){ Set<Type >result
        = super.
    getImportTypes

    ()
    ; result .addAll( this
        . importTypes)
    ;

return
