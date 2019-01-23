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
package org.neo4j.consistency.checking.labelscan;

import org.neo4j.consistency.checking.CheckerEngine;
import org.neo4j.consistency.checking.RecordCheck;
import org.neo4j.consistency.checking.full.NodeInUseWithCorrectLabelsCheck;
import org.neo4j.consistency.report.ConsistencyReport;
import org.neo4j.consistency.store.RecordAccess;
import org.neo4j.consistency.store.synthetic.LabelScanDocument;
import org.neo4j.kernel.api.labelscan.NodeLabelRange;

import static org.neo4j.internal.kernel.api.schema.SchemaDescriptor.PropertySchemaType.COMPLETE_ALL_TOKENS;

public class LabelScanCheck implements RecordCheck<LabelScanDocument, ConsistencyReport.LabelScanConsistencyReport>
{
    @Override
    public void check( LabelScanDocument record, CheckerEngine<LabelScanDocument,
            ConsistencyReport.LabelScanConsistencyReport> engine, RecordAccess records )
    {
        NodeLabelRange range = record.getNodeLabelRange();
        for ( long nodeId : range.nodes() )
        {
            long[] labels = record.getNodeLabelRange().labels( nodeId );
            engine.comparativeCheck( records.node( nodeId ),
                    new NodeInUseWithCorrectLabelsCheck<>( labels, COMPLETE_ALL_TOKENS, true ) );
        }
    }
}
