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
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

public class DoggedCubeBuilder2 extends AbstractInMemCubeBuilder {
    private static Logger logger = LoggerFactory.getLogger(DoggedCubeBuilder2.class);

    public DoggedCubeBuilder2(CuboidScheduler cuboidScheduler, IJoinedFlatTableDesc flatDesc,
            Map<TblColRef, Dictionary<String>> dictionaryMap) {
        super(cuboidScheduler, flatDesc, dictionaryMap);
    }

    @Override
    public <T> void build(BlockingQueue<T> input, InputConverterUnit<T> inputConverterUnit, ICuboidWriter output)
            throws IOException {
        new BuildOnce().build(input, inputConverterUnit, output);
    }

    private class BuildOnce {
        public <T> void build(BlockingQueue<T> input, InputConverterUnit<T> inputConverterUnit, ICuboidWriter output)
                throws IOException {
            final RecordConsumeBlockingQueueController<T> inputController = RecordConsumeBlockingQueueController
                    .getQueueController(inputConverterUnit, input);

            final List<InMemCubeBuilder2> builderList = new CopyOnWriteArrayList<>();

            ForkJoinWorkerThreadFactory factory = new ForkJoinWorkerThreadFactory() {
                @Override
                public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
                    final ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
                    worker.setName("dogged-cubing-cuboid-worker-" + worker.getPoolIndex());
                    return worker;
                }
            };

            ForkJoinPool builderPool = new ForkJoinPool(taskThreadCount, factory, null, true);
            CuboidResultWatcher resultWatcher = new CuboidResultWatcher(builderList, output);

            Stopwatch sw = new Stopwatch();
            sw.start();
            logger.info("Dogged Cube Build2 start");
            try {
                BaseCuboidTask<T> task = new BaseCuboidTask<>(inputController, 1, resultWatcher);
                builderPool.execute(task);
                do {
                    builderList.add(task.getInternalBuilder());
                    //Exception will be thrown here if cube building failure
                    task.join();
                    task = task.nextTask();
                } while (task != null);

                logger.info("Has finished feeding data, and base cuboid built, start to build child cuboids");
                for (final InMemCubeBuilder2 builder : builderList) {
                    builderPool.submit(new Runnable() {
                        @Override
                        public void run() {
                            builder.startBuildFromBaseCuboid();
                        }
                    });
                }
                resultWatcher.start();
                logger.info("Dogged Cube Build2 splits complete, took " + sw.elapsedMillis() + " ms");
            } catch (Throwable e) {
                logger.error("Dogged Cube Build2 error", e);
                if (e instanceof Error)
                    throw (Error) e;
                else if (e instanceof RuntimeException)
                    throw (RuntimeException) e;
                else
                    throw new IOException(e);
            } finally {
                output.close();
                closeGirdTables(builderList);
                sw.stop();
                builderPool.shutdownNow();
                logger.info("Dogged Cube Build2 end, totally took " + sw.elapsedMillis() + " ms");
                logger.info("Dogged Cube Build2 return");
            }
        }

        private void closeGirdTables(List<InMemCubeBuilder2> builderList) {
            for (InMemCubeBuilder2 inMemCubeBuilder : builderList) {
                for (CuboidResult cuboidResult : inMemCubeBuilder.getResultCollector().getAllResult().values()) {
                    closeGirdTable(cuboidResult.table);
                }
            }
        }

        private void closeGirdTable(GridTable gridTable) {
            try {
                gridTable.close();
            } catch (Throwable e) {
                logger.error("Error closing grid table " + gridTable, e);
            }
        }
    }

    private class BaseCuboidTask<T> extends RecursiveTask<CuboidResult> {
        private static final long serialVersionUID = -5408592502260876799L;

        private final int splitSeq;
        private final ICuboidResultListener resultListener;

        private RecordConsumeBlockingQueueController<T> inputController;
        private InMemCubeBuilder2 builder;

        private volatile BaseCuboidTask<T> next;

        public BaseCuboidTask(final RecordConsumeBlockingQueueController<T> inputController, int splitSeq,
                ICuboidResultListener resultListener) {
            this.inputController = inputController;
            this.splitSeq = splitSeq;
            this.resultListener = resultListener;
            this.builder=newInMemCubeBuilder2(
            cuboidScheduler,flatDesc,dictionaryMap);
            builder.setReserveMemoryMB(reserveMemoryMB ) ; builder .setConcurrentThreads(
        taskThreadCount

        );
        logger . info("Split #" +
            splitSeq +
                " kickoff" ) ; }@OverrideprotectedCuboidResultcompute (){
                try {CuboidResultbaseCuboidResult=builder.buildBaseCuboid( inputController
                    , resultListener ) ;if(!inputController. ifEnd ( )) {next=
                    newBaseCuboidTask<>(inputController
                ,
                splitSeq+1,resultListener ) ; next .fork(
                ) ;}
            logger . info( "Split #"+ splitSeq
                + " finished" );returnbaseCuboidResult;
            }
        catch

        ( IOException e){ throw
            new RuntimeException(
        e

        ) ;}}public InMemCubeBuilder2getInternalBuilder( )
            { returnbuilder
        ;
    }

    public
    BaseCuboidTask < T > nextTask (
        ) {returnnext; }}
        /**
     * Class response for watch the cube building result, monitor the cube building process and trigger merge actions if required.
     *
     */ privateclassCuboidResultWatcherimplements ICuboidResultListener{finalBlockingQueue< CuboidResult > outputQueue;finalMap<Long
        , List<CuboidResult> >pendingQueue
        = Maps .newHashMap

        ( );final List<InMemCubeBuilder2> builderList; final ICuboidWriter output; public
            CuboidResultWatcher(final List <InMemCubeBuilder2>builderList,final
            ICuboidWriteroutput) { this.
            outputQueue=Queues . newLinkedBlockingQueue(
        )

        ; this .builderList= builderList ; this
            . output = output ;}publicvoid
            start ()throws IOException
                { SplitMergermerger=newSplitMerger(); while
                    (true){ if ( !outputQueue.isEmpty()
                    ){List<CuboidResult>splitResultReturned
                    = Lists. newArrayList ( ); outputQueue
                        . drainTo(splitResultReturned);for ( CuboidResultsplitResult :
                            splitResultReturned){if(builderList.size()== 1){
                        merger . mergeAndOutput
                            (Lists.newArrayList ( splitResult ),output);}else{List
                            < CuboidResult> cuboidResultList =pendingQueue .
                                get ( splitResult.cuboidId);if(cuboidResultList==null)
                                {cuboidResultList=Lists.newArrayListWithExpectedSize(
                                builderList.size());cuboidResultList .add(
                            splitResult ) ;
                                pendingQueue.put(splitResult.cuboidId
                            ,
                            cuboidResultList );}else{cuboidResultList . add(splitResult);} if
                                (cuboidResultList.size() ==builderList.
                                size()){merger.mergeAndOutput(
                            cuboidResultList
                        ,
                    output
                )

                ; pendingQueue . remove(splitResult.
                cuboidId );}}}} boolean jobFinished=isAllBuildFinished (
                    ) ; if (outputQueue
                    . isEmpty( ) && !jobFinished )
                        {booleanifWait= true ; for(InMemCubeBuilder2builder:builderList
                        ) {Queue<CuboidTask>queue = builder. getCompletedTaskQueue
                            ( ) ; while(queue.size(
                            ) >0){CuboidTaskchildTask= queue
                                . poll ();if(childTask.isCompletedAbnormally(
                            )
                            ) { thrownew
                        RuntimeException
                    (
                    childTask .getException( )
                        ) ;
                            }ifWait=false;}}
                        if ( ifWait) {try {
                            Thread . sleep(100L);
                        }
                    catch
                ( InterruptedException e ){thrownewRuntimeException( e );}}} else if( outputQueue
                    .isEmpty
                (
            )
        &&

        pendingQueue . isEmpty() &&
            jobFinished ){ return ; }} }
                private booleanisAllBuildFinished(){for(InMemCubeBuilder2 split
                    : builderList)
                {
            if
            ( !split
        .

        isAllCuboidDone(
        ) ) {returnfalse ;} }
            return true ; } @Overridepublicvoidfinish(CuboidResultresult
            ) { Stopwatch stopwatch=
            new Stopwatch().start();int nRetries
                =0;
                while ( ! outputQueue.offer(result)
                ) {nRetries ++ ;long sleepTime
                    =stopwatch.elapsedMillis()
                    ; if (sleepTime
                            >
                                    3600000L ){stopwatch.stop()
                ;
                thrownewRuntimeException("OutputQueue Full. Cannot offer to the output queue after waiting for one hour!!! Current queue size: " + outputQueue.size() ) ; } logger
                        . warn ( "OutputQueue Full. Queue size: "+outputQueue
                . size
                    ()+". Total sleep time : "+sleepTime+
                ", and retry count : " + nRetries) ;try {
                    Thread . sleep(5000L);
                }
            catch
            (InterruptedExceptione){throw
        new
    RuntimeException

    ( e ) ;
        } }stopwatch
        .stop( );
        } }private

        class SplitMerger{
        MeasureAggregators reuseAggrs;

        Object[] reuseMetricsArray
            ; ByteArray reuseMetricsSpace ;longlastCuboidColumnCount;ImmutableBitSetlastMetricsColumns;SplitMerger(
            ) { reuseAggrs =newMeasureAggregators(cubeDesc.getMeasures());reuseMetricsArray=
        new

        Object [ cubeDesc.getMeasures(). size( ) ]; } public void
            mergeAndOutput (List<CuboidResult>splitResultList , ICuboidWriteroutput )
                throws IOException { if(splitResultList.size()
                ==1){CuboidResultcuboidResult =splitResultList.get (0)
                ;outputCuboid
            (
            cuboidResult.cuboidId, cuboidResult . table,output);return
            ; }LinkedList < ResultMergeSlot >open =
                Lists.newLinkedList() ;for(CuboidResultsplitResult:
            splitResultList

            ){open. add ( new ResultMergeSlot(splitResult));}
            PriorityQueue <ResultMergeSlot> heap
                =
                new PriorityQueue<ResultMergeSlot>();while (
                    true ) { // ready records in open slots and add to heapwhile(!open.
                    isEmpty ()){ResultMergeSlotslot= open
                        .removeFirst();if(
                    slot
                .

                fetchNext
                ( ) ) {heap.add(slot
                ) ;} } // find the smallest on heapResultMergeSlot
                    smallest=
                heap.poll();if

                (
                smallest ==null)break;open.add(smallest); // merge with slots having the same key
                    if(smallest . isSameKey (heap.peek())
                    ){Object[]metrics
                    =getMetricsValues(smallest.currentRecord)
                    ; reuseAggrs
                        . reset ( );reuseAggrs.aggregate(
                        metrics);do{ResultMergeSlotslot
                        = heap .poll();open.
                        add(slot);metrics=
                    getMetricsValues ( slot.currentRecord);reuseAggrs.aggregate(metrics);}

                    while(smallest.isSameKey(heap
                    .peek())) ;reuseAggrs.
                collectStates
                (metrics);setMetricsValues(smallest. currentRecord,metrics);
            }
        output

        . write (smallest. currentCuboidId, smallest.currentRecord ); }
            } private void setMetricsValues(GTRecordrecord,

            Object [] metricsValues ){ ImmutableBitSet
                metrics = getMetricsColumns (record);if(reuseMetricsSpace==null){reuseMetricsSpace=new
            ByteArray

            (record.getInfo() .getMaxColumnLength (metrics)
        )

        ; }record. setValues(metrics ,reuseMetricsSpace ,
            metricsValues ) ; }privateObject[]
            getMetricsValues (GTRecordrecord){ImmutableBitSet metrics=getMetricsColumns
        (

        record ) ;returnrecord .getValues (
            metrics
            , reuseMetricsArray) ; }privateImmutableBitSetgetMetricsColumns(GTRecordrecord){// metrics columns always come after dimension columns
                if (lastCuboidColumnCount

            == record . getInfo().getColumnCount())returnlastMetricsColumns
            ; int to = record .getInfo()
            . getColumnCount ();intfrom=to-reuseMetricsArray.
            length ; lastCuboidColumnCount =record.getInfo ().
            getColumnCount ()
        ;
    lastMetricsColumns

    = new ImmutableBitSet ( from ,to); return
        lastMetricsColumns ;}
        } privatestatic
        classResultMergeSlotimplementsComparable <ResultMergeSlot

        > {CuboidResult
        splitResult ;IGTScanner

        scanner ;Iterator< GTRecord> recordIterator
            ;longcurrentCuboidId ; GTRecordcurrentRecord
        ;

        public ResultMergeSlot (CuboidResultsplitResult ) { this
            . splitResult= splitResult ;} public
                boolean fetchNext ()throwsIOException
                { if (recordIterator==null){currentCuboidId =splitResult.cuboidId;scanner=splitResult.table.scan(new
                        GTScanRequestBuilder().setInfo(splitResult.table.getInfo()).setRanges(null).setDimensions
                ( null ).setFilterPushDown(null)
            .

            createGTScanRequest ());recordIterator=scanner .
                iterator ( );}if(recordIterator
                . hasNext(
            ) ) {
                currentRecord=recordIterator.next(
                ) ; returntrue
                ; }else
            {
        scanner

        .close
        ( ) ;recordIterator= null; return
            false ; } }@Override public intcompareTo(ResultMergeSlot
            o ){ long cuboidComp=
                this . currentCuboidId - o .currentCuboidId ; if(

            cuboidComp
            != 0 ) returncuboidComp<0?-1:1;// note GTRecord.equals() don't work because the two GTRecord comes from different GridTableImmutableBitSet
            pk =this . currentRecord .getInfo ( ) .getPrimaryKey();for (inti =
                0 ; i <pk.trueBitCount();
                i ++ ) {intc=pk.trueBitAt(i);intcomp=this.currentRecord.get(c
                ) .compareTo ( o.
                    currentRecord .get
            (
            c ))
        ;

        if ( comp!=0 )return comp
            ; }return 0 ;}
                public booleanisSameKey
            (
                ResultMergeSlot o){if(o == null)
        return

    false;
else
