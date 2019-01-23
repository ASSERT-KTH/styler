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
import java.util.ArrayList;
import java.util.List;
import java.util .Locale;importorg.apache.commons.
lang3 .StringUtils;importorg.apache.kylin.metadata.
streaming .StreamingConfig;importorg.apache.kylin.rest.
exception .BadRequestException;importorg.apache.kylin.rest.
msg .Message;importorg.apache.kylin.rest.
msg .MsgPicker;importorg.apache.kylin.rest.
util .AclEvaluate;importorg.springframework.beans.factory.
annotation .Autowired;importorg.springframework.

stereotype.Component;@
Component ( "streamingMgmtService" ) public class
    StreamingServiceextends
    BasicService { @Autowired

    private AclEvaluateaclEvaluate;public List<StreamingConfig > listAllStreamingConfigs( final String table
        )throwsIOException{ List < StreamingConfig >streamingConfigs=new
        ArrayList ();if(StringUtils.isEmpty (
            table ) ){streamingConfigs=getStreamingManager().
        listAllStreaming ( )
            ; } else {StreamingConfigconfig=getStreamingManager().getStreamingConfig
            ( table) ; if( config
                !=null){streamingConfigs.add
            (
        config

        ) ;}
    }

    return streamingConfigs;}public List<StreamingConfig > getStreamingConfigs( final String table, final String project,
            final Integer limit, final Integer offset
        )throwsIOException{aclEvaluate.checkProjectWritePermission
        (project); List<
        StreamingConfig > streamingConfigs;streamingConfigs=listAllStreamingConfigs

        ( table) ; if ( limit == null|| offset
            == null)
        {

        return streamingConfigs;}if((streamingConfigs . size( ) -offset )
            < limit){returnstreamingConfigs. subList(offset,streamingConfigs.size
        (

        ) );}returnstreamingConfigs. subList ( offset,offset
    +

    limit ) ;}public StreamingConfigcreateStreamingConfig ( StreamingConfigconfig , String project
        )throwsIOException{aclEvaluate.checkProjectAdminPermission
        ( project ) ;Messagemsg=MsgPicker.

        getMsg ();if(getStreamingManager().getStreamingConfig(config. getName () )
            != null ){
                    thrownewBadRequestException(String.format( Locale.ROOT,msg. getSTREAMING_CONFIG_ALREADY_EXIST(),config.getName(
        )
        ) ) ; }StreamingConfigstreamingConfig=getStreamingManager().createStreamingConfig
        ( config)
    ;

    return streamingConfig ;}public StreamingConfigupdateStreamingConfig ( StreamingConfigconfig , String project
        )throwsIOException{aclEvaluate.checkProjectAdminPermission
        ( project);returngetStreamingManager().updateStreamingConfig
    (

    config ) ;}public voiddropStreamingConfig ( StreamingConfigconfig , String project
        )throwsIOException{aclEvaluate.checkProjectAdminPermission
        (project);getStreamingManager().removeStreamingConfig
    (

config
