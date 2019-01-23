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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import java.util.Collections;
import javax.ws.rs.
core .MediaType;importjavax.ws.rs.
core .Response.Status;importorg.neo4j.server.

helpers .FunctionalTestHelper;importorg.neo4j.server.
rest .domain.GraphDbHelper;importorg.neo4j.server.
rest .domain.RelationshipDirection;importorg.neo4j.test.
server .HTTP;importstaticorg.junit.Assert

. assertEquals ;importstaticorg.junit.Assert
. assertFalse ;importstaticorg.junit.Assert
. assertTrue ;publicclassHtmlITextendsAbstractRestFunctionalTestBase{private

static FunctionalTestHelper functionalTestHelper ; private
static
    GraphDbHelper helper ; privatelong
    thomasAnderson ; private longtrinity
    ; private longthomasAndersonLovesTrinity
    ; @ BeforeClasspublic
    static void setupServer(

    ){
    functionalTestHelper = new FunctionalTestHelper(server
    (
        ) ) ; helper= functionalTestHelper.getGraphDbHelper ()
        ; } @BeforepublicvoidsetupTheDatabase(
    )

    {// Create the matrix example
    thomasAnderson = createAndIndexNode("Thomas Anderson"
    )
        ;
        trinity = createAndIndexNode( "Trinity" );
        long tank =createAndIndexNode ( "Tank")
        ; long knowsRelationshipId =helper . createRelationship(

        "KNOWS" , thomasAnderson ,trinity); thomasAndersonLovesTrinity= helper. createRelationship ("LOVES"
        , thomasAnderson ,trinity); helper. setRelationshipProperties( thomasAndersonLovesTrinity ,Collections
        .singletonMap("strength" ,100
                ));helper .createRelationship ( "KNOWS" ,thomasAnderson
        ,tank); helper. createRelationship( "KNOWS" ,trinity
        ,tank); // index a relationshiphelper .createRelationshipIndex ( "relationships")

        ;
        helper.addRelationshipToIndex( "relationships" ,"key"
        ,"value",knowsRelationshipId ); // index a relationshiphelper .createRelationshipIndex ( "relationships2")

        ;
        helper.addRelationshipToIndex( "relationships2" ,"key2"
        ,"value2",knowsRelationshipId ); }private longcreateAndIndexNode ( Stringname
    )

    { long id= helper . createNode
    (
        ) ; helper .setNodeProperties(id,Collections
        .singletonMap("name" ,name ));helper .addNodeToIndex ( "node" ,"name"
        ,name,id ); returnid ;} @ Testpublic
        void shouldGetRoot(
    )

    {JaxRsResponse
    response = RestRequest.req
    (
        ) . get (functionalTestHelper.dataUri(),MediaType .TEXT_HTML_TYPE);assertEquals( Status.OK .getStatusCode
        () ,response.getStatus()); assertValidHtml(response.getEntity ()
        ); response.close() ;}
        @TestpublicvoidshouldGetRootWithHTTP(
    )

    {HTTP
    . Response response=HTTP
    .
        withHeaders("Accept" , MediaType
                .TEXT_HTML). GET( functionalTestHelper.dataUri ()); assertEquals(Status.OK .getStatusCode
        () ,response.status()); assertValidHtml(response.rawContent ()
        ); }@Testpublicvoid shouldGetNodeIndexRoot(
    )

    {JaxRsResponse
    response = RestRequest.req
    (
        ) . get (functionalTestHelper.nodeIndexUri(),MediaType .TEXT_HTML_TYPE);assertEquals( Status.OK .getStatusCode
        () ,response.getStatus()); assertValidHtml(response.getEntity ()
        ); response.close() ;}
        @TestpublicvoidshouldGetRelationshipIndexRoot(
    )

    {JaxRsResponse
    response = RestRequest.req
    (
        ) . get
                (functionalTestHelper.relationshipIndexUri(),MediaType .TEXT_HTML_TYPE);assertEquals( Status.OK .getStatusCode
        () ,response.getStatus()); assertValidHtml(response.getEntity ()
        ); response.close() ;}
        @TestpublicvoidshouldGetTrinityWhenSearchingForHer(
    )

    {JaxRsResponse
    response = RestRequest.req
    (
        ) . get (functionalTestHelper.indexNodeUri(
                "node","name" ,"Trinity"), MediaType. TEXT_HTML_TYPE) ; assertEquals( Status.OK .getStatusCode
        () ,response.getStatus()); Stringentity=response. getEntity(
        ) ; assertTrue (entity.contains("Trinity"
        )) ;assertValidHtml(entity ) ; response.
        close( ) ;}
        @TestpublicvoidshouldGetThomasAndersonDirectly(
    )

    {JaxRsResponse
    response = RestRequest.req
    (
        ) . get
                (functionalTestHelper.nodeUri(thomasAnderson), MediaType.TEXT_HTML_TYPE) ; assertEquals( Status.OK .getStatusCode
        () ,response.getStatus()); Stringentity=response. getEntity(
        ) ; assertTrue (entity.contains("Thomas Anderson"
        )) ;assertValidHtml(entity ) ; response.
        close( ) ;}
        @TestpublicvoidshouldGetSomeRelationships(
    )

    {final
    RestRequest request =RestRequest.
    req
        ( ) ; JaxRsResponse response=request.get(
        functionalTestHelper . relationshipsUri (thomasAnderson,RelationshipDirection
                .all.name () ,"KNOWS"),MediaType.TEXT_HTML_TYPE) ; assertEquals(
                Status.OK .getStatusCode
        () ,response.getStatus()); Stringentity=response. getEntity(
        ) ; assertTrue (entity.contains("KNOWS"
        )) ;assertFalse(entity . contains ("LOVES"
        )) ;assertValidHtml(entity ) ; response.
        close( ) ;response
        =request.get(functionalTestHelper

        . relationshipsUri (thomasAnderson,RelationshipDirection
                .all.name () ,"LOVES"),MediaType.TEXT_HTML_TYPE) ; entity=
                response.getEntity ()

        ; assertFalse (entity.contains("KNOWS"
        )) ;assertTrue(entity . contains ("LOVES"
        )) ;assertValidHtml(entity ) ; response.
        close( ) ;response
        =request.get(functionalTestHelper

        . relationshipsUri (thomasAnderson,RelationshipDirection .
                        all.name () ,"LOVES","KNOWS"),MediaType. TEXT_HTML_TYPE) ; entity=
                response.getEntity ()
        ; assertTrue (entity.contains("KNOWS"
        )) ;assertTrue(entity . contains ("LOVES"
        )) ;assertValidHtml(entity ) ; response.
        close( ) ;}
        @TestpublicvoidshouldGetThomasAndersonLovesTrinityRelationship(
    )

    {JaxRsResponse
    response = RestRequest.req
    (
        ) . get (functionalTestHelper.relationshipUri(
                thomasAndersonLovesTrinity), MediaType.TEXT_HTML_TYPE) ; assertEquals( Status.OK .getStatusCode
        () ,response.getStatus()); Stringentity=response. getEntity(
        ) ; assertTrue (entity.contains("strength"
        )) ;assertTrue(entity . contains ("100"
        )) ;assertTrue(entity . contains ("LOVES"
        )) ;assertValidHtml(entity ) ; response.
        close( ) ;}
        privatevoidassertValidHtml(Stringentity
    )

    { assertTrue (entity . contains (
    "<html>"
        )) ;assertTrue(entity . contains ("</html>"
        )) ;}}