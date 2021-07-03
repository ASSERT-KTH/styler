/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.siddhi.api.eventstream.query;

import org.wso2.siddhi.api.OutputDefinition;
import org.wso2.siddhi.api.condition.sequence.SequenceCondition;
import org.wso2.siddhi.api.eventstream.EventStream;
import org.wso2.siddhi.api.eventstream.query.inputstream.QueryInputStream;
import org.wso2.siddhi.api.eventstream.query.utils.QueryHelper;

import java.util.List;

public class SequenceQuery extends Query {

    private List<QueryInputStream> queryInputStreamList = null;
    /**
     * @param streamId            id of the query
     * @param outputDefinition    definition of the output of the query
     * @param eventStreamList     list of event streams
     * @param sequenceCondition condition of the query
     */
    public SequenceQuery(String streamId, OutputDefinition outputDefinition, List<QueryInputStream> eventStreamList, SequenceCondition sequenceCondition) {
        super(streamId, outputDefinition, sequenceCondition);
        this.queryInputStreamList = eventStreamList;
        this.inputEventStreams=new EventStream[queryInputStreamList.size()];
        for (int i = 0, queryInputStreamListSize = queryInputStreamList.size(); i < queryInputStreamListSize; i++) {
            inputEventStreams[i]=queryInputStreamList.get(i).getEventStream();
        }
        setSchema(getAttributeNames(), QueryHelper.generateAttributeClasses(outputDefinition.getPropertyList(),this));
    }

    /**
     * get the list of input event streams
     *
     * @return the list of input event streams
     */
    public List<QueryInputStream> getQueryInputStreamList() {
        return queryInputStreamList;
    }
}
