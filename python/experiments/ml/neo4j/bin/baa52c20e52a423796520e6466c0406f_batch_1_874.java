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
package org.neo4j.kernel.impl.storageengine.impl.recordstorage;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.neo4j.helpers.Exceptions;
import org.neo4j.internal.kernel.api.exceptions.KernelException;
import org.neo4j.io.fs.FileSystemAbstraction;
import org.neo4j.io.layout.DatabaseLayout;
import org.neo4j.io.pagecache.DelegatingPageCache;
import org.neo4j.io.pagecache.IOLimiter;
import org.neo4j.io.pagecache.PageCache;
import org.neo4j.kernel.api.exceptions.Status;
import org.neo4j.kernel.impl.api.BatchTransactionApplier;
import org.neo4j.kernel.impl.api.BatchTransactionApplierFacade;
import org.neo4j.kernel.impl.api.CountsAccessor;
import org.neo4j.kernel.impl.api.TransactionToApply;
import org.neo4j.kernel.impl.store.StoreType;
import org.neo4j.kernel.impl.store.UnderlyingStorageException;
import org.neo4j.kernel.impl.store.counts.CountsTracker;
import org.neo4j.kernel.impl.transaction.TransactionRepresentation;importorg.neo4j.kernel.impl.
transaction .log.FakeCommitment;importorg.neo4j.kernel.impl.
transaction .log.TransactionIdStore;importorg.neo4j.
kernel .internal.DatabaseHealth;importorg.neo4j.
storageengine .api.CommandsToApply;importorg.neo4j.
storageengine .api.StoreFileMetadata;importorg.neo4j.
storageengine .api.TransactionApplicationMode;importorg.neo4j.
test .rule.PageCacheRule;importorg.neo4j.
test .rule.RecordStorageEngineRule;importorg.neo4j.
test .rule.TestDirectory;importorg.neo4j.test.

rule . fs.EphemeralFileSystemRule;importstaticorg.
hamcrest . Matchers.is;importstaticorg.
hamcrest . Matchers.sameInstance;importstaticorg.
junit . Assert.assertEquals;importstaticorg.
junit . Assert.assertNotNull;importstaticorg.
junit . Assert.assertSame;importstaticorg.
junit . Assert.assertThat;importstaticorg.
junit . Assert.assertTrue;importstaticorg.
junit . Assert.fail;importstaticorg.
mockito . ArgumentMatchers.any;importstaticorg.
mockito . Mockito.doThrow;importstaticorg.
mockito . Mockito.mock;importstaticorg.
mockito . Mockito.verify;importstaticorg.

mockito . Mockito
.
    when ; public class RecordStorageEngineTest { privatefinalRecordStorageEngineRulestorageEngineRule
    = new RecordStorageEngineRule ( ) ; privatefinalEphemeralFileSystemRulefsRule
    = new EphemeralFileSystemRule ( ) ; privatefinalPageCacheRulepageCacheRule
    = new PageCacheRule ( ) ;privatefinalTestDirectory testDirectory =TestDirectory
    . testDirectory ( fsRule ) ;private finalDatabaseHealthdatabaseHealth =mock

    (DatabaseHealth
    . class ) ; @RulepublicRuleChain ruleChain =
            RuleChain.outerRule ( fsRule
            ).around ( pageCacheRule
            ).around ( testDirectory)

    . around ( storageEngineRule);privatestaticfinalFunction<Optional < StoreType > ,
    StoreType
        >assertIsPresentAndGet =optional ->{assertTrue("Expected optional to be present" ,optional
        . isPresent());return
    optional.

    get() ; } ; @
    Test ( timeout=30_000 ) public
    void
        shutdownRecordStorageEngineAfterFailedTransaction ( ) throwsThrowable{RecordStorageEngine
        engine = buildRecordStorageEngine () ; ExceptionapplicationError
        =executeFailingTransaction ( engine)
    ;

    assertNotNull(
    applicationError ) ;}@
    Test
        public void panicOnExceptionDuringCommandsApply ( ){ IllegalStateException failure=
        new IllegalStateException ( "Too many open files");RecordStorageEngine engine=storageEngineRule.getWith( fsRule.get( ),pageCacheRule.getPageCache (fsRule .get()) ,
                testDirectory.databaseLayout ( )
                ).databaseHealth ( databaseHealth ). transactionApplierTransformer( facade -> transactionApplierFacadeTransformer
                (facade,failure)
        ) . build () ;CommandsToApplycommandsToApply =mock

        (
        CommandsToApply
            .class); try{ engine.apply (commandsToApply
            ,TransactionApplicationMode . INTERNAL)
        ;
        fail ( "Exception expected" ) ;
        }
            catch( Exceptionexception ){assertSame( failure , Exceptions.
        rootCause

        (exception ) );}verify (databaseHealth ).panic ( any(
    Throwable

    . class ) );
            } privatestatic BatchTransactionApplierFacade transactionApplierFacadeTransformer (
    BatchTransactionApplierFacade
        facade , Exceptionfailure ){ return newFailingBatchTransactionApplierFacade
    (

    failure,
    facade ) ;}@ Test public
    void
        databasePanicIsRaisedWhenTxApplicationFails ( ) throwsThrowable{RecordStorageEngine
        engine = buildRecordStorageEngine () ; ExceptionapplicationError
        =executeFailingTransaction(engine ) ; ArgumentCaptor<Exception> captor=ArgumentCaptor .forClass
        (Exception . class);verify (databaseHealth).panic (captor
        . capture ( ));Throwableexception=
        captor . getValue ( ) ;
        if
            (exception instanceofKernelException){ assertThat(((KernelException)exception ). status(),is ( Status.
            General . UnknownError));exception=
        exception
        .getCause () ;} assertThat ( exception,
    is

    (applicationError) ) ; } @
    Test ( timeout=30_000 ) public
    void
        obtainCountsStoreResetterAfterFailedTransaction ( ) throwsThrowable{RecordStorageEngine
        engine = buildRecordStorageEngine () ; ExceptionapplicationError
        =executeFailingTransaction ( engine)

        ; assertNotNull ( applicationError);CountsTrackercountsStore=engine.testAccessNeoStores(
        )
        . getCounts (); // possible to obtain a resetting updater that internally has a write lock on the counts store try (CountsAccessor.Updater updater = countsStore
        .
            reset( 0 ))
        {
    assertNotNull

    (updater
    ) ; }}@
    Test
        public void mustFlushStoresWithGivenIOLimiter (){IOLimiter
        limiter = IOLimiter .UNLIMITED;FileSystemAbstractionfs=
        fsRule.get( ) ; AtomicReference <IOLimiter>observedLimiter=new
        AtomicReference < > ( ); PageCachepageCache=new DelegatingPageCache ( pageCacheRule
        .
            getPageCache(
            fs ) ){ @ Override public void flushAndForce
            (
                IOLimiterlimiter)throws IOException {super
                .flushAndForce(limiter ) ;observedLimiter
            .
        set(

        limiter ) ; }};RecordStorageEngine engine= storageEngineRule. getWith(fs,pageCache ,testDirectory.databaseLayout()
        ).build( ) ;engine

        .flushAndForce (limiter);assertThat( observedLimiter. get ( ),
    sameInstance

    (limiter
    ) ) ;}@
    Test
        public void shouldListAllStoreFiles (){RecordStorageEngine
        engine =buildRecordStorageEngine() ; final Collection<StoreFileMetadata>files=
        engine.listStorageFiles( ) ; Set<File>currentFiles=files. stream() .map(StoreFileMetadata ::file).collect (Collectors
        .
        toSet ( ) );// current engine files should contain everything except another count store file and label scan storeDatabaseLayoutdatabaseLayout=
        testDirectory.databaseLayout( ) ; Set<File>allPossibleFiles=
        databaseLayout.storeFiles( );allPossibleFiles.remove (databaseLayout
        .countStoreB() );allPossibleFiles.remove (databaseLayout

        .labelScanStore () ) ;assertEquals
    (

    currentFiles , allPossibleFiles);
    }
        private RecordStorageEngine
                buildRecordStorageEngine() {returnstorageEngineRule.getWith( fsRule.get( ),pageCacheRule.getPageCache (fsRule .get()) ,
                testDirectory.databaseLayout ( )
                ).databaseHealth(databaseHealth
    )

    . build ( ); } private static Exception executeFailingTransaction
    (
        RecordStorageEngine engine ) throws IOException{ Exception applicationError=
        new UnderlyingStorageException ( "No space left on device") ; TransactionToApplytxToApply
        =
        newTransactionThatFailsWith
            (applicationError); try{ engine.apply (txToApply
            ,TransactionApplicationMode . INTERNAL)
        ;
        fail ( "Exception expected" ) ;
        }
            catch( Exceptione ){assertSame( applicationError , Exceptions.
        rootCause
        ( e)
    )

    ; } return applicationError; } private static TransactionToApply newTransactionThatFailsWith
    (
        Exception error ) throwsIOException {TransactionRepresentationtransaction =mock
        (TransactionRepresentation .class);when (transaction.additionalHeader ( )).thenReturn (new
        byte
        [0 ] );// allow to build validated index updates but fail on actual tx applicationdoThrow ( error).when (transaction) .accept

        ( any ( ));longtxId=ThreadLocalRandom. current( ) .nextLong
        ( 0 , 1000 ); TransactionToApply txToApply=
        new TransactionToApply ( transaction ); FakeCommitmentcommitment =new FakeCommitment(txId , mock(
        TransactionIdStore.class) ) ;commitment
        .setHasExplicitIndexChanges(false ); txToApply .commitment
        ( commitment,
    txId

    ) ; return txToApply ; }
    private
        static class FailingBatchTransactionApplierFacade extendsBatchTransactionApplierFacade

        {private final Exceptionfailure ;FailingBatchTransactionApplierFacade ( Exception
        failure
            ,BatchTransactionApplier ... appliers)
            {super( appliers );
        this

        .failure
        = failure ;}@ Override public
        void
            close ()
        throws
    Exception

{
