/*
 * Copyright (c) 2002-2018 "Neo4j,"
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
package org.neo4j.kernel.impl.store.kvstore;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

import org.neo4j.io.pagecache.tracing.cursor.context.VersionContextSupplier;

public abstract class PrototypeState<Key >extendsWritableState<
Key
    > { protectedfinalActiveState< Key>

    store ;public PrototypeState(ActiveState< Key >
    store
        ){this . store=
    store

    ; } protectedabstractActiveState< Key> create(ReadableState< Key> sub ,File file , VersionContextSupplierversionContextSupplier

    );
    @ Override protected finalHeadersheaders
    (
        ) {returnstore.headers(
    )

    ;}
    @ Override protected finalintstoredEntryCount
    (
        ) {returnstore.storedEntryCount(
    )

    ;}
    @ Override protectedfinalKeyFormat< Key>keyFormat
    (
        ) {returnstore.keyFormat(
    )

    ;}
    @ OverridefinalEntryUpdater< Key> resetter (Lock lock , Runnable
    runnable
        ) { thrownew UnsupportedOperationException ("should never be invoked"
    )

    ;}
    @ Override public finalvoidclose
    (
        ) { thrownew UnsupportedOperationException ("should never be invoked"
    )

    ;}
    @ OverridefinalOptional<EntryUpdater<Key >> optionalUpdater (long version , Lock
    lock
        ) {returnOptional. of( updater( version , lock)
    )

    ; } protectedabstractEntryUpdater< Key> updater (long version , Locklock
)
