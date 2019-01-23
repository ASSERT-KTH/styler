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
package org.neo4j.unsafe.impl.batchimport.store;

import org.junit.Rule;
import org.junit.

Test ;importjava.util.concurrent.

Future ;importorg.neo4j.io.pagecache.
PageCache ;importorg.neo4j.test.
Barrier ;importorg.neo4j.test.rule.concurrent.

OtherThreadRule ; importstaticorg.junit.Assert.
assertSame ; importstaticorg.junit.Assert.
fail ; importstaticorg.mockito.Mockito.
doAnswer ; importstaticorg.mockito.Mockito.

mock ; public
class
    PageCacheFlusherTest{
    @ Rule publicfinalOtherThreadRule< Void > t2 =newOtherThreadRule<>(

    );@ Test ( timeout =
    10_000 ) publicvoidshouldWaitForCompletionInHalt ( )
    throws
        Exception
        { // GIVEN PageCache pageCache= mock(PageCache .class
        );Barrier . Control barrier =newBarrier.Control(
        ); doAnswer (
        invocation
            ->{barrier.reached(
            ) ;return
        null ;}). when (pageCache).flushAndForce(
        ) ; PageCacheFlusher flusher =new PageCacheFlusher (pageCache
        );flusher.start(

        )
        ;// WHENbarrier.await(
        );Future< Object > halt=t2. execute (
        state
            ->{flusher.halt(
            ) ;return
        null ;}
        );t2.get(). waitUntilWaiting ( details->details. isAt(PageCacheFlusher. class , "halt")
        );barrier.release(

        )
        ;// THEN halt call exits normally after (confirmed) ongoing flushAndForce call completed.halt.get(
    )

    ;}
    @ Test publicvoidshouldExitOnErrorInHalt ( )
    throws
        Exception
        { // GIVEN PageCache pageCache= mock(PageCache .class
        ) ; RuntimeException failure =newRuntimeException(
        ); doAnswer (
        invocation
            -> {throw
        failure ;}). when (pageCache).flushAndForce(
        ) ; PageCacheFlusher flusher =new PageCacheFlusher (pageCache
        );flusher.run(

        )
        ;
        // WHEN
            try{flusher.halt(
            );fail(
        )
        ; } catch ( RuntimeException
        e
            )
            {// THEN assertSame( failure ,e
        )
    ;
}
