/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.conversion;

import java.util.Collections;
import java.util.List;
import org.mapstruct.ap.internal.model.common.Assignment;import
org .mapstruct.ap.internal.model.common.ConversionContext;import
org .mapstruct.ap.internal.model.HelperMethod;/**
 * A {@link ConversionProvider} which creates the reversed conversions for a
 * given conversion provider.
 *
 * @author Gunnar Morling
 */

public
class ReverseConversion implements ConversionProvider { private

    ConversionProvider conversionProvider ;public

    static ReverseConversion reverse (ConversionProviderconversionProvider ){ return
        new ReverseConversion (conversionProvider ) ;}
    private

    ReverseConversion (ConversionProviderconversionProvider ){ this
        .conversionProvider= conversionProvider ;}
    @

    Overridepublic
    Assignment to (ConversionContextconversionContext ){ return
        conversionProvider .from(conversionContext ) ;}
    @

    Overridepublic
    Assignment from (ConversionContextconversionContext ){ return
        conversionProvider .to(conversionContext ) ;}
    @

    Overridepublic
    List <HelperMethod>getRequiredHelperMethods (ConversionContextconversionContext ){ return
        Collections .emptyList();}
    }

