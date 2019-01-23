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
import org.springframework.security.acls.model.ChildrenExistException;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("aclService")
public class AclService implements MutableAclService, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(AclService.

    class ) ; public static final StringDIR_PREFIX
    = "/acl/" ; publicstaticfinalSerializer < AclRecord > SERIALIZER=newJsonSerializer<>(AclRecord .class,

    true

    );
    // ============================================================================ @ Autowiredprotected

    PermissionGrantingStrategypermissionGrantingStrategy
    ; @ Autowiredprotected
    PermissionFactory
    aclPermissionFactory ;// cacheprivateCaseInsensitiveStringCache <AclRecord
    > aclMap;privateCachedCrudAssist <AclRecord
    > crud ; private AutoReadWriteLock lock=newAutoReadWriteLock

    ( );public AclService ( )
        throws IOException { KylinConfigconfig=KylinConfig.getInstanceFromEnv
        ( ) ; ResourceStoreaclStore=ResourceStore.getStore(
        config); this . aclMap=newCaseInsensitiveStringCache<> (config,
        "acl"); this . crud=newCachedCrudAssist<AclRecord> (aclStore ,"/acl" ,"",AclRecord .class ,aclMap ,
            true)
            { @ OverrideprotectedAclRecord initEntityAfterReload( AclRecord acl, String
                resourceName){acl.init (null ,aclPermissionFactory,
                permissionGrantingStrategy );
            return
        acl;
        }};crud.reloadAll
    (

    );
    } @ Overridepublicvoid afterPropertiesSet ( )
        throwsException{Broadcaster.getInstance(KylinConfig.getInstanceFromEnv()). registerStaticListener(newAclRecordSyncListener
                (),
    "acl"

    ) ; } private classAclRecordSyncListenerextends Broadcaster

        .Listener
        { @ Overridepublicvoid onEntityChange( Broadcaster broadcaster, Stringentity, Broadcaster. Event event,
                String cacheKey )
            throws IOException{ try ( AutoLockl=lock.lockForWrite (
                ) ){ if (event==Broadcaster.Event
                    .DROP)aclMap.removeLocal(
                cacheKey
                    );elsecrud.reloadQuietly(
            cacheKey
            );}broadcaster.notifyProjectACLUpdate(
        cacheKey

        );
        } @ Overridepublicvoid onClearAll( Broadcaster broadcaster )
            throws IOException{ try ( AutoLockl=lock.lockForWrite (
                )){aclMap.clear
            (
        )
    ;

    }}
    } @OverridepublicList <ObjectIdentity> findChildren( ObjectIdentity
        parentIdentity){List < ObjectIdentity > oids=newArrayList<>
        ();Collection <AclRecord
        > allAclRecords; try ( AutoLockl=lock.lockForRead (
            ) ) { allAclRecords=newArrayList<>(aclMap.values(
        )
        ) ;} for ( AclRecordrecord :
            allAclRecords ) { ObjectIdentityImplparent=record.getParentDomainObjectInfo
            ( ); if ( parent !=null&&parent.equals( parentIdentity
                ) ) { ObjectIdentityImplchild=record.getDomainObjectInfo
                ();oids.add(
            child
        )
        ; }}
    return

    oids ; }publicMutableAclRecord readAcl( ObjectIdentity oid )
        throws NotFoundException{return (MutableAclRecord)readAclById(
    oid

    );
    } @ OverridepublicAcl readAclById( ObjectIdentity object )
        throwsNotFoundException{Map <ObjectIdentity , Acl >aclsMap=readAclsById(Arrays.asList( object),
        null );returnaclsMap.get(
    object

    );
    } @ OverridepublicAcl readAclById( ObjectIdentityobject,List <Sid > sids )
        throws NotFoundException { Messagemsg=MsgPicker.getMsg
        ();Map <ObjectIdentity , Acl >aclsMap=readAclsById(Arrays.asList( object),
        sids );if(!aclsMap.containsKey( object
            ) ) {thrownewBadRequestException(String.format(Locale .ROOT,msg.getNO_ACL_ENTRY (),object
        )
        ) ;}returnaclsMap.get(
    object

    );
    } @OverridepublicMap <ObjectIdentity ,Acl>readAclsById(List <ObjectIdentity > objects )
        throws NotFoundException{returnreadAclsById (objects,
    null

    );
    } @OverridepublicMap <ObjectIdentity ,Acl>readAclsById(List <ObjectIdentity >oids,List <Sid > sids )
        throwsNotFoundException{Map <ObjectIdentity , Acl > aclMaps=newHashMap<>
        ( ); for ( ObjectIdentityoid :
            oids ) { AclRecordrecord=getAclRecordByCache(objID(oid
            ) ); if (record ==
                null ) { Messagemsg=MsgPicker.getMsg
                ( ) ;thrownewNotFoundException(String.format(Locale .ROOT,msg.getACL_INFO_NOT_FOUND (),oid
            )

            ) ; } AclparentAcl
            = null;if(record. isEntriesInheriting ()&&record. getParentDomainObjectInfo ()
                != null )parentAcl=readAclById(record.getParentDomainObjectInfo(

            ));record.init (parentAcl ,aclPermissionFactory,

            permissionGrantingStrategy);aclMaps.put ( oid,newMutableAclRecord(record
        )
        ) ;}
    return

    aclMaps;
    } @ OverridepublicMutableAcl createAcl( ObjectIdentity objectIdentity )
        throws AlreadyExistsException{ try ( AutoLockl=lock.lockForWrite (
            ) ) { AclRecordaclRecord=getAclRecordByCache(objID(objectIdentity
            ) ); if (aclRecord !=
                null ) {thrownew AlreadyExistsException ( "ACL of " +objectIdentity+
            " exists!"
            ) ; } AclRecordrecord=newPrjACL(
            objectIdentity);crud.save(
            record);logger. debug ( "ACL of " +objectIdentity+
        " created successfully." ) ;} catch( IOException
            e ) {thrownewInternalErrorException(
        e
        ) ;}return (MutableAcl)readAclById(
    objectIdentity

    );
    } @ Overridepublicvoid deleteAcl( ObjectIdentity objectIdentity, boolean deleteChildren )
        throws ChildrenExistException{ try ( AutoLockl=lock.lockForWrite (
            )){List < ObjectIdentity >children=findChildren(
            objectIdentity );if ( !deleteChildren&&children. size () >
                0 ) { Messagemsg=MsgPicker.getMsg
                ( ) ;throw
                        newBadRequestException(String.format(Locale .ROOT,msg.getIDENTITY_EXIST_CHILDREN (),objectIdentity
            )
            ) ;} for ( ObjectIdentityoid :
                children){deleteAcl (oid,
            deleteChildren
            );}crud.delete(objID(objectIdentity
            ));logger. debug ( "ACL of " +objectIdentity+
        " deleted successfully." ) ;} catch( IOException
            e ) {thrownewInternalErrorException(
        e
    )

    ;
    }}
    // Try use the updateAclWithRetry() method family whenever possible @ OverridepublicMutableAcl updateAcl( MutableAcl mutableAcl )
        throws NotFoundException{ try ( AutoLockl=lock.lockForWrite (
            ) ) { AclRecordrecord=( (MutableAclRecord)mutableAcl).getAclRecord
            ();crud.save(
            record);logger. debug ("ACL of "+mutableAcl. getObjectIdentity ()+
        " updated successfully." ) ;} catch( IOException
            e ) {thrownewInternalErrorException(
        e
        ) ;}
    return

    mutableAcl
    ; }// a NULL permission means to delete the aceMutableAclRecord upsertAce( MutableAclRecord acl ,final Sid sid ,final Permission
        perm ){returnupdateAclWithRetry ( acl,new AclRecordUpdater
            ()
            { @ Overridepublicvoid update( AclRecord
                record){record.upsertAce (perm,
            sid
        );}
    }

    ) ;}void batchUpsertAce( MutableAclRecord acl,finalMap <Sid ,Permission >
        sidToPerm){updateAclWithRetry ( acl,new AclRecordUpdater
            ()
            { @ Overridepublicvoid update( AclRecord
                record ){ for ( Sidsid:sidToPerm.keySet (
                    )){record.upsertAce(sidToPerm.get( sid),
                sid
            )
        ;}}
    }

    ) ;}MutableAclRecord inherit( MutableAclRecord acl ,final MutableAclRecord
        parentAcl ){returnupdateAclWithRetry ( acl,new AclRecordUpdater
            ()
            { @ Overridepublicvoid update( AclRecord
                record){record.setEntriesInheriting(
                true);record.setParent(
            parentAcl
        );}
    }

    );
    } @ NullableprivateAclRecord getAclRecordByCache( String
        id ){ try ( AutoLockl=lock.lockForRead (
            ) ){if(aclMap. size () >
                0 ){returnaclMap.get(
            id
        )

        ; }} try ( AutoLockl=lock.lockForWrite (
            )){crud.reloadAll
            ( );returnaclMap.get(
        id ) ;} catch( IOException
            e ) {thrownewRuntimeException ("Can not get ACL record from cache.",
        e
    )

    ; } }privateAclRecord newPrjACL( ObjectIdentity
        objID ) { AclRecord acl=newAclRecord (objID,getCurrentSid(
        ));acl.init (null,this .aclPermissionFactory,this.
        permissionGrantingStrategy);acl.updateRandomUuid
        ( );
    return

    acl ; }privateSid getCurrentSid
        ( ) {returnnewPrincipalSid(SecurityContextHolder.getContext().getAuthentication(
    )

    ) ; } public
        interface AclRecordUpdater{void update(AclRecord
    record

    ) ; }privateMutableAclRecord updateAclWithRetry( MutableAclRecord acl, AclRecordUpdater
        updater ) { intretry
        = 7;while ( retry-- >
            0 ) { AclRecordrecord=acl.getAclRecord

            ();updater.update(
            record )
                ;try{crud.save(
                record ); return

            acl ; // here we are done} catch( WriteConflictException
                ise ){ if (retry <=
                    0){logger.error ("Retry is out, till got error, abandoning...",
                    ise );
                throw

                ise;}logger. warn ("Write conflict to update ACL "+resourceKey(record.getObjectIdentity
                        ( ) ) + " retry remaining " +retry+
                ", will retry..." ) ;acl=readAcl(acl.getObjectIdentity(

            ) ) ;} catch( IOException
                e ) {thrownewInternalErrorException(
            e
        )
        ; } }thrownewRuntimeException(
    "should not reach here"

    ) ; } privatestaticString resourceKey( ObjectIdentity
        domainObjId ){returnresourceKey(objID(domainObjId
    )

    ) ; } privatestaticString objID( ObjectIdentity
        domainObjId ){returnString.valueOf(domainObjId.getIdentifier(
    )

    ) ; }staticString resourceKey( String
        domainObjId ) { returnDIR_PREFIX
    +
domainObjId
