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

import com.fasterxml.jackson.
annotation .JsonAutoDetect;importcom
. fasterxml.jackson.annotation.JsonAutoDetect.Visibility;importcom
. fasterxml.jackson.annotation.JsonProperty;importcom
. google.common.base.Objects;/**
 */@

SuppressWarnings
("serial")@JsonAutoDetect
(fieldVisibility=Visibility . NONE,getterVisibility= Visibility . NONE,isGetterVisibility= Visibility . NONE,setterVisibility= Visibility . NONE)publicclass
RowKeyDesc implements java . io.Serializable{@ JsonProperty

    ("rowkey_columns")privateRowKeyColDesc
    [ ]rowkeyColumns; // computed contentprivate

    long
    fullMask ; privateCubeDesc
    cubeDesc ; privateMap
    < TblColRef,RowKeyColDesc> columnMap; privateSet
    < TblColRef>shardByColumns; privateint
    [ ]columnsNeedIndex; publicRowKeyColDesc

    [ ]getRowKeyColumns( ){return rowkeyColumns
        ; }public
    void

    setCubeDesc ( CubeDesccubeRef) {this .
        cubeDesc=cubeRef ; }public
    int

    getColumnBitIndex ( TblColRefcol) {return getColDesc
        ( col).getBitIndex();}public
    RowKeyColDesc

    getColDesc ( TblColRefcol) {RowKeyColDesc desc
        = columnMap . get(col);if(
        desc ==null ) thrownew
            NullPointerException ( "Column "+col + " does not exist in row key desc" ) ;returndesc
        ; }public
    boolean

    isUseDictionary ( TblColRefcol) {return getColDesc
        ( col).isUsingDictionary();}public
    Set

    < TblColRef>getShardByColumns( ){return shardByColumns
        ; }public
    void

    init ( CubeDesccubeDesc) {setCubeDesc (
        cubeDesc);buildRowKey(
        );initColumnsNeedIndex(
        );}private
    void

    initColumnsNeedIndex ( ){int [
        ]tmp= new int [ 100];intx
        = 0 ; for(
        int i= 0 , n= rowkeyColumns . length;i< n ; i++ ){if (
            "true" .equalsIgnoreCase(rowkeyColumns[i].getIndex())&&rowkeyColumns [ i].isUsingDictionary()){tmp [
                x]=i ; x++
                ;}}
            columnsNeedIndex
        =

        ArrayUtils . subarray(tmp,0, x) ;}public
    void

    setRowkeyColumns ( RowKeyColDesc[]rowkeyColumns) {this .
        rowkeyColumns=rowkeyColumns ; }@
    Override

    publicString
    toString ( ){return Objects
        . toStringHelper(this).add("RowKeyColumns",Arrays. toString(rowkeyColumns)).toString();}private
    void

    buildRowKey ( ){columnMap =
        new HashMap < >();shardByColumns=
        new HashSet < >();for(

        int i= 0 ; i< rowkeyColumns . length;i++ ){RowKeyColDesc rowKeyColDesc
            = rowkeyColumns [ i];rowKeyColDesc.
            init(rowkeyColumns.length-i - 1 , cubeDesc) ;columnMap.
            put(rowKeyColDesc.getColRef(),rowKeyColDesc) ;if(

            rowKeyColDesc .isShardBy()){shardByColumns .
                add(rowKeyColDesc.getColRef());}}
            this
        .

        fullMask=0L ; for(
        int i= 0 ; i< this . rowkeyColumns.length;i++ ){int index
            = rowkeyColumns [ i].getBitIndex();this.
            fullMask|=1L << index ; }}
        public
    long

    getFullMask ( ){return this
        . fullMask;}public
    int

    [ ]getColumnsNeedIndex( ){return columnsNeedIndex
        ; }}

    