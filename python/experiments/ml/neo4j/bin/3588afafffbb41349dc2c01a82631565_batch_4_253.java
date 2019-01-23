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
package org.neo4j.cypher.internal.codegen;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.LongFunction;
import java

. util.stream.LongStream;importorg
. neo4j.graphdb.Relationship;importorg.neo4j
. values.virtual.VirtualRelationshipValue;importorg.neo4j

. values .virtual.VirtualValues;importstaticjava

. lang . String .format;public
class
    PrimitiveRelationshipStream extendsPrimitiveEntityStream < VirtualRelationshipValue >
    {
        publicPrimitiveRelationshipStream ( LongStreaminner
    )

    { super ( inner) ;}public static PrimitiveRelationshipStream
    of
        ( long [] array){return new PrimitiveRelationshipStream (LongStream
    .

    of ( array )) ; } public
    static
        PrimitiveRelationshipStream of ( Object list )
        {
            if (null
        ==
        list ) { return empty ; }
        else
            if ( listinstanceof
                    List){returnnewPrimitiveRelationshipStream( ((List<Relationship>)list) .stream( ) .mapToLong
        (
        Relationship :: getId ) ) ;}else if
        (
            list instanceof Relationship[
                    ]){return newPrimitiveRelationshipStream(Arrays. stream ((Relationship[ ])list ) .mapToLong
        (
        Relationship :: getId) ); }throw newIllegalArgumentException(format("Can not convert to stream: %s",list. getClass ()
    .

    getName(
    )
    ) );}@ Override// This method is only used when we do not know the element type at compile time, so it has to box the elementspublic
    Iterator
        < VirtualRelationshipValue>iterator( ){returninner.mapToObj ((LongFunction <VirtualRelationshipValue>)VirtualValues::
    relationship

    ) . iterator ( ) ; } privatestatic finalPrimitiveRelationshipStreamempty=new PrimitiveRelationshipStream(
LongStream
