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
package org.neo4j.kernel.impl.transaction.command;

import org.eclipse.collections.api.map.primitive.LongObjectMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.neo4j.helpers.collection.NestingIterator;
import org.neo4j.internal.kernel.api.schema.SchemaDescriptor;
import org.neo4j.kernel.api.exceptions.index.IndexEntryConflictException;
import org.neo4j.kernel
. api.index.IndexEntryUpdate;importorg.neo4j.kernel.impl
. api.index.IndexingUpdateService;importorg.neo4j.kernel
. impl.store.UnderlyingStorageException;importorg.neo4j.kernel.impl.transaction
. command.Command.NodeCommand;importorg.neo4j.kernel.impl.transaction
. command.Command.PropertyCommand;importorg.neo4j.kernel.impl
. transaction.state.IndexUpdates;importorg.neo4j

.
util . concurrent . Work;/**
 * Combines {@link IndexUpdates} from multiple transactions into one bigger job.
 */publicclassIndexUpdatesWork
implements
    Work < IndexingUpdateService,IndexUpdatesWork> { private final List<IndexUpdates>updates=

    new ArrayList< > ( )
    ;
        publicIndexUpdatesWork(IndexUpdatesupdates) { this.
    updates

    .add
    ( updates ); } @ Override
    public
        IndexUpdatesWorkcombine(IndexUpdatesWork work){ updates.
        addAll (work
    .

    updates)
    ; return this; } @ Override
    public
        void
        apply
            (IndexingUpdateServicematerial) {try{ material.
        apply
        ( combinedUpdates ( ) ) ; }
        catch
            ( IOException |IndexEntryConflictException e ){
        throw
    new

    UnderlyingStorageException ( e);
    }
        } private IndexUpdatescombinedUpdates(
        )
            {return
            new IndexUpdates(){@Overridepublic Iterator<IndexEntryUpdate
            <
                SchemaDescriptor > >iterator(){returnnewNestingIterator<IndexEntryUpdate <SchemaDescriptor>,IndexUpdates >
                (
                    updates.
                    iterator ()){@Overrideprotected Iterator< IndexEntryUpdate < SchemaDescriptor
                    >
                        > createNestedIterator(IndexUpdatesitem){
                    return
                item.
            iterator

            ()
            ; } }; }@Overridepublicvoidfeed( LongObjectMap<
                    List<PropertyCommand>>propCommandsByNodeId, LongObjectMap< List<PropertyCommand> >propCommandsByRelationshipId
                    ,LongObjectMap<NodeCommand>nodeCommands , LongObjectMap
            <
                Command . RelationshipCommand>relationshipCommandPrimitiveLongObjectMap)
            {

            thrownew
            UnsupportedOperationException ( );}
            @
                Override publicboolean
            hasUpdates
        ()
    {
return
