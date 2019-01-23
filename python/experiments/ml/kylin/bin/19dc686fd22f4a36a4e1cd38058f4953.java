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
import org.slf4j.LoggerFactory;importorg.springframework
. beans.factory.InitializingBean;importorg.springframework.beans
. factory.annotation.Autowired;importorg.springframework.security
. acls.domain.PermissionFactory;importorg.springframework.security
. acls.domain.PrincipalSid;importorg.springframework.security
. acls.model.Acl;importorg.springframework.security
. acls.model.AlreadyExistsException;importorg.springframework.security
. acls.model.ChildrenExistException;importorg.springframework.security
. acls.model.MutableAcl;importorg.springframework.security
. acls.model.MutableAclService;importorg.springframework.security
. acls.model.NotFoundException;importorg.springframework.security
. acls.model.ObjectIdentity;importorg.springframework.security
. acls.model.Permission;importorg.springframework.security
. acls.model.PermissionGrantingStrategy;importorg.springframework.security
. acls.model.Sid;importorg.springframework.security
. core.context.SecurityContextHolder;importorg

.springframework.stereotype.
Component ; @ Component ("aclService" ) public
    class AclService implements MutableAclService , InitializingBean {privatestaticfinalLoggerlogger=LoggerFactory.

    getLogger ( AclService . class ) ;public
    static final String DIR_PREFIX="/acl/"; public static final Serializer<AclRecord>SERIALIZER=newJsonSerializer <>(

    AclRecord

    .class
    , true );

    // ============================================================================@
    Autowired protected PermissionGrantingStrategypermissionGrantingStrategy
    ;
    @ AutowiredprotectedPermissionFactoryaclPermissionFactory ;// cache
    private CaseInsensitiveStringCache<AclRecord> aclMap;
    private CachedCrudAssist < AclRecord > crud;privateAutoReadWriteLock

    lock =newAutoReadWriteLock ( ) ;
        public AclService ( )throwsIOException{KylinConfigconfig
        = KylinConfig . getInstanceFromEnv();ResourceStoreaclStore=
        ResourceStore.getStore ( config );this.aclMap= newCaseInsensitiveStringCache<
        >(config , "acl" );this.crud=new CachedCrudAssist< AclRecord> (aclStore,"/acl" ,"" ,AclRecord .
            class,
            aclMap , true){ @Override protected AclRecordinitEntityAfterReload (
                AclRecordacl,StringresourceName) {acl .init(
                null ,aclPermissionFactory
            ,
        permissionGrantingStrategy)
        ;returnacl;}}
    ;

    crud.
    reloadAll ( );} @ Override public
        voidafterPropertiesSet()throwsException{Broadcaster.getInstance(KylinConfig.getInstanceFromEnv ()).
                registerStaticListener(new
    AclRecordSyncListener

    ( ) , "acl" );} private

        classAclRecordSyncListener
        extends Broadcaster .Listener{ @Override public voidonEntityChange (Broadcasterbroadcaster ,String entity ,Broadcaster
                . Event event
            , StringcacheKey ) throws IOException{try(AutoLockl =
                lock .lockForWrite ( )){if(event
                    ==Broadcaster.Event.DROP)
                aclMap
                    .removeLocal(cacheKey);else
            crud
            .reloadQuietly(cacheKey);}
        broadcaster

        .notifyProjectACLUpdate
        ( cacheKey );} @Override public void onClearAll
            ( Broadcasterbroadcaster ) throws IOException{try(AutoLockl =
                lock.lockForWrite())
            {
        aclMap
    .

    clear(
    ) ;}}} @Overridepublic List< ObjectIdentity
        >findChildren(ObjectIdentity parentIdentity ) { List<ObjectIdentity>oids=
        newArrayList<> ()
        ; Collection< AclRecord > allAclRecords;try(AutoLockl =
            lock . lockForRead ()){allAclRecords=newArrayList<>(
        aclMap
        . values( ) ) ;} for
            ( AclRecord record :allAclRecords){ObjectIdentityImplparent
            = record. getParentDomainObjectInfo ( ) ;if(parent!=null&& parent
                . equals ( parentIdentity)){ObjectIdentityImplchild
                =record.getDomainObjectInfo();
            oids
        .
        add (child
    )

    ; } }returnoids ;} public MutableAclRecord readAcl
        ( ObjectIdentityoid) throwsNotFoundException{return(
    MutableAclRecord

    )readAclById
    ( oid );} @Override public Acl readAclById
        (ObjectIdentityobject) throwsNotFoundException { Map <ObjectIdentity,Acl>aclsMap=readAclsById( Arrays.asList
        ( object),null);return
    aclsMap

    .get
    ( object );} @Override publicAclreadAclById( ObjectIdentityobject , List <
        Sid > sids )throwsNotFoundException{Messagemsg
        =MsgPicker.getMsg () ; Map <ObjectIdentity,Acl>aclsMap=readAclsById( Arrays.asList
        ( object),sids);if(! aclsMap
            . containsKey (object)){thrownewBadRequestException(String .format(Locale.ROOT ,msg.getNO_ACL_ENTRY
        (
        ) ,object));}return
    aclsMap

    .get
    ( object);} @Override publicMap<ObjectIdentity,Acl >readAclsById ( List <
        ObjectIdentity >objects)throws NotFoundException{return
    readAclsById

    (objects
    , null);} @Override publicMap<ObjectIdentity,Acl >readAclsById (List<ObjectIdentity >oids , List <
        Sid>sids) throwsNotFoundException { Map < ObjectIdentity,Acl>aclMaps=
        new HashMap< > ( ); for
            ( ObjectIdentity oid :oids){AclRecordrecord=getAclRecordByCache
            ( objID( oid )) ;
                if ( record ==null){Messagemsg
                = MsgPicker .getMsg();thrownewNotFoundException(String .format(Locale.ROOT ,msg.getACL_INFO_NOT_FOUND
            (

            ) , oid ))
            ; }AclparentAcl=null; if (record.isEntriesInheriting( ) &&record
                . getParentDomainObjectInfo ()!=null)parentAcl=readAclById(

            record.getParentDomainObjectInfo()) ;record .init(

            parentAcl,aclPermissionFactory,permissionGrantingStrategy) ; aclMaps.put(oid,
        new
        MutableAclRecord (record
    )

    );
    } return aclMaps;} @Override public MutableAcl createAcl
        ( ObjectIdentityobjectIdentity ) throws AlreadyExistsException{try(AutoLockl =
            lock . lockForWrite ()){AclRecordaclRecord=getAclRecordByCache
            ( objID( objectIdentity )) ;
                if ( aclRecord!=null ) { throw newAlreadyExistsException(
            "ACL of "
            + objectIdentity + " exists!");}AclRecord
            record=newPrjACL(objectIdentity);
            crud.save(record ) ; logger .debug(
        "ACL of " + objectIdentity+ " created successfully.") ;
            } catch (IOExceptione){
        throw
        new InternalErrorException(e );}return(
    MutableAcl

    )readAclById
    ( objectIdentity );} @Override public voiddeleteAcl ( ObjectIdentity objectIdentity
        , booleandeleteChildren ) throws ChildrenExistException{try(AutoLockl =
            lock.lockForWrite( ) ) {List<ObjectIdentity>
            children =findChildren( objectIdentity );if(! deleteChildren &&children .
                size ( ) >0){Messagemsg
                = MsgPicker .getMsg
                        ();thrownewBadRequestException(String .format(Locale.ROOT ,msg.getIDENTITY_EXIST_CHILDREN
            (
            ) ,objectIdentity ) ) ;} for
                (ObjectIdentityoid: children){
            deleteAcl
            (oid,deleteChildren);}crud.delete
            (objID(objectIdentity) ) ; logger .debug(
        "ACL of " + objectIdentity+ " deleted successfully.") ;
            } catch (IOExceptione){
        throw
    new

    InternalErrorException
    (e
    ) ; }}// Try use the updateAclWithRetry() method family whenever possible @Override public MutableAcl updateAcl
        ( MutableAclmutableAcl ) throws NotFoundException{try(AutoLockl =
            lock . lockForWrite ()){ AclRecordrecord=((MutableAclRecord)
            mutableAcl).getAclRecord();
            crud.save(record ) ;logger.debug( "ACL of " +mutableAcl.
        getObjectIdentity ( )+ " updated successfully.") ;
            } catch (IOExceptione){
        throw
        new InternalErrorException(
    e

    )
    ; }returnmutableAcl ;} // a NULL permission means to delete the ace MutableAclRecord upsertAce( MutableAclRecord acl ,final Sid
        sid ,finalPermissionperm ) {returnupdateAclWithRetry (
            acl,
            new AclRecordUpdater (){ @Override public
                voidupdate(AclRecordrecord) {record.
            upsertAce
        (perm,
    sid

    ) ;}} ); } voidbatchUpsertAce(MutableAclRecord acl, finalMap <
        Sid,Permission> sidToPerm ){updateAclWithRetry (
            acl,
            new AclRecordUpdater (){ @Override public
                void update( AclRecord record ){for(Sidsid :
                    sidToPerm.keySet()){record.upsertAce( sidToPerm.get
                (
            sid
        ),sid
    )

    ; }}} ); } MutableAclRecord inherit( MutableAclRecord
        acl ,finalMutableAclRecordparentAcl ) {returnupdateAclWithRetry (
            acl,
            new AclRecordUpdater (){ @Override public
                voidupdate(AclRecordrecord){
                record.setEntriesInheriting(true);
            record
        .setParent(
    parentAcl

    );
    } } );} @Nullable private
        AclRecord getAclRecordByCache( String id ){try(AutoLockl =
            lock .lockForRead()){ if (aclMap .
                size ()>0){return
            aclMap
        .

        get (id ) ; }}try(AutoLockl =
            lock.lockForWrite())
            { crud.reloadAll();return
        aclMap . get( id) ;
            } catch (IOExceptione) {thrownew
        RuntimeException
    (

    "Can not get ACL record from cache." , e); }} private
        AclRecord newPrjACL ( ObjectIdentity objID){AclRecord acl=newAclRecord(
        objID,getCurrentSid()) ;acl.init (null,this.
        aclPermissionFactory,this.permissionGrantingStrategy)
        ; acl.
    updateRandomUuid

    ( ) ;returnacl ;
        } private SidgetCurrentSid(){returnnewPrincipalSid(SecurityContextHolder.getContext(
    )

    . getAuthentication ( )
        ) ;}public interfaceAclRecordUpdater{
    void

    update ( AclRecordrecord) ;} private MutableAclRecordupdateAclWithRetry (
        MutableAclRecord acl , AclRecordUpdaterupdater
        ) {intretry = 7; while
            ( retry -- >0){AclRecordrecord

            =acl.getAclRecord();
            updater .
                update(record);try{
                crud .save (

            record ) ;return acl; // here we are done
                } catch( WriteConflictException ise) {
                    if(retry<=0) {logger.
                    error ("Retry is out, till got error, abandoning..."
                ,

                ise);throwise ; }logger.warn("Write conflict to update ACL "+resourceKey
                        ( record . getObjectIdentity ( ))+
                " retry remaining " + retry+", will retry...");acl=readAcl(

            acl . getObjectIdentity( )) ;
                } catch (IOExceptione){
            throw
        new
        InternalErrorException ( e);}}
    throw

    new RuntimeException ( "should not reach here"); }private static
        String resourceKey(ObjectIdentitydomainObjId){returnresourceKey
    (

    objID ( domainObjId )); }private static
        String objID(ObjectIdentitydomainObjId){returnString.valueOf(
    domainObjId

    . getIdentifier ()) ;} static
        String resourceKey ( StringdomainObjId
    )
{
