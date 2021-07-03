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
package org.neo4j.kernel.impl.index.schema;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.neo4j.index.internal.gbptree.GBPTree;
import org.neo4j.index.internal.gbptree.RecoveryCleanupWorkCollector;
import org.neo4j.index.internal.gbptree.Writer;
import org.neo4j.internal.schema.IndexDescriptor;
import org.neo4j.io.pagecache.IOLimiter;
import org.neo4j.io.pagecache.tracing.cursor.PageCursorTracer;
import org.neo4j.kernel.api.exceptions.index.IndexEntryConflictException;
import org.neo4j.kernel.api.index.IndexPopulator;
import org.neo4j.kernel.api.index.IndexSample;
import org.neo4j.kernel.api.index.IndexUpdater;
import org.neo4j.kernel.api.index.UniqueIndexSampler;
import org.neo4j.storageengine.api.IndexEntryUpdate;
import org.neo4j.storageengine.api.NodePropertyAccessor;
import org.neo4j.storageengine.api.ValueIndexEntryUpdate;
import org.neo4j.util.Preconditions;
import org.neo4j.values.storable.Value;

import static org.neo4j.index.internal.gbptree.GBPTree.NO_HEADER_WRITER;

/**
 * {@link IndexPopulator} backed by a {@link GBPTree}.
 *
 * @param <KEY> type of {@link NativeIndexKey}.
 * @param <VALUE> type of {@link NativeIndexValue}.
 */
public abstract class NativeIndexPopulator<KEY extends NativeIndexKey<KEY>, VALUE extends NativeIndexValue>
        extends NativeIndex<KEY,VALUE> implements IndexPopulator, ConsistencyCheckable
{
    public static final byte BYTE_FAILED = 0;
    static final byte BYTE_ONLINE = 1;
    static final byte BYTE_POPULATING = 2;

    private final KEY treeKey;
    private final VALUE treeValue;
    private final UniqueIndexSampler uniqueSampler;

    private ConflictDetectingValueMerger<KEY,VALUE,Value[]> mainConflictDetector;
    private ConflictDetectingValueMerger<KEY,VALUE,Value[]> updatesConflictDetector;

    private byte[] failureBytes;
    private boolean dropped;
    private boolean closed;

    NativeIndexPopulator( DatabaseIndexContext databaseIndexContext, IndexFiles indexFiles, IndexLayout<KEY,VALUE> layout,
            IndexDescriptor descriptor )
    {
        super( databaseIndexContext, layout, indexFiles, descriptor );
        this.treeKey = layout.newKey();
        this.treeValue = layout.newValue();
        this.uniqueSampler = descriptor.isUnique() ? new UniqueIndexSampler() : null;
    }

    abstract NativeIndexReader<KEY,VALUE> newReader();

    @Override
    public synchronized void create()
    {
        assertNotDropped();
        assertNotClosed();

        indexFiles.clear();
        NativeIndexHeaderWriter headerWriter = new NativeIndexHeaderWriter( BYTE_POPULATING );
        instantiateTree( RecoveryCleanupWorkCollector.immediate(), headerWriter );

        // true:  tree uniqueness is (value,entityId)
        // false: tree uniqueness is (value) <-- i.e. more strict
        mainConflictDetector = new ThrowingConflictDetector<>( !descriptor.isUnique() );
        // for updates we have to have uniqueness on (value,entityId) to allow for intermediary violating updates.
        // there are added conflict checks after updates have been applied.
        updatesConflictDetector = new ThrowingConflictDetector<>( true );
    }

    @Override
    public synchronized void drop()
    {
        try
        {
            if ( tree != null )
            {
                tree.setDeleteOnClose( true );
            }
            closeTree();
            indexFiles.clear();
        }
        finally
        {
            dropped = true;
            closed = true;
        }
    }

    @Override
    public void add( Collection<? extends IndexEntryUpdate<?>> updates, PageCursorTracer cursorTracer ) throws IndexEntryConflictException
    {
        processUpdates( updates, mainConflictDetector, cursorTracer );
    }

    @Override
    public void verifyDeferredConstraints( NodePropertyAccessor nodePropertyAccessor )
    {
        // No-op, uniqueness is checked for each update in add(IndexEntryUpdate)
    }

    @Override
    public IndexUpdater newPopulatingUpdater( NodePropertyAccessor accessor, PageCursorTracer cursorTracer )
    {
        return newPopulatingUpdater( cursorTracer );
    }

    IndexUpdater newPopulatingUpdater( PageCursorTracer cursorTracer )
    {
        IndexUpdater updater = new CollectingIndexUpdater( updates -> processUpdates( updates, updatesConflictDetector, cursorTracer ) );
        if ( descriptor.isUnique() )
        {
            // The index population detects conflicts on the fly, however for updates coming in we're in a position
            // where we cannot detect conflicts while applying, but instead afterwards.
            updater = new DeferredConflictCheckingIndexUpdater( updater, this::newReader, descriptor, cursorTracer );
        }
        return updater;
    }

    @Override
    public synchronized void close( boolean populationCompletedSuccessfully, PageCursorTracer cursorTracer )
    {
        if ( populationCompletedSuccessfully && failureBytes != null )
        {
            throw new IllegalStateException( "Can't mark index as online after it has been marked as failure" );
        }

        try
        {
            assertNotDropped();
            if ( populationCompletedSuccessfully )
            {
                // Successful and completed population
                assertPopulatorOpen();
                flushTreeAndMarkAs( BYTE_ONLINE, cursorTracer );
            }
            else if ( failureBytes != null )
            {
                // Failed population
                ensureTreeInstantiated();
                markTreeAsFailed( cursorTracer );
            }
            // else cancelled population. Here we simply close the tree w/o checkpointing it and it will look like POPULATING state on next open
        }
        finally
        {
            closeTree();
            closed = true;
        }
    }

    @Override
    public void markAsFailed( String failure )
    {
        failureBytes = failure.getBytes( StandardCharsets.UTF_8 );
    }

    @Override
    public void includeSample( IndexEntryUpdate<?> update )
    {
        if ( descriptor.isUnique() )
        {
            updateUniqueSample( update );
        }
        // else don't do anything here, we'll do a scan in the end instead
    }

    @Override
    public IndexSample sample( PageCursorTracer cursorTracer )
    {
        if ( descriptor.isUnique() )
        {
            return uniqueSampler.result();
        }
        return buildNonUniqueIndexSample( cursorTracer );
    }

    void flushTreeAndMarkAs( byte state, PageCursorTracer cursorTracer )
    {
        tree.checkpoint( IOLimiter.UNLIMITED, new NativeIndexHeaderWriter( state ), cursorTracer );
    }

    IndexSample buildNonUniqueIndexSample( PageCursorTracer cursorTracer )
    {
        return new FullScanNonUniqueIndexSampler<>( tree, layout ).sample( cursorTracer );
    }

    private void markTreeAsFailed( PageCursorTracer cursorTracer )
    {
        Preconditions.checkState( failureBytes != null, "markAsFailed hasn't been called, populator not actually failed?" );
        tree.checkpoint( IOLimiter.UNLIMITED, new FailureHeaderWriter( failureBytes ), cursorTracer );
    }

    private void processUpdates( Iterable<? extends IndexEntryUpdate<?>> indexEntryUpdates, ConflictDetectingValueMerger<KEY,VALUE,Value[]> conflictDetector,
            PageCursorTracer cursorTracer ) throws IndexEntryConflictException
    {
        try ( Writer<KEY,VALUE> writer = tree.writer( cursorTracer ) )
        {
            for ( IndexEntryUpdate<?> indexEntryUpdate : indexEntryUpdates )
            {
                NativeIndexUpdater.processUpdate( treeKey, treeValue, (ValueIndexEntryUpdate<?>) indexEntryUpdate, writer, conflictDetector );
            }
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private void updateUniqueSample( IndexEntryUpdate<?> update )
    {
        switch ( update.updateMode() )
        {
        case ADDED:
            uniqueSampler.increment( 1 );
            break;
        case REMOVED:
            uniqueSampler.increment( -1 );
            break;
        case CHANGED:
            break;
        default:
            throw new IllegalArgumentException( "Unsupported update mode type:" + update.updateMode() );
        }
    }

    private void assertNotDropped()
    {
        if ( dropped )
        {
            throw new IllegalStateException( "Populator has already been dropped." );
        }
    }

    private void assertNotClosed()
    {
        if ( closed )
        {
            throw new IllegalStateException( "Populator has already been closed." );
        }
    }

    private void ensureTreeInstantiated()
    {
        if ( tree == null )
        {
            instantiateTree( RecoveryCleanupWorkCollector.ignore(), NO_HEADER_WRITER );
        }
    }

    private void assertPopulatorOpen()
    {
        if ( tree == null )
        {
            throw new IllegalStateException( "Populator has already been closed." );
        }
    }
}
