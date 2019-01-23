/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.apache.kylin.cube.inmemcubing2;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.RecursiveTask;

import org.apache.kylin.common.util.ByteArray;
import org.apache.kylin.common.util.Dictionary;
import org.apache.kylin.common.util.ImmutableBitSet;
import org.apache.kylin.cube.cuboid.CuboidScheduler;
import org.apache.kylin.cube.inmemcubing.AbstractInMemCubeBuilder;
import org.apache.kylin.cube.inmemcubing.CuboidResult;
import org.apache.kylin.cube.inmemcubing.ICuboidWriter;
import org.apache.kylin.cube.inmemcubing.InputConverterUnit;
import org.apache.kylin.cube.inmemcubing.RecordConsumeBlockingQueueController;
import org.apache.kylin.gridtable.GTRecord;
import org.apache.kylin.gridtable.GTScanRequestBuilder;
import org.apache.kylin.gridtable.GridTable;
import org.apache.kylin.gridtable.IGTScanner;
import org.apache.kylin.measure.MeasureAggregators;
import org.apache.kylin.metadata.model.IJoinedFlatTableDesc;
import org.apache.kylin.metadata.model.
TblColRef;importorg.slf4j
. Logger;importorg.slf4j

. LoggerFactory;importcom.google.common.base
. Stopwatch;importcom.google.common.collect
. Lists;importcom.google.common.collect
. Maps;importcom.google.common.collect

. Queues ; public class DoggedCubeBuilder2
    extends AbstractInMemCubeBuilder { private static Loggerlogger=LoggerFactory.getLogger(DoggedCubeBuilder2.

    class );public DoggedCubeBuilder2( CuboidScheduler cuboidScheduler,
            IJoinedFlatTableDescflatDesc,Map <TblColRef,Dictionary< String> >
        dictionaryMap){super (cuboidScheduler ,flatDesc,
    dictionaryMap

    );
    } @Overridepublic < T>voidbuild(BlockingQueue <T >input,InputConverterUnit <T > inputConverterUnit,
            ICuboidWriter output )
        throws IOException{newBuildOnce().build (input ,inputConverterUnit,
    output

    ) ; } private
        class BuildOnce{public < T>voidbuild(BlockingQueue <T >input,InputConverterUnit <T > inputConverterUnit,
                ICuboidWriter output )
            throws IOException{finalRecordConsumeBlockingQueueController < T >
                    inputController=RecordConsumeBlockingQueueController.getQueueController (inputConverterUnit,

            input );finalList < InMemCubeBuilder2 > builderList=newCopyOnWriteArrayList<>

            ( ) ; ForkJoinWorkerThreadFactory factory=new ForkJoinWorkerThreadFactory
                ()
                { @ OverridepublicForkJoinWorkerThread newThread( ForkJoinPool
                    pool ) { final ForkJoinWorkerThreadworker=ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(
                    pool);worker. setName ("dogged-cubing-cuboid-worker-"+worker.getPoolIndex(
                    ) );
                return
            worker;

            } } ; ForkJoinPool builderPool=newForkJoinPool (taskThreadCount ,factory ,null,
            true ) ; CuboidResultWatcher resultWatcher=newCuboidResultWatcher (builderList,

            output ) ; Stopwatch sw=newStopwatch
            ();sw.start
            ();logger.info(
            "Dogged Cube Build2 start" )
                ;try{BaseCuboidTask < T > task=newBaseCuboidTask<> (inputController ,1,
                resultWatcher);builderPool.execute(
                task )
                    ;do{builderList.add(task.getInternalBuilder(
                    )
                    );//Exception will be thrown here if cube building failuretask.join
                    ( ) ;task=task.nextTask
                ( ) ;} while (task!=

                null);logger.info(
                "Has finished feeding data, and base cuboid built, start to build child cuboids" ); for ( final InMemCubeBuilder2builder :
                    builderList){builderPool. submit(new Runnable
                        ()
                        { @ Overridepublicvoid run
                            (){builder.startBuildFromBaseCuboid
                        (
                    );}
                }
                );}resultWatcher.start
                ();logger. info ("Dogged Cube Build2 splits complete, took "+sw. elapsedMillis ()+
            " ms" ) ;} catch( Throwable
                e){logger.error ("Dogged Cube Build2 error",
                e ); if (e
                    instanceof Error)throw (Error
                ) e ;else if (e
                    instanceof RuntimeException)throw (RuntimeException
                )
                    e ; elsethrownewIOException(
            e ) ;
                }finally{output.close
                ();closeGirdTables(
                builderList);sw.stop
                ();builderPool.shutdownNow
                ();logger. info ("Dogged Cube Build2 end, totally took "+sw. elapsedMillis ()+
                " ms");logger.info(
            "Dogged Cube Build2 return"
        )

        ; } }privatevoidcloseGirdTables(List <InMemCubeBuilder2 >
            builderList ){ for ( InMemCubeBuilder2inMemCubeBuilder :
                builderList ){ for ( CuboidResultcuboidResult:inMemCubeBuilder.getResultCollector().getAllResult().values (
                    )){closeGirdTable(cuboidResult.
                table
            )
        ;

        } } }privatevoid closeGirdTable( GridTable
            gridTable )
                {try{gridTable.close
            ( ) ;} catch( Throwable
                e){logger. error ("Error closing grid table " +gridTable,
            e
        )
    ;

    } } }privateclassBaseCuboidTask < T>extendsRecursiveTask <
        CuboidResult > { private static final longserialVersionUID=

        - 5408592502260876799L ; privatefinal
        int splitSeq ; privatefinal

        ICuboidResultListener resultListener;privateRecordConsumeBlockingQueueController <T
        > inputController ;private

        InMemCubeBuilder2 builder ;privatevolatileBaseCuboidTask <T

        > next;public BaseCuboidTask(finalRecordConsumeBlockingQueueController <T > inputController,
                int splitSeq, ICuboidResultListener
            resultListener){ this .inputController
            =inputController; this .splitSeq
            =splitSeq; this .resultListener
            =resultListener; this . builder=newInMemCubeBuilder2 (cuboidScheduler ,flatDesc,
            dictionaryMap);builder.setReserveMemoryMB(
            reserveMemoryMB);builder.setConcurrentThreads(
            taskThreadCount);logger. info ( "Split #" +splitSeq+
        " kickoff"

        );
        } @ OverrideprotectedCuboidResult compute
            ( )
                { try { CuboidResultbaseCuboidResult=builder.buildBaseCuboid (inputController,
                resultListener );if(!inputController.ifEnd (
                    ) ) { next=newBaseCuboidTask<> ( inputController ,splitSeq +1,
                    resultListener);next.fork
                (
                );}logger. info ( "Split #" +splitSeq+
                " finished" );
            return baseCuboidResult ;} catch( IOException
                e ) {thrownewRuntimeException(
            e
        )

        ; } }publicInMemCubeBuilder2 getInternalBuilder
            ( ){
        return

        builder ;}publicBaseCuboidTask <T> nextTask
            ( ){
        return
    next

    ;
    } } /**
     * Class response for watch the cube building result, monitor the cube building process and trigger merge actions if required.
     *
     */ private class CuboidResultWatcher
        implements ICuboidResultListener{finalBlockingQueue <CuboidResult
        > outputQueue;finalMap <Long,List< CuboidResult > >pendingQueue=Maps.newHashMap
        ( );finalList <InMemCubeBuilder2
        > builderList ;final

        ICuboidWriter output;public CuboidResultWatcher(finalList <InMemCubeBuilder2 > builderList ,final ICuboidWriter
            output){ this .outputQueue=Queues.newLinkedBlockingQueue
            (); this .builderList
            =builderList; this .output
        =

        output ; }publicvoid start ( )
            throws IOException { SplitMerger merger=newSplitMerger
            ( );while (
                true ){if(!outputQueue.isEmpty (
                    )){List < CuboidResult >splitResultReturned=Lists.newArrayList
                    ();outputQueue.drainTo(
                    splitResultReturned ); for ( CuboidResultsplitResult :
                        splitResultReturned ){if(builderList. size () ==
                            1){merger.mergeAndOutput(Lists.newArrayList( splitResult),
                        output ) ;
                            }else{List < CuboidResult >cuboidResultList=pendingQueue.get(splitResult.
                            cuboidId ); if (cuboidResultList ==
                                null ) {cuboidResultList=Lists.newArrayListWithExpectedSize(builderList.size(
                                ));cuboidResultList.add(
                                splitResult);pendingQueue.put(splitResult .cuboidId,
                            cuboidResultList ) ;
                                }else{cuboidResultList.add(
                            splitResult
                            ) ;}if(cuboidResultList. size ()==builderList.size (
                                )){merger.mergeAndOutput (cuboidResultList,
                                output);pendingQueue.remove(splitResult.
                            cuboidId
                        )
                    ;
                }

                } } } booleanjobFinished=isAllBuildFinished
                ( );if(outputQueue. isEmpty ()&& !
                    jobFinished ) { booleanifWait
                    = true; for ( InMemCubeBuilder2builder :
                        builderList){Queue < CuboidTask >queue=builder.getCompletedTaskQueue
                        ( );while(queue. size () >
                            0 ) { CuboidTaskchildTask=queue.poll
                            ( );if(childTask.isCompletedAbnormally (
                                ) ) {thrownewRuntimeException(childTask.getException(
                            )
                            ) ; }ifWait
                        =
                    false
                    ; }}if (
                        ifWait )
                            {try{Thread.sleep(
                        100L ) ;} catch( InterruptedException
                            e ) {thrownewRuntimeException(
                        e
                    )
                ; } } }elseif(outputQueue. isEmpty ()&&pendingQueue. isEmpty () &&
                    jobFinished)
                {
            return
        ;

        } } }privateboolean isAllBuildFinished
            ( ){ for ( InMemCubeBuilder2split :
                builderList ){if(!split.isAllCuboidDone (
                    ) ){
                return
            false
            ; }}
        return

        true;
        } @ Overridepublicvoid finish( CuboidResult
            result ) { Stopwatch stopwatch=newStopwatch().start
            ( ) ; intnRetries
            = 0;while(!outputQueue.offer( result
                )){
                nRetries ++ ; longsleepTime=stopwatch.elapsedMillis
                ( ); if (sleepTime >
                    3600000L){stopwatch.stop
                    ( ) ;throw
                            new
                                    RuntimeException ("OutputQueue Full. Cannot offer to the output queue after waiting for one hour!!! Current queue size: "+outputQueue.size(
                )
                );}logger. warn ("OutputQueue Full. Queue size: "+outputQueue. size ( ) +
                        ". Total sleep time : " + sleepTime +", and retry count : "+
                nRetries )
                    ;try{Thread.sleep(
                5000L ) ;} catch( InterruptedException
                    e ) {thrownewRuntimeException(
                e
            )
            ;}}stopwatch.stop
        (
    )

    ; } } private
        class SplitMerger{
        MeasureAggregatorsreuseAggrs; Object[
        ] reuseMetricsArray;

        ByteArray reuseMetricsSpace;
        long lastCuboidColumnCount;

        ImmutableBitSetlastMetricsColumns; SplitMerger
            ( ) { reuseAggrs=newMeasureAggregators(cubeDesc.getMeasures(
            ) ) ; reuseMetricsArray=newObject[cubeDesc.getMeasures().size(
        )

        ] ; }publicvoidmergeAndOutput(List <CuboidResult > splitResultList, ICuboidWriter output )
            throws IOException{if(splitResultList. size () ==
                1 ) { CuboidResultcuboidResult=splitResultList.get(
                0);outputCuboid(cuboidResult .cuboidId,cuboidResult .table,
                output)
            ;
            return;}LinkedList < ResultMergeSlot >open=Lists.newLinkedList
            ( ); for ( CuboidResultsplitResult :
                splitResultList){open. add(newResultMergeSlot(splitResult
            )

            );}PriorityQueue < ResultMergeSlot > heap=newPriorityQueue<ResultMergeSlot>
            ( );while (
                true
                ) {// ready records in open slots and add to heapwhile(!open.isEmpty (
                    ) ) { ResultMergeSlotslot=open.removeFirst
                    ( );if(slot.fetchNext (
                        )){heap.add(
                    slot
                )

                ;
                } } // find the smallest on heap ResultMergeSlotsmallest=heap.poll
                ( ); if (smallest
                    ==null
                )break;open.add(

                smallest
                ) ;// merge with slots having the same keyif(smallest.isSameKey(heap.peek( )
                    )){ Object [ ]metrics=getMetricsValues(smallest.
                    currentRecord);reuseAggrs.reset
                    ();reuseAggrs.aggregate(
                    metrics )
                        ; do { ResultMergeSlotslot=heap.poll
                        ();open.add(
                        slot ) ;metrics=getMetricsValues(slot.
                        currentRecord);reuseAggrs.aggregate(
                    metrics ) ;}while(smallest.isSameKey(heap.peek()

                    ));reuseAggrs.collectStates(
                    metrics);setMetricsValues(smallest .currentRecord,
                metrics
                );}output.write(smallest .currentCuboidId,smallest.
            currentRecord
        )

        ; } }privatevoid setMetricsValues( GTRecordrecord, Object[ ]
            metricsValues ) { ImmutableBitSetmetrics=getMetricsColumns(

            record ); if (reuseMetricsSpace ==
                null ) { reuseMetricsSpace=newByteArray(record.getInfo().getMaxColumnLength(metrics
            )

            );}record.setValues (metrics ,reuseMetricsSpace,
        metricsValues

        ) ;}private Object[] getMetricsValues( GTRecord
            record ) { ImmutableBitSetmetrics=getMetricsColumns(
            record );returnrecord.getValues (metrics,
        reuseMetricsArray

        ) ; }privateImmutableBitSet getMetricsColumns( GTRecord
            record
            ) {// metrics columns always come after dimension columns if (lastCuboidColumnCount==record.getInfo().getColumnCount
                ( ))

            return lastMetricsColumns ; intto=record.getInfo().getColumnCount
            ( ) ; int from =to-reuseMetricsArray
            . length ;lastCuboidColumnCount=record.getInfo().getColumnCount
            ( ) ; lastMetricsColumns=newImmutableBitSet (from,
            to );
        return
    lastMetricsColumns

    ; } } private static classResultMergeSlotimplementsComparable <
        ResultMergeSlot >{
        CuboidResult splitResult;
        IGTScannerscanner;Iterator <GTRecord

        > recordIterator;
        long currentCuboidId;

        GTRecord currentRecord;public ResultMergeSlot( CuboidResult
            splitResult){ this .splitResult
        =

        splitResult ; }publicboolean fetchNext ( )
            throws IOException{ if (recordIterator ==
                null ) {currentCuboidId=splitResult
                . cuboidId ;scanner=splitResult.table. scan(newGTScanRequestBuilder().setInfo(splitResult.table.getInfo
                        ()).setRanges(null).setDimensions(null).setFilterPushDown(null).createGTScanRequest(
                ) ) ;recordIterator=scanner.iterator
            (

            ) ;}if(recordIterator.hasNext (
                ) ) {currentRecord=recordIterator.next
                ( );
            return true ;
                }else{scanner.close
                ( ) ;recordIterator
                = null;
            return
        false

        ;}
        } @ Overridepublicint compareTo( ResultMergeSlot
            o ) { longcuboidComp= this .currentCuboidId-o
            . currentCuboidId; if (cuboidComp
                != 0 ) return cuboidComp <0 ? -1

            :
            1 ; // note GTRecord.equals() don't work because the two GTRecord comes from different GridTable ImmutableBitSetpk=this.currentRecord.getInfo().getPrimaryKey
            ( ); for ( inti = 0 ;i<pk.trueBitCount (); i
                ++ ) { intc=pk.trueBitAt(
                i ) ; intcomp=this.currentRecord.get(c).compareTo(o.currentRecord.get(c
                ) ); if (comp
                    != 0)
            return
            comp ;}
        return

        0 ; }publicboolean isSameKey( ResultMergeSlot
            o ){ if (o
                == null)
            return
                false ;elsereturnthis.compareTo ( o)
        ==

    0;
}
