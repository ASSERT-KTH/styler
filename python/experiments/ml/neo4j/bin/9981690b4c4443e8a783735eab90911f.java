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
package org.neo4j.bolt.v1.transport.socket.client;

import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.
ExpectedException ;importjava.io.IOException;

import org.neo4j.test.

rule .SuppressOutput;importstaticorg.mockito.Mockito

. mock ;importstaticorg.mockito.Mockito
. when ;publicclassWebSocketConnectionTest{@Rulepublic

ExpectedException expectedException =
ExpectedException

    .none
    ( ) ; @ RulepublicSuppressOutputsuppressOutput=SuppressOutput
    .suppressAll
    ( ) ; @ TestpublicvoidshouldNotThrowAnyExceptionWhenDataReceivedBeforeClose()

    throwsThrowable
    { // Given WebSocketClientclient= mock (
    WebSocketClient
        .
        class ) ; WebSocketConnectionconn =newWebSocketConnection (client
        ) ; when ( client. isStopped ()
        ). thenReturn(true); byte[]data = {0

        ,1, 2 , 3,4 ,5 ,6 ,7 ,8 ,9 }; // Whenconn .onWebSocketBinary (data,

        0
        ,10); conn. recv( 10 );
        // Then// no exception}@ Test publicvoid

        shouldThrowIOExceptionWhenNotEnoughDataReceivedBeforeClose
        (
    )

    throwsThrowable
    { // Given WebSocketClientclient= mock (
    WebSocketClient
        .
        class ) ; WebSocketConnectionconn =newWebSocketConnection (client
        ) ; when ( client. isStopped ()
        ). thenReturn(true,true );byte[ ]data = {0

        ,1, 2 , 3}; // When && Thenconn .onWebSocketBinary (data,

        0
        ,4); expectedException. expect( IOException .class

        );expectedException. expectMessage("Connection closed while waiting for data from the server." );
        conn.recv( 10 );
        }}