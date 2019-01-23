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
package org.neo4j.index.impl.lucene.explicit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.helpers.collection.MapUtil;importorg
. neo4j.io.layout.DatabaseLayout;importorg
. neo4j.kernel.configuration.Config;importorg.neo4j
. kernel.impl.factory.OperationalMode;importorg.neo4j
. kernel.impl.index.IndexConfigStore;importorg.neo4j
. kernel.impl.index.IndexEntityType;importorg
. neo4j.test.rule.CleanupRule;importorg
. neo4j.test.rule.TestDirectory;importorg.neo4j

. test .rule.fs.DefaultFileSystemRule;import

static org .
junit
    . Assert . assertSame ; publicclassReadOnlyIndexReferenceFactoryTest{privatefinal
    TestDirectory testDirectory = TestDirectory . testDirectory();privatefinal
    ExpectedException expectedException = ExpectedException . none ();private
    final CleanupRule cleanupRule = new CleanupRule ();private

    finalDefaultFileSystemRule
    fileSystemRule = new DefaultFileSystemRule ();@ Rule publicRuleChainruleChain= RuleChain .
                                          outerRule(cleanupRule ) .around(expectedException ) .around

    ( testDirectory ) . around ( fileSystemRule)
    ; privatestaticfinal String INDEX_NAME ="testIndex";privateLuceneDataSource.
    LuceneFilesystemFacade filesystemFacade = LuceneDataSource . LuceneFilesystemFacade. FS;privateIndexIdentifier indexIdentifier =new
    IndexIdentifier ( IndexEntityType.

    Node,
    INDEX_NAME ) ;privateIndexConfigStore indexStore ;
    @
        BeforepublicvoidsetUp
    (

    )throws
    Exception { setupIndexInfrastructure() ; }
    @
        Test public void createReadOnlyIndexReference()throws
        Exception { ReadOnlyIndexReferenceFactory indexReferenceFactory=getReadOnlyIndexReferenceFactory( ) ;IndexReference
        indexReference=indexReferenceFactory. createIndexReference (indexIdentifier

        );cleanupRule. add(indexReference );
        expectedException.expect(UnsupportedOperationException.
    class

    );
    indexReference . getWriter() ; }
    @
        Test public void refreshReadOnlyIndexReference()throws
        Exception { ReadOnlyIndexReferenceFactory indexReferenceFactory=getReadOnlyIndexReferenceFactory( ) ;IndexReference
        indexReference=indexReferenceFactory. createIndexReference (indexIdentifier

        ) ; cleanupRule .add(indexReference ) ;IndexReference
        refreshedIndex=indexReferenceFactory. refresh( indexReference);
    assertSame

    ( "Refreshed instance should be the same." ,indexReference, refreshedIndex )
    ;
        } private void setupIndexInfrastructure()throwsException{
        DatabaseLayout databaseLayout = testDirectory. databaseLayout( );indexStore=new IndexConfigStore(
        databaseLayout,fileSystemRule. get()) ;indexStore .set(Node .class,INDEX_NAME ,MapUtil .stringMap ( IndexManager .PROVIDER
        , "lucene" , "type" ,"fulltext" )) ;LuceneDataSourceluceneDataSource=newLuceneDataSource
                (databaseLayout ,Config.defaults() ,indexStore, fileSystemRule.
        get
        (
            ),OperationalMode.single)
            ;try{luceneDataSource . init(
        )
        ;
        luceneDataSource
            .getIndexSearcher(indexIdentifier);
        }
    finally

    { luceneDataSource .shutdown(
    )
        ; } }private ReadOnlyIndexReferenceFactorygetReadOnlyIndexReferenceFactory (){returnnewReadOnlyIndexReferenceFactory(filesystemFacade , testDirectory. databaseLayout () . file ("index"
    )
,
