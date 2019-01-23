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
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kylin.common.util.Dictionary;
import org.apache.kylin.common.util.ImmutableBitSet;
import org.apache.kylin.common.util.MemoryBudgetController;
import org.apache.kylin.common.util.MemoryBudgetController.MemoryWaterLevel;
import org.apache.kylin.common.util.Pair;
import org.apache.kylin.cube.cuboid.Cuboid;
import org.apache.kylin.cube.cuboid.CuboidScheduler;
import org.apache.kylin.cube.gridtable.CubeGridTable;
import org.apache.kylin.cube.inmemcubing.AbstractInMemCubeBuilder;
import org.apache.kylin.cube.inmemcubing.ConcurrentDiskStore;
import org.apache.kylin.cube.inmemcubing.CuboidResult;
import org.apache.kylin.cube.inmemcubing.ICuboidWriter;
import org.apache.kylin.cube.inmemcubing.InMemCubeBuilderUtils;
import org.apache.kylin.cube.inmemcubing.InputConverter;
import org.apache.kylin.cube.inmemcubing.InputConverterUnit;
import org.apache.kylin.cube.inmemcubing.RecordConsumeBlockingQueueController;
import org.apache.kylin.cube.kv.CubeDimEncMap;
import org.apache.kylin.gridtable.GTAggregateScanner;
import org.apache.kylin.gridtable.GTBuilder;
import org.apache.kylin.
gridtable .GTInfo;importorg.apache.kylin.
gridtable .GTRecord;importorg.apache.kylin.
gridtable .GTScanRequest;importorg.apache.kylin.
gridtable .GTScanRequestBuilder;importorg.apache.kylin.
gridtable .GridTable;importorg.apache.kylin.
gridtable .IGTScanner;importorg.apache.kylin.
gridtable .IGTStore;importorg.apache.kylin.metadata.
model .IJoinedFlatTableDesc;importorg.apache.kylin.metadata.
model .MeasureDesc;importorg.apache.kylin.metadata.
model .TblColRef;importorg.
slf4j .Logger;importorg.

slf4j .LoggerFactory;importcom.google.common.
base .Stopwatch;importcom.google.common.

collect
. Lists ; /**
 * Build a cube (many cuboids) in memory. Calculating multiple cuboids at the same time as long as memory permits.
 * Assumes base cuboid fits in memory or otherwise OOM exception will occur.
 */ public class
    InMemCubeBuilder2 extends AbstractInMemCubeBuilder { private staticLoggerlogger=LoggerFactory.getLogger(InMemCubeBuilder2

    .
    class ) ; // by experience private static finaldouble
    DERIVE_AGGR_CACHE_CONSTANT_FACTOR = 0.1 ; private static finaldouble

    DERIVE_AGGR_CACHE_VARIABLE_FACTOR = 0.9;protected finalString
    [ ] metricsAggrFuncs;protected finalMeasureDesc
    [ ] measureDescs ;protected

    final int measureCount;
    private MemoryBudgetController memBudget ;protected
    final long baseCuboidId;

    private CuboidResultbaseResult;private Queue<
    CuboidTask > completedTaskQueue;

    private AtomicInteger taskCuboidCompleted;

    private ICuboidCollectorWithCallBackresultCollector; publicInMemCubeBuilder2 ( CuboidSchedulercuboidScheduler
            ,IJoinedFlatTableDescflatDesc, Map<TblColRef,Dictionary <String >
        >dictionaryMap){ super( cuboidScheduler,flatDesc
        ,dictionaryMap) ; this.measureCount=cubeDesc.getMeasures().
        size() ; this.measureDescs=cubeDesc.getMeasures() .toArray(newMeasureDesc[
        measureCount]); List < String>metricsAggrFuncsList=Lists.

        newArrayList () ; for (int i = 0; i<measureCount ;
            i ++ ) {MeasureDescmeasureDesc=measureDescs
            [i];metricsAggrFuncsList.add(measureDesc.getFunction().getExpression
        (
        )); } this.metricsAggrFuncs=metricsAggrFuncsList .toArray(newString[metricsAggrFuncsList.size(
        )]) ; this.baseCuboidId=Cuboid.getBaseCuboidId
    (

    cubeDesc ) ;}public int
        getBaseResultCacheMB (){return
    baseResult

    . aggrCacheMB ;}private GridTablenewGridTableByCuboidID ( long cuboidID
        ) throws IOException {GTInfoinfo=CubeGridTable.newGTInfo(Cuboid. findForMandatory(cubeDesc
                , cuboidID),new CubeDimEncMap(cubeDesc,

        dictionaryMap
        )
        )
        ; // Below several store implementation are very similar in performance. The ConcurrentDiskStore is the simplest. // MemDiskStore store = new MemDiskStore(info, memBudget == null ? MemoryBudgetController.ZERO_BUDGET : memBudget); // MemDiskStore store = new MemDiskStore(info, MemoryBudgetController.ZERO_BUDGET); IGTStorestore=newConcurrentDiskStore

        ( info ) ; GridTablegridTable=new GridTable(info
        , store)
    ;

    returngridTable
    ; }@Override public <T>voidbuild( BlockingQueue< T>input, InputConverterUnit< T >inputConverterUnit
            , ICuboidWriter output
        )throwsIOException{ NavigableMap< Long , CuboidResult>
                result=buildAndCollect(RecordConsumeBlockingQueueController. getQueueController(inputConverterUnit ,input)
        , null
            ) ;try { for (CuboidResultcuboidResult:result. values
                ()){outputCuboid( cuboidResult.cuboidId, cuboidResult.table
                ,output);cuboidResult.table.
            close
        ( ) ;
            }}finally{output.
        close
    (

    )
    ; }}/**
     * Build all the cuboids and wait for all the tasks finished. 
     * 
     * @param input
     * @param listener
     * @return
     * @throws IOException
     */ private<T> NavigableMap< Long,CuboidResult >buildAndCollect(final RecordConsumeBlockingQueueController<
            T > input, final ICuboidResultListener listener

        ) throws IOException {longstartTime=System.
        currentTimeMillis();logger . info("In Mem Cube Build2 start, "+cubeDesc.getName

        (
        ));// build base cuboid buildBaseCuboid(input

        , listener ) ; ForkJoinWorkerThreadFactoryfactory= new
            ForkJoinWorkerThreadFactory(
            ) { @Overridepublic ForkJoinWorkerThreadnewThread (
                ForkJoinPool pool ) { finalForkJoinWorkerThreadworker=ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread
                (pool);worker . setName("inmem-cubing-cuboid-worker-"+worker.getPoolIndex
                ( ))
            ;
        returnworker
        ; } } ; ForkJoinPoolbuilderPool=new ForkJoinPool( taskThreadCount, factory,null
        , true ) ;ForkJoinTaskrootTask=builderPool .submit( new
            Runnable(
            ) { @Overridepublic void
                run(){
            startBuildFromBaseCuboid
        ();
        }});rootTask.

        join ( ) ;longendTime=System.
        currentTimeMillis();logger . info("In Mem Cube Build2 end, "+cubeDesc . getName ( )+ ", takes " +( endTime -startTime)
        +" ms");logger . info("total CuboidResult count:"+resultCollector.getAllResult().size
        ( ));returnresultCollector.
    getAllResult

    ( ) ;}public ICuboidCollectorWithCallBack
        getResultCollector ()
    {

    return resultCollector;} public <T>CuboidResultbuildBaseCuboid( RecordConsumeBlockingQueueController<
            T > input, final ICuboidResultListener listener
        ) throws IOException {completedTaskQueue=newLinkedBlockingQueue<CuboidTask
        > ( ) ;taskCuboidCompleted=newAtomicInteger

        ( 0 ) ;resultCollector=newDefaultCuboidCollectorWithCallBack

        (listener) ; MemoryBudgetController . MemoryWaterLevelbaseCuboidMemTracker=new
        MemoryWaterLevel();baseCuboidMemTracker.
        markLow ( );baseResult= createBaseCuboid(input

        , baseCuboidMemTracker);if ( baseResult. nRows
            ==0){taskCuboidCompleted.set(cuboidScheduler.getCuboidCount
            ( ))
        ;

        returnbaseResult;}baseCuboidMemTracker.
        markLow() ; baseResult.aggrCacheMB=Math.max(baseCuboidMemTracker. getEstimateMB() ,

        10);// 10 MB at minimal
        makeMemoryBudget ()
    ;

    return baseResult ;}public CuboidResultbuildCuboid ( CuboidTask task
        ) throws IOException {CuboidResultnewCuboid=buildCuboid( task.parent,task
        .childCuboidId);completedTaskQueue.add
        (task);addChildTasks
        ( newCuboid)
    ;

    return newCuboid ;}private CuboidResultbuildCuboid ( CuboidResultparent , long cuboidId
        ) throws IOException { final String consumerName=
        "AggrCache@Cuboid "+cuboidId ; MemoryBudgetController . MemoryConsumerconsumer=newMemoryBudgetController .
            MemoryConsumer(
            ) { @Overridepublic intfreeUp (
                int mb) {
            return

            0;
            // cannot free up on demand } @Overridepublic String
                toString ()
            {
        returnconsumerName

        ;
        }};// reserve memory for aggregation cache, can't be larger than the parentmemBudget. reserveInsist(consumer,parent
        . aggrCacheMB
            ) ;try{return aggregateCuboid(parent
        , cuboidId )
            ;}finally{memBudget. reserve(consumer
        ,
    0

    ) ; }}public boolean
        isAllCuboidDone (){returntaskCuboidCompleted . get()==cuboidScheduler.
    getCuboidCount

    ( ) ;}public void
        startBuildFromBaseCuboid(){addChildTasks
    (

    baseResult ) ;}private voidaddChildTasks (
        CuboidResultparent){ List < Long>children=cuboidScheduler.getSpanningCuboid(parent
        . cuboidId) ; if ( children!=null&&!children. isEmpty
            ()){ List < CuboidTask>childTasks=Lists.newArrayListWithExpectedSize(children.size
            ( )) ; for (Long child
                : children ) { CuboidTasktask=new CuboidTask( parent,child
                ,this);childTasks.add
                (task);task.
            fork
            ( ); } for (CuboidTask childTask
                :childTasks){childTask.
            join
        (
    )

    ; }}}public Queue<CuboidTask >
        getCompletedTaskQueue ()
    {

    return completedTaskQueue ;}private void
        makeMemoryBudget ( ) {intsystemAvailMB=MemoryBudgetController.
        gcAndGetSystemAvailMB();logger . info ( "System avail "+systemAvailMB
        + " MB" ) ;int
        reserve=reserveMemoryMB;logger . info ( "Reserve "+reserve

        + " MB for system basics" ) ; int budget=
        systemAvailMB -reserve ; if(budget< baseResult
            .
            aggrCacheMB ) {// make sure we have base aggr cache as minimalbudget=
            baseResult.aggrCacheMB;logger . warn ( "System avail memory ("
                    + systemAvailMB+" MB) is less than base aggr cache (" + baseResult . aggrCacheMB
                    + " MB) + minimal reservation ("+reserve
        +

        " MB), consider increase JVM heap -Xmx");}logger . info ( "Memory Budget is "+budget
        + " MB" ) ;memBudget=newMemoryBudgetController
    (

    budget );} private <T>CuboidResultcreateBaseCuboid( RecordConsumeBlockingQueueController<
            T>input ,MemoryBudgetController . MemoryWaterLevel baseCuboidMemTracker
        )throwsIOException{logger . info("Calculating base cuboid "

        + baseCuboidId ) ; Stopwatchsw=new
        Stopwatch();sw.
        start ( ) ;GridTablebaseCuboid=newGridTableByCuboidID
        ( baseCuboidId ) ;GTBuilderbaseBuilder=baseCuboid.
        rebuild ( ) ; IGTScannerbaseInput=newInputConverter<>(baseCuboid. getInfo()

        ,input); Pair< ImmutableBitSet , ImmutableBitSet
                >dimensionMetricsBitSet=InMemCubeBuilderUtils. getDimensionAndMetricColumnBitSet(baseCuboidId
        , measureCount ) ; GTScanRequestreq=newGTScanRequestBuilder().setInfo(baseCuboid.getInfo()).setRanges(null).
                setDimensions(null).setAggrGroupBy(dimensionMetricsBitSet.getFirst()).setAggrMetrics(dimensionMetricsBitSet.
                getSecond()).setAggrMetricsFuncs(metricsAggrFuncs).setFilterPushDown(null).
        createGTScanRequest ( ) ; GTAggregateScanneraggregationScanner=new GTAggregateScanner(baseInput
        ,req);aggregationScanner.trackMemoryLevel

        ( baseCuboidMemTracker ) ;int
        count =0 ; for (GTRecord r
            : aggregationScanner) { if( count
                ==0){baseCuboidMemTracker.
            markHigh
            ();}baseBuilder.write
            (r)
        ;
        count++;}aggregationScanner.
        close();baseBuilder.

        close();sw.
        stop();logger . info ( "Cuboid " + baseCuboidId + " has " + count+" rows, build takes "+sw . elapsedMillis()

        + "ms" ) ;intmbEstimateBaseAggrCache =(int)(aggregationScanner
                . getEstimateSizeOfAggrCache()/MemoryBudgetController
        .ONE_MB);logger . info ( "Wild estimate of base aggr cache is "+mbEstimateBaseAggrCache

        + " MB");return updateCuboidResult( baseCuboidId, baseCuboid,count,sw. elapsedMillis(
                ),0,input.inputConverterUnit.ifChange
    (

    ) ) ;}private CuboidResultupdateCuboidResult ( longcuboidId , GridTabletable , intnRows
            , longtimeSpent ,
        int aggrCacheMB){return updateCuboidResult( cuboidId, table, nRows, timeSpent,aggrCacheMB
    ,

    true ) ;}private CuboidResultupdateCuboidResult ( longcuboidId , GridTabletable , intnRows , longtimeSpent
            , intaggrCacheMB ,
        boolean ifCollect) { if ( aggrCacheMB <= 0&& baseResult
            != null ){aggrCacheMB =(int)
                    Math. round ( ( DERIVE_AGGR_CACHE_CONSTANT_FACTOR + DERIVE_AGGR_CACHE_VARIABLE_FACTOR*nRows/ baseResult
                            . nRows)//*baseResult
        .

        aggrCacheMB ) ; } CuboidResultresult=new CuboidResult( cuboidId, table, nRows,timeSpent
        ,aggrCacheMB);taskCuboidCompleted.

        incrementAndGet (); if
            (ifCollect){resultCollector.collectAndNotify
        (
        result );
    }

    return result ;}protected CuboidResultaggregateCuboid ( CuboidResultparent , long cuboidId
        ) throwsIOException{final Pair< ImmutableBitSet , ImmutableBitSet
                >allNeededColumns=InMemCubeBuilderUtils.getDimensionAndMetricColumnBitSet( parent. cuboidId,cuboidId
        , measureCount);returnscanAndAggregateGridTable( parent.table,newGridTableByCuboidID (cuboidId),
                parent. cuboidId,cuboidId,allNeededColumns. getFirst(),allNeededColumns.getSecond
    (

    ) ) ;}private GTAggregateScannerprepareGTAggregationScanner ( GridTablegridTable , longparentId
            , longcuboidId , ImmutableBitSetaggregationColumns , ImmutableBitSet measureColumns
        ) throws IOException {GTInfoinfo=gridTable.
        getInfo ( ) ; GTScanRequestreq=newGTScanRequestBuilder().setInfo(info).setRanges(null).
                setDimensions(null).setAggrGroupBy(aggregationColumns).setAggrMetrics(measureColumns).
                setAggrMetricsFuncs(metricsAggrFuncs).setFilterPushDown(null).
        createGTScanRequest ( ) ;GTAggregateScannerscanner =(GTAggregateScanner)gridTable.scan

        (
        req ); // for child cuboid, some measures don't need aggregation. if( parentId
            !=cuboidId) { boolean [ ]aggrMask=newboolean[measureDescs
            . length] ; for (int i = 0;i< measureDescs.length ;
                i++){ aggrMask [i]=!measureDescs[i].getFunction().getMeasureType().

                onlyAggrInBaseCuboid ();if(!aggrMask [
                    i]){logger.info(measureDescs[i] . toString()
                +
            " doesn't need aggregation."
            );}}scanner.setAggrMask
        (

        aggrMask );
    }

    return scanner ;}protected CuboidResultscanAndAggregateGridTable ( GridTablegridTable , GridTablenewGridTable
            , longparentId , longcuboidId , ImmutableBitSetaggregationColumns , ImmutableBitSet measureColumns
        ) throws IOException { Stopwatchsw=new
        Stopwatch();sw.
        start();logger . info("Calculating cuboid "

        + cuboidId ) ;GTAggregateScannerscanner= prepareGTAggregationScanner( gridTable, parentId,
                cuboidId,aggregationColumns
        , measureColumns ) ;GTBuilderbuilder=newGridTable.

        rebuild ( ) ;ImmutableBitSetallNeededColumns=aggregationColumns.or

        ( measureColumns ) ; GTRecordnewRecord=newGTRecord(newGridTable.getInfo
        ( ) ) ;int
        count =
            0 ;try { for (GTRecord record
                :scanner)
                { count++ ; for (int i = 0;i<allNeededColumns. trueBitCount() ;
                    i ++ ) {intc=allNeededColumns.trueBitAt
                    (i);newRecord. set(i,record.get(
                c
                ));}builder.write
            (
        newRecord ) ;
            }}finally{scanner.
            close();builder.
        close
        ();}sw.
        stop();logger . info ( "Cuboid " + cuboidId + " has " + count+" rows, build takes "+sw . elapsedMillis()

        + "ms");return updateCuboidResult( cuboidId, newGridTable,count,sw. elapsedMillis()
    ,
0
