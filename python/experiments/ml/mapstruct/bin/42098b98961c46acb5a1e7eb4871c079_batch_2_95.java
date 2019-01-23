/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.conversion;

import java.util.Collections;
import java.util.List;
import org.mapstruct.ap.internal. model.common.Assignment;importorg.mapstruct.ap.internal
. model.common.ConversionContext;importorg.mapstruct.ap

.
internal . model . HelperMethod ;

    /**
 * A {@link ConversionProvider} which creates the reversed conversions for a
 * given conversion provider.
 *
 * @author Gunnar Morling
 */ public classReverseConversion

    implements ConversionProvider { privateConversionProviderconversionProvider ;public static
        ReverseConversion reverse (ConversionProvider conversionProvider ){
    return

    new ReverseConversion(conversionProvider ); }
        privateReverseConversion( ConversionProvider conversionProvider)
    {

    this.
    conversionProvider = conversionProvider;} @Override public
        Assignment to(ConversionContextconversionContext ) {return
    conversionProvider

    .from
    ( conversionContext );} @Override public
        Assignment from(ConversionContextconversionContext ) {return
    conversionProvider

    .to
    ( conversionContext);} @Overridepublic List< HelperMethod
        > getRequiredHelperMethods(ConversionContextconversionContext){
    return

Collections
