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

package org.apache.kylin.cube.inmemcubing;

import java.util.concurrent

. BlockingQueue ;publicclassRecordConsumeBlockingQueueController < T>extendsConsumeBlockingQueueController <

    T > {publicfinalInputConverterUnit <T

    > inputConverterUnit;privateRecordConsumeBlockingQueueController(InputConverterUnit <T >inputConverterUnit,BlockingQueue <T > input, int
        batchSize){super (input,
        batchSize); this .inputConverterUnit
    =

    inputConverterUnit ; } private TcurrentObject
    = null ; private volatile booleanifEnd

    =false
    ; @ Overridepublicboolean hasNext (
        ) {// should be idempotentif (
            ifEnd ){
        return
        false ;} if (currentObject !=
            null ){
        return
        true ;}if(!super.hasNext (
            ) ){
        return
        false ; }currentObject=super.next

        ( );if(inputConverterUnit.ifEnd( currentObject
            ) ) {ifEnd
            = true;
        return false ; }elseif(inputConverterUnit.ifCut( currentObject
            ) ) {currentObject
            =null;hasNext
            ( );
        return
        false ;}
    return

    true;
    } @ OverridepublicT next
        ( ){if( ifEnd ( ) ||currentObject
            == null )thrownewIllegalStateException

        ( ) ; Tresult
        = currentObject ;currentObject
        = null;
    return

    result ; }publicboolean ifEnd
        ( ){
    return

    ifEnd ; }publicstatic <T>RecordConsumeBlockingQueueController <T>getQueueController(InputConverterUnit <T >inputConverterUnit,BlockingQueue <T>
        input ) {returnnewRecordConsumeBlockingQueueController<> (inputConverterUnit ,input,
    DEFAULT_BATCH_SIZE

    ) ; }publicstatic <T>RecordConsumeBlockingQueueController <T>getQueueController(InputConverterUnit <T >inputConverterUnit,BlockingQueue <T > input,int
        batchSize ) {returnnewRecordConsumeBlockingQueueController<> (inputConverterUnit ,input,
    batchSize
)
