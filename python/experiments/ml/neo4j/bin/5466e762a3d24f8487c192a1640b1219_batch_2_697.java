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
package org.neo4j.unsafe.impl.batchimport.cache.idmapping.string;

import org.eclipse.collections.api.stack.primitive.MutableLongStack;
import org.eclipse.collections.impl.stack.mutable.primitive.LongArrayStack;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import org.neo4j.helpers.progress.ProgressListener;
import org.neo4j.unsafe.impl.batchimport.Utils;
import org.neo4j.unsafe.impl.batchimport.Utils.CompareType;
import org.neo4j.unsafe.impl.batchimport.cache.LongArray;

import static org.neo4j.helpers.Numbers.safeCastLongToInt;
import static org.neo4j.unsafe.impl.batchimport.cache.idmapping.string.EncodingIdMapper.clearCollision;

/**
 * Sorts input data by dividing up into chunks and sort each chunk in parallel. Each chunk is sorted
 * using a quick sort method, whereas the dividing of the data is first sorted using radix sort.
 */
public class ParallelSort
{
    private final int[] radixIndexCount;
    private final RadixCalculator radixCalculator;
    private final LongArray dataCache;
    private final long highestSetIndex;
    private final Tracker tracker;
    private final int threads;
    private long [ ][

    ] sortBuckets; private finalProgressListener progress ;private final Comparatorcomparator
            ; publicParallelSort ( Radixradix , LongArraydataCache , long highestSetIndex
    ,
        Trackertracker, int threads,
        ProgressListenerprogress, Comparator comparator)
        {this. progress =progress;this.comparator
        =comparator; this .radixIndexCount=radix.getRadixIndexCounts
        (); this .radixCalculator
        =radix. calculator ()
        ;this. dataCache =dataCache
        ;this. highestSetIndex =highestSetIndex
    ;

    this . tracker=tracker;this .threads= threads ;
    }
        publicsynchronizedlong[] [ ] run()throws
        InterruptedException { long []
        [ ] sortParams = sortRadix () ; int threadsNeeded= 0; for
        (
            int i =0;i<threads; i ++ )
            {
                if(
            sortParams
            [i]
        [

        1]==0 ) { break ;}threadsNeeded++ ; }Workers
        <SortWorker>sortWorkers = newWorkers
        < > ( "SortWorker" ) ;progress . started ("SORT" ); for
        (
            int i =0;i<threadsNeeded; i ++ )
            {
                if(
            sortParams
            [i][ 1 ]== 0){break;}sortWorkers. start(newSortWorker(sortParams[ i ][
        0
        ]
        ,
            sortParams[i][1
        ]
        )
        )
            ;}try{sortWorkers.
        awaitAndThrowOnError
        ( );
    }

    finally {progress.done( );} return sortBuckets
    ;
        }privatelong[] [ ] sortRadix ()throwsInterruptedException{long[]
        []rangeParams = new long [threads][2
        ];int[ ] bucketRange = newint[threads ] ;Workers
        < TrackerInitializer > initializers=newWorkers<>("TrackerInitializer"
        ) ; sortBuckets = new long[
        threads ] [ 2 ] ;long
        dataSize = highestSetIndex +1
        ; long bucketSize =dataSize
        /threads;long count =0
        ; long fullCount = 0 ;progress . started ("SPLIT" ) ; for(int i = 0 ,threadIndex =0 ;
        i
            < radixIndexCount .length && threadIndex<threads;i ++ ) {
            if
                ((count+ radixIndexCount [ i ] ) > bucketSize ) { bucketRange[
                threadIndex]=count==0? i :i
                - 1 ; rangeParams [ threadIndex
                ]
                    [0]=fullCount;if ( count!=
                    0 ) {rangeParams
                    [threadIndex][ 1 ]=
                    count ; fullCount+=count;progress
                .
                add
                (
                    count);count=radixIndexCount[ i ];}else{
                    rangeParams [ threadIndex][1]
                    =radixIndexCount[i ];fullCount+= radixIndexCount[
                i
                ];progress. add (radixIndexCount [i ]);}initializers
                        . start ( new TrackerInitializer(threadIndex , rangeParams[ threadIndex ],threadIndex >0?bucketRange[
                        threadIndex-1] : -1
                ,bucketRange[
            threadIndex
            ]
            ,
                sortBuckets [ threadIndex]));
            threadIndex
            ++ ; } else { count += radixIndexCount [ i ];} if ( threadIndex
            ==
                threads-1|| i ==radixIndexCount.length
                -1){bucketRange[threadIndex ] =radixIndexCount
                .length;rangeParams[threadIndex] [ 0 ] =fullCount
                ;rangeParams[threadIndex ] [1 ]= dataSize-fullCount;initializers
                        . start ( new TrackerInitializer(threadIndex , rangeParams[ threadIndex ],threadIndex >0?bucketRange[
                        threadIndex-1] : -1
                ,bucketRange
            [
        threadIndex
        ],sortBuckets[threadIndex]

        )
        )
        ;
        break ; } }progress.done()
        ;// In the loop above where we split up radixes into buckets, we start one thread per bucket whose// job is to populate trackerCache and sortBuckets where each thread will not touch the same // data indexes as any other thread. Here we wait for them all to finish. Throwable error =initializers.await(
        ) ; long []
        bucketIndex = new long [ threads ]
        ;
            inti=0; for (TrackerInitializerinitializer:
        initializers
        ) { bucketIndex [ i ++
        ]
            = initializer .bucketIndex ;}if(error != null ) {throw newAssertionError (error . getMessage(
                    ) +"\n"
        +
        dumpBuckets (rangeParams
    ,

    bucketRange , bucketIndex) ,error);} returnrangeParams ;}private StringdumpBuckets (long[ ] [
    ]
        rangeParams , int [ ]bucketRange,long
        []bucketIndex) { StringBuilderbuilder
        = new StringBuilder() ; builder . append
        (
            "rangeParams:\n");for ( long[]range :rangeParams){ builder . append("  ") . append(
        Arrays
        .toString(range ) ).
        append ( "\n" ) ; } builder
        .
            append("bucketRange:\n") ; for(intrange : bucketRange){builder . append(
        "  "
        ).append( range ).
        append ( "\n" ) ; } builder
        .
            append("bucketIndex:\n") ; for(longindex : bucketIndex){builder . append(
        "  "
        ) .append(index).
    append

    (
    "\n" ) ;
    }
        return
        builder .toString ( ); } /**
     * Pluggable comparator for the comparisons that quick-sort needs in order to function.
     */ publicinterface

        Comparator
        { /**
         * @return {@code true} if {@code left} is less than {@code pivot}.
         */boolean lt (long left , longpivot

        )
        ; /**
         * @return {@code true} if {@code right} is greater than or equal to {@code pivot}.
         */boolean ge ( longright
    ,

    long pivot ) ; /**
         * @param dataValue the data value in the used dataCache for a given tracker index.
         * @return actual data value given the data value retrieved from the dataCache at a given index.
         * This is exposed to be able to introduce an indirection while preparing the tracker indexes
         * just like the other methods on this interface does.
         */ long dataValue (longdataValue
    )
        ;}
        public static finalComparator DEFAULT =new Comparator ( )
        {
            @ Overridepublicbooleanlt (long left, longpivot) {return
        Utils

        .unsignedCompare
        ( left ,pivot , CompareType. LT ) ;
        }
            @ Overridepublicbooleange (long right, longpivot) {return
        Utils

        .unsignedCompare
        ( right ,pivot , CompareType .
        GE
            ) ;}
        @
    Overridepublic

    long
    dataValue ( long dataValue )
    {
        return dataValue ; }}
        ; /**
     * Sorts a part of data in dataCache covered by trackerCache. Values in data cache doesn't change location,
     * instead trackerCache is updated to point to the right indexes. Only touches a designated part of trackerCache
     * so that many can run in parallel on their own part without synchronization.
     */ private classSortWorker
        implements Runnable {private
        final long start;private final long size ;privateintthreadLocalProgress;
        private final long [ ] pivotChoice=newlong[10

        ]; private finalThreadLocalRandom random = ThreadLocalRandom
        .
            current() ; SortWorker(
            longstartRange, long size)
        {

        this .start = startRange ;
        this
            . size =size
            ; } void incrementProgress ( long diff
            )   {
                threadLocalProgress+=diff;
            if
        (

        threadLocalProgress >= 10_000/*reasonably big to dwarf passing a memory barrier*/)
        {
            // Update the total progressreportProgress() ; }}
            private void reportProgress(
        )

        {progress
        . add (threadLocalProgress)
        ;
            threadLocalProgress= 0; } @ Override publicvoid
            run(){
        qsort

        ( start ,start + size) ; reportProgress( ) ; }
        private
            long partition ( longleftIndex
            , long rightIndex , long pivotIndex)
            { long li =leftIndex
            ; long ri =rightIndex -2;long pi=pivotIndex; long pivot = clearCollision(
            dataCache
            .get(tracker .get ( pi ) ))
            ; // save pivot in last index tracker .swap (pi,rightIndex -1); long left = clearCollision(
            dataCache . get (tracker .get(li ))); long right = clearCollision(
            dataCache . get ( tracker .
            get
                ( ri ))); while( li < ri
                )   {
                    if ( comparator. lt(left, pivot)){ // this value is on the correct side of the pivot, moving onleft = clearCollision (dataCache
                .
                get ( tracker .get(++ li) ) ) ;
                }   else
                    if ( comparator. ge(right, pivot)){ // this value is on the correct side of the pivot, moving onright = clearCollision (dataCache
                .
                get
                (   tracker
                    .get(-- ri) ) );
                    } else { // this value is on the wrong side of the pivot, swappingtracker
                    . swap (li
                    , ri );
                long
            temp
            = left ; left=
            right ; right=temp; }} long partingIndex =
            ri
                ;if(
            comparator
            .
            lt(right, pivot ) ){ partingIndex ++;
            } // restore pivottracker
        .

        swap ( rightIndex- 1 ,partingIndex ) ; return
        partingIndex
            ; } private void qsort (longinitialStart,
            longinitialEnd){ final MutableLongStackstack
            =newLongArrayStack( ) ;stack
            . push (initialStart);stack. push
            (
                initialEnd ) ; while(!stack. isEmpty () ) {longend=stack.
                isEmpty ( ) ?-1:stack . pop( ) ;longstart=stack.
                isEmpty ( ) ? - 1:
                stack . pop ( ) ;
                long
                    diff= end -start
                    ;if
                (

                diff< 2 ){

                incrementProgress
                ( 2 ) ; continue ;}incrementProgress( 1 );
                // choose a random pivot between start and end long pivot= start+ random. nextLong (diff

                )
                ; pivot =informedPivot (start ,end , pivot)
                ; // partition, given that pivot pivot = partition (
                start   ,
                    end,pivot) ; if(
                    pivot>start) { // there are elements to left of pivotstack
                .
                push ( start ) ; stack . push
                (   pivot
                    );}if ( pivot + 1<
                    end){// there are elements to right of pivot stack .push
                (
            pivot
        +

        1 ) ;stack . push( end ); } } }
        private
            long informedPivot ( long start , longend, long
            randomIndex
                ) {if
            (

            end - start <pivotChoice.length ){ return randomIndex ; }long
            low = Math .max(start , randomIndex -5 ) ;long
            high = Math .min ( low + 10,

            end ) ; intlength
            = safeCastLongToInt ( high - low) ; int j= 0;for (long i
            =
                low;i< high ;i ++,j++ ){pivotChoice[ j ] = clearCollision(
            dataCache
            .get(tracker .get (i ) ))

            ; } Arrays .sort( pivotChoice ,0,
            length ) ; long middle =pivotChoice [ length /2 ]; for
            (
                long i =low ;i<=high ;i++) { if ( clearCollision ( dataCache .
                get
                    ( tracker.
                get
            (
            i ) )) == middle)
        {
    return

    i
    ; } } throw new
    IllegalStateException
        ( "The middle value somehow disappeared in front of our eyes" );} }/**
     * Sets the initial tracker indexes pointing to data indexes. Only touches a designated part of trackerCache
     * so that many can run in parallel on their own part without synchronization.
     */
        private class TrackerInitializer implementsRunnable
        { private final long[
        ] rangeParams ; privatefinal
        int lowRadixRange ;private
        final int highRadixRange;private finalint

        threadIndex; private longbucketIndex ;privatefinal long[ ] result; TrackerInitializer (int
                threadIndex,long [ ]
        rangeParams
            ,intlowRadixRange , inthighRadixRange
            ,long[ ] result)
            {this. threadIndex =threadIndex
            ;this. rangeParams =rangeParams
            ;this. lowRadixRange =lowRadixRange
        ;

        this.
        highRadixRange = highRadixRange;this
        .
            result = result ; } @Override public void run( ){ for
            (
                long i = 0;i<= highestSetIndex;i++ ){intrIndex = radixCalculator . radixOf(
                comparator . dataValue ( dataCache . get ( i )
                )
                    ) ; if (rIndex>lowRadixRange && rIndex<=highRadixRange
                    ) {longtrackerIndex= rangeParams [ 0 ]+ bucketIndex
                            ++ ; assert tracker . get ( trackerIndex ) == -1
                    :"Overlapping buckets i:"+i +", k:" + threadIndex+
                    ", index:" + trackerIndex ; tracker.set( trackerIndex
                    ,
                        i);if ( bucketIndex==
                        rangeParams[1] ) {result[0]
                    =
                highRadixRange
            ;
        result
    [
1
