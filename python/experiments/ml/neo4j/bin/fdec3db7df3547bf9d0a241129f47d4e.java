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
package org.neo4j.server.rest;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
importorg.junit.Rule
; importorg.junit.Test
; importorg.junit.rules.TemporaryFolder

; importjava.io.IOException

; importorg.neo4j.server.CommunityNeoServer
; importorg.neo4j.server.helpers.CommunityServerBuilder
; importorg.neo4j.server.helpers.FunctionalTestHelper
; importorg.neo4j.server.helpers.ServerHelper
; importorg.neo4j.test.TestData
; importorg.neo4j.test.server.ExclusiveServerTestBase

; import staticorg.hamcrest.Matchers.containsString
; import staticorg.junit.Assert.assertThat

; public class AutoIndexWithNonDefaultConfigurationThroughRESTAPIIT extends
ExclusiveServerTestBase
    { private static CommunityNeoServerserver
    ; private static FunctionalTestHelperfunctionalTestHelper

    ;@
    ClassRule public static TemporaryFolder staticFolder = newTemporaryFolder()

    ;@
    Rule publicTestData<RESTRequestGenerator > gen =TestData.producedThrough (RESTRequestGenerator. PRODUCER)

    ;@
    BeforeClass public static voidallocateServer( ) throws
    IOException
        { server =CommunityServerBuilder.serverOnRandomPorts(
                ).usingDataDir (staticFolder.getRoot().getAbsolutePath( )
                ).withAutoIndexingEnabledForNodes ("foo" , "bar"
                ).build()
        ;server.start()
        ; functionalTestHelper = newFunctionalTestHelper ( server)
    ;

    }@
    Before public voidcleanTheDatabase(
    )
        {ServerHelper.cleanTheDatabase ( server)
    ;

    }@
    AfterClass public static voidstopServer(
    )
        {server.stop()
    ;

    }
    /**
     * Create an auto index for nodes with specific configuration.
     */@
    Test public voidshouldCreateANodeAutoIndexWithGivenFullTextConfiguration(
    )
        { String responseBody =gen.get(
                ).expectedStatus ( 201
                ).payload ( "{\"name\":\"node_auto_index\", \"config\":{\"type\":\"fulltext\",\"provider\":\"lucene\"}}"
                ).post (functionalTestHelper.nodeIndexUri( )
                ).entity()

        ;assertThat (responseBody ,containsString ( "\"type\" : \"fulltext\"" ))
    ;

    }
    /**
     * Create an auto index for relationships with specific configuration.
     */@
    Test public voidshouldCreateARelationshipAutoIndexWithGivenFullTextConfiguration(
    )
        { String responseBody =gen.get(
                ).expectedStatus ( 201
                ).payload
                        ( "{\"name\":\"relationship_auto_index\", \"config\":{\"type\":\"fulltext\","
                                + "\"provider\":\"lucene\"}}"
                ).post (functionalTestHelper.relationshipIndexUri( )
                ).entity()

        ;assertThat (responseBody ,containsString ( "\"type\" : \"fulltext\"" ))
    ;

}
