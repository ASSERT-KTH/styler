/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.conversion;

import java.math.BigInteger;
import java.util.Set;

import org.mapstruct.ap.internal.model.
common .ConversionContext;importorg.mapstruct.ap.internal.model.
common .Type;importorg.mapstruct.ap.internal.

util . NativeTypes;importstaticorg.mapstruct.ap.internal.conversion.
ConversionUtils . bigInteger;importstaticorg.mapstruct.ap.internal.util.

Collections
. asSet ; /**
 * Conversion between {@link BigInteger} and wrappers of native number types.
 *
 * @author Gunnar Morling
 */ public class

    BigIntegerToWrapperConversion extends SimpleConversion{privatefinal Class<

    ? >targetType;publicBigIntegerToWrapperConversion( Class< ?
        >targetType) { this.targetType= NativeTypes .getPrimitiveType
    (

    targetType)
    ; } @Overridepublic StringgetToExpression (
        ConversionContext conversionContext ) {return"<SOURCE>."+targetType . getName(
    )

    +"Value()"
    ; } @Overridepublic StringgetFromExpression (
        ConversionContext conversionContext ) {String
        toLongValueStr = "" ; if(targetType == float . class||targetType == double
            . class ){
        toLongValueStr

        = ".longValue()"; } return bigInteger ( conversionContext ) + ".valueOf( <SOURCE>"+
    toLongValueStr

    +" )"
    ; }@Overrideprotected Set<Type >getFromConversionImportTypes (
        ConversionContext conversionContext) {returnasSet(conversionContext.getTypeFactory( ).getType ( BigInteger.
    class

)
