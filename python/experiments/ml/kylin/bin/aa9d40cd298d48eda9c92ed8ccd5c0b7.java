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
import java.util.concurrent.ConcurrentHashMap
; importjava.util.concurrent.ConcurrentMap
; importjava.util.concurrent.ExecutionException
; importjava.util.concurrent.TimeUnit

; importcom.google.common.collect.Sets
; importorg.apache.kylin.common.KylinConfig
; importorg.apache.kylin.common.persistence.JsonSerializer
; importorg.apache.kylin.common.persistence.ResourceStore
; importorg.apache.kylin.common.persistence.Serializer
; importorg.apache.kylin.metadata.TableMetadataManager
; importorg.apache.kylin.source.IReadableTable.TableSignature
; importorg.slf4j.Logger
; importorg.slf4j.LoggerFactory

; importcom.google.common.cache.CacheBuilder
; importcom.google.common.cache.CacheLoader
; importcom.google.common.cache.LoadingCache
; importcom.google.common.cache.RemovalListener
; importcom.google.common.cache.RemovalNotification

; public class ExtTableSnapshotInfoManager

    { private static final Logger logger =LoggerFactory.getLogger(ExtTableSnapshotInfoManager.class)
    ; public staticSerializer<ExtTableSnapshotInfo > SNAPSHOT_SERIALIZER = newJsonSerializer<>(ExtTableSnapshotInfo.class)

    ;
    // static cached instances private static finalConcurrentMap<KylinConfig ,ExtTableSnapshotInfoManager > SERVICE_CACHE = newConcurrentHashMap<KylinConfig ,ExtTableSnapshotInfoManager>()

    ; public static ExtTableSnapshotInfoManagergetInstance( KylinConfigconfig )
        { ExtTableSnapshotInfoManager r =SERVICE_CACHE.get(config)
        ; if( r ==null )
            { synchronized(ExtTableSnapshotInfoManager.class )
                { r =SERVICE_CACHE.get(config)
                ; if( r ==null )
                    { r = newExtTableSnapshotInfoManager(config)
                    ;SERVICE_CACHE.put(config ,r)
                    ; if(SERVICE_CACHE.size( ) >1 )
                        {logger.warn("More than one singleton exist")
                    ;
                }
            }
        }
        } returnr
    ;

    } public static voidclearCache( )
        { synchronized(SERVICE_CACHE )
            {SERVICE_CACHE.clear()
        ;
    }

    }

    // ============================================================================ private KylinConfigconfig
    ; privateLoadingCache<String ,ExtTableSnapshotInfo >snapshotCache ;

    // resource privateExtTableSnapshotInfoManager( KylinConfigconfig )
        {this. config =config
        ;this. snapshotCache =CacheBuilder.newBuilder().removalListener( newRemovalListener<String ,ExtTableSnapshotInfo>( )
            {@
            Override public voidonRemoval(RemovalNotification<String ,ExtTableSnapshotInfo >notification )
                {ExtTableSnapshotInfoManager.logger.info( "Snapshot with resource path " +notification.getKey(
                        ) + " is removed due to " +notification.getCause())
            ;
        }}).maximumSize(1000)
                //.expireAfterWrite(1 ,TimeUnit.DAYS).build( newCacheLoader<String ,ExtTableSnapshotInfo>( )
                    {@
                    Override public ExtTableSnapshotInfoload( Stringkey ) throws Exception
                        { ExtTableSnapshotInfo snapshot =ExtTableSnapshotInfoManager.this.load(key)
                        ; returnsnapshot
                    ;
                }})
    ;

    }
    /**
     *
     * @param signature source table signature
     * @param tableName
     * @return latest snapshot info
     * @throws IOException
     */ public ExtTableSnapshotInfogetLatestSnapshot( TableSignaturesignature , StringtableName ) throws IOException
        { ExtTableSnapshotInfo snapshot = newExtTableSnapshotInfo(signature ,tableName)
        ;snapshot.updateRandomUuid()
        ; ExtTableSnapshotInfo dupSnapshot =checkDupByInfo(snapshot)
        ; returndupSnapshot
    ;

    } public ExtTableSnapshotInfogetSnapshot( StringsnapshotResPath )
        { try
            { returnsnapshotCache.get(snapshotResPath)
        ; } catch( ExecutionExceptione )
            { throw newRuntimeException(e)
        ;
    }

    } public ExtTableSnapshotInfogetSnapshot( StringtableName , StringsnapshotID )
         { returngetSnapshot(ExtTableSnapshotInfo.getResourcePath(tableName ,snapshotID))
    ;

    } publicList<ExtTableSnapshotInfo >getSnapshots( StringtableName ) throws IOException
        { String tableSnapshotsPath =ExtTableSnapshotInfo.getResourceDir(tableName)
        ; ResourceStore store =TableMetadataManager.getInstance(this.config).getStore()
        ; returnstore.getAllResources(tableSnapshotsPath ,SNAPSHOT_SERIALIZER)
    ;

    } publicSet<String >getAllExtSnapshotResPaths( ) throws IOException
        {Set<String > result =Sets.newHashSet()
        ; ResourceStore store =TableMetadataManager.getInstance(this.config).getStore()
        ;Set<String > snapshotTablePaths =store.listResources(ResourceStore.EXT_SNAPSHOT_RESOURCE_ROOT)
        ; if( snapshotTablePaths ==null )
            { returnresult
        ;
        } for( String snapshotTablePath :snapshotTablePaths )
            {Set<String > snapshotPaths =store.listResources(snapshotTablePath)
            ; if( snapshotPaths !=null )
                {result.addAll(snapshotPaths)
            ;
        }
        } returnresult
    ;

    } public voidremoveSnapshot( StringtableName , StringsnapshotID ) throws IOException
        { String snapshotResPath =ExtTableSnapshotInfo.getResourcePath(tableName ,snapshotID)
        ;snapshotCache.invalidate(snapshotResPath)
        ; ResourceStore store =TableMetadataManager.getInstance(this.config).getStore()
        ;store.deleteResource(snapshotResPath)
    ;

    }
    /**
     * create ext table snapshot
     * @param signature
     * @param tableName
     * @param keyColumns
     *@param storageType
     * @param storageLocation   @return created snapshot
     * @throws IOException
     */ public ExtTableSnapshotInfocreateSnapshot( TableSignaturesignature , StringtableName , StringsnapshotID ,String[ ]keyColumns
                                               , intshardNum , StringstorageType , StringstorageLocation ) throws IOException
        { ExtTableSnapshotInfo snapshot = newExtTableSnapshotInfo()
        ;snapshot.setUuid(snapshotID)
        ;snapshot.setSignature(signature)
        ;snapshot.setTableName(tableName)
        ;snapshot.setKeyColumns(keyColumns)
        ;snapshot.setStorageType(storageType)
        ;snapshot.setStorageLocationIdentifier(storageLocation)
        ;snapshot.setShardNum(shardNum)
        ;save(snapshot)
        ; returnsnapshot
    ;

    } public voidupdateSnapshot( ExtTableSnapshotInfoextTableSnapshot ) throws IOException
        {save(extTableSnapshot)
        ;snapshotCache.invalidate(extTableSnapshot.getResourcePath())
    ;

    } private ExtTableSnapshotInfocheckDupByInfo( ExtTableSnapshotInfosnapshot ) throws IOException
        { ResourceStore store =TableMetadataManager.getInstance(this.config).getStore()
        ; String resourceDir =snapshot.getResourceDir()
        ;NavigableSet<String > existings =store.listResources(resourceDir)
        ; if( existings ==null
            ) returnnull

        ; TableSignature sig =snapshot.getSignature()
        ; for( String existing :existings )
            { ExtTableSnapshotInfo existingSnapshot =load(existing)
            ;
            // direct load from store if( existingSnapshot != null &&sig.equals(existingSnapshot.getSignature())
                ) returnexistingSnapshot
        ;
        } returnnull
    ;

    } private ExtTableSnapshotInfoload( StringresourcePath ) throws IOException
        { ResourceStore store =TableMetadataManager.getInstance(this.config).getStore()
        ; ExtTableSnapshotInfo snapshot =store.getResource(resourcePath ,SNAPSHOT_SERIALIZER)

        ; returnsnapshot
    ;

    } public voidsave( ExtTableSnapshotInfosnapshot ) throws IOException
        { ResourceStore store =TableMetadataManager.getInstance(this.config).getStore()
        ; String path =snapshot.getResourcePath()
        ;store.checkAndPutResource(path ,snapshot ,SNAPSHOT_SERIALIZER)
    ;

}
