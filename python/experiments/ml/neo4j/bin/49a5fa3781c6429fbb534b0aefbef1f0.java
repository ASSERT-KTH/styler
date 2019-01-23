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
package org.neo4j.kernel.impl.index.schema.

fusion ;importjava.io.
File ;importjava.util.

Iterator ;importorg.neo4j.graphdb.
ResourceIterator ;importorg.neo4j.helpers.collection.
BoundedIterable ;importorg.neo4j.helpers.collection.
Iterables ;importorg.neo4j.io.pagecache.
IOLimiter ;importorg.neo4j.kernel.api.exceptions.index.
IndexEntryConflictException ;importorg.neo4j.kernel.api.index.
IndexAccessor ;importorg.neo4j.kernel.api.index.
IndexUpdater ;importorg.neo4j.kernel.api.index.
NodePropertyAccessor ;importorg.neo4j.kernel.impl.api.index.
IndexUpdateMode ;importorg.neo4j.kernel.impl.index.schema.fusion.FusionIndexProvider.
DropAction ;importorg.neo4j.storageengine.api.schema.
IndexReader ;importorg.neo4j.storageengine.api.schema.
StoreIndexDescriptor ;importorg.neo4j.values.storable.

Value ; importstaticorg.neo4j.helpers.collection.Iterators.

concatResourceIterators ; class FusionIndexAccessorextendsFusionIndexBase< IndexAccessor >
implements
    IndexAccessor { private finalStoreIndexDescriptor
    descriptor ; private finalDropAction

    dropAction; FusionIndexAccessor (SlotSelector
            slotSelector,InstanceSelector< IndexAccessor>
            instanceSelector ,StoreIndexDescriptor
            descriptor , DropAction
    dropAction
        ){ super( slotSelector ,instanceSelector
        );this . descriptor=
        descriptor;this . dropAction=
    dropAction

    ;}
    @ Override publicvoiddrop
    (
        ){instanceSelector. forAll(IndexAccessor ::drop
        );dropAction. drop(descriptor.getId ()
    )

    ;}
    @ Override publicIndexUpdater newUpdater ( IndexUpdateMode
    mode
        ){LazyInstanceSelector< IndexUpdater > updaterSelector =newLazyInstanceSelector< > ( slot->instanceSelector. select (slot). newUpdater ( mode)
        ) ; returnnew FusionIndexUpdater( slotSelector ,updaterSelector
    )

    ;}
    @ Override publicvoid force ( IOLimiter
    ioLimiter
        ){instanceSelector. forAll ( accessor->accessor. force ( ioLimiter)
    )

    ;}
    @ Override publicvoidrefresh
    (
        ){instanceSelector. forAll(IndexAccessor ::refresh
    )

    ;}
    @ Override publicvoidclose
    (
        ){instanceSelector. close(IndexAccessor ::close
    )

    ;}
    @ Override publicIndexReadernewReader
    (
        ){LazyInstanceSelector< IndexReader > readerSelector =newLazyInstanceSelector< > ( slot->instanceSelector. select (slot).newReader ()
        ) ; returnnew FusionIndexReader( slotSelector, readerSelector ,descriptor
    )

    ;}
    @ OverridepublicBoundedIterable< Long>newAllEntriesReader
    (
        ){Iterable<BoundedIterable<Long > > entries=instanceSelector. transform(IndexAccessor ::newAllEntriesReader
        ) ; returnnewBoundedIterable<Long>
        (
            ){
            @ Override publiclongmaxCount
            (
                ) { long sum=
                0 ; for ( BoundedIterable entry :
                entries
                    ) { long maxCount=entry.maxCount(
                    ) ; if ( maxCount ==
                    UNKNOWN_MAX_COUNT
                        ) {return
                    UNKNOWN_MAX_COUNT
                    ; } sum+=
                maxCount
                ; }return
            sum

            ;}@ SuppressWarnings (
            "unchecked")
            @ Override publicvoidclose ( )
            throws
                Exception{ forAll(BoundedIterable:: close ,entries
            )

            ;}
            @ OverridepublicIterator< Long>iterator
            (
                ) {returnIterables. concat (entries).iterator(
            )
        ;}
    }

    ;}
    @ OverridepublicResourceIterator< File>snapshotFiles
    (
        ) {return concatResourceIterators(instanceSelector. transform(IndexAccessor ::snapshotFiles).iterator ()
    )

    ;}
    @ Override publicvoid verifyDeferredConstraints ( NodePropertyAccessor nodePropertyAccessor )
    throws
        IndexEntryConflictException { for ( IndexSlot slot:IndexSlot.values (
        )
            ){instanceSelector. select (slot). verifyDeferredConstraints (nodePropertyAccessor
        )
    ;

    }}
    @ Override publicbooleanisDirty
    (
        ) {returnIterables. stream(instanceSelector. transform(IndexAccessor :: isDirty)). anyMatch(Boolean ::booleanValue
    )

    ;}
    @ Override publicvoid validateBeforeCommit(Value [ ]
    tuple
        ){instanceSelector. select(slotSelector. selectSlot( tuple , GROUP_OF)). validateBeforeCommit (tuple
    )
;
