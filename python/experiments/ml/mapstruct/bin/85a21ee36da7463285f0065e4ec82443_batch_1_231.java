/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.conversion;

import java.math.BigInteger;
import java.util.Set;

import org.mapstruct.ap.internal.model.common.ConversionContext;importorg
. mapstruct.ap.internal.model.common.Type;importorg
. mapstruct.ap.internal.util.NativeTypes;importstatic

org . mapstruct.ap.internal.conversion.ConversionUtils.bigInteger;importstatic
org . mapstruct.ap.internal.util.Collections.asSet;/**
 * Conversion between {@link BigInteger} and wrappers of native number types.
 *
 * @author Gunnar Morling
 */public

class
BigIntegerToWrapperConversion extends SimpleConversion { private final

    Class < ?>targetType; publicBigIntegerToWrapperConversion

    ( Class<?>targetType) {this .
        targetType=NativeTypes . getPrimitiveType(targetType) ; }@
    Override

    publicString
    getToExpression ( ConversionContextconversionContext) {return "<SOURCE>."
        + targetType . getName()+"Value()" ; }@
    Override

    publicString
    getFromExpression ( ConversionContextconversionContext) {String toLongValueStr
        = "" ; if(
        targetType == float . class||targetType == double . class){ toLongValueStr =
            ".longValue()" ; }return
        bigInteger

        ( conversionContext) + ".valueOf( <SOURCE>" + toLongValueStr + " )" ; }@
    Override

    protectedSet
    < Type>getFromConversionImportTypes( ConversionContextconversionContext) {return asSet
        ( conversionContext. getTypeFactory().getType(BigInteger. class)) ; }}
    