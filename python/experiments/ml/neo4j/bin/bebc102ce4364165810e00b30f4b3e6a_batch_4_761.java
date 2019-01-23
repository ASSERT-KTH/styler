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
package org.neo4j.values.storable;importorg.neo4j.values.ValueMapper

; import staticjava.lang.String.format

; public final class DoubleValue extends
FloatingPointValue
    { private final doublevalue

    ;DoubleValue ( double value
    )
        {this. value =value
    ;

    } public doublevalue(
    )
        { returnvalue
    ;

    }@
    Override public doubledoubleValue(
    )
        { returnvalue
    ;

    }@
    Override public< E extendsException > voidwriteTo (ValueWriter<E > writer ) throws
    E
        {writer.writeFloatingPoint ( value)
    ;

    }@
    Override public DoubleasObjectCopy(
    )
        { returnvalue
    ;

    }@
    Override public StringprettyPrint(
    )
        { returnDouble.toString ( value)
    ;

    }@
    Override public StringtoString(
    )
        { returnformat ("%s(%e)" ,getTypeName() , value)
    ;

    }@
    Override public<T > Tmap (ValueMapper<T > mapper
    )
        { returnmapper.mapDouble ( this)
    ;

    }@
    Override public StringgetTypeName(
    )
        { return"Double"
    ;
}
