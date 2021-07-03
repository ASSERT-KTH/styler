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
package org.neo4j.cypher.internal.javacompat;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.neo4j.common.DependencyResolver;
import org.neo4j.configuration.Config;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.security.URLAccessRule;
import org.neo4j.graphdb.security.URLAccessValidationError;
import org.neo4j.internal.kernel.api.connectioninfo.ClientConnectionInfo;
import org.neo4j.internal.kernel.api.security.LoginContext;
import org.neo4j.kernel.GraphDatabaseQueryService;
import org.neo4j.kernel.api.KernelTransaction;
import org.neo4j.kernel.impl.coreapi.InternalTransaction;
import org.neo4j.kernel.impl.factory.GraphDatabaseFacade;

public class GraphDatabaseCypherService implements GraphDatabaseQueryService
{
    private final GraphDatabaseFacade graph;
    private final URLAccessRule urlAccessRule;
    private final Config config;

    public GraphDatabaseCypherService( GraphDatabaseService graph )
    {
        this.graph = (GraphDatabaseFacade) graph;
        DependencyResolver dependencyResolver = getDependencyResolver();
        this.urlAccessRule = dependencyResolver.resolveDependency( URLAccessRule.class );
        this.config = dependencyResolver.resolveDependency( Config.class );
    }

    @Override
    public DependencyResolver getDependencyResolver()
    {
        return graph.getDependencyResolver();
    }

    @Override
    public InternalTransaction beginTransaction( KernelTransaction.Type type, LoginContext loginContext )
    {
        return graph.beginTransaction( type, loginContext );
    }

    @Override
    public InternalTransaction beginTransaction( KernelTransaction.Type type, LoginContext loginContext, ClientConnectionInfo connectionInfo )
    {
        return graph.beginTransaction( type, loginContext, connectionInfo );
    }

    @Override
    public InternalTransaction beginTransaction( KernelTransaction.Type type, LoginContext loginContext, ClientConnectionInfo connectionInfo,
            long timeout, TimeUnit unit )
    {
        return graph.beginTransaction( type, loginContext, connectionInfo, timeout, unit );
    }

    @Override
    public URL validateURLAccess( URL url ) throws URLAccessValidationError
    {
        return urlAccessRule.validate( config, url );
    }

    // This provides backwards compatibility to the older API for places that cannot (yet) stop using it.
    // TODO: Remove this when possible (remove RULE, remove older compilers)
    public GraphDatabaseFacade getGraphDatabaseService()
    {
        return graph;
    }
}
