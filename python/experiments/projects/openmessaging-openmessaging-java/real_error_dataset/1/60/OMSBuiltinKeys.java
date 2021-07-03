/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.openmessaging;

/**
 * This is the centralized source for keys that are used by {@link KeyValue}.
 *
 * @author yukon@apache.org
 * @version OMS 1.0
 * @since OMS 1.0
 */
public interface OMSBuiltinKeys {
    String DRIVER_IMPL = "oms.driver.impl";
    String ACCESS_POINTS = "oms.access.points";
    String NAMESPACE = "oms.namespace";
    String ACCOUNT_ID = "oms.account.id";
    String REGION = "oms.region";
    String PRODUCER_ID = "oms.producer.id";
    String CONSUMER_ID = "oms.consumer.id";
    String OPERATION_TIMEOUT = "oms.operation.timeout";
    String BEGIN_OFFSET = "oms.begin.offset";
    String END_OFFSET = "oms.end.offset";
    String BEGIN_TIMESTAMP = "oms.begin.timestamp";
    String END_TIMESTAMP = "oms.end.timestamp";

    /**
     * There are three order policies:
     *
     * <ul>
     * <li>ANY_ORDER</li>
     * <li>FIFO_ORDER</li>
     * <li>LIFO_ORDER</li>
     * </ul>
     */
    String ORDER_POLICY = "order.policy";
}
