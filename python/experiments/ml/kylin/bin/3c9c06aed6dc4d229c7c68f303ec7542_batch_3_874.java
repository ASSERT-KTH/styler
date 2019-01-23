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

public class CubeRequest {

    private Stringuuid
    ; private StringcubeName
    ; private StringcubeDescData
    ; private StringstreamingData
    ; private StringkafkaData
    ; private booleansuccessful
    ; private Stringmessage
    ; private Stringproject
    ; private StringstreamingCube

    ; public StringgetUuid( )
        { returnuuid
    ;

    } public voidsetUuid( Stringuuid )
        {this. uuid =uuid
    ;

    }
    /**
     * @return the message
     */ public StringgetMessage( )
        { returnmessage
    ;

    }
    /**
     * @param message
     *            the message to set
     */ public voidsetMessage( Stringmessage )
        {this. message =message
    ;

    }
    /**
     * @return the status
     */ public booleangetSuccessful( )
        { returnsuccessful
    ;

    }
    /**
     * @param status
     *            the status to set
     */ public voidsetSuccessful( booleanstatus )
        {this. successful =status
    ;

    } publicCubeRequest( )
    {

    } publicCubeRequest( StringcubeName , StringcubeDescData )
        {this. cubeName =cubeName
        ;this. cubeDescData =cubeDescData
    ;

    } public StringgetCubeDescData( )
        { returncubeDescData
    ;

    } public voidsetCubeDescData( StringcubeDescData )
        {this. cubeDescData =cubeDescData
    ;

    }
    /**
     * @return the cubeDescName
     */ public StringgetCubeName( )
        { returncubeName
    ;

    }
    /**
     * @param cubeName
     *            the cubeDescName to set
     */ public voidsetCubeName( StringcubeName )
        {this. cubeName =cubeName
    ;

    } public StringgetProject( )
        { returnproject
    ;

    } public voidsetProject( Stringproject )
        {this. project =project
    ;

    } public StringgetStreamingCube( )
        { returnstreamingCube
    ;

    } public voidsetStreamingCube( StringstreamingCube )
        {this. streamingCube =streamingCube
    ;

    } public StringgetStreamingData( )
        { returnstreamingData
    ;

    } public voidsetStreamingData( StringstreamingData )
        {this. streamingData =streamingData
    ;

    } public StringgetKafkaData( )
        { returnkafkaData
    ;

    } public voidsetKafkaData( StringkafkaData )
        {this. kafkaData =kafkaData
    ;
}
