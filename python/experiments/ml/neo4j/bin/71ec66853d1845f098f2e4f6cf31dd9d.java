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
importjava.
util .Map;importorg.neo4j.bolt.
messaging .RequestMessage;importorg.neo4j.bolt.
runtime .BoltStateMachineSPI;importorg.neo4j.bolt.
runtime .BoltStateMachineState;importorg.neo4j.bolt.
runtime .MutableConnectionState;importorg.neo4j.bolt.runtime.
StateMachineContext ;importorg.neo4j.bolt.security.auth.AuthenticationResult;
import org.neo4j.bolt.v1.messaging.request.AckFailureMessage;
import org.neo4j.bolt.v1.messaging.request.DiscardAllMessage;
import org.neo4j.bolt.v1.messaging.request.InitMessage;
import org.neo4j.bolt.v1.messaging.request.InterruptSignal;
import org.neo4j.bolt.v1.messaging.request.PullAllMessage;
import org.neo4j.bolt.v1.messaging.request.ResetMessage;
import org.neo4j.bolt.v1.messaging.request.RunMessage;

import org .neo4j.internal.kernel.api
. security .LoginContext;importstaticjava.util.Arrays.asList
; import staticorg.junit.jupiter.api.Assertions.assertEquals
; import staticorg.junit.jupiter.api.Assertions.assertNotNull
; import staticorg.junit.jupiter.api.Assertions.assertNull
; import staticorg.junit.jupiter.api
. Assertions .assertThrows;importstaticorg.mockito
. ArgumentMatchers .eq;importstaticorg.mockito
. Mockito .RETURNS_MOCKS;importstaticorg.mockito
. Mockito .mock;importstaticorg.mockito
. Mockito .verify;importstaticorg.mockito.Mockito.when;import
static org .neo4j.kernel.api.security.AuthToken.newBasicAuthToken
; import staticorg.neo4j.values.storable.Values.TRUE
; import staticorg.neo4j.values.storable.Values.stringValue

; import
static
    org . neo4j . values . virtual.
    VirtualValues . EMPTY_MAP ;classConnectedStateTest{privatestatic final String USER_AGENT= "Driver 2.0"; private staticfinal
    Map < String , Object > AUTH_TOKEN =newBasicAuthToken ("neo4j" , "password")

    ; private static final InitMessage INIT_MESSAGE =newInitMessage(

    USER_AGENT , AUTH_TOKEN ) ; privatefinal ConnectedStatestate= newConnectedState
    ( ) ; private final BoltStateMachineStatereadyState =mock( BoltStateMachineState.

    class ) ; private final BoltStateMachineStatefailedState =mock( BoltStateMachineState.
    class ) ; private final StateMachineContextcontext =mock(StateMachineContext . class)
    ; private final BoltStateMachineSPI boltSpi = mock(BoltStateMachineSPI.

    class,
    RETURNS_MOCKS );private
    final
        MutableConnectionStateconnectionState=new MutableConnectionState ()
        ;@BeforeEachvoid setUp ()

        {state .setReadyState(readyState) ;state.setFailedState ( failedState)
        ;when (context.boltSpi( )).thenReturn ( boltSpi)
    ;

    when(
    context .connectionState( ) )
    .
        thenReturn ( connectionState ) ;}@Test

        voidshouldThrowWhenNotInitialized ()throwsException {ConnectedState state =newConnectedState( ); assertThrows ( IllegalStateException.

        class,() -> state.
        process( INIT_MESSAGE,context) ); state .setReadyState(readyState ); assertThrows ( IllegalStateException.

        class,() -> state.
        process(INIT_MESSAGE, context ))
        ;state .setReadyState(null ); state .setFailedState(failedState ); assertThrows ( IllegalStateException.
    class

    ,(
    ) ->state. process (
    INIT_MESSAGE
        , context ) );}@ Testvoid shouldAuthenticateOnInitMessage ()

        throwsException {BoltStateMachineState newState =state
        .process ( INIT_MESSAGE,context) ; assertEquals(
    readyState

    ,newState
    ) ;verify( boltSpi )
    .
        authenticate ( AUTH_TOKEN );}@ Testvoid shouldInitializeStatementProcessorOnInitMessage ()

        throwsException {BoltStateMachineState newState =state
        .process (INIT_MESSAGE,context) ;assertEquals
    (

    readyState,
    newState );assertNotNull ( connectionState
    .
        getStatementProcessor ( ) ); }@Test voidshouldAddMetadataOnExpiredCredentialsOnInitMessage
        () throwsException{MutableConnectionStateconnectionStateMock =mock(MutableConnectionState . class)

        ; when ( context. connectionState() ).
        thenReturn( connectionStateMock);AuthenticationResultauthResult =mock(AuthenticationResult . class)
        ;when (authResult.credentialsExpired( )).thenReturn (true) ;when
        (authResult .getLoginContext() ) . thenReturn(LoginContext. AUTH_DISABLED );

        when ( boltSpi .authenticate(AUTH_TOKEN )) . thenReturn(

        authResult) ;BoltStateMachineState newState =state
        .process ( INIT_MESSAGE,context) ;assertEquals ( readyState,
    newState

    );
    verify (connectionStateMock) . onMetadata
    (
        "credentials_expired", TRUE);}@ TestvoidshouldAddServerVersionMetadataOnInitMessage( ) throwsException
        { when ( boltSpi. version() ).
        thenReturn( "42.42.42");MutableConnectionStateconnectionStateMock =mock(MutableConnectionState . class)

        ; when ( context. connectionState() ).
        thenReturn( connectionStateMock);AuthenticationResultauthResult =mock(AuthenticationResult . class)
        ;when (authResult.credentialsExpired( )).thenReturn (true) ;when
        (authResult .getLoginContext() ) . thenReturn(LoginContext. AUTH_DISABLED );

        when ( boltSpi .authenticate(AUTH_TOKEN )) . thenReturn(

        authResult) ;BoltStateMachineState newState =state
        .process ( INIT_MESSAGE,context) ;assertEquals (readyState , newState );
    verify

    (connectionStateMock
    ) .onMetadata( "server" ,
    stringValue
        ( "42.42.42" ) );}@ Testvoid shouldRegisterClientInUDCOnInitMessage ()

        throwsException {BoltStateMachineState newState =state
        .process ( INIT_MESSAGE,context) ;assertEquals ( readyState ,newState
    )

    ;verify
    ( boltSpi). udcRegisterClient (
    eq
        ( USER_AGENT ) ) ;} @ Testvoid
        shouldHandleFailuresOnInitMessage( )throwsException{ RuntimeException error =newRuntimeException( "Hello" );

        when ( boltSpi .authenticate(AUTH_TOKEN )) . thenThrow(

        error) ;BoltStateMachineState newState =state
        .process ( INIT_MESSAGE,context) ;assertEquals ( failedState,
    newState

    );
    verify (context) . handleFailure
    (
        error,true) ; } @Test voidshouldNotProcessUnsupportedMessage() throwsException{List <RequestMessage>unsupportedMessages
                =asList(AckFailureMessage .INSTANCE,DiscardAllMessage . INSTANCE, InterruptSignal. INSTANCE , PullAllMessage.

        INSTANCE , ResetMessage .INSTANCE , new
        RunMessage
            ("RETURN 1" ,EMPTY_MAP)) ;for ( RequestMessage message:
        unsupportedMessages
    )
{
