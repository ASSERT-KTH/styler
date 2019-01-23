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
import org.apache.kylin.common.
util .Dictionary;importorg
. apache.kylin.common.util.ImmutableBitSet;importorg
. apache.kylin.cube.cuboid.CuboidScheduler;importorg
. apache.kylin.cube.inmemcubing.AbstractInMemCubeBuilder;importorg
. apache.kylin.cube.inmemcubing.CuboidResult;importorg
. apache.kylin.cube.inmemcubing.ICuboidWriter;importorg
. apache.kylin.cube.inmemcubing.InputConverterUnit;importorg
. apache.kylin.cube.inmemcubing.RecordConsumeBlockingQueueController;importorg
. apache.kylin.gridtable.GTRecord;importorg
. apache.kylin.gridtable.GTScanRequestBuilder;importorg
. apache.kylin.gridtable.GridTable;importorg
. apache.kylin.gridtable.IGTScanner;importorg
. apache.kylin.measure.MeasureAggregators;importorg
. apache.kylin.metadata.model.IJoinedFlatTableDesc;importorg
. apache.kylin.metadata.model.TblColRef;importorg
. slf4j.Logger;importorg
. slf4j.LoggerFactory;importcom

. google.common.base.Stopwatch;importcom
. google.common.collect.Lists;importcom
. google.common.collect.Maps;importcom
. google.common.collect.Queues;publicclass

DoggedCubeBuilder2 extends AbstractInMemCubeBuilder { private static
    Logger logger = LoggerFactory . getLogger(DoggedCubeBuilder2.class);publicDoggedCubeBuilder2

    ( CuboidSchedulercuboidScheduler, IJoinedFlatTableDescflatDesc , Map<
            TblColRef,Dictionary< String>>dictionaryMap) {super (
        cuboidScheduler,flatDesc, dictionaryMap) ;}@
    Override

    public<
    T >voidbuild ( BlockingQueue<T>input, InputConverterUnit< T>inputConverterUnit, ICuboidWriteroutput ) throwsIOException
            { new BuildOnce
        ( ).build(input,inputConverterUnit, output) ;}private
    class

    BuildOnce { public <
        T >voidbuild ( BlockingQueue<T>input, InputConverterUnit< T>inputConverterUnit, ICuboidWriteroutput ) throwsIOException
                { final RecordConsumeBlockingQueueController
            < T>inputController= RecordConsumeBlockingQueueController . getQueueController
                    (inputConverterUnit,input) ;finalList

            < InMemCubeBuilder2>builderList= new CopyOnWriteArrayList < >();ForkJoinWorkerThreadFactoryfactory

            = new ForkJoinWorkerThreadFactory ( ){@ Override
                publicForkJoinWorkerThread
                newThread ( ForkJoinPoolpool) {final ForkJoinWorkerThread
                    worker = ForkJoinPool . defaultForkJoinWorkerThreadFactory.newThread(pool);worker.
                    setName("dogged-cubing-cuboid-worker-"+worker . getPoolIndex());returnworker
                    ; }}
                ;
            ForkJoinPoolbuilderPool

            = new ForkJoinPool ( taskThreadCount,factory, null, true) ;CuboidResultWatcherresultWatcher
            = new CuboidResultWatcher ( builderList,output) ;Stopwatchsw

            = new Stopwatch ( );sw.
            start();logger.
            info("Dogged Cube Build2 start");try{
            BaseCuboidTask <
                T>task= new BaseCuboidTask < >(inputController,1, resultWatcher) ;builderPool.
                execute(task);do{
                builderList .
                    add(task.getInternalBuilder());//Exception will be thrown here if cube building failuretask
                    .
                    join();task=
                    task . nextTask();}while
                ( task !=null ) ;logger.

                info("Has finished feeding data, and base cuboid built, start to build child cuboids");for(
                final InMemCubeBuilder2builder : builderList ) {builderPool .
                    submit(newRunnable( ){@ Override
                        publicvoid
                        run ( ){builder .
                            startBuildFromBaseCuboid();}}
                        )
                    ;}resultWatcher
                .
                start();logger.
                info("Dogged Cube Build2 splits complete, took "+sw . elapsedMillis()+" ms" ) ;}catch
            ( Throwable e) {logger .
                error("Dogged Cube Build2 error",e) ;if(
                e instanceofError ) throw(
                    Error )e; elseif
                ( e instanceofRuntimeException ) throw(
                    RuntimeException )e; elsethrow
                new
                    IOException ( e);}finally
            { output .
                close();closeGirdTables(
                builderList);sw.
                stop();builderPool.
                shutdownNow();logger.
                info("Dogged Cube Build2 end, totally took "+sw . elapsedMillis()+" ms" ) ;logger.
                info("Dogged Cube Build2 return");}}
            private
        void

        closeGirdTables ( List<InMemCubeBuilder2>builderList) {for (
            InMemCubeBuilder2 inMemCubeBuilder: builderList ) {for (
                CuboidResult cuboidResult: inMemCubeBuilder . getResultCollector().getAllResult().values()){closeGirdTable (
                    cuboidResult.table);}}
                }
            private
        void

        closeGirdTable ( GridTablegridTable) {try {
            gridTable .
                close();}catch
            ( Throwable e) {logger .
                error("Error closing grid table "+gridTable , e) ;}}
            }
        private
    class

    BaseCuboidTask < T>extendsRecursiveTask < CuboidResult>{private static
        final long serialVersionUID = - 5408592502260876799L ;privatefinal

        int splitSeq ; privatefinal
        ICuboidResultListener resultListener ; privateRecordConsumeBlockingQueueController

        < T>inputController; privateInMemCubeBuilder2
        builder ; privatevolatile

        BaseCuboidTask < T>next; publicBaseCuboidTask

        ( finalRecordConsumeBlockingQueueController< T>inputController, intsplitSeq , ICuboidResultListenerresultListener
                ) {this .
            inputController=inputController ; this.
            splitSeq=splitSeq ; this.
            resultListener=resultListener ; this.
            builder=new InMemCubeBuilder2 ( cuboidScheduler,flatDesc, dictionaryMap) ;builder.
            setReserveMemoryMB(reserveMemoryMB);builder.
            setConcurrentThreads(taskThreadCount);logger.
            info("Split #"+splitSeq + " kickoff" ) ;}@
        Override

        protectedCuboidResult
        compute ( ){try {
            CuboidResult baseCuboidResult
                = builder . buildBaseCuboid(inputController,resultListener) ;if(
                ! inputController.ifEnd()){next =
                    new BaseCuboidTask < >(inputController,splitSeq+ 1 , resultListener) ;next.
                    fork();}logger
                .
                info("Split #"+splitSeq + " finished" ) ;returnbaseCuboidResult
                ; }catch
            ( IOException e) {throw new
                RuntimeException ( e);}}
            public
        InMemCubeBuilder2

        getInternalBuilder ( ){return builder
            ; }public
        BaseCuboidTask

        < T>nextTask( ){return next
            ; }}
        /**
     * Class response for watch the cube building result, monitor the cube building process and trigger merge actions if required.
     *
     */
    private

    class
    CuboidResultWatcher implements ICuboidResultListener { final BlockingQueue
        < CuboidResult>outputQueue; finalMap
        < Long,List< CuboidResult>>pendingQueue= Maps . newHashMap();finalList
        < InMemCubeBuilder2>builderList; finalICuboidWriter
        output ; publicCuboidResultWatcher

        ( finalList< InMemCubeBuilder2>builderList, finalICuboidWriter output ) {this .
            outputQueue=Queues . newLinkedBlockingQueue();this.
            builderList=builderList ; this.
            output=output ; }public
        void

        start ( )throwsIOException { SplitMerger merger
            = new SplitMerger ( );while(
            true ){if (
                ! outputQueue.isEmpty()){List <
                    CuboidResult>splitResultReturned= Lists . newArrayList();outputQueue.
                    drainTo(splitResultReturned);for(
                    CuboidResult splitResult: splitResultReturned ) {if (
                        builderList .size()==1 ) {merger .
                            mergeAndOutput(Lists.newArrayList(splitResult),output) ;}else
                        { List <
                            CuboidResult>cuboidResultList= pendingQueue . get(splitResult.cuboidId);if(
                            cuboidResultList ==null ) {cuboidResultList =
                                Lists . newArrayListWithExpectedSize(builderList.size());cuboidResultList.
                                add(splitResult);pendingQueue.
                                put(splitResult.cuboidId,cuboidResultList) ;}else
                            { cuboidResultList .
                                add(splitResult);}if
                            (
                            cuboidResultList .size()==builderList . size()){merger .
                                mergeAndOutput(cuboidResultList,output) ;pendingQueue.
                                remove(splitResult.cuboidId);}}
                            }
                        }
                    boolean
                jobFinished

                = isAllBuildFinished ( );if(
                outputQueue .isEmpty()&&! jobFinished ){boolean ifWait
                    = true ; for(
                    InMemCubeBuilder2 builder: builderList ) {Queue <
                        CuboidTask>queue= builder . getCompletedTaskQueue();while(
                        queue .size()>0 ) {CuboidTask childTask
                            = queue . poll();if(
                            childTask .isCompletedAbnormally()){throw new
                                RuntimeException ( childTask.getException());}ifWait
                            =
                            false ; }}
                        if
                    (
                    ifWait ){try {
                        Thread .
                            sleep(100L);}catch
                        ( InterruptedException e) {throw new
                            RuntimeException ( e);}}
                        }
                    else
                if ( outputQueue .isEmpty()&&pendingQueue . isEmpty()&&jobFinished ) {return ;
                    }}
                }
            private
        boolean

        isAllBuildFinished ( ){for (
            InMemCubeBuilder2 split: builderList ) {if (
                ! split.isAllCuboidDone()){return false
                    ; }}
                return
            true
            ; }@
        Override

        publicvoid
        finish ( CuboidResultresult) {Stopwatch stopwatch
            = new Stopwatch ( ).start();intnRetries
            = 0 ; while(
            ! outputQueue.offer(result)){nRetries ++
                ;longsleepTime
                = stopwatch . elapsedMillis();if(
                sleepTime >3600000L ) {stopwatch .
                    stop();thrownew
                    RuntimeException ( "OutputQueue Full. Cannot offer to the output queue after waiting for one hour!!! Current queue size: "+
                            outputQueue
                                    . size());}logger
                .
                warn("OutputQueue Full. Queue size: "+outputQueue . size()+". Total sleep time : " + sleepTime + ", and retry count : "
                        + nRetries ) ;try{
                Thread .
                    sleep(5000L);}catch
                ( InterruptedException e) {throw new
                    RuntimeException ( e);}}
                stopwatch
            .
            stop();}}
        private
    class

    SplitMerger { MeasureAggregators reuseAggrs
        ; Object[
        ]reuseMetricsArray; ByteArrayreuseMetricsSpace
        ; longlastCuboidColumnCount

        ; ImmutableBitSetlastMetricsColumns
        ; SplitMerger(

        ){reuseAggrs =
            new MeasureAggregators ( cubeDesc.getMeasures());reuseMetricsArray=
            new Object [ cubeDesc.getMeasures().size()];}public
        void

        mergeAndOutput ( List<CuboidResult>splitResultList, ICuboidWriteroutput ) throwsIOException { if (
            splitResultList .size()==1 ) {CuboidResult cuboidResult
                = splitResultList . get(0);outputCuboid(
                cuboidResult.cuboidId,cuboidResult. table,output) ;return;
                }LinkedList
            <
            ResultMergeSlot>open= Lists . newLinkedList();for(
            CuboidResult splitResult: splitResultList ) {open .
                add(newResultMergeSlot( splitResult));}PriorityQueue
            <

            ResultMergeSlot>heap= new PriorityQueue < ResultMergeSlot>();while(
            true ){// ready records in open slots and add to heap while
                (
                ! open.isEmpty()){ResultMergeSlot slot
                    = open . removeFirst();if(
                    slot .fetchNext()){heap .
                        add(slot);}}
                    // find the smallest on heap
                ResultMergeSlot

                smallest
                = heap . poll();if(
                smallest ==null ) break;
                    open.
                add(smallest);// merge with slots having the same keyif

                (
                smallest .isSameKey(heap.peek())){Object [
                    ]metrics= getMetricsValues ( smallest.currentRecord);reuseAggrs.
                    reset();reuseAggrs.
                    aggregate(metrics);do{
                    ResultMergeSlot slot
                        = heap . poll();open.
                        add(slot);metrics=
                        getMetricsValues ( slot.currentRecord);reuseAggrs.
                        aggregate(metrics);}while
                    ( smallest .isSameKey(heap.peek()));reuseAggrs.

                    collectStates(metrics);setMetricsValues(
                    smallest.currentRecord,metrics) ;}output
                .
                write(smallest.currentCuboidId,smallest. currentRecord);}}
            private
        void

        setMetricsValues ( GTRecordrecord, Object[ ]metricsValues) {ImmutableBitSet metrics
            = getMetricsColumns ( record);if(

            reuseMetricsSpace ==null ) {reuseMetricsSpace =
                new ByteArray ( record.getInfo().getMaxColumnLength(metrics));}record
            .

            setValues(metrics,reuseMetricsSpace, metricsValues) ;}private
        Object

        [ ]getMetricsValues( GTRecordrecord) {ImmutableBitSet metrics
            = getMetricsColumns ( record);returnrecord
            . getValues(metrics,reuseMetricsArray) ;}private
        ImmutableBitSet

        getMetricsColumns ( GTRecordrecord) {// metrics columns always come after dimension columns if
            (
            lastCuboidColumnCount ==record . getInfo().getColumnCount())returnlastMetricsColumns
                ; intto

            = record . getInfo().getColumnCount();intfrom
            = to - reuseMetricsArray . length;lastCuboidColumnCount=
            record . getInfo().getColumnCount();lastMetricsColumns=
            new ImmutableBitSet ( from,to) ;returnlastMetricsColumns
            ; }}
        private
    static

    class ResultMergeSlot implements Comparable < ResultMergeSlot>{CuboidResult splitResult
        ; IGTScannerscanner
        ; Iterator<
        GTRecord>recordIterator; longcurrentCuboidId

        ; GTRecordcurrentRecord
        ; publicResultMergeSlot

        ( CuboidResultsplitResult) {this .
            splitResult=splitResult ; }public
        boolean

        fetchNext ( )throwsIOException { if (
            recordIterator ==null ) {currentCuboidId =
                splitResult . cuboidId;scanner=
                splitResult . table.scan(newGTScanRequestBuilder( ).setInfo(splitResult.table.getInfo()).setRanges
                        (null).setDimensions(null).setFilterPushDown(null).createGTScanRequest());recordIterator=
                scanner . iterator();}if
            (

            recordIterator .hasNext()){currentRecord =
                recordIterator . next();returntrue
                ; }else
            { scanner .
                close();recordIterator=
                null ; returnfalse
                ; }}
            @
        Override

        publicint
        compareTo ( ResultMergeSloto) {long cuboidComp
            = this . currentCuboidId-o . currentCuboidId;if(
            cuboidComp !=0 ) returncuboidComp
                < 0 ? - 1 :1 ; // note GTRecord.equals() don't work because the two GTRecord comes from different GridTableImmutableBitSet

            pk
            = this . currentRecord.getInfo().getPrimaryKey();for(
            int i= 0 ; i< pk . trueBitCount();i++ ){int c
                = pk . trueBitAt(i);intcomp
                = this . currentRecord.get(c).compareTo(o.currentRecord.get(c));if(
                comp !=0 ) returncomp
                    ; }return
            0
            ; }public
        boolean

        isSameKey ( ResultMergeSloto) {if (
            o ==null ) returnfalse
                ; elsereturn
            this
                . compareTo(o)==0 ; }}
        ;

    }