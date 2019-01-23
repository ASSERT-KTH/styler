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

package org.apache.kylin.job.dao;

import java.util .Map;importorg.apache.kylin.common.

persistence .RootPersistentEntity;importcom.fasterxml.jackson.
annotation .JsonAutoDetect;importcom.fasterxml.jackson.
annotation .JsonProperty;importcom.google.common.

collect
.Maps;/**
 */@
SuppressWarnings("serial") @ JsonAutoDetect(fieldVisibility=JsonAutoDetect. Visibility . NONE,getterVisibility=JsonAutoDetect. Visibility . NONE,isGetterVisibility=JsonAutoDetect. Visibility . NONE,setterVisibility=JsonAutoDetect.
Visibility . NONE ) public class

    ExecutableOutputPOextendsRootPersistentEntity{@
    JsonProperty ( "content")

    privateStringcontent;@
    JsonProperty ( "status" ) privateString

    status="READY";@
    JsonProperty ("info")private Map< String , String>info=Maps.

    newHashMap ( );public String
        getContent ()
    {

    return content ;}public voidsetContent (
        Stringcontent) { this.
    content

    = content ;}public String
        getStatus ()
    {

    return status ;}public voidsetStatus (
        Stringstatus) { this.
    status

    = status;}public Map< String,String >
        getInfo ()
    {

    return info ;}publicvoidsetInfo( Map< String, String
        >info) { this.
    info
=
