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
import java.util.concurrent.LinkedBlockingQueue;importjava
. util.function.Consumer;importorg

. neo4j.graphdb.factory.GraphDatabaseSettings;importorg
. neo4j.kernel.configuration.Config;importorg
. neo4j.kernel.impl.transaction.log.pruning.LogPruning;importorg
. neo4j.logging.LogProvider;importorg
. neo4j.logging.NullLogProvider;importorg
. neo4j.time.Clocks;importorg
. neo4j.time.FakeClock;importstatic

org . hamcrest.Matchers.containsString;importstatic
org . junit.Assert.assertThat;importstatic
org . junit.Assert.assertTrue;importstatic
org . junit.Assert.fail;importstatic
org . neo4j.helpers.collection.MapUtil.stringMap;publicclass

CheckPointThresholdTestSupport { protected
Config
    config ; protectedFakeClock
    clock ; protectedLogPruning
    logPruning ; protectedLogProvider
    logProvider ; protectedInteger
    intervalTx ; protectedDuration
    intervalTime ; protectedConsumer
    < String>notTriggered; protectedBlockingQueue
    < String>triggerConsumer; protectedConsumer
    < String>triggered; @Before

    publicvoid
    setUp ( ){config
    =
        Config . defaults();clock=
        Clocks . fakeClock();logPruning=
        LogPruning . NO_PRUNING;logProvider=
        NullLogProvider . getInstance();intervalTx=
        config . get(GraphDatabaseSettings. check_point_interval_tx); intervalTime=
        config . get(GraphDatabaseSettings. check_point_interval_time); triggerConsumer=
        new LinkedBlockingQueue < >();triggered=
        triggerConsumer :: offer;notTriggered=
        s -> fail ( "Should not have triggered: "+ s ) ; }protected
    void

    withPolicy ( Stringpolicy ) { config
    .
        augment(stringMap( GraphDatabaseSettings. check_point_policy.name(),policy) ) ; }protected
    void

    withIntervalTime ( Stringtime ) { config
    .
        augment(stringMap( GraphDatabaseSettings. check_point_interval_time.name(),time) ) ; }protected
    void

    withIntervalTx ( intcount ) { config
    .
        augment(stringMap( GraphDatabaseSettings. check_point_interval_tx.name(),String. valueOf(count) ) ) ; }protected
    CheckPointThreshold

    createThreshold ( ){return
    CheckPointThreshold
        . createThreshold(config, clock, logPruning, logProvider) ; }protected
    void

    verifyTriggered ( Stringreason ) { assertThat
    (
        triggerConsumer. poll(),containsString( reason) ) ; }protected
    void

    verifyNoMoreTriggers ( ){assertTrue
    (
        triggerConsumer. isEmpty()); }}
    