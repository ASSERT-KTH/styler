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
import org.neo4j.kernel.impl.store.MetaDataStore.Position;
import org.neo4j.kernel.impl.store.format.standard.StandardV2_3
; importorg.neo4j.kernel.impl.store.format.standard.StandardV3_2
; importorg.neo4j.kernel.impl.store.format.standard.StandardV3_4
; importorg.neo4j.kernel.impl.util.monitoring.ProgressReporter
; importorg.neo4j.kernel.lifecycle.Lifespan
; importorg.neo4j.kernel.monitoring.Monitors
; importorg.neo4j.storageengine.api.schema.LabelScanReader
; importorg.neo4j.test.TestGraphDatabaseFactory
; importorg.neo4j.test.rule.PageCacheRule
; importorg.neo4j.test.rule.TestDirectory
; importorg.neo4j.test.rule.fs.DefaultFileSystemRule

; import staticjava.lang.String.format
; import staticorg.junit.Assert.assertEquals
; import staticorg.junit.Assert.assertFalse
; import staticorg.junit.Assert.assertTrue
; import staticorg.mockito.Mockito.mock
; import staticorg.mockito.Mockito.times
; import staticorg.mockito.Mockito.verify
; import staticorg.neo4j.kernel.impl.store.MetaDataStore.versionStringToLong

; public class
NativeLabelScanStoreMigratorTest
    { private final TestDirectory testDirectory =TestDirectory.testDirectory()
    ; private final DefaultFileSystemRule fileSystemRule = newDefaultFileSystemRule()
    ; private final PageCacheRule pageCacheRule = newPageCacheRule()

    ;@
    Rule public RuleChain ruleChain =RuleChain.outerRule ( testDirectory).around ( fileSystemRule).around ( pageCacheRule)

    ; private FilestoreDir
    ; private FilenativeLabelIndex
    ; private DatabaseLayoutmigrationLayout
    ; private FileluceneLabelScanStore

    ; private final ProgressReporter progressReporter =mock (ProgressReporter. class)

    ; private FileSystemAbstractionfileSystem
    ; private PageCachepageCache
    ; private NativeLabelScanStoreMigratorindexMigrator
    ; private DatabaseLayoutdatabaseLayout

    ;@
    Before public voidsetUp( ) throws
    Exception
        { databaseLayout =testDirectory.databaseLayout()
        ; storeDir =databaseLayout.databaseDirectory()
        ; nativeLabelIndex =databaseLayout.labelScanStore()
        ; migrationLayout =testDirectory.databaseLayout ( "migrationDir")
        ; luceneLabelScanStore =testDirectory.databaseDir().toPath().resolve (Paths.get ("schema" ,"label" , "lucene" )).toFile()

        ; fileSystem =fileSystemRule.get()
        ; pageCache =pageCacheRule.getPageCache ( fileSystemRule)
        ; indexMigrator = newNativeLabelScanStoreMigrator (fileSystem ,pageCache ,Config.defaults( ))
        ;fileSystem.mkdirs ( luceneLabelScanStore)
    ;

    }@
    Test public voidskipMigrationIfNativeIndexExist( ) throws
    Exception
        { ByteBuffer sourceBuffer =writeFile (nativeLabelIndex , newbyte[]{1 ,2 ,3 })

        ;indexMigrator.migrate (databaseLayout ,migrationLayout ,progressReporter ,StandardV3_2.STORE_VERSION ,StandardV3_2. STORE_VERSION)
        ;indexMigrator.moveMigratedFiles (migrationLayout ,databaseLayout ,StandardV3_2.STORE_VERSION ,StandardV3_2. STORE_VERSION)

        ; ByteBuffer resultBuffer =readFileContent (nativeLabelIndex , 3)
        ;assertEquals (sourceBuffer , resultBuffer)
        ;assertTrue (fileSystem.fileExists ( luceneLabelScanStore ))
    ;

    }@Test ( expected =InvalidIdGeneratorException. class
    ) public voidfailMigrationWhenNodeIdFileIsBroken( ) throws
    Exception
        {prepareEmpty23Database()
        ; File nodeIdFile =databaseLayout.idNodeStore()
        ;writeFile (nodeIdFile , newbyte[]{1 ,2 ,3 })

        ;indexMigrator.migrate (databaseLayout ,migrationLayout ,progressReporter ,StandardV3_2.STORE_VERSION ,StandardV3_2. STORE_VERSION)
    ;

    }@
    Test public voidclearMigrationDirFromAnyLabelScanStoreBeforeMigrating( ) throws
    Exception
        {
        // givenprepareEmpty23Database()
        ;initializeNativeLabelScanStoreWithContent ( migrationLayout)
        ; File toBeDeleted =migrationLayout.labelScanStore()
        ;assertTrue (fileSystem.fileExists ( toBeDeleted ))

        ;
        // whenindexMigrator.migrate (databaseLayout ,migrationLayout ,progressReporter ,StandardV3_2.STORE_VERSION ,StandardV3_2. STORE_VERSION)

        ;
        // thenassertNoContentInNativeLabelScanStore ( migrationLayout)
    ;

    }@
    Test public voidluceneLabelIndexRemovedAfterSuccessfulMigration( ) throws
    IOException
        {prepareEmpty23Database()

        ;indexMigrator.migrate (databaseLayout ,migrationLayout ,progressReporter ,StandardV2_3.STORE_VERSION ,StandardV3_2. STORE_VERSION)
        ;indexMigrator.moveMigratedFiles (migrationLayout ,databaseLayout ,StandardV2_3.STORE_VERSION ,StandardV3_2. STORE_VERSION)

        ;assertFalse (fileSystem.fileExists ( luceneLabelScanStore ))
    ;

    }@
    Test public voidmoveCreatedNativeLabelIndexBackToStoreDirectory( ) throws
    IOException
        {prepareEmpty23Database()
        ;indexMigrator.migrate (databaseLayout ,migrationLayout ,progressReporter ,StandardV2_3.STORE_VERSION ,StandardV3_2. STORE_VERSION)
        ; File migrationNativeIndex =migrationLayout.labelScanStore()
        ; ByteBuffer migratedFileContent =writeFile (migrationNativeIndex , newbyte[]{5 ,4 ,3 ,2 ,1 })

        ;indexMigrator.moveMigratedFiles (migrationLayout ,databaseLayout ,StandardV2_3.STORE_VERSION ,StandardV3_2. STORE_VERSION)

        ; ByteBuffer movedNativeIndex =readFileContent (nativeLabelIndex , 5)
        ;assertEquals (migratedFileContent , movedNativeIndex)
    ;

    }@
    Test public voidpopulateNativeLabelScanIndexDuringMigration( ) throws
    IOException
        {prepare34DatabaseWithNodes()
        ;indexMigrator.migrate (databaseLayout ,migrationLayout ,progressReporter ,StandardV3_4.STORE_VERSION ,StandardV3_4. STORE_VERSION)
        ;indexMigrator.moveMigratedFiles (migrationLayout ,databaseLayout ,StandardV2_3.STORE_VERSION ,StandardV3_2. STORE_VERSION)

        ; try ( Lifespan lifespan = newLifespan( )
        )
            { NativeLabelScanStore labelScanStore =getNativeLabelScanStore (databaseLayout , true)
            ;lifespan.add ( labelScanStore)
            ; for ( int labelId =0 ; labelId <10 ;labelId ++
            )
                { try ( LabelScanReader labelScanReader =labelScanStore.newReader( )
                )
                    { int nodeCount =PrimitiveLongCollections.count (labelScanReader.nodesWithLabel ( labelId ))
                    ;assertEquals (format ("Expected to see only one node for label %d but was %d." ,labelId , nodeCount)
                            ,1 , nodeCount)
                ;
            }
        }
    }

    }@
    Test public voidreportProgressOnNativeIndexPopulation( ) throws
    IOException
        {prepare34DatabaseWithNodes()
        ;indexMigrator.migrate (databaseLayout ,migrationLayout ,progressReporter ,StandardV3_4.STORE_VERSION ,StandardV3_4. STORE_VERSION)
        ;indexMigrator.moveMigratedFiles (migrationLayout ,databaseLayout ,StandardV2_3.STORE_VERSION ,StandardV3_2. STORE_VERSION)

        ;verify ( progressReporter).start ( 10)
        ;verify (progressReporter ,times ( 10 )).progress ( 1)
    ;

    } private NativeLabelScanStoregetNativeLabelScanStore ( DatabaseLayoutdatabaseLayout , boolean readOnly
    )
        { return newNativeLabelScanStore (pageCache ,databaseLayout ,fileSystem ,FullStoreChangeStream.EMPTY ,readOnly , newMonitors()
                ,RecoveryCleanupWorkCollector.ignore( ))
    ;

    } private voidinitializeNativeLabelScanStoreWithContent ( DatabaseLayout databaseLayout ) throws
    IOException
        { try ( Lifespan lifespan = newLifespan( )
        )
            { NativeLabelScanStore nativeLabelScanStore =getNativeLabelScanStore (databaseLayout , false)
            ;lifespan.add ( nativeLabelScanStore)
            ; try ( LabelScanWriter labelScanWriter =nativeLabelScanStore.newWriter( )
            )
                {labelScanWriter.write (NodeLabelUpdate.labelChanges (1 , newlong[0] , newlong[]{1 } ))
            ;
            }nativeLabelScanStore.force (IOLimiter. UNLIMITED)
        ;
    }

    } private voidassertNoContentInNativeLabelScanStore ( DatabaseLayout databaseLayout
    )
        { try ( Lifespan lifespan = newLifespan( )
        )
            { NativeLabelScanStore nativeLabelScanStore =getNativeLabelScanStore (databaseLayout , true)
            ;lifespan.add ( nativeLabelScanStore)
            ; try ( LabelScanReader labelScanReader =nativeLabelScanStore.newReader( )
            )
                { int count =PrimitiveLongCollections.count (labelScanReader.nodesWithLabel ( 1 ))
                ;assertEquals (0 , count)
            ;
        }
    }

    } private ByteBufferwriteFile ( Filefile ,byte[ ] content ) throws
    IOException
        { ByteBuffer sourceBuffer =ByteBuffer.wrap ( content)
        ;storeFileContent (file , sourceBuffer)
        ;sourceBuffer.flip()
        ; returnsourceBuffer
    ;

    } private voidprepare34DatabaseWithNodes(
    )
        { GraphDatabaseService embeddedDatabase = newTestGraphDatabaseFactory().newEmbeddedDatabase ( storeDir)
        ;
        try
            { try ( Transaction transaction =embeddedDatabase.beginTx( )
            )
                { for ( int i =0 ; i <10 ;i ++
                )
                    {embeddedDatabase.createNode (Label.label ( "label" + i ))
                ;
                }transaction.success()
            ;
        }
        }
        finally
            {embeddedDatabase.shutdown()
        ;
        }fileSystem.deleteFile ( nativeLabelIndex)
    ;

    } private voidprepareEmpty23Database( ) throws
    IOException
        { newTestGraphDatabaseFactory().newEmbeddedDatabase ( storeDir).shutdown()
        ;fileSystem.deleteFile ( nativeLabelIndex)
        ;MetaDataStore.setRecord (pageCache ,databaseLayout.metadataStore()
                ,Position.STORE_VERSION ,versionStringToLong (StandardV2_3. STORE_VERSION ))
    ;

    } private ByteBufferreadFileContent ( FilenativeLabelIndex , int length ) throws
    IOException
        { try ( StoreChannel storeChannel =fileSystem.open (nativeLabelIndex ,OpenMode. READ )
        )
            { ByteBuffer readBuffer =ByteBuffer.allocate ( length)
            ;
            //noinspection StatementWithEmptyBody while (readBuffer.hasRemaining( ) &&storeChannel.read ( readBuffer ) > 0
            )
                {
            // read till the end of store channel
            }readBuffer.flip()
            ; returnreadBuffer
        ;
    }

    } private voidstoreFileContent ( Filefile , ByteBuffer sourceBuffer ) throws
    IOException
        { try ( StoreChannel storeChannel =fileSystem.create ( file )
        )
            {storeChannel.writeAll ( sourceBuffer)
        ;
    }
}
