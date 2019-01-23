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
import org.mapstruct.ap.internal.
model.common.Type;importorg
. mapstruct.ap.internal.model.common.TypeFactory;importstatic

org . mapstruct.ap.internal.util.Collections.asSet;/**
 * @author Sjaak Derksen
 */public

class
DateToXmlGregorianCalendar extends AbstractToXmlGregorianCalendar { private final

    Parameter parameter ; privatefinal
    Set < Type>importTypes; publicDateToXmlGregorianCalendar

    ( TypeFactorytypeFactory) {super (
        typeFactory) ; this.
        parameter=new Parameter ( "date", typeFactory. getType(Date. class)) ; this.
        importTypes=asSet ( parameter.
            getType(),typeFactory.
            getType(GregorianCalendar. class)) ;
        }@
    Override

    publicSet
    < Type>getImportTypes( ){Set <
        Type>result= super . getImportTypes();result.
        addAll(this. importTypes); returnresult
        ; }@
    Override

    publicParameter
    getParameter ( ){return parameter
        ; }}
    