/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.siddhi.query.api.query.input;

import org.wso2.siddhi.query.api.condition.Condition;
import org.wso2.siddhi.query.api.definition.StreamDefinition;
import org.wso2.siddhi.query.api.expression.constant.Constant;
import org.wso2.siddhi.query.api.query.QueryEventStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JoinStream implements Stream {

    private Stream leftStream;
    private Type type;
    private Stream rightStream;
    private Condition onCompare;
    private EventTrigger trigger;
    private Constant within;

    public enum EventTrigger {
        LEFT,RIGHT,ALL
    }
    public JoinStream(Stream leftStream, Type type,
                      Stream rightStream, Condition onCompare, Constant within,
                      EventTrigger trigger) {
        this.leftStream = leftStream;
        this.type = type;
        this.rightStream = rightStream;
        this.onCompare = onCompare;
        this.within= within;
        this.trigger = trigger;
    }

    public Stream getLeftStream() {
        return leftStream;
    }

    public Type getType() {
        return type;
    }

    public Stream getRightStream() {
        return rightStream;
    }

    public Condition getOnCompare() {
        return onCompare;
    }

    public EventTrigger getTrigger() {
        return trigger;
    }

    public Constant getWithin() {
        return within;
    }

    @Override
    public List<String> getStreamIds() {
        List<String> list = new ArrayList<String>();
        for (String streamId : leftStream.getStreamIds()) {
            if (!list.contains(streamId)) {
                list.add(streamId);
            }
        }
        for (String streamId : rightStream.getStreamIds()) {
            if (!list.contains(streamId)) {
                list.add(streamId);
            }
        }
        return list;
    }

    @Override
    public List<QueryEventStream> constructQueryEventStreamList(
            Map<String, StreamDefinition> streamDefinitionMap,
            List<QueryEventStream> queryEventStreams) {
        List<QueryEventStream> queryEventStreamList = leftStream.constructQueryEventStreamList(streamDefinitionMap, queryEventStreams);
        return rightStream.constructQueryEventStreamList(streamDefinitionMap, queryEventStreamList);
    }


    public enum Type {JOIN, INNER_JOIN, LEFT_OUTER_JOIN, RIGHT_OUTER_JOIN, FULL_OUTER_JOIN}
}
