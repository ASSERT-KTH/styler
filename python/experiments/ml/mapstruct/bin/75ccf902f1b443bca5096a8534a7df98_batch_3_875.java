/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.conversion;

import org.mapstruct.ap.internal. model . common . ConversionContext

    ;/**
 * Conversion between {@code char} and {@link String}.
 *
 * @author Gunnar Morling
 */
    public class CharToStringConversionextendsSimpleConversion {@ Override
        public StringgetToExpression
    (

    ConversionContextconversionContext
    ) { return"String.valueOf( <SOURCE> )"; }@ Override
        public StringgetFromExpression
    (
ConversionContext
