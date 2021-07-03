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

package org.wso2.siddhi.api.condition.where;

import org.wso2.siddhi.api.condition.ExpirableCondition;

import java.util.Map;

/**
 * SimpleCondition class handles Simple Condtions
 */
public class SimpleCondition extends ExpirableCondition implements WhereCondition {

    /**
     * Both the leftProperty & rightProperty has the format A.foo where A is a Stream and foo is a attribute in the stream
     * Existence of those Streams and properties needs to be validated at org.wso2.siddhi.api.eventstream.query.Query
     */

    private String leftProperty;
    private String rightProperty;
    private ConditionOperator operator;


    /**
     * @param leftProperty  A property in the format StreamId.attribute
     * @param operator      The condition operator of the condition
     * @param rightProperty A property in the format StreamId.attribute
     */
    public SimpleCondition(String leftProperty, ConditionOperator operator,
                           String rightProperty) {
        this.leftProperty = leftProperty;
        this.rightProperty = rightProperty;
        this.operator = operator;
    }

    /**
     * get the left property of the stream
     *
     * @return left property of the stream
     */
    public String getLeftProperty() {
        return leftProperty;
    }

    /**
     * get the right property of the stream
     *
     * @return right property of the stream
     */
    public String getRightProperty() {
        return rightProperty;
    }

    /**
     * get the condition operator
     *
     * @return the condition operator of the condition
     */
    public ConditionOperator getOperator() {
        return operator;
    }

    @Override
    public WhereCondition getNewInstance(Map<String, String> referenceConversion) {
        if (referenceConversion == null) {
            return (WhereCondition) new SimpleCondition(leftProperty, operator, rightProperty).within(getLifeTime());
        } else {
            String leftProp = leftProperty.replaceAll(" ", "");
            String rightProp = rightProperty.replaceAll(" ", "");
            for (String ref : referenceConversion.keySet()) {
                leftProp = leftProp.replace("$" + ref, "$" + referenceConversion.get(ref));
                rightProp = rightProp.replace("$" + ref, "$" + referenceConversion.get(ref));
            }
            return (WhereCondition) new SimpleCondition(leftProp, operator, rightProp).within(getLifeTime());
        }
    }


}
