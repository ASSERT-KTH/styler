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
package org.neo4j.kernel.impl.query;

import org.neo4j.internal.kernel.api.security.SecurityContext;
import org.neo4j.kernel.GraphDatabaseQueryService;
import org.neo4j.kernel.api.KernelTransaction;
import org.neo4j.kernel.api.ResourceTracker;
import org.neo4j.kernel.api.Statement;
import org.neo4j.kernel.api.query.ExecutingQuery;
import org.neo4j.kernel.database.NamedDatabaseId;
import org.neo4j.kernel.impl.coreapi.InternalTransaction;
import org.neo4j.kernel.impl.query.statistic.StatisticProvider;
import org.neo4j.values.ValueMapper;

public interface TransactionalContext
{
    ValueMapper<Object> valueMapper();

    ExecutingQuery executingQuery();

    KernelTransaction kernelTransaction();

    InternalTransaction transaction();

    boolean isTopLevelTx();

    /**
     * This should be called once the query is finished, either successfully or not.
     * Should be called from the same thread the query was executing in.
     */
    void close();

    /**
     * Close and rollback transaction context. For cases when exception occurred during query execution and owning transaction should be rolledback
     */
    void rollback();

    /**
     * This is used to terminate a currently running query. Can be called from any thread. Will roll back the current
     * transaction if it is still open.
     */
    void terminate();

    long commitAndRestartTx();

    TransactionalContext getOrBeginNewIfClosed();

    boolean isOpen();

    GraphDatabaseQueryService graph();

    NamedDatabaseId databaseId();

    Statement statement();

    SecurityContext securityContext();

    StatisticProvider kernelStatisticProvider();

    KernelTransaction.Revertable restrictCurrentTransaction( SecurityContext context );

    ResourceTracker resourceTracker();
}
