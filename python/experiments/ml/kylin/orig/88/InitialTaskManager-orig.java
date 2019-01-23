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

package org.apache.kylin.rest.init;

import org.apache.commons.lang.StringUtils;
import org.apache.kylin.common.KylinConfig;
import org.apache.kylin.common.util.StringUtil;
import org.apache.kylin.rest.metrics.QueryMetrics2Facade;
import org.apache.kylin.rest.metrics.QueryMetricsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by dongli on 3/16/16.
 */
public class InitialTaskManager implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(InitialTaskManager.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Kylin service is starting.....");

        runInitialTasks();
    }

    private void runInitialTasks() {

        // init metrics system for kylin
        QueryMetricsFacade.init();
        QueryMetrics2Facade.init();

        KylinConfig kylinConfig = KylinConfig.getInstanceFromEnv();
        String initTasks = kylinConfig.getInitTasks();
        if (!StringUtils.isEmpty(initTasks)) {
            String[] taskClasses = StringUtil.splitByComma(initTasks);
            for (String taskClass : taskClasses) {
                try {
                    InitialTask task = (InitialTask) Class.forName(taskClass).newInstance();
                    logger.info("Running initial task: " + taskClass);
                    task.execute();
                } catch (Throwable e) {
                    logger.error("Initial task failed: " + taskClass, e);
                }
            }
            logger.info("All initial tasks finished.");
        }
    }
}
