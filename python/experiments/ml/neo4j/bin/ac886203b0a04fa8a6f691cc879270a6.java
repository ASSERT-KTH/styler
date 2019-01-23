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
import org.neo4j.consistency.report.ConsistencyReport.PropertyConsistencyReport;
import org.neo4j.consistency.report.ConsistencyReport.PropertyKeyTokenConsistencyReport;
import org.neo4j.consistency.report.ConsistencyReport.RelationshipConsistencyReport;
import org.neo4j.consistency.report.ConsistencyReport.
RelationshipTypeConsistencyReport ;importorg.neo4j.consistency.store.
RecordAccess ;importorg.neo4j.consistency.store.RecordAccessStub;
import org.neo4j.kernel.impl.store.PropertyType;
import org.neo4j.kernel.impl.store.RecordStore;importorg
. neo4j.kernel.impl.store.record.AbstractBaseRecord;importorg
. neo4j.kernel.impl.store.record.DynamicRecord;importorg
. neo4j.kernel.impl.store.record.NeoStoreRecord;importorg
. neo4j.kernel.impl.store.record.NodeRecord;importorg
. neo4j.kernel.impl.store.record.PropertyBlock;importorg
. neo4j.kernel.impl.store.record.PropertyKeyTokenRecord;importorg
. neo4j.kernel.impl.store.record.PropertyRecord;importorg
. neo4j.kernel.impl.store.record.RelationshipRecord;importorg

. neo4j .kernel.impl.store.record.RelationshipTypeTokenRecord;import
static org .junit.jupiter.api.Assertions

. fail ; importstaticorg . mockito.
        Mockito . mock;
        public abstract classRecordCheckTestBase<RECORD extendsAbstractBaseRecord,
REPORT
    extends ConsistencyReport , CHECKER extends RecordCheck <RECORD,
    REPORT > > {public
    static final intNONE=- 1;
    protected final CHECKERchecker
    ; private finalClass

    <REPORT > reportClass; protectedRecordAccessStubrecords; privateStage stage;RecordCheckTestBase (CHECKER checker, Class <
    REPORT
        >reportClass ,int [] cacheFields ,MultiPassStore...storesToCheck ){ this( checker, reportClass ,new Stage .Adapter
    (

    false, true ,"Test stage" ,cacheFields), storesToCheck) ; }RecordCheckTestBase (CHECKER checker ,
    Class
        <REPORT> reportClass ,Stage
        stage,MultiPassStore ... storesToCheck)
        {this. checker =checker
        ;this . reportClass=
    reportClass

    ; this .stage =stage ; initialize
    (
        storesToCheck); } protected voidinitialize (MultiPassStore ... storesToCheck)
        { this .records=newRecordAccessStub(stage , storesToCheck )
        ;
            if(stage.getCacheSlotSizes(). length>0){ records.
        cacheAccess
    (

    ) . setCacheSlotSizes(stage. getCacheSlotSizes( ));
    }
        } public staticPrimitiveRecordCheck<
        NodeRecord
            ,NodeConsistencyReport
            > dummyNodeCheck () { returnnew
                               NodeRecordCheck(){ @Override publicvoid
                               check ( NodeRecord
            record
            ,
        CheckerEngine<
    NodeRecord

    , NodeConsistencyReport >engine,RecordAccess records) {}}
    ;
        } public staticPrimitiveRecordCheck<
        RelationshipRecord
            ,RelationshipConsistencyReport
            > dummyRelationshipChecker () { returnnew
                               RelationshipRecordCheck(){ @Override publicvoid
                               check ( RelationshipRecord
            record
            ,
        CheckerEngine<
    RelationshipRecord

    , RelationshipConsistencyReport >engine,RecordAccess records) {}}
    ;
        } public staticRecordCheck <PropertyRecord , PropertyConsistencyReport > dummyPropertyChecker()
    {

    return ( record,engine, records) ->{}
    ;
        } public staticPrimitiveRecordCheck < NeoStoreRecord,NeoStoreConsistencyReport> dummyNeoStoreCheck ( ) { return
        new
            NeoStoreCheck(
            new PropertyChain <> ( from->
                               null)){ @Override publicvoid
                               check ( NeoStoreRecord
            record
            ,
        CheckerEngine<
    NeoStoreRecord

    , NeoStoreConsistencyReport >engine,RecordAccess records) {}
            };}public staticRecordCheck < DynamicRecord ,
    DynamicConsistencyReport
        > dummyDynamicCheck (RecordStore <DynamicRecord > store
        ,
            DynamicStoredereference
            ) { returnnew DynamicRecordCheck (store
                               ,dereference){ @Override publicvoid
                               check ( DynamicRecord
            record
            ,
        CheckerEngine<
    DynamicRecord

    , DynamicConsistencyReport >engine,RecordAccess records) {}}
    ;
        } public staticRecordCheck<
        PropertyKeyTokenRecord
            ,PropertyKeyTokenConsistencyReport
            > dummyPropertyKeyCheck () { returnnew
                               PropertyKeyTokenRecordCheck(){ @Override publicvoid
                               check ( PropertyKeyTokenRecord
            record
            ,
        CheckerEngine<
    PropertyKeyTokenRecord

    , PropertyKeyTokenConsistencyReport >engine,RecordAccess records) {}}
    ;
        } public staticRecordCheck<
        RelationshipTypeTokenRecord
            ,RelationshipTypeConsistencyReport
            > dummyRelationshipLabelCheck () { returnnew
                               RelationshipTypeTokenRecordCheck(){ @Override publicvoid
                               check ( RelationshipTypeTokenRecord
            record
            ,
        CheckerEngine<
    RelationshipTypeTokenRecord

    , RelationshipTypeConsistencyReport> engine , RecordAccess
    records
        ) {} }; }REPORT check( RECORD record)
    {

    return check( reportClass ,checker , record ,
    records
        ); }void check( REPORTreport , RECORDrecord
    )

    { check (report , checker, record , records
    )
        ; }final REPORTcheck (CHECKER externalChecker, RECORD record)
    {

    return check (reportClass , externalChecker, record , records)
    ; }public static<RECORDextends AbstractBaseRecord, REPORTextendsConsistencyReport> REPORTcheck (Class < REPORT>
                  reportClass , RecordCheck <
    RECORD
        , REPORT > checker, RECORD record,
        finalRecordAccessStub records) {REPORT report= mock (reportClass
        ) ;check
    (

    report , checker, record ,records ) ; returnreport
    ; }public static <RECORD extendsAbstractBaseRecord,REPORT extendsConsistencyReport >void check (REPORT
                  report , RecordCheck <
    RECORD
        ,REPORT>checker ,RECORD record,finalRecordAccessStub records) { checker. check (record
        ,records.engine(record
    ,

    report) , records) ; records. checkDeferred () ; } <
    R
        extends AbstractBaseRecord>RaddChange (R oldRecord ,R
    newRecord

    ){ return records. addChange (oldRecord , newRecord )
    ;
        } <RextendsAbstractBaseRecord > Radd
    (

    R record) { return records
    .
        add (record); } DynamicRecordaddNodeDynamicLabels
    (

    DynamicRecord labels) { return records
    .
        addNodeDynamicLabels (labels); } DynamicRecordaddKeyName
    (

    DynamicRecord name) { return records
    .
        addPropertyKeyName (name); } DynamicRecordaddRelationshipTypeName
    (

    DynamicRecord name) { return records
    .
        addRelationshipTypeName (name); } DynamicRecordaddLabelName
    (

    DynamicRecord name ) {return records . addLabelName
    (
        name);} publicstaticDynamicRecordstring(DynamicRecordrecord ){
        record .setType
    (

    PropertyType . STRING .intValue ( ) )
    ;
        returnrecord;} publicstaticDynamicRecordarray(DynamicRecordrecord ){
        record .setType
    (

    PropertyType . ARRAY. intValue () ) ; return
    record
        ; } static PropertyBlockpropertyBlock(PropertyKeyTokenRecordkey,
        DynamicRecord value ){PropertyTypetype= value .getType( ) ;if(value. getType ()!= PropertyType
        .
            STRING&& value .getType
            ( )!=
        PropertyType
        . ARRAY) {fail ("Dynamic record must be either STRING or ARRAY" );returnnull; }return
    propertyBlock

    ( key , type, value .getId ( )) ; } public
    static
        PropertyBlock propertyBlock ( PropertyKeyTokenRecord key,PropertyTypetype
        ,longvalue) {PropertyBlockblock=new PropertyBlock ();block. setSingleBlock(key.getId( ) |( ( (long ) type. intValue(
        ) )<<
    24

    ) | (value << 28) ) ;return block ; }
    public
        static<Rextends AbstractBaseRecord >R
        inUse (R
    record

    ) { record. setInUse (true ) ;return record ; }
    public
        static<Rextends AbstractBaseRecord >R
        notInUse (R
    record

    ) { record.setInUse
    (
        false );
    return
record
