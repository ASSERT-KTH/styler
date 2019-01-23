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

package org.apache.kylin.
rest .response;importjava.

io .Serializable;importjava.
util .List;importcom.

fasterxml .jackson.annotation.JsonProperty;importcom.
google .common.collect.Lists;publicclassCuboidTreeResponse

implements Serializable { private static final

    long serialVersionUID = 2835980715891990832L ; private NodeInforoot

    ; public NodeInfogetRoot

    ( ) {returnroot ;
        } publicvoid
    setRoot

    ( NodeInfo root){ this. root
        =root; } publicstatic
    class

    NodeInfo { @ JsonProperty (
        "cuboid_id")privateLongid
        ; @ JsonProperty(
        "name")privateStringname
        ; @ JsonProperty(
        "query_count")privateLongqueryCount
        ; @ JsonProperty(
        "query_rate")privateFloatqueryRate
        ; @ JsonProperty(
        "exactly_match_count")privateLongexactlyMatchCount
        ; @ JsonProperty(
        "row_count")privateLongrowCount
        ; @ JsonProperty(
        "existed")privateBooleanexisted
        ; @ JsonProperty(
        "children")List<NodeInfo
        >children=Lists . newArrayList ();publicLonggetId

        ( ) {returnid ;
            } publicvoid
        setId

        ( Long id){ this. id
            =id; } publicString
        getName

        ( ) {returnname ;
            } publicvoid
        setName

        ( String name){ this. name
            =name; } publicLong
        getQueryCount

        ( ) {returnqueryCount ;
            } publicvoid
        setQueryCount

        ( Long queryCount){ this. queryCount
            =queryCount; } publicFloat
        getQueryRate

        ( ) {returnqueryRate ;
            } publicvoid
        setQueryRate

        ( Float queryRate){ this. queryRate
            =queryRate; } publicLong
        getExactlyMatchCount

        ( ) {returnexactlyMatchCount ;
            } publicvoid
        setExactlyMatchCount

        ( Long exactlyMatchCount){ this. exactlyMatchCount
            =exactlyMatchCount; } publicLong
        getRowCount

        ( ) {returnrowCount ;
            } publicvoid
        setRowCount

        ( Long rowCount){ this. rowCount
            =rowCount; } publicBoolean
        getExisted

        ( ) {returnexisted ;
            } publicvoid
        setExisted

        ( Boolean existed){ this. existed
            =existed; } publicvoid
        addChild

        ( NodeInfo child){ this. children
            .add(child);}publicList
        <

        NodeInfo >getChildren() {returnchildren ;
            } }}
        