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
package org.neo4j.kernel.builtinprocs;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.
assertThat ; importstaticorg.hamcrest.Matchers.
arrayContaining ; importstaticorg.hamcrest.Matchers.
is ; importstaticorg.junit.Assert.

fail ; public
class
    IndexSpecifierTest{
    @ Test publicvoidshouldFormatAsCanonicalRepresentation
    (
        ){ assertThat (new IndexSpecifier (":Person(name)").toString( ), is ( ":Person(name)")
    )

    ;}
    @ Test publicvoidshouldParseASimpleLabel
    (
        ){ assertThat (new IndexSpecifier (":Person_23(name)").label( ), is ( "Person_23")
    )

    ;}
    @ Test publicvoidshouldParseASimpleProperty
    (
        ){ assertThat (new IndexSpecifier (":Person(a_Name_123)").properties( ), is( arrayContaining ( "a_Name_123" ))
    )

    ;}
    @ Test publicvoidshouldParseTwoProperties
    (
        ){ assertThat (new IndexSpecifier (":Person(name, lastName)").properties(
                ), is( arrayContaining( "name" , "lastName" ))
    )

    ;}
    @ Test publicvoidshouldParseManyProperties
    (
        ){String [ ] properties =new IndexSpecifier (":Person(1, 2, 3, 4, 5, 6)").properties(
        ); assertThat(
                properties, is( arrayContaining( "1", "2", "3", "4", "5" , "6" ))
    )

    ;}
    @ Test publicvoidshouldParseOddProperties
    (
        ){String [ ] properties =new IndexSpecifier (
                ": Person(1,    2lskgj_LKHGS, `3sdlkhs,   df``sas;g`, 4, `  5  `, 6)").properties(
        ); assertThat(
                properties, is( arrayContaining( "1", "2lskgj_LKHGS", "3sdlkhs,   df``sas;g", "4", "  5  " , "6" ))
    )

    ;}
    @ Test publicvoidshouldParseANastyLabel
    (
        ){ assertThat (new IndexSpecifier (":`:(!\"£$%^&*( )`(name)").label( ), is ( ":(!\"£$%^&*( )")
    )

    ;}
    @ Test publicvoidshouldParseANastyProperty
    (
        ){ assertThat (new IndexSpecifier (":Person(`(:!\"£$%^&*( )`)").properties(
                ), is( arrayContaining ( "(:!\"£$%^&*( )" ))
    )

    ;}
    @ Test publicvoidshouldProduceAReasonableErrorIfTheSpecificationCantBeParsed
    (
        )
        {
            try {new IndexSpecifier ("rubbish"
            ); fail ("expected exception"
        )
        ; } catch ( IllegalArgumentException
        e
            )
        {
    //expected
}
