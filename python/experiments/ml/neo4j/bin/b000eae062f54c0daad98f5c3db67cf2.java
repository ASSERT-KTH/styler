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
package org.neo4j.helpers;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TransactionFailureException;
import org.neo4j.graphdb.TransactionTerminatedException;
import org.neo4j.kernel.api.exceptions.Status;
import org.neo4j.test.rule.EmbeddedDatabaseRule;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

public class TransactionTemplateTest
{
    @Rule
    public EmbeddedDatabaseRule databaseRule = new EmbeddedDatabaseRule();

    @Rule
    public final ExpectedException expected = ExpectedException.none();

    private TransactionTemplate template;
    private CountingMonitor monitor;

    @ Before publicvoid

    setUp(
    ) { monitor=new
    CountingMonitor
        ( ) ; template=newTransactionTemplate
        ( ) . with(databaseRule
                .getGraphDatabaseAPI( )).monitor( monitor
                ).retries ( 5
                ).backoff ( 3
                ,TimeUnit. MILLISECONDS) ;}@ Testpublic
    void

    shouldForceUserToCallWith(
    ) { expected.expectCause
    (
        allOf(instanceOf( IllegalArgumentException.
                class) ,hasProperty( "message",
                is( "You need to call 'with(GraphDatabaseService)' on the template in order to use it") )) ) ; TransactionTemplate transactionTemplate =new
        TransactionTemplate ( ) ; transactionTemplate.execute(
        transaction->null) ; } @ Testpublic
    void

    validateGraphDatabaseService(
    ) { expected.expect
    (
        NullPointerException.class) ;template. with(
        null);} @ Testpublic
    void

    validateRetires(
    ) { expected.expect
    (
        IllegalArgumentException.class) ;expected. expectMessage(
        "Number of retries must be greater than or equal to 0");template . retries(
        -1); }@ Testpublic
    void

    validateBackoff(
    ) { expected.expect
    (
        IllegalArgumentException.class) ;expected. expectMessage(
        "Backoff time must be a positive number");template . backoff(
        -10,TimeUnit .SECONDS) ;}@ Testpublic
    void

    validateMonitor(
    ) { expected.expect
    (
        NullPointerException.class) ;template. monitor(
        null);} @ Testpublic
    void

    validateRetryOn(
    ) { expected.expect
    (
        NullPointerException.class) ;template. retryOn(
        null);} @ Testpublic
    void

    shouldRetryOnError(
    ) { IllegalArgumentExceptionex=
    new
        IllegalArgumentException ( ) ; template.execute(
        newFailingRetryConsumer(3 , ex) ); assertThat ( monitor.

        numRetry, is(3) ); assertThat ( monitor.
        failures, contains(ex, ex, ex) ); assertThat ( monitor.
        fails, empty()) ;}@ Testpublic
    void

    shouldFailIfAllRetiresFail(
    ) { IllegalArgumentExceptionex=
    new
        IllegalArgumentException ( ) ; try{template.
        execute
        (
            newFailingRetryConsumer(10 , ex) ); } catch (TransactionFailureException
        ignored
        ) { } assertThat (
        monitor
        .

        numRetry, is(5) ); assertThat ( monitor.
        failures, contains(ex, ex, ex, ex, ex, ex) ); // 5 retires results in 6 total failures assertThat (monitor .
        fails, contains(ex) ); } @ Testpublic
    void

    defaultExceptionsForExit(
    ) { Errorerror=
    new
        Error ( ) ; TransactionTerminatedExceptionterminatedException=new
        TransactionTerminatedException ( Status . Transaction. Terminated);try{ template.

        execute
        (
            (Consumer<Transaction >)tx->{throw error ;
            }
                ) ;}
            catch (TransactionFailureException
        ex
        ) { // Expected } try
        {
            template
        .

        execute
        (
            (Consumer<Transaction >)tx->{throw terminatedException ;
            }
                ) ;}
            catch (TransactionFailureException
        ignored
        ) { } assertThat (
        monitor
        .

        numRetry, is(0) ); assertThat ( monitor.
        failures, contains(error, terminatedException) ); assertThat ( monitor.
        fails, contains(error, terminatedException) ); } @ Testpublic
    void

    overrideRetryExceptions(
    ) { template=template
    .
        retryOn ( e->!IllegalArgumentException . class .isInstance(e)); IllegalArgumentException e =new
        IllegalArgumentException ( ) ; try{template.
        execute
        (
            (Consumer<Transaction >)tx->{throw e ;
            }
                ) ;}
            catch (TransactionFailureException
        ignored
        ) { } assertThat (
        monitor
        .

        numRetry, is(0) ); assertThat ( monitor.
        failures, contains(e) ); assertThat ( monitor.
        fails, contains(e) ); } @ Testpublic
    void

    overrideRetryShouldOverrideDefaults(
    ) { template=template
    .
        retryOn ( e->!IllegalArgumentException . class .isInstance(e)); TransactionTerminatedException fakeException =new

        TransactionTerminatedException ( Status . Transaction. Terminated);template. execute(
        newFailingRetryConsumer(1 , fakeException) ); assertThat ( monitor.

        numRetry, is(1) ); assertThat ( monitor.
        failures, contains(fakeException) ); assertThat ( monitor.
        fails, empty()) ;}private staticclass
    FailingRetryConsumer

    implements Consumer < Transaction > {privatefinalint
    successAfter
        ; private final RuntimeExceptionfakeException
        ; private int tries;
        private FailingRetryConsumer (int

        successAfter ,RuntimeException fakeException ){ this . successAfter
        =
            successAfter;this . fakeException=
            fakeException;} @ Overridepublic
        void

        accept(
        Transaction transaction ){ if ( tries
        ++
            < successAfter ){ throw fakeException ;
            }
                } }private
            static
        class
    CountingMonitor

    implements TransactionTemplate . Monitor { intnumRetry;
    List
        < Throwable>
        fails=newArrayList < > ( );List<Throwable>
        failures=newArrayList < > ( );@Overridepublicvoid

        failure(
        Throwable ex ){ failures . add
        (
            ex);} @ Overridepublic
        void

        failed(
        Throwable ex ){ fails . add
        (
            ex);} @ Overridepublic
        void

        retrying(
        ) { numRetry++;
        }
            }}