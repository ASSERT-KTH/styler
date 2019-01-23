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
import java.util.Map;

import org.neo4j.bolt.messaging.RequestMessage;
import org.neo4j.bolt.runtime.BoltStateMachineSPI;
import org.neo4j.bolt.runtime.BoltStateMachineState;
import org.neo4j.bolt.runtime.MutableConnectionState;
import org.neo4j.bolt.runtime.StateMachineContext;
import org.neo4j.bolt.security.auth.AuthenticationResult;
import org.neo4j.bolt.v1.messaging.request.AckFailureMessage;
import org.neo4j.bolt.v1.messaging.request.
DiscardAllMessage ;importorg.neo4j.bolt.v1.messaging.request.
InitMessage ;importorg.neo4j.bolt.v1.messaging.request.
InterruptSignal ;importorg.neo4j.bolt.v1.messaging.request.
PullAllMessage ;importorg.neo4j.bolt.v1.messaging.request.
ResetMessage ;importorg.neo4j.bolt.v1.messaging.request.
RunMessage ;importorg.neo4j.internal.kernel.api.security.

LoginContext ; importstaticjava.util.Arrays.
asList ; importstaticorg.junit.jupiter.api.Assertions.
assertEquals ; importstaticorg.junit.jupiter.api.Assertions.
assertNotNull ; importstaticorg.junit.jupiter.api.Assertions.
assertNull ; importstaticorg.junit.jupiter.api.Assertions.
assertThrows ; importstaticorg.mockito.ArgumentMatchers.
eq ; importstaticorg.mockito.Mockito.
RETURNS_MOCKS ; importstaticorg.mockito.Mockito.
mock ; importstaticorg.mockito.Mockito.
verify ; importstaticorg.mockito.Mockito.
when ; importstaticorg.neo4j.kernel.api.security.AuthToken.
newBasicAuthToken ; importstaticorg.neo4j.values.storable.Values.
TRUE ; importstaticorg.neo4j.values.storable.Values.
stringValue ; importstaticorg.neo4j.values.virtual.VirtualValues.

EMPTY_MAP ;
class
    ConnectedStateTest { private static final String USER_AGENT=
    "Driver 2.0" ; private staticfinalMap<String, Object > AUTH_TOKEN= newBasicAuthToken( "neo4j" ,"password"
    ) ; private static final InitMessage INIT_MESSAGE =new InitMessage( USER_AGENT ,AUTH_TOKEN

    ) ; private final ConnectedState state =newConnectedState(

    ) ; private final BoltStateMachineState readyState= mock(BoltStateMachineState .class
    ) ; private final BoltStateMachineState failedState= mock(BoltStateMachineState .class

    ) ; private final StateMachineContext context= mock(StateMachineContext .class
    ) ; private final BoltStateMachineSPI boltSpi= mock(BoltStateMachineSPI. class ,RETURNS_MOCKS
    ) ; private final MutableConnectionState connectionState =newMutableConnectionState(

    );
    @ BeforeEachvoidsetUp
    (
        ){state. setReadyState (readyState
        );state. setFailedState (failedState

        ); when(context.boltSpi ()). thenReturn (boltSpi
        ); when(context.connectionState ()). thenReturn (connectionState
    )

    ;}
    @ TestvoidshouldThrowWhenNotInitialized ( )
    throws
        Exception { ConnectedState state =newConnectedState(

        ); assertThrows(IllegalStateException. class, ( )->state. process( INIT_MESSAGE , context)

        );state. setReadyState (readyState
        ); assertThrows(IllegalStateException. class, ( )->state. process( INIT_MESSAGE , context)

        );state. setReadyState (null
        );state. setFailedState (failedState
        ); assertThrows(IllegalStateException. class, ( )->state. process( INIT_MESSAGE , context)
    )

    ;}
    @ TestvoidshouldAuthenticateOnInitMessage ( )
    throws
        Exception { BoltStateMachineState newState=state. process( INIT_MESSAGE ,context

        ); assertEquals( readyState ,newState
        ); verify (boltSpi). authenticate (AUTH_TOKEN
    )

    ;}
    @ TestvoidshouldInitializeStatementProcessorOnInitMessage ( )
    throws
        Exception { BoltStateMachineState newState=state. process( INIT_MESSAGE ,context

        ); assertEquals( readyState ,newState
        ); assertNotNull(connectionState.getStatementProcessor ()
    )

    ;}
    @ TestvoidshouldAddMetadataOnExpiredCredentialsOnInitMessage ( )
    throws
        Exception { MutableConnectionState connectionStateMock= mock(MutableConnectionState .class
        ); when(context.connectionState ()). thenReturn (connectionStateMock

        ) ; AuthenticationResult authResult= mock(AuthenticationResult .class
        ); when(authResult.credentialsExpired ()). thenReturn (true
        ); when(authResult.getLoginContext ()). thenReturn(LoginContext .AUTH_DISABLED
        ); when(boltSpi. authenticate ( AUTH_TOKEN)). thenReturn (authResult

        ) ; BoltStateMachineState newState=state. process( INIT_MESSAGE ,context

        ); assertEquals( readyState ,newState
        ); verify (connectionStateMock). onMetadata( "credentials_expired" ,TRUE
    )

    ;}
    @ TestvoidshouldAddServerVersionMetadataOnInitMessage ( )
    throws
        Exception{ when(boltSpi.version ()). thenReturn ("42.42.42"
        ) ; MutableConnectionState connectionStateMock= mock(MutableConnectionState .class
        ); when(context.connectionState ()). thenReturn (connectionStateMock

        ) ; AuthenticationResult authResult= mock(AuthenticationResult .class
        ); when(authResult.credentialsExpired ()). thenReturn (true
        ); when(authResult.getLoginContext ()). thenReturn(LoginContext .AUTH_DISABLED
        ); when(boltSpi. authenticate ( AUTH_TOKEN)). thenReturn (authResult

        ) ; BoltStateMachineState newState=state. process( INIT_MESSAGE ,context

        ); assertEquals( readyState ,newState
        ); verify (connectionStateMock). onMetadata( "server", stringValue ( "42.42.42")
    )

    ;}
    @ TestvoidshouldRegisterClientInUDCOnInitMessage ( )
    throws
        Exception { BoltStateMachineState newState=state. process( INIT_MESSAGE ,context

        ); assertEquals( readyState ,newState
        ); verify (boltSpi). udcRegisterClient( eq ( USER_AGENT)
    )

    ;}
    @ TestvoidshouldHandleFailuresOnInitMessage ( )
    throws
        Exception { RuntimeException error =new RuntimeException ("Hello"
        ); when(boltSpi. authenticate ( AUTH_TOKEN)). thenThrow (error

        ) ; BoltStateMachineState newState=state. process( INIT_MESSAGE ,context

        ); assertEquals( failedState ,newState
        ); verify (context). handleFailure( error ,true
    )

    ;}
    @ TestvoidshouldNotProcessUnsupportedMessage ( )
    throws
        Exception{List< RequestMessage > unsupportedMessages= asList(AckFailureMessage. INSTANCE,DiscardAllMessage. INSTANCE,InterruptSignal.
                INSTANCE,PullAllMessage. INSTANCE,ResetMessage. INSTANCE ,new RunMessage( "RETURN 1" , EMPTY_MAP)

        ) ; for (RequestMessage message :
        unsupportedMessages
            ){ assertNull(state. process( message , context)
        )
    ;
}
