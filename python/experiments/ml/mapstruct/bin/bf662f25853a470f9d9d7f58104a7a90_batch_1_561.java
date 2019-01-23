/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.model.source.builtin

; importjava.util.Set;importorg.mapstruct.ap.
internal .model.common.Parameter;importorg.mapstruct.ap.
internal .model.common.Type;importorg.mapstruct.ap.
internal .model.common.TypeFactory;importorg.mapstruct.

ap
. internal . util . JodaTimeConstants

    ; /**
 * @author Sjaak Derksen
 */ public classJodaDateTimeToXmlGregorianCalendar

    extends AbstractToXmlGregorianCalendar{private finalParameter parameter
        ;public JodaDateTimeToXmlGregorianCalendar (TypeFactory
        typeFactory){ super ( typeFactory);this .parameter=new Parameter("dt" , typeFactory.
    getType

    (JodaTimeConstants
    . DATE_TIME_FQN)); }@Override public
        Set<Type> getImportTypes ( ){Set<Type>
        result=super. getImportTypes();result .add
        ( parameter.
    getType

    ()
    ) ; returnresult; }
        @ Overridepublic
    Parameter

getParameter
