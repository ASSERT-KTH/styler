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

package org.apache.kylin.rest.util;

import org.apache.kylin.metadata.project.ProjectInstance;
import org.apache.kylin.rest.constant.Constant;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Lazy
@Component("aclUtil")
public class AclUtil {
    String getCurrentUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    //such method MUST NOT be called from within same class
    //do not change public to package private
    @PreAuthorize(Constant.ACCESS_HAS_ROLE_ADMIN +
            " or hasPermission(#project, 'ADMINISTRATION')" +
            " or hasPermission(#project, 'MANAGEMENT')" +
            " or hasPermission(#project, 'OPERATION')" +
            " or hasPermission(#project, 'READ')")
    public boolean hasProjectReadPermission(ProjectInstance project) {
        return true;
    }

    @PreAuthorize(Constant.ACCESS_HAS_ROLE_ADMIN +
            " or hasPermission(#project, 'ADMINISTRATION')" +
            " or hasPermission(#project, 'MANAGEMENT')" +
            " or hasPermission(#project, 'OPERATION')")
    public boolean hasProjectOperationPermission(ProjectInstance project) {
        return true;
    }

    @PreAuthorize(Constant.ACCESS_HAS_ROLE_ADMIN +
            " or hasPermission(#project, 'ADMINISTRATION')" +
            " or hasPermission(#project, 'MANAGEMENT')")
    public boolean hasProjectWritePermission(ProjectInstance project) {
        return true;
    }

    @PreAuthorize(Constant.ACCESS_HAS_ROLE_ADMIN +
            " or hasPermission(#project, 'ADMINISTRATION')")
    public boolean hasProjectAdminPermission(ProjectInstance project) {
        return true;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public boolean checkIsGlobalAdmin() {
        return true;
    }
}
