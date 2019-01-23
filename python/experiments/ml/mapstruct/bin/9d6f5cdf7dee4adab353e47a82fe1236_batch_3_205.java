/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.
internal .conversion;importjava.
util .Collections;importjava.util.List;importorg.mapstruct
. ap.internal.model.common.Assignment;importorg.mapstruct
. ap.internal.model.common.ConversionContext;importorg

.
mapstruct . ap . internal .

    model . HelperMethod;

    /**
 * A {@link ConversionProvider} which creates the reversed conversions for a
 * given conversion provider.
 *
 * @author Gunnar Morling
 */ public class ReverseConversionimplementsConversionProvider {private ConversionProvider
        conversionProvider ; publicstatic ReverseConversion reverse(
    ConversionProvider

    conversionProvider ){return newReverseConversion (
        conversionProvider); } privateReverseConversion
    (

    ConversionProviderconversionProvider
    ) { this.conversionProvider =conversionProvider ;
        } @OverridepublicAssignment to (ConversionContext
    conversionContext

    ){
    return conversionProvider .from( conversionContext) ;
        } @OverridepublicAssignment from (ConversionContext
    conversionContext

    ){
    return conversionProvider.to( conversionContext); }@ Override
        public List<HelperMethod>getRequiredHelperMethods(
    ConversionContext

conversionContext
