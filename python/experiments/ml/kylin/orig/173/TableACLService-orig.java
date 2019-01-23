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

package org.apache.kylin.rest.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.kylin.metadata.acl.TableACL;
import org.apache.kylin.rest.util.AclEvaluate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("TableAclService")
public class TableACLService extends BasicService {
    private static final Logger logger = LoggerFactory.getLogger(TableACLService.class);

    @Autowired
    private AclEvaluate aclEvaluate;

    private TableACL getTableACLByProject(String project) throws IOException {
        return getTableACLManager().getTableACLByCache(project);
    }

    public boolean exists(String project, String name, String type) throws IOException {
        aclEvaluate.checkProjectWritePermission(project);
        return getTableACLByProject(project).contains(name, type);
    }

    public List<String> getNoAccessList(String project, String table, String type) throws IOException {
        aclEvaluate.checkProjectWritePermission(project);
        return getTableACLByProject(project).getNoAccessList(table, type);
    }

    public List<String> getCanAccessList(String project, String table, Set<String> allIdentifiers, String type) throws IOException {
        aclEvaluate.checkProjectWritePermission(project);
        return getTableACLByProject(project).getCanAccessList(table, allIdentifiers, type);
    }

    public void addToTableACL(String project, String name, String table, String type) throws IOException {
        aclEvaluate.checkProjectAdminPermission(project);
        getTableACLManager().addTableACL(project, name, table, type);
    }

    public void deleteFromTableACL(String project, String name, String table, String type) throws IOException {
        aclEvaluate.checkProjectAdminPermission(project);
        getTableACLManager().deleteTableACL(project, name, table, type);
    }

    public void deleteFromTableACL(String project, String name, String type) throws IOException {
        aclEvaluate.checkProjectAdminPermission(project);
        getTableACLManager().deleteTableACL(project, name, type);
    }

    public void deleteFromTableACLByTbl(String project, String table) throws IOException {
        aclEvaluate.checkProjectAdminPermission(project);
        getTableACLManager().deleteTableACLByTbl(project, table);
    }
}
