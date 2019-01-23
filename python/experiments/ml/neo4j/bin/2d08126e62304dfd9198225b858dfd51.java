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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.neo4j.kernel.impl.store.record.
DynamicRecord ;importorg.neo4j.kernel.impl.store.record.
NodeRecord ;importorg.neo4j.kernel.impl.util.

Bits ; importstaticjava.lang.Long.
highestOneBit ; importstaticjava.lang.String.
format ; importstaticorg.neo4j.collection.PrimitiveLongCollections.
EMPTY_LONG_ARRAY ; importstaticorg.neo4j.kernel.impl.store.LabelIdArray.
concatAndSort ; importstaticorg.neo4j.kernel.impl.store.LabelIdArray.
filter ; importstaticorg.neo4j.kernel.impl.store.NodeLabelsField.
parseLabelsBody ; importstaticorg.neo4j.kernel.impl.util.Bits.
bits ; importstaticorg.neo4j.kernel.impl.util.Bits.

bitsFromLongs ; public class InlineNodeLabels
implements
    NodeLabels { private static final int LABEL_BITS=
    36 ; private finalNodeRecord

    node ;public InlineNodeLabels ( NodeRecord
    node
        ){this . node=
    node

    ;}
    @ Overridepubliclong [] get ( NodeStore
    nodeStore
        ) {return get (node
    )

    ; } publicstaticlong [] get ( NodeRecord
    node
        ) {return parseInlined(node.getLabelField ()
    )

    ;}
    @ Overridepubliclong []getIfLoaded
    (
        ) {return parseInlined(node.getLabelField ()
    )

    ;}
    @ OverridepublicCollection< DynamicRecord> put(long [] labelIds ,NodeStore nodeStore , DynamicRecordAllocator
    allocator
        ){Arrays. sort (labelIds
        ) ;return putSorted( node, labelIds, nodeStore ,allocator
    )

    ; } publicstaticCollection< DynamicRecord> putSorted (NodeRecord node,long []
            labelIds ,NodeStore nodeStore , DynamicRecordAllocator
    allocator
        ) { if( tryInlineInNodeRecord( node, labelIds,node.getDynamicLabelRecords ( )
        )
            ) {returnCollections.emptyList(
        )

        ; }returnDynamicNodeLabels. putSorted( node, labelIds, nodeStore ,allocator
    )

    ;}
    @ OverridepublicCollection< DynamicRecord> add (long labelId ,NodeStore nodeStore , DynamicRecordAllocator
    allocator
        ){long [ ] augmentedLabelIds= labelCount(node.getLabelField ( ) ) == 0 ?newlong[]{ labelId
                                   }: concatAndSort( parseInlined(node.getLabelField () ) ,labelId

        ) ;return putSorted( node, augmentedLabelIds, nodeStore ,allocator
    )

    ;}
    @ OverridepublicCollection< DynamicRecord> remove (long labelId , NodeStore
    nodeStore
        ){long [ ] newLabelIds= filter( parseInlined(node.getLabelField () ) ,labelId
        ) ; boolean inlined= tryInlineInNodeRecord( node, newLabelIds,node.getDynamicLabelRecords ()
        ) ;assert
        inlined ;returnCollections.emptyList(
    )

    ; } staticboolean tryInlineInNodeRecord (NodeRecord node,long [] ids,Collection< DynamicRecord >
    changedDynamicRecords
        )
        {
        // We reserve the high header bit for future extensions of the format of the in-lined label bits // i.e. the 0-valued high header bit can allow for 0-7 in-lined labels in the bit-packed format. if(ids . length >
        7
            ) {return
        false

        ; } byte bitsPerLabel=( byte)(ids . length > 0? ( LABEL_BITS/ids. length ):LABEL_BITS
        ) ; Bits bits= bits (5
        ) ; if(! inlineValues( ids, bitsPerLabel , bits
        )
            ) {return
        false
        ;}node. setLabelField( combineLabelCountAndLabelStorage(( byte)ids. length,bits.getLongs()[ 0]
                            ) ,changedDynamicRecords
        ) ;return
    true

    ; } private staticboolean inlineValues(long [] values ,int maxBitsPerLabel , Bits
    target
        ) { long limit = 1L<<
        maxBitsPerLabel ; for ( long value :
        values
            ) { if( highestOneBit ( value ) <
            limit
                ){target. put( value ,maxBitsPerLabel
            )
            ;
            }
                else {return
            false
        ;
        } }return
    true

    ; } publicstaticlong [] parseInlined ( long
    labelField
        ) { byte numberOfLabels= labelCount (labelField
        ) ; if ( numberOfLabels ==
        0
            ) {return
        EMPTY_LONG_ARRAY

        ; } long existingLabelsField= parseLabelsBody (labelField
        ) ; byte bitsPerLabel=( byte) ( LABEL_BITS/numberOfLabels
        ) ; Bits bits= bitsFromLongs (newlong[]{ existingLabelsField}
        );long [ ] result =newlong[numberOfLabels
        ] ; for ( int i= 0 ; i<result. length; i
        ++
            ){result[ i ]=bits. getLong (bitsPerLabel
        )
        ; }return
    result

    ; } private staticlong combineLabelCountAndLabelStorage (byte labelCount , long
    labelBits
        ) {return((long ) labelCount<< 36 )|
    labelBits

    ; } private staticbyte labelCount ( long
    labelField
        ) {return( byte)( ( labelField& 0xF000000000L)> >>36
    )

    ;}
    @ Override publicbooleanisInlined
    (
        ) {return
    true

    ;}
    @ Override publicStringtoString
    (
        ) {return format( "Inline(0x%x:%s)",node.getLabelField( ),Arrays. toString(getIfLoaded ( /*it is*/ ))
    )
;
