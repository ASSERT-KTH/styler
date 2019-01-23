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
package org.neo4j.values.storable;

import java.util.Arrays;

import org.neo4j.values.AnyValue;
import org.neo4j.values.ValueMapper;

import static java.lang.String.format;

public class IntArray extends IntegralArray
{
    private final int[] value;

    IntArray( int[] value )
    {
        assert value != null;
        this.value = value;
    }

    @Override
    public int length()
    {
        return value.length;
    }

    @Override
    public long longValue( int index )
    {
        return value[index
        ] ; }@Override
        public
            int computeHash(){ return NumberValues.
        hash

        (value
        ) ;}@ Override public< T>Tmap ( ValueMapper
        <
            T >mapper){ return mapper.
        mapIntArray

        (this
        ) ; }@ Override public boolean
        equals
            ( Valueother){ return other.
        equals

        (value
        ) ; }@ Overridepublicboolean equals (
        byte
            [ ]x){ returnPrimitiveArrayValues . equals(
        x

        ,value
        ) ; }@ Overridepublicboolean equals (
        short
            [ ]x){ returnPrimitiveArrayValues . equals(
        x

        ,value
        ) ; }@ Overridepublicboolean equals (
        int
            [ ]x){ returnArrays . equals(
        value

        ,x
        ) ; }@ Overridepublicboolean equals (
        long
            [ ]x){ returnPrimitiveArrayValues . equals(
        value

        ,x
        ) ; }@ Overridepublicboolean equals (
        float
            [ ]x){ returnPrimitiveArrayValues . equals(
        value

        ,x
        ) ; }@ Overridepublicboolean equals (
        double
            [ ]x){ returnPrimitiveArrayValues . equals(
        value

        ,x
        ) ;} @ Overridepublic < Eextends Exception>voidwriteTo ( ValueWriter < E
        >
            writer)throwsE {PrimitiveArrayWriting . writeTo(
        writer

        ,value
        ) ;}@ Overridepublicint
        [
            ] asObjectCopy(){returnvalue
        .

        clone(
        );
        } @Override@ Deprecatedpublicint
        [
            ] asObject(
        )

        {return
        value ; }@Override
        public
            String prettyPrint(){ return Arrays.
        toString

        (value
        ) ; }@ Override public AnyValue
        value
            ( intoffset){ returnValues.intValue (value
        [

        offset]
        ) ; }@Override
        public
            String toString( ){ returnformat("IntArray%s" , Arrays .toString
        (

        value)
        ) ; }@Override
        public
            String getTypeName(
        )
    {
    