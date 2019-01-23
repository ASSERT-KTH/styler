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
package org.neo4j.consistency.checking;

import org.neo4j.consistency.checking.full.MultiPassStore;
import org.neo4j.consistency.checking.full.Stage;
import org.neo4j.consistency.report.ConsistencyReport;
import org.neo4j.consistency.report.ConsistencyReport.DynamicConsistencyReport;
import org.neo4j.consistency.report.ConsistencyReport.NeoStoreConsistencyReport;
import org.neo4j.consistency.report.ConsistencyReport.NodeConsistencyReport;
import org.neo4j.consistency.report.ConsistencyReport.PropertyConsistencyReport;importorg.neo4j.consistency.report.ConsistencyReport
. PropertyKeyTokenConsistencyReport;importorg.neo4j.consistency.report.ConsistencyReport
. RelationshipConsistencyReport;importorg.neo4j.consistency.report.ConsistencyReport
. RelationshipTypeConsistencyReport;importorg.neo4j.consistency.store
. RecordAccess;importorg.neo4j.consistency.store
. RecordAccessStub;importorg.neo4j.kernel.impl.store
. PropertyType;importorg.neo4j.kernel.impl.store
. RecordStore;importorg.neo4j.kernel.impl.store.record
. AbstractBaseRecord;importorg.neo4j.kernel.impl.store.record
. DynamicRecord;importorg.neo4j.kernel.impl.store.record
. NeoStoreRecord;importorg.neo4j.kernel.impl.store.record
. NodeRecord;importorg.neo4j.kernel.impl.store.record
. PropertyBlock;importorg.neo4j.kernel.impl.store.record
. PropertyKeyTokenRecord;importorg.neo4j.kernel.impl.store.record
. PropertyRecord;importorg.neo4j.kernel.impl.store.record
. RelationshipRecord;importorg.neo4j.kernel.impl.store.record

. RelationshipTypeTokenRecord ;importstaticorg.junit.jupiter.api.Assertions
. fail ;importstaticorg.mockito.Mockito

. mock ; publicabstractclass RecordCheckTestBase <RECORD
        extends AbstractBaseRecord ,REPORT
        extends ConsistencyReport ,CHECKERextendsRecordCheck <RECORD,
REPORT
    > > { public static final intNONE=
    - 1 ; protectedfinal
    CHECKER checker ;privatefinalClass <REPORT
    > reportClass ;protected
    RecordAccessStub records ;private

    Stagestage ; RecordCheckTestBase( CHECKERchecker,Class <REPORT >reportClass, int[ ]cacheFields , MultiPassStore
    ...
        storesToCheck) {this (checker , reportClass,newStage .Adapter (false ,true , "Test stage", cacheFields ),
    storesToCheck

    ); } RecordCheckTestBase( CHECKERchecker,Class <REPORT > reportClass, Stagestage , MultiPassStore
    ...
        storesToCheck){ this .checker
        =checker; this .reportClass
        =reportClass; this .stage
        =stage ; initialize(
    storesToCheck

    ) ; }protected voidinitialize ( MultiPassStore
    ...
        storesToCheck){ this . records= newRecordAccessStub ( stage,
        storesToCheck ) ;if(stage.getCacheSlotSizes( ) . length
        >
            0){records.cacheAccess() .setCacheSlotSizes(stage. getCacheSlotSizes(
        )
    )

    ; } }publicstaticPrimitiveRecordCheck <NodeRecord ,NodeConsistencyReport>
    dummyNodeCheck
        ( ) {returnnew
        NodeRecordCheck
            ()
            { @ Overridepublic void check(
                               NodeRecordrecord,CheckerEngine <NodeRecord ,NodeConsistencyReport
                               > engine ,
            RecordAccess
            records
        ){
    }

    } ; }publicstaticPrimitiveRecordCheck <RelationshipRecord ,RelationshipConsistencyReport>
    dummyRelationshipChecker
        ( ) {returnnew
        RelationshipRecordCheck
            ()
            { @ Overridepublic void check(
                               RelationshipRecordrecord,CheckerEngine <RelationshipRecord ,RelationshipConsistencyReport
                               > engine ,
            RecordAccess
            records
        ){
    }

    } ; }publicstaticRecordCheck <PropertyRecord ,PropertyConsistencyReport>
    dummyPropertyChecker
        ( ) {return (record , engine , records)->
    {

    } ; }publicstaticPrimitiveRecordCheck <NeoStoreRecord ,NeoStoreConsistencyReport>
    dummyNeoStoreCheck
        ( ) {return new NeoStoreCheck(newPropertyChain < > ( from ->
        null
            ))
            { @ Overridepublic void check(
                               NeoStoreRecordrecord,CheckerEngine <NeoStoreRecord ,NeoStoreConsistencyReport
                               > engine ,
            RecordAccess
            records
        ){
    }

    } ; }publicstaticRecordCheck <DynamicRecord ,DynamicConsistencyReport
            >dummyDynamicCheck(RecordStore <DynamicRecord > store ,
    DynamicStore
        dereference ) {return newDynamicRecordCheck ( store
        ,
            dereference)
            { @ Overridepublic void check(
                               DynamicRecordrecord,CheckerEngine <DynamicRecord ,DynamicConsistencyReport
                               > engine ,
            RecordAccess
            records
        ){
    }

    } ; }publicstaticRecordCheck <PropertyKeyTokenRecord ,PropertyKeyTokenConsistencyReport>
    dummyPropertyKeyCheck
        ( ) {returnnew
        PropertyKeyTokenRecordCheck
            ()
            { @ Overridepublic void check(
                               PropertyKeyTokenRecordrecord,CheckerEngine <PropertyKeyTokenRecord ,PropertyKeyTokenConsistencyReport
                               > engine ,
            RecordAccess
            records
        ){
    }

    } ; }publicstaticRecordCheck <RelationshipTypeTokenRecord ,RelationshipTypeConsistencyReport>
    dummyRelationshipLabelCheck
        ( ) {returnnew
        RelationshipTypeTokenRecordCheck
            ()
            { @ Overridepublic void check(
                               RelationshipTypeTokenRecordrecord,CheckerEngine <RelationshipTypeTokenRecord ,RelationshipTypeConsistencyReport
                               > engine ,
            RecordAccess
            records
        ){
    }

    } ;} REPORT check (
    RECORD
        record ){ returncheck (reportClass ,checker , record,
    records

    ) ;} void check( REPORT report ,
    RECORD
        record) {check (report ,checker , record,
    records

    ) ; }final REPORT check( CHECKER externalChecker ,
    RECORD
        record ){ returncheck (reportClass ,externalChecker , record,
    records

    ) ; }public static <RECORD extends AbstractBaseRecord ,REPORT
    extends ConsistencyReport> REPORTcheck(Class <REPORT >reportClass,RecordCheck <RECORD ,REPORT > checker,
                  RECORD record , final
    RecordAccessStub
        records ) { REPORTreport = mock(
        reportClass) ;check (report ,checker , record,
        records );
    return

    report ; }public static <RECORD extends AbstractBaseRecord ,REPORT
    extends ConsistencyReport> void check( REPORTreport,RecordCheck <RECORD ,REPORT > checker,
                  RECORD record , final
    RecordAccessStub
        records){checker .check (record,records .engine ( record, report ),
        records);records.checkDeferred
    (

    ); } <R extends AbstractBaseRecord> R addChange( R oldRecord ,
    R
        newRecord ){returnrecords .addChange ( oldRecord,
    newRecord

    ); } <R extends AbstractBaseRecord> R add (
    R
        record ){returnrecords . add(
    record

    ) ;} DynamicRecord addNodeDynamicLabels (
    DynamicRecord
        labels ){returnrecords . addNodeDynamicLabels(
    labels

    ) ;} DynamicRecord addKeyName (
    DynamicRecord
        name ){returnrecords . addPropertyKeyName(
    name

    ) ;} DynamicRecord addRelationshipTypeName (
    DynamicRecord
        name ){returnrecords . addRelationshipTypeName(
    name

    ) ;} DynamicRecord addLabelName (
    DynamicRecord
        name ){returnrecords . addLabelName(
    name

    ) ; } publicstatic DynamicRecord string (
    DynamicRecord
        record){record .setType(PropertyType.STRING. intValue(
        ) );
    return

    record ; } publicstatic DynamicRecord array (
    DynamicRecord
        record){record .setType(PropertyType.ARRAY. intValue(
        ) );
    return

    record ; }static PropertyBlock propertyBlock( PropertyKeyTokenRecord key ,
    DynamicRecord
        value ) { PropertyTypetype=value.getType
        ( ) ;if(value. getType ()!= PropertyType .STRING&&value. getType ()!= PropertyType
        .
            ARRAY) { fail(
            "Dynamic record must be either STRING or ARRAY" );
        return
        null ;} returnpropertyBlock (key ,type,value. getId(
    )

    ) ; } publicstatic PropertyBlock propertyBlock( PropertyKeyTokenRecord key, PropertyType type ,
    long
        value ) { PropertyBlock block=newPropertyBlock
        ();block .setSingleBlock(key. getId ()|(( (long)type.intValue ( )) << 24) | (value <<28
        ) );
    return

    block ; }public static <R extends AbstractBaseRecord> R inUse (
    R
        record){record . setInUse(
        true );
    return

    record ; }public static <R extends AbstractBaseRecord> R notInUse (
    R
        record){record . setInUse(
        false );
    return

    record ; }protectedCHECKER
    checker
        ( ){
    return
checker
