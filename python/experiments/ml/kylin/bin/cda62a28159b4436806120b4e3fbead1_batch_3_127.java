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
importjava.util.Queue;
import java.util.concurrent.
BlockingQueue ;importjava.util.concurrent.
CopyOnWriteArrayList ;importjava.util.concurrent.
ForkJoinPool ;importjava.util.concurrent.
ForkJoinPool .ForkJoinWorkerThreadFactory;importjava.util.concurrent.
ForkJoinWorkerThread ;importjava.util.concurrent.
RecursiveTask ;importorg.apache.kylin.

common .util.ByteArray;importorg.apache.kylin.
common .util.Dictionary;importorg.apache.kylin.
common .util.ImmutableBitSet;importorg.apache.kylin.
cube .cuboid.CuboidScheduler;importorg.apache.kylin.
cube .inmemcubing.AbstractInMemCubeBuilder;importorg.apache.kylin.
cube .inmemcubing.CuboidResult;importorg.apache.kylin.
cube .inmemcubing.ICuboidWriter;importorg.apache.kylin.
cube .inmemcubing.InputConverterUnit;importorg.apache.kylin.
cube .inmemcubing.RecordConsumeBlockingQueueController;importorg.apache.kylin.
gridtable .GTRecord;importorg.apache.kylin.
gridtable .GTScanRequestBuilder;importorg.apache.kylin.
gridtable .GridTable;importorg.apache.kylin.
gridtable .IGTScanner;importorg.apache.kylin.
measure .MeasureAggregators;importorg.apache.kylin.
metadata .model.IJoinedFlatTableDesc;importorg.apache.kylin.
metadata .model.TblColRef;importorg.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.

base .Stopwatch;importcom.google.common.
collect .Lists;importcom.google.common.
collect .Maps;importcom.google.common.
collect .Queues;publicclassDoggedCubeBuilder2extendsAbstractInMemCubeBuilder{private

static Logger logger = LoggerFactory .
    getLogger ( DoggedCubeBuilder2 . class );publicDoggedCubeBuilder2(CuboidSchedulercuboidScheduler,IJoinedFlatTableDesc

    flatDesc ,Map< TblColRef, Dictionary <String
            >>dictionaryMap) {super(cuboidScheduler, flatDesc, dictionaryMap
        );}@ Overridepublic <T>
    void

    build(
    BlockingQueue <T> input ,InputConverterUnit<T>inputConverterUnit ,ICuboidWriter output)throwsIOException {new BuildOnce ()
            . build (
        input ,inputConverterUnit,output);}private classBuildOnce {public<
    T

    > void build (
        BlockingQueue <T> input ,InputConverterUnit<T>inputConverterUnit ,ICuboidWriter output)throwsIOException {final RecordConsumeBlockingQueueController <T
                > inputController =
            RecordConsumeBlockingQueueController .getQueueController(inputConverterUnit , input )
                    ;finalList<InMemCubeBuilder2 >builderList=

            new CopyOnWriteArrayList<>( ) ; ForkJoinWorkerThreadFactory factory=newForkJoinWorkerThreadFactory()

            { @ Override public ForkJoinWorkerThreadnewThread( ForkJoinPool
                pool)
                { final ForkJoinWorkerThreadworker= ForkJoinPool. defaultForkJoinWorkerThreadFactory
                    . newThread ( pool );worker.setName("dogged-cubing-cuboid-worker-"+worker
                    .getPoolIndex()) ; returnworker;}};ForkJoinPool
                    builderPool =new
                ForkJoinPool
            (taskThreadCount

            , factory , null ,true); CuboidResultWatcherresultWatcher =new CuboidResultWatcher(builderList
            , output ) ; Stopwatchsw=new Stopwatch()

            ; sw . start ();logger
            .info("Dogged Cube Build2 start");
            try{BaseCuboidTask<T>task
            = new
                BaseCuboidTask<>( inputController , 1 ,resultWatcher);builderPool. execute( task);
                do{builderList.add(task
                . getInternalBuilder
                    ());//Exception will be thrown here if cube building failuretask.join();
                    task
                    =task.nextTask()
                    ; } while(task!=null)
                ; logger .info ( "Has finished feeding data, and base cuboid built, start to build child cuboids");

                for(finalInMemCubeBuilder2builder:builderList
                ) {builderPool . submit ( newRunnable (
                    ){@Overridepublic voidrun( )
                        {builder
                        . startBuildFromBaseCuboid (); }
                            });}resultWatcher.
                        start
                    ();
                logger
                .info("Dogged Cube Build2 splits complete, took "+sw
                .elapsedMillis()+ " ms" );}catch( Throwable e){
            logger . error( "Dogged Cube Build2 error", e
                );if(einstanceof Error)throw
                ( Error) e ;else
                    if (einstanceof RuntimeException)
                throw ( RuntimeException) e ;else
                    throw newIOException( e)
                ;
                    } finally {output.close(
            ) ; closeGirdTables
                (builderList);sw.
                stop();builderPool
                .shutdownNow();logger
                .info("Dogged Cube Build2 end, totally took "+sw
                .elapsedMillis()+ " ms" );logger.info ( "Dogged Cube Build2 return");
                }}privatevoidcloseGirdTables(List
            <
        InMemCubeBuilder2

        > builderList ){for(InMemCubeBuilder2inMemCubeBuilder :builderList )
            { for( CuboidResult cuboidResult :inMemCubeBuilder .
                getResultCollector () . getAllResult ().values()){closeGirdTable(cuboidResult.table) ;
                    }}}privatevoidcloseGirdTable(
                GridTable
            gridTable
        )

        { try {gridTable. close( )
            ; }
                catch(Throwablee){
            logger . error( "Error closing grid table "+ gridTable
                ,e);} } }private classBaseCuboidTask<
            T
        >
    extends

    RecursiveTask < CuboidResult>{private static finallongserialVersionUID= -
        5408592502260876799L ; private final int splitSeq ;privatefinal

        ICuboidResultListener resultListener ; privateRecordConsumeBlockingQueueController
        < T > inputController;

        private InMemCubeBuilder2builder;private volatileBaseCuboidTask
        < T >next

        ; public BaseCuboidTask(finalRecordConsumeBlockingQueueController <T

        > inputController,int splitSeq,ICuboidResultListenerresultListener ){ this .inputController
                = inputController; this
            .splitSeq= splitSeq ;this
            .resultListener= resultListener ;this
            .builder= new InMemCubeBuilder2(
            cuboidScheduler,flatDesc , dictionaryMap );builder. setReserveMemoryMB( reserveMemoryMB);
            builder.setConcurrentThreads(taskThreadCount);
            logger.info("Split #"+splitSeq
            +" kickoff");} @ Override protected CuboidResultcompute(
        )

        {try
        { CuboidResult baseCuboidResult=builder .
            buildBaseCuboid (
                inputController , resultListener );if(!inputController .ifEnd(
                ) ){next=newBaseCuboidTask<> (
                    inputController , splitSeq +1,resultListener); next . fork( );}
                    logger.info("Split #"+
                splitSeq
                +" finished");return baseCuboidResult ; } catch(IOException
                e ){
            throw new RuntimeException( e) ;
                } } publicInMemCubeBuilder2getInternalBuilder()
            {
        return

        builder ; }publicBaseCuboidTask <
            T >nextTask
        (

        ) {returnnext; }}/**
     * Class response for watch the cube building result, monitor the cube building process and trigger merge actions if required.
     *
     */ private
            class CuboidResultWatcherimplements
        ICuboidResultListener
    {

    final
    BlockingQueue < CuboidResult > outputQueue ;
        final Map<Long, List<
        CuboidResult >>pendingQueue= Maps.newHashMap() ; final List<InMemCubeBuilder2>builderList;
        final ICuboidWriteroutput;public CuboidResultWatcher(
        final List <InMemCubeBuilder2

        > builderList,final ICuboidWriteroutput){ this. outputQueue = Queues. newLinkedBlockingQueue
            (); this .builderList=builderList;this
            .output= output ;}
            publicvoidstart ( )throws
        IOException

        { SplitMerger merger=new SplitMerger ( )
            ; while ( true ){if(
            ! outputQueue.isEmpty (
                ) ){List<CuboidResult>splitResultReturned= Lists
                    .newArrayList() ; outputQueue .drainTo(splitResultReturned);
                    for(CuboidResultsplitResult:splitResultReturned)
                    { if( builderList . size( )
                        == 1){merger.mergeAndOutput ( Lists. newArrayList
                            (splitResult),output);}else{List <CuboidResult>
                        cuboidResultList = pendingQueue
                            .get(splitResult . cuboidId );if(cuboidResultList==null){
                            cuboidResultList =Lists . newArrayListWithExpectedSize( builderList
                                . size ());cuboidResultList.add(splitResult);
                                pendingQueue.put(splitResult.cuboidId
                                ,cuboidResultList);}else{cuboidResultList .add(
                            splitResult ) ;
                                }if(cuboidResultList.size(
                            )
                            == builderList.size()) { merger.mergeAndOutput(cuboidResultList, output
                                );pendingQueue.remove( splitResult.cuboidId
                                );}}}}booleanjobFinished=
                            isAllBuildFinished
                        (
                    )
                ;

                if ( outputQueue .isEmpty()
                && !jobFinished){booleanifWait = true;for (
                    InMemCubeBuilder2 builder : builderList)
                    { Queue< CuboidTask > queue= builder
                        .getCompletedTaskQueue() ; while (queue.size()
                        > 0){CuboidTaskchildTask= queue .poll (
                            ) ; if (childTask.isCompletedAbnormally()
                            ) {thrownewRuntimeException(childTask. getException
                                ( ) );}ifWait=false;}}
                            if
                            ( ifWait ){
                        try
                    {
                    Thread .sleep( 100L
                        ) ;
                            }catch(InterruptedExceptione){
                        throw new RuntimeException( e) ;
                            } } }elseif(outputQueue
                        .
                    isEmpty
                ( ) && pendingQueue.isEmpty()&& jobFinished ){return;} } }private boolean
                    isAllBuildFinished(
                )
            {
        for

        ( InMemCubeBuilder2 split:builderList )
            { if( ! split .isAllCuboidDone (
                ) ){returnfalse;}}return true
                    ; }@
                Override
            public
            void finish(
        CuboidResult

        result)
        { Stopwatch stopwatch=new Stopwatch( )
            . start ( ) ;intnRetries=0;while(
            ! outputQueue . offer(
            result )){nRetries++;longsleepTime= stopwatch
                .elapsedMillis(
                ) ; if (sleepTime>3600000L){
                stopwatch .stop ( ); throw
                    newRuntimeException("OutputQueue Full. Cannot offer to the output queue after waiting for one hour!!! Current queue size: "+outputQueue
                    . size ()
                            )
                                    ; }logger.warn("OutputQueue Full. Queue size: "+
                outputQueue
                .size()+ ". Total sleep time : " +sleepTime+", and retry count : "+ nRetries ) ; try
                        { Thread . sleep(5000L
                ) ;
                    }catch(InterruptedExceptione){
                throw new RuntimeException( e) ;
                    } } stopwatch.stop()
                ;
            }
            }privateclassSplitMerger{MeasureAggregators
        reuseAggrs
    ;

    Object [ ] reuseMetricsArray
        ; ByteArrayreuseMetricsSpace
        ;longlastCuboidColumnCount ;ImmutableBitSet
        lastMetricsColumns ;SplitMerger

        ( ){
        reuseAggrs =new

        MeasureAggregators(cubeDesc .
            getMeasures ( ) );reuseMetricsArray=newObject[cubeDesc.
            getMeasures ( ) .size()];}publicvoidmergeAndOutput(List<
        CuboidResult

        > splitResultList ,ICuboidWriteroutput)throwsIOException {if ( splitResultList. size ( )
            == 1){CuboidResultcuboidResult= splitResultList .get (
                0 ) ; outputCuboid(cuboidResult.cuboidId,cuboidResult
                .table,output); return;}LinkedList <ResultMergeSlot>
                open=
            Lists
            .newLinkedList() ; for (CuboidResultsplitResult:splitResultList)
            { open. add ( newResultMergeSlot (
                splitResult));} PriorityQueue<ResultMergeSlot>heap=
            new

            PriorityQueue<ResultMergeSlot> ( ) ; while(true){// ready records in open slots and add to heapwhile
            ( !open. isEmpty
                (
                ) ){ResultMergeSlotslot=open.removeFirst (
                    ) ; if (slot.fetchNext()
                    ) {heap.add(slot) ;
                        }}// find the smallest on heapResultMergeSlotsmallest=heap
                    .
                poll

                (
                ) ; if (smallest==null)break
                ; open. add (smallest
                    );
                // merge with slots having the same keyif(smallest.isSameKey(

                heap
                . peek())){Object[]metrics=getMetricsValues (
                    smallest.currentRecord ) ; reuseAggrs.reset();reuseAggrs
                    .aggregate(metrics);
                    do{ResultMergeSlotslot=heap.
                    poll (
                        ) ; open .add(slot);
                        metrics=getMetricsValues(slot.currentRecord
                        ) ; reuseAggrs.aggregate(metrics);
                        }while(smallest.isSameKey(
                    heap . peek()));reuseAggrs.collectStates(metrics);

                    setMetricsValues(smallest.currentRecord,metrics
                    );}output.write (smallest.
                currentCuboidId
                ,smallest.currentRecord);}} privatevoidsetMetricsValues(GTRecord
            record
        ,

        Object [ ]metricsValues) {ImmutableBitSet metrics=getMetricsColumns (record )
            ; if ( reuseMetricsSpace==null){

            reuseMetricsSpace =new ByteArray (record .
                getInfo ( ) .getMaxColumnLength(metrics));}record.setValues(metrics,
            reuseMetricsSpace

            ,metricsValues);}private Object[ ]getMetricsValues(
        GTRecord

        record ){ImmutableBitSet metrics=getMetricsColumns (record )
            ; return record .getValues(metrics,
            reuseMetricsArray );}privateImmutableBitSetgetMetricsColumns (GTRecordrecord
        )

        { // metrics columns always come after dimension columns if(lastCuboidColumnCount ==record .
            getInfo
            ( ). getColumnCount ())returnlastMetricsColumns;intto=record
                . getInfo(

            ) . getColumnCount ();intfrom=to-reuseMetricsArray.
            length ; lastCuboidColumnCount = record .getInfo()
            . getColumnCount ();lastMetricsColumns=newImmutableBitSet(from,
            to ) ; returnlastMetricsColumns;} }privatestatic
            class ResultMergeSlotimplements
        Comparable
    <

    ResultMergeSlot > { CuboidResult splitResult ;IGTScannerscanner; Iterator
        < GTRecord>
        recordIterator ;long
        currentCuboidId;GTRecordcurrentRecord ;public

        ResultMergeSlot (CuboidResult
        splitResult ){

        this .splitResult= splitResult; }
            publicbooleanfetchNext ( )throws
        IOException

        { if (recordIterator== null ) {
            currentCuboidId =splitResult . cuboidId; scanner
                = splitResult .table.scan
                ( new GTScanRequestBuilder().setInfo(splitResult .table.getInfo()).setRanges(null).setDimensions
                        (null).setFilterPushDown(null).createGTScanRequest());recordIterator=scanner.iterator()
                ; } if(recordIterator.hasNext(
            )

            ) {currentRecord=recordIterator.next( )
                ; return true;}else{scanner
                . close(
            ) ; recordIterator
                =null;returnfalse;
                } } @Override
                public intcompareTo
            (
        ResultMergeSlot

        o)
        { long cuboidComp=this .currentCuboidId -
            o . currentCuboidId ;if( cuboidComp !=0)return
            cuboidComp <0 ? -1
                : 1 ; // note GTRecord.equals() don't work because the two GTRecord comes from different GridTable ImmutableBitSet pk= this .currentRecord

            .
            getInfo ( ) .getPrimaryKey();for(inti=0;
            i <pk . trueBitCount () ; i ++){intc= pk.trueBitAt (
                i ) ; intcomp=this.currentRecord.
                get ( c ).compareTo(o.currentRecord.get(c));if(comp!=0)return
                comp ;} return 0;
                    } publicboolean
            isSameKey
            ( ResultMergeSloto
        )

        { if (o== null) return
            false ;else return this.
                compareTo (o
            )
                == 0;}};} 