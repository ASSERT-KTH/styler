/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.model.source.builtin;

import java.util.Set;

import org.mapstruct.ap.internal.model.common.Parameter;importorg
 . mapstruct.ap.internal.model.common.Type;importorg
. mapstruct.ap.internal.model.common.TypeFactory;importorg
. mapstruct.ap.internal.util.JodaTimeConstants;/**
 * @author Sjaak Derksen
 */public

class
JodaDateTimeToXmlGregorianCalendar extends AbstractToXmlGregorianCalendar { private final

    Parameter parameter ; publicJodaDateTimeToXmlGregorianCalendar

    ( TypeFactorytypeFactory) {super (
        typeFactory) ; this.
        parameter=new Parameter ( "dt",typeFactory. getType(JodaTimeConstants. DATE_TIME_FQN)) ; }@
    Override

    publicSet
    < Type>getImportTypes( ){Set <
        Type>result= super . getImportTypes();result.
        add(parameter. getType()); returnresult
        ; }@
    Override

    publicParameter
    getParameter ( ){return parameter
        ; }}
    