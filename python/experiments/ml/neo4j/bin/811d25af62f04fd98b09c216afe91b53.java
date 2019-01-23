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

import org.junit.Test;

import java.util.Map;

import org.neo4j.graphdb.Node;importorg.neo4j.kernel.impl.annotations
. Documented;importorg.neo4j.server.rest.domain
. JsonHelper;importorg.neo4j.server

. rest .domain.JsonParseException;importorg.

neo4j . test . GraphDescription
;
    importstaticorg . junit
                 . Assert
                 . assertEquals
    ;public
    classDegreeITextendsAbstractRestFunctionalTestBase{ @Documented( "Get the degree of a node\n"+ "\n"
    + "Return the total number of relationships associated with a node." )@Test @ GraphDescription
    .
        Graph({"Root knows Mattias","Root knows Johan" } ) publicvoidget_degree()throws
        JsonParseException { Map <String ,Node>nodes = data .get

        (
        );String nodeUri = getNodeUri(nodes.get
                ("Root") ) ;
                // DocumentRESTRequestGenerator. ResponseEntity response = gen.

        get
        () .expectedStatus (200). get(nodeUri+"/degree/all");// ThenassertEquals (2,JsonHelper. jsonNode(
    response

    .response( ) .
                 getEntity (
                 ) )
                 . asInt
    ()
    );}@Documented ("Get the degree of a node by direction\n"+ "\n"+ "Return the number of relationships of a particular direction for a node.\n"
    + "Specify `all`, `in` or `out`." )@Test @ GraphDescription
    .
        Graph({"Root knows Mattias","Root knows Johan" } ) publicvoidget_degree_by_direction()throws
        JsonParseException { Map <String ,Node>nodes = data .get

        (
        );String nodeUri = getNodeUri(nodes.get
                ("Root") ) ;
                // DocumentRESTRequestGenerator. ResponseEntity response = gen.

        get
        () .expectedStatus (200). get(nodeUri+"/degree/out");// ThenassertEquals (2,JsonHelper. jsonNode(
    response

    .response( ) .
                 getEntity (
                 ) )
                 . asInt
                 ( )
    );
    }@Documented("Get the degree of a node by direction and types\n" +"\n"+ "If you are only interested in the degree of a particular relationship type, or a set of "+ "relationship types, you specify relationship types after the direction.\n"+ "You can combine multiple relationship types by using the `&` character."
    ) @ Test@GraphDescription . Graph
    (
        {"Root KNOWS Mattias","Root KNOWS Johan","Root LIKES Cookie" } ) publicvoidget_degree_by_direction_and_type()throws
        JsonParseException { Map <String ,Node>nodes = data .get

        (
        );String nodeUri = getNodeUri(nodes.get
                ("Root") ) ;
                // DocumentRESTRequestGenerator. ResponseEntity response = gen.

        get
        () .expectedStatus (200). get(nodeUri+"/degree/out/KNOWS&LIKES");// ThenassertEquals (3,JsonHelper. jsonNode(
    response
.
