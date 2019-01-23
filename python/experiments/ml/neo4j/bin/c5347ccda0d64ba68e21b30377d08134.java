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
package org.neo4j.harness;

import org.codehaus.jackson.JsonNode;
import org.junit.Rule;
import org.junit.Test;

import org.neo4j.procedure.UserAggregationFunction;
import org.neo4j.procedure.UserAggregationResult;importorg
. neo4j.procedure.UserAggregationUpdate;importorg
. neo4j.test.rule.SuppressOutput;importorg
. neo4j.test.rule.TestDirectory;importorg
. neo4j.test.server.HTTP;importstatic

org . junit.Assert.assertEquals;importstatic
org . neo4j.test.server.HTTP.RawPayload.quotedJson;publicclass

JavaAggregationFunctionsTestIT { @
Rule
    publicTestDirectory
    testDir = TestDirectory . testDirectory();@Rule

    publicSuppressOutput
    suppressOutput = SuppressOutput . suppressAll();publicstatic

    class MyFunctions { @
    UserAggregationFunction
        publicEliteAggregator
        myFunc ( ){return
        new
            EliteAggregator ( );}@
        UserAggregationFunction

        publicEliteAggregator
        funcThatThrows ( ){throw
        new
            RuntimeException ( "This is an exception") ; }}
        public

    static

    class EliteAggregator { @
    UserAggregationUpdate
        publicvoid
        update ( ){}
        @
        UserAggregationResult

        publiclong
        result ( ){return
        1337L
            ; }}
        @
    Test

    publicvoid
    shouldLaunchWithDeclaredFunctions ( )throwsException { // When
    try
        (
        ServerControls server = createServer ( MyFunctions. class). newServer()){ // Then
        HTTP
            .
            Responseresponse= HTTP . POST(server. httpURI().resolve("db/data/transaction/commit") . toString(),quotedJson(
                    "{ 'statements': [ { 'statement': 'RETURN org.neo4j.harness.myFunc() AS someNumber' } ] "+
                            "}" )
                            ) ; JsonNoderesult

            = response . get("results") . get(0) ; assertEquals(
            "someNumber", result. get("columns") . get(0) . asText()); assertEquals(
            1337, result. get("data") . get(0) . get("row") . get(0) . asInt()); assertEquals(
            "[]", response. get("errors") . toString()); }}
        private
    TestServerBuilder

    createServer ( Class< ?>functionClass) { return
    TestServerBuilders
        . newInProcessBuilder().withAggregationFunction
                                 (functionClass) ; }@
    Test

    publicvoid
    shouldGetHelpfulErrorOnProcedureThrowsException ( )throwsException { // When
    try
        (
        ServerControls server = createServer ( MyFunctions. class). newServer()){ // Then
        HTTP
            .
            Responseresponse= HTTP . POST(server. httpURI().resolve("db/data/transaction/commit") . toString(),quotedJson(
                    "{ 'statements': [ { 'statement': 'RETURN org.neo4j.harness.funcThatThrows()' } ] }")
                            ) ; Stringerror

            = response . get("errors") . get(0) . get("message") . asText();assertEquals(
            "Failed to invoke function `org.neo4j.harness.funcThatThrows`: Caused by: java.lang"+
                    ".RuntimeException: This is an exception" ,
                    error)
                    ; }}
        }
    