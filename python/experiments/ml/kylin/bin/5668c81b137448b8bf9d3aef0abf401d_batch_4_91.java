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
springacl.MutableAclRecord;importorg.
apache .kylin.rest.security.springacl.ObjectIdentityImpl;importorg.
slf4j .Logger;importorg.
slf4j .LoggerFactory;importorg.
springframework .beans.factory.InitializingBean;importorg.
springframework .beans.factory.annotation.Autowired;importorg.
springframework .security.acls.domain.PermissionFactory;importorg.
springframework .security.acls.domain.PrincipalSid;importorg.
springframework .security.acls.model.Acl;importorg.
springframework .security.acls.model.AlreadyExistsException;importorg.
springframework .security.acls.model.ChildrenExistException;importorg.
springframework .security.acls.model.MutableAcl;importorg.
springframework .security.acls.model.MutableAclService;importorg.
springframework .security.acls.model.NotFoundException;importorg.
springframework .security.acls.model.ObjectIdentity;importorg.
springframework .security.acls.model.Permission;importorg.
springframework .security.acls.model.PermissionGrantingStrategy;importorg.
springframework .security.acls.model.Sid;importorg.
springframework .security.core.context.SecurityContextHolder;importorg.
springframework .stereotype.Component;@Component(

"aclService")publicclassAclService
implements MutableAclService , InitializingBean {private static final
    Logger logger = LoggerFactory . getLogger (AclService.class);publicstaticfinal

    String DIR_PREFIX = "/acl/" ; public staticfinal
    Serializer < AclRecord >SERIALIZER=new JsonSerializer < > (AclRecord.class,true); // ============================================================================@Autowired

    protected

    PermissionGrantingStrategypermissionGrantingStrategy
    ; @ Autowiredprotected

    PermissionFactoryaclPermissionFactory
    ; // cache privateCaseInsensitiveStringCache
    <
    AclRecord >aclMap;private CachedCrudAssist<
    AclRecord >crud;private AutoReadWriteLocklock
    = new AutoReadWriteLock ( ) ;publicAclService(

    ) throwsIOException{ KylinConfig config =
        KylinConfig . getInstanceFromEnv ();ResourceStoreaclStore=
        ResourceStore . getStore (config);this.aclMap
        =newCaseInsensitiveStringCache < > (config,"acl"); this.crud
        =newCachedCrudAssist < AclRecord >(aclStore,"/acl","" ,AclRecord .class ,aclMap,true ){ @Override protected
            AclRecordinitEntityAfterReload
            ( AclRecord acl,String resourceName) { acl. init
                (null,aclPermissionFactory,permissionGrantingStrategy ); returnacl;
                } };
            crud
        .reloadAll
        ();}@Override
    public

    voidafterPropertiesSet
    ( ) throwsException{ Broadcaster . getInstance
        (KylinConfig.getInstanceFromEnv()).registerStaticListener(newAclRecordSyncListener() ,"acl");
                }privateclass
    AclRecordSyncListener

    extends Broadcaster . Listener {@Override public

        voidonEntityChange
        ( Broadcaster broadcaster,String entity, Broadcaster .Event event,String cacheKey) throws IOException{
                try ( AutoLock
            l =lock . lockForWrite ()){if( event
                == Broadcaster. Event .DROP)aclMap.removeLocal
                    (cacheKey);elsecrud.
                reloadQuietly
                    (cacheKey);}broadcaster.
            notifyProjectACLUpdate
            (cacheKey);}@Override
        public

        voidonClearAll
        ( Broadcaster broadcaster)throws IOException{ try ( AutoLock
            l =lock . lockForWrite ()){aclMap. clear
                ();}}}
            @
        Override
    public

    List<
    ObjectIdentity >findChildren(ObjectIdentity parentIdentity){ List< ObjectIdentity
        >oids=new ArrayList < > ();Collection<AclRecord
        >allAclRecords;try (AutoLock
        l =lock . lockForRead ()){allAclRecords= new
            ArrayList < > (aclMap.values());}for(
        AclRecord
        record :allAclRecords ) { ObjectIdentityImplparent =
            record . getParentDomainObjectInfo ();if(parent
            != null&& parent . equals (parentIdentity)){ObjectIdentityImplchild =
                record . getDomainObjectInfo ();oids.add
                (child);}}return
            oids
        ;
        } publicMutableAclRecord
    readAcl

    ( ObjectIdentity oid)throws NotFoundException{ return ( MutableAclRecord
        ) readAclById(oid );}@Override
    public

    AclreadAclById
    ( ObjectIdentity object)throws NotFoundException{ Map < ObjectIdentity
        ,Acl>aclsMap =readAclsById ( Arrays .asList(object),null); returnaclsMap.
        get (object);}@Override
    public

    AclreadAclById
    ( ObjectIdentity object,List <Sid >sids)throws NotFoundException{ Message msg =
        MsgPicker . getMsg ();Map<ObjectIdentity
        ,Acl>aclsMap =readAclsById ( Arrays .asList(object),sids); if(!
        aclsMap .containsKey(object)){thrownew BadRequestException
            ( String .format(Locale.ROOT,msg.getNO_ACL_ENTRY (),object)) ;}returnaclsMap
        .
        get (object);}@Override
    public

    Map<
    ObjectIdentity ,Acl>readAclsById (List <ObjectIdentity>objects)throws NotFoundException{ return readAclsById (
        objects ,null); }@Override
    public

    Map<
    ObjectIdentity ,Acl>readAclsById (List <ObjectIdentity>oids,List <Sid >sids)throws NotFoundException{ Map < ObjectIdentity
        ,Acl>aclMaps =new HashMap < > ();for(ObjectIdentity
        oid :oids ) { AclRecordrecord =
            getAclRecordByCache ( objID (oid));if(record
            == null) { Messagemsg =
                MsgPicker . getMsg ();thrownewNotFoundException
                ( String .format(Locale.ROOT,msg.getACL_INFO_NOT_FOUND (),oid)) ;}AclparentAcl
            =

            null ; if (record
            . isEntriesInheriting()&&record. getParentDomainObjectInfo ()!=null) parentAcl =readAclById
                ( record .getParentDomainObjectInfo());record.init

            (parentAcl,aclPermissionFactory,permissionGrantingStrategy ); aclMaps.put

            (oid,newMutableAclRecord( record ));}returnaclMaps
        ;
        } @Override
    public

    MutableAclcreateAcl
    ( ObjectIdentity objectIdentity)throws AlreadyExistsException{ try ( AutoLock
        l =lock . lockForWrite ()){AclRecordaclRecord =
            getAclRecordByCache ( objID (objectIdentity));if(aclRecord
            != null) { thrownew AlreadyExistsException
                ( "ACL of " +objectIdentity+ " exists!" ) ; }AclRecordrecord
            =
            newPrjACL ( objectIdentity );crud.save
            (record);logger.debug
            ("ACL of "+objectIdentity+ " created successfully." ) ; }catch(
        IOException e ){ thrownew InternalErrorException
            ( e );}return(
        MutableAcl
        ) readAclById(objectIdentity );}@Override
    public

    voiddeleteAcl
    ( ObjectIdentity objectIdentity,boolean deleteChildren) throws ChildrenExistException{ try ( AutoLock
        l =lock . lockForWrite ()){List< ObjectIdentity
            >children=findChildren ( objectIdentity );if(!
            deleteChildren &&children. size ()>0) { Messagemsg =
                MsgPicker . getMsg ();thrownewBadRequestException
                ( String .format
                        (Locale.ROOT,msg.getIDENTITY_EXIST_CHILDREN (),objectIdentity)) ;}for(
            ObjectIdentity
            oid :children ) { deleteAcl( oid
                ,deleteChildren); }crud.
            delete
            (objID(objectIdentity));logger.debug
            ("ACL of "+objectIdentity+ " deleted successfully." ) ; }catch(
        IOException e ){ thrownew InternalErrorException
            ( e );}}// Try use the updateAclWithRetry() method family whenever possible
        @
    Override

    public
    MutableAclupdateAcl
    ( MutableAcl mutableAcl)throws NotFoundException{ try ( AutoLock
        l =lock . lockForWrite ()){AclRecordrecord =
            ( ( MutableAclRecord )mutableAcl). getAclRecord();crud.save
            (record);logger.debug
            ("ACL of "+mutableAcl. getObjectIdentity ()+" updated successfully.") ; }catch(
        IOException e ){ thrownew InternalErrorException
            ( e );}returnmutableAcl
        ;
        } // a NULL permission means to delete the aceMutableAclRecord
    upsertAce

    (
    MutableAclRecord acl,final Sidsid , final Permissionperm ) { returnupdateAclWithRetry (
        acl ,newAclRecordUpdater( ) {@Override public
            voidupdate
            ( AclRecord record){ record. upsertAce
                (perm,sid); }})
            ;
        }voidbatchUpsertAce
    (

    MutableAclRecord acl,final Map< Sid ,Permission>sidToPerm ){ updateAclWithRetry( acl
        ,newAclRecordUpdater( ) {@Override public
            voidupdate
            ( AclRecord record){ for( Sid
                sid :sidToPerm . keySet ()){record. upsertAce
                    (sidToPerm.get(sid),sid); }}}
                )
            ;
        }MutableAclRecordinherit
    (

    MutableAclRecord acl,final MutableAclRecordparentAcl ) { returnupdateAclWithRetry (
        acl ,newAclRecordUpdater( ) {@Override public
            voidupdate
            ( AclRecord record){ record. setEntriesInheriting
                (true);record.setParent
                (parentAcl);}})
            ;
        }@Nullable
    private

    AclRecordgetAclRecordByCache
    ( String id){ try( AutoLock
        l =lock . lockForRead ()){if( aclMap
            . size()>0) { returnaclMap .
                get (id);}}try
            (
        AutoLock

        l =lock . lockForWrite ()){crud. reloadAll
            ();returnaclMap.
            get (id);}catch(
        IOException e ){ thrownew RuntimeException
            ( "Can not get ACL record from cache." ,e); }}private
        AclRecord
    newPrjACL

    ( ObjectIdentity objID){ AclRecordacl =
        new AclRecord ( objID ,getCurrentSid() );acl.init
        (null,this.aclPermissionFactory ,this.permissionGrantingStrategy );acl.updateRandomUuid
        ();returnacl;
        } privateSid
    getCurrentSid

    ( ) {returnnew PrincipalSid
        ( SecurityContextHolder .getContext().getAuthentication());}publicinterface
    AclRecordUpdater

    { void update (
        AclRecord record); }privateMutableAclRecord
    updateAclWithRetry

    ( MutableAclRecord acl,AclRecordUpdater updater) { intretry =
        7 ; while (retry
        -- >0) { AclRecordrecord =
            acl . getAclRecord ();updater.update

            (record);try{crud
            . save
                (record);returnacl;
                // here we are done }catch (

            WriteConflictException ise ){ if( retry
                <= 0) { logger. error
                    ("Retry is out, till got error, abandoning...",ise); throwise;
                    } logger.
                warn

                ("Write conflict to update ACL "+resourceKey( record .getObjectIdentity())+" retry remaining "+
                        retry + ", will retry..." ) ; acl=readAcl
                ( acl .getObjectIdentity());}catch(

            IOException e ){ thrownew InternalErrorException
                ( e );}}throw
            new
        RuntimeException
        ( "should not reach here" );}privatestatic
    String

    resourceKey ( ObjectIdentity domainObjId){ returnresourceKey (
        objID (domainObjId));}privatestatic
    String

    objID ( ObjectIdentity domainObjId){ returnString .
        valueOf (domainObjId.getIdentifier());}staticString
    resourceKey

    ( String domainObjId){ returnDIR_PREFIX +
        domainObjId ; } }