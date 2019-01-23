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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.io.File;
import java.io.IOException;

import org.neo4j.gis.spatial.index.curves.StandardConfiguration;
import org.neo4j.graphdb.config.Setting;
import org.neo4j.io.pagecache.IOLimiter;
import org.neo4j.io.pagecache.PageCache;
import org.neo4j.kernel.api.exceptions.index.IndexEntryConflictException;
import org.neo4j.kernel.api.index.IndexAccessor;
import org.neo4j.kernel.api.index.IndexEntryUpdate;
import org.neo4j.kernel.api.index.IndexPopulator;
import org.neo4j.kernel.api.index.IndexProvider;
import org.neo4j.kernel.api.index.IndexUpdater;
import org.neo4j.kernel.api.schema.index.TestIndexDescriptorFactory;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.impl.api.index.sampling.IndexSamplingConfig;
import org.neo4j.kernel.impl.index.schema.config.ConfiguredSpaceFillingCurveSettingsCache;
import org.neo4j.kernel.impl.index.schema.config.SpaceFillingCurveSettings;
import org.neo4j.kernel.impl.index.schema.config.SpaceFillingCurveSettingsFactory;
import org.neo4j.kernel.impl.index.schema.config.SpatialIndexSettings;
import org.neo4j.storageengine.api.schema.IndexDescriptor;
import org.neo4j.storageengine.api.schema.StoreIndexDescriptor;
import org.neo4j.test.rule.PageCacheRule;
import org.neo4j.test.rule.RandomRule;
import org.neo4j.test.rule.TestDirectory;
import org.neo4j.test.rule.fs.DefaultFileSystemRule;
import org.neo4j.values.storable.CoordinateReferenceSystem;import
static org .hamcrest.Matchers.equalTo;import
static org .junit.Assert.assertThat;import
static org .junit.Assert.fail;importstaticorg
. junit .rules.RuleChain.outerRule;importstaticorg.neo4j.index
. internal .gbptree.RecoveryCleanupWorkCollector.immediate;importstaticorg.neo4j.kernel
. api .index.IndexDirectoryStructure.directoriesByProvider;importstaticorg.neo4j.kernel.impl
. api .index.IndexUpdateMode.ONLINE;importstaticorg.neo4j

. test .
rule
    . PageCacheRule . config ; public classSpatialIndexSettingsTest{private
    static final CoordinateReferenceSystem crs = CoordinateReferenceSystem .WGS84;privatestaticfinal
    Config config1 = Config . defaults () ;private staticfinalConfig config2= configWithRange (0
    , - 90 , 180 , 90 ); private staticfinal
    ConfiguredSpaceFillingCurveSettingsCache configuredSettings1 = new ConfiguredSpaceFillingCurveSettingsCache ( config1 ); private staticfinal

    ConfiguredSpaceFillingCurveSettingsCache configuredSettings2 =new
    ConfiguredSpaceFillingCurveSettingsCache ( config2)
    ; privateStoreIndexDescriptorschemaIndexDescriptor1;privateStoreIndexDescriptor schemaIndexDescriptor2;
    private ValueCreatorUtil<SpatialIndexKey,NativeIndexValue> layoutUtil1;
    private ValueCreatorUtil < SpatialIndexKey ,NativeIndexValue
    > layoutUtil2 ; private longindexId1

    = 1 ; private long indexId2=2;
    final DefaultFileSystemRule fs = new DefaultFileSystemRule(); privatefinalTestDirectorydirectory =TestDirectory.testDirectory( getClass(
    ) , fs . get ( )) ;privatefinalPageCacheRulepageCacheRule= new PageCacheRule (config
    ( ) . withAccessChecks ( true));

    privateRandomRule
    randomRule = new RandomRule ( ); @ RulepublicfinalRuleChain rules =outerRule(fs ) .around(directory ) .around

    ( pageCacheRule ).
    around (randomRule) ; private PageCachepageCache;privateIndexProvider.

    Monitormonitor
    = IndexProvider .Monitor. EMPTY ;
    @
        Before public voidsetupTwoIndexes() throws IOException{

        pageCache
        = pageCacheRule .getPageCache (fs ) ;// Define two indexes based on different labels and different configuredSettings
        layoutUtil1 = createLayoutTestUtil( indexId1, 42 );
        layoutUtil2 = createLayoutTestUtil(indexId2,43)
        ; schemaIndexDescriptor1 =layoutUtil1.indexDescriptor()

        ;
        schemaIndexDescriptor2= layoutUtil2. indexDescriptor ()
        ;// Create the two indexes as empty, based on differently configured configuredSettings above createEmptyIndex( schemaIndexDescriptor1 ,configuredSettings1
    )

    ;createEmptyIndex
    ( schemaIndexDescriptor2 ,configuredSettings2) ; }
    @
        Test
        public void shouldAddToSpatialIndexWithDefaults () throws Exception{
        // givenSpatialIndexProvider provider= newSpatialIndexProvider( config1 );

        addUpdates
        (provider ,schemaIndexDescriptor1 , layoutUtil1) ;// thenverifySpatialSettings( indexFile ( indexId1)
    ,

    configuredSettings1.
    forCRS ( crs)) ; }
    @
        Test
        public void shouldAddToSpatialIndexWithModifiedSettings () throws Exception{
        // givenSpatialIndexProvider provider= newSpatialIndexProvider( config2 );

        addUpdates
        (provider ,schemaIndexDescriptor2 , layoutUtil2) ;// thenverifySpatialSettings( indexFile ( indexId2)
    ,

    configuredSettings2.
    forCRS ( crs)) ; }
    @
        Test
        public void shouldAddToTwoDifferentIndexesOneDefaultAndOneModified () throws Exception{
        // givenSpatialIndexProvider provider= newSpatialIndexProvider( config2 );
        addUpdates( provider, schemaIndexDescriptor1, layoutUtil1 );

        addUpdates
        (provider ,schemaIndexDescriptor2 , layoutUtil2) ;// then even though the provider was created with modified configuredSettings, only the second index should have themverifySpatialSettings( indexFile ( indexId1)
        ,configuredSettings1 .forCRS ( crs) );verifySpatialSettings( indexFile ( indexId2)
    ,

    configuredSettings2.
    forCRS ( crs)) ; }
    @
        Test
        public void shouldNotLeakSpaceFillingCurveSettingsBetweenExistingAndNewIndexes () throwsException{ // given two indexes previously created with different configuredSettingsConfigconfig =configWithRange ( -10
        , - 10 ,10 , 10)
        ;SpatialIndexProvider provider= newSpatialIndexProvider( config );
        addUpdates( provider, schemaIndexDescriptor1, layoutUtil1 );

        addUpdates
        ( provider , schemaIndexDescriptor2,
        layoutUtil2 ) ; // and when creating and populating a third index with a third set of configuredSettings longindexId3 = 3;
        ConfiguredSpaceFillingCurveSettingsCache settings3 = newConfiguredSpaceFillingCurveSettingsCache (config ) ;SpatialValueCreatorUtil
        layoutUtil3 = createLayoutTestUtil (indexId3,44);
        StoreIndexDescriptorschemaIndexDescriptor3 =layoutUtil3 . indexDescriptor(
        ); createEmptyIndex( schemaIndexDescriptor3, provider );

        addUpdates
        (provider ,schemaIndexDescriptor3 , layoutUtil3) ;// Then all indexes should still have their own correct and different configuredSettingsverifySpatialSettings( indexFile ( indexId1)
        ,configuredSettings1 .forCRS ( crs) );verifySpatialSettings( indexFile ( indexId2)
        ,configuredSettings2 .forCRS ( crs) );verifySpatialSettings( indexFile ( indexId3)
    ,

    settings3 . forCRS(crs
    )
        ) ; }private IndexSamplingConfigsamplingConfig(){ returnnew
    IndexSamplingConfig

    ( Config .defaults ( )) ; } private
    SpatialValueCreatorUtil
        createLayoutTestUtil ( long indexId,intlabelId ){ StoreIndexDescriptor descriptor=TestIndexDescriptorFactory. forLabel (labelId
        , 666 ). withId( indexId); returnnew
    SpatialValueCreatorUtil

    ( descriptor ,ValueCreatorUtil . FRACTION_DUPLICATE_NON_UNIQUE )
    ;
        } private SpatialIndexProvidernewSpatialIndexProvider (Config config) {return newSpatialIndexProvider(pageCache, fs, directoriesByProvider( directory.databaseDir( )) , monitor,
    immediate

    ( ) ,false , config) ; }private
            voidaddUpdates(SpatialIndexProviderprovider, StoreIndexDescriptor schemaIndexDescriptor , ValueCreatorUtil< SpatialIndexKey
    ,
        NativeIndexValue > layoutUtil )throwsIOException, IndexEntryConflictException{ IndexAccessoraccessor= provider.
        getOnlineAccessor ( schemaIndexDescriptor , samplingConfig ()); try ( IndexUpdater
        updater
            =
            accessor . newUpdater(ONLINE) ) { // whenfor(IndexEntryUpdate < IndexDescriptor >
            update
                :layoutUtil.someUpdates ( randomRule)
            )
        {
        updater.process( update); }}
        accessor.force(IOLimiter.
    UNLIMITED

    ) ;accessor. close( ) ;} private SpatialIndexFiles .
    SpatialFile
        makeIndexFile ( longindexId,ConfiguredSpaceFillingCurveSettingsCache configuredSettings){return newSpatialIndexFiles .SpatialFile ( CoordinateReferenceSystem .WGS84
    ,

    configuredSettings , indexDir( indexId ) )
    ;
        } private FileindexDir (longindexId) {returnnewFile ( indexRoot ()
    ,

    Long . toString( indexId ) )
    ;
        }
        private FileindexFile (long indexId ){ // The indexFile location is independent of the configuredSettings, so we just use the defaultsreturnmakeIndexFile(indexId , newConfiguredSpaceFillingCurveSettingsCache(Config
    .

    defaults ( )))
    .
        indexFile ; }private File indexRoot( ) {return newFile(newFile( new File( directory .databaseDir ( ),
    "schema"

    ) , "index") , "spatial-1.0") ; } private
            void createEmptyIndex
    (
        StoreIndexDescriptorschemaIndexDescriptor, ConfiguredSpaceFillingCurveSettingsCache configuredSettings )throws IOException{SpatialIndexFiles.SpatialFileLayoutfileLayout = makeIndexFile(schemaIndexDescriptor.getId(
        ),configuredSettings ) .
                getLayoutForNewIndex ();SpatialIndexPopulator .PartPopulator populator= newSpatialIndexPopulator .PartPopulator (pageCache , fs,fileLayout ,monitor
        ,schemaIndexDescriptor,newStandardConfiguration(
        ));populator . create(
    )

    ; populator .close ( true) ; } private void createEmptyIndex
    (
        StoreIndexDescriptor schemaIndexDescriptor , SpatialIndexProviderprovider)throws IOException{ IndexPopulatorpopulator= provider.
        getPopulator(schemaIndexDescriptor,samplingConfig(
        ));populator . create(
    )

    ; populator .close ( true) ; } private
    void
        verifySpatialSettings
        (
            File indexFile ,
                    SpaceFillingCurveSettingsexpectedSettings){ try{ SpaceFillingCurveSettingssettings =SpaceFillingCurveSettingsFactory. fromGBPTree(
            indexFile, pageCache, NativeIndexHeaderReader:: readFailureMessage) ; assertThat ("Should get correct results from header"
        ,
        settings , equalTo ( expectedSettings
        )
            ); } catch (IOExceptione){ fail(
        "Failed to read GBPTree header: "
    +

    e . getMessage () ) ;} } privatestatic Config configWithRange( double minX ,
    double
        minY,doublemaxX , double maxY){Setting <Double>wgs84MinX =SpatialIndexSettings . makeCRSRangeSetting(
        CoordinateReferenceSystem.WGS84, 0 , "min");Setting <Double>wgs84MinY =SpatialIndexSettings . makeCRSRangeSetting(
        CoordinateReferenceSystem.WGS84, 1 , "min");Setting <Double>wgs84MaxX =SpatialIndexSettings . makeCRSRangeSetting(
        CoordinateReferenceSystem.WGS84, 0 , "max");Setting <Double>wgs84MaxY =SpatialIndexSettings . makeCRSRangeSetting(
        CoordinateReferenceSystem . WGS84 ,1,"max");
        Configconfig=Config .defaults ();config . augment (wgs84MinX
        ,Double.toString (minX ));config . augment (wgs84MinY
        ,Double.toString (minY ));config . augment (wgs84MaxX
        ,Double.toString (maxX ));config . augment (wgs84MaxY
        , Double.
    toString
(
