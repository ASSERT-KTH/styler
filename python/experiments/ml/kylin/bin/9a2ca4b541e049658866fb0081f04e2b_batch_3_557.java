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

import java.io.IOException;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Sets;
import org.apache.kylin.common.KylinConfig;
import org.apache.kylin.common.persistence.JsonSerializer;
import org.apache.kylin.common.persistence.ResourceStore;
import org.apache.kylin.common.persistence.Serializer;
import org.apache.kylin.metadata.TableMetadataManager;
import org.apache.kylin.source.IReadableTable.TableSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.
cache .LoadingCache;importcom.google.common.
cache .RemovalListener;importcom.google.common.

cache . RemovalNotification ;

    public class ExtTableSnapshotInfoManager { private static finalLoggerlogger=LoggerFactory.getLogger(ExtTableSnapshotInfoManager
    . class );publicstatic Serializer < ExtTableSnapshotInfo >SNAPSHOT_SERIALIZER=newJsonSerializer<>(ExtTableSnapshotInfo

    .
    class ) ; // static cached instancesprivatestaticfinal ConcurrentMap< KylinConfig , ExtTableSnapshotInfoManager >SERVICE_CACHE=new ConcurrentHashMap<KylinConfig,ExtTableSnapshotInfoManager

    > ( ) ;publicstatic ExtTableSnapshotInfoManagergetInstance (
        KylinConfig config ) {ExtTableSnapshotInfoManagerr=SERVICE_CACHE.get
        ( config) ; if( r
            == null){synchronized( ExtTableSnapshotInfoManager
                . class ){r=SERVICE_CACHE.get
                ( config) ; if( r
                    == null ) {r=newExtTableSnapshotInfoManager
                    (config);SERVICE_CACHE. put(config
                    , r);if(SERVICE_CACHE . size( )
                        >1){logger.warn
                    (
                "More than one singleton exist"
            )
        ;
        } }}
    }

    return r ; }publicstatic void
        clearCache (){ synchronized
            (SERVICE_CACHE){SERVICE_CACHE.
        clear
    (

    )

    ; } }// ============================================================================
    private KylinConfigconfig;private LoadingCache< String, ExtTableSnapshotInfo

    > snapshotCache;// resource privateExtTableSnapshotInfoManager (
        KylinConfigconfig) { this.
        config=config ; this.snapshotCache=CacheBuilder.newBuilder() .removalListener(new RemovalListener<String, ExtTableSnapshotInfo
            >(
            ) { @OverridepublicvoidonRemoval( RemovalNotification< String, ExtTableSnapshotInfo
                >notification){ExtTableSnapshotInfoManager.logger . info("Snapshot with resource path "+notification
                        . getKey ( )+" is removed due to "+notification.getCause
            (
        ));}}).maximumSize
                (1000)//. expireAfterWrite(1,TimeUnit.DAYS) .build(new CacheLoader<String, ExtTableSnapshotInfo
                    >(
                    ) { @Overridepublic ExtTableSnapshotInfoload ( String key
                        ) throws Exception {ExtTableSnapshotInfosnapshot=ExtTableSnapshotInfoManager.this.load
                        ( key)
                    ;
                returnsnapshot;
    }

    }
    ) ; }/**
     *
     * @param signature source table signature
     * @param tableName
     * @return latest snapshot info
     * @throws IOException
     */public ExtTableSnapshotInfogetLatestSnapshot ( TableSignaturesignature , String tableName
        ) throws IOException { ExtTableSnapshotInfosnapshot=new ExtTableSnapshotInfo(signature
        ,tableName);snapshot.
        updateRandomUuid ( ) ;ExtTableSnapshotInfodupSnapshot=checkDupByInfo
        ( snapshot)
    ;

    return dupSnapshot ;}public ExtTableSnapshotInfogetSnapshot (
        String snapshotResPath
            ) {try{returnsnapshotCache.get
        ( snapshotResPath ); }catch (
            ExecutionException e ){thrownewRuntimeException
        (
    e

    ) ; }}public ExtTableSnapshotInfogetSnapshot ( StringtableName ,
         String snapshotID){returngetSnapshot(ExtTableSnapshotInfo. getResourcePath(tableName,
    snapshotID

    ) );}public List<ExtTableSnapshotInfo >getSnapshots ( String tableName
        ) throws IOException {StringtableSnapshotsPath=ExtTableSnapshotInfo.getResourceDir
        ( tableName ) ;ResourceStorestore=TableMetadataManager.getInstance(this.config).
        getStore ();returnstore. getAllResources(tableSnapshotsPath
    ,

    SNAPSHOT_SERIALIZER );}public Set<String > getAllExtSnapshotResPaths (
        )throwsIOException{ Set < String>result=Sets.
        newHashSet ( ) ;ResourceStorestore=TableMetadataManager.getInstance(this.config).
        getStore(); Set < String>snapshotTablePaths=store.listResources(ResourceStore
        . EXT_SNAPSHOT_RESOURCE_ROOT) ; if( snapshotTablePaths
            == null)
        {
        return result; } for (String snapshotTablePath
            :snapshotTablePaths){ Set < String>snapshotPaths=store.listResources
            ( snapshotTablePath) ; if( snapshotPaths
                !=null){result.addAll
            (
        snapshotPaths
        ) ;}
    }

    return result ;}public voidremoveSnapshot ( StringtableName , String snapshotID
        ) throws IOException {StringsnapshotResPath=ExtTableSnapshotInfo. getResourcePath(tableName
        ,snapshotID);snapshotCache.invalidate
        ( snapshotResPath ) ;ResourceStorestore=TableMetadataManager.getInstance(this.config).
        getStore();store.deleteResource
    (

    snapshotResPath
    ) ; }/**
     * create ext table snapshot
     * @param signature
     * @param tableName
     * @param keyColumns
     *@param storageType
     * @param storageLocation   @return created snapshot
     * @throws IOException
     */public ExtTableSnapshotInfocreateSnapshot ( TableSignaturesignature , StringtableName ,StringsnapshotID ,String
                                               [ ]keyColumns , intshardNum , StringstorageType , String storageLocation
        ) throws IOException { ExtTableSnapshotInfosnapshot=new
        ExtTableSnapshotInfo();snapshot.setUuid
        (snapshotID);snapshot.setSignature
        (signature);snapshot.setTableName
        (tableName);snapshot.setKeyColumns
        (keyColumns);snapshot.setStorageType
        (storageType);snapshot.setStorageLocationIdentifier
        (storageLocation);snapshot.setShardNum
        (shardNum);save
        ( snapshot)
    ;

    return snapshot ;}public voidupdateSnapshot ( ExtTableSnapshotInfo extTableSnapshot
        )throwsIOException{save
        (extTableSnapshot);snapshotCache.invalidate(extTableSnapshot.getResourcePath
    (

    ) ) ;}private ExtTableSnapshotInfocheckDupByInfo ( ExtTableSnapshotInfo snapshot
        ) throws IOException {ResourceStorestore=TableMetadataManager.getInstance(this.config).
        getStore ( ) ;StringresourceDir=snapshot.
        getResourceDir(); NavigableSet < String>existings=store.listResources
        ( resourceDir) ; if(
            existings ==null

        ) return null ;TableSignaturesig=snapshot.
        getSignature () ; for (String existing
            : existings ) {ExtTableSnapshotInfoexistingSnapshot=load
            (
            existing ); // direct load from store if ( existingSnapshot!=null&&sig.equals(existingSnapshot.getSignature
                ( ))
        )
        return existingSnapshot;
    }

    return null ;}private ExtTableSnapshotInfoload ( String resourcePath
        ) throws IOException {ResourceStorestore=TableMetadataManager.getInstance(this.config).
        getStore ( ) ;ExtTableSnapshotInfosnapshot=store. getResource(resourcePath

        , SNAPSHOT_SERIALIZER)
    ;

    return snapshot ;}public voidsave ( ExtTableSnapshotInfo snapshot
        ) throws IOException {ResourceStorestore=TableMetadataManager.getInstance(this.config).
        getStore ( ) ;Stringpath=snapshot.
        getResourcePath();store. checkAndPutResource( path,snapshot
    ,

SNAPSHOT_SERIALIZER
