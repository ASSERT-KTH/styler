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
package org.neo4j.consistency.checking.index;

import java.util.Iterator;

import org.neo4j.helpers.collection.BoundedIterable;
import org.neo4j.kernel.api.index.IndexAccessor;

public class IndexIterator implements BoundedIterable<
    Long > { privatefinal
    IndexAccessor indexAccessor;privateBoundedIterable <Long

    > indexReader; public IndexIterator (
    IndexAccessor
        indexAccessor){ this .indexAccessor
    =

    indexAccessor;
    } @ Overridepubliclong
    maxCount
        ( ) {try(BoundedIterable < Long >reader=indexAccessor. newAllEntriesReader
        (
            ) ){returnreader.maxCount
        (
        ) ; } catch (
        Exception
            e ) {throw new RuntimeException(
        e
    )

    ;}
    } @ Overridepublicvoid close (
    )
        throws Exception { if ( indexReader
        !=
            null){indexReader.close
        (
    )

    ;}
    } @OverridepublicIterator <Long>
    iterator
        ( ) { if ( indexReader
        ==
            null ) {indexReader=indexAccessor.newAllEntriesReader
        (

        ) ;}returnindexReader.iterator
    (
)
