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

import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.helpers.collection.Iterables;
import org.neo4j.io.fs.FileSystemAbstraction;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.neo4j.test.rule.TestDirectory;
import org.neo4j.test.rule.fs.EphemeralFileSystemRule;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;

import static org.junit.Assert.assertEquals;

public class PropertyKeyTest
{
    @Rule
    public final EphemeralFileSystemRule fs = new EphemeralFileSystemRule();
    @Rule
    public final TestDirectory testDirectory = TestDirectory.testDirectory();

    @Test
    public void lazyLoadWithinWriteTransaction() throws Exception
    {
        // Given
        FileSystemAbstraction fileSystem = fs.get();
        BatchInserter inserter = BatchInserters.inserter( testDirectory.databaseDir(), fileSystem );
        int count = 3000;
        long nodeId = inserter.createNode( mapWithManyProperties( count /* larger than initial property index load threshold */ ) );
        inserter.shutdown();

        GraphDatabaseService db = new TestGraphDatabaseFactory().setFileSystem( fileSystem ).newImpermanentDatabase( testDirectory.databaseDir() );

        // When
        try ( Transaction tx = db.beginTx() )
        {
            db.createNode();
            Node node = db.getNodeById( nodeId );

            // Then
            assertEquals( count, Iterables.count( node.getPropertyKeys() ) );
            tx.success();
        }
        finally
        {
            db.shutdown();
        }
    }

    private static Map<String, Object> mapWithManyProperties( int count )
    {
        Map<String, Object> properties = new HashMap<>();
        for ( int i = 0; i < count; i++ )
        {
            properties.put( "key:" + i, "value" );
        }
        return properties;
    }
}
