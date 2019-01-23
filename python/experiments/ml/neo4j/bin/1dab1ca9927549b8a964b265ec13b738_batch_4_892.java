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
package org.neo4j.kernel.impl.index.schema;importorg.
junit .Before;importorg.
junit .Rule;importorg.
junit .Test;importorg.junit.

rules .RuleChain;importjava.
io .File;importjava.

io .IOException;importorg.neo4j.gis.spatial.index.
curves .StandardConfiguration;importorg.neo4j.graphdb.
config .Setting;importorg.neo4j.io.
pagecache .IOLimiter;importorg.neo4j.io.
pagecache .PageCache;importorg.neo4j.kernel.api.exceptions.
index .IndexEntryConflictException;importorg.neo4j.kernel.api.
index .IndexAccessor;importorg.neo4j.kernel.api.
index .IndexEntryUpdate;importorg.neo4j.kernel.api.
index .IndexPopulator;importorg.neo4j.kernel.api.
index .IndexProvider;importorg.neo4j.kernel.api.
index .IndexUpdater;importorg.neo4j.kernel.api.schema.
index .TestIndexDescriptorFactory;importorg.neo4j.kernel.
configuration .Config;importorg.neo4j.kernel.impl.api.index.
sampling .IndexSamplingConfig;importorg.neo4j.kernel.impl.index.schema.
config .ConfiguredSpaceFillingCurveSettingsCache;importorg.neo4j.kernel.impl.index.schema.
config .SpaceFillingCurveSettings;importorg.neo4j.kernel.impl.index.schema.
config .SpaceFillingCurveSettingsFactory;importorg.neo4j.kernel.impl.index.schema.
config .SpatialIndexSettings;importorg.neo4j.storageengine.api.
schema .IndexDescriptor;importorg.neo4j.storageengine.api.
schema .StoreIndexDescriptor;importorg.neo4j.test.
rule .PageCacheRule;importorg.neo4j.test.
rule .RandomRule;importorg.neo4j.test.
rule .TestDirectory;importorg.neo4j.test.rule.
fs .DefaultFileSystemRule;importorg.neo4j.values.

storable . CoordinateReferenceSystem;importstaticorg.hamcrest.
Matchers . equalTo;importstaticorg.junit.
Assert . assertThat;importstaticorg.junit.
Assert . fail;importstaticorg.junit.rules.
RuleChain . outerRule;importstaticorg.neo4j.index.internal.gbptree.
RecoveryCleanupWorkCollector . immediate;importstaticorg.neo4j.kernel.api.index.
IndexDirectoryStructure . directoriesByProvider;importstaticorg.neo4j.kernel.impl.api.index.
IndexUpdateMode . ONLINE;importstaticorg.neo4j.test.rule.

PageCacheRule . config
;
    public class SpatialIndexSettingsTest { private static finalCoordinateReferenceSystemcrs=
    CoordinateReferenceSystem . WGS84 ; private static finalConfigconfig1=Config.
    defaults ( ) ; private static finalConfig config2= configWithRange(0 ,- 90 ,180
    , 90 ) ; private static final ConfiguredSpaceFillingCurveSettingsCacheconfiguredSettings1 = newConfiguredSpaceFillingCurveSettingsCache
    ( config1 ) ; private static final ConfiguredSpaceFillingCurveSettingsCacheconfiguredSettings2 = newConfiguredSpaceFillingCurveSettingsCache

    ( config2 );
    private StoreIndexDescriptor schemaIndexDescriptor1;
    private StoreIndexDescriptorschemaIndexDescriptor2;privateValueCreatorUtil< SpatialIndexKey,
    NativeIndexValue >layoutUtil1;privateValueCreatorUtil< SpatialIndexKey,
    NativeIndexValue > layoutUtil2 ; privatelong
    indexId1 = 1 ; privatelong

    indexId2 = 2 ; final DefaultFileSystemRulefs=new
    DefaultFileSystemRule ( ) ; private finalTestDirectorydirectory= TestDirectory.testDirectory( getClass(),fs .get
    ( ) ) ; private final PageCacheRulepageCacheRule =newPageCacheRule(config( ) . withAccessChecks(
    true ) ) ; private RandomRulerandomRule=new

    RandomRule(
    ) ; @ Rule public finalRuleChain rules =outerRule(fs ) .around(directory ) .around(pageCacheRule ) .around

    ( randomRule );
    private PageCachepageCache; private IndexProvider .Monitormonitor=IndexProvider.

    Monitor.
    EMPTY ; @Beforepublic void setupTwoIndexes
    (
        ) throws IOException{pageCache= pageCacheRule .getPageCache

        (
        fs ) ;// Define two indexes based on different labels and different configuredSettings layoutUtil1= createLayoutTestUtil (indexId1
        , 42 ); layoutUtil2= createLayoutTestUtil (indexId2
        , 43 );schemaIndexDescriptor1=layoutUtil1.
        indexDescriptor ( );schemaIndexDescriptor2=layoutUtil2.

        indexDescriptor
        () ;// Create the two indexes as empty, based on differently configured configuredSettings above createEmptyIndex (schemaIndexDescriptor1
        ,configuredSettings1 ); createEmptyIndex (schemaIndexDescriptor2
    ,

    configuredSettings2)
    ; } @Testpublic void shouldAddToSpatialIndexWithDefaults
    (
        )
        throws Exception { // givenSpatialIndexProvider provider =newSpatialIndexProvider
        (config1 ); addUpdates( provider ,schemaIndexDescriptor1

        ,
        layoutUtil1) ;// then verifySpatialSettings (indexFile (indexId1), configuredSettings1 . forCRS(
    crs

    ))
    ; } @Testpublic void shouldAddToSpatialIndexWithModifiedSettings
    (
        )
        throws Exception { // givenSpatialIndexProvider provider =newSpatialIndexProvider
        (config2 ); addUpdates( provider ,schemaIndexDescriptor2

        ,
        layoutUtil2) ;// then verifySpatialSettings (indexFile (indexId2), configuredSettings2 . forCRS(
    crs

    ))
    ; } @Testpublic void shouldAddToTwoDifferentIndexesOneDefaultAndOneModified
    (
        )
        throws Exception { // givenSpatialIndexProvider provider =newSpatialIndexProvider
        (config2 ); addUpdates( provider ,schemaIndexDescriptor1
        ,layoutUtil1 ); addUpdates( provider ,schemaIndexDescriptor2

        ,
        layoutUtil2) ;// then even though the provider was created with modified configuredSettings, only the second index should have them verifySpatialSettings (indexFile (indexId1), configuredSettings1 . forCRS(
        crs) ); verifySpatialSettings (indexFile (indexId2), configuredSettings2 . forCRS(
    crs

    ))
    ; } @Testpublic void shouldNotLeakSpaceFillingCurveSettingsBetweenExistingAndNewIndexes
    (
        )
        throws Exception { // given two indexes previously created with different configuredSettingsConfig config=configWithRange (-10 ,- 10 ,10
        , 10 ) ;SpatialIndexProvider provider =newSpatialIndexProvider
        (config ); addUpdates( provider ,schemaIndexDescriptor1
        ,layoutUtil1 ); addUpdates( provider ,schemaIndexDescriptor2

        ,
        layoutUtil2 ) ; // and when creating and populating a third index with a third set of configuredSettingslong
        indexId3 = 3 ; ConfiguredSpaceFillingCurveSettingsCachesettings3 = newConfiguredSpaceFillingCurveSettingsCache
        ( config ) ;SpatialValueCreatorUtil layoutUtil3= createLayoutTestUtil (indexId3
        , 44 ) ;StoreIndexDescriptorschemaIndexDescriptor3=layoutUtil3.
        indexDescriptor( ); createEmptyIndex (schemaIndexDescriptor3
        ,provider ); addUpdates( provider ,schemaIndexDescriptor3

        ,
        layoutUtil3) ;// Then all indexes should still have their own correct and different configuredSettings verifySpatialSettings (indexFile (indexId1), configuredSettings1 . forCRS(
        crs) ); verifySpatialSettings (indexFile (indexId2), configuredSettings2 . forCRS(
        crs) ); verifySpatialSettings (indexFile (indexId3), settings3 . forCRS(
    crs

    ) ) ;}private
    IndexSamplingConfig
        samplingConfig ( ){ returnnewIndexSamplingConfig(Config .defaults
    (

    ) ) ;} private SpatialValueCreatorUtilcreateLayoutTestUtil ( long indexId
    ,
        int labelId ) {StoreIndexDescriptordescriptor= TestIndexDescriptorFactory. forLabel (labelId,666 ) .withId
        ( indexId ); returnnew SpatialValueCreatorUtil(descriptor ,ValueCreatorUtil
    .

    FRACTION_DUPLICATE_NON_UNIQUE ) ;} private SpatialIndexProvider newSpatialIndexProvider
    (
        Config config ){ returnnew SpatialIndexProvider( pageCache, fs,directoriesByProvider(directory .databaseDir () ),monitor, immediate( ) ,false
    ,

    config ) ;} private voidaddUpdates ( SpatialIndexProviderprovider
            ,StoreIndexDescriptorschemaIndexDescriptor,ValueCreatorUtil< SpatialIndexKey , NativeIndexValue >layoutUtil )
    throws
        IOException , IndexEntryConflictException {IndexAccessoraccessor= provider. getOnlineAccessor(schemaIndexDescriptor ,samplingConfig
        ( ) ) ; try (IndexUpdaterupdater= accessor . newUpdater
        (
            ONLINE
            ) ) {// whenfor( IndexEntryUpdate < IndexDescriptor>update: layoutUtil . someUpdates
            (
                randomRule)){ updater .process
            (
        update
        );}} accessor.force (IOLimiter
        .UNLIMITED);accessor.
    close

    ( );} privateSpatialIndexFiles . SpatialFilemakeIndexFile ( long indexId
    ,
        ConfiguredSpaceFillingCurveSettingsCache configuredSettings ){returnnew SpatialIndexFiles.SpatialFile( CoordinateReferenceSystem. WGS84, configuredSettings , indexDir(
    indexId

    ) ) ;} private File indexDir
    (
        long indexId ){ returnnewFile( indexRoot(), Long . toString(
    indexId

    ) ) ;} private File indexFile
    (
        long
        indexId ){ // The indexFile location is independent of the configuredSettings, so we just use the defaultsreturn makeIndexFile (indexId ,newConfiguredSpaceFillingCurveSettingsCache(Config . defaults())
    )

    . indexFile ;}private
    File
        indexRoot ( ){ return newFile ( newFile (newFile(directory. databaseDir () , "schema") , "index")
    ,

    "spatial-1.0" ) ;} private voidcreateEmptyIndex ( StoreIndexDescriptor schemaIndexDescriptor
            , ConfiguredSpaceFillingCurveSettingsCache
    configuredSettings
        )throwsIOException { SpatialIndexFiles .SpatialFileLayout fileLayout=makeIndexFile(schemaIndexDescriptor. getId (),configuredSettings).
        getLayoutForNewIndex() ; SpatialIndexPopulator
                . PartPopulatorpopulator=new SpatialIndexPopulator. PartPopulator( pageCache, fs, fileLayout, monitor ,schemaIndexDescriptor, newStandardConfiguration
        ());populator.
        create(); populator .close
    (

    true ) ;} private voidcreateEmptyIndex ( StoreIndexDescriptor schemaIndexDescriptor , SpatialIndexProvider
    provider
        ) throws IOException {IndexPopulatorpopulator= provider. getPopulator(schemaIndexDescriptor ,samplingConfig
        ());populator.
        create(); populator .close
    (

    true ) ;} private voidverifySpatialSettings ( File indexFile
    ,
        SpaceFillingCurveSettings
        expectedSettings
            ) { try
                    {SpaceFillingCurveSettingssettings= SpaceFillingCurveSettingsFactory. fromGBPTree( indexFile,pageCache ,NativeIndexHeaderReader
            ::readFailureMessage ); assertThat( "Should get correct results from header", settings , equalTo(
        expectedSettings
        ) ) ; } catch
        (
            IOExceptione ) { fail("Failed to read GBPTree header: "+e .getMessage
        (
    )

    ) ; } }private static ConfigconfigWithRange ( doubleminX , doubleminY , double maxX
    ,
        doublemaxY){ Setting < Double>wgs84MinX= SpatialIndexSettings.makeCRSRangeSetting( CoordinateReferenceSystem. WGS84 ,0
        ,"min"); Setting < Double>wgs84MinY= SpatialIndexSettings.makeCRSRangeSetting( CoordinateReferenceSystem. WGS84 ,1
        ,"min"); Setting < Double>wgs84MaxX= SpatialIndexSettings.makeCRSRangeSetting( CoordinateReferenceSystem. WGS84 ,0
        ,"max"); Setting < Double>wgs84MaxY= SpatialIndexSettings.makeCRSRangeSetting( CoordinateReferenceSystem. WGS84 ,1
        , "max" ) ;Configconfig=Config.
        defaults(); config. augment(wgs84MinX, Double . toString(
        minX)); config. augment(wgs84MinY, Double . toString(
        minY)); config. augment(wgs84MaxX, Double . toString(
        maxX)); config. augment(wgs84MaxY, Double . toString(
        maxY ))
    ;
return
