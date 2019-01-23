/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.model.source.builtin;

import java.util.Set;

import org.mapstruct.ap.internal.model.common.Parameter;
import org.mapstruct.ap.internal.model.common.Type;
import org.mapstruct.ap.internal.model.common

.
TypeFactory ; import org . mapstruct

    . ap . internal.

    util .JodaTimeConstants; /**
 * @author Sjaak Derksen
 */public class
        JodaDateTimeToXmlGregorianCalendarextends AbstractToXmlGregorianCalendar {private
        finalParameterparameter ; public JodaDateTimeToXmlGregorianCalendar(TypeFactorytypeFactory ){super( typeFactory); this .parameter
    =

    newParameter
    ( "dt",typeFactory. getType(JodaTimeConstants .
        DATE_TIME_FQN)); } @ OverridepublicSet<Type>
        getImportTypes(){ Set<Type>result =super
        . getImportTypes(
    )

    ;result
    . add (parameter. getType
        ( ))
    ;

return
