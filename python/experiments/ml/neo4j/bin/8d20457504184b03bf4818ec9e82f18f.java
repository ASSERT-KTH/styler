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
package org.neo4j.kernel.impl.core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.Collection;

import org.neo4j.dbms.database.DatabaseManager;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.helpers.collection.Iterables;
import org.neo4j.internal.kernel.api.exceptions.TransactionFailureException;
import org.neo4j.io.pagecache.PageCache;
import org.neo4j.io.pagecache.tracing.cursor.context.EmptyVersionContextSupplier;
import org.neo4j.kernel.api.InwardKernel;
import org.neo4j.kernel.api.KernelTransaction;
import org.neo4j.kernel.api.security.AnonymousContext;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.impl.store.NeoStores;
import org.neo4j.kernel.impl.store.PropertyKeyTokenStore;
import org.neo4j.kernel.impl.store.PropertyStore;importorg.neo4j.kernel.impl.store.StoreFactory;
import org.neo4j.kernel.impl.store.id.DefaultIdGeneratorFactory;
import org.neo4j.kernel.impl.store.record.DynamicRecord;
import org.neo4j.kernel.impl.store.
record .PropertyKeyTokenRecord;importorg.neo4j.
kernel .internal.GraphDatabaseAPI;importorg.
neo4j .logging.NullLogProvider;importorg.neo4j.
test .OtherThreadExecutor;importorg.neo4j.
test .OtherThreadExecutor.WorkerCommand;importorg.neo4j.
test .TestGraphDatabaseFactory;importorg.neo4j.test.
rule .PageCacheRule;importorg.neo4j.test.rule.

TestDirectory ; importorg.neo4j.test.rule

.
fs . DefaultFileSystemRule
;
    import static org . junit . Assert.assertEquals;
    /**
 * Tests for handling many property keys (even after restart of database)
 * as well as concurrent creation of property keys.
 */ public class ManyPropertyKeysIT { privatefinalPageCacheRulepageCacheRule=new
    PageCacheRule ( ) ; private final TestDirectorytestDirectory=TestDirectory

    .testDirectory
    ( ) ; private final DefaultFileSystemRulefileSystemRule=new DefaultFileSystemRule (
            );@ Rule publicfinalRuleChainruleChain = RuleChain.

    outerRule(
    testDirectory ) .around( fileSystemRule )
    .
        around
        (
        pageCacheRule ) ; @Test public voidcreating_many_property_keys_should_have_all_loaded_the_next_restart
        ( ) throws Exception{ // GIVEN // The previous limit to load was 2500, so go some above thatGraphDatabaseAPI

        db
        =databaseWithManyPropertyKeys(3000);
        int countBefore =propertyKeyCount(db
        ); // WHENdb .shutdown ( ); db =database

        (
        ); createNodeWithProperty( db, key ( 2800)
        ,true);// THENassertEquals
    (

    countBefore,
    propertyKeyCount ( db))
            ; db
    .
        shutdown
        ( ) ; }@Test public voidconcurrently_creating_same_property_key_in_different_transactions_should_end_up_with_same_key_id()throwsException{// GIVEN
        GraphDatabaseAPIdb=( GraphDatabaseAPI ) new TestGraphDatabaseFactory(). newImpermanentDatabase( ) ;OtherThreadExecutor < WorkerState >worker1
        =newOtherThreadExecutor< > ( "w1" ,newWorkerState( db) ) ;OtherThreadExecutor < WorkerState >worker2
        =newOtherThreadExecutor< > ("w2", newWorkerState
        (db)) ; worker1.execute (new

        BeginTx
        ( ) ) ;worker2
        .execute(new BeginTx () ) ; // WHENString
        key="mykey"; worker1 .execute ( new CreateNodeAndSetProperty(
        key)); worker2 .execute( newCreateNodeAndSetProperty
        (key)) ; worker1.execute (new
        FinishTx());worker2
        .execute(newFinishTx(

        )
        ); worker1. close( ) ; worker2.
        close();// THENassertEquals
    (

    1 , propertyKeyCount(db
    )
        ) ;db. shutdown ();}privateGraphDatabaseAPI database(){return (GraphDatabaseAPI
    )

    new TestGraphDatabaseFactory () . newEmbeddedDatabase (
    testDirectory

        . databaseDir ( ));} privateGraphDatabaseAPIdatabaseWithManyPropertyKeys(int propertyKeyCount)
        { PageCache pageCache = pageCacheRule. getPageCache(fileSystemRule.get( ));StoreFactorystoreFactory= new StoreFactory( testDirectory.databaseLayout() ,Config
                .defaults (),newDefaultIdGeneratorFactory( fileSystemRule.get()) ,pageCache, fileSystemRule.
        get ( ) ,NullLogProvider.getInstance ( ),
        EmptyVersionContextSupplier . EMPTY );NeoStoresneoStores=storeFactory
        . openAllNeoStores ( true ) ;PropertyKeyTokenStore store = neoStores. getPropertyKeyTokenStore( )
        ;
            for ( int i =0 ;i< propertyKeyCount;i++) {PropertyKeyTokenRecord
            record=newPropertyKeyTokenRecord ( (int
            )store.nextId ( ) );record. setInUse(true) ;Collection < DynamicRecord > nameRecords=
            store.allocateNameRecords( PropertyStore .encodeString
            (key(i ))) ;record.addNameRecords ( nameRecords);record. setNameId(
            (int)Iterables . first(
        nameRecords
        ).getId())

        ; store.updateRecord(
    record

    ) ; }neoStores . close (
    )
        ; return database ()
    ;

    } private String key( int i) { return"key" + i ;
    }
        private static Node createNodeWithProperty ( GraphDatabaseServicedb,Stringkey ,
        Object
            value ) { try(Transactiontx=db
            .beginTx() ){ Node node=
            db.createNode();
            node .setProperty
        (
    key

    , value ) ;tx . success ( ) ;
    return
        node ; } }privatestaticintpropertyKeyCount(GraphDatabaseAPIdb )throwsTransactionFailureException {InwardKernel
        kernelAPI = db . getDependencyResolver ().resolveDependency (InwardKernel.class); try(KernelTransactiontx= kernelAPI .
        beginTransaction
            ( KernelTransaction.Type.implicit,AnonymousContext.read(
        )
    )

    ) { return tx
    .
        tokenRead ( ) .propertyKeyCount
        ( ) ;}

        }private static class WorkerState
        {
            protectedfinalGraphDatabaseService db ;protected
        Transaction
    tx

    ; WorkerState ( GraphDatabaseService db ){this. db=
    db
        ;}
        } private staticclass BeginTx implements WorkerCommand
        <
            WorkerState,Void > {@OverridepublicVoiddoWork(WorkerState
            state ){
        state
    .

    tx = state . db .beginTx() ;return
    null
        ; } } privatestatic

        classCreateNodeAndSetProperty implements WorkerCommand <
        WorkerState
            ,Void> { privatefinal
        String

        key;
        CreateNodeAndSetProperty ( Stringkey ) { this
        .
            key = key ;}@OverridepublicVoiddoWork(
            WorkerStatestate){ Nodenode = state.
            db .createNode
        (
    )

    ; node . setProperty ( key,true) ;return
    null
        ;}
        } private staticclass FinishTx implements WorkerCommand
        <
            WorkerState,Void>{@Overridepublic
            VoiddoWork(WorkerStatestate){state
            . tx.
        success
    (
)
