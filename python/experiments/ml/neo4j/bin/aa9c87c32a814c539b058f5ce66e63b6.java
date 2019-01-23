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
package org.neo4j.kernel.impl.store;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
importorg.junit.Test
; importorg.junit.rules.RuleChain

; importjava.io.File
; importjava.io.IOException
; importjava.nio.file.OpenOption

; importorg.neo4j.graphdb.factory.GraphDatabaseSettings
; importorg.neo4j.io.fs.FileSystemAbstraction
; importorg.neo4j.io.layout.DatabaseLayout
; importorg.neo4j.io.pagecache.PageCache
; importorg.neo4j.io.pagecache.tracing.cursor.context.EmptyVersionContextSupplier
; importorg.neo4j.kernel.configuration.Config
; importorg.neo4j.kernel.configuration.Settings
; importorg.neo4j.kernel.impl.store.format.RecordFormats
; importorg.neo4j.kernel.impl.store.id.DefaultIdGeneratorFactory
; importorg.neo4j.kernel.impl.store.id.IdGeneratorFactory
; importorg.neo4j.logging.LogProvider
; importorg.neo4j.logging.NullLogProvider
; importorg.neo4j.test.rule.PageCacheRule
; importorg.neo4j.test.rule.TestDirectory
; importorg.neo4j.test.rule.fs.EphemeralFileSystemRule

; import staticjava.nio.file.StandardOpenOption.DELETE_ON_CLOSE
; import staticorg.hamcrest.CoreMatchers.equalTo
; import staticorg.junit.Assert.assertEquals
; import staticorg.junit.Assert.assertThat
; import staticorg.junit.Assert.assertTrue
; import staticorg.neo4j.kernel.impl.store.format.RecordFormatSelector.selectForStoreOrConfig

; public class
StoreFactoryTest
    { private final PageCacheRule pageCacheRule = newPageCacheRule()
    ; private final EphemeralFileSystemRule fsRule = newEphemeralFileSystemRule()
    ; private final TestDirectory testDirectory =TestDirectory.testDirectory ( fsRule)

    ;@
    Rule public final RuleChain ruleChain =RuleChain.outerRule ( fsRule).around ( testDirectory).around ( pageCacheRule)

    ; private NeoStoresneoStores
    ; private IdGeneratorFactoryidGeneratorFactory
    ; private PageCachepageCache

    ;@
    Before public voidsetUp(
    )
        { FileSystemAbstraction fs =fsRule.get()
        ; pageCache =pageCacheRule.getPageCache ( fs)
        ; idGeneratorFactory = newDefaultIdGeneratorFactory ( fs)
    ;

    } private StoreFactorystoreFactory ( Configconfig ,OpenOption ... openOptions
    )
        { LogProvider logProvider =NullLogProvider.getInstance()
        ; DatabaseLayout databaseLayout =testDirectory.databaseLayout()
        ; RecordFormats recordFormats =selectForStoreOrConfig (config ,databaseLayout ,fsRule ,pageCache , logProvider)
        ; return newStoreFactory (databaseLayout ,config ,idGeneratorFactory ,pageCache ,fsRule.get()
                ,recordFormats ,logProvider ,EmptyVersionContextSupplier.EMPTY , openOptions)
    ;

    }@
    After public voidtearDown(
    )
        { if ( neoStores != null
        )
            {neoStores.close()
        ;
    }

    }@
    Test public voidshouldHaveSameCreationTimeAndUpgradeTimeOnStartup(
    )
        {
        // When neoStores =storeFactory (Config.defaults( )).openAllNeoStores ( true)
        ; MetaDataStore metaDataStore =neoStores.getMetaDataStore()

        ;
        // ThenassertThat (metaDataStore.getUpgradeTime() ,equalTo (metaDataStore.getCreationTime( ) ))
    ;

    }@
    Test public voidshouldHaveSameCommittedTransactionAndUpgradeTransactionOnStartup(
    )
        {
        // When neoStores =storeFactory (Config.defaults( )).openAllNeoStores ( true)
        ; MetaDataStore metaDataStore =neoStores.getMetaDataStore()

        ;
        // ThenassertEquals (metaDataStore.getUpgradeTransaction() ,metaDataStore.getLastCommittedTransaction( ))
    ;

    }@
    Test public voidshouldHaveSpecificCountsTrackerForReadOnlyDatabase( ) throws
    IOException
        {
        // when StoreFactory readOnlyStoreFactory =storeFactory (Config.defaults (GraphDatabaseSettings.read_only ,Settings. TRUE ))
        ; neoStores =readOnlyStoreFactory.openAllNeoStores ( true)
        ; long lastClosedTransactionId =neoStores.getMetaDataStore().getLastClosedTransactionId()

        ;
        // thenassertEquals (-1 ,neoStores.getCounts().rotate ( lastClosedTransactionId ))
    ;

    }@Test ( expected =StoreNotFoundException. class
    ) public voidshouldThrowWhenOpeningNonExistingNeoStores(
    )
        { try ( NeoStores neoStores =storeFactory (Config.defaults( )).openAllNeoStores( )
        )
            {neoStores.getMetaDataStore()
        ;
    }

    }@
    Test public voidshouldDelegateDeletionOptionToStores(
    )
        {
        // GIVEN StoreFactory storeFactory =storeFactory (Config.defaults() , DELETE_ON_CLOSE)

        ;
        // WHEN neoStores =storeFactory.openAllNeoStores ( true)
        ;assertTrue (fsRule.get().listFiles (testDirectory.databaseDir( )). length >=StoreType.values(). length)

        ;
        // THENneoStores.close()
        ;assertEquals (0 ,fsRule.get().listFiles (testDirectory.databaseDir( )). length)
    ;

    }@
    Test public voidshouldHandleStoreConsistingOfOneEmptyFile( ) throws
    Exception
        { StoreFactory storeFactory =storeFactory (Config.defaults( ))
        ; FileSystemAbstraction fs =fsRule.get()
        ;fs.create (testDirectory.databaseLayout().file ( "neostore.nodestore.db.labels" ))
        ;storeFactory.openAllNeoStores ( true).close()
    ;

    }@
    Test public voidshouldCompleteInitializationOfStoresWithIncompleteHeaders( ) throws
    Exception
        { StoreFactory storeFactory =storeFactory (Config.defaults( ))
        ;storeFactory.openAllNeoStores ( true).close()
        ; FileSystemAbstraction fs =fsRule.get()
        ; for ( File f :fs.listFiles (testDirectory.databaseDir( ) )
        )
            {fs.truncate (f , 0)
        ;
        }storeFactory.openAllNeoStores ( true).close()
    ;
}
