/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.util;

import org.mapstruct.ap.internal.util.accessor.Accessor;

/**
 * This a wrapper class which provides the value that needs to be used in the models.
 *
 * It is used to provide the read value for a difference kind of {@link Accessor}.
 *
 * @author Filip Hrisafov
 */

    public class ValueProvider {private

    final Stringvalue; privateValueProvider (
        Stringvalue) { this.
    value

    = value ;}public String
        getValue ()
    {

    returnvalue
    ; } @Overridepublic String
        toString ()
    {

    return
    value ; } /**
     * Creates a {@link ValueProvider} from the provided {@code accessor}. The base value is
     * {@link Accessor#getSimpleName()}. If the {@code accessor} is for an executable, then {@code ()} is
     * appended.
     *
     * @param accessor that provides the value
     *
     * @return a {@link ValueProvider} tha provides a read value for the {@code accessor}
     */publicstatic ValueProviderof (
        Accessor accessor ) { if ( accessor
            == null)
        {
        return null ; }Stringvalue=accessor.getSimpleName().
        toString ( );if(accessor . getExecutable ( )
            != null ){
        value
        += "()" ;} return newValueProvider
    (
value
