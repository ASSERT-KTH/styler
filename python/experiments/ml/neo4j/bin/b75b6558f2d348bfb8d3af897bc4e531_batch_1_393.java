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
package org.neo4j.unsafe.impl.batchimport.staging;

import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.neo4j.test.OtherThreadExecutor.WorkerCommand;
import org.neo4j.test.rule.concurrent.OtherThreadRule;
import org.neo4j.unsafe.impl.batchimport.Configuration;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.neo4j.unsafe.impl.batchimport.staging.Step.ORDER_SEND_DOWNSTREAM;

public class ProcessorStepTest
{
    @Rule
    public final OtherThreadRule<Void> t2 = new OtherThreadRule<>();

    @Test
    public void shouldUpholdProcessOrderingGuarantee() throws Exception
    {
        // GIVEN
        StageControl control = mock( StageControl.class );
        MyProcessorStep step = new MyProcessorStep( control, 0 );
        step.start( ORDER_SEND_DOWNSTREAM );
        step.processors( 4 ); // now at 5
        // WHEN int batches =10
        ; for ( int i =0 ; i <batches ;i ++
        )
            {step.receive (i , i)
        ;
        }step.endOfUpstream()
        ;step.awaitCompleted()

        ;
        // THENassertEquals (batches ,step.nextExpected.get( ))
        ;step.close()
    ;

    }@
    Test public voidshouldHaveTaskQueueSizeEqualToMaxNumberOfProcessors( ) throws
    Exception
        {
        // GIVEN StageControl control =mock (StageControl. class)
        ; final CountDownLatch latch = newCountDownLatch ( 1)
        ; final int processors =2
        ; int maxProcessors =5
        ; Configuration configuration = newConfiguration(
        )
            {@
            Override public intmaxNumberOfProcessors(
            )
                { returnmaxProcessors
            ;
        }}
        ; finalProcessorStep<Void > step = newBlockingProcessorStep (control ,configuration ,processors , latch)
        ;step.start ( ORDER_SEND_DOWNSTREAM)
        ;step.processors ( 1) ;
        // now at 2
        // adding up to max processors should be fine for ( int i =0 ; i < processors + maxProcessors/* +1 since we allow queueing one more*/ ;i ++
        )
            {step.receive (i , null)
        ;

        }
        // WHENFuture<Void > receiveFuture =t2.execute (receive (processors , step ))
        ;t2.get().waitUntilThreadState (Thread.State. TIMED_WAITING)
        ;latch.countDown()

        ;
        // THENreceiveFuture.get()
    ;

    }@
    Test public voidshouldRecycleDoneBatches( ) throws
    Exception
        {
        // GIVEN StageControl control =mock (StageControl. class)
        ; MyProcessorStep step = newMyProcessorStep (control , 0)
        ;step.start ( ORDER_SEND_DOWNSTREAM)

        ;
        // WHEN int batches =10
        ; for ( int i =0 ; i <batches ;i ++
        )
            {step.receive (i , i)
        ;
        }step.endOfUpstream()
        ;step.awaitCompleted()

        ;
        // THENverify (control ,times ( batches )).recycle (any( ))
        ;step.close()
    ;

    } private static class BlockingProcessorStep extendsProcessorStep<Void
    >
        { private final CountDownLatchlatch

        ;BlockingProcessorStep ( StageControlcontrol , Configurationconfiguration
                , intmaxProcessors , CountDownLatch latch
        )
            {super (control ,"test" ,configuration , maxProcessors)
            ;this. latch =latch
        ;

        }@
        Override protected voidprocess ( Voidbatch , BatchSender sender ) throws
        Throwable
            {latch.await()
        ;
    }

    } private static class MyProcessorStep extendsProcessorStep<Integer
    >
        { private final AtomicInteger nextExpected = newAtomicInteger()

        ; privateMyProcessorStep ( StageControlcontrol , int maxProcessors
        )
            {super (control ,"test" ,Configuration.DEFAULT , maxProcessors)
        ;

        }@
        Override protected voidprocess ( Integerbatch , BatchSender sender
        )   {
            // No processing in this testnextExpected.incrementAndGet()
        ;
    }

    } privateWorkerCommand<Void,Void >receive ( final intprocessors , finalProcessorStep<Void > step
    )
        { return state
        ->
            {step.receive (processors , null)
            ; returnnull
        ;}
    ;
}
