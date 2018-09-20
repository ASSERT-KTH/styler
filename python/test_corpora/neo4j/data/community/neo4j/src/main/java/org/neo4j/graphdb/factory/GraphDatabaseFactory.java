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
package org.neo4j.graphdb.factory;

import java.io.File;
import java.util.Map;
import javax.annotation.Nonnull;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.facade.GraphDatabaseFacadeFactory;
import org.neo4j.graphdb.factory.module.edition.CommunityEditionModule;
import org.neo4j.graphdb.security.URLAccessRule;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.configuration.Settings;
import org.neo4j.kernel.impl.factory.DatabaseInfo;
import org.neo4j.kernel.impl.factory.Edition;
import org.neo4j.kernel.monitoring.Monitors;
import org.neo4j.logging.LogProvider;

/**
 * Creates a {@link org.neo4j.graphdb.GraphDatabaseService} with Community Edition features.
 * <p>
 * Use {@link #newEmbeddedDatabase(File)} or
 * {@link #newEmbeddedDatabaseBuilder(File)} to create a database instance.
 * <p>
 * <strong>Note:</strong> If you are using the Enterprise Edition of Neo4j in embedded mode, you have to create your
 * database with the <a href="EnterpriseGraphDatabaseFactory.html">{@code EnterpriseGraphDatabaseFactory}</a>
 * to enable the Enterprise Edition features, or the
 * <a href="HighlyAvailableGraphDatabaseFactory.html">{@code HighlyAvailableGraphDatabaseFactory}</a> for the
 * Enterprise and High-Availability features. There is no factory for the Causal Clustering features, because it is
 * currently not possible to run a causal cluster in embedded mode.
 */
public class GraphDatabaseFactory
{
    private final GraphDatabaseFactoryState state;

    public GraphDatabaseFactory()
    {
        this( new GraphDatabaseFactoryState() );
    }

    protected GraphDatabaseFactory( GraphDatabaseFactoryState state )
    {
        this.state = state;
    }

    protected GraphDatabaseFactoryState getCurrentState()
    {
        return state;
    }

    protected GraphDatabaseFactoryState getStateCopy()
    {
        return new GraphDatabaseFactoryState( getCurrentState() );
    }

    /**
     * @param storeDir desired embedded database store dir
     * @deprecated Currently {@code storeDir} points to a directory where the particular database is located.
     * That is a subject to change in a future major release.
     */
    @Deprecated
    public GraphDatabaseService newEmbeddedDatabase( File storeDir )
    {
        return newEmbeddedDatabaseBuilder( storeDir ).newGraphDatabase();
    }

    /**
     * @param storeDir desired embedded database store dir
     * @deprecated Currently {@code storeDir} points to a directory where the particular database is located.
     * That is a subject to change in a future major release.
     */
    @Deprecated
    public GraphDatabaseBuilder newEmbeddedDatabaseBuilder( File storeDir )
    {
        final GraphDatabaseFactoryState state = getStateCopy();
        GraphDatabaseBuilder.DatabaseCreator creator = createDatabaseCreator( storeDir, state );
        GraphDatabaseBuilder builder = createGraphDatabaseBuilder( creator );
        configure( builder );
        return builder;
    }

    protected GraphDatabaseBuilder createGraphDatabaseBuilder( GraphDatabaseBuilder.DatabaseCreator creator )
    {
        return new GraphDatabaseBuilder( creator );
    }

    protected GraphDatabaseBuilder.DatabaseCreator createDatabaseCreator( final File storeDir, final GraphDatabaseFactoryState state )
    {
        return new EmbeddedDatabaseCreator( storeDir, state );
    }

    protected void configure( GraphDatabaseBuilder builder )
    {
        // Let the default configuration pass through.
    }

    /**
     * See {@link #newDatabase(File, Config, GraphDatabaseFacadeFactory.Dependencies)} instead.
     */
    @Deprecated
    protected GraphDatabaseService newDatabase( File storeDir, Map<String,String> settings,
                                                GraphDatabaseFacadeFactory.Dependencies dependencies )
    {
        return newDatabase( storeDir, Config.defaults( settings ), dependencies );
    }

    protected GraphDatabaseService newEmbeddedDatabase( File storeDir, Config config,
                                                        GraphDatabaseFacadeFactory.Dependencies dependencies )
    {
        return GraphDatabaseFactory.this.newDatabase( storeDir, config, dependencies );
    }

    protected GraphDatabaseService newDatabase( File storeDir, Config config,
                                                GraphDatabaseFacadeFactory.Dependencies dependencies )
    {
        File absoluteStoreDir = storeDir.getAbsoluteFile();
        File databasesRoot = absoluteStoreDir.getParentFile();
        config.augment( GraphDatabaseSettings.ephemeral, Settings.FALSE );
        config.augment( GraphDatabaseSettings.active_database, absoluteStoreDir.getName() );
        config.augment( GraphDatabaseSettings.databases_root_path, databasesRoot.getAbsolutePath() );
        return getGraphDatabaseFacadeFactory().newFacade( databasesRoot, config, dependencies );
    }

    protected GraphDatabaseFacadeFactory getGraphDatabaseFacadeFactory()
    {
        return new GraphDatabaseFacadeFactory( DatabaseInfo.COMMUNITY, CommunityEditionModule::new );
    }

    public GraphDatabaseFactory addURLAccessRule( String protocol, URLAccessRule rule )
    {
        getCurrentState().addURLAccessRule( protocol, rule );
        return this;
    }

    public GraphDatabaseFactory setUserLogProvider( LogProvider userLogProvider )
    {
        getCurrentState().setUserLogProvider( userLogProvider );
        return this;
    }

    public GraphDatabaseFactory setMonitors( Monitors monitors )
    {
        getCurrentState().setMonitors( monitors );
        return this;
    }

    public String getEdition()
    {
        return Edition.community.toString();
    }

    private class EmbeddedDatabaseCreator implements GraphDatabaseBuilder.DatabaseCreator
    {
        private final File storeDir;
        private final GraphDatabaseFactoryState state;

        EmbeddedDatabaseCreator( File storeDir, GraphDatabaseFactoryState state )
        {
            this.storeDir = storeDir;
            this.state = state;
        }

        @Override
        public GraphDatabaseService newDatabase( @Nonnull Config config )
        {
            return newEmbeddedDatabase( storeDir, config, state.databaseDependencies() );
        }
    }
}
