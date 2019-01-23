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
import org.neo4j.kernel.impl.transaction.TransactionRepresentation;importorg
. neo4j.kernel.impl.transaction.log.FakeCommitment;importorg
. neo4j.kernel.impl.transaction.log.TransactionIdStore;importorg
. neo4j.kernel.internal.DatabaseHealth;importorg
. neo4j.storageengine.api.CommandsToApply;importorg
. neo4j.storageengine.api.StoreFileMetadata;importorg
. neo4j.storageengine.api.TransactionApplicationMode;importorg
. neo4j.test.rule.PageCacheRule;importorg
. neo4j.test.rule.RecordStorageEngineRule;importorg
. neo4j.test.rule.TestDirectory;importorg
. neo4j.test.rule.fs.EphemeralFileSystemRule;importstatic

org . hamcrest.Matchers.is;importstatic
org . hamcrest.Matchers.sameInstance;importstatic
org . junit.Assert.assertEquals;importstatic
org . junit.Assert.assertNotNull;importstatic
org . junit.Assert.assertSame;importstatic
org . junit.Assert.assertThat;importstatic
org . junit.Assert.assertTrue;importstatic
org . junit.Assert.fail;importstatic
org . mockito.ArgumentMatchers.any;importstatic
org . mockito.Mockito.doThrow;importstatic
org . mockito.Mockito.mock;importstatic
org . mockito.Mockito.verify;importstatic
org . mockito.Mockito.when;publicclass

RecordStorageEngineTest { private
final
    RecordStorageEngineRule storageEngineRule = new RecordStorageEngineRule ( );privatefinal
    EphemeralFileSystemRule fsRule = new EphemeralFileSystemRule ( );privatefinal
    PageCacheRule pageCacheRule = new PageCacheRule ( );privatefinal
    TestDirectory testDirectory = TestDirectory . testDirectory(fsRule) ; privatefinal
    DatabaseHealth databaseHealth = mock ( DatabaseHealth. class); @Rule

    publicRuleChain
    ruleChain = RuleChain . outerRule(fsRule) . around
            (pageCacheRule) . around
            (testDirectory) . around
            (storageEngineRule) ; privatestatic

    final Function < Optional<StoreType>,StoreType>assertIsPresentAndGet= optional -> { assertTrue
    (
        "Expected optional to be present", optional. isPresent()); returnoptional
        . get();};
    @Test

    (timeout= 30_000 ) public void
    shutdownRecordStorageEngineAfterFailedTransaction ( )throwsThrowable { RecordStorageEngine
    engine
        = buildRecordStorageEngine ( );ExceptionapplicationError
        = executeFailingTransaction ( engine) ; assertNotNull(
        applicationError) ; }@
    Test

    publicvoid
    panicOnExceptionDuringCommandsApply ( ){IllegalStateException
    failure
        = new IllegalStateException ( "Too many open files") ; RecordStorageEngineengine
        = storageEngineRule . getWith(fsRule. get(),pageCacheRule. getPageCache(fsRule. get()), testDirectory. databaseLayout()). databaseHealth
                (databaseHealth) . transactionApplierTransformer
                (facade-> transactionApplierFacadeTransformer ( facade, failure) ) . build
                ();CommandsToApplycommandsToApply
        = mock ( CommandsToApply. class); try{

        engine
        .
            apply(commandsToApply, TransactionApplicationMode. INTERNAL); fail(
            "Exception expected") ; }catch
        (
        Exception exception ) { assertSame
        (
            failure, Exceptions. rootCause(exception) ) ; }verify
        (

        databaseHealth) . panic(any( Throwable. class)) ; }private
    static

    BatchTransactionApplierFacade transactionApplierFacadeTransformer ( BatchTransactionApplierFacadefacade
            , Exceptionfailure ) { return
    new
        FailingBatchTransactionApplierFacade ( failure, facade) ; }@
    Test

    publicvoid
    databasePanicIsRaisedWhenTxApplicationFails ( )throwsThrowable { RecordStorageEngine
    engine
        = buildRecordStorageEngine ( );ExceptionapplicationError
        = executeFailingTransaction ( engine) ; ArgumentCaptor<
        Exception>captor= ArgumentCaptor . forClass(Exception. class); verify(
        databaseHealth) . panic(captor. capture()); Throwableexception
        = captor . getValue();if(
        exception instanceof KernelException ) { assertThat
        (
            (( KernelException)exception) .status(),is( Status. General.UnknownError)) ; exception=
            exception . getCause();}assertThat
        (
        exception, is( applicationError) ) ; }@
    Test

    (timeout= 30_000 ) public void
    obtainCountsStoreResetterAfterFailedTransaction ( )throwsThrowable { RecordStorageEngine
    engine
        = buildRecordStorageEngine ( );ExceptionapplicationError
        = executeFailingTransaction ( engine) ; assertNotNull(
        applicationError) ; CountsTrackercountsStore

        = engine . testAccessNeoStores().getCounts();// possible to obtain a resetting updater that internally has a write lock on the counts storetry
        (
        CountsAccessor . Updaterupdater= countsStore . reset(0) ) { assertNotNull
        (
            updater) ; }}
        @
    Test

    publicvoid
    mustFlushStoresWithGivenIOLimiter ( ){IOLimiter
    limiter
        = IOLimiter . UNLIMITED;FileSystemAbstractionfs
        = fsRule . get();AtomicReference<
        IOLimiter>observedLimiter= new AtomicReference < >();PageCachepageCache
        = new DelegatingPageCache ( pageCacheRule. getPageCache(fs) ) { @
        Override
            publicvoid
            flushAndForce ( IOLimiterlimiter ) throws IOException { super
            .
                flushAndForce(limiter) ; observedLimiter.
                set(limiter) ; }}
            ;
        RecordStorageEngineengine

        = storageEngineRule . getWith(fs, pageCache, testDirectory. databaseLayout()). build();engine.
        flushAndForce(limiter) ; assertThat(

        observedLimiter. get(),sameInstance( limiter) ) ; }@
    Test

    publicvoid
    shouldListAllStoreFiles ( ){RecordStorageEngine
    engine
        = buildRecordStorageEngine ( );finalCollection
        < StoreFileMetadata>files= engine . listStorageFiles();Set<
        File>currentFiles= files . stream().map(StoreFileMetadata:: file). collect(Collectors. toSet()); // current engine files should contain everything except another count store file and label scan storeDatabaseLayout
        databaseLayout
        = testDirectory . databaseLayout();Set<
        File>allPossibleFiles= databaseLayout . storeFiles();allPossibleFiles.
        remove(databaseLayout. countStoreB()); allPossibleFiles.
        remove(databaseLayout. labelScanStore()); assertEquals(

        currentFiles, allPossibleFiles) ; }private
    RecordStorageEngine

    buildRecordStorageEngine ( ){return
    storageEngineRule
        . getWith
                (fsRule. get(),pageCacheRule. getPageCache(fsRule. get()), testDirectory. databaseLayout()). databaseHealth
                (databaseHealth) . build
                ();}private
    static

    Exception executeFailingTransaction ( RecordStorageEngineengine ) throws IOException { Exception
    applicationError
        = new UnderlyingStorageException ( "No space left on device") ; TransactionToApplytxToApply
        = newTransactionThatFailsWith ( applicationError) ; try{
        engine
        .
            apply(txToApply, TransactionApplicationMode. INTERNAL); fail(
            "Exception expected") ; }catch
        (
        Exception e ) { assertSame
        (
            applicationError, Exceptions. rootCause(e) ) ; }return
        applicationError
        ; }private
    static

    TransactionToApply newTransactionThatFailsWith ( Exceptionerror ) throws IOException { TransactionRepresentation
    transaction
        = mock ( TransactionRepresentation. class); when(
        transaction. additionalHeader()). thenReturn(newbyte [ 0]); // allow to build validated index updates but fail on actual tx applicationdoThrow
        (
        error) . when(transaction) . accept(any( )); longtxId

        = ThreadLocalRandom . current().nextLong(0, 1000) ; TransactionToApplytxToApply
        = new TransactionToApply ( transaction) ; FakeCommitmentcommitment
        = new FakeCommitment ( txId, mock( TransactionIdStore. class)) ; commitment.
        setHasExplicitIndexChanges(false) ; txToApply.
        commitment(commitment, txId) ; returntxToApply
        ; }private
    static

    class FailingBatchTransactionApplierFacade extends BatchTransactionApplierFacade { private
    final
        Exception failure ; FailingBatchTransactionApplierFacade(

        Exceptionfailure , BatchTransactionApplier... appliers) { super
        (
            appliers) ; this.
            failure=failure ; }@
        Override

        publicvoid
        close ( )throwsException { throw
        failure
            ; }}
        }
    