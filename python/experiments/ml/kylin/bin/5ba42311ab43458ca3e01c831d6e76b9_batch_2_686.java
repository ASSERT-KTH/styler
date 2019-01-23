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
import org.apache.kylin.common.
KylinConfig ;importorg.apache.kylin.common.persistence.
JsonSerializer ;importorg.apache.kylin.common.persistence.
ResourceStore ;importorg.apache.kylin.common.persistence.
Serializer ;importorg.apache.kylin.metadata.
TableMetadataManager ;importorg.apache.kylin.source.IReadableTable.
TableSignature ;importorg.slf4j.
Logger ;importorg.slf4j.

LoggerFactory ;importcom.google.common.cache.
CacheBuilder ;importcom.google.common.cache.
CacheLoader ;importcom.google.common.cache.
LoadingCache ;importcom.google.common.cache.
RemovalListener ;importcom.google.common.cache.

RemovalNotification ; public class

    ExtTableSnapshotInfoManager { private static final Logger logger=LoggerFactory.getLogger(ExtTableSnapshotInfoManager.class
    ) ; publicstaticSerializer< ExtTableSnapshotInfo > SNAPSHOT_SERIALIZER =newJsonSerializer<>(ExtTableSnapshotInfo.class

    )
    ; // static cached instances private staticfinalConcurrentMap< KylinConfig, ExtTableSnapshotInfoManager > SERVICE_CACHE =newConcurrentHashMap< KylinConfig,ExtTableSnapshotInfoManager>(

    ) ; public staticExtTableSnapshotInfoManagergetInstance (KylinConfig config
        ) { ExtTableSnapshotInfoManager r=SERVICE_CACHE.get(config
        ) ;if ( r== null
            ) {synchronized(ExtTableSnapshotInfoManager. class
                ) { r=SERVICE_CACHE.get(config
                ) ;if ( r== null
                    ) { r =newExtTableSnapshotInfoManager(config
                    );SERVICE_CACHE.put( config,r
                    ) ;if(SERVICE_CACHE.size ( )> 1
                        ){logger.warn("More than one singleton exist"
                    )
                ;
            }
        }
        } }return
    r

    ; } public staticvoidclearCache (
        ) {synchronized( SERVICE_CACHE
            ){SERVICE_CACHE.clear(
        )
    ;

    }

    } // ============================================================================ privateKylinConfig
    config ;privateLoadingCache< String, ExtTableSnapshotInfo> snapshotCache

    ; // resourceprivateExtTableSnapshotInfoManager (KylinConfig config
        ){this . config=
        config;this . snapshotCache=CacheBuilder.newBuilder().removalListener (newRemovalListener< String,ExtTableSnapshotInfo> (
            ){
            @ Override publicvoidonRemoval(RemovalNotification< String, ExtTableSnapshotInfo> notification
                ){ExtTableSnapshotInfoManager.logger.info ( "Snapshot with resource path "+notification.getKey
                        ( ) + " is removed due to "+notification.getCause()
            )
        ;}}).maximumSize(1000
                )//.expireAfterWrite( 1,TimeUnit.DAYS).build (newCacheLoader< String,ExtTableSnapshotInfo> (
                    ){
                    @ Override publicExtTableSnapshotInfoload (String key ) throws
                        Exception { ExtTableSnapshotInfo snapshot=ExtTableSnapshotInfoManager.this.load(key
                        ) ;return
                    snapshot
                ;}}
    )

    ;
    } /**
     *
     * @param signature source table signature
     * @param tableName
     * @return latest snapshot info
     * @throws IOException
     */ publicExtTableSnapshotInfogetLatestSnapshot (TableSignature signature ,String tableName ) throws
        IOException { ExtTableSnapshotInfo snapshot =newExtTableSnapshotInfo( signature,tableName
        );snapshot.updateRandomUuid(
        ) ; ExtTableSnapshotInfo dupSnapshot=checkDupByInfo(snapshot
        ) ;return
    dupSnapshot

    ; } publicExtTableSnapshotInfogetSnapshot (String snapshotResPath
        ) {
            try {returnsnapshotCache.get(snapshotResPath
        ) ; }catch (ExecutionException e
            ) { thrownewRuntimeException(e
        )
    ;

    } } publicExtTableSnapshotInfogetSnapshot (String tableName ,String snapshotID
         ) {returngetSnapshot(ExtTableSnapshotInfo.getResourcePath( tableName,snapshotID)
    )

    ; }publicList< ExtTableSnapshotInfo>getSnapshots (String tableName ) throws
        IOException { String tableSnapshotsPath=ExtTableSnapshotInfo.getResourceDir(tableName
        ) ; ResourceStore store=TableMetadataManager.getInstance(this.config).getStore(
        ) ;returnstore.getAllResources( tableSnapshotsPath,SNAPSHOT_SERIALIZER
    )

    ; }publicSet< String>getAllExtSnapshotResPaths ( ) throws
        IOException{Set< String > result=Sets.newHashSet(
        ) ; ResourceStore store=TableMetadataManager.getInstance(this.config).getStore(
        );Set< String > snapshotTablePaths=store.listResources(ResourceStore.EXT_SNAPSHOT_RESOURCE_ROOT
        ) ;if ( snapshotTablePaths== null
            ) {return
        result
        ; }for ( String snapshotTablePath: snapshotTablePaths
            ){Set< String > snapshotPaths=store.listResources(snapshotTablePath
            ) ;if ( snapshotPaths!= null
                ){result.addAll(snapshotPaths
            )
        ;
        } }return
    result

    ; } publicvoidremoveSnapshot (String tableName ,String snapshotID ) throws
        IOException { String snapshotResPath=ExtTableSnapshotInfo.getResourcePath( tableName,snapshotID
        );snapshotCache.invalidate(snapshotResPath
        ) ; ResourceStore store=TableMetadataManager.getInstance(this.config).getStore(
        );store.deleteResource(snapshotResPath
    )

    ;
    } /**
     * create ext table snapshot
     * @param signature
     * @param tableName
     * @param keyColumns
     *@param storageType
     * @param storageLocation   @return created snapshot
     * @throws IOException
     */ publicExtTableSnapshotInfocreateSnapshot (TableSignature signature ,String tableName ,String snapshotID,String []
                                               keyColumns ,int shardNum ,String storageType ,String storageLocation ) throws
        IOException { ExtTableSnapshotInfo snapshot =newExtTableSnapshotInfo(
        );snapshot.setUuid(snapshotID
        );snapshot.setSignature(signature
        );snapshot.setTableName(tableName
        );snapshot.setKeyColumns(keyColumns
        );snapshot.setStorageType(storageType
        );snapshot.setStorageLocationIdentifier(storageLocation
        );snapshot.setShardNum(shardNum
        );save(snapshot
        ) ;return
    snapshot

    ; } publicvoidupdateSnapshot (ExtTableSnapshotInfo extTableSnapshot ) throws
        IOException{save(extTableSnapshot
        );snapshotCache.invalidate(extTableSnapshot.getResourcePath()
    )

    ; } privateExtTableSnapshotInfocheckDupByInfo (ExtTableSnapshotInfo snapshot ) throws
        IOException { ResourceStore store=TableMetadataManager.getInstance(this.config).getStore(
        ) ; String resourceDir=snapshot.getResourceDir(
        );NavigableSet< String > existings=store.listResources(resourceDir
        ) ;if ( existings==
            null )return

        null ; TableSignature sig=snapshot.getSignature(
        ) ;for ( String existing: existings
            ) { ExtTableSnapshotInfo existingSnapshot=load(existing
            )
            ; // direct load from storeif ( existingSnapshot != null&&sig.equals(existingSnapshot.getSignature()
                ) )return
        existingSnapshot
        ; }return
    null

    ; } privateExtTableSnapshotInfoload (String resourcePath ) throws
        IOException { ResourceStore store=TableMetadataManager.getInstance(this.config).getStore(
        ) ; ExtTableSnapshotInfo snapshot=store.getResource( resourcePath,SNAPSHOT_SERIALIZER

        ) ;return
    snapshot

    ; } publicvoidsave (ExtTableSnapshotInfo snapshot ) throws
        IOException { ResourceStore store=TableMetadataManager.getInstance(this.config).getStore(
        ) ; String path=snapshot.getResourcePath(
        );store.checkAndPutResource( path, snapshot,SNAPSHOT_SERIALIZER
    )

;
