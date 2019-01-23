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
import org.neo4j.graphdb.index.IndexManager;importorg.neo4j.helpers.collection
. MapUtil;importorg.neo4j.io.layout
. DatabaseLayout;importorg.neo4j.kernel.configuration
. Config;importorg.neo4j.kernel.impl.factory
. OperationalMode;importorg.neo4j.kernel.impl.index
. IndexConfigStore;importorg.neo4j.kernel.impl.index
. IndexEntityType;importorg.neo4j.test.rule
. CleanupRule;importorg.neo4j.test.rule
. TestDirectory;importorg.neo4j.test.rule.fs

. DefaultFileSystemRule ;importstaticorg.junit.Assert

. assertSame ;
public
    class ReadOnlyIndexReferenceFactoryTest { private final TestDirectorytestDirectory=TestDirectory.testDirectory
    ( ) ; private final ExpectedExceptionexpectedException=ExpectedException.none
    ( ) ; private final CleanupRule cleanupRule=newCleanupRule
    ( ) ; private final DefaultFileSystemRule fileSystemRule=newDefaultFileSystemRule

    ()
    ; @ Rule public RuleChainruleChain=RuleChain . outerRule(cleanupRule) . around
                                          (expectedException) . around(testDirectory) . around(

    fileSystemRule ) ; private static final StringINDEX_NAME
    = "testIndex";private LuceneDataSource . LuceneFilesystemFacadefilesystemFacade=LuceneDataSource.LuceneFilesystemFacade
    . FS ; private IndexIdentifier indexIdentifier= newIndexIdentifier(IndexEntityType . Node,
    INDEX_NAME ) ;private

    IndexConfigStoreindexStore
    ; @ Beforepublicvoid setUp (
    )
        throwsException{setupIndexInfrastructure
    (

    );
    } @ Testpublicvoid createReadOnlyIndexReference (
    )
        throws Exception { ReadOnlyIndexReferenceFactoryindexReferenceFactory=getReadOnlyIndexReferenceFactory
        ( ) ; IndexReferenceindexReference=indexReferenceFactory . createIndexReference(
        indexIdentifier);cleanupRule . add(

        indexReference);expectedException .expect( UnsupportedOperationException.
        class);indexReference.getWriter
    (

    );
    } @ Testpublicvoid refreshReadOnlyIndexReference (
    )
        throws Exception { ReadOnlyIndexReferenceFactoryindexReferenceFactory=getReadOnlyIndexReferenceFactory
        ( ) ; IndexReferenceindexReference=indexReferenceFactory . createIndexReference(
        indexIdentifier);cleanupRule . add(

        indexReference ) ; IndexReferencerefreshedIndex=indexReferenceFactory . refresh(
        indexReference);assertSame ("Refreshed instance should be the same." ,indexReference,
    refreshedIndex

    ) ; }privatevoid setupIndexInfrastructure (
    )
        throws Exception { DatabaseLayoutdatabaseLayout=testDirectory.databaseLayout
        ( ) ; indexStore= newIndexConfigStore (databaseLayout,fileSystemRule. get(
        ));indexStore .set(Node .class ,INDEX_NAME,MapUtil .stringMap(IndexManager .PROVIDER ,"lucene" , "type" ,"fulltext"
        ) ) ; LuceneDataSource luceneDataSource= newLuceneDataSource (databaseLayout,Config.defaults
                () ,indexStore,fileSystemRule.get (), OperationalMode.
        single
        )
            ;try{luceneDataSource.init
            ();luceneDataSource . getIndexSearcher(
        indexIdentifier
        )
        ;
            }finally{luceneDataSource.shutdown
        (
    )

    ; } }privateReadOnlyIndexReferenceFactory
    getReadOnlyIndexReferenceFactory
        ( ) {return newReadOnlyIndexReferenceFactory (filesystemFacade,testDirectory.databaseLayout() . file( "index" ), new IndexTypeCache (indexStore
    )
)
