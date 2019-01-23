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
import org.mapstruct.ap.

internal .model.common.TypeFactory;importstaticorg

. mapstruct .ap.internal.util.Collections.asSet;/**
 * @author Sjaak Derksen
 */publicclass

DateToXmlGregorianCalendar
extends AbstractToXmlGregorianCalendar { private final Parameter

    parameter ; private finalSet
    < Type >importTypes;public DateToXmlGregorianCalendar(

    TypeFactory typeFactory){ super( typeFactory
        ); this .parameter
        =newParameter ( "date" ,typeFactory .getType (Date.class )); this .importTypes
        =asSet( parameter .getType
            (),typeFactory.getType
            (GregorianCalendar.class )); }
        @Override
    public

    Set<
    Type >getImportTypes() {Set< Type
        >result=super . getImportTypes ();result.addAll
        (this.importTypes );return result;
        } @Override
    public

    ParametergetParameter
    ( ) {returnparameter ;
        } }