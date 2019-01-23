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
package org.neo4j.index.internal.gbptree;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.io.IOException;import
java .lang.reflect.Array
; importjava.util.BitSet
; importjava.util.Comparator
; importjava.util.Map
; importjava.util.Random

; importjava.util.TreeMap;import
org .neo4j.cursor.RawCursor;importorg.
neo4j .io.pagecache.IOLimiter;importorg.
neo4j .io.pagecache.PageCache;importorg.
neo4j .test.rule.PageCacheRule;importorg.
neo4j .test.rule.RandomRule;importorg.
neo4j .test.rule.TestDirectory;importorg.neo4j.

test . rule.fs.DefaultFileSystemRule;importstatic
java . lang.Integer.max;importstatic
org . junit.Assert.assertEquals;importstatic
org . junit.Assert.assertFalse;importstatic
org . junit.Assert.assertTrue;importstatic
org . junit.Assert.fail;importstaticorg.
junit . rules.RuleChain.outerRule;importstaticorg.neo4j.

test . rule .PageCacheRule.config;public
abstract
    class GBPTreeITBase < KEY , VALUE >{privatefinal
    DefaultFileSystemRule fs = new DefaultFileSystemRule ();private finalTestDirectorydirectory= TestDirectory.testDirectory(getClass ()
    , fs . get ( ) );privatefinal
    PageCacheRule pageCacheRule = new PageCacheRule ();final

    RandomRulerandom
    = new RandomRule ( ) ;@ Rule publicfinalRuleChainrules = outerRule(fs) . around(directory) . around(

    pageCacheRule ).around(random) ;private
    TestLayout <KEY,VALUE>layout ;private

    GBPTree <KEY,VALUE>index ;privateGBPTree
            < KEY
    ,
        VALUE
        > createIndex () throws IOException{
        // some random padding layout = getLayout(random) ;PageCachepageCache=pageCacheRule. getPageCache(fs.get( ) ,config() . withPageSize (512
        ) . withAccessChecks ( true)); returnindex =newGBPTreeBuilder< > (pageCache , directory.file("index")
    ,

    layout ).build(); }abstract TestLayout < KEY,

    VALUE >getLayout(RandomRule random);abstract

    Class<
    KEY > getKeyClass() ; @
    Test
        public
        void shouldStayCorrectAfterRandomModifications ()throwsException{// GIVEN try ( GBPTree<KEY ,
        VALUE
            >index=createIndex ( ) ){
            Comparator<KEY>keyComparator= layout ; Map <KEY,VALUE > data=
            new TreeMap < >(
            keyComparator ) ; intcount
            = 100 ; int totalNumberOfRounds =10 ; for (int i= 0
            ;
                i<count; i++ ){data.put (randomKey (random .random()) , randomValue(
            random

            .
            random ( )));}// WHEN try ( Writer<KEY,VALUE >
            writer
                = index .writer()){for( Map . Entry<KEY,VALUE >
                entry
                    :data.entrySet ()){writer. put(entry.getKey ()
                ,
            entry

            . getValue ( ) ) ;} } for (int round= 0
            ;
                round
                < totalNumberOfRounds ; round ++ ){ // THEN for (int i= 0
                ;
                    i < count ;i ++){KEYfirst =randomKey
                    ( random . random( ));KEYsecond =randomKey
                    ( random.
                    random ()
                    ) ; KEYfrom;KEY to ; if (layout.keySeed ( first )
                    <
                        layout . keySeed(
                        second ) ){
                    from
                    =
                    first
                        ; to =second
                        ; } else{
                    from
                    =second;to=first ; } Map< KEY, VALUE> expectedHits= expectedHits (data
                    , from ,to,keyComparator);try(RawCursor<Hit < KEY ,VALUE>, IOException> result = index
                    .
                        seek ( from,to)) {
                        while
                            ( result . next()){KEYkey=result.
                            get ( ).key( ) ; if ( expectedHits
                            .
                                remove( key ) == null ) { fail ( "Unexpected hit " + key +" when searching for "
                            +

                            from+ " - "+to) ;} assertTrue ( keyComparator . compare(
                            key , from)>=0 ); if ( keyComparator . compare
                            (
                                from, to)!=0 ){ assertTrue ( keyComparator . compare(
                            key
                        ,
                        to ) <0);}} if
                        (
                            !expectedHits . isEmpty ( )
                                    ) { fail ( "There were results which were expected to be returned, but weren't:" + expectedHits +" when searching range "
                        +
                    from
                +

                " - "+to) ;}} }index
                .checkpoint (IOLimiter .UNLIMITED );randomlyModifyIndex(index, data,random . random ( ),
            (

            double
            )round/totalNumberOfRounds);
        }
    // and finally

    index.
    consistencyCheck ( );} } @
    Test
        public
        void shouldHandleRemoveEntireTree ()throwsException{// given try ( GBPTree<KEY ,
        VALUE
            > index = createIndex(
            ) ) {intnumberOfNodes=200_000; try ( Writer<KEY,VALUE >
            writer
                = index . writer ( )) { for (int i= 0
                ;
                    i<numberOfNodes; i++ ) {writer .put ( key (i
                )
            ,

            value
            ( i ) ) ;}}// when
            BitSet removed =newBitSet(); try ( Writer<KEY,VALUE >
            writer
                = index . writer ( )) { for ( int i = 0; i< numberOfNodes
                -
                    numberOfNodes /10
                    ;
                    i
                        ++ ) {intcandidate; do{ candidate= random.nextInt( max ( 1 ,random
                    .
                    nextInt ( numberOfNodes))) ; } while(
                    removed.get( candidate ))

                    ;removed.set (candidate ) ; writer.
                remove
            (

            key ( candidate ))
            ; } }intnext=0; try ( Writer<KEY,VALUE >
            writer
                = index . writer ( )) { for ( int i= 0; i
                <
                    numberOfNodes / 10;i++ ) {next
                    =removed.nextClearBit ( next)
                    ;removed.set (next ) ; writer.
                remove
            (

            key
            ( next ));}}// thentry(RawCursor<Hit < KEY ,VALUE>, IOException> seek =index .seek ( key ( 0
            )
                ,key (numberOfNodes))) {assertFalse
            (

            seek
            .next());
        }
    // and finally

    index
    .consistencyCheck( ) ; } }
    // Timeout because test verify no infinite loop @ Test(timeout = 10_000L
    )
        publicvoidshouldHandleDescendingWithEmptyRange ( ) throws IOException{long[]seeds =new long[]
        { 0 ,1,4}; try ( GBPTree<KEY ,
        VALUE
            >
            index = createIndex()) {// Write try ( Writer<KEY,VALUE >
            writer
                = index . writer ( ) )
                {
                    for ( long seed:seeds) { KEYkey
                    = layout . key(seed) ; VALUEvalue
                    =layout.value (0 ) ;writer
                .
            put

            ( key , value);} } KEYfrom
            = layout . key(3) ; KEYto
            = layout .key(1);try(RawCursor <Hit < KEY ,VALUE>, IOException> seek = index
            .
                seek( from,to)) {assertFalse
            (
            seek.next( )); }index
        .
    checkpoint

    ( IOLimiter .UNLIMITED );}}privatevoid randomlyModifyIndex( GBPTree<KEY,VALUE> index, Map <KEY , VALUE >
            data ,
    Random
        random , double removeProbability)throwsIOException { int changeCount =random
        . nextInt (10)+10; try ( Writer<KEY,VALUE >
        writer
            = index . writer ( )) { for (int i= 0
            ;
                i < changeCount;i++) { if ( random.nextDouble() < removeProbability &&
                data   .
                    size ( ) >0 ){ // remove KEYkey
                    = randomKey ( data,random) ; VALUEvalue
                    = data . remove(key) ; VALUEremovedValue
                    =writer .remove ( key)
                ;
                assertEqualsValue
                (   value
                    , removedValue ) ;} else {// put
                    KEY key = randomKey( random );
                    VALUEvalue=randomValue (random ) ;writer
                    .put(key ,value ) ;data
                .
            put
        (
    key

    , value);}}} }private Map<KEY,VALUE> expectedHits( Map <KEY , VALUE> data,KEYfrom , KEY
    to
        ,Comparator<KEY>comparator ) { Map <KEY,VALUE > hits=
        new TreeMap <>(comparator);for( Map . Entry<KEY,VALUE >
        candidate
            : data .entrySet() ){ if ( comparator . compare (from,to )==0&&comparator. compare ( candidate . getKey
            (
                ),from) ==0){hits. put(candidate.getKey ()
            ,
            candidate . getValue ()); }elseif(comparator. compare ( candidate . getKey
                    (),from )>=0&&comparator. compare ( candidate . getKey
            (
                ),to) <0){hits. put(candidate.getKey ()
            ,
        candidate
        . getValue(
    )

    ) ; }} returnhits;}privateKEY randomKey( Map < KEY
    ,
        VALUE
        >data, Random random ){//noinspection uncheckedKEY[]keys= data.keySet() .toArray(( KEY[]) Array.newInstance(getKeyClass ( ),
        data .size())) ;returnkeys [random.
    nextInt

    ( keys .length ) ] ;
    }
        private KEYrandomKey (Randomrandom) { return key(
    random

    . nextInt (1_000 ) ) ;
    }
        private VALUErandomValue (Randomrandom) { return value(
    random

    . nextInt (1_000 ) ) ;
    }
        private VALUEvalue(long seed ){
    return

    layout . value( seed ) ;
    }
        private KEYkey(long seed ){
    return

    layout . key( seed ); } private void
    assertEqualsValue
        (VALUE expected,VALUEactual ){ assertEquals(String.format( "expected equal, expected=%s, actual=%s",expected.toString () ,actual
                .toString() ), 0 , layout.
    compareValue

    (
    expected,actual ) )
    ; } // KEEP even if unused@SuppressWarnings ( "unused"
    )
        privatevoidprintTree( )throws IOException{ index. printTree (false
    ,

    false,false , false
    ) ; }@ SuppressWarnings("unused" ) private void printNode ( @ SuppressWarnings
    (
        "SameParameterValue")intid ) throwsIOException
    {
index
