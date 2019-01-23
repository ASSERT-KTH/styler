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
package org.neo4j.collection.primitive;

import java.util.Arrays;
import java.util.function.LongPredicate;

import org.neo4j.graphdb.Resource;
import org.neo4j.graphdb.ResourceUtils;

import static org.neo4j.collection.primitive.PrimitiveLongCollections.resourceIterator;

public class PrimitiveLongResourceCollections
{
    private static final PrimitiveLongResourceIterator EMPTY = new PrimitiveLongBaseResourceIterator( null )
    {
        @Override
        protected boolean fetchNext()
        {
            return false;
        }
    };

    public static PrimitiveLongResourceIterator emptyIterator()
    {
        return EMPTY;
    }

    public static PrimitiveLongResourceIterator iterator( Resource resource, final long... items )
    {
        return resourceIterator( PrimitiveLongCollections.iterator( items ), resource );
    }

    public static PrimitiveLongResourceIterator concat( PrimitiveLongResourceIterator... primitiveLongResourceIterators )
    {
        return concat( Arrays.asList( primitiveLongResourceIterators ) );
    }

    public static PrimitiveLongResourceIterator concat( Iterable<PrimitiveLongResourceIterator> primitiveLongResourceIterators )
    {
        return new PrimitiveLongConcatingResourceIterator( primitiveLongResourceIterators );
    }

    public static PrimitiveLongResourceIterator filter ( PrimitiveLongResourceIteratorsource , LongPredicate filter
    )
        { return newPrimitiveLongFilteringResourceIterator ( source
        )
            {@
            Override public booleantest ( long item
            )
                { returnfilter.test ( item)
            ;
        }}
    ;

    } abstract static class PrimitiveLongBaseResourceIterator extendsPrimitiveLongCollections.
            PrimitiveLongBaseIterator implements
    PrimitiveLongResourceIterator
        { private Resourceresource

        ;PrimitiveLongBaseResourceIterator ( Resource resource
        )
            {this. resource =resource
        ;

        }@
        Override public voidclose(
        )
            { if ( resource != null
            )
                {resource.close()
                ; resource =null
            ;
        }
    }

    } private static class PrimitiveLongConcatingResourceIterator extendsPrimitiveLongCollections.
            PrimitiveLongConcatingIterator implements
    PrimitiveLongResourceIterator
        { private finalIterable<PrimitiveLongResourceIterator >iterators
        ; private volatile booleanclosed

        ; privatePrimitiveLongConcatingResourceIterator (Iterable<PrimitiveLongResourceIterator > iterators
        )
            {super (iterators.iterator( ))
            ;this. iterators =iterators
        ;

        }@
        Override protected booleanfetchNext(
        )
            { return! closed &&super.fetchNext()
        ;

        }@
        Override public voidclose(
        )
            { if (! closed
            )
                { closed =true
                ;ResourceUtils.closeAll ( iterators)
            ;
        }
    }

    } private abstract static class PrimitiveLongFilteringResourceIterator extends PrimitiveLongBaseResourceIterator implements
    LongPredicate
        { private final PrimitiveLongIteratorsource

        ; privatePrimitiveLongFilteringResourceIterator ( PrimitiveLongResourceIterator source
        )
            {super ( source)
            ;this. source =source
        ;

        }@
        Override protected booleanfetchNext(
        )
            { while (source.hasNext( )
            )
                { long testItem =source.next()
                ; if (test ( testItem )
                )
                    { returnnext ( testItem)
                ;
            }
            } returnfalse
        ;

        }@
        Override public abstract booleantest ( long testItem)
    ;
}
