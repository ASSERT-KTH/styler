/*
 * Copyright (c) 2002-2020 "Neo4j,"
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
package org.neo4j.internal.recordstorage;

import org.eclipse.collections.api.list.primitive.LongList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.impl.factory.primitive.LongLists;

import java.util.Arrays;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import org.neo4j.collection.trackable.HeapTrackingCollections;
import org.neo4j.collection.trackable.HeapTrackingLongObjectHashMap;
import org.neo4j.io.pagecache.tracing.cursor.PageCursorTracer;
import org.neo4j.kernel.impl.store.record.RelationshipRecord;
import org.neo4j.lock.LockTracer;
import org.neo4j.lock.ResourceLocker;
import org.neo4j.memory.MemoryTracker;
import org.neo4j.storageengine.api.txstate.RelationshipModifications;

import static org.neo4j.internal.recordstorage.RelationshipConnection.END_NEXT;
import static org.neo4j.internal.recordstorage.RelationshipConnection.END_PREV;
import static org.neo4j.internal.recordstorage.RelationshipConnection.START_NEXT;
import static org.neo4j.internal.recordstorage.RelationshipConnection.START_PREV;
import static org.neo4j.kernel.impl.store.record.Record.NULL_REFERENCE;
import static org.neo4j.kernel.impl.store.record.Record.isNull;
import static org.neo4j.kernel.impl.store.record.RecordLoad.ALWAYS;
import static org.neo4j.lock.LockTracer.NONE;
import static org.neo4j.lock.ResourceTypes.RELATIONSHIP;

class RelationshipLockHelper
{
    static void lockRelationshipsInOrder( RelationshipModifications.RelationshipBatch idsToLock, long optionalFirstInChain,
            RecordAccess<RelationshipRecord,Void> relRecords, ResourceLocker locks, PageCursorTracer cursorTracer, MemoryTracker memoryTracker )
    {
        int size = idsToLock.size();
        if ( size == 1 )
        {
            lockSingleRelationship( idsToLock.first(), optionalFirstInChain, relRecords, locks, cursorTracer );
        }
        else if ( size > 1 )
        {
            lockMultipleRelationships( idsToLock, optionalFirstInChain, relRecords, locks, cursorTracer, memoryTracker );
        }
    }
    static RecordAccess.RecordProxy<RelationshipRecord,Void> findAndLockEntrypoint( long firstInChain, long nodeId,
            RecordAccess<RelationshipRecord,Void> relRecords, ResourceLocker locks, LockTracer lockTracer, PageCursorTracer cursorTracer )
    {
        long nextRel = firstInChain;
        RecordAccess.RecordProxy<RelationshipRecord,Void> rBefore = null;

        // Start walking the relationship chain and see where we can insert it
        if ( !isNull( nextRel ) )
        {
            while ( !isNull( nextRel ) )
            {
                boolean r1Locked = locks.tryExclusiveLock( RELATIONSHIP, nextRel );
                RecordAccess.RecordProxy<RelationshipRecord,Void> r1 = relRecords.getOrLoad( nextRel, null, ALWAYS, cursorTracer );
                RelationshipRecord r1Record = r1.forReadingLinkage();
                if ( !r1Locked || !r1Record.inUse() )
                {
                    nextRel = r1Record.getNextRel( nodeId );
                    if ( r1Locked )
                    {
                        locks.releaseExclusive( RELATIONSHIP, r1.getKey() );
                    }
                    continue;
                }

                long r2Id = r1Record.getNextRel( nodeId );
                if ( !isNull( r2Id ) )
                {
                    boolean r2Locked = locks.tryExclusiveLock( RELATIONSHIP, r2Id );
                    RecordAccess.RecordProxy<RelationshipRecord,Void> r2 = relRecords.getOrLoad( r2Id, null, ALWAYS, cursorTracer );
                    RelationshipRecord r2Record = r2.forReadingLinkage();
                    if ( !r2Locked || !r2Record.inUse() )
                    {
                        nextRel = r2Record.getNextRel( nodeId );
                        locks.releaseExclusive( RELATIONSHIP, r1.getKey() );
                        if ( r2Locked )
                        {
                            locks.releaseExclusive( RELATIONSHIP, r2.getKey() );
                        }
                        continue;
                    }
                    // We can insert in between r1 and r2 here
                }
                // We can insert at the end here
                rBefore = r1;
                break;
            }

            if ( rBefore == null )
            {
                //Group is minimum read locked, so no need to re-read
                locks.acquireExclusive( lockTracer, RELATIONSHIP, firstInChain );
                RecordAccess.RecordProxy<RelationshipRecord,Void> firstProxy = relRecords.getOrLoad( firstInChain, null, ALWAYS, cursorTracer );
                long secondRel = firstProxy.forReadingLinkage().getNextRel( nodeId );
                if ( !isNull( secondRel ) )
                {
                    locks.acquireExclusive( lockTracer, RELATIONSHIP, secondRel );
                }
                rBefore = firstProxy;
            }
        }
        return rBefore;
    }

    private static void lockMultipleRelationships( RelationshipModifications.RelationshipBatch ids, long firstInChain,
            RecordAccess<RelationshipRecord,Void> relRecords, ResourceLocker locks, PageCursorTracer cursorTracer, MemoryTracker memoryTracker )
    {
        /*
         * The idea here is to take all locks in sorted order to avoid deadlocks
         * We start locking by optimistic reading of relationship neighbours
         * Once all neighbours plus the relationship itself is locked, we verify if there are changes.
         *      If not -> then just continue
         *      Changes means we need to rewind and unlock, to get the new changes in correct order
         */

        int upperLimitOfLocks = ids.size() * 5 + 1;
        try ( HeapTrackingLongObjectHashMap<RelationshipRecord> optimistic = HeapTrackingCollections.newLongObjectMap( memoryTracker ) )
        {
            memoryTracker.allocateHeap( upperLimitOfLocks );

            SortedSeekableList relIds = new SortedSeekableList( upperLimitOfLocks );
            ids.forEach( ( id, type, startNode, endNode ) ->
            {
                RelationshipRecord relationship = relRecords.getOrLoad( id, null, cursorTracer ).forReadingLinkage();
                optimistic.put( id, relationship );
                addAllValid( relIds::add, relationship );
            } );
            if ( !isNull( firstInChain ) )
            {
                relIds.add( firstInChain );
            }
            while ( relIds.nextUnique() )
            {
                long id = relIds.get();

                locks.acquireExclusive( NONE, RELATIONSHIP, id );
                RelationshipRecord old = optimistic.get( id );
                if ( old != null )
                {
                    RelationshipRecord actual = relRecords.getOrLoad( old.getId(), null, cursorTracer ).forReadingLinkage();
                    if ( recordHasLinkageChanges( old, actual ) )
                    {
                        rewindAndUnlockChanged( locks, relIds, old, actual );
                        optimistic.put( id, actual );
                    }
                }
            }
        }
        finally
        {
            memoryTracker.releaseHeap( upperLimitOfLocks );
        }
    }

    private static void rewindAndUnlockChanged( ResourceLocker locks, SortedSeekableList iterator, RelationshipRecord old, RelationshipRecord actual )
    {
        rewindAndUnlockChanged( locks, START_NEXT, iterator, old, actual );
        rewindAndUnlockChanged( locks, START_PREV, iterator, old, actual );
        rewindAndUnlockChanged( locks, END_NEXT, iterator, old, actual );
        rewindAndUnlockChanged( locks, END_PREV, iterator, old, actual );
    }

    private static void rewindAndUnlockChanged( ResourceLocker locks, RelationshipConnection connection, SortedSeekableList iterator, RelationshipRecord old,
            RelationshipRecord actual )
    {
        long actualId = connection.otherSide().get( actual );
        long oldId = connection.otherSide().get( old );
        if ( oldId != actualId )
        {
            //We have a change
            if ( !isNull( oldId ) )
            {
                //We need to unlock the old value, if it was locked and no other relationship depends on it!
                long curr = iterator.validPosition() ? iterator.get() : NULL_REFERENCE.longValue();
                boolean independent = iterator.remove( oldId );
                if ( oldId <= curr )
                {
                    //it is locked
                    if ( independent )
                    {
                        locks.releaseExclusive( RELATIONSHIP, oldId );
                    }

                }
            }
            if ( !isNull( actualId ) )
            {
                if ( iterator.add( actualId ) && iterator.validPosition() )
                {
                    //we only need to do something if it did not already exist
                    long curr = iterator.get();
                    if ( actualId < curr )
                    {
                        //if the new id is lower than what is already locked, and it is not immediately lockable, we'll rewind
                        if ( !locks.tryExclusiveLock( RELATIONSHIP, actualId ) )
                        {
                            do
                            {
                                curr = iterator.get();
                                locks.releaseExclusive( RELATIONSHIP, curr );
                            }
                            while ( iterator.prevUnique() && iterator.get() > actualId );
                            iterator.prevUnique(); //step past it to lock it on the next round
                        }
                    }
                    else if ( actualId == curr )
                    {
                        //we inserted it where we stand, if it is not independent it is already locked, otherwise lock it!
                        locks.acquireExclusive( NONE, RELATIONSHIP, actualId );
                    }
                }
            }
        }
    }

    private static void addAllValid( Consumer<Long> ids, RelationshipRecord relationship )
    {
        ids.accept( relationship.getId() );
        addIfValid( START_NEXT, ids, relationship  );
        addIfValid( START_PREV, ids, relationship  );
        addIfValid( END_NEXT, ids, relationship  );
        addIfValid( END_PREV, ids, relationship  );
    }

    private static void addIfValid( RelationshipConnection connection, Consumer<Long> ids, RelationshipRecord relationship )
    {
        long id = connection.otherSide().get( relationship );
        if ( !isNull( id ) )
        {
            ids.accept( id );
        }
    }

    private static void lockSingleRelationship( long relId, long firstInChain, RecordAccess<RelationshipRecord,Void> relRecords, ResourceLocker locks,
            PageCursorTracer cursorTracer )
    {
        boolean retry;
        RelationshipRecord optimistic = relRecords.getOrLoad( relId, null, cursorTracer ).forReadingLinkage();
        assert optimistic.inUse() : optimistic.toString();
        final long[] neighbours = new long[6];
        do
        {
            retry = false;
            neighbours[0] = firstInChain;
            neighbours[1] = relId;
            neighbours[2] = START_NEXT.otherSide().get( optimistic );
            neighbours[3] = START_PREV.otherSide().get( optimistic );
            neighbours[4] = END_NEXT.otherSide().get( optimistic );
            neighbours[5] = END_PREV.otherSide().get( optimistic );
            Arrays.sort( neighbours );
            lockRelationshipsExclusively( locks, neighbours );

            RelationshipRecord actual = relRecords.getOrLoad( relId, null, cursorTracer ).forReadingLinkage();
            assert actual.inUse();
            if ( recordHasLinkageChanges( optimistic, actual ) )
            {
                retry = true;
                unlockRelationshipsExclusively( locks, neighbours );
                optimistic = actual;
            }
        }
        while ( retry );
    }

    private static boolean recordHasLinkageChanges( RelationshipRecord old, RelationshipRecord actual )
    {
        return connectionNeedsRelock( START_NEXT, old, actual ) || connectionNeedsRelock( START_PREV, old, actual) ||
                connectionNeedsRelock( END_NEXT, old, actual ) || connectionNeedsRelock( END_PREV, old, actual);
    }

    private static boolean connectionNeedsRelock( RelationshipConnection connection, RelationshipRecord old, RelationshipRecord actual )
    {
        return connection.otherSide().get( old ) != connection.otherSide().get( actual );
    }

    private static void lockRelationshipsExclusively( ResourceLocker locker, long[] ids )
    {
        for ( long id : ids )
        {
            if ( !isNull( id ) )
            {
                locker.acquireExclusive( NONE, RELATIONSHIP, id );
            }
        }
    }

    private static void unlockRelationshipsExclusively( ResourceLocker locker, long[] ids )
    {
        for ( long id : ids )
        {
            if ( !isNull( id ) )
            {
                locker.releaseExclusive( RELATIONSHIP, id );
            }
        }
    }

    /**
     * An internal class used to keep a list sorted, while seeking in it
     */
    static class SortedSeekableList
    {
        private final MutableLongList list;
        private int index = -1;

        SortedSeekableList( int initialCapacity )
        {
            this.list = LongLists.mutable.withInitialCapacity( initialCapacity );
        }

        /**
         * @return true if list did not already contain l
         */
        boolean add( long l )
        {
            if ( l != NULL_REFERENCE.longValue() )
            {
                int insertIndex = list.binarySearch( l );
                boolean existed = insertIndex >= 0;
                if ( insertIndex < 0 )
                {
                    insertIndex = -insertIndex - 1;
                }
                if ( insertIndex <= index )
                {
                    index++;
                }
                list.addAtIndex( insertIndex, l );
                return !existed;
            }
            return true;
        }

        /**
         * @param l value to remove
         * @return true if list no longer contains l
         */
        boolean remove( long l )
        {
            if ( !isNull( l ) )
            {
                int removeIndex = list.binarySearch( l );
                assert removeIndex >= 0 : l + " did not exist";
                list.removeAtIndex( removeIndex );
                boolean unique = (list.size() <= removeIndex || list.get( removeIndex ) != l) && (removeIndex <= 0 || list.get( removeIndex - 1 ) != l);
                if ( removeIndex < index )
                {
                    index--;
                }
                else if ( removeIndex == index )
                {
                    if ( unique || (index == list.size() || l != list.get( index )) )
                    {
                        index--;
                    }
                }
                index = Math.min( index, list.size() - 1 );

                return unique;
            }
            return true;
        }

        long get()
        {
            return list.get( index );
        }

        boolean next()
        {
            if ( list.size() > 0 && index < list.size() )
            {
                index++;
                return index < list.size();
            }
            return false;
        }

        boolean nextUnique()
        {
            return unique( this::next );
        }

        boolean prev()
        {
            if ( index >= 0 )
            {
                index--;
                return index >= 0;
            }
            return false;
        }

        boolean prevUnique()
        {
            return unique( this::prev );
        }

        private boolean unique( BooleanSupplier traverser )
        {
            if ( !validPosition() )
            {
                return traverser.getAsBoolean();
            }
            long old = get();
            while ( traverser.getAsBoolean() )
            {
                if ( get() != old )
                {
                    return true;
                }
            }
            return false;
        }

        boolean validPosition()
        {
            return index >= 0 && index < list.size();
        }

        LongList underlyingList()
        {
            return list;
        }
    }
}
