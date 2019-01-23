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
import org.neo4j.consistency.report.ConsistencyReport.NodeConsistencyReport;importorg.neo4j.consistency.report.ConsistencyReport
. PropertyConsistencyReport;importorg.neo4j.consistency.report.ConsistencyReport
. PropertyKeyTokenConsistencyReport;importorg.neo4j.consistency.report.ConsistencyReport
. RelationshipConsistencyReport;importorg.neo4j.consistency.report
. ConsistencyReport.RelationshipTypeConsistencyReport;importorg.neo4j.consistency
. store.RecordAccess;importorg.neo4j.consistency.store
. RecordAccessStub;importorg.neo4j.kernel.impl.store
. PropertyType;importorg.neo4j.kernel.impl.store.RecordStore
; importorg.neo4j.kernel.impl.store.record.AbstractBaseRecord
; importorg.neo4j.kernel.impl.store.record.DynamicRecord
; importorg.neo4j.kernel.impl.store.record.NeoStoreRecord
; importorg.neo4j.kernel.impl.store.record.NodeRecord
; importorg.neo4j.kernel.impl.store.record.PropertyBlock
; importorg.neo4j.kernel.impl.store.record.PropertyKeyTokenRecord
; importorg.neo4j.kernel.impl.store.record.PropertyRecord
; importorg.neo4j.kernel.impl.store.record.RelationshipRecord

; import org.neo4j.kernel.impl.store.record.
RelationshipTypeTokenRecord ; importstaticorg.junit.jupiter.

api . Assertions .fail; import staticorg
        . mockito .Mockito
        . mock ;publicabstractclass RecordCheckTestBase<RECORD
extends
    AbstractBaseRecord , REPORT extends ConsistencyReport , CHECKERextendsRecordCheck
    < RECORD , REPORT>
    > { publicstaticfinalint NONE=
    - 1 ;protected
    final CHECKER checker;

    privatefinal Class <REPORT >reportClass;protected RecordAccessStubrecords ;privateStage stage; RecordCheckTestBase( CHECKER checker
    ,
        Class< REPORT> reportClass, int []cacheFields, MultiPassStore... storesToCheck) {this ( checker, reportClass ,new
    Stage

    .Adapter ( false, true,"Test stage", cacheFields) , storesToCheck) ;} RecordCheckTestBase (
    CHECKER
        checker,Class < REPORT>
        reportClass,Stage stage ,MultiPassStore
        ...storesToCheck) { this.
        checker= checker ;this
    .

    reportClass = reportClass; this. stage =
    stage
        ;initialize( storesToCheck ) ;} protectedvoid initialize (MultiPassStore
        ... storesToCheck ){this.records=new RecordAccessStub ( stage
        ,
            storesToCheck);if(stage.getCacheSlotSizes ().length> 0)
        {
    records

    . cacheAccess ().setCacheSlotSizes (stage .getCacheSlotSizes(
    )
        ) ; }}public
        static
            PrimitiveRecordCheck<
            NodeRecord , NodeConsistencyReport> dummyNodeCheck ()
                               {returnnewNodeRecordCheck () {@
                               Override public void
            check
            (
        NodeRecordrecord
    ,

    CheckerEngine < NodeRecord,NodeConsistencyReport> engine, RecordAccessrecords)
    {
        } } ;}public
        static
            PrimitiveRecordCheck<
            RelationshipRecord , RelationshipConsistencyReport> dummyRelationshipChecker ()
                               {returnnewRelationshipRecordCheck () {@
                               Override public void
            check
            (
        RelationshipRecordrecord
    ,

    CheckerEngine < RelationshipRecord,RelationshipConsistencyReport> engine, RecordAccessrecords)
    {
        } } ;} publicstatic RecordCheck < PropertyRecord ,PropertyConsistencyReport>
    dummyPropertyChecker

    ( ) {return(record ,engine ,records)
    ->
        { } ;} public staticPrimitiveRecordCheck<NeoStoreRecord , NeoStoreConsistencyReport > dummyNeoStoreCheck (
        )
            {return
            new NeoStoreCheck (new PropertyChain <>
                               (from->null )) {@
                               Override public void
            check
            (
        NeoStoreRecordrecord
    ,

    CheckerEngine < NeoStoreRecord,NeoStoreConsistencyReport> engine, RecordAccessrecords
            ){}} ;} public static RecordCheck
    <
        DynamicRecord , DynamicConsistencyReport> dummyDynamicCheck( RecordStore <
        DynamicRecord
            >store
            , DynamicStore dereference) { returnnew
                               DynamicRecordCheck(store, dereference) {@
                               Override public void
            check
            (
        DynamicRecordrecord
    ,

    CheckerEngine < DynamicRecord,DynamicConsistencyReport> engine, RecordAccessrecords)
    {
        } } ;}public
        static
            RecordCheck<
            PropertyKeyTokenRecord , PropertyKeyTokenConsistencyReport> dummyPropertyKeyCheck ()
                               {returnnewPropertyKeyTokenRecordCheck () {@
                               Override public void
            check
            (
        PropertyKeyTokenRecordrecord
    ,

    CheckerEngine < PropertyKeyTokenRecord,PropertyKeyTokenConsistencyReport> engine, RecordAccessrecords)
    {
        } } ;}public
        static
            RecordCheck<
            RelationshipTypeTokenRecord , RelationshipTypeConsistencyReport> dummyRelationshipLabelCheck ()
                               {returnnewRelationshipTypeTokenRecordCheck () {@
                               Override public void
            check
            (
        RelationshipTypeTokenRecordrecord
    ,

    CheckerEngine <RelationshipTypeTokenRecord , RelationshipTypeConsistencyReport >
    engine
        , RecordAccessrecords ){ }} ;} REPORT check(
    RECORD

    record ){ return check( reportClass , checker
    ,
        record, records) ;} voidcheck ( REPORTreport
    ,

    RECORD record ){ check (report , checker ,
    record
        , records) ;} finalREPORT check( CHECKER externalChecker,
    RECORD

    record ) {return check (reportClass , externalChecker ,record
    , records) ;}publicstatic <RECORD extendsAbstractBaseRecord,REPORT extendsConsistencyReport >REPORT check (Class
                  < REPORT > reportClass
    ,
        RecordCheck < RECORD ,REPORT > checker,
        RECORDrecord ,final RecordAccessStubrecords ){ REPORT report=
        mock (reportClass
    )

    ; check (report , checker, record , records)
    ; returnreport ; }public static<RECORDextends AbstractBaseRecord, REPORTextends ConsistencyReport >void
                  check ( REPORT report
    ,
        RecordCheck<RECORD, REPORT> checker,RECORDrecord ,final RecordAccessStub records) { checker.
        check(record,records.
    engine

    (record , report) , records) ; records. checkDeferred ( )
    ;
        } <RextendsAbstractBaseRecord >R addChange (R
    oldRecord

    ,R newRecord ){ return records. addChange ( oldRecord
    ,
        newRecord );}< R extendsAbstractBaseRecord
    >

    R add( R record )
    {
        return records.add( record );
    }

    DynamicRecord addNodeDynamicLabels( DynamicRecord labels )
    {
        return records.addNodeDynamicLabels( labels );
    }

    DynamicRecord addKeyName( DynamicRecord name )
    {
        return records.addPropertyKeyName( name );
    }

    DynamicRecord addRelationshipTypeName( DynamicRecord name )
    {
        return records.addRelationshipTypeName( name );
    }

    DynamicRecord addLabelName ( DynamicRecordname ) { return
    records
        .addLabelName(name );}publicstaticDynamicRecordstring (DynamicRecord
        record ){
    record

    . setType ( PropertyType. STRING . intValue
    (
        ));return record;}publicstaticDynamicRecordarray (DynamicRecord
        record ){
    record

    . setType (PropertyType . ARRAY. intValue ( )
    )
        ; return record ;}staticPropertyBlockpropertyBlock(
        PropertyKeyTokenRecord key ,DynamicRecordvalue){ PropertyType type=value . getType();if ( value.getType (
        )
            !=PropertyType . STRING&&
            value .getType
        (
        ) !=PropertyType .ARRAY ){ fail("Dynamic record must be either STRING or ARRAY"); returnnull
    ;

    } return propertyBlock (key , type, value .getId ( ) )
    ;
        } public static PropertyBlock propertyBlock(PropertyKeyTokenRecordkey
        ,PropertyTypetype, longvalue){PropertyBlock block =newPropertyBlock() ;block.setSingleBlock(key . getId( ) |( ( (long )type
        . intValue(
    )

    ) << 24) | (value << 28) ) ; return
    block
        ;}publicstatic < Rextends
        AbstractBaseRecord >R
    inUse

    ( R record) { record. setInUse (true ) ; return
    record
        ;}publicstatic < Rextends
        AbstractBaseRecord >R
    notInUse

    ( R record){
    record
        . setInUse(
    false
)
