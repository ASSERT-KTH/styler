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

import java.util.Collection;
import java.util.List;

import com.google.common.
collect .Lists;importorg.apache.kylin.metadata.
model .DataModelDesc;importorg.apache.kylin.rest.
request .HybridRequest;importorg.apache.kylin.rest.
response .HybridRespone;importorg.apache.kylin.rest.
service .HybridService;importorg.apache.kylin.storage.
hybrid .HybridInstance;importorg.springframework.beans.factory.
annotation .Autowired;importorg.springframework.
stereotype .Controller;importorg.springframework.web.bind.
annotation .PathVariable;importorg.springframework.web.bind.
annotation .RequestBody;importorg.springframework.web.bind.
annotation .RequestMapping;importorg.springframework.web.bind.
annotation .RequestMethod;importorg.springframework.web.bind.
annotation .RequestParam;importorg.springframework.web.bind.

annotation.
ResponseBody;@Controller @ RequestMapping(
value = "/hybrids" ) public class

    HybridControllerextends
    BasicController { @Autowired

    privateHybridServicehybridService; @ RequestMapping( value = "",method= RequestMethod . POST , produces=
    {"application/json"
    } ) @ResponseBodypublicHybridRespone create (@ RequestBody
        HybridRequestrequest){ checkRequiredArg("hybrid",request.getHybrid
        ()); checkRequiredArg("project",request.getProject
        ()); checkRequiredArg("model",request.getModel
        ()); checkRequiredArg("cubes",request.getCubes
        ( ) ) ;HybridInstancehybridInstance=hybridService.createHybridInstance(request. getHybrid(),request. getProject(),request.
                getModel(),request.getCubes
        ( ));returnhybridInstance2response
    (

    hybridInstance);} @ RequestMapping( value = "",method= RequestMethod . PUT , produces=
    {"application/json"
    } ) @ResponseBodypublicHybridRespone update (@ RequestBody
        HybridRequestrequest){ checkRequiredArg("hybrid",request.getHybrid
        ()); checkRequiredArg("project",request.getProject
        ()); checkRequiredArg("model",request.getModel
        ()); checkRequiredArg("cubes",request.getCubes
        ( ) ) ;HybridInstancehybridInstance=hybridService.updateHybridInstance(request. getHybrid(),request. getProject(),request.
                getModel(),request.getCubes
        ( ));returnhybridInstance2response
    (

    hybridInstance);} @ RequestMapping( value = "",method= RequestMethod . DELETE , produces=
    {"application/json"
    } ) @ResponseBodypublic voiddelete ( Stringhybrid ,
        Stringproject){ checkRequiredArg("hybrid"
        ,hybrid); checkRequiredArg("project"
        ,project);hybridService. deleteHybridInstance(hybrid
    ,

    project);} @ RequestMapping( value = "",method= RequestMethod . GET , produces=
    {"application/json"
    } )@ResponseBodypublic Collection<HybridRespone>list( @ RequestParam( required =false )Stringproject, @ RequestParam( required =false )
        Stringmodel){ List < HybridInstance>hybridInstances=hybridService. listHybrids(project
        ,model); List < HybridRespone>hybridRespones=Lists.newArrayListWithCapacity(hybridInstances.size

        ( )) ; for (HybridInstance hybridInstance
            :hybridInstances){hybridRespones.add(hybridInstance2response(
        hybridInstance

        ) );
    }

    returnhybridRespones;} @ RequestMapping( value = "{hybrid}",method= RequestMethod . GET , produces=
    {"application/json"
    } ) @ResponseBodypublicHybridRespone get (@ PathVariable
        String hybrid ) {HybridInstancehybridInstance=hybridService.getHybridInstance
        ( hybrid);returnhybridInstance2response
    (

    hybridInstance ) ;}private HybridResponehybridInstance2response(
        HybridInstance hybridInstance ) {DataModelDescmodelDesc=hybridInstance.
        getModel ( );return new HybridRespone ( modelDesc==null ? HybridRespone.NO_PROJECT:modelDesc. getProject ( ) , modelDesc==null ? HybridRespone.NO_MODEL:modelDesc. getName()
    ,
hybridInstance
