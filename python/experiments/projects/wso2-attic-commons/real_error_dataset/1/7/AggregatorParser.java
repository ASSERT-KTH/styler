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
package org.wso2.siddhi.core.util.parser;

import org.wso2.siddhi.core.exception.OperationNotSupportedException;
import org.wso2.siddhi.core.projector.attibute.aggregator.Aggregator;
import org.wso2.siddhi.core.projector.attibute.aggregator.avg.AvgAggregatorFloat;
import org.wso2.siddhi.core.projector.attibute.aggregator.avg.AvgAggregatorInt;
import org.wso2.siddhi.core.projector.attibute.aggregator.avg.AvgAggregatorLong;
import org.wso2.siddhi.core.projector.attibute.aggregator.max.MaxAggregatorDouble;
import org.wso2.siddhi.core.projector.attibute.aggregator.max.MaxAggregatorFloat;
import org.wso2.siddhi.core.projector.attibute.aggregator.max.MaxAggregatorInt;
import org.wso2.siddhi.core.projector.attibute.aggregator.max.MaxAggregatorLong;
import org.wso2.siddhi.core.projector.attibute.aggregator.min.MinAggregatorDouble;
import org.wso2.siddhi.core.projector.attibute.aggregator.min.MinAggregatorFloat;
import org.wso2.siddhi.core.projector.attibute.aggregator.min.MinAggregatorInt;
import org.wso2.siddhi.core.projector.attibute.aggregator.min.MinAggregatorLong;
import org.wso2.siddhi.core.projector.attibute.aggregator.sum.SumAggregatorDouble;
import org.wso2.siddhi.core.projector.attibute.aggregator.sum.SumAggregatorFloat;
import org.wso2.siddhi.core.projector.attibute.aggregator.sum.SumAggregatorInt;
import org.wso2.siddhi.core.projector.attibute.aggregator.sum.SumAggregatorLong;
import org.wso2.siddhi.core.projector.attibute.generator.AbstractAggregateAttributeGenerator;
import org.wso2.siddhi.query.api.definition.Attribute;

public class AggregatorParser {


    public static AbstractAggregateAttributeGenerator loadAggregatorClass(String aggregationName) {
        return (AbstractAggregateAttributeGenerator) org.wso2.siddhi.core.util.ClassLoader.loadClass("org.wso2.siddhi.core.projector.attibute.generator." + aggregationName.substring(0, 1).toUpperCase() + aggregationName.substring(1) + "AggregateAttributeGenerator");
    }

    public static Aggregator createSumAggregator(Attribute.Type type) {
        switch (type) {
            case STRING:
                throw new OperationNotSupportedException("Sum not supported for string");
            case INT:
                return new SumAggregatorInt();
            case LONG:
                return new SumAggregatorLong();
            case FLOAT:
                return new SumAggregatorFloat();
            case DOUBLE:
                return new SumAggregatorDouble();
            case BOOL:
                throw new OperationNotSupportedException("Sum not supported for bool");
        }
        throw new OperationNotSupportedException("Sum not supported for " + type);
    }

    public static Aggregator createAvgAggregator(Attribute.Type type) {
        switch (type) {
            case STRING:
                throw new OperationNotSupportedException("Avg not supported for string");
            case INT:
                return new AvgAggregatorInt();
            case LONG:
                return new AvgAggregatorLong();
            case FLOAT:
                return new AvgAggregatorFloat();
            case DOUBLE:
                return new AvgAggregatorInt();
            case BOOL:
                throw new OperationNotSupportedException("Avg not supported for bool");
        }
        throw new OperationNotSupportedException("Avg not supported for " + type);
    }

    public static Aggregator createMaxAggregator(Attribute.Type type) {
        switch (type) {
            case STRING:
                throw new OperationNotSupportedException("Max not supported for string");
            case INT:
                return new MaxAggregatorInt();
            case LONG:
                return new MaxAggregatorLong();
            case FLOAT:
                return new MaxAggregatorFloat();
            case DOUBLE:
                return new MaxAggregatorDouble();
            case BOOL:
                throw new OperationNotSupportedException("Max not supported for bool");
        }
        throw new OperationNotSupportedException("Max not supported for " + type);
    }


    public static Aggregator createMinAggregator(Attribute.Type type) {
        switch (type) {
            case STRING:
                throw new OperationNotSupportedException("Min not supported for string");
            case INT:
                return new MinAggregatorInt();
            case LONG:
                return new MinAggregatorLong();
            case FLOAT:
                return new MinAggregatorFloat();
            case DOUBLE:
                return new MinAggregatorDouble();
            case BOOL:
                throw new OperationNotSupportedException("Min not supported for bool");
        }
        throw new OperationNotSupportedException("Min not supported for " + type);
    }
}
