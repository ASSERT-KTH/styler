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
package org.neo4j.kernel.impl.index.schema;

import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import org.neo4j.gis.

spatial.index.curves.SpaceFillingCurveConfiguration;importorg
. neo4j.io.fs.FileSystemAbstraction;importorg
. neo4j.kernel.api.index.IndexDirectoryStructure;importorg
. neo4j.kernel.api.schema.SchemaDescriptorFactory;importorg
. neo4j.kernel.impl.index.schema.config.IndexSpecificSpaceFillingCurveSettingsCache;importorg
. neo4j.storageengine.api.schema.IndexDescriptorFactory;importorg
. neo4j.storageengine.api.schema.StoreIndexDescriptor;importorg
. neo4j.test.rule.PageCacheAndDependenciesRule;importorg
. neo4j.test.rule.fs.DefaultFileSystemRule;importstatic

org . junit.Assert.assertFalse;importstatic
org . mockito.Mockito.mock;importstatic
org . neo4j.index.internal.gbptree.RecoveryCleanupWorkCollector.immediate;importstatic
org . neo4j.kernel.api.index.IndexProvider.Monitor.EMPTY;publicclass

GenericNativeIndexAccessorTest { @
Rule
    publicfinal
    PageCacheAndDependenciesRule storage = new PageCacheAndDependenciesRule ( ).with(newDefaultFileSystemRule ( )); @Test

    publicvoid
    dropShouldDeleteEntireIndexFolder ( ){// given
    File
        root
        = storage . directory().directory("root") ; IndexDirectoryStructuredirectoryStructure
        = IndexDirectoryStructure . directoriesByProvider(root) . forProvider(GenericNativeIndexProvider. DESCRIPTOR); longindexId
        = 8 ; FileindexDirectory
        = directoryStructure . directoryForIndex(indexId) ; FileindexFile
        = new File ( indexDirectory, "my-index") ; StoreIndexDescriptordescriptor
        = IndexDescriptorFactory . forSchema(SchemaDescriptorFactory. forLabel(1, 1) ) . withId(indexId) ; IndexSpecificSpaceFillingCurveSettingsCachespatialSettings
        = mock ( IndexSpecificSpaceFillingCurveSettingsCache. class); FileSystemAbstractionfs
        = storage . fileSystem();GenericNativeIndexAccessoraccessor
        = new GenericNativeIndexAccessor ( storage. pageCache(),fs, indexFile, newGenericLayout ( 1, spatialSettings) , immediate(
                ),EMPTY, descriptor, spatialSettings, directoryStructure, mock( SpaceFillingCurveConfiguration. class)) ; // whenaccessor

        .
        drop();// thenassertFalse

        (
        fs. fileExists(indexDirectory) ) ; }}
    