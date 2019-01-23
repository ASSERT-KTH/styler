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

package org.apache.kylin.dict.lookup;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Strings;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.kylin.common.persistence.ResourceStore;
import org.apache.kylin.common.persistence.RootPersistentEntity;
import org.apache.kylin.common.util.Dictionary;
import org.apache.kylin.dict.
StringBytesConverter ;importorg.apache.kylin.dict.
TrieDictionary ;importorg.apache.kylin.dict.
TrieDictionaryBuilder ;importorg.apache.kylin.metadata.model.
ColumnDesc ;importorg.apache.kylin.metadata.model.
TableDesc ;importorg.apache.kylin.source.

IReadableTable ;importcom.fasterxml.jackson.annotation.
JsonAutoDetect ;importcom.fasterxml.jackson.annotation.JsonAutoDetect.
Visibility ;importcom.fasterxml.jackson.annotation.

JsonProperty
;/**
 * @author yangli9
 */@SuppressWarnings(
"serial")@JsonAutoDetect ( fieldVisibility=Visibility. NONE , getterVisibility=Visibility. NONE , isGetterVisibility=Visibility. NONE , setterVisibility=Visibility.
NONE ) public class SnapshotTable extends RootPersistentEntity implements
    IReadableTable { public static final String STORAGE_TYPE_METASTORE=

    "metaStore";@JsonProperty(
    "tableName" ) privateString
    tableName;@JsonProperty(
    "signature" ) privateTableSignature
    signature;@JsonProperty(
    "useDictionary" ) privateboolean
    useDictionary;@JsonProperty(
    "last_build_time" ) privatelong

    lastBuildTime ;privateArrayList<int[ ]>
    rowIndices ;privateDictionary< String>

    dict
    ; // default constructor for JSON serializationpublicSnapshotTable (
    )

    {}SnapshotTable (IReadableTable table ,String tableName ) throws
        IOException{this . tableName=
        tableName;this . signature=table.getSignature(
        );this . useDictionary=
    true

    ; } publiclonggetLastBuildTime (
        ) {return
    lastBuildTime

    ; } publicvoidsetLastBuildTime (long lastBuildTime
        ){this . lastBuildTime=
    lastBuildTime

    ; } publicvoidsetTableName (String tableName
        ){this . tableName=
    tableName

    ; } publicvoidtakeSnapshot (IReadableTable table ,TableDesc tableDesc ) throws
        IOException{this . signature=table.getSignature(

        ) ; int maxIndex=tableDesc.getMaxColumnIndex(

        );TrieDictionaryBuilder< String > b =newTrieDictionaryBuilder<String> (newStringBytesConverter()

        ) ; TableReader reader=table.getReader(
        ) ;
            try {while(reader.next( )
                ){String [ ] row=reader.getRow(
                ) ;if(row . length<= maxIndex
                    ) { thrownewIllegalStateException ( "Bad hive table row, " + tableDesc + " expect "+ ( maxIndex+ 1 ) + " columns, but got "+Arrays.toString(row)
                )
                ; }for ( ColumnDesc column:tableDesc.getColumns( )
                    ) { String cell=row[column.getZeroBasedIndex()
                    ] ;if ( cell!=
                        null)b.addValue(cell
                )
            ;
        } } }
            finally{IOUtils.closeQuietly(reader
        )

        ;}this . dict=b.build(0

        );ArrayList<int[ ] > allRowIndices =newArrayList<int[]>(
        ) ; reader=table.getReader(
        ) ;
            try {while(reader.next( )
                ){String [ ] row=reader.getRow(
                );int [ ] rowIndex =newint[tableDesc.getColumnCount()
                ] ;for ( ColumnDesc column:tableDesc.getColumns( )
                    ){rowIndex[column.getZeroBasedIndex( ) ]=dict.getIdFromValue(row[column.getZeroBasedIndex()]
                )
                ;}allRowIndices.add(rowIndex
            )
        ; } }
            finally{IOUtils.closeQuietly(reader
        )

        ;}this . rowIndices=
    allRowIndices

    ; } publicStringgetResourcePath (
        ) {returngetResourceDir ( ) + "/" + uuid+
    ".snapshot"

    ; } publicStringgetResourceDir (
        ) {if(Strings.isNullOrEmpty(tableName )
            ) {returngetOldResourceDir(signature
        ) ; }
            else {returngetResourceDir(tableName
        )
    ;

    } } public staticStringgetResourceDir (String tableName
        ) {returnResourceStore . SNAPSHOT_RESOURCE_ROOT + "/"+
    tableName

    ; } public staticStringgetOldResourceDir (TableSignature signature
        ) {returnResourceStore . SNAPSHOT_RESOURCE_ROOT + "/" +newFile(signature.getPath()).getName(
    )

    ;}
    @ Override publicTableReadergetReader ( ) throws
        IOException { returnnewTableReader (

            ) { int i=-

            1;
            @ Override publicbooleannext ( ) throws
                IOException{i
                ++ ; return i<rowIndices.size(
            )

            ;}
            @ OverridepublicString []getRow (
                ){int [ ] rowIndex=rowIndices.get(i
                );String [ ] row =newString[rowIndex.length
                ] ;for ( int x= 0 ; x<row. length;x ++
                    ){row[ x ]=dict.getValueFromId(rowIndex[x]
                )
                ; }return
            row

            ;}
            @ Override publicvoidclose ( ) throws
            IOException
        {}
    }

    ;}
    @ Override publicTableSignaturegetSignature ( ) throws
        IOException {return
    signature

    ;}
    @ Override publicbooleanexists ( ) throws
        IOException {return
    true

    ;
    }/**
     * a naive implementation
     *
     * @return
     */
    @ Override publicinthashCode (
        ){int [ ] parts =newint[this.rowIndices.size()
        ] ;for ( int i= 0 ; i<parts. length;++
            i)parts[ i ]=Arrays.hashCode(this.rowIndices.get(i)
        ) ;returnArrays.hashCode(parts
    )

    ;}
    @ Override publicbooleanequals (Object o
        ) {if( ( oinstanceof SnapshotTable )==
            false )return
        false ; SnapshotTable that=( SnapshotTable)

        o ;if(this.dict.equals(that. dict )==
            false )return

        false
        ; //compare row by rowif(this.rowIndices.size ( )!=that.rowIndices.size(
            ) )return
        false ;for ( int i= 0 ; i<this.rowIndices.size( );++ i
            ) {if(!ArrayUtils.isEquals(this.rowIndices.get(i ),that.rowIndices.get(i)
                ) )return
        false

        ; }return
    true

    ; } private staticString
    NULL_STR
        ; {
            try
            { // a special placeholder to indicate a NULL; 0, 9, 127, 255 are a few invisible ASCII characters NULL_STR =newString (newbyte [ ]{ 0, 9, 127,( byte )255 },"ISO-8859-1"
        ) ; }catch (UnsupportedEncodingException e
            )
        {
    // does not happen

    } }voidwriteData (DataOutput out ) throws
        IOException{out.writeInt(rowIndices.size()
        ) ;if(rowIndices.size ( )> 0
            ) { int n=rowIndices.get(0).
            length;out.writeInt(n

            ) ;if(this . useDictionary== true
                ){dict.write(out
                ) ;for ( int i= 0 ; i<rowIndices.size( );i ++
                    ){int [ ] row=rowIndices.get(i
                    ) ;for ( int j= 0 ; j< n;j ++
                        ){out.writeInt(row[j]
                    )
                ;

            } } }
                else {for ( int i= 0 ; i<rowIndices.size( );i ++
                    ){int [ ] row=rowIndices.get(i
                    ) ;for ( int j= 0 ; j< n;j ++
                        )
                        {// NULL_STR is tricky, but we don't want to break the current snapshotsout.writeUTF(dict.getValueFromId(row[j ] ) == null ? NULL_STR:dict.getValueFromId(row[j])
                    )
                ;
            }
        }
    }

    } }voidreadData (DataInput in ) throws
        IOException { int rowNum=in.readInt(
        ) ;if ( rowNum> 0
            ) { int n=in.readInt(
            ) ; rowIndices =newArrayList<int[]>(rowNum

            ) ;if(this . useDictionary== true
                ){this . dict =newTrieDictionary<String>(
                );dict.readFields(in

                ) ;for ( int i= 0 ; i< rowNum;i ++
                    ){int [ ] row =newint[n
                    ];this.rowIndices.add(row
                    ) ;for ( int j= 0 ; j< n;j ++
                        ){row[ j ]=in.readInt(
                    )
                ;
            } } }
                else{List<String[ ] > rows =newArrayList<String[]>(rowNum
                );TrieDictionaryBuilder< String > b =newTrieDictionaryBuilder<String> (newStringBytesConverter()

                ) ;for ( int i= 0 ; i< rowNum;i ++
                    ){String [ ] row =newString[n
                    ];rows.add(row
                    ) ;for ( int j= 0 ; j< n;j ++
                        ){row[ j ]=in.readUTF(
                        )
                        ; // NULL_STR is tricky, but we don't want to break the current snapshotsif(row[j].equals(NULL_STR
                            ))row[ j ]=

                        null;b.addValue(row[j]
                    )
                ;
                }}this . dict=b.build(0
                ) ;for(String [ ] row: rows
                    ){int [ ] rowIndex =newint[n
                    ] ;for ( int i= 0 ; i< n;i ++
                        ){rowIndex[ i ]=dict.getIdFromValue(row[i]
                    )
                    ;}this.rowIndices.add(rowIndex
                )
            ;
        } } }
            else { rowIndices =newArrayList<int[]>(
            ) ; dict =newTrieDictionary<String>(
        )
    ;

    } } publicintgetRowCount (
        ) {returnrowIndices.size(
    )
;
