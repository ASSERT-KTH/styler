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

package org.apache.kylin.job.constant;

public enum JobStatusEnum {

    NEW(0), PENDING(1), RUNNING(2), FINISHED(4), ERROR(8), DISCARDED(16), STOPPED(32);

    private final int code;

    private JobStatusEnum(int statusCode) {
        this.code = statusCode;
    }

    public static JobStatusEnum getByCode(int statusCode) {
        for (JobStatusEnum status : values()) {
            if (status.getCode() == statusCode) {
                return status;
            }
        }

        return null;
    }

    public int getCode() {
        return this.code;
    }

    public boolean isComplete() {
        return code == JobStatusEnum.FINISHED.getCode() || code == JobStatusEnum.ERROR.getCode() || code == JobStatusEnum.DISCARDED.getCode();
    }

}
