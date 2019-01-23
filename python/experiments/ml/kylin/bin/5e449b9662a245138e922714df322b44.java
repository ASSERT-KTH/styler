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

import java.io.IOException;importjava
. util.ArrayList;importjava
. util.List;importjava
. util.Locale;importorg

. apache.commons.lang3.StringUtils;importorg
. apache.kylin.metadata.streaming.StreamingConfig;importorg
. apache.kylin.rest.exception.BadRequestException;importorg
. apache.kylin.rest.msg.Message;importorg
. apache.kylin.rest.msg.MsgPicker;importorg
. apache.kylin.rest.util.AclEvaluate;importorg
. springframework.beans.factory.annotation.Autowired;importorg
. springframework.stereotype.Component;@Component

("streamingMgmtService")publicclass
StreamingService extends BasicService { @ Autowired
    privateAclEvaluate
    aclEvaluate ; publicList

    < StreamingConfig>listAllStreamingConfigs( finalStringtable ) throwsIOException { List <
        StreamingConfig>streamingConfigs= new ArrayList ( );if(
        StringUtils .isEmpty(table)){streamingConfigs =
            getStreamingManager ( ).listAllStreaming();}else
        { StreamingConfig config
            = getStreamingManager ( ).getStreamingConfig(table);if(
            config !=null ) {streamingConfigs .
                add(config);}}
            return
        streamingConfigs

        ; }public
    List

    < StreamingConfig>getStreamingConfigs( finalStringtable , finalString project , finalInteger limit , finalInteger
            offset ) throwsIOException { aclEvaluate .
        checkProjectWritePermission(project);List<
        StreamingConfig>streamingConfigs; streamingConfigs=
        listAllStreamingConfigs ( table);if(

        limit ==null || offset == null ) {return streamingConfigs
            ; }if
        (

        ( streamingConfigs.size()-offset ) <limit ) {return streamingConfigs
            . subList(offset,streamingConfigs. size());}return
        streamingConfigs

        . subList(offset,offset+ limit ) ;}public
    StreamingConfig

    createStreamingConfig ( StreamingConfigconfig, Stringproject ) throwsIOException { aclEvaluate .
        checkProjectAdminPermission(project);Messagemsg
        = MsgPicker . getMsg();if(

        getStreamingManager ().getStreamingConfig(config.getName())!=null ) {throw new
            BadRequestException ( String.
                    format(Locale.ROOT,msg. getSTREAMING_CONFIG_ALREADY_EXIST(),config. getName()));}StreamingConfig
        streamingConfig
        = getStreamingManager ( ).createStreamingConfig(config);returnstreamingConfig
        ; }public
    StreamingConfig

    updateStreamingConfig ( StreamingConfigconfig, Stringproject ) throwsIOException { aclEvaluate .
        checkProjectAdminPermission(project);returngetStreamingManager
        ( ).updateStreamingConfig(config);}public
    void

    dropStreamingConfig ( StreamingConfigconfig, Stringproject ) throwsIOException { aclEvaluate .
        checkProjectAdminPermission(project);getStreamingManager(
        ).removeStreamingConfig(config);}}
    