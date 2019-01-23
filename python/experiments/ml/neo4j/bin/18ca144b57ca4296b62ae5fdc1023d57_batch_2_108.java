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
package org.neo4j.kernel.api.impl.fulltext;

import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.eclipse.collections.api.iterator.MutableLongIterator;
import org.eclipse.collections.api.set.primitive.MutableLongSet;
import org.eclipse.collections.impl.set.mutable.primitive.LongHashSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import org.junit.rules.Timeout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.LongFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.neo4j.graphdb.Entity;
import org.neo4j.graphdb.ExecutionPlanDescription;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.QueryExecutionException;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.kernel.configuration.Settings;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.neo4j.scheduler.Group;
import org.neo4j.scheduler.JobScheduler;
import org.neo4j.test.ThreadTestUtils;
import org.neo4j.test.rule.CleanupRule;
import org.neo4j.test.rule.TestDirectory;
import org.neo4j.test.rule.VerboseTimeout;
import org.neo4j.test.rule.fs.DefaultFileSystemRule;
import org.neo4j.util.concurrent.BinaryLatch;
import org.neo4j.values.storable.RandomValues;
import org.neo4j.values.storable.Value;
import org.neo4j.values.storable.ValueGroup;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.eclipse.collections.impl.set.mutable.primitive.LongHashSet.newSetWith;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.neo4j.graphdb.DependencyResolver.SelectionStrategy.ONLY;

public class FulltextProceduresTest
{
    private static final String DB_INDEXES = "CALL db.indexes";
    private static final String DROP = "CALL db.index.fulltext.drop(\"%s\")";
    private static final String LIST_AVAILABLE_ANALYZERS = "CALL db.index.fulltext.listAvailableAnalyzers()";
    static final String QUERY_NODES = "CALL db.index.fulltext.queryNodes(\"%s\", \"%s\")";
    static final String QUERY_RELS = "CALL db.index.fulltext.queryRelationships(\"%s\", \"%s\")";
    static final String AWAIT_REFRESH = "CALL db.index.fulltext.awaitEventuallyConsistentIndexRefresh()";
    static final String NODE_CREATE = "CALL db.index.fulltext.createNodeIndex(\"%s\", %s, %s )";
    static final String RELATIONSHIP_CREATE = "CALL db.index.fulltext.createRelationshipIndex(\"%s\", %s, %s)";

    private static final String SCORE = "score";
    static final String NODE = "node";
    static final String RELATIONSHIP = "relationship";
    private static final String DESCARTES_MEDITATIONES = "/meditationes--rene-descartes--public-domain.txt";
    private static final Label LABEL = Label.label( "Label" );
    private static final RelationshipType REL = RelationshipType.withName( "REL" );

    private final Timeout timeout = VerboseTimeout.builder().withTimeout( 1, TimeUnit.HOURS ).build();
    private final DefaultFileSystemRule fs = new DefaultFileSystemRule();
    private final TestDirectory testDirectory = TestDirectory.testDirectory();
    private final ExpectedException expectedException = ExpectedException.none();
    private final CleanupRule cleanup = new CleanupRule();

    @Rule
    public final RuleChain rules = RuleChain.outerRule( timeout ).around( fs ).around( testDirectory ).around( expectedException ).around( cleanup );

    private GraphDatabaseAPI db;
    private GraphDatabaseBuilder builder;
    private static final String PROP = "prop";
    private static final String EVENTUALLY_CONSISTENT = ", {eventually_consistent: 'true'}";

    @Before
    public void before()
    {
        GraphDatabaseFactory factory = new GraphDatabaseFactory();
        builder = factory.newEmbeddedDatabaseBuilder( testDirectory.databaseDir() );
        builder.setConfig( GraphDatabaseSettings.store_internal_log_level, "DEBUG" );
    }

    @After
    public void tearDown()
    {
        if ( db != null )
        {
            db.shutdown();
        }
    }

    @Test
    public void createNodeFulltextIndex()
    {
        db = createDatabase();
        db.execute( format( NODE_CREATE, "test-index", array( "Label1", "Label2" ), array( "prop1", "prop2" ) ) ).close();
        Result result;
        Map<String,Object> row;
        try ( Transaction tx = db.beginTx() )
        {
            result = db.execute( DB_INDEXES );
            assertTrue( result.hasNext() );
            row = result.next();
            assertEquals( "INDEX ON NODE:Label1, Label2(prop1, prop2)", row.get( "description" ) );
            assertEquals( asList( "Label1", "Label2" ), row.get( "tokenNames" ) );
            assertEquals( asList( "prop1", "prop2" ), row.get( "properties" ) );
            assertEquals( "test-index", row.get( "indexName" ) );
            assertFalse( result.hasNext() );
            result.close();
            awaitIndexesOnline();
            result = db.execute( DB_INDEXES );
            assertTrue( result.hasNext() );
            assertEquals( "ONLINE", result.next().get( "state" ) );
            assertFalse( result.hasNext() );
            result.close();
            assertNotNull( db.schema().getIndexByName( "test-index" ) );
            tx.success();
        }
        db.shutdown();
        db = createDatabase();
        try ( Transaction tx = db.beginTx() )
        {
            result = db.execute( DB_INDEXES );
            assertTrue( result.hasNext() );
            row = result.next();
            assertEquals( "INDEX ON NODE:Label1, Label2(prop1, prop2)", row.get( "description" ) );
            assertEquals( "ONLINE", row.get( "state" ) );
            assertFalse( result.hasNext() );
            assertFalse( result.hasNext() );
            assertNotNull( db.schema().getIndexByName( "test-index" ) );
            tx.success();
        }
    }

    @Test
    public void createRelationshipFulltextIndex()
    {
        db = createDatabase();
        db.execute( format( RELATIONSHIP_CREATE, "test-index", array( "Reltype1", "Reltype2" ), array( "prop1", "prop2" ) ) ).close();
        Result result;
        Map<String,Object> row;
        try ( Transaction tx = db.beginTx() )
        {
            result = db.execute( DB_INDEXES );
            assertTrue( result.hasNext() );
            row = result.next();
            assertEquals( "INDEX ON RELATIONSHIP:Reltype1, Reltype2(prop1, prop2)", row.get( "description" ) );
            assertEquals( asList( "Reltype1", "Reltype2" ), row.get( "tokenNames" ) );
            assertEquals( asList( "prop1", "prop2" ), row.get( "properties" ) );
            assertEquals( "test-index", row.get( "indexName" ) );
            assertFalse( result.hasNext() );
            result.close();
            awaitIndexesOnline();
            result = db.execute( DB_INDEXES );
            assertTrue( result.hasNext() );
            assertEquals( "ONLINE", result.next().get( "state" ) );
            assertFalse( result.hasNext() );
            result.close();
            assertNotNull( db.schema().getIndexByName( "test-index" ) );
            tx.success();
        }
        db.shutdown();
        db = createDatabase();
        try ( Transaction tx = db.beginTx() )
        {
            result = db.execute( DB_INDEXES );
            assertTrue( result.hasNext() );
            row = result.next();
            assertEquals( "INDEX ON RELATIONSHIP:Reltype1, Reltype2(prop1, prop2)", row.get( "description" ) );
            assertEquals( "ONLINE", row.get( "state" ) );
            assertFalse( result.hasNext() );
            assertFalse( result.hasNext() );
            assertNotNull( db.schema().getIndexByName( "test-index" ) );
            tx.success();
        }
    }

    @Test
    public void dropIndex()
    {
        db = createDatabase();
        db.execute( format( NODE_CREATE, "node", array( "Label1", "Label2" ), array( "prop1", "prop2" ) ) ).close();
        db.execute( format( RELATIONSHIP_CREATE, "rel", array( "Reltype1", "Reltype2" ), array( "prop1", "prop2" ) ) ).close();
        Map<String,String> indexes = new HashMap<>();
        db.execute( "call db.indexes" ).forEachRemaining( m -> indexes.put( (String) m.get( "indexName" ), (String) m.get( "description" ) ) );

        db.execute( format( DROP, "node" ) );
        indexes.remove( "node" );
        Map<String,String> newIndexes = new HashMap<>();
        db.execute( "call db.indexes" ).forEachRemaining( m -> newIndexes.put( (String) m.get( "indexName" ), (String) m.get( "description" ) ) );
        assertEquals( indexes, newIndexes)

        ;db.execute (format (DROP , "rel" ))
        ;indexes.remove ( "rel")
        ;newIndexes.clear()
        ;db.execute ( "call db.indexes").forEachRemaining ( m ->newIndexes.put ((String )m.get ( "indexName") ,(String )m.get ( "description" ) ))
        ;assertEquals (indexes , newIndexes)
    ;

    }@
    Test public voidmustNotBeAbleToCreateTwoIndexesWithSameName(
    )
        { db =createDatabase()
        ;db.execute (format (NODE_CREATE ,"node" ,array ("Label1" , "Label2") ,array ("prop1" , "prop2" ) )).close()
        ;expectedException.expectMessage ( "already exists")
        ;db.execute (format (NODE_CREATE ,"node" ,array ("Label1" , "Label2") ,array ("prop3" , "prop4" ) )).close()
    ;

    }@
    Test public voidnodeIndexesMustHaveLabels(
    )
        { db =createDatabase()
        ;expectedException.expect (QueryExecutionException. class)
        ;db.execute (format (NODE_CREATE ,"nodeIndex" ,array() ,array ( PROP ) )).close()
    ;

    }@
    Test public voidrelationshipIndexesMustHaveRelationshipTypes(
    )
        { db =createDatabase()
        ;expectedException.expect (QueryExecutionException. class)
        ;db.execute (format (RELATIONSHIP_CREATE ,"relIndex" ,array() ,array ( PROP ) ))
    ;

    }@
    Test public voidnodeIndexesMustHaveProperties(
    )
        { db =createDatabase()
        ;expectedException.expect (QueryExecutionException. class)
        ;db.execute (format (NODE_CREATE ,"nodeIndex" ,array ( "Label") ,array( ) )).close()
    ;

    }@
    Test public voidrelationshipIndexesMustHaveProperties(
    )
        { db =createDatabase()
        ;expectedException.expect (QueryExecutionException. class)
        ;db.execute (format (RELATIONSHIP_CREATE ,"relIndex" ,array ( "RELTYPE") ,array( ) ))
    ;

    }@
    Test public voidcreatingIndexesWhichImpliesTokenCreateMustNotBlockForever(
    )
        { db =createDatabase()

        ; try ( Transaction ignore =db.beginTx( )
        )
            {
            // The property keys and labels we ask for do not exist, so those tokens will have to be allocated.
            // This test verifies that the locking required for the index modifications do not conflict with the
            // locking required for the token allocation.db.execute (format (NODE_CREATE ,"nodesA" ,array ( "SOME_LABEL") ,array ( "this" ) ))
            ;db.execute (format (RELATIONSHIP_CREATE ,"relsA" ,array ( "SOME_REL_TYPE") ,array ( "foo" ) ))
            ;db.execute (format (NODE_CREATE ,"nodesB" ,array ( "SOME_OTHER_LABEL") ,array ( "that" ) ))
            ;db.execute (format (RELATIONSHIP_CREATE ,"relsB" ,array ( "SOME_OTHER_REL_TYPE") ,array ( "bar" ) ))
        ;
    }

    }@
    Test public voidcreatingIndexWithSpecificAnalyzerMustUseThatAnalyzerForPopulationUpdatingAndQuerying(
    )
        { db =createDatabase()
        ; LongHashSet noResults = newLongHashSet()
        ; LongHashSet swedishNodes = newLongHashSet()
        ; LongHashSet englishNodes = newLongHashSet()
        ; LongHashSet swedishRels = newLongHashSet()
        ; LongHashSet englishRels = newLongHashSet()

        ; String labelledSwedishNodes ="labelledSwedishNodes"
        ; String typedSwedishRelationships ="typedSwedishRelationships"

        ; try ( Transaction tx =db.beginTx( )
        )
            {
            // Nodes and relationships picked up by index population. Node nodeA =db.createNode ( LABEL)
            ;nodeA.setProperty (PROP , "En apa och en tomte bodde i ett hus.")
            ;swedishNodes.add (nodeA.getId( ))
            ; Node nodeB =db.createNode ( LABEL)
            ;nodeB.setProperty (PROP , "Hello and hello again, in the end.")
            ;englishNodes.add (nodeB.getId( ))
            ; Relationship relA =nodeA.createRelationshipTo (nodeB , REL)
            ;relA.setProperty (PROP , "En apa och en tomte bodde i ett hus.")
            ;swedishRels.add (relA.getId( ))
            ; Relationship relB =nodeB.createRelationshipTo (nodeA , REL)
            ;relB.setProperty (PROP , "Hello and hello again, in the end.")
            ;englishRels.add (relB.getId( ))
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            { String lbl =array (LABEL.name( ))
            ; String rel =array (REL.name( ))
            ; String props =array ( PROP)
            ; String swedish = props + ", {analyzer: '" +FulltextAnalyzerTest. SWEDISH +"'}"
            ;db.execute (format (NODE_CREATE ,labelledSwedishNodes ,lbl , swedish )).close()
            ;db.execute (format (RELATIONSHIP_CREATE ,typedSwedishRelationships ,rel , swedish )).close()
            ;tx.success()
        ;
        }awaitIndexesOnline()
        ; try ( Transaction tx =db.beginTx( )
        )
            {
            // Nodes and relationships picked up by index updates. Node nodeC =db.createNode ( LABEL)
            ;nodeC.setProperty (PROP , "En apa och en tomte bodde i ett hus.")
            ;swedishNodes.add (nodeC.getId( ))
            ; Node nodeD =db.createNode ( LABEL)
            ;nodeD.setProperty (PROP , "Hello and hello again, in the end.")
            ;englishNodes.add (nodeD.getId( ))
            ; Relationship relC =nodeC.createRelationshipTo (nodeD , REL)
            ;relC.setProperty (PROP , "En apa och en tomte bodde i ett hus.")
            ;swedishRels.add (relC.getId( ))
            ; Relationship relD =nodeD.createRelationshipTo (nodeC , REL)
            ;relD.setProperty (PROP , "Hello and hello again, in the end.")
            ;englishRels.add (relD.getId( ))
            ;tx.success()
        ;
        } try ( Transaction ignore =db.beginTx( )
        )
            {assertQueryFindsIds (db ,true ,labelledSwedishNodes ,"and" , englishNodes) ;
            // english word
            // swedish stop word (ignored by swedish analyzer, and not among the english nodes)assertQueryFindsIds (db ,true ,labelledSwedishNodes ,"ett" , noResults)
            ;assertQueryFindsIds (db ,true ,labelledSwedishNodes ,"apa" , swedishNodes) ;

            // swedish wordassertQueryFindsIds (db ,false ,typedSwedishRelationships ,"and" , englishRels)
            ;assertQueryFindsIds (db ,false ,typedSwedishRelationships ,"ett" , noResults)
            ;assertQueryFindsIds (db ,false ,typedSwedishRelationships ,"apa" , swedishRels)
        ;
    }

    }@
    Test public voidqueryShouldFindDataAddedInLaterTransactions(
    )
        { db =createDatabase()
        ;db.execute (format (NODE_CREATE ,"node" ,array ("Label1" , "Label2") ,array ("prop1" , "prop2" ) )).close()
        ;db.execute (format (RELATIONSHIP_CREATE ,"rel" ,array ("Reltype1" , "Reltype2") ,array ("prop1" , "prop2" ) )).close()
        ;awaitIndexesOnline()
        ; longhorseId
        ; longhorseRelId
        ; try ( Transaction tx =db.beginTx( )
        )
            { Node zebra =db.createNode()
            ;zebra.setProperty ("prop1" , "zebra")
            ; Node horse =db.createNode (Label.label ( "Label1" ))
            ;horse.setProperty ("prop2" , "horse")
            ;horse.setProperty ("prop3" , "zebra")
            ; Relationship horseRel =zebra.createRelationshipTo (horse ,RelationshipType.withName ( "Reltype1" ))
            ;horseRel.setProperty ("prop1" , "horse")
            ; Relationship loop =horse.createRelationshipTo (horse ,RelationshipType.withName ( "loop" ))
            ;loop.setProperty ("prop2" , "zebra")

            ; horseId =horse.getId()
            ; horseRelId =horseRel.getId()
            ;tx.success()
        ;
        }assertQueryFindsIds (db ,true ,"node" ,"horse" ,newSetWith ( horseId ))
        ;assertQueryFindsIds (db ,true ,"node" ,"horse zebra" ,newSetWith ( horseId ))

        ;assertQueryFindsIds (db ,false ,"rel" ,"horse" ,newSetWith ( horseRelId ))
        ;assertQueryFindsIds (db ,false ,"rel" ,"horse zebra" ,newSetWith ( horseRelId ))
    ;

    }@
    Test public voidqueryShouldFindDataAddedInIndexPopulation(
    )
        {
        // when Nodenode1
        ; Nodenode2
        ; Relationshiprelationship
        ; db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            { node1 =db.createNode ( LABEL)
            ;node1.setProperty (PROP , "This is a integration test.")
            ; node2 =db.createNode ( LABEL)
            ;node2.setProperty ("otherprop" , "This is a related integration test")
            ; relationship =node1.createRelationshipTo (node2 , REL)
            ;relationship.setProperty (PROP , "They relate")
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            {db.execute (format (NODE_CREATE ,"node" ,array (LABEL.name( )) ,array (PROP , "otherprop" ) ))
            ;db.execute (format (RELATIONSHIP_CREATE ,"rel" ,array (REL.name( )) ,array ( PROP ) ))
            ;tx.success()
        ;
        }awaitIndexesOnline()

        ;
        // thenassertQueryFindsIds (db ,true ,"node" ,"integration" ,node1.getId() ,node2.getId( ))
        ;assertQueryFindsIds (db ,true ,"node" ,"test" ,node1.getId() ,node2.getId( ))
        ;assertQueryFindsIds (db ,true ,"node" ,"related" ,newSetWith (node2.getId( ) ))
        ;assertQueryFindsIds (db ,false ,"rel" ,"relate" ,newSetWith (relationship.getId( ) ))
    ;

    }@
    Test public voidupdatesToEventuallyConsistentIndexMustEventuallyBecomeVisible(
    )
        { String value ="bla bla"
        ; db =createDatabase()

        ; try ( Transaction tx =db.beginTx( )
        )
            {db.execute (format (NODE_CREATE ,"node" ,array (LABEL.name( )) ,array ( PROP ) + EVENTUALLY_CONSISTENT ))
            ;db.execute (format (RELATIONSHIP_CREATE ,"rel" ,array (REL.name( )) ,array ( PROP ) + EVENTUALLY_CONSISTENT ))
            ;tx.success()
        ;

        } int entityCount =200
        ; LongHashSet nodeIds = newLongHashSet()
        ; LongHashSet relIds = newLongHashSet()
        ; try ( Transaction tx =db.beginTx( )
        )
            { for ( int i =0 ; i <entityCount ;i ++
            )
                { Node node =db.createNode ( LABEL)
                ;node.setProperty (PROP , value)
                ; Relationship rel =node.createRelationshipTo (node , REL)
                ;rel.setProperty (PROP , value)
                ;nodeIds.add (node.getId( ))
                ;relIds.add (rel.getId( ))
            ;
            }tx.success()
        ;

        }
        // Assert that we can observe our updates within 20 seconds from now. We have, after all, already committed the transaction. long deadline =System.currentTimeMillis( ) +TimeUnit.SECONDS.toMillis ( 20)
        ; boolean success =false
        ;
        do
            {
            try
                {assertQueryFindsIds (db ,true ,"node" ,"bla" , nodeIds)
                ;assertQueryFindsIds (db ,false ,"rel" ,"bla" , relIds)
                ; success =true
            ;
            } catch ( Throwable throwable
            )
                { if ( deadline <=System.currentTimeMillis( )
                )
                    {
                    // We're past the deadline. This test is not successful. throwthrowable
                ;
            }
        }
        } while (! success)
    ;

    }@
    Test public voidupdatesToEventuallyConsistentIndexMustBecomeVisibleAfterAwaitRefresh(
    )
        { String value ="bla bla"
        ; db =createDatabase()

        ; try ( Transaction tx =db.beginTx( )
        )
            {db.execute (format (NODE_CREATE ,"node" ,array (LABEL.name( )) ,array ( PROP ) + EVENTUALLY_CONSISTENT ))
            ;db.execute (format (RELATIONSHIP_CREATE ,"rel" ,array (REL.name( )) ,array ( PROP ) + EVENTUALLY_CONSISTENT ))
            ;tx.success()
        ;
        }awaitIndexesOnline()

        ; int entityCount =200
        ; LongHashSet nodeIds = newLongHashSet()
        ; LongHashSet relIds = newLongHashSet()
        ; try ( Transaction tx =db.beginTx( )
        )
            { for ( int i =0 ; i <entityCount ;i ++
            )
                { Node node =db.createNode ( LABEL)
                ;node.setProperty (PROP , value)
                ; Relationship rel =node.createRelationshipTo (node , REL)
                ;rel.setProperty (PROP , value)
                ;nodeIds.add (node.getId( ))
                ;relIds.add (rel.getId( ))
            ;
            }tx.success()
        ;

        }db.execute ( AWAIT_REFRESH).close()
        ;assertQueryFindsIds (db ,true ,"node" ,"bla" , nodeIds)
        ;assertQueryFindsIds (db ,false ,"rel" ,"bla" , relIds)
    ;

    }@
    Test public voideventuallyConsistentIndexMustPopulateWithExistingDataWhenCreated(
    )
        { String value ="bla bla"
        ; db =createDatabase()

        ; int entityCount =200
        ; LongHashSet nodeIds = newLongHashSet()
        ; LongHashSet relIds = newLongHashSet()
        ; try ( Transaction tx =db.beginTx( )
        )
            { for ( int i =0 ; i <entityCount ;i ++
            )
                { Node node =db.createNode ( LABEL)
                ;node.setProperty (PROP , value)
                ; Relationship rel =node.createRelationshipTo (node , REL)
                ;rel.setProperty (PROP , value)
                ;nodeIds.add (node.getId( ))
                ;relIds.add (rel.getId( ))
            ;
            }tx.success()
        ;

        } try ( Transaction tx =db.beginTx( )
        )
            {db.execute (format (NODE_CREATE ,"node" ,array (LABEL.name( )) ,array ( PROP ) + EVENTUALLY_CONSISTENT ))
            ;db.execute (format (RELATIONSHIP_CREATE ,"rel" ,array (REL.name( )) ,array ( PROP ) + EVENTUALLY_CONSISTENT ))
            ;tx.success()
        ;

        }awaitIndexesOnline()
        ;assertQueryFindsIds (db ,true ,"node" ,"bla" , nodeIds)
        ;assertQueryFindsIds (db ,false ,"rel" ,"bla" , relIds)
    ;

    }@
    Test public voidconcurrentPopulationAndUpdatesToAnEventuallyConsistentIndexMustEventuallyResultInCorrectIndexState( ) throws
    Exception
        { String oldValue ="red"
        ; String newValue ="green"
        ; db =createDatabase()

        ; int entityCount =200
        ; LongHashSet nodeIds = newLongHashSet()
        ; LongHashSet relIds = newLongHashSet()

        ;
        // First we create the nodes and relationships with the property value "red". try ( Transaction tx =db.beginTx( )
        )
            { for ( int i =0 ; i <entityCount ;i ++
            )
                { Node node =db.createNode ( LABEL)
                ;node.setProperty (PROP , oldValue)
                ; Relationship rel =node.createRelationshipTo (node , REL)
                ;rel.setProperty (PROP , oldValue)
                ;nodeIds.add (node.getId( ))
                ;relIds.add (rel.getId( ))
            ;
            }tx.success()
        ;

        }
        // Then, in two concurrent transactions, we create our indexes AND change all the property values to "green". CountDownLatch readyLatch = newCountDownLatch ( 2)
        ; BinaryLatch startLatch = newBinaryLatch()
        ; Runnable createIndexes =( )
        ->
            {readyLatch.countDown()
            ;startLatch.await()
            ; try ( Transaction tx =db.beginTx( )
            )
                {db.execute (format (NODE_CREATE ,"node" ,array (LABEL.name( )) ,array ( PROP ) + EVENTUALLY_CONSISTENT ))
                ;db.execute (format (RELATIONSHIP_CREATE ,"rel" ,array (REL.name( )) ,array ( PROP ) + EVENTUALLY_CONSISTENT ))
                ;tx.success()
            ;
        }}
        ; Runnable makeAllEntitiesGreen =( )
        ->
            { try ( Transaction tx =db.beginTx( )
            )
                {
                // Prepare our transaction state first.nodeIds.forEach ( nodeId ->db.getNodeById ( nodeId).setProperty (PROP , newValue ))
                ;relIds.forEach ( relId ->db.getRelationshipById ( relId).setProperty (PROP , newValue ))
                ;tx.success()
                ;
                // Okay, NOW we're ready to race!readyLatch.countDown()
                ;startLatch.await()
            ;
        }}
        ; ExecutorService executor =cleanup.add (Executors.newFixedThreadPool ( 2 ))
        ;Future<? > future1 =executor.submit ( createIndexes)
        ;Future<? > future2 =executor.submit ( makeAllEntitiesGreen)
        ;readyLatch.await()
        ;startLatch.release()

        ;
        // Finally, when everything has settled down, we should see that all of the nodes and relationships are indexed with the value "green".future1.get()
        ;future2.get()
        ;awaitIndexesOnline()
        ;db.execute ( AWAIT_REFRESH).close()
        ;assertQueryFindsIds (db ,true ,"node" ,newValue , nodeIds)
        ;assertQueryFindsIds (db ,false ,"rel" ,newValue , relIds)
    ;

    }@
    Test public voidfulltextIndexesMustBeEventuallyConsistentByDefaultWhenThisIsConfigured( ) throws
    InterruptedException
        {builder.setConfig (FulltextConfig.eventually_consistent ,Settings. TRUE)
        ; db =createDatabase()

        ; try ( Transaction tx =db.beginTx( )
        )
            {db.execute (format (NODE_CREATE ,"node" ,array (LABEL.name( )) ,array (PROP , "otherprop" ) ))
            ;db.execute (format (RELATIONSHIP_CREATE ,"rel" ,array (REL.name( )) ,array ( PROP ) ))
            ;tx.success()
        ;
        }awaitIndexesOnline()

        ;
        // Prevent index updates from being applied to eventually consistent indexes. BinaryLatch indexUpdateBlocker = newBinaryLatch()
        ;db.getDependencyResolver().resolveDependency (JobScheduler.class , ONLY).schedule (Group.INDEX_UPDATING ,indexUpdateBlocker:: await)

        ; LongHashSet nodeIds = newLongHashSet()
        ; longrelId
        ;
        try
            { try ( Transaction tx =db.beginTx( )
            )
                { Node node1 =db.createNode ( LABEL)
                ;node1.setProperty (PROP , "bla bla")
                ; Node node2 =db.createNode ( LABEL)
                ;node2.setProperty ("otherprop" , "bla bla")
                ; Relationship relationship =node1.createRelationshipTo (node2 , REL)
                ;relationship.setProperty (PROP , "bla bla")
                ;nodeIds.add (node1.getId( ))
                ;nodeIds.add (node2.getId( ))
                ; relId =relationship.getId()
                ;tx.success()
            ;

            }
            // Index updates are still blocked for eventually consistent indexes, so we should not find anything at this point.assertQueryFindsIds (db ,true ,"node" ,"bla" , newLongHashSet( ))
            ;assertQueryFindsIds (db ,false ,"rel" ,"bla" , newLongHashSet( ))
        ;
        }
        finally
            {
            // Uncork the eventually consistent fulltext index updates.Thread.sleep ( 10)
            ;indexUpdateBlocker.release()
        ;
        }
        // And wait for them to apply.db.execute ( AWAIT_REFRESH).close()

        ;
        // Now we should see our data.assertQueryFindsIds (db ,true ,"node" ,"bla" , nodeIds)
        ;assertQueryFindsIds (db ,false ,"rel" ,"bla" ,newSetWith ( relId ))
    ;

    }@
    Test public voidmustBeAbleToListAvailableAnalyzers(
    )
        { db =createDatabase()

        ; try ( Transaction tx =db.beginTx( )
        )
            {Set<String > analyzers = newHashSet<>()
            ; try (ResourceIterator<String > iterator =db.execute ( LIST_AVAILABLE_ANALYZERS).columnAs ( "analyzer" )
            )
                { while (iterator.hasNext( )
                )
                    {analyzers.add (iterator.next( ))
                ;
            }
            }assertThat (analyzers ,hasItem ( "english" ))
            ;assertThat (analyzers ,hasItem ( "swedish" ))
            ;assertThat (analyzers ,hasItem ( "standard" ))
            ;tx.success()
        ;
    }

    }@
    Test public voidqueryNodesMustThrowWhenQueryingRelationshipIndex(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleRelationshipIndex()
            ;tx.success()
        ;

        }awaitIndexesOnline()

        ; try ( Transaction tx =db.beginTx( )
        )
            {expectedException.expect (Exception. class)
            ;db.execute (format (QUERY_NODES ,"rels" , "bla bla" )).close()
            ;tx.success()
        ;
    }

    }@
    Test public voidqueryRelationshipsMustThrowWhenQueryingNodeIndex(
    )
        { db =createDatabase()

        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;tx.success()
        ;

        }awaitIndexesOnline()

        ; try ( Transaction tx =db.beginTx( )
        )
            {expectedException.expect (Exception. class)
            ;db.execute (format (QUERY_RELS ,"nodes" , "bla bla" )).close()
            ;tx.success()
        ;
    }

    }@
    Test public voidfulltextIndexMustIgnoreNonStringPropertiesForUpdate(
    )
        { db =createDatabase()

        ; Label label =LABEL
        ; try ( Transaction tx =db.beginTx( )
        )
            {db.execute (format (NODE_CREATE ,"nodes" ,array (label.name( )) ,array ( PROP ) )).close()
            ;createSimpleRelationshipIndex()
            ;tx.success()
        ;

        }awaitIndexesOnline()

        ;List<Value > values =generateRandomNonStringValues()

        ; try ( Transaction tx =db.beginTx( )
        )
            { for ( Value value : values
            )
                { Node node =db.createNode ( label)
                ; Object propertyValue =value.asObject()
                ;node.setProperty (PROP , propertyValue)
                ;node.createRelationshipTo (node , REL).setProperty (PROP , propertyValue)
            ;
            }tx.success()
        ;

        } for ( Value value : values
        )
            { String fulltextQuery =quoteValueForQuery ( value)
            ; String cypherQuery =format (QUERY_NODES ,"nodes" , fulltextQuery)
            ; Resultnodes
            ;
            try
                { nodes =db.execute ( cypherQuery)
            ;
            } catch ( QueryExecutionException e
            )
                { throw newAssertionError ( "Failed to execute query: " + cypherQuery + " based on value " +value.prettyPrint() , e)
            ;
            } if (nodes.hasNext( )
            )
                {fail ( "did not expect to find any nodes, but found at least: " +nodes.next( ))
            ;
            }nodes.close()
            ; Result relationships =db.execute (format (QUERY_RELS ,"rels" , fulltextQuery ))
            ; if (relationships.hasNext( )
            )
                {fail ( "did not expect to find any relationships, but found at least: " +relationships.next( ))
            ;
            }relationships.close()
        ;
    }

    }@
    Test public voidfulltextIndexMustIgnoreNonStringPropertiesForPopulation(
    )
        { db =createDatabase()

        ;List<Value > values =generateRandomNonStringValues()

        ; try ( Transaction tx =db.beginTx( )
        )
            { for ( Value value : values
            )
                { Node node =db.createNode ( LABEL)
                ; Object propertyValue =value.asObject()
                ;node.setProperty (PROP , propertyValue)
                ;node.createRelationshipTo (node , REL).setProperty (PROP , propertyValue)
            ;
            }tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;createSimpleRelationshipIndex()
            ;tx.success()
        ;

        }awaitIndexesOnline()

        ; for ( Value value : values
        )
            { String fulltextQuery =quoteValueForQuery ( value)
            ; String cypherQuery =format (QUERY_NODES ,"nodes" , fulltextQuery)
            ; Resultnodes
            ;
            try
                { nodes =db.execute ( cypherQuery)
            ;
            } catch ( QueryExecutionException e
            )
                { throw newAssertionError ( "Failed to execute query: " + cypherQuery + " based on value " +value.prettyPrint() , e)
            ;
            } if (nodes.hasNext( )
            )
                {fail ( "did not expect to find any nodes, but found at least: " +nodes.next( ))
            ;
            }nodes.close()
            ; Result relationships =db.execute (format (QUERY_RELS ,"rels" , fulltextQuery ))
            ; if (relationships.hasNext( )
            )
                {fail ( "did not expect to find any relationships, but found at least: " +relationships.next( ))
            ;
            }relationships.close()
        ;
    }

    }@
    Test public voidentitiesMustBeRemovedFromFulltextIndexWhenPropertyValuesChangeAwayFromText(
    )
        { db =createDatabase()

        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;tx.success()
        ;
        } longnodeId
        ; try ( Transaction tx =db.beginTx( )
        )
            { Node node =db.createNode ( LABEL)
            ; nodeId =node.getId()
            ;node.setProperty (PROP , "bla bla")
            ;tx.success()
        ;

        }awaitIndexesOnline()

        ; try ( Transaction tx =db.beginTx( )
        )
            { Node node =db.getNodeById ( nodeId)
            ;node.setProperty (PROP , 42)
            ;tx.success()
        ;

        } try ( Transaction tx =db.beginTx( )
        )
            { Result result =db.execute (format (QUERY_NODES ,"nodes" , "bla" ))
            ;assertFalse (result.hasNext( ))
            ;result.close()
            ;tx.success()
        ;
    }

    }@
    Test public voidentitiesMustBeAddedToFulltextIndexWhenPropertyValuesChangeToText(
    )
        { db =createDatabase()

        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;tx.success()
        ;
        } longnodeId
        ; try ( Transaction tx =db.beginTx( )
        )
            { Node node =db.createNode ( LABEL)
            ;node.setProperty (PROP , 42)
            ; nodeId =node.getId()
            ;tx.success()
        ;

        }awaitIndexesOnline()

        ; try ( Transaction tx =db.beginTx( )
        )
            { Node node =db.getNodeById ( nodeId)
            ;node.setProperty (PROP , "bla bla")
            ;tx.success()
        ;

        } try ( Transaction tx =db.beginTx( )
        )
            {assertQueryFindsIds (db ,true ,"nodes" ,"bla" , nodeId)
            ;tx.success()
        ;
    }

    }@
    Test public voidpropertiesMustBeRemovedFromFulltextIndexWhenTheirValueTypeChangesAwayFromText(
    )
        { db =createDatabase()

        ; try ( Transaction tx =db.beginTx( )
        )
            {db.execute (format (NODE_CREATE ,"nodes" ,array (LABEL.name( )) ,array ("prop1" , "prop2" ) )).close()
            ;tx.success()
        ;
        } longnodeId
        ; try ( Transaction tx =db.beginTx( )
        )
            { Node node =db.createNode ( LABEL)
            ; nodeId =node.getId()
            ;node.setProperty ("prop1" , "foo")
            ;node.setProperty ("prop2" , "bar")
            ;tx.success()
        ;

        }awaitIndexesOnline()

        ; try ( Transaction tx =db.beginTx( )
        )
            { Node node =db.getNodeById ( nodeId)
            ;node.setProperty ("prop2" , 42)
            ;tx.success()
        ;

        } try ( Transaction tx =db.beginTx( )
        )
            {assertQueryFindsIds (db ,true ,"nodes" ,"foo" , nodeId)
            ; Result result =db.execute (format (QUERY_NODES ,"nodes" , "bar" ))
            ;assertFalse (result.hasNext( ))
            ;result.close()
            ;tx.success()
        ;
    }

    }@
    Test public voidpropertiesMustBeAddedToFulltextIndexWhenTheirValueTypeChangesToText(
    )
        { db =createDatabase()

        ; try ( Transaction tx =db.beginTx( )
        )
            {db.execute (format (NODE_CREATE ,"nodes" ,array (LABEL.name( )) ,array ("prop1" , "prop2" ) )).close()
            ;tx.success()
        ;
        } longnodeId
        ; try ( Transaction tx =db.beginTx( )
        )
            { Node node =db.createNode ( LABEL)
            ; nodeId =node.getId()
            ;node.setProperty ("prop1" , "foo")
            ;node.setProperty ("prop2" , 42)
            ;tx.success()
        ;

        }awaitIndexesOnline()

        ; try ( Transaction tx =db.beginTx( )
        )
            { Node node =db.getNodeById ( nodeId)
            ;node.setProperty ("prop2" , "bar")
            ;tx.success()
        ;

        } try ( Transaction tx =db.beginTx( )
        )
            {assertQueryFindsIds (db ,true ,"nodes" ,"foo" , nodeId)
            ;assertQueryFindsIds (db ,true ,"nodes" ,"bar" , nodeId)
            ;tx.success()
        ;
    }

    }@
    Test public voidmustBeAbleToIndexHugeTextPropertiesInIndexUpdates( ) throws
    Exception
        { Stringmeditationes
        ; try ( BufferedReader reader = newBufferedReader
                ( newInputStreamReader (getClass().getResourceAsStream ( DESCARTES_MEDITATIONES) ,StandardCharsets. UTF_8 ) )
        )
            { meditationes =reader.lines().collect (Collectors.joining ( "\n" ))
        ;

        } db =createDatabase()

        ; Label label =Label.label ( "Book")
        ; try ( Transaction tx =db.beginTx( )
        )
            {db.execute (format (NODE_CREATE ,"books" ,array (label.name( )) ,array ("title" ,"author" , "contents" ) )).close()
            ;tx.success()
        ;
        } longnodeId
        ; try ( Transaction tx =db.beginTx( )
        )
            { Node node =db.createNode ( label)
            ; nodeId =node.getId()
            ;node.setProperty ("title" , "Meditationes de prima philosophia")
            ;node.setProperty ("author" , "René Descartes")
            ;node.setProperty ("contents" , meditationes)
            ;tx.success()
        ;

        }awaitIndexesOnline()

        ; try ( Transaction tx =db.beginTx( )
        )
            {assertQueryFindsIds (db ,true ,"books" ,"impellit scriptum offerendum" , nodeId)
            ;tx.success()
        ;
    }

    }@
    Test public voidmustBeAbleToIndexHugeTextPropertiesInIndexPopulation( ) throws
    Exception
        { Stringmeditationes
        ; try ( BufferedReader reader = newBufferedReader
                ( newInputStreamReader (getClass().getResourceAsStream ( DESCARTES_MEDITATIONES) ,StandardCharsets. UTF_8 ) )
        )
            { meditationes =reader.lines().collect (Collectors.joining ( "\n" ))
        ;

        } db =createDatabase()

        ; Label label =Label.label ( "Book")
        ; longnodeId
        ; try ( Transaction tx =db.beginTx( )
        )
            { Node node =db.createNode ( label)
            ; nodeId =node.getId()
            ;node.setProperty ("title" , "Meditationes de prima philosophia")
            ;node.setProperty ("author" , "René Descartes")
            ;node.setProperty ("contents" , meditationes)
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            {db.execute (format (NODE_CREATE ,"books" ,array (label.name( )) ,array ("title" ,"author" , "contents" ) )).close()
            ;tx.success()
        ;

        }awaitIndexesOnline()

        ; try ( Transaction tx =db.beginTx( )
        )
            {assertQueryFindsIds (db ,true ,"books" ,"impellit scriptum offerendum" , nodeId)
            ;tx.success()
        ;
    }

    }@
    Test public voidmustBeAbleToQuerySpecificPropertiesViaLuceneSyntax(
    )
        { db =createDatabase()
        ; Label book =Label.label ( "Book")
        ; try ( Transaction tx =db.beginTx( )
        )
            {db.execute (format (NODE_CREATE ,"books" ,array (book.name( )) ,array ("title" , "author" ) )).close()
            ;tx.success()
        ;

        } longbook2id
        ; try ( Transaction tx =db.beginTx( )
        )
            {db.schema().awaitIndexesOnline (1 ,TimeUnit. MINUTES)
            ; Node book1 =db.createNode ( book)
            ;book1.setProperty ("author" , "René Descartes")
            ;book1.setProperty ("title" , "Meditationes de prima philosophia")
            ; Node book2 =db.createNode ( book)
            ;book2.setProperty ("author" , "E. M. Curley")
            ;book2.setProperty ("title" , "Descartes Against the Skeptics")
            ; book2id =book2.getId()
            ;tx.success()
        ;

        } try ( Transaction tx =db.beginTx( )
        )
            { LongHashSet ids =newSetWith ( book2id)
            ;assertQueryFindsIds (db ,true ,"books" ,"title:Descartes" , ids)
            ;tx.success()
        ;
    }

    }@
    Test public voidmustIndexNodesByCorrectProperties(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {db.execute (format (NODE_CREATE ,"nodes" ,array (LABEL.name( )) ,array ("a" ,"b" ,"c" ,"d" ,"e" , "f" ) )).close()
            ;tx.success()
        ;
        } longnodeId
        ; try ( Transaction tx =db.beginTx( )
        )
            {awaitIndexesOnline()
            ; Node node =db.createNode ( LABEL)
            ;node.setProperty ("e" , "value")
            ; nodeId =node.getId()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            {assertQueryFindsIds (db ,true ,"nodes" ,"e:value" , nodeId)
            ;tx.success()
        ;
    }

    }@
    Test public voidqueryingIndexInPopulatingStateMustBlockUntilIndexIsOnline(
    )
        { db =createDatabase()
        ; long nodeCount =10_000
        ; try ( Transaction tx =db.beginTx( )
        )
            { for ( int i =0 ; i <nodeCount ;i ++
            )

                {db.createNode ( LABEL).setProperty (PROP , "value")
            ;
            }tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            { try ( Result result =db.execute (format (QUERY_NODES ,"nodes" , "value" ))
                  ;Stream<Map<String,Object> > stream =result.stream() ;
            )
                {assertThat (stream.count() ,is ( nodeCount ))
            ;
            }tx.success()
        ;
    }

    }@
    Test public voidqueryingIndexInPopulatingStateMustBlockUntilIndexIsOnlineEvenWhenTransactionHasState(
    )
        { db =createDatabase()
        ; long nodeCount =10_000
        ; try ( Transaction tx =db.beginTx( )
        )
            { for ( int i =0 ; i <nodeCount ;i ++
            )

                {db.createNode ( LABEL).setProperty (PROP , "value")
            ;
            }tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            {db.createNode ( LABEL).setProperty (PROP , "value")
            ; try ( Result result =db.execute (format (QUERY_NODES ,"nodes" , "value" ))
                  ;Stream<Map<String,Object> > stream =result.stream() ;
            )
                {assertThat (stream.count() ,is ( nodeCount + 1 ))
            ;
            }tx.success()
        ;
    }

    }@
    Test public voidqueryingIndexInTransactionItWasCreatedInMustThrow(
    )
        { db =createDatabase()
        ; try ( Transaction ignore =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;expectedException.expect (QueryExecutionException. class)
            ;db.execute (format (QUERY_NODES ,"nodes" , "value" )).close()
        ;
    }

    }@
    Test public voidqueryResultsMustNotIncludeNodesDeletedInOtherConcurrentlyCommittedTransactions( ) throws
    Exception
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;tx.success()
        ;
        } longnodeIdA
        ; longnodeIdB
        ; try ( Transaction tx =db.beginTx( )
        )
            {awaitIndexesOnline()
            ; Node nodeA =db.createNode ( LABEL)
            ;nodeA.setProperty (PROP , "value")
            ; nodeIdA =nodeA.getId()
            ; Node nodeB =db.createNode ( LABEL)
            ;nodeB.setProperty (PROP , "value")
            ; nodeIdB =nodeB.getId()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            { try ( Result result =db.execute (format (QUERY_NODES ,"nodes" , "value" ) )
            )
                {ThreadTestUtils.forkFuture (( )
                ->
                    { try ( Transaction forkedTx =db.beginTx( )
                    )
                        {db.getNodeById ( nodeIdA).delete()
                        ;db.getNodeById ( nodeIdB).delete()
                        ;forkedTx.success()
                    ;
                    } returnnull
                ; }).get()
                ;assertThat (result.stream().count() ,is ( 0L ))
            ;
            }tx.success()
        ;
    }

    }@
    Test public voidqueryResultsMustNotIncludeRelationshipsDeletedInOtherConcurrentlyCommittedTransactions( ) throws
    Exception
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleRelationshipIndex()
            ;tx.success()
        ;
        } longrelIdA
        ; longrelIdB
        ; try ( Transaction tx =db.beginTx( )
        )
            {awaitIndexesOnline()
            ; Node node =db.createNode()
            ; Relationship relA =node.createRelationshipTo (node , REL)
            ;relA.setProperty (PROP , "value")
            ; relIdA =relA.getId()
            ; Relationship relB =node.createRelationshipTo (node , REL)
            ;relB.setProperty (PROP , "value")
            ; relIdB =relB.getId()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            { try ( Result result =db.execute (format (QUERY_RELS ,"rels" , "value" ) )
            )
                {ThreadTestUtils.forkFuture (( )
                ->
                    { try ( Transaction forkedTx =db.beginTx( )
                    )
                        {db.getRelationshipById ( relIdA).delete()
                        ;db.getRelationshipById ( relIdB).delete()
                        ;forkedTx.success()
                    ;
                    } returnnull
                ; }).get()
                ;assertThat (result.stream().count() ,is ( 0L ))
            ;
            }tx.success()
        ;
    }

    }@
    Test public voidqueryResultsMustNotIncludeNodesDeletedInThisTransaction(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;tx.success()
        ;
        } longnodeIdA
        ; longnodeIdB
        ; try ( Transaction tx =db.beginTx( )
        )
            {awaitIndexesOnline()
            ; Node nodeA =db.createNode ( LABEL)
            ;nodeA.setProperty (PROP , "value")
            ; nodeIdA =nodeA.getId()
            ; Node nodeB =db.createNode ( LABEL)
            ;nodeB.setProperty (PROP , "value")
            ; nodeIdB =nodeB.getId()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            {db.getNodeById ( nodeIdA).delete()
            ;db.getNodeById ( nodeIdB).delete()
            ; try ( Result result =db.execute (format (QUERY_NODES ,"nodes" , "value" ) )
            )
                {assertThat (result.stream().count() ,is ( 0L ))
            ;
            }tx.success()
        ;
    }

    }@
    Test public voidqueryResultsMustNotIncludeRelationshipsDeletedInThisTransaction(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleRelationshipIndex()
            ;tx.success()
        ;
        } longrelIdA
        ; longrelIdB
        ; try ( Transaction tx =db.beginTx( )
        )
            {awaitIndexesOnline()
            ; Node node =db.createNode()
            ; Relationship relA =node.createRelationshipTo (node , REL)
            ;relA.setProperty (PROP , "value")
            ; relIdA =relA.getId()
            ; Relationship relB =node.createRelationshipTo (node , REL)
            ;relB.setProperty (PROP , "value")
            ; relIdB =relB.getId()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            {db.getRelationshipById ( relIdA).delete()
            ;db.getRelationshipById ( relIdB).delete()
            ; try ( Result result =db.execute (format (QUERY_RELS ,"rels" , "value" ) )
            )
                {assertThat (result.stream().count() ,is ( 0L ))
            ;
            }tx.success()
        ;
    }

    }@
    Test public voidqueryResultsMustIncludeNodesAddedInThisTransaction(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;tx.success()
        ;
        }awaitIndexesOnline()
        ; try ( Transaction tx =db.beginTx( )
        )
            { Node node =db.createNode ( LABEL)
            ;node.setProperty (PROP , "value")
            ;assertQueryFindsIds (db ,true ,"nodes" ,"value" ,newSetWith (node.getId( ) ))
            ;tx.success()
        ;
    }

    }@
    Test public voidqueryResultsMustIncludeRelationshipsAddedInThisTransaction(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleRelationshipIndex()
            ;tx.success()
        ;
        }awaitIndexesOnline()
        ; try ( Transaction tx =db.beginTx( )
        )
            { Node node =db.createNode()
            ; Relationship relationship =node.createRelationshipTo (node , REL)
            ;relationship.setProperty (PROP , "value")
            ;assertQueryFindsIds (db ,false ,"rels" ,"value" ,newSetWith (relationship.getId( ) ))
            ;tx.success()
        ;
    }

    }@
    Test public voidqueryResultsMustIncludeNodesWithPropertiesAddedToBeIndexed(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;tx.success()
        ;
        } longnodeId
        ; try ( Transaction tx =db.beginTx( )
        )
            {awaitIndexesOnline()
            ; nodeId =db.createNode ( LABEL).getId()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            {db.getNodeById ( nodeId).setProperty (PROP , "value")
            ;assertQueryFindsIds (db ,true ,"nodes" ,"prop:value" , nodeId)
            ;tx.success()
        ;
    }

    }@
    Test public voidqueryResultsMustIncludeRelationshipsWithPropertiesAddedToBeIndexed(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleRelationshipIndex()
            ;tx.success()
        ;
        } longrelId
        ; try ( Transaction tx =db.beginTx( )
        )
            {awaitIndexesOnline()
            ; Node node =db.createNode()
            ; Relationship rel =node.createRelationshipTo (node , REL)
            ; relId =rel.getId()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            { Relationship rel =db.getRelationshipById ( relId)
            ;rel.setProperty (PROP , "value")
            ;assertQueryFindsIds (db ,false ,"rels" ,"prop:value" , relId)
            ;tx.success()
        ;
    }

    }@
    Test public voidqueryResultsMustIncludeNodesWithLabelsModifedToBeIndexed(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;tx.success()
        ;
        } longnodeId
        ; try ( Transaction tx =db.beginTx( )
        )
            {awaitIndexesOnline()
            ; Node node =db.createNode()
            ;node.setProperty (PROP , "value")
            ; nodeId =node.getId()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            { Node node =db.getNodeById ( nodeId)
            ;node.addLabel ( LABEL)
            ;assertQueryFindsIds (db ,true ,"nodes" ,"value" , nodeId)
            ;tx.success()
        ;
    }

    }@
    Test public voidqueryResultsMustIncludeUpdatedValueOfChangedNodeProperties(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;tx.success()
        ;
        } longnodeId
        ; try ( Transaction tx =db.beginTx( )
        )
            {awaitIndexesOnline()
            ; Node node =db.createNode ( LABEL)
            ;node.setProperty (PROP , "primo")
            ; nodeId =node.getId()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            {db.getNodeById ( nodeId).setProperty (PROP , "secundo")
            ;assertQueryFindsIds (db ,true ,"nodes" , "primo")
            ;assertQueryFindsIds (db ,true ,"nodes" ,"secundo" , nodeId)
            ;tx.success()
        ;
    }

    }@
    Test public voidqueryResultsMustIncludeUpdatedValuesOfChangedRelationshipProperties(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleRelationshipIndex()
            ;tx.success()
        ;
        } longrelId
        ; try ( Transaction tx =db.beginTx( )
        )
            {awaitIndexesOnline()
            ; Node node =db.createNode()
            ; Relationship rel =node.createRelationshipTo (node , REL)
            ;rel.setProperty (PROP , "primo")
            ; relId =rel.getId()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            {db.getRelationshipById ( relId).setProperty (PROP , "secundo")
            ;assertQueryFindsIds (db ,false ,"rels" , "primo")
            ;assertQueryFindsIds (db ,false ,"rels" ,"secundo" , relId)
            ;tx.success()
        ;
    }

    }@
    Test public voidqueryResultsMustNotIncludeNodesWithRemovedIndexedProperties(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;tx.success()
        ;
        } longnodeId
        ; try ( Transaction tx =db.beginTx( )
        )
            {awaitIndexesOnline()
            ; Node node =db.createNode ( LABEL)
            ;node.setProperty (PROP , "value")
            ; nodeId =node.getId()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            {db.getNodeById ( nodeId).removeProperty ( PROP)
            ;assertQueryFindsIds (db ,true ,"nodes" , "value")
            ;tx.success()
        ;
    }

    }@
    Test public voidqueryResultsMustNotIncludeRelationshipsWithRemovedIndexedProperties(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleRelationshipIndex()
            ;tx.success()
        ;
        } longrelId
        ; try ( Transaction tx =db.beginTx( )
        )
            {awaitIndexesOnline()
            ; Node node =db.createNode()
            ; Relationship rel =node.createRelationshipTo (node , REL)
            ;rel.setProperty (PROP , "value")
            ; relId =rel.getId()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            {db.getRelationshipById ( relId).removeProperty ( PROP)
            ;assertQueryFindsIds (db ,false ,"rels" , "value")
            ;tx.success()
        ;
    }

    }@
    Test public voidqueryResultsMustNotIncludeNodesWithRemovedIndexedLabels(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;tx.success()
        ;
        } longnodeId
        ; try ( Transaction tx =db.beginTx( )
        )
            { Node node =db.createNode ( LABEL)
            ;node.setProperty (PROP , "value")
            ; nodeId =node.getId()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            {db.getNodeById ( nodeId).removeLabel ( LABEL)
            ;assertQueryFindsIds (db ,true ,"nodes" , "nodes")
            ;tx.success()
        ;
    }

    }@
    Test public voidqueryResultsMustIncludeOldNodePropertyValuesWhenModificationsAreUndone(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;tx.success()
        ;
        } longnodeId
        ; try ( Transaction tx =db.beginTx( )
        )
            {awaitIndexesOnline()
            ; Node node =db.createNode ( LABEL)
            ;node.setProperty (PROP , "primo")
            ; nodeId =node.getId()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            { Node node =db.getNodeById ( nodeId)
            ;assertQueryFindsIds (db ,true ,"nodes" ,"primo" , nodeId)
            ;assertQueryFindsIds (db ,true ,"nodes" , "secundo")
            ;node.setProperty (PROP , "secundo")
            ;assertQueryFindsIds (db ,true ,"nodes" , "primo")
            ;assertQueryFindsIds (db ,true ,"nodes" ,"secundo" , nodeId)
            ;node.setProperty (PROP , "primo")
            ;assertQueryFindsIds (db ,true ,"nodes" ,"primo" , nodeId)
            ;assertQueryFindsIds (db ,true ,"nodes" , "secundo")
            ;tx.success()
        ;
    }

    }@
    Test public voidqueryResultsMustIncludeOldRelationshipPropertyValuesWhenModificationsAreUndone(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleRelationshipIndex()
            ;tx.success()
        ;
        } longrelId
        ; try ( Transaction tx =db.beginTx( )
        )
            {awaitIndexesOnline()
            ; Node node =db.createNode()
            ; Relationship rel =node.createRelationshipTo (node , REL)
            ;rel.setProperty (PROP , "primo")
            ; relId =rel.getId()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            { Relationship rel =db.getRelationshipById ( relId)
            ;assertQueryFindsIds (db ,false ,"rels" ,"primo" , relId)
            ;assertQueryFindsIds (db ,false ,"rels" , "secundo")
            ;rel.setProperty (PROP , "secundo")
            ;assertQueryFindsIds (db ,false ,"rels" , "primo")
            ;assertQueryFindsIds (db ,false ,"rels" ,"secundo" , relId)
            ;rel.setProperty (PROP , "primo")
            ;assertQueryFindsIds (db ,false ,"rels" ,"primo" , relId)
            ;assertQueryFindsIds (db ,false ,"rels" , "secundo")
            ;tx.success()
        ;
    }

    }@
    Test public voidqueryResultsMustIncludeOldNodePropertyValuesWhenRemovalsAreUndone(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;tx.success()
        ;
        } longnodeId
        ; try ( Transaction tx =db.beginTx( )
        )
            {awaitIndexesOnline()
            ; Node node =db.createNode ( LABEL)
            ;node.setProperty (PROP , "primo")
            ; nodeId =node.getId()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            { Node node =db.getNodeById ( nodeId)
            ;assertQueryFindsIds (db ,true ,"nodes" ,"primo" , nodeId)
            ;node.removeProperty ( PROP)
            ;assertQueryFindsIds (db ,true ,"nodes" , "primo")
            ;node.setProperty (PROP , "primo")
            ;assertQueryFindsIds (db ,true ,"nodes" ,"primo" , nodeId)
            ;tx.success()
        ;
    }

    }@
    Test public voidqueryResultsMustIncludeOldRelationshipPropertyValuesWhenRemovalsAreUndone(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleRelationshipIndex()
            ;tx.success()
        ;
        } longrelId
        ; try ( Transaction tx =db.beginTx( )
        )
            {awaitIndexesOnline()
            ; Node node =db.createNode()
            ; Relationship rel =node.createRelationshipTo (node , REL)
            ;rel.setProperty (PROP , "primo")
            ; relId =rel.getId()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            { Relationship rel =db.getRelationshipById ( relId)
            ;assertQueryFindsIds (db ,false ,"rels" ,"primo" , relId)
            ;rel.removeProperty ( PROP)
            ;assertQueryFindsIds (db ,false ,"rels" , "primo")
            ;rel.setProperty (PROP , "primo")
            ;assertQueryFindsIds (db ,false ,"rels" ,"primo" , relId)
            ;tx.success()
        ;
    }

    }@
    Test public voidqueryResultsMustIncludeNodesWhenNodeLabelRemovalsAreUndone(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;tx.success()
        ;
        } longnodeId
        ; try ( Transaction tx =db.beginTx( )
        )
            {awaitIndexesOnline()
            ; Node node =db.createNode ( LABEL)
            ;node.setProperty (PROP , "primo")
            ; nodeId =node.getId()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            { Node node =db.getNodeById ( nodeId)
            ;node.removeLabel ( LABEL)
            ;assertQueryFindsIds (db ,true ,"nodes" , "primo")
            ;node.addLabel ( LABEL)
            ;assertQueryFindsIds (db ,true ,"nodes" ,"primo" , nodeId)
            ;tx.success()
        ;
    }

    }@
    Test public voidqueryResultsFromTransactionStateMustSortTogetherWithResultFromBaseIndex(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;tx.success()
        ;
        } longfirstId
        ; longsecondId
        ; longthirdId
        ; try ( Transaction tx =db.beginTx( )
        )
            {awaitIndexesOnline()
            ; Node first =db.createNode ( LABEL)
            ;first.setProperty (PROP , "God of War")
            ; firstId =first.getId()
            ; Node third =db.createNode ( LABEL)
            ;third.setProperty (PROP , "God Wars: Future Past")
            ; thirdId =third.getId()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            { Node second =db.createNode ( LABEL)
            ;second.setProperty (PROP , "God of War III Remastered")
            ; secondId =second.getId()
            ;assertQueryFindsIds (db ,true ,"nodes" ,"god of war" ,firstId ,secondId , thirdId)
            ;tx.success()
        ;
    }

    }@
    Test public voidqueryingDroppedIndexForNodesInDroppingTransactionMustThrow(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;tx.success()
        ;
        }awaitIndexesOnline()
        ; try ( Transaction tx =db.beginTx( )
        )
            {db.execute (format (DROP , "nodes" )).close()
            ;expectedException.expect (QueryExecutionException. class)
            ;db.execute (format (QUERY_NODES ,"nodes" , "blabla" ))
        ;
    }

    }@
    Test public voidqueryingDroppedIndexForRelationshipsInDroppingTransactionMustThrow(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleRelationshipIndex()
            ;tx.success()
        ;
        }awaitIndexesOnline()
        ; try ( Transaction tx =db.beginTx( )
        )
            {db.execute (format (DROP , "rels" )).close()
            ;expectedException.expect (QueryExecutionException. class)
            ;db.execute (format (QUERY_RELS ,"rels" , "blabla" ))
        ;
    }

    }@
    Test public voidcreatingAndDroppingIndexesInSameTransactionMustNotThrow(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;db.execute (format (DROP , "nodes" )).close()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            {createSimpleRelationshipIndex()
            ;db.execute (format (DROP , "rels" )).close()
            ;tx.success()
        ;
        }awaitIndexesOnline()
        ; try ( Transaction tx =db.beginTx( )
        )
            {assertFalse (db.schema().getIndexes().iterator().hasNext( ))
            ;tx.success()
        ;
    }

    }@
    Test public voideventuallyConsistenIndexMustNotIncludeEntitiesAddedInTransaction(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {db.execute (format (NODE_CREATE ,"nodes" ,array (LABEL.name( )) ,array ( PROP ) + EVENTUALLY_CONSISTENT )).close()
            ;db.execute (format (RELATIONSHIP_CREATE ,"rels" ,array (REL.name( )) ,array ( PROP ) + EVENTUALLY_CONSISTENT )).close()
            ;tx.success()
        ;
        } try ( Transaction tx =db.beginTx( )
        )
            {awaitIndexesOnline()
            ; Node node =db.createNode ( LABEL)
            ;node.setProperty (PROP , "value")
            ;node.createRelationshipTo (node , REL).setProperty (PROP , "value")

            ;assertQueryFindsIds (db ,true ,"nodes" , "value")
            ;assertQueryFindsIds (db ,false ,"rels" , "value")
            ;db.execute ( AWAIT_REFRESH).close()
            ;assertQueryFindsIds (db ,true ,"nodes" , "value")
            ;assertQueryFindsIds (db ,false ,"rels" , "value")
            ;tx.success()
        ;
    }

    }@
    Test public voidtransactionStateMustNotPreventIndexUpdatesFromBeingApplied( ) throws
    Exception
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;createSimpleRelationshipIndex()
            ;tx.success()
        ;
        }awaitIndexesOnline()
        ; try ( Transaction tx =db.beginTx( )
        )
            { Node node =db.createNode ( LABEL)
            ;node.setProperty (PROP , "value")
            ; Relationship rel =node.createRelationshipTo (node , REL)
            ;rel.setProperty (PROP , "value")
            ; LongHashSet nodeIds = newLongHashSet()
            ; LongHashSet relIds = newLongHashSet()
            ;nodeIds.add (node.getId( ))
            ;relIds.add (rel.getId( ))

            ; ExecutorService executor =cleanup.add (Executors.newSingleThreadExecutor( ))
            ;executor.submit (( )
            ->
                { try ( Transaction forkedTx =db.beginTx( )
                )
                    { Node node2 =db.createNode ( LABEL)
                    ;node2.setProperty (PROP , "value")
                    ; Relationship rel2 =node2.createRelationshipTo (node2 , REL)
                    ;rel2.setProperty (PROP , "value")
                    ;nodeIds.add (node2.getId( ))
                    ;relIds.add (rel2.getId( ))
                    ;forkedTx.success()
                ;
            }}).get()
            ;assertQueryFindsIds (db ,true ,"nodes" ,"value" , nodeIds)
            ;assertQueryFindsIds (db ,false ,"rels" ,"value" , relIds)
            ;tx.success()
        ;
    }

    }@
    Test public voiddropMustNotApplyToRegularSchemaIndexes(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {db.schema().indexFor ( LABEL).on ( PROP).create()
            ;tx.success()
        ;
        }awaitIndexesOnline()
        ; StringschemaIndexName
        ; try ( Transaction ignore =db.beginTx( )
        )
            { try ( Result result =db.execute ( "call db.indexes" )
            )
                {assertTrue (result.hasNext( ))
                ; schemaIndexName =result.next().get ( "indexName").toString()
            ;
            }expectedException.expect (QueryExecutionException. class)
            ;db.execute (format (DROP , schemaIndexName )).close()
        ;
    }

    }@
    Test public voidfulltextIndexMustNotBeAvailableForRegularIndexSeeks(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;tx.success()
        ;
        } String valueToQueryFor ="value to query for"
        ; try ( Transaction tx =db.beginTx( )
        )
            {awaitIndexesOnline()
            ;List<Value > values =generateRandomSimpleValues()
            ; for ( Value value : values
            )
                {db.createNode ( LABEL).setProperty (PROP ,value.asObject( ))
            ;
            }db.createNode ( LABEL).setProperty (PROP , valueToQueryFor)
            ;tx.success()
        ;
        }Map<String,Object > params = newHashMap<>()
        ;params.put ("prop" , valueToQueryFor)
        ; try ( Result result =db.execute ( "profile match (n:" +LABEL.name( ) + ") where n." + PROP +" = {prop} return n" , params )
        )
            {assertNoIndexSeeks ( result)
        ;
        } try ( Result result =db.execute ( "cypher planner=rule profile match (n:" +LABEL.name( ) + ") where n." + PROP +" = {prop} return n" , params )
        )
            {assertNoIndexSeeks ( result)
        ;
        } try ( Result result =db.execute ( "cypher 2.3 profile match (n:" +LABEL.name( ) + ") where n." + PROP +" = {prop} return n" , params )
        )
            {assertNoIndexSeeks ( result)
        ;
        } try ( Result result =db.execute ( "cypher 3.1 profile match (n:" +LABEL.name( ) + ") where n." + PROP +" = {prop} return n" , params )
        )
            {assertNoIndexSeeks ( result)
        ;
        } try ( Result result =db.execute ( "cypher 3.4 profile match (n:" +LABEL.name( ) + ") where n." + PROP +" = {prop} return n" , params )
        )
            {assertNoIndexSeeks ( result)
        ;
    }

    }@
    Test public voidfulltextIndexMustNotBeAvailableForRegularIndexSeeksAfterShutDown(
    )
        { db =createDatabase()
        ; try ( Transaction tx =db.beginTx( )
        )
            {createSimpleNodesIndex()
            ;tx.success()
        ;
        }db.shutdown()
        ; db =createDatabase()
        ; String valueToQueryFor ="value to query for"
        ; try ( Transaction tx =db.beginTx( )
        )
            {awaitIndexesOnline()
            ;List<Value > values =generateRandomSimpleValues()
            ; for ( Value value : values
            )
                {db.createNode ( LABEL).setProperty (PROP ,value.asObject( ))
            ;
            }db.createNode ( LABEL).setProperty (PROP , valueToQueryFor)
            ;tx.success()
        ;
        }Map<String,Object > params = newHashMap<>()
        ;params.put ("prop" , valueToQueryFor)
        ; try ( Result result =db.execute ( "profile match (n:" +LABEL.name( ) + ") where n." + PROP +" = {prop} return n" , params )
        )
            {assertNoIndexSeeks ( result)
        ;
        } try ( Result result =db.execute ( "cypher planner=rule profile match (n:" +LABEL.name( ) + ") where n." + PROP +" = {prop} return n" , params )
        )
            {assertNoIndexSeeks ( result)
        ;
        } try ( Result result =db.execute ( "cypher 2.3 profile match (n:" +LABEL.name( ) + ") where n." + PROP +" = {prop} return n" , params )
        )
            {assertNoIndexSeeks ( result)
        ;
        } try ( Result result =db.execute ( "cypher 3.1 profile match (n:" +LABEL.name( ) + ") where n." + PROP +" = {prop} return n" , params )
        )
            {assertNoIndexSeeks ( result)
        ;
        } try ( Result result =db.execute ( "cypher 3.4 profile match (n:" +LABEL.name( ) + ") where n." + PROP +" = {prop} return n" , params )
        )
            {assertNoIndexSeeks ( result)
        ;
    }

    } private voidassertNoIndexSeeks ( Result result
    )
        {assertThat (result.stream().count() ,is ( 1L ))
        ; String planDescription =result.getExecutionPlanDescription().toString()
        ;assertThat (planDescription ,containsString ( "NodeByLabel" ))
        ;assertThat (planDescription ,not (containsString ( "IndexSeek" ) ))
    ;

    } private GraphDatabaseAPIcreateDatabase(
    )
        { return(GraphDatabaseAPI )cleanup.add (builder.newGraphDatabase( ))
    ;

    } private voidawaitIndexesOnline(
    )
        { try ( Transaction tx =db.beginTx( )
        )
            {db.schema().awaitIndexesOnline (1 ,TimeUnit. MINUTES)
            ;tx.success()
        ;
    }

    } static voidassertQueryFindsIds ( GraphDatabaseServicedb , booleanqueryNodes , Stringindex , Stringquery ,long ... ids
    )
        { try ( Transaction tx =db.beginTx( )
        )
            { String queryCall = queryNodes ? QUERY_NODES :QUERY_RELS
            ; Result result =db.execute (format (queryCall ,index , query ))
            ; int num =0
            ; Double score =Double.MAX_VALUE
            ; while (result.hasNext( )
            )
                { Map entry =result.next()
                ; Long nextId =((Entity )entry.get ( queryNodes ? NODE : RELATIONSHIP)).getId()
                ; Double nextScore =(Double )entry.get ( SCORE)
                ;assertThat (nextScore ,lessThanOrEqualTo ( score ))
                ; score =nextScore
                ; if ( num <ids. length
                )
                    {assertEquals (format ("Result returned id %d, expected %d" ,nextId ,ids[num ]) ,ids[num] ,nextId.longValue( ))
                ;
                }
                else
                    {fail (format ("Result returned id %d, which is beyond the number of ids (%d) that were expected." ,nextId ,ids. length ))
                ;
                }num++
            ;
            }assertEquals ("Number of results differ from expected" ,ids.length , num)
            ;tx.success()
        ;
    }

    } static voidassertQueryFindsIds ( GraphDatabaseServicedb , booleanqueryNodes , Stringindex , Stringquery , LongHashSet ids
    )
        { ids = newLongHashSet ( ids) ;
        // Create a defensive copy, because we're going to modify this instance. String queryCall = queryNodes ? QUERY_NODES :QUERY_RELS
        ;LongFunction<Entity > getEntity = queryNodes ?db:: getNodeById :db::getRelationshipById
        ;long[ ] expectedIds =ids.toArray()
        ; MutableLongSet actualIds = newLongHashSet()
        ; try ( Transaction tx =db.beginTx( )
        )
            { Result result =db.execute (format (queryCall ,index , query ))
            ; Double score =Double.MAX_VALUE
            ; while (result.hasNext( )
            )
                { Map entry =result.next()
                ; long nextId =((Entity )entry.get ( queryNodes ? NODE : RELATIONSHIP)).getId()
                ; Double nextScore =(Double )entry.get ( SCORE)
                ;assertThat (nextScore ,lessThanOrEqualTo ( score ))
                ; score =nextScore
                ;actualIds.add ( nextId)
                ; if (!ids.remove ( nextId )
                )
                    { String msg = "This id was not expected: " +nextId
                    ;failQuery (getEntity ,index ,query ,ids ,expectedIds ,actualIds , msg)
                ;
            }
            } if (!ids.isEmpty( )
            )
                { String msg = "Not all expected ids were found: " +ids
                ;failQuery (getEntity ,index ,query ,ids ,expectedIds ,actualIds , msg)
            ;
            }tx.success()
        ;
    }

    } private static voidfailQuery (LongFunction<Entity >getEntity , Stringindex , Stringquery , MutableLongSetids ,long[ ]expectedIds , MutableLongSetactualIds
            , String msg
    )
        { StringBuilder message = newStringBuilder ( msg).append ( '\n')
        ; MutableLongIterator itr =ids.longIterator()
        ; while (itr.hasNext( )
        )
            { long id =itr.next()
            ; Entity entity =getEntity.apply ( id)
            ;message.append ( '\t').append ( entity).append (entity.getAllProperties( )).append ( '\n')
        ;
        }message.append ( "for query: '").append ( query).append ( "'\nin index: ").append ( index).append ( '\n')
        ;message.append ( "all expected ids: ").append (Arrays.toString ( expectedIds )).append ( '\n')
        ;message.append ( "actual ids: ").append ( actualIds)
        ; itr =actualIds.longIterator()
        ; while (itr.hasNext( )
        )
            { long id =itr.next()
            ; Entity entity =getEntity.apply ( id)
            ;message.append ( "\n\t").append ( entity).append (entity.getAllProperties( ))
        ;
        }fail (message.toString( ))
    ;

    } static Stringarray (String ... args
    )
        { returnArrays.stream ( args).map ( s -> "\"" + s + "\"").collect (Collectors.joining (", " ,"[" , "]" ))
    ;

    } privateList<Value >generateRandomNonStringValues(
    )
        {Predicate<Value > nonString = v ->v.valueGroup( ) !=ValueGroup.TEXT
        ; returngenerateRandomValues ( nonString)
    ;

    } privateList<Value >generateRandomSimpleValues(
    )
        {EnumSet<ValueGroup > simpleTypes =EnumSet.of
                (ValueGroup.BOOLEAN ,ValueGroup.BOOLEAN_ARRAY ,ValueGroup.NUMBER ,ValueGroup. NUMBER_ARRAY)
        ; returngenerateRandomValues ( v ->simpleTypes.contains (v.valueGroup( ) ))
    ;

    } privateList<Value >generateRandomValues (Predicate<Value > predicate
    )
        { int valuesToGenerate =1000
        ; RandomValues generator =RandomValues.create()
        ;List<Value > values = newArrayList<> ( valuesToGenerate)
        ; for ( int i =0 ; i <valuesToGenerate ;i ++
        )
            { Valuevalue
            ;
            do
                { value =generator.nextValue()
            ;
            } while (!predicate.test ( value ))
            ;values.add ( value)
        ;
        } returnvalues
    ;

    } private StringquoteValueForQuery ( Value value
    )
        { returnQueryParserUtil.escape (value.prettyPrint( )).replace ("\\" , "\\\\").replace ("\"" , "\\\"")
    ;

    } private voidcreateSimpleRelationshipIndex(
    )
        {db.execute (format (RELATIONSHIP_CREATE ,"rels" ,array (REL.name( )) ,array ( PROP ) )).close()
    ;

    } private voidcreateSimpleNodesIndex(
    )
        {db.execute (format (NODE_CREATE ,"nodes" ,array (LABEL.name( )) ,array ( PROP ) )).close()
    ;
}
