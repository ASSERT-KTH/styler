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

import org.neo4j.dbms.database.DatabaseManager;
import org.neo4j.io.pagecache.tracing.cursor.context.EmptyVersionContextSupplier;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.impl.store.id.DefaultIdGeneratorFactory;
import org.neo4j.kernel.impl.store.record.PropertyBlock;
import org.neo4j.logging.NullLogProvider;
import org.neo4j.test.rule.PageCacheAndDependenciesRule;
import org.neo4j.values.storable.TextValue;
import org.neo4j.values.storable.Value;
import org.neo4j.values.storable.Values;

import static org.junit.Assert.assertEquals;

public class ShortStringPropertyEncodeTest
{
    private static final int KEY_ID = 0;

    @Rule
    public final PageCacheAndDependenciesRule storage = new PageCacheAndDependenciesRule();

    private NeoStores neoStores;
    private PropertyStore propertyStore;

    @Before
    public void setupStore()
    {
        neoStores = new StoreFactory( storage.directory().databaseLayout(), Config.defaults(), new DefaultIdGeneratorFactory( storage.fileSystem() ),
                storage.pageCache(), storage.fileSystem(), NullLogProvider.getInstance(), EmptyVersionContextSupplier.EMPTY ).openNeoStores( true,
                StoreType.PROPERTY, StoreType.PROPERTY_ARRAY, StoreType.PROPERTY_STRING );
        propertyStore = neoStores.getPropertyStore();
    }

    @After
    public void closeStore()
    {
        neoStores.close();
    }

    @Test
    public void canEncodeEmptyString()
    {
        assertCanEncode( "" );
    }

    @Test
    public void canEncodeReallyLongString()
    {
        assertCanEncode( "                    " ); // 20 spaces
        assertCanEncode( "                " ); // 16 spaces
    }

    @Test
    public void canEncodeFifteenSpaces()
    {
        assertCanEncode( "               " );
    }

    @Test
    public void canEncodeNumericalString()
    {
        assertCanEncode( "0123456789+,'.-" );
        assertCanEncode( " ,'.-0123456789" );
        assertCanEncode( "+ '.0123456789-" );
        assertCanEncode( "+, 0123456789.-" );
        assertCanEncode( "+,0123456789' -" );
        assertCanEncode( "+0123456789,'. " );
        // IP(v4) numbersassertCanEncode ( "192.168.0.1")
        ;assertCanEncode ( "127.0.0.1")
        ;assertCanEncode ( "255.255.255.255")
    ;

    }@
    Test public voidcanEncodeTooLongStringsWithCharsInDifferentTables(
    )
        {assertCanEncode ( "____________+")
        ;assertCanEncode ( "_____+_____")
        ;assertCanEncode ( "____+____")
        ;assertCanEncode ( "HELLO world")
        ;assertCanEncode ( "Hello_World")
    ;

    }@
    Test public voidcanEncodeUpToNineEuropeanChars(
    )
        {
        // Shorter than 10 charsassertCanEncode ( "fågel") ;
        // "bird" in SwedishassertCanEncode ( "påfågel") ;
        // "peacock" in SwedishassertCanEncode ( "påfågelö") ;
        // "peacock island" in SwedishassertCanEncode ( "påfågelön") ;
        // "the peacock island" in Swedish
        // 10 charsassertCanEncode ( "påfågelöar") ;
    // "peacock islands" in Swedish

    }@
    Test public voidcanEncodeEuropeanCharsWithPunctuation(
    )
        {assertCanEncode ( "qHm7 pp3")
        ;assertCanEncode ( "UKKY3t.gk")
    ;

    }@
    Test public voidcanEncodeAlphanumerical(
    )
        {assertCanEncode ( "1234567890") ;
        // Just a sanity checkassertCanEncodeInBothCasings ( "HelloWor1d") ;
        // There is a number thereassertCanEncode ( "          ") ;
        // Alphanum is the first that can encode 10 spacesassertCanEncode ( "_ _ _ _ _ ") ;
        // The only available punctuationassertCanEncode ( "H3Lo_ or1D") ;
        // Mixed case + punctuationassertCanEncode ( "q1w2e3r4t+") ;
    // + is not in the charset

    }@
    Test public voidcanEncodeHighUnicode(
    )
        {assertCanEncode ( "˿")
        ;assertCanEncode ( "hello˿")
    ;

    }@
    Test public voidcanEncodeLatin1SpecialChars(
    )
        {assertCanEncode ( "#$#$#$#")
        ;assertCanEncode ( "$hello#")
    ;

    }@
    Test public voidcanEncodeTooLongLatin1String(
    )
        {assertCanEncode ( "#$#$#$#$")
    ;

    }@
    Test public voidcanEncodeLowercaseAndUppercaseStringsUpTo12Chars(
    )
        {assertCanEncodeInBothCasings ( "hello world")
        ;assertCanEncode ( "hello_world")
        ;assertCanEncode ( "_hello_world")
        ;assertCanEncode ( "hello::world")
        ;assertCanEncode ( "hello//world")
        ;assertCanEncode ( "hello world")
        ;assertCanEncode ( "http://ok")
        ;assertCanEncode ( "::::::::")
        ;assertCanEncode ( " _.-:/ _.-:/")
    ;

    } private voidassertCanEncodeInBothCasings ( String string
    )
        {assertCanEncode (string.toLowerCase( ))
        ;assertCanEncode (string.toUpperCase( ))
    ;

    } private voidassertCanEncode ( String string
    )
        {encode ( string)
    ;

    } private voidencode ( String string
    )
        { PropertyBlock block = newPropertyBlock()
        ; TextValue expectedValue =Values.stringValue ( string)
        ;propertyStore.encodeValue (block ,KEY_ID , expectedValue)
        ;assertEquals (0 ,block.getValueRecords().size( ))
        ; Value readValue =block.getType().value (block , propertyStore)
        ;assertEquals (expectedValue , readValue)
    ;
}
