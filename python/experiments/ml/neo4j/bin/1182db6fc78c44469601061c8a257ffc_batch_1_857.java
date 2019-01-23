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
import org.neo4j.test.rule.fs.DefaultFileSystemRule;importorg.neo4j.
values . storable.CoordinateReferenceSystem;importstaticorg.
hamcrest . Matchers.equalTo;importstaticorg.
junit . Assert.assertThat;importstaticorg.junit.
Assert . fail;importstaticorg.junit.rules.RuleChain.outerRule;
import static org.neo4j.index.internal.gbptree.RecoveryCleanupWorkCollector.immediate;
import static org.neo4j.kernel.api.index.IndexDirectoryStructure.directoriesByProvider;importstatic
org . neo4j.kernel.impl.api.index.IndexUpdateMode.

ONLINE ; import
static
    org . neo4j . test . rule.PageCacheRule.
    config ; public class SpatialIndexSettingsTest { privatestaticfinalCoordinateReferenceSystemcrs=
    CoordinateReferenceSystem . WGS84 ; private static finalConfig config1= Config.defaults () ; privatestatic
    final Config config2 = configWithRange ( 0 ,- 90 ,180
    , 90 ) ; private static final ConfiguredSpaceFillingCurveSettingsCacheconfiguredSettings1 = newConfiguredSpaceFillingCurveSettingsCache

    ( config1 );
    private static finalConfiguredSpaceFillingCurveSettingsCache
    configuredSettings2 =newConfiguredSpaceFillingCurveSettingsCache(config2) ;private
    StoreIndexDescriptor schemaIndexDescriptor1;privateStoreIndexDescriptorschemaIndexDescriptor2; privateValueCreatorUtil
    < SpatialIndexKey , NativeIndexValue >layoutUtil1
    ; private ValueCreatorUtil < SpatialIndexKey,

    NativeIndexValue > layoutUtil2 ; private longindexId1=1
    ; private long indexId2 = 2;finalDefaultFileSystemRule fs=newDefaultFileSystemRule ();privatefinal TestDirectorydirectory
    = TestDirectory . testDirectory ( getClass () ,fs.get() ) ; privatefinal
    PageCacheRule pageCacheRule = new PageCacheRule (config()

    .withAccessChecks
    ( true ) ) ; privateRandomRule randomRule =newRandomRule( ) ;@Rulepublic final RuleChainrules=outerRule ( fs)

    . around (directory
    ) .around( pageCacheRule ) .around(randomRule);

    privatePageCache
    pageCache ; privateIndexProvider. Monitor monitor
    =
        IndexProvider . Monitor.EMPTY; @ Beforepublic

        void
        setupTwoIndexes ( )throws IOException{ pageCache =pageCacheRule
        . getPageCache (fs ); // Define two indexes based on different labels and different configuredSettings layoutUtil1=
        createLayoutTestUtil ( indexId1,42);layoutUtil2
        = createLayoutTestUtil (indexId2,43);

        schemaIndexDescriptor1
        =layoutUtil1 .indexDescriptor ( );
        schemaIndexDescriptor2= layoutUtil2. indexDescriptor ()
    ;

    // Create the two indexes as empty, based on differently configured configuredSettings abovecreateEmptyIndex
    ( schemaIndexDescriptor1 ,configuredSettings1) ; createEmptyIndex
    (
        schemaIndexDescriptor2
        , configuredSettings2 ) ;} @ Testpublic
        voidshouldAddToSpatialIndexWithDefaults () throwsException { // givenSpatialIndexProvider

        provider
        =newSpatialIndexProvider (config1 ) ;addUpdates (provider,schemaIndexDescriptor1 , layoutUtil1 );
    // then

    verifySpatialSettings(
    indexFile ( indexId1), configuredSettings1 .
    forCRS
        (
        crs ) ) ;} @ Testpublic
        voidshouldAddToSpatialIndexWithModifiedSettings () throwsException { // givenSpatialIndexProvider

        provider
        =newSpatialIndexProvider (config2 ) ;addUpdates (provider,schemaIndexDescriptor2 , layoutUtil2 );
    // then

    verifySpatialSettings(
    indexFile ( indexId2), configuredSettings2 .
    forCRS
        (
        crs ) ) ;} @ Testpublic
        voidshouldAddToTwoDifferentIndexesOneDefaultAndOneModified () throwsException { // givenSpatialIndexProvider
        provider= newSpatialIndexProvider( config2) ; addUpdates(

        provider
        ,schemaIndexDescriptor1 ,layoutUtil1 ) ;addUpdates (provider,schemaIndexDescriptor2 , layoutUtil2 );
        // then even though the provider was created with modified configuredSettings, only the second index should have themverifySpatialSettings (indexFile ( indexId1) ,configuredSettings1.forCRS ( crs ))
    ;

    verifySpatialSettings(
    indexFile ( indexId2), configuredSettings2 .
    forCRS
        (
        crs ) ) ;} @Testpublic voidshouldNotLeakSpaceFillingCurveSettingsBetweenExistingAndNewIndexes( )throws Exception {// given two indexes previously created with different configuredSettings
        Config config = configWithRange( - 10,
        -10 ,10 ,10 ) ;SpatialIndexProvider
        provider= newSpatialIndexProvider( config) ; addUpdates(

        provider
        , schemaIndexDescriptor1 , layoutUtil1)
        ; addUpdates ( provider ,schemaIndexDescriptor2 , layoutUtil2)
        ; // and when creating and populating a third index with a third set of configuredSettings long indexId3= 3; ConfiguredSpaceFillingCurveSettingsCache settings3=
        new ConfiguredSpaceFillingCurveSettingsCache ( config);SpatialValueCreatorUtillayoutUtil3=
        createLayoutTestUtil( indexId3, 44 );
        StoreIndexDescriptorschemaIndexDescriptor3 =layoutUtil3 .indexDescriptor ( );

        createEmptyIndex
        (schemaIndexDescriptor3 ,provider ) ;addUpdates (provider,schemaIndexDescriptor3 , layoutUtil3 );
        // Then all indexes should still have their own correct and different configuredSettingsverifySpatialSettings (indexFile ( indexId1) ,configuredSettings1.forCRS ( crs ))
        ;verifySpatialSettings (indexFile ( indexId2) ,configuredSettings2.forCRS ( crs ))
    ;

    verifySpatialSettings ( indexFile(indexId3
    )
        , settings3 .forCRS (crs)); }private
    IndexSamplingConfig

    samplingConfig ( ){ return newIndexSamplingConfig ( Config .
    defaults
        ( ) ) ;}privateSpatialValueCreatorUtil createLayoutTestUtil( long indexId,intlabelId ) {StoreIndexDescriptor
        descriptor = TestIndexDescriptorFactory. forLabel( labelId,666 ).
    withId

    ( indexId ); return new SpatialValueCreatorUtil
    (
        descriptor , ValueCreatorUtil. FRACTION_DUPLICATE_NON_UNIQUE) ;} privateSpatialIndexProvider newSpatialIndexProvider(Configconfig) {return newSpatialIndexProvider (pageCache,fs ,directoriesByProvider ( directory.
    databaseDir

    ( ) ), monitor ,immediate ( ),
            false,config);} private void addUpdates (SpatialIndexProvider provider
    ,
        StoreIndexDescriptor schemaIndexDescriptor , ValueCreatorUtil<SpatialIndexKey, NativeIndexValue> layoutUtil)throws IOException,
        IndexEntryConflictException { IndexAccessor accessor = provider.getOnlineAccessor( schemaIndexDescriptor , samplingConfig
        (
            )
            ) ; try(IndexUpdaterupdater = accessor .newUpdater(ONLINE ) ) {
            // when
                for(IndexEntryUpdate< IndexDescriptor >update
            :
        layoutUtil
        .someUpdates(randomRule )){ updater.
        process(update);}
    }

    accessor .force( IOLimiter. UNLIMITED ); accessor . close
    (
        ) ; }privateSpatialIndexFiles. SpatialFilemakeIndexFile(long indexId, ConfiguredSpaceFillingCurveSettingsCacheconfiguredSettings ) { returnnew
    SpatialIndexFiles

    . SpatialFile (CoordinateReferenceSystem . WGS84 ,
    configuredSettings
        , indexDir (indexId ));} privateFileindexDir( long indexId ){
    return

    new File (indexRoot ( ) ,
    Long
        .
        toString (indexId )) ; }private FileindexFile(longindexId ) {// The indexFile location is independent of the configuredSettings, so we just use the defaultsreturnmakeIndexFile
    (

    indexId , newConfiguredSpaceFillingCurveSettingsCache(
    Config
        . defaults () ) ). indexFile ;} privateFileindexRoot(){ return newFile ( newFile ( newFile
    (

    directory . databaseDir( ) ,"schema" ) , "index"
            ) ,
    "spatial-1.0"
        );} private void createEmptyIndex( StoreIndexDescriptorschemaIndexDescriptor,ConfiguredSpaceFillingCurveSettingsCacheconfiguredSettings) throws IOException{SpatialIndexFiles.SpatialFileLayoutfileLayout
        =makeIndexFile( schemaIndexDescriptor .
                getId (),configuredSettings ). getLayoutForNewIndex( ); SpatialIndexPopulator. PartPopulatorpopulator = newSpatialIndexPopulator. PartPopulator(
        pageCache,fs,fileLayout,
        monitor,schemaIndexDescriptor, new StandardConfiguration(
    )

    ) ; populator. create () ; populator . close (
    true
        ) ; } privatevoidcreateEmptyIndex( StoreIndexDescriptorschemaIndexDescriptor ,SpatialIndexProviderprovider )throws
        IOException{IndexPopulatorpopulator=provider
        .getPopulator(schemaIndexDescriptor , samplingConfig(
    )

    ) ; populator. create () ; populator .
    close
        (
        true
            ) ; }
                    privatevoidverifySpatialSettings( FileindexFile ,SpaceFillingCurveSettings expectedSettings){ try{
            SpaceFillingCurveSettingssettings =SpaceFillingCurveSettingsFactory .fromGBPTree (indexFile , pageCache ,NativeIndexHeaderReader
        ::
        readFailureMessage ) ; assertThat (
        "Should get correct results from header"
            ,settings , equalTo (expectedSettings)); }catch
        (
    IOException

    e ) { fail( "Failed to read GBPTree header: " +e . getMessage( ) ); } } private
    static
        ConfigconfigWithRange(double minX , doubleminY,double maxX,doublemaxY ){ Setting <Double
        >wgs84MinX=SpatialIndexSettings . makeCRSRangeSetting (CoordinateReferenceSystem.WGS84 ,0,"min" ); Setting <Double
        >wgs84MinY=SpatialIndexSettings . makeCRSRangeSetting (CoordinateReferenceSystem.WGS84 ,1,"min" ); Setting <Double
        >wgs84MaxX=SpatialIndexSettings . makeCRSRangeSetting (CoordinateReferenceSystem.WGS84 ,0,"max" ); Setting <Double
        > wgs84MaxY = SpatialIndexSettings.makeCRSRangeSetting(CoordinateReferenceSystem.
        WGS84,1, "max") ;Configconfig= Config . defaults(
        );config. augment( wgs84MinX,Double. toString ( minX)
        );config. augment( wgs84MinY,Double. toString ( minY)
        );config. augment( wgs84MaxX,Double. toString ( maxX)
        ) ;config
    .
augment
