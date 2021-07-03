/*
 * Copyright (C) 2014 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stratio.qa.utils;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ldaptive.*;
import org.ldaptive.pool.BlockingConnectionPool;
import org.ldaptive.pool.PooledConnectionFactory;
import org.ldaptive.ssl.SslConfig;
import org.ldaptive.ssl.X509CredentialConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LdapUtils {

    private final Logger logger = LoggerFactory.getLogger(LdapUtils.class);

    private ConnectionFactory connFactory;

    private ConnectionConfig config;

    private BlockingConnectionPool pool;

    private String user;

    private String password;

    private boolean ssl;

    private String url;

    public LdapUtils() {
        this.user = System.getProperty("LDAP_USER");
        this.password = System.getProperty("LDAP_PASSWORD");
        this.ssl = (System.getProperty("LDAP_SSL", "true")).equals("true") ? true : false;
        String ldapUrl = System.getProperty("LDAP_URL") != null ? System.getProperty("LDAP_URL") : ThreadProperty.get("LDAP_URL") != null ? ThreadProperty.get("LDAP_URL") : "";
        String ldapPort = System.getProperty("LDAP_PORT") != null ? System.getProperty("LDAP_PORT") : ThreadProperty.get("LDAP_PORT") != null ? ThreadProperty.get("LDAP_PORT") : "";
        this.url = (this.ssl == true ? "ldaps://" : "ldap://") + ldapUrl + ":" + ldapPort;
    }

    public void connect(String ldapCaTrust) {
        this.config = new ConnectionConfig();
        this.config.setLdapUrl(this.url);
        this.config.setUseSSL(this.ssl);

        // Use CA provided to trust LDAP certificate
        SslConfig sslCfg = new SslConfig();
        X509CredentialConfig crdCfg = new X509CredentialConfig();
        crdCfg.setTrustCertificates("file:" + ldapCaTrust);
        sslCfg.setCredentialConfig(crdCfg);
        this.config.setSslConfig(sslCfg);

        this.config.setConnectionInitializer(new BindConnectionInitializer(user, new Credential(password)));
        this.pool = new BlockingConnectionPool(new DefaultConnectionFactory(this.config));
        if (!this.pool.isInitialized()) {
            this.pool.initialize();
        }
        this.connFactory = new PooledConnectionFactory(this.pool);
    }

    public SearchResult search(SearchRequest request) throws LdapException {
        Connection conn = null;
        try {
            logger.debug("Connecting to LDAP");
            conn = this.connFactory.getConnection();
            SearchOperation search = new SearchOperation(conn);
            Response<SearchResult> response = search.execute(request);
            return response.getResult();
        } catch (LdapException e) {
            throw e;
        } finally {
            conn.close();
        }
    }

    public void add(LdapEntry entry) throws LdapException {
        Connection conn = null;
        try {
            conn = this.connFactory.getConnection();
            AddOperation add = new AddOperation(conn);
            add.execute(new AddRequest(entry.getDn(), entry.getAttributes()));
        } catch (LdapException e) {
            throw e;
        } finally {
            conn.close();
        }
    }

    public void modify(String dn, AttributeModification ... modifications) throws LdapException {
        Connection conn = null;
        try {
            conn = this.connFactory.getConnection();
            ModifyOperation modify = new ModifyOperation(conn);
            modify.execute(new ModifyRequest(dn, modifications));
        } catch (LdapException e) {
            throw e;
        } finally {
            conn.close();
        }
    }

    public void delete(String dn) throws LdapException {
        Connection conn = null;
        try {
            conn = this.connFactory.getConnection();
            DeleteOperation delete = new DeleteOperation(conn);
            delete.execute(new DeleteRequest(dn));
        } catch (LdapException e) {
            throw e;
        } finally {
            conn.close();
        }
    }

    public String hashPassword(String password) throws NoSuchAlgorithmException {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[4];
        secureRandom.nextBytes(salt);

        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        crypt.reset();
        crypt.update(password.getBytes());
        crypt.update(salt);
        byte[] hash = crypt.digest();

        byte[] hashPlusSalt = new byte[hash.length + salt.length];
        System.arraycopy(hash, 0, hashPlusSalt, 0, hash.length);
        System.arraycopy(salt, 0, hashPlusSalt, hash.length, salt.length);

        return new StringBuilder().append("{SSHA}")
                .append(Base64.getEncoder().encodeToString(hashPlusSalt))
                .toString();
    }

    public int getLDAPMaxUidNumber() throws LdapException {
        return this.getLDAPMaxNumber("UID");
    }

    public int getLDAPMaxGidNumber() throws LdapException {
        return this.getLDAPMaxNumber("GID");
    }

    private int getLDAPMaxNumber(String type) throws LdapException {
        String base = "";
        String attr = "";
        int maxId = 0;

        switch (type) {
            case "UID":
                base = ThreadProperty.get("LDAP_USER_DN");
                attr = "uidNumber";
                break;
            case "GID":
                base = ThreadProperty.get("LDAP_GROUP_DN");
                attr = "gidNumber";
                break;
            default:
                break;
        }

        Matcher matcher = this.getSearchResult(base, "cn=*", attr, ".*?" + attr + "\\[(\\d+)\\].*?");
        int actualId;
        while (matcher.find()) {
            actualId = Integer.parseInt(matcher.group(1));
            if (actualId >= maxId) {
                maxId = actualId;
            }
        }
        return maxId;
    }

    public int getLDAPgidNumber(String groupCn) throws LdapException {
        int gid = 0;

        Matcher matcher = this.getSearchResult(ThreadProperty.get("LDAP_GROUP_DN"), "cn=" + groupCn, "gidNumber", ".*gidNumber\\[(\\d+)\\].*");
        if (matcher.matches()) {
            gid = Integer.parseInt(matcher.group(1));
        }
        return gid;
    }

    public ArrayList<String> getLDAPgroupsContainingUserAsAttribute(String userUid, String attr) throws LdapException {
        ArrayList<String> groupsList = new ArrayList<>();

        Matcher matcher = this.getSearchResult(ThreadProperty.get("LDAP_GROUP_DN"), attr + "=uid=" + userUid + "," + ThreadProperty.get("LDAP_USER_DN"), "cn", ".*?cn\\[([^\\[\\]]*)\\].*?");
        while (matcher.find()) {
            groupsList.add(matcher.group(1));
        }

        return groupsList;
    }

    public void deleteLDAPuserFromAllGroupsAttribute(String userUid, String attr) throws LdapException {
        ArrayList<String> groupsList = this.getLDAPgroupsContainingUserAsAttribute(userUid, attr);

        Iterator<String> iter = groupsList.iterator();
        while (iter.hasNext()) {
            this.deleteLDAPuserFromGroupAttribute(userUid, iter.next(), attr);
        }
    }

    public void deleteLDAPuserFromGroupAttribute(String userUid, String groupCn, String attr) throws LdapException {
        String groupDn = "cn=" + groupCn + "," + ThreadProperty.get("LDAP_GROUP_DN");
        String userDn = "uid=" + userUid + "," + ThreadProperty.get("LDAP_USER_DN");

        AttributeModification newAttr = new AttributeModification(AttributeModificationType.REMOVE, new LdapAttribute(attr, userDn));
        this.modify(groupDn, newAttr);
    }

    public boolean isLDAPuserInGroup(String userUid, String groupCn) throws LdapException {
        boolean userBelongsToGroup = false;

        Matcher matcher = this.getSearchResult(ThreadProperty.get("LDAP_GROUP_DN"), "(&(cn=" + groupCn + ")(member=uid=" + userUid + "," + ThreadProperty.get("LDAP_USER_DN") + "))", "cn", ".*?cn\\[([^\\[\\]]*)\\].*?");
        if (matcher.matches()) {
            userBelongsToGroup = true;
        }

        return userBelongsToGroup;
    }

    public boolean userLDAPexists(String userUid) throws LdapException {
        boolean userExists = false;

        Matcher matcher = this.getSearchResult(ThreadProperty.get("LDAP_USER_DN"), "uid=" + userUid, "cn", ".*?cn\\[([^\\[\\]]*)\\].*?");
        if (matcher.matches()) {
            userExists = true;
        }

        return userExists;
    }

    public boolean groupLDAPexists(String groupCn) throws LdapException {
        boolean groupExists = false;

        Matcher matcher = this.getSearchResult(ThreadProperty.get("LDAP_GROUP_DN"), "cn=" + groupCn, "cn", ".*?cn\\[([^\\[\\]]*)\\].*?");
        if (matcher.matches()) {
            groupExists = true;
        }

        return groupExists;
    }

    private Matcher getSearchResult(String baseDn, String searchFilter, String attribute, String REGEX) throws LdapException {
        SearchResult search = this.search(new SearchRequest(baseDn, searchFilter, attribute));
        String INPUT = search.toString();
        Pattern pattern = Pattern.compile(REGEX);

        return pattern.matcher(INPUT);
    }

}
