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
import org.springframework.security.acls.model.
ChildrenExistException ;importorg.springframework.security.acls.model.
MutableAcl ;importorg.springframework.security.acls.model.
MutableAclService ;importorg.springframework.security.acls.model.
NotFoundException ;importorg.springframework.security.acls.model.
ObjectIdentity ;importorg.springframework.security.acls.model.
Permission ;importorg.springframework.security.acls.model.
PermissionGrantingStrategy ;importorg.springframework.security.acls.model.
Sid ;importorg.springframework.security.core.context.
SecurityContextHolder ;importorg.springframework.stereotype.

Component;@Component(
"aclService" ) public class AclServiceimplements MutableAclService ,
    InitializingBean { private static final Logger logger=LoggerFactory.getLogger(AclService.class

    ) ; public static final String DIR_PREFIX=
    "/acl/" ; public staticfinalSerializer< AclRecord > SERIALIZER =newJsonSerializer<>(AclRecord. class,true

    )

    ;// ============================================================================
    @ Autowired protectedPermissionGrantingStrategy

    permissionGrantingStrategy;
    @ Autowired protectedPermissionFactory
    aclPermissionFactory
    ; // cacheprivateCaseInsensitiveStringCache< AclRecord>
    aclMap ;privateCachedCrudAssist< AclRecord>
    crud ; private AutoReadWriteLock lock =newAutoReadWriteLock(

    ) ;publicAclService ( ) throws
        IOException { KylinConfig config=KylinConfig.getInstanceFromEnv(
        ) ; ResourceStore aclStore=ResourceStore.getStore(config
        );this . aclMap =newCaseInsensitiveStringCache<>( config,"acl"
        );this . crud =newCachedCrudAssist<AclRecord>( aclStore, "/acl", "",AclRecord. class, aclMap, true
            ){
            @ Override protectedAclRecordinitEntityAfterReload (AclRecord acl ,String resourceName
                ){acl.init( null, aclPermissionFactory,permissionGrantingStrategy
                ) ;return
            acl
        ;}
        };crud.reloadAll(
    )

    ;}
    @ Override publicvoidafterPropertiesSet ( ) throws
        Exception{Broadcaster.getInstance(KylinConfig.getInstanceFromEnv()).registerStaticListener (newAclRecordSyncListener(
                ),"acl"
    )

    ; } private class AclRecordSyncListenerextendsBroadcaster .

        Listener{
        @ Override publicvoidonEntityChange (Broadcaster broadcaster ,String entity,Broadcaster .Event event ,String
                cacheKey ) throws
            IOException {try ( AutoLock l=lock.lockForWrite( )
                ) {if ( event==Broadcaster.Event.
                    DROP)aclMap.removeLocal(cacheKey
                )
                    ;elsecrud.reloadQuietly(cacheKey
            )
            ;}broadcaster.notifyProjectACLUpdate(cacheKey
        )

        ;}
        @ Override publicvoidonClearAll (Broadcaster broadcaster ) throws
            IOException {try ( AutoLock l=lock.lockForWrite( )
                ){aclMap.clear(
            )
        ;
    }

    }}
    @ OverridepublicList< ObjectIdentity>findChildren (ObjectIdentity parentIdentity
        ){List< ObjectIdentity > oids =newArrayList<>(
        );Collection< AclRecord>
        allAclRecords ;try ( AutoLock l=lock.lockForRead( )
            ) { allAclRecords =newArrayList<>(aclMap.values()
        )
        ; }for ( AclRecord record: allAclRecords
            ) { ObjectIdentityImpl parent=record.getParentDomainObjectInfo(
            ) ;if ( parent != null&&parent.equals(parentIdentity )
                ) { ObjectIdentityImpl child=record.getDomainObjectInfo(
                );oids.add(child
            )
        ;
        } }return
    oids

    ; } publicMutableAclRecordreadAcl (ObjectIdentity oid ) throws
        NotFoundException {return( MutableAclRecord)readAclById(oid
    )

    ;}
    @ Override publicAclreadAclById (ObjectIdentity object ) throws
        NotFoundException{Map< ObjectIdentity, Acl > aclsMap=readAclsById(Arrays.asList(object ),null
        ) ;returnaclsMap.get(object
    )

    ;}
    @ Override publicAclreadAclById (ObjectIdentity object,List< Sid> sids ) throws
        NotFoundException { Message msg=MsgPicker.getMsg(
        );Map< ObjectIdentity, Acl > aclsMap=readAclsById(Arrays.asList(object ),sids
        ) ;if(!aclsMap.containsKey(object )
            ) { thrownewBadRequestException(String.format(Locale. ROOT,msg.getNO_ACL_ENTRY( ),object)
        )
        ; }returnaclsMap.get(object
    )

    ;}
    @ OverridepublicMap< ObjectIdentity, Acl>readAclsById(List< ObjectIdentity> objects ) throws
        NotFoundException {returnreadAclsById( objects,null
    )

    ;}
    @ OverridepublicMap< ObjectIdentity, Acl>readAclsById(List< ObjectIdentity> oids,List< Sid> sids ) throws
        NotFoundException{Map< ObjectIdentity, Acl > aclMaps =newHashMap<>(
        ) ;for ( ObjectIdentity oid: oids
            ) { AclRecord record=getAclRecordByCache(objID(oid)
            ) ;if ( record== null
                ) { Message msg=MsgPicker.getMsg(
                ) ; thrownewNotFoundException(String.format(Locale. ROOT,msg.getACL_INFO_NOT_FOUND( ),oid)
            )

            ; } Acl parentAcl=
            null ;if(record.isEntriesInheriting ( )&&record.getParentDomainObjectInfo ( )!=
                null ) parentAcl=readAclById(record.getParentDomainObjectInfo()

            );record.init( parentAcl, aclPermissionFactory,permissionGrantingStrategy

            );aclMaps.put( oid ,newMutableAclRecord(record)
        )
        ; }return
    aclMaps

    ;}
    @ Override publicMutableAclcreateAcl (ObjectIdentity objectIdentity ) throws
        AlreadyExistsException {try ( AutoLock l=lock.lockForWrite( )
            ) { AclRecord aclRecord=getAclRecordByCache(objID(objectIdentity)
            ) ;if ( aclRecord!= null
                ) { thrownewAlreadyExistsException ( "ACL of " + objectIdentity+" exists!"
            )
            ; } AclRecord record=newPrjACL(objectIdentity
            );crud.save(record
            );logger.debug ( "ACL of " + objectIdentity+" created successfully."
        ) ; }catch (IOException e
            ) { thrownewInternalErrorException(e
        )
        ; }return( MutableAcl)readAclById(objectIdentity
    )

    ;}
    @ Override publicvoiddeleteAcl (ObjectIdentity objectIdentity ,boolean deleteChildren ) throws
        ChildrenExistException {try ( AutoLock l=lock.lockForWrite( )
            ){List< ObjectIdentity > children=findChildren(objectIdentity
            ) ;if( ! deleteChildren&&children.size ( )> 0
                ) { Message msg=MsgPicker.getMsg(
                ) ; thrownew
                        BadRequestException(String.format(Locale. ROOT,msg.getIDENTITY_EXIST_CHILDREN( ),objectIdentity)
            )
            ; }for ( ObjectIdentity oid: children
                ){deleteAcl( oid,deleteChildren
            )
            ;}crud.delete(objID(objectIdentity)
            );logger.debug ( "ACL of " + objectIdentity+" deleted successfully."
        ) ; }catch (IOException e
            ) { thrownewInternalErrorException(e
        )
    ;

    }
    }// Try use the updateAclWithRetry() method family whenever possible
    @ Override publicMutableAclupdateAcl (MutableAcl mutableAcl ) throws
        NotFoundException {try ( AutoLock l=lock.lockForWrite( )
            ) { AclRecord record=(( MutableAclRecord)mutableAcl).getAclRecord(
            );crud.save(record
            );logger.debug ( "ACL of "+mutableAcl.getObjectIdentity ( )+" updated successfully."
        ) ; }catch (IOException e
            ) { thrownewInternalErrorException(e
        )
        ; }return
    mutableAcl

    ;
    } // a NULL permission means to delete the aceMutableAclRecordupsertAce (MutableAclRecord acl , finalSid sid , finalPermission perm
        ) {returnupdateAclWithRetry( acl ,newAclRecordUpdater (
            ){
            @ Override publicvoidupdate (AclRecord record
                ){record.upsertAce( perm,sid
            )
        ;}}
    )

    ; }voidbatchUpsertAce (MutableAclRecord acl ,finalMap< Sid, Permission> sidToPerm
        ){updateAclWithRetry( acl ,newAclRecordUpdater (
            ){
            @ Override publicvoidupdate (AclRecord record
                ) {for ( Sid sid:sidToPerm.keySet( )
                    ){record.upsertAce(sidToPerm.get(sid ),sid
                )
            ;
        }}}
    )

    ; }MutableAclRecordinherit (MutableAclRecord acl , finalMutableAclRecord parentAcl
        ) {returnupdateAclWithRetry( acl ,newAclRecordUpdater (
            ){
            @ Override publicvoidupdate (AclRecord record
                ){record.setEntriesInheriting(true
                );record.setParent(parentAcl
            )
        ;}}
    )

    ;}
    @ Nullable privateAclRecordgetAclRecordByCache (String id
        ) {try ( AutoLock l=lock.lockForRead( )
            ) {if(aclMap.size ( )> 0
                ) {returnaclMap.get(id
            )
        ;

        } }try ( AutoLock l=lock.lockForWrite( )
            ){crud.reloadAll(
            ) ;returnaclMap.get(id
        ) ; }catch (IOException e
            ) { thrownewRuntimeException( "Can not get ACL record from cache.",e
        )
    ;

    } } privateAclRecordnewPrjACL (ObjectIdentity objID
        ) { AclRecord acl =newAclRecord( objID,getCurrentSid()
        );acl.init( null,this. aclPermissionFactory,this.permissionGrantingStrategy
        );acl.updateRandomUuid(
        ) ;return
    acl

    ; } privateSidgetCurrentSid (
        ) { returnnewPrincipalSid(SecurityContextHolder.getContext().getAuthentication()
    )

    ; } public interface
        AclRecordUpdater {voidupdate (AclRecordrecord
    )

    ; } privateMutableAclRecordupdateAclWithRetry (MutableAclRecord acl ,AclRecordUpdater updater
        ) { int retry=
        7 ;while( retry --> 0
            ) { AclRecord record=acl.getAclRecord(

            );updater.update(record
            ) ;
                try{crud.save(record
                ) ;return acl

            ; // here we are done }catch (WriteConflictException ise
                ) {if ( retry<= 0
                    ){logger.error( "Retry is out, till got error, abandoning...",ise
                    ) ;throw
                ise

                ;}logger.warn ( "Write conflict to update ACL "+resourceKey(record.getObjectIdentity(
                        ) ) + " retry remaining " + retry+", will retry..."
                ) ; acl=readAcl(acl.getObjectIdentity()

            ) ; }catch (IOException e
                ) { thrownewInternalErrorException(e
            )
        ;
        } } thrownewRuntimeException("should not reach here"
    )

    ; } private staticStringresourceKey (ObjectIdentity domainObjId
        ) {returnresourceKey(objID(domainObjId)
    )

    ; } private staticStringobjID (ObjectIdentity domainObjId
        ) {returnString.valueOf(domainObjId.getIdentifier()
    )

    ; } staticStringresourceKey (String domainObjId
        ) { return DIR_PREFIX+
    domainObjId
;
