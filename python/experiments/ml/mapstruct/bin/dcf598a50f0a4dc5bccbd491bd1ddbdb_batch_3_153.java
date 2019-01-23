/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.conversion;

import java.util.Collections;
import java.util.List;
import org.mapstruct.ap.internal.model.common.Assignment;
import org.mapstruct.ap.internal.model.common.

ConversionContext
; import org . mapstruct .

    ap . internal.

    model . HelperMethod ;/**
 * A {@link ConversionProvider} which creates the reversed conversions for a
 * given conversion provider.
 *
 * @author Gunnar Morling
 */public classReverseConversion implements
        ConversionProvider { privateConversionProvider conversionProvider ;public
    static

    ReverseConversion reverse(ConversionProvider conversionProvider) {
        returnnewReverseConversion ( conversionProvider)
    ;

    }private
    ReverseConversion ( ConversionProviderconversionProvider) {this .
        conversionProvider =conversionProvider;} @ Overridepublic
    Assignment

    to(
    ConversionContext conversionContext ){return conversionProvider. from
        ( conversionContext);} @ Overridepublic
    Assignment

    from(
    ConversionContext conversionContext){return conversionProvider.to (conversionContext )
        ; }@OverridepublicList<
    HelperMethod

>
