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
    // the column index on flat table private
    int[][] measureColumnIndexes; // [i] is the i.th measure related column index on flat table public CubeJoinedFlatTableEnrich( IJoinedFlatTableDesc
        flatDesc
        , CubeDesccubeDesc){// != works due to object cacheif ( cubeDesc.getModel()!=
            flatDesc . getDataModel())

        thrownewIllegalArgumentException ( );
        this.cubeDesc = cubeDesc;
        this.flatDesc=
    flatDesc

    ;
    parseCubeDesc ( );} // check what columns from hive tables are required, and index them
        private void parseCubeDesc (){CuboidbaseCuboid=Cuboid

        .
        getBaseCuboid(cubeDesc) ; // build index for rowkey columns List<TblColRef>cuboidColumns=
        baseCuboid . getColumns ();introwkeyColCount=cubeDesc.getRowkey().
        getRowKeyColumns ( ) .length;rowKeyColumnIndexes=
        new int[ rowkeyColCount ] ;for ( int i= 0;i <
            rowkeyColCount ; i ++){TblColRefcol=cuboidColumns
            .get(i ) ;rowKeyColumnIndexes[i]=flatDesc
        .

        getColumnIndex(col) ; } List<MeasureDesc>measures=
        cubeDesc . getMeasures ();intmeasureSize=
        measures . size ();measureColumnIndexes=newint
        [ measureSize] [ ] ;for ( int i= 0;i <
            measureSize ; i ++){FunctionDescfunc=measures.get(i
            ).getFunction( ) ; List<TblColRef>colRefs=func.getParameter(
            ) .getColRefs ( ); if
                (colRefs==null ) {measureColumnIndexes
            [ i ]
                =null;} else { measureColumnIndexes[i]=newint[colRefs
                . size( ) ] ;for ( int j=0;j< colRefs.size (
                    ) ; j ++){TblColRefc=colRefs
                    .get(j);measureColumnIndexes [ i][j]=flatDesc
                .
            getColumnIndex
        (
    c

    ) ; }}} }
        public CubeDescgetCubeDesc
    (

    ) {returncubeDesc ;}public int
        [ ]getRowKeyColumnIndexes
    (

    ) {returnrowKeyColumnIndexes;} publicint[ ]
        [ ]getMeasureColumnIndexes
    (

    ){
    return measureColumnIndexes ;}@ Override
        public StringgetTableName(){return
    flatDesc

    .getTableName
    ( );}@ OverridepublicList <
        TblColRef >getAllColumns(){return
    flatDesc

    .getAllColumns
    ( );}@ OverridepublicList <
        TblColRef >getFactColumns(){return
    flatDesc

    .getFactColumns
    ( ) ;}@ Override
        public DataModelDescgetDataModel(){return
    flatDesc

    .getDataModel
    ( ) ;}@ Overridepublic int
        getColumnIndex (TblColRefcolRef){returnflatDesc
    .

    getColumnIndex(
    colRef ) ;}@ Override
        public SegmentRangegetSegRange(){return
    flatDesc

    .getSegRange
    ( ) ;}@ Override
        public TblColRefgetDistributedBy(){return
    flatDesc

    .getDistributedBy
    ( ) ;}@ Override
        public ISegmentgetSegment(){return
    flatDesc

    .getSegment
    ( ) ;}@ Override
        public booleanuseAlias(){return
    flatDesc

    .useAlias
    ( ) ;}@ Override
        public TblColRefgetClusterBy(){return
    flatDesc

.
