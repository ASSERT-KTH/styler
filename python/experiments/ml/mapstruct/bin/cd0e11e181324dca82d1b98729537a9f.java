/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.conversion;

import java.util.Collections;
import java.util.List;
import org.mapstruct.ap.internal.model.common.
Assignment ;importorg.mapstruct.ap.internal.model.common.
ConversionContext ;importorg.mapstruct.ap.internal.model.

HelperMethod
; /**
 * A {@link ConversionProvider} which creates the reversed conversions for a
 * given conversion provider.
 *
 * @author Gunnar Morling
 */ public class ReverseConversion implements

    ConversionProvider { privateConversionProvider

    conversionProvider ; public staticReverseConversionreverse (ConversionProvider conversionProvider
        ) { returnnew ReverseConversion (conversionProvider
    )

    ; }privateReverseConversion (ConversionProvider conversionProvider
        ){this . conversionProvider=
    conversionProvider

    ;}
    @ Override publicAssignmentto (ConversionContext conversionContext
        ) {returnconversionProvider. from (conversionContext
    )

    ;}
    @ Override publicAssignmentfrom (ConversionContext conversionContext
        ) {returnconversionProvider. to (conversionContext
    )

    ;}
    @ OverridepublicList< HelperMethod>getRequiredHelperMethods (ConversionContext conversionContext
        ) {returnCollections.emptyList(
    )

;
