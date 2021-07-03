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
import java.util.Arrays;
import java.util.Comparator;

import org.neo4j.index.internal.gbptree.GBPTree;
import org.neo4j.index.internal.gbptree.ValueMerger;
import org.neo4j.index.internal.gbptree.Writer;
import org.neo4j.io.pagecache.tracing.cursor.PageCursorTracer;
import org.neo4j.storageengine.api.EntityTokenUpdate;

import static java.lang.Long.min;
import static java.lang.Math.toIntExact;
import static org.neo4j.kernel.impl.index.schema.TokenScanValue.RANGE_SIZE;

/**
 * Note that this class will be replaced by NativeTokenIndexUpdater when scan stores are made into indexes,
 * and changes to functionality should be made there as well.
 *
 * {@link TokenScanWriter} for {@link NativeTokenScanStore}, or rather a {@link Writer} for its
 * internal {@link GBPTree}.
 * <p>
 * {@link #write(EntityTokenUpdate) updates} are queued up to a maximum batch size and, for performance,
 * applied in sorted order (by the token and entity id) when reaches batch size or on {@link #close()}.
 * <p>
 * Updates aren't visible to {@link TokenScanReader readers} immediately, rather when queue happens to be applied.
 * <p>
 * Incoming {@link EntityTokenUpdate updates} are actually modified from representing physical before/after
 * state to represent logical to-add/to-remove state. These changes are done directly inside the provided
 * {@link EntityTokenUpdate#getTokensAfter()} and {@link EntityTokenUpdate#getTokensBefore()} arrays,
 * relying on the fact that those arrays are returned in its essential form, instead of copies.
 * This conversion is done like so mostly to reduce garbage.
 *
 * @see PhysicalToLogicalTokenChanges
 */
class NativeTokenScanWriter implements TokenScanWriter
{
    /**
     * {@link Comparator} for sorting the entity id ranges, used in batches to apply updates in sorted order.
     */
    private static final Comparator<EntityTokenUpdate> UPDATE_SORTER =
            Comparator.comparingLong( EntityTokenUpdate::getEntityId );

    /**
     * {@link ValueMerger} used for adding token->entity mappings, see {@link TokenScanValue#add(TokenScanValue)}.
     */
    private final ValueMerger<TokenScanKey,TokenScanValue> addMerger;

    /**
     * {@link ValueMerger} used for removing token->entity mappings, see {@link TokenScanValue#remove(TokenScanValue)}.
     */
    private final ValueMerger<TokenScanKey,TokenScanValue> removeMerger;

    private final WriteMonitor monitor;

    /**
     * {@link Writer} acquired when acquiring this {@link NativeTokenScanWriter},
     * acquired from {@link GBPTree#writer(PageCursorTracer)}.
     */
    private Writer<TokenScanKey,TokenScanValue> writer;

    /**
     * Instance of {@link TokenScanKey} acting as place to read keys into and to set for each applied update.
     */
    private final TokenScanKey key = new TokenScanKey();

    /**
     * Instance of {@link TokenScanValue} acting as place to read values into and to update
     * for each applied update.
     */
    private final TokenScanValue value = new TokenScanValue();

    /**
     * Batch currently building up as {@link #write(EntityTokenUpdate) updates} come in. Cursor for where
     * to place new updates is {@link #pendingUpdatesCursor}. The constructor set the length of this queue
     * and the length defines the maximum batch size.
     */
    private final EntityTokenUpdate[] pendingUpdates;

    /**
     * Cursor into {@link #pendingUpdates}, where to place new {@link #write(EntityTokenUpdate) updates}.
     * When full the batch is applied and this cursor reset to {@code 0}.
     */
    private int pendingUpdatesCursor;

    /**
     * There are two levels of batching, one for {@link EntityTokenUpdate updates} and one when applying.
     * This variable helps keeping track of the second level where updates to the actual {@link GBPTree}
     * are batched per entity id range, i.e. to add several tokenId->entityId mappings falling into the same
     * range, all of those updates are made into one {@link TokenScanValue} and then issues as one update
     * to the tree. There are additions and removals, this variable keeps track of which.
     */
    private boolean addition;

    /**
     * When applying {@link EntityTokenUpdate updates} (when batch full or in {@link #close()}), updates are
     * applied tokenId by tokenId. All updates are scanned through multiple times, with one token in mind at a time.
     * For each round the current round tries to figure out which is the closest higher tokenId to apply
     * in the next round. This variable keeps track of that next tokenId.
     */
    private long lowestTokenId;

    interface WriteMonitor
    {
        default void range( long range, int tokenId )
        {
        }

        default void prepareAdd( long txId, int offset )
        {
        }

        default void prepareRemove( long txId, int offset )
        {
        }

        default void mergeAdd( TokenScanValue existingValue, TokenScanValue newValue )
        {
        }

        default void mergeRemove( TokenScanValue existingValue, TokenScanValue newValue )
        {
        }

        default void flushPendingUpdates()
        {
        }

        default void writeSessionEnded()
        {
        }

        default void force()
        {
        }

        default void close()
        {
        }
    }

    static final WriteMonitor EMPTY = new WriteMonitor()
    {
    };

    NativeTokenScanWriter( int batchSize, WriteMonitor monitor )
    {
        this.pendingUpdates = new EntityTokenUpdate[batchSize];
        this.addMerger = new AddMerger( monitor );
        this.removeMerger = ( existingKey, newKey, existingValue, newValue ) ->
        {
            monitor.mergeRemove( existingValue, newValue );
            existingValue.remove( newValue );
            return existingValue.isEmpty()
                   ? ValueMerger.MergeResult.REMOVED
                   : ValueMerger.MergeResult.MERGED;
        };
        this.monitor = monitor;
    }

    NativeTokenScanWriter initialize( Writer<TokenScanKey,TokenScanValue> writer )
    {
        this.writer = writer;
        this.pendingUpdatesCursor = 0;
        this.addition = false;
        this.lowestTokenId = Long.MAX_VALUE;
        return this;
    }

    /**
     * Queues a {@link EntityTokenUpdate} to this writer for applying when batch gets full,
     * or when {@link #close() closing}.
     */
    @Override
    public void write( EntityTokenUpdate update ) throws IOException
    {
        if ( pendingUpdatesCursor == pendingUpdates.length )
        {
            flushPendingChanges();
        }

        pendingUpdates[pendingUpdatesCursor++] = update;
        PhysicalToLogicalTokenChanges.convertToAdditionsAndRemovals( update );
        checkNextTokenId( update.getTokensBefore() );
        checkNextTokenId( update.getTokensAfter() );
    }

    private void checkNextTokenId( long[] tokens )
    {
        if ( tokens.length > 0 && tokens[0] != -1 )
        {
            lowestTokenId = min( lowestTokenId, tokens[0] );
        }
    }

    private void flushPendingChanges()
    {
        Arrays.sort( pendingUpdates, 0, pendingUpdatesCursor, UPDATE_SORTER );
        monitor.flushPendingUpdates();
        long currentTokenId = lowestTokenId;
        value.clear();
        key.clear();
        while ( currentTokenId != Long.MAX_VALUE )
        {
            long nextTokenId = Long.MAX_VALUE;
            for ( int i = 0; i < pendingUpdatesCursor; i++ )
            {
                EntityTokenUpdate update = pendingUpdates[i];
                long entityId = update.getEntityId();
                nextTokenId = extractChange( update.getTokensAfter(), currentTokenId, entityId, nextTokenId, true, update.getTxId() );
                nextTokenId = extractChange( update.getTokensBefore(), currentTokenId, entityId, nextTokenId, false, update.getTxId() );
            }
            currentTokenId = nextTokenId;
        }
        flushPendingRange();
        pendingUpdatesCursor = 0;
    }

    private long extractChange( long[] tokens, long currentTokenId, long entityId, long nextTokenId, boolean addition, long txId )
    {
        long foundNextTokenId = nextTokenId;
        for ( int li = 0; li < tokens.length; li++ )
        {
            long tokenId = tokens[li];
            if ( tokenId == -1 )
            {
                break;
            }

            // Have this check here so that we can pick up the next tokenId in our change set
            if ( tokenId == currentTokenId )
            {
                change( currentTokenId, entityId, addition, txId );

                // We can do a little shorter check for next tokenId here straight away,
                // we just check the next if it's less than what we currently think is next tokenId
                // and then break right after
                if ( li + 1 < tokens.length && tokens[li + 1] != -1 )
                {
                    long nextTokenCandidate = tokens[li + 1];
                    if ( nextTokenCandidate < currentTokenId )
                    {
                        throw new IllegalArgumentException(
                                "The entity token contained unsorted tokens ids " + Arrays.toString( tokens ) );
                    }
                    if ( nextTokenCandidate > currentTokenId )
                    {
                        foundNextTokenId = min( foundNextTokenId, nextTokenCandidate );
                    }
                }
                break;
            }
            else if ( tokenId > currentTokenId )
            {
                foundNextTokenId = min( foundNextTokenId, tokenId );
            }
        }
        return foundNextTokenId;
    }

    private void change( long currentTokenId, long entityId, boolean add, long txId )
    {
        int tokenId = toIntExact( currentTokenId );
        long idRange = rangeOf( entityId );
        if ( tokenId != key.tokenId || idRange != key.idRange || addition != add )
        {
            flushPendingRange();

            // Set key to current and reset value
            key.tokenId = tokenId;
            key.idRange = idRange;
            addition = add;
            monitor.range( idRange, tokenId );
        }

        int offset = toIntExact( entityId % RANGE_SIZE );
        value.set( offset );
        if ( addition )
        {
            monitor.prepareAdd( txId, offset );
        }
        else
        {
            monitor.prepareRemove( txId, offset );
        }
    }

    private void flushPendingRange()
    {
        if ( value.bits != 0 )
        {
            // There are changes in the current range, flush them
            if ( addition )
            {
                writer.merge( key, value, addMerger );
            }
            else
            {
                writer.mergeIfExists( key, value, removeMerger );
            }
            value.clear();
        }
    }

    static long rangeOf( long entityId )
    {
        return entityId / RANGE_SIZE;
    }

    static int offsetOf( long entityId )
    {
        return (int) (entityId % RANGE_SIZE);
    }

    /**
     * Applies {@link #write(EntityTokenUpdate) queued updates} which has not yet been applied.
     * No more {@link #write(EntityTokenUpdate) updates} can be applied after this call.
     */
    @Override
    public void close() throws IOException
    {
        try
        {
            flushPendingChanges();
            monitor.writeSessionEnded();
        }
        finally
        {
            writer.close();
        }
    }
}
