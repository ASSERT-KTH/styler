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
package org.neo4j.kernel.api.impl.schema.populator;

import org.neo4j.io.pagecache.tracing.cursor.PageCursorTracer;
import org.neo4j.kernel.api.impl.schema.LuceneDocumentStructure;
import org.neo4j.kernel.api.impl.schema.SchemaIndex;
import org.neo4j.kernel.api.index.IndexSample;
import org.neo4j.kernel.api.index.IndexUpdater;
import org.neo4j.kernel.api.index.NonUniqueIndexSampler;
import org.neo4j.kernel.impl.api.index.IndexSamplingConfig;
import org.neo4j.storageengine.api.IndexEntryUpdate;
import org.neo4j.storageengine.api.NodePropertyAccessor;
import org.neo4j.storageengine.api.ValueIndexEntryUpdate;

/**
 * A {@link LuceneIndexPopulator} used for non-unique Lucene schema indexes.
 * Performs sampling using {@link DefaultNonUniqueIndexSampler}.
 */
public class NonUniqueLuceneIndexPopulator extends LuceneIndexPopulator<SchemaIndex>
{
    private final IndexSamplingConfig samplingConfig;
    private final NonUniqueIndexSampler sampler;

    public NonUniqueLuceneIndexPopulator( SchemaIndex luceneIndex, IndexSamplingConfig samplingConfig )
    {
        super( luceneIndex );
        this.samplingConfig = samplingConfig;
        this.sampler = createDefaultSampler();
    }

    @Override
    public void verifyDeferredConstraints( NodePropertyAccessor accessor )
    {
        // no constraints to verify so do nothing
    }

    @Override
    public IndexUpdater newPopulatingUpdater( NodePropertyAccessor nodePropertyAccessor, PageCursorTracer cursorTracer )
    {
        return new NonUniqueLuceneIndexPopulatingUpdater( writer, sampler );
    }

    @Override
    public void includeSample( IndexEntryUpdate<?> update )
    {
        sampler.include( LuceneDocumentStructure.encodedStringValuesForSampling( ((ValueIndexEntryUpdate<?>) update).values() ) );
    }

    @Override
    public IndexSample sample( PageCursorTracer cursorTracer )
    {
        return sampler.sample( cursorTracer );
    }

    private DefaultNonUniqueIndexSampler createDefaultSampler()
    {
        return new DefaultNonUniqueIndexSampler( samplingConfig.sampleSizeLimit() );
    }
}
