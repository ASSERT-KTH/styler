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

import org.eclipse.collections.api.iterator.LongIterator;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;

import org.neo4j.collection.PrimitiveLongCollections;
import org.neo4j.collection.PrimitiveLongResourceIterator;
import org.neo4j.cursor.RawCursor;
import org .neo4j. index .internal.gbptree. Hit ; importorg
        . neo4j.values
        . storable
.
    Value ; /**
 * Wraps number key/value results in a {@link LongIterator}.
 *
 * @param <KEY> type of {@link NumberIndexKey}.
 * @param <VALUE> type of {@link NativeIndexValue}.
 */publicclassNativeHitIterator<KEYextendsNativeIndexKey<KEY> ,VALUE
    extends NativeIndexValue >extendsPrimitiveLongCollections.PrimitiveLongBaseIteratorimplementsPrimitiveLongResourceIterator{privatefinalRawCursor<Hit< KEY,
    VALUE > ,IOException

    >seeker ;privatefinalCollection<RawCursor<Hit<KEY, VALUE>
            ,IOException>>toRemoveFromWhenExhausted;privatebooleanclosed;NativeHitIterator(RawCursor< Hit <
    KEY
        ,VALUE> , IOException>
        seeker,Collection < RawCursor<
    Hit

    <KEY
    , VALUE >,IOException
    >
        >
        toRemoveFromWhenExhausted
            ) { this.seeker=seeker ;
            this
                . toRemoveFromWhenExhausted = toRemoveFromWhenExhausted;}@OverrideprotectedbooleanfetchNext()
                { try {while (seeker.next( ) )
                {
                    KEY key= seeker.get() .key
                (
            )
            ; if(
        acceptValues
        ( key . asValues (
        )
            ) ) {return next (key
        .
    getEntityId

    ( )) ;}} return false
    ;
        } catch(
    IOException

    e ) {thrownew UncheckedIOException (
    e
        ) ; }} boolean
        acceptValues
            (Value[]value)
            {returntrue; } privatevoid
            ensureCursorClosed ( )throws
        IOException
    {

    if(
    ! closed ){seeker
    .
        close
        (
            );toRemoveFromWhenExhausted.
        remove
        ( seeker ) ; closed
        =
            true ; }} @ Overridepublic
        void
    close
(
