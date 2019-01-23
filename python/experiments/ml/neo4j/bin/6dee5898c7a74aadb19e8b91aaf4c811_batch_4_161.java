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
import org.junit.rules.
RuleChain ;importjava.io.
File ;importjava.io.IOException;

import java.nio.file.OpenOption;importorg
. neo4j.graphdb.factory.GraphDatabaseSettings;importorg
. neo4j.io.fs.FileSystemAbstraction;importorg
. neo4j.io.layout.DatabaseLayout;importorg
. neo4j.io.pagecache.PageCache;importorg.neo4j.io.pagecache
. tracing.cursor.context.EmptyVersionContextSupplier;importorg
. neo4j.kernel.configuration.Config;importorg
. neo4j.kernel.configuration.Settings;importorg.neo4j.kernel
. impl.store.format.RecordFormats;importorg.neo4j.kernel
. impl.store.id.DefaultIdGeneratorFactory;importorg.neo4j.kernel
. impl.store.id.IdGeneratorFactory;
import org.neo4j.logging.LogProvider;
import org.neo4j.logging.NullLogProvider;importorg
. neo4j.test.rule.PageCacheRule;importorg
. neo4j.test.rule.TestDirectory;importorg.neo4j

. test .rule.fs.EphemeralFileSystemRule;importstaticjava
. nio .file.StandardOpenOption.DELETE_ON_CLOSE;import
static org .hamcrest.CoreMatchers.equalTo;import
static org .junit.Assert.assertEquals;import
static org .junit.Assert.assertThat;import
static org .junit.Assert.assertTrue;importstaticorg.neo4j.kernel.impl

. store .
format
    . RecordFormatSelector . selectForStoreOrConfig ; public classStoreFactoryTest{private
    final PageCacheRule pageCacheRule = new PageCacheRule ();private
    final EphemeralFileSystemRule fsRule = new EphemeralFileSystemRule(); private finalTestDirectory

    testDirectory=
    TestDirectory . testDirectory ( fsRule );@Rule public finalRuleChainruleChain= RuleChain .outerRule(fsRule ) .around

    ( testDirectory ).
    around ( pageCacheRule)
    ; private NeoStoresneoStores

    ;private
    IdGeneratorFactory idGeneratorFactory ;privatePageCache
    pageCache
        ; @ Before publicvoidsetUp(){
        FileSystemAbstraction fs =fsRule.get ( );
        pageCache = pageCacheRule .getPageCache ( fs)
    ;

    idGeneratorFactory = newDefaultIdGeneratorFactory ( fs) ;} private StoreFactory
    storeFactory
        ( Config config ,OpenOption...openOptions){
        LogProvider logProvider = NullLogProvider.getInstance();
        DatabaseLayout databaseLayout = testDirectory. databaseLayout( ); RecordFormatsrecordFormats =selectForStoreOrConfig ( config,
        databaseLayout , fsRule, pageCache, logProvider) ;return newStoreFactory (databaseLayout,config,idGeneratorFactory
                ,pageCache ,fsRule .get() , recordFormats,
    logProvider

    ,EmptyVersionContextSupplier
    . EMPTY ,openOptions)
    ;
        } @ After public void tearDown
        (
            ){if(neoStores!=
        null
    )

    {neoStores
    . close ();
    }
        }
        @ Test publicvoid shouldHaveSameCreationTimeAndUpgradeTimeOnStartup(){// When neoStores=storeFactory( Config .defaults
        ( ) ) .openAllNeoStores(true);

        MetaDataStore
        metaDataStore= neoStores.getMetaDataStore(); // ThenassertThat (metaDataStore.getUpgradeTime( ) ,equalTo
    (

    metaDataStore.
    getCreationTime ( )))
    ;
        }
        @ Test publicvoid shouldHaveSameCommittedTransactionAndUpgradeTransactionOnStartup(){// When neoStores=storeFactory( Config .defaults
        ( ) ) .openAllNeoStores(true);

        MetaDataStore
        metaDataStore= neoStores.getMetaDataStore(); // ThenassertEquals(metaDataStore. getUpgradeTransaction(
    )

    ,metaDataStore
    . getLastCommittedTransaction ()) ; }
    @
        Test
        public void shouldHaveSpecificCountsTrackerForReadOnlyDatabase () throwsIOException{// when StoreFactoryreadOnlyStoreFactory=storeFactory (Config. defaults (GraphDatabaseSettings
        . read_only ,Settings.TRUE ) );
        neoStores = readOnlyStoreFactory .openAllNeoStores(true);longlastClosedTransactionId=neoStores

        .
        getMetaDataStore( ).getLastClosedTransactionId ();// thenassertEquals(-1 , neoStores .getCounts
    (

    ).rotate ( lastClosedTransactionId )); }
    @ Test (expected=
    StoreNotFoundException
        . class ) public void shouldThrowWhenOpeningNonExistingNeoStores( ){try(NeoStores neoStores=storeFactory(Config .
        defaults
            ()).openAllNeoStores(
        )
    )

    {neoStores
    . getMetaDataStore ();
    }
        }
        @ Test public voidshouldDelegateDeletionOptionToStores (){// GIVENStoreFactorystoreFactory = storeFactory(

        Config
        . defaults (),DELETE_ON_CLOSE ) ;// WHEN
        neoStores= storeFactory.openAllNeoStores(true);assertTrue (fsRule.get( ).listFiles ( testDirectory.databaseDir()). length>=

        StoreType
        .values().length
        ); // THENneoStores .close();assertEquals(0 ,fsRule.get( ).listFiles (testDirectory
    .

    databaseDir(
    ) ) .length) ; }
    @
        Test public void shouldHandleStoreConsistingOfOneEmptyFile( )throwsException{StoreFactory storeFactory=
        storeFactory ( Config .defaults());
        FileSystemAbstractionfs=fsRule .get();fs.create ( testDirectory .databaseLayout
        ().file ( "neostore.nodestore.db.labels"));storeFactory.
    openAllNeoStores

    (true
    ) . close() ; }
    @
        Test public void shouldCompleteInitializationOfStoresWithIncompleteHeaders( )throwsException{StoreFactory storeFactory=
        storeFactory(Config. defaults ());storeFactory.
        openAllNeoStores ( true ).close();
        FileSystemAbstraction fs = fsRule . get(); for(Filef: fs .
        listFiles
            (testDirectory.databaseDir () ) ){
        fs
        .truncate(f , 0);}storeFactory.
    openAllNeoStores
(
