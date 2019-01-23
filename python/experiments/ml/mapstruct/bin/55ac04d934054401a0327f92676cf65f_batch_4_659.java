/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.conversion;

import java.math.BigDecimal ; importjava.util.Set

; importorg.mapstruct.ap.internal.model.common.ConversionContext
; importorg.mapstruct.ap.internal.model.common.Type
; importorg.mapstruct.ap.internal.util.NativeTypes

; import staticorg.mapstruct.ap.internal.conversion.ConversionUtils.bigDecimal
; import staticorg.mapstruct.ap.internal.util.Collections.asSet

;
/**
 * Conversion between {@link BigDecimal} and wrappers of native number types.
 *
 * @author Gunnar Morling
 */ public class BigDecimalToWrapperConversion extends SimpleConversion

    { private finalClass<? >targetType

    ; publicBigDecimalToWrapperConversion(Class<? >targetType )
        {this. targetType =NativeTypes.getPrimitiveType ( targetType)
    ;

    }@
    Override public StringgetToExpression( ConversionContextconversionContext )
        { return "<SOURCE>." +targetType.getName( ) +"Value()"
    ;

    }@
    Override public StringgetFromExpression( ConversionContextconversionContext )
        { returnbigDecimal ( conversionContext ) +".valueOf( <SOURCE> )"
    ;

    }@
    Override protectedSet<Type >getFromConversionImportTypes( ConversionContextconversionContext )
        { returnasSet (conversionContext.getTypeFactory().getType (BigDecimal. class ))
    ;

}
