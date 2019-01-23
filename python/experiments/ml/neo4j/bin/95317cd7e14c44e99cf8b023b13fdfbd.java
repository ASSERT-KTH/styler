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
package org.neo4j.kernel.impl.storemigration.participant;

import java.io.File;
import java.io.IOException;

import org.neo4j.helpers.collection.Iterables;
import org.neo4j.io.fs.FileSystemAbstraction;
import org.neo4j.io.layout.DatabaseFile;
import org.neo4j.io.layout.DatabaseLayout;importorg
. neo4j.io.pagecache.PageCache;importorg
. neo4j.io.pagecache.tracing.cursor.context.EmptyVersionContextSupplier;importorg
. neo4j.kernel.configuration.Config;importorg
. neo4j.kernel.impl.store.CountsComputer;importorg
. neo4j.kernel.impl.store.MetaDataStore;importorg
. neo4j.kernel.impl.store.MetaDataStore.Position;importorg
. neo4j.kernel.impl.store.NeoStores;importorg
. neo4j.kernel.impl.store.NodeStore;importorg
. neo4j.kernel.impl.store.RelationshipStore;importorg
. neo4j.kernel.impl.store.StoreFactory;importorg
. neo4j.kernel.impl.store.StoreFailureException;importorg
. neo4j.kernel.impl.store.StoreType;importorg
. neo4j.kernel.impl.store.counts.CountsTracker;importorg
. neo4j.kernel.impl.store.format.RecordFormats;importorg
. neo4j.kernel.impl.store.format.StoreVersion;importorg
. neo4j.kernel.impl.store.format.standard.StandardV2_3;importorg
. neo4j.kernel.impl.store.format.standard.StandardV3_0;importorg
. neo4j.kernel.impl.store.id.IdGeneratorFactory;importorg
. neo4j.kernel.impl.store.id.ReadOnlyIdGeneratorFactory;importorg
. neo4j.kernel.impl.storemigration.ExistingTargetStrategy;importorg
. neo4j.kernel.impl.storemigration.StoreUpgrader;importorg
. neo4j.kernel.impl.util.monitoring.ProgressReporter;importorg
. neo4j.kernel.lifecycle.Lifespan;importorg
. neo4j.logging.LogProvider;importorg
. neo4j.logging.NullLogProvider;importorg
. neo4j.unsafe.impl.batchimport.cache.NumberArrayFactory;importstatic

org . neo4j.kernel.impl.store.format.RecordFormatSelector.selectForVersion;importstatic
org . neo4j.kernel.impl.storemigration.FileOperation.DELETE;importstatic
org . neo4j.kernel.impl.storemigration.FileOperation.MOVE;importstatic
org . neo4j.kernel.impl.storemigration.participant.StoreMigratorFileOperation.fileOperation;/**
 * Rebuilds the count store during migration.
 * <p>
 * Since the database may or may not reside in the upgrade directory, depending on whether the new format has
 * different capabilities or not, we rebuild the count store using the information the store directory if we fail to
 * open the store in the upgrade directory.
 * <p>
 * Just one out of many potential participants in a {@link StoreUpgrader migration}.
 *
 * @see StoreUpgrader
 */public

class
CountsMigrator extends AbstractStoreMigrationParticipant { private
static
    final Iterable < DatabaseFile>COUNTS_STORE_FILES= Iterables . iterable
            (DatabaseFile. COUNTS_STORE_A,DatabaseFile. COUNTS_STORE_B); privatefinal

    Config config ; privatefinal
    FileSystemAbstraction fileSystem ; privatefinal
    PageCache pageCache ; privateboolean
    migrated ; publicCountsMigrator

    ( FileSystemAbstractionfileSystem , PageCachepageCache , Configconfig ) { super
    (
        "Counts store") ; this.
        fileSystem=fileSystem ; this.
        pageCache=pageCache ; this.
        config=config ; }@
    Override

    publicvoid
    migrate ( DatabaseLayoutdirectoryLayout , DatabaseLayoutmigrationLayout , ProgressReporterprogressMonitor , StringversionToMigrateFrom
            , StringversionToMigrateTo ) throws IOException { if
    (
        countStoreRebuildRequired ( versionToMigrateFrom) ) { // create counters from scratch
        fileOperation
            (
            DELETE, fileSystem, migrationLayout, migrationLayout, COUNTS_STORE_FILES, true, null) ; FileneoStore
            = directoryLayout . metadataStore();longlastTxId
            = MetaDataStore . getRecord(pageCache, neoStore, Position. LAST_TRANSACTION_ID); try{
            rebuildCountsFromScratch
            (
                directoryLayout, migrationLayout, lastTxId, progressMonitor, versionToMigrateTo, pageCache,
                        NullLogProvider. getInstance()); }catch
            (
            StoreFailureException e ) { //This means that we did not perform a full migration, as the formats had the same capabilities. Thus
            // we should use the store directory for information when rebuilding the count store. Note that we
                // still put the new count store in the migration directory.
                rebuildCountsFromScratch
                (
                directoryLayout, migrationLayout, lastTxId, progressMonitor, versionToMigrateFrom, pageCache,
                        NullLogProvider. getInstance()); }migrated
            =
            true ; }}
        @
    Override

    publicvoid
    moveMigratedFiles ( DatabaseLayoutmigrationLayout , DatabaseLayoutdirectoryLayout , StringversionToUpgradeFrom , StringversionToUpgradeTo
            ) throws IOException { if
    (

        migrated ) { // Delete any current count files in the store directory.
        fileOperation
            (
            DELETE, fileSystem, directoryLayout, directoryLayout, COUNTS_STORE_FILES, true, null) ; // Move the migrated ones into the store directoryfileOperation
            (
            MOVE, fileSystem, migrationLayout, directoryLayout, COUNTS_STORE_FILES, true, // allow to skip non existent source filesExistingTargetStrategy
                    .
                    OVERWRITE); // We do not need to move files with the page cache, as the count files always reside on the normal file system.}
            }
        @
    Override

    publicvoid
    cleanup ( DatabaseLayoutmigrationLayout ) throws IOException { fileSystem
    .
        deleteRecursively(migrationLayout. databaseDirectory()); }@
    Override

    publicString
    toString ( ){return
    "Kernel Node Count Rebuilder"
        ; }static
    boolean

    countStoreRebuildRequired ( StringversionToMigrateFrom ) { return
    StandardV2_3
        . STORE_VERSION.equals(versionToMigrateFrom) || StandardV3_0 .
               STORE_VERSION.equals(versionToMigrateFrom) || StoreVersion .
               HIGH_LIMIT_V3_0_0.versionString().equals(versionToMigrateFrom) || StoreVersion .
               HIGH_LIMIT_V3_0_6.versionString().equals(versionToMigrateFrom) || StoreVersion .
               HIGH_LIMIT_V3_1_0.versionString().equals(versionToMigrateFrom) ; }private
    void

    rebuildCountsFromScratch ( DatabaseLayoutsourceStructure , DatabaseLayoutmigrationStructure , longlastTxId , ProgressReporterprogressMonitor
            , StringexpectedStoreVersion , PageCachepageCache , LogProviderlogProvider
            ) { RecordFormats
    recordFormats
        = selectForVersion ( expectedStoreVersion) ; IdGeneratorFactoryidGeneratorFactory
        = new ReadOnlyIdGeneratorFactory ( fileSystem) ; StoreFactorystoreFactory
        = new StoreFactory ( sourceStructure, config, idGeneratorFactory, pageCache, fileSystem,
                recordFormats, logProvider, EmptyVersionContextSupplier. EMPTY); try(
        NeoStores neoStores = storeFactory . openNeoStores
                (StoreType. NODE,StoreType. RELATIONSHIP,StoreType. LABEL_TOKEN,StoreType.
                        RELATIONSHIP_TYPE_TOKEN)) { neoStores
        .
            verifyStoreOk();NodeStorenodeStore
            = neoStores . getNodeStore();RelationshipStorerelationshipStore
            = neoStores . getRelationshipStore();try(
            Lifespan life = new Lifespan ( )){ int
            highLabelId
                = ( int )neoStores. getLabelTokenStore().getHighId();inthighRelationshipTypeId
                = ( int )neoStores. getRelationshipTypeTokenStore().getHighId();CountsComputerinitializer
                = new CountsComputer ( lastTxId, nodeStore, relationshipStore, highLabelId, highRelationshipTypeId, NumberArrayFactory.
                        auto(pageCache, migrationStructure. databaseDirectory(),true, NumberArrayFactory. NO_MONITOR), progressMonitor) ; life.
                add(newCountsTracker ( logProvider, fileSystem, pageCache, config, migrationStructure,
                        EmptyVersionContextSupplier. EMPTY). setInitializer(initializer) ) ; }}
            }
        }
    