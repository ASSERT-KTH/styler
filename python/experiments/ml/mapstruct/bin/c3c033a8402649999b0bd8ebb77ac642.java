/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.conversion;

import java.util.Collections;
import java.util.List;
importorg.mapstruct.ap.internal.model.common.Assignment
; importorg.mapstruct.ap.internal.model.common.ConversionContext
; importorg.mapstruct.ap.internal.model.HelperMethod

;
/**
 * A {@link ConversionProvider} which creates the reversed conversions for a
 * given conversion provider.
 *
 * @author Gunnar Morling
 */ public class ReverseConversion implements ConversionProvider

    { private ConversionProviderconversionProvider

    ; public static ReverseConversionreverse( ConversionProviderconversionProvider )
        { return newReverseConversion ( conversionProvider)
    ;

    } privateReverseConversion( ConversionProviderconversionProvider )
        {this. conversionProvider =conversionProvider
    ;

    }@
    Override public Assignmentto( ConversionContextconversionContext )
        { returnconversionProvider.from ( conversionContext)
    ;

    }@
    Override public Assignmentfrom( ConversionContextconversionContext )
        { returnconversionProvider.to ( conversionContext)
    ;

    }@
    Override publicList<HelperMethod >getRequiredHelperMethods( ConversionContextconversionContext )
        { returnCollections.emptyList()
    ;

}
