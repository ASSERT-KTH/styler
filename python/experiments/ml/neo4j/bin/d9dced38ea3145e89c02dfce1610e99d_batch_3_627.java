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

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.io.fs.DefaultFileSystemAbstraction;
import org.neo4j.io.fs.FileSystemAbstraction;
import org.neo4j.io.pagecache.PageCache;
import org.neo4j.io.pagecache.tracing.cursor.context.EmptyVersionContextSupplier;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.impl.MyRelTypes;
import org.neo4j.kernel.impl.storageengine.impl.recordstorage.RecordStorageEngine;
import org.neo4j.kernel.impl.store.id.DefaultIdGeneratorFactory;
import org.neo4j.kernel.impl.store.record.NodeRecord;
import org.neo4j.kernel.impl.store.record.Record;
import org.neo4j.kernel.impl.store.record.RelationshipGroupRecord;
import org.neo4j.kernel.impl.store.record.RelationshipRecord;
import org.neo4j.logging.NullLogProvider;
import org.neo4j.test.ImpermanentGraphDatabase;
import org.neo4j.test.rule.PageCacheRule;importorg

. neo4j .test.rule.TestDirectory;import
static java .lang.Integer.parseInt;import
static java .util.Arrays.asList;import
static org .hamcrest.Matchers.equalTo;import
static org .junit.Assert.assertEquals;import
static org .junit.Assert.assertNotEquals;import
static org .junit.Assert.assertThat;importstaticorg.neo4j.kernel
. impl .store.RecordStore.getRecord;importstaticorg.neo4j.kernel.impl
. store .record.RecordLoad.NORMAL;importstaticorg.neo4j

. test .
rule
    .PageCacheRule
    . config ; public class RelationshipGroupStoreTest{ @RulepublicPageCacheRulepageCacheRule= new PageCacheRule (config
    ()
    . withInconsistentReads ( false ));@Rulepublic
    TestDirectory testDir =TestDirectory
    . testDirectory ()
    ; private intdefaultThreshold

    ;private
    FileSystemAbstraction fs ;privateImpermanentGraphDatabase
    db
        ; @ Before publicvoidbefore(
        ) { fs= newDefaultFileSystemAbstraction();defaultThreshold= parseInt(
    GraphDatabaseSettings

    .dense_node_threshold
    . getDefaultValue ()) ; }
    @
        After public void after ( )
        throws
            IOException{if(db!=
        null
        ){db.shutdown(
    )

    ;}
    fs . close()
    ;
        }@ Test publicvoid
    createWithDefaultThreshold

    ()
    { createAndVerify (null)
    ;
        }@ Test public void createWithCustomThreshold(
    )

    {createAndVerify
    ( defaultThreshold *2)
    ;
        }@ Test publicvoid

        createDenseNodeWithLowThreshold
        ( ){
        newDb ( 2 ) ; // Create node with two relationshipsNodenode;try (
        Transaction
            tx = db.beginTx())
            {node=db .createNode();node .createRelationshipTo( db.
            createNode(), MyRelTypes.TEST);node .createRelationshipTo( db.
            createNode( ), MyRelTypes.TEST2); assertEquals(
            2, node. getDegree()) ;assertEquals( 1 ,node
            .getDegree (MyRelTypes .TEST)) ;assertEquals( 1 ,node
            .getDegree(MyRelTypes.TEST2
        )

        ) ; tx . success ();}try (
        Transaction
            tx=db. beginTx()){node .createRelationshipTo( db.
            createNode(),MyRelTypes.
        TEST

        );tx.success(
    )

    ; } db. shutdown ( )
    ;
        } private void newDb( intdenseNodeThreshold){ db= new ImpermanentGraphDatabase ( MapUtil .stringMap
        ( "dbms.relationship_grouping_threshold" ,""+denseNodeThreshold));fs =db. getDependencyResolver(
    )

    . resolveDependency (FileSystemAbstraction . class )
    ;
        } private void createAndVerify ( Integer customThreshold ) { intexpectedThreshold
        = customThreshold != null? customThreshold :defaultThreshold
        ; StoreFactory factory =factory(customThreshold ) ;NeoStores
        neoStores= factory. openAllNeoStores(true);assertEquals(expectedThreshold, neoStores.
        getRelationshipGroupStore().getStoreHeaderInt(

        )
        ) ; neoStores.close();
        // Next time we open it it should be the sameneoStores =factory .openAllNeoStores();assertEquals(expectedThreshold, neoStores.
        getRelationshipGroupStore().getStoreHeaderInt(

        )
        ) ; neoStores. close ()
        ; // Even if we open with a different config setting it should just ignore it factory=factory(999999)
        ;neoStores =factory .openAllNeoStores();assertEquals(expectedThreshold, neoStores.
        getRelationshipGroupStore().getStoreHeaderInt(
    )

    ) ; neoStores. close ( )
    ;
        } privateStoreFactory factory( IntegercustomThreshold){ return factory (customThreshold
    ,

    pageCacheRule . getPageCache( fs )) ; } private
    StoreFactory
        factory(IntegercustomThreshold ,PageCache pageCache ) { Map<String,String>
        customConfig = new HashMap < >
        (
            );if( customThreshold!=null){customConfig.put ( GraphDatabaseSettings . dense_node_threshold.
        name
        ( ) ,"" +customThreshold);}return newStoreFactory(testDir . databaseLayout( ) ,Config . defaults( customConfig)
                ,new DefaultIdGeneratorFactory(fs),pageCache ,fs, NullLogProvider.
    getInstance

    ()
    , EmptyVersionContextSupplier .EMPTY)
    ;
        }@ Test publicvoid

        makeSureRelationshipGroupsNextAndPrevGetsAssignedCorrectly ( ) { newDb (1);try (
        Transaction
            tx = db .beginTx()){
            Node node = db.createNode();
            Node node0 = db.createNode();
            Nodenode2=db .createNode (); node0.
            createRelationshipTo(node, MyRelTypes. TEST); node.

            createRelationshipTo ( node2 , MyRelTypes .TEST2);for (
            Relationship
                rel:node.getRelationships(
            )
            ){rel.delete(
            );}node.delete
        (

        );tx.success(
    )

    ;}
    db . shutdown()
    ;
        }@ Test publicvoid

        verifyRecordsForDenseNodeWithOneRelType ()
        { newDb(
        2 );
        Node node;
        Relationship rel1;
        Relationship rel2;
        Relationship rel3;
        Relationship rel4 ; Relationship rel5 ;Relationshiprel6;try (
        Transaction
            tx = db.beginTx())
            { node =db.createNode ();rel1=node .createRelationshipTo( db.
            createNode ( ),MyRelTypes.TEST);rel2 =db .createNode( ).
            createRelationshipTo ( node,MyRelTypes. TEST) ;rel3= node.
            createRelationshipTo ( node,MyRelTypes. TEST);rel4=node .createRelationshipTo( db.
            createNode ( ),MyRelTypes.TEST);rel5 =db .createNode( ).
            createRelationshipTo ( node,MyRelTypes. TEST) ;rel6= node.
            createRelationshipTo(node,MyRelTypes.
        TEST

        ) ; tx .success();}NeoStoresneoStores =db. getDependencyResolver().resolveDependency(
        RecordStorageEngine . class ).testAccessNeoStores();
        NodeStore nodeStore = neoStores. getNodeStore( );NodeRecordnodeRecord= getRecord(
        nodeStore , node .getId());
        longgroup=nodeRecord . getNextRel ();RecordStore<RelationshipGroupRecord
        > groupStore = neoStores. getRelationshipGroupStore( ) ;RelationshipGroupRecord
        groupRecord= getRecord(groupStore ,group);assertEquals (-
        1, groupRecord.getNext ());assertEquals (-
        1, groupRecord.getPrev()) ;assertRelationshipChain (neoStores.getRelationshipStore() ,node,groupRecord.getFirstOut (),rel1. getId(
        ), rel4.getId()) ;assertRelationshipChain (neoStores.getRelationshipStore() ,node,groupRecord.getFirstIn (),rel2. getId(
        ), rel5.getId()) ;assertRelationshipChain (neoStores.getRelationshipStore() ,node,groupRecord.getFirstLoop (),rel3. getId(
    )

    ,rel6
    . getId ())
    ;
        }@ Test publicvoid

        verifyRecordsForDenseNodeWithTwoRelTypes ()
        { newDb(
        2 );
        Node node;
        Relationship rel1;
        Relationship rel2;
        Relationship rel3;
        Relationship rel4 ; Relationship rel5 ;Relationshiprel6;try (
        Transaction
            tx = db.beginTx())
            { node =db.createNode ();rel1=node .createRelationshipTo( db.
            createNode ( ),MyRelTypes. TEST);rel2=node .createRelationshipTo( db.
            createNode ( ),MyRelTypes. TEST);rel3=node .createRelationshipTo( db.
            createNode ( ),MyRelTypes. TEST);rel4=node .createRelationshipTo( db.
            createNode ( ),MyRelTypes. TEST2);rel5=node .createRelationshipTo( db.
            createNode ( ),MyRelTypes. TEST2);rel6=node .createRelationshipTo( db.
            createNode(),MyRelTypes.
        TEST2

        ) ; tx .success();}NeoStoresneoStores =db. getDependencyResolver().resolveDependency(
        RecordStorageEngine . class ).testAccessNeoStores();
        NodeStore nodeStore = neoStores. getNodeStore( );NodeRecordnodeRecord= getRecord(
        nodeStore , node .getId());

        longgroup=nodeRecord . getNextRel ();RecordStore<RelationshipGroupRecord
        > groupStore = neoStores. getRelationshipGroupStore( ) ;RelationshipGroupRecord
        groupRecord= getRecord(groupStore,group) ;assertNotEquals (groupRecord
        .getNext (),-1) ;assertRelationshipChain (neoStores.getRelationshipStore() ,node,groupRecord.getFirstOut
                (),rel1.getId (),rel2. getId(

        ) , rel3 .getId() ); RelationshipGroupRecordotherGroupRecord=RecordStore. getRecord(
        groupStore, groupRecord.getNext ());assertEquals (-
        1, otherGroupRecord.getNext()) ;assertRelationshipChain (neoStores.getRelationshipStore() ,node,otherGroupRecord.getFirstOut
                (),rel4.getId (),rel5. getId(
    )

    ,rel6
    . getId ())
    ;
        }

        @Test public voidverifyGroupIsDeletedWhenNeeded

        ( ) { // TODO test on a lower level insteadnewDb(2);
        Transaction tx = db.beginTx();
        Node node = db.createNode( );Relationshiprel1=node .createRelationshipTo( db.
        createNode ( ) ,MyRelTypes.TEST );Relationshiprel2=node .createRelationshipTo( db.
        createNode ( ) ,MyRelTypes.TEST );Relationshiprel3=node .createRelationshipTo( db.
        createNode ( ) ,MyRelTypes.TEST );Relationshiprel4=node .createRelationshipTo( db.
        createNode ( ) ,MyRelTypes.TEST2 );Relationshiprel5=node .createRelationshipTo( db.
        createNode ( ) ,MyRelTypes.TEST2 );Relationshiprel6=node .createRelationshipTo( db.
        createNode(),MyRelTypes.
        TEST2);tx.success

        ( ) ; tx.close();NeoStoresneoStores =db. getDependencyResolver().resolveDependency(
        RecordStorageEngine . class ).testAccessNeoStores();
        NodeStore nodeStore = neoStores. getNodeStore( );NodeRecordnodeRecord= getRecord(
        nodeStore , node .getId());

        longgroup=nodeRecord . getNextRel ();RecordStore<RelationshipGroupRecord
        > groupStore = neoStores. getRelationshipGroupStore( ) ;RelationshipGroupRecord
        groupRecord= getRecord(groupStore,group) ;assertNotEquals (groupRecord
        . getNext ( ),-1 );RelationshipGroupRecordotherGroupRecord=groupStore .getRecord(groupRecord.getNext
                ( ),
        groupStore. newRecord() ,NORMAL);assertEquals (-

        1
    ,

    otherGroupRecord.
    getNext ( ));
    // TODO Delete all relationships of one type and see to that the correct group is deleted.
        } @ Test public voidcheckingIfRecordIsInUseMustHappenAfterConsistentRead ( ){
        AtomicBoolean nextReadIsInconsistent = newAtomicBoolean(false );
                PageCachepageCache=pageCacheRule.getPageCache ( fs ,config
        ( ) . withInconsistentReads( nextReadIsInconsistent) ) ;StoreFactory

        factory = factory ( null ,pageCache); try ( NeoStores
        neoStores
            =factory.openAllNeoStores ( true )){RecordStore<RelationshipGroupRecord
            > relationshipGroupStore = neoStores .getRelationshipGroupStore ( );RelationshipGroupRecordrecord =new RelationshipGroupRecord( 1) .initialize (true ,2
                    ,3,4,5, 6,
            Record.NO_NEXT_RELATIONSHIP. intValue ()
            );relationshipGroupStore. updateRecord (record
            )
            ; nextReadIsInconsistent . set(true) ;// Now the following should not throw any RecordNotInUse exceptions RelationshipGroupRecordreadBack=relationshipGroupStore.getRecord ( 1,
            relationshipGroupStore. newRecord(),NORMAL) ;assertThat (readBack.toString( ) ,equalTo
        (
    record

    . toString () ) ); } }private void assertRelationshipChain( RelationshipStorerelationshipStore , Node
    node
        , long firstId ,long...chainedIds){
        long nodeId = node.getId( ); RelationshipRecordrecord=relationshipStore.getRecord ( firstId,
        relationshipStore.newRecord( ) , NORMAL );Set<Long>
        readChain=newHashSet < >(
        ) ; readChain .
        add
            ( firstId ) ;while(true) { long nextId
                    =record.getFirstNode( )
                    ==nodeId?record.getFirstNextRel
            ( ) : record .getSecondNextRel (
            )
                ;if
            (

            nextId==-1 ) {break
            ;}readChain. add( nextId) ; relationshipStore.
        getRecord

        (nextId,record , NORMAL ) ;}Set< Long> expectedChain = newHashSet
        < > ( asList ( firstId )
        )
            ;for(long id :chainedIds
        )
        {expectedChain .add ( id)
    ;
}
