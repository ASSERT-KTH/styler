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

package org.apache.kylin.rest.request;

public class ExternalFilterRequest {

    private String project;

    private String extFilter;

    private boolean successful;

    private String message;

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getExtFilter() {
        return extFilter;
    }

    public void setExtFilter(String extFilter) {
        this.extFilter = extFilter;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
