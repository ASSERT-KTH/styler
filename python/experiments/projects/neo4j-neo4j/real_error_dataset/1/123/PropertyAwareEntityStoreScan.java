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
package org.neo4j.kernel.impl.transaction.state.storeview;

import org.apache.commons.lang3.ArrayUtils;

import java.util.function.IntPredicate;
import java.util.function.LongFunction;

import org.neo4j.internal.kernel.api.PopulationProgress;
import org.neo4j.io.pagecache.tracing.cursor.PageCursorTracer;
import org.neo4j.kernel.impl.api.index.MultipleIndexPopulator;
import org.neo4j.kernel.impl.api.index.PhaseTracker;
import org.neo4j.kernel.impl.api.index.StoreScan;
import org.neo4j.kernel.impl.index.schema.LabelScanStore;
import org.neo4j.kernel.impl.index.schema.RelationshipTypeScanStore;
import org.neo4j.lock.Lock;
import org.neo4j.memory.MemoryTracker;
import org.neo4j.storageengine.api.EntityUpdates;
import org.neo4j.storageengine.api.IndexEntryUpdate;
import org.neo4j.storageengine.api.StorageEntityScanCursor;
import org.neo4j.storageengine.api.StoragePropertyCursor;
import org.neo4j.storageengine.api.StorageReader;
import org.neo4j.values.storable.Value;

import static org.neo4j.io.IOUtils.closeAllUnchecked;

/**
 * Scan store with the view given by iterator created by {@link #getEntityIdIterator()}. This might be a full scan of the store
 * or a partial scan backed by {@link LabelScanStore} or {@link RelationshipTypeScanStore}.
 *
 * The {@link #entityCursor cursor} is placed on each record and then {@link #process(StorageEntityScanCursor) processed},
 * this is where we extract updates for indexes that we are populating.
 *
 * @param <CURSOR> the type of cursor used to read the records.
 * @param <FAILURE> on failure during processing.
 */
public abstract class PropertyAwareEntityStoreScan<CURSOR extends StorageEntityScanCursor, FAILURE extends Exception> implements StoreScan<FAILURE>
{
    final CURSOR entityCursor;
    private final StoragePropertyCursor propertyCursor;
    private final StorageReader storageReader;
    private volatile boolean continueScanning;
    private long count;
    private final long totalCount;
    private final IntPredicate propertyKeyIdFilter;
    private final LongFunction<Lock> lockFunction;
    private PhaseTracker phaseTracker;

    protected PropertyAwareEntityStoreScan( StorageReader storageReader, long totalEntityCount, IntPredicate propertyKeyIdFilter,
            LongFunction<Lock> lockFunction, PageCursorTracer cursorTracer, MemoryTracker memoryTracker )
    {
        this.storageReader = storageReader;
        this.entityCursor = allocateCursor( storageReader, cursorTracer );
        this.propertyCursor = storageReader.allocatePropertyCursor( cursorTracer, memoryTracker );
        this.propertyKeyIdFilter = propertyKeyIdFilter;
        this.lockFunction = lockFunction;
        this.totalCount = totalEntityCount;
        this.phaseTracker = PhaseTracker.nullInstance;
    }

    protected abstract CURSOR allocateCursor( StorageReader storageReader, PageCursorTracer cursorTracer );

    static boolean containsAnyEntityToken( int[] entityTokenFilter, long... entityTokens )
    {
        for ( long candidate : entityTokens )
        {
            if ( ArrayUtils.contains( entityTokenFilter, Math.toIntExact( candidate ) ) )
            {
                return true;
            }
        }
        return false;
    }

    boolean hasRelevantProperty( CURSOR cursor, EntityUpdates.Builder updates )
    {
        if ( !cursor.hasProperties() )
        {
            return false;
        }
        boolean hasRelevantProperty = false;
        cursor.properties( propertyCursor );
        while ( propertyCursor.next() )
        {
            int propertyKeyId = propertyCursor.propertyKey();
            if ( propertyKeyIdFilter.test( propertyKeyId ) )
            {
                // This relationship has a property of interest to us
                Value value = propertyCursor.propertyValue();
                // No need to validate values before passing them to the updater since the index implementation
                // is allowed to fail in which ever way it wants to. The result of failure will be the same as
                // a failed validation, i.e. population FAILED.
                updates.added( propertyKeyId, value );
                hasRelevantProperty = true;
            }
        }
        return hasRelevantProperty;
    }

    @Override
    public void run() throws FAILURE
    {
        entityCursor.scan();
        try ( EntityIdIterator entityIdIterator = getEntityIdIterator() )
        {
            continueScanning = true;
            while ( continueScanning && entityIdIterator.hasNext() )
            {
                phaseTracker.enterPhase( PhaseTracker.Phase.SCAN );
                long id = entityIdIterator.next();
                try ( Lock ignored = lockFunction.apply( id ) )
                {
                    count++;
                    if ( process( entityCursor ) )
                    {
                        entityIdIterator.invalidateCache();
                    }
                }
            }
        }
        finally
        {
            closeAllUnchecked( propertyCursor, entityCursor, storageReader );
        }
    }

    @Override
    public void acceptUpdate( MultipleIndexPopulator.MultipleIndexUpdater updater, IndexEntryUpdate<?> update,
            long currentlyIndexedNodeId )
    {
        if ( update.getEntityId() <= currentlyIndexedNodeId )
        {
            updater.process( update );
        }
    }

    /**
     * Process the given {@code record}.
     *
     * @param cursor CURSOR with information to process.
     * @return {@code true} if external updates have been applied such that the scan iterator needs to be 100% up to date with store,
     * i.e. invalidate any caches if it has any.
     * @throws FAILURE on failure.
     */
    protected abstract boolean process( CURSOR cursor ) throws FAILURE;

    @Override
    public void stop()
    {
        continueScanning = false;
    }

    @Override
    public PopulationProgress getProgress()
    {
        if ( totalCount > 0 )
        {
            return PopulationProgress.single( count, totalCount );
        }

        // nothing to do 100% completed
        return PopulationProgress.DONE;
    }

    @Override
    public void setPhaseTracker( PhaseTracker phaseTracker )
    {
        this.phaseTracker = phaseTracker;
    }

    protected EntityIdIterator getEntityIdIterator()
    {
        return new EntityIdIterator()
        {
            private boolean hasSeenNext;
            private boolean hasNext;

            @Override
            public void invalidateCache()
            {
                // Nothing to invalidate, we're reading directly from the store
            }

            @Override
            public long next()
            {
                if ( !hasNext() )
                {
                    throw new IllegalStateException();
                }
                hasSeenNext = false;
                hasNext = false;
                return entityCursor.entityReference();
            }

            @Override
            public boolean hasNext()
            {
                if ( !hasSeenNext )
                {
                     hasNext = entityCursor.next();
                     hasSeenNext = true;
                }
                return hasNext;
            }

            @Override
            public void close()
            {
                // Nothing to close
            }
        };
    }
}
