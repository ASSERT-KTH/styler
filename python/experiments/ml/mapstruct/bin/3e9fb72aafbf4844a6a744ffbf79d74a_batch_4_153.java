/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.conversion;

import java.util.Collections;
import java.util.List;
import org.mapstruct.ap.internal.model
. common.Assignment;importorg.mapstruct.ap.internal

.
model . common . ConversionContext ;

    import org .mapstruct

    . ap . internal.model .HelperMethod ;
        /**
 * A {@link ConversionProvider} which creates the reversed conversions for a
 * given conversion provider.
 *
 * @author Gunnar Morling
 */ public classReverseConversion implements ConversionProvider{
    private

    ConversionProvider conversionProvider;public staticReverseConversion reverse
        (ConversionProviderconversionProvider ) {return
    new

    ReverseConversion(
    conversionProvider ) ;}private ReverseConversion( ConversionProvider
        conversionProvider ){this. conversionProvider =conversionProvider
    ;

    }@
    Override public Assignmentto( ConversionContextconversionContext )
        { returnconversionProvider.from ( conversionContext)
    ;

    }@
    Override publicAssignmentfrom( ConversionContextconversionContext) {return conversionProvider
        . to(conversionContext);}
    @

Override
