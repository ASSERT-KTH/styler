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
import org.apache.kylin.dict.StringBytesConverter;
import org.apache.kylin.dict.TrieDictionary;
import org.apache.kylin.dict.TrieDictionaryBuilder;
import org.apache.kylin.metadata.model.ColumnDesc;
import org.apache.kylin.metadata.model.TableDesc;
import org.apache.kylin.source.IReadableTable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author yangli9
 */
@SuppressWarnings("serial")
@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class SnapshotTable extends RootPersistentEntity implements IReadableTable {
    public static final String STORAGE_TYPE_METASTORE = "metaStore";

    @JsonProperty("tableName")
    private String tableName;
    @JsonProperty("signature")
    private TableSignature signature;
    @JsonProperty("useDictionary")
    private boolean useDictionary;
    @JsonProperty("last_build_time")
    private long lastBuildTime;

    private ArrayList<int[]> rowIndices;
    private Dictionary<String> dict;

    // default constructor for JSON serialization
    public SnapshotTable() {
    }

    SnapshotTable(IReadableTable table, String tableName) throws IOException {
        this.tableName = tableName;
        this.signature = table.getSignature();
        this.useDictionary = true;
    }

    public long getLastBuildTime() {
        return lastBuildTime;
    }

    public void setLastBuildTime(long lastBuildTime) {
        this.lastBuildTime = lastBuildTime;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void takeSnapshot(IReadableTable table, TableDesc tableDesc) throws IOException {
        this.signature = table.getSignature();

        int maxIndex = tableDesc.getMaxColumnIndex();

        TrieDictionaryBuilder<String> b = new TrieDictionaryBuilder<String>(new StringBytesConverter());

        TableReader reader = table.getReader();
        try {
            while (reader.next()) {
                String[] row = reader.getRow();
                if (row.length <= maxIndex) { throw
                    new IllegalStateException ("Bad hive table row, "+ tableDesc + " expect " + ( maxIndex+ 1 )+ " columns, but got " + Arrays .toString(row));}
                for
                ( ColumnDesccolumn : tableDesc .getColumns()){ String
                    cell = row [column.getZeroBasedIndex()];if
                    ( cell!= null )b
                        .addValue(cell);}
                }
            }
        finally { IOUtils
            .closeQuietly(reader);}
        this

        .dict= b .build(0);ArrayList

        <int[]>allRowIndices = new ArrayList <int[]>();reader
        = table .getReader();try
        { while
            ( reader.next()){ String
                []row = reader .getRow();int
                []rowIndex = new int [tableDesc.getColumnCount()];for
                ( ColumnDesccolumn : tableDesc .getColumns()){ rowIndex
                    [column.getZeroBasedIndex()]= dict .getIdFromValue(row[column.getZeroBasedIndex()]);}
                allRowIndices
                .add(rowIndex);}
            }
        finally { IOUtils
            .closeQuietly(reader);}
        this

        .rowIndices= allRowIndices ;}
    public

    String getResourcePath (){ return
        getResourceDir ()+ "/" + uuid + ".snapshot" ;}
    public

    String getResourceDir (){ if
        ( Strings.isNullOrEmpty(tableName)){ return
            getOldResourceDir (signature);}
        else { return
            getResourceDir (tableName);}
        }
    public

    static String getResourceDir (StringtableName ){ return
        ResourceStore .SNAPSHOT_RESOURCE_ROOT+ "/" + tableName ;}
    public

    static String getOldResourceDir (TableSignaturesignature ){ return
        ResourceStore .SNAPSHOT_RESOURCE_ROOT+ "/" + new File (signature.getPath()).getName();}
    @

    Overridepublic
    TableReader getReader ()throws IOException { return
        new TableReader (){ int

            i = - 1;@

            Overridepublic
            boolean next ()throws IOException { i
                ++;return
                i < rowIndices .size();}
            @

            Overridepublic
            String []getRow (){ int
                []rowIndex = rowIndices .get(i);String
                []row = new String [rowIndex.length];for
                ( intx = 0 ;x < row .length;x ++){ row
                    [x]= dict .getValueFromId(rowIndex[x]);}
                return
                row ;}
            @

            Overridepublic
            void close ()throws IOException { }
            }
        ;}
    @

    Overridepublic
    TableSignature getSignature ()throws IOException { return
        signature ;}
    @

    Overridepublic
    boolean exists ()throws IOException { return
        true ;}
    /**
     * a naive implementation
     *
     * @return
     */

    @
    Overridepublic
    int hashCode (){ int
        []parts = new int [this.rowIndices.size()];for
        ( inti = 0 ;i < parts .length;++ i)parts
            [i]= Arrays .hashCode(this.rowIndices.get(i));return
        Arrays .hashCode(parts);}
    @

    Overridepublic
    boolean equals (Objecto ){ if
        ( (oinstanceof SnapshotTable )== false )return
            false ;SnapshotTable
        that = ( SnapshotTable)o ;if

        ( this.dict.equals(that.dict)== false )return
            false ;//compare row by row

        if
        ( this.rowIndices.size()!= that .rowIndices.size())return
            false ;for
        ( inti = 0 ;i < this .rowIndices.size();++ i){ if
            ( !ArrayUtils.isEquals(this.rowIndices.get(i),that .rowIndices.get(i)))return
                false ;}
        return

        true ;}
    private

    static String NULL_STR ;{
    try
        { // a special placeholder to indicate a NULL; 0, 9, 127, 255 are a few invisible ASCII characters
            NULL_STR
            = new String (newbyte []{ 0 ,9 ,127 ,( byte)255 } ,"ISO-8859-1" );}
        catch ( UnsupportedEncodingExceptione ){ // does not happen
            }
        }
    void

    writeData (DataOutputout )throws IOException { out
        .writeInt(rowIndices.size());if
        ( rowIndices.size()> 0 ){ int
            n = rowIndices .get(0).length;out
            .writeInt(n);if

            ( this.useDictionary== true ){ dict
                .write(out);for
                ( inti = 0 ;i < rowIndices .size();i ++){ int
                    []row = rowIndices .get(i);for
                    ( intj = 0 ;j < n ;j ++){ out
                        .writeInt(row[j]);}
                    }
                }

            else { for
                ( inti = 0 ;i < rowIndices .size();i ++){ int
                    []row = rowIndices .get(i);for
                    ( intj = 0 ;j < n ;j ++){ // NULL_STR is tricky, but we don't want to break the current snapshots
                        out
                        .writeUTF(dict.getValueFromId(row[j])== null ? NULL_STR : dict .getValueFromId(row[j]));}
                    }
                }
            }
        }
    void

    readData (DataInputin )throws IOException { int
        rowNum = in .readInt();if
        ( rowNum> 0 ){ int
            n = in .readInt();rowIndices
            = new ArrayList <int[]>(rowNum);if

            ( this.useDictionary== true ){ this
                .dict= new TrieDictionary <String>();dict
                .readFields(in);for

                ( inti = 0 ;i < rowNum ;i ++){ int
                    []row = new int [n];this
                    .rowIndices.add(row);for
                    ( intj = 0 ;j < n ;j ++){ row
                        [j]= in .readInt();}
                    }
                }
            else { List
                <String[]>rows = new ArrayList <String[]>(rowNum);TrieDictionaryBuilder
                <String>b = new TrieDictionaryBuilder <String>(newStringBytesConverter ());for

                ( inti = 0 ;i < rowNum ;i ++){ String
                    []row = new String [n];rows
                    .add(row);for
                    ( intj = 0 ;j < n ;j ++){ row
                        [j]= in .readUTF();// NULL_STR is tricky, but we don't want to break the current snapshots
                        if
                        ( row[j].equals(NULL_STR))row
                            [j]= null ;b

                        .addValue(row[j]);}
                    }
                this
                .dict= b .build(0);for
                ( String[]row : rows ){ int
                    []rowIndex = new int [n];for
                    ( inti = 0 ;i < n ;i ++){ rowIndex
                        [i]= dict .getIdFromValue(row[i]);}
                    this
                    .rowIndices.add(rowIndex);}
                }
            }
        else { rowIndices
            = new ArrayList <int[]>();dict
            = new TrieDictionary <String>();}
        }
    public

    int getRowCount (){ return
        rowIndices .size();}
    }
