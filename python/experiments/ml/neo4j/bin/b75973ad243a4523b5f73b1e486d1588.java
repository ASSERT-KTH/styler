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
package org.neo4j.collection.primitive;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.neo4j.collection.primitive.PrimitiveLongCollections.PrimitiveLongBaseIterator;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.
Assertions . assertArrayEquals;importstaticorg.junit.jupiter.api.
Assertions . assertEquals;importstaticorg.junit.jupiter.api.
Assertions . assertFalse;importstaticorg.junit.jupiter.api.
Assertions . assertNotSame;importstaticorg.junit.jupiter.api.
Assertions . assertThrows;importstaticorg.junit.jupiter.api.

Assertions .
assertTrue
    ;class
    PrimitiveLongCollectionsTest {@Test
    void
        arrayOfItemsAsIterator
        (){ // GIVEN long [ ]items=newlong[ ]{ 2,5

        ,
        234 } ; // WHENPrimitiveLongIteratoriterator= PrimitiveLongCollections .iterator

        (
        items) ;// THEN assertItems (iterator
    ,

    items)
    ; }@Test
    void
        filter
        ( ) { // GIVENPrimitiveLongIteratoritems= PrimitiveLongCollections. iterator( 1 ,2

        ,
        3 ) ; // WHENPrimitiveLongIteratorfiltered= PrimitiveLongCollections. filter ( items , item ->item

        !=
        2) ;// THEN assertItems( filtered ,1
    ,

    3 ) ; } private static finalclass CountingPrimitiveLongIteratorResource
    implements
        PrimitiveLongIterator , AutoCloseable {private
        final PrimitiveLongIterator delegate ;private

        final AtomicIntegercloseCounter ; privateCountingPrimitiveLongIteratorResource ( PrimitiveLongIterator delegate
        ,
            AtomicIntegercloseCounter) { this.
            delegate=delegate ; this.
        closeCounter

        =closeCounter
        ; } @Overridepublic
        void
            close(){closeCounter.
        incrementAndGet

        ()
        ; } @Overridepublic
        boolean
            hasNext (){returndelegate.
        hasNext

        ()
        ; } @Overridepublic
        long
            next (){returndelegate.
        next
    (

    );
    } }@Test
    void
        singleWithDefaultMustAutoCloseIterator ( ) { AtomicIntegercounter=new
        AtomicInteger ( ) ; CountingPrimitiveLongIteratorResourceitr
                =newCountingPrimitiveLongIteratorResource( PrimitiveLongCollections .iterator ( 13)
        ,counter );assertEquals( PrimitiveLongCollections. single (itr , 2)
        ,13 ); assertEquals(1,counter .get
    (

    ))
    ; }@Test
    void
        singleWithDefaultMustAutoCloseEmptyIterator ( ) { AtomicIntegercounter=new
        AtomicInteger ( ) ; CountingPrimitiveLongIteratorResourceitr
                =newCountingPrimitiveLongIteratorResource(PrimitiveLongCollections. emptyIterator ()
        ,counter );assertEquals( PrimitiveLongCollections. single (itr , 2)
        ,2 ); assertEquals(1,counter .get
    (

    ))
    ; }@Test
    void
        indexOf
        ( ) { // GIVENPrimitiveLongIterable items =()-> PrimitiveLongCollections. iterator( 10 ,20

        ,
        30) ;// THENassertEquals (-1, PrimitiveLongCollections.indexOf(items. iterator ( ),
        55) ); assertEquals(0, PrimitiveLongCollections.indexOf(items. iterator ( ),
        10) ); assertEquals(1, PrimitiveLongCollections.indexOf(items. iterator ( ),
        20) ); assertEquals(2, PrimitiveLongCollections.indexOf(items. iterator ( ),
    30

    ))
    ; }@Test
    void
        iteratorAsSet
        ( ) { // GIVENPrimitiveLongIteratoritems= PrimitiveLongCollections. iterator( 1 ,2

        ,
        3 ) ; // WHENPrimitiveLongSetset= PrimitiveLongCollections .asSet

        (
        items) ;// THENassertTrue( set . contains(
        1) );assertTrue( set . contains(
        2) );assertTrue( set . contains(
        3) );assertFalse( set . contains(
    4

    ))
    ; }@Test
    void
        count
        ( ) { // GIVENPrimitiveLongIteratoritems= PrimitiveLongCollections. iterator( 1 ,2

        ,
        3 ) ; // WHENintcount= PrimitiveLongCollections .count

        (
        items) ;// THEN assertEquals (3
    ,

    count)
    ; }@Test
    void
        asArray
        ( ) { // GIVENPrimitiveLongIteratoritems= PrimitiveLongCollections. iterator( 1 ,2

        ,
        3); // WHEN long []array= PrimitiveLongCollections .asArray

        (
        items) ;// THENassertTrue( Arrays .equals(newlong[ ]{ 1,2 , 3 },
    array

    ))
    ; }@Test
    void
        shouldDeduplicate
        (){ // GIVEN long [ ]array=newlong[ ]{ 1L, 1L, 2L, 5L,6L

        ,
        6L}; // WHEN long []deduped= PrimitiveLongCollections .deduplicate

        (
        array) ; // THENassertArrayEquals(newlong[ ]{ 1L, 2L,5L , 6L}
    ,

    deduped)
    ; }@Test
    void
        shouldNotContinueToCallNextOnHasNextFalse
        ( ) { // GIVEN AtomicLongcount = newAtomicLong
        ( 2 ) ; PrimitiveLongIteratoriterator=
        new
            PrimitiveLongBaseIterator(
            ) { @Overrideprotected
            boolean
                fetchNext (){returncount . decrementAndGet ( )>= 0&&next(count .get
            (
        ))

        ;
        }} ;// WHEN/THENassertTrue(iterator .hasNext
        () );assertTrue(iterator .hasNext
        () ); assertEquals(1L,iterator .next
        () );assertTrue(iterator .hasNext
        () );assertTrue(iterator .hasNext
        () ); assertEquals(0L,iterator .next
        () );assertFalse(iterator .hasNext
        () );assertFalse(iterator .hasNext
        () );assertEquals (-1L,count .get
    (

    ))
    ; }@Test
    void
        copyPrimitiveSet ( ) {PrimitiveLongSetlongSet= PrimitiveLongCollections. setOf( 1L ,3L
        , 5L ) ;PrimitiveLongSetcopySet= PrimitiveLongCollections .asSet
        (longSet ); assertNotSame (copySet

        ,longSet );assertTrue( copySet . contains(
        1L) );assertTrue( copySet . contains(
        3L) );assertTrue( copySet . contains(
        5L) ); assertEquals(3,copySet .size
    (

    ))
    ; }@Test
    void
        convertJavaCollectionToSetOfPrimitives(){ List < Long> longs= asList( 1L ,4L
        , 7L ) ;PrimitiveLongSetlongSet= PrimitiveLongCollections .asSet
        (longs );assertTrue( longSet . contains(
        1L) );assertTrue( longSet . contains(
        4L) );assertTrue( longSet . contains(
        7L) ); assertEquals(3,longSet .size
    (

    ))
    ; }@Test
    void
        convertPrimitiveSetToJavaSet ( ) {PrimitiveLongSetlongSet= PrimitiveLongCollections. setOf( 1L ,3L
        ,5L); Set < Long>longs= PrimitiveLongCollections .toSet
        (longSet ); assertThat( longs, containsInAnyOrder( 1L , 3L,
    5L

    ))
    ; }@Test
    void
        copyMap(){ PrimitiveLongObjectMap < Object>originalMap=Primitive.
        longObjectMap(); originalMap. put (1L
        ,"a"); originalMap. put (2L
        ,"b"); originalMap. put (3L
        ,"c"); PrimitiveLongObjectMap < Object>copyMap= PrimitiveLongCollections .copy
        (originalMap ); assertNotSame (originalMap
        ,copyMap ); assertEquals(3,copyMap .size
        () ); assertEquals("a", copyMap . get(
        1L) ); assertEquals("b", copyMap . get(
        2L) ); assertEquals("c", copyMap . get(
    3L

    ) ) ; }private static void assertNoMoreItems
    (
        PrimitiveLongIteratoriterator ){assertFalse(iterator. hasNext ( ) ,iterator
        +" should have no more items" );assertThrows( NoSuchElementException.class ,iterator
    ::

    next ) ; }private static voidassertNextEquals ( long expected
    ,
        PrimitiveLongIteratoriterator ){assertTrue(iterator. hasNext ( ) ,iterator
        +" should have had more items" ); assertEquals(expected,iterator .next
    (

    ) ) ; }private static voidassertItems (PrimitiveLongIterator iterator ,
    long
        ... expectedItems ) {for ( long
        expectedItem
            :expectedItems ){ assertNextEquals (expectedItem
        ,
        iterator) ; }assertNoMoreItems
    (
iterator
