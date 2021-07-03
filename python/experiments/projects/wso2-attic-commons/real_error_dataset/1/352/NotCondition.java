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
import org.wso2.siddhi.api.condition.Condition;
import org.wso2.siddhi.api.condition.where.WhereCondition;

import java.util.Map;

/**
 * 
 *
 * NotCondition class handles the Logical Not operation of the condition
 */
public class NotCondition extends ExpirableCondition implements WhereCondition {
    private Condition condition;

    public NotCondition(Condition condition) {
        this.condition = condition;
    }

    /**
     * get the condition
     *
     * @return the condition
     */
    public Condition getCondition() {
        return condition;
    }

    @Override
    public WhereCondition getNewInstance(Map<String, String> referenceConversion) {
        return (WhereCondition) new NotCondition(((WhereCondition)condition).getNewInstance(referenceConversion)).within(getLifeTime());

    }
}
