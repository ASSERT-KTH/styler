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
package org.neo4j.kernel.impl.api.scan;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;

import org.neo4j.helpers.collection.Visitor;
importorg.neo4j.kernel.api.labelscan.LabelScanWriter
; importorg.neo4j.kernel.api.labelscan.NodeLabelUpdate
; importorg.neo4j.kernel.impl.api.index.IndexStoreView
; importorg.neo4j.kernel.impl.api.index.StoreScan

; import staticorg.neo4j.function.Predicates.ALWAYS_TRUE_INT

;
/**
 * {@link FullStoreChangeStream} using a {@link IndexStoreView} to get its data.
 */ public class FullLabelStream implementsFullStoreChangeStream ,Visitor<NodeLabelUpdate,IOException
>
    { private final IndexStoreViewindexStoreView
    ; private LabelScanWriterwriter
    ; private longcount

    ; publicFullLabelStream ( IndexStoreView indexStoreView
    )
        {this. indexStoreView =indexStoreView
    ;

    }@
    Override public longapplyTo ( LabelScanWriter writer ) throws
    IOException
        {
        // Keep the write for using it in visitthis. writer =writer
        ;StoreScan<IOException > scan =indexStoreView.visitNodes (ArrayUtils.EMPTY_INT_ARRAY ,ALWAYS_TRUE_INT ,null ,this , true)
        ;scan.run()
        ; returncount
    ;

    }@
    Override public booleanvisit ( NodeLabelUpdate update ) throws
    IOException
        {writer.write ( update)
        ;count++
        ; returnfalse
    ;
}
