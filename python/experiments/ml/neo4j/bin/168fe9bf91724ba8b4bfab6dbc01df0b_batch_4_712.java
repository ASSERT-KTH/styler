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
import org.

neo4j . values.storable.Values;importstaticorg.neo4j.kernel.impl.
index . schema.GenericKey.SIZE_NUMBER_BYTE;importstaticorg.neo4j.kernel.impl.
index . schema.GenericKey.SIZE_NUMBER_DOUBLE;importstaticorg.neo4j.kernel.impl.
index . schema.GenericKey.SIZE_NUMBER_FLOAT;importstaticorg.neo4j.kernel.impl.
index . schema.GenericKey.SIZE_NUMBER_INT;importstaticorg.neo4j.kernel.impl.
index . schema.GenericKey.SIZE_NUMBER_LONG;importstaticorg.neo4j.kernel.impl.
index . schema.GenericKey.SIZE_NUMBER_SHORT;importstaticorg.neo4j.kernel.impl.

index . schema .
GenericKey
    .
    SIZE_NUMBER_TYPE
    ;

    classNumberType extends Type {
    // Affected key state:
        // long0 (value)// long1 (number type) NumberType(bytetypeId ){ super(ValueGroup. NUMBER,typeId ,Values .of(Double .NEGATIVE_INFINITY) , Values.
    of

    (Double
    . POSITIVE_INFINITY) ) ; }
    @
        Override intvalueSize (GenericKeystate ) { returnnumberKeySize
    (

    state.
    long1 )+ SIZE_NUMBER_TYPE ;} @ Override void
    copyValue
        (GenericKeyto , GenericKeyfrom){
        to.long0 = from.long0;
    to

    .long1
    = from. long1 ; }
    @
        Override ValueasValue (GenericKeystate) {returnasValue (state
    .

    long0,
    state .long1 ) ;} @ Override int
    compareValue
        ( GenericKeyleft
                ,GenericKeyright) {returncompare(
                left.long0, left.long1 ,right
    .

    long0,
    right .long1 ) ;} @ Override void
    putValue
        (PageCursorcursor, GenericKeystate) {cursor. putByte(
        ( byte )state. long1); switch
        (
        ( int)state.
            long1){case RawBits.BYTE :cursor. putByte(
            (byte
        ) state.long0)
            ;break;case RawBits.SHORT :cursor. putShort(
            (short
        ) state.long0)
        ; break;caseRawBits
            .INT:case RawBits.FLOAT :cursor. putInt(
            (int
        ) state.long0)
        ; break;caseRawBits
            .LONG:case RawBits.DOUBLE :cursor
            .putLong
        (state
            . long0 ); break ; default:throw newIllegalArgumentException
        (
    "Unknown number type "

    +state
    . long1) ; }} @ Overrideboolean readValue ( PageCursor
    cursor
        ,intsize , GenericKeyinto){into.
        long1 = cursor.getByte (); switch
        (
        ( int)into.
            long1){ case RawBits.BYTE:into.
            long0 =cursor
        . getByte();
            returntrue; case RawBits.SHORT:into.
            long0 =cursor
        . getShort();
        return true;caseRawBits
            .INT: case RawBits.FLOAT:into.
            long0 =cursor
        . getInt();
        return true;caseRawBits
            .LONG: case RawBits.DOUBLE:into.
            long0 =cursor
        .getLong
            ( );
        return
    true

    ; default :return false ; }
    }
        static int numberKeySize(long long1 )
        {
        switch ((int)
            long1 ){
        case RawBits.BYTE:
            return SIZE_NUMBER_BYTE;
        case RawBits.SHORT:
            return SIZE_NUMBER_SHORT;
        case RawBits.INT:
            return SIZE_NUMBER_INT;
        case RawBits.LONG:
            return SIZE_NUMBER_LONG;
        case RawBits.FLOAT:
            return SIZE_NUMBER_FLOAT;
        caseRawBits
            . DOUBLE :return SIZE_NUMBER_DOUBLE ; default :throw
        new
    IllegalArgumentException

    ( "Unknown number type " +long1 ) ;} } static NumberValue
    asValue
        ( longlong0,long long1) {returnRawBits . asNumberValue(
    long0

    , ( byte)
            long1 ); } staticint
            compare (long this_long0 , long
    this_long1
        , longthat_long0,long that_long1) {returnRawBits .compare (this_long0 ,(byte ) this_long1,
    that_long0

    , (byte ) that_long1) ; }void write ( GenericKey
    state
        ,longvalue , bytenumberType
        ){state . long0=
    value

    ;state
    . long1 =numberType ; }@ Override protected void
    addTypeSpecificDetails
        (StringJoinerjoiner, GenericKey state ){joiner .add
        ("long0="+state . long0 );joiner .add
    (
"long1="
