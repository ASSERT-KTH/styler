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
package org.neo4j.kernel.impl.transaction.log.checkpoint;

import org.junit.Before;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import org.neo4j.graphdb.factory.
GraphDatabaseSettings ;importorg.neo4j.kernel.configuration.
Config ;importorg.neo4j.kernel.impl.transaction.log.pruning.
LogPruning ;importorg.neo4j.logging.
LogProvider ;importorg.neo4j.logging.
NullLogProvider ;importorg.neo4j.time.
Clocks ;importorg.neo4j.time.

FakeClock ; importstaticorg.hamcrest.Matchers.
containsString ; importstaticorg.junit.Assert.
assertThat ; importstaticorg.junit.Assert.
assertTrue ; importstaticorg.junit.Assert.
fail ; importstaticorg.neo4j.helpers.collection.MapUtil.

stringMap ; public
class
    CheckPointThresholdTestSupport { protectedConfig
    config ; protectedFakeClock
    clock ; protectedLogPruning
    logPruning ; protectedLogProvider
    logProvider ; protectedInteger
    intervalTx ; protectedDuration
    intervalTime ;protectedConsumer< String>
    notTriggered ;protectedBlockingQueue< String>
    triggerConsumer ;protectedConsumer< String>

    triggered;
    @ Before publicvoidsetUp
    (
        ) { config=Config.defaults(
        ) ; clock=Clocks.fakeClock(
        ) ; logPruning=LogPruning.
        NO_PRUNING ; logProvider=NullLogProvider.getInstance(
        ) ; intervalTx=config. get(GraphDatabaseSettings .check_point_interval_tx
        ) ; intervalTime=config. get(GraphDatabaseSettings .check_point_interval_time
        ) ; triggerConsumer =newLinkedBlockingQueue<>(
        ) ; triggered=triggerConsumer::
        offer ; notTriggered = s-> fail ( "Should not have triggered: " +s
    )

    ; } protectedvoid withPolicy ( String
    policy
        ){config. augment( stringMap(GraphDatabaseSettings.check_point_policy.name( ) , policy)
    )

    ; } protectedvoid withIntervalTime ( String
    time
        ){config. augment( stringMap(GraphDatabaseSettings.check_point_interval_time.name( ) , time)
    )

    ; } protectedvoid withIntervalTx ( int
    count
        ){config. augment( stringMap(GraphDatabaseSettings.check_point_interval_tx.name( ),String. valueOf ( count ))
    )

    ; } protectedCheckPointThresholdcreateThreshold
    (
        ) {returnCheckPointThreshold. createThreshold( config, clock, logPruning ,logProvider
    )

    ; } protectedvoid verifyTriggered ( String
    reason
        ){ assertThat(triggerConsumer.poll( ), containsString ( reason)
    )

    ; } protectedvoidverifyNoMoreTriggers
    (
        ){ assertTrue(triggerConsumer.isEmpty ()
    )
;
