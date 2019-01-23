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
import org.apache.kylin.rest.security.
springacl .AclRecord;importorg.apache.kylin.rest.security.
springacl .MutableAclRecord;importorg.apache.kylin.rest.security.
springacl .ObjectIdentityImpl;importorg.
slf4j .Logger;importorg.
slf4j .LoggerFactory;importorg.springframework.beans.
factory .InitializingBean;importorg.springframework.beans.factory.
annotation .Autowired;importorg.springframework.security.acls.
domain .PermissionFactory;importorg.springframework.security.acls.
domain .PrincipalSid;importorg.springframework.security.acls.
model .Acl;importorg.springframework.security.acls.
model .AlreadyExistsException;importorg.springframework.security.acls.
model .ChildrenExistException;importorg.springframework.security.acls.
model .MutableAcl;importorg.springframework.security.acls.
model .MutableAclService;importorg.springframework.security.acls.
model .NotFoundException;importorg.springframework.security.acls.
model .ObjectIdentity;importorg.springframework.security.acls.
model .Permission;importorg.springframework.security.acls.
model .PermissionGrantingStrategy;importorg.springframework.security.acls.
model .Sid;importorg.springframework.security.core.
context .SecurityContextHolder;importorg.springframework.

stereotype.Component;@
Component ( "aclService" ) publicclass AclService implements
    MutableAclService , InitializingBean { private static finalLoggerlogger=LoggerFactory.getLogger(AclService

    . class ) ; public static finalString
    DIR_PREFIX = "/acl/" ;publicstaticfinal Serializer < AclRecord >SERIALIZER=newJsonSerializer<>( AclRecord.class

    ,

    true)
    ; // ============================================================================ @Autowired

    protectedPermissionGrantingStrategy
    permissionGrantingStrategy ; @Autowired
    protected
    PermissionFactory aclPermissionFactory;// cacheprivate CaseInsensitiveStringCache<
    AclRecord >aclMap;private CachedCrudAssist<
    AclRecord > crud ; private AutoReadWriteLocklock=new

    AutoReadWriteLock (); public AclService (
        ) throws IOException {KylinConfigconfig=KylinConfig.
        getInstanceFromEnv ( ) ;ResourceStoreaclStore=ResourceStore.getStore
        (config) ; this .aclMap=newCaseInsensitiveStringCache< >(config
        ,"acl") ; this .crud=newCachedCrudAssist<AclRecord >( aclStore, "/acl","", AclRecord. class, aclMap
            ,true
            ) { @Overrideprotected AclRecordinitEntityAfterReload ( AclRecordacl ,
                StringresourceName){acl. init( null,aclPermissionFactory
                , permissionGrantingStrategy)
            ;
        returnacl
        ;}};crud.
    reloadAll

    ()
    ; } @Overridepublic void afterPropertiesSet (
        )throwsException{Broadcaster.getInstance(KylinConfig.getInstanceFromEnv()) .registerStaticListener(new
                AclRecordSyncListener()
    ,

    "acl" ) ; } privateclassAclRecordSyncListener extends

        Broadcaster.
        Listener { @Overridepublic voidonEntityChange ( Broadcasterbroadcaster ,Stringentity ,Broadcaster . Eventevent
                , String cacheKey
            ) throwsIOException { try (AutoLockl=lock. lockForWrite
                ( )) { if(event==Broadcaster.
                    Event.DROP)aclMap.removeLocal
                (
                    cacheKey);elsecrud.reloadQuietly
            (
            cacheKey);}broadcaster.notifyProjectACLUpdate
        (

        cacheKey)
        ; } @Overridepublic voidonClearAll ( Broadcaster broadcaster
            ) throwsIOException { try (AutoLockl=lock. lockForWrite
                ()){aclMap.
            clear
        (
    )

    ;}
    } }@Overridepublic List<ObjectIdentity >findChildren (
        ObjectIdentityparentIdentity){ List < ObjectIdentity >oids=newArrayList<
        >(); Collection<
        AclRecord >allAclRecords ; try (AutoLockl=lock. lockForRead
            ( ) ) {allAclRecords=newArrayList<>(aclMap.values
        (
        ) ); } for (AclRecord record
            : allAclRecords ) {ObjectIdentityImplparent=record.
            getParentDomainObjectInfo () ; if ( parent!=null&&parent.equals (
                parentIdentity ) ) {ObjectIdentityImplchild=record.
                getDomainObjectInfo();oids.add
            (
        child
        ) ;}
    }

    return oids ;}public MutableAclRecordreadAcl ( ObjectIdentity oid
        ) throwsNotFoundException{ return(MutableAclRecord)readAclById
    (

    oid)
    ; } @Overridepublic AclreadAclById ( ObjectIdentity object
        )throwsNotFoundException{ Map< ObjectIdentity , Acl>aclsMap=readAclsById(Arrays.asList (object)
        , null);returnaclsMap.get
    (

    object)
    ; } @Overridepublic AclreadAclById (ObjectIdentityobject, List< Sid > sids
        ) throws NotFoundException {Messagemsg=MsgPicker.
        getMsg(); Map< ObjectIdentity , Acl>aclsMap=readAclsById(Arrays.asList (object)
        , sids);if(!aclsMap.containsKey (
            object ) ){thrownewBadRequestException(String.format( Locale.ROOT,msg. getNO_ACL_ENTRY(),
        object
        ) );}returnaclsMap.get
    (

    object)
    ; }@Overridepublic Map< ObjectIdentity,Acl>readAclsById( List< ObjectIdentity > objects
        ) throwsNotFoundException{return readAclsById(objects
    ,

    null)
    ; }@Overridepublic Map< ObjectIdentity,Acl>readAclsById( List< ObjectIdentity>oids, List< Sid > sids
        )throwsNotFoundException{ Map< ObjectIdentity , Acl >aclMaps=newHashMap<
        > () ; for (ObjectIdentity oid
            : oids ) {AclRecordrecord=getAclRecordByCache(objID(
            oid )) ; if( record
                == null ) {Messagemsg=MsgPicker.
                getMsg ( );thrownewNotFoundException(String.format( Locale.ROOT,msg. getACL_INFO_NOT_FOUND(),
            oid

            ) ) ; }Acl
            parentAcl =null;if(record . isEntriesInheriting()&&record . getParentDomainObjectInfo(
                ) != null)parentAcl=readAclById(record.getParentDomainObjectInfo

            ());record. init( parentAcl,aclPermissionFactory

            ,permissionGrantingStrategy);aclMaps. put (oid,newMutableAclRecord(
        record
        ) );
    }

    returnaclMaps
    ; } @Overridepublic MutableAclcreateAcl ( ObjectIdentity objectIdentity
        ) throwsAlreadyExistsException { try (AutoLockl=lock. lockForWrite
            ( ) ) {AclRecordaclRecord=getAclRecordByCache(objID(
            objectIdentity )) ; if( aclRecord
                != null ){throw new AlreadyExistsException ( "ACL of "+objectIdentity
            +
            " exists!" ) ; }AclRecordrecord=newPrjACL
            (objectIdentity);crud.save
            (record);logger . debug ( "ACL of "+objectIdentity
        + " created successfully." ); }catch (
            IOException e ){thrownewInternalErrorException
        (
        e );} return(MutableAcl)readAclById
    (

    objectIdentity)
    ; } @Overridepublic voiddeleteAcl ( ObjectIdentityobjectIdentity , boolean deleteChildren
        ) throwsChildrenExistException { try (AutoLockl=lock. lockForWrite
            ()){ List < ObjectIdentity>children=findChildren
            ( objectIdentity); if (!deleteChildren&&children . size( )
                > 0 ) {Messagemsg=MsgPicker.
                getMsg ( );
                        thrownewBadRequestException(String.format( Locale.ROOT,msg. getIDENTITY_EXIST_CHILDREN(),
            objectIdentity
            ) ); } for (ObjectIdentity oid
                :children){ deleteAcl(oid
            ,
            deleteChildren);}crud.delete(objID(
            objectIdentity));logger . debug ( "ACL of "+objectIdentity
        + " deleted successfully." ); }catch (
            IOException e ){thrownewInternalErrorException
        (
    e

    )
    ;}
    } // Try use the updateAclWithRetry() method family whenever possible @Overridepublic MutableAclupdateAcl ( MutableAcl mutableAcl
        ) throwsNotFoundException { try (AutoLockl=lock. lockForWrite
            ( ) ) {AclRecordrecord= ((MutableAclRecord)mutableAcl).
            getAclRecord();crud.save
            (record);logger . debug("ACL of "+mutableAcl . getObjectIdentity()
        + " updated successfully." ); }catch (
            IOException e ){thrownewInternalErrorException
        (
        e );
    }

    return
    mutableAcl ;}// a NULL permission means to delete the ace MutableAclRecordupsertAce ( MutableAclRecord acl, final Sid sid, final
        Permission perm){return updateAclWithRetry (acl, new
            AclRecordUpdater(
            ) { @Overridepublic voidupdate (
                AclRecordrecord){record. upsertAce(perm
            ,
        sid);
    }

    } );} voidbatchUpsertAce ( MutableAclRecordacl,final Map< Sid, Permission
        >sidToPerm){ updateAclWithRetry (acl, new
            AclRecordUpdater(
            ) { @Overridepublic voidupdate (
                AclRecord record) { for (Sidsid:sidToPerm. keySet
                    ()){record.upsertAce(sidToPerm.get (sid)
                ,
            sid
        );}
    }

    } );} MutableAclRecordinherit ( MutableAclRecord acl, final
        MutableAclRecord parentAcl){return updateAclWithRetry (acl, new
            AclRecordUpdater(
            ) { @Overridepublic voidupdate (
                AclRecordrecord){record.setEntriesInheriting
                (true);record.setParent
            (
        parentAcl);
    }

    })
    ; } @Nullableprivate AclRecordgetAclRecordByCache (
        String id) { try (AutoLockl=lock. lockForRead
            ( )){if(aclMap . size( )
                > 0){returnaclMap.get
            (
        id

        ) ;} } try (AutoLockl=lock. lockForWrite
            ()){crud.
            reloadAll ();returnaclMap.get
        ( id ); }catch (
            IOException e ){thrownew RuntimeException("Can not get ACL record from cache."
        ,
    e

    ) ; }}private AclRecordnewPrjACL (
        ObjectIdentity objID ) { AclRecordacl=new AclRecord(objID,getCurrentSid
        ());acl. init(null, this.aclPermissionFactory,this
        .permissionGrantingStrategy);acl.
        updateRandomUuid ()
    ;

    return acl ;}private Sid
        getCurrentSid ( ){returnnewPrincipalSid(SecurityContextHolder.getContext().getAuthentication
    (

    ) ) ; }
        public interfaceAclRecordUpdater{ voidupdate(
    AclRecord

    record ) ;}private MutableAclRecordupdateAclWithRetry ( MutableAclRecordacl ,
        AclRecordUpdater updater ) {int
        retry =7; while (retry --
            > 0 ) {AclRecordrecord=acl.

            getAclRecord();updater.update
            ( record
                );try{crud.save
                ( record) ;

            return acl ;// here we are done }catch (
                WriteConflictException ise) { if( retry
                    <=0){logger. error("Retry is out, till got error, abandoning..."
                    , ise)
                ;

                throwise;}logger . warn("Write conflict to update ACL "+resourceKey(record.
                        getObjectIdentity ( ) ) + " retry remaining "+retry
                + ", will retry..." );acl=readAcl(acl.getObjectIdentity

            ( ) ); }catch (
                IOException e ){thrownewInternalErrorException
            (
        e
        ) ; }}thrownewRuntimeException
    (

    "should not reach here" ) ; }privatestatic StringresourceKey (
        ObjectIdentity domainObjId){returnresourceKey(objID(
    domainObjId

    ) ) ; }privatestatic StringobjID (
        ObjectIdentity domainObjId){returnString.valueOf(domainObjId.getIdentifier
    (

    ) ) ;}static StringresourceKey (
        String domainObjId ) {return
    DIR_PREFIX
+
