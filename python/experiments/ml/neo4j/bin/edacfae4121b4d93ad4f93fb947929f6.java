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
package org.neo4j.kernel.impl.newapi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;

import org.neo4j.helpers.collection.Iterators;
import org.neo4j.internal.kernel.api.LabelSet;
import org.neo4j.internal.kernel.api.Write;
import org.neo4j.internal.kernel.api.exceptions.EntityNotFoundException;
import org.neo4j.internal.kernel.api.exceptions.InvalidTransactionTypeKernelException;
import org.neo4j.internal.kernel.api.exceptions.KernelException;
import org.neo4j.internal.kernel.api.exceptions.explicitindex.AutoIndexingKernelException;
import org.neo4j.internal.kernel.api.helpers.StubNodeCursor;
import org.neo4j.internal.kernel.api.helpers.TestRelationshipChain;
import org.neo4j.internal.kernel.api.schema.LabelSchemaDescriptor;
import org.neo4j.internal.kernel.api.schema.constraints.ConstraintDescriptor;
import org.neo4j.internal.kernel.api.security.SecurityContext;
import org.neo4j.kernel.api.explicitindex.AutoIndexOperations;
import org.neo4j.kernel.api.explicitindex.AutoIndexing;
import org.neo4j.kernel.api.schema.SchemaDescriptorFactory;
import org.neo4j.kernel.api.schema.constraints.ConstraintDescriptorFactory;
import org.neo4j.kernel.api.schema.constraints.RelExistenceConstraintDescriptor;
import org.neo4j.kernel.api.schema.constraints.UniquenessConstraintDescriptor;
import org.neo4j.kernel.api.schema.index.TestIndexDescriptorFactory;
import org.neo4j.kernel.api.txstate.TransactionState;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.impl.api.KernelTransactionImplementation;
import org.neo4j.kernel.impl.api.SchemaState;
import org.neo4j.kernel.impl.api.index.IndexingProvidersService;
import org.neo4j.kernel.impl.api.state.ConstraintIndexCreator;
import org.neo4j.kernel.impl.api.state.TxState;
import org.neo4j.kernel.impl.constraints.ConstraintSemantics;
import org.neo4j.kernel.impl.index.ExplicitIndexStore;
import org.neo4j.kernel.impl.locking.Locks;
import org.neo4j.kernel.impl.locking.ResourceTypes;
import org.neo4j.kernel.impl.locking.SimpleStatementLocks;
import org.neo4j.kernel.impl.proc.Procedures;
import org.neo4j.kernel.impl.util.Dependencies;
import org.neo4j.storageengine.api.StorageEngine;
import org.neo4j.storageengine.api.StorageReader;
import org.neo4j.storageengine.api.lock.LockTracer;
import org.neo4j.storageengine.api.schema.CapableIndexDescriptor;
import org.neo4j.values.storable.Value;
import org.neo4j.values.storable.Values;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.neo4j.collection.PrimitiveLongCollections.EMPTY_LONG_ARRAY;
import static org.neo4j.graphdb.factory.GraphDatabaseSettings.default_schema_provider;
import static org.neo4j.helpers.collection.Iterators.asList;
import static org.neo4j.kernel.api.schema.constraints.ConstraintDescriptorFactory.existsForRelType;
import static org.neo4j.kernel.api.schema.constraints.ConstraintDescriptorFactory.uniqueForLabel;
import static org.neo4j.kernel.api.schema.constraints.ConstraintDescriptorFactory.uniqueForSchema;
import static org.neo4j.kernel.impl.newapi.TwoPhaseNodeForRelationshipLockingTest.returnRelationships;
import static org.neo4j.test.MockedNeoStores.mockedTokenHolders;
import static org.neo4j.values.storable.Values.NO_VALUE;

public class OperationsLockTest
{
    private KernelTransactionImplementation transaction = mock( KernelTransactionImplementation.class );
    private Operations operations;
    private final Locks.Clientlocks=mock(Locks.Client . class)
    ; private finalWritewrite = mock (Write .class);private InOrderorder
    ; private DefaultNodeCursor nodeCursor ; privateDefaultPropertyCursor propertyCursor;private DefaultRelationshipScanCursorrelationshipCursor
    ; private TransactionStatetxState
    ; private AllStoreHolderallStoreHolder
    ; private finalLabelSchemaDescriptor
    descriptor = SchemaDescriptorFactory.
    forLabel ( 123,
    456 ) ;private
    StorageReader storageReader ; private ConstraintIndexCreator constraintIndexCreator;@Before publicvoid setUp ()
    throws InvalidTransactionTypeKernelException {txState
    = Mockito .spy

    (new
    TxState ( )); when (
    transaction
        . getReasonIfTerminated ()). thenReturn (Optional. empty(
        )) ;when(transaction. statementLocks()) .thenReturn(newSimpleStatementLocks (locks
        )) ;when(transaction. dataWrite()) . thenReturn( write ) ;when
        (transaction .isOpen()) .thenReturn(true ) ;when
        (transaction .lockTracer()) .thenReturn(LockTracer . NONE)
        ;when (transaction.txState( )).thenReturn (txState) ;when
        (transaction .securityContext()) .thenReturn(SecurityContext . AUTH_DISABLED)
        ;DefaultCursors cursors=mock(DefaultCursors .class); nodeCursor=mock (DefaultNodeCursor

        . class ) ;propertyCursor =mock( DefaultPropertyCursor.
        class ) ;relationshipCursor =mock( DefaultRelationshipScanCursor.
        class ) ;when (cursors. allocateNodeCursor(
        ) ) .thenReturn (nodeCursor) ;when
        (cursors .allocatePropertyCursor()) .thenReturn(propertyCursor ) ;when
        (cursors .allocateRelationshipScanCursor()) .thenReturn(relationshipCursor ) ;AutoIndexing
        autoindexing= mock(AutoIndexing.class );AutoIndexOperationsautoIndexOperations = mock(
        AutoIndexOperations . class ); when(autoindexing .nodes
        ( ) ) .thenReturn (autoIndexOperations) ;when
        (autoindexing .relationships()) .thenReturn(autoIndexOperations ) ;StorageEngine
        engine= mock(StorageEngine.class );storageReader= mock (StorageReader
        . class ) ;when (storageReader. nodeExists(
        anyLong ( )) ).thenReturn (true
        ); when(storageReader. constraintsGetForLabel(anyInt ( ))). thenReturn (Collections
        .emptyIterator ()); when(storageReader .constraintsGetAll()) .thenReturn(Collections. emptyIterator(
        )) ;when(engine. newReader()) .thenReturn(storageReader) ;allStoreHolder
        =new AllStoreHolder(storageReader,transaction ,cursors,mock ( ExplicitIndexStore.
        class ) , mock( Procedures.  class) ,mock (SchemaState
                .class) ,new Dependencies( )); constraintIndexCreator= mock( ConstraintIndexCreator.class ); operations =newOperations (allStoreHolder
        , mock (IndexTxStateUpdater .class) ,storageReader
        , transaction , newKernelToken (storageReader ,transaction ,mockedTokenHolders( )),cursors
                 ,autoindexing , constraintIndexCreator, mock( ConstraintSemantics. class), mock( IndexingProvidersService. class)
                ,Config .defaults ()) ;operations .initialize (); this. order=inOrder(locks ,txState
        ,storageReader);}@

        Afterpublicvoid tearDown () {operations .release ( );
    }

    @Test
    public void shouldAcquireEntityWriteLockCreatingRelationship()
    throws
        Exception{// whenlongrId=
    operations

    .relationshipCreate
    ( 1 ,2, 3 )
    ;
        // then
        order . verify (locks). acquireExclusive( LockTracer. NONE ,ResourceTypes

        .
        NODE,1) ; order.verify( locks).acquireExclusive (LockTracer.NONE , ResourceTypes.
        NODE,3) ; order.verify( txState).relationshipDoCreate (rId,2 , 1,
        3);} @ TestpublicvoidshouldAcquireNodeLocksWhenCreatingRelationshipInOrderOfAscendingId () throwsException {// GIVEN long lowId=
    3

    ;long
    highId = 5;int relationshipLabel =
    0
        ;
        { // WHEN operations .relationshipCreate
        ( lowId , relationshipLabel,
        highId ) ; // THENInOrder

        lockingOrder
            =
            inOrder(locks) ;lockingOrder .verify ( locks)

            .
            acquireExclusive ( LockTracer .NONE , ResourceTypes.
            NODE,lowId) ; lockingOrder.verify( locks).acquireExclusive (LockTracer.NONE , ResourceTypes.
            NODE,highId) ; lockingOrder.verifyNoMoreInteractions( );reset( locks);} { // WHENoperations
            .relationshipCreate(highId,relationshipLabel
            ,lowId ) ;// THEN
        InOrder

        lockingOrder
            =
            inOrder(locks) ;lockingOrder .verify ( locks)

            .
            acquireExclusive ( LockTracer .NONE , ResourceTypes.
            NODE,lowId) ; lockingOrder.verify( locks).acquireExclusive (LockTracer.NONE , ResourceTypes.
            NODE,highId) ; lockingOrder.verifyNoMoreInteractions( );}} @Testpublicvoid shouldAcquireNodeLocksWhenDeletingRelationshipInOrderOfAscendingId ()
            throwsException{// GIVENfinallong
        relationshipId
    =

    10;
    final long lowId=3 ; final
    long
        highId
        = 5 ; int relationshipLabel=
        0 ; { // and GIVEN setStoreRelationship(
        relationshipId , lowId , highId,
        relationshipLabel ) ; // WHENoperations

        .
            relationshipDelete
            (relationshipId ); // THENInOrder lockingOrder= inOrder (locks

            )
            ;lockingOrder.verify ( locks)

            .
            acquireExclusive ( LockTracer .NONE , ResourceTypes.
            NODE,lowId) ; lockingOrder.verify( locks).acquireExclusive (LockTracer.NONE , ResourceTypes.
            NODE,highId) ; lockingOrder.verify( locks).acquireExclusive (LockTracer.NONE , ResourceTypes.
            RELATIONSHIP,relationshipId) ; lockingOrder.verifyNoMoreInteractions( );reset( locks);} { // and GIVENsetStoreRelationship
            (relationshipId,highId,lowId
            ,relationshipLabel ) ;// WHEN
        operations

        .
            relationshipDelete
            (relationshipId ); // THENInOrder lockingOrder= inOrder (locks

            )
            ;lockingOrder.verify ( locks)

            .
            acquireExclusive ( LockTracer .NONE , ResourceTypes.
            NODE,lowId) ; lockingOrder.verify( locks).acquireExclusive (LockTracer.NONE , ResourceTypes.
            NODE,highId) ; lockingOrder.verify( locks).acquireExclusive (LockTracer.NONE , ResourceTypes.
            RELATIONSHIP,relationshipId) ; lockingOrder.verifyNoMoreInteractions( );}} @Testpublicvoid shouldAcquireEntityWriteLockBeforeAddingLabelToNode ()
            throwsException{// givenwhen(
        nodeCursor
    .

    next(
    ) ) .thenReturn( true )
    ;
        when
        (nodeCursor .labels()) .thenReturn(LabelSet . NONE)
        ;// when operations.nodeAddLabel(123L ,456); // thenorder. verify(

        locks
        ).acquireExclusive( LockTracer. NONE ,ResourceTypes

        .
        NODE,123L) ; order.verify( txState).nodeDoAddLabel (456,123L ) ;}
        @Testpublicvoid shouldNotAcquireEntityWriteLockBeforeAddingLabelToJustCreatedNode ()throwsException {// given when (nodeCursor
    .

    next(
    ) ) .thenReturn( true )
    ;
        when
        (nodeCursor .labels()) .thenReturn(LabelSet . NONE)
        ;when (transaction.hasTxStateWithChanges( )).thenReturn (true) ;// when
        txState. nodeDoCreate(123); operations.nodeAddLabel( 123 ,456

        )
        ;// thenverify( locks ,never
        ()). acquireExclusive( LockTracer .NONE

        ,
        ResourceTypes. NODE, 123); }@Testpublic voidshouldAcquireSchemaReadLockBeforeAddingLabelToNode() throwsException{// given when (nodeCursor
    .

    next(
    ) ) .thenReturn( true )
    ;
        when
        (nodeCursor .labels()) .thenReturn(LabelSet . NONE)
        ;// when intlabelId=456; operations.nodeAddLabel( 123,labelId );

        // then
        order . verify (locks
        ).acquireShared( LockTracer. NONE ,ResourceTypes

        .
        LABEL,labelId) ; order.verify( txState).nodeDoAddLabel (labelId,123 ) ;}
        @Testpublicvoid shouldAcquireEntityWriteLockBeforeSettingPropertyOnNode ()throwsException {// given when (nodeCursor
    .

    next(
    ) ) .thenReturn( true )
    ;
        when
        (nodeCursor .labels()) .thenReturn(LabelSet . NONE)
        ;int propertyKeyId=8;Value value=Values. of(9 );
        when ( propertyCursor .next
        ( ) ) .thenReturn(true ) ;when
        (propertyCursor .propertyKey()) .thenReturn(propertyKeyId ) ;when
        (propertyCursor .propertyValue()) .thenReturn(NO_VALUE ) ;// when
        operations. nodeSetProperty(123,propertyKeyId ,value); // then order.

        verify
        (locks). acquireExclusive( LockTracer. NONE ,ResourceTypes

        .
        NODE,123) ; order.verify( txState).nodeDoAddProperty (123,propertyKeyId , value)
        ;}@Test public voidshouldAcquireEntityWriteLockBeforeSettingPropertyOnRelationship() throwsException {// given when (relationshipCursor
    .

    next(
    ) ) .thenReturn( true )
    ;
        int
        propertyKeyId= 8;Valuevalue= Values.of( 9 );
        when ( propertyCursor .next
        ( ) ) .thenReturn(true ) ;when
        (propertyCursor .propertyKey()) .thenReturn(propertyKeyId ) ;when
        (propertyCursor .propertyValue()) .thenReturn(NO_VALUE ) ;// when
        operations. relationshipSetProperty(123,propertyKeyId ,value); // then order.

        verify
        (locks). acquireExclusive( LockTracer. NONE ,ResourceTypes

        .
        RELATIONSHIP,123) ; order.verify( txState).relationshipDoReplaceProperty (123,propertyKeyId , NO_VALUE,
        value);} @ TestpublicvoidshouldNotAcquireEntityWriteLockBeforeSettingPropertyOnJustCreatedNode () throwsException {// given when (nodeCursor
    .

    next(
    ) ) .thenReturn( true )
    ;
        when
        (nodeCursor .labels()) .thenReturn(LabelSet . NONE)
        ;when (transaction.hasTxStateWithChanges( )).thenReturn (true) ;txState
        .nodeDoCreate (123);int propertyKeyId=8; Value value=
        Values.of( 9 );
        // when operations . nodeSetProperty(
        123 , propertyKeyId ,value); // then verify(

        locks
        ,never() ). acquireExclusive( LockTracer .NONE

        ,
        ResourceTypes. NODE, 123); verify(txState) .nodeDoAddProperty(123 ,propertyKeyId,value ) ;}
        @Test public voidshouldNotAcquireEntityWriteLockBeforeSettingPropertyOnJustCreatedRelationship() throwsException {// given when (relationshipCursor
    .

    next(
    ) ) .thenReturn( true )
    ;
        when
        (transaction .hasTxStateWithChanges()) .thenReturn(true ) ;txState
        .relationshipDoCreate (123,42, 43,45) ; intpropertyKeyId
        =8;Value value= Values. of( 9 );
        // when operations . relationshipSetProperty(
        123 , propertyKeyId ,value); // then verify(

        locks
        ,never() ). acquireExclusive( LockTracer .NONE

        ,
        ResourceTypes. RELATIONSHIP, 123); verify(txState) .relationshipDoReplaceProperty(123 ,propertyKeyId,NO_VALUE , value)
        ;} @ TestpublicvoidshouldAcquireEntityWriteLockBeforeDeletingNode () throwsAutoIndexingKernelException {// GIVEN when (nodeCursor
    .

    next(
    ) ) .thenReturn(
            true )
    ;
        when
        (nodeCursor .labels()) .thenReturn(LabelSet . NONE)
        ;when (allStoreHolder.nodeExistsInStore( 123)). thenReturn(true );
        // WHENoperations .nodeDelete(123 ) ; //THENorder.verify ( locks)

        .
        acquireExclusive(LockTracer.  NONE ,ResourceTypes

        .
        NODE,123) ; order.verify( txState).nodeDoDelete (123); } @Test
        publicvoidshouldNotAcquireEntityWriteLockBeforeDeletingJustCreatedNode( ) throwsException{// THEN txState .nodeDoCreate
    (

    123)
    ; when (transaction. hasTxStateWithChanges (
    )
        )
        .thenReturn(true ) ;// WHEN
        operations. nodeDelete(123); //THENverify(locks , never(

        )
        ).acquireExclusive( LockTracer .NONE

        ,
        ResourceTypes. NODE, 123); verify(txState) .nodeDoDelete(123 );}@ Test publicvoid
        shouldAcquireSchemaReadLockBeforeGettingConstraintsByLabelAndProperty( ) {// WHENallStoreHolder. constraintsGetForSchema (descriptor
    )

    ;// THEN
    order . verify(locks
    )
        .
        acquireShared(LockTracer. NONE ,ResourceTypes

        .
        LABEL,descriptor. getLabelId ()); order.verify( storageReader).constraintsGetForSchema (descriptor);} @Test
        publicvoidshouldAcquireSchemaReadLockBeforeGettingConstraintsByLabel( ) {// WHENallStoreHolder. constraintsGetForLabel (42
    )

    ;// THEN
    order . verify(locks
    )
        .
        acquireShared(LockTracer. NONE ,ResourceTypes

        .
        LABEL,42) ; order.verify( storageReader).constraintsGetForLabel (42); } @Test
        publicvoidshouldAcquireSchemaReadLockBeforeCheckingExistenceConstraints( ) {// WHENallStoreHolder. constraintExists (ConstraintDescriptorFactory
    .

    uniqueForSchema(
    descriptor ) );// THEN
    order
        .
        verify(locks) .acquireShared(LockTracer . NONE ,ResourceTypes

        .
        LABEL,123) ; order.verify( storageReader).constraintExists (any() ) ;}
        @Testpublicvoid shouldAcquireSchemaReadLockLazilyBeforeGettingAllConstraints (){// given intlabelId= 1;
    int

    relTypeId=
    2 ; UniquenessConstraintDescriptoruniquenessConstraint=
    uniqueForLabel
        (
        labelId , 2 ,3
        , 3 ) ;RelExistenceConstraintDescriptor
        existenceConstraint = existsForRelType (relTypeId ,3 ,4 ,5 ) ;when
        ( storageReader . constraintsGetAll( )) .thenReturn (Iterators . iterator(
        uniquenessConstraint, existenceConstraint));// when Iterator
                <ConstraintDescriptor> result=allStoreHolder. constraintsGetAll( ) ; Iterators.

        count
        (result); // then assertThat (asList(result ),
        empty()) ; order.

        verify
        (storageReader ). constraintsGetAll () ;order. verify(
        locks).acquireShared ( LockTracer.NONE,ResourceTypes.
        LABEL,labelId) ; order.verify( locks).acquireShared (LockTracer.NONE , ResourceTypes.
        RELATIONSHIP_TYPE,relTypeId) ; }@Testpublic voidshouldAcquireSchemaWriteLockBeforeRemovingIndexRule() throwsException{// given CapableIndexDescriptor index=
    TestIndexDescriptorFactory

    .forLabel
    ( 0 ,0) . withId
    (
        0
        ) . withoutCapabilities  ();when (storageReader . indexGetForSchema(any( ) )).thenReturn(index
        ); // whenoperations.indexDrop (index) ;// thenorder.verify ( locks)

        .
        acquireExclusive(LockTracer. NONE ,ResourceTypes

        .
        LABEL,0) ; order.verify( txState).indexDoDrop (index); } @Test
        publicvoidshouldAcquireSchemaWriteLockBeforeCreatingUniquenessConstraint( ) throwsException{// given String defaultProvider=
    Config

    .defaults
    ( ) .get( default_schema_provider )
    ;
        when
        ( constraintIndexCreator . createUniquenessConstraintIndex(transaction,descriptor,defaultProvider) ) .thenReturn
        (42L );when( storageReader. constraintsGetForSchema( descriptor . schema()) ) .thenReturn
        (Collections .emptyIterator()  );// whenoperations. uniquePropertyConstraintCreate (descriptor); // thenorder.verify( locks)

        .
        acquireExclusive(LockTracer. NONE ,ResourceTypes

        .
        LABEL,descriptor. getLabelId ()); order.verify( txState).constraintDoAdd (ConstraintDescriptorFactory.uniqueForSchema( descriptor)
        ,42L); } @Testpublicvoid shouldAcquireSchemaWriteLockBeforeDroppingConstraint()throws Exception {// given UniquenessConstraintDescriptor constraint=
    uniqueForSchema

    (descriptor
    ) ; when(storageReader . constraintExists
    (
        constraint
        ) ) . thenReturn( true );
        // whenoperations .constraintDrop(constraint ) ; // thenorder.verify ( locks)

        .
        acquireExclusive(LockTracer. NONE ,ResourceTypes

        .
        LABEL,descriptor. getLabelId ()); order.verify( txState).constraintDoDrop (constraint);} @Test
        publicvoiddetachDeleteNodeWithoutRelationshipsExclusivelyLockNode( ) throwsKernelException{long nodeId =1L
    ;

    returnRelationships(
    transaction , false,new TestRelationshipChain (
    nodeId
        ) ) ; when(
        transaction. ambientNodeCursor( )) . thenReturn( new StubNodeCursor (false
        )) ;when(nodeCursor. next()) . thenReturn( true ) ;LabelSet
        labels= mock(LabelSet.class );when( labels .all
        ( ) ) .thenReturn (EMPTY_LONG_ARRAY) ;when
        (nodeCursor .labels()) .thenReturn(labels ) ;operations
        .nodeDetachDelete (nodeId);order .verify(locks ) .acquireExclusive

        (LockTracer.NONE , ResourceTypes.

        NODE,nodeId) ; order.verify( locks,never( )).releaseExclusive ( ResourceTypes.
        NODE,nodeId) ;order .verify( txState).nodeDoDelete (nodeId); } @Test
        publicvoiddetachDeleteNodeExclusivelyLockNodes( ) throwsKernelException{long nodeId =1L
    ;

    returnRelationships(
    transaction , false,new TestRelationshipChain (
    nodeId
        ) . outgoing (1
        ,2L ,42 ))
                ; when( transaction .ambientNodeCursor() ). thenReturn( new StubNodeCursor (false
        )) ;LabelSetlabels=mock (LabelSet.class ) ;when ( labels .all
        ( ) ) .thenReturn (EMPTY_LONG_ARRAY) ;when
        (nodeCursor .labels()) .thenReturn(labels ) ;when
        (nodeCursor .next()) .thenReturn(true ) ;operations
        .nodeDetachDelete (nodeId);order .verify(locks ) .acquireExclusive

        (LockTracer.NONE , ResourceTypes.

        NODE,nodeId, 2L );order.
                verify(locks, never()) .releaseExclusive ( ResourceTypes.
        NODE,nodeId) ;order .verify( locks,never( )).releaseExclusive ( ResourceTypes.
        NODE,2L) ;order .verify( txState).nodeDoDelete (nodeId); } @Test
        publicvoidshouldAcquiredSharedLabelLocksWhenDeletingNode( ) throwsAutoIndexingKernelException{// given long nodeId=
    1L

    ;long
    labelId1 = 1;long labelId2 =
    2
        ;
        when ( nodeCursor .next
        ( ) ) .thenReturn
        ( true ) ;LabelSet
        labels= mock(LabelSet.class );when( labels .all
        ( ) ) .thenReturn (newlong []
        {labelId1 ,labelId2}); when(nodeCursor. labels ()).thenReturn( labels) ;// when
        operations. nodeDelete(nodeId); // thenInOrderorder= inOrder (locks

        )
        ;order.verify ( locks)

        .
        acquireExclusive ( LockTracer .NONE , ResourceTypes.
        NODE,nodeId) ; order.verify( locks).acquireShared (LockTracer.NONE , ResourceTypes.
        LABEL,labelId1, labelId2 );order. verifyNoMoreInteractions(); }@Testpublic voidshouldAcquiredSharedLabelLocksWhenDetachDeletingNode ( )throws
        KernelException{// givenlongnodeId=
    1L

    ;long
    labelId1 = 1;long labelId2 =
    2
        ;
        returnRelationships ( transaction ,false
        , new TestRelationshipChain (nodeId
        ) ) ; when(

        transaction. ambientNodeCursor( )) . thenReturn( new StubNodeCursor (false
        )) ;when(nodeCursor. next()) . thenReturn( true ) ;LabelSet
        labels= mock(LabelSet.class );when( labels .all
        ( ) ) .thenReturn (newlong []
        {labelId1 ,labelId2}); when(nodeCursor. labels ()).thenReturn( labels) ;// when
        operations. nodeDetachDelete(nodeId); // thenInOrderorder= inOrder (locks

        )
        ;order.verify ( locks)

        .
        acquireExclusive ( LockTracer .NONE , ResourceTypes.
        NODE,nodeId) ; order.verify( locks).acquireShared (LockTracer.NONE , ResourceTypes.
        LABEL,labelId1, labelId2 );order. verifyNoMoreInteractions(); }@Testpublic voidshouldAcquiredSharedLabelLocksWhenRemovingNodeLabel ( )throws
        EntityNotFoundException{// givenlongnodeId=
    1L

    ;int
    labelId = 1;when ( nodeCursor
    .
        next
        ( ) ) .thenReturn
        ( true ) ;when
        (nodeCursor .hasLabel(labelId) ).thenReturn( true );
        // whenoperations .nodeRemoveLabel(nodeId , labelId );// thenInOrder order =inOrder

        (
        locks);order .verify ( locks)

        .
        acquireExclusive ( LockTracer .NONE , ResourceTypes.
        NODE,nodeId) ; order.verify( locks).acquireShared (LockTracer.NONE , ResourceTypes.
        LABEL,labelId) ; order.verifyNoMoreInteractions( );}@ TestpublicvoidshouldAcquiredSharedLabelLocksWhenRemovingNodeProperty ( )throws
        AutoIndexingKernelException,EntityNotFoundException{// givenlong
    nodeId

    =1L
    ; long labelId1=1 ; longlabelId2 =
    1
        ;
        int propertyKeyId = 5;
        when ( nodeCursor .next
        ( ) ) .thenReturn
        ( true ) ;LabelSet
        labels= mock(LabelSet.class );when( labels .all
        ( ) ) .thenReturn (newlong []
        {labelId1 ,labelId2}); when(nodeCursor. labels ()).thenReturn( labels) ;when
        (propertyCursor .next()) .thenReturn(true ) ;when
        (propertyCursor .propertyKey()) .thenReturn(propertyKeyId ) ;when
        (propertyCursor .propertyValue()) .thenReturn(Values . of(
        "abc") );// whenoperations. nodeRemoveProperty(nodeId, propertyKeyId);// then InOrder order =inOrder

        (
        locks);order .verify ( locks)

        .
        acquireExclusive ( LockTracer .NONE , ResourceTypes.
        NODE,nodeId) ; order.verify( locks).acquireShared (LockTracer.NONE , ResourceTypes.
        LABEL,labelId1, labelId2 );order. verifyNoMoreInteractions(); }privatevoidsetStoreRelationship (long relationshipId ,long
        sourceNode,longtargetNode,int
    relationshipLabel

    ) { when( relationshipCursor .next ( )) . thenReturn( true ) ;
    when
        (relationshipCursor .relationshipReference()) .thenReturn(relationshipId ) ;when
        (relationshipCursor .sourceNodeReference()) .thenReturn(sourceNode ) ;when
        (relationshipCursor .targetNodeReference()) .thenReturn(targetNode ) ;when
        (relationshipCursor .type()) .thenReturn(relationshipLabel ) ;}
        }