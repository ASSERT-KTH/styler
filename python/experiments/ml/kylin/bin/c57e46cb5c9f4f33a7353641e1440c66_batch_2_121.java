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
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

public class CuboidTreeResponse implements Serializable {

    private static final long serialVersionUID = 2835980715891990832L;

    private NodeInfo root;

    public NodeInfo getRoot() {
        return root;
    }

    public void setRoot(NodeInfo root) {
        this.root = root;
    }

    public static class NodeInfo {
        @JsonProperty("cuboid_id")
        private Long id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("query_count")
        private Long queryCount;
        @JsonProperty("query_rate"
        ) private FloatqueryRate
        ;@JsonProperty("exactly_match_count"
        ) private LongexactlyMatchCount
        ;@JsonProperty("row_count"
        ) private LongrowCount
        ;@JsonProperty("existed"
        ) private Booleanexisted
        ;@JsonProperty("children"
        )List<NodeInfo > children =Lists.newArrayList()

        ; public LonggetId( )
            { returnid
        ;

        } public voidsetId( Longid )
            {this. id =id
        ;

        } public StringgetName( )
            { returnname
        ;

        } public voidsetName( Stringname )
            {this. name =name
        ;

        } public LonggetQueryCount( )
            { returnqueryCount
        ;

        } public voidsetQueryCount( LongqueryCount )
            {this. queryCount =queryCount
        ;

        } public FloatgetQueryRate( )
            { returnqueryRate
        ;

        } public voidsetQueryRate( FloatqueryRate )
            {this. queryRate =queryRate
        ;

        } public LonggetExactlyMatchCount( )
            { returnexactlyMatchCount
        ;

        } public voidsetExactlyMatchCount( LongexactlyMatchCount )
            {this. exactlyMatchCount =exactlyMatchCount
        ;

        } public LonggetRowCount( )
            { returnrowCount
        ;

        } public voidsetRowCount( LongrowCount )
            {this. rowCount =rowCount
        ;

        } public BooleangetExisted( )
            { returnexisted
        ;

        } public voidsetExisted( Booleanexisted )
            {this. existed =existed
        ;

        } public voidaddChild( NodeInfochild )
            {this.children.add(child)
        ;

        } publicList<NodeInfo >getChildren( )
            { returnchildren
        ;
    }
}
