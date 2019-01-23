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
    private int[] rowKeyColumnIndexes; // the column index on flat table
    private int[][] measureColumnIndexes;

    // [i] is the i.th measure related column index on flat table publicCubeJoinedFlatTableEnrich(IJoinedFlatTableDescflatDesc ,CubeDesc cubeDesc
        )
        { // != works due to object cacheif(cubeDesc.getModel ( )!=flatDesc.getDataModel(
            ) ) thrownewIllegalArgumentException(

        );this . cubeDesc=
        cubeDesc;this . flatDesc=
        flatDesc;parseCubeDesc(
    )

    ;
    } // check what columns from hive tables are required, and index them privatevoidparseCubeDesc (
        ) { Cuboid baseCuboid=Cuboid.getBaseCuboid(cubeDesc

        )
        ;// build index for rowkey columnsList< TblColRef > cuboidColumns=baseCuboid.getColumns(
        ) ; int rowkeyColCount=cubeDesc.getRowkey().getRowKeyColumns().
        length ; rowKeyColumnIndexes =newint[rowkeyColCount
        ] ;for ( int i= 0 ; i< rowkeyColCount;i ++
            ) { TblColRef col=cuboidColumns.get(i
            );rowKeyColumnIndexes[ i ]=flatDesc.getColumnIndex(col
        )

        ;}List< MeasureDesc > measures=cubeDesc.getMeasures(
        ) ; int measureSize=measures.size(
        ) ; measureColumnIndexes =newint[measureSize][
        ] ;for ( int i= 0 ; i< measureSize;i ++
            ) { FunctionDesc func=measures.get(i).getFunction(
            );List< TblColRef > colRefs=func.getParameter().getColRefs(
            ) ;if ( colRefs== null
                ){measureColumnIndexes[ i ]=
            null ; }
                else{measureColumnIndexes[ i ] =newint[colRefs.size()
                ] ;for ( int j= 0 ; j<colRefs.size( );j ++
                    ) { TblColRef c=colRefs.get(j
                    );measureColumnIndexes[i][ j ]=flatDesc.getColumnIndex(c
                )
            ;
        }
    }

    } } publicCubeDescgetCubeDesc (
        ) {return
    cubeDesc

    ; }publicint []getRowKeyColumnIndexes (
        ) {return
    rowKeyColumnIndexes

    ; }publicint[] []getMeasureColumnIndexes (
        ) {return
    measureColumnIndexes

    ;}
    @ Override publicStringgetTableName (
        ) {returnflatDesc.getTableName(
    )

    ;}
    @ OverridepublicList< TblColRef>getAllColumns (
        ) {returnflatDesc.getAllColumns(
    )

    ;}
    @ OverridepublicList< TblColRef>getFactColumns (
        ) {returnflatDesc.getFactColumns(
    )

    ;}
    @ Override publicDataModelDescgetDataModel (
        ) {returnflatDesc.getDataModel(
    )

    ;}
    @ Override publicintgetColumnIndex (TblColRef colRef
        ) {returnflatDesc.getColumnIndex(colRef
    )

    ;}
    @ Override publicSegmentRangegetSegRange (
        ) {returnflatDesc.getSegRange(
    )

    ;}
    @ Override publicTblColRefgetDistributedBy (
        ) {returnflatDesc.getDistributedBy(
    )

    ;}
    @ Override publicISegmentgetSegment (
        ) {returnflatDesc.getSegment(
    )

    ;}
    @ Override publicbooleanuseAlias (
        ) {returnflatDesc.useAlias(
    )

    ;}
    @ Override publicTblColRefgetClusterBy (
        ) {returnflatDesc.getClusterBy(
    )

;
