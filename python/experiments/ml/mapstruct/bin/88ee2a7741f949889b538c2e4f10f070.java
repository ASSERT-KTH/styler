/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.conversion;

import java.math.BigDecimal;
import java.util.Set;

import org.mapstruct.ap.internal.model.common.ConversionContext;
import org.mapstruct.ap.internal.model.common.Type;
import org.mapstruct.ap.internal.util.NativeTypes;

import static org.mapstruct.ap.internal.conversion.ConversionUtils.bigDecimal;
import static org.mapstruct.ap.internal.util.Collections.asSet

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
