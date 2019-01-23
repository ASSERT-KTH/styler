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
package org.neo4j.index.internal.gbptree;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.neo4j.helpers.Exceptions;
import org.neo4j.scheduler.Group;
import org.neo4j.scheduler.JobHandle;
import org.neo4j.scheduler.JobSchedulerAdapter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GroupingRecoveryCleanupWorkCollectorTest
{
    private final ImmediateJobScheduler jobScheduler = new ImmediateJobScheduler();
    private final GroupingRecoveryCleanupWorkCollector collector =
            new GroupingRecoveryCleanupWorkCollector( jobScheduler );

    @Test
    void mustNotScheduleAnyJobsBeforeStart()
    {
        // given
        List<DummyJob> allRuns = new ArrayList<>();
        List<DummyJob> expectedJobs = someJobs( allRuns );

        // when
        collector.init();
        addAll( expectedJobs );

        // then
        assertTrue( allRuns.isEmpty() );
    }

    @Test
    void mustScheduleAllJobs()
    {
        // given
        List<DummyJob> allRuns = new ArrayList<>();
        List<DummyJob> expectedJobs = someJobs( allRuns );

        // when
        collector.init();
        addAll( expectedJobs );
        collector.start();

        // then
        assertSame( expectedJobs, allRuns );
    }

    @Test
    void mustThrowIfOldJobsDuringInit()
    {
        // given
        List<DummyJob> allRuns = new ArrayList<>();
        List<DummyJob> someJobs = someJobs( allRuns );

        // when
        addAll( someJobs );
        assertThrows( IllegalStateException.class, collector::init );
    }

    @Test
    void mustCloseOldJobsOnShutdown()
    {
        // given
        List<DummyJob> allRuns = new ArrayList<>();
        List<DummyJob> someJobs = someJobs( allRuns );

        // when
        collector.init();
        addAll( someJobs );
        collector.shutdown();

        // then
        assertTrue( allRuns.isEmpty(), "Expected no jobs to run" );
        for ( DummyJob job : someJobs )
        {
            assertTrue( job.isClosed(), "Expected all jobs to be closed" );
        }
    }

    @Test
    void mustNotScheduleOldJobsOnMultipleStart()
    {
        // given
        List<DummyJob> allRuns = new ArrayList<>();
        List<DummyJob> expectedJobs = someJobs( allRuns );

        // when
        collector.init();
        addAll( expectedJobs );
        collector.start();
        collector.start();

        // then
        assertSame( expectedJobs, allRuns );
    }

    @Test
    void mustNotScheduleOldJobsOnStartStopStart() throws Throwable
    {
        // given
        List<DummyJob> allRuns = new ArrayList<>();
        List<DummyJob> expectedJobs = someJobs( allRuns );

        // when
        collector.init();
        addAll( expectedJobs );
        collector.start();
        collector.stop();
        collector.start();

        // then
        assertSame( expectedJobs, allRuns );
    }

    @Test
    void executeAllTheJobsWhenSeparateJobFails()
    {
        List<DummyJob> allRuns = new ArrayList<>();
        collector.init();

        DummyJob firstJob = new DummyJob( "first", allRuns );
        DummyJob thirdJob = new DummyJob( "third", allRuns );
        DummyJob fourthJob = new DummyJob( "fourth", allRuns );
        List<DummyJob> expectedJobs = Arrays.asList( firstJob, thirdJob, fourthJob );

        collector.add( firstJob );
        collector.add( new EvilJob() );
        collector.add( thirdJob );
        collector.add( fourthJob );

        RuntimeException exception = assertThrows( RuntimeException.class, collector::start );
        assertTrue( Exceptions.contains( exception, "Resilient to run attempts", RuntimeException.class ) );

        assertSame( expectedJobs, allRuns );
    }

    @Test
    void throwOnAddingJobsAfterStart()
    {
        collector.init();
        collector.start();

        assertThrows( IllegalStateException.class, () -> collector.add( new DummyJob( "first", new ArrayList<>() ) ) );
    }

    private void addAll( Collection<DummyJob> jobs )
    {
        jobs.forEach( collector::add );
    }

    private void assertSame( List<DummyJob> someJobs, List<DummyJob> actual )
    {
        assertTrue( actual.containsAll( someJobs ) );
        assertTrue( someJobs.containsAll( actual ) );
    }

    private List<DummyJob> someJobs( List<DummyJob> allRuns )
    {
        return new ArrayList<>( Arrays.asList(
                new DummyJob( "A", allRuns ),
                new DummyJob( "B", allRuns ),
                new DummyJob( "C", allRuns )
        ) );
    }

    private class ImmediateJobScheduler extends JobSchedulerAdapter
    {
        @Override
        public JobHandle schedule( Group group, Runnable job )
        {
            job.run();
            return super.schedule( group, job );
        }
    }

    private class EvilJob implements CleanupJob
    {

        @Override
        public boolean needed()
        {
            return false;
        }

        @Override
        public boolean hasFailed()
        {
            return false;
        }

        @Override
        public Throwable getCause()
        {
            return null;
        }

        @Override
        public void close()
        {
            // nothing to close
        }

        @Override
        public void run( ExecutorService executor )
        {
            throw new RuntimeException( "Resilient to run attempts" );
        }
    }

    private class DummyJob implements CleanupJob
    {
        private final String name;
        private final List<DummyJob> allRuns;
        private boolean closed;

        DummyJob( String name, List<DummyJob> allRuns )
        {
            this.name = name;
            this.allRuns = allRuns;
        }

        @Override
        public String toString()
        {
            return name;
        }

        @Override
        public boolean needed()
        {
            return false;
        }

        @Override
        public boolean hasFailed()
        {
            return false;
        }

        @Override
        public Throwable getCause()
        {
            return null;
        }

        @Override
        public void close()
        {
            closed = true;
        }

        @Override
        public void run( ExecutorService executor )
        {
            allRuns.add( this );
        }

        public boolean isClosed()
        {
            return closed;
        }
    }
}
