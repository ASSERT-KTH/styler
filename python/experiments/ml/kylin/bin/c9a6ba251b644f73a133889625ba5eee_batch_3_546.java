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

package org.apache.kylin.rest.response;

import java.io.Serializable;
import java.util.

List ;importcom.fasterxml.jackson.annotation.
JsonProperty ;importcom.google.common.collect.

Lists ; public class CuboidTreeResponse implements

    Serializable { private static final long serialVersionUID=

    2835980715891990832L ; privateNodeInfo

    root ; publicNodeInfogetRoot (
        ) {return
    root

    ; } publicvoidsetRoot (NodeInfo root
        ){this . root=
    root

    ; } public static class
        NodeInfo{@JsonProperty(
        "cuboid_id" ) privateLong
        id;@JsonProperty(
        "name" ) privateString
        name;@JsonProperty(
        "query_count" ) privateLong
        queryCount;@JsonProperty(
        "query_rate" ) privateFloat
        queryRate;@JsonProperty(
        "exactly_match_count" ) privateLong
        exactlyMatchCount;@JsonProperty(
        "row_count" ) privateLong
        rowCount;@JsonProperty(
        "existed" ) privateBoolean
        existed;@JsonProperty(
        "children")List< NodeInfo > children=Lists.newArrayList(

        ) ; publicLonggetId (
            ) {return
        id

        ; } publicvoidsetId (Long id
            ){this . id=
        id

        ; } publicStringgetName (
            ) {return
        name

        ; } publicvoidsetName (String name
            ){this . name=
        name

        ; } publicLonggetQueryCount (
            ) {return
        queryCount

        ; } publicvoidsetQueryCount (Long queryCount
            ){this . queryCount=
        queryCount

        ; } publicFloatgetQueryRate (
            ) {return
        queryRate

        ; } publicvoidsetQueryRate (Float queryRate
            ){this . queryRate=
        queryRate

        ; } publicLonggetExactlyMatchCount (
            ) {return
        exactlyMatchCount

        ; } publicvoidsetExactlyMatchCount (Long exactlyMatchCount
            ){this . exactlyMatchCount=
        exactlyMatchCount

        ; } publicLonggetRowCount (
            ) {return
        rowCount

        ; } publicvoidsetRowCount (Long rowCount
            ){this . rowCount=
        rowCount

        ; } publicBooleangetExisted (
            ) {return
        existed

        ; } publicvoidsetExisted (Boolean existed
            ){this . existed=
        existed

        ; } publicvoidaddChild (NodeInfo child
            ){this.children.add(child
        )

        ; }publicList< NodeInfo>getChildren (
            ) {return
        children
    ;
}
