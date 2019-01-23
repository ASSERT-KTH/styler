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
package org.neo4j.kernel.impl.index.schema;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.neo4j.cursor.RawCursor;
import org.neo4j.index.internal.gbptree.Hit;
import org.neo4j.internal.kernel.api.IndexOrder;
import org.neo4j.internal.kernel.api.IndexQuery;
import org.neo4j.kernel.api.schema.index.TestIndexDescriptorFactory;
import org.neo4j.test.rule.RandomRule;
import org.neo4j.values.storable.TextValue;
import org.neo4j.values.storable.Value;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when

; public class
FilteringNativeHitIndexProgressorTest
    {@
    Rule public final RandomRule random = newRandomRule()

    ;@
    Test public voidshouldFilterResults(
    )
        {
        // givenList<String > keys = newArrayList<>()
        ; for ( int i =0 ; i <100 ;i ++
        )
            {
            // duplicates are finekeys.add (random.nextString( ))
        ;

        }RawCursor<Hit<StringIndexKey,NativeIndexValue>,IOException > cursor = newResultCursor (keys.iterator( ))
        ; NodeValueIterator valueClient = newNodeValueIterator(
        )
            {@
            Override public booleanneedsValues(
            )
                { returntrue
            ;
        }}
        ;IndexQuery[ ] predicates = newIndexQuery[]{mock (IndexQuery. class)}
        ;Predicate<String > filter = string ->string.contains ( "a")
        ;when (predicates[0].acceptsValue (any (Value. class ) )).then ( invocation ->filter.test (((TextValue)invocation.getArgument ( 0)).stringValue( ) ))
        ;FilteringNativeHitIndexProgressor<StringIndexKey,NativeIndexValue > progressor = newFilteringNativeHitIndexProgressor<> (cursor ,valueClient
                , newArrayList<>() , predicates)
        ;valueClient.initialize (TestIndexDescriptorFactory.forLabel (0 , 0) ,progressor ,predicates ,IndexOrder.NONE ,valueClient.needsValues( ))
        ;List<Long > result = newArrayList<>()

        ;
        // when while (valueClient.hasNext( )
        )
            {result.add (valueClient.next( ))
        ;

        }
        // then for ( int i =0 ; i <keys.size() ;i ++
        )
            { if (filter.test (keys.get ( i ) )
            )
                {assertTrue (result.remove ((long ) i ))
            ;
        }
        }assertTrue (result.isEmpty( ))
    ;
}
