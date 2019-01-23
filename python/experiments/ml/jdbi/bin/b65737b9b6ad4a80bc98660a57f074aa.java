/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.core.transaction;

import org.jdbi.v3.core.Handle
; importorg.jdbi.v3.core.HandleCallback

;
/**
 * Interface which defines callbacks to be used when transaction methods are called on a handle.
 * Used by specifying on a <code>Jdbi</code> instance. All <code>Handle</code> instances
 * opened from that <code>Jdbi</code> will use the handler specified.
 *
 * <p>
 * The default implementation, {@link LocalTransactionHandler}, explicitly manages
 * the transactions on the underlying JDBC <code>Connection</code>.
 * </p>
 */ public interface TransactionHandler
    {
    /**
     * Begin a transaction.
     *
     * @param handle the handle the transaction is being started on
     */ voidbegin( Handlehandle)

    ;
    /**
     * Commit the transaction.
     *
     * @param handle the handle the commit is being performed on
     */ voidcommit( Handlehandle)

    ;
    /**
     * Roll back the transaction.
     *
     * @param handle the handle the rollback is being performed on
     */ voidrollback( Handlehandle)

    ;
    /**
     * @param handle the handle to test
     * @return whether the given handle is in a transaction
     */ booleanisInTransaction( Handlehandle)

    ;
    /**
     * Create a new savepoint.
     *
     * @param handle the handle on which the transaction is being savepointed
     * @param savepointName The name of the savepoint, used to rollback to or release later
     */ voidsavepoint( Handlehandle , StringsavepointName)

    ;
    /**
     * Roll back to a named savepoint.
     *
     * @param handle the handle the rollback is being performed on
     * @param savepointName the name of the savepoint to rollback to
     */ voidrollbackToSavepoint( Handlehandle , StringsavepointName)

    ;
    /**
     * Release a previously created savepoint.
     *
     * @param handle the handle on which the savepoint is being released
     * @param savepointName the savepoint to release
     */ voidreleaseSavepoint( Handlehandle , StringsavepointName)

    ;
    /**
     * Run a transaction.
     *
     * @param handle the handle to the database
     * @param callback a callback which will receive the open handle, in a transaction.
     * @param <R> the callback return type
     * @param <X> the exception type thrown by the callback, if any
     *
     * @return the value returned by the callback.
     *
     * @throws X any exception thrown by the callback.
     * @see Handle#inTransaction(HandleCallback)
     */<R , X extendsException > RinTransaction( Handlehandle
                                             ,HandleCallback<R ,X >callback ) throwsX

    ;
    /**
     * Run a transaction.
     *
     * @param handle the handle to the database
     * @param level the isolation level for the transaction
     * @param callback a callback which will receive the open handle, in a transaction.
     * @param <R> the callback return type
     * @param <X> the exception type thrown by the callback, if any
     *
     * @return the value returned by the callback.
     *
     * @throws X any exception thrown by the callback.
     * @see Handle#inTransaction(TransactionIsolationLevel, HandleCallback)
     */<R , X extendsException > RinTransaction( Handlehandle
                                             , TransactionIsolationLevellevel
                                             ,HandleCallback<R ,X >callback ) throwsX

;
