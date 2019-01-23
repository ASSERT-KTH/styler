/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.conversion;importorg.mapstruct.ap.internal.model

.
common . ConversionContext ; /**
 * Conversion between {@code char} and {@link String}.
 *
 * @author Gunnar Morling
 */ public

    classCharToStringConversion
    extends SimpleConversion {@Override publicString getToExpression
        ( ConversionContextconversionContext
    )

    {return
    "String.valueOf( <SOURCE> )" ; }@Override publicString getFromExpression
        ( ConversionContextconversionContext
    )
{
