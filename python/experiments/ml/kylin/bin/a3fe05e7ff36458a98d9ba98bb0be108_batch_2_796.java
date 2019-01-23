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

package org.apache.kylin.dict.lookup;

import java.io
. IOException;importjava.util
. List;importjava.util
. NavigableSet;importjava.util
. Set;importjava.util.concurrent
. ConcurrentHashMap;importjava.util.concurrent
. ConcurrentMap;importjava.util.concurrent
. ExecutionException;importjava.util.concurrent

. TimeUnit;importcom.google.common.collect
. Sets;importorg.apache.kylin.common
. KylinConfig;importorg.apache.kylin.common.persistence
. JsonSerializer;importorg.apache.kylin.common.persistence
. ResourceStore;importorg.apache.kylin.common.persistence
. Serializer;importorg.apache.kylin.metadata
. TableMetadataManager;importorg.apache.kylin.source.IReadableTable
. TableSignature;importorg.slf4j
. Logger;importorg.slf4j

. LoggerFactory;importcom.google.common.cache
. CacheBuilder;importcom.google.common.cache
. CacheLoader;importcom.google.common.cache
. LoadingCache;importcom.google.common.cache
. RemovalListener;importcom.google.common.cache

. RemovalNotification ; public

    class ExtTableSnapshotInfoManager { private static final Loggerlogger=LoggerFactory.getLogger(ExtTableSnapshotInfoManager.
    class ) ;publicstaticSerializer < ExtTableSnapshotInfo > SNAPSHOT_SERIALIZER=newJsonSerializer<>(ExtTableSnapshotInfo.

    class
    ) ; // static cached instances privatestaticfinalConcurrentMap <KylinConfig , ExtTableSnapshotInfoManager > SERVICE_CACHE=newConcurrentHashMap <KylinConfig,ExtTableSnapshotInfoManager>

    ( ) ; publicstaticExtTableSnapshotInfoManager getInstance( KylinConfig
        config ) { ExtTableSnapshotInfoManagerr=SERVICE_CACHE.get(
        config ); if (r ==
            null ){synchronized(ExtTableSnapshotInfoManager .
                class ) {r=SERVICE_CACHE.get(
                config ); if (r ==
                    null ) { r=newExtTableSnapshotInfoManager(
                    config);SERVICE_CACHE.put (config,
                    r );if(SERVICE_CACHE. size () >
                        1){logger.warn(
                    "More than one singleton exist"
                )
            ;
        }
        } }}
    return

    r ; } publicstaticvoid clearCache
        ( ){synchronized (
            SERVICE_CACHE){SERVICE_CACHE.clear
        (
    )

    ;

    } } // ============================================================================private
    KylinConfig config;privateLoadingCache <String ,ExtTableSnapshotInfo >

    snapshotCache ;// resourceprivate ExtTableSnapshotInfoManager( KylinConfig
        config){ this .config
        =config; this .snapshotCache=CacheBuilder.newBuilder(). removalListener(newRemovalListener <String,ExtTableSnapshotInfo >
            ()
            { @ OverridepublicvoidonRemoval(RemovalNotification <String ,ExtTableSnapshotInfo >
                notification){ExtTableSnapshotInfoManager.logger. info ("Snapshot with resource path "+notification.
                        getKey ( ) +" is removed due to "+notification.getCause(
            )
        );}}).maximumSize(
                1000)//.expireAfterWrite (1,TimeUnit.DAYS). build(newCacheLoader <String,ExtTableSnapshotInfo >
                    ()
                    { @ OverridepublicExtTableSnapshotInfo load( String key )
                        throws Exception { ExtTableSnapshotInfosnapshot=ExtTableSnapshotInfoManager.this.load(
                        key );
                    return
                snapshot;}
    }

    )
    ; } /**
     *
     * @param signature source table signature
     * @param tableName
     * @return latest snapshot info
     * @throws IOException
     */publicExtTableSnapshotInfo getLatestSnapshot( TableSignature signature, String tableName )
        throws IOException { ExtTableSnapshotInfo snapshot=newExtTableSnapshotInfo (signature,
        tableName);snapshot.updateRandomUuid
        ( ) ; ExtTableSnapshotInfodupSnapshot=checkDupByInfo(
        snapshot );
    return

    dupSnapshot ; }publicExtTableSnapshotInfo getSnapshot( String
        snapshotResPath )
            { try{returnsnapshotCache.get(
        snapshotResPath ) ;} catch( ExecutionException
            e ) {thrownewRuntimeException(
        e
    )

    ; } }publicExtTableSnapshotInfo getSnapshot( String tableName, String
         snapshotID ){returngetSnapshot(ExtTableSnapshotInfo.getResourcePath (tableName,snapshotID
    )

    ) ;}publicList <ExtTableSnapshotInfo> getSnapshots( String tableName )
        throws IOException { StringtableSnapshotsPath=ExtTableSnapshotInfo.getResourceDir(
        tableName ) ; ResourceStorestore=TableMetadataManager.getInstance(this.config).getStore
        ( );returnstore.getAllResources (tableSnapshotsPath,
    SNAPSHOT_SERIALIZER

    ) ;}publicSet <String> getAllExtSnapshotResPaths ( )
        throwsIOException{Set < String >result=Sets.newHashSet
        ( ) ; ResourceStorestore=TableMetadataManager.getInstance(this.config).getStore
        ();Set < String >snapshotTablePaths=store.listResources(ResourceStore.
        EXT_SNAPSHOT_RESOURCE_ROOT ); if (snapshotTablePaths ==
            null ){
        return
        result ;} for ( StringsnapshotTablePath :
            snapshotTablePaths){Set < String >snapshotPaths=store.listResources(
            snapshotTablePath ); if (snapshotPaths !=
                null){result.addAll(
            snapshotPaths
        )
        ; }}
    return

    result ; }publicvoid removeSnapshot( String tableName, String snapshotID )
        throws IOException { StringsnapshotResPath=ExtTableSnapshotInfo.getResourcePath (tableName,
        snapshotID);snapshotCache.invalidate(
        snapshotResPath ) ; ResourceStorestore=TableMetadataManager.getInstance(this.config).getStore
        ();store.deleteResource(
    snapshotResPath

    )
    ; } /**
     * create ext table snapshot
     * @param signature
     * @param tableName
     * @param keyColumns
     *@param storageType
     * @param storageLocation   @return created snapshot
     * @throws IOException
     */publicExtTableSnapshotInfo createSnapshot( TableSignature signature, String tableName, StringsnapshotID, String[
                                               ] keyColumns, int shardNum, String storageType, String storageLocation )
        throws IOException { ExtTableSnapshotInfo snapshot=newExtTableSnapshotInfo
        ();snapshot.setUuid(
        snapshotID);snapshot.setSignature(
        signature);snapshot.setTableName(
        tableName);snapshot.setKeyColumns(
        keyColumns);snapshot.setStorageType(
        storageType);snapshot.setStorageLocationIdentifier(
        storageLocation);snapshot.setShardNum(
        shardNum);save(
        snapshot );
    return

    snapshot ; }publicvoid updateSnapshot( ExtTableSnapshotInfo extTableSnapshot )
        throwsIOException{save(
        extTableSnapshot);snapshotCache.invalidate(extTableSnapshot.getResourcePath(
    )

    ) ; }privateExtTableSnapshotInfo checkDupByInfo( ExtTableSnapshotInfo snapshot )
        throws IOException { ResourceStorestore=TableMetadataManager.getInstance(this.config).getStore
        ( ) ; StringresourceDir=snapshot.getResourceDir
        ();NavigableSet < String >existings=store.listResources(
        resourceDir ); if (existings
            == null)

        return null ; TableSignaturesig=snapshot.getSignature
        ( ); for ( Stringexisting :
            existings ) { ExtTableSnapshotInfoexistingSnapshot=load(
            existing
            ) ;// direct load from store if ( existingSnapshot !=null&&sig.equals(existingSnapshot.getSignature(
                ) ))
        return
        existingSnapshot ;}
    return

    null ; }privateExtTableSnapshotInfo load( String resourcePath )
        throws IOException { ResourceStorestore=TableMetadataManager.getInstance(this.config).getStore
        ( ) ; ExtTableSnapshotInfosnapshot=store.getResource (resourcePath,

        SNAPSHOT_SERIALIZER );
    return

    snapshot ; }publicvoid save( ExtTableSnapshotInfo snapshot )
        throws IOException { ResourceStorestore=TableMetadataManager.getInstance(this.config).getStore
        ( ) ; Stringpath=snapshot.getResourcePath
        ();store.checkAndPutResource (path ,snapshot,
    SNAPSHOT_SERIALIZER

)
