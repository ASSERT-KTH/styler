/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.conversion;

import java. util.Collections;importjava
. util.List;importorg
. mapstruct.ap.internal.model.common.Assignment;importorg
. mapstruct.ap.internal.model.common.ConversionContext;importorg
. mapstruct.ap.internal.model.HelperMethod;/**
 * A {@link ConversionProvider} which creates the reversed conversions for a
 * given conversion provider.
 *
 * @author Gunnar Morling
 */public

class
ReverseConversion implements ConversionProvider { private ConversionProvider

    conversionProvider ; publicstatic

    ReverseConversion reverse ( ConversionProviderconversionProvider) {return new
        ReverseConversion ( conversionProvider) ; }private
    ReverseConversion

    ( ConversionProviderconversionProvider) {this .
        conversionProvider=conversionProvider ; }@
    Override

    publicAssignment
    to ( ConversionContextconversionContext) {return conversionProvider
        . from(conversionContext) ; }@
    Override

    publicAssignment
    from ( ConversionContextconversionContext) {return conversionProvider
        . to(conversionContext) ; }@
    Override

    publicList
    < HelperMethod>getRequiredHelperMethods( ConversionContextconversionContext) {return Collections
        . emptyList();}}
    