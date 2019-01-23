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
import org.neo4j.consistency.report.ConsistencyReport.NodeConsistencyReport;importorg.neo4j.consistency.report.ConsistencyReport.
PropertyConsistencyReport ;importorg.neo4j.consistency.report.ConsistencyReport.
PropertyKeyTokenConsistencyReport ;importorg.neo4j.consistency.report.ConsistencyReport.
RelationshipConsistencyReport ;importorg.neo4j.consistency.report.
ConsistencyReport .RelationshipTypeConsistencyReport;importorg.neo4j.consistency.
store .RecordAccess;importorg.neo4j.consistency.store.
RecordAccessStub ;importorg.neo4j.kernel.impl.store.
PropertyType ;importorg.neo4j.kernel.impl.store.RecordStore;
import org.neo4j.kernel.impl.store.record.AbstractBaseRecord;
import org.neo4j.kernel.impl.store.record.DynamicRecord;
import org.neo4j.kernel.impl.store.record.NeoStoreRecord;
import org.neo4j.kernel.impl.store.record.NodeRecord;
import org.neo4j.kernel.impl.store.record.PropertyBlock;
import org.neo4j.kernel.impl.store.record.PropertyKeyTokenRecord;
import org.neo4j.kernel.impl.store.record.PropertyRecord;
import org.neo4j.kernel.impl.store.record.RelationshipRecord;

import org .neo4j.kernel.impl.store.record.RelationshipTypeTokenRecord
; import staticorg.junit.jupiter.api

. Assertions . fail;import static org.
        mockito . Mockito.
        mock ; publicabstractclassRecordCheckTestBase <RECORDextends
AbstractBaseRecord
    , REPORT extends ConsistencyReport , CHECKER extendsRecordCheck<
    RECORD , REPORT >>
    { public staticfinalintNONE =-
    1 ; protectedfinal
    CHECKER checker ;private

    finalClass < REPORT> reportClass;protectedRecordAccessStub records; privateStagestage ;RecordCheckTestBase (CHECKER checker ,
    Class
        <REPORT >reportClass ,int [ ]cacheFields,MultiPassStore ...storesToCheck ){ this( checker ,reportClass , newStage
    .

    Adapter( false ,true ,"Test stage",cacheFields ), storesToCheck ); }RecordCheckTestBase ( CHECKER
    checker
        ,Class< REPORT >reportClass
        ,Stagestage , MultiPassStore...
        storesToCheck){ this .checker
        =checker ; this.
    reportClass

    = reportClass ;this .stage = stage
    ;
        initialize(storesToCheck ) ; }protected voidinitialize ( MultiPassStore...
        storesToCheck ) {this.records=newRecordAccessStub ( stage ,
        storesToCheck
            );if(stage.getCacheSlotSizes( ).length>0 ){
        records
    .

    cacheAccess ( ).setCacheSlotSizes( stage. getCacheSlotSizes()
    )
        ; } }publicstatic
        PrimitiveRecordCheck
            <NodeRecord
            , NodeConsistencyReport >dummyNodeCheck ( ){
                               returnnewNodeRecordCheck( ){ @Override
                               public void check
            (
            NodeRecord
        record,
    CheckerEngine

    < NodeRecord ,NodeConsistencyReport>engine ,RecordAccess records){
    }
        } ; }publicstatic
        PrimitiveRecordCheck
            <RelationshipRecord
            , RelationshipConsistencyReport >dummyRelationshipChecker ( ){
                               returnnewRelationshipRecordCheck( ){ @Override
                               public void check
            (
            RelationshipRecord
        record,
    CheckerEngine

    < RelationshipRecord ,RelationshipConsistencyReport>engine ,RecordAccess records){
    }
        } ; }public staticRecordCheck < PropertyRecord , PropertyConsistencyReport>dummyPropertyChecker
    (

    ) { return(record, engine, records)->
    {
        } ; }public static PrimitiveRecordCheck<NeoStoreRecord, NeoStoreConsistencyReport > dummyNeoStoreCheck ( )
        {
            returnnew
            NeoStoreCheck ( newPropertyChain < >(
                               from->null) ){ @Override
                               public void check
            (
            NeoStoreRecord
        record,
    CheckerEngine

    < NeoStoreRecord ,NeoStoreConsistencyReport>engine ,RecordAccess records)
            {}}; }public static RecordCheck <
    DynamicRecord
        , DynamicConsistencyReport >dummyDynamicCheck (RecordStore < DynamicRecord
        >
            store,
            DynamicStore dereference ){ return newDynamicRecordCheck
                               (store,dereference ){ @Override
                               public void check
            (
            DynamicRecord
        record,
    CheckerEngine

    < DynamicRecord ,DynamicConsistencyReport>engine ,RecordAccess records){
    }
        } ; }publicstatic
        RecordCheck
            <PropertyKeyTokenRecord
            , PropertyKeyTokenConsistencyReport >dummyPropertyKeyCheck ( ){
                               returnnewPropertyKeyTokenRecordCheck( ){ @Override
                               public void check
            (
            PropertyKeyTokenRecord
        record,
    CheckerEngine

    < PropertyKeyTokenRecord ,PropertyKeyTokenConsistencyReport>engine ,RecordAccess records){
    }
        } ; }publicstatic
        RecordCheck
            <RelationshipTypeTokenRecord
            , RelationshipTypeConsistencyReport >dummyRelationshipLabelCheck ( ){
                               returnnewRelationshipTypeTokenRecordCheck( ){ @Override
                               public void check
            (
            RelationshipTypeTokenRecord
        record,
    CheckerEngine

    < RelationshipTypeTokenRecord, RelationshipTypeConsistencyReport > engine
    ,
        RecordAccess records) {} }; }REPORT check (RECORD
    record

    ) {return check (reportClass , checker ,
    record
        ,records ); }void check( REPORT report,
    RECORD

    record ) {check ( report, checker , record
    ,
        records ); }final REPORTcheck (CHECKER externalChecker ,RECORD
    record

    ) { returncheck ( reportClass, externalChecker , record,
    records ); }publicstatic< RECORDextends AbstractBaseRecord,REPORTextends ConsistencyReport> REPORTcheck ( Class<
                  REPORT > reportClass ,
    RecordCheck
        < RECORD , REPORT> checker ,RECORD
        record, finalRecordAccessStub records) {REPORT report =mock
        ( reportClass)
    ;

    check ( report, checker ,record , records );
    return report; } publicstatic <RECORDextendsAbstractBaseRecord ,REPORT extendsConsistencyReport > voidcheck
                  ( REPORT report ,
    RecordCheck
        <RECORD,REPORT >checker ,RECORDrecord, finalRecordAccessStub records ){ checker .check
        (record,records.engine
    (

    record, report ), records ); records .checkDeferred ( ) ;
    }
        < RextendsAbstractBaseRecord> RaddChange ( RoldRecord
    ,

    RnewRecord ) {return records .addChange ( oldRecord ,
    newRecord
        ) ;}<R extends AbstractBaseRecord>
    R

    add (R record ) {
    return
        records .add(record ) ;}
    DynamicRecord

    addNodeDynamicLabels (DynamicRecord labels ) {
    return
        records .addNodeDynamicLabels(labels ) ;}
    DynamicRecord

    addKeyName (DynamicRecord name ) {
    return
        records .addPropertyKeyName(name ) ;}
    DynamicRecord

    addRelationshipTypeName (DynamicRecord name ) {
    return
        records .addRelationshipTypeName(name ) ;}
    DynamicRecord

    addLabelName ( DynamicRecord name) { return records
    .
        addLabelName(name) ;}publicstaticDynamicRecordstring( DynamicRecordrecord
        ) {record
    .

    setType ( PropertyType .STRING . intValue (
    )
        );returnrecord ;}publicstaticDynamicRecordarray( DynamicRecordrecord
        ) {record
    .

    setType ( PropertyType. ARRAY .intValue ( ) )
    ;
        return record ; }staticPropertyBlockpropertyBlock(PropertyKeyTokenRecord
        key , DynamicRecordvalue){PropertyType type =value. getType ();if( value .getType( )
        !=
            PropertyType. STRING &&value
            . getType(
        )
        != PropertyType. ARRAY) {fail ("Dynamic record must be either STRING or ARRAY");return null;
    }

    return propertyBlock ( key, type ,value . getId( ) ) ;
    }
        public static PropertyBlock propertyBlock (PropertyKeyTokenRecordkey,
        PropertyTypetype,long value){PropertyBlockblock = newPropertyBlock(); block.setSingleBlock(key. getId () | (( ( long) type.
        intValue ()
    )

    << 24 )| ( value<< 28 )) ; return block
    ;
        }publicstatic< R extendsAbstractBaseRecord
        > RinUse
    (

    R record ){ record .setInUse ( true) ; return record
    ;
        }publicstatic< R extendsAbstractBaseRecord
        > RnotInUse
    (

    R record ){record
    .
        setInUse (false
    )
;
