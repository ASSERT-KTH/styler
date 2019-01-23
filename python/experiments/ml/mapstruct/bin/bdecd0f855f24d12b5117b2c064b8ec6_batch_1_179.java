/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.conversion;

import java.util.Collections;
import java.util.List;
import org.mapstruct.ap.internal.model.common.Assignment;
import org.mapstruct.ap.internal.
model .common.ConversionContext;importorg.mapstruct.ap.

internal
. model . HelperMethod ; /**
 * A {@link ConversionProvider} which creates the reversed conversions for a
 * given conversion provider.
 *
 * @author Gunnar Morling
 */

    public class ReverseConversionimplements

    ConversionProvider { private ConversionProviderconversionProvider; publicstatic ReverseConversion
        reverse ( ConversionProviderconversionProvider ) {return
    new

    ReverseConversion (conversionProvider) ;} private
        ReverseConversion(ConversionProvider conversionProvider ){
    this

    .conversionProvider
    = conversionProvider ;}@ Overridepublic Assignment
        to (ConversionContextconversionContext) { returnconversionProvider
    .

    from(
    conversionContext ) ;}@ Overridepublic Assignment
        from (ConversionContextconversionContext) { returnconversionProvider
    .

    to(
    conversionContext );}@ OverridepublicList <HelperMethod >
        getRequiredHelperMethods (ConversionContextconversionContext){return
    Collections

.
