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
package org.neo4j.kernel.impl.transaction ;importorg.junit.Rule;

import org.junit.Test;importjava.util.concurrent
. Future;importorg.neo4j.kernel.impl.util
. IdOrderingQueue;importorg.neo4j.kernel
. impl.util.SynchronizedArrayIdOrderingQueue;importorg.neo4j
. test.OtherThreadExecutor;importorg.neo4j.test

. OtherThreadExecutor .WorkerCommand;importorg.neo4j.
test . rule.CleanupRule;importstaticorg.

junit . Assert
.
    assertFalse;
    import static org . junit . Assert.assertTrue;

    publicclass
    SynchronizedArrayIdOrderingQueueTest { @Rulepublic final CleanupRule
    cleanup
        =
        new CleanupRule ( ) ;@ Test publicvoid

        shouldOfferQueueABunchOfIds
        ( ) throws Exception { // GIVENIdOrderingQueue queue = newSynchronizedArrayIdOrderingQueue (5 )
        ;
            // WHENfor(int i =0
        ;

        i
        < 7 ; i ++ ){ queue . offer( i) ;
        }
            // THENfor (inti=0 ;i
            <7;i ++ ){
            assertFalse(queue. isEmpty ()
        )
        ;queue .waitFor(i) ;queue
    .

    removeChecked(
    i ) ;}assertTrue ( queue
    .
        isEmpty
        ( ) ) ; }@ Test publicvoid
        shouldOfferAwaitAndRemoveRoundAndRound ( ) throwsException
        { // GIVEN IdOrderingQueue queue=
        newSynchronizedArrayIdOrderingQueue(5 ); longofferedId
        =0;long awaitedId= 0;

        queue
        . offer ( offeredId ++ ); queue . offer( offeredId++ )
        ;
            // WHENfor(int i =0
            ;i<20 ;i ++)
            {queue.waitFor (awaitedId );
            queue. removeChecked(awaitedId++) ;queue
        .

        offer
        (offeredId++) ;assertFalse (queue
        .isEmpty() ) ;}
        // THENqueue .removeChecked(awaitedId++ );
    queue

    .removeChecked
    ( awaitedId );assertTrue ( queue
    .
        isEmpty
        ( ) ) ; }@ Test publicvoid
        shouldHaveOneThreadWaitForARemoval()throws Exception {// GIVEN
        IdOrderingQueuequeue=new SynchronizedArrayIdOrderingQueue (5

        )
        ;queue.offer ( 3 );queue. offer (5);// WHEN another thread comes in and awaits 5 OtherThreadExecutor< Void > t2=
        cleanup.add( new OtherThreadExecutor <Void>( "T2", null) ) ; Future<
        Object>await5=t2.
        executeDontWait
        (awaitHead(queue , 5)

        )
        ;t2.waitUntilWaiting()
    ;

    // ... and head (3) gets removedqueue
    . removeChecked (3)
    ;
        // THEN the other thread should be OK to continue
        await5 . get ( ); } @Test
        public void shouldExtendArrayWhenIdsAreWrappingAround ( ) {// GIVEN IdOrderingQueue queue =new SynchronizedArrayIdOrderingQueue( 5
        )
            ;for(int i =0
            ;i<3 ; i++
        )
        {
        queue
        . offer ( i ) ;queue . removeChecked (i ); }
        // Now we're at [0,1,2,0,0]
            //                     ^-- headIndex and offerIndexfor(int i =3
        ;
        i
        <

        8
        ;i++) { queue.

        offer
        ( i ) ; } // Now we're at [5,6,2,3,4]//                     ^-- headIndex and offerIndex%length // WHEN offering one more, so that the queue is forced to resize queue .offer (8 )
        ;
            // THEN it should have been offered as well as all the previous ids should be intactfor (inti=3 ;i
            <=8;i ++ ){
        assertFalse
        (queue .isEmpty()) ;queue
    .

    removeChecked (i); }assertTrue (queue . isEmpty () ) ; } private
    WorkerCommand
        < Void ,
        Object
            >awaitHead(final IdOrderingQueue queue,
            final longid
        ){
    return
state
