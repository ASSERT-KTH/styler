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
package org.neo4j.kernel.impl.storageengine.impl.recordstorage;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.neo4j.helpers.collection.IteratorWrapper;
import org.neo4j.io.pagecache.tracing.cursor.context.EmptyVersionContextSupplier;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.impl.store.NeoStores;
import org.neo4j.kernel.impl.store.StoreFactory;
import org.neo4j.kernel.impl.store.id.DefaultIdGeneratorFactory;
import org.neo4j.kernel.impl.store.record.NodeRecord;
import org.neo4j.kernel.impl.store.record.PropertyBlock;
import org.neo4j.logging.NullLogProvider;
import org.neo4j.test.rule.PageCacheAndDependenciesRule;
import org.neo4j.test.rule.RandomRule;
import org.neo4j.unsafe.batchinsert.internal.DirectRecordAccessSet;
import org.neo4j.values.storable.RandomValues;
import org.neo4j.values.storable.Value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.neo4j.helpers.collection.Iterators.iterator;

public class RecordPropertyCursorTest
{
    @Rule
    public final PageCacheAndDependenciesRule storage = new PageCacheAndDependenciesRule();
    @Rule
    public final RandomRule random = new RandomRule().withConfiguration( new RandomValues.Default()
    {
        @Override
        public int stringMaxLength()
        {
            return 10_000;
        }
    } );
    private NeoStores neoStores;
    private PropertyCreator creator;
    private NodeRecord owner;

    @Before
    public void setup()
    {
        neoStores = new StoreFactory( storage.directory().databaseLayout(), Config.defaults(), new DefaultIdGeneratorFactory( storage. fileSystem(
        ) ) ,storage.pageCache(),storage.fileSystem
    (

    ),
    NullLogProvider . getInstance()
    ,
        EmptyVersionContextSupplier.EMPTY).openAllNeoStores
    (

    true)
    ; creator =newPropertyCreator
    (
        neoStores
        .getPropertyStore( ) , newPropertyTraverser()
        ) ; owner =neoStores .getNodeStore () . newRecord(

        )
        ;} @After publicvoid closeStore() {neoStores
    .

    close(
    ) ; }@Test
    public
        void
        shouldReadPropertyChain() { // given Value[]values
        = createValues ( ); longfirstPropertyId =storeValuesAsPropertyChain ( creator,
        owner,values ) ; // whenassertPropertyChain(values
        , firstPropertyId , createCursor( )) ;} @ Testpublic

        void
        shouldReuseCursor ( ) {// givenValue[
        ]valuesA =createValues () ; longfirstPropertyIdA
        =storeValuesAsPropertyChain (creator ,owner , valuesA)
    ;

    Value[
    ] valuesB =createValues(
    )
        ;
        long firstPropertyIdB = storeValuesAsPropertyChain(creator,

        owner
        ,valuesB);// thenRecordPropertyCursor

        cursor
        =createCursor();assertPropertyChain
    (

    valuesA , firstPropertyIdA,cursor
    )
        ; assertPropertyChain (valuesB ,firstPropertyIdB,cursor) ;}
    @

    Test public voidcloseShouldBeIdempotent (){ // givenRecordPropertyCursor cursor =createCursor ( ) ;
    // when
        cursor.close(); // then cursor .close ( );
        }privateRecordPropertyCursorcreateCursor ( ){
        return new RecordPropertyCursor(neoStores.getPropertyStore (
        )
            )
            ;} privatevoidassertPropertyChain( Value[]values, longfirstPropertyId ,RecordPropertyCursorcursor){ Map<
        Integer
        ,Value >expectedValues=asMap( values)
    ;

    cursor .init( firstPropertyId);
    while
        ( cursor . next()) {// then assertEquals (expectedValues
        .remove( cursor . propertyKey ()),cursor
        . propertyValue ( ) ) ;} assertTrue ( expectedValues. isEmpty( )
        )
            ;}privateValue [ ]createValues(){int
        numberOfProperties
        = random.
    nextInt

    ( 1 ,20 ) ;Value [ ]values =newValue [ numberOfProperties
    ]
        ; for ( int key= 0 ;key
        < numberOfProperties ; key++){ values[ key] =random . nextValue( );}returnvalues ;}
        privatelongstoreValuesAsPropertyChain(PropertyCreatorcreator
        , NodeRecordowner
    ,

    Value []values){DirectRecordAccessSet access= newDirectRecordAccessSet( neoStores )
    ;
        longfirstPropertyId=creator.createPropertyChain ( owner , blocksOf(creator,values)
        , access . getPropertyRecords ( )) ; access .close() ;return firstPropertyId
        ;
            }privateMap< Integer, Value>asMap( Value[
        ]
        values ){
    Map

    < Integer,Value> map= new HashMap< >() ; for
    (
        int key =0;key<values. length; key ++ )
        {
            map .put

            (key
            , values [key ] ) ;
            }
                return map;}private Iterator<PropertyBlock > blocksOf(
            PropertyCreator
        creator,
    Value
[
