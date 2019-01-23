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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.kylin.metadata.
model .FunctionDesc;importorg.apache.kylin.metadata.

model .MeasureDesc;importjava.

util
.Arrays;/**
 */ @ JsonAutoDetect(fieldVisibility= Visibility . NONE,getterVisibility= Visibility . NONE,isGetterVisibility= Visibility . NONE,setterVisibility=
Visibility . NONE ) publicclassHBaseColumnDescimplementsjava .

    io.Serializable{@
    JsonProperty ( "qualifier")
    privateStringqualifier;@
    JsonProperty ("measure_refs") privateString

    [
    ] measureRefs;// these two will be assembled at runtime privateMeasureDesc
    [ ]measures; privateint [
    ] measureIndex ;// the index on CubeDesc.getMeasures()

    private String columnFamilyName;public String
        getQualifier ()
    {

    return qualifier ;}public voidsetQualifier (
        Stringqualifier) { this.
    qualifier

    = qualifier;} publicString[ ]
        getMeasureRefs ()
    {

    return measureRefs ;}publicvoidsetMeasureRefs (String [
        ]measureRefs) { this.
    measureRefs

    = measureRefs;} publicint[ ]
        getMeasureIndex ()
    {

    return measureIndex ;}publicvoidsetMeasureIndex (int [
        ]index) { this.
    measureIndex

    = index;} publicMeasureDesc[ ]
        getMeasures ()
    {

    return measures ;}publicvoidsetMeasures (MeasureDesc [
        ]measures) { this.
    measures

    = measures ;}public String
        getColumnFamilyName ()
    {

    return columnFamilyName ;}public voidsetColumnFamilyName (
        StringcolumnFamilyName) { this.
    columnFamilyName

    = columnFamilyName ;}public intfindMeasure (
        FunctionDesc function) { for (int i = 0;i< measures.length ;
            i ++){if(measures[i].getFunction().equals (
                function ))
            {
        return
        i ;}}
    return

    - 1 ;}public booleancontainsMeasure (
        String refName) { for (String ref
            : measureRefs){if(ref.equals
                ( refName)
        )
        return true;
    }

    returnfalse
    ; } @Overridepublic int
        hashCode ( ) { finalint
        prime = 31 ;int
        result = 1 ; result = prime*result + (( columnFamilyName == null )?0:columnFamilyName.hashCode
        ( ) ) ; result = prime*result + (( qualifier == null )?0:qualifier.hashCode
        ( ))
    ;

    returnresult
    ; } @Overridepublic booleanequals (
        Object obj) { if(
            this ==obj
        ) returntrue ; if(
            obj ==null
        ) returnfalse;if ( getClass()!=obj.
            getClass ()
        ) return false ;HBaseColumnDescother =(
        HBaseColumnDesc )obj ; if( columnFamilyName
            == null){if ( other.
                columnFamilyName !=null
        ) return false ;}elseif(!columnFamilyName.equals(other
            . columnFamilyName)
        ) returnfalse ; if( qualifier
            == null){if ( other.
                qualifier !=null
        ) return false ;}elseif(!qualifier.equals(other
            . qualifier)
        ) returnfalse
    ;

    returntrue
    ; } @Overridepublic String
        toString ( ) { return "HBaseColumnDesc [qualifier=" + qualifier+", measureRefs="+Arrays. toString (measureRefs
    )

+
