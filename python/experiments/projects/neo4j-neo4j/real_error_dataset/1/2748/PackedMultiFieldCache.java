/*
 * Copyright (c) 2002-2020 "Neo4j,"
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
package org.neo4j.consistency.checking.cache;

import org.neo4j.consistency.checking.ByteArrayBitsManipulator;
import org.neo4j.internal.batchimport.cache.ByteArray;
import org.neo4j.kernel.impl.store.record.Record;

import static org.neo4j.consistency.checking.cache.CacheSlots.ID_SLOT_SIZE;

/**
 * Simply combining a {@link ByteArray} with {@link ByteArrayBitsManipulator}, so that each byte[] index can be split up into
 * slots, i.e. holding multiple values for space efficiency and convenience.
 */
class PackedMultiFieldCache
{
    private final ByteArray array;
    private ByteArrayBitsManipulator slots;
    private long[] initValues;

    PackedMultiFieldCache( ByteArray array, int... slotSizes )
    {
        this.array = array;
        setSlotSizes( slotSizes );
    }

    void put( long index, long... values )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            slots.set( array, index, i, values[i] );
        }
    }

    void put( long index, int slot, long value )
    {
        slots.set( array, index, slot, value );
    }

    long get( long index, int slot )
    {
        if ( index < array.length() )
        {
            return slots.get( array, index, slot );
        }
        return initValues[slot];
    }

    void setSlotSizes( int... slotSizes )
    {
        this.slots = new ByteArrayBitsManipulator( slotSizes );
        this.initValues = getInitVals( slotSizes );
    }

    void clear()
    {
        long length = array.length();
        for ( long i = 0; i < length; i++ )
        {
            clear( i );
        }
    }

    void clear( long index )
    {
        put( index, initValues );
    }

    private static long[] getInitVals( int[] slotSizes )
    {
        long[] initVals = new long[slotSizes.length];
        for ( int i = 0; i < initVals.length; i++ )
        {
            initVals[i] = isId( slotSizes, i ) ? Record.NO_NEXT_RELATIONSHIP.intValue() : 0;
        }
        return initVals;
    }

    private static boolean isId( int[] slotSizes, int i )
    {
        return slotSizes[i] >= ID_SLOT_SIZE;
    }
}
