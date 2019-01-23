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
package org.neo4j.kernel.impl.transaction;

import org.junit.Rule;
import org.junit.Test; importjava.util.concurrent.Future

; importorg.neo4j.kernel.impl.util.IdOrderingQueue
; importorg.neo4j.kernel.impl.util.SynchronizedArrayIdOrderingQueue
; importorg.neo4j.test.OtherThreadExecutor
; importorg.neo4j.test.OtherThreadExecutor.WorkerCommand
; importorg.neo4j.test.rule.CleanupRule

; import staticorg.junit.Assert.assertFalse
; import staticorg.junit.Assert.assertTrue

; public class
SynchronizedArrayIdOrderingQueueTest
    {@
    Rule public final CleanupRule cleanup = newCleanupRule()

    ;@
    Test public voidshouldOfferQueueABunchOfIds( ) throws
    Exception
        {
        // GIVEN IdOrderingQueue queue = newSynchronizedArrayIdOrderingQueue ( 5)

        ;
        // WHEN for ( int i =0 ; i <7 ;i ++
        )
            {queue.offer ( i)
        ;

        }
        // THEN for ( int i =0 ; i <7 ;i ++
        )
            {assertFalse (queue.isEmpty( ))
            ;queue.waitFor ( i)
            ;queue.removeChecked ( i)
        ;
        }assertTrue (queue.isEmpty( ))
    ;

    }@
    Test public voidshouldOfferAwaitAndRemoveRoundAndRound( ) throws
    Exception
        {
        // GIVEN IdOrderingQueue queue = newSynchronizedArrayIdOrderingQueue ( 5)
        ; long offeredId =0
        ; long awaitedId =0
        ;queue.offer (offeredId ++)
        ;queue.offer (offeredId ++)

        ;
        // WHEN for ( int i =0 ; i <20 ;i ++
        )
            {queue.waitFor ( awaitedId)
            ;queue.removeChecked (awaitedId ++)
            ;queue.offer (offeredId ++)
            ;assertFalse (queue.isEmpty( ))
        ;

        }
        // THENqueue.removeChecked (awaitedId ++)
        ;queue.removeChecked ( awaitedId)
        ;assertTrue (queue.isEmpty( ))
    ;

    }@
    Test public voidshouldHaveOneThreadWaitForARemoval( ) throws
    Exception
        {
        // GIVEN IdOrderingQueue queue = newSynchronizedArrayIdOrderingQueue ( 5)
        ;queue.offer ( 3)
        ;queue.offer ( 5)

        ;
        // WHEN another thread comes in and awaits 5OtherThreadExecutor<Void > t2 =cleanup.add ( newOtherThreadExecutor<Void> ("T2" , null ))
        ;Future<Object > await5 =t2.executeDontWait (awaitHead (queue , 5 ))
        ;t2.waitUntilWaiting()
        ;
        // ... and head (3) gets removedqueue.removeChecked ( 3)

        ;
        // THEN the other thread should be OK to continueawait5.get()
    ;

    }@
    Test public voidshouldExtendArrayWhenIdsAreWrappingAround(
    )
        {
        // GIVEN IdOrderingQueue queue = newSynchronizedArrayIdOrderingQueue ( 5)
        ; for ( int i =0 ; i <3 ;i ++
        )
            {queue.offer ( i)
            ;queue.removeChecked ( i)
        ;
        }
        // Now we're at [0,1,2,0,0]
        //                     ^-- headIndex and offerIndex for ( int i =3 ; i <8 ;i ++
        )
            {queue.offer ( i)
        ;
        }
        // Now we're at [5,6,2,3,4]

        //                     ^-- headIndex and offerIndex%length
        // WHEN offering one more, so that the queue is forced to resizequeue.offer ( 8)

        ;
        // THEN it should have been offered as well as all the previous ids should be intact for ( int i =3 ; i <=8 ;i ++
        )
            {assertFalse (queue.isEmpty( ))
            ;queue.removeChecked ( i)
        ;
        }assertTrue (queue.isEmpty( ))
    ;

    } privateWorkerCommand<Void ,Object >awaitHead ( final IdOrderingQueuequeue , final long id
    )
        { return state
        ->
            {queue.waitFor ( id)
            ; returnnull
        ;}
    ;
}
