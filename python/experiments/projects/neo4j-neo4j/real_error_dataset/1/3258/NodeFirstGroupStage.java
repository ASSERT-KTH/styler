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
package org.neo4j.internal.batchimport;

import org.neo4j.internal.batchimport.cache.ByteArray;
import org.neo4j.internal.batchimport.staging.BatchFeedStep;
import org.neo4j.internal.batchimport.staging.ReadRecordsStep;
import org.neo4j.internal.batchimport.staging.Stage;
import org.neo4j.internal.batchimport.store.StorePrepareIdSequence;
import org.neo4j.io.pagecache.tracing.PageCacheTracer;
import org.neo4j.kernel.impl.store.NodeStore;
import org.neo4j.kernel.impl.store.RecordStore;
import org.neo4j.kernel.impl.store.record.NodeRecord;
import org.neo4j.kernel.impl.store.record.RelationshipGroupRecord;

import static org.neo4j.internal.batchimport.RecordIdIterator.allIn;

/**
 * Updates dense nodes with which will be the {@link NodeRecord#setNextRel(long) first group} to point to,
 * after a {@link RelationshipGroupDefragmenter} has been run.
 */
public class NodeFirstGroupStage extends Stage
{
    public static final String NAME = "Node --> Group";

    NodeFirstGroupStage( Configuration config, RecordStore<RelationshipGroupRecord> groupStore, NodeStore nodeStore, ByteArray cache,
            PageCacheTracer pageCacheTracer )
    {
        super( NAME, null, config, 0 );
        add( new BatchFeedStep( control(), config, allIn( groupStore, config ), groupStore.getRecordSize() ) );
        add( new ReadRecordsStep<>( control(), config, true, groupStore, pageCacheTracer ) );
        add( new NodeSetFirstGroupStep( control(), config, nodeStore, cache, pageCacheTracer ) );
        add( new UpdateRecordsStep<>( control(), config, nodeStore, new StorePrepareIdSequence(), pageCacheTracer ) );
    }
}
