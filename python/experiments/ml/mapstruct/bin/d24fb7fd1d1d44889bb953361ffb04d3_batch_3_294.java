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

import static org.mapstruct.ap.
internal .conversion.ConversionUtils.bigDecimal;importstaticorg
. mapstruct .ap.internal.util.Collections.asSet;/**
 * Conversion between {@link BigDecimal} and wrappers of native number types.
 *
 * @author Gunnar Morling
 */publicclass

BigDecimalToWrapperConversion
extends SimpleConversion { private final Class

    < ? >targetType;public BigDecimalToWrapperConversion(

    Class <?>targetType){ this. targetType
        =NativeTypes. getPrimitiveType (targetType); } @Override
    public

    StringgetToExpression
    ( ConversionContext conversionContext){ return"<SOURCE>." +
        targetType . getName ()+"Value()"; } @Override
    public

    StringgetFromExpression
    ( ConversionContext conversionContext){ returnbigDecimal (
        conversionContext )+ ".valueOf( <SOURCE> )" ; } @Override
    protected

    Set<
    Type >getFromConversionImportTypes(ConversionContext conversionContext){ returnasSet (
        conversionContext .getTypeFactory ().getType(BigDecimal.class )); } }