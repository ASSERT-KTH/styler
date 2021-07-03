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
package org.neo4j.internal.schema;

import org.neo4j.values.storable.ValueCategory;

/**
 * Capabilities of an index.
 * Capabilities of an index cannot change during the indexes lifetimes.
 * Caching of IndexCapability is allowed.
 * It does NOT describe the capabilities of the index at some given moment. For example, it does not describe
 * index state. Rather, it describes the functionality that the index provides given that it is available.
 */
public interface IndexCapability
{
    IndexBehaviour[] BEHAVIOURS_NONE = new IndexBehaviour[0];

    /**
     * What possible orderings is this index capable to provide for a query on given combination of {@link ValueCategory}.
     * Ordering of ValueCategory correspond to ordering of related {@link SchemaDescriptor#getPropertyIds()}.
     *
     * @param valueCategories Ordered array of {@link ValueCategory ValueCategories} for which index should be queried. Note that valueCategory
     * must correspond to related {@link SchemaDescriptor#getPropertyIds()}. A {@code null} value in the array
     * ({@code new ValueCategory[]{null}}) is interpreted as a wildcard for any {@link ValueCategory}. Note that this is not the same as
     * {@code order(null)} which is undefined.
     * @return {@link IndexOrder} array containing all possible orderings for provided value categories or empty array if no explicit
     * ordering is possible or if length of {@code valueCategories} and {@link SchemaDescriptor#getPropertyIds()} differ.
     */
    IndexOrderCapability orderCapability( ValueCategory... valueCategories );

    /**
     * Is the index capable of providing values for a query on given combination of {@link ValueCategory}.
     * Ordering of ValueCategory correspond to ordering of {@link SchemaDescriptor#getPropertyIds()}.
     *
     * @param valueCategories Ordered array of {@link ValueCategory ValueCategories} for which index should be queried. Note that valueCategory
     * must correspond to related {@link SchemaDescriptor#getPropertyIds()}. {@link ValueCategory#UNKNOWN} can be used as a wildcard for
     * any {@link ValueCategory}. Behaviour is undefined for empty {@code null} array and {@code null} values in array.
     * @return {@link IndexValueCapability#YES} if index is capable of providing values for query on provided array of value categories,
     * {@link IndexValueCapability#NO} if not or {@link IndexValueCapability#PARTIAL} for some results. If length of
     * {@code valueCategories} and {@link SchemaDescriptor#getPropertyIds()} differ {@link IndexValueCapability#NO} is returned.
     */
    IndexValueCapability valueCapability( ValueCategory... valueCategories );

    /**
     * @return an array of behaviours that are particular to the implementation or configuration of this index.
     * It could be anything that planning could look at and either try to avoid, seek out, or issue warning for.
     */
    default IndexBehaviour[] behaviours()
    {
        return BEHAVIOURS_NONE;
    }

    IndexCapability NO_CAPABILITY = new IndexCapability()
    {
        @Override
        public IndexOrderCapability orderCapability( ValueCategory... valueCategories )
        {
            return IndexOrderCapability.NONE;
        }

        @Override
        public IndexValueCapability valueCapability( ValueCategory... valueCategories )
        {
            return IndexValueCapability.NO;
        }
    };
}
