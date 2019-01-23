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
package org.neo4j.kernel.impl.event;

import org.junit.BeforeClass;
import org.junit.Test;

import org.neo4j .graphdb.GraphDatabaseService;importorg.neo4j.
graphdb .event.ErrorState;importorg.neo4j.
graphdb .event.KernelEventHandler;importorg.

neo4j . test.TestGraphDatabaseFactory;importstaticorg.
junit . Assert.assertEquals;importstaticorg.
junit . Assert.assertSame;importstaticorg.
junit . Assert.fail;importstaticorg.neo4j.kernel.

impl . AbstractNeo4jTestCase
.
    deleteFileOrDirectory ; public class TestKernelEvents { privatestatic

    final String PATH = "target/var/neodb" ; private staticfinalObjectRESOURCE1
    = new Object ( ) ; private staticfinalObjectRESOURCE2

    =new
    Object ( ) ;@BeforeClass
    public
        staticvoid doBefore ()
    {

    deleteFileOrDirectory(
    PATH ) ;}@
    Test
        public void testRegisterUnregisterHandlers ( ){GraphDatabaseServicegraphDb=newTestGraphDatabaseFactory(
        ) . newImpermanentDatabase ( ); KernelEventHandler handler1
        =
            newDummyKernelEventHandler
            ( RESOURCE1 ){ @ Override public
            ExecutionOrder
                orderComparedTo (KernelEventHandlerother)
            {
        returnExecutionOrder
        . DOESNT_MATTER ; } }; KernelEventHandler handler2
        =
            newDummyKernelEventHandler
            ( RESOURCE2 ){ @ Override public
            ExecutionOrder
                orderComparedTo (KernelEventHandlerother)
            {
        returnExecutionOrder

        .
        DOESNT_MATTER
            ;}}; try {graphDb
            .unregisterKernelEventHandler (
                  handler1 ) ;fail
        (
        "Shouldn't be able to do unregister on a " + "unregistered handler" ) ;
        } catch
        (

        IllegalStateExceptione ){ /* Good */}assertSame( handler1 , graphDb.
        registerKernelEventHandler( handler1) );assertSame( handler1 , graphDb.
        registerKernelEventHandler( handler1) );assertSame( handler1 , graphDb.

        unregisterKernelEventHandler
        (
            handler1)); try {graphDb
            .unregisterKernelEventHandler (
                  handler1 ) ;fail
        (
        "Shouldn't be able to do unregister on a " + "unregistered handler" ) ;
        } catch
        (

        IllegalStateExceptione ){ /* Good */}assertSame( handler1 , graphDb.
        registerKernelEventHandler( handler1) );assertSame( handler2 , graphDb.
        registerKernelEventHandler( handler2) );assertSame( handler1 , graphDb.
        unregisterKernelEventHandler( handler1) );assertSame( handler2 , graphDb.

        unregisterKernelEventHandler(handler2));
    graphDb

    .shutdown
    ( ) ;}@
    Test
        public void testShutdownEvents ( ){GraphDatabaseServicegraphDb=newTestGraphDatabaseFactory(
        ) . newImpermanentDatabase ( ); DummyKernelEventHandler handler1
        =
            newDummyKernelEventHandler
            ( RESOURCE1 ){ @ Override public
            ExecutionOrder
                orderComparedTo ( KernelEventHandlerother){ if((( DummyKernelEventHandler ) other
                )
                    . resource==RESOURCE2)
                {
                return ExecutionOrder.AFTER;
            }
        returnExecutionOrder
        . DOESNT_MATTER ; } }; DummyKernelEventHandler handler2
        =
            newDummyKernelEventHandler
            ( RESOURCE1 ){ @ Override public
            ExecutionOrder
                orderComparedTo ( KernelEventHandlerother){ if((( DummyKernelEventHandler ) other
                )
                    . resource==RESOURCE1)
                {
                return ExecutionOrder.BEFORE;
            }
        returnExecutionOrder
        .DOESNT_MATTER;} } ;graphDb
        .registerKernelEventHandler(handler1 ) ;graphDb

        .registerKernelEventHandler(handler2);

        graphDb. shutdown(); assertEquals (Integer .valueOf( 0)
        ,handler2 .beforeShutdown); assertEquals (Integer .valueOf( 1)
    ,

    handler1 . beforeShutdown ) ; } private
    abstract
        static class DummyKernelEventHandler implementsKernelEventHandler
        { private staticint
        counter ; privateInteger
        beforeShutdown ; private IntegerkernelPanic

        ;private final Object resource
        ;
            DummyKernelEventHandler(Object resource ){
        this

        .resource
        = resource ;}@
        Override
            public void beforeShutdown()
        {

        beforeShutdown=
        counter ++ ;}@
        Override
            public ObjectgetResource()
        {

        returnthis
        . resource ;} @ Override public
        void
            kernelPanic ( ErrorStateerror)
        {
    kernelPanic
=
