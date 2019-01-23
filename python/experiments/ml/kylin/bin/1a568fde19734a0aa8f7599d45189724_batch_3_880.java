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
    private static final Logger logger = LoggerFactory.getLogger(AclService.class);

    public static final String DIR_PREFIX = "/acl/";
    public static final Serializer<AclRecord> SERIALIZER = new JsonSerializer<>(AclRecord.class, true);

    // ============================================================================

    @Autowired
    protected PermissionGrantingStrategy permissionGrantingStrategy;

    @Autowired
    protected PermissionFactory aclPermissionFactory;
    // cache
    private CaseInsensitiveStringCache<AclRecord> aclMap;
    private CachedCrudAssist<AclRecord> crud;
    private AutoReadWriteLock lock = new AutoReadWriteLock();

    public AclService() throws IOException {
        KylinConfig config = KylinConfig.getInstanceFromEnv();
        ResourceStore aclStore = ResourceStore.getStore(config);
        this.aclMap = new CaseInsensitiveStringCache<>(config, "acl");
        this.crud = new CachedCrudAssist<AclRecord>(aclStore, "/acl", "", AclRecord.class, aclMap, true) {
            @Override
            protected AclRecord initEntityAfterReload(AclRecord acl, String resourceName) {
                acl.init(null, aclPermissionFactory, permissionGrantingStrategy);
                return acl;
            }
        };
        crud.reloadAll();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Broadcaster.getInstance(KylinConfig.getInstanceFromEnv()).registerStaticListener(new AclRecordSyncListener(),
                "acl");
    }

    private class AclRecordSyncListener extends Broadcaster.Listener {

        @Override
        public void onEntityChange(Broadcaster broadcaster, String entity, Broadcaster.Event event, String cacheKey)
                throws IOException {
            try (AutoLock l = lock.lockForWrite()) {
                if (event == Broadcaster.Event.DROP)
                    aclMap.removeLocal(cacheKey);
                else
                    crud.reloadQuietly(cacheKey);
            }
            broadcaster.notifyProjectACLUpdate(cacheKey);
        }

        @Override
        public void onClearAll(Broadcaster broadcaster) throws IOException {
            try (AutoLock l = lock.lockForWrite()) {
                aclMap.clear();
            }
        }
    }

    @Override
    public List<ObjectIdentity> findChildren(ObjectIdentity parentIdentity) {
        List<ObjectIdentity> oids = new ArrayList<>();
        Collection<AclRecord> allAclRecords;
        try (AutoLock l = lock.lockForRead()) {
            allAclRecords = new ArrayList<>(aclMap.values());
        }
        for (AclRecord record : allAclRecords) {
            ObjectIdentityImpl parent = record.getParentDomainObjectInfo();
            if (parent != null && parent.equals(parentIdentity)) {
                ObjectIdentityImpl child = record.getDomainObjectInfo();
                oids.add(child);
            }
        }
        return oids;
    }

    public MutableAclRecord readAcl(ObjectIdentity oid) throws NotFoundException {
        return (MutableAclRecord) readAclById(oid);
    }

    @Override
    public Acl readAclById(ObjectIdentity object) throws NotFoundException {
        Map<ObjectIdentity, Acl> aclsMap = readAclsById(Arrays.asList(object), null);
        return aclsMap.get(object);
    }

    @Override
    public Acl readAclById(ObjectIdentity object, List<Sid> sids) throws NotFoundException {
        Message msg = MsgPicker.getMsg();
        Map<ObjectIdentity, Acl> aclsMap = readAclsById(Arrays.asList(object), sids);
        if (!aclsMap.containsKey(object)) {
            throw new BadRequestException(String.format(Locale.ROOT, msg.getNO_ACL_ENTRY(), object));
        }
        return aclsMap.get(object);
    }

    @Override
    public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects) throws NotFoundException {
        return readAclsById(objects, null);
    }

    @Override
    public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> oids, List<Sid> sids) throws NotFoundException {
        Map<ObjectIdentity, Acl> aclMaps = new HashMap<>();
        for (ObjectIdentity oid : oids) {
            AclRecord record = getAclRecordByCache(objID(oid));
            if (record == null) {
                Message msg = MsgPicker.getMsg();
                throw new NotFoundException(String.format(Locale.ROOT ,msg.getACL_INFO_NOT_FOUND( ) ,oid
                    ) ) ;}AclparentAcl=null;if(

                record.isEntriesInheriting()&& record. getParentDomainObjectInfo()

                !=null)parentAcl=readAclById ( record.getParentDomainObjectInfo())
            ;
            record .init
        (

        parentAcl,
        aclPermissionFactory , permissionGrantingStrategy); aclMaps. put ( oid
            , newMutableAclRecord ( record ));}returnaclMaps ;
                } @ Override publicMutableAclcreateAcl(ObjectIdentityobjectIdentity)throws
                AlreadyExistsException {try ( AutoLockl =
                    lock . lockForWrite() ) { AclRecord aclRecord=getAclRecordByCache
                (
                objID ( objectIdentity ));if(
                aclRecord!=null){thrownew
                AlreadyExistsException("ACL of "+objectIdentity + " exists!" ) ;}AclRecord
            record = newPrjACL( objectIdentity) ;
                crud . save(record);
            logger
            . debug("ACL of " +objectIdentity+" created successfully.")
        ;

        }catch
        ( IOException e){ thrownew InternalErrorException (e ) ; }
            return (MutableAcl ) readAclById (objectIdentity);}@ Override
                publicvoiddeleteAcl( ObjectIdentity objectIdentity ,booleandeleteChildren)throws
                ChildrenExistException {try( AutoLock l=lock.lockForWrite ( )) {
                    List < ObjectIdentity >children=findChildren(objectIdentity
                    ) ; if(
                            !deleteChildren&&children.size() >0){Messagemsg =MsgPicker.getMsg
                (
                ) ;throw new BadRequestException (String .
                    format(Locale. ROOT,msg
                .
                getIDENTITY_EXIST_CHILDREN(),objectIdentity));}for
                (ObjectIdentityoid:children ) { deleteAcl (oid,
            deleteChildren ) ;} crud. delete
                ( objID (objectIdentity));
            logger
        .

        debug
        ("ACL of "
        + objectIdentity +" deleted successfully.") ;} catch ( IOException
            e ){ throw new InternalErrorException(e);} }
                // Try use the updateAclWithRetry() method family whenever possible @ Override publicMutableAclupdateAcl( MutableAclmutableAcl)throwsNotFoundException{try
                (AutoLockl=lock.lockForWrite
                ()){AclRecord record =((MutableAclRecord) mutableAcl ).getAclRecord
            ( ) ;crud .save (
                record ) ;logger.debug(
            "ACL of "
            + mutableAcl.
        getObjectIdentity

        (
        ) +" updated successfully.") ;} catch ( IOExceptione ) { thrownew InternalErrorException
            ( e);} return mutableAcl;} // a NULL permission means to delete the ace
                MutableAclRecordupsertAce
                ( MutableAclRecord acl,final Sidsid ,
                    finalPermissionperm){return updateAclWithRetry(acl
                ,
            newAclRecordUpdater(
        )

        { @Overridepublic voidupdate ( AclRecordrecord){ record. upsertAce( perm
            ,sid); } }); }
                voidbatchUpsertAce
                ( MutableAclRecord acl,final Map< Sid
                    , Permission> sidToPerm ) {updateAclWithRetry(acl,new AclRecordUpdater
                        (){@Overridepublicvoidupdate(AclRecordrecord ){for
                    (
                Sid
            sid:sidToPerm
        .

        keySet ()) {record . upsertAce (sidToPerm .
            get (sid), sid );} }
                })
                ; } MutableAclRecordinherit( MutableAclRecordacl ,
                    finalMutableAclRecordparentAcl){returnupdateAclWithRetry
                    (acl,newAclRecordUpdater()
                {
            @Overridepublic
        void

        update(
        AclRecord record ){record .setEntriesInheriting (
            true ); record . setParent(parentAcl);} }
                ) ;}@NullableprivateAclRecord getAclRecordByCache (String id
                    ) {try(AutoLockl=lock
                .
            lockForRead

            ( )) { if (aclMap.size() >
                0){returnaclMap.
                get (id);}}try
            ( AutoLock l= lock. lockForWrite
                ( ) ){crud. reloadAll()
            ;
        return

        aclMap . get(id ); }
            catch ( IOException e ){thrownew RuntimeException("Can not get ACL record from cache.",e
            );}}privateAclRecord newPrjACL(ObjectIdentityobjID ){AclRecordacl=
            newAclRecord(objID,getCurrentSid
            ( ))
        ;

        acl . init(null ,
            this . aclPermissionFactory,this.permissionGrantingStrategy);acl.updateRandomUuid();
        return

        acl ; } private
            Sid getCurrentSid() {returnnew
        PrincipalSid

        ( SecurityContextHolder .getContext( ). getAuthentication () )
            ; } public interfaceAclRecordUpdater
            { voidupdate( AclRecord record) ;
                } private MutableAclRecord updateAclWithRetry(MutableAclRecordacl,AclRecordUpdater

                updater){intretry=7
                ; while
                    (retry-->0){
                    AclRecord record= acl

                . getAclRecord () ;updater .
                    update (record ) ;try {
                        crud.save(record) ;returnacl
                        ; // here we are done}
                    catch

                    (WriteConflictExceptionise){ if (retry<=0){logger.
                            error ( "Retry is out, till got error, abandoning..." , ise );throw
                    ise ; }logger.warn("Write conflict to update ACL "+resourceKey(

                record . getObjectIdentity( )) +
                    " retry remaining " + retry+", will retry...");
                acl
            =
            readAcl ( acl.getObjectIdentity()
        )

        ; } catch (IOExceptione ){ throw
            new InternalErrorException(e);}}throw
        new

        RuntimeException ( "should not reach here" );} privatestatic String
            resourceKey (ObjectIdentitydomainObjId){returnresourceKey(objID(domainObjId
        )

        ) ; }privatestatic StringobjID (
            ObjectIdentity domainObjId ) {return
        String
    .
    