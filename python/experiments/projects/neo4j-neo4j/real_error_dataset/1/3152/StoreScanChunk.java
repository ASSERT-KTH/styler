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
package org.neo4j.kernel.impl.storemigration;

import java.io.IOException;

import org.neo4j.internal.batchimport.input.InputChunk;
import org.neo4j.internal.batchimport.input.InputEntityVisitor;
import org.neo4j.internal.recordstorage.RecordStorageReader;
import org.neo4j.io.IOUtils;
import org.neo4j.io.pagecache.tracing.cursor.PageCursorTracer;
import org.neo4j.memory.MemoryTracker;
import org.neo4j.storageengine.api.StorageEntityCursor;
import org.neo4j.storageengine.api.StoragePropertyCursor;

abstract class StoreScanChunk<T extends StorageEntityCursor> implements InputChunk
{
    final StoragePropertyCursor storePropertyCursor;
    protected final T cursor;
    private final boolean requiresPropertyMigration;
    private final PageCursorTracer cursorTracer;
    private long id;
    private long endId;

    StoreScanChunk( T cursor, RecordStorageReader storageReader, boolean requiresPropertyMigration, PageCursorTracer cursorTracer, MemoryTracker memoryTracker )
    {
        this.cursor = cursor;
        this.requiresPropertyMigration = requiresPropertyMigration;
        this.storePropertyCursor = storageReader.allocatePropertyCursor( cursorTracer, memoryTracker );
        this.cursorTracer = cursorTracer;
    }

    void visitProperties( T record, InputEntityVisitor visitor )
    {
        if ( !requiresPropertyMigration )
        {
            visitor.propertyId( record.propertiesReference() );
        }
        else
        {
            record.properties( storePropertyCursor );
            while ( storePropertyCursor.next() )
            {
                // add key as int here as to have the importer use the token id
                visitor.property( storePropertyCursor.propertyKey(), storePropertyCursor.propertyValue().asObject() );
            }
            storePropertyCursor.close();
        }
    }

    @Override
    public void close()
    {
        IOUtils.closeAllUnchecked( storePropertyCursor, cursorTracer );
    }

    @Override
    public boolean next( InputEntityVisitor visitor ) throws IOException
    {
        if ( id < endId )
        {
            read( cursor, id );
            if ( cursor.next() )
            {
                visitRecord( cursor, visitor );
                visitor.endOfEntity();
            }
            id++;
            return true;
        }
        return false;
    }

    protected abstract void read( T cursor, long id );

    public void initialize( long startId, long endId )
    {
        this.id = startId;
        this.endId = endId;
    }

    abstract void visitRecord( T record, InputEntityVisitor visitor );
}
