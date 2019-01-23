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
import static org.junit.jupiter.api.Assertions.assertArrayEquals;importstatic
org . junit.jupiter.api.Assertions.assertEquals;importstatic
org . junit.jupiter.api.Assertions.assertFalse;importstatic
org . junit.jupiter.api.Assertions.assertNotSame;importstatic
org . junit.jupiter.api.Assertions.assertThrows;importstatic
org . junit.jupiter.api.Assertions.assertTrue;classPrimitiveLongCollectionsTest

{ @
Test
    voidarrayOfItemsAsIterator
    ( ){// GIVEN
    long
        [
        ]items= new long [ ]{2,5, 234} ;// WHENPrimitiveLongIterator

        iterator
        = PrimitiveLongCollections . iterator(items) ; // THENassertItems

        (
        iterator, items) ; }@
    Test

    voidfilter
    ( ){// GIVEN
    PrimitiveLongIterator
        items
        = PrimitiveLongCollections . iterator(1, 2, 3) ; // WHENPrimitiveLongIterator

        filtered
        = PrimitiveLongCollections . filter(items, item-> item != 2 ) ; // THENassertItems

        (
        filtered, 1, 3) ; }private
    static

    final class CountingPrimitiveLongIteratorResource implements PrimitiveLongIterator , AutoCloseable{ private
    final
        PrimitiveLongIterator delegate ; privatefinal
        AtomicInteger closeCounter ; privateCountingPrimitiveLongIteratorResource

        ( PrimitiveLongIteratordelegate , AtomicIntegercloseCounter ) { this
        .
            delegate=delegate ; this.
            closeCounter=closeCounter ; }@
        Override

        publicvoid
        close ( ){closeCounter
        .
            incrementAndGet();}@
        Override

        publicboolean
        hasNext ( ){return
        delegate
            . hasNext();}@
        Override

        publiclong
        next ( ){return
        delegate
            . next();}}
        @
    Test

    voidsingleWithDefaultMustAutoCloseIterator
    ( ){AtomicInteger
    counter
        = new AtomicInteger ( );CountingPrimitiveLongIteratorResourceitr
        = new CountingPrimitiveLongIteratorResource ( PrimitiveLongCollections.
                iterator(13) , counter) ; assertEquals(
        PrimitiveLongCollections. single(itr, 2) , 13) ; assertEquals(
        1, counter. get()); }@
    Test

    voidsingleWithDefaultMustAutoCloseEmptyIterator
    ( ){AtomicInteger
    counter
        = new AtomicInteger ( );CountingPrimitiveLongIteratorResourceitr
        = new CountingPrimitiveLongIteratorResource ( PrimitiveLongCollections.
                emptyIterator(),counter) ; assertEquals(
        PrimitiveLongCollections. single(itr, 2) , 2) ; assertEquals(
        1, counter. get()); }@
    Test

    voidindexOf
    ( ){// GIVEN
    PrimitiveLongIterable
        items
        = ( ) ->PrimitiveLongCollections . iterator(10, 20, 30) ; // THENassertEquals

        (
        -1 ,PrimitiveLongCollections. indexOf(items. iterator(),55) ) ; assertEquals(
        0, PrimitiveLongCollections. indexOf(items. iterator(),10) ) ; assertEquals(
        1, PrimitiveLongCollections. indexOf(items. iterator(),20) ) ; assertEquals(
        2, PrimitiveLongCollections. indexOf(items. iterator(),30) ) ; }@
    Test

    voiditeratorAsSet
    ( ){// GIVEN
    PrimitiveLongIterator
        items
        = PrimitiveLongCollections . iterator(1, 2, 3) ; // WHENPrimitiveLongSet

        set
        = PrimitiveLongCollections . asSet(items) ; // THENassertTrue

        (
        set. contains(1) ) ; assertTrue(
        set. contains(2) ) ; assertTrue(
        set. contains(3) ) ; assertFalse(
        set. contains(4) ) ; }@
    Test

    voidcount
    ( ){// GIVEN
    PrimitiveLongIterator
        items
        = PrimitiveLongCollections . iterator(1, 2, 3) ; // WHENint

        count
        = PrimitiveLongCollections . count(items) ; // THENassertEquals

        (
        3, count) ; }@
    Test

    voidasArray
    ( ){// GIVEN
    PrimitiveLongIterator
        items
        = PrimitiveLongCollections . iterator(1, 2, 3) ; // WHENlong

        [
        ]array= PrimitiveLongCollections . asArray(items) ; // THENassertTrue

        (
        Arrays. equals(newlong [ ]{1,2, 3} ,array) ) ; }@
    Test

    voidshouldDeduplicate
    ( ){// GIVEN
    long
        [
        ]array= new long [ ]{1L,1L, 2L, 5L, 6L, 6L} ;// WHENlong

        [
        ]deduped= PrimitiveLongCollections . deduplicate(array) ; // THENassertArrayEquals

        (
        newlong [ ]{1L,2L, 5L, 6L} ,deduped) ; }@
    Test

    voidshouldNotContinueToCallNextOnHasNextFalse
    ( ){// GIVEN
    AtomicLong
        count
        = new AtomicLong ( 2) ; PrimitiveLongIteratoriterator
        = new PrimitiveLongBaseIterator ( ){@
        Override
            protectedboolean
            fetchNext ( ){return
            count
                . decrementAndGet()>=0 && next ( count. get()); }}
            ;
        // WHEN/THENassertTrue

        (
        iterator. hasNext()); assertTrue(
        iterator. hasNext()); assertEquals(
        1L, iterator. next()); assertTrue(
        iterator. hasNext()); assertTrue(
        iterator. hasNext()); assertEquals(
        0L, iterator. next()); assertFalse(
        iterator. hasNext()); assertFalse(
        iterator. hasNext()); assertEquals(
        -1L ,count. get()); }@
    Test

    voidcopyPrimitiveSet
    ( ){PrimitiveLongSet
    longSet
        = PrimitiveLongCollections . setOf(1L, 3L, 5L) ; PrimitiveLongSetcopySet
        = PrimitiveLongCollections . asSet(longSet) ; assertNotSame(
        copySet, longSet) ; assertTrue(

        copySet. contains(1L) ) ; assertTrue(
        copySet. contains(3L) ) ; assertTrue(
        copySet. contains(5L) ) ; assertEquals(
        3, copySet. size()); }@
    Test

    voidconvertJavaCollectionToSetOfPrimitives
    ( ){List
    <
        Long>longs= asList ( 1L, 4L, 7L) ; PrimitiveLongSetlongSet
        = PrimitiveLongCollections . asSet(longs) ; assertTrue(
        longSet. contains(1L) ) ; assertTrue(
        longSet. contains(4L) ) ; assertTrue(
        longSet. contains(7L) ) ; assertEquals(
        3, longSet. size()); }@
    Test

    voidconvertPrimitiveSetToJavaSet
    ( ){PrimitiveLongSet
    longSet
        = PrimitiveLongCollections . setOf(1L, 3L, 5L) ; Set<
        Long>longs= PrimitiveLongCollections . toSet(longSet) ; assertThat(
        longs, containsInAnyOrder( 1L, 3L, 5L) ) ; }@
    Test

    voidcopyMap
    ( ){PrimitiveLongObjectMap
    <
        Object>originalMap= Primitive . longObjectMap();originalMap.
        put(1L, "a") ; originalMap.
        put(2L, "b") ; originalMap.
        put(3L, "c") ; PrimitiveLongObjectMap<
        Object>copyMap= PrimitiveLongCollections . copy(originalMap) ; assertNotSame(
        originalMap, copyMap) ; assertEquals(
        3, copyMap. size()); assertEquals(
        "a", copyMap. get(1L) ) ; assertEquals(
        "b", copyMap. get(2L) ) ; assertEquals(
        "c", copyMap. get(3L) ) ; }private
    static

    void assertNoMoreItems ( PrimitiveLongIteratoriterator ) { assertFalse
    (
        iterator. hasNext(),iterator+ " should have no more items" ) ; assertThrows(
        NoSuchElementException. class,iterator:: next); }private
    static

    void assertNextEquals ( longexpected , PrimitiveLongIteratoriterator ) { assertTrue
    (
        iterator. hasNext(),iterator+ " should have had more items" ) ; assertEquals(
        expected, iterator. next()); }private
    static

    void assertItems ( PrimitiveLongIteratoriterator , long... expectedItems) { for
    (
        long expectedItem : expectedItems) { assertNextEquals
        (
            expectedItem, iterator) ; }assertNoMoreItems
        (
        iterator) ; }}
    