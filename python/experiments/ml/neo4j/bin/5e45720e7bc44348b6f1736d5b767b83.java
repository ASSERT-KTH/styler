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
package org.neo4j.kernel.impl.scheduler;

import java.util.Comparator;
import java.util.concurrent.
PriorityBlockingQueue ;importjava.util.concurrent.
TimeUnit ;importjava.util.concurrent.locks.

LockSupport ;importorg.neo4j.scheduler.
Group ;importorg.neo4j.scheduler.
JobHandle ;importorg.neo4j.time.

SystemNanoClock ; final class TimeBasedTaskScheduler
implements
    Runnable { private static final long NO_TASKS_PARK=TimeUnit.MINUTES. toNanos (10
    ) ; private staticfinalComparator< ScheduledJobHandle >
            DEADLINE_COMPARATOR=Comparator. comparingLong ( handle->handle .nextDeadlineNanos

    ) ; private finalSystemNanoClock
    clock ; private finalThreadPoolManager
    pools ; privatefinalPriorityBlockingQueue< ScheduledJobHandle>
    delayedTasks ; private volatileThread
    timeKeeper ; private volatileboolean

    stopped; TimeBasedTaskScheduler (SystemNanoClock clock , ThreadPoolManager
    pools
        ){this . clock=
        clock;this . pools=
        pools ; delayedTasks =newPriorityBlockingQueue< >( 42 ,DEADLINE_COMPARATOR
    )

    ; } publicJobHandle submit (Group group ,Runnable job ,long initialDelayNanos , long
    reschedulingDelayNanos
        ) { long now=clock.nanos(
        ) ; long nextDeadlineNanos = now+
        initialDelayNanos ; ScheduledJobHandle task =new ScheduledJobHandle( this, group, job, nextDeadlineNanos ,reschedulingDelayNanos
        ); enqueueTask (task
        ) ;return
    task

    ; }void enqueueTask ( ScheduledJobHandle
    newTasks
        ){delayedTasks. offer (newTasks
        );LockSupport. unpark (timeKeeper
    )

    ;}
    @ Override publicvoidrun
    (
        ) { timeKeeper=Thread.currentThread(
        ) ; while( !
        stopped
            ) { long timeToNextTickNanos=tick(
            ) ; if (
            stopped
                ){
            return
            ;}LockSupport. parkNanos( this ,timeToNextTickNanos
        )
    ;

    } } publiclongtick
    (
        ) { long now=clock.nanos(
        ) ; long timeToNextDeadlineSinceStart= scheduleDueTasks (now
        ) ; long processingTime=clock.nanos ( )-
        now ; return timeToNextDeadlineSinceStart-
    processingTime

    ; } privatelong scheduleDueTasks ( long
    now
        ) { if(delayedTasks.isEmpty (
        )
            )
            { // We have no tasks to run. Park until we're woken up by an enqueueTask() call.return
        NO_TASKS_PARK
        ; } while( ! stopped&&!delayedTasks.isEmpty ( )&&delayedTasks.peek() . nextDeadlineNanos <=
        now
            ) { ScheduledJobHandle task=delayedTasks.poll(
            );task. submitIfRunnable (pools
        )
        ; }returndelayedTasks.isEmpty ( ) ? NO_TASKS_PARK:delayedTasks.peek() . nextDeadlineNanos-
    now

    ; } publicvoidstop
    (
        ) { stopped=
        true;LockSupport. unpark (timeKeeper
    )
;
