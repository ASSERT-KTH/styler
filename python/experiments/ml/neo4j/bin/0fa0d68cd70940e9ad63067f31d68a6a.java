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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;importstatic
org . hamcrest.Matchers.is;importstatic
org . junit.Assert.fail;publicclass

IndexSpecifierTest { @
Test
    publicvoid
    shouldFormatAsCanonicalRepresentation ( ){assertThat
    (
        newIndexSpecifier ( ":Person(name)") . toString(),is( ":Person(name)") ) ; }@
    Test

    publicvoid
    shouldParseASimpleLabel ( ){assertThat
    (
        newIndexSpecifier ( ":Person_23(name)") . label(),is( "Person_23") ) ; }@
    Test

    publicvoid
    shouldParseASimpleProperty ( ){assertThat
    (
        newIndexSpecifier ( ":Person(a_Name_123)") . properties(),is( arrayContaining( "a_Name_123") ) ) ; }@
    Test

    publicvoid
    shouldParseTwoProperties ( ){assertThat
    (
        newIndexSpecifier ( ":Person(name, lastName)") . properties(),is(
                arrayContaining( "name", "lastName") ) ) ; }@
    Test

    publicvoid
    shouldParseManyProperties ( ){String
    [
        ]properties= new IndexSpecifier ( ":Person(1, 2, 3, 4, 5, 6)") . properties();assertThat(
        properties, is(
                arrayContaining( "1", "2", "3", "4", "5", "6") ) ) ; }@
    Test

    publicvoid
    shouldParseOddProperties ( ){String
    [
        ]properties= new IndexSpecifier ( ": Person(1,    2lskgj_LKHGS, `3sdlkhs,   df``sas;g`, 4, `  5  `, 6)") . properties
                ();assertThat(
        properties, is(
                arrayContaining( "1", "2lskgj_LKHGS", "3sdlkhs,   df``sas;g", "4", "  5  ", "6") ) ) ; }@
    Test

    publicvoid
    shouldParseANastyLabel ( ){assertThat
    (
        newIndexSpecifier ( ":`:(!\"£$%^&*( )`(name)") . label(),is( ":(!\"£$%^&*( )") ) ; }@
    Test

    publicvoid
    shouldParseANastyProperty ( ){assertThat
    (
        newIndexSpecifier ( ":Person(`(:!\"£$%^&*( )`)") . properties(),is(
                arrayContaining( "(:!\"£$%^&*( )") ) ) ; }@
    Test

    publicvoid
    shouldProduceAReasonableErrorIfTheSpecificationCantBeParsed ( ){try
    {
        new
        IndexSpecifier
            ( "rubbish") ; fail(
            "expected exception") ; }catch
        (
        IllegalArgumentException e ) { //expected
        }
            }
        }
    