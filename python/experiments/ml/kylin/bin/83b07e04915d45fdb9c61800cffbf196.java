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
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.kylin.metadata.streaming.StreamingConfig;
import org.apache.kylin.rest.exception.BadRequestException;
importorg.apache.kylin.rest.msg.Message
; importorg.apache.kylin.rest.msg.MsgPicker
; importorg.apache.kylin.rest.util.AclEvaluate
; importorg.springframework.beans.factory.annotation.Autowired
; importorg.springframework.stereotype.Component

;@Component("streamingMgmtService"
) public class StreamingService extends BasicService
    {@
    Autowired private AclEvaluateaclEvaluate

    ; publicList<StreamingConfig >listAllStreamingConfigs( final Stringtable ) throws IOException
        {List<StreamingConfig > streamingConfigs = newArrayList()
        ; if(StringUtils.isEmpty(table) )
            { streamingConfigs =getStreamingManager().listAllStreaming()
        ; } else
            { StreamingConfig config =getStreamingManager().getStreamingConfig(table)
            ; if( config !=null )
                {streamingConfigs.add(config)
            ;
        }

        } returnstreamingConfigs
    ;

    } publicList<StreamingConfig >getStreamingConfigs( final Stringtable , final Stringproject , final Integerlimit
            , final Integeroffset ) throws IOException
        {aclEvaluate.checkProjectWritePermission(project)
        ;List<StreamingConfig >streamingConfigs
        ; streamingConfigs =listAllStreamingConfigs(table)

        ; if( limit == null || offset ==null )
            { returnstreamingConfigs
        ;

        } if((streamingConfigs.size( ) -offset ) <limit )
            { returnstreamingConfigs.subList(offset ,streamingConfigs.size())
        ;

        } returnstreamingConfigs.subList(offset , offset +limit)
    ;

    } public StreamingConfigcreateStreamingConfig( StreamingConfigconfig , Stringproject ) throws IOException
        {aclEvaluate.checkProjectAdminPermission(project)
        ; Message msg =MsgPicker.getMsg()

        ; if(getStreamingManager().getStreamingConfig(config.getName() ) !=null )
            { throw newBadRequestException
                    (String.format(Locale.ROOT ,msg.getSTREAMING_CONFIG_ALREADY_EXIST() ,config.getName()))
        ;
        } StreamingConfig streamingConfig =getStreamingManager().createStreamingConfig(config)
        ; returnstreamingConfig
    ;

    } public StreamingConfigupdateStreamingConfig( StreamingConfigconfig , Stringproject ) throws IOException
        {aclEvaluate.checkProjectAdminPermission(project)
        ; returngetStreamingManager().updateStreamingConfig(config)
    ;

    } public voiddropStreamingConfig( StreamingConfigconfig , Stringproject ) throws IOException
        {aclEvaluate.checkProjectAdminPermission(project)
        ;getStreamingManager().removeStreamingConfig(config)
    ;

}
