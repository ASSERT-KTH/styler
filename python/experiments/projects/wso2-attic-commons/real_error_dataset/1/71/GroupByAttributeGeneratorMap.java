/*
*  Copyright (c) 2005-2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.siddhi.core.query.projector.attibute.generator.groupby;

import org.wso2.siddhi.core.event.AtomicEvent;
import org.wso2.siddhi.core.executor.expression.ExpressionExecutor;
import org.wso2.siddhi.core.query.projector.attibute.generator.AbstractAggregateAttributeGenerator;

import java.util.HashMap;
import java.util.Map;

public class GroupByAttributeGeneratorMap {

    private Map<String, AbstractAggregateAttributeGenerator> attributeGeneratorMap = new HashMap<String, AbstractAggregateAttributeGenerator>();
    private ExpressionExecutor[] expressionExecutors;
    private AbstractAggregateAttributeGenerator attributeGenerator;

    public GroupByAttributeGeneratorMap(ExpressionExecutor[] expressionExecutors,
                                        AbstractAggregateAttributeGenerator attributeGenerator) {
        this.expressionExecutors = expressionExecutors;
        this.attributeGenerator = attributeGenerator;
    }

    public AbstractAggregateAttributeGenerator get(AtomicEvent event) {
        String key = getKey(event);
        AbstractAggregateAttributeGenerator generator = attributeGeneratorMap.get(key);
        if (generator != null) {
            return generator;
        } else {
            generator = attributeGenerator.createNewInstance();
            attributeGeneratorMap.put(key, generator);
           return generator;

        }
    }

    private String getKey(AtomicEvent event) {
        StringBuilder sb = new StringBuilder();
        for (ExpressionExecutor executor : expressionExecutors) {
            sb.append(executor.execute(event)).append("::");
        }
        return sb.toString();
    }
}
