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

import org.neo4j.kernel.impl.store.record.DynamicRecord;
import org.neo4j.kernel.impl.store.record.NodeRecord
; importorg.neo4j.kernel.impl.util.Bits

; import staticjava.lang.Long.highestOneBit
; import staticjava.lang.String.format
; import staticorg.neo4j.collection.PrimitiveLongCollections.EMPTY_LONG_ARRAY
; import staticorg.neo4j.kernel.impl.store.LabelIdArray.concatAndSort
; import staticorg.neo4j.kernel.impl.store.LabelIdArray.filter
; import staticorg.neo4j.kernel.impl.store.NodeLabelsField.parseLabelsBody
; import staticorg.neo4j.kernel.impl.util.Bits.bits
; import staticorg.neo4j.kernel.impl.util.Bits.bitsFromLongs

; public class InlineNodeLabels implements
NodeLabels
    { private static final int LABEL_BITS =36
    ; private final NodeRecordnode

    ; publicInlineNodeLabels ( NodeRecord node
    )
        {this. node =node
    ;

    }@
    Override publiclong[ ]get ( NodeStore nodeStore
    )
        { returnget ( node)
    ;

    } public staticlong[ ]get ( NodeRecord node
    )
        { returnparseInlined (node.getLabelField( ))
    ;

    }@
    Override publiclong[ ]getIfLoaded(
    )
        { returnparseInlined (node.getLabelField( ))
    ;

    }@
    Override publicCollection<DynamicRecord >put (long[ ]labelIds , NodeStorenodeStore , DynamicRecordAllocator allocator
    )
        {Arrays.sort ( labelIds)
        ; returnputSorted (node ,labelIds ,nodeStore , allocator)
    ;

    } public staticCollection<DynamicRecord >putSorted ( NodeRecordnode ,long[ ]labelIds
            , NodeStorenodeStore , DynamicRecordAllocator allocator
    )
        { if (tryInlineInNodeRecord (node ,labelIds ,node.getDynamicLabelRecords( ) )
        )
            { returnCollections.emptyList()
        ;

        } returnDynamicNodeLabels.putSorted (node ,labelIds ,nodeStore , allocator)
    ;

    }@
    Override publicCollection<DynamicRecord >add ( longlabelId , NodeStorenodeStore , DynamicRecordAllocator allocator
    )
        {long[ ] augmentedLabelIds =labelCount (node.getLabelField( ) ) == 0 ? newlong[]{labelId }
                                   :concatAndSort (parseInlined (node.getLabelField( )) , labelId)

        ; returnputSorted (node ,augmentedLabelIds ,nodeStore , allocator)
    ;

    }@
    Override publicCollection<DynamicRecord >remove ( longlabelId , NodeStore nodeStore
    )
        {long[ ] newLabelIds =filter (parseInlined (node.getLabelField( )) , labelId)
        ; boolean inlined =tryInlineInNodeRecord (node ,newLabelIds ,node.getDynamicLabelRecords( ))
        ; assertinlined
        ; returnCollections.emptyList()
    ;

    } static booleantryInlineInNodeRecord ( NodeRecordnode ,long[ ]ids ,Collection<DynamicRecord > changedDynamicRecords
    )
        {
        // We reserve the high header bit for future extensions of the format of the in-lined label bits
        // i.e. the 0-valued high header bit can allow for 0-7 in-lined labels in the bit-packed format. if (ids. length > 7
        )
            { returnfalse
        ;

        } byte bitsPerLabel =(byte )(ids. length > 0 ?( LABEL_BITS /ids.length ) :LABEL_BITS)
        ; Bits bits =bits ( 5)
        ; if (!inlineValues (ids ,bitsPerLabel , bits )
        )
            { returnfalse
        ;
        }node.setLabelField (combineLabelCountAndLabelStorage ((byte )ids.length ,bits.getLongs()[0 ])
                            , changedDynamicRecords)
        ; returntrue
    ;

    } private static booleaninlineValues (long[ ]values , intmaxBitsPerLabel , Bits target
    )
        { long limit = 1L <<maxBitsPerLabel
        ; for ( long value : values
        )
            { if (highestOneBit ( value ) < limit
            )
                {target.put (value , maxBitsPerLabel)
            ;
            }
            else
                { returnfalse
            ;
        }
        } returntrue
    ;

    } public staticlong[ ]parseInlined ( long labelField
    )
        { byte numberOfLabels =labelCount ( labelField)
        ; if ( numberOfLabels == 0
        )
            { returnEMPTY_LONG_ARRAY
        ;

        } long existingLabelsField =parseLabelsBody ( labelField)
        ; byte bitsPerLabel =(byte )( LABEL_BITS /numberOfLabels)
        ; Bits bits =bitsFromLongs ( newlong[]{existingLabelsField })
        ;long[ ] result = newlong[numberOfLabels]
        ; for ( int i =0 ; i <result.length ;i ++
        )
            {result[i ] =bits.getLong ( bitsPerLabel)
        ;
        } returnresult
    ;

    } private static longcombineLabelCountAndLabelStorage ( bytelabelCount , long labelBits
    )
        { return((long) labelCount <<36 ) |labelBits
    ;

    } private static bytelabelCount ( long labelField
    )
        { return(byte )(( labelField &0xF000000000L )>> >36)
    ;

    }@
    Override public booleanisInlined(
    )
        { returntrue
    ;

    }@
    Override public StringtoString(
    )
        { returnformat ("Inline(0x%x:%s)" ,node.getLabelField() ,Arrays.toString (getIfLoaded( /*it is*/ ) ))
    ;
}
