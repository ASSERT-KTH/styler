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
package org.neo4j.kernel.impl.api.integrationtest;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import org.neo4j.internal.kernel.api.Kernel;
import org.neo4j.internal.kernel.api.NodeCursor;
import org.neo4j.internal.kernel.api.Transaction;importorg.neo4j.
internal .kernel.api.exceptions.KernelException;importorg.neo4j.
internal .kernel.api.exceptions.TransactionFailureException;importorg.neo4j.
internal .kernel.api.security.LoginContext;import
org .neo4j.kernel.api.KernelTransaction;importorg.neo4j.
kernel .api.exceptions.schema.UniquePropertyValueValidationException;importorg.neo4j.
kernel .api.schema.constraints.ConstraintDescriptorFactory;import
org .neo4j.kernel.internal.GraphDatabaseAPI;import
org .neo4j.test.rule.ImpermanentDatabaseRule;import

org . neo4j.values.storable.Values;
import static org.junit.Assert.fail;importstaticorg.neo4j.
kernel . api.schema.SchemaDescriptorFactory.forLabel;importstaticorg.

neo4j.test .assertion. Assert
. assertException ;
@
    RunWith(
    Parameterized . class ) public class CompositeUniquenessConstraintValidationIT{@ClassRule

    publicstatic
    ImpermanentDatabaseRule dbRule = new ImpermanentDatabaseRule ( );@Rule
    public final TestName testName=
    new TestName (); privatefinal
    int numberOfProps ;privatefinal Object[

    ]aValues;privatefinal Object [ ] bValues
    ; @ Parameterized.Parameters( name="{index}: {0}"
    )
        public staticIterable<TestParams
                >parameterValues () { returnArrays .asList ( param (values
                (10 ), values( 10d )) ,param (values ( 10 ,20
                ), values( 10, 20 )) ,param (values ( 10L ,20L
                ), values( 10, 20 )) ,param (values ( 10 ,20
                ), values( 10L, 20L )) ,param (values ( 10 ,20
                ), values( 10.0, 20.0 )) ,param (values ( 10 ,20
                ), values( 10.0 ,20.0)),param (values( new int[ ]{ 1 ,2},"v2") ,values( new int []
                {1 ,2 }, "v2") ) ,param (values ("a" ,"b" , "c" ),
                values( "a", "b" ,"c" )) , param (values
                (285414114323346805L ), values( 285414114323346805L) ), param( values( 1, 2, 3, 4, 5 ,6 ,7 ,8 ,9 ,10 ), values( 1d, 2d, 3d, 4d, 5d , 6d
        ,7d
    ,

    8d , 9d ,10d ))) ;} privatestaticTestParams param (
    Object
        [ ] l, Object[ ] r)
    {

    return new TestParams(l ,r ); } private
    static
        Object []
    values

    ( Object ... values ) { returnvalues

    ; }private static final int
    label
        = 1;publicCompositeUniquenessConstraintValidationIT( TestParams params){assertparams.
        lhs . length==params.
        rhs . length;aValues=
        params . lhs;bValues=
    params

    . rhs ;numberOfProps
    = aValues .length
    ; } privateTransaction

    transaction;
    private GraphDatabaseAPI graphDatabaseAPI;protected Kernel kernel
    ;
        @ Before publicvoidsetup()throws
        Exception { graphDatabaseAPI=dbRule.getGraphDatabaseAPI(); kernel=graphDatabaseAPI .getDependencyResolver

        ().resolveDependency
        (Kernel.class);newTransaction( ); transaction. schemaWrite() . uniquePropertyConstraintCreate(
        forLabel(label,
    propertyIds

    ()
    ) ) ;commit( ) ;
    }
        @ After public void clean (
        )
            throwsException{if(transaction
        !=

        null){transaction
        .close();
                }newTransaction( );transaction. schemaWrite( ).constraintDrop ( ConstraintDescriptorFactory.
        uniqueForLabel(label,

        propertyIds ( ) ) ) ;commit() ;try(Transactiontx= kernel.beginTransaction (Transaction
              . Type . implicit,LoginContext.AUTH_DISABLED);NodeCursornode =
        tx
            .cursors().allocateNodeCursor() ) {tx
            . dataRead ().allNodesScan( node
            )
                ;while(node.next() ){tx.dataWrite ()
            .
            nodeDelete(node.nodeReference(
        )
    )

    ;}
    tx . success() ; }
    }
        @
        Test public void shouldAllowRemoveAndAddConflictingDataInOneTransaction_DeleteNode( )throws Exception {// given

        long
        node=createNodeWithLabelAndProps(
        label,aValues);// whennewTransaction( ) ;transaction
        . dataWrite ( ). nodeDelete (node
        ); longnewNode = createLabeledNode(

        label
        );setProperties(
    newNode

    ,aValues
    ) ; // then does not failcommit( ) ;
    }
        @
        Test public void shouldAllowRemoveAndAddConflictingDataInOneTransaction_RemoveLabel( )throws Exception {// given

        long
        node=createNodeWithLabelAndProps(
        label,aValues);// whennewTransaction( ); transaction .dataWrite
        ( ) . nodeRemoveLabel( node ,label
        ); longnewNode = createLabeledNode(

        label
        );setProperties(
    newNode

    ,aValues
    ) ; // then does not failcommit( ) ;
    }
        @
        Test public void shouldAllowRemoveAndAddConflictingDataInOneTransaction_RemoveProperty( )throws Exception {// given

        long
        node=createNodeWithLabelAndProps(
        label,aValues);// whennewTransaction( ); transaction .dataWrite
        ( ) . nodeRemoveProperty( node ,0
        ); longnewNode = createLabeledNode(

        label
        );setProperties(
    newNode

    ,aValues
    ) ; // then does not failcommit( ) ;
    }
        @
        Test public void shouldAllowRemoveAndAddConflictingDataInOneTransaction_ChangeProperty( )throws Exception {// given

        long
        node=createNodeWithLabelAndProps(
        label,aValues);// whennewTransaction( ); transaction. dataWrite(). nodeSetProperty ( node,
        0 , Values .of ( "Alive!")
        ); longnewNode = createLabeledNode(

        label
        );setProperties(
    newNode

    ,aValues
    ) ; // then does not failcommit( ) ;
    }
        @

        Test
        publicvoidshouldPreventConflictingDataInTx(
        ) throws Throwable {// Given // When newTransaction(
        ) ; long n1= createLabeledNode (label
        ); longn2 = createLabeledNode(
        label ) ; setProperties ( n1,
        aValues ) ; int lastPropertyOffset =numberOfProps - 1 ;for (int prop
        =
            0; prop< lastPropertyOffset; prop++){ setProperty( n2
        ,

        prop, aValues[ prop
        ]
            ); // still ok} assertException( ()->{ setProperty( n2

        ,lastPropertyOffset ,aValues[ lastPropertyOffset]

        )
        ;// boom!},
    UniquePropertyValueValidationException

    .class
    ) ; // Then should failcommit( ) ;
    }
        @
        Testpublic voidshouldEnforceOnSetProperty ()throws Exception{

        // given
        createNodeWithLabelAndProps(label,
        this . aValues ); // when newTransaction(

        ) ; long node = createLabeledNode(
        label ) ; int lastPropertyOffset =numberOfProps - 1 ;for (int prop
        =
            0; prop< lastPropertyOffset; prop++){ setProperty( node
        ,

        prop, aValues[ prop
        ]
            ); // still ok} assertException( ()->{ setProperty( node

        ,lastPropertyOffset ,aValues[ lastPropertyOffset]
        );// boom!}
    ,

    UniquePropertyValueValidationException.
    class ) ;commit( ) ;
    }
        @
        Testpublic voidshouldEnforceOnSetLabel ()throws Exception{

        // given
        createNodeWithLabelAndProps(label,
        this . aValues );// whennewTransaction
        () ;long node =createNode (

        ); setProperties( node
        ,
            bValues) ;// ok because no label is set assertException (( )

        ->{ addLabel(node ,label
        );// boom!}
    ,

    UniquePropertyValueValidationException.
    class ) ;commit( ) ;
    }
        @
        TestpublicvoidshouldEnforceOnSetPropertyInTx
        ( ) throws Exception{ // when newTransaction(
        ); longaNode = createLabeledNode(

        label ) ; setProperties( aNode ,aValues
        ) ; long nodeB = createLabeledNode(
        label ) ; int lastPropertyOffset =numberOfProps - 1 ;for (int prop
        =
            0; prop< lastPropertyOffset; prop++){ setProperty( nodeB
        ,

        prop, bValues[ prop
        ]
            ); // still ok} assertException( ()->{ setProperty( nodeB
        ,lastPropertyOffset ,bValues[ lastPropertyOffset]
        );// boom!}
    ,

    UniquePropertyValueValidationException.
    class ) ;commit( ) ;
    }
        @
        Testpublic voidshouldEnforceOnSetLabelInTx ( )throws

        Exception
        {// givencreateNodeWithLabelAndProps(
        label , aValues );// whennewTransaction
        () ;long nodeB =createNode

        () ;setProperties (
        nodeB
            ,bValues ); assertException (( )

        ->{ addLabel(nodeB ,label
        );// boom!}
    ,

    UniquePropertyValueValidationException . class); commit (
    )
        ; } private void newTransaction (
        )
            throwsKernelException { if(
        transaction
        != null ){fail( "tx already opened");}transaction= kernel.beginTransaction (KernelTransaction
    .

    Type . implicit,LoginContext . AUTH_DISABLED
    )
        ;}protectedvoidcommit(
        )
        throws
            TransactionFailureException{transaction.success(
        )
        ;
        try
            { transaction .close
        (
    )

    ; } finally{ transaction = null ; }
    }
        private long createLabeledNode (intlabelId)throwsKernelException{longnode=
        transaction.dataWrite().nodeCreate( ); transaction .dataWrite
        ( ).
    nodeAddLabel

    ( node ,labelId ) ;return node ; } private void
    addLabel
        (longnodeId,intlabelId)throws KernelException{ transaction .dataWrite
    (

    ) . nodeAddLabel( nodeId ,labelId ) ;} private void setProperty ( long
    nodeId
        ,intpropertyId,Objectvalue)throws KernelException{ transaction. dataWrite(). nodeSetProperty ( nodeId,
    propertyId

    , Values .of( value )
    )
        ; }privatelongcreateNode()throwsKernelException{return
    transaction

    . dataWrite () . nodeCreate( );} private long
            createNodeWithLabelAndProps (
    int
        labelId,Object[
        ] propertyValues ) throwsKernelException{newTransaction
        () ;long nodeId =createNode
        ( ) ; addLabel ( nodeId, labelId ) ;for (int prop
        =
            0; prop< numberOfProps; prop++){ setProperty(
        nodeId
        ,prop,propertyValues
        [ prop]
    )

    ; } commit( ) ;return nodeId;} private void
            setProperties (
    long
        nodeId , Object [ ] propertyValues) throws KernelException {for(int prop= 0
        ;
            prop< propertyValues. length; prop++){ setProperty(
        nodeId
    ,

    prop ,propertyValues[ prop])
    ;
        }}private int [ ] propertyIds(){int
        [ ] props = new int[ numberOfProps ] ;for (int i
        =
            0;i< numberOfProps ;i
        ++
        ) {props
    [

    i ] = i
    ;
        } return props;} staticclass
        TestParams // Only here to be able to produce readable output {privatefinal Object[

        ]lhs ;privatefinal Object[ ]rhs; TestParams (
        Object
            []lhs , Object[
            ]rhs) { this.
        lhs

        =lhs
        ; this .rhs=
        rhs
            ; }@Overridepublic StringtoString (){return String .format ("lhs=%s, rhs=%s",ArrayUtils . toString (lhs
        )
    ,
ArrayUtils
