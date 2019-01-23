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
import org.apache.kylin.metadata.model.TblColRef;
import org.slf4j.Logger;
import org.slf4j

.
LoggerFactory ;importcom.google.

common .base.Stopwatch;importcom.google.
common .collect.Lists;importcom.google.
common .collect.Maps;importcom.google.
common .collect.Queues;publicclassDoggedCubeBuilder2extendsAbstractInMemCubeBuilder

{ private static Logger logger =
    LoggerFactory . getLogger ( DoggedCubeBuilder2 .class);publicDoggedCubeBuilder2(CuboidSchedulercuboidScheduler

    , IJoinedFlatTableDescflatDesc, Map< TblColRef ,Dictionary
            <String>> dictionaryMap){super( cuboidScheduler, flatDesc
        ,dictionaryMap); }@ Overridepublic<
    T

    >void
    build (BlockingQueue< T >input,InputConverterUnit<T >inputConverterUnit ,ICuboidWriteroutput) throwsIOException { newBuildOnce
            ( ) .
        build (input,inputConverterUnit,output); }private classBuildOnce{
    public

    < T > void
        build (BlockingQueue< T >input,InputConverterUnit<T >inputConverterUnit ,ICuboidWriteroutput) throwsIOException { finalRecordConsumeBlockingQueueController
                < T >
            inputController =RecordConsumeBlockingQueueController.getQueueController ( inputConverterUnit ,
                    input);finalList <InMemCubeBuilder2>

            builderList =newCopyOnWriteArrayList< > ( ) ;ForkJoinWorkerThreadFactoryfactory=newForkJoinWorkerThreadFactory

            ( ) { @ OverridepublicForkJoinWorkerThread newThread
                (ForkJoinPool
                pool ) {finalForkJoinWorkerThread worker= ForkJoinPool
                    . defaultForkJoinWorkerThreadFactory . newThread (pool);worker.setName("dogged-cubing-cuboid-worker-"
                    +worker.getPoolIndex( ) );returnworker;}}
                    ; ForkJoinPoolbuilderPool
                =
            newForkJoinPool

            ( taskThreadCount , factory ,null,true ); CuboidResultWatcherresultWatcher =newCuboidResultWatcher
            ( builderList , output );Stopwatchsw =newStopwatch

            ( ) ; sw .start()
            ;logger.info("Dogged Cube Build2 start"
            );try{BaseCuboidTask<T
            > task
                =newBaseCuboidTask< > ( inputController ,1,resultWatcher); builderPool. execute(task
                );do{builderList.add
                ( task
                    .getInternalBuilder());//Exception will be thrown here if cube building failuretask.join(
                    )
                    ;task=task.nextTask
                    ( ) ;}while(task!=
                null ) ;logger . info("Has finished feeding data, and base cuboid built, start to build child cuboids"

                );for(finalInMemCubeBuilder2builder
                : builderList) { builderPool . submit( new
                    Runnable(){@ Overridepublicvoid run
                        ()
                        { builder .startBuildFromBaseCuboid( )
                            ;}});}
                        resultWatcher
                    .start(
                )
                ;logger.info("Dogged Cube Build2 splits complete, took "
                +sw.elapsedMillis( ) +" ms");} catch (Throwablee
            ) { logger. error( "Dogged Cube Build2 error"
                ,e);if( einstanceofError
                ) throw( Error )e
                    ; elseif( einstanceof
                RuntimeException ) throw( RuntimeException )e
                    ; elsethrownew IOException(
                e
                    ) ; }finally{output.
            close ( )
                ;closeGirdTables(builderList);
                sw.stop()
                ;builderPool.shutdownNow()
                ;logger.info("Dogged Cube Build2 end, totally took "
                +sw.elapsedMillis( ) +" ms");logger . info("Dogged Cube Build2 return"
                );}}privatevoidcloseGirdTables
            (
        List

        < InMemCubeBuilder2 >builderList){for( InMemCubeBuilder2inMemCubeBuilder :
            builderList ){ for ( CuboidResultcuboidResult :
                inMemCubeBuilder .getResultCollector ( ) .getAllResult().values()){closeGirdTable(cuboidResult. table
                    );}}}privatevoid
                closeGirdTable
            (
        GridTable

        gridTable ) {try{ gridTable. close
            ( )
                ;}catch(Throwablee
            ) { logger. error( "Error closing grid table "
                +gridTable,e) ; }} }privateclass
            BaseCuboidTask
        <
    T

    > extends RecursiveTask<CuboidResult> { privatestaticfinallong serialVersionUID
        = - 5408592502260876799L ; private final intsplitSeq;

        private final ICuboidResultListener resultListener;
        private RecordConsumeBlockingQueueController < T>

        inputController ;privateInMemCubeBuilder2builder ;private
        volatile BaseCuboidTask <T

        > next ;publicBaseCuboidTask( finalRecordConsumeBlockingQueueController

        < T>inputController ,intsplitSeq, ICuboidResultListenerresultListener ) {this
                . inputController= inputController
            ;this. splitSeq =splitSeq
            ;this. resultListener =resultListener
            ;this. builder =new
            InMemCubeBuilder2(cuboidScheduler , flatDesc ,dictionaryMap); builder. setReserveMemoryMB(reserveMemoryMB
            );builder.setConcurrentThreads(taskThreadCount
            );logger.info("Split #"
            +splitSeq+" kickoff") ; } @ OverrideprotectedCuboidResult
        compute

        ()
        { try {CuboidResultbaseCuboidResult =
            builder .
                buildBaseCuboid ( inputController ,resultListener);if( !inputController.
                ifEnd ()){next=newBaseCuboidTask <
                    > ( inputController ,splitSeq+1,resultListener ) ; next. fork()
                    ;}logger.info(
                "Split #"
                +splitSeq+" finished") ; return baseCuboidResult ;}catch
                ( IOExceptione
            ) { thrownew RuntimeException( e
                ) ; }}publicInMemCubeBuilder2getInternalBuilder
            (
        )

        { return builder;} public
            BaseCuboidTask <T
        >

        nextTask (){return next;} }
            /**
     * Class response for watch the cube building result, monitor the cube building process and trigger merge actions if required.
     *
     */ privateclass
        CuboidResultWatcher
    implements

    ICuboidResultListener
    { final BlockingQueue < CuboidResult >
        outputQueue ;finalMap< Long,
        List <CuboidResult>> pendingQueue=Maps.newHashMap ( ) ;finalList<InMemCubeBuilder2>
        builderList ;finalICuboidWriteroutput ;public
        CuboidResultWatcher ( finalList

        < InMemCubeBuilder2>builderList ,finalICuboidWriteroutput ){ this . outputQueue= Queues
            .newLinkedBlockingQueue( ) ;this.builderList=builderList
            ;this. output =output
            ;}public void start(
        )

        throws IOException {SplitMergermerger = new SplitMerger
            ( ) ; while (true){
            if (!outputQueue .
                isEmpty ()){List<CuboidResult> splitResultReturned
                    =Lists.newArrayList ( ) ;outputQueue.drainTo(splitResultReturned
                    );for(CuboidResultsplitResult:
                    splitResultReturned ){ if ( builderList. size
                        ( )==1){merger . mergeAndOutput( Lists
                            .newArrayList(splitResult),output);}else {List<
                        CuboidResult > cuboidResultList
                            =pendingQueue.get ( splitResult .cuboidId);if(cuboidResultList==null
                            ) {cuboidResultList = Lists. newArrayListWithExpectedSize
                                ( builderList .size());cuboidResultList.add(splitResult
                                );pendingQueue.put(splitResult
                                .cuboidId,cuboidResultList);}else {cuboidResultList.
                            add ( splitResult
                                );}if(cuboidResultList.
                            size
                            ( )==builderList.size( ) ){merger.mergeAndOutput( cuboidResultList
                                ,output);pendingQueue. remove(splitResult
                                .cuboidId);}}}}boolean
                            jobFinished
                        =
                    isAllBuildFinished
                (

                ) ; if (outputQueue.isEmpty
                ( )&&!jobFinished){ boolean ifWait=true ;
                    for ( InMemCubeBuilder2 builder:
                    builderList ){ Queue < CuboidTask> queue
                        =builder.getCompletedTaskQueue ( ) ;while(queue.size
                        ( )>0){CuboidTask childTask =queue .
                            poll ( ) ;if(childTask.isCompletedAbnormally
                            ( )){thrownewRuntimeException( childTask
                                . getException ());}ifWait=false;
                            }
                            } if (ifWait
                        )
                    {
                    try {Thread. sleep
                        ( 100L
                            );}catch(InterruptedExceptione
                        ) { thrownew RuntimeException( e
                            ) ; }}}elseif
                        (
                    outputQueue
                . isEmpty ( )&&pendingQueue.isEmpty( ) &&jobFinished){return ; }} }
                    privateboolean
                isAllBuildFinished
            (
        )

        { for (InMemCubeBuilder2split :
            builderList ){ if ( !split .
                isAllCuboidDone ()){returnfalse;} }
                    return true;
                }
            @
            Override publicvoid
        finish

        (CuboidResult
        result ) {Stopwatchstopwatch =new Stopwatch
            ( ) . start ();intnRetries=0;
            while ( ! outputQueue.
            offer (result)){nRetries++;long sleepTime
                =stopwatch.
                elapsedMillis ( ) ;if(sleepTime>3600000L
                ) {stopwatch . stop( )
                    ;thrownewRuntimeException("OutputQueue Full. Cannot offer to the output queue after waiting for one hour!!! Current queue size: "
                    + outputQueue .size
                            (
                                    ) );}logger.warn(
                "OutputQueue Full. Queue size: "
                +outputQueue.size( ) +". Total sleep time : "+sleepTime+ ", and retry count : " + nRetries )
                        ; try { Thread.sleep
                ( 5000L
                    );}catch(InterruptedExceptione
                ) { thrownew RuntimeException( e
                    ) ; }}stopwatch.stop
                (
            )
            ;}}privateclassSplitMerger
        {
    MeasureAggregators

    reuseAggrs ; Object [
        ] reuseMetricsArray;
        ByteArrayreuseMetricsSpace; longlastCuboidColumnCount
        ; ImmutableBitSetlastMetricsColumns

        ; SplitMerger(
        ) {reuseAggrs

        =newMeasureAggregators (
            cubeDesc . getMeasures ());reuseMetricsArray=newObject[
            cubeDesc . getMeasures ().size()];}publicvoidmergeAndOutput(
        List

        < CuboidResult >splitResultList,ICuboidWriteroutput) throwsIOException { if( splitResultList . size
            ( )==1){CuboidResult cuboidResult =splitResultList .
                get ( 0 );outputCuboid(cuboidResult.cuboidId
                ,cuboidResult.table,output );return; }LinkedList<
                ResultMergeSlot>
            open
            =Lists.newLinkedList ( ) ;for(CuboidResultsplitResult:
            splitResultList ){ open . add( new
                ResultMergeSlot(splitResult)) ;}PriorityQueue<ResultMergeSlot>
            heap

            =newPriorityQueue< ResultMergeSlot > ( );while(true){
            // ready records in open slots and add to heap while(! open
                .
                isEmpty ()){ResultMergeSlotslot=open .
                    removeFirst ( ) ;if(slot.fetchNext
                    ( )){heap.add( slot
                        );}}// find the smallest on heapResultMergeSlotsmallest
                    =
                heap

                .
                poll ( ) ;if(smallest==null
                ) break; open .add
                    (smallest
                );// merge with slots having the same keyif(smallest.

                isSameKey
                ( heap.peek())){Object[]metrics =
                    getMetricsValues(smallest . currentRecord );reuseAggrs.reset()
                    ;reuseAggrs.aggregate(metrics
                    );do{ResultMergeSlotslot=
                    heap .
                        poll ( ) ;open.add(slot
                        );metrics=getMetricsValues(slot
                        . currentRecord );reuseAggrs.aggregate(metrics
                        );}while(smallest.
                    isSameKey ( heap.peek()));reuseAggrs.collectStates(metrics

                    );setMetricsValues(smallest.currentRecord
                    ,metrics);}output .write(
                smallest
                .currentCuboidId,smallest.currentRecord); }}privatevoidsetMetricsValues
            (
        GTRecord

        record , Object[] metricsValues) {ImmutableBitSetmetrics =getMetricsColumns (
            record ) ; if(reuseMetricsSpace==null

            ) {reuseMetricsSpace = newByteArray (
                record . getInfo ().getMaxColumnLength(metrics));}record.setValues(
            metrics

            ,reuseMetricsSpace,metricsValues); }private Object[]
        getMetricsValues

        ( GTRecordrecord) {ImmutableBitSetmetrics =getMetricsColumns (
            record ) ; returnrecord.getValues(
            metrics ,reuseMetricsArray);}private ImmutableBitSetgetMetricsColumns(
        GTRecord

        record ) {// metrics columns always come after dimension columnsif (lastCuboidColumnCount ==
            record
            . getInfo( ) .getColumnCount())returnlastMetricsColumns;intto
                = record.

            getInfo ( ) .getColumnCount();intfrom=to-
            reuseMetricsArray . length ; lastCuboidColumnCount =record.getInfo
            ( ) .getColumnCount();lastMetricsColumns=newImmutableBitSet(
            from , to );returnlastMetricsColumns ;}}
            private staticclass
        ResultMergeSlot
    implements

    Comparable < ResultMergeSlot > { CuboidResultsplitResult;IGTScanner scanner
        ; Iterator<
        GTRecord >recordIterator
        ;longcurrentCuboidId; GTRecordcurrentRecord

        ; publicResultMergeSlot
        ( CuboidResultsplitResult

        ) {this. splitResult= splitResult
            ;}public boolean fetchNext(
        )

        throws IOException {if( recordIterator == null
            ) {currentCuboidId = splitResult. cuboidId
                ; scanner =splitResult.table
                . scan (newGTScanRequestBuilder().setInfo (splitResult.table.getInfo()).setRanges(null)
                        .setDimensions(null).setFilterPushDown(null).createGTScanRequest());recordIterator=scanner.iterator
                ( ) ;}if(recordIterator.
            hasNext

            ( )){currentRecord=recordIterator. next
                ( ) ;returntrue;}else
                { scanner.
            close ( )
                ;recordIterator=null;return
                false ; }}
                @ Overridepublic
            int
        compareTo

        (ResultMergeSlot
        o ) {longcuboidComp =this .
            currentCuboidId - o .currentCuboidId; if (cuboidComp!=0
            ) returncuboidComp < 0?
                - 1 : 1 ; // note GTRecord.equals() don't work because the two GTRecord comes from different GridTableImmutableBitSet pk =this

            .
            currentRecord . getInfo ().getPrimaryKey();for(inti=
            0 ;i < pk .trueBitCount ( ) ;i++){int c=pk .
                trueBitAt ( i );intcomp=this.
                currentRecord . get (c).compareTo(o.currentRecord.get(c));if(comp!=0
                ) returncomp ; }return
                    0 ;}
            public
            boolean isSameKey(
        ResultMergeSlot

        o ) {if( o== null
            ) returnfalse ; elsereturn
                this .compareTo
            (
                o )==0;}} ; }