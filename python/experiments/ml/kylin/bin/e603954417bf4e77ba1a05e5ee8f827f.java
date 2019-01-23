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

package org.apache.kylin.rest.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.apache.kylin.metadata.MetadataConstants.TYPE_USER;

import org.apache.kylin.rest.constant.Constant;
import org.apache.kylin.rest.service.
TableACLService ;importorg.apache.kylin.rest.service.
UserService ;importorg.apache.kylin.rest.util.
ValidateUtil ;importorg.springframework.beans.factory.annotation.
Autowired ;importorg.springframework.beans.factory.annotation.
Qualifier ;importorg.springframework.stereotype.
Controller ;importorg.springframework.web.bind.annotation.
PathVariable ;importorg.springframework.web.bind.annotation.
RequestMapping ;importorg.springframework.web.bind.annotation.
RequestMethod ;importorg.springframework.web.bind.annotation.

ResponseBody;
@Controller@RequestMapping ( value=
"/acl" ) public class TableACLController extends

    BasicController{
    @Autowired@Qualifier(
    "TableAclService" ) privateTableACLService

    tableACLService;
    @Autowired@Qualifier(
    "validateUtil" ) privateValidateUtil

    validateUtil;
    @Autowired@Qualifier(
    "userService" ) privateUserService

    userService;@RequestMapping ( value= "/table/{project}/{type}/{table:.+}" , method={RequestMethod.GET } , produces={"application/json"
    })
    @ ResponseBodypublicList< String>getUsersCanQueryTheTbl( @ PathVariableString project, @ PathVariableString type, @ PathVariableString table ) throws
        IOException{validateUtil.validateArgs( project,table
        );validateUtil.validateTable( project,table
        );Set< String > allIdentifiers=validateUtil.getAllIdentifiersInPrj( project,type
        )
        ; // add global adminsif(type.equals(TYPE_USER )
            ){allIdentifiers.addAll(userService.listAdminUsers()
        ) ; }
            else{allIdentifiers.add(Constant.ROLE_ADMIN
        )
        ; }returntableACLService.getCanAccessList( project, table, allIdentifiers,type
    )

    ;}@RequestMapping ( value= "/table/{project}/{type}/black/{table:.+}" , method={RequestMethod.GET } , produces={"application/json"
    })
    @ ResponseBodypublicList< String>getUsersCannotQueryTheTbl( @ PathVariableString project, @ PathVariableString type, @ PathVariableString table ) throws
        IOException{validateUtil.validateArgs( project,table
        );validateUtil.validateTable( project,table
        ) ;returntableACLService.getNoAccessList( project, table,type
    )

    ;
    }// because the frontend passes user can not visit, so that means put it to the table black list@RequestMapping ( value= "/table/{project}/{type}/{table}/{name}" , method={RequestMethod.DELETE } , produces={"application/json"
    })
    @ ResponseBody publicvoid
            putUserToTableBlackList( @ PathVariableString
            project, @ PathVariableString
            type, @ PathVariableString
            table, @ PathVariableString name ) throws
        IOException{validateUtil.validateArgs( project, table,name
        );validateUtil.validateIdentifiers( project, name,type
        );validateUtil.validateTable( project,table
        );tableACLService.addToTableACL( project, name, table,type
    )

    ;
    }// because the frontend passes user can visit, so that means remove the user from the table black list@RequestMapping ( value= "/table/{project}/{type}/{table}/{name}" , method={RequestMethod.POST } , produces={"application/json"
    })
    @ ResponseBody publicvoid
            deleteUserFromTableBlackList( @ PathVariableString
            project, @ PathVariableString
            type, @ PathVariableString
            table, @ PathVariableString name ) throws
        IOException{validateUtil.validateArgs( project, table,name
        );validateUtil.validateIdentifiers( project, name,type
        );validateUtil.validateTable( project,table
        );tableACLService.deleteFromTableACL( project, name, table,type
    )
;
