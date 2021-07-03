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
package org.neo4j.consistency.checking.full;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ConsistencyFlags
{
    public static final ConsistencyFlags DEFAULT = new ConsistencyFlags( true, true, true, true, false, false );

    private final boolean checkGraph;
    private final boolean checkIndexes;
    private final boolean checkIndexStructure;
    private final boolean checkLabelScanStore;
    private final boolean checkRelationshipTypeScanStore;
    private final boolean checkPropertyOwners;

    public ConsistencyFlags( boolean checkGraph,
            boolean checkIndexes,
            boolean checkIndexStructure,
            boolean checkLabelScanStore,
            boolean checkRelationshipTypeScanStore,
            boolean checkPropertyOwners )
    {
        this.checkGraph = checkGraph;
        this.checkIndexes = checkIndexes;
        this.checkIndexStructure = checkIndexStructure;
        this.checkLabelScanStore = checkLabelScanStore;
        this.checkRelationshipTypeScanStore = checkRelationshipTypeScanStore;
        this.checkPropertyOwners = checkPropertyOwners;
    }

    public boolean isCheckGraph()
    {
        return checkGraph;
    }

    public boolean isCheckIndexes()
    {
        return checkIndexes;
    }

    public boolean isCheckIndexStructure()
    {
        return checkIndexStructure;
    }

    public boolean isCheckLabelScanStore()
    {
        return checkLabelScanStore;
    }

    public boolean isCheckRelationshipTypeScanStore()
    {
        return checkRelationshipTypeScanStore;
    }

    public boolean isCheckPropertyOwners()
    {
        return checkPropertyOwners;
    }

    public ConsistencyFlags withCheckRelationshipTypeScanStore( boolean check )
    {
        return new ConsistencyFlags( checkGraph, checkIndexes, checkIndexStructure, checkLabelScanStore, check, checkPropertyOwners );
    }

    @Override
    public boolean equals( Object o )
    {
        return EqualsBuilder.reflectionEquals( this, o );
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode( this );
    }
}
