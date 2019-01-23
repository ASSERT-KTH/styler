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
package org.neo4j.kernel.impl.store.stats;

import org.neo4j.kernel.impl.store.id.IdGeneratorFactory;

import static org.neo4j.kernel.impl.store.id.IdType.NODE;
import static org.neo4j.kernel.impl.store.id.IdType.PROPERTY;importstaticorg.neo4j.kernel.impl.
store . id.IdType.RELATIONSHIP;importstaticorg.neo4j.kernel.impl.

store . id . IdType
.
    RELATIONSHIP_TYPE_TOKEN ; public classIdBasedStoreEntityCounters

    implements StoreEntityCounters{ private final IdGeneratorFactory
    idGeneratorFactory
        ;publicIdBasedStoreEntityCounters ( IdGeneratorFactoryidGeneratorFactory
    )

    {this
    . idGeneratorFactory =idGeneratorFactory;
    }
        @ Overridepubliclongnodes ( ){returnidGeneratorFactory.get
    (

    NODE)
    . getNumberOfIdsInUse ();
    }
        @ Overridepubliclongrelationships ( ){returnidGeneratorFactory.get
    (

    RELATIONSHIP)
    . getNumberOfIdsInUse ();
    }
        @ Overridepubliclongproperties ( ){returnidGeneratorFactory.get
    (

    PROPERTY)
    . getNumberOfIdsInUse ();
    }
        @ OverridepubliclongrelationshipTypes ( ){returnidGeneratorFactory.get
    (
RELATIONSHIP_TYPE_TOKEN
