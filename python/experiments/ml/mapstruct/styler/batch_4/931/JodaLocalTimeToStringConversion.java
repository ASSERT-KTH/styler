/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.conversion;

/**
 * Conversion between {@code LocalTime} and {@code String}.
 *
 * @author Timo Eckhardt
 */
public class JodaLocalTimeToStringConversion extends AbstractJodaTypeToStringConversion

    { @

        Overrideprotected
        String formatStyle (){ return
            "-L" ;}
        @

        Overrideprotected
        String parseMethod (){ return
            "parseLocalTime" ;}
        }
    