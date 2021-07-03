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
package org.neo4j.kernel.api.impl.fulltext;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

import org.neo4j.internal.helpers.collection.BoundedIterable;
import org.neo4j.internal.schema.IndexDescriptor;
import org.neo4j.io.pagecache.tracing.cursor.PageCursorTracer;
import org.neo4j.kernel.api.impl.index.AbstractLuceneIndexAccessor;
import org.neo4j.kernel.api.impl.index.DatabaseIndex;
import org.neo4j.kernel.api.index.IndexUpdater;
import org.neo4j.kernel.impl.api.index.IndexUpdateMode;
import org.neo4j.storageengine.api.NodePropertyAccessor;
import org.neo4j.values.storable.Value;

import static org.neo4j.kernel.api.impl.fulltext.FulltextIndexSettings.isEventuallyConsistent;
import static org.neo4j.kernel.api.impl.fulltext.LuceneFulltextDocumentStructure.documentRepresentingProperties;
import static org.neo4j.kernel.api.impl.fulltext.LuceneFulltextDocumentStructure.newTermForChangeOrRemove;

public class FulltextIndexAccessor extends AbstractLuceneIndexAccessor<FulltextIndexReader,DatabaseIndex<FulltextIndexReader>>
{
    private final IndexUpdateSink indexUpdateSink;
    private final IndexDescriptor index;
    private final String[] propertyNames;

    FulltextIndexAccessor( IndexUpdateSink indexUpdateSink, DatabaseIndex<FulltextIndexReader> luceneIndex, IndexDescriptor index,
            String[] propertyNames )
    {
        super( luceneIndex, index );
        this.indexUpdateSink = indexUpdateSink;
        this.index = index;
        this.propertyNames = propertyNames;
    }

    @Override
    public IndexUpdater getIndexUpdater( IndexUpdateMode mode )
    {
        IndexUpdater indexUpdater = new FulltextIndexUpdater( mode.requiresIdempotency(), mode.requiresRefresh() );
        if ( isEventuallyConsistent( index ) )
        {
            indexUpdater = new EventuallyConsistentIndexUpdater( luceneIndex, indexUpdater, indexUpdateSink );
        }
        return indexUpdater;
    }

    @Override
    public void close()
    {
        if ( isEventuallyConsistent( index ) )
        {
            indexUpdateSink.awaitUpdateApplication();
        }
        super.close();
    }

    @Override
    public BoundedIterable<Long> newAllEntriesReader( long fromIdInclusive, long toIdExclusive, PageCursorTracer cursorTracer )
    {
        return super.newAllEntriesReader( LuceneFulltextDocumentStructure::getNodeId, fromIdInclusive, toIdExclusive );
    }

    @Override
    public void verifyDeferredConstraints( NodePropertyAccessor propertyAccessor )
    {
        //The fulltext index does not care about constraints.
    }

    @Override
    public Map<String,Value> indexConfig()
    {
        return index.getIndexConfig().asMap();
    }

    public class FulltextIndexUpdater extends AbstractLuceneIndexUpdater
    {
        private FulltextIndexUpdater( boolean idempotent, boolean refresh )
        {
            super( idempotent, refresh );
        }

        @Override
        protected void addIdempotent( long entityId, Value[] values )
        {
            try
            {
                Document document = documentRepresentingProperties( entityId, propertyNames, values );
                writer.updateOrDeleteDocument( newTermForChangeOrRemove( entityId ), document );
            }
            catch ( IOException e )
            {
                throw new UncheckedIOException( e );
            }
        }

        @Override
        public void add( long entityId, Value[] values )
        {
            try
            {
                Document document = documentRepresentingProperties( entityId, propertyNames, values );
                writer.nullableAddDocument( document );
            }
            catch ( IOException e )
            {
                throw new UncheckedIOException( e );
            }
        }

        @Override
        protected void change( long entityId, Value[] values )
        {
            try
            {
                Term term = newTermForChangeOrRemove( entityId );
                Document document = documentRepresentingProperties( entityId, propertyNames, values );
                // If the property types have changed away from TEXT we may no longer
                // have any properties that should be indexed and the old document should be removed.
                writer.updateOrDeleteDocument( term, document );
            }
            catch ( IOException e )
            {
                throw new UncheckedIOException( e );
            }
        }

        @Override
        protected void remove( long entityId )
        {
            try
            {
                Term term = newTermForChangeOrRemove( entityId );
                writer.deleteDocuments( term );
            }
            catch ( IOException e )
            {
                throw new UncheckedIOException( e );
            }
        }
    }
}
