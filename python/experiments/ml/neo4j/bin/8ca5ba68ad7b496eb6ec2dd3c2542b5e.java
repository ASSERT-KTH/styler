/*
 * Copyright (c) 2002-2018 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.values.

storable ;importorg.neo4j.values.

ValueMapper ; importstaticjava.lang.String.

format ; public final class DoubleValue
extends
    FloatingPointValue { private finaldouble

    value; DoubleValue ( double
    value
        ){this . value=
    value

    ; } publicdoublevalue
    (
        ) {return
    value

    ;}
    @ Override publicdoubledoubleValue
    (
        ) {return
    value

    ;}
    @ Overridepublic < Eextends Exception >void writeTo(ValueWriter< E > writer )
    throws
        E{writer. writeFloatingPoint (value
    )

    ;}
    @ Override publicDoubleasObjectCopy
    (
        ) {return
    value

    ;}
    @ Override publicStringprettyPrint
    (
        ) {returnDouble. toString (value
    )

    ;}
    @ Override publicStringtoString
    (
        ) {return format( "%s(%e)",getTypeName( ) ,value
    )

    ;}
    @ Overridepublic< T >T map(ValueMapper< T >
    mapper
        ) {returnmapper. mapDouble (this
    )

    ;}
    @ Override publicStringgetTypeName
    (
        ) {return
    "Double"
;
