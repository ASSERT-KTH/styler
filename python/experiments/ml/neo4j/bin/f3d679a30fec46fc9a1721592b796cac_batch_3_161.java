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
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.io.
File ;importjava.io.
IOException ;importjava.nio.file.

OpenOption ;importorg.neo4j.graphdb.factory.
GraphDatabaseSettings ;importorg.neo4j.io.fs.
FileSystemAbstraction ;importorg.neo4j.io.layout.
DatabaseLayout ;importorg.neo4j.io.pagecache.
PageCache ;importorg.neo4j.io.pagecache.tracing.cursor.context.
EmptyVersionContextSupplier ;importorg.neo4j.kernel.configuration.
Config ;importorg.neo4j.kernel.configuration.
Settings ;importorg.neo4j.kernel.impl.store.format.
RecordFormats ;importorg.neo4j.kernel.impl.store.id.
DefaultIdGeneratorFactory ;importorg.neo4j.kernel.impl.store.id.
IdGeneratorFactory ;importorg.neo4j.logging.
LogProvider ;importorg.neo4j.logging.
NullLogProvider ;importorg.neo4j.test.rule.
PageCacheRule ;importorg.neo4j.test.rule.
TestDirectory ;importorg.neo4j.test.rule.fs.

EphemeralFileSystemRule ; importstaticjava.nio.file.StandardOpenOption.
DELETE_ON_CLOSE ; importstaticorg.hamcrest.CoreMatchers.
equalTo ; importstaticorg.junit.Assert.
assertEquals ; importstaticorg.junit.Assert.
assertThat ; importstaticorg.junit.Assert.
assertTrue ; importstaticorg.neo4j.kernel.impl.store.format.RecordFormatSelector.

selectForStoreOrConfig ; public
class
    StoreFactoryTest { private final PageCacheRule pageCacheRule =newPageCacheRule(
    ) ; private final EphemeralFileSystemRule fsRule =newEphemeralFileSystemRule(
    ) ; private final TestDirectory testDirectory=TestDirectory. testDirectory (fsRule

    );
    @ Rule public final RuleChain ruleChain=RuleChain. outerRule (fsRule). around (testDirectory). around (pageCacheRule

    ) ; privateNeoStores
    neoStores ; privateIdGeneratorFactory
    idGeneratorFactory ; privatePageCache

    pageCache;
    @ Before publicvoidsetUp
    (
        ) { FileSystemAbstraction fs=fsRule.get(
        ) ; pageCache=pageCacheRule. getPageCache (fs
        ) ; idGeneratorFactory =new DefaultIdGeneratorFactory (fs
    )

    ; } privateStoreFactory storeFactory (Config config, OpenOption ...
    openOptions
        ) { LogProvider logProvider=NullLogProvider.getInstance(
        ) ; DatabaseLayout databaseLayout=testDirectory.databaseLayout(
        ) ; RecordFormats recordFormats= selectForStoreOrConfig( config, databaseLayout, fsRule, pageCache ,logProvider
        ) ; returnnew StoreFactory( databaseLayout, config, idGeneratorFactory, pageCache,fsRule.get(
                ), recordFormats, logProvider,EmptyVersionContextSupplier. EMPTY ,openOptions
    )

    ;}
    @ After publicvoidtearDown
    (
        ) { if ( neoStores !=
        null
            ){neoStores.close(
        )
    ;

    }}
    @ Test publicvoidshouldHaveSameCreationTimeAndUpgradeTimeOnStartup
    (
        )
        { // When neoStores= storeFactory(Config.defaults ()). openAllNeoStores (true
        ) ; MetaDataStore metaDataStore=neoStores.getMetaDataStore(

        )
        ;// Then assertThat(metaDataStore.getUpgradeTime( ), equalTo(metaDataStore.getCreationTime ( ))
    )

    ;}
    @ Test publicvoidshouldHaveSameCommittedTransactionAndUpgradeTransactionOnStartup
    (
        )
        { // When neoStores= storeFactory(Config.defaults ()). openAllNeoStores (true
        ) ; MetaDataStore metaDataStore=neoStores.getMetaDataStore(

        )
        ;// Then assertEquals(metaDataStore.getUpgradeTransaction( ),metaDataStore.getLastCommittedTransaction ()
    )

    ;}
    @ Test publicvoidshouldHaveSpecificCountsTrackerForReadOnlyDatabase ( )
    throws
        IOException
        { // when StoreFactory readOnlyStoreFactory= storeFactory(Config. defaults(GraphDatabaseSettings. read_only,Settings . TRUE)
        ) ; neoStores=readOnlyStoreFactory. openAllNeoStores (true
        ) ; long lastClosedTransactionId=neoStores.getMetaDataStore().getLastClosedTransactionId(

        )
        ;// then assertEquals(- 1,neoStores.getCounts(). rotate ( lastClosedTransactionId)
    )

    ;}@ Test ( expected=StoreNotFoundException .
    class ) publicvoidshouldThrowWhenOpeningNonExistingNeoStores
    (
        ) { try ( NeoStores neoStores= storeFactory(Config.defaults ()).openAllNeoStores (
        )
            ){neoStores.getMetaDataStore(
        )
    ;

    }}
    @ Test publicvoidshouldDelegateDeletionOptionToStores
    (
        )
        { // GIVEN StoreFactory storeFactory= storeFactory(Config.defaults( ) ,DELETE_ON_CLOSE

        )
        ; // WHEN neoStores=storeFactory. openAllNeoStores (true
        ); assertTrue(fsRule.get(). listFiles(testDirectory.databaseDir ()) . length>=StoreType.values() .length

        )
        ;// THENneoStores.close(
        ); assertEquals( 0,fsRule.get(). listFiles(testDirectory.databaseDir ()) .length
    )

    ;}
    @ Test publicvoidshouldHandleStoreConsistingOfOneEmptyFile ( )
    throws
        Exception { StoreFactory storeFactory= storeFactory(Config.defaults ()
        ) ; FileSystemAbstraction fs=fsRule.get(
        );fs. create(testDirectory.databaseLayout(). file ( "neostore.nodestore.db.labels")
        );storeFactory. openAllNeoStores (true).close(
    )

    ;}
    @ Test publicvoidshouldCompleteInitializationOfStoresWithIncompleteHeaders ( )
    throws
        Exception { StoreFactory storeFactory= storeFactory(Config.defaults ()
        );storeFactory. openAllNeoStores (true).close(
        ) ; FileSystemAbstraction fs=fsRule.get(
        ) ; for ( File f:fs. listFiles(testDirectory.databaseDir ( )
        )
            ){fs. truncate( f ,0
        )
        ;}storeFactory. openAllNeoStores (true).close(
    )
;
