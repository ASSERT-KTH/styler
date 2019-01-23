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
import org.apache.kylin.rest.security.
springacl.MutableAclRecord;importorg
. apache.kylin.rest.security.springacl.ObjectIdentityImpl;importorg
. slf4j.Logger;importorg
. slf4j.LoggerFactory;importorg
. springframework.beans.factory.InitializingBean;importorg
. springframework.beans.factory.annotation.Autowired;importorg
. springframework.security.acls.domain.PermissionFactory;importorg
. springframework.security.acls.domain.PrincipalSid;importorg
. springframework.security.acls.model.Acl;importorg
. springframework.security.acls.model.AlreadyExistsException;importorg
. springframework.security.acls.model.ChildrenExistException;importorg
. springframework.security.acls.model.MutableAcl;importorg
. springframework.security.acls.model.MutableAclService;importorg
. springframework.security.acls.model.NotFoundException;importorg
. springframework.security.acls.model.ObjectIdentity;importorg
. springframework.security.acls.model.Permission;importorg
. springframework.security.acls.model.PermissionGrantingStrategy;importorg
. springframework.security.acls.model.Sid;importorg
. springframework.security.core.context.SecurityContextHolder;importorg
. springframework.stereotype.Component;@Component

("aclService")publicclass
AclService implements MutableAclService , InitializingBean{ private static
    final Logger logger = LoggerFactory . getLogger(AclService.class);publicstatic

    final String DIR_PREFIX = "/acl/" ; publicstatic
    final Serializer < AclRecord>SERIALIZER= new JsonSerializer < >(AclRecord.class,true) ;// ============================================================================@

    Autowired

    protectedPermissionGrantingStrategy
    permissionGrantingStrategy ; @Autowired

    protectedPermissionFactory
    aclPermissionFactory ; // cacheprivate
    CaseInsensitiveStringCache
    < AclRecord>aclMap; privateCachedCrudAssist
    < AclRecord>crud; privateAutoReadWriteLock
    lock = new AutoReadWriteLock ( );publicAclService

    ( )throwsIOException { KylinConfig config
        = KylinConfig . getInstanceFromEnv();ResourceStoreaclStore
        = ResourceStore . getStore(config);this.
        aclMap=new CaseInsensitiveStringCache < >(config,"acl") ;this.
        crud=new CachedCrudAssist < AclRecord>(aclStore,"/acl", "", AclRecord. class,aclMap, true) {@ Override
            protectedAclRecord
            initEntityAfterReload ( AclRecordacl, StringresourceName ) {acl .
                init(null,aclPermissionFactory, permissionGrantingStrategy) ;returnacl
                ; }}
            ;
        crud.
        reloadAll();}@
    Override

    publicvoid
    afterPropertiesSet ( )throwsException { Broadcaster .
        getInstance(KylinConfig.getInstanceFromEnv()).registerStaticListener(newAclRecordSyncListener( ),"acl")
                ;}private
    class

    AclRecordSyncListener extends Broadcaster . Listener{@ Override

        publicvoid
        onEntityChange ( Broadcasterbroadcaster, Stringentity , Broadcaster. Eventevent, StringcacheKey ) throwsIOException
                { try (
            AutoLock l= lock . lockForWrite()){if (
                event ==Broadcaster . Event.DROP)aclMap.
                    removeLocal(cacheKey);elsecrud
                .
                    reloadQuietly(cacheKey);}broadcaster
            .
            notifyProjectACLUpdate(cacheKey);}@
        Override

        publicvoid
        onClearAll ( Broadcasterbroadcaster) throwsIOException { try (
            AutoLock l= lock . lockForWrite()){aclMap .
                clear();}}
            }
        @
    Override

    publicList
    < ObjectIdentity>findChildren( ObjectIdentityparentIdentity) {List <
        ObjectIdentity>oids= new ArrayList < >();Collection<
        AclRecord>allAclRecords; try(
        AutoLock l= lock . lockForRead()){allAclRecords =
            new ArrayList < >(aclMap.values());}for
        (
        AclRecord record: allAclRecords ) {ObjectIdentityImpl parent
            = record . getParentDomainObjectInfo();if(
            parent !=null && parent . equals(parentIdentity)){ObjectIdentityImpl child
                = record . getDomainObjectInfo();oids.
                add(child);}}
            return
        oids
        ; }public
    MutableAclRecord

    readAcl ( ObjectIdentityoid) throwsNotFoundException { return (
        MutableAclRecord )readAclById( oid);}@
    Override

    publicAcl
    readAclById ( ObjectIdentityobject) throwsNotFoundException { Map <
        ObjectIdentity,Acl> aclsMap= readAclsById ( Arrays.asList(object),null) ;returnaclsMap
        . get(object);}@
    Override

    publicAcl
    readAclById ( ObjectIdentityobject, List< Sid>sids) throwsNotFoundException { Message msg
        = MsgPicker . getMsg();Map<
        ObjectIdentity,Acl> aclsMap= readAclsById ( Arrays.asList(object),sids) ;if(
        ! aclsMap.containsKey(object)){throw new
            BadRequestException ( String.format(Locale.ROOT,msg. getNO_ACL_ENTRY(),object) );}return
        aclsMap
        . get(object);}@
    Override

    publicMap
    < ObjectIdentity,Acl> readAclsById( List<ObjectIdentity>objects) throwsNotFoundException { return readAclsById
        ( objects,null) ;}@
    Override

    publicMap
    < ObjectIdentity,Acl> readAclsById( List<ObjectIdentity>oids, List< Sid>sids) throwsNotFoundException { Map <
        ObjectIdentity,Acl> aclMaps= new HashMap < >();for(
        ObjectIdentity oid: oids ) {AclRecord record
            = getAclRecordByCache ( objID(oid));if(
            record ==null ) {Message msg
                = MsgPicker . getMsg();thrownew
                NotFoundException ( String.format(Locale.ROOT,msg. getACL_INFO_NOT_FOUND(),oid) );}Acl
            parentAcl

            = null ; if(
            record .isEntriesInheriting()&&record . getParentDomainObjectInfo()!=null ) parentAcl=
                readAclById ( record.getParentDomainObjectInfo());record.

            init(parentAcl,aclPermissionFactory, permissionGrantingStrategy) ;aclMaps.

            put(oid,newMutableAclRecord ( record));}return
        aclMaps
        ; }@
    Override

    publicMutableAcl
    createAcl ( ObjectIdentityobjectIdentity) throwsAlreadyExistsException { try (
        AutoLock l= lock . lockForWrite()){AclRecord aclRecord
            = getAclRecordByCache ( objID(objectIdentity));if(
            aclRecord !=null ) {throw new
                AlreadyExistsException ( "ACL of "+objectIdentity + " exists!" ) ;}AclRecord
            record
            = newPrjACL ( objectIdentity);crud.
            save(record);logger.
            debug("ACL of "+objectIdentity + " created successfully." ) ;}catch
        ( IOException e) {throw new
            InternalErrorException ( e);}return
        (
        MutableAcl )readAclById( objectIdentity);}@
    Override

    publicvoid
    deleteAcl ( ObjectIdentityobjectIdentity, booleandeleteChildren ) throwsChildrenExistException { try (
        AutoLock l= lock . lockForWrite()){List <
            ObjectIdentity>children= findChildren ( objectIdentity);if(
            ! deleteChildren&&children . size()>0 ) {Message msg
                = MsgPicker . getMsg();thrownew
                BadRequestException ( String.
                        format(Locale.ROOT,msg. getIDENTITY_EXIST_CHILDREN(),objectIdentity) );}for
            (
            ObjectIdentity oid: children ) {deleteAcl (
                oid,deleteChildren) ;}crud
            .
            delete(objID(objectIdentity));logger.
            debug("ACL of "+objectIdentity + " deleted successfully." ) ;}catch
        ( IOException e) {throw new
            InternalErrorException ( e);}}
        // Try use the updateAclWithRetry() method family whenever possible
    @

    Override
    publicMutableAcl
    updateAcl ( MutableAclmutableAcl) throwsNotFoundException { try (
        AutoLock l= lock . lockForWrite()){AclRecord record
            = ( ( MutableAclRecord)mutableAcl) .getAclRecord();crud.
            save(record);logger.
            debug("ACL of "+mutableAcl . getObjectIdentity()+" updated successfully." ) ;}catch
        ( IOException e) {throw new
            InternalErrorException ( e);}return
        mutableAcl
        ; }// a NULL permission means to delete the ace
    MutableAclRecord

    upsertAce
    ( MutableAclRecordacl, finalSid sid , finalPermission perm ) {return updateAclWithRetry
        ( acl,newAclRecordUpdater ( ){@ Override
            publicvoid
            update ( AclRecordrecord) {record .
                upsertAce(perm,sid) ;}}
            )
        ;}void
    batchUpsertAce

    ( MutableAclRecordacl, finalMap < Sid,Permission> sidToPerm) {updateAclWithRetry (
        acl,newAclRecordUpdater ( ){@ Override
            publicvoid
            update ( AclRecordrecord) {for (
                Sid sid: sidToPerm . keySet()){record .
                    upsertAce(sidToPerm.get(sid),sid) ;}}
                }
            )
        ;}MutableAclRecord
    inherit

    ( MutableAclRecordacl, finalMutableAclRecord parentAcl ) {return updateAclWithRetry
        ( acl,newAclRecordUpdater ( ){@ Override
            publicvoid
            update ( AclRecordrecord) {record .
                setEntriesInheriting(true);record.
                setParent(parentAcl);}}
            )
        ;}@
    Nullable

    privateAclRecord
    getAclRecordByCache ( Stringid) {try (
        AutoLock l= lock . lockForRead()){if (
            aclMap .size()>0 ) {return aclMap
                . get(id);}}
            try
        (

        AutoLock l= lock . lockForWrite()){crud .
            reloadAll();returnaclMap
            . get(id);}catch
        ( IOException e) {throw new
            RuntimeException ( "Can not get ACL record from cache.",e) ;}}
        private
    AclRecord

    newPrjACL ( ObjectIdentityobjID) {AclRecord acl
        = new AclRecord ( objID,getCurrentSid( ));acl.
        init(null,this. aclPermissionFactory,this. permissionGrantingStrategy);acl.
        updateRandomUuid();returnacl
        ; }private
    Sid

    getCurrentSid ( ){return new
        PrincipalSid ( SecurityContextHolder.getContext().getAuthentication());}public
    interface

    AclRecordUpdater { void update
        ( AclRecordrecord) ;}private
    MutableAclRecord

    updateAclWithRetry ( MutableAclRecordacl, AclRecordUpdaterupdater ) {int retry
        = 7 ; while(
        retry -->0 ) {AclRecord record
            = acl . getAclRecord();updater.

            update(record);try{
            crud .
                save(record);returnacl
                ; // here we are done} catch

            ( WriteConflictException ise) {if (
                retry <=0 ) {logger .
                    error("Retry is out, till got error, abandoning...",ise) ;throwise
                    ; }logger
                .

                warn("Write conflict to update ACL "+resourceKey ( record.getObjectIdentity())+" retry remaining "
                        + retry + ", will retry..." ) ;acl=
                readAcl ( acl.getObjectIdentity());}catch

            ( IOException e) {throw new
                InternalErrorException ( e);}}
            throw
        new
        RuntimeException ( "should not reach here");}private
    static

    String resourceKey ( ObjectIdentitydomainObjId) {return resourceKey
        ( objID(domainObjId));}private
    static

    String objID ( ObjectIdentitydomainObjId) {return String
        . valueOf(domainObjId.getIdentifier());}static
    String

    resourceKey ( StringdomainObjId) {return DIR_PREFIX
        + domainObjId ; }}
    