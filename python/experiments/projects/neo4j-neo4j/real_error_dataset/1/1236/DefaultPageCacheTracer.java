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
package org.neo4j.io.pagecache.tracing;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

import org.neo4j.internal.helpers.MathUtil;
import org.neo4j.io.pagecache.PageSwapper;
import org.neo4j.io.pagecache.tracing.cursor.DefaultPageCursorTracer;
import org.neo4j.io.pagecache.tracing.cursor.PageCursorTracer;

/**
 * The default PageCacheTracer implementation, that just increments counters.
 */
public class DefaultPageCacheTracer implements PageCacheTracer
{
    protected final LongAdder faults = new LongAdder();
    protected final LongAdder evictions = new LongAdder();
    protected final LongAdder pins = new LongAdder();
    protected final LongAdder unpins = new LongAdder();
    protected final LongAdder hits = new LongAdder();
    protected final LongAdder flushes = new LongAdder();
    protected final LongAdder merges = new LongAdder();
    protected final LongAdder bytesRead = new LongAdder();
    protected final LongAdder bytesWritten = new LongAdder();
    protected final LongAdder filesMapped = new LongAdder();
    protected final LongAdder filesUnmapped = new LongAdder();
    protected final LongAdder evictionExceptions = new LongAdder();
    protected final AtomicLong maxPages = new AtomicLong();

    private final FlushEvent flushEvent = new FlushEvent()
    {
        @Override
        public void addBytesWritten( long bytes )
        {
            bytesWritten.add( bytes );
        }

        @Override
        public void done()
        {
        }

        @Override
        public void done( IOException exception )
        {
            done();
        }

        @Override
        public void addPagesFlushed( int pageCount )
        {
            flushes.add( pageCount );
        }

        @Override
        public void addPagesMerged( int pagesMerged )
        {
            merges.add( pagesMerged );
        }
    };

    private final FlushEventOpportunity flushEventOpportunity = new FlushEventOpportunity()
    {
        @Override
        public FlushEvent beginFlush( long filePageId, long cachePageId, PageSwapper swapper, int pagesToFlush, int mergedPages )
        {
            return flushEvent;
        }

        @Override
        public void startFlush( int[][] translationTable )
        {

        }

        @Override
        public ChunkEvent startChunk( int[] chunk )
        {
            return ChunkEvent.NULL;
        }
    };

    private final EvictionEvent evictionEvent = new EvictionEvent()
    {
        @Override
        public void setFilePageId( long filePageId )
        {
        }

        @Override
        public void setSwapper( PageSwapper swapper )
        {
        }

        @Override
        public FlushEventOpportunity flushEventOpportunity()
        {
            return flushEventOpportunity;
        }

        @Override
        public void threwException( IOException exception )
        {
            evictionExceptions.increment();
        }

        @Override
        public void setCachePageId( long cachePageId )
        {
        }

        @Override
        public void close()
        {
            evictions.increment();
        }
    };

    private final EvictionRunEvent evictionRunEvent = new EvictionRunEvent()
    {
        @Override
        public EvictionEvent beginEviction()
        {
            return evictionEvent;
        }

        @Override
        public void close()
        {
        }
    };

    private final MajorFlushEvent majorFlushEvent = new MajorFlushEvent()
    {
        @Override
        public FlushEventOpportunity flushEventOpportunity()
        {
            return flushEventOpportunity;
        }

        @Override
        public void close()
        {
        }
    };

    @Override
    public PageCursorTracer createPageCursorTracer( String tag )
    {
        return new DefaultPageCursorTracer( this, tag );
    }

    @Override
    public void mappedFile( Path path )
    {
        filesMapped.increment();
    }

    @Override
    public void unmappedFile( Path path )
    {
        filesUnmapped.increment();
    }

    @Override
    public EvictionRunEvent beginPageEvictions( int pageCountToEvict )
    {
        return evictionRunEvent;
    }

    @Override
    public MajorFlushEvent beginFileFlush( PageSwapper swapper )
    {
        return majorFlushEvent;
    }

    @Override
    public MajorFlushEvent beginCacheFlush()
    {
        return majorFlushEvent;
    }

    @Override
    public long faults()
    {
        return faults.sum();
    }

    @Override
    public long evictions()
    {
        return evictions.sum();
    }

    @Override
    public long pins()
    {
        return pins.sum();
    }

    @Override
    public long unpins()
    {
        return unpins.sum();
    }

    @Override
    public long hits()
    {
        return hits.sum();
    }

    @Override
    public long flushes()
    {
        return flushes.sum();
    }

    @Override
    public long merges()
    {
        return merges.sum();
    }

    @Override
    public long bytesRead()
    {
        return bytesRead.sum();
    }

    @Override
    public long bytesWritten()
    {
        return bytesWritten.sum();
    }

    @Override
    public long filesMapped()
    {
        return filesMapped.sum();
    }

    @Override
    public long filesUnmapped()
    {
        return filesUnmapped.sum();
    }

    @Override
    public long evictionExceptions()
    {
        return evictionExceptions.sum();
    }

    @Override
    public double hitRatio()
    {
        return MathUtil.portion( hits(), faults() );
    }

    @Override
    public double usageRatio()
    {
        long pages = maxPages.get();
        if ( pages == 0 )
        {
            return 0;
        }
        return Math.max( 0, (faults.sum() - evictions.sum()) / (double) pages );
    }

    @Override
    public void pins( long pins )
    {
        this.pins.add( pins );
    }

    @Override
    public void unpins( long unpins )
    {
        this.unpins.add( unpins );
    }

    @Override
    public void hits( long hits )
    {
        this.hits.add( hits );
    }

    @Override
    public void faults( long faults )
    {
        this.faults.add( faults );
    }

    @Override
    public void bytesRead( long bytesRead )
    {
        this.bytesRead.add( bytesRead );
    }

    @Override
    public void evictions( long evictions )
    {
        this.evictions.add( evictions );
    }

    @Override
    public void evictionExceptions( long evictionExceptions )
    {
        this.evictionExceptions.add( evictionExceptions );
    }

    @Override
    public void bytesWritten( long bytesWritten )
    {
        this.bytesWritten.add( bytesWritten );
    }

    @Override
    public void flushes( long flushes )
    {
        this.flushes.add( flushes );
    }

    @Override
    public void merges( long merges )
    {
        this.merges.add( merges );
    }

    @Override
    public void maxPages( long maxPages )
    {
        this.maxPages.set( maxPages );
    }
}
