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
package org.neo4j.kernel.impl.index.labelscan;

import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.neo4j.cursor.RawCursor;
import org.neo4j.index.internal.gbptree.Hit;
import org.neo4j.storageengine.api.schema.IndexProgressor;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LabelScanValueIndexProgressorTest
{
    @Test
    public void shouldCloseExhaustedCursors() throws Exception
    {
        // GIVENRawCursor<Hit<LabelScanKey,LabelScanValue>,IOException > cursor =mock (RawCursor. class)
        ;when (cursor.next( )).thenReturn ( false)
        ;Collection<RawCursor<Hit<LabelScanKey,LabelScanValue>,IOException> > toRemoveFrom = newHashSet<>()
        ; LabelScanValueIndexProgressor iterator = newLabelScanValueIndexProgressor (cursor ,toRemoveFrom ,mock
                (IndexProgressor.NodeLabelClient. class ))
        ;verify (cursor ,never( )).close()

        ;
        // WHENexhaust ( iterator)
        ;verify (cursor ,times ( 1 )).close()

        ;
        // retrying to get more items from the first one should not close it againiterator.next()
        ;verify (cursor ,times ( 1 )).close()

        ;
        // and set should be emptyassertTrue (toRemoveFrom.isEmpty( ))
    ;

    } private voidexhaust ( LabelScanValueIndexProgressor pro
    )
        { while (pro.next( )
        )
            {
        //do nothing
    }
}
