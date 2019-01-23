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
package org.neo4j.kernel.impl.index.schema;

import java.util.StringJoiner;

import org.neo4j.io.pagecache.PageCursor;
import org.neo4j.values.storable.NumberValue;
import org.neo4j.values.storable.Value;
import org.neo4j.values.storable.ValueGroup;
import org.neo4j.values.storable

. Values ;importstaticorg.neo4j.kernel.impl.index.schema.GenericKey
. SIZE_NUMBER_BYTE ;importstaticorg.neo4j.kernel.impl.index.schema.GenericKey
. SIZE_NUMBER_DOUBLE ;importstaticorg.neo4j.kernel.impl.index.schema.GenericKey
. SIZE_NUMBER_FLOAT ;importstaticorg.neo4j.kernel.impl.index.schema.GenericKey
. SIZE_NUMBER_INT ;importstaticorg.neo4j.kernel.impl.index.schema.GenericKey
. SIZE_NUMBER_LONG ;importstaticorg.neo4j.kernel.impl.index.schema.GenericKey
. SIZE_NUMBER_SHORT ;importstaticorg.neo4j.kernel.impl.index.schema.GenericKey

. SIZE_NUMBER_TYPE ; class
NumberType
    extends
    Type
    {

    // Affected key state:// long0 (value) // long1 (number type) NumberType (
    byte
        typeId) {super(ValueGroup .NUMBER ,typeId,Values .of( Double. NEGATIVE_INFINITY),Values .of( Double .POSITIVE_INFINITY
    )

    );
    } @Override int valueSize (
    GenericKey
        state ){ returnnumberKeySize( state . long1)
    +

    SIZE_NUMBER_TYPE;
    } @Override void copyValue( GenericKey to ,
    GenericKey
        from){ to .long0=from
        .long0; to .long1=from
    .

    long1;
    } @Override Value asValue (
    GenericKey
        state ){ returnasValue(state .long0, state.
    long1

    );
    } @Override int compareValue( GenericKey left ,
    GenericKey
        right ){
                returncompare(left .long0,left
                .long1,right .long0, right.
    long1

    );
    } @Override void putValue( PageCursor cursor ,
    GenericKey
        state){cursor .putByte( (byte) state.
        long1 ) ;switch( (int) state
        .
        long1 ){caseRawBits
            .BYTE:cursor .putByte( (byte) state.
            long0)
        ; break;caseRawBits
            .SHORT:cursor .putShort( (short) state.
            long0)
        ; break;caseRawBits
        . INT:caseRawBits
            .FLOAT:cursor .putInt( (int) state.
            long0)
        ; break;caseRawBits
        . LONG:caseRawBits
            .DOUBLE:cursor .putLong( state.
            long0)
        ;break
            ; default :throw new IllegalArgumentException ("Unknown number type "+ state.
        long1
    )

    ;}
    } @Override boolean readValue( PageCursor cursor, int size ,
    GenericKey
        into){ into .long1=cursor.getByte
        ( ) ;switch( (int) into
        .
        long1 ){caseRawBits
            .BYTE: into .long0=cursor.getByte
            ( );
        return true;caseRawBits
            .SHORT: into .long0=cursor.getShort
            ( );
        return true;caseRawBits
        . INT:caseRawBits
            .FLOAT: into .long0=cursor.getInt
            ( );
        return true;caseRawBits
        . LONG:caseRawBits
            .DOUBLE: into .long0=cursor.getLong
            ( );
        returntrue
            ; default:
        return
    false

    ; } }static int numberKeySize (
    long
        long1 ) {switch( ( int
        )
        long1 ){caseRawBits
            . BYTE:
        return SIZE_NUMBER_BYTE;caseRawBits
            . SHORT:
        return SIZE_NUMBER_SHORT;caseRawBits
            . INT:
        return SIZE_NUMBER_INT;caseRawBits
            . LONG:
        return SIZE_NUMBER_LONG;caseRawBits
            . FLOAT:
        return SIZE_NUMBER_FLOAT;caseRawBits
            . DOUBLE:
        returnSIZE_NUMBER_DOUBLE
            ; default :throw new IllegalArgumentException ( "Unknown number type "+
        long1
    )

    ; } }static NumberValue asValue( long long0 ,
    long
        long1 ){returnRawBits .asNumberValue (long0, ( byte)
    long1

    ) ; }static
            int compare( long this_long0,
            long this_long1, long that_long0 ,
    long
        that_long1 ){returnRawBits .compare (this_long0, (byte )this_long1 ,that_long0, ( byte)
    that_long1

    ) ;} void write( GenericKey state, long value ,
    byte
        numberType){ state .long0
        =value; state .long1
    =

    numberType;
    } @ Overrideprotected void addTypeSpecificDetails( StringJoiner joiner ,
    GenericKey
        state){joiner . add ("long0="+ state.
        long0);joiner . add ("long1="+ state.
    long1
)
