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

package org.apache.kylin.rest.job;

import java.io.IOException;import
java .util.Collections;import
java .util.List;import
java .util.NavigableSet;import
java .util.Set;import

java .util.TreeSet;importorg.apache.
kylin .common.KylinConfig;importorg.apache.kylin.
common .persistence.ResourceStore;importorg.apache.
kylin .cube.CubeInstance;importorg.apache.
kylin .cube.CubeManager;importorg.apache.
kylin .cube.CubeSegment;importorg.apache.kylin.
job .dao.ExecutableDao;importorg.apache.kylin.
job .dao.ExecutablePO;importorg.apache.kylin.
job .execution.ExecutableState;import
org .slf4j.Logger;import

org .slf4j.LoggerFactory;importcom.google.
common .collect.Lists;importcom.google.

common . collect .

    Sets ; public class MetadataCleanupJob { privatestaticfinalLoggerlogger=LoggerFactory.getLogger

    ( MetadataCleanupJob . class ) ; private static final long NEW_RESOURCE_THREADSHOLD_MS= 12

    *

    3600 * 1000L;

    // 12 hour // ============================================================================finalKylinConfigconfig ; private List<String>garbageResources=

    Collections .emptyList( )
        ;publicMetadataCleanupJob(){this(KylinConfig
    .

    getInstanceFromEnv ()) ;} public
        MetadataCleanupJob(KylinConfig config ){
    this

    . config=config; }publicList <
        String >getGarbageResources
    (

    )
    { returngarbageResources;} // function entrancepublicList <String > cleanup( boolean delete ,
        int jobOutdatedDays ) throwsException{CubeManagercubeManager=CubeManager
        . getInstance ( config);ResourceStorestore=ResourceStore
        . getStore ( config);longnewResourceTimeCut = System.

        currentTimeMillis()- NEW_RESOURCE_THREADSHOLD_MS ; List<String>toDeleteCandidates=

        Lists
        . newArrayList( ) ; // two level resources, snapshot tables and cube statistics for(String resourceRoot :newString[
                ]{ResourceStore. SNAPSHOT_RESOURCE_ROOT,ResourceStore.CUBE_STATISTICS_ROOT ,
            ResourceStore .EXT_SNAPSHOT_RESOURCE_ROOT } ) {for(Stringdir:noNull(store. listResources
                ( resourceRoot) ) ) {for(Stringres:noNull(store. listResources
                    ( dir))){if( store .getResourceTimestamp
                        (res)<newResourceTimeCut)toDeleteCandidates
                .
            add
        (

        res
        ) ;} } } // three level resources, only dictionaries for(String resourceRoot :newString [] {
            ResourceStore .DICT_RESOURCE_ROOT } ) {for(Stringdir:noNull(store. listResources
                ( resourceRoot) ) ) {for(Stringdir2:noNull(store. listResources
                    ( dir) ) ) {for(Stringres:noNull(store. listResources
                        ( dir2))){if( store .getResourceTimestamp
                            (res)<newResourceTimeCut)toDeleteCandidates
                    .
                add
            (
        res

        )
        ;}}} } // exclude resources in use Set<String>activeResources=
        Sets .newHashSet ( ) ;for(CubeInstancecube: cubeManager
            .listAllCubes()){activeResources.addAll(cube.getSnapshots()
            . values( ) ) ;for(CubeSegmentsegment: cube
                .getSegments()){activeResources.addAll(segment
                .getSnapshotPaths());activeResources.addAll(segment
                .getDictionaryPaths());activeResources.add(segment
            .
        getStatisticsResourcePath
        ());}}toDeleteCandidates

        .
        removeAll ( activeResources );// delete old and completed jobslongoutdatedJobTimeCut = System . currentTimeMillis ( ) - jobOutdatedDays*
        24 * 3600 *1000L;ExecutableDaoexecutableDao=ExecutableDao
        .getInstance(config ) ; List<ExecutablePO>allExecutable=
        executableDao .getJobs ( ) ;for (
            ExecutablePO executable : allExecutable){longlastModified=
            executable . getLastModified ();StringjobStatus=executableDao.getJobOutput(executable.getUuid()

            ) .getStatus ( ) ; if(lastModified<outdatedJobTimeCut&&(ExecutableState.SUCCEED.toString(
                    ) .equals(jobStatus)||ExecutableState.DISCARDED.toString(). equals
                (jobStatus))){toDeleteCandidates . add ( ResourceStore.EXECUTE_RESOURCE_ROOT+"/"+executable
                .getUuid());toDeleteCandidates . add ( ResourceStore.EXECUTE_OUTPUT_RESOURCE_ROOT+"/"+executable

                . getUuid( ) ) ;for(ExecutablePOtask: executable
                    .getTasks()){toDeleteCandidates . add ( ResourceStore.EXECUTE_OUTPUT_RESOURCE_ROOT+"/"+task
                .
            getUuid
        (

        ) ) ;}}} garbageResources=cleanupConclude
        ( delete,
    toDeleteCandidates

    ) ;returngarbageResources; }privateList <String >cleanupConclude(boolean delete, List
        < String>toDeleteResources){if( toDeleteResources
            .isEmpty()){logger
            . info(
        "No metadata resource to clean up"

        );returntoDeleteResources;}logger.info ( toDeleteResources.size

        ( )+" metadata resource to clean up" )
            ; if ( delete){ResourceStorestore=ResourceStore
            . getStore( config ) ;for (
                Stringres:toDeleteResources) { logger.info
                ( "Deleting metadata "
                    +res);try{store
                . deleteResource (res ); }
                    catch(IOExceptione) { logger. error("Failed to delete resource "
                +
            res
        , e )
            ; }} } else {for (
                Stringres:toDeleteResources) { logger.info
            (
        "Dry run, pending delete metadata "
        + res)
    ;

    } }returntoDeleteResources; }privateNavigableSet<String> noNull( NavigableSet
        < String> list ){ return ( list==null)?new TreeSet <String
    >

(
