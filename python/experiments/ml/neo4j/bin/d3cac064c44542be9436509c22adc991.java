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
package org.neo4j.unsafe.impl.batchimport.staging;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.unsafe.impl.batchimport.stats.DetailLevel;
import org.neo4j.unsafe.impl.batchimport.stats.Key;
import org.neo4j.unsafe.impl.batchimport.stats.Keys;
import org.neo4j.unsafe.impl.batchimport.stats.Stat;
import org.neo4j.unsafe.impl.batchimport.stats.StatsProvider;
import org.neo4j.unsafe.impl.batchimport.stats.StepStats;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

/**
 * A bit like a mocked {@link Step}, but easier to work with.
 */
public class ControlledStep<T> implements Step<T>, StatsProvider
    { public staticControlledStep<? >stepWithAverageOf ( Stringname , intmaxProcessors , long avg
    )
        { returnstepWithStats (name ,maxProcessors ,Keys.avg_processing_time , avg)
    ;

    } public staticControlledStep<? >stepWithStats ( Stringname , intmaxProcessors
            ,Map<Key,Long > statistics
    )
        {ControlledStep<? > step = newControlledStep<> (name , maxProcessors)
        ; for (Map.Entry<Key,Long > statistic :statistics.entrySet( )
        )
            {step.setStat (statistic.getKey() ,statistic.getValue().longValue( ))
        ;
        } returnstep
    ;

    } public staticControlledStep<? >stepWithStats ( Stringname , intmaxProcessors ,Object ... statisticsAltKeyAndValue
    )
        { returnstepWithStats (name ,maxProcessors ,MapUtil.genericMap ( statisticsAltKeyAndValue ))
    ;

    } private final Stringname
    ; private finalMap<Key,ControlledStat > stats = newHashMap<>()
    ; private final intmaxProcessors
    ; private volatile int numberOfProcessors =1
    ; private final CountDownLatch completed = newCountDownLatch ( 1)

    ; publicControlledStep ( Stringname , int maxProcessors
    )
        {this (name ,maxProcessors , 1)
    ;

    } publicControlledStep ( Stringname , intmaxProcessors , int initialProcessorCount
    )
        {this. maxProcessors = maxProcessors == 0 ?Integer. MAX_VALUE :maxProcessors
        ;this. name =name
        ;processors ( initialProcessorCount - 1)
    ;

    } publicControlledStep<T >setProcessors ( int numberOfProcessors
    )
        {
        // We don't have to assert max processors here since importer will not count every processor
        // equally. A step being very idle (due to being very very fast) counts as almost nothing.processors ( numberOfProcessors)
        ; returnthis
    ;

    }@
    Override public intprocessors ( int delta
    )
        { if ( delta > 0
        )
            { numberOfProcessors =min ( numberOfProcessors +delta , maxProcessors)
        ;
        } else if ( delta < 0
        )
            { numberOfProcessors =max (1 , numberOfProcessors + delta)
        ;
        } returnnumberOfProcessors
    ;

    }@
    Override public Stringname(
    )
        { returnname
    ;

    }@
    Override public longreceive ( longticket , T batch
    )
        { throw newUnsupportedOperationException ( "Cannot participate in actual processing yet")
    ;

    } public voidsetStat ( Keykey , long value
    )
        {stats.put (key , newControlledStat ( value ))
    ;

    }@
    Override public StepStatsstats(
    )
        { return newStepStats (name ,!isCompleted() ,Arrays.asList ( this ))
    ;

    }@
    Override public voidendOfUpstream(
    )
    {

    }@
    Override public booleanisCompleted(
    )
        { returncompleted.getCount( ) ==0
    ;

    }@
    Override public voidawaitCompleted( ) throws
    InterruptedException
        {completed.await()
    ;

    }@
    Override public voidsetDownstream (Step<? > downstreamStep
    )
    {

    }@
    Override public voidreceivePanic ( Throwable cause
    )
    {

    }@
    Override public voidclose(
    )
    {

    }@
    Override public Statstat ( Key key
    )
        { returnstats.get ( key)
    ;

    }@
    Override public voidstart ( int orderingGuarantees
    )
    {

    }@
    Override publicKey[ ]keys(
    )
        { returnstats.keySet().toArray ( newKey[stats.size() ])
    ;

    } public voidcomplete(
    )
        {completed.countDown()
    ;

    } private static class ControlledStat implements
    Stat
        { private final longvalue

        ;ControlledStat ( long value
        )
            {this. value =value
        ;

        }@
        Override public DetailLeveldetailLevel(
        )
            { returnDetailLevel.BASIC
        ;

        }@
        Override public longasLong(
        )
            { returnvalue
        ;

        }@
        Override public StringtoString(
        )
            { return "" +value
        ;
    }

    }@
    Override public StringtoString(
    )
        { returngetClass().getSimpleName( ) + "[" +name( ) + ", " + stats +"]"
    ;
}
