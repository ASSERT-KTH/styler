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
package org.neo4j.bolt.v1.runtime;

import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.ExecutionPlanDescription;
import org.neo4j.helpers.collection.Iterators;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.kernel.impl.util.ValueUtils;
import org.neo4j.values.storable.DoubleValue;
import org.neo4j.values.virtual.MapValue;
import org.neo4j.values.virtual.VirtualValues;

import static org.junit.Assert.assertEquals;
import static org.neo4j.values.storable.Values.longValue;
import static org.neo4j.values.storable.Values.stringValue;

public class ExecutionPlanConverterTest
{

    @Test
    public void profileStatisticConversion()
    {
        MapValue convertedMap = ExecutionPlanConverter.convert(
                new TestExecutionPlanDescription( "description", getProfilerStatistics(), getIdentifiers(),
                        getArguments() ) );
        assertEquals( convertedMap.get( "operatorType" ), stringValue( "description" ) );
        assertEquals( convertedMap.get( "args" ), ValueUtils.asMapValue( getArguments() ) );
        assertEquals( convertedMap.get( "identifiers" ), ValueUtils.asListValue( getIdentifiers() ));
        assertEquals( convertedMap.get( "children" ), VirtualValues.EMPTY_LIST );
        assertEquals( convertedMap.get( "rows" ), longValue( 1L ));
        assertEquals( convertedMap.get( "dbHits" ), longValue( 2L ) );
        assertEquals( convertedMap.get( "pageCacheHits" ), longValue( 3L ) );
        assertEquals( convertedMap.get( "pageCacheMisses" ), longValue( 2L ) );
        assertEquals( ((DoubleValue) convertedMap.get
        ("pageCacheHitRatio" )) . doubleValue(),3.0/5  , 0.0001 ); assertEquals (convertedMap
        .size (),9); } privateMap
    <

    String ,Object>getArguments() {returnMapUtil
    .
        map ("argKey","argValue" ); } privateSet
    <

    String >getIdentifiers() {returnIterators
    .
        asSet ("identifier1","identifier2" ); } privateTestProfilerStatistics
    getProfilerStatistics

    ( ) {returnnew
    TestProfilerStatistics
        ( 1 ,2 ,3 ,2 ); } privateclass
    TestExecutionPlanDescription

    implements ExecutionPlanDescription { private final
    String

        name ; private finalProfilerStatistics
        profilerStatistics ; private finalSet
        < String >identifiers;private finalMap
        < String ,Object>arguments;TestExecutionPlanDescription (String

        name, ProfilerStatistics profilerStatistics, Set <String >identifiers,Map <String
                ,Object>arguments){ this .
        name
            =name; this .profilerStatistics
            =profilerStatistics; this .identifiers
            =identifiers; this .arguments
            =arguments; } @Override
        public

        StringgetName
        ( ) {returnname
        ;
            } @Override
        public

        List<
        ExecutionPlanDescription >getChildren() {returnCollections
        .
            emptyList ();}@Override
        public

        Map<
        String ,Object>getArguments() {returnarguments
        ;
            } @Override
        public

        Set<
        String >getIdentifiers() {returnidentifiers
        ;
            } @Override
        public

        booleanhasProfilerStatistics
        ( ) {returnprofilerStatistics
        !=
            null ; } @Override
        public

        ProfilerStatisticsgetProfilerStatistics
        ( ) {returnprofilerStatistics
        ;
            } }private
        class
    TestProfilerStatistics

    implements ExecutionPlanDescription . ProfilerStatistics {privatefinal
    long

        rows ; private finallong
        dbHits ; private finallong
        pageCacheHits ; private finallong
        pageCacheMisses ; private TestProfilerStatistics(

        long rows, long dbHits, long pageCacheHits, long pageCacheMisses) { this .
        rows
            =rows; this .dbHits
            =dbHits; this .pageCacheHits
            =pageCacheHits; this .pageCacheMisses
            =pageCacheMisses; } @Override
        public

        longgetRows
        ( ) {returnrows
        ;
            } @Override
        public

        longgetDbHits
        ( ) {returndbHits
        ;
            } @Override
        public

        longgetPageCacheHits
        ( ) {returnpageCacheHits
        ;
            } @Override
        public

        longgetPageCacheMisses
        ( ) {returnpageCacheMisses
        ;
            } }}
        