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
package org.neo4j.kernel.impl.newapi;

import org.neo4j.internal.kernel.api.NodeCursor;
import org.neo4j.storageengine.api.schema.IndexProgressor;
import org.neo4j.storageengine.api.schema.IndexProgressor.ExplicitClient;

import static org.neo4j.kernel.impl.store.record.AbstractBaseRecord.NO_ID;

class DefaultNodeExplicitIndexCursor extends IndexCursor<IndexProgressor>
        implements org.neo4j.internal.kernel.api.NodeExplicitIndexCursor, ExplicitClient
{
    private Read read;
    private int expectedSize;
    private long node;
    private float score;

    private final DefaultCursors pool;

    DefaultNodeExplicitIndexCursor( DefaultCursors pool )
    {
        this.pool = pool;
        node = NO_ID;
    }

    @Override
    public void initialize( IndexProgressor progressor , int
    expectedSize
        ){super. initialize (progressor
        );this . expectedSize=
    expectedSize

    ;}
    @ Override publicboolean acceptEntity (long reference , float
    score
        ){this . node=
        reference;this . score=
        score ;return
    true

    ;}
    @ Override publicbooleannext
    (
        ) {returninnerNext(
    )

    ; } publicvoid setRead ( Read
    read
        ){this . read=
    read

    ;}
    @ Override publicintexpectedTotalNumberOfResults
    (
        ) {return
    expectedSize

    ;}
    @ Override publicfloatscore
    (
        ) {return
    score

    ;}
    @ Override publicvoid node ( NodeCursor
    cursor
        ){read. singleNode( node ,cursor
    )

    ;}
    @ Override publiclongnodeReference
    (
        ) {return
    node

    ;}
    @ Override publicvoidclose
    (
        ) { if(!isClosed (
        )
            ){super.close(
            ) ; node=
            NO_ID ; score=
            0 ; expectedSize=
            0 ; read=

            null;pool. accept (this
        )
    ;

    }}
    @ Override publicbooleanisClosed
    (
        ) {returnsuper.isClosed(
    )

    ;}
    @ Override publicStringtoString
    (
        ) { if(isClosed (
        )
            ) {return
        "NodeExplicitIndexCursor[closed state]"
        ;
        }
            else { return "NodeExplicitIndexCursor[node=" + node + ", expectedSize=" + expectedSize + ", score=" +
                    score + ", underlying record="+super.toString ( )+
        "]"
    ;

    } } publicvoidrelease
    (
        )
    {
// nothing to do
