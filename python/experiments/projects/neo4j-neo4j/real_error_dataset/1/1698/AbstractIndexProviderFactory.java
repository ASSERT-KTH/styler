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
package org.neo4j.kernel.impl.index.schema;

import java.nio.file.Path;

import org.neo4j.configuration.Config;
import org.neo4j.index.internal.gbptree.RecoveryCleanupWorkCollector;
import org.neo4j.internal.schema.IndexProviderDescriptor;
import org.neo4j.io.fs.FileSystemAbstraction;
import org.neo4j.io.pagecache.PageCache;
import org.neo4j.kernel.api.index.IndexProvider;
import org.neo4j.kernel.api.index.LoggingMonitor;
import org.neo4j.kernel.extension.ExtensionFactory;
import org.neo4j.kernel.extension.ExtensionType;
import org.neo4j.kernel.extension.context.ExtensionContext;
import org.neo4j.kernel.impl.factory.OperationalMode;
import org.neo4j.kernel.recovery.RecoveryExtension;
import org.neo4j.logging.Log;
import org.neo4j.logging.internal.LogService;
import org.neo4j.monitoring.Monitors;

@RecoveryExtension
public abstract class AbstractIndexProviderFactory extends ExtensionFactory<AbstractIndexProviderFactory.Dependencies>
{
    protected AbstractIndexProviderFactory( String key )
    {
        super( ExtensionType.DATABASE, key );
    }

    @Override
    public IndexProvider newInstance( ExtensionContext context, Dependencies dependencies )
    {
        PageCache pageCache = dependencies.pageCache();
        Path databaseDir = context.directory();
        FileSystemAbstraction fs = dependencies.fileSystem();
        Log log = dependencies.getLogService().getInternalLogProvider().getLog( loggingClass() );
        Monitors monitors = dependencies.monitors();
        String monitorTag = descriptor().toString();
        monitors.addMonitorListener( new LoggingMonitor( log ), monitorTag );
        Config config = dependencies.getConfig();
        OperationalMode operationalMode = context.dbmsInfo().operationalMode;
        RecoveryCleanupWorkCollector recoveryCleanupWorkCollector = dependencies.recoveryCleanupWorkCollector();
        return internalCreate( pageCache, databaseDir, fs, monitors, monitorTag, config, operationalMode, recoveryCleanupWorkCollector );
    }

    protected abstract Class<?> loggingClass();

    public abstract IndexProviderDescriptor descriptor();

    protected abstract IndexProvider internalCreate( PageCache pageCache, Path storeDir, FileSystemAbstraction fs,
            Monitors monitors, String monitorTag, Config config, OperationalMode operationalMode,
            RecoveryCleanupWorkCollector recoveryCleanupWorkCollector );

    public interface Dependencies
    {
        PageCache pageCache();

        FileSystemAbstraction fileSystem();

        LogService getLogService();

        Monitors monitors();

        Config getConfig();

        RecoveryCleanupWorkCollector recoveryCleanupWorkCollector();
    }
}
