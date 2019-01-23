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

package org.apache.kylin.rest.constant;

/**
 * @author xduo
 * 
 */
public class Constant {

    public final static String FakeSchemaName = "defaultSchema";
    public final static String FakeCatalogName = "defaultCatalog";

    public final static String IDENTITY_USER = "user";
    public final static String IDENTITY_ROLE = "role";

    public final static String ROLE_ADMIN = "ROLE_ADMIN";
    public final static String ROLE_MODELER = "ROLE_MODELER";
    public final static String ROLE_ANALYST = "ROLE_ANALYST";

    public final static String GROUP_ALL_USERS = "ALL_USERS";

    public final static String ACCESS_HAS_ROLE_ADMIN = "hasRole('ROLE_ADMIN')";
    //public final static String ACCESS_HAS_ROLE_MODELER = "hasRole('ROLE_MODELER')";

    public final static String ACCESS_POST_FILTER_READ =
            "hasRole('ROLE_ADMIN') " +
            " or hasPermission(filterObject, 'ADMINISTRATION')"+
            " or hasPermission(filterObject, 'MANAGEMENT')" +
            " or hasPermission(filterObject, 'OPERATION')" +
            " or hasPermission(filterObject, 'READ')";

    public final static String SERVER_MODE_QUERY = "query";
    public final static String SERVER_MODE_JOB = "job";
    public final static String SERVER_MODE_ALL = "all";

}
