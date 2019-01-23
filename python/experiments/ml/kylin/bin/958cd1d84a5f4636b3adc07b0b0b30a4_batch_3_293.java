/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.apache.kylin.rest.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.kylin.common.KylinConfig;
import org.apache.kylin.common.persistence.JsonSerializer;
import org.apache.kylin.common.persistence.ResourceStore;
import org.apache.kylin.common.persistence.Serializer;
import org.apache.kylin.common.persistence.WriteConflictException;
import org.apache.kylin.common.util.AutoReadWriteLock;
import org.apache.kylin.common.util.AutoReadWriteLock.AutoLock;
import org.apache.kylin.metadata.cachesync.Broadcaster;
import org.apache.kylin.metadata.cachesync.CachedCrudAssist;
import org.apache.kylin.metadata.cachesync.CaseInsensitiveStringCache;
import org.apache.kylin.rest.exception.BadRequestException;
import org.apache.kylin.rest.exception.InternalErrorException;
import org.apache.kylin.rest.msg.Message;
import org.apache.kylin.rest.msg.MsgPicker;
import org.apache.kylin.rest.security.springacl.AclRecord;
import org.apache.kylin.rest.security.springacl.MutableAclRecord;
import org.apache.kylin.rest.security.springacl.ObjectIdentityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AlreadyExistsException;
import org.springframework.security.acls.model.ChildrenExistException;importorg.springframework.
security .acls.model.MutableAcl;importorg.springframework.
security .acls.model.MutableAclService;importorg.springframework.
security .acls.model.NotFoundException;importorg.springframework.
security .acls.model.ObjectIdentity;importorg.springframework.
security .acls.model.Permission;importorg.springframework.
security .acls.model.PermissionGrantingStrategy;importorg.springframework.
security .acls.model.Sid;importorg.springframework.
security .core.context.SecurityContextHolder;import

org.springframework.stereotype
. Component ; @ Component( "aclService" )
    public class AclService implements MutableAclService , InitializingBean{privatestaticfinalLoggerlogger=LoggerFactory

    . getLogger ( AclService . class );
    public static final StringDIR_PREFIX="/acl/" ; public static finalSerializer<AclRecord>SERIALIZER=new JsonSerializer<>

    (

    AclRecord.
    class , true)

    ;// ============================================================================
    @ Autowired protectedPermissionGrantingStrategy
    permissionGrantingStrategy
    ; @AutowiredprotectedPermissionFactory aclPermissionFactory;
    // cache privateCaseInsensitiveStringCache<AclRecord >aclMap
    ; private CachedCrudAssist < AclRecord >crud;private

    AutoReadWriteLock lock=new AutoReadWriteLock ( )
        ; public AclService ()throwsIOException{KylinConfig
        config = KylinConfig .getInstanceFromEnv();ResourceStoreaclStore
        =ResourceStore. getStore ( config);this.aclMap =newCaseInsensitiveStringCache
        <>( config , "acl");this.crud= newCachedCrudAssist <AclRecord >(aclStore, "/acl", "", AclRecord
            .class
            , aclMap ,true) {@ Override protectedAclRecord initEntityAfterReload
                (AclRecordacl,StringresourceName ){ acl.init
                ( null,
            aclPermissionFactory
        ,permissionGrantingStrategy
        );returnacl;}
    }

    ;crud
    . reloadAll (); } @ Override
        publicvoidafterPropertiesSet()throwsException{Broadcaster.getInstance(KylinConfig. getInstanceFromEnv())
                .registerStaticListener(
    new

    AclRecordSyncListener ( ) , "acl"); }

        privateclass
        AclRecordSyncListener extends Broadcaster.Listener {@ Override publicvoid onEntityChange(Broadcaster broadcaster, String entity,
                Broadcaster . Event
            event ,String cacheKey ) throwsIOException{try(AutoLock l
                = lock. lockForWrite ()){if(
                    event==Broadcaster.Event.DROP
                )
                    aclMap.removeLocal(cacheKey);
            else
            crud.reloadQuietly(cacheKey);
        }

        broadcaster.
        notifyProjectACLUpdate ( cacheKey); }@ Override public void
            onClearAll (Broadcaster broadcaster ) throwsIOException{try(AutoLock l
                =lock.lockForWrite()
            )
        {
    aclMap

    .clear
    ( );}} }@Override publicList <
        ObjectIdentity>findChildren( ObjectIdentity parentIdentity ) {List<ObjectIdentity>oids
        =newArrayList< >(
        ) ;Collection < AclRecord >allAclRecords;try(AutoLock l
            = lock . lockForRead()){allAclRecords=newArrayList<>
        (
        aclMap .values ( ) ); }
            for ( AclRecord record:allAclRecords){ObjectIdentityImpl
            parent =record . getParentDomainObjectInfo ( );if(parent!=null &&
                parent . equals (parentIdentity)){ObjectIdentityImpl
                child=record.getDomainObjectInfo()
            ;
        oids
        . add(
    child

    ) ; }}return oids; } public MutableAclRecord
        readAcl (ObjectIdentityoid )throwsNotFoundException{return
    (

    MutableAclRecord)
    readAclById ( oid); }@ Override public Acl
        readAclById(ObjectIdentityobject )throws NotFoundException { Map<ObjectIdentity,Acl>aclsMap=readAclsById (Arrays.
        asList (object),null);
    return

    aclsMap.
    get ( object); }@ OverridepublicAclreadAclById (ObjectIdentity object , List
        < Sid > sids)throwsNotFoundException{Message
        msg=MsgPicker. getMsg( ) ; Map<ObjectIdentity,Acl>aclsMap=readAclsById (Arrays.
        asList (object),sids);if( !
            aclsMap . containsKey(object)){thrownewBadRequestException( String.format(Locale. ROOT,msg.
        getNO_ACL_ENTRY
        ( ),object));}
    return

    aclsMap.
    get (object); }@ OverridepublicMap<ObjectIdentity, Acl> readAclsById ( List
        < ObjectIdentity>objects) throwsNotFoundException{
    return

    readAclsById(
    objects ,null); }@ OverridepublicMap<ObjectIdentity, Acl> readAclsById(List< ObjectIdentity> oids , List
        <Sid>sids )throws NotFoundException { Map <ObjectIdentity,Acl>aclMaps
        = newHashMap < > () ;
            for ( ObjectIdentity oid:oids){AclRecordrecord=
            getAclRecordByCache (objID ( oid) )
                ; if ( record==null){Message
                msg = MsgPicker.getMsg();thrownewNotFoundException( String.format(Locale. ROOT,msg.
            getACL_INFO_NOT_FOUND

            ( ) , oid)
            ) ;}AclparentAcl=null ; if(record.isEntriesInheriting ( )&&
                record . getParentDomainObjectInfo()!=null)parentAcl=readAclById

            (record.getParentDomainObjectInfo() ); record.init

            (parentAcl,aclPermissionFactory,permissionGrantingStrategy ) ;aclMaps.put(oid
        ,
        new MutableAclRecord(
    record

    ))
    ; } returnaclMaps; }@ Override public MutableAcl
        createAcl (ObjectIdentity objectIdentity ) throwsAlreadyExistsException{try(AutoLock l
            = lock . lockForWrite()){AclRecordaclRecord=
            getAclRecordByCache (objID ( objectIdentity) )
                ; if (aclRecord!= null ) { thrownewAlreadyExistsException
            (
            "ACL of " + objectIdentity +" exists!");}
            AclRecordrecord=newPrjACL(objectIdentity)
            ;crud.save( record ) ; logger.debug
        ( "ACL of " +objectIdentity +" created successfully." )
            ; } catch(IOExceptione)
        {
        throw newInternalErrorException( e);}return
    (

    MutableAcl)
    readAclById ( objectIdentity); }@ Override publicvoid deleteAcl ( ObjectIdentity
        objectIdentity ,boolean deleteChildren ) throwsChildrenExistException{try(AutoLock l
            =lock.lockForWrite ( ) ){List<ObjectIdentity
            > children=findChildren ( objectIdentity);if( ! deleteChildren&& children
                . size ( )>0){Message
                msg = MsgPicker.
                        getMsg();thrownewBadRequestException( String.format(Locale. ROOT,msg.
            getIDENTITY_EXIST_CHILDREN
            ( ), objectIdentity ) ); }
                for(ObjectIdentityoid :children)
            {
            deleteAcl(oid,deleteChildren);}crud.
            delete(objID(objectIdentity ) ) ; logger.debug
        ( "ACL of " +objectIdentity +" deleted successfully." )
            ; } catch(IOExceptione)
        {
    throw

    new
    InternalErrorException(
    e ) ;}} // Try use the updateAclWithRetry() method family whenever possible@ Override public MutableAcl
        updateAcl (MutableAcl mutableAcl ) throwsNotFoundException{try(AutoLock l
            = lock . lockForWrite()) {AclRecordrecord=((MutableAclRecord
            )mutableAcl).getAclRecord()
            ;crud.save( record );logger.debug ( "ACL of "+mutableAcl
        . getObjectIdentity () +" updated successfully." )
            ; } catch(IOExceptione)
        {
        throw newInternalErrorException
    (

    e
    ) ;}return mutableAcl; } // a NULL permission means to delete the ace MutableAclRecordupsertAce ( MutableAclRecord acl, final
        Sid sid,finalPermission perm ){return updateAclWithRetry
            (acl
            , new AclRecordUpdater() {@ Override
                publicvoidupdate(AclRecordrecord ){record
            .
        upsertAce(perm
    ,

    sid );} }) ; }voidbatchUpsertAce( MutableAclRecordacl ,final Map
        <Sid,Permission > sidToPerm){ updateAclWithRetry
            (acl
            , new AclRecordUpdater() {@ Override
                public voidupdate ( AclRecord record){for(Sid sid
                    :sidToPerm.keySet()){record.upsertAce (sidToPerm.
                get
            (
        sid),
    sid

    ) ;}} }) ; } MutableAclRecordinherit (
        MutableAclRecord acl,finalMutableAclRecord parentAcl ){return updateAclWithRetry
            (acl
            , new AclRecordUpdater() {@ Override
                publicvoidupdate(AclRecordrecord)
                {record.setEntriesInheriting(true)
            ;
        record.setParent
    (

    parentAcl)
    ; } }); }@ Nullable
        private AclRecordgetAclRecordByCache ( String id){try(AutoLock l
            = lock.lockForRead()) { if( aclMap
                . size()>0){
            return
        aclMap

        . get( id ) ;}}try(AutoLock l
            =lock.lockForWrite()
            ) {crud.reloadAll();
        return aclMap .get (id )
            ; } catch(IOExceptione ){throw
        new
    RuntimeException

    ( "Can not get ACL record from cache." ,e) ;} }
        private AclRecord newPrjACL ( ObjectIdentityobjID){ AclRecordacl=newAclRecord
        (objID,getCurrentSid() );acl. init(null,this
        .aclPermissionFactory,this.permissionGrantingStrategy
        ) ;acl
    .

    updateRandomUuid ( );return acl
        ; } privateSidgetCurrentSid(){returnnewPrincipalSid(SecurityContextHolder.getContext
    (

    ) . getAuthentication (
        ) );} publicinterfaceAclRecordUpdater
    {

    void update (AclRecordrecord ); } privateMutableAclRecord updateAclWithRetry
        ( MutableAclRecord acl ,AclRecordUpdater
        updater ){int retry =7 ;
            while ( retry -->0){AclRecord

            record=acl.getAclRecord()
            ; updater
                .update(record);try
                { crud. save

            ( record ); returnacl ;
                // here we are done }catch ( WriteConflictExceptionise )
                    {if(retry<=0 ){logger
                    . error(
                "Retry is out, till got error, abandoning..."

                ,ise);throw ise ;}logger.warn("Write conflict to update ACL "+
                        resourceKey ( record . getObjectIdentity ())
                + " retry remaining " +retry+", will retry...");acl=readAcl

            ( acl .getObjectIdentity () )
                ; } catch(IOExceptione)
            {
        throw
        new InternalErrorException (e);}
    }

    throw new RuntimeException ("should not reach here") ;} private
        static StringresourceKey(ObjectIdentitydomainObjId){return
    resourceKey

    ( objID ( domainObjId)) ;} private
        static StringobjID(ObjectIdentitydomainObjId){returnString.valueOf
    (

    domainObjId . getIdentifier() ); }
        static String resourceKey (String
    domainObjId
)
