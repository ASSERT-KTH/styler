/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.conversion;

import java.math.BigInteger;
import java.util.Set;

import org.mapstruct.ap.internal.model.common.ConversionContext;
import org.mapstruct.ap.internal.
model .common.Type;importorg.mapstruct.ap.

internal . util.NativeTypes;importstaticorg.mapstruct.ap.internal.
conversion . ConversionUtils.bigInteger;importstaticorg.mapstruct.ap.internal.

util
. Collections . asSet ; /**
 * Conversion between {@link BigInteger} and wrappers of native number types.
 *
 * @author Gunnar Morling
 */

    public class BigIntegerToWrapperConversionextendsSimpleConversion{ privatefinal

    Class <?>targetType;public BigIntegerToWrapperConversion( Class
        <?> targetType ){this. targetType =NativeTypes
    .

    getPrimitiveType(
    targetType ) ;}@ Overridepublic String
        getToExpression ( ConversionContext conversionContext){return"<SOURCE>." + targetType.
    getName

    ()
    + "Value()" ;}@ Overridepublic String
        getFromExpression ( ConversionContext conversionContext)
        { String toLongValueStr = "";if ( targetType == float.class || targetType
            == double .class
        )

        { toLongValueStr= ".longValue()" ; } return bigInteger ( conversionContext )+
    ".valueOf( <SOURCE>"

    +toLongValueStr
    + " )";}@ OverrideprotectedSet <Type >
        getFromConversionImportTypes (ConversionContext conversionContext){returnasSet(conversionContext. getTypeFactory() . getType(
    BigInteger

.
