/*
 * Copyright (c) 2002-2018 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.impl.storemigration.participant;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;

import org.neo4j.collection.PrimitiveLongCollections;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.internal.gbptree.RecoveryCleanupWorkCollector;
import org.neo4j.io.fs.FileSystemAbstraction;
import org.neo4j.io.fs.OpenMode;
import org.neo4j.io.fs.StoreChannel;
import org.neo4j.io.layout.DatabaseLayout;
import org.neo4j.io.pagecache.IOLimiter;
import org.neo4j.io.pagecache.PageCache;
import org.neo4j.kernel.api.labelscan.LabelScanWriter;
import org.neo4j.kernel.api.labelscan.NodeLabelUpdate;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.impl.api.scan.FullStoreChangeStream;
import org.neo4j.kernel.impl.index.labelscan.NativeLabelScanStore;
import org.neo4j.kernel.impl.store.InvalidIdGeneratorException;
import org.neo4j.kernel.impl.store.MetaDataStore;
import org.neo4j.kernel.impl.store.MetaDataStore.
Position ;importorg.neo4j.kernel.impl.store.format.standard.
StandardV2_3 ;importorg.neo4j.kernel.impl.store.format.standard.
StandardV3_2 ;importorg.neo4j.kernel.impl.store.format.standard.
StandardV3_4 ;importorg.neo4j.kernel.impl.util.monitoring.
ProgressReporter ;importorg.neo4j.kernel.lifecycle.
Lifespan ;importorg.neo4j.kernel.monitoring.
Monitors ;importorg.neo4j.storageengine.api.schema.
LabelScanReader ;importorg.neo4j.test.
TestGraphDatabaseFactory ;importorg.neo4j.test.rule.
PageCacheRule ;importorg.neo4j.test.rule.
TestDirectory ;importorg.neo4j.test.rule.fs.

DefaultFileSystemRule ; importstaticjava.lang.String.
format ; importstaticorg.junit.Assert.
assertEquals ; importstaticorg.junit.Assert.
assertFalse ; importstaticorg.junit.Assert.
assertTrue ; importstaticorg.mockito.Mockito.
mock ; importstaticorg.mockito.Mockito.
times ; importstaticorg.mockito.Mockito.
verify ; importstaticorg.neo4j.kernel.impl.store.MetaDataStore.

versionStringToLong ; public
class
    NativeLabelScanStoreMigratorTest { private final TestDirectory testDirectory=TestDirectory.testDirectory(
    ) ; private final DefaultFileSystemRule fileSystemRule =newDefaultFileSystemRule(
    ) ; private final PageCacheRule pageCacheRule =newPageCacheRule(

    );
    @ Rule public RuleChain ruleChain=RuleChain. outerRule (testDirectory). around (fileSystemRule). around (pageCacheRule

    ) ; privateFile
    storeDir ; privateFile
    nativeLabelIndex ; privateDatabaseLayout
    migrationLayout ; privateFile

    luceneLabelScanStore ; private final ProgressReporter progressReporter= mock(ProgressReporter .class

    ) ; privateFileSystemAbstraction
    fileSystem ; privatePageCache
    pageCache ; privateNativeLabelScanStoreMigrator
    indexMigrator ; privateDatabaseLayout

    databaseLayout;
    @ Before publicvoidsetUp ( )
    throws
        Exception { databaseLayout=testDirectory.databaseLayout(
        ) ; storeDir=databaseLayout.databaseDirectory(
        ) ; nativeLabelIndex=databaseLayout.labelScanStore(
        ) ; migrationLayout=testDirectory. databaseLayout ("migrationDir"
        ) ; luceneLabelScanStore=testDirectory.databaseDir().toPath(). resolve(Paths. get( "schema", "label" , "lucene")).toFile(

        ) ; fileSystem=fileSystemRule.get(
        ) ; pageCache=pageCacheRule. getPageCache (fileSystemRule
        ) ; indexMigrator =new NativeLabelScanStoreMigrator( fileSystem, pageCache,Config.defaults ()
        );fileSystem. mkdirs (luceneLabelScanStore
    )

    ;}
    @ Test publicvoidskipMigrationIfNativeIndexExist ( )
    throws
        Exception { ByteBuffer sourceBuffer= writeFile( nativeLabelIndex ,newbyte[]{ 1, 2, 3}

        );indexMigrator. migrate( databaseLayout, migrationLayout, progressReporter,StandardV3_2. STORE_VERSION,StandardV3_2 .STORE_VERSION
        );indexMigrator. moveMigratedFiles( migrationLayout, databaseLayout,StandardV3_2. STORE_VERSION,StandardV3_2 .STORE_VERSION

        ) ; ByteBuffer resultBuffer= readFileContent( nativeLabelIndex ,3
        ); assertEquals( sourceBuffer ,resultBuffer
        ); assertTrue(fileSystem. fileExists ( luceneLabelScanStore)
    )

    ;}@ Test ( expected=InvalidIdGeneratorException .
    class ) publicvoidfailMigrationWhenNodeIdFileIsBroken ( )
    throws
        Exception{prepareEmpty23Database(
        ) ; File nodeIdFile=databaseLayout.idNodeStore(
        ); writeFile( nodeIdFile ,newbyte[]{ 1, 2, 3}

        );indexMigrator. migrate( databaseLayout, migrationLayout, progressReporter,StandardV3_2. STORE_VERSION,StandardV3_2 .STORE_VERSION
    )

    ;}
    @ Test publicvoidclearMigrationDirFromAnyLabelScanStoreBeforeMigrating ( )
    throws
        Exception
        {// givenprepareEmpty23Database(
        ); initializeNativeLabelScanStoreWithContent (migrationLayout
        ) ; File toBeDeleted=migrationLayout.labelScanStore(
        ); assertTrue(fileSystem. fileExists ( toBeDeleted)

        )
        ;// whenindexMigrator. migrate( databaseLayout, migrationLayout, progressReporter,StandardV3_2. STORE_VERSION,StandardV3_2 .STORE_VERSION

        )
        ;// then assertNoContentInNativeLabelScanStore (migrationLayout
    )

    ;}
    @ Test publicvoidluceneLabelIndexRemovedAfterSuccessfulMigration ( )
    throws
        IOException{prepareEmpty23Database(

        );indexMigrator. migrate( databaseLayout, migrationLayout, progressReporter,StandardV2_3. STORE_VERSION,StandardV3_2 .STORE_VERSION
        );indexMigrator. moveMigratedFiles( migrationLayout, databaseLayout,StandardV2_3. STORE_VERSION,StandardV3_2 .STORE_VERSION

        ); assertFalse(fileSystem. fileExists ( luceneLabelScanStore)
    )

    ;}
    @ Test publicvoidmoveCreatedNativeLabelIndexBackToStoreDirectory ( )
    throws
        IOException{prepareEmpty23Database(
        );indexMigrator. migrate( databaseLayout, migrationLayout, progressReporter,StandardV2_3. STORE_VERSION,StandardV3_2 .STORE_VERSION
        ) ; File migrationNativeIndex=migrationLayout.labelScanStore(
        ) ; ByteBuffer migratedFileContent= writeFile( migrationNativeIndex ,newbyte[]{ 5, 4, 3, 2, 1}

        );indexMigrator. moveMigratedFiles( migrationLayout, databaseLayout,StandardV2_3. STORE_VERSION,StandardV3_2 .STORE_VERSION

        ) ; ByteBuffer movedNativeIndex= readFileContent( nativeLabelIndex ,5
        ); assertEquals( migratedFileContent ,movedNativeIndex
    )

    ;}
    @ Test publicvoidpopulateNativeLabelScanIndexDuringMigration ( )
    throws
        IOException{prepare34DatabaseWithNodes(
        );indexMigrator. migrate( databaseLayout, migrationLayout, progressReporter,StandardV3_4. STORE_VERSION,StandardV3_4 .STORE_VERSION
        );indexMigrator. moveMigratedFiles( migrationLayout, databaseLayout,StandardV2_3. STORE_VERSION,StandardV3_2 .STORE_VERSION

        ) ; try ( Lifespan lifespan =newLifespan (
        )
            ) { NativeLabelScanStore labelScanStore= getNativeLabelScanStore( databaseLayout ,true
            );lifespan. add (labelScanStore
            ) ; for ( int labelId= 0 ; labelId< 10; labelId
            ++
                ) { try ( LabelScanReader labelScanReader=labelScanStore.newReader (
                )
                    ) { int nodeCount=PrimitiveLongCollections. count(labelScanReader. nodesWithLabel ( labelId)
                    ); assertEquals( format( "Expected to see only one node for label %d but was %d.", labelId ,nodeCount
                            ), 1 ,nodeCount
                )
            ;
        }
    }

    }}
    @ Test publicvoidreportProgressOnNativeIndexPopulation ( )
    throws
        IOException{prepare34DatabaseWithNodes(
        );indexMigrator. migrate( databaseLayout, migrationLayout, progressReporter,StandardV3_4. STORE_VERSION,StandardV3_4 .STORE_VERSION
        );indexMigrator. moveMigratedFiles( migrationLayout, databaseLayout,StandardV2_3. STORE_VERSION,StandardV3_2 .STORE_VERSION

        ); verify (progressReporter). start (10
        ); verify( progressReporter, times ( 10)). progress (1
    )

    ; } privateNativeLabelScanStore getNativeLabelScanStore (DatabaseLayout databaseLayout , boolean
    readOnly
        ) { returnnew NativeLabelScanStore( pageCache, databaseLayout, fileSystem,FullStoreChangeStream. EMPTY, readOnly ,newMonitors(
                ),RecoveryCleanupWorkCollector.ignore ()
    )

    ; } privatevoid initializeNativeLabelScanStoreWithContent ( DatabaseLayout databaseLayout )
    throws
        IOException { try ( Lifespan lifespan =newLifespan (
        )
            ) { NativeLabelScanStore nativeLabelScanStore= getNativeLabelScanStore( databaseLayout ,false
            );lifespan. add (nativeLabelScanStore
            ) ; try ( LabelScanWriter labelScanWriter=nativeLabelScanStore.newWriter (
            )
                ){labelScanWriter. write(NodeLabelUpdate. labelChanges( 1 ,newlong[0 ] ,newlong[]{ 1 })
            )
            ;}nativeLabelScanStore. force(IOLimiter .UNLIMITED
        )
    ;

    } } privatevoid assertNoContentInNativeLabelScanStore ( DatabaseLayout
    databaseLayout
        ) { try ( Lifespan lifespan =newLifespan (
        )
            ) { NativeLabelScanStore nativeLabelScanStore= getNativeLabelScanStore( databaseLayout ,true
            );lifespan. add (nativeLabelScanStore
            ) ; try ( LabelScanReader labelScanReader=nativeLabelScanStore.newReader (
            )
                ) { int count=PrimitiveLongCollections. count(labelScanReader. nodesWithLabel ( 1)
                ); assertEquals( 0 ,count
            )
        ;
    }

    } } privateByteBuffer writeFile (File file,byte [ ] content )
    throws
        IOException { ByteBuffer sourceBuffer=ByteBuffer. wrap (content
        ); storeFileContent( file ,sourceBuffer
        );sourceBuffer.flip(
        ) ;return
    sourceBuffer

    ; } privatevoidprepare34DatabaseWithNodes
    (
        ) { GraphDatabaseService embeddedDatabase =newTestGraphDatabaseFactory(). newEmbeddedDatabase (storeDir
        )
        ;
            try { try ( Transaction transaction=embeddedDatabase.beginTx (
            )
                ) { for ( int i= 0 ; i< 10; i
                ++
                    ){embeddedDatabase. createNode(Label. label ( "label" + i)
                )
                ;}transaction.success(
            )
        ;
        }
        }
            finally{embeddedDatabase.shutdown(
        )
        ;}fileSystem. deleteFile (nativeLabelIndex
    )

    ; } privatevoidprepareEmpty23Database ( )
    throws
        IOException {newTestGraphDatabaseFactory(). newEmbeddedDatabase (storeDir).shutdown(
        );fileSystem. deleteFile (nativeLabelIndex
        );MetaDataStore. setRecord( pageCache,databaseLayout.metadataStore(
                ),Position. STORE_VERSION, versionStringToLong(StandardV2_3 . STORE_VERSION)
    )

    ; } privateByteBuffer readFileContent (File nativeLabelIndex , int length )
    throws
        IOException { try ( StoreChannel storeChannel=fileSystem. open( nativeLabelIndex,OpenMode . READ
        )
            ) { ByteBuffer readBuffer=ByteBuffer. allocate (length
            )
            ; //noinspection StatementWithEmptyBody while(readBuffer.hasRemaining ( )&&storeChannel. read ( readBuffer ) >
            0
                )
            {
            // read till the end of store channel}readBuffer.flip(
            ) ;return
        readBuffer
    ;

    } } privatevoid storeFileContent (File file , ByteBuffer sourceBuffer )
    throws
        IOException { try ( StoreChannel storeChannel=fileSystem. create ( file
        )
            ){storeChannel. writeAll (sourceBuffer
        )
    ;
}
