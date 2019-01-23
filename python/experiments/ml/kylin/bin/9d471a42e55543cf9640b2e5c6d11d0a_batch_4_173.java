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

import java.io.Serializable;
import java.util.List;

import org.apache.kylin.cube.cuboid.Cuboid;
import org.apache.kylin.metadata.model.DataModelDesc;
import org.apache.kylin.metadata.model.FunctionDesc;
import org.apache.kylin.metadata.model.IJoinedFlatTableDesc;
import org.apache.kylin.metadata.model.ISegment;
import org.apache.kylin.metadata.model.MeasureDesc;
import org.apache.kylin.metadata.model.SegmentRange;
import org.apache.kylin.metadata.model.TblColRef;

/**
 * An enrich of IJoinedFlatTableDesc for cubes
 */
@SuppressWarnings("serial")
public class CubeJoinedFlatTableEnrich implements IJoinedFlatTableDesc, Serializable {

    private CubeDesc cubeDesc;
    private IJoinedFlatTableDesc flatDesc;
    private int[] rowKeyColumnIndexes;
    // the column index on flat table private int [] [
        ]
        measureColumnIndexes ;// [i] is the i.th measure related column index on flat tablepublicCubeJoinedFlatTableEnrich(IJoinedFlatTableDesc flatDesc ,CubeDesccubeDesc){// != works due to object cache
            if ( cubeDesc.getModel(

        )!=flatDesc . getDataModel(
        ))throw new IllegalArgumentException(
        );this.
    cubeDesc

    =
    cubeDesc ; this.flatDesc =
        flatDesc ; parseCubeDesc ();}// check what columns from hive tables are required, and index themprivatevoid

        parseCubeDesc
        (){Cuboid baseCuboid = Cuboid.getBaseCuboid(cubeDesc)
        ; // build index for rowkey columns List <TblColRef>cuboidColumns=baseCuboid.getColumns();int
        rowkeyColCount = cubeDesc .getRowkey().
        getRowKeyColumns () . length ;rowKeyColumnIndexes = new int[ rowkeyColCount]; for
            ( int i =0;i<rowkeyColCount;
            i++){ TblColRef col=cuboidColumns.get(i
        )

        ;rowKeyColumnIndexes[i ] = flatDesc.getColumnIndex(col)
        ; } List <MeasureDesc>measures=cubeDesc
        . getMeasures ( );intmeasureSize=measures.
        size () ; measureColumnIndexes =new int [ measureSize] []; for
            ( int i =0;i<measureSize;i++){
            FunctionDescfunc=measures . get (i).getFunction();List<
            TblColRef >colRefs = func. getParameter
                ().getColRefs ( );
            if ( colRefs
                ==null){ measureColumnIndexes [ i]=null;}else{measureColumnIndexes
                [ i] = new int[ colRefs . size()];for (intj =
                    0 ; j <colRefs.size();
                    j++){TblColRefc= colRefs .get(j);measureColumnIndexes
                [
            i
        ]
    [

    j ] =flatDesc. getColumnIndex
        ( c)
    ;

    } }}} publicCubeDescgetCubeDesc (
        ) {return
    cubeDesc

    ; }publicint[] getRowKeyColumnIndexes() {
        return rowKeyColumnIndexes;
    }

    publicint
    [ ] []getMeasureColumnIndexes (
        ) {returnmeasureColumnIndexes;}@
    Override

    publicString
    getTableName (){return flatDesc.getTableName (
        ) ;}@OverridepublicList
    <

    TblColRef>
    getAllColumns (){return flatDesc.getAllColumns (
        ) ;}@OverridepublicList
    <

    TblColRef>
    getFactColumns ( ){return flatDesc
        . getFactColumns();}@
    Override

    publicDataModelDesc
    getDataModel ( ){return flatDesc. getDataModel
        ( );}@Overridepublicint
    getColumnIndex

    (TblColRef
    colRef ) {returnflatDesc .
        getColumnIndex (colRef);}@
    Override

    publicSegmentRange
    getSegRange ( ){return flatDesc
        . getSegRange();}@
    Override

    publicTblColRef
    getDistributedBy ( ){return flatDesc
        . getDistributedBy();}@
    Override

    publicISegment
    getSegment ( ){return flatDesc
        . getSegment();}@
    Override

    publicboolean
    useAlias ( ){return flatDesc
        . useAlias();}@
    Override

public
