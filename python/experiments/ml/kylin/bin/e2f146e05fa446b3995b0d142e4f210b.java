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

package org.apache.kylin.cube.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.kylin.metadata.model.TblColRef;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

/**
 */
@SuppressWarnings("serial")
@ JsonAutoDetect ( fieldVisibility=Visibility. NONE , getterVisibility=Visibility. NONE , isGetterVisibility=Visibility.
NONE , setterVisibility = Visibility.NONE)public class

    RowKeyDescimplementsjava.io
    . Serializable{@ JsonProperty(

    "rowkey_columns"
    ) private RowKeyColDesc[
    ] rowkeyColumns ;// computed content
    private longfullMask;private CubeDesccubeDesc ;private
    Map <TblColRef,RowKeyColDesc >columnMap
    ; privateSet< TblColRef>

    shardByColumns ;privateint []columnsNeedIndex ;
        public RowKeyColDesc[
    ]

    getRowKeyColumns ( ){return rowkeyColumns; }
        publicvoidsetCubeDesc ( CubeDesccubeRef
    )

    { this .cubeDesc= cubeRef; }
        public intgetColumnBitIndex(TblColRefcol){returngetColDesc
    (

    col ) .getBitIndex( ); }
        public RowKeyColDesc getColDesc (TblColRefcol){RowKeyColDescdesc
        = columnMap. get (col
            ) ; if(desc == null ) thrownewNullPointerException
        ( "Column "+
    col

    + " does not exist in row key desc" );return desc; }
        public booleanisUseDictionary(TblColRefcol){returngetColDesc
    (

    col ).isUsingDictionary( );} public
        Set <TblColRef
    >

    getShardByColumns ( ){return shardByColumns; }
        publicvoidinit(CubeDesc
        cubeDesc){setCubeDesc
        (cubeDesc);
    buildRowKey

    ( ) ;initColumnsNeedIndex( )
        ;}private void initColumnsNeedIndex ( ){int[]
        tmp = new int[
        100 ]; int x =0 ; for (inti= 0 , n= rowkeyColumns.length ;
            i <n;i++){if("true".equalsIgnoreCase(rowkeyColumns [ i].getIndex())&&rowkeyColumns [
                i].isUsingDictionary ( ))
                {tmp[
            x
        ]

        = i ;x++;}} columnsNeedIndex= ArrayUtils.subarray
    (

    tmp , 0,x); }public void
        setRowkeyColumns(RowKeyColDesc [ ]rowkeyColumns
    )

    {this
    . rowkeyColumns =rowkeyColumns; }
        @ OverridepublicStringtoString(){returnObjects.toStringHelper (this).add("RowKeyColumns",Arrays.toString(
    rowkeyColumns

    ) ) .toString( )
        ; } private voidbuildRowKey(){columnMap
        = new HashMap <>();shardByColumns

        = newHashSet < > () ; for (inti= 0;i <
            rowkeyColumns . length ;i++){
            RowKeyColDescrowKeyColDesc=rowkeyColumns[i] ; rowKeyColDesc . init( rowkeyColumns.length
            -i-1,cubeDesc);columnMap. put(rowKeyColDesc

            . getColRef(),rowKeyColDesc); if
                (rowKeyColDesc.isShardBy()){shardByColumns.add
            (
        rowKeyColDesc

        .getColRef( ) );
        } }this . fullMask =0L ; for (inti=0; i<this .
            rowkeyColumns . length ;i++){intindex=rowkeyColumns
            [i] . getBitIndex ( );
        this
    .

    fullMask |= 1L<<index ;
        } }publiclonggetFullMask
    (

    ) {returnthis .fullMask; }
        public int[

    ]

getColumnsNeedIndex
