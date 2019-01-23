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
import org.neo4j.consistency.report.ConsistencyReport.PropertyConsistencyReport;importorg.neo4j
. consistency.report.ConsistencyReport.PropertyKeyTokenConsistencyReport;importorg.neo4j
. consistency.report.ConsistencyReport.RelationshipConsistencyReport;importorg.neo4j
. consistency.report.ConsistencyReport.RelationshipTypeConsistencyReport;importorg
. neo4j.consistency.store.RecordAccess;importorg
. neo4j.consistency.store.RecordAccessStub;importorg.neo4j
. kernel.impl.store.PropertyType;importorg.neo4j
. kernel.impl.store.RecordStore;importorg.neo4j.kernel
. impl.store.record.AbstractBaseRecord;importorg.neo4j.kernel
. impl.store.record.DynamicRecord;importorg.neo4j.kernel
. impl.store.record.NeoStoreRecord;importorg.neo4j.kernel
. impl.store.record.NodeRecord;importorg.neo4j.kernel
. impl.store.record.PropertyBlock;importorg.neo4j.kernel
. impl.store.record.PropertyKeyTokenRecord;importorg.neo4j.kernel
. impl.store.record.PropertyRecord;importorg.neo4j.kernel
. impl.store.record.RelationshipRecord;importorg.neo4j.kernel

. impl .store.record.RelationshipTypeTokenRecord;importstaticorg.junit
. jupiter .api.Assertions.fail;import

static org . mockito.Mockito . mock;
        public abstract classRecordCheckTestBase
        < RECORD extendsAbstractBaseRecord,REPORT extendsConsistencyReport,
CHECKER
    extends RecordCheck < RECORD , REPORT >>{
    public static final intNONE
    = - 1;protectedfinal CHECKERchecker
    ; private finalClass
    < REPORT >reportClass

    ;protected RecordAccessStub records; privateStagestage; RecordCheckTestBase( CHECKERchecker, Class< REPORT> reportClass ,
    int
        [] cacheFields, MultiPassStore... storesToCheck ){this( checker, reportClass, newStage . Adapter( false ,true
    ,

    "Test stage", cacheFields ), storesToCheck);} RecordCheckTestBase( CHECKER checker, Class< REPORT >
    reportClass
        ,Stagestage , MultiPassStore...
        storesToCheck){ this .checker
        =checker; this .reportClass
        =reportClass ; this.
    stage

    = stage ;initialize (storesToCheck ) ;
    }
        protectedvoidinitialize ( MultiPassStore ...storesToCheck ){ this .records
        = new RecordAccessStub(stage,storesToCheck); if ( stage
        .
            getCacheSlotSizes().length>0) {records.cacheAccess( ).
        setCacheSlotSizes
    (

    stage . getCacheSlotSizes()) ;} }publicstatic
    PrimitiveRecordCheck
        < NodeRecord ,NodeConsistencyReport>
        dummyNodeCheck
            ()
            { return newNodeRecordCheck ( ){
                               @Overridepublicvoid check( NodeRecordrecord
                               , CheckerEngine <
            NodeRecord
            ,
        NodeConsistencyReport>
    engine

    , RecordAccess records){} }; }publicstatic
    PrimitiveRecordCheck
        < RelationshipRecord ,RelationshipConsistencyReport>
        dummyRelationshipChecker
            ()
            { return newRelationshipRecordCheck ( ){
                               @Overridepublicvoid check( RelationshipRecordrecord
                               , CheckerEngine <
            RelationshipRecord
            ,
        RelationshipConsistencyReport>
    engine

    , RecordAccess records){} }; }publicstatic
    RecordCheck
        < PropertyRecord ,PropertyConsistencyReport >dummyPropertyChecker ( ) { return(record
    ,

    engine , records)->{ }; }publicstatic
    PrimitiveRecordCheck
        < NeoStoreRecord ,NeoStoreConsistencyReport > dummyNeoStoreCheck(){ return new NeoStoreCheck ( new
        PropertyChain
            <>
            ( from ->null ) ){
                               @Overridepublicvoid check( NeoStoreRecordrecord
                               , CheckerEngine <
            NeoStoreRecord
            ,
        NeoStoreConsistencyReport>
    engine

    , RecordAccess records){} }; }public
            staticRecordCheck<DynamicRecord ,DynamicConsistencyReport > dummyDynamicCheck (
    RecordStore
        < DynamicRecord >store ,DynamicStore dereference )
        {
            returnnew
            DynamicRecordCheck ( store, dereference ){
                               @Overridepublicvoid check( DynamicRecordrecord
                               , CheckerEngine <
            DynamicRecord
            ,
        DynamicConsistencyReport>
    engine

    , RecordAccess records){} }; }publicstatic
    RecordCheck
        < PropertyKeyTokenRecord ,PropertyKeyTokenConsistencyReport>
        dummyPropertyKeyCheck
            ()
            { return newPropertyKeyTokenRecordCheck ( ){
                               @Overridepublicvoid check( PropertyKeyTokenRecordrecord
                               , CheckerEngine <
            PropertyKeyTokenRecord
            ,
        PropertyKeyTokenConsistencyReport>
    engine

    , RecordAccess records){} }; }publicstatic
    RecordCheck
        < RelationshipTypeTokenRecord ,RelationshipTypeConsistencyReport>
        dummyRelationshipLabelCheck
            ()
            { return newRelationshipTypeTokenRecordCheck ( ){
                               @Overridepublicvoid check( RelationshipTypeTokenRecordrecord
                               , CheckerEngine <
            RelationshipTypeTokenRecord
            ,
        RelationshipTypeConsistencyReport>
    engine

    , RecordAccessrecords ) { }
    }
        ; }REPORT check( RECORDrecord ){ return check(
    reportClass

    , checker, record ,records ) ; }
    void
        check( REPORTreport ,RECORD record) { check(
    report

    , checker ,record , records) ; } final
    REPORT
        check (CHECKER externalChecker, RECORDrecord ){ return check(
    reportClass

    , externalChecker ,record , records) ; } publicstatic
    < RECORDextends AbstractBaseRecord,REPORTextends ConsistencyReport> REPORTcheck(Class <REPORT >reportClass , RecordCheck<
                  RECORD , REPORT >
    checker
        , RECORD record ,final RecordAccessStub records)
        {REPORT report= mock( reportClass) ; check(
        report ,checker
    ,

    record , records) ; returnreport ; } publicstatic
    < RECORDextends AbstractBaseRecord ,REPORT extendsConsistencyReport>void check( REPORTreport , RecordCheck<
                  RECORD , REPORT >
    checker
        ,RECORDrecord, finalRecordAccessStub records){checker .check ( record, records .engine
        (record,report),
    records

    ); records .checkDeferred ( ); } <R extends AbstractBaseRecord >
    R
        addChange (RoldRecord, RnewRecord ) {return
    records

    .addChange ( oldRecord, newRecord ); } < R
    extends
        AbstractBaseRecord >Radd( R record)
    {

    return records. add ( record
    )
        ; }DynamicRecordaddNodeDynamicLabels( DynamicRecord labels)
    {

    return records. addNodeDynamicLabels ( labels
    )
        ; }DynamicRecordaddKeyName( DynamicRecord name)
    {

    return records. addPropertyKeyName ( name
    )
        ; }DynamicRecordaddRelationshipTypeName( DynamicRecord name)
    {

    return records. addRelationshipTypeName ( name
    )
        ; }DynamicRecordaddLabelName( DynamicRecord name)
    {

    return records . addLabelName( name ) ;
    }
        publicstaticDynamicRecordstring (DynamicRecordrecord){record. setType(
        PropertyType .STRING
    .

    intValue ( ) ); return record ;
    }
        publicstaticDynamicRecordarray (DynamicRecordrecord){record. setType(
        PropertyType .ARRAY
    .

    intValue ( )) ; returnrecord ; } static
    PropertyBlock
        propertyBlock ( PropertyKeyTokenRecord key,DynamicRecordvalue){
        PropertyType type =value.getType( ) ;if( value .getType()!= PropertyType .STRING&& value
        .
            getType( ) !=PropertyType
            . ARRAY)
        {
        fail ("Dynamic record must be either STRING or ARRAY" ); returnnull ;}returnpropertyBlock( key,
    type

    , value . getId( ) ); } publicstatic PropertyBlock propertyBlock (
    PropertyKeyTokenRecord
        key , PropertyType type ,longvalue)
        {PropertyBlockblock= newPropertyBlock(); block .setSingleBlock(key. getId()|(( ( long) type .intValue ( )) <<24
        ) |(
    value

    << 28 )) ; returnblock ; }public static < R
    extends
        AbstractBaseRecord>RinUse ( Rrecord
        ) {record
    .

    setInUse ( true) ; returnrecord ; }public static < R
    extends
        AbstractBaseRecord>RnotInUse ( Rrecord
        ) {record
    .

    setInUse ( false);
    return
        record ;}
    protected
CHECKER
