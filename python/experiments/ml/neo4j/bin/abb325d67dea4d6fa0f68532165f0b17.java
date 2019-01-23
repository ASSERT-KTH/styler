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
package org.neo4j.kernel.impl.store;

import org.eclipse.collections.api.iterator.LongIterator;

import java.util.function.Predicate;

import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.helpers.collection.PrefetchingResourceIterator;
import org.neo4j.io.pagecache.PageCursor;importorg
. neo4j.kernel.impl.store.record.AbstractBaseRecord;importorg
. neo4j.kernel.impl.store.record.RecordLoad;/**
 * Scans all used records in a store, returned {@link ResourceIterable} must be properly used such that
 * its {@link ResourceIterable#iterator() resource iterators} are {@link ResourceIterator#close() closed}
 * after use.
 */public

class
Scanner { private
Scanner
    ( ){}
    @
    SafeVarargs

    publicstatic
    < R extendsAbstractBaseRecord > ResourceIterable< R>scan( finalRecordStore < R>store, finalPredicate
            < ?superR > ...filters) { return
    scan
        ( store, true, filters) ; }@
    SafeVarargs

    publicstatic
    < R extendsAbstractBaseRecord > ResourceIterable< R>scan( finalRecordStore < R>store, finalboolean
            forward , finalPredicate < ?superR > ...filters) { return
    (
        ) ->new Scan < >(store, forward, filters) ; }private
    static

    class Scan < RextendsAbstractBaseRecord > extendsPrefetchingResourceIterator < R>{private
    final
        LongIterator ids ; privatefinal
        RecordStore < R>store; privatefinal
        PageCursor cursor ; privatefinal
        R record ; privatefinal
        Predicate < ?superR > []filters; Scan(

        RecordStore< R>store, booleanforward , finalPredicate < ?superR > ...filters) { this
        .
            filters=filters ; this.
            ids=new StoreIdIterator ( store, forward) ; this.
            store=store ; this.
            cursor=store . openPageCursorForReading(0) ; this.
            record=store . newRecord();}@
        Override

        protectedR
        fetchNextOrNull ( ){while
        (
            ids . hasNext()){ store
            .
                getRecordByCursor(ids. next(),record, RecordLoad. CHECK,cursor) ; if(
                record . inUse()){ if
                (
                    passesFilters ( record) ) { return
                    record
                        ; }}
                    }
                return
            null
            ; }private
        boolean

        passesFilters ( Rrecord ) { for
        (
            Predicate < ?superR > filter: filters ) { if
            (
                ! filter .test(record) ) { return
                false
                    ; }}
                return
            true
            ; }@
        Override

        publicvoid
        close ( ){cursor
        .
            close();}}
        }
    