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
package org.wso2.siddhi.api.condition.where.logical;

import org.wso2.siddhi.api.condition.ExpirableCondition;
import org.wso2.siddhi.api.condition.where.WhereCondition;
import org.wso2.siddhi.api.condition.Condition;

import java.util.Map;

/**
 * 
 * AndCondition has two conditions which performs logical AND operation
 */

public class AndCondition extends ExpirableCondition implements WhereCondition {
    private Condition leftCondition;
    private Condition rightCondition;

    /**
     * @param leftCondition  One of the conditions to be evaluated
     * @param rightCondition One of the conditions to be evaluated
     */
    public AndCondition(Condition leftCondition, Condition rightCondition) {
        this.leftCondition = leftCondition;
        this.rightCondition = rightCondition;
    }

    /**
     * get the left condition
     *
     * @return Left condition
     */
    public Condition getLeftCondition() {
        return leftCondition;
    }


    /**
     * get the right condition
     *
     * @return Right Condition
     */
    public Condition getRightCondition() {
        return rightCondition;
    }

    @Override
    public WhereCondition getNewInstance(Map<String, String> referenceConversion) {
        return (WhereCondition) new AndCondition(((WhereCondition)leftCondition).getNewInstance(referenceConversion),((WhereCondition)rightCondition).getNewInstance(referenceConversion)).within(getLifeTime());

    }
}
