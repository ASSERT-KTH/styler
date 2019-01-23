/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.conversion;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.mapstruct.ap.internal.model.common.ConversionContext;
import org.mapstruct.ap.internal.model.common.Type;
import org.mapstruct.ap.
internal.util.JodaTimeConstants;importstaticorg.mapstruct.

ap . internal.util.Collections.asSet;importstaticorg.mapstruct.
ap . internal.conversion.ConversionUtils.dateTimeFormat;importstaticorg.mapstruct.
ap . internal.conversion.ConversionUtils.locale;/**
 * Base class for conversions between Joda-Time types and String.
 *
 * @author Timo Eckhardt
 */publicabstractclassAbstractJodaTypeToStringConversionextends

SimpleConversion
{ @ Override protected String getToExpression (

    ConversionContextconversionContext
    ) { returnconversionString( conversionContext, "print"
        ) +".trim()" ;} @ Override protected Set<
    Type

    >getToConversionImportTypes
    ( ConversionContextconversionContext){ if(conversionContext .getDateFormat (
        ) != null){returnCollections . singleton ( conversionContext
            . getTypeFactory().
                getType(JodaTimeConstants.DATE_TIME_FORMAT_FQN
                    )); }else{ return
            asSet(
        conversionContext
        . getTypeFactory
            ( ).
                getType(JodaTimeConstants.DATE_TIME_FORMAT_FQN),conversionContext .getTypeFactory( ).
                getType(Locale.class)); }}@ Override
            protectedString
        getFromExpression
    (

    ConversionContextconversionContext
    ) { returnconversionString( conversionContext, parseMethod
        ( )) ;} @Overrideprotected Set<
    Type

    >getFromConversionImportTypes
    ( ConversionContextconversionContext){ if(conversionContext .getDateFormat (
        ) != null){returnCollections . singleton ( conversionContext
            . getTypeFactory().
                getType(JodaTimeConstants.DATE_TIME_FORMAT_FQN
                    )); }else{ return
            asSet(
        conversionContext
        . getTypeFactory
            ( ).
                getType(JodaTimeConstants.DATE_TIME_FORMAT_FQN),conversionContext .getTypeFactory( ).
                getType(Locale.class)); }}private String
            conversionString(
        ConversionContext
    conversionContext

    , String method){ StringBuilderconversionString = newStringBuilder (
        dateTimeFormat ( conversionContext ) ); conversionString. append ( dateFormatPattern(
        conversionContext)); conversionString. append ( ".")
        ;conversionString.append ( method)
        ;conversionString.append ( "( <SOURCE> )")
        ;returnconversionString. toString ()
        ; }privateStringdateFormatPattern(ConversionContext
    conversionContext

    ) { StringBuilderconversionString= newStringBuilder (
        ) ; conversionString . append(".forPattern(")
        ;StringdateFormat= conversionContext .getDateFormat

        ( ) ; if(dateFormat==null)
        { conversionString . append ( defaultDateFormatPattern (
            conversionContext)); }else { conversionString .append

        (
        " \"" )
            ;conversionString.append ( dateFormat)
            ;conversionString.append ( "\"")
            ;}conversionString. append (" )"

        )
        ;returnconversionString. toString ()
        ; }privateStringdefaultDateFormatPattern(ConversionContext
    conversionContext

    ) { return" "+ dateTimeFormat( conversionContext
        ) +
            ".patternForStyle( \"" +formatStyle ( )
            + "\", " + locale(conversionContext ) +
            ".getDefault() )" ;} /**
     * @return the default format style to be applied if non is given explicitly.
     */ protected
            abstract StringformatStyle
    (

    )
    ; /**
     * @return the name of the parse method for converting a String into a specific Joda-Time type.
     */ protected abstractStringparseMethod(

    )
    ; } 