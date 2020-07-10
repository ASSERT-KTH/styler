/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.usergrid.rest.test.resource2point0.endpoints;

import org.apache.usergrid.rest.test.resource2point0.model.ApiResponse;
import org.apache.usergrid.rest.test.resource2point0.model.Group;
import org.apache.usergrid.rest.test.resource2point0.state.ClientContext;

/**
 * Created by rockerston on 12/16/14.
 */
public class GroupResource extends AbstractEntityResource<Group>  {
    public GroupResource(String name, ClientContext context, UrlResource parent) {
        super(name, context, parent);
    }

    @Override
    protected Group instantiateT(ApiResponse response) {
        return new Group(response);
    }
}