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
package org.neo4j.bolt.v1.runtime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import org.neo4j.bolt.messaging.RequestMessage;
import org.neo4j.bolt.runtime.BoltStateMachineState;
importorg.neo4j.bolt.runtime.MutableConnectionState
; importorg.neo4j.bolt.runtime.StateMachineContext
; importorg.neo4j.bolt.v1.messaging
. request.AckFailureMessage;importorg.neo4j.bolt.v1.messaging
. request.DiscardAllMessage;importorg.neo4j.bolt.v1.messaging
. request.InitMessage;importorg.neo4j.bolt.v1.messaging
. request.InterruptSignal;importorg.neo4j.bolt.v1.messaging
. request.PullAllMessage;importorg.neo4j.bolt.v1.messaging
. request.ResetMessage;importorg.neo4j.bolt.v1.messaging
. request.RunMessage;importstaticjava.util.Arrays.asList;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.
Assertions . assertEquals;importstaticorg.junit.jupiter.api.
Assertions . assertFalse;importstaticorg.junit.jupiter.api.
Assertions . assertThrows;importstaticorg.junit.jupiter.api.
Assertions . assertTrue;importstaticorg.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.neo4j.values.virtual.
VirtualValues . EMPTY_MAP;classInterruptedStateTest{privatefinalInterruptedStatestate=newInterruptedState

( )
;
   private final BoltStateMachineState readyState = mock (BoltStateMachineState.class

   ) ; private final BoltStateMachineState failedState= mock(BoltStateMachineState .class
   ) ; private final StateMachineContext context= mock(StateMachineContext .class

   ) ; private final MutableConnectionState connectionState= newMutableConnectionState( );
   @ BeforeEach void setUp ( ) {state.setReadyState

   (readyState
   ) ;state.
   setFailedState
       (failedState); when (context
       .connectionState() ) .thenReturn

       (connectionState );}@Test voidshouldThrowWhenNotInitialized() throws Exception{
   InterruptedState

   state=
   new InterruptedState() ; assertThrows
   (
       IllegalStateException . class , ()->state

       .process (ResetMessage.INSTANCE ,context ) );state. setReadyState(readyState) ; assertThrows (IllegalStateException

       .class,( ) ->state
       .process (ResetMessage.INSTANCE ,context ) );state. setReadyState(null) ; state .setFailedState

       (failedState); assertThrows (IllegalStateException
       .class,( ) ->state
       .process (ResetMessage.INSTANCE ,context ) );}@ TestvoidshouldProcessInterruptMessage( ) throws Exception{
   BoltStateMachineState

   newState=
   state .process( InterruptSignal .
   INSTANCE
       , context ) ;assertEquals(state ,newState); // remains in interrupted state }@

       Testvoid shouldProcessResetMessageWhenInterrupted( ) throwsException {
   connectionState

   .incrementInterruptCounter
   ( );connectionState . incrementInterruptCounter
   (
       );assertTrue(connectionState.
       isInterrupted());assertFalse
       (connectionState .hasPendingIgnore()) ;BoltStateMachineState
       newState= state.process(ResetMessage .INSTANCE

       , context ) ;assertEquals(state ,newState); // remains in interrupted state assertTrue(

       connectionState. hasPendingIgnore( ) ); }
       @Test voidshouldProcessResetMessage()throws Exception{
   when

   (context
   . resetMachine() ) .
   thenReturn
       (true );// reset successfulBoltStateMachineStatenewState =state.process ( ResetMessage. INSTANCE
       , context ) ;assertEquals(readyState ,newState); } @Test

       voidshouldHandleFailureDuringResetMessageProcessing () throws Exception{
   when

   (context
   . resetMachine() ) .
   thenReturn
       (false );// reset failedBoltStateMachineStatenewState =state.process ( ResetMessage. INSTANCE
       , context ) ;assertEquals(failedState ,newState); } @Test

       voidshouldIgnoreMessagesOtherThanInterruptAndReset () throws Exception{
   List

   <RequestMessage
   > messages=asList ( AckFailureMessage
   .
       INSTANCE,PullAllMessage. INSTANCE , DiscardAllMessage. INSTANCE,newRunMessage ("RETURN 1",EMPTY_MAP ),newInitMessage
               ( "Driver", emptyMap( ) )) ; for( RequestMessagemessage :messages) { connectionState.

       resetPendingFailedAndIgnored ( ) ;BoltStateMachineState newState =
       state
           .process(message,context

           ) ; assertEquals (state,newState ); // remains in interrupted state assertTrue(

           connectionState. hasPendingIgnore( ) ); }
           }} 