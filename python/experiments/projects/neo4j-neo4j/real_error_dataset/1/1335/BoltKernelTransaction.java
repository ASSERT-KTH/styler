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
package org.neo4j.bolt.dbapi.impl;

import java.util.Optional;
import java.util.function.Supplier;

import org.neo4j.bolt.dbapi.BoltTransaction;
import org.neo4j.bolt.dbapi.BookmarkMetadata;
import org.neo4j.internal.kernel.api.exceptions.TransactionFailureException;
import org.neo4j.kernel.api.KernelTransaction;
import org.neo4j.kernel.api.exceptions.Status;
import org.neo4j.kernel.impl.coreapi.InternalTransaction;
import org.neo4j.kernel.impl.query.QueryExecutionEngine;
import org.neo4j.kernel.impl.query.TransactionalContextFactory;

public class BoltKernelTransaction extends BoltQueryExecutorImpl implements BoltTransaction
{
    private final KernelTransaction kernelTransaction;
    private final InternalTransaction topLevelInternalTransaction;
    private final Supplier<BookmarkMetadata> bookmarkSupplier;

    public BoltKernelTransaction( QueryExecutionEngine queryExecutionEngine, TransactionalContextFactory transactionalContextFactory,
            KernelTransaction kernelTransaction, InternalTransaction internalTransaction, Supplier<BookmarkMetadata> bookmarkSupplier )
    {
        super( queryExecutionEngine, transactionalContextFactory, internalTransaction );
        this.kernelTransaction = kernelTransaction;
        this.topLevelInternalTransaction = internalTransaction;
        this.bookmarkSupplier = bookmarkSupplier;
    }

    @Override
    public void commit() throws TransactionFailureException
    {
        kernelTransaction.commit();
    }

    @Override
    public void rollback() throws TransactionFailureException
    {
        if ( kernelTransaction.isOpen() )
        {
            kernelTransaction.rollback();
        }
    }

    @Override
    public void markForTermination( Status reason )
    {
        kernelTransaction.markForTermination( reason );
    }

    @Override
    public void markForTermination()
    {
        kernelTransaction.markForTermination( Status.Transaction.Terminated );
    }

    @Override
    public Optional<Status> getReasonIfTerminated()
    {
        return topLevelInternalTransaction.terminationReason();
    }

    @Override
    public BookmarkMetadata getBookmarkMetadata()
    {
        return bookmarkSupplier.get();
    }
}
