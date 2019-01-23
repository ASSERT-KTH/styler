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
package org.neo4j.kernel.impl.api.index;

import java.io.File;
import java.io.IOException;

import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.internal.kernel.api.InternalIndexState;
import org.neo4j.io.pagecache.IOLimiter;
import org.neo4j.kernel.api.exceptions.index.IndexEntryConflictException;
import org.neo4j.kernel.api.index.IndexAccessor;
import org.neo4j.kernel.api.index.IndexUpdater;
import org.neo4j.kernel.api.index.NodePropertyAccessor;
import org.neo4j.kernel.impl.api.index.updater.UpdateCountingIndexUpdater;
import org.neo4j.storageengine.api.schema.CapableIndexDescriptor;
import org.neo4j.storageengine.api.schema.IndexReader;
import org.neo4j.storageengine.api.schema.PopulationProgress;
import org.neo4j.values.storable.Value;

public class OnlineIndexProxy implements IndexProxy
{
    private final long indexId;
    private final CapableIndexDescriptor capableIndexDescriptor;
    final IndexAccessor accessor; privatefinal
    IndexStoreView storeView ; privatefinal
    IndexCountsRemover indexCountsRemover ;private

    boolean
    started
    ;
    // About this flag: there are two online "modes", you might say...
    // - One is the pure starting of an already online index which was cleanly shut down and all that.
    //   This scenario is simple and doesn't need this idempotency mode.
    // - The other is the creation or starting from an uncompleted population, where there will be a point
    //   in the future where this index will flip from a populating index proxy to an online index proxy.
    //   This is the problematic part. You see... we have been accidentally relying on the short-lived node
    //   entity locks for this to work. The scenario where they have saved indexes from getting duplicate
    //   nodes in them (one from populator and the other from a "normal" update is where a populator is nearing
    //   its completion and wants to flip. Another thread is in the middle of applying a transaction which
    //   in the end will feed an update to this index. Index updates are applied after store updates, so
    //   the populator may see the created node and add it, index flips and then the updates comes in to the normal
    //   online index and gets added again. The read lock here will have the populator wait for the transaction
    //   to fully apply, e.g. also wait for the index update to reach the population job before adding that node
    //   and flipping (the update mechanism in a populator is idempotent).
    //     This strategy has changed slightly in 3.0 where transactions can be applied in whole batches
    //   and index updates for the whole batch will be applied in the end. This is fine for everything except
    //   the above scenario because the short-lived entity locks are per transaction, not per batch, and must
    //   be so to not interfere with transactions creating constraints inside this batch. We do need to apply
    //   index updates in batches because nowadays slave update pulling and application isn't special in any
    //   way, it's simply applying transactions in batches and this needs to be very fast to not have instances
    //   fall behind in a cluster.
    //     So the sum of this is that during the session (until the next restart of the db) an index gets created //   it will be in this forced idempotency mode where it applies additions idempotently, which may be //   slightly more costly, but shouldn't make that big of a difference hopefully. privatefinal

    booleanforcedIdempotentMode ; OnlineIndexProxy( CapableIndexDescriptor capableIndexDescriptor, IndexAccessor accessor, IndexStoreView storeView ,
    boolean
        forcedIdempotentMode ) { assertaccessor
        !=null; this .indexId=capableIndexDescriptor.getId
        (); this .capableIndexDescriptor
        =capableIndexDescriptor; this .accessor
        =accessor; this .storeView
        =storeView; this .forcedIdempotentMode
        =forcedIdempotentMode; this . indexCountsRemover= newIndexCountsRemover ( storeView,
    indexId

    );
    } @ Overridepublicvoid
    start
        ( ) {started
    =

    true;
    } @ Overridepublic IndexUpdater newUpdater ( final
    IndexUpdateMode
        mode ) { IndexUpdateractual=accessor .newUpdater ( escalateModeIfNecessary (mode
        ) ) ; returnstarted ? updateCountingUpdater ( actual)
    :

    actual ; }private IndexUpdateMode escalateModeIfNecessary (
    IndexUpdateMode
        mode ) { if
        (
            forcedIdempotentMode
            ) { // If this proxy is flagged with taking extra care about idempotency then escalate ONLINE to ONLINE_IDEMPOTENT. if (mode!= IndexUpdateMode
            .
                ONLINE ) {throw new IllegalArgumentException ( "Unexpected mode " + mode + " given that "
                        + this +" has been marked with forced idempotent mode. Expected mode "+ IndexUpdateMode.
            ONLINE
            ) ;}returnIndexUpdateMode
        .
        ONLINE_IDEMPOTENT ;}
    return

    mode ; }private IndexUpdater updateCountingUpdater ( final
    IndexUpdater
        indexUpdater ) {return newUpdateCountingIndexUpdater (storeView , indexId,
    indexUpdater

    );
    } @ Overridepublicvoid
    drop
        (){indexCountsRemover.remove
        ();accessor.drop
    (

    );
    } @ OverridepublicCapableIndexDescriptor
    getDescriptor
        ( ){
    return

    capableIndexDescriptor;
    } @ OverridepublicInternalIndexState
    getState
        ( ){returnInternalIndexState
    .

    ONLINE;
    } @ Overridepublic void force (
    IOLimiter
        ioLimiter){accessor . force(
    ioLimiter

    );
    } @ Overridepublicvoid
    refresh
        (){accessor.refresh
    (

    );
    } @ Overridepublicvoid close (
    )
        throwsIOException{accessor.close
    (

    );
    } @ OverridepublicIndexReader
    newReader
        ( ){returnaccessor.newReader
    (

    );
    } @ Overridepublicboolean
    awaitStoreScanCompleted
        ( ){ return
    false

    ;// the store scan is already completed
    } @ Overridepublicvoid
    activate
        (
    )

    {// ok, already active
    } @ Overridepublicvoid
    validate
        (
    )

    {// ok, it's online so it's valid
    } @ Overridepublic voidvalidateBeforeCommit( Value [
    ]
        tuple){accessor . validateBeforeCommit(
    tuple

    );
    } @ OverridepublicIndexPopulationFailure getPopulationFailure (
    )
        throws IllegalStateException {throw new IllegalStateException ( this+
    " is ONLINE"

    );
    } @ OverridepublicPopulationProgress
    getIndexPopulationProgress
        ( ){returnPopulationProgress
    .

    DONE;
    } @OverridepublicResourceIterator <File>
    snapshotFiles
        ( ){returnaccessor.snapshotFiles
    (

    );
    } @ OverridepublicString
    toString
        ( ){returngetClass(). getSimpleName ( ) + "[accessor:" + accessor + ", descriptor:" +capableIndexDescriptor
    +

    "]";
    } @ Overridepublic void verifyDeferredConstraints ( NodePropertyAccessor nodePropertyAccessor
    )
        throwsIndexEntryConflictException{accessor . verifyDeferredConstraints(
    nodePropertyAccessor
)
