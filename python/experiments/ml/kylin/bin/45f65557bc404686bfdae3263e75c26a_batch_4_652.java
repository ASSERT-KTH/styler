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

package org.apache.kylin.rest.request;

import java.util.Map

; importcom.google.common.collect.Maps

; public class JobBuildRequest2

    { private longsourceOffsetStart

    ; private longsourceOffsetEnd

    ; privateMap<Integer ,Long > sourcePartitionOffsetStart =Maps.newHashMap()

    ; privateMap<Integer ,Long > sourcePartitionOffsetEnd =Maps.newHashMap()

    ; private StringbuildType

    ; private booleanforce

    ; public longgetSourceOffsetStart( )
        { returnsourceOffsetStart
    ;

    } public voidsetSourceOffsetStart( longsourceOffsetStart )
        {this. sourceOffsetStart =sourceOffsetStart
    ;

    } public longgetSourceOffsetEnd( )
        { returnsourceOffsetEnd
    ;

    } public voidsetSourceOffsetEnd( longsourceOffsetEnd )
        {this. sourceOffsetEnd =sourceOffsetEnd
    ;

    } publicMap<Integer ,Long >getSourcePartitionOffsetStart( )
        { returnsourcePartitionOffsetStart
    ;

    } public voidsetSourcePartitionOffsetStart(Map<Integer ,Long >sourcePartitionOffsetStart )
        {this. sourcePartitionOffsetStart =sourcePartitionOffsetStart
    ;

    } publicMap<Integer ,Long >getSourcePartitionOffsetEnd( )
        { returnsourcePartitionOffsetEnd
    ;

    } public voidsetSourcePartitionOffsetEnd(Map<Integer ,Long >sourcePartitionOffsetEnd )
        {this. sourcePartitionOffsetEnd =sourcePartitionOffsetEnd
    ;

    } public StringgetBuildType( )
        { returnbuildType
    ;

    } public voidsetBuildType( StringbuildType )
        {this. buildType =buildType
    ;

    } public booleanisForce( )
        { returnforce
    ;

    } public voidsetForce( booleanforce )
        {this. force =force
    ;

}
