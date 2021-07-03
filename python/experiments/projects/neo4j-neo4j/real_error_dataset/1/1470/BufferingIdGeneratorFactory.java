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
package org.neo4j.internal.id;

import org.eclipse.collections.api.set.ImmutableSet;

import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.neo4j.configuration.Config;
import org.neo4j.io.pagecache.PageCache;
import org.neo4j.io.pagecache.tracing.cursor.PageCursorTracer;

/**
 * Wraps {@link IdGenerator} so that ids can be freed using reuse marker at safe points in time, after all transactions
 * which were active at the time of freeing, have been closed.
 */
public class BufferingIdGeneratorFactory implements IdGeneratorFactory
{
    private final BufferingIdGenerator[/*IdType#ordinal as key*/] overriddenIdGenerators =
            new BufferingIdGenerator[IdType.values().length];
    private Supplier<IdController.ConditionSnapshot> boundaries;
    private final Predicate<IdController.ConditionSnapshot> safeThreshold;
    private final IdGeneratorFactory delegate;

    public BufferingIdGeneratorFactory( IdGeneratorFactory delegate )
    {
        this.delegate = delegate;
        this.safeThreshold = IdController.ConditionSnapshot::conditionMet;
    }

    public void initialize( Supplier<IdController.ConditionSnapshot> conditionSnapshotSupplier )
    {
        boundaries = conditionSnapshotSupplier;
    }

    @Override
    public IdGenerator open( PageCache pageCache, Path filename, IdType idType, LongSupplier highIdScanner, long maxId, boolean readOnly, Config config,
            PageCursorTracer cursorTracer, ImmutableSet<OpenOption> openOptions )
    {
        assert boundaries != null : "Factory needs to be initialized before usage";

        IdGenerator generator = delegate.open( pageCache, filename, idType, highIdScanner, maxId, readOnly, config, cursorTracer, openOptions );
        return wrapAndKeep( idType, generator );
    }

    @Override
    public IdGenerator create( PageCache pageCache, Path filename, IdType idType, long highId, boolean throwIfFileExists, long maxId, boolean readOnly,
            Config config, PageCursorTracer cursorTracer, ImmutableSet<OpenOption> openOptions )
    {
        IdGenerator idGenerator = delegate.create( pageCache, filename, idType, highId, throwIfFileExists, maxId, readOnly, config, cursorTracer, openOptions );
        return wrapAndKeep( idType, idGenerator );
    }

    @Override
    public IdGenerator get( IdType idType )
    {
        IdGenerator generator = overriddenIdGenerators[idType.ordinal()];
        return generator != null ? generator : delegate.get( idType );
    }

    @Override
    public void visit( Consumer<IdGenerator> visitor )
    {
        Stream.of( overriddenIdGenerators ).forEach( visitor );
    }

    @Override
    public void clearCache( PageCursorTracer cursorTracer )
    {
        delegate.clearCache( cursorTracer );
    }

    @Override
    public Collection<Path> listIdFiles()
    {
        return delegate.listIdFiles();
    }

    private IdGenerator wrapAndKeep( IdType idType, IdGenerator generator )
    {
        BufferingIdGenerator bufferingGenerator = new BufferingIdGenerator( generator );
        bufferingGenerator.initialize( boundaries, safeThreshold );
        overriddenIdGenerators[idType.ordinal()] = bufferingGenerator;
        return bufferingGenerator;
    }

    public void maintenance( boolean awaitOngoing, PageCursorTracer pageCursorTracer )
    {
        for ( BufferingIdGenerator generator : overriddenIdGenerators )
        {
            if ( generator != null )
            {
                generator.maintenance( awaitOngoing, pageCursorTracer );
            }
        }
    }

    public void clear()
    {
        for ( BufferingIdGenerator generator : overriddenIdGenerators )
        {
            if ( generator != null )
            {
                generator.clear();
            }
        }
    }
}
