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
package org.neo4j.kernel.database;

import java.util.Optional;
import java.util.UUID;

import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.configuration.helpers.NormalizedDatabaseName;

/**
 * Encapsulates the retrieval of a persistent {@link NamedDatabaseId} for a database of a given name.
 */
public interface DatabaseIdRepository
{
    NamedDatabaseId NAMED_SYSTEM_DATABASE_ID = new NamedDatabaseId( GraphDatabaseSettings.SYSTEM_DATABASE_NAME, new UUID( 0L, 1L ) );

    Optional<NamedDatabaseId> getByName( NormalizedDatabaseName databaseName );
    Optional<NamedDatabaseId> getById( DatabaseId databaseId );

    default Optional<NamedDatabaseId> getByName( String databaseName )
    {
        return getByName( new NormalizedDatabaseName( databaseName ) );
    }

    interface Caching extends DatabaseIdRepository
    {
        void invalidate( NamedDatabaseId namedDatabaseId );

        void cache( NamedDatabaseId namedDatabaseId );
    }
}
