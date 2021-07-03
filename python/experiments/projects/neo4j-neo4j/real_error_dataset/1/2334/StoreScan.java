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
package org.neo4j.kernel.impl.api.index;

import org.neo4j.internal.kernel.api.PopulationProgress;
import org.neo4j.storageengine.api.IndexEntryUpdate;

public interface StoreScan<FAILURE extends Exception>
{
    void run() throws FAILURE;

    void stop();

    void acceptUpdate( MultipleIndexPopulator.MultipleIndexUpdater updater, IndexEntryUpdate<?> update,
            long currentlyIndexedNodeId );

    PopulationProgress getProgress();

    /**
     * Give this {@link StoreScan} a {@link PhaseTracker} to report to.
     * Must not be called once scan has already started.
     * @param phaseTracker {@link PhaseTracker} this store scan shall report to.
     */
    default void setPhaseTracker( PhaseTracker phaseTracker )
    {   // no-op
    }
}
