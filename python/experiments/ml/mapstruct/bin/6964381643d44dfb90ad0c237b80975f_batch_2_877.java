/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.conversion;

import java.math.BigDecimal;
import java.util.Set;

import org.mapstruct.ap.internal.
model .common.ConversionContext;importorg.mapstruct.ap.internal.model.common
. Type;importorg.mapstruct.ap.internal.util.NativeTypes
; importstaticorg.mapstruct.ap.internal.conversion.

ConversionUtils . bigDecimal;importstaticorg.mapstruct.ap.internal.util.
Collections . asSet;/**
 * Conversion between {@link BigDecimal} and wrappers of native number types.
 *
 * @author Gunnar Morling
 */publicclassBigDecimalToWrapperConversionextendsSimpleConversion{privatefinalClass<?

>
targetType ; public BigDecimalToWrapperConversion ( Class

    < ? >targetType){ this.

    targetType =NativeTypes.getPrimitiveType(targetType ); }
        @Overridepublic String getToExpression(ConversionContextconversionContext ) {return
    "<SOURCE>."

    +targetType
    . getName ()+ "Value()"; }
        @ Override public StringgetFromExpression(ConversionContextconversionContext ) {return
    bigDecimal

    (conversionContext
    ) + ".valueOf( <SOURCE> )";} @Override protected
        Set <Type > getFromConversionImportTypes ( ConversionContextconversionContext
    )

    {return
    asSet (conversionContext.getTypeFactory (). getType( BigDecimal
        . class) );}}