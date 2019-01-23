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
public class ControlledStep
    < T >implementsStep< T> , StatsProvider{ public staticControlledStep < ? >
    stepWithAverageOf
        ( Stringname ,int maxProcessors, longavg){ return stepWithStats(
    name

    , maxProcessors ,Keys.avg_processing_time ,avg ) ;} public staticControlledStep
            <?>stepWithStats(String name ,
    int
        maxProcessors,Map< Key , Long >statistics){ ControlledStep< ? >step
        = new ControlledStep<>(name,maxProcessors) ; for (Map.Entry< Key
        ,
            Long>statistic: statistics.entrySet()) {step.setStat(statistic.getKey( ),
        statistic
        . getValue(
    )

    . longValue ()); }return step ;} public staticControlledStep <? > stepWithStats
    (
        String name, intmaxProcessors ,Object ...statisticsAltKeyAndValue){ return stepWithStats (name
    ,

    maxProcessors , MapUtil .genericMap
    ( statisticsAltKeyAndValue ));}privatefinal String name ; privatefinalMap<Key,
    ControlledStat > stats =new
    HashMap < > ( ) ;private
    final int maxProcessors ; private volatile intnumberOfProcessors = 1;

    private finalCountDownLatch completed =new CountDownLatch ( 1
    )
        ;public ControlledStep( Stringname , intmaxProcessors
    )

    { this( name ,maxProcessors , 1) ; } public
    ControlledStep
        (Stringname , int maxProcessors , int initialProcessorCount){ this .maxProcessors
        =maxProcessors== 0 ?Integer
        .MAX_VALUE : maxProcessors ; this.
    name

    = name;processors( initialProcessorCount- 1 ) ;
    }
        public
        ControlledStep
        <T > setProcessors(
        int numberOfProcessors)
    {

    // We don't have to assert max processors here since importer will not count every processor// equally. A step being very idle (due to being very very fast) counts as almost nothing.
    processors ( numberOfProcessors) ; return this
    ;
        } @ Override public int processors
        (
            int delta ){ if ( delta> 0 ){
        numberOfProcessors
        = min ( numberOfProcessors + delta ,
        maxProcessors
            ) ; }else if( delta < 0 ){
        numberOfProcessors
        = max(
    1

    ,numberOfProcessors
    + delta );}
    return
        numberOfProcessors ;}
    @

    Overridepublic
    String name () { returnname ; } @
    Override
        public long receive( long ticket,
    T

    batch ) {throw new UnsupportedOperationException( "Cannot participate in actual processing yet" ) ;
    }
        publicvoidsetStat( Keykey , longvalue ) { stats.
    put

    (key
    , new ControlledStat(value
    )
        ) ; }@ Overridepublic StepStatsstats(){ returnnewStepStats( name , !isCompleted
    (

    ),
    Arrays . asList(this
    )
    )

    ;}
    @ Override publicvoidendOfUpstream
    (
        ) {}@Overridepublic boolean isCompleted(
    )

    {return
    completed . getCount() == 0
    ;
        }@OverridepublicvoidawaitCompleted
    (

    )throws
    InterruptedException { completed. await(); } @
    Override
    public

    voidsetDownstream
    ( Step <? > downstreamStep )
    {
    }

    @Override
    public void receivePanic(Throwable
    cause
    )

    {}
    @ Override publicvoid close ( )
    {
        } @OverridepublicStat stat (Key
    key

    ){
    return stats .get ( key )
    ;
    }

    @Override
    public voidstart( intorderingGuarantees)
    {
        } @OverridepublicKey[]keys( ) {returnstats.keySet(). toArray(
    new

    Key [ stats.size
    (
        )]);}public
    void

    complete ( ) { completed .
    countDown
        ( ) ; }private

        staticclass ControlledStat implements Stat
        {
            privatefinallong value ;ControlledStat
        (

        longvalue
        ) { this.value
        =
            value ;}@Override
        public

        DetailLeveldetailLevel
        ( ) {returnDetailLevel
        .
            BASIC ;}
        @

        Overridepublic
        long asLong (){
        return
            value ; } @Override
        public
    String

    toString(
    ) { return""+
    value
        ; }}@OverridepublicStringtoString ( ) { returngetClass( ) . getSimpleName ( ) +"["
    +
name
