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
package org.neo4j.batchinsert.internal;

import java.util.Iterator;

import org.neo4j.graphdb.NotFoundException;
import org.neo4j.internal.helpers.collection.PrefetchingIterator;
import org.neo4j.internal.recordstorage.RecordNodeCursor;
import org.neo4j.internal.recordstorage.RecordStorageReader;
import org.neo4j.io.pagecache.tracing.cursor.PageCursorTracer;
import org.neo4j.storageengine.api.StorageRelationshipTraversalCursor;

import static org.neo4j.storageengine.api.RelationshipSelection.ALL_RELATIONSHIPS;

abstract class BatchRelationshipIterable<T> implements Iterable<T>
{
    private final StorageRelationshipTraversalCursor relationshipCursor;

    BatchRelationshipIterable( RecordStorageReader storageReader, long nodeId, PageCursorTracer cursorTracer )
    {
        relationshipCursor = storageReader.allocateRelationshipTraversalCursor( cursorTracer );
        RecordNodeCursor nodeCursor = storageReader.allocateNodeCursor( cursorTracer );
        nodeCursor.single( nodeId );
        if ( !nodeCursor.next() )
        {
            throw new NotFoundException( "Node " + nodeId + " not found" );
        }
        relationshipCursor.init( nodeId, nodeCursor.relationshipsReference(), ALL_RELATIONSHIPS );
    }

    @Override
    public Iterator<T> iterator()
    {
        return new PrefetchingIterator<>()
        {
            @Override
            protected T fetchNextOrNull()
            {
                if ( !relationshipCursor.next() )
                {
                    return null;
                }

                return nextFrom( relationshipCursor.entityReference(), relationshipCursor.type(),
                                 relationshipCursor.sourceNodeReference(), relationshipCursor.targetNodeReference() );
            }
        };
    }

    protected abstract T nextFrom( long relId, int type, long startNode, long endNode );
}
