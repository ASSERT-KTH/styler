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
package org.wso2.siddhi.core.node.processor.utils;

import org.wso2.siddhi.api.eventstream.EventStream;
import org.wso2.siddhi.api.eventstream.query.Query;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.exception.InvalidAttributeCastException;
import org.wso2.siddhi.core.exception.InvalidQueryException;
import org.wso2.siddhi.core.exception.PropertyFormatException;
import org.wso2.siddhi.core.exception.SiddhiException;
import org.wso2.siddhi.core.exception.UndefinedPropertyException;
import org.wso2.siddhi.core.node.processor.executor.Executor;
import org.wso2.siddhi.core.parser.ConditionParser;

/**
 * Checks for having conditions
 */
public class HavingHandler {

    public static HavingHandler assignHandler(Query query)
            throws InvalidQueryException, PropertyFormatException, UndefinedPropertyException,
                   InvalidAttributeCastException, SiddhiException {
        if (query.hasHaving()) {
            return new HavingHandler(query);
        } else {
            return null;
        }
    }

    private Executor havingExecutor = null;

    private HavingHandler(Query query)
            throws InvalidQueryException, PropertyFormatException, UndefinedPropertyException,
                   SiddhiException, InvalidAttributeCastException {
        ConditionParser havingConditionParser = new ConditionParser(query.getHavingCondition(), new EventStream[]{query});
        havingExecutor = havingConditionParser.getExecutor();
    }

    public boolean successHavingCondition(Event generatedEvent) {
        return havingExecutor == null || havingExecutor.execute(generatedEvent);
    }
}
